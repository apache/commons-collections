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

/**
 * The interface that describes a Bloom filter that associates a count with each
 * bit index rather than a bit.  This allows reversal of merge operations with
 * remove operations.
 *
 * <p>A counting Bloom filter is expected to function identically to a standard
 * Bloom filter that is the merge of all the Bloom filters that have been added
 * to and not later subtracted from the counting Bloom filter. The functional
 * state of a CountingBloomFilter at the start and end of a series of merge and
 * subsequent remove operations of the same Bloom filters, irrespective of
 * remove order, is expected to be the same.</p>
 *
 * <p>Removal of a filter that has not previously been merged results in an
 * invalid state where the cells no longer represent a sum of merged Bloom
 * filters. It is impossible to validate merge and remove exactly without
 * explicitly storing all filters. Consequently such an operation may go
 * undetected. The CountingBloomFilter maintains a state flag that is used as a
 * warning that an operation was performed that resulted in invalid cells and
 * thus an invalid state. For example this may occur if a cell for an index was
 * set to negative following a remove operation.</p>
 *
 * <p>Implementations should document the expected state of the filter after an
 * operation that generates invalid cells, and any potential recovery options.
 * An implementation may support a reversal of the operation to restore the
 * state to that prior to the operation. In the event that invalid cells are
 * adjusted to a valid range then it should be documented if there has been
 * irreversible information loss.</p>
 *
 * <p>Implementations may choose to throw an exception during an operation that
 * generates invalid cells. Implementations should document the expected state
 * of the filter after such an operation. For example are the cells not updated,
 * partially updated or updated entirely before the exception is raised.</p>
 *
 * @see CellProducer
 * @since 4.5
 */
public interface CountingBloomFilter extends BloomFilter, CellProducer {

    // Query Operations

    /**
     * Adds the specified CellProducer to this Bloom filter.
     *
     * <p>Specifically
     * all cells for the indexes identified by the {@code other} will be incremented
     * by their corresponding values in the {@code other}.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param other the CellProducer to add.
     * @return {@code true} if the addition was successful and the state is valid
     * @see #isValid()
     * @see #subtract(CellProducer)
     */
    boolean add(CellProducer other);

    /**
     * Creates a new instance of the CountingBloomFilter with the same properties as the current one.
     * @return a copy of this CountingBloomFilter
     */
    @Override
    CountingBloomFilter copy();

    /**
     * Returns the maximum allowable value for a cell count in this Counting filter.
     * @return the maximum allowable value for a cell count in this Counting filter.
     */
    int getMaxCell();

    /**
     * Determines the maximum number of times the BitMapProducer could have been merged into this
     * counting filter.
     * @param bitMapProducer the BitMapProducer to provide the indices.
     * @return the maximum number of times the BitMapProducer could have been inserted.
     */
    default int getMaxInsert(final BitMapProducer bitMapProducer) {
        if (!contains(bitMapProducer)) {
            return 0;
        }
        final long[] bitMaps = bitMapProducer.asBitMapArray();
        final int[] max = { Integer.MAX_VALUE };
        forEachCell((x, y) -> {
            if ((bitMaps[BitMap.getLongIndex(x)] & BitMap.getLongBit(x)) != 0) {
                max[0] = max[0] <= y ? max[0] : y;
            }
            return true;
        });
        return max[0];
    }

    /**
     * Determines the maximum number of times the Bloom filter could have been merged
     * into this counting filter.
     * @param bloomFilter the Bloom filter the check for.
     * @return the maximum number of times the Bloom filter could have been inserted.
     */
    default int getMaxInsert(final BloomFilter bloomFilter) {
        return getMaxInsert((BitMapProducer) bloomFilter);
    }

    /**
     * Determines the maximum number of times the Cell Producer could have been add.
     * @param cellProducer the producer of cells.
     * @return the maximum number of times the CellProducer could have been inserted.
     */
    int getMaxInsert(CellProducer cellProducer);

    /**
     * Determines the maximum number of times the Hasher could have been merged into this
     * counting filter.
     * @param hasher the Hasher to provide the indices.
     * @return the maximum number of times the hasher could have been inserted.
     */
    default int getMaxInsert(final Hasher hasher) {
        return getMaxInsert(hasher.indices(getShape()));
    }

    // Modification Operations

    /**
     * Determines the maximum number of times the IndexProducer could have been merged
     * into this counting filter.
     * <p>To determine how many times an indxProducer could have been added create a CellProducer
     * from the indexProducer and check that</p>
     * @param idxProducer the producer to drive the count check.
     * @return the maximum number of times the IndexProducer could have been inserted.
     * @see #getMaxInsert(CellProducer)
     */
    default int getMaxInsert(final IndexProducer idxProducer) {
        return getMaxInsert(CellProducer.from(idxProducer.uniqueIndices()) );
    }

    /**
     * Returns {@code true} if the internal state is valid.
     *
     * <p>This flag is a warning that an addition or
     * subtraction of cells from this filter resulted in an invalid cell for one or more
     * indexes. For example this may occur if a cell for an index was
     * set to negative following a subtraction operation, or overflows the value specified by {@code getMaxCell()} following an
     * addition operation.</p>
     *
     * <p>A counting Bloom filter that has an invalid state is no longer ensured to function
     * identically to a standard Bloom filter instance that is the merge of all the Bloom filters
     * that have been added to and not later subtracted from this counting Bloom filter.</p>
     *
     * <p>Note: The change to an invalid state may or may not be reversible. Implementations
     * are expected to document their policy on recovery from an addition or removal operation
     * that generated an invalid state.</p>
     *
     * @return {@code true} if the state is valid
     */
    boolean isValid();

    /**
     * Merges the specified BitMap producer into this Bloom filter.
     *
     * <p>Specifically: all cells for the indexes identified by the {@code bitMapProducer} will be incremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param bitMapProducer the BitMapProducer
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #add(CellProducer)
     */
    @Override
    default boolean merge(final BitMapProducer bitMapProducer) {
        Objects.requireNonNull(bitMapProducer, "bitMapProducer");
        return merge(IndexProducer.fromBitMapProducer(bitMapProducer));
    }

    /**
     * Merges the specified Bloom filter into this Bloom filter.
     *
     * <p>Specifically: all cells for the indexes identified by the {@code other} filter will be incremented by 1.</p>
     *
     * <p>Note: If the other filter is a counting Bloom filter the other filter's cells are ignored and it is treated as an
     * IndexProducer.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param other the other Bloom filter
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #add(CellProducer)
     */
    @Override
    default boolean merge(final BloomFilter other) {
        Objects.requireNonNull(other, "other");
        return merge((IndexProducer) other);
    }

    /**
     * Merges the specified Hasher into this Bloom filter.
     *
     * <p>Specifically: all cells for the unique indexes identified by the {@code hasher} will be incremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param hasher the hasher
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #add(CellProducer)
     */
    @Override
    default boolean merge(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return merge(hasher.indices(getShape()));
    }

    /**
     * Merges the specified index producer into this Bloom filter.
     *
     * <p>Specifically: all unique cells for the indices identified by the {@code indexProducer} will be incremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * <p>Note: If indices that are returned multiple times should be incremented multiple times convert the IndexProducer
     * to a CellProducer and add that.</p>
     *
     * @param indexProducer the IndexProducer
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #add(CellProducer)
     */
    @Override
    default boolean merge(final IndexProducer indexProducer) {
        Objects.requireNonNull(indexProducer, "indexProducer");
        try {
            return add(CellProducer.from(indexProducer.uniqueIndices()));
        } catch (final IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    String.format("Filter only accepts values in the [0,%d) range", getShape().getNumberOfBits()), e);
        }
    }

    /**
     * Removes the specified BitMapProducer from this Bloom filter.
     *
     * <p>Specifically all cells for the indices produced by the {@code bitMapProducer} will be
     * decremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param bitMapProducer the BitMapProducer to provide the indexes
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #subtract(CellProducer)
     */
    default boolean remove(final BitMapProducer bitMapProducer) {
        Objects.requireNonNull(bitMapProducer, "bitMapProducer");
        return remove(IndexProducer.fromBitMapProducer(bitMapProducer));
    }

    /**
     * Removes the specified Bloom filter from this Bloom filter.
     *
     * <p>Specifically: all cells for the indexes identified by the {@code other} filter will be decremented by 1.</p>
     *
     * <p>Note: If the other filter is a counting Bloom filter the other filter's cells are ignored and it is treated as an
     * IndexProducer.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param other the other Bloom filter
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #subtract(CellProducer)
     */
    default boolean remove(final BloomFilter other) {
        Objects.requireNonNull(other, "other");
        return remove((IndexProducer) other);
    }

    /**
     * Removes the unique values from the specified hasher from this Bloom filter.
     *
     * <p>Specifically all cells for the unique indices produced by the {@code hasher} will be
     * decremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param hasher the hasher to provide the indexes
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #subtract(CellProducer)
     */
    default boolean remove(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return remove(hasher.indices(getShape()));
    }

    /**
     * Removes the values from the specified IndexProducer from the Bloom filter from this Bloom filter.
     *
     * <p>Specifically all cells for the unique indices produced by the {@code hasher} will be
     * decremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * <p>Note: If indices that are returned multiple times should be decremented multiple times convert the IndexProducer
     * to a CellProducer and subtract that.</p>
     *
     * @param indexProducer the IndexProducer to provide the indexes
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #subtract(CellProducer)
     */
    default boolean remove(final IndexProducer indexProducer) {
        Objects.requireNonNull(indexProducer, "indexProducer");
        try {
            return subtract(CellProducer.from(indexProducer.uniqueIndices()));
        } catch (final IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    String.format("Filter only accepts values in the [0,%d) range", getShape().getNumberOfBits()));
        }
    }


    /**
     * Adds the specified CellProducer to this Bloom filter.
     *
     * <p>Specifically
     * all cells for the indexes identified by the {@code other} will be decremented
     * by their corresponding values in the {@code other}.</p>
     *
     * <p>This method will return true if the filter is valid after the operation.</p>
     *
     * @param other the CellProducer to subtract.
     * @return {@code true} if the subtraction was successful and the state is valid
     * @see #isValid()
     * @see #add(CellProducer)
     */
    boolean subtract(CellProducer other);

    @Override
    default IndexProducer uniqueIndices() {
        return this;
    }
}
