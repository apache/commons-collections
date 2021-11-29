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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        public void forEachCount(BitCountProducer.BitCountConsumer consumer) {
            for (int i = 1; i < 18; i++) {
                consumer.accept(i, Integer.MAX_VALUE);
            }
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
        bf.forEachCount(m::put);
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
    public void constructorTest_Hasher_Duplicates() {
        // bit hasher has duplicates for 11, 12,13,14,15,16, and 17
        final CountingBloomFilter bf = createFilter(shape, from1);
        bf.add(BitCountProducer.from(from11.indices(shape)));

        final long[] lb = BloomFilter.asBitMapArray(bf);
        assertEquals(1, lb.length);
        assertEquals(bigHashValue, lb[0]);

        assertCounts(bf, bigHashCounts);
    }

    @Override
    @Test
    public void containsTest() {
        final BloomFilter bf = new SimpleBloomFilter(shape, from1);
        final CountingBloomFilter bf2 = createFilter(shape, bigHasher);

        assertTrue("BF Should contain itself", bf.contains(bf));
        assertTrue("BF2 Should contain itself", bf2.contains(bf2));
        assertFalse("BF should not contain BF2", bf.contains(bf2));
        assertTrue("BF2 should contain BF", bf2.contains(bf));
        BitMapProducer producer = bf2;
        assertTrue("BF2 should contain BF bitMapProducer", bf2.contains(producer));

    }

    /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
    @Test
    public final void mergeTest_Mixed() {
        final BloomFilter bf1 = createFilter(shape, from1);

        final BloomFilter bf2 = new SimpleBloomFilter(shape, from11);

        final BloomFilter bf3 = bf1.merge(bf2);
        assertTrue("Should contain", bf3.contains(bf1));
        assertTrue("Should contain", bf3.contains(bf2));

        final BloomFilter bf4 = bf2.merge(bf1);
        assertTrue("Should contain", bf4.contains(bf1));
        assertTrue("Should contain", bf4.contains(bf2));
        assertTrue("Should contain", bf4.contains(bf3));
        assertTrue("Should contain", bf3.contains(bf4));
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void addTest() {
        final CountingBloomFilter bf1 = createFilter(shape, from1);
        assertTrue("Add should work", bf1.add(createFilter(shape, from11)));
        assertTrue("Should contain", bf1.contains(from1));
        assertTrue("Should contain", bf1.contains(from11));
        assertCounts(bf1, bigHashCounts);

    }

    @Test
    public void addTest_overflow() {

        final CountingBloomFilter bf1 = createEmptyFilter(shape);
        assertTrue("Should add to empty", bf1.add(maximumValueProducer));
        assertTrue("Should be valid", bf1.isValid());

        assertFalse("Should not add", bf1.add(createFilter(shape, from1)));
        assertFalse("Should not be valid", bf1.isValid());
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void subtractTest() {
        final CountingBloomFilter bf1 = createFilter(shape, from1);
        bf1.add(BitCountProducer.from(from11.indices(shape)));

        final CountingBloomFilter bf2 = createFilter(shape, from11);

        assertTrue("Subtract should work", bf1.subtract(bf2));
        assertFalse("Should not contain bitHasher", bf1.contains(bigHasher));
        assertTrue("Should contain from1", bf1.contains(from1));

        assertCounts(bf1, from1Counts);

    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void subtractTest_underflow() {
        final CountingBloomFilter bf1 = createFilter(shape, from1);

        final CountingBloomFilter bf2 = createFilter(shape, from11);

        assertFalse("Subtract should not work", bf1.subtract(bf2));
        assertFalse("isValid should return false", bf1.isValid());
        assertFalse("Should not contain", bf1.contains(from1));
        assertFalse("Should not contain", bf1.contains(bf2));

        assertCounts(bf1, new int[] { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 });

    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void removeTest() {
        final CountingBloomFilter bf1 = createFilter(shape, from1);
        bf1.add(BitCountProducer.from(from11.indices(shape)));

        assertTrue("Remove should work", bf1.remove(new SimpleBloomFilter(shape, from11)));
        assertFalse("Should not contain", bf1.contains(from11));
        assertTrue("Should contain", bf1.contains(from1));

        assertCounts(bf1, from1Counts);

    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void removeTest_hasher() {
        final CountingBloomFilter bf1 = createFilter(shape, from1);
        bf1.add(BitCountProducer.from(from11.indices(shape)));

        assertTrue("Remove should work", bf1.remove(from11));
        assertFalse("Should not contain", bf1.contains(from11));
        assertTrue("Should contain", bf1.contains(from1));

        assertCounts(bf1, from1Counts);

    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void removeTest_underflow() {
        final CountingBloomFilter bf1 = createFilter(shape, from1);

        final BloomFilter bf2 = new SimpleBloomFilter(shape, from11);

        assertFalse("Subtract should not work", bf1.remove(bf2));
        assertFalse("isValid should return false", bf1.isValid());
        assertFalse("Should not contain", bf1.contains(from1));
        assertFalse("Should not contain", bf1.contains(bf2));

        assertCounts(bf1, new int[] { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });

    }

    @Test
    public void mergeTest_overflow() {

        final CountingBloomFilter bf1 = createEmptyFilter(shape);
        assertTrue("Should add to empty", bf1.add(maximumValueProducer));
        assertTrue("Should be valid", bf1.isValid());

        CountingBloomFilter bf2 = bf1.merge(new SimpleBloomFilter(shape, from1));
        assertFalse("Should not be valid", bf2.isValid());
    }

}
