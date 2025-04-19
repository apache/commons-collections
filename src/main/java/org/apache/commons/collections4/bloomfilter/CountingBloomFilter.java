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
 * @see CellExtractor
 * @since 4.5.0-M1
 */
public interface CountingBloomFilter extends BloomFilter<CountingBloomFilter>, CellExtractor {

    // Query Operations

    /**
     * Adds the specified CellExtractor to this Bloom filter.
     *
     * <p>Specifically
     * all cells for the indexes identified by the {@code other} will be incremented
     * by their corresponding values in the {@code other}.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param other the CellExtractor to add.
     * @return {@code true} if the addition was successful and the state is valid
     * @see #isValid()
     * @see #subtract(CellExtractor)
     */
    boolean add(CellExtractor other);

    /**
     * Gets the maximum allowable value for a cell count in this Counting filter.
     *
     * @return the maximum allowable value for a cell count in this Counting filter.
     */
    int getMaxCell();

    /**
     * Determines the maximum number of times the BitMapExtractor could have been merged into this counting filter.
     *
     * @param bitMapExtractor the BitMapExtractor to provide the indices.
     * @return the maximum number of times the BitMapExtractor could have been inserted.
     */
    default int getMaxInsert(final BitMapExtractor bitMapExtractor) {
        if (!contains(bitMapExtractor)) {
            return 0;
        }
        final long[] bitMaps = bitMapExtractor.asBitMapArray();
        final int[] max = { Integer.MAX_VALUE };
        processCells((x, y) -> {
            if ((bitMaps[BitMaps.getLongIndex(x)] & BitMaps.getLongBit(x)) != 0) {
                max[0] = max[0] <= y ? max[0] : y;
            }
            return true;
        });
        return max[0];
    }

    /**
     * Determines the maximum number of times the Bloom filter could have been merged into this counting filter.
     *
     * @param bloomFilter the Bloom filter the check for.
     * @return the maximum number of times the Bloom filter could have been inserted.
     */
    default int getMaxInsert(final BloomFilter<?> bloomFilter) {
        return getMaxInsert((BitMapExtractor) bloomFilter);
    }

    /**
     * Determines the maximum number of times the Cell Extractor could have been added.
     *
     * @param cellExtractor the extractor of cells.
     * @return the maximum number of times the CellExtractor could have been inserted.
     */
    int getMaxInsert(CellExtractor cellExtractor);

    /**
     * Determines the maximum number of times the Hasher could have been merged into this counting filter.
     *
     * @param hasher the Hasher to provide the indices.
     * @return the maximum number of times the hasher could have been inserted.
     */
    default int getMaxInsert(final Hasher hasher) {
        return getMaxInsert(hasher.indices(getShape()));
    }

    /**
     * Determines the maximum number of times the IndexExtractor could have been merged into this counting filter.
     * <p>
     * To determine how many times an indexExtractor could have been added create a CellExtractor from the indexExtractor and check that
     * </p>
     *
     * @param indexExtractor the extractor to drive the count check.
     * @return the maximum number of times the IndexExtractor could have been inserted.
     * @see #getMaxInsert(CellExtractor)
     */
    default int getMaxInsert(final IndexExtractor indexExtractor) {
        return getMaxInsert(CellExtractor.from(indexExtractor.uniqueIndices()));
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
     * Merges the specified BitMap extractor into this Bloom filter.
     *
     * <p>Specifically: all cells for the indexes identified by the {@code bitMapExtractor} will be incremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param bitMapExtractor the BitMapExtractor
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #add(CellExtractor)
     */
    @Override
    default boolean merge(final BitMapExtractor bitMapExtractor) {
        return merge(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
    }

    /**
     * Merges the specified Bloom filter into this Bloom filter.
     *
     * <p>Specifically: all cells for the indexes identified by the {@code other} filter will be incremented by 1.</p>
     *
     * <p>Note: If the other filter is a counting Bloom filter the other filter's cells are ignored and it is treated as an
     * IndexExtractor.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param other the other Bloom filter
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #add(CellExtractor)
     */
    @Override
    default boolean merge(final BloomFilter<?> other) {
        Objects.requireNonNull(other, "other");
        return merge((IndexExtractor) other);
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
     * @see #add(CellExtractor)
     */
    @Override
    default boolean merge(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return merge(hasher.indices(getShape()));
    }

    /**
     * Merges the specified index extractor into this Bloom filter.
     *
     * <p>Specifically: all unique cells for the indices identified by the {@code indexExtractor} will be incremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * <p>Notes:</p>
     * <ul>
     * <li>If indices that are returned multiple times should be incremented multiple times convert the IndexExtractor
     * to a CellExtractor and add that.</li>
     * <li>Implementations should throw {@code IllegalArgumentException} and no other exception on bad input.</li>
     * </ul>
     * @param indexExtractor the IndexExtractor
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #add(CellExtractor)
     */
    @Override
    default boolean merge(final IndexExtractor indexExtractor) {
        Objects.requireNonNull(indexExtractor, "indexExtractor");
        try {
            return add(CellExtractor.from(indexExtractor.uniqueIndices()));
        } catch (final IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    String.format("Filter only accepts values in the [0,%d) range", getShape().getNumberOfBits()), e);
        }
    }

    /**
     * Removes the specified BitMapExtractor from this Bloom filter.
     *
     * <p>Specifically all cells for the indices produced by the {@code bitMapExtractor} will be
     * decremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param bitMapExtractor the BitMapExtractor to provide the indexes
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #subtract(CellExtractor)
     */
    default boolean remove(final BitMapExtractor bitMapExtractor) {
        return remove(IndexExtractor.fromBitMapExtractor(bitMapExtractor));
    }

    /**
     * Removes the specified Bloom filter from this Bloom filter.
     *
     * <p>Specifically: all cells for the indexes identified by the {@code other} filter will be decremented by 1.</p>
     *
     * <p>Note: If the other filter is a counting Bloom filter the other filter's cells are ignored and it is treated as an
     * IndexExtractor.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param other the other Bloom filter
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #subtract(CellExtractor)
     */
    default boolean remove(final BloomFilter<?> other) {
        return remove((IndexExtractor) other);
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
     * @see #subtract(CellExtractor)
     */
    default boolean remove(final Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        return remove(hasher.indices(getShape()));
    }

    /**
     * Removes the values from the specified IndexExtractor from the Bloom filter from this Bloom filter.
     *
     * <p>Specifically all cells for the unique indices produced by the {@code hasher} will be
     * decremented by 1.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * <p>Note: If indices that are returned multiple times should be decremented multiple times convert the IndexExtractor
     * to a CellExtractor and subtract that.</p>
     *
     * @param indexExtractor the IndexExtractor to provide the indexes
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #subtract(CellExtractor)
     */
    default boolean remove(final IndexExtractor indexExtractor) {
        Objects.requireNonNull(indexExtractor, "indexExtractor");
        try {
            return subtract(CellExtractor.from(indexExtractor.uniqueIndices()));
        } catch (final IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    String.format("Filter only accepts values in the [0,%d) range", getShape().getNumberOfBits()));
        }
    }

    /**
     * Adds the specified CellExtractor to this Bloom filter.
     *
     * <p>Specifically
     * all cells for the indexes identified by the {@code other} will be decremented
     * by their corresponding values in the {@code other}.</p>
     *
     * <p>This method will return true if the filter is valid after the operation.</p>
     *
     * @param other the CellExtractor to subtract.
     * @return {@code true} if the subtraction was successful and the state is valid
     * @see #isValid()
     * @see #add(CellExtractor)
     */
    boolean subtract(CellExtractor other);

    /**
     * The default implementation is a no-op since the counting bloom filter returns an unique IndexExtractor by default.
     * @return this counting Bloom filter.
     */
    @Override
    default IndexExtractor uniqueIndices() {
        return this;
    }
}
