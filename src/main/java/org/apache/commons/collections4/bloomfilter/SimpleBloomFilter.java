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
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import org.apache.commons.collections4.bloomfilter.exceptions.NoMatchException;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

/**
 * A bloom filter using an array of BitMaps to track enabled bits. This is a standard
 * implementation and should work well for most Bloom filters.
 * @since 4.5
 */
public class SimpleBloomFilter implements BloomFilter {

    /**
     * The array of BitMap longs that defines this Bloom filter.
     */
    private long[] bitMap;

    /**
     * The Shape of this Bloom filter
     */
    private final Shape shape;

    /**
     * The cardinality of this Bloom filter.
     */
    private int cardinality;

    /**
     * Constructs an empty SimpleBloomFilter.
     *
     * @param shape The shape for the filter.
     */
    public SimpleBloomFilter(Shape shape) {
        Objects.requireNonNull(shape, "shape");
        this.shape = shape;
        this.bitMap = new long[0];
        this.cardinality = 0;
    }

    /**
     * Constructor.
     * @param shape The shape for the filter.
     * @param hasher the Hasher to initialize the filter with.
     */
    public SimpleBloomFilter(final Shape shape, Hasher hasher) {
        Objects.requireNonNull(shape, "shape");
        Objects.requireNonNull(hasher, "hasher");
        this.shape = shape;
        this.bitMap = new long[0];
        mergeInPlace(hasher);
    }

    /**
     * Constructor.
     * @param shape The shape for the filter.
     * @param producer the BitMap Producer to initialize the filter with.
     * @throws IllegalArgumentException if the producer returns too many bit maps.
     */
    public SimpleBloomFilter(final Shape shape, BitMapProducer producer) {
        Objects.requireNonNull(shape, "shape");
        Objects.requireNonNull(producer, "producer");
        this.shape = shape;

        BitMapProducer.ArrayBuilder builder = new BitMapProducer.ArrayBuilder(shape);
        try {
            producer.forEachBitMap(builder);
            this.bitMap = builder.getArray();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException( String.format("BitMapProducer should only send %s maps",
                    BitMap.numberOfBitMaps( shape.getNumberOfBits())), e);
        }
        this.cardinality = -1;
    }

    @Override
    public boolean mergeInPlace(Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        Shape shape = getShape();

        hasher.indices(shape).forEachIndex(idx -> {
            int lidx = BitMap.getLongIndex(idx);
            if (bitMap.length <= lidx) {
                long[] newMap = new long[lidx + 1];
                System.arraycopy(bitMap, 0, newMap, 0, bitMap.length);
                bitMap = newMap;
            }
            BitMap.set(bitMap, idx);
        });
        this.cardinality = -1;
        return true;
    }

    @Override
    public boolean mergeInPlace(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        BitMapProducer.ArrayBuilder builder = new BitMapProducer.ArrayBuilder(shape, this.bitMap);
        other.forEachBitMap(builder);
        this.bitMap = builder.getArray();
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
                    forEachBitMap(w -> this.cardinality += Long.bitCount(w));
                }
            }
        }
        return this.cardinality;
    }

    @Override
    public void forEachIndex(IntConsumer consumer) {
        Objects.requireNonNull(consumer, "consumer");
        IndexProducer.fromBitMapProducer(this).forEachIndex(consumer);
    }

    @Override
    public void forEachBitMap(LongConsumer consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (long l : bitMap) {
            consumer.accept(l);
        }
    }

    @Override
    public boolean contains(IndexProducer indexProducer) {
        try {
            indexProducer.forEachIndex(idx -> {
                if (!BitMap.contains(bitMap, idx)) {
                    throw new NoMatchException();
                }
            });
            return true;
        } catch (NoMatchException e) {
            return false;
        }
    }

    @Override
    public boolean contains(BitMapProducer bitMapProducer) {
        LongConsumer consumer = new LongConsumer() {
            int i = 0;

            @Override
            public void accept(long w) {
                if ( i>= bitMap.length || (bitMap[i++] & w) != w) {
                    throw new NoMatchException();
                }
            }
        };
        try {
            bitMapProducer.forEachBitMap(consumer);
            return true;
        } catch (NoMatchException e) {
            return false;
        }

    }

}
