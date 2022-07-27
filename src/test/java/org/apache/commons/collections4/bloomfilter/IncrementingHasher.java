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
 * To be used for testing only.
 */
class IncrementingHasher implements Hasher {

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
     * <p><em>If the increment is zero the default increment is used instead.</em></p>
     * @param initial The initial value for the hasher.
     * @param increment The value to increment the hash by on each iteration.
     * @see #getDefaultIncrement()
     */
    IncrementingHasher(long initial, long increment) {
        this.initial = initial;
        this.increment = increment;
    }

    /**
     * Performs a modulus calculation on an unsigned long and an integer divisor.
     * @param dividend a unsigned long value to calculate the modulus of.
     * @param divisor the divisor for the modulus calculation.
     * @return the remainder or modulus value.
     */
    static int mod(long dividend, int divisor) {
        // See Hacker's Delight (2nd ed), section 9.3.
        // Assume divisor is positive.
        // Divide half the unsigned number and then double the quotient result.
        final long quotient = ((dividend >>> 1) / divisor) << 1;
        final long remainder = dividend - quotient * divisor;
        // remainder in [0, 2 * divisor)
        return (int) (remainder >= divisor ? remainder - divisor : remainder);
    }

    @Override
    public IndexProducer indices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");

        return new IndexProducer() {

            @Override
            public boolean forEachIndex(IntPredicate consumer) {
                Objects.requireNonNull(consumer, "consumer");
                int bits = shape.getNumberOfBits();
                /*
                 * Essentially this is computing a wrapped modulus from a start point and an
                 * increment. So actually you only need two modulus operations before the loop.
                 * This avoids any modulus operation inside the while loop. It uses a long index
                 * to avoid overflow.
                 */
                long index = mod(initial, bits);
                int inc = mod(increment, bits);

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
                int[] result = new int[shape.getNumberOfHashFunctions()];
                int[] idx = new int[1];
                /*
                 * This method needs to return duplicate indices
                 */
                forEachIndex(i -> {
                    result[idx[0]++] = i;
                    return true;
                });
                return result;
            }
        };
    }
}
