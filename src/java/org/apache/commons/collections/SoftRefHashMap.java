/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/SoftRefHashMap.java,v 1.5 2002/08/12 18:24:33 pjack Exp $
 * $Revision: 1.5 $
 * $Date: 2002/08/12 18:24:33 $
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
  * @since 1.0
  * @author  James.Dodd
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @deprecated  This class is all kinds of wonky; use ReferenceMap instead.
  * @see <A HREF="http://issues.apache.org/bugzilla/show_bug.cgi?id=9571">
  *  Bug#9571</A>
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
