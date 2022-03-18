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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.Unmodifiable;
import org.junit.Test;

/**
 * Extension of {@link AbstractSortedMapTest} for exercising the
 * {@link UnmodifiableSortedMap} implementation.
 *
 * @since 3.0
 */
public class UnmodifiableSortedMapTest<K, V> extends AbstractSortedMapTest<K, V> {

    public UnmodifiableSortedMapTest(final String testName) {
        super(testName);
    }

    //-------------------------------------------------------------------

    @Override
    public SortedMap<K, V> makeObject() {
        return UnmodifiableSortedMap.unmodifiableSortedMap(new TreeMap<K, V>());
    }

    @Override
    public boolean isPutChangeSupported() {
        return false;
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
    public SortedMap<K, V> makeFullMap() {
        final SortedMap<K, V> m = new TreeMap<>();
        addSampleMappings(m);
        return UnmodifiableSortedMap.unmodifiableSortedMap(m);
    }

    @Test
    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    @Test
    public void testDecorateFactory() {
        final SortedMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableSortedMap.unmodifiableSortedMap(map));

        assertThrows(NullPointerException.class, () -> UnmodifiableSortedMap.unmodifiableSortedMap(null));
    }

    @Test
    public void testHeadMap() {
        final SortedMap<K, V> map = makeFullMap();
        final SortedMap<K, V> m = new TreeMap<>();
        // "again" is the first key of the map
        assertSame(m.isEmpty(), map.headMap((K) "again").isEmpty());
        assertSame(18, map.size());
        // "you" is the last key of the map
        assertSame(17, map.headMap((K) "you").size());
        // "we'll" is the before key of "you"
        assertSame(16, map.headMap((K) "we'll").size());
    }

    @Test
    public void testTailMap() {
        final SortedMap<K, V> map = makeFullMap();

        assertSame(18, map.size());
        // "you" is the last key of the map
        assertSame(1, map.tailMap((K) "you").size());
        // "we'll" is the before key of "you"
        assertSame(2, map.tailMap((K) "we'll").size());
        // "again" is the first key of the map
        assertSame(18, map.tailMap((K) "again").size());
    }

    @Test
    public void testSubMap() {
        final SortedMap<K, V> map = makeFullMap();

        assertSame(18, map.size());
        // get the sub map from again to you(exclusive)
        assertSame(17, map.subMap((K) "again", (K) "you").size());
        // get the sub map from again to we'll(exclusive)
        assertSame(16, map.subMap((K) "again", (K) "we'll").size());
        // "again" is the first key of the map
        assertSame(0, map.subMap((K) "again", (K) "again").size());

        assertSame(map.headMap((K) "you").size(), map.subMap((K) "again", (K) "you").size());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/UnmodifiableSortedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/UnmodifiableSortedMap.fullCollection.version4.obj");
//    }

}
