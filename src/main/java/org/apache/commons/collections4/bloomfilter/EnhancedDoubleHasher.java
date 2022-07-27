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
 * A Hasher that implements combinatorial hashing as as described by
 * <a href="https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf">Krisch and Mitzenmacher</a> using the enhanced double hashing technique
 * described in the wikipedia article  <a href="https://en.wikipedia.org/wiki/Double_hashing#Enhanced_double_hashing">Double Hashing</a>.
 * <p>
 * Common use for this hasher is to generate a byte array as the output of a hashing
 * or MessageDigest algorithm.</p>
 *
 * @since 4.5
 */
public class EnhancedDoubleHasher implements Hasher {

    /**
     * The initial hash value.
     */
    private final long initial;

    /**
     * The value to increment the hash value by.
     */
    private final long increment;

    /**
     * Convert bytes to long.
     * @param byteArray the byte array to extract the values from.
     * @param offset the offset to start extraction from.
     * @param len the length of the extraction, may be longer than 8.
     * @return
     */
    private static long toLong(byte[] byteArray, int offset, int len) {
        long val = 0;
        len = Math.min(len, Long.BYTES);
        for (int i = 0; i < len; i++) {
            val <<= 8;
            val |= (byteArray[offset + i] & 0x00FF);
        }
        return val;
    }

    /**
     * Constructs the EnhancedDoubleHasher from a byte array.
     * <p>The byte array is split in 2 and each half is interpreted as a long value.
     * Excess bytes are ignored.  This simplifies the conversion from a Digest or hasher algorithm output
     * to the two values used by the SimpleHasher.</p>
     * @param buffer the buffer to extract the longs from.
     * @throws IllegalArgumentException is buffer length is zero.
     */
    public EnhancedDoubleHasher(byte[] buffer) {
        if (buffer.length == 0) {
            throw new IllegalArgumentException("buffer length must be greater than 0");
        }
        int segment = buffer.length / 2;
        this.initial = toLong(buffer, 0, segment);
        this.increment = toLong(buffer, segment, buffer.length - segment);
    }

    /**
     * Constructs the EnhancedDoubleHasher from 2 longs.  The long values will be interpreted as unsigned values.
     * @param initial The initial value for the hasher.
     * @param increment The value to increment the hash by on each iteration.
     */
    public EnhancedDoubleHasher(long initial, long increment) {
        this.initial = initial;
        this.increment = increment;
    }

    /**
     * Gets the initial value for the hash calculation.
     * @return the initial value for the hash calculation.
     */
    long getInitial() {
        return initial;
    }

    /**
     * Gets the increment value for the hash calculation.
     * @return the increment value for the hash calculation.
     */
    long getIncrement() {
        return increment;
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
                final int bits = shape.getNumberOfBits();
                // Enhanced double hashing:
                // hash[i] = ( h1(x) + i*h2(x) + (i*i*i - i)/6 ) mod bits
                // See: https://en.wikipedia.org/wiki/Double_hashing#Enhanced_double_hashing
                //
                // Essentially this is computing a wrapped modulus from a start point and an
                // increment and an additional term as a tetrahedral number.
                // You only need two modulus operations before the loop. Within the loop
                // the modulus is handled using the sign bit to detect wrapping to ensure:
                // 0 <= index < bits
                // 0 <= inc < bits
                // The final hash is:
                // hash[i] = ( h1(x) - i*h2(x) - (i*i*i - i)/6 ) wrapped in [0, bits)

                int index = mod(initial, bits);
                int inc = mod(increment, bits);

                final int k = shape.getNumberOfHashFunctions();
                if (k>bits) {
                    for (int j = k; j > 0;) {
                        // handle k > bits
                        final int block = Math.min(j, bits);
                        j -= block;
                        for (int i = 0; i < block; i++) {
                            if (!consumer.test(index)) {
                                return false;
                            }
                            // Update index and handle wrapping
                            index -= inc;
                            index = index < 0 ? index + bits : index;

                            // Incorporate the counter into the increment to create a
                            // tetrahedral number additional term, and handle wrapping.
                            inc -= i;
                            inc = inc < 0 ? inc + bits : inc;
                        }
                    }
                } else {
                    for (int i = 0; i < k; i++) {
                        if (!consumer.test(index)) {
                            return false;
                        }
                        // Update index and handle wrapping
                        index -= inc;
                        index = index < 0 ? index + bits : index;

                        // Incorporate the counter into the increment to create a
                        // tetrahedral number additional term, and handle wrapping.
                        inc -= i;
                        inc = inc < 0 ? inc + bits : inc;
                    }

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
