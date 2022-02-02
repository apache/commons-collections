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

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

/**
 * Produces bit map longs for a Bloom filter.
 *
 * Each bit map is a little-endian long value representing a block of bits of this filter.
 *
 * <p>The returned array will have length {@code ceil(m / 64)} where {@code m} is the
 * number of bits in the filter and {@code ceil} is the ceiling function.
 * Bits 0-63 are in the first long. A value of 1 at a bit position indicates the bit
 * index is enabled.
 * </p><p>
 * The producer may stop at the last non zero bit map or may produce zero value bit maps to the limit determined by
 * a shape.
 * </p>
 * @since 4.5
 */
@FunctionalInterface
public interface BitMapProducer {

    /**
     * Each bit map is passed to the predicate in order.  The predicate is applied to each
     * bit map value, if the predicate returns {@code false} the execution is stopped, {@code false}
     * is returned, and no further bit maps are processed.
     *
     * <p>Any exceptions thrown by the action are relayed to the caller.</p>
     *
     * @param predicate the function to execute
     * @return {@code true} if all bit maps returned {@code true}, {@code false} otherwise.
     * @throws NullPointerException if the specified consumer is null
     */
    boolean forEachBitMap(LongPredicate predicate);

    /**
     * Creates a BitMapProducer from an array of Long.
     * @param bitMaps the bit maps to return.
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
     * @return A BitMapProducer that produces the bit maps equivalent of the Indices from the producer.
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
                // process all the ints into a array of bit maps
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
                // send the bit maps to the consumer.
                for (int bucket = 0; bucket <= maxBucket; bucket++) {
                    if (!predicate.test(result[bucket])) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

}
