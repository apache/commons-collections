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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.EnumSet;

import org.apache.commons.collections4.map.ConcurrentReferenceHashMap.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ConcurrentReferenceHashMap}.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public abstract class AbstractConcurrentReferenceHashMapTest<K, V> extends AbstractMapTest<ConcurrentReferenceHashMap<K, V>, K, V> {

    protected static final EnumSet<Option> IDENTITY_COMPARISONS = EnumSet.of(Option.IDENTITY_COMPARISONS);

    @BeforeEach
    void beforeEachFill() {
        resetFull();
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isAllowNullValueGet() {
        return false;
    }

    @Override
    public boolean isAllowNullValuePut() {
        return false;
    }

    @Override
    public boolean isTestSerialization() {
        return false;
    }

    @Test
    void testComputeNullKey() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        assertThrows(NullPointerException.class, () -> map.compute(null, (k, v) -> getSampleValues()[0]));
    }

    @Test
    void testComputeNullKeyIfAbsent() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        assertThrows(NullPointerException.class, () -> map.computeIfAbsent(null, k -> getSampleValues()[0]));
    }

    @Test
    void testComputeNullKeyIfPresent() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        assertThrows(NullPointerException.class, () -> map.computeIfPresent(null, (k, v) -> getSampleValues()[0]));
    }

    @Test
    void testContainsNullKey() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        assertThrows(NullPointerException.class, () -> map.containsKey(null));
    }

    @Test
    void testContainsNullValue() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        assertThrows(NullPointerException.class, () -> map.containsValue(null));
    }

    @Test
    void testGetNullKey() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        assertThrows(NullPointerException.class, () -> map.get(null));
    }

    @Test
    void testGetNullKeyOrDefault() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        assertThrows(NullPointerException.class, () -> map.getOrDefault(null, getSampleValues()[0]));
    }

    @Test
    void testMergeNullKey() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        final V[] sampleValues = getSampleValues();
        assertThrows(NullPointerException.class, () -> map.merge(null, sampleValues[0], (k, v) -> sampleValues[0]));
    }

    @Test
    void testMergeNullValue() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        assertThrows(NullPointerException.class, () -> map.merge(getSampleKeys()[0], null, (k, v) -> null));
        map.merge(getSampleKeys()[0], getSampleValues()[0], (k, v) -> null);
    }

    @Test
    void testPutNullKey() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        final K key = null;
        assertThrows(NullPointerException.class, () -> map.put(key, getSampleValues()[0]));
    }

    @Test
    void testPutNullValue() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        assertThrows(NullPointerException.class, () -> map.put(getSampleKeys()[0], null));
    }

    @Test
    void testRemoveNullKey() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        final K key = null;
        final V[] values = getSampleValues();
        assertThrows(NullPointerException.class, () -> map.remove(key, values[0]));
        assertThrows(NullPointerException.class, () -> map.remove(key));
        assertThrows(NullPointerException.class, () -> map.replace(key, values[0]));
        assertThrows(NullPointerException.class, () -> map.replace(key, values[0], values[1]));
    }

    @Test
    void testReplaceNullKey() {
        final ConcurrentReferenceHashMap<K, V> map = getMap();
        final K key = null;
        final V[] values = getSampleValues();
        assertThrows(NullPointerException.class, () -> map.remove(key, values[0]));
        assertThrows(NullPointerException.class, () -> map.remove(key));
        assertThrows(NullPointerException.class, () -> map.replace(key, values[0]));
        assertThrows(NullPointerException.class, () -> map.replace(key, values[0], values[1]));
    }
}
