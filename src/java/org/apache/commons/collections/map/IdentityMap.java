/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/map/IdentityMap.java,v 1.1 2003/12/02 21:57:08 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
package org.apache.commons.collections.map;

import java.util.Map;

/**
 * A <code>Map</code> implementation that matches keys and values based
 * on <code>==</code> not <code>equals()</code>.
 * <p>
 * This map will violate the detail of various Map and map view contracts.
 * As a general rule, don't compare this map to other maps.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/12/02 21:57:08 $
 *
 * @author java util HashMap
 * @author Stephen Colebourne
 */
public class IdentityMap extends HashedMap {
    
    /** Serialisation version */

    /**
     * Constructs a new empty map with default size and load factor.
     */
    public IdentityMap() {
        super();
    }

    /**
     * Constructs a new, empty map with the specified initial capacity. 
     *
     * @param initialCapacity  the initial capacity
     * @throws IllegalArgumentException if the initial capacity is less than one
     */
    public IdentityMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor. 
     *
     * @param initialCapacity  the initial capacity
     * @param loadFactor  the load factor
     * @throws IllegalArgumentException if the initial capacity is less than one
     * @throws IllegalArgumentException if the load factor is less than one
     */
    public IdentityMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructor copying elements from another map.
     *
     * @param map  the map to copy
     * @throws NullPointerException if the map is null
     */
    public IdentityMap(Map map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hash code for the key specified.
     * This implementation uses the identity hash code.
     * 
     * @param key  the key to get a hash code for
     * @return the hash code
     */
    protected int hash(Object key) {
        return System.identityHashCode(key);
    }
    
    /**
     * Compares two keys for equals.
     * This implementation uses <code>==</code>.
     * 
     * @param key1  the first key to compare
     * @param key2  the second key to compare
     * @return true if equal by identity
     */
    protected boolean isEqualKey(Object key1, Object key2) {
        return (key1 == key2);
    }
    
    /**
     * Compares two values for equals.
     * This implementation uses <code>==</code>.
     * 
     * @param value1  the first value to compare
     * @param value2  the second value to compare
     * @return true if equal by identity
     */
    protected boolean isEqualValue(Object value1, Object value2) {
        return (value1 == value2);
    }
    
    /**
     * Creates an entry to store the data.
     * This implementation creates an IdentityEntry instance.
     * 
     * @param next  the next entry in sequence
     * @param hashCode  the hash code to use
     * @param key  the key to store
     * @param value  the value to store
     * @return the newly created entry
     */
    protected HashEntry createEntry(HashEntry next, int hashCode, Object key, Object value) {
        return new IdentityEntry(next, hashCode, key, value);
    }
    
    //-----------------------------------------------------------------------
    /**
     * HashEntry
     */
    protected static class IdentityEntry extends HashEntry {
        
        protected IdentityEntry(HashEntry next, int hashCode, Object key, Object value) {
            super(next, hashCode, key, value);
        }
        
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            Map.Entry other = (Map.Entry) obj;
            return
                (getKey() == other.getKey()) &&
                (getValue() == other.getValue());
        }
        
        public int hashCode() {
            return System.identityHashCode(getKey()) ^
                   System.identityHashCode(getValue());
        }
    }
    
}
