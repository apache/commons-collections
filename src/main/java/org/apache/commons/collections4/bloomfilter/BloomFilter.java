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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

/**
 * The interface that describes a Bloom filter.
 * @since 4.5
 */
public interface BloomFilter extends IndexProducer, BitMapProducer {

    /**
     * Return the Bloom filter data as a BitMap array.
     * @param filter the filter to get the data from.
     * @return An array of BitMap long.
     */
    static long[] asBitMapArray(BloomFilter filter) {
        BitMapProducer.ArrayBuilder builder = new BitMapProducer.ArrayBuilder(filter.getShape());
        filter.forEachBitMap(builder);
        return builder.getArray();
    }

    /**
     * Return the Bloom filter data as an array of indices for the enabled bits.
     * @param filter the Filter to get the data from.
     * @return An array of indices for enabled bits in the Bloom filter.
     */
    static int[] asIndexArray(BloomFilter filter) {
        int[] result = new int[filter.cardinality()];

        filter.forEachIndex(new IntConsumer() {
            int i = 0;

            @Override
            public void accept(int idx) {
                result[i++] = idx;
            }
        });
        return result;
    }

    // Query Operations

    /**
     * This method is used to determine the best method for matching.
     *
     * <p>For `sparse` implementations
     * the {@code forEachIndex(IntConsumer consumer)} method is more efficient.  For non `sparse` implementations
     * the {@code forEachBitMap(LongConsumer consumer)} is more efficient.  Implementers should determine if it is easier
     * for the implementation to produce indexes of BitMap blocks.</p>
     *
     * @return {@code true} if the implementation is sparse {@code false} otherwise.
     * @see BitMap
     */
    boolean isSparse();

    /**
     * Gets the shape that was used when the filter was built.
     * @return The shape the filter was built with.
     */
    Shape getShape();

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
        return isSparse() ? contains((IndexProducer) other) : contains((BitMapProducer) other);
    }

    /**
     * Returns {@code true} if this filter contains the bits specified in the hasher.
     *
     * <p>Specifically this returns {@code true} if this filter is enabled for all bit indexes
     * identified by the {@code hasher}. Using the BitMap representations this is
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
     * Returns {@code true} if this filter contains the bits specified in the BitMaps produced by the
     * bitMapProducer.
     *
     * @param bitMapProducer the the {@code BitMapProducer} to provide the BitMaps.
     * @return {@code true} if this filter is enabled for all bits specified by the BitMaps
     */
    boolean contains(BitMapProducer bitMapProducer);

    /**
     * Merges the specified Bloom filter with this Bloom filter creating a new Bloom filter.
     *
     * <p>Specifically all bit indexes that are enabled in the {@code other} and in @code this} filter will be
     * enabled in the resulting filter.</p>
     *
     * @param other the other Bloom filter
     * @return The new Bloom filter.
     */
    default BloomFilter merge(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        Shape shape = getShape();
        BloomFilter result = BitMap.isSparse((cardinality() + other.cardinality()), getShape())
                ? new SparseBloomFilter(shape)
                : new SimpleBloomFilter(shape);

        result.mergeInPlace(this);
        result.mergeInPlace(other);
        return result;
    }

    /**
     * Merges the specified Hasher with this Bloom filter and returns a new Bloom filter.
     *
     * <p>Specifically all bit indexes that are identified by the {@code hasher} and in {@code this} Bloom filter
     * be enabled in the resulting filter.</p>
     *
     * @param hasher the hasher to provide the indices
     * @return the new Bloom filter.
     */
    default BloomFilter merge(Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        Shape shape = getShape();
        BloomFilter result = BitMap.isSparse((hasher.size() * shape.getNumberOfHashFunctions()) + cardinality(), shape)
                ? new SparseBloomFilter(shape, hasher)
                : new SimpleBloomFilter(shape, hasher);
        result.mergeInPlace(this);
        return result;
    }

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
    boolean mergeInPlace(BloomFilter other);

    /**
     * Merges the specified hasher into this Bloom filter. Specifically all
     * bit indexes that are identified by the {@code hasher} will be enabled in this filter.
     *
     * <p><em>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter may or may not contain
     * the {@code other} Bloom filter.</em>  This state may occur in complex Bloom filter implementations like
     * counting Bloom filters.</p>
     *
     * @param hasher The hasher to merge.
     * @return true if the merge was successful
     */
    default boolean mergeInPlace(Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        Shape shape = getShape();
        BloomFilter result = BitMap.isSparse((hasher.size() * shape.getNumberOfHashFunctions()) + cardinality(), shape)
                ? new SparseBloomFilter(getShape(), hasher)
                : new SimpleBloomFilter(getShape(), hasher);
        return mergeInPlace(result);
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

    // Counting Operations

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
     * <p>An item is roughly equivalent to the number of Hashers that have been merged.  As the Bloom filter
     * is a probabilistic structure this value is an estimate.</p>
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
     * <p>An item is roughly equivalent to the number of Hashers that have been merged.  As the Bloom filter
     * is a probabilistic structure this value is an estimate.</p>
     *
     * @param other The other Bloom filter
     * @return an estimate of the number of items in the union.
     * @see #estimateN()
     */
    default int estimateUnion(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        return this.merge(other).estimateN();
    }

    /**
     * Estimates the number of items in the intersection of this Bloom filter with the other bloom filter.
     *
     * <p>By default this is the {@code estimateN() + other.estimateN() - estimateUnion(other)} </p>
     *
     * <p>An item is roughly equivalent to the number of Hashers that have been merged.  As the Bloom filter
     * is a probabilistic structure this value is an estimate.</p>
     *
     * @param other The other Bloom filter
     * @return an estimate of the number of items in the intersection.
     */
    default int estimateIntersection(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        return estimateN() + other.estimateN() - estimateUnion(other);
    }
}
