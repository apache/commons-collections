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
 * A BitCountProducer may return duplicate indices and may be unordered.
 *
 * The guaranteeS are:
 * <ul>
 * <li>that for every unique value produced by the IndexProducer there will be at least one
 * index in the BitCountProducer.</li>
 * <li>that the total count of a specific value produced by the IndexProducer will equal the
 * total of the counts in the BitCountProducer for that index.</li>
 * </ul>
 * Example:
 *
 * An IndexProducer that generates the values [1,2,3,1,5,6] can be represented by a BitCountProducer
 * that generates [(1,2),(2,1),(3,1),(5,1)(6,1)] or [(1,1),(2,1),(3,1),(1,1),(5,1),(6,1)] where the
 * entries may be in any order.
 *
 * @since 4.5
 */
@FunctionalInterface
public interface BitCountProducer extends IndexProducer {

    /**
     * Performs the given action for each {@code <index, count>} pair where the count is non-zero.
     * Any exceptions thrown by the action are relayed to the caller.  The consumer is applied to each
     * index-count pair, if the consumer returns {@code false} the execution is stopped, {@code false}
     * is returned, and no further pairs are processed.
     *
     * @param consumer the action to be performed for each non-zero bit count
     * @return {@code true} if all count pairs return true from consumer, {@code false} otherwise.
     * @throws NullPointerException if the specified consumer is null
     */
    boolean forEachCount(BitCountConsumer consumer);

    @Override
    default boolean forEachIndex(IntPredicate predicate) {
        return forEachCount((i, v) -> predicate.test(i));
    }

    /**
     * Creates a BitCountProducer from an IndexProducer.  The resulting
     * producer will return every index from the IndexProducer with a count of 1.
     * @param idx An index producer.
     * @return A BitCountProducer with the same indices as the IndexProducer.
     */
    static BitCountProducer from(IndexProducer idx) {
        return new BitCountProducer() {
            @Override
            public boolean forEachCount(BitCountConsumer consumer) {
                return idx.forEachIndex(i -> consumer.test(i, 1));
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
         * @return {@code true} if processing should continue, {@code false} it processing should stop.
         */
        boolean test(int index, int count);
    }
}
