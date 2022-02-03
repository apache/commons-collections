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
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

/**
 * A bloom filter using an array of bit maps to track enabled bits. This is a standard
 * implementation and should work well for most Bloom filters.
 * @since 4.5
 */
public class SimpleBloomFilter implements BloomFilter {

    /**
     * The array of bit map longs that defines this Bloom filter.  Will be null if the filter is empty.
     */
    private long[] bitMap;

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
        this.bitMap = null;
        this.cardinality = 0;
    }

    /**
     * Creates a populated instance.
     * @param shape The shape for the filter.
     * @param hasher the Hasher to initialize the filter with.
     */
    public SimpleBloomFilter(final Shape shape, Hasher hasher) {
        Objects.requireNonNull(shape, "shape");
        Objects.requireNonNull(hasher, "hasher");
        this.shape = shape;
        mergeInPlace(hasher);
    }

    /**
     * Creates a populated instance.
     * @param shape The shape for the filter.
     * @param producer the BitMapProducer to initialize the filter with.
     * @throws IllegalArgumentException if the producer returns too many bit maps.
     */
    public SimpleBloomFilter(final Shape shape, BitMapProducer producer) {
        Objects.requireNonNull(shape, "shape");
        Objects.requireNonNull(producer, "producer");
        this.shape = shape;
        needsBitMap();

        try {
            producer.forEachBitMap(new LongPredicate() {
                int idx = 0;

                @Override
                public boolean test(long value) {
                    bitMap[idx++] = value;
                    return true;
                }
            });
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.format("BitMapProducer should only send %s maps",
                    BitMap.numberOfBitMaps(shape.getNumberOfBits())), e);
        }
        this.cardinality = -1;
    }

    /**
     * Creates the bit map array if necessary.
     */
    private void needsBitMap() {
        if (bitMap == null) {
            bitMap = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
        }
    }

    @Override
    public boolean mergeInPlace(Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        needsBitMap();
        Shape shape = getShape();

        hasher.indices(shape).forEachIndex(idx -> {
            BitMap.set(bitMap, idx);
            return true;
        });
        this.cardinality = -1;
        return true;
    }

    @Override
    public boolean mergeInPlace(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        needsBitMap();
        other.forEachBitMap(new LongPredicate() {
            int idx = 0;

            @Override
            public boolean test(long value) {
                bitMap[idx++] |= value;
                return true;
            }
        });
        this.cardinality = -1;
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
        if (this.cardinality == -1) {
            synchronized (this) {
                if (this.cardinality == -1) {
                    this.cardinality = 0;
                    forEachBitMap(w -> {
                        this.cardinality += Long.bitCount(w);
                        return true;
                    });
                }
            }
        }
        return this.cardinality;
    }

    @Override
    public boolean forEachIndex(IntPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        return IndexProducer.fromBitMapProducer(this).forEachIndex(consumer);
    }

    @Override
    public boolean forEachBitMap(LongPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        if (bitMap != null) {
            for (long l : bitMap) {
                if (!consumer.test(l)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean contains(IndexProducer indexProducer) {
        needsBitMap();
        return indexProducer.forEachIndex(idx -> BitMap.contains(bitMap, idx));
    }

    @Override
    public boolean contains(BitMapProducer bitMapProducer) {
        if (bitMap != null) {
            LongPredicate consumer = new LongPredicate() {
                int i = 0;

                @Override
                public boolean test(long w) {
                    return i < bitMap.length && (bitMap[i++] & w) == w;
                }
            };

            return bitMapProducer.forEachBitMap(consumer);
        }
        return false;
    }
}
