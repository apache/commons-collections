/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/Flat3Map.java,v 1.1 2003/11/02 23:41:46 scolebourne Exp $
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
package org.apache.commons.collections;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.iterators.DefaultMapIterator;
import org.apache.commons.collections.iterators.MapIterator;
import org.apache.commons.collections.iterators.ResetableMapIterator;
import org.apache.commons.collections.pairs.AbstractMapEntry;

/**
 * A <code>Map</code> implementation that stores data in simple fields until
 * the size is greater than 3.
 * <p>
 * This map is designed for performance and can outstrip HashMap.
 * It also has good garbage collection characteristics.
 * <ul>
 * <li>Optimised for operation at size 3 or less.
 * <li>Still works well once size 3 exceeded.
 * <li>Gets at size 3 or less are about 0-10% faster than HashMap,
 * <li>Puts at size 3 or less are over 4 times faster than HashMap.
 * <li>Performance 5% slower than HashMap once size 3 exceeded once.
 * </ul>
 * The design uses two distinct modes of operation - flat and delegate.
 * While the map is size 3 or less, operations map straight onto fields using
 * switch statements. Once size 4 is reached, the map switches to delegate mode
 * and never switches back. In delegate mode, all operations are forwarded 
 * straight to a HashMap resulting in the 5% performance loss.
 * <p>
 * The performance gains on puts are due to not needing to create a Map Entry
 * object. This is a large saving not only in performance but in garbage collection.
 * <p>
 * Whilst in flat mode this map is also easy for the garbage collector to dispatch.
 * This is because it contains no complex objects or arrays which slow the progress.
 * (Note that the impact of this has not actually been tested!)
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/11/02 23:41:46 $
 *
 * @author Stephen Colebourne
 */
public class Flat3Map implements Map {
    
    /** The size of the map, used while in flat mode */
    private int iSize;
    /** Hash, used while in flat mode */
    private int iHash1;
    /** Hash, used while in flat mode */
    private int iHash2;
    /** Hash, used while in flat mode */
    private int iHash3;
    /** Key, used while in flat mode */
    private Object iKey1;
    /** Key, used while in flat mode */
    private Object iKey2;
    /** Key, used while in flat mode */
    private Object iKey3;
    /** Value, used while in flat mode */
    private Object iValue1;
    /** Value, used while in flat mode */
    private Object iValue2;
    /** Value, used while in flat mode */
    private Object iValue3;
    /** Map, used while in delegate mode */
    private HashMap iMap;

    /**
     * Constructor.
     */
    public Flat3Map() {
        super();
    }

    /**
     * Constructor copying elements from another map.
     *
     * @param map  the map to copy
     */
    public Flat3Map(Map map) {
        super();
        putAll(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value mapped to the key specified.
     * 
     * @param key  the key
     * @return the mapped value, null if no match
     */
    public Object get(Object key) {
        if (iMap != null) {
            return iMap.get(key);
        }
        if (key == null) {
            switch (iSize) {
                // drop through
                case 3:
                    if (iKey3 == null) return iValue3;
                case 2:
                    if (iKey2 == null) return iValue2;
                case 1:
                    if (iKey1 == null) return iValue1;
            }
        } else {
            if (iSize > 0) {
                int hashCode = key.hashCode();
                switch (iSize) {
                    // drop through
                    case 3:
                        if (iHash3 == hashCode && key.equals(iKey3)) return iValue3;
                    case 2:
                        if (iHash2 == hashCode && key.equals(iKey2)) return iValue2;
                    case 1:
                        if (iHash1 == hashCode && key.equals(iKey1)) return iValue1;
                }
            }
        }
        return null;
    }

    /**
     * Gets the size of the map.
     * 
     * @return the size
     */
    public int size() {
        if (iMap != null) {
            return iMap.size();
        }
        return iSize;
    }

    /**
     * Checks whether the map is currently empty.
     * 
     * @return true if the map is currently size zero
     */
    public boolean isEmpty() {
        return (size() == 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the map contains the specified key.
     * 
     * @param key  the key to search for
     * @return true if the map contains the key
     */
    public boolean containsKey(Object key) {
        if (iMap != null) {
            return iMap.containsKey(key);
        }
        if (key == null) {
            switch (iSize) {  // drop through
                case 3:
                    if (iKey3 == null) return true;
                case 2:
                    if (iKey2 == null) return true;
                case 1:
                    if (iKey1 == null) return true;
            }
        } else {
            if (iSize > 0) {
                int hashCode = key.hashCode();
                switch (iSize) {  // drop through
                    case 3:
                        if (iHash3 == hashCode && key.equals(iKey3)) return true;
                    case 2:
                        if (iHash2 == hashCode && key.equals(iKey2)) return true;
                    case 1:
                        if (iHash1 == hashCode && key.equals(iKey1)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the map contains the specified value.
     * 
     * @param value  the value to search for
     * @return true if the map contains the key
     */
    public boolean containsValue(Object value) {
        if (iMap != null) {
            return iMap.containsValue(value);
        }
        if (value == null) {  // drop through
            switch (iSize) {
                case 3:
                    if (iValue3 == null) return true;
                case 2:
                    if (iValue2 == null) return true;
                case 1:
                    if (iValue1 == null) return true;
            }
        } else {
            switch (iSize) {  // drop through
                case 3:
                    if (value.equals(iValue3)) return true;
                case 2:
                    if (value.equals(iValue2)) return true;
                case 1:
                    if (value.equals(iValue1)) return true;
            }
        }
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Puts a key-value mapping into this map.
     * 
     * @param key  the key to add
     * @param value  the value to add
     * @return the value previously mapped to this key, null if none
     */
    public Object put(Object key, Object value) {
        if (iMap != null) {
            return iMap.put(key, value);
        }
        // change existing mapping
        if (key == null) {
            switch (iSize) {  // drop through
                case 3:
                    if (iKey3 == null) {
                        Object old = iValue3;
                        iValue3 = value;
                        return old;
                    }
                case 2:
                    if (iKey2 == null) {
                        Object old = iValue2;
                        iValue2 = value;
                        return old;
                    }
                case 1:
                    if (iKey1 == null) {
                        Object old = iValue1;
                        iValue1 = value;
                        return old;
                    }
            }
        } else {
            if (iSize > 0) {
                int hashCode = key.hashCode();
                switch (iSize) {  // drop through
                    case 3:
                        if (iHash3 == hashCode && key.equals(iKey3)) {
                            Object old = iValue3;
                            iValue3 = value;
                            return old;
                        }
                    case 2:
                        if (iHash2 == hashCode && key.equals(iKey2)) {
                            Object old = iValue2;
                            iValue2 = value;
                            return old;
                        }
                    case 1:
                        if (iHash1 == hashCode && key.equals(iKey1)) {
                            Object old = iValue1;
                            iValue1 = value;
                            return old;
                        }
                }
            }
        }
        
        // add new mapping
        switch (iSize) {
            default:
                convertToMap();
                iMap.put(key, value);
                return null;
            case 2:
                iHash3 = (key == null ? 0 : key.hashCode());
                iKey3 = key;
                iValue3 = value;
                break;
            case 1:
                iHash2 = (key == null ? 0 : key.hashCode());
                iKey2 = key;
                iValue2 = value;
                break;
            case 0:
                iHash1 = (key == null ? 0 : key.hashCode());
                iKey1 = key;
                iValue1 = value;
                break;
        }
        iSize++;
        return null;
    }

    /**
     * Puts all the values from the specified map into this map.
     * 
     * @param map
     */
    public void putAll(Map map) {
        int size = map.size();
        if (size == 0) {
            return;
        }
        if (iMap != null) {
            iMap.putAll(map);
        }
        if (size < 4) {
            for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                put(entry.getKey(), entry.getValue());
            }
        } else {
            convertToMap();
            iMap.putAll(map);
        }
    }

    /**
     * Converts the flat map data to a HashMap.
     */
    private void convertToMap() {
        iMap = new HashMap();
        switch (iSize) {  // drop through
            case 3:
                iMap.put(iKey3, iValue3);
            case 2:
                iMap.put(iKey2, iValue2);
            case 1:
                iMap.put(iKey1, iValue1);
        }
        
        iSize = 0;
        iHash1 = iHash2 = iHash3 = 0;
        iKey1 = iKey2 = iKey3 = null;
        iValue1 = iValue2 = iValue3 = null;
    }

    /**
     * Removes the specified mapping from this map.
     * 
     * @param key  the mapping to remove
     * @return the value mapped to the removed key, null if key not in map
     */
    public Object remove(Object key) {
        if (iMap != null) {
            return iMap.remove(key);
        }
        if (iSize == 0) {
            return null;
        }
        if (key == null) {
            switch (iSize) {  // drop through
                case 3:
                    if (iKey3 == null) {
                        Object old = iValue3;
                        iHash3 = 0;
                        iKey3 = null;
                        iValue3 = null;
                        iSize = 2;
                        return old;
                    }
                    if (iKey2 == null) {
                        Object old = iValue3;
                        iHash2 = iHash3;
                        iKey2 = iKey3;
                        iValue2 = iValue3;
                        iHash3 = 0;
                        iKey3 = null;
                        iValue3 = null;
                        iSize = 2;
                        return old;
                    }
                    if (iKey1 == null) {
                        Object old = iValue3;
                        iHash1 = iHash3;
                        iKey1 = iKey3;
                        iValue1 = iValue3;
                        iHash3 = 0;
                        iKey3 = null;
                        iValue3 = null;
                        iSize = 2;
                        return old;
                    }
                    return null;
                case 2:
                    if (iKey2 == null) {
                        Object old = iValue2;
                        iHash2 = 0;
                        iKey2 = null;
                        iValue2 = null;
                        iSize = 1;
                        return old;
                    }
                    if (iKey1 == null) {
                        Object old = iValue2;
                        iHash1 = iHash2;
                        iKey1 = iKey2;
                        iValue1 = iValue2;
                        iHash2 = 0;
                        iKey2 = null;
                        iValue2 = null;
                        iSize = 1;
                        return old;
                    }
                    return null;
                case 1:
                    if (iKey1 == null) {
                        Object old = iValue1;
                        iHash1 = 0;
                        iKey1 = null;
                        iValue1 = null;
                        iSize = 0;
                        return old;
                    }
            }
        } else {
            if (iSize > 0) {
                int hashCode = key.hashCode();
                switch (iSize) {  // drop through
                    case 3:
                        if (iHash3 == hashCode && key.equals(iKey3)) {
                            Object old = iValue3;
                            iHash3 = 0;
                            iKey3 = null;
                            iValue3 = null;
                            iSize = 2;
                            return old;
                        }
                        if (iHash2 == hashCode && key.equals(iKey2)) {
                            Object old = iValue3;
                            iHash2 = iHash3;
                            iKey2 = iKey3;
                            iValue2 = iValue3;
                            iHash3 = 0;
                            iKey3 = null;
                            iValue3 = null;
                            iSize = 2;
                            return old;
                        }
                        if (iHash1 == hashCode && key.equals(iKey1)) {
                            Object old = iValue3;
                            iHash1 = iHash3;
                            iKey1 = iKey3;
                            iValue1 = iValue3;
                            iHash3 = 0;
                            iKey3 = null;
                            iValue3 = null;
                            iSize = 2;
                            return old;
                        }
                        return null;
                    case 2:
                        if (iHash2 == hashCode && key.equals(iKey2)) {
                            Object old = iValue2;
                            iHash2 = 0;
                            iKey2 = null;
                            iValue2 = null;
                            iSize = 1;
                            return old;
                        }
                        if (iHash1 == hashCode && key.equals(iKey1)) {
                            Object old = iValue2;
                            iHash1 = iHash2;
                            iKey1 = iKey2;
                            iValue1 = iValue2;
                            iHash2 = 0;
                            iKey2 = null;
                            iValue2 = null;
                            iSize = 1;
                            return old;
                        }
                        return null;
                    case 1:
                        if (iHash1 == hashCode && key.equals(iKey1)) {
                            Object old = iValue1;
                            iHash1 = 0;
                            iKey1 = null;
                            iValue1 = null;
                            iSize = 0;
                            return old;
                        }
                }
            }
        }
        return null;
    }

    /**
     * Clears the map, resetting the size to zero and nullifying references
     * to avoid garbage collection issues.
     */
    public void clear() {
        if (iMap != null) {
            iMap.clear();
        }
        iSize = 0;
        iHash1 = iHash2 = iHash3 = 0;
        iKey1 = iKey2 = iKey3 = iValue1 = iValue2 = iValue3 = null;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an iterator over the map.
     * Changes made to the iterator affect this map.
     * <p>
     * A MapIterator returns the keys in the map. It also provides convenient
     * methods to get the key and value, and set the value.
     * It avoids the need to create an entrySet/keySet/values object.
     * It also avoids creating the Mep Entry object.
     * 
     * @return the map iterator
     */
    public MapIterator mapIterator() {
        if (iMap != null) {
            return new DefaultMapIterator(this);
        }
        if (iSize == 0) {
            return IteratorUtils.EMPTY_MAP_ITERATOR;
        }
        return new FlatMapIterator(this);
    }

    /**
     * FlatMapIterator
     */
    static class FlatMapIterator implements ResetableMapIterator {
        private final Flat3Map iFlatMap;
        private int iIndex = 0;
        private boolean iCanRemove = false;
        
        FlatMapIterator(Flat3Map map) {
            super();
            iFlatMap = map;
        }

        public boolean hasNext() {
            return (iIndex < iFlatMap.iSize);
        }

        public Object next() {
            if (hasNext() == false) {
                throw new NoSuchElementException("No more elements in the iteration");
            }
            iCanRemove = true;
            iIndex++;
            return getKey();
        }

        public void remove() {
            if (iCanRemove == false) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            iFlatMap.remove(getKey());
            iIndex--;
            iCanRemove = false;
        }

        public Object getKey() {
            if (iCanRemove == false) {
                throw new IllegalStateException("Map Entry cannot be queried");
            }
            switch (iIndex) {
                case 3:
                    return iFlatMap.iKey3;
                case 2:
                    return iFlatMap.iKey2;
                case 1:
                    return iFlatMap.iKey1;
            }
            throw new IllegalStateException("Invalid map index");
        }

        public Object getValue() {
            if (iCanRemove == false) {
                throw new IllegalStateException("Map Entry cannot be queried");
            }
            switch (iIndex) {
                case 3:
                    return iFlatMap.iValue3;
                case 2:
                    return iFlatMap.iValue2;
                case 1:
                    return iFlatMap.iValue1;
            }
            throw new IllegalStateException("Invalid map index");
        }

        public Object setValue(Object value) {
            if (iCanRemove == false) {
                throw new IllegalStateException("Map Entry cannot be changed");
            }
            Object old = getValue();
            switch (iIndex) {
                case 3: 
                    iFlatMap.iValue3 = value;
                case 2:
                    iFlatMap.iValue2 = value;
                case 1:
                    iFlatMap.iValue1 = value;
            }
            return old;
        }
        
        public void reset() {
            iIndex = 0;
            iCanRemove = false;
        }
        
        public Entry asMapEntry() {
            return new AbstractMapEntry(getKey(), getValue()) {
                public Object setValue(Object value) {
                    FlatMapIterator.this.setValue(value);
                    return super.setValue(value);
                }
            };
        }
        
        public String toString() {
            if (iCanRemove) {
                return "MapIterator[" + getKey() + "=" + getValue() + "]";
            } else {
                return "MapIterator[]";
            }
        }
    }
    
    /**
     * Gets the entrySet view of the map.
     * Changes made to the view affect this map.
     * The Map Entry is not an independent object and changes as the 
     * iterator progresses.
     * To simply iterate through the entries, use {@link #mapIterator()}.
     * 
     * @return the entrySet view
     */
    public Set entrySet() {
        if (iMap != null) {
            return iMap.entrySet();
        }
        return new EntrySet(this);
    }
    
    /**
     * EntrySet
     */
    static class EntrySet extends AbstractSet {
        private final Flat3Map iFlatMap;
        
        EntrySet(Flat3Map map) {
            super();
            iFlatMap = map;
        }

        public int size() {
            return iFlatMap.size();
        }
        
        public void clear() {
            iFlatMap.clear();
        }
        
        public boolean remove(Object obj) {
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Object key = entry.getKey();
            boolean result = iFlatMap.containsKey(key);
            iFlatMap.remove(key);
            return result;
        }

        public Iterator iterator() {
            if (iFlatMap.iMap != null) {
                return iFlatMap.iMap.entrySet().iterator();
            }
            if (iFlatMap.size() == 0) {
                return IteratorUtils.EMPTY_ITERATOR;
            }
            return new EntrySetIterator(iFlatMap);
        }
    }

    /**
     * EntrySetIterator and MapEntry
     */
    static class EntrySetIterator implements Iterator, Map.Entry {
        private final Flat3Map iFlatMap;
        private int iIndex = 0;
        private boolean iCanRemove = false;
        
        EntrySetIterator(Flat3Map map) {
            super();
            iFlatMap = map;
        }

        public boolean hasNext() {
            return (iIndex < iFlatMap.iSize);
        }

        public Object next() {
            if (hasNext() == false) {
                throw new NoSuchElementException("No more elements in the iteration");
            }
            iCanRemove = true;
            iIndex++;
            return this;
        }

        public void remove() {
            if (iCanRemove == false) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            iFlatMap.remove(getKey());
            iIndex--;
            iCanRemove = false;
        }

        public Object getKey() {
            if (iCanRemove == false) {
                throw new IllegalStateException("Map Entry cannot be queried");
            }
            switch (iIndex) {
                case 3:
                    return iFlatMap.iKey3;
                case 2:
                    return iFlatMap.iKey2;
                case 1:
                    return iFlatMap.iKey1;
            }
            throw new IllegalStateException("Invalid map index");
        }

        public Object getValue() {
            if (iCanRemove == false) {
                throw new IllegalStateException("Map Entry cannot be queried");
            }
            switch (iIndex) {
                case 3:
                    return iFlatMap.iValue3;
                case 2:
                    return iFlatMap.iValue2;
                case 1:
                    return iFlatMap.iValue1;
            }
            throw new IllegalStateException("Invalid map index");
        }

        public Object setValue(Object value) {
            if (iCanRemove == false) {
                throw new IllegalStateException("Map Entry cannot be changed");
            }
            Object old = getValue();
            switch (iIndex) {
                case 3: 
                    iFlatMap.iValue3 = value;
                case 2:
                    iFlatMap.iValue2 = value;
                case 1:
                    iFlatMap.iValue1 = value;
            }
            return old;
        }
        
        public boolean equals(Object obj) {
            if (iCanRemove == false) {
                return false;
            }
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            Map.Entry other = (Map.Entry) obj;
            Object key = getKey();
            Object value = getValue();
            return (key == null ? other.getKey() == null : key.equals(other.getKey())) &&
                   (value == null ? other.getValue() == null : value.equals(other.getValue()));
        }
        
        public int hashCode() {
            if (iCanRemove == false) {
                return 0;
            }
            Object key = getKey();
            Object value = getValue();
            return (key == null ? 0 : key.hashCode()) ^
                   (value == null ? 0 : value.hashCode());
        }
        
        public String toString() {
            if (iCanRemove) {
                return getKey() + "=" + getValue();
            } else {
                return "";
            }
        }
    }
    
    /**
     * Gets the keySet view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the keys, use {@link #mapIterator()}.
     * 
     * @return the keySet view
     */
    public Set keySet() {
        if (iMap != null) {
            return iMap.keySet();
        }
        return new KeySet(this);
    }

    /**
     * KeySet
     */
    static class KeySet extends AbstractSet {
        private final Flat3Map iFlatMap;
        
        KeySet(Flat3Map map) {
            super();
            iFlatMap = map;
        }

        public int size() {
            return iFlatMap.size();
        }
        
        public void clear() {
            iFlatMap.clear();
        }
        
        public boolean contains(Object key) {
            return iFlatMap.containsKey(key);
        }

        public boolean remove(Object key) {
            boolean result = iFlatMap.containsKey(key);
            iFlatMap.remove(key);
            return result;
        }

        public Iterator iterator() {
            if (iFlatMap.iMap != null) {
                return iFlatMap.iMap.keySet().iterator();
            }
            if (iFlatMap.size() == 0) {
                return IteratorUtils.EMPTY_ITERATOR;
            }
            return new KeySetIterator(iFlatMap);
        }
    }

    /**
     * KeySetIterator
     */
    static class KeySetIterator extends EntrySetIterator {
        
        KeySetIterator(Flat3Map map) {
            super(map);
        }

        public Object next() {
            super.next();
            return getKey();
        }
    }
    
    /**
     * Gets the values view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the values, use {@link #mapIterator()}.
     * 
     * @return the values view
     */
    public Collection values() {
        if (iMap != null) {
            return iMap.values();
        }
        return new Values(this);
    }

    /**
     * Values
     */
    static class Values extends AbstractCollection {
        private final Flat3Map iFlatMap;
        
        Values(Flat3Map map) {
            super();
            iFlatMap = map;
        }

        public int size() {
            return iFlatMap.size();
        }
        
        public void clear() {
            iFlatMap.clear();
        }
        
        public boolean contains(Object value) {
            return iFlatMap.containsValue(value);
        }

        public Iterator iterator() {
            if (iFlatMap.iMap != null) {
                return iFlatMap.iMap.values().iterator();
            }
            if (iFlatMap.size() == 0) {
                return IteratorUtils.EMPTY_ITERATOR;
            }
            return new ValuesIterator(iFlatMap);
        }
    }

    /**
     * ValuesIterator
     */
    static class ValuesIterator extends EntrySetIterator {
        
        ValuesIterator(Flat3Map map) {
            super(map);
        }

        public Object next() {
            super.next();
            return getValue();
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Compares this map with another.
     * 
     * @param obj  the object to compare to
     * @return true if equal
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (iMap != null) {
            return iMap.equals(obj);
        }
        if (obj instanceof Map == false) {
            return false;
        }
        Map other = (Map) obj;
        if (iSize != other.size()) {
            return false;
        }
        if (iSize > 0) {
            Object otherValue = null;
            switch (iSize) {  // drop through
                case 3:
                    if (other.containsKey(iKey3) == false) {
                        otherValue = other.get(iKey3);
                        if (iValue3 == null ? otherValue != null : !iValue3.equals(otherValue)) {
                            return false;
                        }
                    }
                case 2:
                    if (other.containsKey(iKey2) == false) {
                        otherValue = other.get(iKey2);
                        if (iValue2 == null ? otherValue != null : !iValue2.equals(otherValue)) {
                            return false;
                        }
                    }
                case 1:
                    if (other.containsKey(iKey1) == false) {
                        otherValue = other.get(iKey1);
                        if (iValue1 == null ? otherValue != null : !iValue1.equals(otherValue)) {
                            return false;
                        }
                    }
            }
        }
        return true;
    }

    /**
     * Gets the standard Map hashCode.
     * 
     * @return the hashcode defined in the Map interface
     */
    public int hashCode() {
        if (iMap != null) {
            return iMap.hashCode();
        }
        int total = 0;
        switch (iSize) {  // drop through
            case 3:
                total += (iHash3 ^ (iValue3 == null ? 0 : iValue3.hashCode()));
            case 2:
                total += (iHash2 ^ (iValue2 == null ? 0 : iValue2.hashCode()));
            case 1:
                total += (iHash1 ^ (iValue1 == null ? 0 : iValue1.hashCode()));
        }
        return total;
    }

    /**
     * Gets the map as a String.
     * 
     * @return a string version of the map
     */
    public String toString() {
        if (iMap != null) {
            return iMap.toString();
        }
        if (iSize == 0) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer(128);
        buf.append('{');
        switch (iSize) {  // drop through
            case 3:
                buf.append(iKey3);
                buf.append('=');
                buf.append(iValue3);
                buf.append(',');
            case 2:
                buf.append(iKey2);
                buf.append('=');
                buf.append(iValue2);
                buf.append(',');
            case 1:
                buf.append(iKey1);
                buf.append('=');
                buf.append(iValue1);
        }
        buf.append('}');
        return buf.toString();
    }

}
