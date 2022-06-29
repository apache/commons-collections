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

import java.util.function.IntPredicate;

/**
 * A Hasher creates IndexProducer based on the hash implementation and the
 * provided Shape.
 *
 * @since 4.5
 */
public interface Hasher {

    /**
     * Creates an IndexProducer for this hasher based on the Shape.
     *
     * <p>The @{code IndexProducer} will create indices within the range defined by the number of bits in
     * the shape. The total number of indices will respect the number of hash functions per item
     * defined by the shape. However the count of indices may not be a multiple of the number of
     * hash functions if the implementation has removed duplicates.</p>
     *
     * <p>This IndexProducer must be deterministic in that it must return the same indices for the
     * same Shape.</p>
     *
     * <p>No guarantee is made as to order of indices.</p>
     * <p>Duplicates indices for a single item may be produced.</p>
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the iterator of integers
     */
    IndexProducer indices(Shape shape);

    /**
     * Creates an IndexProducer of unique indices for this hasher based on the Shape.
     *
     * <p>This is like the `indices(Shape)` method except that it adds the guarantee that no
     * duplicate values will be returned</p>
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the iterator of integers
     */
    IndexProducer uniqueIndices(Shape shape);

    /**
     * A convenience class for Hasher implementations to filter out duplicate indices.
     *
     * <p><em>If the index is negative the behavior is not defined.</em></p>
     *
     * <p>This is conceptually a unique filter implemented as a {@code IntPredicate}.</p>
     * @since 4.5
     */
    final class IndexFilter implements IntPredicate {
        private final IntPredicate tracker;
        private final int size;
        private final IntPredicate consumer;

        /**
         * Creates an instance optimized for the specified shape.
         * @param shape The shape that is being generated.
         * @param consumer The consumer to accept the values.
         * @return an IndexFilter optimized for the specified shape.
         */
        public static IndexFilter create(Shape shape, IntPredicate consumer) {
            return new IndexFilter(shape, consumer);
        }

        /**
         * Creates an instance optimized for the specified shape.
         * @param shape The shape that is being generated.
         * @param consumer The consumer to accept the values.
         */
        private IndexFilter(Shape shape, IntPredicate consumer) {
            this.size = shape.getNumberOfBits();
            this.consumer = consumer;
            if (BitMap.numberOfBitMaps(shape.getNumberOfBits()) * Long.BYTES < (long) shape.getNumberOfHashFunctions()
                    * Integer.BYTES) {
                this.tracker = new BitMapTracker(shape);
            } else {
                this.tracker = new ArrayTracker(shape);
            }
        }

        /**
         * Test if the number should be processed by the {@code consumer}.
         *
         * <p>If the number has <em>not</em> been seen before it is passed to the {@code consumer} and the result returned.
         * If the number has been seen before the {@code consumer} is not called and {@code true} returned.</p>
         *
         * <p><em>If the input is not in the range [0,size) an IndexOutOfBoundsException exception is thrown.</em></p>
         *
         * @param number the number to check.
         * @return {@code true} if processing should continue, {@code false} otherwise.
         */
        @Override
        public boolean test(int number) {
            if (number >= size) {
                throw new IndexOutOfBoundsException(String.format("number too large %d >= %d", number, size));
            }
            return tracker.test(number) ? consumer.test(number) : true;
        }
    }
}
