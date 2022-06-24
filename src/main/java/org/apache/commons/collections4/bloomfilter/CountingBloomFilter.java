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

/**
 * The interface that describes a Bloom filter that associates a count with each
 * bit index to allow reversal of merge operations with remove operations.
 *
 * <p>A counting Bloom filter is expected to function identically to a standard
 * Bloom filter that is the merge of all the Bloom filters that have been added
 * to and not later subtracted from the counting Bloom filter. The functional
 * state of a CountingBloomFilter at the start and end of a series of merge and
 * subsequent remove operations of the same Bloom filters, irrespective of
 * remove order, is expected to be the same.</p>
 *
 * <p>Removal of a filter that has not previously been merged results in an
 * invalid state where the counts no longer represent a sum of merged Bloom
 * filters. It is impossible to validate merge and remove exactly without
 * explicitly storing all filters. Consequently such an operation may go
 * undetected. The CountingBloomFilter maintains a state flag that is used as a
 * warning that an operation was performed that resulted in invalid counts and
 * thus an invalid state. For example this may occur if a count for an index was
 * set to negative following a remove operation.</p>
 *
 * <p>Implementations should document the expected state of the filter after an
 * operation that generates invalid counts, and any potential recovery options.
 * An implementation may support a reversal of the operation to restore the
 * state to that prior to the operation. In the event that invalid counts are
 * adjusted to a valid range then it should be documented if there has been
 * irreversible information loss.</p>
 *
 * <p>Implementations may choose to throw an exception during an operation that
 * generates invalid counts. Implementations should document the expected state
 * of the filter after such an operation. For example are the counts not updated,
 * partially updated or updated entirely before the exception is raised.</p>
 *
 * @since 4.5
 */
public interface CountingBloomFilter extends BloomFilter, BitCountProducer {

    // Query Operations

    /**
     * Returns {@code true} if the internal state is valid.
     *
     * <p>This flag is a warning that an addition or
     * subtraction of counts from this filter resulted in an invalid count for one or more
     * indexes. For example this may occur if a count for an index was
     * set to negative following a subtraction operation, or overflows an {@code int} following an
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

    // Modification Operations

    /**
     * Removes the specified Bloom filter from this Bloom filter.
     *
     * <p>Specifically: all counts for the indexes identified by the {@code other} filter will be decremented by 1,</p>
     *
     * <p>Note: If the other filter is a counting Bloom filter the index counts are ignored and it is treated as an
     * IndexProducer.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param other the other Bloom filter
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #subtract(BitCountProducer)
     */
    boolean remove(BloomFilter other);

    /**
     * Removes the specified hasher from the Bloom filter from this Bloom filter.
     *
     * <p>Specifically all counts for the indices produced by the {@code hasher} will be
     * decremented by 1.</p>
     *
     * <p>For HasherCollections each enclosed Hasher will be considered a single item and decremented
     * from the counts separately.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param hasher the hasher to provide the indexes
     * @return {@code true} if the removal was successful and the state is valid
     * @see #isValid()
     * @see #subtract(BitCountProducer)
     */
    boolean remove(Hasher hasher);

    /**
     * Adds the specified BitCountProducer to this Bloom filter.
     *
     * <p>Specifically
     * all counts for the indexes identified by the {@code other} will be incremented
     * by their corresponding values in the {@code other}.</p>
     *
     * <p>This method will return {@code true} if the filter is valid after the operation.</p>
     *
     * @param other the BitCountProducer to add.
     * @return {@code true} if the addition was successful and the state is valid
     * @see #isValid()
     * @see #subtract(BitCountProducer)
     */
    boolean add(BitCountProducer other);

    /**
     * Adds the specified BitCountProducer to this Bloom filter.
     *
     * <p>Specifically
     * all counts for the indexes identified by the {@code other} will be decremented
     * by their corresponding values in the {@code other}.</p>
     *
     * <p>This method will return true if the filter is valid after the operation.</p>
     *
     * @param other the BitCountProducer to subtract.
     * @return {@code true} if the subtraction was successful and the state is valid
     * @see #isValid()
     * @see #add(BitCountProducer)
     */
    boolean subtract(BitCountProducer other);


    /**
     * Creates a new instance of the CountingBloomFilter with the same properties as the current one.
     * @return a copy of this CountingBloomFilter
     */
    @Override
    CountingBloomFilter copy();
}
