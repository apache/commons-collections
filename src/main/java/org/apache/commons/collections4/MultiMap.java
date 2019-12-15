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

/**
 * Defines a map that holds a collection of values against each key.
 * <p>
 * A {@code MultiMap} is a Map with slightly different semantics.
 * Putting a value into the map will add the value to a Collection at that key.
 * Getting a value will return a Collection, holding all the values put to that key.
 * </p>
 * <p>
 * For example:
 * </p>
 * <pre>
 * MultiMap mhm = new MultiValueMap();
 * mhm.put(key, "A");
 * mhm.put(key, "B");
 * mhm.put(key, "C");
 * Collection coll = (Collection) mhm.get(key);</pre>
 * <p>
 * {@code coll} will be a collection containing "A", "B", "C".
 * </p>
 * <p>
 * NOTE: Additional methods were added to this interface in Commons Collections 3.1.
 * These were added solely for documentation purposes and do not change the interface
 * as they were defined in the superinterface {@code Map} anyway.
 * </p>
 *
 * @param <K> the type of the keys in this map
 * @param <V> the type of the values in this map
 *
 * @since 2.0
 * @deprecated since 4.1, use {@link MultiValuedMap} instead
 */
@Deprecated
public interface MultiMap<K, V> extends IterableMap<K, Object> {

    /**
     * Removes a specific value from map.
     * <p>
     * The item is removed from the collection mapped to the specified key.
     * Other values attached to that key are unaffected.
     * <p>
     * If the last value for a key is removed, implementations typically
     * return {@code null} from a subsequent {@code get(Object)}, however
     * they may choose to return an empty collection.
     *
     * @param key  the key to remove from
     * @param item  the item to remove
     * @return {@code true} if the mapping was removed, {@code false} otherwise
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException if the key or value is of an invalid type
     * @throws NullPointerException if the key or value is null and null is invalid
     * @since 4.0 (signature in previous releases: V remove(K, V))
     */
    boolean removeMapping(K key, V item);

    //-----------------------------------------------------------------------
    /**
     * Gets the number of keys in this map.
     * <p>
     * Implementations typically return only the count of keys in the map
     * This cannot be mandated due to backwards compatibility of this interface.
     *
     * @return the number of key-collection mappings in this map
     */
    @Override
    int size();

    /**
     * Gets the collection of values associated with the specified key.
     * <p>
     * The returned value will implement {@code Collection}. Implementations
     * are free to declare that they return {@code Collection} subclasses
     * such as {@code List} or {@code Set}.
     * <p>
     * Implementations typically return {@code null} if no values have
     * been mapped to the key, however the implementation may choose to
     * return an empty collection.
     * <p>
     * Implementations may choose to return a clone of the internal collection.
     *
     * @param key  the key to retrieve
     * @return the {@code Collection} of values, implementations should
     *  return {@code null} for no mapping, but may return an empty collection
     * @throws ClassCastException if the key is of an invalid type
     * @throws NullPointerException if the key is null and null keys are invalid
     */
    @Override
    Object get(Object key); // Cannot use get(K key) as that does not properly implement Map#get

    /**
     * Checks whether the map contains the value specified.
     * <p>
     * Implementations typically check all collections against all keys for the value.
     * This cannot be mandated due to backwards compatibility of this interface.
     *
     * @param value  the value to search for
     * @return true if the map contains the value
     * @throws ClassCastException if the value is of an invalid type
     * @throws NullPointerException if the value is null and null value are invalid
     */
    @Override
    boolean containsValue(Object value);

    /**
     * Adds the value to the collection associated with the specified key.
     * <p>
     * Unlike a normal {@code Map} the previous value is not replaced.
     * Instead the new value is added to the collection stored against the key.
     * The collection may be a {@code List}, {@code Set} or other
     * collection dependent on implementation.
     *
     * @param key  the key to store against
     * @param value  the value to add to the collection at the key
     * @return typically the value added if the map changed and null if the map did not change
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException if the key or value is of an invalid type
     * @throws NullPointerException if the key or value is null and null is invalid
     * @throws IllegalArgumentException if the key or value is invalid
     */
    @Override
    Object put(K key, Object value);

    /**
     * Removes all values associated with the specified key.
     * <p>
     * Implementations typically return {@code null} from a subsequent
     * {@code get(Object)}, however they may choose to return an empty collection.
     *
     * @param key  the key to remove values from
     * @return the {@code Collection} of values removed, implementations should
     *  return {@code null} for no mapping found, but may return an empty collection
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException if the key is of an invalid type
     * @throws NullPointerException if the key is null and null keys are invalid
     */
    @Override
    Object remove(Object key); // Cannot use remove(K key) as that does not properly implement Map#remove

    /**
     * Gets a collection containing all the values in the map.
     * <p>
     * Implementations typically return a collection containing the combination
     * of values from all keys.
     * This cannot be mandated due to backwards compatibility of this interface.
     *
     * @return a collection view of the values contained in this map
     */
    @Override
    Collection<Object> values();

}
