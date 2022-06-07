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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.Unmodifiable;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests.
 */
public class UnmodifiableBidiMapTest<K, V> extends AbstractBidiMapTest<K, V> {

    public static junit.framework.Test suite() {
        return BulkTest.makeSuite(UnmodifiableBidiMapTest.class);
    }

    public UnmodifiableBidiMapTest() {
        super(UnmodifiableBidiMapTest.class.getSimpleName());
    }

    @Override
    public BidiMap<K, V> makeObject() {
        return UnmodifiableBidiMap.unmodifiableBidiMap(new DualHashBidiMap<K, V>());
    }

    @Override
    public BidiMap<K, V> makeFullMap() {
        final BidiMap<K, V> bidi = new DualHashBidiMap<>();
        addSampleMappings(bidi);
        return UnmodifiableBidiMap.unmodifiableBidiMap(bidi);
    }

    @Override
    public Map<K, V> makeConfirmedMap() {
        return new HashMap<>();
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

    @Test
    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    @Test
    public void testDecorateFactory() {
        final BidiMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableBidiMap.unmodifiableBidiMap(map));

        assertThrows(NullPointerException.class, () -> UnmodifiableBidiMap.unmodifiableBidiMap(null));
    }

}
