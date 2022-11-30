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

/**
 * Defines a mapping of index to counts.
 *
 * <p>Note that a BitCountProducer may return duplicate indices and may be unordered.
 *
 * <p>Implementations must guarantee that:
 *
 * <ul>
 * <li>The mapping of index to counts is the combined sum of counts at each index.
 * <li>For every unique value produced by the IndexProducer there will be at least one matching
 * index and count produced by the BitCountProducer.
 * <li>The BitCountProducer will not generate indices that are not output by the IndexProducer.
 * </ul>
 *
 * <p>Note that implementations that do not output duplicate indices for BitCountProducer and
 * do for IndexProducer, or vice versa, are consistent if the distinct indices from each are
 * the same.
 *
 * <p>For example the mapping [(1,2),(2,3),(3,1)] can be output with many combinations including:
 * <pre>
 * [(1,2),(2,3),(3,1)]
 * [(1,1),(1,1),(2,1),(2,1),(2,1),(3,1)]
 * [(1,1),(3,1),(1,1),(2,1),(2,1),(2,1)]
 * [(3,1),(1,1),(2,2),(1,1),(2,1)]
 * ...
 * </pre>
 *
 * @since 4.5
 */
@FunctionalInterface
public interface BitCountProducer extends IndexProducer {

    /**
     * Performs the given action for each {@code <index, count>} pair where the count is non-zero.
     * Any exceptions thrown by the action are relayed to the caller. The consumer is applied to each
     * index-count pair, if the consumer returns {@code false} the execution is stopped, {@code false}
     * is returned, and no further pairs are processed.
     *
     * Duplicate indices are not required to be aggregated. Duplicates may be output by the producer as
     * noted in the class javadoc.
     *
     * @param consumer the action to be performed for each non-zero bit count
     * @return {@code true} if all count pairs return true from consumer, {@code false} otherwise.
     * @throws NullPointerException if the specified consumer is null
     */
    boolean forEachCount(BitCountConsumer consumer);

    /**
     * The default implementation returns indices with ordering and uniqueness of {@code forEachCount()}.
     */
    @Override
    default boolean forEachIndex(final IntPredicate predicate) {
        return forEachCount((i, v) -> predicate.test(i));
    }

    /**
     * Creates a BitCountProducer from an IndexProducer. The resulting
     * producer will return every index from the IndexProducer with a count of 1.
     *
     * <p>Note that the BitCountProducer does not remove duplicates. Any use of the
     * BitCountProducer to create an aggregate mapping of index to counts, such as a
     * CountingBloomFilter, should use the same BitCountProducer in both add and
     * subtract operations to maintain consistency.
     * </p>
     * @param idx An index producer.
     * @return A BitCountProducer with the same indices as the IndexProducer.
     */
    static BitCountProducer from(final IndexProducer idx) {
        return new BitCountProducer() {
            @Override
            public boolean forEachCount(final BitCountConsumer consumer) {
                return idx.forEachIndex(i -> consumer.test(i, 1));
            }

            @Override
            public int[] asIndexArray() {
                return idx.asIndexArray();
            }

            @Override
            public boolean forEachIndex(final IntPredicate predicate) {
                return idx.forEachIndex(predicate);
            }
        };
    }

    /**
     * Represents an operation that accepts an {@code <index, count>} pair representing
     * the count for a bit index. Returns {@code true}
     * if processing should continue, {@code false} otherwise.
     *
     * <p>Note: This is a functional interface as a specialization of
     * {@link java.util.function.BiPredicate} for {@code int}.</p>
     */
    @FunctionalInterface
    interface BitCountConsumer {
        /**
         * Performs an operation on the given {@code <index, count>} pair.
         *
         * @param index the bit index.
         * @param count the count at the specified bit index.
         * @return {@code true} if processing should continue, {@code false} if processing should stop.
         */
        boolean test(int index, int count);
    }
}
