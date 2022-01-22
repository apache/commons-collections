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

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

/**
 * Produces BitMap longs for a Bloom filter.
 *
 * Each bit map is a little-endian long value representing a block of bits of this filter.
 *
 * <p>The returned array will have length {@code ceil(m / 64)} where {@code m} is the
 * number of bits in the filter and {@code ceil} is the ceiling function.
 * Bits 0-63 are in the first long. A value of 1 at a bit position indicates the bit
 * index is enabled.
 *
 * The producer may stop at the last non zero BitMap or may produce zero value bit maps to the limit determined by
 * a shape.
 *
 * @since 4.5
 */
public interface BitMapProducer {

    /**
     * Each BitMap is passed to the predicate in order.  The predicate is applied to each
     * bitmap value, if the predicate returns {@code false} the execution is stopped, {@code false}
     * is returned, and no further bitmaps are processed.
     *
     * <p>Any exceptions thrown by the action are relayed to the caller.</p>
     *
     * @param predicate the function to execute
     * @return {@code true} if all bitmaps returned {@code true}, {@code false} otherwise.
     * @throws NullPointerException if the specified consumer is null
     */
    boolean forEachBitMap(LongPredicate predicate);

    /**
     * Creates a BitMapProducer from an array of Long.
     * @param bitMaps the bitMaps to return.
     * @return a BitMapProducer.
     */
    static BitMapProducer fromLongArray(long... bitMaps) {
        return new BitMapProducer() {

            @Override
            public boolean forEachBitMap(LongPredicate predicate) {
                for (long word : bitMaps) {
                    if (!predicate.test(word)) {
                        return false;
                    }
                }
                return true;
            }

        };
    }

    /**
     * Creates a BitMapProducer from an IndexProducer.
     * @param producer the IndexProducer that specifies the indexes of the bits to enable.
     * @param numberOfBits the number of bits in the Bloom filter.
     * @return A BitMapProducer that produces the BitMap equivalent of the Indices from the producer.
     */
    static BitMapProducer fromIndexProducer(IndexProducer producer, int numberOfBits) {
        Objects.requireNonNull(producer, "producer");
        Objects.requireNonNull(numberOfBits, "numberOfBits");

        return new BitMapProducer() {
            private int maxBucket = -1;
            private long[] result = new long[BitMap.numberOfBitMaps(numberOfBits)];

            @Override
            public boolean forEachBitMap(LongPredicate predicate) {
                Objects.requireNonNull(predicate, "predicate");
                /*
                 * we can not assume that all the ints will be in order and not repeated. This
                 * is because the HasherCollection does not make the guarantee.
                 */
                // process all the ints into a array of BitMaps
                IntPredicate builder = new IntPredicate() {
                    @Override
                    public boolean test(int i) {
                        int bucketIdx = BitMap.getLongIndex(i);
                        maxBucket = maxBucket < bucketIdx ? bucketIdx : maxBucket;
                        result[bucketIdx] |= BitMap.getLongBit(i);
                        return true;
                    }
                };
                producer.forEachIndex(builder);
                // send the bitmaps to the consumer.
                for (int bucket = 0; bucket <= maxBucket; bucket++) {
                    if (!predicate.test(result[bucket])) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    /**
     * A LongConsumer that builds an Array of BitMaps as produced by a BitMapProducer.
     *
     */
    class ArrayBuilder implements LongPredicate {
        private long[] result;
        private int idx = 0;
        private int bucketCount = 0;

        /**
         * Constructor that creates an empty ArrayBuilder.
         * @param shape The shape used to generate the BitMaps.
         */
        public ArrayBuilder(Shape shape) {
            this(shape, null);
        }

        /**
         * Constructor that creates an array builder with an initial value.
         * @param shape The shape used to generate the BitMaps.
         * @param initialValue an array of BitMap values to initialize the builder with.  May be {@code null}.
         * @throws IllegalArgumentException is the length of initialValue is greater than the number of
         * buckets as specified by the number of bits in the Shape.
         */
        public ArrayBuilder(Shape shape, long[] initialValue) {
            Objects.requireNonNull(shape, "shape");
            result = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
            if (initialValue != null) {
                if (initialValue.length > result.length) {
                    throw new IllegalArgumentException(
                            String.format("initialValue length (%s) is longer than shape length (%s)",
                                    initialValue.length, result.length));
                }
                bucketCount = initialValue.length;
                System.arraycopy(initialValue, 0, result, 0, initialValue.length);
            }
        }

        @Override
        public boolean test(long bitmap) {
            result[idx++] |= bitmap;
            bucketCount = bucketCount >= idx ? bucketCount : idx;
            return true;
        }

        /**
         * Returns the array.
         * @return the Array of BitMaps.
         */
        public long[] getArray() {
            return Arrays.copyOf(result, bucketCount);
        }
    }

}
