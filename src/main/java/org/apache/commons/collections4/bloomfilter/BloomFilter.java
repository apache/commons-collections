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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;

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
    public static long[] asBitMapArray( BloomFilter filter ) {
        BitMapProducer.ArrayBuilder builder = new BitMapProducer.ArrayBuilder(filter.getShape());
        filter.forEachBitMap( builder );
        return builder.getArray();
    }

    /**
     * Return the Bloom filter data as an array of indices for the enabled bits.
     * @param filter the Filter to get the data from.
     * @return An array of indices for enabled bits in the Bloom filter.
     */
    public static int[] asIndexArray( BloomFilter filter ) {
        List<Integer> lst = new ArrayList<Integer>();
        filter.forEachIndex( lst::add );
        return lst.stream().mapToInt( Integer::intValue ).toArray();
    }


    // Query Operations

    /**
     * This method is used to determine the best method for matching.  For `sparse` implementations the `getIndices()`
     * method is more efficient.  Implementers should determine if it is easier for the implementation to return am array of
     * Indices (sparse) or a bit map as an array of unsigned longs.
     * @return
     */
    boolean isSparse();

    /**
     * Gets the shape that was used when the filter was built.
     * @return The shape the filter was built with.
     */
    Shape getShape();

    /**
     * Returns {@code true} if this filter contains the specified filter. Specifically this
     * returns {@code true} if this filter is enabled for all bits that are enabled in the
     * {@code other} filter. Using the bit representations this is
     * effectively {@code (this AND other) == other}.
     *
     * @param other the other Bloom filter
     * @return true if all enabled bits in the other filter are enabled in this filter.
     */
    default boolean contains(BloomFilter other) {
        Objects.requireNonNull( other, "other");
        return isSparse() ? contains( (IndexProducer) other) :
            contains( (BitMapProducer) other );
    }

    /**
     * Returns {@code true} if this filter contains the bits specified in the hasher.
     * Specifically this returns {@code true} if this filter is enabled for all bit indexes
     * identified by the {@code hasher}. Using the BitMap representations this is
     * effectively {@code (this AND hasher) == hasher}.
     *
     * @param hasher the hasher to provide the indexes
     * @return true if this filter is enabled for all bits specified by the hasher
     * @throws IllegalArgumentException if the hasher cannot generate indices for the shape of
     * this filter
     */
    default boolean contains(Hasher hasher) {
        Objects.requireNonNull( hasher, "Hasher");
        Shape shape = getShape();
        return contains( hasher.indices(shape));
    }

    /**
     * Returns {@code true} if this filter contains the indices specified IndexProducer.
     * Specifically this returns {@code true} if this filter is enabled for all bit indexes
     * identified by the {@code IndexProducer}.
     *
     * @param indexProducer the IndexProducer to provide the indexes
     * @return true if this filter is enabled for all bits specified by the IndexProducer
     */
    boolean contains(IndexProducer indexProducer);

    /**
     * Returns {@code true} if this filter contains the bits specified in the BitMaps produced by the
     * bitMapProducer.
     *
     * @param bitMapProducer the the {@code BitMapProducer} to provide the BitMaps.
     * @return true if this filter is enabled for all bits specified by the BitMaps
     */
    boolean contains(BitMapProducer bitMapProducer);

    /**
     * Merges the specified Bloom filter with this Bloom filter creating a new Bloom filter.
     * Specifically all bit indexes that are enabled in the {@code other} and in @code this} filter will be
     * enabled in the resulting filter.
     *
     * @param other the other Bloom filter
     * @return The new Bloom filter.
     */
    default BloomFilter merge(BloomFilter other) {
        Objects.requireNonNull( other, "other");
        Shape shape = getShape();
        BloomFilter result = BitMap.isSparse( (cardinality() + other.cardinality()), getShape() ) ?
                new SparseBloomFilter(shape) :
                    new SimpleBloomFilter(shape);

        result.mergeInPlace( this );
        result.mergeInPlace( other );
        return result;
    }

    /**
     * Merges the specified Hasher with this Bloom filter and returns a new Bloom filter.
     * Specifically all bit indexes that are identified by the {@code hasher} and in {@code this} Bloom filter
     * be enabled in the resulting filter.
     *
     * @param hasher the hasher to provide the indices
     * @return the new Bloom filter.
     */
    default BloomFilter merge(Hasher hasher) {
        Objects.requireNonNull( hasher, "hasher");
        Shape shape = getShape();
        BloomFilter result = BitMap.isSparse( (hasher.size() * shape.getNumberOfHashFunctions())+ cardinality(), shape ) ?
                new SparseBloomFilter(shape, hasher) :
                    new SimpleBloomFilter(shape, hasher);
        result.mergeInPlace( this );
        return result;
    }

    /**
     * Merges the specified Bloom filter into this Bloom filter. Specifically all
     * bit indexes that are identified by the {@code other} will be enabled in this filter.
     *
     * <p>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter is not ensured to contain
     * the specified Bloom filter.
     *
     * @param other The bloom filter to merge into this one.
     * @return true if the merge was successful
     */
    boolean mergeInPlace(BloomFilter other);

    /**
     * Merges the specified hasher into this Bloom filter. Specifically all
     * bit indexes that are identified by the {@code hasher} will be enabled in this filter.
     *
     * <p>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter is not ensured to contain
     * the specified Bloom filter.
     *
     * @param hasher The hasher to merge.
     * @return true if the merge was successful
     */
    default boolean mergeInPlace(Hasher hasher) {
        Objects.requireNonNull( hasher, "hasher");
        Shape shape = getShape();
        BloomFilter result = BitMap.isSparse( (hasher.size() * shape.getNumberOfHashFunctions())+cardinality(),shape ) ?
                new SparseBloomFilter(getShape(), hasher) :
                    new SimpleBloomFilter(getShape(), hasher);
        return mergeInPlace( result );
    }

    /**
     * Determines if the bloom filter is "full". Full is defined as having no unset
     * bits.
     *
     * @return true if the filter is full.
     */
    default boolean isFull(Shape shape) {
        Objects.requireNonNull( shape, "shape");
        return cardinality() == shape.getNumberOfBits();
    }

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
     * Estimates the number of items in the Bloom filter.
     * @return an estimate of the number of items in the bloom filter.
     */
    default int estimateN() {
        return (int) Math.round( getShape().estimateN( cardinality() ));
    }

    /**
     * Estimates the number of items in the union of this Bloom filter with the other bloom filter.
     * @param other The other Bloom filter
     * @return an estimate of the number of items in the union.
     */
    default int estimateUnion( BloomFilter other) {
        Objects.requireNonNull( other, "other");
        return this.merge( other ).estimateN();
    }

    /**
     * Estimates the number of items in the intersection of this Bloom filter with the other bloom filter.
     * @param other The other Bloom filter
     * @return an estimate of the number of items in the intersection.
     */
    default int estimateIntersection( BloomFilter other) {
        Objects.requireNonNull( other, "other");
        return estimateN() + other.estimateN() - estimateUnion(  other );
    }
}
