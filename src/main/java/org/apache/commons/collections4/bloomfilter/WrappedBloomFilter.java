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

import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

/**
 * An abstract class to assist in implementing Bloom filter decorators.
 *
 * @param <T> The WrappedBloomFilter type.
 * @param <W> The <em>wrapped</em> BloomFilter type.
 * @since 4.5.0-M1
 */
public abstract class WrappedBloomFilter<T extends WrappedBloomFilter<T, W>, W extends BloomFilter<W>> implements BloomFilter<T> {

    private final W wrapped;

    /**
     * Wraps a Bloom filter.  The wrapped filter is maintained as a reference
     * not a copy.  Changes in one will be reflected in the other.
     *
     * @param wrapped The Bloom filter.
     */
    public WrappedBloomFilter(final W wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public long[] asBitMapArray() {
        return wrapped.asBitMapArray();
    }

    @Override
    public int[] asIndexArray() {
        return wrapped.asIndexArray();
    }

    @Override
    public int cardinality() {
        return wrapped.cardinality();
    }

    @Override
    public int characteristics() {
        return wrapped.characteristics();
    }

    @Override
    public void clear() {
        wrapped.clear();
    }

    @Override
    public boolean contains(final BitMapExtractor bitMapExtractor) {
        return wrapped.contains(bitMapExtractor);
    }

    @Override
    public boolean contains(final BloomFilter<?> other) {
        return wrapped.contains(other);
    }

    @Override
    public boolean contains(final Hasher hasher) {
        return wrapped.contains(hasher);
    }

    @Override
    public boolean contains(final IndexExtractor indexExtractor) {
        return wrapped.contains(indexExtractor);
    }

    @Override
    public int estimateIntersection(final BloomFilter<?> other) {
        return wrapped.estimateIntersection(other);
    }

    @Override
    public int estimateN() {
        return wrapped.estimateN();
    }

    @Override
    public int estimateUnion(final BloomFilter<?> other) {
        return wrapped.estimateUnion(other);
    }

    @Override
    public Shape getShape() {
        return wrapped.getShape();
    }

    /**
     * Gets the wrapped BloomFilter.
     *
     * @return the wrapped BloomFilter.
     */
    protected W getWrapped() {
        return wrapped;
    }

    @Override
    public boolean isFull() {
        return wrapped.isFull();
    }

    @Override
    public boolean merge(final BitMapExtractor bitMapExtractor) {
        return wrapped.merge(bitMapExtractor);
    }

    @Override
    public boolean merge(final BloomFilter<?> other) {
        return wrapped.merge(other);
    }

    @Override
    public boolean merge(final Hasher hasher) {
        return wrapped.merge(hasher);
    }

    @Override
    public boolean merge(final IndexExtractor indexExtractor) {
        return wrapped.merge(indexExtractor);
    }

    @Override
    public boolean processBitMapPairs(final BitMapExtractor other, final LongBiPredicate func) {
        return wrapped.processBitMapPairs(other, func);
    }

    @Override
    public boolean processBitMaps(final LongPredicate predicate) {
        return wrapped.processBitMaps(predicate);
    }

    @Override
    public boolean processIndices(final IntPredicate predicate) {
        return wrapped.processIndices(predicate);
    }
}
