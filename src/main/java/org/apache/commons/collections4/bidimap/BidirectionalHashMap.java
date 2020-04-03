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
package org.apache.commons.collections4.bidimap;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements a bidirectional, bijective hash map. It asks a key type
 * and a value type. Both must be {@code Comparable}. It allows creating
 * mappings, which are just key/value pairs, and it allows accessing values via
 * keys and keys via values.
 * <p>
 * Suppose we have a mapping {@code (1, A)}. If we access it via the key,
 * {@code A} will be returned. If we access it via the value, {@code 1} will
 * be returned. When asking to put, say, {@code (1, B)} via the key, the
 * aforementioned mapping will become {@code (1, B)}. Conversely, you can update
 * {@code (1, A)} via the value: you can put {@code (2, A)} via it which will
 * update the given mapping to {@code (2, A)}.
 * <p>
 * As a slight optimization, of each mapping, both the key and the value hashes
 * are cached which might give a slight performance advantage when dealing, say,
 * with strings or other containers.
 * <p>
 * Also, unlike most hash tables that maintain collision chains in order to
 * store hash ties, this implementation implements the collision chains as
 * AVL-trees, which guarantees that no (not resizing) operation runs in time
 * worse than {@code O(log n)}.
 * <p>
 * If a user needs to optimize the memory usage of this hash map, a method
 * {@code compact()} is provided which effectively will try to compact the
 * underlying hash tables to a smallest size (power of two) that does not exceed
 * the given maximum load factor.
 * <p>
 * Since this hash map may become sparse (by first adding many mappings and then
 * removing most of them) the primary mappings maintain a doubly-linked list
 * which provides faster iteration and faster moving of all the mappings to a
 * new hash tables when expanding or compacting the map.
 *
 * @param <K> the type of the key.
 * @param <V> the type of the value.
 * @author Rodion "rodde" Efremov
 * @author Chen Guoping "dota17"
 * @version 4.5
 */
public final class BidirectionalHashMap<K extends Comparable<? super K>,
        V extends Comparable<? super V>>
        implements Map<K, V> {

    /**
     * The class for holding the keys, the values, and their respective hash
     * values.
     *
     * @param <K> the type of the key.
     * @param <V> the type of the value.
     */
    public static final class Mapping<K, V> implements Entry<K, V> {
        /**
         * The key.
         */
        private K key;

        /**
         * The value.
         */
        private V value;

        /**
         * The hash value of the key. We cache this in order to have a
         * slight performance advantage when dealing with, say, strings or other
         * containers.
         */
        private int keyHash;

        /**
         * The hash of the value.
         */
        private int valueHash;

        /**
         * Constructs a key pair.
         *
         * @param key   the key;
         * @param value the value.
         */
        Mapping(K key, V value) {
            this.key = Objects.requireNonNull(key,
                    "This BidirectionalHashMap does not permit null keys.");

            this.value = Objects.requireNonNull(value,
                    "This BidirectionalHashMap does not permit null values.");

            this.keyHash = key.hashCode();
            this.valueHash = value.hashCode();
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
        public V setValue(V value) {
            throw new UnsupportedOperationException("Changing values is not supported!");
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (o == this) {
                return true;
            }

            if (!getClass().equals(o.getClass())) {
                return false;
            }

            Mapping<K, V> other = (Mapping<K, V>) o;

            if (keyHash != other.keyHash || valueHash != other.valueHash) {
                return false;
            }

            return key.equals(other.key) &&
                    value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return keyHash ^ valueHash;
        }
    }

    /**
     * Implements the basics of a collision tree nodes.
     *
     * @param <K> the type of the keys.
     * @param <V> the type of the values.
     */
    private abstract static class AbstractCollisionTreeNode
            <K extends Comparable<? super K>, V extends Comparable<? super V>> {

        /**
         * The parent node of this collision tree node. Set to {@code null} if
         * this node is a root.
         */
        AbstractCollisionTreeNode<K, V> parent;

        /**
         * The left child node of this node.
         */
        AbstractCollisionTreeNode<K, V> leftChild;

        /**
         * The right child node of this node.
         */
        AbstractCollisionTreeNode<K, V> rightChild;

        /**
         * The height of this tree node.
         */
        int height;

        /**
         * The mapping of this node.
         */
        Mapping<K, V> keyPair;
    }

    /**
     * Implements the primary collision tree node which maintains an additional
     * doubly linked list for faster iteration and relinking when the load
     * factor is exceeded.
     *
     * @param <K> the type of the keys.
     * @param <V> the type of the values.
     */
    private static final class PrimaryCollisionTreeNode<K extends Comparable<? super K>,
            V extends Comparable<? super V>>
            extends AbstractCollisionTreeNode<K, V> {

        /**
         * The last primary collision tree node that was added before this node.
         */
        PrimaryCollisionTreeNode<K, V> up;

        /**
         * The first primary collision tree node that was added after this node.
         */
        PrimaryCollisionTreeNode<K, V> down;
    }

    /**
     * Implements a secondary collision tree node.
     *
     * @param <K> the type of the keys.
     * @param <V> the type of the values.
     */
    private static final class SecondaryCollisionTreeNode<K extends Comparable<? super K>,
            V extends Comparable<? super V>>
            extends AbstractCollisionTreeNode<K, V> {
    }

    /**
     * Minimum capacity of the hash tables.
     */
    private static final int MINIMUM_INITIAL_CAPACITY = 8;

    /**
     * The smallest upper bound for the maximum load factor.
     */
    private static final float SMALLEST_MAXIMUM_LOAD_FACTOR = 0.2f;

    /**
     * The default hash table capacity.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 8;

    /**
     * The default maximum load factor.
     */
    private static final float DEFAULT_MAXIMUM_LOAD_FACTOR = 1.0f;

    /**
     * The number of mappings in this map.
     */
    private int size;

    /**
     * The hash table containing primary collision trees. The length of this
     * array will be always a power of two.
     */
    private PrimaryCollisionTreeNode<K, V>[] primaryHashTable;

    /**
     * The hash table containing secondary collision trees. The length of this
     * array will be always equal to the length of {@code primaryHashTable} and
     * thus will be always a power of two.
     */
    private SecondaryCollisionTreeNode<K, V>[] secondaryHashTable;

    /**
     * The maximum load factor.
     */
    private final float maximumLoadFactor;

    /**
     * The binary mask used to compute modulo of hash values. Since the hash
     * tables are always of length that is a power of two. This mask will always
     * be {@code 2^k - 1} for some integer {@code k}.
     */
    private int moduloMask;

    /**
     * The head node of the iteration list.
     */
    private PrimaryCollisionTreeNode<K, V> iterationListHead;

    /**
     * The tail node of the iteration list.
     */
    private PrimaryCollisionTreeNode<K, V> iterationListTail;

    /**
     * The inverse map mapping the keys and values in opposite order.
     */
    private Map<V, K> inverseMap;

    /**
     * Used for keeping track of modification during iteration.
     */
    private int modificationCount;

    private class InverseMap implements Map<V, K> {

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public boolean containsKey(Object key) {
            AbstractCollisionTreeNode<K, V> node =
                    getSecondaryCollisionTreeNode((V) key);

            return node != null;
        }

        @Override
        public boolean containsValue(Object value) {
            AbstractCollisionTreeNode<K, V> node =
                    getPrimaryCollisionTreeNode((K) value);

            return node != null;
        }

        @Override
        public K get(Object key) {
            AbstractCollisionTreeNode<K, V> node =
                    getSecondaryCollisionTreeNode((V) key);

            if (node != null) {
                return node.keyPair.key;
            } else {
                return null;
            }
        }

        @Override
        public K put(V secondaryKey, K primaryKey) {
            AbstractCollisionTreeNode<K, V> node =
                    getSecondaryCollisionTreeNode(secondaryKey);

            if (node != null) {
                K oldKey = node.keyPair.key;

                if (primaryKey.equals(oldKey)) {
                    return primaryKey;
                }

                ++modificationCount;

                int newKeyHash = primaryKey.hashCode();
                int currentPrimaryCollisionTreeBucketIndex =
                        node.keyPair.keyHash & moduloMask;
                int newPrimaryCollisionTreeBucketIndex =
                        newKeyHash & moduloMask;

                AbstractCollisionTreeNode<K, V> oppositeNode =
                        getPrimaryTreeNodeViaSecondaryTreeNode(
                                (SecondaryCollisionTreeNode<K, V>) node);

                unlinkCollisionTreeNode(oppositeNode,
                        primaryHashTable,
                        currentPrimaryCollisionTreeBucketIndex);

                linkCollisionTreeNodeToPrimaryTable(
                        oppositeNode,
                        primaryHashTable,
                        newPrimaryCollisionTreeBucketIndex);

                node.keyPair.key = primaryKey;
                node.keyPair.keyHash = newKeyHash;
                return oldKey;
            } else {
                addNewMapping(primaryKey, secondaryKey);
                ++modificationCount;
                ++size;
                return null;
            }
        }

        @Override
        public K remove(Object key) {
            AbstractCollisionTreeNode<K, V> node =
                    getSecondaryCollisionTreeNode((V) key);

            if (node == null) {
                return null;
            }

            K oldPrimaryKey = node.keyPair.key;
            int hashCode = node.keyPair.valueHash;
            int secondaryCollisionTreeBucketIndex = hashCode & moduloMask;
            AbstractCollisionTreeNode<K, V> oppositeNode =
                    getPrimaryTreeNodeViaSecondaryTreeNode(
                            (SecondaryCollisionTreeNode<K, V>) node);

            int oppositeNodeHashCode = oppositeNode.keyPair.keyHash;
            int primaryCollisionTreeBucketIndex = oppositeNodeHashCode
                    & moduloMask;

            unlinkCollisionTreeNode(node,
                    secondaryHashTable,
                    secondaryCollisionTreeBucketIndex);

            unlinkCollisionTreeNode(oppositeNode,
                    primaryHashTable,
                    primaryCollisionTreeBucketIndex);

            unlinkPrimaryCollisionTreeNodeFromIterationChain(
                    (PrimaryCollisionTreeNode<K, V>) oppositeNode);

            ++modificationCount;
            --size;
            return oldPrimaryKey;
        }

        @Override
        public void putAll(Map<? extends V, ? extends K> m) {
            for (Entry<? extends V, ? extends K> e : m.entrySet()) {
                BidirectionalHashMap.this.put(e.getValue(), e.getKey());
            }
        }

        @Override
        public void clear() {
            BidirectionalHashMap.this.clear();
        }

        @Override
        public Set<V> keySet() {
            return new InverseKeySet();
        }

        @Override
        public Collection<K> values() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Entry<V, K>> entrySet() {
            throw new UnsupportedOperationException(
                    "Not supported yet. Use BidirectionalHashMap.entrySet() instead.");
        }

        private final class InverseKeySet implements Set<V> {

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean isEmpty() {
                return size == 0;
            }

            @Override
            public boolean contains(Object o) {
                return BidirectionalHashMap.this.containsValue((V) o);
            }

            @Override
            public Iterator<V> iterator() {
                return new InverseKeySetIterator();
            }

            private final class InverseKeySetIterator implements Iterator<V> {

                private int expectedModificationCount = modificationCount;
                private int cachedSize = size;

                private PrimaryCollisionTreeNode<K, V> currentNode =
                        iterationListHead;

                private PrimaryCollisionTreeNode<K, V> lastIteratedNode =
                        null;

                private int iterated = 0;
                private boolean canRemove = false;

                @Override
                public boolean hasNext() {
                    return iterated < cachedSize;
                }

                @Override
                public V next() {
                    checkModificationCount(expectedModificationCount);

                    if (!hasNext()) {
                        throw new NoSuchElementException(
                                "There is no next value to iterate!");
                    }

                    lastIteratedNode = currentNode;
                    V ret = currentNode.keyPair.value;
                    currentNode = currentNode.down;
                    canRemove = true;
                    ++iterated;
                    return ret;
                }

                @Override
                public void remove() {
                    if (!canRemove) {
                        if (iterated == 0) {
                            throw new IllegalStateException(
                                    "'next()' is not called at least once. " +
                                            "Nothing to remove!");
                        } else {
                            throw new IllegalStateException(
                                    "Cannot remove a value twice!");
                        }
                    }

                    checkModificationCount(expectedModificationCount);
                    BidirectionalHashMap
                            .this.remove(lastIteratedNode.keyPair.key);
                    canRemove = false;
                    expectedModificationCount = modificationCount;
                }
            }

            @Override
            public Object[] toArray() {
                Object[] array = new Object[size];
                int index = 0;

                for (V value : this) {
                    array[index++] = value;
                }

                return array;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                Objects.requireNonNull(a, "The input array is null.");

                if (a.length < size) {
                    T[] array =
                            (T[]) Array.newInstance(a.getClass()
                                    .getComponentType(), size);

                    int index = 0;

                    for (V value : this) {
                        array[index++] = (T) value;
                    }

                    return array;
                }

                int index = 0;

                for (V value : this) {
                    a[index++] = (T) value;
                }

                if (a.length > size) {
                    a[size] = null;
                }

                return a;
            }

            @Override
            public boolean add(V e) {
                throw new UnsupportedOperationException(
                        "add() is not supported.");
            }

            @Override
            public boolean remove(Object o) {
                boolean contains = BidirectionalHashMap.this.containsValue(o);

                if (contains) {
                    // yeah!
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                for (Object o : c) {
                    if (!contains(o)) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public boolean addAll(Collection<? extends V> c) {
                throw new UnsupportedOperationException(
                        "addAll() is not supported!");
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                boolean modified = false;
                Iterator<V> iterator = iterator();

                while (iterator.hasNext()) {
                    V key = iterator.next();

                    if (!c.contains(key)) {
                        modified = true;
                        iterator.remove();
                    }
                }

                return modified;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void clear() {
                BidirectionalHashMap.this.clear();
            }
        }
    }

    public BidirectionalHashMap(int initialCapacity,
                                float maximumLoadFactor) {
        initialCapacity = Math.max(initialCapacity, MINIMUM_INITIAL_CAPACITY);
        maximumLoadFactor = Math.max(maximumLoadFactor,
                SMALLEST_MAXIMUM_LOAD_FACTOR);

        initialCapacity = roundToPowerOfTwo(initialCapacity);

        this.maximumLoadFactor = maximumLoadFactor;
        this.primaryHashTable = new PrimaryCollisionTreeNode[initialCapacity];
        this.secondaryHashTable =
                new SecondaryCollisionTreeNode[initialCapacity];
        this.moduloMask = initialCapacity - 1;
        this.inverseMap = new InverseMap();
    }

    public BidirectionalHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_MAXIMUM_LOAD_FACTOR);
    }

    public BidirectionalHashMap(float maximumLoadFactor) {
        this(DEFAULT_INITIAL_CAPACITY, maximumLoadFactor);
    }

    public BidirectionalHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAXIMUM_LOAD_FACTOR);
    }

    public float getCurrentLoadFactor() {
        return 1.0f * size / primaryHashTable.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public Map<V, K> inverse() {
        return inverseMap;
    }

    @Override
    public boolean containsKey(Object key) {
        AbstractCollisionTreeNode<K, V> collisionTreeNode =
                getPrimaryCollisionTreeNode((K) key);

        return collisionTreeNode != null;
    }

    @Override
    public boolean containsValue(Object value) {
        AbstractCollisionTreeNode<K, V> node =
                getSecondaryCollisionTreeNode((V) value);

        return node != null;
    }

    @Override
    public V get(Object key) {
        AbstractCollisionTreeNode<K, V> node =
                getPrimaryCollisionTreeNode((K) key);

        if (node != null) {
            return node.keyPair.value;
        } else {
            return null;
        }
    }

    @Override
    public V put(K key, V value) {
        AbstractCollisionTreeNode<K, V> node =
                getPrimaryCollisionTreeNode(key);

        if (node != null) {
            V oldValue = node.keyPair.value;

            if (value.equals(oldValue)) {
                return value;
            }

            ++modificationCount;

            int newValueHash = value.hashCode();
            int currentSecondaryCollisionTreeBucketIndex = node.keyPair.valueHash
                    & moduloMask;
            int newSecondaryCollisionTreeBucketIndex = newValueHash
                    & moduloMask;

            AbstractCollisionTreeNode<K, V> oppositeNode =
                    getSecondaryTreeNodeViaPrimaryTreeNode(
                            (PrimaryCollisionTreeNode<K, V>) node);

            unlinkCollisionTreeNode(oppositeNode,
                    secondaryHashTable,
                    currentSecondaryCollisionTreeBucketIndex);

            linkCollisionTreeNodeToSecondaryTable(
                    oppositeNode,
                    secondaryHashTable,
                    newSecondaryCollisionTreeBucketIndex);

            node.keyPair.value = value;
            node.keyPair.valueHash = newValueHash;
            return oldValue;
        } else {
            addNewMapping(key, value);
            ++modificationCount;
            ++size;
            return null;
        }
    }

    @Override
    public V remove(Object key) {
        AbstractCollisionTreeNode<K, V> node =
                getPrimaryCollisionTreeNode((K) key);

        if (node == null) {
            return null;
        }

        V oldValue = node.keyPair.value;
        int hashCode = node.keyPair.keyHash;
        int primaryCollisionTreeBucketIndex = hashCode & moduloMask;
        AbstractCollisionTreeNode<K, V> oppositeNode =
                getSecondaryTreeNodeViaPrimaryTreeNode(
                        (PrimaryCollisionTreeNode<K, V>) node);

        int oppositeNodeHashCode = oppositeNode.keyPair.valueHash;
        int secondaryCollisionTreeBucketIndex = oppositeNodeHashCode
                & moduloMask;

        unlinkCollisionTreeNode(node,
                primaryHashTable,
                primaryCollisionTreeBucketIndex);

        unlinkCollisionTreeNode(oppositeNode,
                secondaryHashTable,
                secondaryCollisionTreeBucketIndex);

        unlinkPrimaryCollisionTreeNodeFromIterationChain(
                (PrimaryCollisionTreeNode<K, V>) node);

        ++modificationCount;
        --size;
        return oldValue;
    }

    @Override
    public void clear() {
        PrimaryCollisionTreeNode<K, V> node = iterationListHead;

        for (; node != null; node = node.down) {
            int primaryCollisionTreeBucketIndex = node.keyPair.keyHash
                    & moduloMask;

            int secondaryCollisionTreeBucketIndex =
                    node.keyPair.valueHash & moduloMask;

            primaryHashTable[primaryCollisionTreeBucketIndex] = null;
            secondaryHashTable[secondaryCollisionTreeBucketIndex] = null;
        }

        modificationCount += size;
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    private final class KeySet implements Set<K> {

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public boolean contains(Object o) {
            return BidirectionalHashMap.this.containsKey((K) o);
        }

        @Override
        public Iterator<K> iterator() {
            return new KeySetIterator();
        }

        private final class KeySetIterator implements Iterator<K> {

            private int expectedModificationCount = modificationCount;
            private int cachedSize = size;

            private PrimaryCollisionTreeNode<K, V> currentNode =
                    iterationListHead;

            private PrimaryCollisionTreeNode<K, V> lastIteratedNode = null;

            private int iterated = 0;
            private boolean canRemove = false;

            @Override
            public boolean hasNext() {
                return iterated < cachedSize;
            }

            @Override
            public K next() {
                checkModificationCount(expectedModificationCount);

                if (!hasNext()) {
                    throw new NoSuchElementException(
                            "There is no next key to iterate!");
                }

                lastIteratedNode = currentNode;
                K ret = currentNode.keyPair.key;
                currentNode = currentNode.down;
                canRemove = true;
                ++iterated;
                return ret;

            }

            @Override
            public void remove() {
                if (!canRemove) {
                    if (iterated == 0) {
                        throw new IllegalStateException(
                                "'next()' is not called at least once. " +
                                        "Nothing to remove!");
                    } else {
                        throw new IllegalStateException(
                                "Cannot remove a key twice!");
                    }
                }

                checkModificationCount(expectedModificationCount);
                BidirectionalHashMap
                        .this.remove(lastIteratedNode.keyPair.key);
                canRemove = false;
                expectedModificationCount = modificationCount;
            }
        }

        @Override
        public Object[] toArray() {
            Object[] array = new Object[size];
            int index = 0;

            for (K key : this) {
                array[index++] = key;
            }

            return array;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            Objects.requireNonNull(a, "The input array is null.");

            if (a.length < size) {
                T[] array =
                        (T[]) Array.newInstance(a.getClass()
                                .getComponentType(), size);
                int index = 0;

                for (K key : this) {
                    array[index++] = (T) key;
                }

                return array;
            }

            int index = 0;

            for (K key : this) {
                a[index++] = (T) key;
            }

            if (a.length > size) {
                a[size] = null;
            }

            return a;
        }

        @Override
        public boolean add(K e) {
            throw new UnsupportedOperationException(
                    "add() is not supported!");
        }

        @Override
        public boolean remove(Object o) {
            boolean contains = BidirectionalHashMap.this.containsKey(o);

            if (contains) {
                BidirectionalHashMap.this.remove(o);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c) {
                if (!contains(o)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException(
                    "addAll() is not supported!");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean modified = false;
            Iterator<K> iterator = iterator();

            while (iterator.hasNext()) {
                K key = iterator.next();

                if (!c.contains(key)) {
                    modified = true;
                    iterator.remove();
                }
            }

            return modified;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean modified = false;

            for (Object o : c) {
                if (remove(o)) {
                    modified = true;
                }
            }

            return modified;
        }

        @Override
        public void clear() {
            BidirectionalHashMap.this.clear();
        }
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException(
                "values() not implemented. Use inverse() instead.");
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    private class EntrySet implements Set<Entry<K, V>> {

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public boolean contains(Object o) {
            Objects.requireNonNull(o, "The input map entry is null!");
            Mapping<K, V> keyPair = (Mapping<K, V>) o;
            AbstractCollisionTreeNode<K, V> node =
                    getPrimaryCollisionTreeNode(keyPair.key);

            if (node == null) {
                return false;
            }

            AbstractCollisionTreeNode<K, V> oppositeNode =
                    BidirectionalHashMap.this
                            .getSecondaryTreeNodeViaPrimaryTreeNode(
                                    (PrimaryCollisionTreeNode<K, V>) node);

            return node != null
                    && oppositeNode.keyPair
                    .value.equals(keyPair.value);
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new KeyPairIterator();
        }

        private final class KeyPairIterator implements Iterator<Entry<K, V>> {

            /**
             * Caches the modification count of the owning BidirectionalHashMap.
             */
            private int expectedModificationCount = modificationCount;

            /**
             * How many key pairs in total we need to visit. We cache this as
             * we may remove key pairs via remove() method.
             */
            private int cachedSize = size;

            /**
             * A pointer to a current node.
             */
            private PrimaryCollisionTreeNode<K, V> currentNode =
                    iterationListHead;

            /**
             * Holds a node last iterated over with next(). It is set to null
             * whenever we remove a node via remove() and haven't yet called
             * next() in order to advance to a node that is removable.
             */
            private PrimaryCollisionTreeNode<K, V> lastIteratedNode = null;

            /**
             * Caches the number of elements iterated via next().
             */
            private int iterated = 0;

            /**
             * Indicates whether we are pointing to a valid current node that is
             * possible to remove.
             */
            private boolean canRemove = false;

            @Override
            public boolean hasNext() {
                return iterated < cachedSize;
            }

            @Override
            public Entry<K, V> next() {
                checkModificationCount(expectedModificationCount);

                if (!hasNext()) {
                    throw new NoSuchElementException(
                            "There is no next key pair to iterate!");
                }

                lastIteratedNode = currentNode;
                Mapping<K, V> ret = currentNode.keyPair;
                currentNode = currentNode.down;
                canRemove = true;
                ++iterated;
                return ret;
            }

            public void remove() {
                if (!canRemove) {
                    if (iterated == 0) {
                        throw new IllegalStateException(
                                "'next()' is not called at least once. " +
                                        "Nothing to remove!");
                    } else {
                        throw new IllegalStateException(
                                "Cannot remove a key pair twice!");
                    }
                }

                checkModificationCount(expectedModificationCount);
                BidirectionalHashMap
                        .this.remove(lastIteratedNode.keyPair.key);
                canRemove = false;
                expectedModificationCount = modificationCount;
            }
        }

        @Override
        public Object[] toArray() {
            Object[] array = new Object[size];
            int index = 0;

            for (Entry<K, V> e : this) {
                array[index++] = e;
            }

            return array;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            Objects.requireNonNull(a, "The input array is null.");

            if (a.length < size) {
                T[] array =
                        (T[]) Array.newInstance(a.getClass()
                                .getComponentType(), size);

                int index = 0;

                for (Entry<K, V> entry : this) {
                    array[index++] = (T) entry;
                }

                return array;
            }

            int index = 0;

            for (Entry<K, V> e : this) {
                a[index++] = (T) e;
            }

            if (a.length > size) {
                a[size] = null;
            }

            return a;
        }

        @Override
        public boolean add(Entry<K, V> e) {
            Object o = put(e.getKey(), e.getValue());
            return !Objects.equals(o, e.getValue());
        }

        /**
         * This method expects a {@link Mapping} as input. Removes a mapping
         * from the owner bidirectional map via the primary key of the input
         * mapping; that is, the secondary key is ignored.
         *
         * @param o the key mapping as an {@code Object}.
         * @return {@code true} if the underlying map changed due to the call.
         */
        @Override
        public boolean remove(Object o) {
            return BidirectionalHashMap
                    .this.remove(((Mapping<K, V>) o).key) != null;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c) {
                if (!contains(o)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            boolean changed = false;

            for (Entry<K, V> e : c) {
                if (add(e)) {
                    changed = true;
                }
            }

            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean modified = false;
            Iterator<Entry<K, V>> iterator = iterator();

            while (iterator.hasNext()) {
                Entry<K, V> entry = iterator.next();

                if (!c.contains(entry)) {
                    modified = true;
                    iterator.remove();
                }
            }

            return modified;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean modified = false;

            for (Object o : c) {
                Mapping<K, V> keyPair = (Mapping<K, V>) o;

                if (remove(keyPair)) {
                    modified = true;
                }
            }

            return modified;
        }

        @Override
        public void clear() {
            BidirectionalHashMap.this.clear();
        }
    }

    /**
     * Makes the internal hash tables as small as possible without exceeding the
     * maximum load factor. If actual condensing is possible, the resulting new
     * hash tables will be of length of a power of two.
     */
    public void compact() {
        int newCapacity = MINIMUM_INITIAL_CAPACITY;

        while (size * maximumLoadFactor > newCapacity) {
            newCapacity <<= 1;
        }

        if (newCapacity == primaryHashTable.length) {
            // No compacting is possible.
            return;
        }


        PrimaryCollisionTreeNode<K, V>[] newPrimaryHashTable =
                new PrimaryCollisionTreeNode[newCapacity];

        SecondaryCollisionTreeNode<K, V>[] newSecondaryHashTable =
                new SecondaryCollisionTreeNode[newCapacity];

        relink(newPrimaryHashTable, newSecondaryHashTable);
        this.moduloMask = newCapacity - 1;
        this.primaryHashTable = newPrimaryHashTable;
        this.secondaryHashTable = newSecondaryHashTable;
        // Do I need the following?
        ++modificationCount;
    }

    private static int roundToPowerOfTwo(int number) {
        int ret = 1;

        while (ret < number) {
            ret <<= 1;
        }

        return ret;
    }

    private static <K extends Comparable<? super K>,
            V extends Comparable<? super V>>
        int getHeight(AbstractCollisionTreeNode<K, V> node) {
        return node != null ? node.height : -1;
    }

    private static <K extends Comparable<? super K>,
            V extends Comparable<? super V>> AbstractCollisionTreeNode<K, V>
        getMinimumNode(AbstractCollisionTreeNode<K, V> node) {
        while (node.leftChild != null) {
            node = node.leftChild;
        }

        return node;
    }

    private static <K extends Comparable<? super K>,
            V extends Comparable<? super V>> AbstractCollisionTreeNode<K, V>
        leftRotate(AbstractCollisionTreeNode<K, V> node1) {
        AbstractCollisionTreeNode<K, V> node2 = node1.rightChild;

        node2.parent = node1.parent;
        node1.parent = node2;
        node1.rightChild = node2.leftChild;
        node2.leftChild = node1;

        if (node1.rightChild != null) {
            node1.rightChild.parent = node1;
        }

        node1.height = Math.max(getHeight(node1.leftChild),
                getHeight(node1.rightChild)) + 1;
        node2.height = Math.max(getHeight(node2.leftChild),
                getHeight(node2.rightChild)) + 1;
        return node2;
    }

    private static <K extends Comparable<? super K>,
            V extends Comparable<? super V>> AbstractCollisionTreeNode<K, V>
        rightRotate(AbstractCollisionTreeNode<K, V> node1) {
        AbstractCollisionTreeNode<K, V> node2 = node1.leftChild;

        node2.parent = node1.parent;
        node1.parent = node2;
        node1.leftChild = node2.rightChild;
        node2.rightChild = node1;

        if (node1.leftChild != null) {
            node1.leftChild.parent = node1;
        }

        node1.height = Math.max(getHeight(node1.leftChild),
                getHeight(node1.rightChild)) + 1;
        node2.height = Math.max(getHeight(node2.leftChild),
                getHeight(node2.rightChild)) + 1;
        return node2;
    }

    private static <K extends Comparable<? super K>,
            V extends Comparable<? super V>> AbstractCollisionTreeNode<K, V>
        leftRightRotate(AbstractCollisionTreeNode<K, V> node1) {
        AbstractCollisionTreeNode<K, V> node2 = node1.leftChild;
        node1.leftChild = leftRotate(node2);
        return rightRotate(node1);
    }

    private static <K extends Comparable<? super K>,
            V extends Comparable<? super V>> AbstractCollisionTreeNode<K, V>
        rightLeftRotate(AbstractCollisionTreeNode<K, V> node1) {
        AbstractCollisionTreeNode<K, V> node2 = node1.rightChild;
        node1.rightChild = rightRotate(node2);
        return leftRotate(node1);
    }

    private static <K extends Comparable<? super K>,
            V extends Comparable<? super V>> void fixCollisionTreeAfterInsertion(
            AbstractCollisionTreeNode<K, V> node,
            AbstractCollisionTreeNode<K, V>[] hashTable,
            int bucketIndex) {
        fixCollisionTree(node, hashTable, bucketIndex, true);
    }

    private static <K extends Comparable<? super K>,
            V extends Comparable<? super V>> void fixCollisionTreeAfterDeletion(
            AbstractCollisionTreeNode<K, V> node,
            AbstractCollisionTreeNode<K, V>[] hashTable,
            int bucketIndex) {
        fixCollisionTree(node, hashTable, bucketIndex, false);
    }

    private static <K extends Comparable<? super K>,
            V extends Comparable<? super V>> void fixCollisionTree(AbstractCollisionTreeNode<K, V> node,
                          AbstractCollisionTreeNode<K, V>[] hashTable,
                          int bucketIndex,
                          boolean insertionMode) {
        AbstractCollisionTreeNode<K, V> grandParent;
        AbstractCollisionTreeNode<K, V> parent = node.parent;
        AbstractCollisionTreeNode<K, V> subtree;

        while (parent != null) {
            if (getHeight(parent.leftChild) ==
                    getHeight(parent.rightChild) + 2) {
                grandParent = parent.parent;

                if (getHeight(parent.leftChild.leftChild) >=
                        getHeight(parent.leftChild.rightChild)) {
                    subtree = rightRotate(parent);
                } else {
                    subtree = leftRightRotate(parent);
                }

                if (grandParent == null) {
                    hashTable[bucketIndex] = subtree;
                } else if (grandParent.leftChild == parent) {
                    grandParent.leftChild = subtree;
                } else {
                    grandParent.rightChild = subtree;
                }

                if (grandParent != null) {
                    grandParent.height =
                            Math.max(getHeight(grandParent.leftChild),
                                    getHeight(grandParent.rightChild)) + 1;
                }

                if (insertionMode) {
                    return;
                }
            } else if (getHeight(parent.rightChild) ==
                    getHeight(parent.leftChild) + 2) {
                grandParent = parent.parent;

                if (getHeight(parent.rightChild.rightChild) >=
                        getHeight(parent.rightChild.leftChild)) {
                    subtree = leftRotate(parent);
                } else {
                    subtree = rightLeftRotate(parent);
                }

                if (grandParent == null) {
                    hashTable[bucketIndex] = subtree;
                } else if (grandParent.leftChild == parent) {
                    grandParent.leftChild = subtree;
                } else {
                    grandParent.rightChild = subtree;
                }

                if (grandParent != null) {
                    grandParent.height =
                            Math.max(getHeight(grandParent.leftChild),
                                    getHeight(grandParent.rightChild)) + 1;
                }

                if (insertionMode) {
                    return;
                }
            }

            parent.height =
                    Math.max(getHeight(parent.leftChild),
                            getHeight(parent.rightChild)) + 1;
            parent = parent.parent;
        }
    }

    private AbstractCollisionTreeNode<K, V> getPrimaryCollisionTreeNode(K primaryKey) {
        int hashCode = primaryKey.hashCode();
        int primaryCollisionTreeBucketIndex = hashCode & moduloMask;

        AbstractCollisionTreeNode<K, V> node =
                primaryHashTable[primaryCollisionTreeBucketIndex];

        while (node != null) {
            int cmp = primaryKey.compareTo(node.keyPair.key);

            if (cmp < 0) {
                node = node.leftChild;
            } else if (cmp > 0) {
                node = node.rightChild;
            } else {
                return node;
            }
        }

        return null;
    }

    private AbstractCollisionTreeNode<K, V> getSecondaryCollisionTreeNode(V secondaryKey) {
        int hashCode = secondaryKey.hashCode();
        int secondaryCollisionTreeBucketIndex = hashCode & moduloMask;

        AbstractCollisionTreeNode<K, V> node =
                secondaryHashTable[secondaryCollisionTreeBucketIndex];

        while (node != null) {
            int cmp = secondaryKey.compareTo(node.keyPair.value);

            if (cmp < 0) {
                node = node.leftChild;
            } else if (cmp > 0) {
                node = node.rightChild;
            } else {
                return node;
            }
        }

        return null;
    }

    private void addNewMapping(K primaryKey, V secondaryKey) {
        expandHashTablesIfNeeded();

        Mapping<K, V> keyPair = new Mapping<>(primaryKey, secondaryKey);

        PrimaryCollisionTreeNode<K, V> primaryCollisionTreeNode =
                new PrimaryCollisionTreeNode<>();

        SecondaryCollisionTreeNode<K, V> secondaryCollisionTreeNode =
                new SecondaryCollisionTreeNode<>();

        primaryCollisionTreeNode.keyPair = keyPair;
        secondaryCollisionTreeNode.keyPair = keyPair;

        int primaryCollisionTreeBucketIndex =
                keyPair.keyHash & moduloMask;

        int secondaryCollisionTreeBucketIndex =
                keyPair.valueHash & moduloMask;

        linkCollisionTreeNodeToPrimaryTable(primaryCollisionTreeNode,
                primaryHashTable,
                primaryCollisionTreeBucketIndex);

        linkCollisionTreeNodeToSecondaryTable(
                secondaryCollisionTreeNode,
                secondaryHashTable,
                secondaryCollisionTreeBucketIndex);

        linkPrimaryCollisionTreeNodeIntoIterationChain(
                primaryCollisionTreeNode);
    }

    private void linkPrimaryCollisionTreeNodeIntoIterationChain(
            PrimaryCollisionTreeNode<K, V> node) {
        if (size == 0) {
            iterationListHead = node;
            iterationListTail = node;
        } else {
            iterationListTail.down = node;
            node.up = iterationListTail;
            iterationListTail = node;
        }
    }

    private void unlinkPrimaryCollisionTreeNodeFromIterationChain(
            PrimaryCollisionTreeNode<K, V> node) {
        if (node.up != null) {
            node.up.down = node.down;
        } else {
            iterationListHead = node.down;
        }

        if (node.down != null) {
            node.down.up = node.up;
        } else {
            iterationListTail = iterationListTail.up;

            if (iterationListTail != null) {
                iterationListTail.down = null;
            }
        }
    }

    private void unlinkCollisionTreeNode(
            AbstractCollisionTreeNode<K, V> node,
            AbstractCollisionTreeNode<K, V>[] hashTable,
            int bucketIndex) {
        if (node.leftChild == null && node.rightChild == null) {
            unlinkCollisionTreeNodeWithNoChildren(node, hashTable, bucketIndex);
        } else if (node.leftChild != null && node.rightChild != null) {
            unlinkCollisionTreeNodeWithBothChildren(node,
                    hashTable,
                    bucketIndex);
        } else {
            unlinkCollisionTreeNodeWithOneChild(node, hashTable, bucketIndex);
        }
    }

    private void unlinkCollisionTreeNodeWithNoChildren(
            AbstractCollisionTreeNode<K, V> node,
            AbstractCollisionTreeNode<K, V>[] hashTable,
            int bucketIndex) {
        if (node.parent == null) {
            hashTable[bucketIndex] = null;
            return;
        }

        if (node.parent.leftChild == node) {
            node.parent.leftChild = null;
        } else {
            node.parent.rightChild = null;
        }

        fixCollisionTreeAfterDeletion(node.parent, hashTable, bucketIndex);
    }

    private void unlinkCollisionTreeNodeWithOneChild(
            AbstractCollisionTreeNode<K, V> node,
            AbstractCollisionTreeNode<K, V>[] hashTable,
            int bucketIndex) {
        AbstractCollisionTreeNode<K, V> child;

        if (node.leftChild != null) {
            child = node.leftChild;
        } else {
            child = node.rightChild;
        }

        AbstractCollisionTreeNode<K, V> parent = node.parent;
        child.parent = parent;

        if (parent == null) {
            hashTable[bucketIndex] = child;

            if (node.leftChild == child) {
                node.leftChild = null;
            } else {
                node.rightChild = null;
            }

            return;
        }

        if (node == parent.leftChild) {
            parent.leftChild = child;
        } else {
            parent.rightChild = child;
        }

        fixCollisionTreeAfterDeletion(node, hashTable, bucketIndex);
    }

    private void unlinkCollisionTreeNodeWithBothChildren(
            AbstractCollisionTreeNode<K, V> node,
            AbstractCollisionTreeNode<K, V>[] hashTable,
            int bucketIndex) {
        AbstractCollisionTreeNode<K, V> successor =
                getMinimumNode(node.rightChild);

        node.keyPair = successor.keyPair;

        AbstractCollisionTreeNode<K, V> parent = successor.parent;
        AbstractCollisionTreeNode<K, V> child = successor.rightChild;

        if (parent.leftChild == successor) {
            parent.leftChild = child;
        } else {
            parent.rightChild = child;
        }

        if (child != null) {
            child.parent = parent;
        }

        fixCollisionTreeAfterDeletion(successor, hashTable, bucketIndex);
    }

    private void linkCollisionTreeNodeToPrimaryTable(
            AbstractCollisionTreeNode<K, V> node,
            AbstractCollisionTreeNode<K, V>[] hashTable,
            int bucketIndex) {
        if (hashTable[bucketIndex] == null) {
            hashTable[bucketIndex] = node;
            return;
        }

        AbstractCollisionTreeNode<K, V> currentNode = hashTable[bucketIndex];
        AbstractCollisionTreeNode<K, V> parentOfCurrentNode =
                currentNode.parent;

        while (currentNode != null) {
            parentOfCurrentNode = currentNode;

            int cmp =
                    node.keyPair.key
                            .compareTo(currentNode.keyPair.key);

            if (cmp < 0) {
                currentNode = currentNode.leftChild;
            } else if (cmp > 0) {
                currentNode = currentNode.rightChild;
            } else {
                throw new IllegalStateException("This should not be thrown.");
            }
        }

        node.parent = parentOfCurrentNode;

        if (node.keyPair.key
                .compareTo(parentOfCurrentNode.keyPair.key) < 0) {
            parentOfCurrentNode.leftChild = node;
        } else {
            parentOfCurrentNode.rightChild = node;
        }

        fixCollisionTreeAfterInsertion(parentOfCurrentNode,
                hashTable,
                bucketIndex);
    }

    private void linkCollisionTreeNodeToSecondaryTable(
            AbstractCollisionTreeNode<K, V> node,
            AbstractCollisionTreeNode<K, V>[] hashTable,
            int bucketIndex) {
        if (hashTable[bucketIndex] == null) {
            hashTable[bucketIndex] = node;
            // Remove null
            node.leftChild = null;
            node.rightChild = null;
            return;
        }

        AbstractCollisionTreeNode<K, V> currentNode = hashTable[bucketIndex];
        AbstractCollisionTreeNode<K, V> parentOfCurrentNode = null;

        while (currentNode != null) {
            parentOfCurrentNode = currentNode;

            int cmp =
                    node.keyPair.value
                            .compareTo(currentNode.keyPair.value);

            if (cmp < 0) {
                currentNode = currentNode.leftChild;
            } else if (cmp > 0) {
                currentNode = currentNode.rightChild;
            } else {
                throw new IllegalStateException("This should not be thrown.");
            }
        }

        node.parent = parentOfCurrentNode;

        if (node.keyPair.value
                .compareTo(parentOfCurrentNode.keyPair.value) < 0) {
            parentOfCurrentNode.leftChild = node;
        } else {
            parentOfCurrentNode.rightChild = node;
        }

        fixCollisionTreeAfterInsertion(node,
                hashTable,
                bucketIndex);
    }

    private void relink(
            PrimaryCollisionTreeNode<K, V>[] newPrimaryHashTable,
            SecondaryCollisionTreeNode<K, V>[] newSecondaryHashTable) {
        PrimaryCollisionTreeNode<K, V> finger = iterationListHead;

        // We expect 'newPrimaryHashTable.length' to be a power of two!!!
        int newModuloMask = newPrimaryHashTable.length - 1;

        while (finger != null) {
            int primaryKeyHash = finger.keyPair.keyHash;
            int secondaryKeyHash = finger.keyPair.valueHash;
            int primaryCollisionTreeBucketIndex = primaryKeyHash & moduloMask;
            int secondaryCollisionTreeBucketIndex = secondaryKeyHash
                    & moduloMask;

            // Unlink the pair of collision tree nodes from their collision
            // trees in current hash tables:
            AbstractCollisionTreeNode<K, V> oppositeNode =
                    getSecondaryTreeNodeViaPrimaryTreeNode(finger);

            unlinkCollisionTreeNode(finger,
                    primaryHashTable,
                    primaryCollisionTreeBucketIndex);

            unlinkCollisionTreeNode(oppositeNode,
                    secondaryHashTable,
                    secondaryCollisionTreeBucketIndex);

            int newPrimaryCollisionTreeBucketIndex = primaryKeyHash
                    & newModuloMask;

            int newSecondaryCollisionTreeBucketIndex = secondaryKeyHash
                    & newModuloMask;

            // Link the pair of collision tree nodes to the argument tables:
            linkCollisionTreeNodeToPrimaryTable(
                    finger,
                    newPrimaryHashTable,
                    newPrimaryCollisionTreeBucketIndex);

            linkCollisionTreeNodeToSecondaryTable(
                    oppositeNode,
                    newSecondaryHashTable,
                    newSecondaryCollisionTreeBucketIndex);

            finger = finger.down;
        }
    }

    private void expandHashTablesIfNeeded() {
        if (size <= maximumLoadFactor * primaryHashTable.length) {
            return;
        }

        int newCapacity = primaryHashTable.length << 1;

        PrimaryCollisionTreeNode<K, V>[] newPrimaryHashTable =
                new PrimaryCollisionTreeNode[newCapacity];

        SecondaryCollisionTreeNode<K, V>[] newSecondaryHashTable =
                new SecondaryCollisionTreeNode[newCapacity];

        relink(newPrimaryHashTable, newSecondaryHashTable);
        this.moduloMask = newCapacity - 1;
        this.primaryHashTable = newPrimaryHashTable;
        this.secondaryHashTable = newSecondaryHashTable;
    }

    private AbstractCollisionTreeNode<K, V> getSecondaryTreeNodeViaPrimaryTreeNode(
            PrimaryCollisionTreeNode<K, V> primaryCollisionTreeNode) {
        int secondaryNodeHash = primaryCollisionTreeNode.keyPair
                .valueHash;
        int secondaryCollisionTreeBucketIndex = secondaryNodeHash & moduloMask;

        AbstractCollisionTreeNode<K, V> secondaryCollisionTreeNode =
                secondaryHashTable[secondaryCollisionTreeBucketIndex];

        V targetSecondaryKey = primaryCollisionTreeNode.keyPair.value;

        while (secondaryCollisionTreeNode != null) {
            if (secondaryCollisionTreeNode.keyPair ==
                    primaryCollisionTreeNode.keyPair) {
                return secondaryCollisionTreeNode;
            }

            int cmp = targetSecondaryKey
                    .compareTo(secondaryCollisionTreeNode.keyPair.value);

            if (cmp < 0) {
                secondaryCollisionTreeNode =
                        secondaryCollisionTreeNode.leftChild;
            } else {
                secondaryCollisionTreeNode =
                        secondaryCollisionTreeNode.rightChild;
            }
        }

        throw new IllegalStateException(
                "Failed to find a secondary node given an existing primary " +
                        "node.");
    }

    private AbstractCollisionTreeNode<K, V> getPrimaryTreeNodeViaSecondaryTreeNode(
            SecondaryCollisionTreeNode<K, V> secondaryCollisionTreeNode) {
        int primaryNodeHash = secondaryCollisionTreeNode.keyPair.keyHash;
        int primaryCollisionTreeBucketIndex = primaryNodeHash & moduloMask;

        AbstractCollisionTreeNode<K, V> primaryCollisionTreeNode =
                primaryHashTable[primaryCollisionTreeBucketIndex];

        K targetPrimaryKey = secondaryCollisionTreeNode.keyPair.key;

        while (primaryCollisionTreeNode != null) {
            if (primaryCollisionTreeNode.keyPair ==
                    secondaryCollisionTreeNode.keyPair) {
                return primaryCollisionTreeNode;
            }

            int cmp = targetPrimaryKey
                    .compareTo(primaryCollisionTreeNode.keyPair.key);

            if (cmp < 0) {
                primaryCollisionTreeNode =
                        primaryCollisionTreeNode.leftChild;
            } else {
                primaryCollisionTreeNode =
                        primaryCollisionTreeNode.rightChild;
            }
        }

        throw new IllegalStateException(
                "Failed to find a primary node given an existing secondary node.");
    }

    private void checkModificationCount(int expectedModificationCount) {
        if (modificationCount != expectedModificationCount) {
            throw new ConcurrentModificationException(
                    "This BidirectionalHashMap was modified during iteration!");
        }
    }
}
