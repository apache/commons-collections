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
 * Contains functions to convert {@code int} indices into Bloom filter bit positions and visa versa.
 *
 * <p>The functions view an array of longs as a collection of bit maps each containing 64 bits.  The bits are arranged
 * in memory as a little-endian long value.  This matches the requirements of the BitMapProducer interface.</p>
 *
 * @since 4.5
 */
public class BitMap {
    /** A bit shift to apply to an integer to divided by 64 (2^6). */
    private static final int DIVIDE_BY_64 = 6;

    /** Do not instantiate. */
    private BitMap() {
    }

    /**
     * Calculates the number of bit maps (longs) required for the numberOfBits parameter.
     *
     * <p><em>If the input is negative the behavior is not defined.</em></p>

     * @param numberOfBits the number of bits to store in the array of bit maps.
     * @return the number of bit maps necessary.
     */
    public static int numberOfBitMaps(int numberOfBits) {
        return numberOfBits == 0 ? 0 : ((numberOfBits - 1) >> DIVIDE_BY_64) + 1;
    }

    /**
     * Checks if the specified index bit is enabled in the array of bit maps.
     *
     * If the bit specified by idx is not in the bit map false is returned.
     *
     * @param bitMaps  The array of bit maps.
     * @param idx the index of the bit to locate.
     * @return {@code true} if the bit is enabled, {@code false} otherwise.
     * @throws IndexOutOfBoundsException if idx specifies a bit not in the range being tracked.
     */
    public static boolean contains(long[] bitMaps, int idx) {
        return (bitMaps[getLongIndex(idx)] & getLongBit(idx)) != 0;
    }

    /**
     * Sets the bit in the bit maps.
     * <p><em>Does not perform range checking</em></p>
     *
     * @param bitMaps  The array of bit maps.
     * @param idx the index of the bit to set.
     * @throws IndexOutOfBoundsException if idx specifies a bit not in the range being tracked.
     */
    public static void set(long[] bitMaps, int idx) {
        bitMaps[getLongIndex(idx)] |= getLongBit(idx);
    }

    /**
     * Gets the filter index for the specified bit index assuming the filter is using 64-bit longs
     * to store bits starting at index 0.
     *
     * <p>The index is assumed to be positive. For a positive index the result will match
     * {@code bitIndex / 64}.</p>
     *
     * <p><em>The divide is performed using bit shifts. If the input is negative the behavior
     * is not defined.</em></p>
     *
     * @param bitIndex the bit index (assumed to be positive)
     * @return the index of the bit map in an array of bit maps.
     */
    public static int getLongIndex(final int bitIndex) {
        // An integer divide by 64 is equivalent to a shift of 6 bits if the integer is
        // positive.
        // We do not explicitly check for a negative here. Instead we use a
        // a signed shift. Any negative index will produce a negative value
        // by sign-extension and if used as an index into an array it will throw an
        // exception.
        return bitIndex >> DIVIDE_BY_64;
    }

    /**
     * Gets the filter bit mask for the specified bit index assuming the filter is using 64-bit
     * longs to store bits starting at index 0. The returned value is a {@code long} with only
     * 1 bit set.
     *
     * <p>The index is assumed to be positive. For a positive index the result will match
     * {@code 1L << (bitIndex % 64)}.</p>
     *
     * <p><em>If the input is negative the behavior is not defined.</em></p>
     *
     * @param bitIndex the bit index (assumed to be positive)
     * @return the filter bit
     */
    public static long getLongBit(final int bitIndex) {
        // Bit shifts only use the first 6 bits. Thus it is not necessary to mask this
        // using 0x3f (63) or compute bitIndex % 64.
        // Note: If the index is negative the shift will be (64 - (bitIndex & 0x3f)) and
        // this will identify an incorrect bit.
        return 1L << bitIndex;
    }

}
