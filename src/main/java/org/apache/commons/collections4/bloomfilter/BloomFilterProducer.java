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
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Produces Bloom filters that are copies of Bloom filters in a collection (e.g.
 * LayerBloomFilter).
 *
 * @since 4.5
 */
public interface BloomFilterProducer {
    /**
     * Executes a Bloom filter Predicate on each Bloom filter in the manager in
     * depth order. Oldest filter first.
     *
     * @param bloomFilterPredicate the predicate to evaluate each Bloom filter with.
     * @return {@code false} when the first filter fails the predicate test. Returns
     *         {@code true} if all filters pass the test.
     */
    boolean forEachBloomFilter(Predicate<BloomFilter> bloomFilterPredicate);

    /**
     * Return a deep copy of the BloomFilterProducer data as a Bloom filter array.
     * <p>
     * The default implementation of this method is slow. It is recommended that
     * implementing classes reimplement this method.
     * </p>
     *
     * @return An array of Bloom filters.
     */
    default BloomFilter[] asBloomFilterArray() {
        class Filters {
            private BloomFilter[] data = new BloomFilter[16];
            private int size;

            boolean add(final BloomFilter filter) {
                if (size == data.length) {
                    // This will throw an out-of-memory error if there are too many Bloom filters.
                    data = Arrays.copyOf(data, size * 2);
                }
                data[size++] = filter.copy();
                return true;
            }

            BloomFilter[] toArray() {
                // Edge case to avoid a large array copy
                return size == data.length ? data : Arrays.copyOf(data, size);
            }
        }
        final Filters filters = new Filters();
        forEachBloomFilter(filters::add);
        return filters.toArray();
    }

    /**
     * Applies the {@code func} to each Bloom filter pair in order. Will apply all
     * of the Bloom filters from the other BloomFilterProducer to this producer. If
     * this producer does not have as many BloomFilters it will provide
     * {@code null} for all excess calls to the BiPredicate.
     *
     * @param other The other BloomFilterProducer that provides the y values in the
     *              (x,y) pair.
     * @param func  The function to apply.
     * @return A LongPredicate that tests this BitMapProducers bitmap values in
     *         order.
     */
    default boolean forEachBloomFilterPair(final BloomFilterProducer other,
            final BiPredicate<BloomFilter, BloomFilter> func) {
        final CountingPredicate<BloomFilter> p = new CountingPredicate<>(asBloomFilterArray(), func);
        return other.forEachBloomFilter(p) && p.forEachRemaining();
    }

}
