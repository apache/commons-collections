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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ArrayCountingBloomFilter}.
 */
public abstract class AbstractCountingBloomFilterTest<T extends CountingBloomFilter>
        extends AbstractBloomFilterTest<T> {
    protected int[] from1Counts = { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 };
    protected int[] from11Counts = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0 };
    protected int[] bigHashCounts = { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 0 };

    protected final BitCountProducer maximumValueProducer = new BitCountProducer() {

        @Override
        public boolean forEachCount(BitCountProducer.BitCountConsumer consumer) {
            for (int i = 1; i < 18; i++) {
                if (!consumer.test(i, Integer.MAX_VALUE)) {
                    return false;
                }
            }
            return true;
        }
    };

    /**
     * Assert the counts match the expected values. Values are for indices starting
     * at 0. Assert the cardinality equals the number of non-zero counts.
     *
     * @param bf the bloom filter
     * @param expected the expected counts
     */
    private static void assertCounts(final CountingBloomFilter bf, final int[] expected) {
        final Map<Integer, Integer> m = new HashMap<>();
        bf.forEachCount((i, c) -> {
            m.put(i, c);
            return true;
        });
        int zeros = 0;
        for (int i = 0; i < expected.length; i++) {
            if (m.get(i) == null) {
                assertEquals(expected[i], 0, "Wrong value for " + i);
                zeros++;
            } else {
                assertEquals(expected[i], m.get(i).intValue(), "Wrong value for " + i);
            }
        }
        assertEquals(expected.length - zeros, bf.cardinality());
    }

    /**
     * Tests that counts are correct when a hasher with duplicates is used in the
     * constructor.
     */
    @Test
    public final void testCountingSpecificConstructor() {
        // verify hasher duplicates are counted.
        // bit hasher has duplicates for 11, 12,13,14,15,16, and 17
        final CountingBloomFilter bf = createFilter(getTestShape(), from1);
        bf.add(BitCountProducer.from(from11.indices(getTestShape())));

        final long[] lb = bf.asBitMapArray();
        assertEquals(2, lb.length);
        assertEquals(bigHashValue, lb[0]);

        assertCounts(bf, bigHashCounts);
    }

    @Test
    public final void testCountingBloomFilterSpecificContains() {
        final BloomFilter bf = new SimpleBloomFilter(getTestShape());
        bf.merge(from1);
        final CountingBloomFilter bf2 = createFilter(getTestShape(), bigHasher);

        assertTrue(bf.contains(bf), "BF Should contain itself");
        assertTrue(bf2.contains(bf2), "BF2 Should contain itself");
        assertFalse(bf.contains(bf2), "BF should not contain BF2");
        assertTrue(bf2.contains(bf), "BF2 should contain BF");
        BitMapProducer producer = bf2;
        assertTrue(bf2.contains(producer), "BF2 should contain BF bitMapProducer");
    }

    /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
    @Test
    public final void testCountingSpecificMerge() {
        final BloomFilter bf1 = createFilter(getTestShape(), from1);

        final BloomFilter bf2 = new SimpleBloomFilter(getTestShape());
        bf2.merge(from11);

        final BloomFilter bf3 = bf1.copy();
        bf3.merge(bf2);
        assertTrue(bf3.contains(bf1), "Should contain");
        assertTrue(bf3.contains(bf2), "Should contain");

        final BloomFilter bf4 = bf2.copy();
        bf4.merge(bf1);
        assertTrue(bf4.contains(bf1), "Should contain");
        assertTrue(bf4.contains(bf2), "Should contain");
        assertTrue(bf4.contains(bf3), "Should contain");
        assertTrue(bf3.contains(bf4), "Should contain");

        // test overflow

        final CountingBloomFilter bf5 = createEmptyFilter(getTestShape());
        assertTrue(bf5.add(maximumValueProducer), "Should add to empty");
        assertTrue(bf5.isValid(), "Should be valid");

        CountingBloomFilter bf6 = bf5.copy();
        BloomFilter bf7 = new SimpleBloomFilter(getTestShape());
        bf7.merge(from1);
        bf6.merge(bf7);
        assertFalse(bf6.isValid(), "Should not be valid");
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void testAdd() {
        final CountingBloomFilter bf1 = createFilter(getTestShape(), from1);
        assertTrue(bf1.add(createFilter(getTestShape(), from11)), "Add should work");
        assertTrue(bf1.contains(from1), "Should contain");
        assertTrue(bf1.contains(from11), "Should contain");
        assertCounts(bf1, bigHashCounts);

        // test overflow

        final CountingBloomFilter bf2 = createEmptyFilter(getTestShape());
        assertTrue(bf2.add(maximumValueProducer), "Should add to empty");
        assertTrue(bf2.isValid(), "Should be valid");

        assertFalse(bf2.add(createFilter(getTestShape(), from1)), "Should not add");
        assertFalse(bf2.isValid(), "Should not be valid");
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public final void testSubtract() {
        final CountingBloomFilter bf1 = createFilter(getTestShape(), from1);
        bf1.add(BitCountProducer.from(from11.indices(getTestShape())));

        final CountingBloomFilter bf2 = createFilter(getTestShape(), from11);

        assertTrue(bf1.subtract(bf2), "Subtract should work");
        assertFalse(bf1.contains(bigHasher), "Should not contain bitHasher");
        assertTrue(bf1.contains(from1), "Should contain from1");

        assertCounts(bf1, from1Counts);

        // test underflow
        final CountingBloomFilter bf3 = createFilter(getTestShape(), from1);

        final CountingBloomFilter bf4 = createFilter(getTestShape(), from11);

        assertFalse(bf3.subtract(bf4), "Subtract should not work");
        assertFalse(bf3.isValid(), "isValid should return false");
        assertFalse(bf3.contains(from1), "Should not contain");
        assertFalse(bf3.contains(bf4), "Should not contain");

        assertCounts(bf3, new int[] { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 });
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public final void testRemove() {
        BloomFilter simple = new SimpleBloomFilter(getTestShape());
        simple.merge(from11);

        final CountingBloomFilter bf1 = createFilter(getTestShape(), from1);
        bf1.add(BitCountProducer.from(from11.indices(getTestShape())));

        assertTrue(bf1.remove(simple), "Remove should work");
        assertFalse(bf1.contains(from11), "Should not contain");
        assertTrue(bf1.contains(from1), "Should contain");

        assertCounts(bf1, from1Counts);

        // with hasher
        final CountingBloomFilter bf2 = createFilter(getTestShape(), from1);
        bf2.add(BitCountProducer.from(from11.indices(getTestShape())));

        assertTrue(bf2.remove(from11), "Remove should work");
        assertFalse(bf2.contains(from11), "Should not contain");
        assertTrue(bf2.contains(from1), "Should contain");

        assertCounts(bf2, from1Counts);

        // test underflow
        final CountingBloomFilter bf3 = createFilter(getTestShape(), from1);
        assertFalse(bf3.remove(simple), "Subtract should not work");
        assertFalse(bf3.isValid(), "isValid should return false");
        assertFalse(bf3.contains(from1), "Should not contain");
        assertFalse(bf3.contains(simple), "Should not contain");

        assertCounts(bf3, new int[] { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });

        // with IndexProducer
        IndexProducer ip = from11.indices(getTestShape());

        final CountingBloomFilter bf4 = createFilter(getTestShape(), from1);
        bf4.add(BitCountProducer.from(from11.indices(getTestShape())));

        assertTrue(bf4.remove(ip), "Remove should work");
        assertFalse(bf4.contains(from11), "Should not contain");
        assertTrue(bf4.contains(from1), "Should contain");

        assertCounts(bf4, from1Counts);

        // with BitMapProducer
        final BitMapProducer bmp = BitMapProducer.fromIndexProducer(ip, getTestShape().getNumberOfBits());
        final CountingBloomFilter bf5 = createFilter(getTestShape(), from1);
        bf5.add(BitCountProducer.from(from11.indices(getTestShape())));

        assertTrue(bf5.remove(bmp), "Remove should work");
        assertFalse(bf5.contains(from11), "Should not contain");
        assertTrue(bf5.contains(from1), "Should contain");

        assertCounts(bf5, from1Counts);

        // test producer errors
        IndexProducer ip2 = IndexProducer.fromIndexArray(1, 2, getTestShape().getNumberOfBits());
        final CountingBloomFilter bf6 = createFilter(getTestShape(), from1);
        assertThrows( IllegalArgumentException.class, () -> bf6.remove(ip2));

        final CountingBloomFilter bf7 = createFilter(getTestShape(), from1);
        final BitMapProducer bmp2 = BitMapProducer.fromIndexProducer(ip2, getTestShape().getNumberOfBits());
        assertThrows( IllegalArgumentException.class, () -> bf7.remove(bmp2));
    }

    @Test
    public void testExcludesDuplicates() {

        // create a hasher that produces duplicates with the specified shape.
        // this setup produces 5, 17, 29, 41, 53, 65 two times
        Shape shape = Shape.fromKM(12, 72);
        Hasher hasher = new IncrementingHasher(5, 12);

        CountingBloomFilter bf1 = createFilter(shape, hasher);
        assertEquals(6, bf1.cardinality());
        bf1.forEachCount((x, y) -> {
            assertEquals(1, y, "Hasher in constructor results in value not equal to 1");
            return true;
        });

        bf1 = createEmptyFilter(shape);
        bf1.merge(hasher);
        assertEquals(6, bf1.cardinality());
        bf1.forEachCount((x, y) -> {
            assertEquals(1, y, "Hasher in merge results in value not equal to 1");
            return true;
        });

        bf1 = createEmptyFilter(shape);
        bf1.merge(hasher);
        bf1.remove(hasher);
        assertEquals(0, bf1.cardinality());
        assertTrue(bf1.forEachCount((x, y) -> (false)), "Hasher in removes results in value not equal to 0");
    }
}
