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

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.SimpleHasher;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link BloomFilter}.
 */
public class DefaultBloomFilterTest extends AbstractBloomFilterTest<DefaultBloomFilterTest.DefaultBloomFilter> {
    @Override
    protected DefaultBloomFilter createEmptyFilter(final Shape shape) {
        return new DefaultBloomFilter(shape);
    }

    @Override
    protected DefaultBloomFilter createFilter(final Shape shape, final Hasher hasher) {
        return new DefaultBloomFilter(shape, hasher);
    }

    @Test
    public void testDefaultBloomFilterSimpleSpecificMergeInPlace() {
        DefaultBloomFilter filter = new DefaultBloomFilter(Shape.fromKM(3, 150));
        Hasher hasher = new SimpleHasher(0, 1);
        assertTrue(filter.mergeInPlace(hasher));
        assertEquals(3, filter.cardinality());
    }

    @Test
    public void testDefaultBloomFilterSparseSpecificMergeInPlace() {
        DefaultBloomFilter filter = new DefaultBloomFilter(Shape.fromKM(3, 150));
        Hasher hasher = new SimpleHasher(0, 1);
        BloomFilter newFilter = filter.merge(hasher);
        assertTrue(newFilter instanceof SparseBloomFilter);
        assertEquals(3, newFilter.cardinality());
    }

    @Test
    public void testDefaultBloomFilterSparseSpecificMerge() {
        Shape shape = Shape.fromKM(3, 150);
        DefaultBloomFilter filter = new DefaultBloomFilter(shape);
        DefaultBloomFilter filter2 = new DefaultBloomFilter(shape, new SimpleHasher(0, 1));
        BloomFilter newFilter = filter.merge(filter2);
        assertTrue(newFilter instanceof SparseBloomFilter);
        assertEquals(3, newFilter.cardinality());
    }

    public class DefaultBloomFilter implements BloomFilter {
        private Shape shape;
        private TreeSet<Integer> indices;

        DefaultBloomFilter(Shape shape) {
            this.shape = shape;
            this.indices = new TreeSet<Integer>();
        }

        DefaultBloomFilter(Shape shape, Hasher hasher) {
            this(shape);
            hasher.indices(shape).forEachIndex((i) -> {
                indices.add(i);
                return true;
            });
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
        public boolean isSparse() {
            return true;
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
}
