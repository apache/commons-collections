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

import java.util.PrimitiveIterator.OfInt;
import java.util.function.LongBinaryOperator;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;

/**
 * An abstract Bloom filter providing default implementations for most Bloom filter
 * functions. Specific implementations are encouraged to override the methods that can be
 * more efficiently implemented.
 * <p>
 * This abstract class provides additional functionality not declared in the interface.
 * Specifically:
 * <ul>
 * <li>{@link #isFull()}</li>
 * </ul>
 *
 * @since 4.5
 */
public abstract class AbstractBloomFilter implements BloomFilter {

    /**
     * The shape used by this BloomFilter
     */
    private final Shape shape;

    /**
     * Construct a Bloom filter with the specified shape.
     *
     * @param shape The shape.
     */
    protected AbstractBloomFilter(final Shape shape) {
        this.shape = shape;
    }

    @Override
    public int andCardinality(final BloomFilter other) {
        verifyShape(other);
        final long[] mine = getBits();
        final long[] theirs = other.getBits();
        final int limit = Integer.min(mine.length, theirs.length);
        int count = 0;
        for (int i = 0; i < limit; i++) {
            count += Long.bitCount(mine[i] & theirs[i]);
        }
        return count;
    }

    @Override
    public int cardinality() {
        int count = 0;
        for (final long bits : getBits()) {
            count += Long.bitCount(bits);
        }
        return count;
    }

    @Override
    public boolean contains(final BloomFilter other) {
        verifyShape(other);
        return other.cardinality() == andCardinality(other);
    }

    @Override
    public boolean contains(final Hasher hasher) {
        verifyHasher(hasher);
        final long[] buff = getBits();

        final OfInt iter = hasher.iterator(shape);
        while (iter.hasNext()) {
            final int idx = iter.nextInt();
            BloomFilterIndexer.checkPositive(idx);
            final int buffIdx = BloomFilterIndexer.getLongIndex(idx);
            final long buffOffset = BloomFilterIndexer.getLongBit(idx);
            if ((buff[buffIdx] & buffOffset) == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public final Shape getShape() {
        return shape;
    }

    /**
     * Determines if the bloom filter is "full". Full is defined as having no unset
     * bits.
     *
     * @return true if the filter is full.
     */
    public final boolean isFull() {
        return cardinality() == getShape().getNumberOfBits();
    }

    @Override
    public int orCardinality(final BloomFilter other) {
        // Logical OR
        return opCardinality(other, (a, b) -> a | b);
    }

    /**
     * Verifies that the hasher has the same name as the shape.
     *
     * @param hasher the Hasher to check
     */
    protected void verifyHasher(final Hasher hasher) {
        // It is assumed that the filter and hasher have been constructed using the
        // same hash function. Use the signature for a fast check the hash function is equal.
        // Collisions will occur at a rate of 1 in 2^64.
        if (shape.getHashFunctionIdentity().getSignature() != hasher.getHashFunctionIdentity().getSignature()) {
            throw new IllegalArgumentException(
                String.format("Hasher (%s) is not the hasher for shape (%s)",
                    HashFunctionIdentity.asCommonString(hasher.getHashFunctionIdentity()),
                    shape.toString()));
        }
    }

    /**
     * Verify the other Bloom filter has the same shape as this Bloom filter.
     *
     * @param other the other filter to check.
     * @throws IllegalArgumentException if the shapes are not the same.
     */
    protected void verifyShape(final BloomFilter other) {
        verifyShape(other.getShape());
    }

    /**
     * Verify the specified shape has the same shape as this Bloom filter.
     *
     * @param shape the other shape to check.
     * @throws IllegalArgumentException if the shapes are not the same.
     */
    protected void verifyShape(final Shape shape) {
        if (!this.shape.equals(shape)) {
            throw new IllegalArgumentException(String.format("Shape %s is not the same as %s", shape, this.shape));
        }
    }

    @Override
    public int xorCardinality(final BloomFilter other) {
        // Logical XOR
        return opCardinality(other, (a, b) -> a ^ b);
    }

    /**
     * Perform the operation on the matched longs from this filter and the other filter
     * and count the cardinality.
     *
     * <p>The remaining unmatched longs from the larger filter are always counted. This
     * method is suitable for OR and XOR cardinality.
     *
     * @param other the other Bloom filter.
     * @param operation the operation (e.g. OR, XOR)
     * @return the cardinality
     */
    private int opCardinality(final BloomFilter other, final LongBinaryOperator operation) {
        verifyShape(other);
        final long[] mine = getBits();
        final long[] theirs = other.getBits();
        long[] small;
        long[] big;
        if (mine.length > theirs.length) {
            big = mine;
            small = theirs;
        } else {
            small = mine;
            big = theirs;
        }
        int count = 0;
        for (int i = 0; i < small.length; i++) {
            count += Long.bitCount(operation.applyAsLong(small[i], big[i]));
        }
        for (int i = small.length; i < big.length; i++) {
            count += Long.bitCount(big[i]);
        }
        return count;
    }
}
