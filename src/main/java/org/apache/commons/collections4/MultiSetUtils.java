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

import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.collections4.multiset.PredicatedMultiSet;
import org.apache.commons.collections4.multiset.PredicatedSortedMultiSet;
import org.apache.commons.collections4.multiset.SynchronizedMultiSet;
import org.apache.commons.collections4.multiset.SynchronizedSortedMultiSet;
import org.apache.commons.collections4.multiset.TransformedMultiSet;
import org.apache.commons.collections4.multiset.TransformedSortedMultiSet;
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
     * Returns {@code true} if {@code superMultiSet} contains at least as many
     * occurrences of each element as {@code subMultiSet} does; in other words,
     * whether {@code subMultiSet} is a sub-multiset of {@code superMultiSet}.
     * <p>
     * This method provides the cardinality-respecting behavior of
     * {@link Bag#containsAll(java.util.Collection)} under an explicitly named
     * method. To compare against a plain collection, wrap it first, for example
     * {@code containsOccurrences(multiSet, new HashMultiSet<>(coll))}.
     * </p>
     *
     * @param superMultiSet the multiset to check against, must not be null
     * @param subMultiSet the multiset whose occurrences must all be present, must not be null
     * @return {@code true} if {@code superMultiSet} contains all occurrences in {@code subMultiSet}
     * @throws NullPointerException if either MultiSet is null
     * @since 4.6.0
     */
    public static boolean containsOccurrences(final MultiSet<?> superMultiSet, final MultiSet<?> subMultiSet) {
        Objects.requireNonNull(superMultiSet, "superMultiSet");
        Objects.requireNonNull(subMultiSet, "subMultiSet");
        for (final MultiSet.Entry<?> entry : subMultiSet.entrySet()) {
            if (superMultiSet.getCount(entry.getElement()) < entry.getCount()) {
                return false;
            }
        }
        return true;
    }

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
     * For each occurrence of an element in {@code occurrencesToRemove}, removes
     * one occurrence of that element from {@code multiSetToModify}, if present.
     * That is, if {@code occurrencesToRemove} contains {@code n} occurrences of
     * an element, {@code multiSetToModify} will have {@code n} fewer occurrences,
     * assuming it had at least {@code n} to begin with.
     * <p>
     * This method provides the cardinality-respecting behavior of
     * {@link Bag#removeAll(java.util.Collection)} under an explicitly named
     * method. To remove the occurrences of a plain collection, wrap it first,
     * for example {@code removeOccurrences(multiSet, new HashMultiSet<>(coll))}.
     * </p>
     *
     * @param multiSetToModify the multiset to remove occurrences from, must not be null
     * @param occurrencesToRemove the occurrences to remove, must not be null
     * @return {@code true} if {@code multiSetToModify} was changed as a result of this operation
     * @throws NullPointerException if either MultiSet is null
     * @since 4.6.0
     */
    public static boolean removeOccurrences(final MultiSet<?> multiSetToModify, final MultiSet<?> occurrencesToRemove) {
        Objects.requireNonNull(multiSetToModify, "multiSetToModify");
        Objects.requireNonNull(occurrencesToRemove, "occurrencesToRemove");
        if (multiSetToModify == occurrencesToRemove) {
            final boolean changed = !multiSetToModify.isEmpty();
            multiSetToModify.clear();
            return changed;
        }
        boolean changed = false;
        // snapshot the entries to avoid ConcurrentModificationException when
        // occurrencesToRemove is a view backed by multiSetToModify
        for (final MultiSet.Entry<?> entry : new ArrayList<>(occurrencesToRemove.entrySet())) {
            if (multiSetToModify.remove(entry.getElement(), entry.getCount()) > 0) {
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Modifies {@code multiSetToModify} so that no element has more occurrences
     * than it has in {@code occurrencesToRetain}. That is, if
     * {@code occurrencesToRetain} contains {@code n} occurrences of an element
     * and {@code multiSetToModify} has {@code m > n} occurrences, {@code m - n}
     * occurrences are removed; elements not contained in
     * {@code occurrencesToRetain} are removed entirely.
     * <p>
     * This method provides the cardinality-respecting behavior of
     * {@link Bag#retainAll(java.util.Collection)} under an explicitly named
     * method. To retain the occurrences of a plain collection, wrap it first,
     * for example {@code retainOccurrences(multiSet, new HashMultiSet<>(coll))}.
     * </p>
     *
     * @param <E> The element type
     * @param multiSetToModify the multiset to limit occurrences in, must not be null
     * @param occurrencesToRetain the occurrences to retain, must not be null
     * @return {@code true} if {@code multiSetToModify} was changed as a result of this operation
     * @throws NullPointerException if either MultiSet is null
     * @since 4.6.0
     */
    public static <E> boolean retainOccurrences(final MultiSet<E> multiSetToModify, final MultiSet<?> occurrencesToRetain) {
        Objects.requireNonNull(multiSetToModify, "multiSetToModify");
        Objects.requireNonNull(occurrencesToRetain, "occurrencesToRetain");
        boolean changed = false;
        for (final E element : new ArrayList<>(multiSetToModify.uniqueSet())) {
            final int retainCount = occurrencesToRetain.getCount(element);
            if (multiSetToModify.getCount(element) > retainCount) {
                multiSetToModify.setCount(element, retainCount);
                changed = true;
            }
        }
        return changed;
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
     * Returns a transformed multiset backed by the given multiset.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * MultiSet. It is important not to use the original multiset after invoking this
     * method, as it is a backdoor for adding untransformed objects.
     * </p>
     * <p>
     * Existing entries in the specified multiset will not be transformed.
     * If you want that behavior, see
     * {@link TransformedMultiSet#transformedMultiSet(MultiSet, Transformer)}.
     * </p>
     *
     * @param <E> The element type
     * @param multiset the multiset to transform, must not be null
     * @param transformer the transformer for the multiset, must not be null
     * @return a transformed multiset backed by the given multiset
     * @throws NullPointerException if the MultiSet or Transformer is null
     * @since 4.6.0
     */
    public static <E> MultiSet<E> transformingMultiSet(final MultiSet<E> multiset,
            final Transformer<? super E, ? extends E> transformer) {
        return TransformedMultiSet.transformingMultiSet(multiset, transformer);
    }

    /**
     * Returns a transformed sorted multiset backed by the given multiset.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * MultiSet. It is important not to use the original multiset after invoking this
     * method, as it is a backdoor for adding untransformed objects.
     * </p>
     * <p>
     * Existing entries in the specified multiset will not be transformed.
     * If you want that behavior, see
     * {@link TransformedSortedMultiSet#transformedSortedMultiSet(SortedMultiSet, Transformer)}.
     * </p>
     *
     * @param <E> The element type
     * @param multiset the sorted multiset to transform, must not be null
     * @param transformer the transformer for the multiset, must not be null
     * @return a transformed sorted multiset backed by the given multiset
     * @throws NullPointerException if the SortedMultiSet or Transformer is null
     * @since 4.6.0
     */
    public static <E> SortedMultiSet<E> transformingSortedMultiSet(final SortedMultiSet<E> multiset,
            final Transformer<? super E, ? extends E> transformer) {
        return TransformedSortedMultiSet.transformingSortedMultiSet(multiset, transformer);
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
