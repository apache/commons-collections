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
 * @since 4.5
 */
public abstract class WrappedBloomFilter implements BloomFilter {
    final BloomFilter wrapped;

    /**
     * Wraps a Bloom filter.  The wrapped filter is maintained as a reference
     * not a copy.  Changes in one will be reflected in the other.
     * @param bf The Bloom filter.
     */
    public WrappedBloomFilter(BloomFilter bf) {
        this.wrapped = bf;
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
    public boolean contains(BitMapProducer bitMapProducer) {
        return wrapped.contains(bitMapProducer);
    }

    @Override
    public boolean contains(BloomFilter other) {
        return wrapped.contains(other);
    }

    @Override
    public boolean contains(Hasher hasher) {
        return wrapped.contains(hasher);
    }

    @Override
    public boolean contains(IndexProducer indexProducer) {
        return wrapped.contains(indexProducer);
    }

    @Override
    public BloomFilter copy() {
        return wrapped.copy();
    }

    @Override
    public int estimateIntersection(BloomFilter other) {
        return wrapped.estimateIntersection(other);
    }

    @Override
    public int estimateN() {
        return wrapped.estimateN();
    }

    @Override
    public int estimateUnion(BloomFilter other) {
        return wrapped.estimateUnion(other);
    }

    @Override
    public boolean forEachBitMap(LongPredicate predicate) {
        return wrapped.forEachBitMap(predicate);
    }

    @Override
    public boolean forEachBitMapPair(BitMapProducer other, LongBiPredicate func) {
        return wrapped.forEachBitMapPair(other, func);
    }

    @Override
    public boolean forEachIndex(IntPredicate predicate) {
        return wrapped.forEachIndex(predicate);
    }

    @Override
    public Shape getShape() {
        return wrapped.getShape();
    }

    @Override
    public boolean isFull() {
        return wrapped.isFull();
    }

    @Override
    public boolean merge(BitMapProducer bitMapProducer) {
        return wrapped.merge(bitMapProducer);
    }

    @Override
    public boolean merge(BloomFilter other) {
        return wrapped.merge(other);
    }

    @Override
    public boolean merge(Hasher hasher) {
        return wrapped.merge(hasher);
    }

    @Override
    public boolean merge(IndexProducer indexProducer) {
        return wrapped.merge(indexProducer);
    }
}
