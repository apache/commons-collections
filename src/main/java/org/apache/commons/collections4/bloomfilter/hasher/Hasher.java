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
package org.apache.commons.collections4.bloomfilter.hasher;

import org.apache.commons.collections4.bloomfilter.Shape;

import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.BitMap;
import org.apache.commons.collections4.bloomfilter.IndexProducer;

/**
 * A Hasher creates IndexProducer based on the hash implementation and the
 * provided Shape.
 *
 * @since 4.5
 */
public interface Hasher {

    /**
     * Creates an IndexProducer for this hasher based on the Shape.
     *
     * <p>The @{code IndexProducer} will create indices within the range defined by the number of bits in
     * the shape. The total number of indices will respect the number of hash functions per item
     * defined by the shape. However the count of indices may not be a multiple of the number of
     * hash functions once implementation has removed duplicates.</p>
     *
     * <p>This IndexProducer must be deterministic in that it must return the same indices for the
     * same Shape.</p>
     *
     * <p>No guarantee is made as to order of indices.</p>
     * <p>Duplicates indices for a single item must be removed.</p>
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the iterator of integers
     */
    IndexProducer indices(Shape shape);

    /**
     * Gets the number of items that will be hashed by the {@code IndexProducer}.
     * @return The number of items that will be hashed by the {@code IndexProducer}.
     */
    int size();

    /**
     * Returns true if there are no items to be hashed.
     * @return {@code true} if there are no items to be hashed.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * A convenience class for Hasher implementations to filter out duplicate indices.
     *
     * <p><em>If the index is negative the behavior is not defined.</em></p>
     *
     * <p>This is conceptually a unique filter implemented as a {@code Predicate<int>}.</p>
     * @since 4.5
     */
    public class Filter {
        private long[] bits;
        private int size;

        /**
         * Constructor.
         *
         * @param size The number of numbers to track. Values from 0 to size-1 will be tracked.
         */
        public Filter(int size) {
            bits = new long[BitMap.numberOfBitMaps(size)];
            this.size = size;
        }

        /**
         * Test if the number has not been seen.
         *
         * <p>The first time a number is tested the method returns {@code true} and returns
         * {@code false} for every time after that.</p>
         *
         * <p><em>If the input is negative the behavior is not defined.</em></p>
         *
         * <p><em>Note: only positive number are
         * @param number the number to check.
         * @return {@code true} if the number has not been seen, {@code false} otherwise.
         */
        public boolean test(int number) {
            BitMap.checkPositive(number);
            if (number >= size) {
                throw new IndexOutOfBoundsException(String.format("number to large %d >= %d", number, size));
            }
            boolean retval = !BitMap.contains(bits, number);
            BitMap.set(bits, number);
            return retval;
        }
    }

    /**
     * Class to wrap an that an IntConsumer only receives an integer value once.
     *
     * <p><em>If the index is negative the behavior is not defined.</em></p>
     *
     * @since 4.5
     */
    public class FilteredIntConsumer implements IntConsumer {
        private Hasher.Filter filter;
        private IntConsumer consumer;

        /**
         * Constructor.
         * @param shape The shape of the output.
         * @param consumer to wrap.
         */
        public FilteredIntConsumer(int maxIntegerValue, IntConsumer consumer) {
            this.filter = new Hasher.Filter(maxIntegerValue);
            this.consumer = consumer;
        }

        @Override
        public void accept(int value) {
            if (filter.test(value)) {
                consumer.accept(value);
                ;
            }

        }
    }

}
