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
package org.apache.commons.collections4.bidimap;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.Unmodifiable;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests.
 */
public class UnmodifiableSortedBidiMapTest<K extends Comparable<K>, V extends Comparable<V>> extends AbstractSortedBidiMapTest<K, V> {

    public static junit.framework.Test suite() {
        return BulkTest.makeSuite(UnmodifiableSortedBidiMapTest.class);
    }

    public UnmodifiableSortedBidiMapTest() {
        super(UnmodifiableSortedBidiMapTest.class.getSimpleName());
    }

    @Override
    public SortedBidiMap<K, V> makeObject() {
        return UnmodifiableSortedBidiMap.unmodifiableSortedBidiMap(new DualTreeBidiMap<K, V>());
    }

    @Override
    public SortedBidiMap<K, V> makeFullMap() {
        final SortedBidiMap<K, V> bidi = new DualTreeBidiMap<>();
        addSampleMappings(bidi);
        return UnmodifiableSortedBidiMap.unmodifiableSortedBidiMap(bidi);
    }

    @Override
    public SortedMap<K, V> makeConfirmedMap() {
        return new TreeMap<>();
    }

    @Override
    public boolean isSubMapViewsSerializable() {
        // TreeMap sub map views have a bug in deserialization.
        return false;
    }

    @Override
    public String[] ignoredTests() {
        // Override to prevent infinite recursion of tests.
        return new String[] {"UnmodifiableSortedBidiMapTest.bulkTestInverseMap.bulkTestInverseMap"};
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isAllowNullValue() {
        return false;
    }

    @Override
    public boolean isPutAddSupported() {
        return false;
    }

    @Override
    public boolean isPutChangeSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Test
    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    @Test
    public void testDecorateFactory() {
        final SortedBidiMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableSortedBidiMap.unmodifiableSortedBidiMap(map));

        assertThrows(NullPointerException.class, () -> UnmodifiableSortedBidiMap.unmodifiableSortedBidiMap(null));
    }

}
