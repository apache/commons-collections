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
 * @since 4.5
 */
public class LayerManager implements BloomFilterProducer {

    /**
     * Builder to create Layer Manager
     */
    public static class Builder {
        private Predicate<LayerManager> extendCheck;
        private Supplier<BloomFilter> supplier;
        private Consumer<LinkedList<BloomFilter>> cleanup;

        private Builder() {
            extendCheck = ExtendCheck.neverAdvance();
            cleanup = Cleanup.noCleanup();
        }

        /**
         * Builds the layer manager with the specified properties.
         *
         * @return a new LayerManager.
         */
        public LayerManager build() {
            Objects.requireNonNull(supplier, "Supplier must not be null");
            Objects.requireNonNull(extendCheck, "ExtendCheck must not be null");
            Objects.requireNonNull(cleanup, "Cleanup must not be null");
            return new LayerManager(supplier, extendCheck, cleanup, true);
        }

        /**
         * Sets the Consumer that cleans the list of Bloom filters.
         *
         * @param cleanup the Consumer that will modify the list of filters removing out
         *                dated or stale filters.
         * @return this for chaining.
         */
        public Builder setCleanup(Consumer<LinkedList<BloomFilter>> cleanup) {
            this.cleanup = cleanup;
            return this;
        }

        /**
         * Sets the extendCheck predicate. When the predicate returns {@code true} a new
         * target will be created.
         *
         * @param extendCheck The predicate to determine if a new target should be
         *                    created.
         * @return this for chaining.
         */
        public Builder setExtendCheck(Predicate<LayerManager> extendCheck) {
            this.extendCheck = extendCheck;
            return this;
        }

        /**
         * Sets the supplier of Bloom filters. When extendCheck creates a new target,
         * the supplier provides the instance of the Bloom filter.
         *
         * @param supplier The supplier of new Bloom filter instances.
         * @return this for chaining.
         */
        public Builder setSupplier(Supplier<BloomFilter> supplier) {
            this.supplier = supplier;
            return this;
        }
    }

    /**
     * Static methods to create a Consumer of a LinkedList of BloomFilter perform
     * tests on whether to reduce the collection of Bloom filters.
     */
    public static final class Cleanup {
        /**
         * A Cleanup that never removes anything.
         * @return A Consumer suitable for the LayerManager {@code cleanup} parameter.
         */
        public static Consumer<LinkedList<BloomFilter>> noCleanup() {
            return x -> {};
        }

        /**
         * Removes the earliest filters in the list when the the number of filters
         * exceeds maxSize.
         *
         * @param maxSize the maximum number of filters for the list. Must be greater
         *                than 0
         * @return A Consumer suitable for the LayerManager {@code cleanup} parameter.
         * @throws IllegalArgumentException if {@code maxSize <= 0}.
         */
        public static Consumer<LinkedList<BloomFilter>> onMaxSize(int maxSize) {
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
         * @return A Consumer suitable for the LayerManager {@code cleanup} parameter.
         */
        public static Consumer<LinkedList<BloomFilter>> removeEmptyTarget() {
            return x -> {
                if (x.getLast().cardinality() == 0) {
                    x.removeLast();
                }
            };
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
         * @param breakAt the number of filters to merge into each filter in the list.
         * @return A Predicate suitable for the LayerManager {@code extendCheck} parameter.
         * @throws IllegalArgumentException if {@code breakAt <= 0}
         */
        public static Predicate<LayerManager> advanceOnCount(int breakAt) {
            if (breakAt <= 0) {
                throw new IllegalArgumentException("'breakAt' must be greater than 0");
            }
            return new Predicate<LayerManager>() {
                int count;

                @Override
                public boolean test(LayerManager filter) {
                    return ++count % breakAt == 0;
                }
            };
        }

        /**
         * Advances the target once a merge has been performed.
         * @return A Predicate suitable for the LayerManager {@code extendCheck} parameter.
         */
        public static Predicate<LayerManager> advanceOnPopulated() {
            return lm -> !lm.filters.peekLast().isEmpty();
        }

        /**
         * Creates a new target after the current target is saturated. Saturation is
         * defined as the {@code Bloom filter estimated N >= maxN}.
         *
         * <p>An example usage is advancing on a calculated saturation by calling:
         * {@code ExtendCheck.advanceOnSaturation(shape.estimateMaxN()) }</p>
         *
         * @param maxN the maximum number of estimated items in the filter.
         * @return A Predicate suitable for the LayerManager {@code extendCheck} parameter.
         * @throws IllegalArgumentException if {@code maxN <= 0}
         */
        public static Predicate<LayerManager> advanceOnSaturation(double maxN) {
            if (maxN <= 0) {
                throw new IllegalArgumentException("'maxN' must be greater than 0");
            }
            return manager -> {
                BloomFilter bf = manager.filters.peekLast();
                return maxN <= bf.getShape().estimateN(bf.cardinality());
            };
        }

        /**
         * Does not automatically advance the target. @{code next()} must be called directly to
         * perform the advance.
         * @return A Predicate suitable for the LayerManager {@code extendCheck} parameter.
         */
        public static Predicate<LayerManager> neverAdvance() {
            return x -> false;
        }

        private ExtendCheck() {
        }
    }
    /**
     * Creates a new Builder with defaults of {@code ExtendCheck.neverAdvance()} and
     * {@code Cleanup.noCleanup()}.
     *
     * @return A builder.
     * @see ExtendCheck#neverAdvance()
     * @see Cleanup#noCleanup()
     */
    public static Builder builder() {
        return new Builder();
    }
    private final LinkedList<BloomFilter> filters = new LinkedList<>();
    private final Consumer<LinkedList<BloomFilter>> filterCleanup;

    private final Predicate<LayerManager> extendCheck;

    private final Supplier<BloomFilter> filterSupplier;

    /**
     * Constructor.
     *
     * @param filterSupplier the supplier of new Bloom filters to add the the list
     *                       when necessary.
     * @param extendCheck    The predicate that checks if a new filter should be
     *                       added to the list.
     * @param filterCleanup  the consumer that removes any old filters from the
     *                       list.
     * @param initialize     true if the filter list should be initialized.
     */
    private LayerManager(Supplier<BloomFilter> filterSupplier, Predicate<LayerManager> extendCheck,
            Consumer<LinkedList<BloomFilter>> filterCleanup, boolean initialize) {
        this.filterSupplier = filterSupplier;
        this.extendCheck = extendCheck;
        this.filterCleanup = filterCleanup;
        if (initialize) {
            addFilter();
        }
    }

    /**
     * Adds a new Bloom filter to the list.
     */
    private void addFilter() {
        BloomFilter bf = filterSupplier.get();
        if (bf == null) {
            throw new NullPointerException("filterSupplier returned null.");
        }
        filters.add(bf);
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
     * Creates a deep copy of this LayerManager.
     * <p><em>Filters in the copy are deep copies, not references, so changes in the copy
     * are NOT reflected in the original.</em></p>
     * <p>The {@code filterSupplier}, {@code extendCheck}, and the {@code filterCleanup} are shared between
     * the copy and this instance.</p>
     *
     * @return a copy of this layer Manager.
     */
    public LayerManager copy() {
        LayerManager newMgr = new LayerManager(filterSupplier, extendCheck, filterCleanup, false);
        for (BloomFilter bf : filters) {
            newMgr.filters.add(bf.copy());
        }
        return newMgr;
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
    public boolean forEachBloomFilter(Predicate<BloomFilter> bloomFilterPredicate) {
        for (BloomFilter bf : filters) {
            if (!bloomFilterPredicate.test(bf)) {
                return false;
            }
        }
        return true;
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
    public final BloomFilter get(int depth) {
        if (depth < 0 || depth >= filters.size()) {
            throw new NoSuchElementException(String.format("Depth must be in the range [0,%s)", filters.size()));
        }
        return filters.get(depth);
    }

    /**
     * Returns the number of filters in the LayerManager.  In the default LayerManager implementation
     * there is alwasy at least one layer.
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
    public final BloomFilter getTarget() {
        if (extendCheck.test(this)) {
            next();
        }
        return filters.peekLast();
    }

    /**
     * Forces an advance to the next depth. This method will clean-up the current
     * layers and generate a new filter layer. In most cases is it unnecessary to
     * call this method directly.
     * <p>
     * Ths method is used within {@link #getTarget()} when the configured
     * {@code ExtendCheck} returns {@code true}.
     * </p>
     */
    void next() {
        this.filterCleanup.accept(filters);
        addFilter();
    }
}
