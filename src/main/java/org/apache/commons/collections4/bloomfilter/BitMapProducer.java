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
import java.util.function.LongPredicate;

/**
 * Produces bit map longs for a Bloom filter.
 *
 * Each bit map is a little-endian long value representing a block of bits of in a filter.
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
     * <p>If the producer is empty this method will return true.</p>
     *
     * <p>Any exceptions thrown by the action are relayed to the caller.</p>
     *
     * @param predicate the function to execute
     * @return {@code true} if all bit maps returned {@code true}, {@code false} otherwise.
     * @throws NullPointerException if the specified consumer is null
     */
    boolean forEachBitMap(LongPredicate predicate);

    /**
     * Applies the {@code func} to each bit map pair in order.  Will apply all of the bit maps from the other
     * BitMapProducer to this producer.  If this producer does not have as many bit maps it will provide 0 (zero)
     * for all excess calls to the LongBiPredicate.
     * <p>
     * <em>The default implementation of this method uses {@code asBitMapArray()}  It is recommended that implementations
     * of BitMapProducer that have local arrays reimplement this method.</em></p>
     *
     * @param other The other BitMapProducer that provides the y values in the (x,y) pair.
     * @param func The function to apply.
     * @return A LongPredicate that tests this BitMapProducers bitmap values in order.
     */
    default boolean forEachBitMapPair(BitMapProducer other, LongBiPredicate func) {
        CountingLongPredicate p = new CountingLongPredicate(asBitMapArray(), func);
        return other.forEachBitMap(p) && p.forEachRemaining();
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
        class Bits {
            private long[] data = new long[16];
            private int size;

            boolean add(long bits) {
                if (size == data.length) {
                    // This will throw an out-of-memory error if there are too many bits.
                    // Since bits are addressed using 32-bit signed integer indices
                    // the maximum length should be ~2^31 / 2^6 = ~2^25.
                    // Any more is a broken implementation.
                    data = Arrays.copyOf(data, size * 2);
                }
                data[size++] = bits;
                return true;
            }

            long[] toArray() {
                // Edge case to avoid a large array copy
                return size == data.length ? data : Arrays.copyOf(data, size);
            }
        }
        Bits bits = new Bits();
        forEachBitMap(bits::add);
        return bits.toArray();
    }

    /**
     * Creates a BitMapProducer from an array of Long.
     * @param bitMaps the bit maps to return.
     * @return a BitMapProducer.
     */
    static BitMapProducer fromBitMapArray(long... bitMaps) {
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
            public boolean forEachBitMapPair(BitMapProducer other, LongBiPredicate func) {
                CountingLongPredicate p = new CountingLongPredicate(bitMaps, func);
                return other.forEachBitMap(p) && p.forEachRemaining();
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
        return fromBitMapArray(result);
    }

    class CountingLongPredicate implements LongPredicate {
        int idx = 0;
        final long[] ary;
        final LongBiPredicate func;

        CountingLongPredicate(long[] ary, LongBiPredicate func) {
            this.ary = ary;
            this.func = func;
        }

        @Override
        public boolean test(long other) {
            return func.test(idx == ary.length ? 0 : ary[idx++], other);
        }

        boolean forEachRemaining() {
            while (idx != ary.length && func.test(ary[idx], 0)) {
                idx++;
            }
            return idx == ary.length;
        }
    }
}
