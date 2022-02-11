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
 * A convenience class for Hasher implementations to filter out duplicate indices.
 *
 * <p><em>If the index is negative the behavior is not defined.</em></p>
 *
 * <p>This is conceptually a unique filter implemented as a {@code Predicate<int>}.</p>
 * @since 4.5
 */
public final class Filter implements IntPredicate {
    private IndexTracker tracker;
    private int size;
    private IntPredicate consumer;

    /**
     * Creates an instance optimized for the specified shape.
     * @param shape The shape that is being generated.
     * @param consumer The consumer to accept the values.
     */
    public Filter(Shape shape, IntPredicate consumer) {
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
    public Filter(Shape shape, IntPredicate consumer, IndexTracker tracker) {
        this.size = shape.getNumberOfBits();
        this.consumer = consumer;
        this.tracker = tracker;
    }

    /**
     * Test if the number has not been seen.
     *
     * <p>The first time a number is tested the method returns {@code true} and returns
     * {@code false} for every time after that.</p>
     *
     * <p><em>If the input is not in the range [0,size) an IndexOutOfBoundsException exception is thrown.</em></p>
     *
     * @param number the number to check.
     * @return {@code true} if the number has not been seen, {@code false} otherwise.
     * @see Filter#Filter(int)
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
