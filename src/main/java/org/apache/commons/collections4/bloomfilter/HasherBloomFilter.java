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

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.IteratorChain;

/**
 * A Bloom filter built on a single hasher. This filter type should only be used for small
 * filters (few on bits).  While this implementation correctly supports the merge() methods
 * it is recommended that if merges are expected that one of the other Bloom filter
 * implementations be used.
 * @since 4.5
 */
public class HasherBloomFilter extends AbstractBloomFilter {

    /**
     * The internal hasher representation.
     */
    private StaticHasher hasher;

    /**
     * Constructs a HasherBloomFilter from a hasher and a shape.
     *
     * @param hasher the hasher to use.
     * @param shape the shape of the Bloom filter.
     */
    public HasherBloomFilter(Hasher hasher, BloomFilter.Shape shape) {
        super(shape);
        verifyHasher(hasher);
        if (hasher instanceof StaticHasher) {
            this.hasher = (StaticHasher) hasher;
            verifyShape(this.hasher.getShape());
        } else {
            this.hasher = new StaticHasher(hasher, shape);
        }
    }

    /**
     * Constructs an empty HasherBloomFilter from a shape.
     *
     * @param shape the shape of the Bloom filter.
     */
    public HasherBloomFilter(BloomFilter.Shape shape) {
        super(shape);
        this.hasher = new StaticHasher(EmptyIterator.emptyIterator(), shape);
    }

    @Override
    public long[] getBits() {
        if (hasher.size() == 0) {
            return new long[0];
        }
        int n = (int) Math.ceil(hasher.getShape().getNumberOfBits() * 1.0 / Long.SIZE);
        long[] result = new long[n];
        OfInt iter = hasher.getBits(hasher.getShape());
        iter.forEachRemaining((IntConsumer) idx -> {
            long buff = result[idx / Long.SIZE];
            long pwr = Math.floorMod(idx, Long.SIZE);
            long buffOffset = 1L << pwr;
            buff |= buffOffset;
            result[idx / Long.SIZE] = buff;
        });

        int limit = result.length;
        while (limit > 0 && result[limit - 1] == 0) {
            limit--;
        }
        if (limit == 0) {
            return new long[0];
        }
        if (limit < result.length) {
            return Arrays.copyOf(result, limit);
        }
        return result;
    }

    @Override
    public StaticHasher getHasher() {
        return hasher;
    }

    @Override
    public void merge(BloomFilter other) {
        merge(other.getHasher());
    }

    @Override
    public void merge(Hasher hasher) {
        verifyHasher(hasher);
        IteratorChain<Integer> iter = new IteratorChain<Integer>(this.hasher.getBits(getShape()),
            hasher.getBits(getShape()));
        this.hasher = new StaticHasher(iter, getShape());
    }

    @Override
    public int cardinality() {
        return hasher.size();
    }

    @Override
    public boolean contains(Hasher hasher) {
        verifyHasher(hasher);
        Set<Integer> set = new TreeSet<Integer>();
        hasher.getBits(getShape()).forEachRemaining((IntConsumer) idx -> {
            set.add(idx);
        });
        OfInt iter = this.hasher.getBits(getShape());
        while (iter.hasNext()) {
            int idx = iter.nextInt();
            set.remove(idx);
            if (set.isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
