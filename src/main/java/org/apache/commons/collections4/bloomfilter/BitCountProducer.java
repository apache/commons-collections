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
 * Produces bit counts for counting type Bloom filters.
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
     * <p>Must only process each index once, and must process indexes in order.</p>
     *
     * @param consumer the action to be performed for each non-zero bit count
     * @return {@code true} if all count pairs return true from consumer, {@code false} otherwise.
     * @throws NullPointerException if the specified action is null
     */
    boolean forEachCount(BitCountConsumer consumer);

    @Override
    default boolean forEachIndex(IntPredicate predicate) {
        return forEachCount((i, v) -> predicate.test(i));
    }

    /**
     * Creates a BitCountProducer from an IndexProducer.  The resulting
     * producer will count each enabled bit once.
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
     * the count for a bit index in a Bit Count Producer Bloom filter and returns {@code true}
     * if processing should continue, {@code false} otherwise.
     *
     * <p>Note: This is a functional interface as a specialization of
     * {@link java.util.function.BiPredicate} for {@code int}.</p>
     */
    @FunctionalInterface
    interface BitCountConsumer {
        /**
         * Performs this operation on the given {@code <index, count>} pair.
         *
         * @param index the bit index.
         * @param count the count at the specified bit index.
         * @return {@code true} if processing should continue, {@code false} it processing should stop.
         */
        boolean test(int index, int count);
    }
}
