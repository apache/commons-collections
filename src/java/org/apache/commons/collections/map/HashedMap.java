/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/map/HashedMap.java,v 1.3 2003/12/02 00:37:11 scolebourne Exp $
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.AMap;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.MapIterator;

/**
 * A <code>Map</code> implementation that is a general purpose replacement
 * for <code>HashMap</code>.
 * <p>
 * This implementation improves on the JDK1.4 HahMap by adding the 
 * {@link org.apache.commons.collections.iterators.MapIterator MapIterator}
 * functionality and improving performance of <code>putAll</code>.
 * <p>
 * The implementation is also designed to be subclassed, with lots of useful
 * methods exposed.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/12/02 00:37:11 $
 *
 * @author java util HashMap
 * @author Stephen Colebourne
 */
public class HashedMap implements AMap, Serializable, Cloneable {
    
    /** Serialisation version */
    static final long serialVersionUID = -1593250834999590599L;
    /** The default capacity to use */
    protected static final int DEFAULT_CAPACITY = 16;
    /** The default load factor to use */
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
    /** The maximum capacity allowed */
    protected static final int MAXIMUM_CAPACITY = 1 << 30;
    /** An object for masking null */
    protected static final Object NULL = new Object();
    
    /** Load factor, normally 0.75 */
    private final float loadFactor;
    /** The size of the map */
    private transient int size;
    /** Map entries */
    private transient HashEntry[] data;
    /** Size at which to rehash */
    private transient int threshold;
    /** Modification count for iterators */
    private transient int modCount;
    /** Entry set */
    private transient EntrySet entrySet;
    /** Key set */
    private transient KeySet keySet;
    /** Values */
    private transient Values values;

    /**
     * Constructs a new empty map with default size and load factor.
     */
    public HashedMap() {
        super();
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = calculateThreshold(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
        this.data = new HashEntry[DEFAULT_CAPACITY];
    }

    /**
     * Constructs a new, empty map with the specified initial capacity. 
     *
     * @param initialCapacity  the initial capacity
     * @throws IllegalArgumentException if the initial capacity is less than one
     */
    public HashedMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
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
    public HashedMap(int initialCapacity, float loadFactor) {
        super();
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor must be greater than 0");
        }
        this.loadFactor = loadFactor;
        this.threshold = calculateThreshold(initialCapacity, loadFactor);
        initialCapacity = calculateNewCapacity(initialCapacity);
        this.data = new HashEntry[initialCapacity];
    }

    /**
     * Constructor copying elements from another map.
     *
     * @param map  the map to copy
     * @throws NullPointerException if the map is null
     */
    public HashedMap(Map map) {
        this(Math.max(2 * map.size(), DEFAULT_CAPACITY), DEFAULT_LOAD_FACTOR);
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
        key = convertKey(key);
        int hashCode = hash(key);
        HashEntry entry = data[hashIndex(hashCode, data.length)]; // no local for hash index
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(key, entry.key)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Gets the size of the map.
     * 
     * @return the size
     */
    public int size() {
        return size;
    }

    /**
     * Checks whether the map is currently empty.
     * 
     * @return true if the map is currently size zero
     */
    public boolean isEmpty() {
        return (size == 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the map contains the specified key.
     * 
     * @param key  the key to search for
     * @return true if the map contains the key
     */
    public boolean containsKey(Object key) {
        key = convertKey(key);
        int hashCode = hash(key);
        HashEntry entry = data[hashIndex(hashCode, data.length)]; // no local for hash index
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(key, entry.key)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    /**
     * Checks whether the map contains the specified value.
     * 
     * @param value  the value to search for
     * @return true if the map contains the value
     */
    public boolean containsValue(Object value) {
        if (value == null) {
            for (int i = 0, isize = data.length; i < isize; i++) {
                HashEntry entry = data[i];
                while (entry != null) {
                    if (entry.getValue() == null) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        } else {
            for (int i = 0, isize = data.length; i < isize; i++) {
                HashEntry entry = data[i];
                while (entry != null) {
                    if (isEqualValue(value, entry.getValue())) {
                        return true;
                    }
                    entry = entry.next;
                }
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
        key = convertKey(key);
        int hashCode = hash(key);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(key, entry.key)) {
                Object oldValue = entry.getValue();
                entry.setValue(value);
                return oldValue;
            }
            entry = entry.next;
        }
        
        modCount++;
        add(hashCode, index, key, value);
        return null;
    }

    /**
     * Puts all the values from the specified map into this map.
     * 
     * @param map  the map to add
     * @throws NullPointerException if the map is null
     */
    public void putAll(Map map) {
        int mapSize = map.size();
        if (mapSize == 0) {
            return;
        }
        ensureCapacity(calculateNewCapacity(size + mapSize));
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Removes the specified mapping from this map.
     * 
     * @param key  the mapping to remove
     * @return the value mapped to the removed key, null if key not in map
     */
    public Object remove(Object key) {
        key = convertKey(key);
        int hashCode = hash(key);
        int index = hashIndex(hashCode, data.length);
        HashEntry entry = data[index]; 
        HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(key, entry.key)) {
                modCount++;
                if (previous == null) {
                    data[index] = entry.next;
                } else {
                    previous.next = entry.next;
                }
                size--;
                return destroyEntry(entry);
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Clears the map, resetting the size to zero and nullifying references
     * to avoid garbage collection issues.
     */
    public void clear() {
        modCount++;
        HashEntry[] data = this.data;
        for (int i = data.length - 1; i >= 0; i--) {
            data[i] = null;
        }
        size = 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts input keys to another object for storage in the map.
     * This implementation masks nulls.
     * Subclasses can override this to perform alternate key conversions.
     * <p>
     * The reverse conversion can be changed, if required, by overriding the
     * getKey() method in the hash entry.
     * 
     * @param key  the key to get a hash code for
     * @return the hash code
     */
    protected Object convertKey(Object key) {
        return (key == null ? NULL : key);
    }
    
    /**
     * Gets the hash code for the key specified.
     * This implementation uses the additional hashing routine from JDK1.4.
     * Subclasses can override this to return alternate hash codes.
     * 
     * @param key  the key to get a hash code for
     * @return the hash code
     */
    protected int hash(Object key) {
        // same as JDK 1.4
        int h = key.hashCode();
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }
    
    /**
     * Compares two keys for equals.
     * This implementation uses the equals method.
     * Subclasses can override this to match differently.
     * 
     * @param key1  the first key to compare
     * @param key2  the second key to compare
     * @return true if equal
     */
    protected boolean isEqualKey(Object key1, Object key2) {
        return (key1 == key2 || key1.equals(key2));
    }
    
    /**
     * Compares two values for equals.
     * This implementation uses the equals method.
     * Subclasses can override this to match differently.
     * 
     * @param value1  the first value to compare
     * @param value2  the second value to compare
     * @return true if equal
     */
    protected boolean isEqualValue(Object value1, Object value2) {
        return (value1 == value2 || value1.equals(value2));
    }
    
    /**
     * Gets the index into the data storage for the hashCode specified.
     * This implementation uses the least significant bits of the hashCode.
     * Subclasses can override this to return alternate bucketing.
     * 
     * @param hashCode  the hash code to use
     * @param dataSize  the size of the data to pick a bucket from
     * @return the bucket index
     */
    protected int hashIndex(int hashCode, int dataSize) {
        return hashCode & (dataSize - 1);
    }
    
    /**
     * Creates an entry to store the data.
     * This implementation creates a HashEntry instance.
     * Subclasses can override this to return a different storage class,
     * or implement caching.
     * 
     * @param next  the next entry in sequence
     * @param hashCode  the hash code to use
     * @param key  the key to store
     * @param value  the value to store
     * @return the newly created entry
     */
    protected HashEntry createEntry(HashEntry next, int hashCode, Object key, Object value) {
        return new HashEntry(next, hashCode, key, value);
    }
    
    /**
     * Kills an entry ready for the garbage collector.
     * This implementation prepares the HashEntry for garbage collection.
     * Subclasses can override this to implement caching (override clear as well).
     * 
     * @param entry  the entry to destroy
     * @return the value from the entry
     */
    protected Object destroyEntry(HashEntry entry) {
        entry.next = null;
        return entry.value;
    }
    
    /**
     * Adds a new key-value mapping into this map.
     * Subclasses could override to fix the size of the map.
     * 
     * @param key  the key to add
     * @param value  the value to add
     * @return the value previously mapped to this key, null if none
     */
    protected void add(int hashCode, int hashIndex, Object key, Object value) {
        data[hashIndex] = createEntry(data[hashIndex], hashCode, key, value);
        if (size++ >= threshold) {
            ensureCapacity(data.length * 2);
        }
    }
    
    /**
     * Changes the size of the data structure to the capacity proposed.
     * 
     * @param newCapacity  the new capacity of the array
     */
    protected void ensureCapacity(int newCapacity) {
        int oldCapacity = data.length;
        if (newCapacity <= oldCapacity) {
            return;
        }
        HashEntry oldEntries[] = data;
        HashEntry newEntries[] = new HashEntry[newCapacity];

        modCount++;
        for (int i = oldCapacity - 1; i >= 0; i--) {
            HashEntry entry = oldEntries[i];
            if (entry != null) {
                oldEntries[i] = null;  // gc
                do {
                    HashEntry next = entry.next;
                    int index = hashIndex(entry.hashCode, newCapacity);  
                    entry.next = newEntries[index];
                    newEntries[index] = entry;
                    entry = next;
                } while (entry != null);
            }
        }
        threshold = calculateThreshold(newCapacity, loadFactor);
        data = newEntries;
    }

    /**
     * Calculates the new capacity of the map.
     * This implementation normalizes the capacity to a power of two.
     * 
     * @param proposedCapacity  the proposed capacity
     * @return the normalized new capacity
     */
    protected int calculateNewCapacity(int proposedCapacity) {
        int newCapacity = 1;
        if (proposedCapacity > MAXIMUM_CAPACITY) {
            newCapacity = MAXIMUM_CAPACITY;
        } else {
            while (newCapacity < proposedCapacity) {
                newCapacity <<= 1;  // multiply by two
            }
            if (proposedCapacity > MAXIMUM_CAPACITY) {
                newCapacity = MAXIMUM_CAPACITY;
            }
        }
        return newCapacity;
    }
    
    /**
     * Calculates the new threshold of the map, where it will be resized.
     * This implementation uses the load factor.
     * 
     * @param newCapacity  the new capacity
     * @param factor  the load factor
     * @return the new resize threshold
     */
    protected int calculateThreshold(int newCapacity, float factor) {
        return (int) (newCapacity * factor);
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
        if (size == 0) {
            return IteratorUtils.EMPTY_MAP_ITERATOR;
        }
        return new HashMapIterator(this);
    }

    /**
     * MapIterator
     */
    static class HashMapIterator extends HashIterator implements MapIterator {
        
        HashMapIterator(HashedMap map) {
            super(map);
        }

        public Object next() {
            return super.nextEntry().getKey();
        }

        public Object getKey() {
            HashEntry current = currentEntry();
            if (current == null) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            return current.getKey();
        }

        public Object getValue() {
            HashEntry current = currentEntry();
            if (current == null) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            return current.getValue();
        }

        public Object setValue(Object value) {
            HashEntry current = currentEntry();
            if (current == null) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            return current.setValue(value);
        }
    }
    
    //-----------------------------------------------------------------------    
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
        if (entrySet == null) {
            entrySet = new EntrySet(this);
        }
        return entrySet;
    }
    
    /**
     * Creates an entry set iterator.
     * Subclasses can override this to return iterators with different properties.
     * 
     * @return the entrySet iterator
     */
    protected Iterator createEntrySetIterator() {
        if (size() == 0) {
            return IteratorUtils.EMPTY_ITERATOR;
        }
        return new EntrySetIterator(this);
    }

    /**
     * EntrySet
     */
    static class EntrySet extends AbstractSet {
        private final HashedMap map;
        
        EntrySet(HashedMap map) {
            super();
            this.map = map;
        }

        public int size() {
            return map.size();
        }
        
        public void clear() {
            map.clear();
        }
        
        public boolean contains(Object entry) {
            if (entry instanceof Map.Entry) {
                return map.containsKey(((Map.Entry) entry).getKey());
            }
            return false;
        }
        
        public boolean remove(Object obj) {
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Object key = entry.getKey();
            boolean result = map.containsKey(key);
            map.remove(key);
            return result;
        }

        public Iterator iterator() {
            return map.createEntrySetIterator();
        }
    }

    /**
     * EntrySetIterator and MapEntry
     */
    static class EntrySetIterator extends HashIterator {
        
        EntrySetIterator(HashedMap map) {
            super(map);
        }

        public Object next() {
            return super.nextEntry();
        }
    }

    //-----------------------------------------------------------------------    
    /**
     * Gets the keySet view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the keys, use {@link #mapIterator()}.
     * 
     * @return the keySet view
     */
    public Set keySet() {
        if (keySet == null) {
            keySet = new KeySet(this);
        }
        return keySet;
    }

    /**
     * Creates a key set iterator.
     * Subclasses can override this to return iterators with different properties.
     * 
     * @return the keySet iterator
     */
    protected Iterator createKeySetIterator() {
        if (size() == 0) {
            return IteratorUtils.EMPTY_ITERATOR;
        }
        return new KeySetIterator(this);
    }

    /**
     * KeySet
     */
    static class KeySet extends AbstractSet {
        private final HashedMap map;
        
        KeySet(HashedMap map) {
            super();
            this.map = map;
        }

        public int size() {
            return map.size();
        }
        
        public void clear() {
            map.clear();
        }
        
        public boolean contains(Object key) {
            return map.containsKey(key);
        }
        
        public boolean remove(Object key) {
            boolean result = map.containsKey(key);
            map.remove(key);
            return result;
        }

        public Iterator iterator() {
            return map.createKeySetIterator();
        }
    }

    /**
     * KeySetIterator
     */
    static class KeySetIterator extends EntrySetIterator {
        
        KeySetIterator(HashedMap map) {
            super(map);
        }

        public Object next() {
            return super.nextEntry().getKey();
        }
    }
    
    //-----------------------------------------------------------------------    
    /**
     * Gets the values view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the values, use {@link #mapIterator()}.
     * 
     * @return the values view
     */
    public Collection values() {
        if (values == null) {
            values = new Values(this);
        }
        return values;
    }

    /**
     * Creates a values iterator.
     * Subclasses can override this to return iterators with different properties.
     * 
     * @return the values iterator
     */
    protected Iterator createValuesIterator() {
        if (size() == 0) {
            return IteratorUtils.EMPTY_ITERATOR;
        }
        return new ValuesIterator(this);
    }

    /**
     * Values
     */
    static class Values extends AbstractCollection {
        private final HashedMap map;
        
        Values(HashedMap map) {
            super();
            this.map = map;
        }

        public int size() {
            return map.size();
        }
        
        public void clear() {
            map.clear();
        }
        
        public boolean contains(Object value) {
            return map.containsValue(value);
        }
        
        public Iterator iterator() {
            return map.createValuesIterator();
        }
    }

    /**
     * ValuesIterator
     */
    static class ValuesIterator extends HashIterator {
        
        ValuesIterator(HashedMap map) {
            super(map);
        }

        public Object next() {
            return super.nextEntry().getValue();
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * HashEntry
     */
    protected static class HashEntry implements Map.Entry {
        protected HashEntry next;
        protected int hashCode;
        protected Object key;
        protected Object value;
        
        protected HashEntry(HashEntry next, int hashCode, Object key, Object value) {
            super();
            this.next = next;
            this.hashCode = hashCode;
            this.key = key;
            this.value = value;
        }
        public Object getKey() {
            return (key == NULL ? null : key);
        }
        public Object getValue() {
            return value;
        }
        public Object setValue(Object value) {
            Object old = this.value;
            this.value = value;
            return old;
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
                (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey())) &&
                (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
        }
        public int hashCode() {
            return (getKey() == null ? 0 : getKey().hashCode()) ^
                   (getValue() == null ? 0 : getValue().hashCode()); 
        }
        public String toString() {
            return new StringBuffer().append(getKey()).append('=').append(getValue()).toString();
        }
    }
    
    /**
     * Base Iterator
     */
    protected static abstract class HashIterator implements Iterator {
        private final HashedMap map;
        private int hashIndex;
        private HashEntry current;
        private HashEntry next;
        private int expectedModCount;
        
        protected HashIterator(HashedMap map) {
            super();
            this.map = map;
            HashEntry[] data = map.data;
            int i = data.length;
            HashEntry next = null;
            while (i > 0 && next == null) {
                next = data[--i];
            }
            this.next = next;
            this.hashIndex = i;
            this.expectedModCount = map.modCount;
        }

        public boolean hasNext() {
            return (next != null);
        }

        protected HashEntry nextEntry() { 
            if (map.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            HashEntry newCurrent = next;
            if (newCurrent == null)  {
                throw new NoSuchElementException("No more elements in the iteration");
            }
            HashEntry[] data = map.data;
            int i = hashIndex;
            HashEntry n = newCurrent.next;
            while (n == null && i > 0) {
                n = data[--i];
            }
            next = n;
            hashIndex = i;
            current = newCurrent;
            return newCurrent;
        }

        protected HashEntry currentEntry() {
            return current;
        }
        
        public void remove() {
            if (current == null) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            if (map.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            map.remove(current.getKey());
            current = null;
            expectedModCount = map.modCount;
        }

        public String toString() {
            if (current != null) {
                return "Iterator[" + current.getKey() + "=" + current.getValue() + "]";
            } else {
                return "Iterator[]";
            }
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Write the map out using a custom routine.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(data.length);
        out.writeInt(size);
        for (MapIterator it = mapIterator(); it.hasNext();) {
            out.writeObject(it.next());
            out.writeObject(it.getValue());
        }
    }

    /**
     * Read the map in using a custom routine.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int capacity = in.readInt();
        int size = in.readInt();
        data = new HashEntry[capacity];
        for (int i = 0; i < size; i++) {
            Object key = in.readObject();
            Object value = in.readObject();
            put(key, value);
        }
    }
    //-----------------------------------------------------------------------
    /**
     * Clones the map without cloning the keys or values.
     *
     * @return a shallow clone
     */
    public Object clone() {
        try {
            HashedMap cloned = (HashedMap) super.clone();
            cloned.data = new HashEntry[data.length];
            cloned.entrySet = null;
            cloned.keySet = null;
            cloned.values = null;
            cloned.modCount = 0;
            cloned.size = 0;
            cloned.putAll(this);
            return cloned;
            
        } catch (CloneNotSupportedException ex) {
            return null;  // should never happen
        }
    }
    
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
        if (obj instanceof Map == false) {
            return false;
        }
        Map map = (Map) obj;
        if (map.size() != size()) {
            return false;
        }
        MapIterator it = mapIterator();
        try {
            while (it.hasNext()) {
                Object key = it.next();
                Object value = it.getValue();
                if (value == null) {
                    if (map.get(key) != null || map.containsKey(key) == false) {
                        return false;
                    }
                } else {
                    if (value.equals(map.get(key)) == false) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException ignored)   {
            return false;
        } catch (NullPointerException ignored) {
            return false;
        }
        return true;
    }

    /**
     * Gets the standard Map hashCode.
     * 
     * @return the hashcode defined in the Map interface
     */
    public int hashCode() {
        int total = 0;
        Iterator it = createEntrySetIterator();
        while (it.hasNext()) {
            total += it.next().hashCode();
        }
        return total;
    }

    /**
     * Gets the map as a String.
     * 
     * @return a string version of the map
     */
    public String toString() {
        if (size() == 0) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer(32 * size());
        buf.append('{');

        MapIterator it = mapIterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object key = it.next();
            Object value = it.getValue();
            buf.append(key == this ? "(this Map)" : key)
               .append('=')
               .append(value == this ? "(this Map)" : value);

            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(',').append(' ');
            }
        }

        buf.append('}');
        return buf.toString();
    }
}
