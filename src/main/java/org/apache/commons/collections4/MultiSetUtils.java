/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4;

import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.collections4.multiset.PredicatedMultiSet;
import org.apache.commons.collections4.multiset.PredicatedSortedMultiSet;
import org.apache.commons.collections4.multiset.SynchronizedMultiSet;
import org.apache.commons.collections4.multiset.SynchronizedSortedMultiSet;
import org.apache.commons.collections4.multiset.TreeMultiSet;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;
import org.apache.commons.collections4.multiset.UnmodifiableSortedMultiSet;

/**
 * Provides utility methods and decorators for {@link MultiSet} and
 * {@link SortedMultiSet} instances.
 *
 * @since 4.1
 */
public class MultiSetUtils {

    /**
     * An empty unmodifiable multiset.
     */
    @SuppressWarnings("rawtypes") // OK, empty multiset is compatible with any type
    public static final MultiSet EMPTY_MULTISET =
        UnmodifiableMultiSet.unmodifiableMultiSet(new HashMultiSet<>());

    /**
     * An empty unmodifiable sorted multiset.
     *
     * @since 4.6.0
     */
    @SuppressWarnings("rawtypes") // OK, empty multiset is compatible with any type
    public static final SortedMultiSet EMPTY_SORTED_MULTISET =
        UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(new TreeMultiSet<>());

    /**
     * Gets an empty {@code MultiSet}.
     *
     * @param <E> The element type
     * @return an empty MultiSet
     */
    @SuppressWarnings("unchecked") // OK, empty multiset is compatible with any type
    public static <E> MultiSet<E> emptyMultiSet() {
        return EMPTY_MULTISET;
    }

    /**
     * Gets an empty {@code SortedMultiSet}.
     *
     * @param <E> The element type
     * @return an empty SortedMultiSet
     * @since 4.6.0
     */
    @SuppressWarnings("unchecked") // OK, empty multiset is compatible with any type
    public static <E> SortedMultiSet<E> emptySortedMultiSet() {
        return EMPTY_SORTED_MULTISET;
    }

    /**
     * Returns a predicated (validating) multiset backed by the given multiset.
     * <p>
     * Only objects that pass the test in the given predicate can be added to
     * the multiset. Trying to add an invalid object results in an
     * IllegalArgumentException. It is important not to use the original multiset
     * after invoking this method, as it is a backdoor for adding invalid
     * objects.
     * </p>
     *
     * @param <E> The element type
     * @param multiset the multiset to predicate, must not be null
     * @param predicate the predicate for the multiset, must not be null
     * @return a predicated multiset backed by the given multiset
     * @throws NullPointerException if the MultiSet or Predicate is null
     */
    public static <E> MultiSet<E> predicatedMultiSet(final MultiSet<E> multiset,
            final Predicate<? super E> predicate) {
        return PredicatedMultiSet.predicatedMultiSet(multiset, predicate);
    }

    /**
     * Returns a predicated (validating) sorted multiset backed by the given sorted
     * multiset.
     * <p>
     * Only objects that pass the test in the given predicate can be added to
     * the multiset. Trying to add an invalid object results in an
     * IllegalArgumentException. It is important not to use the original multiset
     * after invoking this method, as it is a backdoor for adding invalid
     * objects.
     * </p>
     *
     * @param <E> The element type
     * @param multiset the sorted multiset to predicate, must not be null
     * @param predicate the predicate for the multiset, must not be null
     * @return a predicated sorted multiset backed by the given sorted multiset
     * @throws NullPointerException if the SortedMultiSet or Predicate is null
     * @since 4.6.0
     */
    public static <E> SortedMultiSet<E> predicatedSortedMultiSet(final SortedMultiSet<E> multiset,
            final Predicate<? super E> predicate) {
        return PredicatedSortedMultiSet.predicatedSortedMultiSet(multiset, predicate);
    }

    /**
     * Returns a synchronized (thread-safe) multiset backed by the given multiset.
     * In order to guarantee serial access, it is critical that all access to the
     * backing multiset is accomplished through the returned multiset.
     * <p>
     * It is imperative that the user manually synchronize on the returned multiset
     * when iterating over it:
     * </p>
     * <pre>
     * MultiSet multiset = MultiSetUtils.synchronizedMultiSet(new HashMultiSet());
     * ...
     * synchronized(multiset) {
     *     Iterator i = multiset.iterator(); // Must be in synchronized block
     *     while (i.hasNext())
     *         foo(i.next());
     *     }
     * }
     * </pre>
     *
     * Failure to follow this advice may result in non-deterministic behavior.
     *
     * @param <E> The element type
     * @param multiset the multiset to synchronize, must not be null
     * @return a synchronized multiset backed by that multiset
     * @throws NullPointerException if the MultiSet is null
     */
    public static <E> MultiSet<E> synchronizedMultiSet(final MultiSet<E> multiset) {
        return SynchronizedMultiSet.synchronizedMultiSet(multiset);
    }

    /**
     * Returns a synchronized (thread-safe) sorted multiset backed by the given
     * sorted multiset. In order to guarantee serial access, it is critical that all
     * access to the backing multiset is accomplished through the returned multiset.
     * <p>
     * It is imperative that the user manually synchronize on the returned multiset
     * when iterating over it:
     * </p>
     * <pre>
     * SortedMultiSet multiset = MultiSetUtils.synchronizedSortedMultiSet(new TreeMultiSet());
     * ...
     * synchronized(multiset) {
     *     Iterator i = multiset.iterator(); // Must be in synchronized block
     *     while (i.hasNext())
     *         foo(i.next());
     *     }
     * }
     * </pre>
     *
     * Failure to follow this advice may result in non-deterministic behavior.
     *
     * @param <E> The element type
     * @param multiset the sorted multiset to synchronize, must not be null
     * @return a synchronized sorted multiset backed by that multiset
     * @throws NullPointerException if the SortedMultiSet is null
     * @since 4.6.0
     */
    public static <E> SortedMultiSet<E> synchronizedSortedMultiSet(final SortedMultiSet<E> multiset) {
        return SynchronizedSortedMultiSet.synchronizedSortedMultiSet(multiset);
    }

    /**
     * Returns an unmodifiable view of the given multiset. Any modification attempts
     * to the returned multiset will raise an {@link UnsupportedOperationException}.
     *
     * @param <E> The element type
     * @param multiset the multiset whose unmodifiable view is to be returned, must not be null
     * @return an unmodifiable view of that multiset
     * @throws NullPointerException if the MultiSet is null
     */
    public static <E> MultiSet<E> unmodifiableMultiSet(final MultiSet<? extends E> multiset) {
        return UnmodifiableMultiSet.unmodifiableMultiSet(multiset);
    }

    /**
     * Returns an unmodifiable view of the given sorted multiset. Any modification
     * attempts to the returned multiset will raise an
     * {@link UnsupportedOperationException}.
     *
     * @param <E> The element type
     * @param multiset the sorted multiset whose unmodifiable view is to be returned, must not be null
     * @return an unmodifiable view of that sorted multiset
     * @throws NullPointerException if the SortedMultiSet is null
     * @since 4.6.0
     */
    public static <E> SortedMultiSet<E> unmodifiableSortedMultiSet(final SortedMultiSet<? extends E> multiset) {
        return UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(multiset);
    }

    /**
     * Don't allow instances.
     */
    private MultiSetUtils() {
        // empty
    }

}
