/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.collections4.bloomfilter;

import java.util.BitSet;

/**
 * The interface for all BloomFilter implementations. Instances of BloomFilters should be
 * immutable.
 *
 * @since 4.5
 */
public interface BloomFilter {

    /**
     * Returns true if {@code other & this == other}. <p> This is the inverse of the match
     * method. </p> {@code X.match(Y)} is the same as {@code Y.inverseMatch(X) }
     *
     * @param other the other Bloom filter to match.
     * @return true if they match.
     */
    boolean inverseMatches(BloomFilter other);

    /**
     * Returns true if {@code this & other == this}.
     *
     * This is the standard Bloom filter match.
     *
     * @param other the other Bloom filter to match.
     * @return true if they match.
     */
    boolean matches(BloomFilter other);

    /**
     * Calculates the hamming distance from this Bloom filter to the other. The hamming
     * distance is defined as {@code this xor other} and is the number of bits that have
     * to be flipped to convert one filter to the other.
     *
     * @param other The other Bloom filter to calculate the distance to.
     * @return the distance.
     */
    int distance(BloomFilter other);

    /**
     * Gets the hamming weight for this filter.
     *
     * This is the number of bits that are on in the filter.
     *
     * @return The hamming weight.
     */
    int getHammingWeight();

    /**
     * Gets the log2 (log base 2) of this Bloom filter. This is the base 2 logarithm of
     * this Bloom filter if the bits in this filter were considers the bits in an unsigned
     * integer.
     *
     * @return the base 2 logarithm
     */
    double getLog();

    /**
     * Merges this Bloom filter with the other creating a new filter.
     *
     * @param other the other filter.
     * @return a new filter.
     * @throws IllegalArgumentException if other can not be merged.
     */
    BloomFilter merge(BloomFilter other);

    /**
     * Gets a copy of the bitset representation of the filter.
     *
     * @return the bit set representation.
     */
    BitSet getBitSet();

}
