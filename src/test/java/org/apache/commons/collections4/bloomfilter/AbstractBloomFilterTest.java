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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongPredicate;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.HasherCollection;
import org.apache.commons.collections4.bloomfilter.hasher.SimpleHasher;
import org.junit.jupiter.api.Test;

/**
 * Test standard methods in the {@link BloomFilter} interface.
 */
public abstract class AbstractBloomFilterTest<T extends BloomFilter> {

    protected final SimpleHasher from1 = new SimpleHasher(1, 1);
    protected final long from1Value = 0x3FFFEL;
    protected final SimpleHasher from11 = new SimpleHasher(11, 1);
    protected final long from11Value = 0xFFFF800L;
    protected final HasherCollection bigHasher = new HasherCollection(from1, from11);
    protected final long bigHashValue = 0xFFFFFFEL;
    protected final HasherCollection fullHasher = new HasherCollection(new SimpleHasher(0, 1)/* 0-16 */,
            new SimpleHasher(17, 1)/* 17-33 */, new SimpleHasher(33, 1)/* 33-49 */, new SimpleHasher(50, 1)/* 50-66 */,
            new SimpleHasher(67, 1)/* 67-83 */
            );
    protected final long[] fullHashValue = { 0xFFFFFFFFFFFFFFFFL, 0xFFFFFL };

    /**
     * The shape of the Bloom filters for testing.
     * <ul>
     *  <li>Hash functions (k) = 17
     *  <li>Number of bits (m) = 72
     * </ul>
     * @return the testing shape.
     */
    protected final Shape getTestShape() {
        return Shape.fromKM(17, 72);
    }

    /**
     * Create an empty version of the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @return a BloomFilter implementation.
     */
    protected abstract T createEmptyFilter(Shape shape);

    /**
     * Create the BloomFilter implementation we are testing.
     *
     * @param hasher the hasher to use to create the filter.
     * @param shape the shape of the filter.
     * @return a BloomFilter implementation.
     */
    protected abstract T createFilter(Shape shape, Hasher hasher);

    /**
     * Tests that the andCardinality calculations are correct.
     *
     * @param filterFactory the factory function to create the filter
     */
    @Test
    public final void testContains() {
        final BloomFilter bf = createFilter(getTestShape(), from1);
        final BloomFilter bf2 = createFilter(getTestShape(), bigHasher);

        assertTrue(bf.contains(bf), "BF Should contain itself");
        assertTrue(bf2.contains(bf2), "BF2 Should contain itself");
        assertFalse(bf.contains(bf2), "BF should not contain BF2");
        assertTrue(bf2.contains(bf), "BF2 should contain BF");

        assertTrue(bf2.contains(new SimpleHasher(1, 1)), "BF2 Should contain this hasher");
        assertFalse(bf2.contains(new SimpleHasher(1, 3)), "BF2 Should not contain this hasher");

        IndexProducer indexProducer = new SimpleHasher(1, 1).indices(getTestShape());
        assertTrue(bf2.contains(indexProducer), "BF2 Should contain this hasher");
        indexProducer = new SimpleHasher(1, 3).indices(getTestShape());
        assertFalse(bf2.contains(indexProducer), "BF2 Should not contain this hasher");

        BitMapProducer bitMapProducer = BitMapProducer.fromIndexProducer(new SimpleHasher(1, 1).indices(getTestShape()),
                getTestShape().getNumberOfBits());
        assertTrue(bf2.contains(bitMapProducer), "BF2 Should contain this hasher");
        bitMapProducer = BitMapProducer.fromIndexProducer(new SimpleHasher(1, 3).indices(getTestShape()),
                getTestShape().getNumberOfBits());
        assertFalse(bf2.contains(bitMapProducer), "BF2 Should not contain this hasher");
    }

    /**
     * Tests that the andCardinality calculations are correct.
     *
     * @param filterFactory the factory function to create the filter
     */
    @Test
    public final void testEstimateIntersection() {

        final BloomFilter bf = createFilter(getTestShape(), from1);
        final BloomFilter bf2 = createFilter(getTestShape(), bigHasher);

        assertEquals(1, bf.estimateIntersection(bf2));
        assertEquals(1, bf2.estimateIntersection(bf));

        final BloomFilter bf3 = createEmptyFilter(getTestShape());

        assertEquals(0, bf.estimateIntersection(bf3));
        assertEquals(0, bf3.estimateIntersection(bf));
    }

    /**
     * Tests that the andCardinality calculations are correct.
     *
     * @param filterFactory the factory function to create the filter
     */
    @Test
    public final void testEstimateUnion() {
        final BloomFilter bf = createFilter(getTestShape(), from1);
        final BloomFilter bf2 = createFilter(getTestShape(), from11);

        assertEquals(2, bf.estimateUnion(bf2));
        assertEquals(2, bf2.estimateUnion(bf));

        final BloomFilter bf3 = createEmptyFilter(getTestShape());

        assertEquals(1, bf.estimateUnion(bf3));
        assertEquals(1, bf3.estimateUnion(bf));
    }

    /**
     * Tests that the size estimate is correctly calculated.
     */
    @Test
    public final void testEstimateN() {
        // build a filter
        BloomFilter filter1 = new SimpleBloomFilter(getTestShape(), from1);
        assertEquals(1, filter1.estimateN());

        // the data provided above do not generate an estimate that is equivalent to the
        // actual.
        filter1.mergeInPlace(new SimpleHasher(4, 1));

        assertEquals(1, filter1.estimateN());

        filter1.mergeInPlace(new SimpleHasher(17, 1));

        assertEquals(3, filter1.estimateN());
    }

    /**
     * Tests that asBitMapArray works correctly.
     */
    @Test
    public final void testAsBitMapArray() {

        // test when multiple long values are returned.
        final SimpleHasher hasher = new SimpleHasher(63, 1);
        final BloomFilter bf = createFilter(Shape.fromKM(2, 72), hasher);
        final long[] lb = bf.asBitMapArray();
        assertEquals(2, lb.length);
        assertEquals(0x8000000000000000L, lb[0]);
        assertEquals(0x1, lb[1]);
    }

    /**
     * Tests that isFull() returns the proper values.
     */
    @Test
    public final void testIsFull() {

        // create empty filter
        BloomFilter filter = createEmptyFilter(getTestShape());
        assertFalse(filter.isFull(), "Should not be full");

        filter = createFilter(getTestShape(), fullHasher);
        assertTrue(filter.isFull(), "Should be full");

        filter = createFilter(getTestShape(), new SimpleHasher(1, 3));
        assertFalse(filter.isFull(), "Should not be full");
    }

    /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
    @Test
    public final void testMerge() {

        // test with BloomFilter
        final BloomFilter bf1 = createFilter(getTestShape(), from1);

        final BloomFilter bf2 = createFilter(getTestShape(), from11);

        final BloomFilter bf3 = bf1.merge(bf2);
        assertTrue(bf3.contains(bf1), "Should contain bf1");
        assertTrue(bf3.contains(bf2), "Should contain bf2");

        final BloomFilter bf4 = bf2.merge(bf1);
        assertTrue(bf4.contains(bf1), "Should contain bf1");
        assertTrue(bf4.contains(bf2), "Should contain bf2");
        assertTrue(bf4.contains(bf3), "Should contain bf3");
        assertTrue(bf3.contains(bf4), "Should contain bf4");

        // test with Hasher

        final BloomFilter bf5 = bf1.merge(from11);
        assertTrue(bf5.contains(bf1), "Should contain bf1");
        assertTrue(bf5.contains(bf2), "Should contain bf2");
    }

    /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
    @Test
    public final void testMergeInPlace() {

        final BloomFilter bf1 = createFilter(getTestShape(), from1);
        final BloomFilter bf2 = createFilter(getTestShape(), from11);
        final BloomFilter bf3 = bf1.merge(bf2);

        // test with BloomFilter

        long[] bf1Val = bf1.asBitMapArray();
        long[] bf2Val = bf2.asBitMapArray();
        for (int i = 0; i < bf1Val.length; i++) {
            bf1Val[i] |= bf2Val[i];
        }
        bf1.mergeInPlace(bf2);

        long[] bf1New = bf1.asBitMapArray();
        for (int i = 0; i < bf1Val.length; i++) {
            assertEquals(bf1Val[i], bf1New[i], "Bad value at " + i);
        }

        assertTrue(bf1.contains(bf2), "Should contain bf2");
        assertTrue(bf1.contains(bf3), "Should contain bf3");

        // test with hasher

        final BloomFilter bf4 = createFilter(getTestShape(), from1);
        bf4.mergeInPlace(from11);

        assertTrue(bf4.contains(bf2), "Should contain Bf2");
        assertTrue(bf4.contains(bf3), "Should contain Bf3");
    }

    private void assertIndexProducerConstructor(Shape shape, int[] values, int[] expected) {
        IndexProducer indices = IndexProducer.fromIntArray(values);
        SparseBloomFilter filter = new SparseBloomFilter(shape, indices);
        List<Integer> lst = new ArrayList<>();
        filter.forEachIndex(x -> {
            lst.add(x);
            return true;
        });
        assertEquals(expected.length, lst.size());
        for (int value : expected) {
            assertTrue(lst.contains(Integer.valueOf(value)), "Missing " + value);
        }
    }

    private void assertFailedIndexProducerConstructor(Shape shape, int[] values) {
        IndexProducer indices = IndexProducer.fromIntArray(values);
        assertThrows(IllegalArgumentException.class, () -> new SparseBloomFilter(shape, indices));
    }

    @Test
    public void testIndexProducerConstructor() {
        Shape shape = Shape.fromKM(5, 10);

        assertIndexProducerConstructor(shape, new int[] { 0, 2, 4, 6, 8 }, new int[] { 0, 2, 4, 6, 8 });
        // test duplicate values
        assertIndexProducerConstructor(shape, new int[] { 0, 2, 4, 2, 8 }, new int[] { 0, 2, 4, 8 });
        // test negative values
        assertFailedIndexProducerConstructor(shape, new int[] { 0, 2, 4, -2, 8 });
        // test index too large
        assertFailedIndexProducerConstructor(shape, new int[] { 0, 2, 4, 12, 8 });
        // test no indicies
        assertIndexProducerConstructor(shape, new int[0], new int[0]);
    }

    @Test
    public void testForEachBitMapEarlyExit() {

        Shape shape = Shape.fromKM(5, 100);
        IndexProducer indices = IndexProducer.fromIntArray(new int[] { 66, 67 });
        BloomFilter filter = new SparseBloomFilter(shape, indices);
        EarlyExitTestPredicate consumer = new EarlyExitTestPredicate();
        assertFalse(filter.forEachBitMap(consumer));
        assertEquals(1, consumer.passes);
    }

    class EarlyExitTestPredicate implements LongPredicate {
        int passes = 0;

        @Override
        public boolean test(long arg0) {
            passes++;
            return false;
        }
    }

}
