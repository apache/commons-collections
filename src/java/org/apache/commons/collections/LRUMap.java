/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

import java.io.*;
import java.util.*;

/** <p>
  * An implementation of a Map which has a maximum size and uses a Least Recently Used
  * algorithm to remove items from the Map when the maximum size is reached and new items are added.
  * </p>
  *
  * <p>
  * This implementation uses a simple bubbling
  * algorithm, whereby every random access get() method call bubbles the item
  * up the list, further away from the 'drop zone'.
  * </p>
  *
  * <p>
  * A synchronized version can be obtained with:
  * <code>Collections.synchronizedMap( theMapToSynchronize )</code>
  * </p>
  *
  * <p>
  * <b>WARNING</b> the values() and entrySet() methods require optimisation
  * like the standard {@link HashMap} implementations so that iteration
  * over this Map is efficient.
  * </p>
  * 
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */
public class LRUMap extends HashMap implements Externalizable {
    
    /** Holds value of property maximumSize. */
    private int maximumSize;
    /** Used to hold the bubble list - bubbles keys up the list as they are accessed */
    private ArrayList bubbleList;
    
    //static final long serialVersionUID = 0x9e1e06764b24cb05L;

    public LRUMap() {
        this( 100 );
    }

    public LRUMap(int i) {
        super( i );
        maximumSize = i;
        bubbleList = new ArrayList( i );
    }

    /** Removes the least recently used object from the Map.
      * @return the key of the removed item
      */
    public Object removeLRU() {
        int lastItem = size();
        Object key = bubbleList.remove( lastItem );
        ValuePositionPair pair = removePair( key );
        return key;
    }
    
    // Map interface
    //-------------------------------------------------------------------------        
    public Object get( Object key ) {
        ValuePositionPair pair = getPair( key );
        if ( pair == null ) {
            return null;
        }
        int position = pair.position;
        if ( position > 0 ) {
            // lets bubble up this entry up the list
            // avoiding expesive list removal / insertion
            int position2 = position - 1;
            Object key2 = bubbleList.get( position2 );
            ValuePositionPair pair2 = getPair( key2 );
            if ( pair2 != null ) {
                pair2.position = position;
                pair.position = position2;
                bubbleList.set( position, key2 );
                bubbleList.set( position2, key );
            }
        }
        return pair.value;
    }

    public Object put( Object key, Object value ) {
        int i = size();
        ValuePositionPair pair = new ValuePositionPair( value );
        if ( i >= maximumSize ) {
            // lets retire the least recently used item in the cache
            int lastIndex = maximumSize - 1;
            pair.position = lastIndex;
            Object oldKey = bubbleList.set( lastIndex, key );
            super.remove( oldKey );
        } 
        else {
            pair.position = i;
            bubbleList.add( i, key );
        }
        pair = (ValuePositionPair) putPair( key, pair );
        return ( pair != null ) ? pair.value : null;
    }

    public Object remove( Object key ) {
        ValuePositionPair pair = removePair( key );
        return ( pair != null ) ? pair.value : null;
    }
    
    public boolean containsKey( Object key ) {
        return super.containsKey( key );
    }

    public boolean containsValue( Object value ) {
        for ( Iterator iter = pairIterator(); iter.hasNext(); ) {
            ValuePositionPair pair = (ValuePositionPair) iter.next();
            Object otherValue = pair.value;
            if ( value == otherValue ) {
                return true;
            }
            if ( value != null && value.equals( otherValue ) ) {
                return true;
            }
        }
        return false;
    }
    
    public Set keySet() {
        return super.keySet();
    }
    
    public Set entrySet() {
        HashSet answer = new HashSet();
        for ( Iterator iter = super.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry otherEntry = (Map.Entry) iter.next();
            Object key = otherEntry.getKey();
            ValuePositionPair pair = (ValuePositionPair) otherEntry.getValue();
            Object value = pair.value;
            Entry newEntry = new Entry( key, value );
            answer.add( newEntry );
        }
        return answer;
    }

    public Collection values() {
        ArrayList answer = new ArrayList();
        for ( Iterator iter = super.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry otherEntry = (Map.Entry) iter.next();
            Entry newEntry = new Entry( otherEntry.getKey(), otherEntry.getValue() );
            answer.add( newEntry );
        }
        return answer;
    }


 
    // Externalizable interface
    //-------------------------------------------------------------------------        
    public void readExternal( ObjectInput in )  throws IOException, ClassNotFoundException {
        maximumSize = in.readInt();
        int size = in.readInt();
        
        // create a populated list
        bubbleList = new ArrayList( maximumSize );
        for( int i = 0; i < size; i++ )  {
            bubbleList.add( "" );
        }

        for( int i = 0; i < size; i++ )  {
            Object key = in.readObject();
            Object value = in.readObject();
            ValuePositionPair pair = (ValuePositionPair) value;            
            int position = pair.position;
            bubbleList.set( position, pair );
            putPair( key, pair );
        }
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeInt( maximumSize );
        out.writeInt( size() );
        for( Iterator iterator = keySet().iterator(); iterator.hasNext(); ) {
            Object key = iterator.next();
            out.writeObject( key );
            Object value = getPair( key );
            out.writeObject( value );
        }
    }
    
    
    // Properties
    //-------------------------------------------------------------------------        
    /** Getter for property maximumSize.
     * @return Value of property maximumSize.
     */
    public int getMaximumSize() {
        return maximumSize;
    }
    /** Setter for property maximumSize.
     * @param maximumSize New value of property maximumSize.
     */
    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }
    
    
    // Implementation methods
    //-------------------------------------------------------------------------        
    protected ValuePositionPair getPair( Object key ) {
        return (ValuePositionPair) super.get( key );
    }
    
    protected ValuePositionPair putPair( Object key, ValuePositionPair pair ) {
        return (ValuePositionPair) super.put( key, pair );
    }
    
    protected ValuePositionPair removePair( Object key ) {
        return (ValuePositionPair) super.remove( key );
    }
    
    protected Iterator pairIterator() {
        return super.values().iterator();
    }
    
    // Implementation classes
    //-------------------------------------------------------------------------    
    protected static class ValuePositionPair implements Serializable {

        public Object value;
        public int position;

        public ValuePositionPair() {
        }
        
        public ValuePositionPair( Object value ) {
            this.value = value;
        }
        
        public String toString() {
            return "[ " + position + ": " + value + " ]";
        }
    }
    
    /** 
     * A map entry, which is backed by this RefHashMap
     */
    class Entry implements Map.Entry {
        
        /**
         * Constructor
         */
        public Entry( Object key, Object value ) {
            this.key = key;
            this.value = value;
        }

        // Map.Entry interface
        // -----------------------------------------------------------
        
        /**
         * Retrieves the key of this mapping
         */
        public Object getKey() {
            return key;
        }
        
        /**
         * Retrieves the value of this mapping
         */
        public Object getValue() {
           return value;
        }
        
        /**
         * Sets the value of this mapping
         */
        public Object setValue( Object value ) {
            this.value = value;
            put( key, value ); 
            return value;
        }
        
        /**
         * Return the hash code of this mapping.
         * This algorithm was taken from the JavaDoc for Map.Entry
         */
        public int hashCode() {
            return ( getKey() == null ? 0 : getKey().hashCode() ) ^
                ( getValue() == null ? 0 : getValue().hashCode() );
         }
        
        /** The domain of this mapping */
        private Object key;
        /** The range of this mapping */
        private Object value;    
    }
}
