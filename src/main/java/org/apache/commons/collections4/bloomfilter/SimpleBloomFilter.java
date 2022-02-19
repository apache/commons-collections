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
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

/**
 * A bloom filter using an array of bit maps to track enabled bits. This is a standard
 * implementation and should work well for most Bloom filters.
 * @since 4.5
 */
public final class SimpleBloomFilter implements BloomFilter {

    /**
     * The array of bit map longs that defines this Bloom filter.  Will be null if the filter is empty.
     */
    private final long[] bitMap;

    /**
     * The Shape of this Bloom filter.
     */
    private final Shape shape;

    /**
     * The cardinality of this Bloom filter.
     */
    private int cardinality;

    /**
     * Creates an empty instance.
     *
     * @param shape The shape for the filter.
     */
    public SimpleBloomFilter(Shape shape) {
        Objects.requireNonNull(shape, "shape");
        this.shape = shape;
        this.bitMap = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
        this.cardinality = 0;
    }

    /**
     * Creates an instance that is equivalent to {@code other}.
     *
     * @param other The bloom filter to copy.
     */
    public SimpleBloomFilter(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        this.shape = other.getShape();
        this.bitMap = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
        this.cardinality = 0;
        if (other.isSparse()) {
            mergeInPlace((IndexProducer) other);
        } else {
            mergeInPlace((BitMapProducer) other);
        }
    }

    /**
     * Creates a populated instance.
     * @param shape The shape for the filter.
     * @param hasher the Hasher to initialize the filter with.
     */
    public SimpleBloomFilter(final Shape shape, Hasher hasher) {
        this(shape);
        Objects.requireNonNull(hasher, "hasher");
        mergeInPlace(hasher);
    }

    /**
     * Creates a populated instance.
     * @param shape The shape for the filter.
     * @param indices the IndexProducer to initialize the filter with.
     * @throws IllegalArgumentException if producer sends illegal value.
     */
    public SimpleBloomFilter(final Shape shape, IndexProducer indices) {
        this(shape);
        Objects.requireNonNull(indices, "indices");
        mergeInPlace(indices);
    }

    /**
     * Creates a populated instance.
     * @param shape The shape for the filter.
     * @param bitMaps the BitMapProducer to initialize the filter with.
     * @throws IllegalArgumentException if the producer returns too many or too few bit maps.
     */
    public SimpleBloomFilter(final Shape shape, BitMapProducer bitMaps) {
        this(shape);
        Objects.requireNonNull(bitMaps, "bitMaps");
        mergeInPlace(bitMaps);
    }

    /**
     * Copy constructor for {@code copy()} use.
     * @param source
     */
    private SimpleBloomFilter(SimpleBloomFilter source) {
        this.shape = source.shape;
        this.bitMap = source.bitMap.clone();
        this.cardinality = source.cardinality;
    }

    @Override
    public long[] asBitMapArray() {
        return Arrays.copyOf(bitMap, bitMap.length);
    }

    @Override
    public boolean forEachBitMapPair(BitMapProducer other, LongBiPredicate func) {
        CountingLongPredicate p = new CountingLongPredicate(bitMap, func);
        return other.forEachBitMap(p) && p.forEachRemaining();
    }

    @Override
    public SimpleBloomFilter copy() {
        return new SimpleBloomFilter(this);
    }

    /**
     * Performs a merge in place using an IndexProducer.
     * @param indexProducer the IndexProducer to merge from.
     * @throws IllegalArgumentException if producer sends illegal value.
     */
    private void mergeInPlace(IndexProducer indexProducer) {
        indexProducer.forEachIndex(idx -> {
            if (idx < 0 || idx >= shape.getNumberOfBits()) {
                throw new IllegalArgumentException(String.format(
                        "IndexProducer should only send values in the range[0,%s]", shape.getNumberOfBits() - 1));
            }
            BitMap.set(bitMap, idx);
            return true;
        });
        cardinality = -1;
    }

    /**
     * Performs a merge in place using an BitMapProducer.
     * @param bitMapProducer the BitMapProducer to merge from.
     * @throws IllegalArgumentException if producer sends illegal value.
     */
    private void mergeInPlace(BitMapProducer bitMapProducer) {
        try {
            int[] idx = new int[1];
            bitMapProducer.forEachBitMap(value -> {
                bitMap[idx[0]++] |= value;
                return true;
            });
            // idx[0] will be limit+1 so decrement it
            idx[0]--;
            int idxLimit = BitMap.getLongIndex(shape.getNumberOfBits());
            if (idxLimit < idx[0]) {
                throw new IllegalArgumentException(String.format(
                        "BitMapProducer set a bit higher than the limit for the shape: %s", shape.getNumberOfBits()));
            }
            if (idxLimit == idx[0]) {
                long excess = (bitMap[idxLimit] >> shape.getNumberOfBits());
                if (excess != 0) {
                    throw new IllegalArgumentException(
                            String.format("BitMapProducer set a bit higher than the limit for the shape: %s",
                                    shape.getNumberOfBits()));
                }
            }
            cardinality = -1;
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    String.format("BitMapProducer should send at most %s maps", bitMap.length), e);
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
        if (other.isSparse()) {
            mergeInPlace((IndexProducer) other);
        } else {
            mergeInPlace((BitMapProducer) other);
        }
        return true;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public int cardinality() {
        // Lazy evaluation with caching
        if (cardinality < 0) {
            cardinality = SetOperations.cardinality(this);
        }
        return cardinality;
    }

    @Override
    public boolean forEachIndex(IntPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        return IndexProducer.fromBitMapProducer(this).forEachIndex(consumer);
    }

    @Override
    public boolean forEachBitMap(LongPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (long l : bitMap) {
            if (!consumer.test(l)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(IndexProducer indexProducer) {
        return indexProducer.forEachIndex(idx -> BitMap.contains(bitMap, idx));
    }
}
