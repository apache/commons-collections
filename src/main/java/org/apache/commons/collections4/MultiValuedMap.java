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
package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Defines a map that holds a collection of values against each key.
 * <p>
 * A <code>MultiValuedMap</code> is a Map with slightly different semantics:
 * <ul>
 *   <li>Putting a value into the map will add the value to a Collection at that key.</li>
 *   <li>Getting a value will return a Collection, holding all the values put to that key.</li>
 * </ul>
 * <p>
 * For example:
 * <pre>
 * MultiValuedMap&lt;K, String&gt; map = new MultiValuedHashMap&lt;K, String&gt;();
 * map.put(key, &quot;A&quot;);
 * map.put(key, &quot;B&quot;);
 * map.put(key, &quot;C&quot;);
 * Collection&lt;String&gt; coll = map.get(key);
 * </pre>
 * <p>
 * <code>coll</code> will be a collection containing "A", "B", "C".
 * <p>
 *
 * @since 4.1
 * @version $Id$
 */
public interface MultiValuedMap<K, V> {
    // Query operations

    /**
     * Gets the total size of the map.
     * <p>
     * Implementations would return the total size of the map which is the count
     * of the values from all keys.
     *
     * @return the total size of the map
     */
    int size();

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    boolean isEmpty();

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key. More formally, returns <tt>true</tt> if and only if this map
     * contains a mapping for a key <tt>k</tt> such that
     * <tt>(key==null ? k==null : key.equals(k))</tt>. (There can be at most one
     * such mapping.)
     *
     * @param key key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified key
     * @throws ClassCastException if the key is of an inappropriate type for this map (optional)
     * @throws NullPointerException if the specified key is null and this map
     *         does not permit null keys (optional)
     */
    boolean containsKey(Object key);

    /**
     * Checks whether the map contains at least one mapping for the specified value.
     *
     * @param value the value to search for
     * @return true if the map contains the value
     * @throws ClassCastException if the value is of an invalid type
     * @throws NullPointerException if the value is null and null value are invalid
     */
    boolean containsValue(Object value);

    /**
     * Checks whether the map contains a mapping for the specified key and value.
     *
     * @param key the key to search for
     * @param value the value to search for
     * @return true if the map contains the value
     */
    boolean containsMapping(Object key, Object value);

    /**
     * Gets the collection of values associated with the specified key.
     * <p>
     * Implementations are free to declare that they return
     * <code>Collection</code> subclasses such as <code>List</code> or
     * <code>Set</code>.
     * <p>
     * Implementations typically return <code>null</code> if no values have been
     * mapped to the key, however the implementation may choose to return an
     * empty collection.
     * <p>
     * Implementations may choose to return a clone of the internal collection.
     *
     * @param key the key to retrieve
     * @return the <code>Collection</code> of values, implementations should
     *         return <code>null</code> for no mapping, but may return an empty collection
     * @throws ClassCastException if the key is of an invalid type
     * @throws NullPointerException if the key is null and null keys are invalid
     */
    Collection<V> get(Object key);

    // Modification operations

    /**
     * Adds the value to the collection associated with the specified key.
     * <p>
     * Unlike a normal <code>Map</code> the previous value is not replaced.
     * Instead the new value is added to the collection stored against the key.
     * The collection may be a <code>List</code>, <code>Set</code> or other
     * collection dependent on implementation.
     *
     * @param key the key to store against
     * @param value the value to add to the collection at the key
     * @return typically the value added if the map changed and null if the map
     *         did not change
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException if the key or value is of an invalid type
     * @throws NullPointerException if the key or value is null and null is invalid
     * @throws IllegalArgumentException if the key or value is invalid
     */
    V put(K key, V value);

    /**
     * Adds Iterable values to the collection associated with the specified key.
     *
     * @param key the key to store against
     * @param values the values to add to the collection at the key, null ignored
     * @return true if this map changed
     */
    boolean putAll(K key, Iterable<? extends V> values);

    /**
     * Copies all of the mappings from the specified map to this map (optional
     * operation). The effect of this call is equivalent to that of calling
     * {@link #put(Object,Object) put(k, v)} on this map once for each mapping
     * from key <tt>k</tt> to value <tt>v</tt> in the specified map. The
     * behavior of this operation is undefined if the specified map is modified
     * while the operation is in progress.
     *
     * @param m mappings to be stored in this map
     * @throws UnsupportedOperationException if the <tt>putAll</tt> operation is
     *         not supported by this map
     * @throws ClassCastException if the class of a key or value in the
     *         specified map prevents it from being stored in this map
     * @throws NullPointerException if the specified map is null, or if this map
     *         does not permit null keys or values, and the specified map
     *         contains null keys or values
     * @throws IllegalArgumentException if some property of a key or value in
     *         the specified map prevents it from being stored in this map
     */
    void putAll(Map<? extends K, ? extends V> m);

    /**
     * Copies all of the mappings from the specified MultiValuedMap to this map
     * (optional operation). The effect of this call is equivalent to that of
     * calling {@link #put(Object,Object) put(k, v)} on this map once for each
     * mapping from key <tt>k</tt> to value <tt>v</tt> in the specified map. The
     * behavior of this operation is undefined if the specified map is modified
     * while the operation is in progress.
     *
     * @param m mappings to be stored in this map
     * @throws UnsupportedOperationException if the <tt>putAll</tt> operation is
     *         not supported by this map
     * @throws ClassCastException if the class of a key or value in the
     *         specified map prevents it from being stored in this map
     * @throws NullPointerException if the specified map is null, or if this map
     *         does not permit null keys or values, and the specified map
     *         contains null keys or values
     * @throws IllegalArgumentException if some property of a key or value in
     *         the specified map prevents it from being stored in this map
     */
    void putAll(MultiValuedMap<? extends K, ? extends V> m);

    /**
     * Removes all values associated with the specified key.
     * <p>
     * Implementations typically return <code>null</code> from a subsequent
     * <code>get(Object)</code>, however they may choose to return an empty
     * collection.
     *
     * @param key the key to remove values from
     * @return the <code>Collection</code> of values removed, implementations
     *         should return <code>null</code> for no mapping found, but may
     *         return an empty collection
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException if the key is of an invalid type
     * @throws NullPointerException if the key is null and null keys are invalid
     */
    Collection<V> remove(Object key);

    /**
     * Removes a key-value mapping from the map.
     * <p>
     * The item is removed from the collection mapped to the specified key.
     * Other values attached to that key are unaffected.
     * <p>
     * If the last value for a key is removed, implementations typically return
     * <code>null</code> from a subsequent <code>get(Object)</code>, however
     * they may choose to return an empty collection.
     *
     * @param key the key to remove from
     * @param item the item to remove
     * @return {@code true} if the mapping was removed, {@code false} otherwise
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException if the key or value is of an invalid type
     * @throws NullPointerException if the key or value is null and null is
     *         invalid
     */
    boolean removeMapping(K key, V item);

    /**
     * Removes all of the mappings from this map (optional operation).
     * The map will be empty after this call returns.
     *
     * @throws UnsupportedOperationException if the map is unmodifiable
     */
    void clear();

    // Views

    /**
     * Returns a {@link Collection} view of the mappings contained in this map.
     * The collection is backed by the map, so changes to the map are reflected
     * in this, and vice-versa.
     *
     * @return a set view of the mappings contained in this map
     */
    Collection<Entry<K, V>> entries();

    /**
     * Returns a {@link Bag} view of the key mapping contained in this map.
     * <p>
     * Implementations typically return a Bag of keys with its values count as
     * the count of the Bag. This bag is backed by the map, so any changes in
     * the map is reflected here.
     *
     * @return a bag view of the key mapping contained in this map
     */
    Bag<K> keys();

    /**
     * Returns a {@link Set} view of the keys contained in this map. The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. If the map is modified while an iteration over the set is in
     * progress (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined. The set supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not support
     * the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this map
     */
    Set<K> keySet();

    /**
     * Gets a collection containing all the values in the map.
     * <p>
     * Implementations typically return a collection containing the combination
     * of values from all keys.
     *
     * @return a collection view of the values contained in this map
     */
    Collection<V> values();

    /**
     * Returns a {@link Map} view of this MultiValuedMap with a Collection as
     * its value. The Collection holds all the values mapped to that key.
     *
     * @return a Map view of the mappings in this MultiValuedMap
     */
    Map<K, Collection<V>> asMap();

    // Iterators

    /**
     * Obtains a <code>MapIterator</code> over the map.
     * <p>
     * A map iterator is an efficient way of iterating over maps. There is no
     * need to access the entries collection or use Map Entry objects.
     *
     * @return a map iterator
     */
    MapIterator<K, V> mapIterator();

}
