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
package org.apache.commons.collections4.multimap;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.SetValuedMap;

/**
 * Implements a {@link MultiValuedMap}, using a {@link LinkedHashMap} to provide
 * data storage. This MultiValuedMap implementation the allows insertion order
 * to be maintained.
 * <p>
 * A <code>MultiValuedMap</code> is a Map with slightly different semantics.
 * Putting a value into the map will add the value to a Collection at that key.
 * Getting a value will return a Collection, holding all the values put to that
 * key
 * <p>
 * In addition, this implementation allows the type of collection used for the
 * values to be controlled. By default, an <code>LinkedList</code> is used,
 * however a <code>Class<? extends Collection></code> to instantiate the value
 * collection may be specified.
 * <p>
 * <strong>Note that MultiValuedLinkedHashMap is not synchronized and is not
 * thread-safe.</strong> If you wish to use this map from multiple threads
 * concurrently, you must use appropriate synchronization. This class may throw
 * exceptions when accessed by concurrent threads without synchronization.
 *
 * @since 4.1
 * @version $Id: $
 */
public class MultiValuedLinkedHashMap<K, V> extends AbstractMultiValuedMap<K, V> implements MultiValuedMap<K, V> {

    /** Serialization Version */
    private static final long serialVersionUID = -5845183518195365857L;

    /**
     * The initial capacity used when none specified in constructor.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Creates a {@link ListValuedMap} with a {@link LinkedHashMap} as its internal
     * storage
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a new <code>ListValuedMap</code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <K, V> ListValuedMap<K, V> listValuedLinkedHashMap() {
        return new ListValuedLinkedHashMap(LinkedList.class);
    }

    /**
     * Creates a {@link ListValuedMap} with a {@link LinkedHashMap} as its internal
     * storage which maps the keys to list of type <code>listClass</code>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param <C> the List class type
     * @param listClass the class of the list
     * @return a new <code>ListValuedMap</code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <K, V, C extends List<V>> ListValuedMap<K, V> listValuedLinkedHashMap(final Class<C> listClass) {
        return new ListValuedLinkedHashMap(listClass);
    }

    /**
     * Creates a {@link SetValuedMap} with a {@link LinkedHashMap} as its internal
     * storage. This <code>SetValuedMap</code> implementation uses {@link LinkedHashSet} as its
     * underlying <code>Collection</code> to preserve the item insertion order
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a new <code>SetValuedMap</code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <K, V> SetValuedMap<K, V> setValuedLinkedHashMap() {
        return new SetValuedLinkedHashMap(LinkedHashSet.class);
    }

    /**
     * Creates a {@link SetValuedMap} with a {@link LinkedHashMap} as its internal
     * storage which maps the keys to a set of type <code>setClass</code>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param <C> the Set class type
     * @param setClass the class of the set
     * @return a new <code>SetValuedMap</code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <K, V, C extends Set<V>> SetValuedMap<K, V> setValuedLinkedHashMap(final Class<C> setClass) {
        return new SetValuedLinkedHashMap(setClass);
    }

    /**
     * Creates a MultiValueMap based on a <code>LinkedHashMap</code> with the default
     * initial capacity (16) and the default load factor (0.75), which stores
     * the multiple values in an <code>LinkedList</code>.
     */
    @SuppressWarnings("unchecked")
    public MultiValuedLinkedHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, LinkedList.class);
    }

    /**
     * Creates a MultiValueMap based on a <code>LinkedHashMap</code> with the initial
     * capacity and the default load factor (0.75), which stores the multiple
     * values in an <code>LinkedList</code>.
     *
     * @param initialCapacity the initial capacity of the underlying hash map
     */
    @SuppressWarnings("unchecked")
    public MultiValuedLinkedHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, LinkedList.class);
    }

    /**
     * Creates a MultiValueMap based on a <code>LinkedHashMap</code> with the initial
     * capacity and the load factor, which stores the multiple values in an
     * <code>LinkedList</code>.
     *
     * @param initialCapacity the initial capacity of the underlying hash map
     * @param loadFactor the load factor of the underlying hash map
     */
    @SuppressWarnings("unchecked")
    public MultiValuedLinkedHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, LinkedList.class);
    }

    /**
     * Creates a MultiValueMap based on a <code>LinkedHashMap</code> with the initial
     * capacity and the load factor, which stores the multiple values in an
     * <code>LinkedList</code> with the initial collection capacity.
     *
     * @param initialCapacity the initial capacity of the underlying hash map
     * @param loadFactor the load factor of the underlying hash map
     * @param initialCollectionCapacity the initial capacity of the Collection
     *        of values
     */
    @SuppressWarnings("unchecked")
    public MultiValuedLinkedHashMap(int initialCapacity, float loadFactor, int initialCollectionCapacity) {
        this(initialCapacity, loadFactor, LinkedList.class, initialCollectionCapacity);
    }

    /**
     * Creates a MultiValuedLinkedHashMap copying all the mappings of the given map.
     *
     * @param map a <code>MultiValuedMap</code> to copy into this map
     */
    @SuppressWarnings("unchecked")
    public MultiValuedLinkedHashMap(final MultiValuedMap<? extends K, ? extends V> map) {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, LinkedList.class);
        super.putAll(map);
    }

    /**
     * Creates a MultiValuedLinkedHashMap copying all the mappings of the given map.
     *
     * @param map a <code>Map</code> to copy into this map
     */
    @SuppressWarnings("unchecked")
    public MultiValuedLinkedHashMap(final Map<? extends K, ? extends V> map) {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, LinkedList.class);
        super.putAll(map);
    }

    /**
     * Creates a MultiValuedLinkedHashMap which creates the value collections using
     * the supplied <code>collectionClazz</code>.
     *
     * @param initialCapacity the initial capacity of the underlying
     *        <code>LinkedHashMap</code>
     * @param loadFactor the load factor of the underlying <code>LinkedHashMap</code>
     * @param <C> the collection type
     * @param collectionClazz the class of the <code>Collection</code> to use to
     *        create the value collections
     */
    protected <C extends Collection<V>> MultiValuedLinkedHashMap(int initialCapacity, float loadFactor,
            final Class<C> collectionClazz) {
        super(new LinkedHashMap<K, Collection<V>>(initialCapacity, loadFactor), collectionClazz);
    }

    /**
     * Creates a MultiValuedLinkedHashMap which creates the value collections using
     * the supplied <code>collectionClazz</code> and the initial collection
     * capacity .
     *
     * @param initialCapacity the initial capacity of the underlying
     *        <code>LinkedHashMap</code>
     * @param loadFactor the load factor of the underlying <code>LinkedHashMap</code>
     * @param initialCollectionCapacity the initial capacity of the
     *        <code>Collection</code>
     * @param <C> the collection type
     * @param collectionClazz the class of the <code>Collection</code> to use to
     *        create the value collections
     */
    protected <C extends Collection<V>> MultiValuedLinkedHashMap(int initialCapacity, float loadFactor,
            final Class<C> collectionClazz, int initialCollectionCapacity) {
        super(new LinkedHashMap<K, Collection<V>>(initialCapacity, loadFactor), collectionClazz,
                                                  initialCollectionCapacity);
    }

    /** Inner class for ListValuedLinkedMap */
    private static class ListValuedLinkedHashMap<K, V> extends AbstractListValuedMap<K, V> {

        private static final long serialVersionUID = 3667581458573135234L;

        public <C extends List<V>> ListValuedLinkedHashMap(Class<C> listClazz) {
            super(new LinkedHashMap<K, List<V>>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR), listClazz);
        }

        public <C extends List<V>> ListValuedLinkedHashMap(Class<C> listClazz, int initialListCapacity) {
            super(new LinkedHashMap<K, List<V>>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR), listClazz,
                    initialListCapacity);
        }

    }

    /** Inner class for SetValuedLinkedMap */
    private static class SetValuedLinkedHashMap<K, V> extends AbstractSetValuedMap<K, V> {

        private static final long serialVersionUID = -3817515514829894543L;

        public <C extends Set<V>> SetValuedLinkedHashMap(Class<C> setClazz) {
            super(new LinkedHashMap<K, Set<V>>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR), setClazz);
        }

        public <C extends Set<V>> SetValuedLinkedHashMap(Class<C> setClazz, int initialSetCapacity) {
            super(new LinkedHashMap<K, Set<V>>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR), setClazz,
                    initialSetCapacity);
        }
    }

}
