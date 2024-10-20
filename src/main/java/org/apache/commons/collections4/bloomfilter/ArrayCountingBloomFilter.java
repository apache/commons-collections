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
import java.util.stream.IntStream;

/**
 * A counting Bloom filter using an int array to track cells for each enabled bit.
 *
 * <p>
 * Any operation that results in negative counts or integer overflow of counts will mark this filter as invalid. This transition is not reversible. The
 * operation is completed in full, no exception is raised and the state is set to invalid. This allows the cells for the filter immediately prior to the
 * operation that created the invalid state to be recovered. See the documentation in {@link #isValid()} for details.
 * </p>
 *
 * <p>
 * All the operations in the filter assume the cells are currently valid, for example {@code cardinality} or {@code contains} operations. Behavior of an invalid
 * filter is undefined. It will no longer function identically to a standard Bloom filter that is the merge of all the Bloom filters that have been added to and
 * not later subtracted from the counting Bloom filter.
 * </p>
 *
 * <p>
 * The maximum supported number of items that can be stored in the filter is limited by the maximum array size combined with the {@link Shape}. For example an
 * implementation using a {@link Shape} with a false-positive probability of 1e-6 and {@link Integer#MAX_VALUE} bits can reversibly store approximately 75
 * million items using 20 hash functions per item with a memory consumption of approximately 8 GB.
 * </p>
 *
 * @see Shape
 * @see CellExtractor
 * @since 4.5.0
 */
public final class ArrayCountingBloomFilter implements CountingBloomFilter {

    /**
     * The shape of this Bloom filter.
     */
    private final Shape shape;

    /**
     * The cell for each bit index in the filter.
     */
    private final int[] cells;

    /**
     * The state flag. This is a bitwise {@code OR} of the entire history of all updated
     * cells. If negative then a negative cell or integer overflow has occurred on
     * one or more cells in the history of the filter and the state is invalid.
     *
     * <p>Maintenance of this state flag is branch-free for improved performance. It
     * eliminates a conditional check for a negative cell during remove/subtract
     * operations and a conditional check for integer overflow during merge/add
     * operations.</p>
     *
     * <p>Note: Integer overflow is unlikely in realistic usage scenarios. A cell
     * that overflows indicates that the number of items in the filter exceeds the
     * maximum possible size (number of bits) of any Bloom filter constrained by
     * integer indices. At this point the filter is most likely full (all bits are
     * non-zero) and thus useless.</p>
     *
     * <p>Negative cells are a concern if the filter is used incorrectly by
     * removing an item that was never added. It is expected that a user of a
     * counting Bloom filter will not perform this action as it is a mistake.
     * Enabling an explicit recovery path for negative or overflow cells is a major
     * performance burden not deemed necessary for the unlikely scenarios when an
     * invalid state is created. Maintenance of the state flag is a concession to
     * flag improper use that should not have a major performance impact.</p>
     */
    private int state;

    private ArrayCountingBloomFilter(final ArrayCountingBloomFilter source) {
        this.shape = source.shape;
        this.state = source.state;
        this.cells = source.cells.clone();
    }

    /**
     * Constructs an empty counting Bloom filter with the specified shape.
     *
     * @param shape the shape of the filter
     */
    public ArrayCountingBloomFilter(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        this.shape = shape;
        cells = new int[shape.getNumberOfBits()];
    }

    @Override
    public boolean add(final CellExtractor other) {
        Objects.requireNonNull(other, "other");
        other.processCells(this::add);
        return isValid();
    }

    /**
     * Add to the cell for the bit index.
     *
     * @param idx the index
     * @param addend the amount to add
     * @return {@code true} always.
     */
    private boolean add(final int idx, final int addend) {
        try {
            final int updated = cells[idx] + addend;
            state |= updated;
            cells[idx] = updated;
            return true;
        } catch (final IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    String.format("Filter only accepts values in the [0,%d) range", getShape().getNumberOfBits()), e);
        }
    }

    @Override
    public int[] asIndexArray() {
        return IntStream.range(0, cells.length).filter(i -> cells[i] > 0).toArray();
    }

    @Override
    public int cardinality() {
        return (int) IntStream.range(0, cells.length).filter(i -> cells[i] > 0).count();
    }

    @Override
    public int characteristics() {
        return SPARSE;
    }

    @Override
    public void clear() {
        Arrays.fill(cells, 0);
    }

    @Override
    public boolean contains(final BitMapExtractor bitMapExtractor) {
        return contains(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
    }

    @Override
    public boolean contains(final IndexExtractor indexExtractor) {
        return indexExtractor.processIndices(idx -> cells[idx] != 0);
    }

    /**
     * Creates a new instance of this {@link ArrayCountingBloomFilter} with the same properties as the current one.
     *
     * @return a copy of this BloomFilter.
     */
    @Override
    public ArrayCountingBloomFilter copy() {
        return new ArrayCountingBloomFilter(this);
    }

    @Override
    public int getMaxCell() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxInsert(final CellExtractor cellExtractor) {
        final int[] max = { Integer.MAX_VALUE };
        cellExtractor.processCells((x, y) -> {
            final int count = cells[x] / y;
            if (count < max[0]) {
                max[0] = count;
            }
            return max[0] > 0;
        });
        return max[0];
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <em>Implementation note</em>
     * </p>
     *
     * <p>
     * The state transition to invalid is permanent.
     * </p>
     *
     * <p>
     * This implementation does not correct negative cells to zero or integer overflow cells to {@link Integer#MAX_VALUE}. Thus the operation that generated
     * invalid cells can be reversed by using the complement of the original operation with the same Bloom filter. This will restore the cells to the state
     * prior to the invalid operation. Cells can then be extracted using {@link #processCells(CellPredicate)}.
     * </p>
     */
    @Override
    public boolean isValid() {
        return state >= 0;
    }

    @Override
    public boolean processBitMaps(final LongPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        final int blocksm1 = BitMaps.numberOfBitMaps(cells.length) - 1;
        int i = 0;
        long value;
        // must break final block separate as the number of bits may not fall on the long boundary
        for (int j = 0; j < blocksm1; j++) {
            value = 0;
            for (int k = 0; k < Long.SIZE; k++) {
                if (cells[i++] != 0) {
                    value |= BitMaps.getLongBit(k);
                }
            }
            if (!consumer.test(value)) {
                return false;
            }
        }
        // Final block
        value = 0;
        for (int k = 0; i < cells.length; k++) {
            if (cells[i++] != 0) {
                value |= BitMaps.getLongBit(k);
            }
        }
        return consumer.test(value);
    }

    @Override
    public boolean processCells(final CellPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] != 0 && !consumer.test(i, cells[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean processIndices(final IntPredicate consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] != 0 && !consumer.test(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean subtract(final CellExtractor other) {
        Objects.requireNonNull(other, "other");
        other.processCells(this::subtract);
        return isValid();
    }

    /**
     * Subtracts from the cell for the bit index.
     *
     * @param idx the index
     * @param subtrahend the amount to subtract
     * @return {@code true} always.
     */
    private boolean subtract(final int idx, final int subtrahend) {
        try {
            final int updated = cells[idx] - subtrahend;
            state |= updated;
            cells[idx] = updated;
            return true;
        } catch (final IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    String.format("Filter only accepts values in the [0,%d) range", getShape().getNumberOfBits()), e);
        }
    }
}
