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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Implementation of the methods to manage the Layers in a Layered Bloom filter.
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
 * <li>Cleanup - A Consumer of a LinkedList of BloomFilter that removes any
 * expired or out dated filters from the list.</li>
 * </ul>
 * <p>
 * When extendCheck returns {@code true} the following steps are taken:
 * </p>
 * <ol>
 * <li>If the current target is empty it is removed.</li>
 * <li>{@code Cleanup} is called</li>
 * <li>{@code FilterSuplier} is executed and the new filter added to the list as
 * the {@code target} filter.</li>
 * </ol>
 */
public class LayerManager implements BloomFilterProducer {

    /**
     * Static methods an variable for standard extend checks.
     *
     */
    public static class ExtendCheck {
        private ExtendCheck() {
        }

        /**
         * Advances the target once a merge has been performed.
         */
        public static final Predicate<LayerManager> ADVANCE_ON_POPULATED = lm -> {
            return !lm.filters.isEmpty() && !lm.filters.peekLast().forEachBitMap(y -> y == 0);
        };

        /**
         * Does not automatically advance the target. next() must be called directly to
         * perform the advance.
         */
        public static final Predicate<LayerManager> NEVER_ADVANCE = x -> false;

        /**
         * Calculates the estimated number of Bloom filters (n) that have been merged
         * into the target and compares that with the estimated maximum expected n based
         * on the shape. If the target is full then a new target is created.
         */
        public static final Predicate<LayerManager> advanceOnCalculatedFull(Shape shape) {
            return advanceOnSaturation(shape.estimateMaxN());
        }

        /**
         * Creates a new target after a specific number of filters have been added to
         * the current target.
         *
         * @param breakAt the number of filters to merge into each filter in the list.
         * @return a Predicate suitable for the LayerManager externCheck parameter.
         */
        public static Predicate<LayerManager> advanceOnCount(int breakAt) {
            return new Predicate<LayerManager>() {
                int count = 0;

                @Override
                public boolean test(LayerManager filter) {
                    return ++count % breakAt == 0;
                }
            };
        }

        /**
         * Creates a new target after the current target is saturated. Saturation is
         * defined as the estimated N of the target Bloom filter being greater than the
         * maxN specified.
         * <p>
         * This method uses the integer estimation found in the Bloom filter. To use the
         * estimation from the Shape use the double version of this function.
         *
         * @param maxN the maximum number of estimated items in the filter.
         * @return a Predicate suitable for an ExtendCheck.
         */
        public static final Predicate<LayerManager> advanceOnSaturation(int maxN) {
            return new Predicate<LayerManager>() {
                @Override
                public boolean test(LayerManager manager) {
                    if (manager.filters.isEmpty()) {
                        return false;
                    }
                    return maxN <= manager.filters.peekLast().estimateN();
                }

            };
        }

        /**
         * Creates a new target after the current target is saturated. Saturation is
         * defined as the estimated N of the target Bloom filter being greater than the
         * maxN specified.
         * <p>
         * This method uses the integer estimation found in the Bloom filter. To use the
         * estimation from the Shape use the double version of this function.
         *
         * @param maxN the maximum number of estimated items in the filter.
         * @return a Predicate suitable for an ExtendCheck.
         */
        public static final Predicate<LayerManager> advanceOnSaturation(double maxN) {
            return new Predicate<LayerManager>() {
                @Override
                public boolean test(LayerManager manager) {
                    if (manager.filters.isEmpty()) {
                        return false;
                    }
                    BloomFilter bf = manager.filters.peekLast();
                    return maxN <= bf.getShape().estimateN(bf.cardinality());
                }

            };
        }

    }

    /**
     * Static methods to create a Consumer of a LinkedList of BloomFilter to manage
     * the size of the list.
     *
     */
    public static class Cleanup {
        private Cleanup() {
        }

        /**
         * A Cleanup that never removes anything.
         */
        public static final Consumer<LinkedList<BloomFilter>> NO_CLEANUP = x -> {
        };

        /**
         * Removes the earliest filters in the list when the the number of filters
         * exceeds maxSize.
         *
         * @param maxSize the maximum number of filters for the list.
         * @return A Consumer for the LayerManager filterCleanup constructor argument.
         */
        public static final Consumer<LinkedList<BloomFilter>> onMaxSize(int maxSize) {
            return (ll) -> {
                while (ll.size() > maxSize) {
                    ll.removeFirst();
                }
            };
        }
    }

    private final LinkedList<BloomFilter> filters = new LinkedList<>();
    private final Consumer<LinkedList<BloomFilter>> filterCleanup;
    private final Predicate<LayerManager> extendCheck;
    private final Supplier<BloomFilter> filterSupplier;

    /**
     * Creates a new Builder with defaults of NEVER_ADVANCE and NO_CLEANUP
     *
     * @return A builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Constructor.
     *
     * @param filterSupplier the supplier of new Bloom filters to add the the list
     *                       when necessary.
     * @param extendCheck    The predicate that checks if a new filter should be
     *                       added to the list.
     * @param filterCleanup  the consumer the removes any old filters from the list.
     */
    private LayerManager(Supplier<BloomFilter> filterSupplier, Predicate<LayerManager> extendCheck,
            Consumer<LinkedList<BloomFilter>> filterCleanup) {
        this.filterSupplier = filterSupplier;
        this.extendCheck = extendCheck;
        this.filterCleanup = filterCleanup;
        filters.add(this.filterSupplier.get());
    }

    /**
     * Creates a deep copy of this LayerManager.
     *
     * @return a copy of this layer Manager.
     */
    public LayerManager copy() {
        LayerManager newMgr = new LayerManager(filterSupplier, extendCheck, filterCleanup);
        newMgr.filters.clear();
        for (BloomFilter bf : filters) {
            newMgr.filters.add(bf.copy());
        }
        return newMgr;
    }

    /**
     * Forces an advance to the next depth for subsequent merges. Executes the same
     * logic as when {@code ExtendCheck} returns {@code true}
     */
    public void next() {
        if (!filters.isEmpty() && filters.getLast().cardinality() == 0) {
            filters.removeLast();
        }
        this.filterCleanup.accept(filters);
        filters.add(this.filterSupplier.get());
    }

    /**
     * Returns the number of filters in the LayerManager.
     *
     * @return the current depth.
     */
    public final int getDepth() {
        return filters.size();
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
     * Returns the current target filter. If the a new filter should be created
     * based on {@code extendCheck} it will be created before this method returns.
     *
     * @return the current target filter.
     */
    public final BloomFilter target() {
        if (extendCheck.test(this)) {
            next();
        }
        return filters.peekLast();
    }

    /**
     * Clear all the filters in the layer manager, and set up a new one as the
     * target.
     */
    public final void clear() {
        filters.clear();
        next();
    }

    /**
     * Executes a Bloom filter Predicate on each Bloom filter in the manager in
     * depth order. Oldest filter first.
     *
     * @param bloomFilterPredicate the predicate to evaluate each Bloom filter with.
     * @return {@code false} when the first filter fails the predicate test. Returns
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
     * Builder to create Layer Manager
     */
    public static class Builder {
        private Predicate<LayerManager> extendCheck;
        private Supplier<BloomFilter> supplier;
        private Consumer<LinkedList<BloomFilter>> cleanup;

        private Builder() {
            extendCheck = ExtendCheck.NEVER_ADVANCE;
            cleanup = Cleanup.NO_CLEANUP;
        }

        public LayerManager build() {
            if (supplier == null) {
                throw new IllegalStateException("Supplier must not be null");
            }
            if (extendCheck == null) {
                throw new IllegalStateException("ExtendCheck must not be null");
            }
            if (cleanup == null) {
                throw new IllegalStateException("Cleanup must not be null");
            }
            return new LayerManager(supplier, extendCheck, cleanup);
        }

        public Builder withExtendCheck(Predicate<LayerManager> extendCheck) {
            this.extendCheck = extendCheck;
            return this;
        }

        public Builder withSuplier(Supplier<BloomFilter> supplier) {
            this.supplier = supplier;
            return this;
        }

        public Builder withCleanup(Consumer<LinkedList<BloomFilter>> cleanup) {
            this.cleanup = cleanup;
            return this;
        }
    }
}
