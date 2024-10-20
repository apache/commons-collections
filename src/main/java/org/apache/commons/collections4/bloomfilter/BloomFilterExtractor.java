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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Produces Bloom filters from a collection (for example, {@link LayeredBloomFilter}).
 *
 * @since 4.5.0-M2
 */
@FunctionalInterface
public interface BloomFilterExtractor {

    /**
     * Creates a BloomFilterExtractor from an array of Bloom filters.
     *
     * <ul>
     * <li>The asBloomFilterArray() method returns a copy of the original array with references to the original filters.</li>
     * <li>The forEachBloomFilterPair() method uses references to the original filters.</li>
     * </ul>
     * <p>
     * <em>All modifications to the Bloom filters are reflected in the original filters</em>
     * </p>
     *
     * @param <T>     The BloomFilter type.
     * @param filters The filters to be returned by the extractor.
     * @return THe BloomFilterExtractor containing the filters.
     */
    static <T extends BloomFilter<T>> BloomFilterExtractor fromBloomFilterArray(final BloomFilter<?>... filters) {
        Objects.requireNonNull(filters, "filters");
        return new BloomFilterExtractor() {

            /**
             * This implementation returns a copy the original array, the contained Bloom filters are references to the originals, any modifications to them are
             * reflected in the original filters.
             */
            @Override
            public BloomFilter[] asBloomFilterArray() {
                return filters.clone();
            }

            /**
             * This implementation uses references to the original filters. Any modifications to the filters are reflected in the originals.
             */
            @Override
            public boolean processBloomFilterPair(final BloomFilterExtractor other, final BiPredicate<BloomFilter, BloomFilter> func) {
                final CountingPredicate<BloomFilter> p = new CountingPredicate<>(filters, func);
                return other.processBloomFilters(p) && p.processRemaining();
            }

            @Override
            public boolean processBloomFilters(final Predicate<BloomFilter> predicate) {
                for (final BloomFilter filter : filters) {
                    if (!predicate.test(filter)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    /**
     * Return an array of the Bloom filters in the collection.
     * <p>
     * <em>Implementations should specify if the array contains deep copies, immutable instances, or references to the filters in the collection.</em>
     * </p>
     * <p>
     * The default method returns a deep copy of the enclosed filters.
     * </p>
     *
     * @return An array of Bloom filters.
     */
    default BloomFilter[] asBloomFilterArray() {
        final List<BloomFilter> filters = new ArrayList<>();
        processBloomFilters(f -> filters.add(f.copy()));
        return filters.toArray(new BloomFilter[0]);
    }

    /**
     * Create a standard (non-layered) Bloom filter by merging all of the layers. If the filter is empty this method will return an empty Bloom filter.
     *
     * @return the merged bloom filter, never null.
     * @throws NullPointerException if this call did not process any filters.
     */
    default BloomFilter flatten() {
        final AtomicReference<BloomFilter> ref = new AtomicReference<>();
        processBloomFilters(x -> {
            if (ref.get() == null) {
                ref.set(new SimpleBloomFilter(x.getShape()));
            }
            return ref.get().merge(x);
        });
        return Objects.requireNonNull(ref.get(), "No filters.");
    }

    /**
     * Applies the {@code func} to each Bloom filter pair in order. Will apply all of the Bloom filters from the other BloomFilterExtractor to this extractor.
     * If either {@code this} extractor or {@code other} extractor has fewer BloomFilters the method will provide {@code null} for all excess calls to the
     * {@code func}.
     *
     * <p>
     * <em>This implementation returns references to the Bloom filter. Other implementations should specify if the array contains deep copies, immutable
     * instances, or references to the filters in the collection.</em>
     * </p>
     *
     * @param other The other BloomFilterExtractor that provides the y values in the (x,y) pair.
     * @param func  The function to apply.
     * @return {@code true} if the {@code func} returned {@code true} for every pair, {@code false} otherwise.
     */
    default boolean processBloomFilterPair(final BloomFilterExtractor other, final BiPredicate<BloomFilter, BloomFilter> func) {
        final CountingPredicate<BloomFilter> p = new CountingPredicate<>(asBloomFilterArray(), func);
        return other.processBloomFilters(p) && p.processRemaining();
    }

    /**
     * Executes a Bloom filter Predicate on each Bloom filter in the collection. The ordering of the Bloom filters is not specified by this interface.
     *
     * @param bloomFilterPredicate the predicate to evaluate each Bloom filter with.
     * @return {@code false} when the first filter fails the predicate test. Returns {@code true} if all filters pass the test.
     */
    boolean processBloomFilters(Predicate<BloomFilter> bloomFilterPredicate);
}
