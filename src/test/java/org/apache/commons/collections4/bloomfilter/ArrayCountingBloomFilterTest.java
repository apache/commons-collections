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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.junit.Test;

/**
 * Tests for the {@link ArrayCountingBloomFilter}.
 */
public class ArrayCountingBloomFilterTest extends AbstractBloomFilterTest {

    /**
     * Function to convert int arrays to BloomFilters for testing.
     */
    private Function<int[], BloomFilter> converter = counts -> {
        BloomFilter testingFilter = new BitSetBloomFilter(shape);
        testingFilter.merge(new FixedIndexesTestHasher(shape, counts));
        return testingFilter;
    };

    @Override
    protected ArrayCountingBloomFilter createEmptyFilter(final Shape shape) {
        return new ArrayCountingBloomFilter(shape);
    }

    @Override
    protected ArrayCountingBloomFilter createFilter(final Hasher hasher, final Shape shape) {
        ArrayCountingBloomFilter result = new ArrayCountingBloomFilter(shape);
        result.merge( hasher );
        return result;
    }

    private ArrayCountingBloomFilter createFromCounts(int[] counts) {
        // Use a dummy filter to add the counts to an empty filter
        final CountingBloomFilter dummy = new ArrayCountingBloomFilter(shape) {
            @Override
            public void forEachCount(BitCountConsumer action) {
                for (int i = 0; i < counts.length; i++) {
                    action.accept(i, counts[i]);
                }
            }
        };
        final ArrayCountingBloomFilter bf = new ArrayCountingBloomFilter(shape);
        bf.add(dummy);
        return bf;
    }

    /**
     * Assert the counts match the expected values. Values are for indices starting
     * at 0. Assert the cardinality equals the number of non-zero counts.
     *
     * @param bf the bloom filter
     * @param expected the expected counts
     */
    private static void assertCounts(CountingBloomFilter bf, int[] expected) {
        final Map<Integer, Integer> m = new HashMap<>();
        bf.forEachCount(m::put);
        int zeros = 0;
        for (int i = 0; i < expected.length; i++) {
            if (m.get(i) == null) {
                assertEquals("Wrong value for " + i, expected[i], 0);
                zeros++;
            } else {
                assertEquals("Wrong value for " + i, expected[i], m.get(i).intValue());
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
        final int[] expected = {0, 1, 1, 0, 0, 1};
        // Some indexes with duplicates
        final Hasher hasher = new FixedIndexesTestHasher(shape, 1, 2, 2, 5);

        final ArrayCountingBloomFilter bf = createFilter(hasher, shape);
        final long[] lb = bf.getBits();
        assertEquals(1, lb.length);
        assertEquals(0b100110L, lb[0]);

        assertCounts(bf, expected);
    }

    /**
     * Test the contains function with a standard Bloom filter.
     * The contains function is tested using a counting Bloom filter in the parent test class.
     */
    @Test
    public void contains_BloomFilter() {
        // Some indexes with duplicates
        final Hasher hasher = new FixedIndexesTestHasher(shape, 1, 2, 5);
        final ArrayCountingBloomFilter bf = createFilter(hasher, shape);
        BitSetBloomFilter testingFilter = new BitSetBloomFilter(shape);
        testingFilter.merge( new FixedIndexesTestHasher(shape, 3, 4));
        assertFalse(bf.contains(testingFilter));
        testingFilter = new BitSetBloomFilter(shape);
        testingFilter.merge( new FixedIndexesTestHasher(shape, 2, 5));
        assertTrue(bf.contains(testingFilter));
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void mergeTest_Counts_CountingBloomFilter() {
        assertMerge(counts -> createFilter(new FixedIndexesTestHasher(shape, counts), shape),
                BloomFilter::merge);
    }

    /**
     * Tests that merge correctly updates the counts when a BloomFilter is passed.
     */
    @Test
    public void mergeTest_Counts_BloomFilter() {
        assertMerge(converter, BloomFilter::merge);
    }

    /**
     * Test that merge correctly updates the counts when a Hasher is passed.
     */
    @Test
    public void mergeTest_Counts_Hasher() {
        assertMerge(counts -> new FixedIndexesTestHasher(shape, counts),
                BloomFilter::merge);
    }

    /**
     * Test that merge correctly updates the counts when a Hasher is passed with duplicates.
     */
    @Test
    public void mergeTest_Counts_Hasher_Duplicates() {
        assertMerge(counts -> new FixedIndexesTestHasher(shape, createDuplicates(counts)),
                BloomFilter::merge);
    }

    /**
     * Tests that remove correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void removeTest_Counts_CountingBloomFilter() {
        assertRemove(counts -> createFilter(new FixedIndexesTestHasher(shape, counts), shape),
                CountingBloomFilter::remove);
    }

    /**
     * Tests that remove correctly updates the counts when a BloomFilter is passed.
     */
    @Test
    public void removeTest_Counts_BloomFilter() {
        assertRemove(converter, CountingBloomFilter::remove);
    }

    /**
     * Test that remove correctly updates the counts when a Hasher is passed.
     */
    @Test
    public void removeTest_Counts_Hasher() {
        assertRemove(counts -> new FixedIndexesTestHasher(shape, counts),
                CountingBloomFilter::remove);
    }

    /**
     * Test that remove correctly updates the counts when a Hasher is passed with duplicates.
     */
    @Test
    public void removeTest_Counts_Hasher_Duplicates() {
        assertRemove(counts -> new FixedIndexesTestHasher(shape, createDuplicates(counts)),
                CountingBloomFilter::remove);
    }

    /**
     * Creates duplicates in the counts.
     *
     * @param counts the counts
     * @return the new counts
     */
    private static int[] createDuplicates(int[] counts) {
        // Duplicate some values randomly
        final int length = counts.length;
        int[] countsWithDuplicates = Arrays.copyOf(counts, 2 * length);
        for (int i = length; i < countsWithDuplicates.length; i++) {
            // Copy a random value from the counts into the end position
            countsWithDuplicates[i] = countsWithDuplicates[ThreadLocalRandom.current().nextInt(i)];
        }
        return countsWithDuplicates;
    }

    /**
     * Assert a merge operation. The converter should construct a suitable object
     * to remove the indices from the provided Bloom filter with the remove operation.
     *
     * @param <F> the type of the filter
     * @param converter the converter
     * @param merge the merge operation
     */
    private <F> void assertMerge(Function<int[], F> converter,
            BiPredicate<ArrayCountingBloomFilter, F> merge) {
        final int[] indexes1 = {   1, 2,    4, 5, 6};
        final int[] indexes2 = {         3, 4,    6};
        final int[] expected = {0, 1, 1, 1, 2, 1, 2};
        assertOperation(indexes1, indexes2, converter, merge, true, expected);
    }

    /**
     * Assert a remove operation. The converter should construct a suitable object
     * to remove the indices from the provided Bloom filter with the remove operation.
     *
     * @param <F> the type of the filter
     * @param converter the converter
     * @param remove the remove operation
     */
    private <F> void assertRemove(Function<int[], F> converter,
            BiPredicate<ArrayCountingBloomFilter, F> remove) {
        final int[] indexes1 = {   1, 2,    4, 5, 6};
        final int[] indexes2 = {      2,       5, 6};
        final int[] expected = {0, 1, 0, 0, 1, 0, 0};
        assertOperation(indexes1, indexes2, converter, remove, true, expected);
    }

    /**
     * Assert a counting operation. The first set of indexes is used to create the
     * CountingBloomFilter. The second set of indices is passed to the converter to
     * construct a suitable object to combine with the counting Bloom filter. The counts
     * of the first Bloom filter are checked using the expected counts.
     *
     * <p>Counts are assumed to map to indexes starting from 0.
     *
     * @param <F> the type of the filter
     * @param indexes1 the first set of indexes
     * @param indexes2 the second set of indexes
     * @param converter the converter
     * @param operation the operation
     * @param isValid the expected value for the operation result
     * @param expected the expected counts after the operation
     */
    private <F> void assertOperation(int[] indexes1, int[] indexes2,
            Function<int[], F> converter,
            BiPredicate<ArrayCountingBloomFilter, F> operation,
            boolean isValid, int[] expected) {
        final Hasher hasher = new FixedIndexesTestHasher(shape, indexes1);
        final ArrayCountingBloomFilter bf = createFilter(hasher, shape);
        final F filter = converter.apply(indexes2);
        final boolean result = operation.test(bf, filter);
        assertEquals(isValid, result);
        assertEquals(isValid, bf.isValid());
        assertCounts(bf, expected);
    }

    /**
     * Tests that merge errors when the counts overflow the maximum integer value.
     */
    @Test
    public void mergeTest_Overflow() {
        final Hasher hasher = new FixedIndexesTestHasher(shape, 1, 2, 3);
        final ArrayCountingBloomFilter bf = createFilter(hasher, shape);

        ArrayCountingBloomFilter bf2 = createFromCounts(new int[] {0, 0, Integer.MAX_VALUE});

        // Small + 1 = OK
        // should not fail as the counts are ignored
        assertTrue(bf.merge(bf2));
        assertTrue(bf.isValid());
        assertCounts(bf, new int[] {0, 1, 2, 1});

        // Big + 1 = Overflow
        assertTrue(bf2.isValid());
        assertFalse(bf2.merge(bf));
        assertFalse("Merge should overflow and the filter is invalid", bf2.isValid());

        // The counts are not clipped to max. They have simply overflowed.
        // Note that this is a merge and the count is only incremented by 1
        // and not the actual count at each index. So it is not 2 + Integer.MAX_VALUE.
        assertCounts(bf2, new int[] {0, 1, 1 + Integer.MAX_VALUE, 1});
    }

    /**
     * Tests that removal errors when the counts become negative.
     */
    @Test
    public void removeTest_Negative() {
        final Hasher hasher = new FixedIndexesTestHasher(shape, 1, 2, 3);
        ArrayCountingBloomFilter bf = createFilter(hasher, shape);

        final Hasher hasher2 = new FixedIndexesTestHasher(shape, 2);
        ArrayCountingBloomFilter bf2 = createFilter(hasher2, shape);

        // More - Less = OK
        bf.remove(bf2);
        assertTrue(bf.isValid());
        assertCounts(bf, new int[] {0, 1, 0, 1});

        // Less - More = Negative
        assertTrue(bf2.isValid());
        bf2.remove(bf);
        assertFalse("Remove should create negative counts and the filter is invalid", bf2.isValid());

        // The counts are not clipped to zero. They have been left as negative.
        assertCounts(bf2, new int[] {0, -1, 1, -1});
    }

    /**
     * Tests that counts can be added to a new instance.
     *
     * <p>Note: This test ensures the CountingBloomFilter
     * can be created with whatever counts are required for other tests.
     */
    @Test
    public void addTest_NewInstance() {
        for (final int[] counts : new int[][] {
            { /* empty */},
            {0, 0, 1},
            {0, 1, 2},
            {2, 3, 4},
            {66, 77, 0, 99},
            {Integer.MAX_VALUE, 42},
        }) {
            assertCounts(createFromCounts(counts), counts);
        }
    }

    /**
     * Test that add correctly ignores an empty CountingBloomFilter.
     */
    @Test
    public void addTest_Empty() {
        assertCountingOperation(new int[] {5, 2, 1},
                new int[0],
                CountingBloomFilter::add,
                true,
                new int[] {5, 2, 1});
    }

    /**
     * Test that add correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void addTest_Counts() {
        assertCountingOperation(new int[] {5, 2, 1},
                new int[] {0, 6, 4, 1},
                CountingBloomFilter::add,
                true,
                new int[] {5, 8, 5, 1});
    }

    /**
     * Test that add correctly updates the isValid state when a CountingBloomFilter is
     * passed and an integer overflow occurs.
     */
    @Test
    public void addTest_Overflow() {
        assertCountingOperation(new int[] {5, 2, 1},
                new int[] {0, 6, Integer.MAX_VALUE},
                CountingBloomFilter::add,
                false,
                new int[] {5, 8, 1 + Integer.MAX_VALUE});
    }

    /**
     * Test that subtract correctly ignores an empty CountingBloomFilter.
     */
    @Test
    public void subtractTest_Empty() {
        assertCountingOperation(new int[] {5, 2, 1},
                new int[0],
                CountingBloomFilter::subtract,
                true,
                new int[] {5, 2, 1});
    }

    /**
     * Test that subtract correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void subtractTest_Counts() {
        assertCountingOperation(new int[] {5, 9, 1, 1},
                new int[] {0, 2, 1},
                CountingBloomFilter::subtract,
                true,
                new int[] {5, 7, 0, 1});
    }

    /**
     * Test that subtract correctly updates the isValid state when a CountingBloomFilter is
     * passed and the counts become negative.
     */
    @Test
    public void subtractTest_Negative() {
        assertCountingOperation(new int[] {5, 2, 1},
                new int[] {0, 6, 1},
                CountingBloomFilter::subtract,
                false,
                new int[] {5, -4, 0});
    }

    /**
     * Assert a counting operation. Two CountingBloomFilters are created from the
     * two sets of counts. The operation is applied and the counts of the first
     * Bloom filter is checked using the expected counts.
     *
     * <p>Counts are assumed to map to indexes starting from 0.
     *
     * @param counts1 the first set counts
     * @param counts2 the first set counts
     * @param operation the operation
     * @param isValid the expected value for the operation result
     * @param expected the expected counts after the operation
     */
    private void assertCountingOperation(int[] counts1, int[] counts2,
            BiPredicate<ArrayCountingBloomFilter, ArrayCountingBloomFilter> operation,
            boolean isValid, int[] expected) {
        final ArrayCountingBloomFilter bf1 = createFromCounts(counts1);
        final ArrayCountingBloomFilter bf2 = createFromCounts(counts2);
        final boolean result = operation.test(bf1, bf2);
        assertEquals(isValid, result);
        assertEquals(isValid, bf1.isValid());
        assertCounts(bf1, expected);
    }

    /**
     * Tests that the andCardinality calculation executes correctly when using a
     * CountingBloomFilter argument.
     */
    @Test
    public void andCardinalityTest_CountingBloomFilter() {
        assertCardinalityOperation(new int[] {1, 1},
                new int[] {1, 1},
                BloomFilter::andCardinality,
                2);
        assertCardinalityOperation(new int[] {0, 1, 0, 1, 1, 1, 0, 1, 0},
                new int[] {1, 1, 0, 0, 0, 1},
                BloomFilter::andCardinality,
                2);
        assertCardinalityOperation(new int[] {1, 1},
                new int[] {0, 0, 1, 1, 1},
                BloomFilter::andCardinality,
                0);
    }

    /**
     * Tests that the orCardinality calculation executes correctly when using a
     * CountingBloomFilter argument.
     */
    @Test
    public void orCardinalityTest_CountingBloomFilter() {
        assertCardinalityOperation(new int[] {1, 1},
                new int[] {1, 1},
                BloomFilter::orCardinality,
                2);
        assertCardinalityOperation(new int[] {0, 1, 0, 1, 1, 1, 0, 1, 0},
                new int[] {1, 1, 0, 0, 0, 1},
                BloomFilter::orCardinality,
                6);
        assertCardinalityOperation(new int[] {1, 1},
                new int[] {0, 0, 1, 1, 1},
                BloomFilter::orCardinality,
                5);
    }

    /**
     * Tests that the xorCardinality calculation executes correctly when using a
     * CountingBloomFilter argument.
     */
    @Test
    public void xorCardinalityTest_CountingBloomFilter() {
        assertCardinalityOperation(new int[] {1, 1},
                new int[] {1, 1},
                BloomFilter::xorCardinality,
                0);
        assertCardinalityOperation(new int[] {0, 1, 0, 1, 1, 1, 0, 1, 0},
                new int[] {1, 1, 0, 0, 0, 1},
                BloomFilter::xorCardinality,
                4);
        assertCardinalityOperation(new int[] {1, 1},
                new int[] {0, 0, 1, 1, 1},
                BloomFilter::xorCardinality,
                5);
    }

    /**
     * Assert a cardinality operation. Two CountingBloomFilters are created from the
     * two sets of counts. The operation is applied and the counts of the first
     * Bloom filter is checked using the expected counts.
     *
     * <p>Counts are assumed to map to indexes starting from 0.
     *
     * @param counts1 the first set counts
     * @param counts2 the first set counts
     * @param operation the operation
     * @param expected the expected cardinality
     */
    private void assertCardinalityOperation(int[] counts1, int[] counts2,
            ToIntBiFunction<ArrayCountingBloomFilter, ArrayCountingBloomFilter> operation,
            int expected) {
        final ArrayCountingBloomFilter bf1 = createFromCounts(counts1);
        final ArrayCountingBloomFilter bf2 = createFromCounts(counts2);
        assertEquals(expected, operation.applyAsInt(bf1, bf2));
        assertEquals(expected, operation.applyAsInt(bf2, bf1));
    }
}
