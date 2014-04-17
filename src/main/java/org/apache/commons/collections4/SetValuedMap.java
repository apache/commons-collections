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

import java.util.Set;

/**
 * Defines a map that holds a set of values against each key.
 * <p>
 * A <code>SetValuedMap</code> is a Map with slightly different semantics:
 * <ul>
 * <li>Putting a value into the map will add the value to a <code>Set</code> at
 * that key.</li>
 * <li>Getting a value will return a <code>Set</code>, holding all the values
 * put to that key.</li>
 * </ul>
 *
 * @since 4.1
 * @version $Id$
 */
public interface SetValuedMap<K, V> extends MultiValuedMap<K, V> {

    /**
     * Gets the set of values associated with the specified key.
     * <p>
     * Implementations typically return an empty <code>Set</code> if no values
     * have been mapped to the key.
     * <p>
     *
     * @param key the key to retrieve
     * @return the <code>Set</code> of values, implementations should return an
     *         empty Set for no mapping
     * @throws ClassCastException if the key is of an invalid type
     * @throws NullPointerException if the key is null and null keys are invalid
     */
    Set<V> get(Object key);

    /**
     * Removes all values associated with the specified key.
     * <p>
     * The returned set <i>may</i> be modifiable, but updates will not be
     * propagated to this set-valued map. In case no mapping was stored for the
     * specified key, an empty, unmodifiable set will be returned.
     *
     * @param key the key to remove values from
     * @return the <code>Set</code> of values removed, implementations should
     *         return <code>null</code> for no mapping found, but may return an
     *         empty collection
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException if the key is of an invalid type
     * @throws NullPointerException if the key is null and null keys are invalid
     */
    Set<V> remove(Object key);
}
