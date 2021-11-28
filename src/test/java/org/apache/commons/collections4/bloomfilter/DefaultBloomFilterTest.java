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

import java.util.TreeSet;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import org.apache.commons.collections4.bloomfilter.exceptions.NoMatchException;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

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

    public class DefaultBloomFilter implements BloomFilter {
        private Shape shape;
        private TreeSet<Integer> indices;

        DefaultBloomFilter(Shape shape) {
            this.shape = shape;
            this.indices = new TreeSet<Integer>();
        }

        DefaultBloomFilter(Shape shape, Hasher hasher) {
            this(shape);
            hasher.indices(shape).forEachIndex(indices::add);
        }

        @Override
        public void forEachIndex(IntConsumer consumer) {
            indices.forEach(i -> consumer.accept(i.intValue()));
        }

        @Override
        public void forEachBitMap(LongConsumer consumer) {
            BitMapProducer.fromIndexProducer(this, shape).forEachBitMap(consumer);
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
            try {
                indexProducer.forEachIndex(i -> {
                    if (!indices.contains(i)) {
                        throw new NoMatchException();
                    }
                });
                return true;
            } catch (NoMatchException e) {
                return false;
            }
        }

        @Override
        public boolean contains(BitMapProducer bitMapProducer) {
            return contains(IndexProducer.fromBitMapProducer(bitMapProducer));
        }

        @Override
        public boolean mergeInPlace(BloomFilter other) {
            other.forEachIndex(indices::add);
            return true;
        }

        @Override
        public int cardinality() {
            return indices.size();
        }

    }
}
