/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

import java.lang.ref.*;
import java.lang.reflect.*;
import java.util.*;

/** <p>
  * HashMap with SoftReference links to values which allows the values of the Map
  * to be garbage collected by the JVM if it becomes low on memory.  
  * Derive from this class and 
  * override the factory method <code>createReference()</code> method to make 
  * a Map wrapped in other types of Reference.
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
  * @author  James.Dodd
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */
public class SoftRefHashMap implements Map {
    
    /** The wrapped HashMap */
    private Map hashMap = new HashMap();

    
    public SoftRefHashMap() {
    }

    
    /** 
     * Removes References that have had their referents garbage collected
     */
    public void purge() {
        Map map = getMap();
        Set keys = map.keySet();
        if ( keys == null ) {
            return;
        }
        for ( Iterator i = keys.iterator(); i.hasNext(); ) {
            Object key = (Object) i.next();
            Reference ref = (Reference) map.get( key );
            if ( ref.get() == null ) {
                map.remove( key );
            }
        }
    }
    
    // Map implementation
    // -------------------------------------------------------

    /**
     * Retrieves the referent of the Referenced value
     * @param key The key with which to retrieve the value
     */
    public Object get( final Object key ) {
        Reference ref = (Reference) getMap().get( key );
        if ( ref == null ) {
            return null;
        }
        return ref.get();    
    }
    
    /**
     * Adds a key-value mapping, wrapping the value in a Reference 
     */
    public Object put( final Object key, final Object value ) {
        Object answer = getMap().put( key, createReference( value ) );
        if ( answer != null ) {
            return ((Reference) answer).get();
        }
        return null;
    }

    /** 
      * Returns a collection of the Referenced values
      */
    public Collection values() {
        Set wrappedValues = (Set) getMap().values();
        Set values = new TreeSet();
        if ( wrappedValues == null ) {
            return values;
        }
        for ( Iterator i = wrappedValues.iterator(); i.hasNext(); ) {
            Reference ref = (Reference) i.next();
            if ( ref != null ) {
                values.add( ref.get() );
            }
        }
        return values;
    }

    /**
     * Answers whether the argument is in the domain of the mappings
     */
    public boolean containsKey( Object key ) {
        return getMap().containsKey( key );
    }

    /**
     * Answers whether the argument is a Referenced value
     */
    public boolean containsValue( Object value ) {
        Collection values = (Collection) getMap().values();
        if ( values == null ) {
            return false;
        }
        for ( Iterator i = values.iterator(); i.hasNext(); ) {
            Reference ref = (Reference) i.next();
            if ( ref == null ) {
                continue;
            }
            Object target = ref.get();
            if ( target == value ) {
                return true;
            }
        }
        return false;
    }

    /** 
      * Put all of the mappings in the argument into this wrapped map
      */
    public void putAll( final java.util.Map map ) {
        if ( map == null || map.size() == 0 ) {
            return;
        }   
        for ( Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            Object key = (Object) i.next();
            put( key, map.get( key ) );
        }
    }
    
    /**
      * Returns a set view of the mappings in the wrapped map
      */
    public Set entrySet() {
        Set entries = new HashSet();
        if ( size() == 0 ) {
            return entries;
        }
        for ( Iterator i = keySet().iterator(); i.hasNext(); ) {
            Object key = i.next();
            Object value = get( key );
            Entry entry = new Entry( key, value );
            entries.add( entry );
        }
        return entries;
    }
    
    /** 
      * Removes a mapping from this map
      */
    public Object remove( final Object key ) {
        Reference ref = (Reference) getMap().remove( key );
        if ( ref != null ) {
            return ref.get();
        }
        return null;
    }
    
    /** 
      * Clears all  mappings 
      */
    public void clear() {
        getMap().clear();
    }

    /** 
      * Calculates the hash code for this map
      */
    public int hashCode() {
        return getMap().hashCode();
    }
    
    /** 
      * Returns the domain of the mappings
      */
    public Set keySet() {
        return getMap().keySet();
    }
               
    /** 
      * Answers whether there are any mappings
      */
    public boolean isEmpty() {
        return getMap().isEmpty();
    }
    
    /** 
      * Answers whether this map and the argument are 'the same' 
      */
    public boolean equals( final Object object ) {
        return getMap().equals( object );
    }
    
    /**
      * Returns the number of mappings in this map
      */
    public int size() {
        return getMap().size();
    }
    
    // Inner Classes 
    // ---------------------------------------------------------------------
    
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

    /**
     * Returns a reference to the argument.
     * Override this method to make wrapped maps for other Reference types
     */
    protected Reference createReference( Object referent ) {
        return new SoftReference( referent );
    }
    
    /** 
     * Retrieves the wrapped HashMap
     * @return The wrapped HashMap
     */
    protected Map getMap() {
        return hashMap;
    }
}
