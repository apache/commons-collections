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
 * <em>See implementation notes for {@link BitMapExtractor} and {@link IndexExtractor}.</em>
 * </p>
 *
 * @param <T> The BloomFilter type.
 * @see BitMapExtractor
 * @see IndexExtractor
 * @since 4.5.0-M1
 */
public interface BloomFilter<T extends BloomFilter<T>> extends IndexExtractor, BitMapExtractor {

    /**
     * The sparse characteristic used to determine the best method for matching: {@value}.
     * <p>
     * For `sparse` implementations the {@code forEachIndex(IntConsumer consumer)} method is more efficient. For non `sparse` implementations the
     * {@code forEachBitMap(LongConsumer consumer)} is more efficient. Implementers should determine if it is easier.
     * </p>
     */
    int SPARSE = 0x1;

    /**
     * Gets the cardinality (number of enabled bits) of this Bloom filter.
     *
     * <p>This is also known as the Hamming value or Hamming number.</p>
     *
     * @return the cardinality of this filter
     */
    int cardinality();

    // Query Operations

    /**
     * Gets the characteristics of the filter.
     * <p>
     * Characteristics are defined as bits within the characteristics integer.
     * </p>
     *
     * @return the characteristics for this bloom filter.
     */
    int characteristics();

    /**
     * Clears the filter to by resetting it to its initial, unpopulated state.
     */
    void clear();

    /**
     * Returns {@code true} if this filter contains the bits specified in the bit maps produced by the
     * bitMapExtractor.
     *
     * @param bitMapExtractor the {@code BitMapExtractor} to provide the bit maps.
     * @return {@code true} if this filter is enabled for all bits specified by the bit maps
     */
    default boolean contains(final BitMapExtractor bitMapExtractor) {
        return processBitMapPairs(bitMapExtractor, (x, y) -> (x & y) == y);
    }

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
    default boolean contains(final BloomFilter<?> other) {
        Objects.requireNonNull(other, "other");
        return (characteristics() & SPARSE) != 0 ? contains((IndexExtractor) other) : contains((BitMapExtractor) other);
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
    default boolean contains(final Hasher hasher) {
        Objects.requireNonNull(hasher, "Hasher");
        final Shape shape = getShape();
        return contains(hasher.indices(shape));
    }

    /**
     * Returns {@code true} if this filter contains the indices specified IndexExtractor.
     *
     * <p>Specifically this returns {@code true} if this filter is enabled for all bit indexes
     * identified by the {@code IndexExtractor}.</p>
     *
     * @param indexExtractor the IndexExtractor to provide the indexes
     * @return {@code true} if this filter is enabled for all bits specified by the IndexExtractor
     */
    boolean contains(IndexExtractor indexExtractor);

    /**
     * Creates a new instance of this {@link BloomFilter} with the same properties as the current one.
     *
     * @return a copy of this {@link BloomFilter}.
     */
    T copy();

    // update operations

    /**
     * Estimates the number of items in the intersection of this Bloom filter with the other bloom filter.
     *
     * <p>This method produces estimate is roughly equivalent to the number of unique Hashers that have been merged into both
     * of the filters by rounding the value from the calculation described in the {@link Shape} class Javadoc.</p>
     *
     * <p><em>{@code estimateIntersection} should only be called with Bloom filters of the same Shape.  If called on Bloom
     * filters of differing shape this method is not symmetric. If {@code other} has more bits an {@code IllegalArgumentException}
     * may be thrown.</em></p>
     *
     * @param other The other Bloom filter
     * @return an estimate of the number of items in the intersection. If the calculated estimate is larger than Integer.MAX_VALUE then MAX_VALUE is returned.
     * @throws IllegalArgumentException if the estimated N for the union of the filters is infinite.
     * @see #estimateN()
     * @see Shape
     */
    default int estimateIntersection(final BloomFilter<?> other) {
        Objects.requireNonNull(other, "other");
        final double eThis = getShape().estimateN(cardinality());
        final double eOther = getShape().estimateN(other.cardinality());
        if (Double.isInfinite(eThis) && Double.isInfinite(eOther)) {
            // if both are infinite the union is infinite and we return Integer.MAX_VALUE
            return Integer.MAX_VALUE;
        }
        long estimate;
        // if one is infinite the intersection is the other.
        if (Double.isInfinite(eThis)) {
            estimate = Math.round(eOther);
        } else if (Double.isInfinite(eOther)) {
            estimate = Math.round(eThis);
        } else {
            final T union = this.copy();
            union.merge(other);
            final double eUnion = getShape().estimateN(union.cardinality());
            if (Double.isInfinite(eUnion)) {
                throw new IllegalArgumentException("The estimated N for the union of the filters is infinite");
            }
            // maximum estimate value using integer values is: 46144189292 thus
            // eThis + eOther cannot overflow the long value.
            estimate = Math.round(eThis + eOther - eUnion);
            estimate = estimate < 0 ? 0 : estimate;
        }
        return estimate > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) estimate;
    }

    /**
     * Estimates the number of items in the Bloom filter.
     *
     * <p>By default this is the rounding of the {@code Shape.estimateN(cardinality)} calculation for the
     * shape and cardinality of this filter.</p>
     *
     * <p>This produces an estimate roughly equivalent to the number of Hashers that have been merged into the filter
     * by rounding the value from the calculation described in the {@link Shape} class Javadoc.</p>
     *
     * <p><em>Note:</em></p>
     * <ul>
     * <li>if cardinality == numberOfBits, then result is Integer.MAX_VALUE.</li>
     * <li>if cardinality &gt; numberOfBits, then an IllegalArgumentException is thrown.</li>
     * </ul>
     *
     * @return an estimate of the number of items in the bloom filter.  Will return Integer.MAX_VALUE if the
     * estimate is larger than Integer.MAX_VALUE.
     * @throws IllegalArgumentException if the cardinality is &gt; numberOfBits as defined in Shape.
     * @see Shape#estimateN(int)
     * @see Shape
     */
    default int estimateN() {
        final double d = getShape().estimateN(cardinality());
        if (Double.isInfinite(d)) {
            return Integer.MAX_VALUE;
        }
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException("Cardinality too large: " + cardinality());
        }
        final long l = Math.round(d);
        return l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) l;
    }

    /**
     * Estimates the number of items in the union of this Bloom filter with the other bloom filter.
     *
     * <p>This produces an estimate roughly equivalent to the number of unique Hashers that have been merged into either
     * of the filters by rounding the value from the calculation described in the {@link Shape} class Javadoc.</p>
     *
     * <p><em>{@code estimateUnion} should only be called with Bloom filters of the same Shape.  If called on Bloom
     * filters of differing shape this method is not symmetric. If {@code other} has more bits an {@code IllegalArgumentException}
     * may be thrown.</em></p>
     *
     * @param other The other Bloom filter
     * @return an estimate of the number of items in the union.  Will return Integer.MAX_VALUE if the
     * estimate is larger than Integer.MAX_VALUE.
     * @see #estimateN()
     * @see Shape
     */
    default int estimateUnion(final BloomFilter<?> other) {
        Objects.requireNonNull(other, "other");
        final T copy = this.copy();
        copy.merge(other);
        return copy.estimateN();
    }

    /**
     * Gets the shape that was used when the filter was built.
     * @return The shape the filter was built with.
     */
    Shape getShape();

    // Counting Operations

    /**
     * Determines if all the bits are off. This is equivalent to
     * {@code cardinality() == 0}.
     *
     * <p>
     * <em>Note: This method is optimised for non-sparse filters.</em> Implementers
     * are encouraged to implement faster checks if possible.
     * </p>
     *
     * @return {@code true} if no bits are enabled, {@code false} otherwise.
     */
    default boolean isEmpty() {
        return processBitMaps(y -> y == 0);
    }

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
     * Merges the specified hasher into this Bloom filter. Specifically all
     * bit indexes that are identified by the {@code bitMapExtractor} will be enabled in this filter.
     *
     * <p><em>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter may or may not contain all the indexes
     * enabled in the {@code bitMapExtractor}.</em>  This state may occur in complex Bloom filter implementations like
     * counting Bloom filters.</p>
     *
     * @param bitMapExtractor The BitMapExtractor to merge.
     * @return true if the merge was successful
     * @throws IllegalArgumentException if bitMapExtractor sends illegal value.
     */
    boolean merge(BitMapExtractor bitMapExtractor);

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
    default boolean merge(final BloomFilter<?> other) {
        return (characteristics() & SPARSE) != 0 ? merge((IndexExtractor) other) : merge((BitMapExtractor) other);
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
     * @throws IllegalArgumentException if hasher produces an illegal value.
     */
    default boolean merge(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return merge(hasher.indices(getShape()));
    }

    /**
     * Merges the specified IndexExtractor into this Bloom filter. Specifically all
     * bit indexes that are identified by the {@code indexExtractor} will be enabled in this filter.
     *
     * <p><em>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter may or may not contain all the indexes of
     * the {@code indexExtractor}.</em>  This state may occur in complex Bloom filter implementations like
     * counting Bloom filters.</p>
     *
     * @param indexExtractor The IndexExtractor to merge.
     * @return true if the merge was successful
     * @throws IllegalArgumentException if indexExtractor sends illegal value.
     */
    boolean merge(IndexExtractor indexExtractor);

    /**
     * Most Bloom filters create unique IndexExtractors.
     */
    @Override
    default IndexExtractor uniqueIndices() {
        return this;
    }
}
