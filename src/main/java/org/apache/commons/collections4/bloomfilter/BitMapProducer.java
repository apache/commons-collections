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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
 * </p><p><em>
 * The default implementations of the {@code makePredicate()} and {@code asBitMapArray} methods
 * are slow and should be reimplemented in the implementing classes where possible.</em></p>
 *
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
     * Applies the {@code func} to each bit map pair in order.
     * <p>
     * Creates a LongPredicate that is used in apply {@code func} to this BitMapProducer
     * and another.  For example:
     * <pre>
     * BitMapProducer a = ....;
     * BitMapProducer b = ....;
     * LongPredicate predicate = a.apply( (x,y) -&gt; x==y );
     * boolean result = b.apply( predicate );
     * </pre>
     * The above example will execute a.bitmapValue == b.bitmapValue for every value in b.
     * </p><p>
     * Notes:
     * <ul>
     * <li>The resulting LongPredicate should only be used once.</li>
     * <li>Any changes made to the {@code func} arguments will not survive outside of the {@code func} call.</li>
     * </ul>
     * </p><p>
     * <em>The default implementation of this method uses {@code asBitMapArray()}  It is recommended that implementations
     * of BitMapProducer that have local arrays reimplement this method.</em></p>
     *
     * @param func The function to apply.
     * @return A LongPredicate that tests this BitMapProducers bitmap values in order.
     * @see #asBitMapArray()
     */
    default LongPredicate makePredicate(LongBiFunction func) {
        long[] ary = asBitMapArray();

        return new LongPredicate() {
            int idx = 0;

            @Override
            public boolean test(long other) {
                return func.test(idx > ary.length ? 0L : ary[idx++], other);
            }
        };
    }

    /**
     * Return a copy of the BitMapProducer data as a bit map array.
     * <p>
     * The default implementation of this method is slow.  It is recommended
     * that implementing classes reimplement this method.
     * </p>
     * @return An array of bit map data.
     */
    default long[] asBitMapArray() {
        List<Long> lst = new ArrayList<>();
        forEachBitMap(lst::add);
        long[] result = new long[lst.size()];
        for (int i = 0; i < lst.size(); i++) {
            result[i] = lst.get(i);
        }
        return result;
    }

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

            @Override
            public long[] asBitMapArray() {
                return Arrays.copyOf(bitMaps, bitMaps.length);
            }

            @Override
            public LongPredicate makePredicate(LongBiFunction func) {

                return new LongPredicate() {
                    int idx = 0;

                    @Override
                    public boolean test(long other) {
                        return func.test(idx >= bitMaps.length ? 0L : bitMaps[idx++], other);
                    }
                };
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

        long[] result = new long[BitMap.numberOfBitMaps(numberOfBits)];
        producer.forEachIndex(i -> {
            BitMap.set(result, i);
            return true;
        });
        return fromLongArray(result);
    }
}
