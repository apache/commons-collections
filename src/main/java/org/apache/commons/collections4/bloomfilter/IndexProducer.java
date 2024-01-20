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
import java.util.BitSet;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

/**
 * An object that produces indices of a Bloom filter.
 * <p><em>
 * The default implementation of {@code asIndexArray} is slow. Implementers should reimplement the
 * method where possible.</em></p>
 *
 * @since 4.5
 */
@FunctionalInterface
public interface IndexProducer {

    /**
     * Creates an IndexProducer from a {@code BitMapProducer}.
     * @param producer the {@code BitMapProducer}
     * @return a new {@code IndexProducer}.
     */
    static IndexProducer fromBitMapProducer(final BitMapProducer producer) {
        Objects.requireNonNull(producer, "producer");
        return consumer -> {
            final LongPredicate longPredicate = new LongPredicate() {
                int wordIdx;

                @Override
                public boolean test(long word) {
                    int i = wordIdx;
                    while (word != 0) {
                        if ((word & 1) == 1 && !consumer.test(i)) {
                            return false;
                        }
                        word >>>= 1;
                        i++;
                    }
                    wordIdx += 64;
                    return true;
                }
            };
            return producer.forEachBitMap(longPredicate::test);
        };
    }

    /**
     * Creates an IndexProducer from an array of integers.
     * @param values the index values
     * @return an IndexProducer that uses the values.
     */
    static IndexProducer fromIndexArray(final int... values) {
        return new IndexProducer() {

            @Override
            public int[] asIndexArray() {
                return values.clone();
            }

            @Override
            public boolean forEachIndex(final IntPredicate predicate) {
                for (final int value : values) {
                    if (!predicate.test(value)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    /**
     * Return a copy of the IndexProducer data as an int array.
     *
     * <p>Indices ordering and uniqueness is not guaranteed.</p>
     *
     * <p><em>
     * The default implementation of this method creates an array and populates
     * it.  Implementations that have access to an index array should consider
     * returning a copy of that array if possible.
     * </em></p>
     *
     * @return An int array of the data.
     */
    default int[] asIndexArray() {
        class Indices {
            private int[] data = new int[32];
            private int size;

            boolean add(final int index) {
                data = IndexUtils.ensureCapacityForAdd(data, size);
                data[size++] = index;
                return true;
            }

            int[] toArray() {
                // Edge case to avoid a large array copy
                return size == data.length ? data : Arrays.copyOf(data, size);
            }
        }
        final Indices indices = new Indices();
        forEachIndex(indices::add);
        return indices.toArray();
    }

    /**
     * Each index is passed to the predicate. The predicate is applied to each
     * index value, if the predicate returns {@code false} the execution is stopped, {@code false}
     * is returned, and no further indices are processed.
     *
     * <p>Any exceptions thrown by the action are relayed to the caller.</p>
     *
     * <p>Indices ordering and uniqueness is not guaranteed.</p>
     *
     * @param predicate the action to be performed for each non-zero bit index.
     * @return {@code true} if all indexes return true from consumer, {@code false} otherwise.
     * @throws NullPointerException if the specified action is null
     */
    boolean forEachIndex(IntPredicate predicate);

    /**
     * Creates an IndexProducer comprising the unique indices for this producer.
     *
     * <p>By default creates a new producer with some overhead to remove
     * duplicates.  IndexProducers that return unique indices by default
     * should override this to return {@code this}.</p>
     *
     * <p>The default implementation will filter the indices from this instance
     * and return them in ascending order.</p>
     *
     * @return the IndexProducer of unique values.
     * @throws IndexOutOfBoundsException if any index is less than zero.
     */
    default IndexProducer uniqueIndices() {
        final BitSet bitSet = new BitSet();
        forEachIndex(i -> {
            bitSet.set(i);
            return true;
        });

        return new IndexProducer() {
            @Override
            public boolean forEachIndex(final IntPredicate predicate) {
                for (int idx = bitSet.nextSetBit(0); idx >= 0; idx = bitSet.nextSetBit(idx + 1)) {
                    if (!predicate.test(idx)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public IndexProducer uniqueIndices() {
                return this;
            }
        };
    }
}
