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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractSortedMapTest} for exercising the {@link FixedSizeSortedMap}
 * implementation.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class FixedSizeSortedMapTest<K, V> extends AbstractSortedMapTest<K, V> {

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Test
    void testPutAllAllowsUpdatesRejectsNewKeys() {
        final SortedMap<String, String> base = new TreeMap<>();
        base.put("a", "1");
        final SortedMap<String, String> fixed = FixedSizeSortedMap.fixedSizeSortedMap(base);
        // updating the value of an existing key is allowed
        fixed.putAll(Collections.singletonMap("a", "2"));
        assertEquals("2", fixed.get("a"));
        // an empty map is a no-op, not a rejection
        fixed.putAll(Collections.emptyMap());
        assertEquals(1, fixed.size());
        // a new key must be rejected and must not grow the map
        assertThrows(IllegalArgumentException.class, () -> fixed.putAll(Collections.singletonMap("b", "9")));
        assertEquals(1, fixed.size());
        assertFalse(fixed.containsKey("b"));
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
