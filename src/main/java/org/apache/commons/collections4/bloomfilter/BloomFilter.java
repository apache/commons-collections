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
 * The interface that describes a Bloom filter.
 * <p>
 * <em>See implementation notes for BitMapProducer and IndexProducer.</em>
 * </p>
 * @see BitMapProducer
 * @see IndexProducer
 * @since 4.5
 */
public interface BloomFilter extends IndexProducer, BitMapProducer {

    /**
     * The sparse characteristic used to determine the best method for matching.
     * <p>For `sparse` implementations
     * the {@code forEachIndex(IntConsumer consumer)} method is more efficient. For non `sparse` implementations
     * the {@code forEachBitMap(LongConsumer consumer)} is more efficient. Implementers should determine if it is easier
     * for the implementation to produce indexes of bit map blocks.</p>
     */
    int SPARSE = 0x1;

    /**
     * Creates a new instance of the BloomFilter with the same properties as the current one.
     * @return a copy of this BloomFilter
     */
    BloomFilter copy();

    // Query Operations

    /**
     * Returns the characteristics of the filter.
     * <p>
     * Characteristics are defined as bits within the characteristics integer.
     * @return the characteristics for this bloom filter.
     */
    int characteristics();

    /**
     * Gets the shape that was used when the filter was built.
     * @return The shape the filter was built with.
     */
    Shape getShape();

    /**
     * Resets the filter to its initial, unpopulated state.
     */
    void clear();

    /**
     * Returns {@code true} if this filter contains the specified filter.
     *
     * <p>Specifically this
     * returns {@code true} if this filter is enabled for all bits that are enabled in the
     * {@code other} filter. Using the bit representations this is
     * effectively {@code (this AND other) == other}.</p>
     *
     * @param other the other Bloom filter
     * @return true if all enabled bits in the other filter are enabled in this filter.
     */
    default boolean contains(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        return (characteristics() & SPARSE) != 0 ? contains((IndexProducer) other) : contains((BitMapProducer) other);
    }

    /**
     * Returns {@code true} if this filter contains the bits specified in the hasher.
     *
     * <p>Specifically this returns {@code true} if this filter is enabled for all bit indexes
     * identified by the {@code hasher}. Using the bit map representations this is
     * effectively {@code (this AND hasher) == hasher}.</p>
     *
     * @param hasher the hasher to provide the indexes
     * @return true if this filter is enabled for all bits specified by the hasher
     */
    default boolean contains(Hasher hasher) {
        Objects.requireNonNull(hasher, "Hasher");
        Shape shape = getShape();
        return contains(hasher.indices(shape));
    }

    /**
     * Returns {@code true} if this filter contains the indices specified IndexProducer.
     *
     * <p>Specifically this returns {@code true} if this filter is enabled for all bit indexes
     * identified by the {@code IndexProducer}.</p>
     *
     * @param indexProducer the IndexProducer to provide the indexes
     * @return {@code true} if this filter is enabled for all bits specified by the IndexProducer
     */
    boolean contains(IndexProducer indexProducer);

    /**
     * Returns {@code true} if this filter contains the bits specified in the bit maps produced by the
     * bitMapProducer.
     *
     * @param bitMapProducer the the {@code BitMapProducer} to provide the bit maps.
     * @return {@code true} if this filter is enabled for all bits specified by the bit maps
     */
    default boolean contains(BitMapProducer bitMapProducer) {
        return forEachBitMapPair(bitMapProducer, (x, y) -> (x & y) == y);
    }

    // update operations

    /**
     * Merges the specified Bloom filter into this Bloom filter.
     *
     * <p>Specifically all
     * bit indexes that are identified by the {@code other} will be enabled in this filter.</p>
     *
     * <p><em>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter may or may not contain
     * the {@code other} Bloom filter.</em>  This state may occur in complex Bloom filter implementations like
     * counting Bloom filters.</p>
     *
     * @param other The bloom filter to merge into this one.
     * @return true if the merge was successful
     */
    default boolean merge(BloomFilter other) {
        return (characteristics() & SPARSE) != 0 ? merge((IndexProducer) other) : merge((BitMapProducer) other);
    }

    /**
     * Merges the specified hasher into this Bloom filter. Specifically all
     * bit indexes that are identified by the {@code hasher} will be enabled in this filter.
     *
     * <p><em>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter may or may not contain
     * the {@code hasher} values.</em>  This state may occur in complex Bloom filter implementations like
     * counting Bloom filters.</p>
     *
     * @param hasher The hasher to merge.
     * @return true if the merge was successful
     */
    default boolean merge(Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return merge(hasher.indices(getShape()));
    }

    /**
     * Merges the specified IndexProducer into this Bloom filter. Specifically all
     * bit indexes that are identified by the {@code producer} will be enabled in this filter.
     *
     * <p><em>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter may or may not contain all the indexes of
     * the {@code producer}.</em>  This state may occur in complex Bloom filter implementations like
     * counting Bloom filters.</p>
     *
     * @param indexProducer The IndexProducer to merge.
     * @return true if the merge was successful
     * @throws IllegalArgumentException if producer sends illegal value.
     */
    boolean merge(IndexProducer indexProducer);

    /**
     * Merges the specified hasher into this Bloom filter. Specifically all
     * bit indexes that are identified by the {@code producer} will be enabled in this filter.
     *
     * <p><em>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter may or may not contain all the indexes
     * enabled in the {@code producer}.</em>  This state may occur in complex Bloom filter implementations like
     * counting Bloom filters.</p>
     *
     * @param bitMapProducer The producer to merge.
     * @return true if the merge was successful
     * @throws IllegalArgumentException if producer sends illegal value.
     */
    boolean merge(BitMapProducer bitMapProducer);

    // Counting Operations

    /**
     * Determines if the bloom filter is "full".
     *
     * <p>Full is defined as having no unset bits.</p>
     *
     * @return {@code true} if the filter is full, {@code false} otherwise.
     */
    default boolean isFull() {
        return cardinality() == getShape().getNumberOfBits();
    }

    /**
     * Gets the cardinality (number of enabled bits) of this Bloom filter.
     *
     * <p>This is also known as the Hamming value or Hamming number.</p>
     *
     * @return the cardinality of this filter
     */
    int cardinality();

    /**
     * Estimates the number of items in the Bloom filter.
     *
     * <p>By default this is the rounding of the {@code Shape.estimateN(cardinality)} calculation for the
     * shape and cardinality of this filter.</p>
     *
     * <p>This produces an estimate roughly equivalent to the number of Hashers that have been merged into the filter.</p>
     *
     * @return an estimate of the number of items in the bloom filter.
     * @see Shape#estimateN(int)
     */
    default int estimateN() {
        return (int) Math.round(getShape().estimateN(cardinality()));
    }

    /**
     * Estimates the number of items in the union of this Bloom filter with the other bloom filter.
     *
     * <p>By default this is the {@code estimateN()} of the merging of this filter with the {@code other} filter.</p>
     *
     * <p>This produces an estimate roughly equivalent to the number of unique Hashers that have been merged into either
     * of the filters.</p>
     *
     * @param other The other Bloom filter
     * @return an estimate of the number of items in the union.
     * @see #estimateN()
     */
    default int estimateUnion(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        BloomFilter cpy = this.copy();
        cpy.merge(other);
        return cpy.estimateN();
    }

    /**
     * Estimates the number of items in the intersection of this Bloom filter with the other bloom filter.
     *
     * <p>By default this is the {@code estimateN() + other.estimateN() - estimateUnion(other)} </p>
     *
     * <p>This produces estimate is roughly equivalent to the number of unique Hashers that have been merged into both
     * of the filters.</p>
     *
     * @param other The other Bloom filter
     * @return an estimate of the number of items in the intersection.
     */
    default int estimateIntersection(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        return estimateN() + other.estimateN() - estimateUnion(other);
    }
}
