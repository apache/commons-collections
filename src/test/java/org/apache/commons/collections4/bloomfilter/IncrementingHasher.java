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

/**
 * A Hasher that implements simple combinatorial hashing as described by
 * <a href="https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf">Krisch and Mitzenmacher</a>.
 *
 * <p>To be used for testing only.</p>
 */
final class IncrementingHasher implements Hasher {

    /**
     * The initial hash value.
     */
    private final long initial;

    /**
     * The value to increment the hash value by.
     */
    private final long increment;

    /**
     * Constructs the IncrementingHasher from 2 longs.  The long values will be interpreted as unsigned values.
     * <p>
     * The initial hash value will be the modulus of the initial value.
     * Subsequent values will be calculated by repeatedly adding the increment to the last value and taking the modulus.
     * </p>
     * @param initial The initial value for the hasher.
     * @param increment The value to increment the hash by on each iteration.
     */
    IncrementingHasher(final long initial, final long increment) {
        this.initial = initial;
        this.increment = increment;
    }

    @Override
    public IndexProducer indices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");

        return new IndexProducer() {
            @Override
            public boolean forEachIndex(final IntPredicate consumer) {
                Objects.requireNonNull(consumer, "consumer");
                final int bits = shape.getNumberOfBits();

                // Essentially this is computing a wrapped modulus from a start point and an
                // increment. So actually you only need two modulus operations before the loop.
                // This avoids any modulus operation inside the while loop. It uses a long index
                // to avoid overflow.

                long index = EnhancedDoubleHasher.mod(initial, bits);
                final int inc = EnhancedDoubleHasher.mod(increment, bits);

                for (int functionalCount = 0; functionalCount < shape.getNumberOfHashFunctions(); functionalCount++) {
                    if (!consumer.test((int) index)) {
                        return false;
                    }
                    index += inc;
                    index = index >= bits ? index - bits : index;
                }
                return true;
            }

            @Override
            public int[] asIndexArray() {
                final int[] result = new int[shape.getNumberOfHashFunctions()];
                final int[] idx = new int[1];

                // This method needs to return duplicate indices

                forEachIndex(i -> {
                    result[idx[0]++] = i;
                    return true;
                });
                return result;
            }
        };
    }
}
