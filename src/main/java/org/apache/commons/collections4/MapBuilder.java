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

import org.apache.commons.collections4.map.HashedMap;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;

/**
 * Defines an Helper Builder that generates a {@code Map}
 * A Builder class to help decide which type of map to use based on simple requirements.
 * Currently It takes care of only basic types of Maps and can be extended to different types of Maps available in the ecosystem.
 *
 * <pre>{@code
 * Map builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.INSERTION_ORDER).build();
 * builderMap.put("A", 1);
 * builderMap.put("X", 24);
 * builderMap.put("B", 2);
 * builderMap.put("Y", 26);
 * }</pre>
 *
 */
public class MapBuilder<K, V> {

    private Comparator<? super K> comparator;
    private KeyOrder iterationOrder;
    private boolean synchronizedMap;
    private boolean immutable;
    private Map<K, V> data;

    public MapBuilder() {
        comparator = null;
        iterationOrder = KeyOrder.UNORDERED;
        synchronizedMap = false;
        immutable = false;
        data = null;
    }

    /*
     Sets the comparator to be used to decide the Iteration order in case of iterationOrder = COMPARATOR_ORDER;
     */
    public MapBuilder setComparator(Comparator comparator) {
        this.comparator = comparator;
        return this;
    }

    /*
    Sets the Iteration order to be used from [UNORDERED, NATURAL_ORDER, INSERTION_ORDER, COMPARATOR_ORDER]
     */
    public MapBuilder setIterationOrder(KeyOrder iterationOrder) {
        this.iterationOrder = iterationOrder;
        return this;
    }

    /*
    Since most of the maps are not inherently thread safe , this option provides the option if the map has to be synchronised or not
     */
    public MapBuilder setSynchronizedMap(boolean synchronizedMap) {
        this.synchronizedMap = synchronizedMap;
        return this;
    }

    /*
    Option to create a immutable map from the provided data
     */
    public MapBuilder setImmutable(boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    /*
    Populates the Map with some preexisting data. All the selected conditions will be automatically applied  to the existing data
     */
    public MapBuilder setData(Map data) {
        this.data = data;
        return this;
    }

    /*
    Builder Method which takes care of all the conditions and returns the required Map.
     */
    public Map build() {
        Map<K, V> map;
        switch (iterationOrder) {
        case NATURAL_ORDER :
        case COMPARATOR_ORDER:
            map = new TreeMap(comparator);
            break;
        case INSERTION_ORDER :
            map = new LinkedHashMap();
            break;
        default:
            map = new HashedMap();
            break;
        }

        if (MapUtils.isNotEmpty(data)) {
            map.putAll(data);
        }

        if (synchronizedMap) {
            map = Collections.synchronizedMap(map);
        }

        if (immutable) {
            map = Collections.unmodifiableMap(map);
        }

        return map;
    }

    enum KeyOrder {
        UNORDERED, NATURAL_ORDER, INSERTION_ORDER, COMPARATOR_ORDER;
    }
}
