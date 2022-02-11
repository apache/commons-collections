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
     * Gets the number of items that will be hashed by the {@code IndexProducer}.
     * @return The number of items that will be hashed by the {@code IndexProducer}.
     */
    int size();

    /**
     * Returns true if there are no items to be hashed.
     * @return {@code true} if there are no items to be hashed.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * A convenience class for Hasher implementations to filter out duplicate indices.
     *
     * <p><em>If the index is negative the behavior is not defined.</em></p>
     *
     * <p>This is conceptually a unique filter implemented as a {@code Predicate<int>}.</p>
     * @since 4.5
     */
    final class IndexFilter implements IntPredicate {
        private final IndexTracker tracker;
        private final int size;
        private final IntPredicate consumer;

        /**
         * Creates an instance optimized for the specified shape.
         * @param shape The shape that is being generated.
         * @param consumer The consumer to accept the values.
         */
        public static IndexFilter create(Shape shape, IntPredicate consumer) {
            return new IndexFilter( shape, consumer );
        }

        /**
         * Creates an instance optimized for the specified shape.
         * @param shape The shape that is being generated.
         * @param consumer The consumer to accept the values.
         */
        private IndexFilter(Shape shape, IntPredicate consumer) {
            this.size = shape.getNumberOfBits();
            this.consumer = consumer;
            if (BitMap.numberOfBitMaps(shape.getNumberOfBits()) * Long.BYTES < shape.getNumberOfHashFunctions()
                    * Integer.BYTES) {
                this.tracker = new BitMapTracker(shape);
            } else {
                this.tracker = new ArrayTracker(shape);
            }
        }

        /**
         * Creates an instance of Filter with the specified IndexTracker.
         *
         * @param shape The shape that is being generated.
         * @param consumer The consumer to accept the values
         * @param tracker The index tracker to use.
         */
        IndexFilter(Shape shape, IntPredicate consumer, IndexTracker tracker) {
            this.size = shape.getNumberOfBits();
            this.consumer = consumer;
            this.tracker = tracker;
        }

        /**
         * Test if the number should be processed by teh {@code consumer}.
         *
         * <p>If the number has <em>not</em> been seen before it is passed to the {@code consumer} and the result returned.
         * If the number has been seen before the {@code consumer} is not called and {@code true} returned.</p>
         *
         * <p><em>If the input is not in the range [0,size) an IndexOutOfBoundsException exception is thrown.</em></p>
         *
         * @param number the number to check.
         * @return {@code true} if processing should continue, {@code false} otherwise.
         * @see IndexFilter#Filter(int)
         */
        @Override
        public boolean test(int number) {
            if (number < 0) {
                throw new IndexOutOfBoundsException("number may not be less than zero. " + number);
            }
            if (number >= size) {
                throw new IndexOutOfBoundsException(String.format("number too large %d >= %d", number, size));
            }
            return tracker.seen(number) ? true : consumer.test(number);
        }

        /**
         * A functional interface that defines the seen function for the Hasher Filter.
         * @since 4.5
         */
        @FunctionalInterface
        interface IndexTracker {
            /**
             * Returns {@code true} if the number has been seen before, {@code false} otherwise.
             * @param number the number to check
             * @return {@code true} if the number has been seen before, {@code false} otherwise.
             */
            boolean seen(int number);
        }

        /**
         * An IndexTracker implementation that uses an array of integers to track whether or not a
         * number has been seen.  Suitable for Shapes that have few hash functions.
         * @since 4.5
         */
        static class ArrayTracker implements IndexTracker {
            private int[] seenAry;
            private int idx;

            /**
             * Constructs the tracker based on the shape.
             * @param shape the shape to build the tracker for.
             */
            ArrayTracker(Shape shape) {
                seenAry = new int[shape.getNumberOfHashFunctions()];
                idx = 0;
            }

            @Override
            public boolean seen(int number) {
                for (int i = 0; i < idx; i++) {
                    if (seenAry[i] == number) {
                        return true;
                    }
                }
                seenAry[idx++] = number;
                return false;
            }
        }

        /**
         * An IndexTracker implementation that uses an array of bit maps to track whether or not a
         * number has been seen.
         * @since 4.5
         */
        static class BitMapTracker implements IndexTracker {
            private long[] bits;

            /**
             * Constructs a bit map based tracker for the specified shape.
             * @param shape The shape that is being generated.
             */
            BitMapTracker(Shape shape) {
                bits = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
            }

            @Override
            public boolean seen(int number) {
                boolean retval = BitMap.contains(bits, number);
                BitMap.set(bits, number);
                return retval;
            }
        }
    }

}
