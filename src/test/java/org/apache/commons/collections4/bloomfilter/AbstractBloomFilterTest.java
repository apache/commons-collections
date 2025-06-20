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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests standard methods in the {@link BloomFilter} interface.
 */
public abstract class AbstractBloomFilterTest<T extends BloomFilter> {

    /**
     * Test fixture class returns the value as the only value.
     */
    public static class BadHasher implements Hasher {

        IndexExtractor extractor;

        public BadHasher(final int value) {
            this.extractor = IndexExtractor.fromIndexArray(value);
        }

        @Override
        public IndexExtractor indices(final Shape shape) {
            return extractor;
        }
    }

    private void assertFailedIndexExtractorConstructor(final Shape shape, final int[] values) {
        final IndexExtractor indices = IndexExtractor.fromIndexArray(values);
        assertThrows(IllegalArgumentException.class, () -> createFilter(shape, indices));
    }

    private void assertIndexExtractorMerge(final Shape shape, final int[] values, final int[] expected) {
        final IndexExtractor indices = IndexExtractor.fromIndexArray(values);
        final BloomFilter filter = createFilter(shape, indices);
        final List<Integer> lst = new ArrayList<>();
        filter.processIndices(x -> {
            lst.add(x);
            return true;
        });
        assertEquals(expected.length, lst.size());
        for (final int value : expected) {
            assertTrue(lst.contains(Integer.valueOf(value)), "Missing " + value);
        }
    }

    /**
     * Creates an empty version of the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @return a BloomFilter implementation.
     */
    protected abstract T createEmptyFilter(Shape shape);

    /**
     * Creates the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @param extractor A BitMap extractor to build the filter with.
     * @return a BloomFilter implementation.
     */
    protected final T createFilter(final Shape shape, final BitMapExtractor extractor) {
        final T bf = createEmptyFilter(shape);
        bf.merge(extractor);
        return bf;
    }

    /**
     * Creates the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @param hasher the hasher to use to create the filter.
     * @return a BloomFilter implementation.
     */
    protected final T createFilter(final Shape shape, final Hasher hasher) {
        final T bf = createEmptyFilter(shape);
        bf.merge(hasher);
        return bf;
    }

    /**
     * Creates the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @param extractor An Index extractor to build the filter with.
     * @return a BloomFilter implementation.
     */
    protected final T createFilter(final Shape shape, final IndexExtractor extractor) {
        final T bf = createEmptyFilter(shape);
        bf.merge(extractor);
        return bf;
    }

    /**
     * The shape of the Bloom filters for testing.
     * <ul>
     *  <li>Hash functions (k) = 17
     *  <li>Number of bits (m) = 72
     * </ul>
     * @return the testing shape.
     */
    protected Shape getTestShape() {
        return Shape.fromKM(17, 72);
    }

    /**
     * Tests that asBitMapArray works correctly.
     */
    @Test
    final void testAsBitMapArray() {

        // test when multiple long values are returned.
        final IncrementingHasher hasher = new IncrementingHasher(63, 1);
        final BloomFilter bf = createFilter(Shape.fromKM(2, 72), hasher);
        final long[] lb = bf.asBitMapArray();
        assertEquals(2, lb.length);
        assertEquals(0x8000000000000000L, lb[0]);
        assertEquals(0x1, lb[1]);
    }

    @Test
    void testBitMapExtractorSize() {
        final int[] idx = new int[1];
        createFilter(getTestShape(), TestingHashers.FROM1).processBitMaps(i -> {
            idx[0]++;
            return true;
        });
        assertEquals(BitMaps.numberOfBitMaps(getTestShape()), idx[0]);

        idx[0] = 0;
        createEmptyFilter(getTestShape()).processBitMaps(i -> {
            idx[0]++;
            return true;
        });
        assertEquals(BitMaps.numberOfBitMaps(getTestShape()), idx[0]);
    }

    @Test
    void testCardinalityAndIsEmpty() {
        testCardinalityAndIsEmpty(createEmptyFilter(getTestShape()));
    }

    /**
     * Tests cardinality and isEmpty. Bloom filter must be able to accept multiple
     * IndexExtractor merges until all the bits are populated.
     *
     * @param bf The Bloom filter to test.
     */
    protected void testCardinalityAndIsEmpty(final BloomFilter bf) {
        assertTrue(bf.isEmpty());
        assertEquals(0, bf.cardinality());
        for (int i = 0; i < getTestShape().getNumberOfBits(); i++) {
            bf.merge(IndexExtractor.fromIndexArray(i));
            assertFalse(bf.isEmpty(), "Wrong value at " + i);
            assertEquals(i + 1, bf.cardinality(), "Wrong value at " + i);
        }

        // check operations in reverse order
        bf.clear();
        assertEquals(0, bf.cardinality());
        assertTrue(bf.isEmpty());
        for (int i = 0; i < getTestShape().getNumberOfBits(); i++) {
            bf.merge(IndexExtractor.fromIndexArray(i));
            assertEquals(i + 1, bf.cardinality(), "Wrong value at " + i);
            assertFalse(bf.isEmpty(), "Wrong value at " + i);
        }
    }

    @Test
    void testClear() {
        final BloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
        assertNotEquals(0, bf1.cardinality());
        bf1.clear();
        assertEquals(0, bf1.cardinality());
    }

    @Test
    final void testContains() {
        BloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
        final BloomFilter bf2 = TestingHashers.populateFromHashersFrom1AndFrom11(createEmptyFilter(getTestShape()));

        assertTrue(bf1.contains(bf1), "BF1 Should contain itself");
        assertTrue(bf2.contains(bf2), "BF2 Should contain itself");
        assertFalse(bf1.contains(bf2), "BF1 should not contain BF2");
        assertTrue(bf2.contains(bf1), "BF2 should contain BF1");

        assertTrue(bf2.contains(new IncrementingHasher(1, 1)), "BF2 Should contain this hasher");
        assertFalse(bf2.contains(new IncrementingHasher(1, 3)), "BF2 Should not contain this hasher");

        IndexExtractor indexExtractor = new IncrementingHasher(1, 1).indices(getTestShape());
        assertTrue(bf2.contains(indexExtractor), "BF2 Should contain this hasher");
        indexExtractor = new IncrementingHasher(1, 3).indices(getTestShape());
        assertFalse(bf2.contains(indexExtractor), "BF2 Should not contain this hasher");

        BitMapExtractor bitMapExtractor = BitMapExtractor.fromIndexExtractor(new IncrementingHasher(1, 1).indices(getTestShape()),
                getTestShape().getNumberOfBits());
        assertTrue(bf2.contains(bitMapExtractor), "BF2 Should contain this hasher");
        bitMapExtractor = BitMapExtractor.fromIndexExtractor(new IncrementingHasher(1, 3).indices(getTestShape()), getTestShape().getNumberOfBits());
        assertFalse(bf2.contains(bitMapExtractor), "BF2 Should not contain this hasher");

        // Test different lengths
        bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
        final BloomFilter bf3 = createFilter(Shape.fromKM(getTestShape().getNumberOfHashFunctions(), Long.SIZE - 1), TestingHashers.FROM1);
        assertTrue(bf1.contains(bf3));
        assertTrue(bf3.contains(bf1));

        final BloomFilter bf4 = TestingHashers.populateRange(createEmptyFilter(Shape.fromKM(getTestShape().getNumberOfHashFunctions(), Long.SIZE - 1)), 1,
                11 + getTestShape().getNumberOfHashFunctions());

        assertFalse(bf1.contains(bf4));
        assertTrue(bf4.contains(bf1));
    }

    @Test
    void testCopy() {
        testCopy(true);
    }

    protected void testCopy(final boolean assertClass) {
        final BloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
        assertNotEquals(0, bf1.cardinality());
        final BloomFilter copy = bf1.copy();
        assertNotSame(bf1, copy);
        assertArrayEquals(bf1.asBitMapArray(), copy.asBitMapArray());
        assertArrayEquals(bf1.asIndexArray(), copy.asIndexArray());
        assertEquals(bf1.cardinality(), copy.cardinality());
        assertEquals(bf1.characteristics(), copy.characteristics());
        assertEquals(bf1.estimateN(), copy.estimateN());
        if (assertClass) {
            assertEquals(bf1.getClass(), copy.getClass());
        }
        assertEquals(bf1.getShape(), copy.getShape());
        assertEquals(bf1.isEmpty(), copy.isEmpty());
        assertEquals(bf1.isFull(), copy.isFull());
    }

    @Test
    void testEmptyAfterMergeWithNothing() {
        // test the case where is empty after merge
        // in this case the internal cardinality == -1
        final BloomFilter bf = createEmptyFilter(getTestShape());
        bf.merge(IndexExtractor.fromIndexArray());
        assertTrue(bf.isEmpty());
    }

    /**
     * Tests that the estimated intersection calculations are correct.
     */
    @Test
    final void testEstimateIntersection() {
        final BloomFilter bf = createFilter(getTestShape(), TestingHashers.FROM1);
        final BloomFilter bf2 = TestingHashers.populateFromHashersFrom1AndFrom11(createEmptyFilter(getTestShape()));

        final BloomFilter bf3 = TestingHashers.populateEntireFilter(createEmptyFilter(getTestShape()));

        assertEquals(1, bf.estimateIntersection(bf2));
        assertEquals(1, bf2.estimateIntersection(bf));
        assertEquals(1, bf.estimateIntersection(bf3));
        assertEquals(1, bf2.estimateIntersection(bf));
        assertEquals(2, bf3.estimateIntersection(bf2));

        final BloomFilter bf4 = createEmptyFilter(getTestShape());

        assertEquals(0, bf.estimateIntersection(bf4));
        assertEquals(0, bf4.estimateIntersection(bf));

        final int midPoint = getTestShape().getNumberOfBits() / 2;
        final BloomFilter bf5 = TestingHashers.populateRange(createEmptyFilter(getTestShape()), 0, midPoint);
        final BloomFilter bf6 = TestingHashers.populateRange(createEmptyFilter(getTestShape()), midPoint + 1, getTestShape().getNumberOfBits() - 1);
        assertThrows(IllegalArgumentException.class, () -> bf5.estimateIntersection(bf6));

        // infinite with infinite
        assertEquals(Integer.MAX_VALUE, bf3.estimateIntersection(bf3));
    }

    /**
     * Tests that the size estimate is correctly calculated.
     */
    @Test
    final void testEstimateN() {
        // build a filter
        BloomFilter filter1 = createFilter(getTestShape(), TestingHashers.FROM1);
        assertEquals(1, filter1.estimateN());

        // the data provided above do not generate an estimate that is equivalent to the
        // actual.
        filter1.merge(new IncrementingHasher(4, 1));
        assertEquals(1, filter1.estimateN());

        filter1.merge(new IncrementingHasher(17, 1));

        assertEquals(3, filter1.estimateN());

        filter1 = TestingHashers.populateEntireFilter(createEmptyFilter(getTestShape()));
        assertEquals(Integer.MAX_VALUE, filter1.estimateN());
    }

    /**
     * Tests that the estimated union calculations are correct.
     */
    @Test
    final void testEstimateUnion() {
        final BloomFilter bf = createFilter(getTestShape(), TestingHashers.FROM1);
        final BloomFilter bf2 = createFilter(getTestShape(), TestingHashers.FROM11);

        assertEquals(2, bf.estimateUnion(bf2));
        assertEquals(2, bf2.estimateUnion(bf));

        final BloomFilter bf3 = createEmptyFilter(getTestShape());

        assertEquals(1, bf.estimateUnion(bf3));
        assertEquals(1, bf3.estimateUnion(bf));
    }

    @Test
    void testIndexExtractorMerge() {
        final Shape shape = Shape.fromKM(5, 10);

        assertIndexExtractorMerge(shape, new int[] {0, 2, 4, 6, 8}, new int[] {0, 2, 4, 6, 8});
        // test duplicate values
        assertIndexExtractorMerge(shape, new int[] {0, 2, 4, 2, 8}, new int[] {0, 2, 4, 8});
        // test negative values
        assertFailedIndexExtractorConstructor(shape, new int[] {0, 2, 4, -2, 8});
        // test index too large
        assertFailedIndexExtractorConstructor(shape, new int[] {0, 2, 4, 12, 8});
        // test no indices
        assertIndexExtractorMerge(shape, new int[0], new int[0]);
    }

    /**
     * Tests that isFull() returns the proper values.
     */
    @Test
    final void testIsFull() {

        // create empty filter
        BloomFilter filter = createEmptyFilter(getTestShape());
        assertFalse(filter.isFull(), "Should not be full");

        filter = TestingHashers.populateEntireFilter(filter);
        assertTrue(filter.isFull(), "Should be full");

        filter = createFilter(getTestShape(), new IncrementingHasher(1, 3));
        assertFalse(filter.isFull(), "Should not be full");
    }

    /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
    @Test
    final void testMerge() {

        final BloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
        final BloomFilter bf2 = createFilter(getTestShape(), TestingHashers.FROM11);
        final BloomFilter bf3 = bf1.copy();
        bf3.merge(bf2);

        // test with BloomFilter

        final long[] bf1Val = bf1.asBitMapArray();
        final long[] bf2Val = bf2.asBitMapArray();
        for (int i = 0; i < bf1Val.length; i++) {
            bf1Val[i] |= bf2Val[i];
        }
        bf1.merge(bf2);

        final long[] bf1New = bf1.asBitMapArray();
        for (int i = 0; i < bf1Val.length; i++) {
            assertEquals(bf1Val[i], bf1New[i], "Bad value at " + i);
        }

        assertTrue(bf1.contains(bf2), "Should contain bf2");
        assertTrue(bf1.contains(bf3), "Should contain bf3");

        // test with hasher

        final BloomFilter bf4 = createFilter(getTestShape(), TestingHashers.FROM1);
        bf4.merge(TestingHashers.FROM11);

        assertTrue(bf4.contains(bf2), "Should contain Bf2");
        assertTrue(bf4.contains(bf3), "Should contain Bf3");

        // test with hasher returning numbers out of range
        assertThrows(IllegalArgumentException.class,
                () -> bf1.merge(new BadHasher(bf1.getShape().getNumberOfBits())));
        assertThrows(IllegalArgumentException.class, () -> bf1.merge(new BadHasher(-1)));

        // test error when bloom filter returns values out of range
        final Shape s = Shape.fromKM(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits() * 3);
        final Hasher h = new IncrementingHasher(getTestShape().getNumberOfBits() * 2, 1);
        final BloomFilter bf5 = new SimpleBloomFilter(s);
        bf5.merge(h);
        assertThrows(IllegalArgumentException.class, () -> bf1.merge(bf5));

        final BloomFilter bf6 = new SparseBloomFilter(s);
        bf6.merge(h);
        assertThrows(IllegalArgumentException.class, () -> bf1.merge(bf6));
    }

    @Test
    void testMergeWithBadHasher() {
        // value too large
        final BloomFilter f = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class,
                () -> f.merge(new BadHasher(getTestShape().getNumberOfBits())));
        // negative value
        final BloomFilter f2 = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class, () -> f2.merge(new BadHasher(-1)));
    }

    @Test
    void testMergeWithBitMapExtractor() {
        final int bitMapCount = BitMaps.numberOfBitMaps(getTestShape());
        for (int i = 0; i < 5; i++) {
            final long[] values = new long[bitMapCount];
            for (final int idx : DefaultIndexExtractorTest.generateIntArray(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits())) {
                BitMaps.set(values, idx);
            }
            final BloomFilter f = createFilter(getTestShape(), BitMapExtractor.fromBitMapArray(values));
            final List<Long> lst = new ArrayList<>();
            for (final long l : values) {
                lst.add(l);
            }
            assertTrue(f.processBitMaps(l -> lst.remove(Long.valueOf(l))));
            assertTrue(lst.isEmpty());
        }
        // values too large
        final long[] values = new long[bitMapCount];
        Arrays.fill(values, Long.MAX_VALUE);
        final BitMapExtractor badExtractor = BitMapExtractor.fromBitMapArray(values);
        final BloomFilter bf = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class, () -> bf.merge(badExtractor));

        // test where merged bits exceed expected bits but both bitmaps are the same length.
        final BitMapExtractor badExtractor2 = BitMapExtractor.fromBitMapArray(0x80_00_00_00_00_00_00_00L);
        final BloomFilter bf2 = createEmptyFilter(Shape.fromKM(3, 32));
        assertThrows(IllegalArgumentException.class, () -> bf2.merge(badExtractor2));
    }

    @Test
    void testMergeWithHasher() {
        for (int i = 0; i < 5; i++) {
            final BloomFilter f = createEmptyFilter(getTestShape());
            final int[] expected = DefaultIndexExtractorTest.generateIntArray(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits());
            final Hasher hasher = new ArrayHasher(expected);
            f.merge(hasher);
            // create sorted unique array of expected values
            assertArrayEquals(DefaultIndexExtractorTest.unique(expected), f.asIndexArray());
        }
    }

    @Test
    void testMergeWithIndexExtractor() {
        for (int i = 0; i < 5; i++) {
            final int[] values = DefaultIndexExtractorTest.generateIntArray(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits());
            final BloomFilter f = createFilter(getTestShape(), IndexExtractor.fromIndexArray(values));
            final BitSet uniqueValues = DefaultIndexExtractorTest.uniqueSet(values);
            assertTrue(f.processIndices(idx -> {
                final boolean result = uniqueValues.get(idx);
                uniqueValues.clear(idx);
                return result;
            }));
            assertTrue(uniqueValues.isEmpty());
        }
        // value to large
        final BloomFilter f1 = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class,
                () -> f1.merge(IndexExtractor.fromIndexArray(getTestShape().getNumberOfBits())));
        // negative value
        final BloomFilter f2 = createEmptyFilter(getTestShape());
        assertThrows(IllegalArgumentException.class,
                () -> f2.merge(IndexExtractor.fromIndexArray(-1)));
    }

    @Test
    final void testNegativeIntersection() {
        final IndexExtractor p1 = IndexExtractor.fromIndexArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 20, 26, 28, 30, 32, 34, 35, 36, 37, 39, 40, 41, 42, 43, 45, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71);
        final IndexExtractor p2 = IndexExtractor.fromIndexArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);

        final BloomFilter filter1 = createEmptyFilter(Shape.fromKM(17, 72));
        filter1.merge(p1);
        final BloomFilter filter2 = createEmptyFilter(Shape.fromKM(17, 72));
        filter2.merge(p2);
        assertEquals(0, filter1.estimateIntersection(filter2));
    }
}
