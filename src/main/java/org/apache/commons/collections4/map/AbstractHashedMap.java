/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;

/**
 * An abstract implementation of a hash-based map which provides numerous points for
 * subclasses to override.
 * <p>
 * This class implements all the features necessary for a subclass hash-based map.
 * Key-value entries are stored in instances of the {@code HashEntry} class,
 * which can be overridden and replaced. The iterators can similarly be replaced,
 * without the need to replace the KeySet, EntrySet and Values view classes.
 * </p>
 * <p>
 * Overridable methods are provided to change the default hashing behavior, and
 * to change how entries are added to and removed from the map. Hopefully, all you
 * need for unusual subclasses is here.
 * </p>
 * <p>
 * NOTE: From Commons Collections 3.1 this class extends AbstractMap.
 * This is to provide backwards compatibility for ReferenceMap between v3.0 and v3.1.
 * This extends clause will be removed in v5.0.
 * </p>
 *
 * @param <K> the type of the keys in this map
 * @param <V> the type of the values in this map
 * @since 3.0
 */
public class AbstractHashedMap<K, V> extends AbstractMap<K, V> implements IterableMap<K, V> {

    /**
     * EntrySet implementation.
     *
     * @param <K> the type of the keys in the map
     * @param <V> the type of the values in the map
     */
    protected static class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {

        /** The parent map */
        private final AbstractHashedMap<K, V> parent;

        /**
         * Constructs a new instance.
         *
         * @param parent The parent map.
         */
        protected EntrySet(final AbstractHashedMap<K, V> parent) {
            this.parent = parent;
        }

        @Override
        public void clear() {
            parent.clear();
        }

        @Override
        public boolean contains(final Object entry) {
            if (entry instanceof Map.Entry) {
                final Map.Entry<?, ?> e = (Map.Entry<?, ?>) entry;
                final Entry<K, V> match = parent.getEntry(e.getKey());
                return match != null && match.equals(e);
            }
            return false;
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return parent.createEntrySetIterator();
        }

        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            if (!contains(obj)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) obj;
            parent.remove(entry.getKey());
            return true;
        }

        @Override
        public int size() {
            return parent.size();
        }
    }

    /**
     * EntrySet iterator.
     *
     * @param <K> the type of the keys in the map
     * @param <V> the type of the values in the map
     */
    protected static class EntrySetIterator<K, V> extends HashIterator<K, V> implements Iterator<Map.Entry<K, V>> {

        /**
         * Constructs a new instance.
         *
         * @param parent The parent map.
         */
        protected EntrySetIterator(final AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
        public Map.Entry<K, V> next() {
            return super.nextEntry();
        }
    }

    /**
     * HashEntry used to store the data.
     * <p>
     * If you subclass {@code AbstractHashedMap} but not {@code HashEntry}
     * then you will not be able to access the protected fields.
     * The {@code entryXxx()} methods on {@code AbstractHashedMap} exist
     * to provide the necessary access.
     * </p>
     *
     * @param <K> the type of the keys
     * @param <V> the type of the values
     */
    protected static class HashEntry<K, V> implements Map.Entry<K, V>, KeyValue<K, V> {

        /** The next entry in the hash chain */
        protected HashEntry<K, V> next;

        /** The hash code of the key */
        protected int hashCode;

        /** The key */
        protected Object key;

        /** The value */
        protected Object value;

        /**
         * Constructs a new instance.
         *
         * @param next next.
         * @param hashCode hash code.
         * @param key key.
         * @param value value.
         */
        protected HashEntry(final HashEntry<K, V> next, final int hashCode, final Object key, final V value) {
            this.next = next;
            this.hashCode = hashCode;
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            return
                Objects.equals(getKey(), other.getKey()) &&
                Objects.equals(getValue(), other.getValue());
        }

        @Override
        @SuppressWarnings("unchecked")
        public K getKey() {
            if (key == NULL) {
                return null;
            }
            return (K) key;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V getValue() {
            return (V) value;
        }

        @Override
        public int hashCode() {
            return (getKey() == null ? 0 : getKey().hashCode()) ^
                   (getValue() == null ? 0 : getValue().hashCode());
        }

        @Override
        @SuppressWarnings("unchecked")
        public V setValue(final V value) {
            final Object old = this.value;
            this.value = value;
            return (V) old;
        }

        @Override
        public String toString() {
            return new StringBuilder().append(getKey()).append('=').append(getValue()).toString();
        }
    }
    /**
     * Base Iterator.
     *
     * @param <K> the type of the keys in the map
     * @param <V> the type of the values in the map
     */
    protected abstract static class HashIterator<K, V> {

        /** The parent map */
        private final AbstractHashedMap<K, V> parent;

        /** The current index into the array of buckets */
        private int hashIndex;

        /** The last returned entry */
        private HashEntry<K, V> last;

        /** The next entry */
        private HashEntry<K, V> next;

        /** The modification count expected */
        private int expectedModCount;

        /**
         * Constructs a new instance.
         *
         * @param parent The parent AbstractHashedMap.
         */
        protected HashIterator(final AbstractHashedMap<K, V> parent) {
            this.parent = parent;
            final HashEntry<K, V>[] data = parent.data;
            int i = data.length;
            HashEntry<K, V> next = null;
            while (i > 0 && next == null) {
                next = data[--i];
            }
            this.next = next;
            this.hashIndex = i;
            this.expectedModCount = parent.modCount;
        }

        /**
         * Gets the current entry.
         *
         * @return the current entry.
         */
        protected HashEntry<K, V> currentEntry() {
            return last;
        }

        /**
         * Tests whether there is a next entry.
         *
         * @return whether there is a next entry.
         */
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Gets the next entry.
         *
         * @return the next entry.
         */
        protected HashEntry<K, V> nextEntry() {
            if (parent.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            final HashEntry<K, V> newCurrent = next;
            if (newCurrent == null)  {
                throw new NoSuchElementException(NO_NEXT_ENTRY);
            }
            final HashEntry<K, V>[] data = parent.data;
            int i = hashIndex;
            HashEntry<K, V> n = newCurrent.next;
            while (n == null && i > 0) {
                n = data[--i];
            }
            next = n;
            hashIndex = i;
            last = newCurrent;
            return newCurrent;
        }

        /**
         * Removes the current element.
         */
        public void remove() {
            if (last == null) {
                throw new IllegalStateException(REMOVE_INVALID);
            }
            if (parent.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            parent.remove(last.getKey());
            last = null;
            expectedModCount = parent.modCount;
        }

        @Override
        public String toString() {
            if (last != null) {
                return "Iterator[" + last.getKey() + "=" + last.getValue() + "]";
            }
            return "Iterator[]";
        }
    }

    /**
     * MapIterator implementation.
     *
     * @param <K> the type of the keys in the map
     * @param <V> the type of the values in the map
     */
    protected static class HashMapIterator<K, V> extends HashIterator<K, V> implements MapIterator<K, V> {

        /**
         * Constructs a new instance.
         *
         * @param parent The parent AbstractHashedMap.
         */
        protected HashMapIterator(final AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
        public K getKey() {
            final HashEntry<K, V> current = currentEntry();
            if (current == null) {
                throw new IllegalStateException(GETKEY_INVALID);
            }
            return current.getKey();
        }

        @Override
        public V getValue() {
            final HashEntry<K, V> current = currentEntry();
            if (current == null) {
                throw new IllegalStateException(GETVALUE_INVALID);
            }
            return current.getValue();
        }

        @Override
        public K next() {
            return super.nextEntry().getKey();
        }

        @Override
        public V setValue(final V value) {
            final HashEntry<K, V> current = currentEntry();
            if (current == null) {
                throw new IllegalStateException(SETVALUE_INVALID);
            }
            return current.setValue(value);
        }
    }

    /**
     * KeySet implementation.
     *
     * @param <K> the type of elements maintained by this set
     */
    protected static class KeySet<K> extends AbstractSet<K> {

        /** The parent map */
        private final AbstractHashedMap<K, ?> parent;

        /**
         * Constructs a new instance.
         *
         * @param parent The parent AbstractHashedMap.
         */
        protected KeySet(final AbstractHashedMap<K, ?> parent) {
            this.parent = parent;
        }

        @Override
        public void clear() {
            parent.clear();
        }

        @Override
        public boolean contains(final Object key) {
            return parent.containsKey(key);
        }

        @Override
        public Iterator<K> iterator() {
            return parent.createKeySetIterator();
        }

        @Override
        public boolean remove(final Object key) {
            final boolean result = parent.containsKey(key);
            parent.remove(key);
            return result;
        }

        @Override
        public int size() {
            return parent.size();
        }
    }

    /**
     * KeySet iterator.
     *
     * @param <K> the type of elements maintained by this set
     */
    protected static class KeySetIterator<K> extends HashIterator<K, Object> implements Iterator<K> {

        /**
         * Constructs a new instance.
         *
         * @param parent The parent AbstractHashedMap.
         */
        @SuppressWarnings("unchecked")
        protected KeySetIterator(final AbstractHashedMap<K, ?> parent) {
            super((AbstractHashedMap<K, Object>) parent);
        }

        @Override
        public K next() {
            return super.nextEntry().getKey();
        }
    }

    /**
     * Values implementation.
     *
     * @param <V> the type of elements maintained by this collection
     */
    protected static class Values<V> extends AbstractCollection<V> {

        /** The parent map */
        private final AbstractHashedMap<?, V> parent;

        /**
         * Constructs a new instance.
         *
         * @param parent The parent AbstractHashedMap.
         */
        protected Values(final AbstractHashedMap<?, V> parent) {
            this.parent = parent;
        }

        @Override
        public void clear() {
            parent.clear();
        }

        @Override
        public boolean contains(final Object value) {
            return parent.containsValue(value);
        }

        @Override
        public Iterator<V> iterator() {
            return parent.createValuesIterator();
        }

        @Override
        public int size() {
            return parent.size();
        }
    }

    /**
     * Values iterator.
     *
     * @param <V> the type of elements maintained by this collection
     */
    protected static class ValuesIterator<V> extends HashIterator<Object, V> implements Iterator<V> {

        /**
         * Constructs a new instance.
         *
         * @param parent The parent AbstractHashedMap.
         */
        @SuppressWarnings("unchecked")
        protected ValuesIterator(final AbstractHashedMap<?, V> parent) {
            super((AbstractHashedMap<Object, V>) parent);
        }

        @Override
        public V next() {
            return super.nextEntry().getValue();
        }
    }

    /** Exception message. */
    protected static final String NO_NEXT_ENTRY = "No next() entry in the iteration";

    /** Exception message. */
    protected static final String NO_PREVIOUS_ENTRY = "No previous() entry in the iteration";

    /** Exception message. */
    protected static final String REMOVE_INVALID = "remove() can only be called once after next()";

    /** Exception message. */
    protected static final String GETKEY_INVALID = "getKey() can only be called after next() and before remove()";

    /** Exception message. */
    protected static final String GETVALUE_INVALID = "getValue() can only be called after next() and before remove()";

    /** Exception message. */
    protected static final String SETVALUE_INVALID = "setValue() can only be called after next() and before remove()";

    /** The default capacity to use */
    protected static final int DEFAULT_CAPACITY = 16;

    /** The default threshold to use */
    protected static final int DEFAULT_THRESHOLD = 12;

    /** The default load factor to use */
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /** The maximum capacity allowed */
    protected static final int MAXIMUM_CAPACITY = 1 << 30;

    /** An object for masking null */
    protected static final Object NULL = new Object();

    /** Load factor, normally 0.75 */
    transient float loadFactor;

    /** The size of the map */
    transient int size;

    /** Map entries */
    transient HashEntry<K, V>[] data;

    /** Size at which to rehash */
    transient int threshold;

    /** Modification count for iterators */
    transient int modCount;

    /** Entry set */
    transient EntrySet<K, V> entrySet;

    /** Key set */
    transient KeySet<K> keySet;

    /** Values */
    transient Values<V> values;

    /**
     * Constructor only used in deserialization, do not use otherwise.
     */
    protected AbstractHashedMap() {
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * default load factor.
     *
     * @param initialCapacity  the initial capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    protected AbstractHashedMap(final int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor.
     *
     * @param initialCapacity  the initial capacity
     * @param loadFactor  the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     * @throws IllegalArgumentException if the load factor is less than or equal to zero
     */
    @SuppressWarnings("unchecked")
    protected AbstractHashedMap(int initialCapacity, final float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity must be a non negative number");
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor must be greater than 0");
        }
        this.loadFactor = loadFactor;
        initialCapacity = calculateNewCapacity(initialCapacity);
        this.threshold = calculateThreshold(initialCapacity, loadFactor);
        this.data = new HashEntry[initialCapacity];
        init();
    }

    /**
     * Constructor which performs no validation on the passed in parameters.
     *
     * @param initialCapacity  the initial capacity, must be a power of two
     * @param loadFactor  the load factor, must be &gt; 0.0f and generally &lt; 1.0f
     * @param threshold  the threshold, must be sensible
     */
    @SuppressWarnings("unchecked")
    protected AbstractHashedMap(final int initialCapacity, final float loadFactor, final int threshold) {
        this.loadFactor = loadFactor;
        this.data = new HashEntry[initialCapacity];
        this.threshold = threshold;
        init();
    }

    /**
     * Constructor copying elements from another map.
     *
     * @param map  the map to copy
     * @throws NullPointerException if the map is null
     */
    protected AbstractHashedMap(final Map<? extends K, ? extends V> map) {
        this(Math.max(2 * map.size(), DEFAULT_CAPACITY), DEFAULT_LOAD_FACTOR);
        putAll(map);
    }

    /**
     * Adds an entry into this map.
     * <p>
     * This implementation adds the entry to the data storage table.
     * Subclasses could override to handle changes to the map.
     * </p>
     *
     * @param entry  the entry to add
     * @param hashIndex  the index into the data array to store at
     */
    protected void addEntry(final HashEntry<K, V> entry, final int hashIndex) {
        data[hashIndex] = entry;
    }

    /**
     * Adds a new key-value mapping into this map.
     * <p>
     * This implementation calls {@code createEntry()}, {@code addEntry()}
     * and {@code checkCapacity()}.
     * It also handles changes to {@code modCount} and {@code size}.
     * Subclasses could override to fully control adds to the map.
     * </p>
     *
     * @param hashIndex  the index into the data array to store at
     * @param hashCode  the hash code of the key to add
     * @param key  the key to add
     * @param value  the value to add
     */
    protected void addMapping(final int hashIndex, final int hashCode, final K key, final V value) {
        modCount++;
        final HashEntry<K, V> entry = createEntry(data[hashIndex], hashCode, key, value);
        addEntry(entry, hashIndex);
        size++;
        checkCapacity();
    }

    /**
     * Calculates the new capacity of the map.
     * This implementation normalizes the capacity to a power of two.
     *
     * @param proposedCapacity  the proposed capacity
     * @return the normalized new capacity
     */
    protected int calculateNewCapacity(final int proposedCapacity) {
        int newCapacity = 1;
        if (proposedCapacity > MAXIMUM_CAPACITY) {
            newCapacity = MAXIMUM_CAPACITY;
        } else {
            while (newCapacity < proposedCapacity) {
                newCapacity <<= 1;  // multiply by two
            }
            if (newCapacity > MAXIMUM_CAPACITY) {
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
    protected int calculateThreshold(final int newCapacity, final float factor) {
        return (int) (newCapacity * factor);
    }

    /**
     * Checks the capacity of the map and enlarges it if necessary.
     * <p>
     * This implementation uses the threshold to check if the map needs enlarging
     * </p>
     */
    protected void checkCapacity() {
        if (size >= threshold) {
            final int newCapacity = data.length * 2;
            if (newCapacity <= MAXIMUM_CAPACITY) {
                ensureCapacity(newCapacity);
            }
        }
    }

    /**
     * Clears the map, resetting the size to zero and nullifying references
     * to avoid garbage collection issues.
     */
    @Override
    public void clear() {
        modCount++;
        final HashEntry<K, V>[] data = this.data;
        Arrays.fill(data, null);
        size = 0;
    }

    /**
     * Clones the map without cloning the keys or values.
     * <p>
     * To implement {@code clone()}, a subclass must implement the
     * {@code Cloneable} interface and make this method public.
     * </p>
     *
     * @return a shallow clone
     * @throws InternalError if {@link AbstractMap#clone()} failed
     */
    @Override
    @SuppressWarnings("unchecked")
    protected AbstractHashedMap<K, V> clone() {
        try {
            final AbstractHashedMap<K, V> cloned = (AbstractHashedMap<K, V>) super.clone();
            cloned.data = new HashEntry[data.length];
            cloned.entrySet = null;
            cloned.keySet = null;
            cloned.values = null;
            cloned.modCount = 0;
            cloned.size = 0;
            cloned.init();
            cloned.putAll(this);
            return cloned;
        } catch (final CloneNotSupportedException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * Checks whether the map contains the specified key.
     *
     * @param key  the key to search for
     * @return true if the map contains the key
     */
    @Override
    public boolean containsKey(Object key) {
        key = convertKey(key);
        final int hashCode = hash(key);
        HashEntry<K, V> entry = data[hashIndex(hashCode, data.length)]; // no local for hash index
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
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            for (final HashEntry<K, V> element : data) {
                HashEntry<K, V> entry = element;
                while (entry != null) {
                    if (entry.getValue() == null) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        } else {
            for (final HashEntry<K, V> element : data) {
                HashEntry<K, V> entry = element;
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

    /**
     * Converts input keys to another object for storage in the map.
     * This implementation masks nulls.
     * Subclasses can override this to perform alternate key conversions.
     * <p>
     * The reverse conversion can be changed, if required, by overriding the
     * getKey() method in the hash entry.
     * </p>
     *
     * @param key  the key convert
     * @return the converted key
     */
    protected Object convertKey(final Object key) {
        return key == null ? NULL : key;
    }

    /**
     * Creates an entry to store the key-value data.
     * <p>
     * This implementation creates a new HashEntry instance.
     * Subclasses can override this to return a different storage class,
     * or implement caching.
     * </p>
     *
     * @param next  the next entry in sequence
     * @param hashCode  the hash code to use
     * @param key  the key to store
     * @param value  the value to store
     * @return the newly created entry
     */
    protected HashEntry<K, V> createEntry(final HashEntry<K, V> next, final int hashCode, final K key, final V value) {
        return new HashEntry<>(next, hashCode, convertKey(key), value);
    }

    /**
     * Creates an entry set iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @return the entrySet iterator
     */
    protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
        if (isEmpty()) {
            return EmptyIterator.<Map.Entry<K, V>>emptyIterator();
        }
        return new EntrySetIterator<>(this);
    }

    /**
     * Creates a key set iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @return the keySet iterator
     */
    protected Iterator<K> createKeySetIterator() {
        if (isEmpty()) {
            return EmptyIterator.<K>emptyIterator();
        }
        return new KeySetIterator<>(this);
    }

    /**
     * Creates a values iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @return the values iterator
     */
    protected Iterator<V> createValuesIterator() {
        if (isEmpty()) {
            return EmptyIterator.<V>emptyIterator();
        }
        return new ValuesIterator<>(this);
    }

    /**
     * Kills an entry ready for the garbage collector.
     * <p>
     * This implementation prepares the HashEntry for garbage collection.
     * Subclasses can override this to implement caching (override clear as well).
     * </p>
     *
     * @param entry  the entry to destroy
     */
    protected void destroyEntry(final HashEntry<K, V> entry) {
        entry.next = null;
        entry.key = null;
        entry.value = null;
    }

    /**
     * Reads the map data from the stream. This method must be overridden if a
     * subclass must be setup before {@code put()} is used.
     * <p>
     * Serialization is not one of the JDK's nicest topics. Normal serialization will
     * initialize the superclass before the subclass. Sometimes however, this isn't
     * what you want, as in this case the {@code put()} method on read can be
     * affected by subclass state.
     * </p>
     * <p>
     * The solution adopted here is to deserialize the state data of this class in
     * this protected method. This method must be called by the
     * {@code readObject()} of the first serializable subclass.
     * </p>
     * <p>
     * Subclasses may override if the subclass has a specific field that must be present
     * before {@code put()} or {@code calculateThreshold()} will work correctly.
     * </p>
     *
     * @param in  the input stream
     * @throws IOException if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     */
    @SuppressWarnings("unchecked")
    protected void doReadObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        loadFactor = in.readFloat();
        final int capacity = in.readInt();
        final int size = in.readInt();
        init();
        threshold = calculateThreshold(capacity, loadFactor);
        data = new HashEntry[capacity];
        for (int i = 0; i < size; i++) {
            final K key = (K) in.readObject();
            final V value = (V) in.readObject();
            put(key, value);
        }
    }

    /**
     * Writes the map data to the stream. This method must be overridden if a
     * subclass must be setup before {@code put()} is used.
     * <p>
     * Serialization is not one of the JDK's nicest topics. Normal serialization will
     * initialize the superclass before the subclass. Sometimes however, this isn't
     * what you want, as in this case the {@code put()} method on read can be
     * affected by subclass state.
     * </p>
     * <p>
     * The solution adopted here is to serialize the state data of this class in
     * this protected method. This method must be called by the
     * {@code writeObject()} of the first serializable subclass.
     * </p>
     * <p>
     * Subclasses may override if they have a specific field that must be present
     * on read before this implementation will work. Generally, the read determines
     * what must be serialized here, if anything.
     * </p>
     *
     * @param out  the output stream
     * @throws IOException if an error occurs while writing to the stream
     */
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeFloat(loadFactor);
        out.writeInt(data.length);
        out.writeInt(size);
        for (final MapIterator<K, V> it = mapIterator(); it.hasNext();) {
            out.writeObject(it.next());
            out.writeObject(it.getValue());
        }
    }

    /**
     * Changes the size of the data structure to the capacity proposed.
     *
     * @param newCapacity  the new capacity of the array (a power of two, less or equal to max)
     */
    @SuppressWarnings("unchecked")
    protected void ensureCapacity(final int newCapacity) {
        final int oldCapacity = data.length;
        if (newCapacity <= oldCapacity) {
            return;
        }
        if (size == 0) {
            threshold = calculateThreshold(newCapacity, loadFactor);
            data = new HashEntry[newCapacity];
        } else {
            final HashEntry<K, V>[] oldEntries = data;
            final HashEntry<K, V>[] newEntries = new HashEntry[newCapacity];

            modCount++;
            for (int i = oldCapacity - 1; i >= 0; i--) {
                HashEntry<K, V> entry = oldEntries[i];
                if (entry != null) {
                    oldEntries[i] = null;  // gc
                    do {
                        final HashEntry<K, V> next = entry.next;
                        final int index = hashIndex(entry.hashCode, newCapacity);
                        entry.next = newEntries[index];
                        newEntries[index] = entry;
                        entry = next;
                    } while (entry != null);
                }
            }
            threshold = calculateThreshold(newCapacity, loadFactor);
            data = newEntries;
        }
    }

    /**
     * Gets the {@code hashCode} field from a {@code HashEntry}.
     * Used in subclasses that have no visibility of the field.
     *
     * @param entry  the entry to query, must not be null
     * @return the {@code hashCode} field of the entry
     * @throws NullPointerException if the entry is null
     * @since 3.1
     */
    protected int entryHashCode(final HashEntry<K, V> entry) {
        return entry.hashCode;
    }

    /**
     * Gets the {@code key} field from a {@code HashEntry}.
     * Used in subclasses that have no visibility of the field.
     *
     * @param entry  the entry to query, must not be null
     * @return the {@code key} field of the entry
     * @throws NullPointerException if the entry is null
     * @since 3.1
     */
    protected K entryKey(final HashEntry<K, V> entry) {
        return entry.getKey();
    }

    /**
     * Gets the {@code next} field from a {@code HashEntry}.
     * Used in subclasses that have no visibility of the field.
     *
     * @param entry  the entry to query, must not be null
     * @return the {@code next} field of the entry
     * @throws NullPointerException if the entry is null
     * @since 3.1
     */
    protected HashEntry<K, V> entryNext(final HashEntry<K, V> entry) {
        return entry.next;
    }

    /**
     * Gets the entrySet view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the entries, use {@link #mapIterator()}.
     *
     * @return the entrySet view
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet<>(this);
        }
        return entrySet;
    }

    /**
     * Gets the {@code value} field from a {@code HashEntry}.
     * Used in subclasses that have no visibility of the field.
     *
     * @param entry  the entry to query, must not be null
     * @return the {@code value} field of the entry
     * @throws NullPointerException if the entry is null
     * @since 3.1
     */
    protected V entryValue(final HashEntry<K, V> entry) {
        return entry.getValue();
    }

    /**
     * Compares this map with another.
     *
     * @param obj  the object to compare to
     * @return true if equal
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        final Map<?, ?> map = (Map<?, ?>) obj;
        if (map.size() != size()) {
            return false;
        }
        final MapIterator<?, ?> it = mapIterator();
        try {
            while (it.hasNext()) {
                final Object key = it.next();
                final Object value = it.getValue();
                if (value == null) {
                    if (map.get(key) != null || !map.containsKey(key)) {
                        return false;
                    }
                } else if (!value.equals(map.get(key))) {
                    return false;
                }
            }
        } catch (final ClassCastException | NullPointerException ignored) {
            return false;
        }
        return true;
    }

    /**
     * Gets the value mapped to the key specified.
     *
     * @param key  the key
     * @return the mapped value, null if no match
     */
    @Override
    public V get(Object key) {
        key = convertKey(key);
        final int hashCode = hash(key);
        HashEntry<K, V> entry = data[hashIndex(hashCode, data.length)]; // no local for hash index
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(key, entry.key)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Gets the entry mapped to the key specified.
     * <p>
     * This method exists for subclasses that may need to perform a multi-step
     * process accessing the entry. The public methods in this class don't use this
     * method to gain a small performance boost.
     * </p>
     *
     * @param key  the key
     * @return the entry, null if no match
     */
    protected HashEntry<K, V> getEntry(Object key) {
        key = convertKey(key);
        final int hashCode = hash(key);
        HashEntry<K, V> entry = data[hashIndex(hashCode, data.length)]; // no local for hash index
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(key, entry.key)) {
                return entry;
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Gets the hash code for the key specified.
     * This implementation uses the additional hashing routine from JDK1.4.
     * Subclasses can override this to return alternate hash codes.
     *
     * @param key  the key to get a hash code for
     * @return the hash code
     */
    protected int hash(final Object key) {
        // same as JDK 1.4
        int h = key.hashCode();
        h += ~(h << 9);
        h ^=  h >>> 14;
        h +=  h << 4;
        h ^=  h >>> 10;
        return h;
    }

    /**
     * Gets the standard Map hashCode.
     *
     * @return the hash code defined in the Map interface
     */
    @Override
    public int hashCode() {
        int total = 0;
        final Iterator<Map.Entry<K, V>> it = createEntrySetIterator();
        while (it.hasNext()) {
            total += it.next().hashCode();
        }
        return total;
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
    protected int hashIndex(final int hashCode, final int dataSize) {
        return hashCode & dataSize - 1;
    }

    /**
     * Initialize subclasses during construction, cloning or deserialization.
     */
    protected void init() {
        // noop
    }

    /**
     * Checks whether the map is currently empty.
     *
     * @return true if the map is currently size zero
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Compares two keys, in internal converted form, to see if they are equal.
     * This implementation uses the equals method and assumes neither key is null.
     * Subclasses can override this to match differently.
     *
     * @param key1  the first key to compare passed in from outside
     * @param key2  the second key extracted from the entry via {@code entry.key}
     * @return true if equal
     */
    protected boolean isEqualKey(final Object key1, final Object key2) {
        return Objects.equals(key1, key2);
    }

    /**
     * Compares two values, in external form, to see if they are equal.
     * This implementation uses the equals method and assumes neither value is null.
     * Subclasses can override this to match differently.
     *
     * @param value1  the first value to compare passed in from outside
     * @param value2  the second value extracted from the entry via {@code getValue()}
     * @return true if equal
     */
    protected boolean isEqualValue(final Object value1, final Object value2) {
        return Objects.equals(value1, value2);
    }

    /**
     * Gets the keySet view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the keys, use {@link #mapIterator()}.
     *
     * @return the keySet view
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet<>(this);
        }
        return keySet;
    }

    /**
     * Gets an iterator over the map.
     * Changes made to the iterator affect this map.
     * <p>
     * A MapIterator returns the keys in the map. It also provides convenient
     * methods to get the key and value, and set the value.
     * It avoids the need to create an entrySet/keySet/values object.
     * It also avoids creating the Map.Entry object.
     * </p>
     *
     * @return the map iterator
     */
    @Override
    public MapIterator<K, V> mapIterator() {
        if (size == 0) {
            return EmptyMapIterator.<K, V>emptyMapIterator();
        }
        return new HashMapIterator<>(this);
    }

    /**
     * Puts a key-value mapping into this map.
     *
     * @param key  the key to add
     * @param value  the value to add
     * @return the value previously mapped to this key, null if none
     */
    @Override
    public V put(final K key, final V value) {
        final Object convertedKey = convertKey(key);
        final int hashCode = hash(convertedKey);
        final int index = hashIndex(hashCode, data.length);
        HashEntry<K, V> entry = data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(convertedKey, entry.key)) {
                final V oldValue = entry.getValue();
                updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }

        addMapping(index, hashCode, key, value);
        return null;
    }

    /**
     * Puts all the values from the specified map into this map.
     * <p>
     * This implementation iterates around the specified map and
     * uses {@link #put(Object, Object)}.
     * </p>
     *
     * @param map  the map to add
     * @throws NullPointerException if the map is null
     */
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        final int mapSize = map.size();
        if (mapSize == 0) {
            return;
        }
        final int newSize = (int) ((size + mapSize) / loadFactor + 1);
        ensureCapacity(calculateNewCapacity(newSize));
        for (final Map.Entry<? extends K, ? extends V> entry: map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Removes the specified mapping from this map.
     *
     * @param key  the mapping to remove
     * @return the value mapped to the removed key, null if key not in map
     */
    @Override
    public V remove(Object key) {
        key = convertKey(key);
        final int hashCode = hash(key);
        final int index = hashIndex(hashCode, data.length);
        HashEntry<K, V> entry = data[index];
        HashEntry<K, V> previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(key, entry.key)) {
                final V oldValue = entry.getValue();
                removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Removes an entry from the chain stored in a particular index.
     * <p>
     * This implementation removes the entry from the data storage table.
     * The size is not updated.
     * Subclasses could override to handle changes to the map.
     * </p>
     *
     * @param entry  the entry to remove
     * @param hashIndex  the index into the data structure
     * @param previous  the previous entry in the chain
     */
    protected void removeEntry(final HashEntry<K, V> entry, final int hashIndex, final HashEntry<K, V> previous) {
        if (previous == null) {
            data[hashIndex] = entry.next;
        } else {
            previous.next = entry.next;
        }
    }

    /**
     * Removes a mapping from the map.
     * <p>
     * This implementation calls {@code removeEntry()} and {@code destroyEntry()}.
     * It also handles changes to {@code modCount} and {@code size}.
     * Subclasses could override to fully control removals from the map.
     * </p>
     *
     * @param entry  the entry to remove
     * @param hashIndex  the index into the data structure
     * @param previous  the previous entry in the chain
     */
    protected void removeMapping(final HashEntry<K, V> entry, final int hashIndex, final HashEntry<K, V> previous) {
        modCount++;
        removeEntry(entry, hashIndex, previous);
        size--;
        destroyEntry(entry);
    }

    /**
     * Reuses an existing key-value mapping, storing completely new data.
     * <p>
     * This implementation sets all the data fields on the entry.
     * Subclasses could populate additional entry fields.
     * </p>
     *
     * @param entry  the entry to update, not null
     * @param hashIndex  the index in the data array
     * @param hashCode  the hash code of the key to add
     * @param key  the key to add
     * @param value  the value to add
     */
    protected void reuseEntry(final HashEntry<K, V> entry, final int hashIndex, final int hashCode,
                              final K key, final V value) {
        entry.next = data[hashIndex];
        entry.hashCode = hashCode;
        entry.key = key;
        entry.value = value;
    }

    /**
     * Gets the size of the map.
     *
     * @return the size
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Gets the map as a String.
     *
     * @return a string version of the map
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        final StringBuilder buf = new StringBuilder(32 * size());
        buf.append('{');

        final MapIterator<K, V> it = mapIterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            final K key = it.next();
            final V value = it.getValue();
            buf.append(key == this ? "(this Map)" : key)
                .append('=')
                .append(value == this ? "(this Map)" : value);

            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(CollectionUtils.COMMA).append(' ');
            }
        }

        buf.append('}');
        return buf.toString();
    }

    /**
     * Updates an existing key-value mapping to change the value.
     * <p>
     * This implementation calls {@code setValue()} on the entry.
     * Subclasses could override to handle changes to the map.
     * </p>
     *
     * @param entry  the entry to update
     * @param newValue  the new value to store
     */
    protected void updateEntry(final HashEntry<K, V> entry, final V newValue) {
        entry.setValue(newValue);
    }

    /**
     * Gets the values view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the values, use {@link #mapIterator()}.
     *
     * @return the values view
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new Values<>(this);
        }
        return values;
    }
}
