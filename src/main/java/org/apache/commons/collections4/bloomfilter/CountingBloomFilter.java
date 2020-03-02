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

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

/**
 * The interface that describes a Bloom filter that associates a count with each
 * bit index to allow reversal of merge operations with remove operations.
 *
 * <p>A counting Bloom filter is expected to function identically to a standard
 * Bloom filter that is the merge of all the Bloom filters that have been added
 * to and not later subtracted from the counting Bloom filter. The functional
 * state of a CountingBloomFilter at the start and end of a series of merge and
 * subsequent remove operations of the same Bloom filters, irrespective of
 * remove order, is expected to be the same.
 *
 * <p>Removal of a filter that has not previously been merged results in an
 * invalid state where the counts no longer represent a sum of merged Bloom
 * filters. It is impossible to validate merge and remove exactly without
 * explicitly storing all filters. Consequently such an operation may go
 * undetected. The CountingBloomFilter maintains a state flag that is used as a
 * warning that an operation was performed that resulted in invalid counts and
 * thus an invalid state. For example this may occur if a count for an index was
 * set to negative following a remove operation.
 *
 * <p>Implementations should document the expected state of the filter after an
 * operation that generates invalid counts, and any potential recovery options.
 * An implementation may support a reversal of the operation to restore the
 * state to that prior to the operation. In the event that invalid counts are
 * adjusted to a valid range then it should be documented if there has been
 * irreversible information loss.
 *
 * <p>Implementations may choose to throw an exception during an operation that
 * generates invalid counts. Implementations should document the expected state
 * of the filter after such an operation. For example are the counts not updated,
 * partially updated or updated entirely before the exception is raised.
 *
 * @since 4.5
 */
public interface CountingBloomFilter extends BloomFilter {

    /**
     * Represents an operation that accepts an {@code <index, count>} pair representing
     * the count for a bit index in a counting Bloom filter and returns no result.
     *
     * <p>Note: This is a functional interface as a primitive type specialization of
     * {@link java.util.function.BiConsumer} for {@code int}.
     */
    @FunctionalInterface
    interface BitCountConsumer {
        /**
         * Performs this operation on the given {@code <index, count>} pair.
         *
         * @param index the bit index
         * @param count the count at the specified bit index
         */
        void accept(int index, int count);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Note: If the hasher contains duplicate bit indexes these are ignored.
     * All counts for the indexes identified by the other filter will be incremented by 1.
     */
    @Override
    void merge(BloomFilter other);

    /**
     * {@inheritDoc}
     *
     * <p>Note: If the hasher contains duplicate bit indexes these are ignored.
     * All counts for the indexes identified by the other filter will be incremented by 1.
     */
    @Override
    void merge(Hasher other);

    /**
     * Removes the other Bloom filter from this one.
     * All counts for the indexes identified by the other filter will be decremented by 1.
     *
     * <p>This method will return true if the filter is valid after the operation.
     *
     * @param other the other Bloom filter
     * @return true if the removal was successful and the state is valid
     * @throws IllegalArgumentException if the shape of the other filter does not match
     * the shape of this filter
     * @see #isValid()
     */
    boolean remove(BloomFilter other);

    /**
     * Removes the decomposed Bloom filter defined by the hasher from this Bloom filter.
     * All counts for the indexes identified by the hasher will be decremented by 1.
     * Duplicate indexes should be ignored.
     *
     * <p>This method will return true if the filter is valid after the operation.
     *
     * @param hasher the hasher to provide the indexes
     * @return true if the removal was successful and the state is valid
     * @throws IllegalArgumentException if the hasher cannot generate indices for the shape of
     * this filter
     * @see #isValid()
     */
    boolean remove(Hasher hasher);

    /**
     * Adds the other counting Bloom filter to this one.
     * All counts for the indexes identified by the other filter will be incremented by their
     * corresponding counts in the other filter.
     *
     * <p>This method will return true if the filter is valid after the operation.
     *
     * @param other the other counting Bloom filter
     * @return true if the addition was successful and the state is valid
     * @throws IllegalArgumentException if the shape of the other filter does not match
     * the shape of this filter
     * @see #isValid()
     */
    boolean add(CountingBloomFilter other);

    /**
     * Subtracts the other counting Bloom filter from this one.
     * All counts for the indexes identified by the other filter will be decremented by their
     * corresponding counts in the other filter.
     *
     * <p>This method will return true if the filter is valid after the operation.
     *
     * @param other the other counting Bloom filter
     * @return true if the subtraction was successful and the state is valid
     * @throws IllegalArgumentException if the shape of the other filter does not match
     * the shape of this filter
     * @see #isValid()
     */
    boolean subtract(CountingBloomFilter other);

    /**
     * Returns true if the internal state is valid. This flag is a warning that an addition or
     * subtraction of counts from this filter resulted in an invalid count for one or more
     * indexes. For example this may occur if a count for an index was
     * set to negative following a subtraction operation, or overflows an {@code int} following an
     * addition operation.
     *
     * <p>A counting Bloom filter that has an invalid state is no longer ensured to function
     * identically to a standard Bloom filter instance that is the merge of all the Bloom filters
     * that have been added to and not later subtracted from this counting Bloom filter.
     *
     * <p>Note: The change to an invalid state may or may not be reversible. Implementations
     * are expected to document their policy on recovery from an addition or removal operation
     * that generated an invalid state.
     *
     * @return true if the state is valid
     */
    boolean isValid();

    /**
     * Performs the given action for each {@code <index, count>} pair where the count is non-zero.
     * Any exceptions thrown by the action are relayed to the caller.
     *
     * @param action the action to be performed for each non-zero bit count
     * @throws NullPointerException if the specified action is null
     */
    void forEachCount(BitCountConsumer action);
}
