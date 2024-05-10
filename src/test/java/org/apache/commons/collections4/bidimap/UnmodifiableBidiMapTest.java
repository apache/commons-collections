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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests.
 */
public class UnmodifiableBidiMapTest<K, V> extends AbstractBidiMapTest<K, V> {

    public UnmodifiableBidiMapTest() {
        super(UnmodifiableBidiMapTest.class.getSimpleName());
    }

    @Override
    protected int getIterationBehaviour() {
        return AbstractCollectionTest.UNORDERED;
    }

    /**
     * Override to prevent infinite recursion of tests.
     */
    @Override
    public String[] ignoredTests() {
        return new String[] {"UnmodifiableBidiMapTest.bulkTestInverseMap.bulkTestInverseMap"};
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

    @Override
    public Map<K, V> makeConfirmedMap() {
        return new HashMap<>();
    }

    @Override
    public BidiMap<K, V> makeFullMap() {
        final BidiMap<K, V> bidi = new DualHashBidiMap<>();
        addSampleMappings(bidi);
        return UnmodifiableBidiMap.unmodifiableBidiMap(bidi);
    }

    @Override
    public BidiMap<K, V> makeObject() {
        return UnmodifiableBidiMap.unmodifiableBidiMap(new DualHashBidiMap<>());
    }

    public Set<Map.Entry<K, V>> makeCustomFullMap() {
        final BidiMap<K, V> bidi = new DualHashBidiMap<>();
        addSampleMappings(bidi);
        return UnmodifiableBidiMap.unmodifiableBidiMap(bidi).entrySet();
    }

    @Test
    public void testDecorateFactory() {
        final BidiMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableBidiMap.unmodifiableBidiMap(map));

        assertThrows(NullPointerException.class, () -> UnmodifiableBidiMap.unmodifiableBidiMap(null));
    }

    @Test
    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    @Test
    public void testToArray() {
        // arrange
        Set<Map.Entry<K, V>> x = makeCustomFullMap();

        // act
        Object[] array1 = x.toArray();
        Object[] array2 = x.toArray(new Map.Entry[x.size()]);
        Object[] array3 = x.toArray(new Map.Entry[0]);

        // assert
        // check for same length
        assertEquals(18, array1.length);
        assertEquals(18, array2.length);
        assertEquals(18, array3.length);

        // verify the type for each entry
        for (Object obj : array1) {
            assertTrue(obj instanceof Map.Entry);
        }
        for (Object obj : array2) {
            assertTrue(obj instanceof Map.Entry);
        }
        for (Object obj : array3) {
            assertTrue(obj instanceof Map.Entry);
        }
    }
}
