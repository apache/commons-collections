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

    /**
     * Performs a logical "AND" with the other Bloom filter and returns the cardinality of
     * the result.
     *
     * @param other the other Bloom filter.
     * @return the cardinality of the result of {@code ( this AND other )}.
     */
    int andCardinality(BloomFilter other);

    /**
     * Gets the cardinality of this Bloom filter.
     * <p>This is also known as the Hamming value.</p>
     *
     * @return the cardinality (number of enabled bits) in this filter.
     */
    int cardinality();

    /**
     * Performs a contains check. Effectively this AND other == other.
     *
     * @param other the Other Bloom filter.
     * @return true if this filter matches the other.
     */
    boolean contains(BloomFilter other);

    /**
     * Performs a contains check against a decomposed Bloom filter. The shape must match
     * the shape of this filter. The hasher provides bit indexes to check for. Effectively
     * decomposed AND this == decomposed.
     *
     * @param hasher The hasher containing the bits to check.
     * @return true if this filter contains the other.
     * @throws IllegalArgumentException if the shape argument does not match the shape of
     * this filter, or if the hasher is not the specified one
     */
    boolean contains(Hasher hasher);

    /**
     * Gets an array of little-endian long values representing the on bits of this filter.
     * bits 0-63 are in the first long.
     *
     * @return the LongBuffer representation of this filter.
     */
    long[] getBits();

    /**
     * Creates a StaticHasher that contains the indexes of the bits that are on in this
     * filter.
     *
     * @return a StaticHasher for that produces this Bloom filter.
     */
    StaticHasher getHasher();

    /**
     * Gets the shape of this filter.
     *
     * @return The shape of this filter.
     */
    Shape getShape();

    /**
     * Merges the other Bloom filter into this one.
     *
     * @param other the other Bloom filter.
     */
    void merge(BloomFilter other);

    /**
     * Merges the decomposed Bloom filter defined by the hasher into this Bloom
     * filter. The hasher provides an iterator of bit indexes to enable.
     *
     * @param hasher the hasher to provide the indexes.
     * @throws IllegalArgumentException if the shape argument does not match the shape of
     * this filter, or if the hasher is not the specified one
     */
    void merge(Hasher hasher);

    /**
     * Performs a logical "OR" with the other Bloom filter and returns the cardinality of
     * the result.
     *
     * @param other the other Bloom filter.
     * @return the cardinality of the result of {@code ( this OR other )}.
     */
    int orCardinality(BloomFilter other);

    /**
     * Performs a logical "XOR" with the other Bloom filter and returns the cardinality of
     * the result.
     *
     * @param other the other Bloom filter.
     * @return the cardinality of the result of {@code ( this XOR other )}
     */
    int xorCardinality(BloomFilter other);
}
