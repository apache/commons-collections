/*
 *  Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.keyvalue.MultiKey;

/**
 * A <code>Map</code> implementation that uses multiple keys to map the value.
 * <p>
 * This class is the most efficient way to uses multiple keys to map to a value.
 * The best way to use this class is via the additional map-style methods.
 * These provide <code>get</code>, <code>containsKey</code>, <code>put</code> and
 * <code>remove</code> for individual keys which operate without extra object creation.
 * <p>
 * The additional methods are the main interface of this map.
 * As such, you will not mormally hold this map in a variable of type <code>Map</code>.
 * The normal map methods take in and return a {@link MultiKey}.
 * <p>
 * As an example, consider a cache that uses a String airline code and a Locale
 * to lookup the airline's name:
 * <pre>
 * public String getAirlineName(String code, String locale) {
 *   MultiKeyMap cache = getCache();
 *   String name = (String) cache.get(code, locale);
 *   if (name == null) {
 *     name = getAirlineNameFromDB(code, locale);
 *     cache.put(code, locale, name);
 *   }
 *   return name;
 * }
 * </pre>
 *
 * @since Commons Collections 3.1
 * @version $Revision: 1.1 $ $Date: 2004/04/12 12:05:30 $
 *
 * @author Stephen Colebourne
 */
public class MultiKeyMap
        extends AbstractHashedMap implements Serializable, Cloneable {

    /** Serialisation version */
    private static final long serialVersionUID = -1788199231038721040L;
    
    /**
     * Constructs a new empty map with default size and load factor.
     */
    public MultiKeyMap() {
        super(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_THRESHOLD);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity. 
     *
     * @param initialCapacity  the initial capacity
     * @throws IllegalArgumentException if the initial capacity is less than one
     */
    public MultiKeyMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor. 
     *
     * @param initialCapacity  the initial capacity
     * @param loadFactor  the load factor
     * @throws IllegalArgumentException if the initial capacity is less than one
     * @throws IllegalArgumentException if the load factor is less than zero
     */
    public MultiKeyMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructor copying elements from another map.
     *
     * @param map  the map to copy
     * @throws NullPointerException if the map is null
     */
    public MultiKeyMap(Map map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Clones the map without cloning the keys or values.
     *
     * @return a shallow clone
     */
    public Object clone() {
        return super.clone();
    }
    
    /**
     * Write the map out using a custom routine.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

    /**
     * Read the map in using a custom routine.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        doReadObject(in);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value mapped to the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @return the mapped value, null if no match
     */
    public Object get(Object key1, Object key2) {
        int hashCode = hash(key1, key2);
        HashEntry entry = data[hashIndex(hashCode, data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Checks whether the map contains the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @return true if the map contains the key
     */
    public boolean containsKey(Object key1, Object key2) {
        int hashCode = hash(key1, key2);
        HashEntry entry = data[hashIndex(hashCode, data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    /**
     * Stores the value against the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param value  the value to store
     * @return the value previously mapped to this combined key, null if none
     */
    public Object put(Object key1, Object key2, Object value) {
        int hashCode = hash(key1, key2);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2)) {
                Object oldValue = entry.getValue();
                updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        
        addMapping(index, hashCode, new MultiKey(key1, key2), value);
        return null;
    }

    /**
     * Removes the specified multi-key from this map.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @return the value mapped to the removed key, null if key not in map
     */
    public Object remove(Object key1, Object key2) {
        int hashCode = hash(key1, key2);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index];
        HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2)) {
                Object oldValue = entry.getValue();
                removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Gets the hash code for the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @return the hash code
     */
    protected int hash(Object key1, Object key2) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }

    /**
     * Is the key equal to the combined key.
     * 
     * @param entry  the entry to compare to
     * @param key1  the first key
     * @param key2  the second key
     * @return true if the key matches
     */
    protected boolean isEqualKey(HashEntry entry, Object key1, Object key2) {
        MultiKey multi = (MultiKey) entry.getKey();
        return
            multi.size() == 2 &&
            (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
            (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1)));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value mapped to the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @return the mapped value, null if no match
     */
    public Object get(Object key1, Object key2, Object key3) {
        int hashCode = hash(key1, key2, key3);
        HashEntry entry = data[hashIndex(hashCode, data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Checks whether the map contains the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @return true if the map contains the key
     */
    public boolean containsKey(Object key1, Object key2, Object key3) {
        int hashCode = hash(key1, key2, key3);
        HashEntry entry = data[hashIndex(hashCode, data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    /**
     * Stores the value against the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param value  the value to store
     * @return the value previously mapped to this combined key, null if none
     */
    public Object put(Object key1, Object key2, Object key3, Object value) {
        int hashCode = hash(key1, key2, key3);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3)) {
                Object oldValue = entry.getValue();
                updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        
        addMapping(index, hashCode, new MultiKey(key1, key2, key3), value);
        return null;
    }

    /**
     * Removes the specified multi-key from this map.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @return the value mapped to the removed key, null if key not in map
     */
    public Object remove(Object key1, Object key2, Object key3) {
        int hashCode = hash(key1, key2, key3);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index];
        HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3)) {
                Object oldValue = entry.getValue();
                removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Gets the hash code for the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @return the hash code
     */
    protected int hash(Object key1, Object key2, Object key3) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }

    /**
     * Is the key equal to the combined key.
     * 
     * @param entry  the entry to compare to
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @return true if the key matches
     */
    protected boolean isEqualKey(HashEntry entry, Object key1, Object key2, Object key3) {
        MultiKey multi = (MultiKey) entry.getKey();
        return
            multi.size() == 3 &&
            (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
            (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
            (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2)));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value mapped to the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @return the mapped value, null if no match
     */
    public Object get(Object key1, Object key2, Object key3, Object key4) {
        int hashCode = hash(key1, key2, key3, key4);
        HashEntry entry = data[hashIndex(hashCode, data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Checks whether the map contains the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @return true if the map contains the key
     */
    public boolean containsKey(Object key1, Object key2, Object key3, Object key4) {
        int hashCode = hash(key1, key2, key3, key4);
        HashEntry entry = data[hashIndex(hashCode, data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    /**
     * Stores the value against the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @param value  the value to store
     * @return the value previously mapped to this combined key, null if none
     */
    public Object put(Object key1, Object key2, Object key3, Object key4, Object value) {
        int hashCode = hash(key1, key2, key3, key4);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4)) {
                Object oldValue = entry.getValue();
                updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        
        addMapping(index, hashCode, new MultiKey(key1, key2, key3, key4), value);
        return null;
    }

    /**
     * Removes the specified multi-key from this map.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @return the value mapped to the removed key, null if key not in map
     */
    public Object remove(Object key1, Object key2, Object key3, Object key4) {
        int hashCode = hash(key1, key2, key3, key4);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index];
        HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4)) {
                Object oldValue = entry.getValue();
                removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Gets the hash code for the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @return the hash code
     */
    protected int hash(Object key1, Object key2, Object key3, Object key4) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        if (key4 != null) {
            h ^= key4.hashCode();
        }
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }

    /**
     * Is the key equal to the combined key.
     * 
     * @param entry  the entry to compare to
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @return true if the key matches
     */
    protected boolean isEqualKey(HashEntry entry, Object key1, Object key2, Object key3, Object key4) {
        MultiKey multi = (MultiKey) entry.getKey();
        return
            multi.size() == 4 &&
            (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
            (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
            (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) &&
            (key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3)));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value mapped to the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @param key5  the fifth key
     * @return the mapped value, null if no match
     */
    public Object get(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int hashCode = hash(key1, key2, key3, key4, key5);
        HashEntry entry = data[hashIndex(hashCode, data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4, key5)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Checks whether the map contains the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @param key5  the fifth key
     * @return true if the map contains the key
     */
    public boolean containsKey(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int hashCode = hash(key1, key2, key3, key4, key5);
        HashEntry entry = data[hashIndex(hashCode, data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4, key5)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    /**
     * Stores the value against the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @param key5  the fifth key
     * @param value  the value to store
     * @return the value previously mapped to this combined key, null if none
     */
    public Object put(Object key1, Object key2, Object key3, Object key4, Object key5, Object value) {
        int hashCode = hash(key1, key2, key3, key4, key5);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4, key5)) {
                Object oldValue = entry.getValue();
                updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        
        addMapping(index, hashCode, new MultiKey(key1, key2, key3, key4, key5), value);
        return null;
    }

    /**
     * Removes the specified multi-key from this map.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @param key5  the fifth key
     * @return the value mapped to the removed key, null if key not in map
     */
    public Object remove(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int hashCode = hash(key1, key2, key3, key4, key5);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index];
        HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, key1, key2, key3, key4, key5)) {
                Object oldValue = entry.getValue();
                removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Gets the hash code for the specified multi-key.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @param key5  the fifth key
     * @return the hash code
     */
    protected int hash(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        if (key4 != null) {
            h ^= key4.hashCode();
        }
        if (key5 != null) {
            h ^= key5.hashCode();
        }
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }

    /**
     * Is the key equal to the combined key.
     * 
     * @param entry  the entry to compare to
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @param key5  the fifth key
     * @return true if the key matches
     */
    protected boolean isEqualKey(HashEntry entry, Object key1, Object key2, Object key3, Object key4, Object key5) {
        MultiKey multi = (MultiKey) entry.getKey();
        return
            multi.size() == 5 &&
            (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
            (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
            (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) &&
            (key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3))) &&
            (key5 == null ? multi.getKey(4) == null : key5.equals(multi.getKey(4)));
    }

    //-----------------------------------------------------------------------
    /**
     * Removes all mappings where the first key is that specified.
     * <p>
     * This method removes all the mappings where the <code>MultiKey</code>
     * has one or more keys, and the first matches that specified.
     * 
     * @param key1  the first key
     * @return true if any elements were removed
     */
    public boolean removeAll(Object key1) {
        boolean modified = false;
        MapIterator it = mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey) it.next();
            if (multi.size() >= 1 &&
                (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0)))) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes all mappings where the first two keys are those specified.
     * <p>
     * This method removes all the mappings where the <code>MultiKey</code>
     * has two or more keys, and the first two match those specified.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @return true if any elements were removed
     */
    public boolean removeAll(Object key1, Object key2) {
        boolean modified = false;
        MapIterator it = mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey) it.next();
            if (multi.size() >= 2 &&
                (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
                (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1)))) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes all mappings where the first three keys are those specified.
     * <p>
     * This method removes all the mappings where the <code>MultiKey</code>
     * has three or more keys, and the first three match those specified.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @return true if any elements were removed
     */
    public boolean removeAll(Object key1, Object key2, Object key3) {
        boolean modified = false;
        MapIterator it = mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey) it.next();
            if (multi.size() >= 3 &&
                (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
                (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
                (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2)))) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes all mappings where the first four keys are those specified.
     * <p>
     * This method removes all the mappings where the <code>MultiKey</code>
     * has four or more keys, and the first four match those specified.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @return true if any elements were removed
     */
    public boolean removeAll(Object key1, Object key2, Object key3, Object key4) {
        boolean modified = false;
        MapIterator it = mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey) it.next();
            if (multi.size() >= 4 &&
                (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) &&
                (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) &&
                (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) &&
                (key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3)))) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    //-----------------------------------------------------------------------
    /**
     * Override superclass to ensure that input keys are valid MultiKey objects.
     * 
     * @param key  the key to check
     * @return the validated key
     */
    protected Object convertKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null");
        }
        if (key instanceof MultiKey == false) {
            throw new ClassCastException("Key must be a MultiKey");
        }
        return key;
    }

}
