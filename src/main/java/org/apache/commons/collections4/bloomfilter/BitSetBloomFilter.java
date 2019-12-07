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

import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;

/**
 * A bloom filter using a Java BitSet to track enabled bits. This is a standard
 * implementation and should work well for most Bloom filters.
 * @since 4.5
 */
public class BitSetBloomFilter extends BloomFilter {

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
    public BitSetBloomFilter(Hasher hasher, Shape shape) {
        this(shape);
        verifyHasher(hasher);
        hasher.getBits(shape).forEachRemaining((IntConsumer) bitSet::set);
    }

    /**
     * Constructs an empty BitSetBloomFilter.
     *
     * @param shape the desired shape of the filter.
     */
    public BitSetBloomFilter(Shape shape) {
        super(shape);
        this.bitSet = new BitSet();
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
    public void merge(BloomFilter other) {
        verifyShape(other);
        bitSet.or(BitSet.valueOf(other.getBits()));
    }

    @Override
    public boolean contains(Hasher hasher) {
        verifyHasher(hasher);
        OfInt iter = hasher.getBits(getShape());
        while (iter.hasNext()) {
            if (!bitSet.get(iter.nextInt())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hammingValue() {
        return bitSet.cardinality();
    }

    @Override
    public String toString() {
        return bitSet.toString();
    }

    /**
     * Merge another BitSetBloomFilter into this one. <p> This method takes advantage of
     * internal structures of BitSetBloomFilter. </p>
     *
     * @param other the other BitSetBloomFilter.
     * @see #merge(BloomFilter)
     */
    public void merge(BitSetBloomFilter other) {
        verifyShape(other);
        bitSet.or(other.bitSet);
    }

    @Override
    public void merge(Hasher hasher) {
        verifyHasher(hasher);
        hasher.getBits(getShape()).forEachRemaining((IntConsumer) bitSet::set);
    }

    /**
     * Calculates the andCardinality with another BitSetBloomFilter. <p> This method takes
     * advantage of internal structures of BitSetBloomFilter. </p>
     *
     * @param other the other BitSetBloomFilter.
     * @return the cardinality of the result of {@code ( this AND other )}.
     * @see #andCardinality(BloomFilter)
     */
    public int andCardinality(BitSetBloomFilter other) {
        verifyShape(other);
        BitSet result = (BitSet) bitSet.clone();
        result.and(other.bitSet);
        return result.cardinality();
    }

    /**
     * Calculates the orCardinality with another BitSetBloomFilter. <p> This method takes
     * advantage of internal structures of BitSetBloomFilter. </p>
     *
     * @param other the other BitSetBloomFilter.
     * @return the cardinality of the result of {@code ( this OR other )}.
     * @see #orCardinality(BloomFilter)
     */
    public int orCardinality(BitSetBloomFilter other) {
        verifyShape(other);
        BitSet result = (BitSet) bitSet.clone();
        result.or(other.bitSet);
        return result.cardinality();
    }

    /**
     * Calculates the xorCardinality with another BitSetBloomFilter. <p> This method takes
     * advantage of internal structures of BitSetBloomFilter. </p>
     *
     * @param other the other BitSetBloomFilter.
     * @return the cardinality of the result of {@code( this XOR other )}
     * @see #xorCardinality(BloomFilter)
     */
    public int xorCardinality(BitSetBloomFilter other) {
        verifyShape(other);
        BitSet result = (BitSet) bitSet.clone();
        result.xor(other.bitSet);
        return result.cardinality();
    }

}
