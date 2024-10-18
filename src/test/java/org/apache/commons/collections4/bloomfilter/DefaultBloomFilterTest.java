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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.TreeSet;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link BloomFilter}.
 */
public class DefaultBloomFilterTest extends AbstractBloomFilterTest<DefaultBloomFilterTest.AbstractDefaultBloomFilter> {

    abstract static class AbstractDefaultBloomFilter<T extends AbstractDefaultBloomFilter<T>> implements BloomFilter<T> {

        private final Shape shape;
        protected TreeSet<Integer> indices;

        AbstractDefaultBloomFilter(final Shape shape) {
            this.shape = shape;
            this.indices = new TreeSet<>();
        }

        @Override
        public int cardinality() {
            return indices.size();
        }

        private void checkIndicesRange() {
            if (!indices.isEmpty()) {
                if (indices.last() >= shape.getNumberOfBits()) {
                    throw new IllegalArgumentException(
                        String.format("Value in list %s is greater than maximum value (%s)", indices.last(),
                            shape.getNumberOfBits()));
                }
                if (indices.first() < 0) {
                    throw new IllegalArgumentException(
                        String.format("Value in list %s is less than 0", indices.first()));
                }
            }
        }

        @Override
        public void clear() {
            indices.clear();
        }

        @Override
        public boolean contains(final BitMapExtractor bitMapExtractor) {
            return contains(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
        }

        @Override
        public boolean contains(final IndexExtractor indexExtractor) {
            return indexExtractor.processIndices(indices::contains);
        }

        @Override
        public Shape getShape() {
            return shape;
        }

        @Override
        public boolean merge(final BitMapExtractor bitMapExtractor) {
            return merge(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
        }

        @Override
        public boolean merge(final IndexExtractor indexExtractor) {
            final boolean result = indexExtractor.processIndices(x -> {
                indices.add(x);
                return true;
            });
            checkIndicesRange();
            return result;
        }

        @Override
        public boolean processBitMaps(final LongPredicate consumer) {
            return BitMapExtractor.fromIndexExtractor(this, shape.getNumberOfBits()).processBitMaps(consumer);
        }

        @Override
        public boolean processIndices(final IntPredicate consumer) {
            for (final Integer i : indices) {
                if (!consumer.test(i)) {
                    return false;
                }
            }
            return true;
        }
    }

    static class BrokenCardinality extends NonSparseDefaultBloomFilter {

        BrokenCardinality(final Shape shape) {
            super(shape);
        }

        @Override
        public int cardinality() {
            return super.cardinality() + 1;
        }
    }

    /**
     * A default implementation of a non-sparse Bloom filter.
     */
    public static class NonSparseDefaultBloomFilter extends AbstractDefaultBloomFilter {

        public NonSparseDefaultBloomFilter(final Shape shape) {
            super(shape);
        }

        @Override
        public int characteristics() {
            return 0;
        }

        @Override
        public AbstractDefaultBloomFilter copy() {
            final AbstractDefaultBloomFilter result = new NonSparseDefaultBloomFilter(getShape());
            result.indices.addAll(indices);
            return result;
        }
    }

    /**
     * A default implementation of a Sparse bloom filter.
     */
    public static class SparseDefaultBloomFilter extends AbstractDefaultBloomFilter<SparseDefaultBloomFilter> {

        public SparseDefaultBloomFilter(final Shape shape) {
            super(shape);
        }

        @Override
        public int characteristics() {
            return SPARSE;
        }

        @Override
        public SparseDefaultBloomFilter copy() {
            final SparseDefaultBloomFilter result = new SparseDefaultBloomFilter(getShape());
            result.indices.addAll(indices);
            return result;
        }
    }

    @Override
    protected AbstractDefaultBloomFilter createEmptyFilter(final Shape shape) {
        return new SparseDefaultBloomFilter(shape);
    }

    @Test
    public void testDefaultBloomFilterSimpleSpecificMerge() {
        final AbstractDefaultBloomFilter filter = new SparseDefaultBloomFilter(Shape.fromKM(3, 150));
        final Hasher hasher = new IncrementingHasher(0, 1);
        assertTrue(filter.merge(hasher));
        assertEquals(3, filter.cardinality());
    }

    @Test
    public void testDefaultBloomFilterSparseSpecificMerge() {
        final Shape shape = Shape.fromKM(3, 150);
        final AbstractDefaultBloomFilter filter = new SparseDefaultBloomFilter(shape);
        final AbstractDefaultBloomFilter filter2 = createFilter(shape, new IncrementingHasher(0, 1));
        final BloomFilter newFilter = filter.copy();
        newFilter.merge(filter2);
        assertEquals(3, newFilter.cardinality());
    }

    @Test
    public void testEstimateLargeN() {
        final Shape s = Shape.fromKM(1, Integer.MAX_VALUE);
        // create a very large filter with Integer.MAX_VALUE-1 bits set.
        final BloomFilter bf1 = new SimpleBloomFilter(s);
        bf1.merge((BitMapExtractor) predicate -> {
            int limit = Integer.MAX_VALUE - 1;
            while (limit > 64) {
                predicate.test(0xFFFFFFFFFFFFFFFFL);
                limit -= 64;
            }
            long last = 0L;
            for (int i = 0; i < limit; i++) {
                last |= BitMaps.getLongBit(i);
            }
            predicate.test(last);
            return true;
        });
        // the actual result of the calculation is: 46144189292, so the returned value
        // should be Integer.MAX_VALUE.
        assertEquals(Integer.MAX_VALUE, bf1.estimateN());
    }

    @Test
    public void testEstimateNWithBrokenCardinality() {
        // build a filter
        final BloomFilter filter1 = TestingHashers.populateEntireFilter(new BrokenCardinality(getTestShape()));
        assertThrows(IllegalArgumentException.class, () -> filter1.estimateN());
    }

    @Test
    public void testHasherBasedMergeWithDifferingSparseness() {
        final Hasher hasher = new IncrementingHasher(1, 1);

        BloomFilter bf1 = new NonSparseDefaultBloomFilter(getTestShape());
        bf1.merge(hasher);
        assertTrue(BitMapExtractor.fromIndexExtractor(hasher.indices(getTestShape()), getTestShape().getNumberOfBits())
                .processBitMapPairs(bf1, (x, y) -> x == y));

        bf1 = new SparseDefaultBloomFilter(getTestShape());
        bf1.merge(hasher);
        assertTrue(BitMapExtractor.fromIndexExtractor(hasher.indices(getTestShape()), getTestShape().getNumberOfBits())
                .processBitMapPairs(bf1, (x, y) -> x == y));
    }

    @Test
    public void testIntersectionLimit() {
        final Shape s = Shape.fromKM(1, Integer.MAX_VALUE);
        // create a very large filter with Integer.MAX_VALUE-1 bit set.
        final BloomFilter bf1 = new SimpleBloomFilter(s);
        bf1.merge((BitMapExtractor) predicate -> {
            int limit = Integer.MAX_VALUE - 1;
            while (limit > 64) {
                predicate.test(0xFFFFFFFFFFFFFFFFL);
                limit -= 64;
            }
            long last = 0L;
            for (int i = 0; i < limit; i++) {
                last |= BitMaps.getLongBit(i);
            }
            predicate.test(last);
            return true;
        });
        // the actual result of the calculation is: 46144189292
        assertEquals(Integer.MAX_VALUE, bf1.estimateIntersection(bf1));
    }

    @Test
    public void testSparseNonSparseMerging() {
        final BloomFilter bf1 = new SparseDefaultBloomFilter(getTestShape());
        bf1.merge(TestingHashers.FROM1);
        final BloomFilter bf2 = new NonSparseDefaultBloomFilter(getTestShape());
        bf2.merge(TestingHashers.FROM11);

        BloomFilter result = bf1.copy();
        result.merge(bf2);
        assertEquals(27, result.cardinality());

        result = bf2.copy();
        result.merge(bf1);
        assertEquals(27, result.cardinality());
    }
}
