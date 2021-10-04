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

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

/**
 * Produces bit counts for counting type Bloom filters.
 *
 */
public interface BitCountProducer {

    /**
     * Performs the given action for each {@code <index, count>} pair where the count is non-zero.
     * Any exceptions thrown by the action are relayed to the caller.
     *
     * Must only process each index once, and must process indexes in order.
     *
     * @param consumer the action to be performed for each non-zero bit count
     * @throws NullPointerException if the specified action is null
     */
    void forEachCount(BitCountConsumer consumer);

    /**
     * Factory to construct BitCountProducers from common Bloom filter and Hashers.
     *
     */
    public static class Factory {
        /**
         * Creates a BitCountProducer from a bloom filter.
         *
         *  If the filter implements the BitCountProducer it is returned unchanged.
         *  If the filter does not implement the BitCountProducer each enabled bit is
         *  returned with a count of one (1).
         *
         * @param filter the Bloom filter to count.
         * @return The BitCountProducer for the Bloom filter.
         */
        public static BitCountProducer from( BloomFilter filter ) {
            return (filter instanceof BitCountProducer) ? (BitCountProducer) filter : simple( filter );
        }

        /**
         * Create a BitCountProducer from a bloom filter without regard to previous BitCountProducer
         * implementation.
         *
         *  for each enabled bit a count of 1 is returned.
         *
         * @param filter The Bloom filter to create the BitCountProducer from.
         * @return the BitCountProducer for the Bloom filter.
         */
        public static BitCountProducer simple( BloomFilter filter ) {
            return new BitCountProducer() {

                @Override
                public void forEachCount(BitCountConsumer consumer) {
                    for (int i : filter.getIndices() )
                    {
                        consumer.accept(i, 1);
                    }
                }
            };
        }

        /**
         * Creates a Bit count producer from a shape and hasher.
         * @param shape The shape to use
         * @param hasher the hasher to use.
         * @return A BitCountProducer for the hasher produced values.
         */
        public static BitCountProducer from( Shape shape, Hasher hasher ) {
            return new BitCountProducer() {

                @Override
                public void forEachCount(BitCountConsumer consumer) {
                    final Set<Integer> distinct = new TreeSet<>();
                    hasher.iterator(shape).forEachRemaining((Consumer<Integer>) distinct::add );
                    distinct.forEach( i -> consumer.accept(i, 1));
                }
            };
        }
    }

    /**
     * Represents an operation that accepts an {@code <index, count>} pair representing
     * the count for a bit index in a Bit Count Producer Bloom filter and returns no result.
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
}
