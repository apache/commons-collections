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

import java.util.List;

/**
 * Defines a map that holds a list of values against each key.
 * <p>
 * A <code>ListValuedMap</code> is a Map with slightly different semantics:
 * <ul>
 *   <li>Putting a value into the map will add the value to a Collection at that key.</li>
 *   <li>Getting a value will return a Collection, holding all the values put to that key.</li>
 * </ul>
 *
 * @since 4.1
 * @version $Id$
 */
public interface ListValuedMap<K, V> extends MultiValuedMap<K, V> {

    /**
     * Gets the list of values associated with the specified key.
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
    List<V> get(Object key);

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
    List<V> remove(Object key);

}
