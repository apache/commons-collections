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

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Implementation of the methods to manage the layers in a layered Bloom filter.
 * <p>
 * The manager comprises a list of Bloom filters that are managed based on
 * various rules. The last filter in the list is known as the {@code target} and
 * is the filter into which merges are performed. The Layered manager utilizes
 * three methods to manage the list.
 * </p>
 * <ul>
 * <li>ExtendCheck - A Predicate that if true causes a new Bloom filter to be
 * created as the new target.</li>
 * <li>FilterSupplier - A Supplier that produces empty Bloom filters to be used
 * as a new target.</li>
 * <li>Cleanup - A Consumer of a {@code LinkedList} of BloomFilter that removes any
 * expired or out dated filters from the list.</li>
 * </ul>
 * <p>
 * When extendCheck returns {@code true} the following steps are taken:
 * </p>
 * <ol>
 * <li>{@code Cleanup} is called</li>
 * <li>{@code FilterSuplier} is executed and the new filter added to the list as
 * the {@code target} filter.</li>
 * </ol>
 *
 *
 * @param <T> the {@link BloomFilter} type.
 * @since 4.5.0-M1
 */
public class LayerManager<T extends BloomFilter<T>> implements BloomFilterExtractor {

    /**
     * Builds new instances of {@link LayerManager}.
     *
     * @param <T> the {@link BloomFilter} type.
     */
    public static class Builder<T extends BloomFilter<T>> implements Supplier<LayerManager<T>> {

        private Predicate<LayerManager<T>> extendCheck;
        private Supplier<T> supplier;
        private Consumer<Deque<T>> cleanup;

        private Builder() {
            extendCheck = ExtendCheck.neverAdvance();
            cleanup = Cleanup.noCleanup();
        }

        /**
         * Builds the layer manager with the specified properties.
         *
         * @return a new LayerManager.
         */
        @Override
        public LayerManager<T> get() {
            return new LayerManager<>(supplier, extendCheck, cleanup, true);
        }

        /**
         * Sets the Consumer that cleans the list of Bloom filters.
         *
         * @param cleanup the Consumer that will modify the list of filters removing out
         *                dated or stale filters.
         * @return {@code this} instance.
         */
        public Builder<T> setCleanup(final Consumer<Deque<T>> cleanup) {
            this.cleanup = cleanup;
            return this;
        }

        /**
         * Sets the extendCheck predicate. When the predicate returns {@code true} a new
         * target will be created.
         *
         * @param extendCheck The predicate to determine if a new target should be
         *                    created.
         * @return {@code this} instance.
         */
        public Builder<T> setExtendCheck(final Predicate<LayerManager<T>> extendCheck) {
            this.extendCheck = extendCheck;
            return this;
        }

        /**
         * Sets the supplier of Bloom filters. When extendCheck creates a new target,
         * the supplier provides the instance of the Bloom filter.
         *
         * @param supplier The supplier of new Bloom filter instances.
         * @return {@code this} instance.
         */
        public Builder<T> setSupplier(final Supplier<T> supplier) {
            this.supplier = supplier;
            return this;
        }
    }

    /**
     * Static methods to create a Consumer of a List of BloomFilter perform
     * tests on whether to reduce the collection of Bloom filters.
     */
    public static final class Cleanup {

        /**
         * A Cleanup that never removes anything.
         *
         * @param <T> Type of BloomFilter.
         * @return A Consumer suitable for the LayerManager {@code cleanup} parameter.
         */
        public static <T extends BloomFilter<T>> Consumer<Deque<T>> noCleanup() {
            return x -> {
                // empty
            };
        }

        /**
         * Removes the earliest filters in the list when the the number of filters
         * exceeds maxSize.
         *
         * @param <T> Type of BloomFilter.
         * @param maxSize the maximum number of filters for the list. Must be greater
         *                than 0
         * @return A Consumer suitable for the LayerManager {@code cleanup} parameter.
         * @throws IllegalArgumentException if {@code maxSize <= 0}.
         */
        public static <T extends BloomFilter<T>> Consumer<Deque<T>> onMaxSize(final int maxSize) {
            if (maxSize <= 0) {
                throw new IllegalArgumentException("'maxSize' must be greater than 0");
            }
            return ll -> {
                while (ll.size() > maxSize) {
                    ll.removeFirst();
                }
            };
        }

        /**
         * Removes the last added target if it is empty.  Useful as the first in a chain
         * of cleanup consumers.  (e.g. {@code Cleanup.removeEmptyTarget.andThen( otherConsumer )})
         *
         * @param <T> Type of BloomFilter.
         * @return A Consumer suitable for the LayerManager {@code cleanup} parameter.
         */
        public static <T extends BloomFilter<T>> Consumer<Deque<T>> removeEmptyTarget() {
            return x -> {
                if (!x.isEmpty() && x.getLast().isEmpty()) {
                    x.removeLast();
                }
            };
        }

        /**
         * Removes any layer identified by the predicate.
         *
         * @param <T> Type of BloomFilter.
         * @param test Predicate.
         * @return A Consumer suitable for the LayerManager {@code cleanup} parameter.
         */
        public static <T extends BloomFilter<T>> Consumer<Deque<T>> removeIf(final Predicate<? super T> test) {
            return x -> x.removeIf(test);
        }

        private Cleanup() {
        }
    }

    /**
     * A collection of common ExtendCheck implementations to test whether to extend
     * the depth of a LayerManager.
     */
    public static final class ExtendCheck {

        /**
         * Creates a new target after a specific number of filters have been added to
         * the current target.
         *
         * @param <T> Type of BloomFilter.
         * @param breakAt the number of filters to merge into each filter in the list.
         * @return A Predicate suitable for the LayerManager {@code extendCheck} parameter.
         * @throws IllegalArgumentException if {@code breakAt <= 0}
         */
        public static <T extends BloomFilter<T>> Predicate<LayerManager<T>> advanceOnCount(final int breakAt) {
            if (breakAt <= 0) {
                throw new IllegalArgumentException("'breakAt' must be greater than 0");
            }
            return new Predicate<LayerManager<T>>() {
                int count;

                @Override
                public boolean test(final LayerManager<T> filter) {
                    if (++count == breakAt) {
                        count = 0;
                        return true;
                    }
                    return false;
                }
            };
        }

        /**
         * Advances the target once a merge has been performed.
         *
         * @param <T> Type of BloomFilter.
         * @return A Predicate suitable for the LayerManager {@code extendCheck} parameter.
         */
        public static <T extends BloomFilter<T>> Predicate<LayerManager<T>> advanceOnPopulated() {
            return lm -> !lm.last().isEmpty();
        }

        /**
         * Creates a new target after the current target is saturated. Saturation is
         * defined as the {@code Bloom filter estimated N >= maxN}.
         *
         * <p>An example usage is advancing on a calculated saturation by calling:
         * {@code ExtendCheck.advanceOnSaturation(shape.estimateMaxN()) }</p>
         *
         * @param <T> Type of BloomFilter.
         * @param maxN the maximum number of estimated items in the filter.
         * @return A Predicate suitable for the LayerManager {@code extendCheck} parameter.
         * @throws IllegalArgumentException if {@code maxN <= 0}
         */
        public static <T extends BloomFilter<T>> Predicate<LayerManager<T>> advanceOnSaturation(final double maxN) {
            if (maxN <= 0) {
                throw new IllegalArgumentException("'maxN' must be greater than 0");
            }
            return manager -> {
                final T bf = manager.last();
                return maxN <= bf.getShape().estimateN(bf.cardinality());
            };
        }

        /**
         * Does not automatically advance the target. @{code next()} must be called directly to
         * perform the advance.
         *
         * @param <T> Type of BloomFilter.
         * @return A Predicate suitable for the LayerManager {@code extendCheck} parameter.
         */
        public static <T extends BloomFilter<T>> Predicate<LayerManager<T>> neverAdvance() {
            return x -> false;
        }

        private ExtendCheck() {
        }
    }

    /**
     * Creates a new Builder with defaults of {@link ExtendCheck#neverAdvance()} and
     * {@link Cleanup#noCleanup()}.
     *
     * @param <T> Type of BloomFilter.
     * @return A builder.
     * @see ExtendCheck#neverAdvance()
     * @see Cleanup#noCleanup()
     */
    public static <T extends BloomFilter<T>> Builder<T> builder() {
        return new Builder<>();
    }

    private final LinkedList<T> filters = new LinkedList<>();

    private final Consumer<Deque<T>> filterCleanup;

    private final Predicate<LayerManager<T>> extendCheck;

    private final Supplier<T> filterSupplier;

    /**
     * Constructs a new instance.
     *
     * @param filterSupplier the non-null supplier of new Bloom filters to add the the list
     *                       when necessary.
     * @param extendCheck    The non-null predicate that checks if a new filter should be
     *                       added to the list.
     * @param filterCleanup  the non-null consumer that removes any old filters from the
     *                       list.
     * @param initialize     true if the filter list should be initialized.
     */
    private LayerManager(final Supplier<T> filterSupplier, final Predicate<LayerManager<T>> extendCheck,
            final Consumer<Deque<T>> filterCleanup, final boolean initialize) {
        this.filterSupplier = Objects.requireNonNull(filterSupplier, "filterSupplier");
        this.extendCheck = Objects.requireNonNull(extendCheck, "extendCheck");
        this.filterCleanup = Objects.requireNonNull(filterCleanup, "filterCleanup");
        if (initialize) {
            addFilter();
        }
    }

    /**
     * Adds a new Bloom filter to the list.
     */
    private void addFilter() {
        filters.add(Objects.requireNonNull(filterSupplier.get(), "filterSupplier.get() returned null."));
    }

    /**
     * Forces execution the configured cleanup without creating a new filter except in cases
     * where the cleanup removes all the layers.
     *
     * @see LayerManager.Builder#setCleanup(Consumer)
     */
    void cleanup() {
        filterCleanup.accept(filters);
        if (filters.isEmpty()) {
            addFilter();
        }
    }

    /**
     * Removes all the filters from the layer manager, and sets up a new one as the
     * target.
     */
    public final void clear() {
        filters.clear();
        addFilter();
    }

    /**
     * Creates a deep copy of this {@link LayerManager}.
     * <p>
     * <em>Filters in the copy are deep copies, not references, so changes in the copy are NOT reflected in the original.</em>
     * </p>
     * <p>
     * The {@code filterSupplier}, {@code extendCheck}, and the {@code filterCleanup} are shared between the copy and this instance.
     * </p>
     *
     * @return a copy of this {@link LayerManager}.
     */
    public LayerManager<T> copy() {
        final LayerManager<T> newMgr = new LayerManager<>(filterSupplier, extendCheck, filterCleanup, false);
        for (final T bf : filters) {
            newMgr.filters.add(bf.copy());
        }
        return newMgr;
    }

    /**
     * Gets the Bloom filter from the first layer.
     * No extension check is performed during this call.
     * @return The Bloom filter from the first layer.
     * @see #getTarget()
     */
    public final T first() {
        return filters.getFirst();
    }

    /**
     * Gets the Bloom filter at the specified depth. The filter at depth 0 is the
     * oldest filter.
     *
     * @param depth the depth at which the desired filter is to be found.
     * @return the filter.
     * @throws NoSuchElementException if depth is not in the range
     *                                [0,filters.size())
     */
    public final T get(final int depth) {
        if (depth < 0 || depth >= filters.size()) {
            throw new NoSuchElementException(String.format("Depth must be in the range [0,%s)", filters.size()));
        }
        return filters.get(depth);
    }

    /**
     * Returns the number of filters in the LayerManager.  In the default LayerManager implementation
     * there is always at least one layer.
     *
     * @return the current depth.
     */
    public final int getDepth() {
        return filters.size();
    }

    /**
     * Returns the current target filter. If a new filter should be created based on
     * {@code extendCheck} it will be created before this method returns.
     *
     * @return the current target filter after any extension.
     */
    public final T getTarget() {
        if (extendCheck.test(this)) {
            next();
        }
        return last();
    }

    /**
     * Gets the Bloom filter from the last layer.
     * No extension check is performed during this call.
     *
     * @return The Bloom filter from the last layer.
     * @see #getTarget()
     */
    public final T last() {
        return filters.getLast();
    }

    /**
     * Forces an advance to the next depth. This method will clean-up the current
     * layers and generate a new filter layer. In most cases is it unnecessary to
     * call this method directly.
     * <p>
     * Ths method is used within {@link #getTarget()} when the configured
     * {@code ExtendCheck} returns {@code true}.
     * </p>
     *
     * @see LayerManager.Builder#setExtendCheck(Predicate)
     * @see LayerManager.Builder#setCleanup(Consumer)
     */
    void next() {
        filterCleanup.accept(filters);
        addFilter();
    }

    /**
     * Executes a Bloom filter Predicate on each Bloom filter in the manager in
     * depth order. Oldest filter first.
     *
     * @param bloomFilterPredicate the predicate to evaluate each Bloom filter with.
     * @return {@code false} when the a filter fails the predicate test. Returns
     *         {@code true} if all filters pass the test.
     */
    @Override
    public boolean processBloomFilters(final Predicate<BloomFilter> bloomFilterPredicate) {
        for (final T bf : filters) {
            if (!bloomFilterPredicate.test(bf)) {
                return false;
            }
        }
        return true;
    }
}
