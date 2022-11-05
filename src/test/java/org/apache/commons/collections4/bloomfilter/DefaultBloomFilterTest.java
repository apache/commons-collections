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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.TreeSet;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link BloomFilter}.
 */
public class DefaultBloomFilterTest extends AbstractBloomFilterTest<DefaultBloomFilterTest.AbstractDefaultBloomFilter> {
    @Override
    protected AbstractDefaultBloomFilter createEmptyFilter(final Shape shape) {
        return new SparseDefaultBloomFilter(shape);
    }

    @Test
    public void testDefaultBloomFilterSimpleSpecificMerge() {
        AbstractDefaultBloomFilter filter = new SparseDefaultBloomFilter(Shape.fromKM(3, 150));
        Hasher hasher = new IncrementingHasher(0, 1);
        assertTrue(filter.merge(hasher));
        assertEquals(3, filter.cardinality());
    }

    @Test
    public void testDefaultBloomFilterSparseSpecificMerge() {
        Shape shape = Shape.fromKM(3, 150);
        AbstractDefaultBloomFilter filter = new SparseDefaultBloomFilter(shape);
        AbstractDefaultBloomFilter filter2 = createFilter(shape, new IncrementingHasher(0, 1));
        BloomFilter newFilter = filter.copy();
        newFilter.merge(filter2);
        assertEquals(3, newFilter.cardinality());
    }

    @Test
    public void testHasherBasedMergeWithDifferingSparseness() {
        Hasher hasher = new IncrementingHasher(1, 1);

        BloomFilter bf1 = new NonSparseDefaultBloomFilter(getTestShape());
        bf1.merge(hasher);
        assertTrue(BitMapProducer.fromIndexProducer(hasher.indices(getTestShape()), getTestShape().getNumberOfBits())
                .forEachBitMapPair(bf1, (x, y) -> x == y));

        bf1 = new SparseDefaultBloomFilter(getTestShape());
        bf1.merge(hasher);
        assertTrue(BitMapProducer.fromIndexProducer(hasher.indices(getTestShape()), getTestShape().getNumberOfBits())
                .forEachBitMapPair(bf1, (x, y) -> x == y));
    }

    abstract static class AbstractDefaultBloomFilter implements BloomFilter {
        private Shape shape;
        protected TreeSet<Integer> indices;

        AbstractDefaultBloomFilter(Shape shape) {
            this.shape = shape;
            this.indices = new TreeSet<>();
        }

        @Override
        public void clear() {
            indices.clear();
        }

        @Override
        public boolean forEachIndex(IntPredicate consumer) {
            for (Integer i : indices) {
                if (!consumer.test(i)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean forEachBitMap(LongPredicate consumer) {
            return BitMapProducer.fromIndexProducer(this, shape.getNumberOfBits()).forEachBitMap(consumer);
        }

        @Override
        public Shape getShape() {
            return shape;
        }

        @Override
        public boolean contains(IndexProducer indexProducer) {
            return indexProducer.forEachIndex((i) -> indices.contains(i));
        }

        @Override
        public boolean contains(BitMapProducer bitMapProducer) {
            return contains(IndexProducer.fromBitMapProducer(bitMapProducer));
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
        public boolean merge(IndexProducer indexProducer) {
            boolean result = indexProducer.forEachIndex(x -> {
                indices.add(x);
                return true;
            });
            checkIndicesRange();
            return result;
        }

        @Override
        public boolean merge(BitMapProducer bitMapProducer) {
            return merge(IndexProducer.fromBitMapProducer(bitMapProducer));
        }

        @Override
        public int cardinality() {
            return indices.size();
        }
    }

    static class SparseDefaultBloomFilter extends AbstractDefaultBloomFilter {

        SparseDefaultBloomFilter(Shape shape) {
            super(shape);
        }

        @Override
        public int characteristics() {
            return SPARSE;
        }

        @Override
        public AbstractDefaultBloomFilter copy() {
            AbstractDefaultBloomFilter result = new SparseDefaultBloomFilter(getShape());
            result.indices.addAll(indices);
            return result;
        }
    }

    static class NonSparseDefaultBloomFilter extends AbstractDefaultBloomFilter {

        NonSparseDefaultBloomFilter(Shape shape) {
            super(shape);
        }

        @Override
        public int characteristics() {
            return 0;
        }

        @Override
        public AbstractDefaultBloomFilter copy() {
            AbstractDefaultBloomFilter result = new SparseDefaultBloomFilter(getShape());
            result.indices.addAll(indices);
            return result;
        }
    }
}
