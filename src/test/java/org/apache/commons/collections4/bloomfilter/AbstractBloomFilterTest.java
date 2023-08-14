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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test standard methods in the {@link BloomFilter} interface.
 */
public abstract class AbstractBloomFilterTest<T extends BloomFilter> {

    protected final Hasher from1 = new IncrementingHasher(1, 1);
    protected final long from1Value = 0x3fffeL;
    protected final Hasher from11 = new IncrementingHasher(11, 1);
    protected final long from11Value = 0xffff800L;
    protected final HasherCollection bigHasher = new HasherCollection(from1, from11);
    protected final long bigHashValue = 0xffffffeL;
    protected final HasherCollection fullHasher = new HasherCollection(new IncrementingHasher(0, 1)/* 0-16 */,
            new IncrementingHasher(17, 1)/* 17-33 */, new IncrementingHasher(33, 1)/* 33-49 */, new IncrementingHasher(50, 1)/* 50-66 */,
            new IncrementingHasher(67, 1)/* 67-83 */
    );
    protected final long[] fullHashValue = { 0xffffffffffffffffL, 0xfffffL };

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
     * @param shape the shape of the filter.
     * @param hasher the hasher to use to create the filter.
     * @return a BloomFilter implementation.
     */
    protected final T createFilter(Shape shape, Hasher hasher) {
        T bf = createEmptyFilter(shape);
        bf.merge(hasher);
        return bf;
    }

    /**
     * Create the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @param producer A BitMap producer to build the filter with.
     * @return a BloomFilter implementation.
     */
    protected final T createFilter(Shape shape, BitMapProducer producer) {
        T bf = createEmptyFilter(shape);
        bf.merge(producer);
        return bf;
    }

    /**
     * Create the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @param producer An Index producer to build the filter with.
     * @return a BloomFilter implementation.
     */
    protected final T createFilter(Shape shape, IndexProducer producer) {
        T bf = createEmptyFilter(shape);
        bf.merge(producer);
        return bf;
    }

    /**
     *
     */
    @Test
    public void testMergeWithBadHasher() {
        // value too large
        final BloomFilter f = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class,
                () -> f.merge(new BadHasher(getTestShape().getNumberOfBits())));
        // negative value
        BloomFilter f2 = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class, () -> f2.merge(new BadHasher(-1)));
    }

    @Test
    public void testMergeWithHasher() {
        for (int i = 0; i < 5; i++) {
            final BloomFilter f = createEmptyFilter(getTestShape());
            int[] expected = DefaultIndexProducerTest.generateIntArray(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits());
            Hasher hasher = new ArrayHasher(expected);
            f.merge(hasher);
            // create sorted unique array of expected values
            assertArrayEquals(DefaultIndexProducerTest.unique(expected), f.asIndexArray( ));
        }
    }

    @Test
    public void testMergeWithBitMapProducer() {
        for (int i = 0; i < 5; i++) {
            long[] values = new long[2];
            for (int idx :  DefaultIndexProducerTest.generateIntArray(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits())) {
                BitMap.set(values, idx);
            }
            BloomFilter f = createFilter(getTestShape(), BitMapProducer.fromBitMapArray(values));
            List<Long> lst = new ArrayList<>();
            for (long l : values) {
                lst.add(l);
            }
            assertTrue(f.forEachBitMap(l -> {
                return lst.remove(Long.valueOf(l));
            }));
            assertTrue(lst.isEmpty());
        }
        // values too large
        final BitMapProducer badProducer = BitMapProducer.fromBitMapArray(0L, Long.MAX_VALUE);
        final BloomFilter bf = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class, () -> bf.merge(badProducer));

        // test where merged bits exceed expected bits but both bitmaps are the same length.
        final BitMapProducer badProducer2 = BitMapProducer.fromBitMapArray(0x80_00_00_00_00_00_00_00L);
        final BloomFilter bf2 = createEmptyFilter(Shape.fromKM(3, 32));
        assertThrows(IllegalArgumentException.class, () -> bf2.merge(badProducer2));
    }

    @Test
    public void testMergeWithIndexProducer() {
        for (int i = 0; i < 5; i++) {
            int[] values = DefaultIndexProducerTest.generateIntArray(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits());
            BloomFilter f = createFilter(getTestShape(), IndexProducer.fromIndexArray(values));
            BitSet uniqueValues = DefaultIndexProducerTest.uniqueSet(values);
            assertTrue(f.forEachIndex(idx -> {
                final boolean result = uniqueValues.get(idx);
                uniqueValues.clear(idx);
                return result;
            }));
            assertTrue(uniqueValues.isEmpty());
        }
        // value to large
        final BloomFilter f1 = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class,
                () -> f1.merge(IndexProducer.fromIndexArray(new int[] { getTestShape().getNumberOfBits() })));
        // negative value
        final BloomFilter f2 = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class,
                () -> f2.merge(IndexProducer.fromIndexArray(new int[] { -1 })));
    }

    @Test
    public final void testContains() {
        BloomFilter bf1 = createFilter(getTestShape(), from1);
        final BloomFilter bf2 = createFilter(getTestShape(), bigHasher);

        assertTrue(bf1.contains(bf1), "BF Should contain itself");
        assertTrue(bf2.contains(bf2), "BF2 Should contain itself");
        assertFalse(bf1.contains(bf2), "BF should not contain BF2");
        assertTrue(bf2.contains(bf1), "BF2 should contain BF");

        assertTrue(bf2.contains(new IncrementingHasher(1, 1)), "BF2 Should contain this hasher");
        assertFalse(bf2.contains(new IncrementingHasher(1, 3)), "BF2 Should not contain this hasher");

        IndexProducer indexProducer = new IncrementingHasher(1, 1).indices(getTestShape());
        assertTrue(bf2.contains(indexProducer), "BF2 Should contain this hasher");
        indexProducer = new IncrementingHasher(1, 3).indices(getTestShape());
        assertFalse(bf2.contains(indexProducer), "BF2 Should not contain this hasher");

        BitMapProducer bitMapProducer = BitMapProducer.fromIndexProducer(new IncrementingHasher(1, 1).indices(getTestShape()),
                getTestShape().getNumberOfBits());
        assertTrue(bf2.contains(bitMapProducer), "BF2 Should contain this hasher");
        bitMapProducer = BitMapProducer.fromIndexProducer(new IncrementingHasher(1, 3).indices(getTestShape()),
                getTestShape().getNumberOfBits());
        assertFalse(bf2.contains(bitMapProducer), "BF2 Should not contain this hasher");

        // Test different lengths
        bf1 = createFilter(getTestShape(), from1);
        final BloomFilter bf3 = createFilter(Shape.fromKM(getTestShape().getNumberOfHashFunctions(), Long.SIZE - 1),
                from1);
        assertTrue(bf1.contains(bf3));
        assertTrue(bf3.contains(bf1));

        final BloomFilter bf4 = createFilter(Shape.fromKM(getTestShape().getNumberOfHashFunctions(), Long.SIZE - 1),
                bigHasher);
        assertFalse(bf1.contains(bf4));
        assertTrue(bf4.contains(bf1));
    }

    @Test
    public void testClear() {
        BloomFilter bf1 = createFilter(getTestShape(), from1);
        assertNotEquals(0, bf1.cardinality());
        bf1.clear();
        assertEquals(0, bf1.cardinality());
    }

    /**
     * Tests that the andCardinality calculations are correct.
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
        BloomFilter filter1 = createFilter(getTestShape(), from1);
        assertEquals(1, filter1.estimateN());

        // the data provided above do not generate an estimate that is equivalent to the
        // actual.
        filter1.merge(new IncrementingHasher(4, 1));

        assertEquals(1, filter1.estimateN());

        filter1.merge(new IncrementingHasher(17, 1));

        assertEquals(3, filter1.estimateN());
    }

    /**
     * Tests that asBitMapArray works correctly.
     */
    @Test
    public final void testAsBitMapArray() {

        // test when multiple long values are returned.
        final IncrementingHasher hasher = new IncrementingHasher(63, 1);
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

        filter = createFilter(getTestShape(), new IncrementingHasher(1, 3));
        assertFalse(filter.isFull(), "Should not be full");
    }

    /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
    @Test
    public final void testMerge() {

        final BloomFilter bf1 = createFilter(getTestShape(), from1);
        final BloomFilter bf2 = createFilter(getTestShape(), from11);
        final BloomFilter bf3 = bf1.copy();
        bf3.merge(bf2);

        // test with BloomFilter

        long[] bf1Val = bf1.asBitMapArray();
        long[] bf2Val = bf2.asBitMapArray();
        for (int i = 0; i < bf1Val.length; i++) {
            bf1Val[i] |= bf2Val[i];
        }
        bf1.merge(bf2);

        long[] bf1New = bf1.asBitMapArray();
        for (int i = 0; i < bf1Val.length; i++) {
            assertEquals(bf1Val[i], bf1New[i], "Bad value at " + i);
        }

        assertTrue(bf1.contains(bf2), "Should contain bf2");
        assertTrue(bf1.contains(bf3), "Should contain bf3");

        // test with hasher

        BloomFilter bf4 = createFilter(getTestShape(), from1);
        bf4.merge(from11);

        assertTrue(bf4.contains(bf2), "Should contain Bf2");
        assertTrue(bf4.contains(bf3), "Should contain Bf3");

        // test with hasher returning numbers out of range
        assertThrows(IllegalArgumentException.class,
                () -> bf1.merge(new BadHasher(bf1.getShape().getNumberOfBits())));
        assertThrows(IllegalArgumentException.class, () -> bf1.merge(new BadHasher(-1)));

        // test error when bloom filter returns values out of range
        BloomFilter bf5 = new SimpleBloomFilter(
                Shape.fromKM(getTestShape().getNumberOfHashFunctions(), 3 * Long.SIZE));
        bf5.merge(new IncrementingHasher(Long.SIZE * 2, 1));
        assertThrows(IllegalArgumentException.class, () -> bf1.merge(bf5));

        BloomFilter bf6 = new SparseBloomFilter(
                Shape.fromKM(getTestShape().getNumberOfHashFunctions(), 3 * Long.SIZE));
        bf6.merge(new IncrementingHasher(Long.SIZE * 2, 1));
        assertThrows(IllegalArgumentException.class, () -> bf1.merge(bf6));
    }

    private void assertIndexProducerMerge(Shape shape, int[] values, int[] expected) {
        IndexProducer indices = IndexProducer.fromIndexArray(values);
        BloomFilter filter = createFilter(shape, indices);
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
        IndexProducer indices = IndexProducer.fromIndexArray(values);
        assertThrows(IllegalArgumentException.class, () -> createFilter(shape, indices));
    }

    @Test
    public void testIndexProducerMerge() {
        Shape shape = Shape.fromKM(5, 10);

        assertIndexProducerMerge(shape, new int[] { 0, 2, 4, 6, 8 }, new int[] { 0, 2, 4, 6, 8 });
        // test duplicate values
        assertIndexProducerMerge(shape, new int[] { 0, 2, 4, 2, 8 }, new int[] { 0, 2, 4, 8 });
        // test negative values
        assertFailedIndexProducerConstructor(shape, new int[] { 0, 2, 4, -2, 8 });
        // test index too large
        assertFailedIndexProducerConstructor(shape, new int[] { 0, 2, 4, 12, 8 });
        // test no indices
        assertIndexProducerMerge(shape, new int[0], new int[0]);
    }

    @Test
    public void testBitMapProducerSize() {
        int[] idx = new int[1];
        createFilter(getTestShape(), from1).forEachBitMap(i -> {
            idx[0]++;
            return true;
        });
        assertEquals(BitMap.numberOfBitMaps(getTestShape().getNumberOfBits()), idx[0]);

        idx[0] = 0;
        createEmptyFilter(getTestShape()).forEachBitMap(i -> {
            idx[0]++;
            return true;
        });
        assertEquals(BitMap.numberOfBitMaps(getTestShape().getNumberOfBits()), idx[0]);
    }

    /**
     * Testing class returns the value as the only value.
     */
    class BadHasher implements Hasher {

        IndexProducer producer;

        BadHasher(int value) {
            this.producer = IndexProducer.fromIndexArray(new int[] { value });
        }

        @Override
        public IndexProducer indices(Shape shape) {
            return producer;
        }

        @Override
        public IndexProducer uniqueIndices(Shape shape) {
            return producer;
        }
    }
}
