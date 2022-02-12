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
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 * Produces BitMap longs for a Bloom filter.
 *
 *  Each bit map is a little-endian long value representing a block of bits of this filter.
 *
 * <p>The returned array will have length {@code ceil(m / 64)} where {@code m} is the
 * number of bits in the filter and {@code ceil} is the ceiling function.
 * Bits 0-63 are in the first long. A value of 1 at a bit position indicates the bit
 * index is enabled.
 *
 * The producer may produce empty bit maps at the end of the sequence.
 *
 */
public interface BitMapProducer {

    /**
     * Performs the given action for each {@code index} that represents an enabled bit.
     * Any exceptions thrown by the action are relayed to the caller.
     *
     * @param consumer the action to be performed for each non-zero bit index.
     * @throws NullPointerException if the specified action is null
     */
    void forEachBitMap(LongConsumer consumer);

    /**
     * Creates a BitMapProducer from an IndexProducer.
     * @param producer the IndexProducer that specifies the indexes of the bits to enable.
     * @param shape the desired shape.
     * @return A BitMapProducer that produces the BitMap equivalent of the Indices from the producer.
     */
    public static BitMapProducer fromIndexProducer( IndexProducer producer, Shape shape ) {

        return new BitMapProducer() {
            private int maxBucket = -1;
            private long[] result = new long[ BitMap.numberOfBuckets( shape.getNumberOfBits())];

            @Override
            public void forEachBitMap(LongConsumer consumer) {
                /* we can not assume that all the processes ints will be in order
                 * and not repeated.  This is because the HasherCollection does
                 * not make the guarantee.
                 */
                // process all the ints into a array of BitMaps
                IntConsumer builder = new IntConsumer() {
                    @Override
                    public void accept( int i ) {
                        int bucketIdx = BitMap.getLongIndex( i );
                        maxBucket = maxBucket < bucketIdx ? bucketIdx : maxBucket;
                        result[bucketIdx] |= BitMap.getLongBit(i);
                    }
                };
                producer.forEachIndex( builder );
                // send the bitmaps to the consumer.
                for (int bucket=0;bucket<=maxBucket;bucket++) {
                    consumer.accept( result[bucket] );
                }
            }
        };
    }

    /**
     * A LongConsumer that builds an Array of BitMaps as produced by a BitMapProducer.
     *
     */
    public class ArrayBuilder implements LongConsumer {
        private long[] result;
        private int idx=0;
        private int bucketCount=0;

        /**
         * Constructor.
         * @param shape The shape used to generate the BitMaps.
         */
        public ArrayBuilder( Shape shape ) {
            this( shape, null );
        }

        /**
         * Constructor.
         * @param shape The shape used to generate the BitMaps.
         * @param initialValue an array of BitMap values to initialize the builder with.  May be {@code null}.
         * @throws IllegalArgumentException is the length of initialValue is greater than the number of
         * buckets as specified by the number of bits in the Shape.
         */
        public ArrayBuilder( Shape shape, long[] initialValue ) {
            Objects.requireNonNull( shape, "shape");
            result = new long[ BitMap.numberOfBuckets( shape.getNumberOfBits() )];
            if (initialValue != null) {
                if (initialValue.length > result.length) {
                    throw new IllegalArgumentException( String.format(
                            "initialValue length (%s) is longer than shape length (%s)", initialValue.length, result.length));
                }
                bucketCount = initialValue.length;
                System.arraycopy(initialValue, 0, result, 0, bucketCount);
            }
        }

        @Override
        public void accept(long bitmap) {
            result[idx++] |= bitmap;
            bucketCount = bucketCount>=idx?bucketCount:idx;
        }

        /**
         * Returns the array.
         * @return the Array of BitMaps.
         */
        public long[] getArray() {
            return Arrays.copyOf( result, bucketCount );
        }
    }

}
