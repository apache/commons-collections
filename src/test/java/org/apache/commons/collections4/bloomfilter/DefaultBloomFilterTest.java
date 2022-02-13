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

    @Override
    protected AbstractDefaultBloomFilter createFilter(final Shape shape, final Hasher hasher) {
        return new SparseDefaultBloomFilter(shape, hasher);
    }

    @Override
    protected AbstractDefaultBloomFilter createFilter(final Shape shape, final BitMapProducer producer) {
        return new SparseDefaultBloomFilter(shape, producer);
    }

    @Override
    protected AbstractDefaultBloomFilter createFilter(final Shape shape, final IndexProducer producer) {
        return new SparseDefaultBloomFilter(shape, producer);
    }

    @Test
    public void testDefaultBloomFilterSimpleSpecificMergeInPlace() {
        AbstractDefaultBloomFilter filter = new SparseDefaultBloomFilter(Shape.fromKM(3, 150));
        Hasher hasher = new SimpleHasher(0, 1);
        assertTrue(filter.mergeInPlace(hasher));
        assertEquals(3, filter.cardinality());
    }

    @Test
    public void testDefaultBloomFilterSparseSpecificMergeInPlace() {
        AbstractDefaultBloomFilter filter = new SparseDefaultBloomFilter(Shape.fromKM(3, 150));
        Hasher hasher = new SimpleHasher(0, 1);
        BloomFilter newFilter = filter.merge(hasher);
        assertEquals(3, newFilter.cardinality());
    }

    @Test
    public void testDefaultBloomFilterSparseSpecificMerge() {
        Shape shape = Shape.fromKM(3, 150);
        AbstractDefaultBloomFilter filter = new SparseDefaultBloomFilter(shape);
        AbstractDefaultBloomFilter filter2 = new SparseDefaultBloomFilter(shape, new SimpleHasher(0, 1));
        BloomFilter newFilter = filter.merge(filter2);
        assertEquals(3, newFilter.cardinality());
    }

    @Test
    public void testMergeInPlaceWithDifferingSparseness() {
        int[] values = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
        Hasher hasher = new SimpleHasher(1, 1);

        BloomFilter bf1 = new NonSparseDefaultBloomFilter(getTestShape());
        bf1.mergeInPlace(hasher);
        LongPredicate lp = bf1.makePredicate((x, y) -> x == y);
        assertTrue(BitMapProducer.fromIndexProducer(hasher.indices(getTestShape()), getTestShape().getNumberOfBits())
                .forEachBitMap(lp));

        bf1 = new SparseDefaultBloomFilter(getTestShape());
        bf1.mergeInPlace(hasher);
        lp = bf1.makePredicate((x, y) -> x == y);
        assertTrue(BitMapProducer.fromIndexProducer(hasher.indices(getTestShape()), getTestShape().getNumberOfBits())
                .forEachBitMap(lp));

    }

    static abstract class AbstractDefaultBloomFilter implements BloomFilter {
        private Shape shape;
        protected TreeSet<Integer> indices;

        AbstractDefaultBloomFilter(Shape shape) {
            this.shape = shape;
            this.indices = new TreeSet<>();
        }

        AbstractDefaultBloomFilter(Shape shape, Hasher hasher) {
            this(shape, hasher.indices(shape));
        }

        AbstractDefaultBloomFilter(Shape shape, BitMapProducer producer) {
            this(shape, IndexProducer.fromBitMapProducer(
                    new CheckBitMapCount(producer, BitMap.numberOfBitMaps(shape.getNumberOfBits()))));
        }

        AbstractDefaultBloomFilter(Shape shape, IndexProducer producer) {
            this(shape);
            producer.forEachIndex((i) -> {
                indices.add(i);
                return true;
            });
            if (this.indices.floor(-1) != null || this.indices.ceiling(shape.getNumberOfBits()) != null) {
                throw new IllegalArgumentException(
                        String.format("Filter only accepts values in the [0,%d) range", shape.getNumberOfBits()));
            }
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

        @Override
        public boolean mergeInPlace(BloomFilter other) {
            other.forEachIndex((i) -> {
                indices.add(i);
                return true;
            });
            return true;
        }

        @Override
        public int cardinality() {
            return indices.size();
        }
    }

    static class SparseDefaultBloomFilter extends AbstractDefaultBloomFilter {

        public SparseDefaultBloomFilter(Shape shape, BitMapProducer producer) {
            super(shape, producer);
        }

        public SparseDefaultBloomFilter(Shape shape, Hasher hasher) {
            super(shape, hasher);
        }

        public SparseDefaultBloomFilter(Shape shape, IndexProducer producer) {
            super(shape, producer);
        }

        public SparseDefaultBloomFilter(Shape shape) {
            super(shape);
        }

        @Override
        public boolean isSparse() {
            return true;
        }

        @Override
        public AbstractDefaultBloomFilter copy() {
            AbstractDefaultBloomFilter result = new SparseDefaultBloomFilter(getShape());
            result.indices.addAll(indices);
            return result;
        }

    }

    static class NonSparseDefaultBloomFilter extends AbstractDefaultBloomFilter {

        public NonSparseDefaultBloomFilter(Shape shape, BitMapProducer producer) {
            super(shape, producer);
        }

        public NonSparseDefaultBloomFilter(Shape shape, Hasher hasher) {
            super(shape, hasher);
        }

        public NonSparseDefaultBloomFilter(Shape shape, IndexProducer producer) {
            super(shape, producer);
        }

        public NonSparseDefaultBloomFilter(Shape shape) {
            super(shape);
        }

        @Override
        public boolean isSparse() {
            return false;
        }

        @Override
        public AbstractDefaultBloomFilter copy() {
            AbstractDefaultBloomFilter result = new SparseDefaultBloomFilter(getShape());
            result.indices.addAll(indices);
            return result;
        }

    }

}
