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
import java.util.Set;
import java.util.TreeSet;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
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
    /** The bit representation for an empty Bloom filter. */
    private static final long[] EMPTY = new long[0];

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
    public HasherBloomFilter(final Hasher hasher, final Shape shape) {
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
    public HasherBloomFilter(final Shape shape) {
        super(shape);
        this.hasher = new StaticHasher(EmptyIterator.emptyIterator(), shape);
    }

    @Override
    public int cardinality() {
        return hasher.size();
    }

    @Override
    public boolean contains(final Hasher hasher) {
        verifyHasher(hasher);
        final Set<Integer> set = new TreeSet<>();
        hasher.getBits(getShape()).forEachRemaining((IntConsumer) idx -> {
            set.add(idx);
        });
        final OfInt iter = this.hasher.getBits(getShape());
        while (iter.hasNext()) {
            final int idx = iter.nextInt();
            set.remove(idx);
            if (set.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long[] getBits() {
        if (hasher.size() == 0) {
            return EMPTY;
        }

        // Note: This can be simplified if the StaticHasher exposed a getMaxIndex()
        // method. Since it maintains an ordered list of unique indices the maximum
        // is the last value in the iterator. Knowing this value would allow
        // exact allocation of the long[].
        // For now we assume that the long[] will have a positive length and at least
        // 1 bit set in the entire array.

        final int n = (int) Math.ceil(hasher.getShape().getNumberOfBits() * (1.0 / Long.SIZE));
        final long[] result = new long[n];
        final OfInt iter = hasher.getBits(hasher.getShape());
        iter.forEachRemaining((IntConsumer) idx -> {
            BloomFilterIndexer.checkPositive(idx);
            final int buffIdx = BloomFilterIndexer.getLongIndex(idx);
            final long buffOffset = BloomFilterIndexer.getLongBit(idx);
            result[buffIdx] |= buffOffset;
        });

        int limit = result.length;

        // Assume the array has a non-zero length and at least 1 bit set.
        // This is tested using assertions.
        assert limit > 0 : "Number of bits in Shape is 0";
        while (result[limit - 1] == 0) {
            limit--;
            // If the hasher was not empty it is not possible to return
            // an array of length zero.
            assert limit > 0 : "Hasher reported a non-zero size but has no indices";
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
    public boolean merge(final BloomFilter other) {
        return merge(other.getHasher());
    }

    @Override
    public boolean merge(final Hasher hasher) {
        verifyHasher(hasher);
        final IteratorChain<Integer> iter = new IteratorChain<>(this.hasher.getBits(getShape()),
            hasher.getBits(getShape()));
        this.hasher = new StaticHasher(iter, getShape());
        return true;
    }
}
