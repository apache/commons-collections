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

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;

/**
 * The interface that describes a Bloom filter.
 * @since 4.5
 */
public interface BloomFilter {

    // Query Operations

    /**
     * Gets the shape of this filter.
     *
     * @return the shape of this filter
     */
    Shape getShape();

    /**
     * Gets an array of little-endian long values representing the bits of this filter.
     *
     * <p>The returned array will have length {@code ceil(m / 64)} where {@code m} is the
     * number of bits in the filter and {@code ceil} is the ceiling function.
     * Bits 0-63 are in the first long. A value of 1 at a bit position indicates the bit
     * index is enabled.
     *
     * @return the {@code long[]} representation of this filter
     */
    long[] getBits();

    /**
     * Creates a StaticHasher that contains the indexes of the bits that are on in this
     * filter.
     *
     * @return a StaticHasher for that produces this Bloom filter
     */
    StaticHasher getHasher();

    /**
     * Returns {@code true} if this filter contains the specified filter. Specifically this
     * returns {@code true} if this filter is enabled for all bits that are enabled in the
     * {@code other} filter. Using the bit representations this is
     * effectively {@code (this AND other) == other}.
     *
     * @param other the other Bloom filter
     * @return true if this filter is enabled for all enabled bits in the other filter
     * @throws IllegalArgumentException if the shape of the other filter does not match
     * the shape of this filter
     */
    boolean contains(BloomFilter other);

    /**
     * Returns {@code true} if this filter contains the specified decomposed Bloom filter.
     * Specifically this returns {@code true} if this filter is enabled for all bit indexes
     * identified by the {@code hasher}. Using the bit representations this is
     * effectively {@code (this AND hasher) == hasher}.
     *
     * @param hasher the hasher to provide the indexes
     * @return true if this filter is enabled for all bits specified by the hasher
     * @throws IllegalArgumentException if the hasher cannot generate indices for the shape of
     * this filter
     */
    boolean contains(Hasher hasher);

    // Modification Operations

    /**
     * Merges the specified Bloom filter into this Bloom filter. Specifically all bit indexes
     * that are enabled in the {@code other} filter will be enabled in this filter.
     *
     * <p>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter is not ensured to contain
     * the {@code other} Bloom filter.
     *
     * @param other the other Bloom filter
     * @return true if the merge was successful
     * @throws IllegalArgumentException if the shape of the other filter does not match
     * the shape of this filter
     */
    boolean merge(BloomFilter other);

    /**
     * Merges the specified decomposed Bloom filter into this Bloom filter. Specifically all
     * bit indexes that are identified by the {@code hasher} will be enabled in this filter.
     *
     * <p>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter is not ensured to contain
     * the specified decomposed Bloom filter.
     *
     * @param hasher the hasher to provide the indexes
     * @return true if the merge was successful
     * @throws IllegalArgumentException if the hasher cannot generate indices for the shape of
     * this filter
     */
    boolean merge(Hasher hasher);

    // Counting Operations

    /**
     * Gets the cardinality (number of enabled bits) of this Bloom filter.
     *
     * <p>This is also known as the Hamming value.</p>
     *
     * @return the cardinality of this filter
     */
    int cardinality();

    /**
     * Performs a logical "AND" with the other Bloom filter and returns the cardinality
     * (number of enabled bits) of the result.
     *
     * @param other the other Bloom filter
     * @return the cardinality of the result of {@code (this AND other)}
     */
    int andCardinality(BloomFilter other);

    /**
     * Performs a logical "OR" with the other Bloom filter and returns the cardinality
     * (number of enabled bits) of the result.
     *
     * @param other the other Bloom filter
     * @return the cardinality of the result of {@code (this OR other)}
     */
    int orCardinality(BloomFilter other);

    /**
     * Performs a logical "XOR" with the other Bloom filter and returns the cardinality
     * (number of enabled bits) of the result.
     *
     * @param other the other Bloom filter
     * @return the cardinality of the result of {@code (this XOR other)}
     */
    int xorCardinality(BloomFilter other);
}
