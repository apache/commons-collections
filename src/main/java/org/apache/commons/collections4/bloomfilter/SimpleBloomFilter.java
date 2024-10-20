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
 * A bloom filter using an array of bit maps to track enabled bits. This is a standard implementation and should work well for most Bloom filters.
 *
 * @since 4.5.0-M1
 */
public final class SimpleBloomFilter implements BloomFilter<SimpleBloomFilter> {

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
        this.bitMap = BitMaps.newBitMap(shape);
        this.cardinality = 0;
    }

    /**
     * Copy constructor for {@code copy()} use.
     *
     * @param source
     */
    private SimpleBloomFilter(final SimpleBloomFilter source) {
        this.shape = source.shape;
        this.bitMap = source.bitMap.clone();
        this.cardinality = source.cardinality;
    }

    @Override
    public long[] asBitMapArray() {
        return Arrays.copyOf(bitMap, bitMap.length);
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
    public int characteristics() {
        return 0;
    }

    @Override
    public void clear() {
        Arrays.fill(bitMap, 0L);
        cardinality = 0;
    }

    @Override
    public boolean contains(final IndexExtractor indexExtractor) {
        return indexExtractor.processIndices(idx -> BitMaps.contains(bitMap, idx));
    }

    /**
     * Creates a new instance of this {@link SimpleBloomFilter} with the same properties as the current one.
     *
     * @return a copy of this {@link SimpleBloomFilter}.
     */
    @Override
    public SimpleBloomFilter copy() {
        return new SimpleBloomFilter(this);
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean isEmpty() {
        return cardinality == 0 || processBitMaps(y -> y == 0);
    }

    @Override
    public boolean merge(final BitMapExtractor bitMapExtractor) {
        Objects.requireNonNull(bitMapExtractor, "bitMapExtractor");
        try {
            final int[] idx = new int[1];
            bitMapExtractor.processBitMaps(value -> {
                bitMap[idx[0]++] |= value;
                return true;
            });
            // idx[0] will be limit+1 so decrement it
            idx[0]--;
            final int idxLimit = BitMaps.getLongIndex(shape.getNumberOfBits());
            if (idxLimit == idx[0]) {
                final long excess = bitMap[idxLimit] >> shape.getNumberOfBits();
                if (excess != 0) {
                    throw new IllegalArgumentException(
                            String.format("BitMapExtractor set a bit higher than the limit for the shape: %s", shape.getNumberOfBits()));
                }
            }
            cardinality = -1;
        } catch (final IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.format("BitMapExtractor should send at most %s maps", bitMap.length), e);
        }
        return true;
    }

    @Override
    public boolean merge(final BloomFilter<?> other) {
        Objects.requireNonNull(other, "other");
        if ((other.characteristics() & SPARSE) != 0) {
            merge((IndexExtractor) other);
        } else {
            merge((BitMapExtractor) other);
        }
        return true;
    }

    @Override
    public boolean merge(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return merge(hasher.indices(shape));
    }

    @Override
    public boolean merge(final IndexExtractor indexExtractor) {
        Objects.requireNonNull(indexExtractor, "indexExtractor");
        indexExtractor.processIndices(idx -> {
            if (idx < 0 || idx >= shape.getNumberOfBits()) {
                throw new IllegalArgumentException(String.format("IndexExtractor should only send values in the range[0,%s)", shape.getNumberOfBits()));
            }
            BitMaps.set(bitMap, idx);
            return true;
        });
        cardinality = -1;
        return true;
    }

    @Override
    public boolean processBitMapPairs(final BitMapExtractor other, final LongBiPredicate func) {
        final CountingLongPredicate p = new CountingLongPredicate(bitMap, func);
        return other.processBitMaps(p) && p.processRemaining();
    }

    @Override
    public boolean processBitMaps(final LongPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (final long l : bitMap) {
            if (!consumer.test(l)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean processIndices(final IntPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        return IndexExtractor.fromBitMapExtractor(this).processIndices(consumer);
    }
}
