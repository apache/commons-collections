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
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;

/**
 * A bloom filter using a Java BitSet to track enabled bits. This is a standard
 * implementation and should work well for most Bloom filters.
 * @since 4.5
 */
public class BitSetBloomFilter extends AbstractBloomFilter {

    /**
     * The bitset that defines this BloomFilter.
     */
    private final BitSet bitSet;

    /**
     * Constructs a BitSetBloomFilter from a hasher and a shape.
     *
     * @param hasher the Hasher to use.
     * @param shape the desired shape of the filter.
     */
    public BitSetBloomFilter(final Hasher hasher, final Shape shape) {
        this(shape);
        verifyHasher(hasher);
        hasher.getBits(shape).forEachRemaining((IntConsumer) bitSet::set);
    }

    /**
     * Constructs an empty BitSetBloomFilter.
     *
     * @param shape the desired shape of the filter.
     */
    public BitSetBloomFilter(final Shape shape) {
        super(shape);
        this.bitSet = new BitSet();
    }

    /**
     * Calculates the andCardinality with another BitSetBloomFilter. <p> This method takes
     * advantage of internal structures of BitSetBloomFilter. </p>
     *
     * @param other the other BitSetBloomFilter.
     * @return the cardinality of the result of {@code ( this AND other )}.
     * @see #andCardinality(BloomFilter)
     */
    @Override
    public int andCardinality(final BloomFilter other) {
        if (other instanceof BitSetBloomFilter) {
            verifyShape(other);
            final BitSet result = (BitSet) bitSet.clone();
            result.and(((BitSetBloomFilter)other).bitSet);
            return result.cardinality();
        }
        return super.andCardinality(other);
    }

    @Override
    public int cardinality() {
        return bitSet.cardinality();
    }

    @Override
    public boolean contains(final Hasher hasher) {
        verifyHasher(hasher);
        final OfInt iter = hasher.getBits(getShape());
        while (iter.hasNext()) {
            if (!bitSet.get(iter.nextInt())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long[] getBits() {
        return bitSet.toLongArray();
    }

    @Override
    public StaticHasher getHasher() {
        return new StaticHasher(bitSet.stream().iterator(), getShape());
    }

    @Override
    public void merge(final BloomFilter other) {
        verifyShape(other);
        if (other instanceof BitSetBloomFilter) {
            bitSet.or(((BitSetBloomFilter)other).bitSet);
        } else {
            bitSet.or(BitSet.valueOf(other.getBits()));
        }
    }


    @Override
    public void merge(final Hasher hasher) {
        verifyHasher(hasher);
        hasher.getBits(getShape()).forEachRemaining((IntConsumer) bitSet::set);
    }

    @Override
    public String toString() {
        return bitSet.toString();
    }


    @Override
    public int xorCardinality(final BloomFilter other) {
        if (other instanceof BitSetBloomFilter) {
            verifyShape(other);
            final BitSet result = (BitSet) bitSet.clone();
            result.xor(((BitSetBloomFilter)other).bitSet);
            return result.cardinality();
        }
        return super.xorCardinality(other);
    }

}
