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
package org.apache.commons.collections4.bloomfilter.hasher;

import java.util.Objects;
import java.util.function.IntPredicate;

import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;

/**
 * A Hasher that implements combinatorial hashing as as described by
 * <a href="https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf">Krisch and Mitzenmacher</a>.
 * <p>
 * Common use for this hasher is to generate a byte array as the output of a hashing
 * or MessageDigest algorithm.</p>
 *
 * @since 4.5
 */
public final class SimpleHasher implements Hasher {
    /**
     * A default increment used when the requested increment is zero. This is the same
     * default increment used in Java's SplittableRandom random number generator.  It is the
     * fractional representation of the golden ratio (0.618...) with a base of 2^64.
     */
    public static final long DEFAULT_INCREMENT = 0x9e3779b97f4a7c15L;

    /**
     * This mask is used to obtain the value of an int as if it were unsigned.
     */
    private static final long LONG_MASK = 0xffffffffL;

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
     * Constructs the SimpleHasher from a byte array.
     * <p>The byte array is split in 2 and each half is interpreted as a long value.
     * Excess bytes are ignored.  This simplifies the conversion from a Digest or hasher algorithm output
     * to the two values used by the SimpleHasher.</p>
     * <p><em>If the second long is zero the DEFAULT_INCREMENT is used instead.</em></p>
     * @param buffer the buffer to extract the longs from.
     * @throws IllegalArgumentException is buffer length is zero.
     */
    public SimpleHasher(byte[] buffer) {
        if (buffer.length == 0) {
            throw new IllegalArgumentException("buffer length must be greater than 0");
        }
        int segment = buffer.length / 2;
        this.initial = toLong(buffer, 0, segment);
        long possibleIncrement = toLong(buffer, segment, buffer.length - segment);
        this.increment = possibleIncrement == 0 ? DEFAULT_INCREMENT : possibleIncrement;
    }

    /**
     * Constructs the SimpleHasher from 2 longs.  The long values will be interpreted as unsigned values.
     * <p><em>If the increment is zero the DEFAULT_INCREMENT is used instead.</em></p>
     * @param initial The initial value for the hasher.
     * @param increment The value to increment the hash by on each iteration.
     */
    public SimpleHasher(long initial, long increment) {
        this.initial = initial;
        this.increment = increment == 0 ? DEFAULT_INCREMENT : increment;
    }

    /**
     * Performs a modulus calculation and then ensures tha the result is positive.
     * @param dividend a signed long value to calculate the modulus of.
     * @param divisor the divisor for the modulus calculation.
     * @return the remainder or modulus value.
     */
    static int mod(long dividend, int divisor) {
        int result = (int) dividend % divisor;
        return result < 0 ? result + divisor : result;
    }

    /**
     * Gets an IndexProducer that produces indices based on the shape.
     * The iterator will not return the same value multiple
     * times.
     *
     * @param shape {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public IndexProducer indices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");

        return new IndexProducer() {

            @Override
            public boolean forEachIndex(IntPredicate consumer) {
                Objects.requireNonNull(consumer, "consumer");
                // Filter filter = new Filter(shape, consumer);

                int bits = shape.getNumberOfBits();

                // Set up for the modulus. Use a long index to avoid overflow.
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
        };
    }

    @Override
    public IndexProducer uniqueIndices(final Shape shape) {
        return new IndexProducer() {

            @Override
            public boolean forEachIndex(IntPredicate consumer) {
                Objects.requireNonNull(consumer, "consumer");
                Filter filter = new Filter(shape, consumer);

                int bits = shape.getNumberOfBits();

                // Set up for the modulus. Use a long index to avoid overflow.
                long index = mod(initial, bits);
                int inc = mod(increment, bits);

                for (int functionalCount = 0; functionalCount < shape.getNumberOfHashFunctions(); functionalCount++) {

                    if (!filter.test((int) index)) {
                        return false;
                    }
                    index += inc;
                    index = index >= bits ? index - bits : index;
                }
                return true;
            }
        };
    }

    @Override
    public int size() {
        return 1;
    }
}
