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
 * <p>The functions view an array of longs as a collection of bit maps each containing 64 bits. The bits are arranged
 * in memory as a little-endian long value. This matches the requirements of the BitMapExtractor interface.</p>
 *
 * @since 4.5.0-M2
 */
public class BitMaps {

    /** A bit shift to apply to an integer to divided by 64 (2^6). */
    private static final int DIVIDE_BY_64 = 6;

    /**
     * Checks if the specified index bit is enabled in the array of bit maps.
     * <p>
     * If the bit specified by bitIndex is not in the bit map false is returned.
     * </p>
     *
     * @param bitMaps  The array of bit maps.
     * @param bitIndex the index of the bit to locate.
     * @return {@code true} if the bit is enabled, {@code false} otherwise.
     * @throws IndexOutOfBoundsException if bitIndex specifies a bit not in the range being tracked.
     */
    public static boolean contains(final long[] bitMaps, final int bitIndex) {
        return (bitMaps[getLongIndex(bitIndex)] & getLongBit(bitIndex)) != 0;
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
        // signed shift. Any negative index will produce a negative value
        // by sign-extension and if used as an index into an array it will throw an
        // exception.
        return bitIndex >> DIVIDE_BY_64;
    }

    /**
     * Performs a modulus calculation on an unsigned long and a positive integer divisor.
     *
     * <p>This method computes the same result as {@link Long#remainderUnsigned(long, long)}
     * but assumes that the divisor is an integer in the range 1 to 2<sup>31</sup> - 1 inclusive,
     * that is a strictly positive integer size.
     *
     * <p><em>If the divisor is negative the behavior is not defined.</em></p>
     *
     * @param dividend an unsigned long value to calculate the modulus of.
     * @param divisor the divisor for the modulus calculation, must be strictly positive.
     * @return the remainder or modulus value.
     * @throws ArithmeticException if the divisor is zero
     * @see Long#remainderUnsigned(long, long)
     */
    public static int mod(final long dividend, final int divisor) {
        // See Hacker's Delight (2nd ed), section 9.3.
        // Assume divisor is positive.
        // Divide half the unsigned number and then double the quotient result.
        final long quotient = (dividend >>> 1) / divisor << 1;
        final long remainder = dividend - quotient * divisor;
        // remainder in [0, 2 * divisor)
        return (int) (remainder >= divisor ? remainder - divisor : remainder);
    }

    /**
     * Creates a new bitmap for the number of bit maps (longs) required for the numberOfBits parameter.
     *
     * <p><em>If the input is negative the behavior is not defined.</em></p>
     *
     * @param numberOfBits the number of bits to store in the array of bit maps.
     * @return a new bitmap.
     */
    static long[] newBitMap(final int numberOfBits) {
        return new long[numberOfBitMaps(numberOfBits)];
    }

    /**
     * Creates a new bitmap for given shape parameter.
     *
     * @param shape the shape.
     * @return a new bitmap.
     */
    static long[] newBitMap(final Shape shape) {
        return newBitMap(shape.getNumberOfBits());
    }

    /**
     * Calculates the number of bit maps (longs) required for the numberOfBits parameter.
     *
     * <p><em>If the input is negative the behavior is not defined.</em></p>
     *
     * @param numberOfBits the number of bits to store in the array of bit maps.
     * @return the number of bit maps necessary.
     */
    public static int numberOfBitMaps(final int numberOfBits) {
        return (numberOfBits - 1 >> DIVIDE_BY_64) + 1;
    }

    /**
     * Calculates the number of bit maps (longs) required for the shape parameter.
     *
     * @param shape the shape.
     * @return the number of bit maps necessary.
     */
    static int numberOfBitMaps(final Shape shape) {
        return numberOfBitMaps(shape.getNumberOfBits());
    }

    /**
     * Sets the bit in the bit maps.
     * <p><em>Does not perform range checking</em></p>
     *
     * @param bitMaps  The array of bit maps.
     * @param bitIndex the index of the bit to set.
     * @throws IndexOutOfBoundsException if bitIndex specifies a bit not in the range being tracked.
     */
    public static void set(final long[] bitMaps, final int bitIndex) {
        bitMaps[getLongIndex(bitIndex)] |= getLongBit(bitIndex);
    }

    /** Do not instantiate. */
    private BitMaps() {
    }
}
