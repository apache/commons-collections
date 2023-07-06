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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Produces Bloom filters from a collection (e.g. LayeredBloomFilter).
 *
 * @since 4.5
 */
public interface BloomFilterProducer {

    /**
     * Executes a Bloom filter Predicate on each Bloom filter in the collection. The
     * ordering of the Bloom filters is not specified by this interface.
     *
     * @param bloomFilterPredicate the predicate to evaluate each Bloom filter with.
     * @return {@code false} when the first filter fails the predicate test. Returns
     *         {@code true} if all filters pass the test.
     */
    boolean forEachBloomFilter(Predicate<BloomFilter> bloomFilterPredicate);

    /**
     * Return a deep copy of the BloomFilterProducer data as a Bloom filter array.
     *
     * @return An array of Bloom filters.
     */
    default BloomFilter[] asBloomFilterArray() {
        final List<BloomFilter> filters = new ArrayList<>();
        forEachBloomFilter(f -> filters.add(f.copy()));
        return filters.toArray(new BloomFilter[filters.size()]);
    }

    /**
     * Applies the {@code func} to each Bloom filter pair in order. Will apply all
     * of the Bloom filters from the other BloomFilterProducer to this producer. If
     * this producer does not have as many BloomFilters it will provide {@code null}
     * for all excess calls to the BiPredicate.
     *
     * @param other The other BloomFilterProducer that provides the y values in the
     *              (x,y) pair.
     * @param func  The function to apply.
     * @return {@code true} if the func returned {@code true} for every pair,
     *         {@code false} otherwise.
     */
    default boolean forEachBloomFilterPair(final BloomFilterProducer other,
            final BiPredicate<BloomFilter, BloomFilter> func) {
        final CountingPredicate<BloomFilter> p = new CountingPredicate<>(asBloomFilterArray(), func);
        return other.forEachBloomFilter(p) && p.forEachRemaining();
    }

    /**
     * Creates a BloomFilterProducer from an array of Bloom filters.
     *
     * @param filters The filters to be returned by the producer.
     * @return THe BloomFilterProducer containing the filters.
     */
    static BloomFilterProducer fromBloomFilterArray(BloomFilter... filters) {
        return new BloomFilterProducer() {
            @Override
            public boolean forEachBloomFilter(final Predicate<BloomFilter> predicate) {
                for (final BloomFilter filter : filters) {
                    if (!predicate.test(filter)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public BloomFilter[] asBloomFilterArray() {
                return Arrays.copyOf(filters, filters.length);
            }

            @Override
            public boolean forEachBloomFilterPair(final BloomFilterProducer other,
                    final BiPredicate<BloomFilter, BloomFilter> func) {
                final CountingPredicate<BloomFilter> p = new CountingPredicate<>(filters, func);
                return other.forEachBloomFilter(p) && p.forEachRemaining();
            }
        };
    }
}
