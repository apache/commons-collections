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

/**
 * Contains functions to convert {@code int} indices into Bloom filter bit positions.
 * @since 4.5
 */
public class BitMap {
    /** A bit shift to apply to an integer to divided by 64 (2^6). */
    private static final int DIVIDE_BY_64 = 6;

    /** Do not instantiate. */
    private BitMap() {}

    /**
     * Calculates the number of buckets required for the numberOfBits parameter.
     * @param numberOfBits the number of bits to store in the array of buckets.
     * @return the number of buckets necessary.
     */
    public static int numberOfBuckets( int numberOfBits ) {
        int bucket = numberOfBits >> DIVIDE_BY_64;
        return bucket+1;
    }

    /**
     * Checks if the specified index bit is enabled in the array of bit buckets.
     * @param buckets  The array of bit buckets
     * @param idx the index of the bit to locate.
     * @return {@code true} if the bit is enabled, {@code false} otherwise.
     */
    public static boolean contains( long[] buckets, int idx ) {
        return (buckets[ getLongIndex( idx )] & getLongBit( idx )) != 0;
    }

    /**
     * Check the index is positive.
     *
     * @param bitIndex the bit index
     * @throws IndexOutOfBoundsException if the index is not positive
     */
    public static void checkPositive(final int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("Negative bitIndex: " + bitIndex);
        }
    }


    /**
     * Gets the filter index for the specified bit index assuming the filter is using 64-bit longs
     * to store bits starting at index 0.
     *
     * <p>The index is assumed to be positive. For a positive index the result will match
     * {@code bitIndex / 64}.
     *
     * <p>The divide is performed using bit shifts. If the input is negative the behavior
     * is not defined.
     *
     * @param bitIndex the bit index (assumed to be positive)
     * @return the filter index
     * @see #checkPositive(int)
     */
    public static int getLongIndex(final int bitIndex) {
        // An integer divide by 64 is equivalent to a shift of 6 bits if the integer is positive.
        // We do not explicitly check for a negative here. Instead we use a
        // a signed shift. Any negative index will produce a negative value
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
     * <p>If the input is negative the behavior is not defined.
     *
     * @param bitIndex the bit index (assumed to be positive)
     * @return the filter bit
     * @see #checkPositive(int)
     */
    public static long getLongBit(final int bitIndex) {
        // Bit shifts only use the first 6 bits. Thus it is not necessary to mask this
        // using 0x3f (63) or compute bitIndex % 64.
        // Note: If the index is negative the shift will be (64 - (bitIndex & 0x3f)) and
        // this will identify an incorrect bit.
        return 1L << bitIndex;
    }

    /**
     * Determines id a cardinality is sparse for the shape.
     * Since the size of a bucket is a long and the size of an index is an int, there can be
     * 2 indexes for each bucket.  Since indexes are evenly distributed sparse is defined as
     * {@code numberOfBuckets*2 >= cardinality}
     * @param cardinality the cardinality to check.
     * @param shape the Shape to check against
     * @return true if the cardinality is sparse within the bucket.
     */
    public static boolean isSparse( int cardinality, Shape shape ) {
        Objects.requireNonNull( shape, "shape");
        return numberOfBuckets(shape.getNumberOfBits()-1)*2 >= cardinality;
    }

}