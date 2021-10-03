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

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

/**
 * The interface that describes a Bloom filter.
 * @since 4.5
 */
public interface BloomFilter extends IndexProducer, BitMapProducer {

    // Query Operations

    /**
     * This method is used to determine the best mechod for matching.  For `sparse` implementations the `getIndices()`
     * method is more efficient.  Implementers should determine if it is easier for the implementation to return am array of
     * Indices (sparse) or a bit map as an array of unsigned longs.
     * @return
     */
    boolean isSparse();

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
    default long[] getBits() {

        if (cardinality() == 0) {
            return new long[0];
        }

        BitMapProducer.ArrayBuilder consumer = new BitMapProducer.ArrayBuilder(getShape());
        forEachBitMap( consumer );
        return consumer.trim();
    }

    /**
     * Gets an array of indices of bits that are enabled.
     * Array must be in sorted order.
     * @return an array of indices for bits that are enabled in the filter.
     */
    default int[]  getIndices() {
        int[] result = new int[ cardinality() ];
        IntConsumer consumer = new IntConsumer() {
            int idx = 0;
            @Override
            public void accept(int i) {
                result[idx++] = i;
            }
        };
        forEachIndex( consumer );
        return result;
    }

    /**
     * Gets the shape that was used when the filter was built.
     * @return The shape the flter was built with.
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
        if (isSparse()) {
            int[] myIndicies = getIndices();
            if (other.isSparse()) {
                int[] otherIndicies = other.getIndices();
                if (otherIndicies.length > myIndicies.length) {
                    return false;
                }
                return Arrays.stream( otherIndicies ).allMatch( i -> Arrays.binarySearch( myIndicies, i) >= 0);
            } else {
                BitIterator iter = new BitIterator( other.getBits() );
                while (iter.hasNext())
                {
                    if (Arrays.binarySearch( myIndicies, iter.next()) < 0) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            long[] myBits = getBits();
            if (other.isSparse()) {
                return Arrays.stream( other.getIndices() ).allMatch( i -> BitMap.contains( myBits, i ));
            } else {
                long[] otherBits = other.getBits();
                if (myBits.length != otherBits.length)
                {
                    return false;
                }
                for (int i=0;i<myBits.length;i++)
                {
                    if ((myBits[i] & otherBits[i]) != otherBits[i])
                    {
                        return false;
                    }
                }
                return true;
            }
        }
    }

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
    default boolean contains(Hasher hasher) {
        Objects.requireNonNull( hasher, "Hasher");
        Shape shape = getShape();
        BloomFilter result = BitMap.isSparse( (hasher.size() * shape.getNumberOfHashFunctions()), shape ) ?
                new SparseBloomFilter(getShape(), hasher) :
                    new SimpleBloomFilter(getShape(), hasher);
        return contains( result );
    }

    // Modification Operations

    /**
     * Merges the specified Bloom filter withthis Bloom filter creating a new Bloom filter.
     * Specifically all bit indexes that are enabled in the {@code other} filter will be
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
     * Specifically all bit indexes that are identified by the {@code hasher} will be enabled
     * in the resulting filter.
     *
     *
     * @param hasher the hasher to provide the indexes
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

    /**
     * Iterates over the enabled bits in an array of bit maps.  Useful for when a
     * array of bitmaps is available but an iterator of indices is needed.
     *
     */
    public class BitIterator implements PrimitiveIterator.OfInt {
        private long[] bits;
        private int bucket;
        private int offset;
        private int next;

        /**
         * Constructs a bit iterator from an array of bit maps
         * @param bits the array of bit maps.
         */
        BitIterator( long[] bits ) {
            this.bits = bits;
            bucket = 0;
            offset = -1;
            next = -1;
        }
        @Override
        public boolean hasNext() {
            while (next<0 && bucket < bits.length) {

                offset++;
                if (offset>=64)
                {
                    offset=0;
                    bucket++;
                }
                if (bucket < bits.length && 0 != (bits[bucket] & (1L << offset))) {
                    next = (bucket*64)+offset;
                }
            }
            return next >= 0;
        }


        @SuppressWarnings("cast") // Cast to long to workaround a bug in animal-sniffer.
        @Override
        public int nextInt() {
            if (hasNext()) {
                try {
                    return next;
                } finally {
                    next = -1;
                }
            }
            throw new NoSuchElementException();
        }
    }

}
