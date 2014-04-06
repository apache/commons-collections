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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;

/**
 * Implements a {@link MultiValuedMap}, using a {@link HashMap} to provide data
 * storage. This is the standard implementation of a MultiValuedMap
 * <p>
 * A <code>MultiValuedMap</code> is a Map with slightly different semantics.
 * Putting a value into the map will add the value to a Collection at that key.
 * Getting a value will return a Collection, holding all the values put to that
 * key
 * <p>
 * In addition, this implementation allows the type of collection used for the
 * values to be controlled. By default, an <code>ArrayList</code> is used,
 * however a <code>Class<? extends Collection></code> to instantiate the value
 * collection may be specified.
 * <p>
 * <strong>Note that MultiValuedHashMap is not synchronized and is not
 * thread-safe.</strong> If you wish to use this map from multiple threads
 * concurrently, you must use appropriate synchronization. This class may throw
 * exceptions when accessed by concurrent threads without synchronization.
 *
 * @since 4.1
 * @version $Id$
 */
public class MultiValuedHashMap<K, V> extends AbstractMultiValuedMap<K, V> implements MultiValuedMap<K, V> {

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
     * Creates a MultiValuedHashMap which maps keys to collections of type
     * <code>collectionClass</code>.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param <C> the collection class type
     * @param collectionClass the type of the collection class
     * @return a new MultiValuedMap
     */
    public static <K, V, C extends Collection<V>> MultiValuedMap<K, V> multiValuedMap(
            final Class<C> collectionClass) {
        return new MultiValuedHashMap<K, V>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, collectionClass);
    }

    /**
     * Creates a MultiValueMap based on a <code>HashMap</code> with the default
     * initial capacity (16) and the default load factor (0.75), which stores
     * the multiple values in an <code>ArrayList</code>.
     */
    @SuppressWarnings("unchecked")
    public MultiValuedHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, ArrayList.class);
    }

    /**
     * Creates a MultiValueMap based on a <code>HashMap</code> with the initial
     * capacity and the default load factor (0.75), which stores the multiple
     * values in an <code>ArrayList</code>.
     *
     * @param initialCapacity the initial capacity of the underlying hash map
     */
    @SuppressWarnings("unchecked")
    public MultiValuedHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, ArrayList.class);
    }

    /**
     * Creates a MultiValueMap based on a <code>HashMap</code> with the initial
     * capacity and the load factor, which stores the multiple values in an
     * <code>ArrayList</code>.
     *
     * @param initialCapacity the initial capacity of the underlying hash map
     * @param loadFactor the load factor of the underlying hash map
     */
    @SuppressWarnings("unchecked")
    public MultiValuedHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, ArrayList.class);
    }

    /**
     * Creates a MultiValueMap based on a <code>HashMap</code> with the initial
     * capacity and the load factor, which stores the multiple values in an
     * <code>ArrayList</code> with the initial collection capacity.
     *
     * @param initialCapacity the initial capacity of the underlying hash map
     * @param loadFactor the load factor of the underlying hash map
     * @param initialCollectionCapacity the initial capacity of the Collection
     *        of values
     */
    @SuppressWarnings("unchecked")
    public MultiValuedHashMap(int initialCapacity, float loadFactor, int initialCollectionCapacity) {
        this(initialCapacity, loadFactor, initialCollectionCapacity, ArrayList.class);
    }

    /**
     * Creates a MultiValuedHashMap copying all the mappings of the given map.
     *
     * @param map a <code>MultiValuedMap</code> to copy into this map
     */
    @SuppressWarnings("unchecked")
    public MultiValuedHashMap(final MultiValuedMap<? extends K, ? extends V> map) {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, ArrayList.class);
        super.putAll(map);
    }

    /**
     * Creates a MultiValuedHashMap copying all the mappings of the given map.
     *
     * @param map a <code>Map</code> to copy into this map
     */
    @SuppressWarnings("unchecked")
    public MultiValuedHashMap(final Map<? extends K, ? extends V> map) {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, ArrayList.class);
        super.putAll(map);
    }

    /**
     * Creates a MultiValuedHashMap which creates the value collections using
     * the supplied <code>collectionClazz</code>.
     *
     * @param initialCapacity the initial capacity of the underlying
     *        <code>HashMap</code>
     * @param loadFactor the load factor of the underlying <code>HashMap</code>
     * @param <C> the collection type
     * @param collectionClazz the class of the <code>Collection</code> to use to
     *        create the value collections
     */
    protected <C extends Collection<V>> MultiValuedHashMap(int initialCapacity, float loadFactor,
            final Class<C> collectionClazz) {
        super(new HashMap<K, Collection<V>>(initialCapacity, loadFactor), collectionClazz);
    }

    /**
     * Creates a MultiValuedHashMap which creates the value collections using
     * the supplied <code>collectionClazz</code> and the initial collection
     * capacity .
     *
     * @param initialCapacity the initial capacity of the underlying
     *        <code>HashMap</code>
     * @param loadFactor the load factor of the underlying <code>HashMap</code>
     * @param initialCollectionCapacity the initial capacity of the
     *        <code>Collection</code>
     * @param <C> the collection type
     * @param collectionClazz the class of the <code>Collection</code> to use to
     *        create the value collections
     */
    protected <C extends Collection<V>> MultiValuedHashMap(int initialCapacity, float loadFactor,
            int initialCollectionCapacity, final Class<C> collectionClazz) {
        super(new HashMap<K, Collection<V>>(initialCapacity, loadFactor), initialCollectionCapacity, collectionClazz);
    }

}
