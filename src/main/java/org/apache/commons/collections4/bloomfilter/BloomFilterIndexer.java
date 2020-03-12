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

/**
 * Contains functions to convert {@code int} indices into Bloom filter bit positions.
 */
final class BloomFilterIndexer {
    /** A bit shift to apply to an integer to divided by 64 (2^6). */
    private static final int DIVIDE_BY_64 = 6;

    /** Do not instantiate. */
    private BloomFilterIndexer() {}

    /**
     * Check the index is positive.
     *
     * @param bitIndex the bit index
     * @throws IndexOutOfBoundsException if the index is not positive
     */
    static void checkPositive(int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("Negative bitIndex: " + bitIndex);
        }
    }

    /**
     * Gets the filter index for the specified bit index assuming the filter is using 64-bit longs
     * to store bits starting at index 0.
     *
     * <p>The index is assumed to be positive. For a positive index the result will match
     * {@code bitIndex >> 6}.
     *
     * <p>The divide is performed using bit shifts. If the input is negative the behaviour
     * is not defined.
     *
     * @param bitIndex the bit index (assumed to be positive)
     * @return the filter index
     * @see #checkPositive(int)
     */
    static int getLongIndex(int bitIndex) {
        // Use a signed shift. Any negative index will produce a negative value
        // by sign-extension and if used as an index into an array it will throw an exception.
        return bitIndex >> DIVIDE_BY_64;
    }

    /**
     * Gets the filter bit mask for the specified bit index assuming the filter is using 64-bit
     * longs to store bits starting at index 0. The returned value is a {@code long} with only
     * 1 bit set.
     *
     * <p>The index is assumed to be positive. For a positive index the result will match
     * {@code 1L << (bitIndex % 64)}.
     *
     * <p>If the input is negative the behaviour is not defined.
     *
     * @param bitIndex the bit index (assumed to be positive)
     * @return the filter bit
     * @see #checkPositive(int)
     */
    static long getLongBit(int bitIndex) {
        // Bit shifts only use the first 6 bits. Thus it is not necessary to mask this
        // using 0x3f (63) or compute bitIndex % 64.
        // If the index is negative the shift will be in the opposite direction.
        return 1L << bitIndex;
    }
}
