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
 *
 * @since 4.5.0-M1
 */
public final class SparseBloomFilter implements BloomFilter<SparseBloomFilter> {

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
    public SparseBloomFilter(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        this.shape = shape;
        this.indices = new TreeSet<>();
    }

    private SparseBloomFilter(final SparseBloomFilter source) {
        shape = source.shape;
        indices = new TreeSet<>(source.indices);
    }

    /**
     * Adds the index to the indices.
     *
     * @param idx the index to add.
     * @return {@code true} always
     */
    private boolean add(final int idx) {
        indices.add(idx);
        return true;
    }

    @Override
    public long[] asBitMapArray() {
        final long[] result = BitMaps.newBitMap(shape);
        for (final int i : indices) {
            BitMaps.set(result, i);
        }
        return result;
    }

    @Override
    public int cardinality() {
        return indices.size();
    }

    @Override
    public int characteristics() {
        return SPARSE;
    }

    @Override
    public void clear() {
        indices.clear();
    }

    @Override
    public boolean contains(final BitMapExtractor bitMapExtractor) {
        return contains(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
    }

    @Override
    public boolean contains(final IndexExtractor indexExtractor) {
        return indexExtractor.processIndices(indices::contains);
    }

    /**
     * Creates a new instance of this {@link SparseBloomFilter} with the same properties as the current one.
     *
     * @return a copy of this {@link SparseBloomFilter}.
     */
    @Override
    public SparseBloomFilter copy() {
        return new SparseBloomFilter(this);
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean isEmpty() {
        return indices.isEmpty();
    }

    @Override
    public boolean merge(final BitMapExtractor bitMapExtractor) {
        Objects.requireNonNull(bitMapExtractor, "bitMapExtractor");
        return this.merge(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
    }

    @Override
    public boolean merge(final BloomFilter<?> other) {
        Objects.requireNonNull(other, "other");
        final IndexExtractor indexExtractor = (other.characteristics() & SPARSE) != 0 ? (IndexExtractor) other : IndexExtractor.fromBitMapExtractor(other);
        merge(indexExtractor);
        return true;
    }

    @Override
    public boolean merge(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        merge(hasher.indices(shape));
        return true;
    }

    @Override
    public boolean merge(final IndexExtractor indexExtractor) {
        Objects.requireNonNull(indexExtractor, "indexExtractor");
        indexExtractor.processIndices(this::add);
        if (!indices.isEmpty()) {
            if (indices.last() >= shape.getNumberOfBits()) {
                throw new IllegalArgumentException(String.format("Value in list %s is greater than maximum value (%s)",
                        indices.last(), shape.getNumberOfBits() - 1));
            }
            if (indices.first() < 0) {
                throw new IllegalArgumentException(
                        String.format("Value in list %s is less than 0", indices.first()));
            }
        }
        return true;
    }

    @Override
    public boolean processBitMaps(final LongPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        final int limit = BitMaps.numberOfBitMaps(shape);
        //
        // because our indices are always in order we can shorten the time necessary to
        // create the longs for the consumer
        //
        // the currently constructed bitMap
        long bitMap = 0;
        // the bitmap we are working on
        int idx = 0;
        for (final int i : indices) {
            while (BitMaps.getLongIndex(i) != idx) {
                if (!consumer.test(bitMap)) {
                    return false;
                }
                bitMap = 0;
                idx++;
            }
            bitMap |= BitMaps.getLongBit(i);
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
    public boolean processIndices(final IntPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        return indices.stream().allMatch(consumer::test);
    }
}
