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
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;

/**
 * An abstract implementation of a hash-based map that allows the entries to
 * be removed by the garbage collector.
 * <p>
 * This class implements all the features necessary for a subclass reference
 * hash-based map. Key-value entries are stored in instances of the
 * {@code ReferenceEntry} class which can be overridden and replaced.
 * The iterators can similarly be replaced, without the need to replace the KeySet,
 * EntrySet and Values view classes.
 * </p>
 * <p>
 * Overridable methods are provided to change the default hashing behavior, and
 * to change how entries are added to and removed from the map. Hopefully, all you
 * need for unusual subclasses is here.
 * </p>
 * <p>
 * When you construct an {@code AbstractReferenceMap}, you can specify what
 * kind of references are used to store the map's keys and values.
 * If non-hard references are used, then the garbage collector can remove
 * mappings if a key or value becomes unreachable, or if the JVM's memory is
 * running low. For information on how the different reference types behave,
 * see {@link Reference}.
 * </p>
 * <p>
 * Different types of references can be specified for keys and values.
 * The keys can be configured to be weak but the values hard,
 * in which case this class will behave like a
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/WeakHashMap.html">
 * {@code WeakHashMap}</a>. However, you can also specify hard keys and
 * weak values, or any other combination. The default constructor uses
 * hard keys and soft values, providing a memory-sensitive cache.
 * </p>
 * <p>
 * This {@link Map} implementation does <em>not</em> allow null elements.
 * Attempting to add a null key or value to the map will raise a
 * {@code NullPointerException}.
 * </p>
 * <p>
 * All the available iterators can be reset back to the start by casting to
 * {@code ResettableIterator} and calling {@code reset()}.
 * </p>
 * <p>
 * This implementation is not synchronized.
 * You can use {@link java.util.Collections#synchronizedMap} to
 * provide synchronized access to a {@code ReferenceMap}.
 * </p>
 *
 * @param <K> the type of the keys in this map
 * @param <V> the type of the values in this map
 * @see java.lang.ref.Reference
 * @since 3.1 (extracted from ReferenceMap in 3.0)
 */
public abstract class AbstractReferenceMap<K, V> extends AbstractHashedMap<K, V> {

    /**
     * Base iterator class.
     */
    static class ReferenceBaseIterator<K, V> {
        /** The parent map */
        final AbstractReferenceMap<K, V> parent;

        // These fields keep track of where we are in the table.
        int index;
        ReferenceEntry<K, V> next;
        ReferenceEntry<K, V> current;

        // These Object fields provide hard references to the
        // current and next entry; this assures that if hasNext()
        // returns true, next() will actually return a valid element.
        K currentKey;
        K nextKey;
        V currentValue;
        V nextValue;

        int expectedModCount;

        ReferenceBaseIterator(final AbstractReferenceMap<K, V> parent) {
            this.parent = parent;
            index = !parent.isEmpty() ? parent.data.length : 0;
            // have to do this here!  size() invocation above
            // may have altered the modCount.
            expectedModCount = parent.modCount;
        }

        private void checkMod() {
            if (parent.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        protected ReferenceEntry<K, V> currentEntry() {
            checkMod();
            return current;
        }

        public boolean hasNext() {
            checkMod();
            while (nextNull()) {
                ReferenceEntry<K, V> e = next;
                int i = index;
                while (e == null && i > 0) {
                    i--;
                    e = (ReferenceEntry<K, V>) parent.data[i];
                }
                next = e;
                index = i;
                if (e == null) {
                    return false;
                }
                nextKey = e.getKey();
                nextValue = e.getValue();
                if (nextNull()) {
                    next = next.next();
                }
            }
            return true;
        }

        protected ReferenceEntry<K, V> nextEntry() {
            checkMod();
            if (nextNull() && !hasNext()) {
                throw new NoSuchElementException();
            }
            current = next;
            next = next.next();
            currentKey = nextKey;
            currentValue = nextValue;
            nextKey = null;
            nextValue = null;
            return current;
        }

        private boolean nextNull() {
            return nextKey == null || nextValue == null;
        }

        public void remove() {
            checkMod();
            if (current == null) {
                throw new IllegalStateException();
            }
            parent.remove(currentKey);
            current = null;
            currentKey = null;
            currentValue = null;
            expectedModCount = parent.modCount;
        }
    }

    /**
     * A MapEntry implementation for the map.
     * <p>
     * If getKey() or getValue() returns null, it means
     * the mapping is stale and should be removed.
     * </p>
     *
     * @param <K> the type of the keys
     * @param <V> the type of the values
     * @since 3.1
     */
    protected static class ReferenceEntry<K, V> extends HashEntry<K, V> {
        /** The parent map */
        private final AbstractReferenceMap<K, V> parent;

        /**
         * Creates a new entry object for the ReferenceMap.
         *
         * @param parent  the parent map
         * @param next  the next entry in the hash bucket
         * @param hashCode  the hash code of the key
         * @param key  the key
         * @param value  the value
         */
        public ReferenceEntry(final AbstractReferenceMap<K, V> parent, final HashEntry<K, V> next,
                              final int hashCode, final K key, final V value) {
            super(next, hashCode, null, null);
            this.parent = parent;
            this.key = toReference(parent.keyType, key, hashCode);
            this.value = toReference(parent.valueType, value, hashCode); // the key hashCode is passed in deliberately
        }

        /**
         * Compares this map entry to another.
         * <p>
         * This implementation uses {@code isEqualKey} and
         * {@code isEqualValue} on the main map for comparison.
         * </p>
         *
         * @param obj  the other map entry to compare to
         * @return true if equal, false if not
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }

            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) obj;
            final Object entryKey = entry.getKey();  // convert to hard reference
            final Object entryValue = entry.getValue();  // convert to hard reference
            if (entryKey == null || entryValue == null) {
                return false;
            }
            // compare using map methods, aiding identity subclass
            // note that key is direct access and value is via method
            return parent.isEqualKey(entryKey, key) &&
                   parent.isEqualValue(entryValue, getValue());
        }

        /**
         * Gets the key from the entry.
         * This method dereferences weak and soft keys and thus may return null.
         *
         * @return the key, which may be null if it was garbage collected
         */
        @Override
        @SuppressWarnings("unchecked")
        public K getKey() {
            return (K) (parent.keyType == ReferenceStrength.HARD ? key : ((Reference<K>) key).get());
        }

        /**
         * Gets the value from the entry.
         * This method dereferences weak and soft value and thus may return null.
         *
         * @return the value, which may be null if it was garbage collected
         */
        @Override
        @SuppressWarnings("unchecked")
        public V getValue() {
            return (V) (parent.valueType == ReferenceStrength.HARD ? value : ((Reference<V>) value).get());
        }

        /**
         * Gets the hash code of the entry using temporary hard references.
         * <p>
         * This implementation uses {@code hashEntry} on the main map.
         *
         * @return the hash code of the entry
         */
        @Override
        public int hashCode() {
            return parent.hashEntry(getKey(), getValue());
        }

        /**
         * Gets the next entry in the bucket.
         *
         * @return the next entry in the bucket
         */
        protected ReferenceEntry<K, V> next() {
            return (ReferenceEntry<K, V>) next;
        }

        /**
         * This method can be overridden to provide custom logic to purge value
         */
        protected void nullValue() {
            value = null;
        }

        /**
         * This is the callback for custom "after purge" logic
         */
        protected void onPurge() {
            // empty
        }

        /**
         * Purges the specified reference
         * @param ref  the reference to purge
         * @return true or false
         */
        protected boolean purge(final Reference<?> ref) {
            boolean r = parent.keyType != ReferenceStrength.HARD && key == ref;
            r = r || parent.valueType != ReferenceStrength.HARD && value == ref;
            if (r) {
                if (parent.keyType != ReferenceStrength.HARD) {
                    ((Reference<?>) key).clear();
                }
                if (parent.valueType != ReferenceStrength.HARD) {
                    ((Reference<?>) value).clear();
                } else if (parent.purgeValues) {
                    nullValue();
                }
            }
            return r;
        }

        /**
         * Sets the value of the entry.
         *
         * @param value  the object to store
         * @return the previous value
         */
        @Override
        @SuppressWarnings("unchecked")
        public V setValue(final V value) {
            final V old = getValue();
            if (parent.valueType != ReferenceStrength.HARD) {
                ((Reference<V>) this.value).clear();
            }
            this.value = toReference(parent.valueType, value, hashCode);
            return old;
        }

        /**
         * Constructs a reference of the given type to the given referent.
         * The reference is registered with the queue for later purging.
         *
         * @param <T> the type of the referenced object
         * @param type  HARD, SOFT or WEAK
         * @param referent  the object to refer to
         * @param hash  the hash code of the <em>key</em> of the mapping;
         *    this number might be different from referent.hashCode() if
         *    the referent represents a value and not a key
         * @return the reference to the object
         */
        protected <T> Object toReference(final ReferenceStrength type, final T referent, final int hash) {
            switch (type) {
            case HARD:
                return referent;
            case SOFT:
                return new SoftRef<>(hash, referent, parent.queue);
            case WEAK:
                return new WeakRef<>(hash, referent, parent.queue);
            default:
                break;
            }
            throw new IllegalArgumentException(type.toString());
        }
    }

    /**
     * EntrySet implementation.
     */
    static class ReferenceEntrySet<K, V> extends EntrySet<K, V> {

        protected ReferenceEntrySet(final AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
        public Object[] toArray() {
            return toArray(new Object[size()]);
        }

        @Override
        public <T> T[] toArray(final T[] arr) {
            // special implementation to handle disappearing entries
            final ArrayList<Map.Entry<K, V>> list = new ArrayList<>(size());
            for (final Map.Entry<K, V> entry : this) {
                list.add(new DefaultMapEntry<>(entry));
            }
            return list.toArray(arr);
        }
    }

    /**
     * The EntrySet iterator.
     */
    static class ReferenceEntrySetIterator<K, V>
            extends ReferenceBaseIterator<K, V> implements Iterator<Map.Entry<K, V>> {

        ReferenceEntrySetIterator(final AbstractReferenceMap<K, V> parent) {
            super(parent);
        }

        @Override
        public Map.Entry<K, V> next() {
            return nextEntry();
        }

    }

    /**
     * KeySet implementation.
     */
    static class ReferenceKeySet<K> extends KeySet<K> {

        protected ReferenceKeySet(final AbstractHashedMap<K, ?> parent) {
            super(parent);
        }

        @Override
        public Object[] toArray() {
            return toArray(new Object[size()]);
        }

        @Override
        public <T> T[] toArray(final T[] arr) {
            // special implementation to handle disappearing keys
            final List<K> list = new ArrayList<>(size());
            forEach(list::add);
            return list.toArray(arr);
        }
    }

    /**
     * The keySet iterator.
     */
    static class ReferenceKeySetIterator<K> extends ReferenceBaseIterator<K, Object> implements Iterator<K> {

        @SuppressWarnings("unchecked")
        ReferenceKeySetIterator(final AbstractReferenceMap<K, ?> parent) {
            super((AbstractReferenceMap<K, Object>) parent);
        }

        @Override
        public K next() {
            return nextEntry().getKey();
        }
    }

    /**
     * The MapIterator implementation.
     */
    static class ReferenceMapIterator<K, V> extends ReferenceBaseIterator<K, V> implements MapIterator<K, V> {

        protected ReferenceMapIterator(final AbstractReferenceMap<K, V> parent) {
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
            return nextEntry().getKey();
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
     * Enumerates reference types.
     */
    public enum ReferenceStrength {

        /**
         * Hard reference type.
         */
        HARD(0),

        /**
         * Soft reference type.
         */
        SOFT(1),

        /**
         * Weak reference type.
         */
        WEAK(2);

        /**
         * Resolve enum from int.
         * @param value  the int value
         * @return ReferenceType
         * @throws IllegalArgumentException if the specified value is invalid.
         */
        public static ReferenceStrength resolve(final int value) {
            switch (value) {
            case 0:
                return HARD;
            case 1:
                return SOFT;
            case 2:
                return WEAK;
            default:
                throw new IllegalArgumentException();
            }
        }

        /** Value */
        public final int value;

        ReferenceStrength(final int value) {
            this.value = value;
        }

    }

    /**
     * Values implementation.
     */
    static class ReferenceValues<V> extends Values<V> {

        protected ReferenceValues(final AbstractHashedMap<?, V> parent) {
            super(parent);
        }

        @Override
        public Object[] toArray() {
            return toArray(new Object[size()]);
        }

        @Override
        public <T> T[] toArray(final T[] arr) {
            // special implementation to handle disappearing values
            final List<V> list = new ArrayList<>(size());
            forEach(list::add);
            return list.toArray(arr);
        }
    }

    /**
     * The values iterator.
     */
    static class ReferenceValuesIterator<V> extends ReferenceBaseIterator<Object, V> implements Iterator<V> {

        @SuppressWarnings("unchecked")
        ReferenceValuesIterator(final AbstractReferenceMap<?, V> parent) {
            super((AbstractReferenceMap<Object, V>) parent);
        }

        @Override
        public V next() {
            return nextEntry().getValue();
        }
    }

    /**
     * A soft reference holder.
     */
    static class SoftRef<T> extends SoftReference<T> {
        /** The hashCode of the key (even if the reference points to a value) */
        private final int hash;

        SoftRef(final int hash, final T r, final ReferenceQueue<? super T> q) {
            super(r, q);
            this.hash = hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SoftRef<?> other = (SoftRef<?>) obj;
            return hash == other.hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    /**
     * A weak reference holder.
     */
    static class WeakRef<T> extends WeakReference<T> {
        /** The hashCode of the key (even if the reference points to a value) */
        private final int hash;

        WeakRef(final int hash, final T r, final ReferenceQueue<? super T> q) {
            super(r, q);
            this.hash = hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final WeakRef<?> other = (WeakRef<?>) obj;
            return hash == other.hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    /**
     * The reference type for keys.
     */
    private ReferenceStrength keyType;

    /**
     * The reference type for values.
     */
    private ReferenceStrength valueType;

    /**
     * Should the value be automatically purged when the associated key has been collected?
     */
    private boolean purgeValues;

    /**
     * ReferenceQueue used to eliminate stale mappings.
     * See purge.
     */
    private transient ReferenceQueue<Object> queue;

    /**
     * Constructor used during deserialization.
     */
    protected AbstractReferenceMap() {
    }

    /**
     * Constructs a new empty map with the specified reference types,
     * load factor and initial capacity.
     *
     * @param keyType  the type of reference to use for keys;
     *   must be {@link ReferenceStrength#HARD HARD},
     *   {@link ReferenceStrength#SOFT SOFT},
     *   {@link ReferenceStrength#WEAK WEAK}
     * @param valueType  the type of reference to use for values;
     *   must be {@link ReferenceStrength#HARD},
     *   {@link ReferenceStrength#SOFT SOFT},
     *   {@link ReferenceStrength#WEAK WEAK}
     * @param capacity  the initial capacity for the map
     * @param loadFactor  the load factor for the map
     * @param purgeValues  should the value be automatically purged when the
     *   key is garbage collected
     */
    protected AbstractReferenceMap(
            final ReferenceStrength keyType, final ReferenceStrength valueType, final int capacity,
            final float loadFactor, final boolean purgeValues) {
        super(capacity, loadFactor);
        this.keyType = keyType;
        this.valueType = valueType;
        this.purgeValues = purgeValues;
    }

    /**
     * Clears this map.
     */
    @Override
    public void clear() {
        super.clear();
        // Drain the queue
        while (queue.poll() != null) { // NOPMD
        }
    }

    /**
     * Checks whether the map contains the specified key.
     *
     * @param key  the key to search for
     * @return true if the map contains the key
     */
    @Override
    public boolean containsKey(final Object key) {
        purgeBeforeRead();
        final Entry<K, V> entry = getEntry(key);
        if (entry == null) {
            return false;
        }
        return entry.getValue() != null;
    }

    /**
     * Checks whether the map contains the specified value.
     *
     * @param value  the value to search for
     * @return true if the map contains the value
     */
    @Override
    public boolean containsValue(final Object value) {
        purgeBeforeRead();
        if (value == null) {
            return false;
        }
        return super.containsValue(value);
    }

    /**
     * Creates a ReferenceEntry instead of a HashEntry.
     *
     * @param next  the next entry in sequence
     * @param hashCode  the hash code to use
     * @param key  the key to store
     * @param value  the value to store
     * @return the newly created entry
     */
    @Override
    protected ReferenceEntry<K, V> createEntry(final HashEntry<K, V> next, final int hashCode,
                                               final K key, final V value) {
        return new ReferenceEntry<>(this, next, hashCode, key, value);
    }

    /**
     * Creates an entry set iterator.
     *
     * @return the entrySet iterator
     */
    @Override
    protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
        return new ReferenceEntrySetIterator<>(this);
    }

    /**
     * Creates a key set iterator.
     *
     * @return the keySet iterator
     */
    @Override
    protected Iterator<K> createKeySetIterator() {
        return new ReferenceKeySetIterator<>(this);
    }

    /**
     * Creates a values iterator.
     *
     * @return the values iterator
     */
    @Override
    protected Iterator<V> createValuesIterator() {
        return new ReferenceValuesIterator<>(this);
    }

    /**
     * Replaces the superclass method to read the state of this class.
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
    @Override
    @SuppressWarnings("unchecked")
    protected void doReadObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        keyType = ReferenceStrength.resolve(in.readInt());
        valueType = ReferenceStrength.resolve(in.readInt());
        purgeValues = in.readBoolean();
        loadFactor = in.readFloat();
        final int capacity = in.readInt();
        init();
        data = new HashEntry[capacity];

        // COLLECTIONS-599: Calculate threshold before populating, otherwise it will be 0
        // when it hits AbstractHashedMap.checkCapacity() and so will unnecessarily
        // double up the size of the "data" array during population.
        //
        // NB: AbstractHashedMap.doReadObject() DOES calculate the threshold before populating.
        //
        threshold = calculateThreshold(data.length, loadFactor);

        while (true) {
            final K key = (K) in.readObject();
            if (key == null) {
                break;
            }
            final V value = (V) in.readObject();
            put(key, value);
        }
        // do not call super.doReadObject() as code there doesn't work for reference map
    }

    /**
     * Replaces the superclass method to store the state of this class.
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
    @Override
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(keyType.value);
        out.writeInt(valueType.value);
        out.writeBoolean(purgeValues);
        out.writeFloat(loadFactor);
        out.writeInt(data.length);
        for (final MapIterator<K, V> it = mapIterator(); it.hasNext();) {
            out.writeObject(it.next());
            out.writeObject(it.getValue());
        }
        out.writeObject(null);  // null terminate map
        // do not call super.doWriteObject() as code there doesn't work for reference map
    }

    /**
     * Returns a set view of this map's entries.
     * An iterator returned entry is valid until {@code next()} is called again.
     * The {@code setValue()} method on the {@code toArray} entries has no effect.
     *
     * @return a set view of this map's entries
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new ReferenceEntrySet<>(this);
        }
        return entrySet;
    }

    /**
     * Gets the value mapped to the key specified.
     *
     * @param key  the key
     * @return the mapped value, null if no match
     */
    @Override
    public V get(final Object key) {
        purgeBeforeRead();
        final Entry<K, V> entry = getEntry(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    /**
     * Gets the entry mapped to the key specified.
     *
     * @param key  the key
     * @return the entry, null if no match
     */
    @Override
    protected HashEntry<K, V> getEntry(final Object key) {
        if (key == null) {
            return null;
        }
        return super.getEntry(key);
    }

    /**
     * Gets the hash code for a MapEntry.
     * Subclasses can override this, for example to use the identityHashCode.
     *
     * @param key  the key to get a hash code for, may be null
     * @param value  the value to get a hash code for, may be null
     * @return the hash code, as per the MapEntry specification
     */
    protected int hashEntry(final Object key, final Object value) {
        return (key == null ? 0 : key.hashCode()) ^
               (value == null ? 0 : value.hashCode());
    }

    /**
     * Initialize this subclass during construction, cloning or deserialization.
     */
    @Override
    protected void init() {
        queue = new ReferenceQueue<>();
    }

    /**
     * Checks whether the map is currently empty.
     *
     * @return true if the map is currently size zero
     */
    @Override
    public boolean isEmpty() {
        purgeBeforeRead();
        return super.isEmpty();
    }

    /**
     * Compares two keys, in internal converted form, to see if they are equal.
     * <p>
     * This implementation converts the key from the entry to a real reference
     * before comparison.
     * </p>
     *
     * @param key1  the first key to compare passed in from outside
     * @param key2  the second key extracted from the entry via {@code entry.key}
     * @return true if equal
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean isEqualKey(final Object key1, Object key2) {
        key2 = keyType == ReferenceStrength.HARD ? key2 : ((Reference<K>) key2).get();
        return Objects.equals(key1, key2);
    }

    /**
     * Provided protected read-only access to the key type.
     *
     * @param type the type to check against.
     * @return true if keyType has the specified type
     */
    protected boolean isKeyType(final ReferenceStrength type) {
        return keyType == type;
    }

    /**
     * Provided protected read-only access to the value type.
     *
     * @param type the type to check against.
     * @return true if valueType has the specified type
     */
    protected boolean isValueType(final ReferenceStrength type) {
        return valueType == type;
    }

    /**
     * Returns a set view of this map's keys.
     *
     * @return a set view of this map's keys
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new ReferenceKeySet<>(this);
        }
        return keySet;
    }

    /**
     * Gets a MapIterator over the reference map.
     * The iterator only returns valid key/value pairs.
     *
     * @return a map iterator
     */
    @Override
    public MapIterator<K, V> mapIterator() {
        return new ReferenceMapIterator<>(this);
    }

    /**
     * Purges stale mappings from this map.
     * <p>
     * Note that this method is not synchronized!  Special
     * care must be taken if, for instance, you want stale
     * mappings to be removed on a periodic basis by some
     * background thread.
     * </p>
     */
    protected void purge() {
        Reference<?> ref = queue.poll();
        while (ref != null) {
            purge(ref);
            ref = queue.poll();
        }
    }

    /**
     * Purges the specified reference.
     *
     * @param ref  the reference to purge
     */
    protected void purge(final Reference<?> ref) {
        // The hashCode of the reference is the hashCode of the
        // mapping key, even if the reference refers to the
        // mapping value...
        final int hash = ref.hashCode();
        final int index = hashIndex(hash, data.length);
        HashEntry<K, V> previous = null;
        HashEntry<K, V> entry = data[index];
        while (entry != null) {
            final ReferenceEntry<K, V> refEntry = (ReferenceEntry<K, V>) entry;
            if (refEntry.purge(ref)) {
                if (previous == null) {
                    data[index] = entry.next;
                } else {
                    previous.next = entry.next;
                }
                size--;
                refEntry.onPurge();
                return;
            }
            previous = entry;
            entry = entry.next;
        }

    }

    // These two classes store the hashCode of the key of
    // the mapping, so that after they're dequeued a quick
    // lookup of the bucket in the table can occur.

    /**
     * Purges stale mappings from this map before read operations.
     * <p>
     * This implementation calls {@link #purge()} to maintain a consistent state.
     */
    protected void purgeBeforeRead() {
        purge();
    }

    /**
     * Purges stale mappings from this map before write operations.
     * <p>
     * This implementation calls {@link #purge()} to maintain a consistent state.
     * </p>
     */
    protected void purgeBeforeWrite() {
        purge();
    }

    /**
     * Puts a key-value mapping into this map.
     * Neither the key nor the value may be null.
     *
     * @param key  the key to add, must not be null
     * @param value  the value to add, must not be null
     * @return the value previously mapped to this key, null if none
     * @throws NullPointerException if either the key or value is null
     */
    @Override
    public V put(final K key, final V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        purgeBeforeWrite();
        return super.put(key, value);
    }

    /**
     * Removes the specified mapping from this map.
     *
     * @param key  the mapping to remove
     * @return the value mapped to the removed key, null if key not in map
     */
    @Override
    public V remove(final Object key) {
        if (key == null) {
            return null;
        }
        purgeBeforeWrite();
        return super.remove(key);
    }

    /**
     * Gets the size of the map.
     *
     * @return the size
     */
    @Override
    public int size() {
        purgeBeforeRead();
        return super.size();
    }

    /**
     * Returns a collection view of this map's values.
     *
     * @return a set view of this map's values
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new ReferenceValues<>(this);
        }
        return values;
    }
}
