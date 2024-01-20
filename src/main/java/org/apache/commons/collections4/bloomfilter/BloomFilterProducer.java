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
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Produces Bloom filters from a collection (e.g. LayeredBloomFilter).
 *
 * @since 4.5
 */
public interface BloomFilterProducer {

    /**
     * Creates a BloomFilterProducer from an array of Bloom filters.
     *
     * <ul>
     * <li>The asBloomFilterArray() method returns a copy of the original array
     * with references to the original filters.</li>
     * <li>The forEachBloomFilterPair() method uses references to the original filters.</li>
     * </ul>
     * <p><em>All modifications to the Bloom filters are reflected in the original filters</em></p>
     *
     * @param filters The filters to be returned by the producer.
     * @return THe BloomFilterProducer containing the filters.
     */
    static BloomFilterProducer fromBloomFilterArray(BloomFilter... filters) {
        Objects.requireNonNull(filters, "filters");
        return new BloomFilterProducer() {
            /**
             * This implementation returns a copy the original array, the contained Bloom filters
             * are references to the originals, any modifications to them are reflected in the original
             * filters.
             */
            @Override
            public BloomFilter[] asBloomFilterArray() {
                return filters.clone();
            }

            @Override
            public boolean forEachBloomFilter(final Predicate<BloomFilter> predicate) {
                for (final BloomFilter filter : filters) {
                    if (!predicate.test(filter)) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * This implementation uses references to the original filters.  Any modifications to the
             * filters are reflected in the originals.
             */
            @Override
            public boolean forEachBloomFilterPair(final BloomFilterProducer other,
                    final BiPredicate<BloomFilter, BloomFilter> func) {
                final CountingPredicate<BloomFilter> p = new CountingPredicate<>(filters, func);
                return other.forEachBloomFilter(p) && p.forEachRemaining();
            }
        };
    }

    /**
     * Return an array of the Bloom filters in the collection.
     * <p><em>Implementations should specify if the array contains deep copies, immutable instances,
     * or references to the filters in the collection.</em></p>
     * <p>The default method returns a deep copy of the enclosed filters.</p>
     *
     * @return An array of Bloom filters.
     */
    default BloomFilter[] asBloomFilterArray() {
        final List<BloomFilter> filters = new ArrayList<>();
        forEachBloomFilter(f -> filters.add(f.copy()));
        return filters.toArray(new BloomFilter[0]);
    }

    /**
     * Create a standard (non-layered) Bloom filter by merging all of the layers. If
     * the filter is empty this method will return an empty Bloom filter.
     *
     * @return the merged bloom filter.
     */
    default BloomFilter flatten() {
        BloomFilter[] bf = {null};
        forEachBloomFilter( x -> {
            if (bf[0] == null) {
                bf[0] = new SimpleBloomFilter( x.getShape());
            }
            return bf[0].merge( x );
        });
        return bf[0];
    }

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
     * Applies the {@code func} to each Bloom filter pair in order. Will apply all
     * of the Bloom filters from the other BloomFilterProducer to this producer. If
     * either {@code this} producer or {@code other} producer has fewer BloomFilters
     * ths method will provide {@code null} for all excess calls to the {@code func}.
     *
     * <p><em>This implementation returns references to the Bloom filter.  Other implementations
     * should specify if the array contains deep copies, immutable instances,
     * or references to the filters in the collection.</em></p>
     *
     * @param other The other BloomFilterProducer that provides the y values in the
     *              (x,y) pair.
     * @param func  The function to apply.
     * @return {@code true} if the {@code func} returned {@code true} for every pair,
     *         {@code false} otherwise.
     */
    default boolean forEachBloomFilterPair(final BloomFilterProducer other,
            final BiPredicate<BloomFilter, BloomFilter> func) {
        final CountingPredicate<BloomFilter> p = new CountingPredicate<>(asBloomFilterArray(), func);
        return other.forEachBloomFilter(p) && p.forEachRemaining();
    }
}
