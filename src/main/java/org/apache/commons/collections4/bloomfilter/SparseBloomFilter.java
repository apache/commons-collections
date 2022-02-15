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
import java.util.TreeSet;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

/**
 * A bloom filter using a TreeSet of integers to track enabled bits. This is a standard
 * implementation and should work well for most low cardinality Bloom filters.
 * @since 4.5
 */
public final class SparseBloomFilter implements BloomFilter {

    /**
     * The bitSet that defines this BloomFilter.
     */
    private final TreeSet<Integer> indices;

    /**
     * The shape of this BloomFilter.
     */
    private final Shape shape;

    /**
     * Constructs an empty BitSetBloomFilter.
     *
     * @param shape The shape of the filter.
     */
    public SparseBloomFilter(Shape shape) {
        Objects.requireNonNull(shape, "shape");
        this.shape = shape;
        this.indices = new TreeSet<>();
    }

    /**
     * Creates an instance that is equivalent to {@code other}.
     *
     * @param other The bloom filter to copy.
     */
    public SparseBloomFilter(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        this.shape = other.getShape();
        this.indices = new TreeSet<>();
        if (other.isSparse()) {
            mergeInPlace((IndexProducer) other);
        } else {
            mergeInPlace(IndexProducer.fromBitMapProducer(other));
        }
    }

    private void checkIndices(Shape shape) {
        if (this.indices.floor(-1) != null || this.indices.ceiling(shape.getNumberOfBits()) != null) {
            throw new IllegalArgumentException(
                    String.format("Filter only accepts values in the [0,%d) range", shape.getNumberOfBits()));
        }
    }

    /**
     * Constructs a populated Bloom filter.
     * @param shape the shape for the bloom filter.
     * @param hasher the hasher to provide the initial data.
     */
    public SparseBloomFilter(final Shape shape, Hasher hasher) {
        this(shape);
        Objects.requireNonNull(hasher, "hasher");
        hasher.indices(shape).forEachIndex(this::add);
        checkIndices(shape);
    }

    /**
     * Constructs a populated Bloom filter.
     * @param shape the shape of the filter.
     * @param indices an index producer for the indices to to enable.
     * @throws IllegalArgumentException if indices contains a value greater than the number
     * of bits in the shape.
     */
    public SparseBloomFilter(Shape shape, IndexProducer indices) {
        this(shape);
        Objects.requireNonNull(indices, "indices");
        indices.forEachIndex(this::add);
        checkIndices(shape);
    }

    /**
     * Constructs a populated Bloom filter.
     * @param shape the shape of the filter.
     * @param bitMaps a BitMapProducer for the bit maps to add.
     * @throws IllegalArgumentException if the bit maps contain a value greater than the number
     * of bits in the shape.
     */
    public SparseBloomFilter(Shape shape, BitMapProducer bitMaps) {
        this(shape);
        Objects.requireNonNull(bitMaps, "bitMaps");
        mergeInPlace(IndexProducer
                .fromBitMapProducer(new CheckBitMapCount(bitMaps, BitMap.numberOfBitMaps(shape.getNumberOfBits()))));
    }

    @Override
    public long[] asBitMapArray() {
        long[] result = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
        for (int i : indices) {
            BitMap.set(result, i);
        }
        return result;
    }

    @Override
    public SparseBloomFilter copy() {
        SparseBloomFilter result = new SparseBloomFilter(shape);
        result.indices.addAll(indices);
        return result;
    }

    /**
     * Adds the index to the indices.
     * @param idx the index to add.
     * @return {@code true} always
     */
    private boolean add(int idx) {
        indices.add(idx);
        return true;
    }

    /**
     * Performs a merge in place using an IndexProducer.
     * @param indexProducer the IndexProducer to merge from.
     * @throws IllegalArgumentException if producer sends illegal value.
     */
    private void mergeInPlace(IndexProducer indexProducer) {
        indexProducer.forEachIndex(this::add);
        if (!this.indices.isEmpty()) {
            if (this.indices.last() >= shape.getNumberOfBits()) {
                throw new IllegalArgumentException(String.format("Value in list %s is greater than maximum value (%s)",
                        this.indices.last(), shape.getNumberOfBits()));
            }
            if (this.indices.first() < 0) {
                throw new IllegalArgumentException(
                        String.format("Value in list %s is less than 0", this.indices.first()));
            }
        }
    }

    @Override
    public boolean mergeInPlace(Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        mergeInPlace(hasher.indices(shape));
        return true;
    }

    @Override
    public boolean mergeInPlace(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        IndexProducer producer = other.isSparse() ? (IndexProducer) other : IndexProducer.fromBitMapProducer(other);
        mergeInPlace(producer);
        return true;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean isSparse() {
        return true;
    }

    @Override
    public int cardinality() {
        return indices.size();
    }

    @Override
    public boolean forEachIndex(IntPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (int value : indices) {
            if (!consumer.test(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean forEachBitMap(LongPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        int limit = BitMap.numberOfBitMaps(shape.getNumberOfBits());
        /*
         * because our indices are always in order we can shorten the time necessary to
         * create the longs for the consumer
         */
        // the currenlty constructed bitMap
        long bitMap = 0;
        // the bitmap we are working on
        int idx = 0;
        for (int i : indices) {
            while (BitMap.getLongIndex(i) != idx) {
                if (!consumer.test(bitMap)) {
                    return false;
                }
                bitMap = 0;
                idx++;
            }
            bitMap |= BitMap.getLongBit(i);
        }
        // we fall through with data in the bitMap
        if (!consumer.test(bitMap)) {
            return false;
        }
        // account for hte bitMap in the previous block + the next one
        idx++;
        // while there are more blocks to generate send zero to the consumer.
        while (idx < limit) {
            if (!consumer.test(0L)) {
                return false;
            }
            idx++;
        }
        return true;
    }

    @Override
    public boolean contains(IndexProducer indexProducer) {
        return indexProducer.forEachIndex(indices::contains);
    }

    @Override
    public boolean contains(BitMapProducer bitMapProducer) {
        return contains(IndexProducer.fromBitMapProducer(
                new CheckBitMapCount(bitMapProducer, BitMap.numberOfBitMaps(shape.getNumberOfBits()))));
    }
}
