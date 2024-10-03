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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ArrayCountingBloomFilter}.
 */
public abstract class AbstractCountingBloomFilterTest<T extends CountingBloomFilter>
        extends AbstractBloomFilterTest<T> {

    private static final int[] from1Counts = {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};

    private static final int[] bigHashCounts = {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        1, 0};

    private static final long bigHashValue = 0xffffffeL;

    /**
     * Assert the counts match the expected values. Values are for indices starting
     * at 0. Assert the cardinality equals the number of non-zero counts.
     *
     * @param bf the bloom filter
     * @param expected the expected counts
     */
    private static void assertCounts(final CountingBloomFilter bf, final int[] expected) {
        final Map<Integer, Integer> m = new HashMap<>();
        bf.processCells((i, c) -> {
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

    private void assertCell3(final CountingBloomFilter bf, final int value) {
        bf.processCells((k, v) -> {
            if (k == 3) {
                assertEquals(value, v, "Mismatch at position 3");
            } else {
                assertEquals(0, v, "Mismatch at position " + k);
            }
            return true;
        });
    }

    protected final CellExtractor getMaximumValueExtractor(final int maxValue) {
        return consumer -> {
            for (int i = 1; i < 18; i++) {
                if (!consumer.test(i, maxValue)) {
                    return false;
                }
            }
            return true;
        };
    }

    @Test
    public void mergeIncrementsAllCellsTest() {
        final CountingBloomFilter f1 = createEmptyFilter(Shape.fromKM(1, 10));
        final CountingBloomFilter f2 = f1.copy();
        final CountingBloomFilter f3 = f1.copy();
        // index extractor produces 3 two times.
        final IndexExtractor ip = p -> {
            p.test(3);
            p.test(3);
            return true;
        };
        // The merge should increment cell 3 by 1
        f1.merge(ip);
        assertCell3(f1, 1);

        // The add should increment cells 3 by 2
        f2.add(CellExtractor.from(ip));
        assertCell3(f2, 2);
    }

    @Test
    public void removeDecrementsAllCellsTest() {
        final CountingBloomFilter f1 = createEmptyFilter(Shape.fromKM(1, 10));
        final CellExtractor cp = p -> {
            p.test(3, 3);
            return true;
        };
        f1.add(cp);
        final CountingBloomFilter f2 = f1.copy();
        final CountingBloomFilter f3 = f1.copy();
        // index extractor produces 3 two times.
        final IndexExtractor ip = p -> {
            p.test(3);
            p.test(3);
            return true;
        };
        // The merge should decrement cell 3 by 1
        f1.remove(ip);
        assertCell3(f1, 2);

        // The add should decrement cells 3 by 2
        f2.subtract(CellExtractor.from(ip));
        assertCell3(f2, 1);

        // This merge will decrement by 1 as the round-trip makes the indices unique
        f3.remove(IndexExtractor.fromIndexArray(ip.asIndexArray()));
        assertCell3(f3, 2);
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public void testAdd() {
        final CountingBloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
        assertTrue(bf1.add(createFilter(getTestShape(), TestingHashers.FROM11)), "Add should work");
        assertTrue(bf1.contains(TestingHashers.FROM1), "Should contain");
        assertTrue(bf1.contains(TestingHashers.FROM11), "Should contain");
        assertCounts(bf1, bigHashCounts);

        // test overflow

        final CountingBloomFilter bf2 = createEmptyFilter(getTestShape());
        assertTrue(bf2.add(getMaximumValueExtractor(bf2.getMaxCell())), "Should add to empty");
        assertTrue(bf2.isValid(), "Should be valid");

        assertFalse(bf2.add(createFilter(getTestShape(), TestingHashers.FROM1)), "Should not add");
        assertFalse(bf2.isValid(), "Should not be valid");
    }

    @Test
    public final void testCountingBloomFilterSpecificContains() {
        final BloomFilter bf = new SimpleBloomFilter(getTestShape());
        bf.merge(TestingHashers.FROM1);
        final CountingBloomFilter bf2 = TestingHashers.populateFromHashersFrom1AndFrom11(createEmptyFilter(getTestShape()));

        assertTrue(bf.contains(bf), "BF Should contain itself");
        assertTrue(bf2.contains(bf2), "BF2 Should contain itself");
        assertFalse(bf.contains(bf2), "BF should not contain BF2");
        assertTrue(bf2.contains(bf), "BF2 should contain BF");
        final BitMapExtractor extractor = bf2;
        assertTrue(bf2.contains(extractor), "BF2 should contain BF bitMapExtractor");
    }

    /**
     * Tests that counts are correct when a hasher with duplicates is used in the
     * constructor.
     */
    @Test
    public final void testCountingSpecificConstructor() {
        // verify hasher duplicates are counted.
        // bit hasher has duplicates for 11, 12,13,14,15,16, and 17
        final CountingBloomFilter bf = createFilter(getTestShape(), TestingHashers.FROM1);
        bf.add(CellExtractor.from(TestingHashers.FROM11.indices(getTestShape())));

        final long[] lb = bf.asBitMapArray();
        assertEquals(2, lb.length);
        assertEquals(bigHashValue, lb[0]);

        assertCounts(bf, bigHashCounts);
    }

    /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
    @Test
    public final void testCountingSpecificMerge() {
        final BloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);

        final BloomFilter bf2 = new SimpleBloomFilter(getTestShape());
        bf2.merge(TestingHashers.FROM11);

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
        assertTrue(bf5.add(getMaximumValueExtractor(bf5.getMaxCell())), "Should add to empty");
        assertTrue(bf5.isValid(), "Should be valid");

        final CountingBloomFilter bf6 = bf5.copy();
        final BloomFilter bf7 = new SimpleBloomFilter(getTestShape());
        bf7.merge(TestingHashers.FROM1);
        bf6.merge(bf7);
        assertFalse(bf6.isValid(), "Should not be valid");
    }

    @Test
    public void testExcludesDuplicates() {

        // create a hasher that produces duplicates with the specified shape.
        // this setup produces 5, 17, 29, 41, 53, 65 two times
        final Shape shape = Shape.fromKM(12, 72);
        final Hasher hasher = new IncrementingHasher(5, 12);

        CountingBloomFilter bf1 = createFilter(shape, hasher);
        assertEquals(6, bf1.cardinality());
        bf1.processCells((x, y) -> {
            assertEquals(1, y, "Hasher in constructor results in value not equal to 1");
            return true;
        });

        bf1 = createEmptyFilter(shape);
        bf1.merge(hasher);
        assertEquals(6, bf1.cardinality());
        bf1.processCells((x, y) -> {
            assertEquals(1, y, "Hasher in merge results in value not equal to 1");
            return true;
        });

        bf1 = createEmptyFilter(shape);
        bf1.merge(hasher);
        bf1.remove(hasher);
        assertEquals(0, bf1.cardinality());
        assertTrue(bf1.processCells((x, y) -> false), "Hasher in removes results in value not equal to 0");
    }

    @Test
    public void testGetMaxInsert() {
        final CountingBloomFilter bf = createEmptyFilter(getTestShape());
        verifyMaxInsert(bf, 0, 0);
        bf.merge(TestingHashers.FROM1);
        verifyMaxInsert(bf, 1, 0);
        bf.merge(TestingHashers.FROM1);
        verifyMaxInsert(bf, 2, 0);
        bf.merge(TestingHashers.FROM11);
        verifyMaxInsert(bf, 2, 1);
        bf.remove(TestingHashers.FROM1);
        verifyMaxInsert(bf, 1, 1);
        // verify remove false positive works
        // Incrementing hasher 5,1 spans the single count cells for both FROM1 and FROM11
        assertEquals(1, bf.getMaxInsert(new IncrementingHasher(5, 1)));
        bf.remove(new IncrementingHasher(5, 1));
        verifyMaxInsert(bf, 0, 0);
        assertEquals(0, bf.getMaxInsert(new IncrementingHasher(5, 1)));
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public final void testRemove() {
        final BloomFilter simple = new SimpleBloomFilter(getTestShape());
        simple.merge(TestingHashers.FROM11);

        final CountingBloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
        bf1.add(CellExtractor.from(TestingHashers.FROM11.indices(getTestShape())));

        assertTrue(bf1.remove(simple), "Remove should work");
        assertFalse(bf1.contains(TestingHashers.FROM11), "Should not contain");
        assertTrue(bf1.contains(TestingHashers.FROM1), "Should contain");

        assertCounts(bf1, from1Counts);

        // with hasher
        final CountingBloomFilter bf2 = createFilter(getTestShape(), TestingHashers.FROM1);
        bf2.add(CellExtractor.from(TestingHashers.FROM11.indices(getTestShape())));

        assertTrue(bf2.remove(TestingHashers.FROM11), "Remove should work");
        assertFalse(bf2.contains(TestingHashers.FROM11), "Should not contain");
        assertTrue(bf2.contains(TestingHashers.FROM1), "Should contain");

        assertCounts(bf2, from1Counts);

        // test underflow
        final CountingBloomFilter bf3 = createFilter(getTestShape(), TestingHashers.FROM1);
        assertFalse(bf3.remove(simple), "Subtract should not work");
        assertFalse(bf3.isValid(), "isValid should return false");
        assertFalse(bf3.contains(TestingHashers.FROM1), "Should not contain");
        assertFalse(bf3.contains(simple), "Should not contain");

        assertCounts(bf3, new int[] {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

        // with IndexExtractor
        final IndexExtractor ip = TestingHashers.FROM11.indices(getTestShape());

        final CountingBloomFilter bf4 = createFilter(getTestShape(), TestingHashers.FROM1);
        bf4.add(CellExtractor.from(TestingHashers.FROM11.indices(getTestShape())));

        assertTrue(bf4.remove(ip), "Remove should work");
        assertFalse(bf4.contains(TestingHashers.FROM11), "Should not contain");
        assertTrue(bf4.contains(TestingHashers.FROM1), "Should contain");

        assertCounts(bf4, from1Counts);

        // with BitMapExtractor
        final BitMapExtractor bmp = BitMapExtractor.fromIndexExtractor(ip, getTestShape().getNumberOfBits());
        final CountingBloomFilter bf5 = createFilter(getTestShape(), TestingHashers.FROM1);
        bf5.add(CellExtractor.from(TestingHashers.FROM11.indices(getTestShape())));

        assertTrue(bf5.remove(bmp), "Remove should work");
        assertFalse(bf5.contains(TestingHashers.FROM11), "Should not contain");
        assertTrue(bf5.contains(TestingHashers.FROM1), "Should contain");

        assertCounts(bf5, from1Counts);

        // test extractor errors
        final IndexExtractor ip2 = IndexExtractor.fromIndexArray(1, 2, getTestShape().getNumberOfBits());
        final CountingBloomFilter bf6 = createFilter(getTestShape(), TestingHashers.FROM1);
        assertThrows(IllegalArgumentException.class, () -> bf6.remove(ip2));

        final CountingBloomFilter bf7 = createFilter(getTestShape(), TestingHashers.FROM1);
        final BitMapExtractor bmp2 = BitMapExtractor.fromIndexExtractor(ip2, getTestShape().getNumberOfBits());
        assertThrows(IllegalArgumentException.class, () -> bf7.remove(bmp2));
        assertThrows(IllegalArgumentException.class, () -> bf7.remove(new BadHasher(-1)));
        assertThrows(IllegalArgumentException.class, () -> bf7.remove(new BadHasher(getTestShape().getNumberOfBits())));
    }

    /**
     * Tests that merge correctly updates the counts when a CountingBloomFilter is
     * passed.
     */
    @Test
    public final void testSubtract() {
        final CountingBloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
        bf1.add(CellExtractor.from(TestingHashers.FROM11.indices(getTestShape())));

        final CountingBloomFilter bf2 = createFilter(getTestShape(), TestingHashers.FROM11);

        assertTrue(bf1.subtract(bf2), "Subtract should work");
        assertFalse(bf1.contains(TestingHashers.populateFromHashersFrom1AndFrom11(new SimpleBloomFilter(getTestShape()))), "Should not contain bitHasher");
        assertTrue(bf1.contains(TestingHashers.FROM1), "Should contain TestingHashers.from1");

        assertCounts(bf1, from1Counts);

        // test underflow
        final CountingBloomFilter bf3 = createFilter(getTestShape(), TestingHashers.FROM1);

        final CountingBloomFilter bf4 = createFilter(getTestShape(), TestingHashers.FROM11);

        assertFalse(bf3.subtract(bf4), "Subtract should not work");
        assertFalse(bf3.isValid(), "isValid should return false");
        assertFalse(bf3.contains(TestingHashers.FROM1), "Should not contain");
        assertFalse(bf3.contains(bf4), "Should not contain");

        assertCounts(bf3, new int[] {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0});

        assertThrows(IllegalArgumentException.class, () -> bf3.remove(new BadHasher(-1)));
        assertThrows(IllegalArgumentException.class, () -> bf3.remove(new BadHasher(getTestShape().getNumberOfBits())));
    }

    private void verifyMaxInsert(final CountingBloomFilter bf, final int from1, final int from11) {
        final BloomFilter bfFrom0 = new DefaultBloomFilterTest.SparseDefaultBloomFilter(getTestShape());
        bfFrom0.merge(new IncrementingHasher(0, 1));
        final BloomFilter bfFrom1 = new DefaultBloomFilterTest.SparseDefaultBloomFilter(getTestShape());
        bfFrom1.merge(TestingHashers.FROM1);
        final BloomFilter bfFrom11 = new DefaultBloomFilterTest.SparseDefaultBloomFilter(getTestShape());
        bfFrom11.merge(TestingHashers.FROM11);

        assertEquals(0, bf.getMaxInsert(new IncrementingHasher(0, 1)));
        assertEquals(0, bf.getMaxInsert(bfFrom0));
        assertEquals(0, bf.getMaxInsert((BitMapExtractor) bfFrom0));
        assertEquals(0, bf.getMaxInsert((IndexExtractor) bfFrom0));

        assertEquals(from1, bf.getMaxInsert(TestingHashers.FROM1));
        assertEquals(from1, bf.getMaxInsert(bfFrom1));
        assertEquals(from1, bf.getMaxInsert((BitMapExtractor) bfFrom1));
        assertEquals(from1, bf.getMaxInsert((IndexExtractor) bfFrom1));

        assertEquals(from11, bf.getMaxInsert(TestingHashers.FROM11));
        assertEquals(from11, bf.getMaxInsert(bfFrom11));
        assertEquals(from11, bf.getMaxInsert((BitMapExtractor) bfFrom11));
        assertEquals(from11, bf.getMaxInsert((IndexExtractor) bfFrom11));
    }
}
