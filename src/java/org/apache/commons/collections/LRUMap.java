/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/LRUMap.java,v 1.4 2002/02/13 21:03:20 morgand Exp $
 * $Revision: 1.4 $
 * $Date: 2002/02/13 21:03:20 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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
  * <b>Warning</b>: This class is not a true "Least Recently Used" map.  When 
  * mappings are accessed, the mapping is moved one position away from the end
  * of the list, rather than all the way to the front of the list.  This means
  * that the "most" recently used, is not at the front of the list, and the 
  * "least" recently used is not necessarily at the end of the list.  Here's a
  * simple example (Provided by Aaron Smuts on commons-dev@):
  *
  * <pre>
  *    Say that items 0 - 9 are put in.  The limit is 10.
  *
  *    The list looks like:
  *
  *    Index order (0-9)
  *    9,8,7,6,5,4,3,2,1,0
  *
  *    Item 1 is accessed and the list now looks like:
  *    Index order (0-9)
  *    9,8,7,6,5,4,3,1,2,0
  *
  *    Item 0 is accessed and the list now looks like:
  *    Index order (0-9)
  *    9,8,7,6,5,4,3,1,0,2
  *
  *    Item 2 is accessed and the list now looks like:
  *    Index order (0-9)
  *    9,8,7,6,5,4,3,1,2,0
  *
  *    Item 10 is added and the list now looks like
  *    Index order (0-9)
  *    10,9,8,7,6,5,4,3,1,2
  *
  *    Item 0 was droped but it was not the least recently used element.
  * </pre>
   * </p>
  * <p>
  * Additionally, the results from entrySet() and values() are not properly 
  * backed by the map in violation of the Map API contract.  These methods 
  * are also not implemented efficiently.</li>
  * </ul>
  * </p>
  * 
  * <p>These issues hopefully will be corrected at a later date.</p>
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
        int lastItem = size() - 1;
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
        if ( pair != null ) {
            bubbleList.remove( pair.position );
            return pair.value;
        }
        return null;
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
