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
     * Creates an IndexProducer from an array of integers.
     * @param values the index values
     * @return an IndexProducer that uses the values.
     */
    static IndexProducer fromIndexArray(final int... values) {
        return new IndexProducer() {

            @Override
            public boolean forEachIndex(final IntPredicate predicate) {
                for (final int value : values) {
                    if (!predicate.test(value)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public int[] asIndexArray() {
                return values.clone();
            }
        };
    }

    /**
     * Creates an IndexProducer from a {@code BitMapProducer}.
     * @param producer the {@code BitMapProducer}
     * @return a new {@code IndexProducer}.
     */
    static IndexProducer fromBitMapProducer(final BitMapProducer producer) {
        Objects.requireNonNull(producer, "producer");
        return consumer -> {
            final LongPredicate longPredicate = new LongPredicate() {
                int wordIdx = 0;

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
     * Return a copy of the IndexProducer data as an int array.
     *
     * <p>Indices ordering and uniqueness is not guaranteed.</p>
     *
     * <p><em>
     * The default implementation of this method is slow. It is recommended
     * that implementing classes reimplement this method.
     * </em></p>
     *
     * <p><em>
     * The default implementation of this method returns unique values in order.
     * </em></p>
     * @return An int array of the data.
     */
    default int[] asIndexArray() {
        class Indices {
            private int[] data = new int[32];
            private int size;

            boolean add(final int index) {
                if (size == data.length) {
                    // This will throw an out-of-memory error if there are too many bits.
                    // Since bits are addressed using 32-bit signed integer indices
                    // the maximum length should be ~2^31 / 2^6 = ~2^25.
                    // Any more is a broken implementation.
                    data = Arrays.copyOf(data, size * 2);
                }
                data[size++] = index;
                return true;
            }

            int[] toArray() {
                // Edge case to avoid a large array copy
                return size == data.length ? data : Arrays.copyOf(data, size);
            }
        }
        Indices indices = new Indices();
        forEachIndex(indices::add);
        return indices.toArray();
    }

    /**
     * Creates an IndexProducer of unique indices for this index.
     *
     * <p>This is like the `indices(Shape)` method except that it adds the guarantee that no
     * duplicate values will be returned. The indices produced are equivalent to those returned
     * from by a Bloom filter created from this hasher.</p>
     *
     * @return the iterator of integers
     * @throws IndexOutOfBoundsException if any index is less than 0
     */
    default IndexProducer uniqueIndices() {
        final BitSet bitSet = new BitSet();
        forEachIndex(i -> {
            bitSet.set(i);
            return true;
        });

        return new IndexProducer() {
            @Override
            public boolean forEachIndex(IntPredicate predicate) {
                for (int idx = bitSet.nextSetBit(0); idx >= 0; idx = bitSet.nextSetBit(idx+1)) {
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
