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
import java.util.PrimitiveIterator.OfInt;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;

/**
 * An abstract Bloom filter providing default implementations for most Bloom filter
 * functions. Specific implementations are encouraged to override the methods that can be
 * more efficiently implemented.
 * @since 4.5
 */
public abstract class AbstractBloomFilter implements BloomFilter {

    /**
     * The shape used by this BloomFilter
     */
    private final BloomFilter.Shape shape;

    /**
     * Gets an array of little-endian long values representing the on bits of this filter.
     * bits 0-63 are in the first long.
     *
     * @return the LongBuffer representation of this filter.
     */
    @Override
    public abstract long[] getBits();

    /**
     * Creates a StaticHasher that contains the indexes of the bits that are on in this
     * filter.
     *
     * @return a StaticHasher for that produces this Bloom filter.
     */
    @Override
    public abstract StaticHasher getHasher();

    /**
     * Construct a Bloom filter with the specified shape.
     *
     * @param shape The shape.
     */
    protected AbstractBloomFilter(BloomFilter.Shape shape) {
        this.shape = shape;
    }

    /**
     * Verify the other Bloom filter has the same shape as this Bloom filter.
     *
     * @param other the other filter to check.
     * @throws IllegalArgumentException if the shapes are not the same.
     */
    protected void verifyShape(BloomFilter other) {
        verifyShape(other.getShape());
    }

    /**
     * Verify the specified shape has the same shape as this Bloom filter.
     *
     * @param shape the other shape to check.
     * @throws IllegalArgumentException if the shapes are not the same.
     */
    protected void verifyShape(BloomFilter.Shape shape) {
        if (!this.shape.equals(shape)) {
            throw new IllegalArgumentException(String.format("Shape %s is not the same as %s", shape, this.shape));
        }
    }

    /**
     * Verifies that the hasher has the same name as the shape.
     *
     * @param hasher the Hasher to check
     */
    protected void verifyHasher(Hasher hasher) {
        if (shape.getHashFunctionIdentity().getSignature() != hasher.getHashFunctionIdentity().getSignature()) {
            throw new IllegalArgumentException(
                String.format("Hasher (%s) is not the hasher for shape (%s)",
                    HashFunctionIdentity.asCommonString(hasher.getHashFunctionIdentity()),
                    shape.toString()));
        }
    }

    /**
     * Gets the shape of this filter.
     *
     * @return The shape of this filter.
     */
    @Override
    public final BloomFilter.Shape getShape() {
        return shape;
    }

    /**
     * Merge the other Bloom filter into this one.
     *
     * @param other the other Bloom filter.
     */
    @Override
    abstract public void merge(BloomFilter other);

    /**
     * Merge the decomposed Bloom filter defined by the hasher into this Bloom
     * filter. The hasher provides an iterator of bit indexes to enable.
     *
     * @param hasher the hasher to provide the indexes.
     * @throws IllegalArgumentException if the shape argument does not match the shape of
     * this filter, or if the hasher is not the specified one
     */
    @Override
    abstract public void merge(Hasher hasher);

    /**
     * Gets the cardinality of this Bloom filter.
     *
     * @return the cardinality (number of enabled bits) in this filter.
     */
    @Override
    public int cardinality() {
        return BitSet.valueOf(getBits()).cardinality();
    }

    /**
     * Performs a logical "AND" with the other Bloom filter and returns the cardinality of
     * the result.
     *
     * @param other the other Bloom filter.
     * @return the cardinality of the result of {@code ( this AND other )}.
     */
    @Override
    public int andCardinality(BloomFilter other) {
        verifyShape(other);
        long[] mine = getBits();
        long[] theirs = other.getBits();
        int limit = Integer.min(mine.length, theirs.length);
        long[] result = new long[limit];
        for (int i = 0; i < limit; i++) {
            result[i] = mine[i] & theirs[i];
        }
        return BitSet.valueOf(result).cardinality();
    }

    /**
     * Performs a logical "XOR" with the other Bloom filter and returns the cardinality of
     * the result.
     *
     * @param other the other Bloom filter.
     * @return the cardinality of the result of {@code( this XOR other )}
     */
    @Override
    public int xorCardinality(BloomFilter other) {
        verifyShape(other);
        long[] mine = getBits();
        long[] theirs = other.getBits();
        long[] remainder = null;
        long[] result = null;
        if (mine.length > theirs.length) {
            result = new long[mine.length];
            remainder = mine;
        } else {
            result = new long[theirs.length];
            remainder = theirs;

        }
        int limit = Integer.min(mine.length, theirs.length);
        for (int i = 0; i < limit; i++) {
            result[i] = mine[i] ^ theirs[i];
        }
        if (limit<result.length)
        {
            System.arraycopy(remainder, limit, result, limit, result.length-limit);
        }
        return BitSet.valueOf(result).cardinality();
    }

    /**
     * Performs a contains check. Effectively this AND other == other.
     *
     * @param other the Other Bloom filter.
     * @return true if this filter matches the other.
     */
    @Override
    public boolean contains(BloomFilter other) {
        verifyShape(other);
        return other.cardinality() == andCardinality(other);
    }

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
    @Override
    public boolean contains(Hasher hasher) {
        verifyHasher( hasher );
        long[] buff = getBits();

        OfInt iter = hasher.getBits(shape);
        while (iter.hasNext()) {
            int idx = iter.nextInt();
            int buffIdx = idx / Long.SIZE;
            int pwr = Math.floorMod(idx, Long.SIZE);
            long buffOffset = 1L << pwr;
            if ((buff[buffIdx] & buffOffset) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the Hamming value of this Bloom filter.
     *
     * @return the hamming value.
     */
    @Override
    public int hammingValue() {
        return cardinality();
    }

    /**
     * Gets the Hamming distance to the other Bloom filter.
     *
     * @param other the Other bloom filter.
     * @return the Hamming distance.
     */
    @Override
    public int hammingDistance(BloomFilter other) {
        verifyShape(other);
        return xorCardinality(other);
    }

}
