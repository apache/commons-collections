/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;
import org.junit.Test;

/**
 * Tests for the Counting Bloom filter implementation.
 *
 */
public class CountingBloomFilterTest extends AbstractBloomFilterTest {

    /**
     * Tests that the andCardinality calculation executes correctly when using a
     * CountingBloomFilter argument.
     */
    @Test
    public void andCardinalityTest_CountingBloomFilter() {
        final Hasher hasher = new StaticHasher(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).iterator(), shape);

        final CountingBloomFilter bf = createFilter(hasher, shape);

        Hasher hasher2 = new StaticHasher(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).iterator(), shape);
        CountingBloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals(10, bf.andCardinality(bf2));
        assertEquals(10, bf2.andCardinality(bf));

        hasher2 = new StaticHasher(Arrays.asList(1, 2, 3, 4, 5).iterator(), shape);
        bf2 = createFilter(hasher2, shape);

        assertEquals(5, bf.andCardinality(bf2));
        assertEquals(5, bf2.andCardinality(bf));

        hasher2 = new StaticHasher(Arrays.asList(11, 12, 13, 14, 15).iterator(), shape);
        bf2 = createFilter(hasher2, shape);
        assertEquals(0, bf.andCardinality(bf2));
        assertEquals(0, bf2.andCardinality(bf));
    }

    /**
     * Tests that counts are correct when a hasher is used.
     */
    @Test
    public void ConstructorTest_HasherValues_CountsTest() {
        final List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final CountingBloomFilter bf = createFilter(hasher, shape);
        final long[] lb = bf.getBits();
        assertEquals(0x1FFFF, lb[0]);
        assertEquals(1, lb.length);

        assertEquals(17, bf.getCounts().count());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).max(Integer::compare).get());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).min(Integer::compare).get());
    }

    /**
     * Tests that counts are correct when a map of counts is used.
     */
    @Test
    public void ConstructorTest_Map_CountsTest() {
        final Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 17; i++) {
            map.put(i, 1);
        }

        CountingBloomFilter bf = new CountingBloomFilter(map, shape);
        assertEquals(17, bf.getCounts().count());

        map.put(shape.getNumberOfBits(), 1);
        try {
            bf = new CountingBloomFilter(map, shape);
            fail("Should have thrown IllegalArgumentExceptionW");
        } catch (final IllegalArgumentException exprected) {
            // expected
        }

        map.clear();
        map.put(-1, 1);
        try {
            bf = new CountingBloomFilter(map, shape);
            fail("Should have thrown IllegalArgumentExceptionW");
        } catch (final IllegalArgumentException exprected) {
            // expected
        }

        map.clear();
        map.put(1, -1);
        try {
            bf = new CountingBloomFilter(map, shape);
            fail("Should have thrown IllegalArgumentExceptionW");
        } catch (final IllegalArgumentException exprected) {
            // expected
        }
    }

    @Override
    protected CountingBloomFilter createEmptyFilter(final Shape shape) {
        return new CountingBloomFilter(shape);
    }

    @Override
    protected CountingBloomFilter createFilter(final Hasher hasher, final Shape shape) {
        return new CountingBloomFilter(hasher, shape);
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is passed
     */
    @Test
    public void mergeTest_Counts() {
        final int[] expected = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0
        };
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final CountingBloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        final BloomFilter bf2 = createFilter(hasher2, shape);

        bf.merge(bf2);

        assertEquals(27, bf.getCounts().count());
        assertEquals(Integer.valueOf(2), bf.getCounts().map(Map.Entry::getValue).max(Integer::compare).get());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).min(Integer::compare).get());

        final Map<Integer, Integer> m = new HashMap<>();
        bf.getCounts().forEach(e -> m.put(e.getKey(), e.getValue()));
        for (int i = 0; i < 29; i++) {
            if (m.get(i) == null) {
                assertEquals("Wrong value for " + i, expected[i], 0);
            } else {
                assertEquals("Wrong value for " + i, expected[i], m.get(i).intValue());
            }
        }
    }

    /**
     * Test that merge correctly updates the counts when a BitSetBloomFilter is passed
     */
    @Test
    public void mergeTest_Counts_BitSetFilter() {
        final int[] expected = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0
        };
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final CountingBloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        final BloomFilter bf2 = new BitSetBloomFilter(hasher2, shape);

        bf.merge(bf2);

        assertEquals(27, bf.getCounts().count());
        assertEquals(Integer.valueOf(2), bf.getCounts().map(Map.Entry::getValue).max(Integer::compare).get());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).min(Integer::compare).get());

        final Map<Integer, Integer> m = new HashMap<>();
        bf.getCounts().forEach(e -> m.put(e.getKey(), e.getValue()));
        for (int i = 0; i < 29; i++) {
            if (m.get(i) == null) {
                assertEquals("Wrong value for " + i, expected[i], 0);
            } else {
                assertEquals("Wrong value for " + i, expected[i], m.get(i).intValue());
            }
        }
    }

    /**
     * Test that merge correctly updates the counts when a CountingBloomFilter is passed and an integer overflow occurs.
     */
    @Test
    public void mergeTest_Overflow() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        CountingBloomFilter bf = createFilter(hasher, shape);

        final Map<Integer, Integer> map = new HashMap<>();
        bf.getCounts().forEach(e -> map.put(e.getKey(), e.getValue()));
        map.put(1, Integer.MAX_VALUE);

        CountingBloomFilter bf2 = new CountingBloomFilter(map, shape);

        // should not fail
        bf.merge(bf2);

        // try max int on other side of merge.
        bf2 = createFilter(hasher, shape);
        bf = new CountingBloomFilter(map, shape);

        try {
            bf.merge(bf2);
            fail("Should have thrown IllegalStateException");
        } catch (final IllegalStateException expected) {
            // do nothing
        }
    }

    /**
     * Test that merge correctly updates the counts when a Hasher is passed
     */
    @Test
    public void mergeTest_Shape_Hasher_Count() {
        final int[] expected = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0
        };

        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final CountingBloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);

        bf.merge(hasher2);

        assertEquals(27, bf.getCounts().count());
        assertEquals(Integer.valueOf(2), bf.getCounts().map(Map.Entry::getValue).max(Integer::compare).get());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).min(Integer::compare).get());

        final Map<Integer, Integer> m = new HashMap<>();
        bf.getCounts().forEach(e -> m.put(e.getKey(), e.getValue()));
        for (int i = 0; i < 29; i++) {
            if (m.get(i) == null) {
                assertEquals("Wrong value for " + i, expected[i], 0);
            } else {
                assertEquals("Wrong value for " + i, expected[i], m.get(i).intValue());
            }
        }
    }

    /**
     * Tests that when removing a counting Bloom filter the counts are correctly updated.
     */
    @Test
    public void removeTest_Counting() {
        final int[] values = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1
        };
        final Map<Integer,Integer> map = new HashMap<>();
        for (int i=1;i<values.length;i++)
        {
            map.put(i, values[i]);
        }

        final CountingBloomFilter bf = new CountingBloomFilter(map, shape);

        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter bf2 = new CountingBloomFilter(hasher, shape);

        bf.remove(bf2);
        assertEquals(17, bf.cardinality());
        final Map<Integer, Integer> map2 = new HashMap<>();
        bf.getCounts().forEach(e -> map2.put(e.getKey(), e.getValue()));

        for (int i = 11; i < values.length; i++) {
            assertNotNull(map2.get(i));
            assertEquals(1, map2.get(i).intValue());
        }
    }

    /**
     * Tests that removing a hasher update the counts properly.
     */
    @Test
    public void removeTest_Hasher() {
        final int[] values = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1
        };
        final Map<Integer,Integer> map = new HashMap<>();
        for (int i=1;i<values.length;i++)
        {
            map.put(i, values[i]);
        }

        final CountingBloomFilter bf = new CountingBloomFilter(map, shape);

        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        bf.remove(hasher);
        assertEquals(17, bf.cardinality());
        final Map<Integer, Integer> map2 = new HashMap<>();
        bf.getCounts().forEach(e -> map2.put(e.getKey(), e.getValue()));

        for (int i = 11; i < values.length; i++) {
            assertNotNull(map2.get(i));
            assertEquals(1, map2.get(i).intValue());
        }
    }

    /**
     * Tests that when removing a standard Bloom filter the counts are correctly updated.
     */
    @Test
    public void removeTest_Standard() {
        final int[] values = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1
        };
        final Map<Integer,Integer> map = new HashMap<>();
        for (int i=1;i<values.length;i++)
        {
            map.put(i, values[i]);
        }

        final CountingBloomFilter bf = new CountingBloomFilter(map, shape);

        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BitSetBloomFilter bf2 = new BitSetBloomFilter(hasher, shape);

        bf.remove(bf2);
        assertEquals(17, bf.cardinality());
        final Map<Integer, Integer> map2 = new HashMap<>();
        bf.getCounts().forEach(e -> map2.put(e.getKey(), e.getValue()));

        for (int i = 11; i < values.length; i++) {
            assertNotNull(map2.get(i));
            assertEquals(1, map2.get(i).intValue());
        }
    }

    /**
     * Tests that removel errors when the count fall below 0.
     */
    @Test
    public void removeTest_Underflow() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        CountingBloomFilter bf = createFilter(hasher, shape);

        final Map<Integer, Integer> map = new HashMap<>();
        bf.getCounts().forEach(e -> map.put(e.getKey(), e.getValue()));
        map.remove(1);

        CountingBloomFilter bf2 = new CountingBloomFilter(map, shape);

        // should not fail
        bf.remove(bf2);

        // try max int on other side of remove.
        bf2 = createFilter(hasher, shape);
        bf = new CountingBloomFilter(map, shape);

        try {
            bf.remove(bf2);
            fail("Should have thrown IllegalStateException");
        } catch (final IllegalStateException expected) {
            // do nothing
        }
    }
}
