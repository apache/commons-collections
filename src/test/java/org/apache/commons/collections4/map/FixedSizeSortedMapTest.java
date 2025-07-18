/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.map;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Extension of {@link AbstractSortedMapTest} for exercising the {@link FixedSizeSortedMap}
 * implementation.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class FixedSizeSortedMapTest<K, V> extends AbstractSortedMapTest<K, V> {

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public boolean isPutAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public boolean isSubMapViewsSerializable() {
        // TreeMap sub map views have a bug in deserialization.
        return false;
    }

    @Override
    public SortedMap<K, V> makeFullMap() {
        final SortedMap<K, V> map = new TreeMap<>();
        addSampleMappings(map);
        return FixedSizeSortedMap.fixedSizeSortedMap(map);
    }

    @Override
    public SortedMap<K, V> makeObject() {
        return FixedSizeSortedMap.fixedSizeSortedMap(new TreeMap<>());
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/FixedSizeSortedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/FixedSizeSortedMap.fullCollection.version4.obj");
//    }

}
