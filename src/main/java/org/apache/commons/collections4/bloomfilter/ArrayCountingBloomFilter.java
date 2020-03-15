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

import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;

/**
 * A counting Bloom filter using an array to track counts for each enabled bit
 * index.
 *
 * <p>Any operation that results in negative counts or integer overflow of counts will
 * mark this filter as invalid. This transition is not reversible. The counts for the
 * filter immediately prior to the operation that create invalid counts can be recovered.
 * See the documentation in {@link #isValid()} for details.
 *
 * <p>All the operations in the filter assume the counts are currently valid. Behaviour
 * of an invalid filter is undefined. It will no longer function identically to a standard
 * Bloom filter that is the merge of all the Bloom filters that have been added
 * to and not later subtracted from the counting Bloom filter.
 *
 * <p>The maximum supported number of items that can be stored in the filter is
 * limited by the maximum array size combined with the {@link Shape}. For
 * example an implementation using a {@link Shape} with a false-positive
 * probability of 1e-6 and {@link Integer#MAX_VALUE} bits can reversibly store
 * approximately 75 million items using 20 hash functions per item with a memory
 * consumption of approximately 8 GB.
 *
 * @since 4.5
 * @see Shape
 */
public class ArrayCountingBloomFilter extends AbstractBloomFilter implements CountingBloomFilter {

    /**
     * The count of each bit index in the filter.
     */
    private final int[] counts;

    /**
     * The state flag. This is a bitwise OR of the entire history of all updated
     * counts. If negative then a negative count or integer overflow has occurred on
     * one or more counts in the history of the filter and the state is invalid.
     *
     * <p>Maintenance of this state flag is branch-free for improved performance. It
     * eliminates a conditional check for a negative count during remove/subtract
     * operations and a conditional check for integer overflow during merge/add
     * operations.
     *
     * <p>Note: Integer overflow is unlikely in realistic usage scenarios. A count
     * that overflows indicates that the number of items in the filter exceeds the
     * maximum possible size (number of bits) of any Bloom filter constrained by
     * integer indices. At this point the filter is most likely full (all bits are
     * non-zero) and thus useless.
     *
     * <p>Negative counts are a concern if the filter is used incorrectly by
     * removing an item that was never added. It is expected that a user of a
     * counting Bloom filter will not perform this action as it is a mistake.
     * Enabling an explicit recovery path for negative or overflow counts is a major
     * performance burden not deemed necessary for the unlikely scenarios when an
     * invalid state is created. Maintenance of the state flag is a concession to
     * flag improper use that should not have a major performance impact.
     */
    private int state;

    /**
     * An iterator of all indexes with non-zero counts.
     *
     * <p>In the event that the filter state is invalid any index with a negative count
     * will also be produced by the iterator.
     */
    private class IndexIterator implements PrimitiveIterator.OfInt {
        /** The next non-zero index (or counts.length). */
        private int next;

        /**
         * Create an instance.
         */
        IndexIterator() {
            advance();
        }

        /**
         * Advance to the next non-zero index.
         */
        void advance() {
            while (next < counts.length && counts[next] == 0) {
                next++;
            }
        }

        @Override
        public boolean hasNext() {
            return next < counts.length;
        }

        @Override
        public int nextInt() {
            if (hasNext()) {
                final int result = next++;
                advance();
                return result;
            }
            // Currently unreachable as the iterator is only used by
            // the StaticHasher which iterates correctly.
            throw new NoSuchElementException();
        }
    }

    /**
     * Constructs an empty counting Bloom filter with the specified shape.
     *
     * @param shape the shape of the filter
     */
    public ArrayCountingBloomFilter(final Shape shape) {
        super(shape);
        counts = new int[shape.getNumberOfBits()];
    }

    /**
     * Constructs a counting Bloom filter from a hasher and a shape.
     *
     * <p>The filter will be equal to the result of merging the hasher with an empty
     * filter; specifically duplicate indexes in the hasher are ignored.
     *
     * @param hasher the hasher to build the filter from
     * @param shape the shape of the filter
     * @throws IllegalArgumentException if the hasher cannot generate indices for
     * the shape
     * @see #merge(Hasher)
     */
    public ArrayCountingBloomFilter(final Hasher hasher, final Shape shape) {
        super(shape);
        // Given the filter is empty we can optimise the operation of merge(hasher)
        verifyHasher(hasher);
        // Delay array allocation until after hasher is verified
        counts = new int[shape.getNumberOfBits()];
        // All counts are zero. Ignore duplicates by initialising to 1
        hasher.getBits(shape).forEachRemaining((IntConsumer) idx -> counts[idx] = 1);
    }

    @Override
    public int cardinality() {
        int size = 0;
        for (final int c : counts) {
            if (c != 0) {
                size++;
            }
        }
        return size;
    }

    @Override
    public boolean contains(BloomFilter other) {
        // The AbstractBloomFilter implementation converts both filters to long[] bits.
        // This would involve checking all indexes in this filter against zero.
        // Ideally we use an iterator of bit indexes to allow fail-fast on the
        // first bit index that is zero.
        if (other instanceof ArrayCountingBloomFilter) {
            verifyShape(other);
            return contains(((ArrayCountingBloomFilter) other).iterator());
        }

        // Note:
        // This currently creates a StaticHasher which stores all the indexes.
        // It would greatly benefit from direct generation of the index iterator
        // avoiding the intermediate storage.
        return contains(other.getHasher());
    }

    @Override
    public boolean contains(final Hasher hasher) {
        verifyHasher(hasher);
        return contains(hasher.getBits(getShape()));
    }

    /**
     * Return true if this filter is has non-zero counts for each index in the iterator.
     *
     * @param iter the iterator
     * @return true if this filter contains all the indexes
     */
    private boolean contains(final OfInt iter) {
        while (iter.hasNext()) {
            if (counts[iter.nextInt()] == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long[] getBits() {
        final BitSet bs = new BitSet();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] != 0) {
                bs.set(i);
            }
        }
        return bs.toLongArray();
    }

    @Override
    public StaticHasher getHasher() {
        return new StaticHasher(iterator(), getShape());
    }

    /**
     * Returns an iterator over the enabled indexes in this filter.
     * Any index with a non-zero count is considered enabled.
     * The iterator returns indexes in their natural order.
     *
     * @return an iterator over the enabled indexes
     */
    private PrimitiveIterator.OfInt iterator() {
        return new IndexIterator();
    }

    @Override
    public boolean merge(final BloomFilter other) {
        applyAsBloomFilter(other, this::increment);
        return isValid();
    }

    @Override
    public boolean merge(final Hasher hasher) {
        applyAsHasher(hasher, this::increment);
        return isValid();
    }

    @Override
    public boolean remove(BloomFilter other) {
        applyAsBloomFilter(other, this::decrement);
        return isValid();
    }

    @Override
    public boolean remove(Hasher hasher) {
        applyAsHasher(hasher, this::decrement);
        return isValid();
    }

    @Override
    public boolean add(CountingBloomFilter other) {
        applyAsCountingBloomFilter(other, this::add);
        return isValid();
    }

    @Override
    public boolean subtract(CountingBloomFilter other) {
        applyAsCountingBloomFilter(other, this::subtract);
        return isValid();
    }

    /**
     * {@inheritDoc}
     *
     * <p><em>Implementation note</em>
     *
     * <p>The state transition to invalid is permanent.
     *
     * <p>This implementation does not correct negative counts to zero or integer
     * overflow counts to {@link Integer#MAX_VALUE}. Thus the operation that
     * generated invalid counts can be reversed by using the complement of the
     * original operation with the same Bloom filter. This will restore the counts
     * to the state prior to the invalid operation. Counts can then be extracted
     * using {@link #forEachCount(BitCountConsumer)}.
     */
    @Override
    public boolean isValid() {
        return state >= 0;
    }

    @Override
    public void forEachCount(BitCountConsumer action) {
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] != 0) {
                action.accept(i, counts[i]);
            }
        }
    }

    /**
     * Apply the action for each index in the Bloom filter.
     */
    private void applyAsBloomFilter(final BloomFilter other, final IntConsumer action) {
        verifyShape(other);
        if (other instanceof ArrayCountingBloomFilter) {
            // Only use the presence of non-zero and not the counts
            final int[] counts2 = ((ArrayCountingBloomFilter) other).counts;
            for (int i = 0; i < counts2.length; i++) {
                if (counts2[i] != 0) {
                    action.accept(i);
                }
            }
        } else {
            BitSet.valueOf(other.getBits()).stream().forEach(action);
        }
    }

    /**
     * Apply the action for each index in the hasher.
     */
    private void applyAsHasher(final Hasher hasher, final IntConsumer action) {
        verifyHasher(hasher);
        // We do not naturally handle duplicates so filter them.
        IndexFilters.distinctIndexes(hasher, getShape(), action);
    }

    /**
     * Apply the action for each index in the Bloom filter.
     */
    private void applyAsCountingBloomFilter(final CountingBloomFilter other, final BitCountConsumer action) {
        verifyShape(other);
        other.forEachCount(action);
    }

    /**
     * Increment to the count for the bit index.
     *
     * @param idx the index
     */
    private void increment(int idx) {
        final int updated = counts[idx] + 1;
        state |= updated;
        counts[idx] = updated;
    }

    /**
     * Decrement from the count for the bit index.
     *
     * @param idx the index
     */
    private void decrement(int idx) {
        final int updated = counts[idx] - 1;
        state |= updated;
        counts[idx] = updated;
    }

    /**
     * Add to the count for the bit index.
     *
     * @param idx the index
     * @param addend the amount to add
     */
    private void add(int idx, int addend) {
        final int updated = counts[idx] + addend;
        state |= updated;
        counts[idx] = updated;
    }

    /**
     * Subtract from the count for the bit index.
     *
     * @param idx the index
     * @param subtrahend the amount to subtract
     */
    private void subtract(int idx, int subtrahend) {
        final int updated = counts[idx] - subtrahend;
        state |= updated;
        counts[idx] = updated;
    }
}
