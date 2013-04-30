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
package org.apache.commons.collections4.map;

import java.util.Map;
import java.util.Collection;

import org.apache.commons.collections4.map.CompositeMap;

/**
 * This class is used in TestCompositeMap. When testing serialization,
 * the class has to be separate of TestCompositeMap, else the test
 * class also has to be serialized.
 */
class EmptyMapMutator<K,V> implements CompositeMap.MapMutator<K,V> {
    /** Serialization version */
    private static final long serialVersionUID = -2729718980002476794L;

    public void resolveCollision(final CompositeMap<K,V> composite,
    final Map<K,V> existing,
    final Map<K,V> added,
    final Collection<K> intersect) {
        // Do nothing
    }

    public V put(final CompositeMap<K, V> map, final Map<K, V>[] composited, final K key, final V value) {
        return composited[0].put(key, value);
    }

    public void putAll(final CompositeMap<K, V> map, final Map<K, V>[] composited, final Map<? extends K, ? extends V> t) {
        composited[0].putAll(t);
    }

}
