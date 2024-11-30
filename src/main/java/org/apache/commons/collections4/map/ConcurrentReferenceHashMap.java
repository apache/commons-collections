/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 */

package org.apache.commons.collections4.map;

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An advanced hash map supporting configurable garbage collection semantics of keys and values, optional referential-equality, full concurrency of retrievals,
 * and adjustable expected concurrency for updates.
 * <p>
 * This map is designed around specific advanced use-cases. If there is any doubt whether this map is for you, you most likely should be using
 * {@link java.util.concurrent.ConcurrentHashMap} instead.
 * </p>
 * <p>
 * This map supports strong, weak, and soft keys and values. By default, keys are weak, and values are strong. Such a configuration offers similar behavior to
 * {@link java.util.WeakHashMap}, entries of this map are periodically removed once their corresponding keys are no longer referenced outside of this map. In
 * other words, this map will not prevent a key from being discarded by the garbage collector. Once a key has been discarded by the collector, the corresponding
 * entry is no longer visible to this map; however, the entry may occupy space until a future map operation decides to reclaim it. For this reason, summary
 * functions such as {@code size} and {@code isEmpty} might return a value greater than the observed number of entries. In order to support a high level of
 * concurrency, stale entries are only reclaimed during blocking (usually mutating) operations.
 * </p>
 * <p>
 * Enabling soft keys allows entries in this map to remain until their space is absolutely needed by the garbage collector. This is unlike weak keys which can
 * be reclaimed as soon as they are no longer referenced by a normal strong reference. The primary use case for soft keys is a cache, which ideally occupies
 * memory that is not in use for as long as possible.
 * </p>
 * <p>
 * By default, values are held using a normal strong reference. This provides the commonly desired guarantee that a value will always have at least the same
 * life-span as its key. For this reason, care should be taken to ensure that a value never refers, either directly or indirectly, to its key, thereby
 * preventing reclamation. If this is unavoidable, then it is recommended to use the same reference type in use for the key. However, it should be noted that
 * non-strong values may disappear before their corresponding key.
 * </p>
 * <p>
 * While this map does allow the use of both strong keys and values, it is recommended you use {@link java.util.concurrent.ConcurrentHashMap} for such a
 * configuration, since it is optimized for that case.
 * </p>
 * <p>
 * Just like {@link java.util.concurrent.ConcurrentHashMap}, this class obeys the same functional specification as {@link Hashtable}, and includes versions of
 * methods corresponding to each method of {@code Hashtable}. However, even though all operations are thread-safe, retrieval operations do <em>not</em> entail
 * locking, and there is <em>not</em> any support for locking the entire map in a way that prevents all access. This class is fully interoperable with
 * {@code Hashtable} in programs that rely on its thread safety but not on its synchronization details.
 * </p>
 * <p>
 * Retrieval operations (including {@code get}) generally do not block, so they may overlap with update operations (including {@code put} and {@code remove}).
 * Retrievals reflect the results of the most recently <em>completed</em> update operations holding upon their onset. For aggregate operations such as
 * {@code putAll} and {@code clear}, concurrent retrievals may reflect insertion or removal of only some entries. Similarly, Iterators and Enumerations return
 * elements reflecting the state of the hash map at some point at or since the creation of the iterator/enumeration. They do <em>not</em> throw
 * {@link ConcurrentModificationException}. However, iterators are designed to be used by only one thread at a time.
 * </p>
 * <p>
 * The allowed concurrency among update operations is guided by the optional {@code concurrencyLevel} constructor argument (default
 * {@value #DEFAULT_CONCURRENCY_LEVEL}), which is used as a hint for internal sizing. The map is internally partitioned to try to permit the indicated number of
 * concurrent updates without contention. Because placement in hash tables is essentially random, the actual concurrency will vary. Ideally, you should choose a
 * value to accommodate as many threads as will ever concurrently modify the map. Using a significantly higher value than you need can waste space and time, and
 * a significantly lower value can lead to thread contention. But overestimates and underestimates within an order of magnitude do not usually have much
 * noticeable impact. A value of one is appropriate when it is known that only one thread will modify and all others will only read. Also, resizing this or any
 * other kind of hash map is a relatively slow operation, so, when possible, it is a good idea that you provide estimates of expected map sizes in constructors.
 * </p>
 * <p>
 * This class and its views and iterators implement all of the <em>optional</em> methods of the {@link Map} and {@link Iterator} interfaces.
 * </p>
 * <p>
 * Like {@link Hashtable} but unlike {@link HashMap}, this class does <em>not</em> allow {@code null} to be used as a key or value.
 * </p>
 * <p>
 * Provenance: Copied and edited from Apache Groovy git master at commit 77dc80a7512ceb2168b1bc866c3d0c69b002fe11; via Doug Lea, Jason T. Greene, with
 * assistance from members of JCP JSR-166, and Hazelcast.
 * </p>
 *
 * @param <K> the type of keys maintained by this map.
 * @param <V> the type of mapped values.
 */
public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

    /**
     * Builds new ConcurrentReferenceHashMap instances.
     * <p>
     * By default, keys are weak, and values are strong.
     * </p>
     * <p>
     * The default values are:
     * </p>
     * <ul>
     * <li>concurrency level: {@value #DEFAULT_CONCURRENCY_LEVEL}</li>
     * <li>initial capacity: {@value #DEFAULT_INITIAL_CAPACITY}</li>
     * <li>key reference type: {@link ReferenceType#WEAK}</li>
     * <li>load factor: {@value #DEFAULT_LOAD_FACTOR}</li>
     * <li>options: {@code null}</li>
     * <li>source map: {@code null}</li>
     * <li>value reference type: {@link ReferenceType#STRONG}</li>
     * </ul>
     *
     * @param <K> the type of keys.
     * @param <V> the type of values.
     */
    public static class Builder<K, V> implements Supplier<ConcurrentReferenceHashMap<K, V>> {

        private static final Map<?, ?> DEFAULT_SOURCE_MAP = null;

        private int initialCapacity = DEFAULT_INITIAL_CAPACITY;
        private float loadFactor = DEFAULT_LOAD_FACTOR;
        private int concurrencyLevel = DEFAULT_CONCURRENCY_LEVEL;
        private ReferenceType keyReferenceType = DEFAULT_KEY_TYPE;
        private ReferenceType valueReferenceType = DEFAULT_VALUE_TYPE;
        private EnumSet<Option> options = DEFAULT_OPTIONS;
        @SuppressWarnings("unchecked")
        private Map<? extends K, ? extends V> sourceMap = (Map<? extends K, ? extends V>) DEFAULT_SOURCE_MAP;

        /**
         * Builds a new {@link ConcurrentReferenceHashMap}.
         * <p>
         * By default, keys are weak, and values are strong.
         * </p>
         * <p>
         * The default values are:
         * </p>
         * <ul>
         * <li>concurrency level: {@value #DEFAULT_CONCURRENCY_LEVEL}</li>
         * <li>initial capacity: {@value #DEFAULT_INITIAL_CAPACITY}</li>
         * <li>key reference type: {@link ReferenceType#WEAK}</li>
         * <li>load factor: {@value #DEFAULT_LOAD_FACTOR}</li>
         * <li>options: {@code null}</li>
         * <li>source map: {@code null}</li>
         * <li>value reference type: {@link ReferenceType#STRONG}</li>
         * </ul>
         */
        @Override
        public ConcurrentReferenceHashMap<K, V> get() {
            final ConcurrentReferenceHashMap<K, V> map = new ConcurrentReferenceHashMap<>(initialCapacity, loadFactor, concurrencyLevel, keyReferenceType,
                    valueReferenceType, options);
            if (sourceMap != null) {
                map.putAll(sourceMap);
            }
            return map;
        }

        /**
         * Sets the estimated number of concurrently updating threads. The implementation performs internal sizing to try to accommodate this many threads.
         *
         * @param concurrencyLevel estimated number of concurrently updating threads
         * @return this instance.
         */
        public Builder<K, V> setConcurrencyLevel(final int concurrencyLevel) {
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }

        /**
         * Sets the initial capacity. The implementation performs internal sizing to accommodate this many elements.
         *
         * @param initialCapacity the initial capacity.
         * @return this instance.
         */
        public Builder<K, V> setInitialCapacity(final int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }

        /**
         * Sets the reference type to use for keys.
         *
         * @param keyReferenceType the reference type to use for keys.
         * @return this instance.
         */
        public Builder<K, V> setKeyReferenceType(final ReferenceType keyReferenceType) {
            this.keyReferenceType = keyReferenceType;
            return this;
        }

        /**
         * Sets the load factor factor, used to control resizing. Resizing may be performed when the average number of elements per bin exceeds this threshold.
         *
         * @param loadFactor the load factor factor, used to control resizing
         * @return this instance.
         */
        public Builder<K, V> setLoadFactor(final float loadFactor) {
            this.loadFactor = loadFactor;
            return this;
        }

        /**
         * Sets the behavioral options.
         *
         * @param options the behavioral options.
         * @return this instance.
         */
        public Builder<K, V> setOptions(final EnumSet<Option> options) {
            this.options = options;
            return this;
        }

        /**
         * Sets the values to load into a new map.
         *
         * @param sourceMap the values to load into a new map.
         * @return this instance.
         */
        public Builder<K, V> setSourceMap(final Map<? extends K, ? extends V> sourceMap) {
            this.sourceMap = sourceMap;
            return this;
        }

        /**
         * Sets the reference type to use for values.
         *
         * @param valueReferenceType the reference type to use for values.
         * @return this instance.
         */
        public Builder<K, V> setValueReferenceType(final ReferenceType valueReferenceType) {
            this.valueReferenceType = valueReferenceType;
            return this;
        }

        /**
         * Sets key reference type to {@link ReferenceType#SOFT}.
         *
         * @return this instance.
         */
        public Builder<K, V> softKeys() {
            setKeyReferenceType(ReferenceType.SOFT);
            return this;
        }

        /**
         * Sets value reference type to {@link ReferenceType#SOFT}.
         *
         * @return this instance.
         */
        public Builder<K, V> softValues() {
            setValueReferenceType(ReferenceType.SOFT);
            return this;
        }

        /**
         * Sets key reference type to {@link ReferenceType#STRONG}.
         *
         * @return this instance.
         */
        public Builder<K, V> strongKeys() {
            setKeyReferenceType(ReferenceType.STRONG);
            return this;
        }

        /**
         * Sets value reference type to {@link ReferenceType#STRONG}.
         *
         * @return this instance.
         */
        public Builder<K, V> strongValues() {
            setValueReferenceType(ReferenceType.STRONG);
            return this;
        }

        /**
         * Sets key reference type to {@link ReferenceType#WEAK}.
         *
         * @return this instance.
         */
        public Builder<K, V> weakKeys() {
            setKeyReferenceType(ReferenceType.WEAK);
            return this;
        }

        /**
         * Sets value reference type to {@link ReferenceType#WEAK}.
         *
         * @return this instance.
         */
        public Builder<K, V> weakValues() {
            setValueReferenceType(ReferenceType.WEAK);
            return this;
        }

    }

    /**
     * The basic strategy is to subdivide the table among Segments, each of which itself is a concurrently readable hash table.
     */
    private final class CachedEntryIterator extends HashIterator implements Iterator<Entry<K, V>> {
        private final InitializableEntry<K, V> entry = new InitializableEntry<>();

        @Override
        public Entry<K, V> next() {
            final HashEntry<K, V> e = super.nextEntry();
            return entry.init(e.key(), e.value());
        }
    }

    private final class EntryIterator extends HashIterator implements Iterator<Entry<K, V>> {
        @Override
        public Entry<K, V> next() {
            final HashEntry<K, V> e = super.nextEntry();
            return new WriteThroughEntry(e.key(), e.value());
        }
    }

    private final class EntrySet extends AbstractSet<Entry<K, V>> {

        private final boolean cached;

        private EntrySet(final boolean cached) {
            this.cached = cached;
        }

        @Override
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }

        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Entry<?, ?> e = (Entry<?, ?>) o;
            final V v = ConcurrentReferenceHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentReferenceHashMap.this.isEmpty();
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return cached ? new CachedEntryIterator() : new EntryIterator();
        }

        @Override
        public boolean remove(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Entry<?, ?> e = (Entry<?, ?>) o;
            return ConcurrentReferenceHashMap.this.remove(e.getKey(), e.getValue());
        }

        @Override
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }
    }

    /**
     * ConcurrentReferenceHashMap list entry. Note that this is never exported out as a user-visible Map.Entry.
     * <p>
     * Because the value field is volatile, not final, it is legal wrt the Java Memory Model for an unsynchronized reader to see null instead of initial value
     * when read via a data race. Although a reordering leading to this is not likely to ever actually occur, the Segment.readValueUnderLock method is used as a
     * backup in case a null (pre-initialized) value is ever seen in an unsynchronized access method.
     * </p>
     */
    private static final class HashEntry<K, V> {

        @SuppressWarnings("unchecked")
        static <K, V> HashEntry<K, V>[] newArray(final int i) {
            return new HashEntry[i];
        }

        private final Object keyRef;
        private final int hash;
        private volatile Object valueRef;
        private final HashEntry<K, V> next;

        HashEntry(final K key, final int hash, final HashEntry<K, V> next, final V value, final ReferenceType keyType, final ReferenceType valueType,
                final ReferenceQueue<Object> refQueue) {
            this.hash = hash;
            this.next = next;
            this.keyRef = newKeyReference(key, keyType, refQueue);
            this.valueRef = newValueReference(value, valueType, refQueue);
        }

        @SuppressWarnings("unchecked")
        V dereferenceValue(final Object value) {
            if (value instanceof KeyReference) {
                return ((Reference<V>) value).get();
            }
            return (V) value;
        }

        @SuppressWarnings("unchecked")
        K key() {
            if (keyRef instanceof KeyReference) {
                return ((Reference<K>) keyRef).get();
            }
            return (K) keyRef;
        }

        Object newKeyReference(final K key, final ReferenceType keyType, final ReferenceQueue<Object> refQueue) {
            if (keyType == ReferenceType.WEAK) {
                return new WeakKeyReference<>(key, hash, refQueue);
            }
            if (keyType == ReferenceType.SOFT) {
                return new SoftKeyReference<>(key, hash, refQueue);
            }

            return key;
        }

        Object newValueReference(final V value, final ReferenceType valueType, final ReferenceQueue<Object> refQueue) {
            if (valueType == ReferenceType.WEAK) {
                return new WeakValueReference<>(value, keyRef, hash, refQueue);
            }
            if (valueType == ReferenceType.SOFT) {
                return new SoftValueReference<>(value, keyRef, hash, refQueue);
            }

            return value;
        }

        void setValue(final V value, final ReferenceType valueType, final ReferenceQueue<Object> refQueue) {
            this.valueRef = newValueReference(value, valueType, refQueue);
        }

        V value() {
            return dereferenceValue(valueRef);
        }
    }

    private abstract class HashIterator {
        private int nextSegmentIndex;
        private int nextTableIndex;
        private HashEntry<K, V>[] currentTable;
        private HashEntry<K, V> nextEntry;
        private HashEntry<K, V> lastReturned;
        // Strong reference to weak key (prevents gc)
        private K currentKey;

        private HashIterator() {
            nextSegmentIndex = segments.length - 1;
            nextTableIndex = -1;
            advance();
        }

        final void advance() {
            if (nextEntry != null && (nextEntry = nextEntry.next) != null) {
                return;
            }
            while (nextTableIndex >= 0) {
                if ((nextEntry = currentTable[nextTableIndex--]) != null) {
                    return;
                }
            }
            while (nextSegmentIndex >= 0) {
                final Segment<K, V> seg = segments[nextSegmentIndex--];
                if (seg.count != 0) {
                    currentTable = seg.table;
                    for (int j = currentTable.length - 1; j >= 0; --j) {
                        if ((nextEntry = currentTable[j]) != null) {
                            nextTableIndex = j - 1;
                            return;
                        }
                    }
                }
            }
        }

        public boolean hasMoreElements() {
            return hasNext();
        }

        public boolean hasNext() {
            while (nextEntry != null) {
                if (nextEntry.key() != null) {
                    return true;
                }
                advance();
            }
            return false;
        }

        HashEntry<K, V> nextEntry() {
            do {
                if (nextEntry == null) {
                    throw new NoSuchElementException();
                }
                lastReturned = nextEntry;
                currentKey = lastReturned.key();
                advance();
            } while /* Skip GC'd keys */ (currentKey == null);
            return lastReturned;
        }

        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentReferenceHashMap.this.remove(currentKey);
            lastReturned = null;
        }
    }

    private static final class InitializableEntry<K, V> implements Entry<K, V> {
        private K key;
        private V value;

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        public Entry<K, V> init(final K key, final V value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public V setValue(final V value) {
            throw new UnsupportedOperationException();
        }
    }

    private final class KeyIterator extends HashIterator implements Iterator<K>, Enumeration<K> {
        @Override
        public K next() {
            return super.nextEntry().key();
        }

        @Override
        public K nextElement() {
            return super.nextEntry().key();
        }
    }

    private interface KeyReference {
        int keyHash();

        Object keyRef();
    }

    private final class KeySet extends AbstractSet<K> {
        @Override
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }

        @Override
        public boolean contains(final Object o) {
            return ConcurrentReferenceHashMap.this.containsKey(o);
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentReferenceHashMap.this.isEmpty();
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public boolean remove(final Object o) {
            return ConcurrentReferenceHashMap.this.remove(o) != null;
        }

        @Override
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }
    }

    /**
     * Behavior-changing configuration options for the map
     */
    public enum Option {
        /**
         * Indicates that referential-equality (== instead of .equals()) should be used when locating keys. This offers similar behavior to
         * {@link IdentityHashMap}
         */
        IDENTITY_COMPARISONS
    }

    /**
     * An option specifying which Java reference type should be used to refer to a key and/or value.
     */
    public enum ReferenceType {
        /**
         * Indicates a normal Java strong reference should be used
         */
        STRONG,
        /**
         * Indicates a {@link WeakReference} should be used
         */
        WEAK,
        /**
         * Indicates a {@link SoftReference} should be used
         */
        SOFT
    }

    /**
     * Segments are specialized versions of hash tables. This subclasses from ReentrantLock opportunistically, just to simplify some locking and avoid separate
     * construction.
     * <p>
     * Segments maintain a table of entry lists that are ALWAYS kept in a consistent state, so they can be read without locking. Next fields of nodes are
     * immutable (final). All list additions are performed at the front of each bin. This makes it easy to check changes, and also fast to traverse. When nodes
     * would otherwise be changed, new nodes are created to replace them. This works well for hash tables since the bin lists tend to be short. (The average
     * length is less than two for the default load factor threshold.)
     * </p>
     * <p>
     * Read operations can thus proceed without locking, but rely on selected uses of volatiles to ensure that completed write operations performed by other
     * threads are noticed. For most purposes, the "count" field, tracking the number of elements, serves as that volatile variable ensuring visibility. This is
     * convenient because this field needs to be read in many read operations anyway:
     * </p>
     * <ul>
     * <li>All (unsynchronized) read operations must first read the "count" field, and should not look at table entries if it is 0.</li>
     * <li>All (synchronized) write operations should write to the "count" field after structurally changing any bin. The operations must not take any action
     * that could even momentarily cause a concurrent read operation to see inconsistent data. This is made easier by the nature of the read operations in Map.
     * For example, no operation can reveal that the table has grown but the threshold has not yet been updated, so there are no atomicity requirements for this
     * with respect to reads.</li>
     * </ul>
     * <p>
     * As a guide, all critical volatile reads and writes to the count field are marked in code comments.
     * </p>
     *
     * @param <K> the type of keys maintained by this Segment.
     * @param <V> the type of mapped values.
     */
    private static final class Segment<K, V> extends ReentrantLock {

        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        static <K, V> Segment<K, V>[] newArray(final int i) {
            return new Segment[i];
        }

        /**
         * The number of elements in this segment's region.
         */
        // @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification =
        // "I trust Doug Lea's technical decision")
        private transient volatile int count;

        /**
         * Number of updates that alter the size of the table. This is used during bulk-read methods to make sure they see a consistent snapshot: If modCounts
         * change during a traversal of segments computing size or checking containsValue, then we might have an inconsistent view of state so (usually) we must
         * retry.
         */
        // @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification =
        // "I trust Doug Lea's technical decision")
        private transient int modCount;

        /**
         * The table is rehashed when its size exceeds this threshold. (The value of this field is always <code>(int)(capacity *
         * loadFactor)</code>.)
         */
        private transient int threshold;

        /**
         * The per-segment table.
         */
        private transient volatile HashEntry<K, V>[] table;

        /**
         * The load factor for the hash table. Even though this value is same for all segments, it is replicated to avoid needing links to outer object.
         */
        private final float loadFactor;

        /**
         * The collected weak-key reference queue for this segment. This should be (re)initialized whenever table is assigned,
         */
        private transient volatile ReferenceQueue<Object> refQueue;

        private final ReferenceType keyType;

        private final ReferenceType valueType;

        private final boolean identityComparisons;

        Segment(final int initialCapacity, final float loadFactor, final ReferenceType keyType, final ReferenceType valueType,
                final boolean identityComparisons) {
            this.loadFactor = loadFactor;
            this.keyType = keyType;
            this.valueType = valueType;
            this.identityComparisons = identityComparisons;
            setTable(HashEntry.<K, V>newArray(initialCapacity));
        }

        V apply(final K key, final int hash, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            lock();
            try {
                final V oldValue = get(key, hash);
                final V newValue = remappingFunction.apply(key, oldValue);

                if (newValue == null) {
                    // delete mapping
                    if (oldValue != null) {
                        // something to remove
                        removeInternal(key, hash, oldValue, false);
                    }
                    return null;
                }
                // add or replace old mapping
                putInternal(key, hash, newValue, null, false);
                return newValue;
            } finally {
                unlock();
            }
        }

        V applyIfPresent(final K key, final int hash, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            lock();
            try {
                final V oldValue = get(key, hash);
                if (oldValue == null) {
                    return null;
                }

                final V newValue = remappingFunction.apply(key, oldValue);

                if (newValue == null) {
                    removeInternal(key, hash, oldValue, false);
                    return null;
                }
                putInternal(key, hash, newValue, null, false);
                return newValue;
            } finally {
                unlock();
            }
        }

        void clear() {
            if (count != 0) {
                lock();
                try {
                    final HashEntry<K, V>[] tab = table;
                    Arrays.fill(tab, null);
                    ++modCount;
                    // replace the reference queue to avoid unnecessary stale cleanups
                    refQueue = new ReferenceQueue<>();
                    // write-volatile
                    count = 0;
                } finally {
                    unlock();
                }
            }
        }

        boolean containsKey(final Object key, final int hash) {
            // read-volatile
            if (count != 0) {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && keyEq(key, e.key())) {
                        return true;
                    }
                    e = e.next;
                }
            }
            return false;
        }

        boolean containsValue(final Object value) {
            // read-volatile
            if (count != 0) {
                final HashEntry<K, V>[] tab = table;
                final int len = tab.length;
                for (int i = 0; i < len; i++) {
                    for (HashEntry<K, V> e = tab[i]; e != null; e = e.next) {
                        final Object opaque = e.valueRef;
                        V v;
                        if (opaque == null) {
                            // recheck
                            v = readValueUnderLock(e);
                        } else {
                            v = e.dereferenceValue(opaque);
                        }
                        if (value.equals(v)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        /* Specialized implementations of map methods */
        V get(final Object key, final int hash) {
            // read-volatile
            if (count != 0) {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && keyEq(key, e.key())) {
                        final Object opaque = e.valueRef;
                        if (opaque != null) {
                            return e.dereferenceValue(opaque);
                        }
                        // recheck
                        return readValueUnderLock(e);
                    }
                    e = e.next;
                }
            }
            return null;
        }

        /**
         * Gets properly casted first entry of bin for given hash.
         */
        HashEntry<K, V> getFirst(final int hash) {
            final HashEntry<K, V>[] tab = table;
            return tab[hash & tab.length - 1];
        }

        V getValue(final K key, final V value, final Function<? super K, ? extends V> function) {
            return value != null ? value : function.apply(key);
        }

        private boolean keyEq(final Object src, final Object dest) {
            return identityComparisons ? src == dest : src.equals(dest);
        }

        HashEntry<K, V> newHashEntry(final K key, final int hash, final HashEntry<K, V> next, final V value) {
            return new HashEntry<>(key, hash, next, value, keyType, valueType, refQueue);
        }

        /**
         * This method must be called with exactly one of <code>value</code> and <code>function</code> non-null.
         **/
        V put(final K key, final int hash, final V value, final Function<? super K, ? extends V> function, final boolean onlyIfAbsent) {
            lock();
            try {
                return putInternal(key, hash, value, function, onlyIfAbsent);
            } finally {
                unlock();
            }
        }

        private V putInternal(final K key, final int hash, final V value, final Function<? super K, ? extends V> function, final boolean onlyIfAbsent) {
            removeStale();
            int c = count;
            // ensure capacity
            if (c++ > threshold) {
                final int reduced = rehash();
                // adjust from possible weak cleanups
                if (reduced > 0) {
                    // write-volatile
                    count = (c -= reduced) - 1;
                }
            }
            final HashEntry<K, V>[] tab = table;
            final int index = hash & tab.length - 1;
            final HashEntry<K, V> first = tab[index];
            HashEntry<K, V> e = first;
            while (e != null && (e.hash != hash || !keyEq(key, e.key()))) {
                e = e.next;
            }
            V resultValue;
            if (e != null) {
                resultValue = e.value();
                if (!onlyIfAbsent) {
                    e.setValue(getValue(key, value, function), valueType, refQueue);
                }
            } else {
                final V v = getValue(key, value, function);
                resultValue = function != null ? v : null;

                if (v != null) {
                    ++modCount;
                    tab[index] = newHashEntry(key, hash, first, v);
                    // write-volatile
                    count = c;
                }
            }
            return resultValue;
        }

        /**
         * Reads value field of an entry under lock. Called if value field ever appears to be null. This is possible only if a compiler happens to reorder a
         * HashEntry initialization with its table assignment, which is legal under memory model but is not known to ever occur.
         */
        V readValueUnderLock(final HashEntry<K, V> e) {
            lock();
            try {
                removeStale();
                return e.value();
            } finally {
                unlock();
            }
        }

        int rehash() {
            final HashEntry<K, V>[] oldTable = table;
            final int oldCapacity = oldTable.length;
            if (oldCapacity >= MAXIMUM_CAPACITY) {
                return 0;
            }
            //
            // Reclassify nodes in each list to new Map. Because we are using power-of-two expansion, the elements from each bin must either stay at the same
            // index, or move with a power of two offset. We eliminate unnecessary node creation by catching cases where old nodes can be reused because their
            // next fields won't change. Statistically, at the default threshold, only about one-sixth of them need cloning when a table doubles. The nodes they
            // replace will be garbage collectable as soon as they are no longer referenced by any reader thread that may be in the midst of traversing table
            // right now.
            //
            final HashEntry<K, V>[] newTable = HashEntry.newArray(oldCapacity << 1);
            threshold = (int) (newTable.length * loadFactor);
            final int sizeMask = newTable.length - 1;
            int reduce = 0;
            for (int i = 0; i < oldCapacity; i++) {
                // We need to guarantee that any existing reads of old Map can
                // proceed. So we cannot yet null out each bin.
                final HashEntry<K, V> e = oldTable[i];
                if (e != null) {
                    final HashEntry<K, V> next = e.next;
                    final int idx = e.hash & sizeMask;
                    // Single node on list
                    if (next == null) {
                        newTable[idx] = e;
                    } else {
                        // Reuse trailing consecutive sequence at same slot
                        HashEntry<K, V> lastRun = e;
                        int lastIdx = idx;
                        for (HashEntry<K, V> last = next; last != null; last = last.next) {
                            final int k = last.hash & sizeMask;
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        newTable[lastIdx] = lastRun;
                        // Clone all remaining nodes
                        for (HashEntry<K, V> p = e; p != lastRun; p = p.next) {
                            // Skip GC'd weak refs
                            final K key = p.key();
                            if (key == null) {
                                reduce++;
                                continue;
                            }
                            final int k = p.hash & sizeMask;
                            final HashEntry<K, V> n = newTable[k];
                            newTable[k] = newHashEntry(key, p.hash, n, p.value());
                        }
                    }
                }
            }
            table = newTable;
            return reduce;
        }

        /**
         * Removes match on key only if value is null, else match both.
         */
        V remove(final Object key, final int hash, final Object value, final boolean refRemove) {
            lock();
            try {
                return removeInternal(key, hash, value, refRemove);
            } finally {
                unlock();
            }
        }

        private V removeInternal(final Object key, final int hash, final Object value, final boolean refRemove) {
            if (!refRemove) {
                removeStale();
            }
            int c = count - 1;
            final HashEntry<K, V>[] tab = table;
            final int index = hash & tab.length - 1;
            final HashEntry<K, V> first = tab[index];
            HashEntry<K, V> e = first;
            // a ref remove operation compares the Reference instance
            while (e != null && key != e.keyRef && (refRemove || hash != e.hash || !keyEq(key, e.key()))) {
                e = e.next;
            }

            V oldValue = null;
            if (e != null) {
                final V v = e.value();
                if (value == null || value.equals(v)) {
                    oldValue = v;
                    // All entries following removed node can stay
                    // in list, but all preceding ones need to be
                    // cloned.
                    ++modCount;
                    HashEntry<K, V> newFirst = e.next;
                    for (HashEntry<K, V> p = first; p != e; p = p.next) {
                        final K pKey = p.key();
                        // Skip GC'd keys
                        if (pKey == null) {
                            c--;
                            continue;
                        }
                        newFirst = newHashEntry(pKey, p.hash, newFirst, p.value());
                    }
                    tab[index] = newFirst;
                    // write-volatile
                    count = c;
                }
            }
            return oldValue;
        }

        void removeStale() {
            KeyReference ref;
            while ((ref = (KeyReference) refQueue.poll()) != null) {
                remove(ref.keyRef(), ref.keyHash(), null, true);
            }
        }

        V replace(final K key, final int hash, final V newValue) {
            lock();
            try {
                return replaceInternal(key, hash, newValue);
            } finally {
                unlock();
            }
        }

        boolean replace(final K key, final int hash, final V oldValue, final V newValue) {
            lock();
            try {
                return replaceInternal2(key, hash, oldValue, newValue);
            } finally {
                unlock();
            }
        }

        private V replaceInternal(final K key, final int hash, final V newValue) {
            removeStale();
            HashEntry<K, V> e = getFirst(hash);
            while (e != null && (e.hash != hash || !keyEq(key, e.key()))) {
                e = e.next;
            }
            V oldValue = null;
            if (e != null) {
                oldValue = e.value();
                e.setValue(newValue, valueType, refQueue);
            }
            return oldValue;
        }

        private boolean replaceInternal2(final K key, final int hash, final V oldValue, final V newValue) {
            removeStale();
            HashEntry<K, V> e = getFirst(hash);
            while (e != null && (e.hash != hash || !keyEq(key, e.key()))) {
                e = e.next;
            }
            boolean replaced = false;
            if (e != null && oldValue.equals(e.value())) {
                replaced = true;
                e.setValue(newValue, valueType, refQueue);
            }
            return replaced;
        }

        /**
         * Sets table to new HashEntry array. Call only while holding lock or in constructor.
         */
        void setTable(final HashEntry<K, V>[] newTable) {
            threshold = (int) (newTable.length * loadFactor);
            table = newTable;
            refQueue = new ReferenceQueue<>();
        }
    }

    private static class SimpleEntry<K, V> implements Entry<K, V> {

        private static boolean eq(final Object o1, final Object o2) {
            return o1 == null ? o2 == null : o1.equals(o2);
        }

        private final K key;

        private V value;

        SimpleEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Entry<?, ?> e = (Entry<?, ?>) o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        @Override
        public V setValue(final V value) {
            final V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * A soft-key reference which stores the key hash needed for reclamation.
     */
    private static final class SoftKeyReference<K> extends SoftReference<K> implements KeyReference {

        private final int hash;

        SoftKeyReference(final K key, final int hash, final ReferenceQueue<Object> refQueue) {
            super(key, refQueue);
            this.hash = hash;
        }

        @Override
        public int keyHash() {
            return hash;
        }

        @Override
        public Object keyRef() {
            return this;
        }
    }

    private static final class SoftValueReference<V> extends SoftReference<V> implements KeyReference {
        private final Object keyRef;
        private final int hash;

        SoftValueReference(final V value, final Object keyRef, final int hash, final ReferenceQueue<Object> refQueue) {
            super(value, refQueue);
            this.keyRef = keyRef;
            this.hash = hash;
        }

        @Override
        public int keyHash() {
            return hash;
        }

        @Override
        public Object keyRef() {
            return keyRef;
        }
    }

    private final class ValueIterator extends HashIterator implements Iterator<V>, Enumeration<V> {
        @Override
        public V next() {
            return super.nextEntry().value();
        }

        @Override
        public V nextElement() {
            return super.nextEntry().value();
        }
    }

    private final class Values extends AbstractCollection<V> {
        @Override
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }

        @Override
        public boolean contains(final Object o) {
            return ConcurrentReferenceHashMap.this.containsValue(o);
        }

        @Override
        public boolean isEmpty() {
            return ConcurrentReferenceHashMap.this.isEmpty();
        }

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }
    }

    /**
     * A weak-key reference which stores the key hash needed for reclamation.
     */
    private static final class WeakKeyReference<K> extends WeakReference<K> implements KeyReference {
        private final int hash;

        WeakKeyReference(final K key, final int hash, final ReferenceQueue<Object> refQueue) {
            super(key, refQueue);
            this.hash = hash;
        }

        @Override
        public int keyHash() {
            return hash;
        }

        @Override
        public Object keyRef() {
            return this;
        }
    }

    private static final class WeakValueReference<V> extends WeakReference<V> implements KeyReference {
        private final Object keyRef;
        private final int hash;

        WeakValueReference(final V value, final Object keyRef, final int hash, final ReferenceQueue<Object> refQueue) {
            super(value, refQueue);
            this.keyRef = keyRef;
            this.hash = hash;
        }

        @Override
        public int keyHash() {
            return hash;
        }

        @Override
        public Object keyRef() {
            return keyRef;
        }
    }

    /**
     * Custom Entry class used by EntryIterator.next(), that relays setValue changes to the underlying map.
     */
    private final class WriteThroughEntry extends SimpleEntry<K, V> {

        private WriteThroughEntry(final K k, final V v) {
            super(k, v);
        }

        /**
         * Set our entry's value and writes it through to the map. The value to return is somewhat arbitrary: since a WriteThroughEntry does not necessarily
         * track asynchronous changes, the most recent "previous" value could be different from what we return (or could even have been removed in which case
         * the put will re-establish). We do not and cannot guarantee more.
         */
        @Override
        public V setValue(final V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            final V v = super.setValue(value);
            ConcurrentReferenceHashMap.this.put(getKey(), value);
            return v;
        }
    }

    static final ReferenceType DEFAULT_KEY_TYPE = ReferenceType.WEAK;

    static final ReferenceType DEFAULT_VALUE_TYPE = ReferenceType.STRONG;

    static final EnumSet<Option> DEFAULT_OPTIONS = null;

    /**
     * The default initial capacity for this table, used when not otherwise specified in a constructor.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The default load factor for this table, used when not otherwise specified in a constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The default concurrency level for this table, used when not otherwise specified in a constructor.
     */
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified by either of the constructors with arguments. MUST be a power of two &lt;=
     * 1&lt;&lt;30 to ensure that entries are indexable using ints.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The maximum number of segments to allow; used to bound constructor arguments.
     */
    private static final int MAX_SEGMENTS = 1 << 16;

    /**
     * Number of unsynchronized retries in size and containsValue methods before resorting to locking. This is used to avoid unbounded retries if tables undergo
     * continuous modification which would make it impossible to obtain an accurate result.
     */
    private static final int RETRIES_BEFORE_LOCK = 2;

    /**
     * Creates a new Builder.
     * <p>
     * By default, keys are weak, and values are strong.
     * </p>
     * <p>
     * The default values are:
     * </p>
     * <ul>
     * <li>concurrency level: {@value #DEFAULT_CONCURRENCY_LEVEL}</li>
     * <li>initial capacity: {@value #DEFAULT_INITIAL_CAPACITY}</li>
     * <li>key reference type: {@link ReferenceType#WEAK}</li>
     * <li>load factor: {@value #DEFAULT_LOAD_FACTOR}</li>
     * <li>options: {@code null}</li>
     * <li>source map: {@code null}</li>
     * <li>value reference type: {@link ReferenceType#STRONG}</li>
     * </ul>
     *
     * @param <K> the type of keys.
     * @param <V> the type of values.
     * @return a new Builder.
     */
    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    /**
     * Applies a supplemental hash function to a given hashCode, which defends against poor quality hash functions. This is critical because
     * ConcurrentReferenceHashMap uses power-of-two length hash tables, that otherwise encounter collisions for hashCodes that do not differ in lower or upper
     * bits.
     */
    private static int hash(int h) {
        // Spread bits to regularize both segment and index locations,
        // using variant of single-word Wang/Jenkins hash.
        h += h << 15 ^ 0xffffcd7d;
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }

    /**
     * Mask value for indexing into segments. The upper bits of a key's hash code are used to choose the segment.
     */
    private final int segmentMask;

    /**
     * Shift value for indexing within segments.
     */
    private final int segmentShift;

    /**
     * The segments, each of which is a specialized hash table
     */
    private final Segment<K, V>[] segments;

    private final boolean identityComparisons;

    private transient Set<K> keySet;

    private transient Set<Entry<K, V>> entrySet;

    private transient Collection<V> values;

    /**
     * Creates a new, empty map with the specified initial capacity, reference types, load factor, and concurrency level.
     * <p>
     * Behavioral changing options such as {@link Option#IDENTITY_COMPARISONS} can also be specified.
     * </p>
     *
     * @param initialCapacity  the initial capacity. The implementation performs internal sizing to accommodate this many elements.
     * @param loadFactor       the load factor threshold, used to control resizing. Resizing may be performed when the average number of elements per bin
     *                         exceeds this threshold.
     * @param concurrencyLevel the estimated number of concurrently updating threads. The implementation performs internal sizing to try to accommodate this
     *                         many threads.
     * @param keyType          the reference type to use for keys.
     * @param valueType        the reference type to use for values.
     * @param options          the behavioral options.
     * @throws IllegalArgumentException if the initial capacity is negative or the load factor or concurrencyLevel are nonpositive.
     */
    private ConcurrentReferenceHashMap(int initialCapacity, final float loadFactor, int concurrencyLevel, final ReferenceType keyType,
            final ReferenceType valueType, final EnumSet<Option> options) {
        if (!(loadFactor > 0) || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (concurrencyLevel > MAX_SEGMENTS) {
            concurrencyLevel = MAX_SEGMENTS;
        }
        // Find power-of-two sizes best matching arguments
        int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
        }
        segmentShift = 32 - sshift;
        segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity) {
            ++c;
        }
        int cap = 1;
        while (cap < c) {
            cap <<= 1;
        }
        identityComparisons = options != null && options.contains(Option.IDENTITY_COMPARISONS);
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment<>(cap, loadFactor, keyType, valueType, identityComparisons);
        }
    }

    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        for (final Segment<K, V> segment : segments) {
            segment.clear();
        }
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);

        final int hash = hashOf(key);
        final Segment<K, V> segment = segmentFor(hash);
        return segment.apply(key, hash, remappingFunction);
    }

    /**
     * The default implementation is equivalent to the following steps for this {@code map}, then returning the current value or {@code null} if now absent:
     *
     * <pre>{@code
     * if (map.get(key) == null) {
     *     V newValue = mappingFunction.apply(key);
     *     if (newValue != null)
     *         return map.putIfAbsent(key, newValue);
     * }
     * }</pre>
     * <p>
     * The default implementation may retry these steps when multiple threads attempt updates including potentially calling the mapping function multiple times.
     * </p>
     * <p>
     * This implementation assumes that the ConcurrentMap cannot contain null values and {@code get()} returning null unambiguously means the key is absent.
     * Implementations which support null values <strong>must</strong> override this default implementation.
     * </p>
     */
    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mappingFunction);

        final int hash = hashOf(key);
        final Segment<K, V> segment = segmentFor(hash);
        final V v = segment.get(key, hash);
        return v == null ? segment.put(key, hash, null, mappingFunction, true) : v;
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);

        final int hash = hashOf(key);
        final Segment<K, V> segment = segmentFor(hash);
        final V v = segment.get(key, hash);
        if (v == null) {
            return null;
        }

        return segmentFor(hash).applyIfPresent(key, hash, remappingFunction);
    }

    /**
     * Tests if the specified object is a key in this table.
     *
     * @param key possible key
     * @return {@code true} if and only if the specified object is a key in this table, as determined by the {@code equals} method; {@code false} otherwise.
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public boolean containsKey(final Object key) {
        final int hash = hashOf(key);
        return segmentFor(hash).containsKey(key, hash);
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the specified value. Note: This method requires a full internal traversal of the hash table,
     * therefore it is much slower than the method {@code containsKey}.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     * @throws NullPointerException if the specified value is null
     */
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        // See explanation of modCount use above
        final Segment<K, V>[] segments = this.segments;
        final int[] mc = new int[segments.length];
        // Try a few times without locking
        for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
            // final int sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
                // final int c = segments[i].count;
                mcsum += mc[i] = segments[i].modCount;
                if (segments[i].containsValue(value)) {
                    return true;
                }
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (int i = 0; i < segments.length; ++i) {
                    // final int c = segments[i].count;
                    if (mc[i] != segments[i].modCount) {
                        cleanSweep = false;
                        break;
                    }
                }
            }
            if (cleanSweep) {
                return false;
            }
        }
        // Resort to locking all segments
        for (final Segment<K, V> segment : segments) {
            segment.lock();
        }
        boolean found = false;
        try {
            for (final Segment<K, V> segment : segments) {
                if (segment.containsValue(value)) {
                    found = true;
                    break;
                }
            }
        } finally {
            for (final Segment<K, V> segment : segments) {
                segment.unlock();
            }
        }
        return found;
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map. The set is backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. The set supports element removal, which removes the corresponding mapping from the map, via the {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear} operations. It does not support the {@code add} or {@code addAll} operations.
     * <p>
     * The view's {@code iterator} is a "weakly consistent" iterator that will never throw {@link ConcurrentModificationException}, and is guaranteed to
     * traverse elements as they existed upon construction of the iterator, and may (but is not guaranteed to) reflect any modifications subsequent to
     * construction.
     * </p>
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        final Set<Entry<K, V>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet(false));
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key.
     * <p>
     * If this map contains a mapping from a key {@code k} to a value {@code v} such that {@code key.equals(k)}, then this method returns {@code v}; otherwise
     * it returns {@code null}. (There can be at most one such mapping.)
     * </p>
     *
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public V get(final Object key) {
        final int hash = hashOf(key);
        return segmentFor(hash).get(key, hash);
    }

    private int hashOf(final Object key) {
        return hash(identityComparisons ? System.identityHashCode(key) : key.hashCode());
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        final Segment<K, V>[] segments = this.segments;
        //
        // We keep track of per-segment modCounts to avoid ABA problems in which an element in one segment was added and in another removed during traversal, in
        // which case the table was never actually empty at any point. Note the similar use of modCounts in the size() and containsValue() methods, which are
        // the only other methods also susceptible to ABA problems.
        //
        final int[] mc = new int[segments.length];
        int mcsum = 0;
        for (int i = 0; i < segments.length; ++i) {
            if (segments[i].count != 0) {
                return false;
            }
            mcsum += mc[i] = segments[i].modCount;
        }
        // If mcsum happens to be zero, then we know we got a snapshot
        // before any modifications at all were made. This is
        // probably common enough to bother tracking.
        if (mcsum != 0) {
            for (int i = 0; i < segments.length; ++i) {
                if (segments[i].count != 0 || mc[i] != segments[i].modCount) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map. The set is backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. The set supports element removal, which removes the corresponding mapping from this map, via the {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear} operations. It does not support the {@code add} or {@code addAll} operations.
     * <p>
     * The view's {@code iterator} is a "weakly consistent" iterator that will never throw {@link ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator, and may (but is not guaranteed to) reflect any modifications subsequent to construction.
     * </p>
     */
    @Override
    public Set<K> keySet() {
        final Set<K> ks = keySet;
        return ks != null ? ks : (keySet = new KeySet());
    }

    /**
     * Removes any stale entries whose keys have been finalized. Use of this method is normally not necessary since stale entries are automatically removed
     * lazily, when blocking operations are required. However, there are some cases where this operation should be performed eagerly, such as cleaning up old
     * references to a ClassLoader in a multi-classloader environment.
     * <p>
     * Note: this method will acquire locks one at a time across all segments of this table, so this method should be used sparingly.
     * </p>
     */
    public void purgeStaleEntries() {
        for (final Segment<K, V> segment : segments) {
            segment.removeStale();
        }
    }

    /**
     * Maps the specified key to the specified value in this table. Neither the key nor the value can be null.
     * <p>
     * The value can be retrieved by calling the {@code get} method with a key that is equal to the original key.
     * </p>
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}
     * @throws NullPointerException if the specified key or value is null
     */
    @Override
    public V put(final K key, final V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        final int hash = hashOf(key);
        return segmentFor(hash).put(key, hash, value, null, false);
    }

    /**
     * Copies all of the mappings from the specified map to this one. These mappings replace any mappings that this map had for any of the keys currently in the
     * specified map.
     *
     * @param m mappings to be stored in this map
     */
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     */
    @Override
    public V putIfAbsent(final K key, final V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hashOf(key);
        return segmentFor(hash).put(key, hash, value, null, true);
    }

    /**
     * Removes the key (and its corresponding value) from this map. This method does nothing if the key is not in the map.
     *
     * @param key the key that needs to be removed
     * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public V remove(final Object key) {
        final int hash = hashOf(key);
        return segmentFor(hash).remove(key, hash, null, false);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public boolean remove(final Object key, final Object value) {
        final int hash = hashOf(key);
        if (value == null) {
            return false;
        }
        return segmentFor(hash).remove(key, hash, value, false) != null;
    }

    /**
     * {@inheritDoc}
     *
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     */
    @Override
    public V replace(final K key, final V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hashOf(key);
        return segmentFor(hash).replace(key, hash, value);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if any of the arguments are null
     */
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        final int hash = hashOf(key);
        return segmentFor(hash).replace(key, hash, oldValue, newValue);
    }

    /**
     * Returns the segment that should be used for key with given hash
     *
     * @param hash the hash code for the key
     * @return the segment
     */
    private Segment<K, V> segmentFor(final int hash) {
        return segments[hash >>> segmentShift & segmentMask];
    }

    /**
     * Returns the number of key-value mappings in this map. If the map contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        final Segment<K, V>[] segments = this.segments;
        long sum = 0;
        long check = 0;
        final int[] mc = new int[segments.length];
        // Try a few times to get accurate count. On failure due to
        // continuous async changes in table, resort to locking.
        for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
            check = 0;
            sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
                sum += segments[i].count;
                mcsum += mc[i] = segments[i].modCount;
            }
            if (mcsum != 0) {
                for (int i = 0; i < segments.length; ++i) {
                    check += segments[i].count;
                    if (mc[i] != segments[i].modCount) {
                        // force retry
                        check = -1;
                        break;
                    }
                }
            }
            if (check == sum) {
                break;
            }
        }
        if (check != sum) {
            // Resort to locking all segments
            sum = 0;
            for (final Segment<K, V> segment : segments) {
                segment.lock();
            }
            for (final Segment<K, V> segment : segments) {
                sum += segment.count;
            }
            for (final Segment<K, V> segment : segments) {
                segment.unlock();
            }
        }
        return sum > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) sum;
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map. The collection is backed by the map, so changes to the map are reflected in the
     * collection, and vice-versa. The collection supports element removal, which removes the corresponding mapping from this map, via the
     * {@code Iterator.remove}, {@code Collection.remove}, {@code removeAll}, {@code retainAll}, and {@code clear} operations. It does not support the
     * {@code add} or {@code addAll} operations.
     * <p>
     * The view's {@code iterator} is a "weakly consistent" iterator that will never throw {@link ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator, and may (but is not guaranteed to) reflect any modifications subsequent to construction.
     * </p>
     */
    @Override
    public Collection<V> values() {
        final Collection<V> vs = values;
        return vs != null ? vs : (values = new Values());
    }

}
