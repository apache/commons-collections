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
 * A Hasher that implements combinatorial hashing as described by
 * <a href="https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf">Krisch and Mitzenmacher</a> using the enhanced double hashing technique
 * described in the wikipedia article  <a href="https://en.wikipedia.org/wiki/Double_hashing#Enhanced_double_hashing">Double Hashing</a>.
 * <p>
 * Common use for this hasher is to generate bit indices from a byte array output of a hashing
 * or MessageDigest algorithm.</p>
 *
 * <h2>Thoughts on the hasher input</h2>
 *
 *<p>Note that it is worse to create smaller numbers for the {@code initial} and {@code increment}. If the {@code initial} is smaller than
 * the number of bits in a filter then hashing will start at the same point when the size increases; likewise the {@code increment} will be
 * the same if it remains smaller than the number of bits in the filter and so the first few indices will be the same if the number of bits
 * changes (but is still larger than the {@code increment}). In a worse case scenario with small {@code initial} and {@code increment} for
 * all items, hashing may not create indices that fill the full region within a much larger filter. Imagine hashers created with {@code initial}
 * and {@code increment} values less than 255 with a filter size of 30000 and number of hash functions as 5. Ignoring the
 * tetrahedral addition (a maximum of 20 for k=5) the max index is 255 * 4 + 255 = 1275, this covers 4.25% of the filter. This also
 * ignores the negative wrapping but the behavior is the same, some bits cannot be reached.
 * </p><p>
 * So this needs to be avoided as the filter probability assumptions will be void. If the {@code initial} and {@code increment} are larger
 * than the number of bits then the modulus will create a 'random' position and increment within the size.
 * </p>
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
     * Convert bytes to big-endian long filling with zero bytes as necessary..
     * @param byteArray the byte array to extract the values from.
     * @param offset the offset to start extraction from.
     * @param len the length of the extraction, may be longer than 8.
     * @return
     */
    private static long toLong(final byte[] byteArray, final int offset, final int len) {
        long val = 0;
        int shift = Long.SIZE;
        final int end = offset + Math.min(len, Long.BYTES);
        for (int i = offset; i < end; i++) {
            shift -= Byte.SIZE;
            val |= (long) (byteArray[i] & 0xFF) << shift;
        }
        return val;
    }

    /**
     * Constructs the EnhancedDoubleHasher from a byte array.
     * <p>
     * This method simplifies the conversion from a Digest or hasher algorithm output
     * to the two values used by the EnhancedDoubleHasher.</p>
     * <p>The byte array is split in 2 and the first 8 bytes of each half are interpreted as a big-endian long value.
     * Excess bytes are ignored.
     * If there are fewer than 16 bytes the following conversions are made.
     *</p>
     * <ol>
     * <li>If there is an odd number of bytes the excess byte is assigned to the increment value</li>
     * <li>The bytes alloted are read in big-endian order any byte not populated is set to zero.</li>
     * </ol>
     * <p>
     * This ensures that small arrays generate the largest possible increment and initial values.
     * </p>
     * @param buffer the buffer to extract the longs from.
     * @throws IllegalArgumentException is buffer length is zero.
     */
    public EnhancedDoubleHasher(final byte[] buffer) {
        if (buffer.length == 0) {
            throw new IllegalArgumentException("buffer length must be greater than 0");
        }
        // divide by 2
        final int segment = buffer.length / 2;
        this.initial = toLong(buffer, 0, segment);
        this.increment = toLong(buffer, segment, buffer.length - segment);
    }

    /**
     * Constructs the EnhancedDoubleHasher from 2 longs. The long values will be interpreted as unsigned values.
     * @param initial The initial value for the hasher.
     * @param increment The value to increment the hash by on each iteration.
     */
    public EnhancedDoubleHasher(final long initial, final long increment) {
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
    static int mod(final long dividend, final int divisor) {
        // See Hacker's Delight (2nd ed), section 9.3.
        // Assume divisor is positive.
        // Divide half the unsigned number and then double the quotient result.
        final long quotient = (dividend >>> 1) / divisor << 1;
        final long remainder = dividend - quotient * divisor;
        // remainder in [0, 2 * divisor)
        return (int) (remainder >= divisor ? remainder - divisor : remainder);
    }

    @Override
    public IndexProducer indices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");

        return new IndexProducer() {

            @Override
            public boolean forEachIndex(final IntPredicate consumer) {
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
                if (k > bits) {
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
