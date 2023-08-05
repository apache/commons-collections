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
     * The array of bit map longs that defines this Bloom filter. Will be null if the filter is empty.
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
    public SimpleBloomFilter(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        this.shape = shape;
        this.bitMap = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
        this.cardinality = 0;
    }

    /**
     * Copy constructor for {@code copy()} use.
     * @param source
     */
    private SimpleBloomFilter(final SimpleBloomFilter source) {
        this.shape = source.shape;
        this.bitMap = source.bitMap.clone();
        this.cardinality = source.cardinality;
    }

    @Override
    public void clear() {
        Arrays.fill(bitMap, 0L);
        cardinality = 0;
    }

    @Override
    public long[] asBitMapArray() {
        return Arrays.copyOf(bitMap, bitMap.length);
    }

    @Override
    public boolean forEachBitMapPair(final BitMapProducer other, final LongBiPredicate func) {
        final CountingLongPredicate p = new CountingLongPredicate(bitMap, func);
        return other.forEachBitMap(p) && p.forEachRemaining();
    }

    @Override
    public SimpleBloomFilter copy() {
        return new SimpleBloomFilter(this);
    }

    @Override
    public boolean merge(final IndexProducer indexProducer) {
        Objects.requireNonNull(indexProducer, "indexProducer");
        indexProducer.forEachIndex(idx -> {
            if (idx < 0 || idx >= shape.getNumberOfBits()) {
                throw new IllegalArgumentException(String.format(
                        "IndexProducer should only send values in the range[0,%s)", shape.getNumberOfBits()));
            }
            BitMap.set(bitMap, idx);
            return true;
        });
        cardinality = -1;
        return true;
    }

    @Override
    public boolean merge(final BitMapProducer bitMapProducer) {
        Objects.requireNonNull(bitMapProducer, "bitMapProducer");
        try {
            final int[] idx = new int[1];
            bitMapProducer.forEachBitMap(value -> {
                bitMap[idx[0]++] |= value;
                return true;
            });
            // idx[0] will be limit+1 so decrement it
            idx[0]--;
            final int idxLimit = BitMap.getLongIndex(shape.getNumberOfBits());
            if (idxLimit == idx[0]) {
                final long excess = bitMap[idxLimit] >> shape.getNumberOfBits();
                if (excess != 0) {
                    throw new IllegalArgumentException(
                            String.format("BitMapProducer set a bit higher than the limit for the shape: %s",
                                    shape.getNumberOfBits()));
                }
            }
            cardinality = -1;
        } catch (final IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    String.format("BitMapProducer should send at most %s maps", bitMap.length), e);
        }
        return true;
    }

    @Override
    public boolean merge(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return merge(hasher.indices(shape));
    }

    @Override
    public boolean merge(final BloomFilter other) {
        Objects.requireNonNull(other, "other");
        if ((other.characteristics() & SPARSE) != 0) {
            merge((IndexProducer) other);
        } else {
            merge((BitMapProducer) other);
        }
        return true;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public int characteristics() {
        return 0;
    }

    @Override
    public int cardinality() {
        // Lazy evaluation with caching
        int c = cardinality;
        if (c < 0) {
            cardinality = c = SetOperations.cardinality(this);
        }
        return c;
    }

    @Override
    public boolean forEachIndex(final IntPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        return IndexProducer.fromBitMapProducer(this).forEachIndex(consumer);
    }

    @Override
    public boolean forEachBitMap(final LongPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (final long l : bitMap) {
            if (!consumer.test(l)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(final IndexProducer indexProducer) {
        return indexProducer.forEachIndex(idx -> BitMap.contains(bitMap, idx));
    }
}
