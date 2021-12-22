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
import java.util.stream.IntStream;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

/**
 * A counting Bloom filter using an int array to track counts for each enabled bit
 * index.
 *
 * <p>Any operation that results in negative counts or integer overflow of
 * counts will mark this filter as invalid. This transition is not reversible.
 * The operation is completed in full, no exception is raised and the state is
 * set to invalid. This allows the counts for the filter immediately prior to the
 * operation that created the invalid state to be recovered. See the documentation
 * in {@link #isValid()} for details.</p>
 *
 * <p>All the operations in the filter assume the counts are currently valid,
 * for example {@code cardinality} or {@code contains} operations. Behavior of an invalid
 * filter is undefined. It will no longer function identically to a standard
 * Bloom filter that is the merge of all the Bloom filters that have been added
 * to and not later subtracted from the counting Bloom filter.</p>
 *
 * <p>The maximum supported number of items that can be stored in the filter is
 * limited by the maximum array size combined with the {@link Shape}. For
 * example an implementation using a {@link Shape} with a false-positive
 * probability of 1e-6 and {@link Integer#MAX_VALUE} bits can reversibly store
 * approximately 75 million items using 20 hash functions per item with a memory
 * consumption of approximately 8 GB.
 *
 * @see Shape
 * @since 4.5
 */
public class ArrayCountingBloomFilter implements CountingBloomFilter {

    /**
     * The shape of this Bloom filter.
     */
    private final Shape shape;

    /**
     * The count of each bit index in the filter.
     */
    private final int[] counts;

    /**
     * The state flag. This is a bitwise @{code OR} of the entire history of all updated
     * counts. If negative then a negative count or integer overflow has occurred on
     * one or more counts in the history of the filter and the state is invalid.
     *
     * <p>Maintenance of this state flag is branch-free for improved performance. It
     * eliminates a conditional check for a negative count during remove/subtract
     * operations and a conditional check for integer overflow during merge/add
     * operations.</p>
     *
     * <p>Note: Integer overflow is unlikely in realistic usage scenarios. A count
     * that overflows indicates that the number of items in the filter exceeds the
     * maximum possible size (number of bits) of any Bloom filter constrained by
     * integer indices. At this point the filter is most likely full (all bits are
     * non-zero) and thus useless.</p>
     *
     * <p>Negative counts are a concern if the filter is used incorrectly by
     * removing an item that was never added. It is expected that a user of a
     * counting Bloom filter will not perform this action as it is a mistake.
     * Enabling an explicit recovery path for negative or overflow counts is a major
     * performance burden not deemed necessary for the unlikely scenarios when an
     * invalid state is created. Maintenance of the state flag is a concession to
     * flag improper use that should not have a major performance impact.</p>
     */
    private int state;

    /**
     * Constructs an empty counting Bloom filter with the specified shape.
     *
     * @param shape the shape of the filter
     *
     */
    public ArrayCountingBloomFilter(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        this.shape = shape;
        counts = new int[shape.getNumberOfBits()];
    }

    @Override
    public boolean isSparse() {
        return true;
    }

    @Override
    public int cardinality() {
        return (int) IntStream.range(0, counts.length).filter(i -> counts[i] > 0).count();
    }

    /**
     * Clones the filter.  Used to create merged values.
     * @return A clone of this filter.
     */
    protected ArrayCountingBloomFilter makeClone() {
        ArrayCountingBloomFilter filter = new ArrayCountingBloomFilter(shape);
        filter.add(this);
        filter.state = this.state;
        return filter;
    }

    @Override
    public CountingBloomFilter merge(BloomFilter other) {
        Objects.requireNonNull(other, "other");
        CountingBloomFilter filter = makeClone();
        filter.add(BitCountProducer.from(other));
        return filter;
    }

    @Override
    public CountingBloomFilter merge(Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        ArrayCountingBloomFilter filter = makeClone();
        filter.add(BitCountProducer.from(hasher.indices(shape)));
        return filter;
    }

    @Override
    public boolean mergeInPlace(final BloomFilter other) {
        Objects.requireNonNull(other, "other");
        return add(BitCountProducer.from(other));
    }

    @Override
    public boolean mergeInPlace(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return add(BitCountProducer.from(hasher.indices(shape)));
    }

    @Override
    public boolean remove(final BloomFilter other) {
        Objects.requireNonNull(other, "other");
        return subtract(BitCountProducer.from(other));
    }

    @Override
    public boolean remove(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return subtract(BitCountProducer.from(hasher.indices(shape)));
    }

    @Override
    public boolean add(final BitCountProducer other) {
        Objects.requireNonNull(other, "other");
        other.forEachCount(this::add);
        return isValid();
    }

    @Override
    public boolean subtract(final BitCountProducer other) {
        Objects.requireNonNull(other, "other");
        other.forEachCount(this::subtract);
        return isValid();
    }

    /**
     * {@inheritDoc}
     *
     * <p><em>Implementation note</em>
     *
     * <p>The state transition to invalid is permanent.</p>
     *
     * <p>This implementation does not correct negative counts to zero or integer
     * overflow counts to {@link Integer#MAX_VALUE}. Thus the operation that
     * generated invalid counts can be reversed by using the complement of the
     * original operation with the same Bloom filter. This will restore the counts
     * to the state prior to the invalid operation. Counts can then be extracted
     * using {@link #forEachCount(BitCountConsumer)}.</p>
     */
    @Override
    public boolean isValid() {
        return state >= 0;
    }

    @Override
    public boolean forEachCount(final BitCountProducer.BitCountConsumer consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] != 0 && !consumer.test(i, counts[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean forEachIndex(IntPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] != 0 && !consumer.test(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean forEachBitMap(LongPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        return BitMapProducer.fromIndexProducer(this, shape.getNumberOfBits()).forEachBitMap(consumer);
    }

    /**
     * Add to the count for the bit index.
     *
     * @param idx the index
     * @param addend the amount to add
     * @return {@code true} always.
     */
    protected boolean add(final int idx, final int addend) {
        final int updated = counts[idx] + addend;
        state |= updated;
        counts[idx] = updated;
        return true;
    }

    /**
     * Subtract from the count for the bit index.
     *
     * @param idx the index
     * @param subtrahend the amount to subtract
     * @return {@code true} always.
     */
    protected boolean subtract(final int idx, final int subtrahend) {
        final int updated = counts[idx] - subtrahend;
        state |= updated;
        counts[idx] = updated;
        return true;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean contains(IndexProducer indexProducer) {
        return indexProducer.forEachIndex((idx) -> this.counts[idx] != 0);
    }

    @Override
    public boolean contains(BitMapProducer bitMapProducer) {
        return contains(IndexProducer.fromBitMapProducer(bitMapProducer));
    }

}
