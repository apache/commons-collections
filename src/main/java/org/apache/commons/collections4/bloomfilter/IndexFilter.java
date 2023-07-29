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
 * <p>This is conceptually a unique filter implemented as an {@code IntPredicate}.</p>
 * @since 4.5
 */
public final class IndexFilter {

    // do not instantiate.
    private IndexFilter() {}

    /**
     * Creates an instance optimized for the specified shape.
     * @param shape The shape that is being generated.
     * @param consumer The consumer to accept the values.
     * @return an IndexFilter optimized for the specified shape.
     */
    public static IntPredicate create(final Shape shape, final IntPredicate consumer) {
        int size = shape.getNumberOfBits();
        IntPredicate result = number -> {
            if (number >= size) {
                throw new IndexOutOfBoundsException(String.format("number too large %d >= %d", number, size));
            }
            return true;
        };
        if (BitMap.numberOfBitMaps(shape.getNumberOfBits()) * Long.BYTES < (long) shape.getNumberOfHashFunctions()
                * Integer.BYTES) {
            result = result.and(new BitMapTracker(shape).negate());
        } else {
            result = result.and(new ArrayTracker(shape).negate());
        }
        return result.or(consumer);
    }

    /**
     * An IndexTracker implementation that uses an array of integers to track whether or not a
     * number has been seen. Suitable for Shapes that have few hash functions.
     * @since 4.5
     */
    static class ArrayTracker implements IntPredicate {
        private final int[] seen;
        private int populated;

        /**
         * Constructs the tracker based on the shape.
         * @param shape the shape to build the tracker for.
         */
        ArrayTracker(final Shape shape) {
            seen = new int[shape.getNumberOfHashFunctions()];
        }

        @Override
        public boolean test(final int number) {
            if (number < 0) {
                throw new IndexOutOfBoundsException("number may not be less than zero. " + number);
            }
            for (int i = 0; i < populated; i++) {
                if (seen[i] == number) {
                    return false;
                }
            }
            seen[populated++] = number;
            return true;
        }
    }

    /**
     * An IndexTracker implementation that uses an array of bit maps to track whether or not a
     * number has been seen.
     * @since 4.5
     */
    static class BitMapTracker implements IntPredicate {
        private final long[] bits;

        /**
         * Constructs a bit map based tracker for the specified shape.
         * @param shape The shape that is being generated.
         */
        BitMapTracker(final Shape shape) {
            bits = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
        }

        @Override
        public boolean test(final int number) {
            final boolean retval = !BitMap.contains(bits, number);
            BitMap.set(bits, number);
            return retval;
        }
    }
}
