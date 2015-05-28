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
package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.collections4.set.PredicatedSet;
import org.apache.commons.collections4.set.PredicatedSortedSet;
import org.apache.commons.collections4.set.TransformedSet;
import org.apache.commons.collections4.set.TransformedSortedSet;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.collections4.set.UnmodifiableSortedSet;

/**
 * Provides utility methods and decorators for
 * {@link Set} and {@link SortedSet} instances.
 *
 * @since 2.1
 * @version $Id$
 */
public class SetUtils {

    /**
     * Get a typed empty unmodifiable Set.
     * @param <E> the element type
     * @return an empty Set
     */
    public static <E> Set<E> emptySet() {
        return Collections.<E>emptySet();
    }

    /**
     * An empty unmodifiable sorted set.
     * This is not provided in the JDK.
     */
    @SuppressWarnings("rawtypes")
    public static final SortedSet EMPTY_SORTED_SET =
            UnmodifiableSortedSet.unmodifiableSortedSet(new TreeSet<Object>());

    /**
     * Get a typed empty unmodifiable sorted set.
     * @param <E> the element type
     * @return an empty sorted Set
     */
    @SuppressWarnings("unchecked") // empty set is OK for any type
    public static <E> SortedSet<E> emptySortedSet() {
        return (SortedSet<E>) EMPTY_SORTED_SET;
    }

    /**
     * <code>SetUtils</code> should not normally be instantiated.
     */
    private SetUtils() {}

    //-----------------------------------------------------------------------

    /**
     * Returns an immutable empty set if the argument is <code>null</code>,
     * or the argument itself otherwise.
     *
     * @param <T> the element type
     * @param set the set, possibly <code>null</code>
     * @return an empty set if the argument is <code>null</code>
     */
    public static <T> Set<T> emptyIfNull(final Set<T> set) {
        return set == null ? Collections.<T>emptySet() : set;
    }

    /**
     * Tests two sets for equality as per the <code>equals()</code> contract
     * in {@link java.util.Set#equals(java.lang.Object)}.
     * <p>
     * This method is useful for implementing <code>Set</code> when you cannot
     * extend AbstractSet. The method takes Collection instances to enable other
     * collection types to use the Set implementation algorithm.
     * <p>
     * The relevant text (slightly paraphrased as this is a static method) is:
     * <blockquote>
     * <p>Two sets are considered equal if they have
     * the same size, and every member of the first set is contained in
     * the second. This ensures that the {@code equals} method works
     * properly across different implementations of the {@code Set}
     * interface.</p>
     *
     * <p>
     * This implementation first checks if the two sets are the same object:
     * if so it returns {@code true}.  Then, it checks if the two sets are
     * identical in size; if not, it returns false. If so, it returns
     * {@code a.containsAll((Collection) b)}.</p>
     * </blockquote>
     *
     * @see java.util.Set
     * @param set1  the first set, may be null
     * @param set2  the second set, may be null
     * @return whether the sets are equal by value comparison
     */
    public static boolean isEqualSet(final Collection<?> set1, final Collection<?> set2) {
        if (set1 == set2) {
            return true;
        }
        if (set1 == null || set2 == null || set1.size() != set2.size()) {
            return false;
        }

        return set1.containsAll(set2);
    }

    /**
     * Generates a hash code using the algorithm specified in
     * {@link java.util.Set#hashCode()}.
     * <p>
     * This method is useful for implementing <code>Set</code> when you cannot
     * extend AbstractSet. The method takes Collection instances to enable other
     * collection types to use the Set implementation algorithm.
     *
     * @param <T> the element type
     * @see java.util.Set#hashCode()
     * @param set  the set to calculate the hash code for, may be null
     * @return the hash code
     */
    public static <T> int hashCodeForSet(final Collection<T> set) {
        if (set == null) {
            return 0;
        }

        int hashCode = 0;
        for (final T obj : set) {
            if (obj != null) {
                hashCode += obj.hashCode();
            }
        }
        return hashCode;
    }

    /**
     * Returns a new hash set that matches elements based on <code>==</code> not
     * <code>equals()</code>.
     * <p>
     * <strong>This set will violate the detail of various Set contracts.</note>
     * As a general rule, don't compare this set to other sets. In particular, you can't
     * use decorators like {@link ListOrderedSet} on it, which silently assume that these
     * contracts are fulfilled.</strong>
     * <p>
     * <strong>Note that the returned set is not synchronized and is not thread-safe.</strong>
     * If you wish to use this set from multiple threads concurrently, you must use
     * appropriate synchronization. The simplest approach is to wrap this map
     * using {@link java.util.Collections#synchronizedSet(Set)}. This class may throw
     * exceptions when accessed by concurrent threads without synchronization.
     *
     * @param <E>  the element type
     * @return a new identity hash set
     * @since 4.1
     */
    public static <E> Set<E> identityHashSet() {
        return Collections.newSetFromMap(new IdentityHashMap<E, Boolean>());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized set backed by the given set.
     * <p>
     * You must manually synchronize on the returned set's iterator to
     * avoid non-deterministic behavior:
     *
     * <pre>
     * Set s = SetUtils.synchronizedSet(mySet);
     * synchronized (s) {
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     *
     * This method is just a wrapper for {@link Collections#synchronizedSet(Set)}.
     *
     * @param <E> the element type
     * @param set  the set to synchronize, must not be null
     * @return a synchronized set backed by the given set
     * @throws IllegalArgumentException  if the set is null
     */
    public static <E> Set<E> synchronizedSet(final Set<E> set) {
        return Collections.synchronizedSet(set);
    }

    /**
     * Returns an unmodifiable set backed by the given set.
     * <p>
     * This method uses the implementation in the decorators subpackage.
     *
     * @param <E> the element type
     * @param set  the set to make unmodifiable, must not be null
     * @return an unmodifiable set backed by the given set
     * @throws IllegalArgumentException  if the set is null
     */
    public static <E> Set<E> unmodifiableSet(final Set<? extends E> set) {
        return UnmodifiableSet.unmodifiableSet(set);
    }

    /**
     * Returns a predicated (validating) set backed by the given set.
     * <p>
     * Only objects that pass the test in the given predicate can be added to the set.
     * Trying to add an invalid object results in an IllegalArgumentException.
     * It is important not to use the original set after invoking this method,
     * as it is a backdoor for adding invalid objects.
     *
     * @param <E> the element type
     * @param set  the set to predicate, must not be null
     * @param predicate  the predicate for the set, must not be null
     * @return a predicated set backed by the given set
     * @throws IllegalArgumentException  if the Set or Predicate is null
     */
    public static <E> Set<E> predicatedSet(final Set<E> set, final Predicate<? super E> predicate) {
        return PredicatedSet.predicatedSet(set, predicate);
    }

    /**
     * Returns a transformed set backed by the given set.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * Set. It is important not to use the original set after invoking this
     * method, as it is a backdoor for adding untransformed objects.
     * <p>
     * Existing entries in the specified set will not be transformed.
     * If you want that behaviour, see {@link TransformedSet#transformedSet}.
     *
     * @param <E> the element type
     * @param set  the set to transform, must not be null
     * @param transformer  the transformer for the set, must not be null
     * @return a transformed set backed by the given set
     * @throws IllegalArgumentException  if the Set or Transformer is null
     */
    public static <E> Set<E> transformedSet(final Set<E> set, final Transformer<? super E, ? extends E> transformer) {
        return TransformedSet.transformingSet(set, transformer);
    }

    /**
     * Returns a set that maintains the order of elements that are added
     * backed by the given set.
     * <p>
     * If an element is added twice, the order is determined by the first add.
     * The order is observed through the iterator or toArray.
     *
     * @param <E> the element type
     * @param set  the set to order, must not be null
     * @return an ordered set backed by the given set
     * @throws IllegalArgumentException  if the Set is null
     */
    public static <E> Set<E> orderedSet(final Set<E> set) {
        return ListOrderedSet.listOrderedSet(set);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized sorted set backed by the given sorted set.
     * <p>
     * You must manually synchronize on the returned set's iterator to
     * avoid non-deterministic behavior:
     *
     * <pre>
     * Set s = SetUtils.synchronizedSortedSet(mySet);
     * synchronized (s) {
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     *
     * This method is just a wrapper for {@link Collections#synchronizedSortedSet(SortedSet)}.
     *
     * @param <E> the element type
     * @param set  the sorted set to synchronize, must not be null
     * @return a synchronized set backed by the given set
     * @throws IllegalArgumentException  if the set is null
     */
    public static <E> SortedSet<E> synchronizedSortedSet(final SortedSet<E> set) {
        return Collections.synchronizedSortedSet(set);
    }

    /**
     * Returns an unmodifiable sorted set backed by the given sorted set.
     * <p>
     * This method uses the implementation in the decorators subpackage.
     *
     * @param <E> the element type
     * @param set  the sorted set to make unmodifiable, must not be null
     * @return an unmodifiable set backed by the given set
     * @throws IllegalArgumentException  if the set is null
     */
    public static <E> SortedSet<E> unmodifiableSortedSet(final SortedSet<E> set) {
        return UnmodifiableSortedSet.unmodifiableSortedSet(set);
    }

    /**
     * Returns a predicated (validating) sorted set backed by the given sorted set.
     * <p>
     * Only objects that pass the test in the given predicate can be added to the set.
     * Trying to add an invalid object results in an IllegalArgumentException.
     * It is important not to use the original set after invoking this method,
     * as it is a backdoor for adding invalid objects.
     *
     * @param <E> the element type
     * @param set  the sorted set to predicate, must not be null
     * @param predicate  the predicate for the sorted set, must not be null
     * @return a predicated sorted set backed by the given sorted set
     * @throws IllegalArgumentException  if the Set or Predicate is null
     */
    public static <E> SortedSet<E> predicatedSortedSet(final SortedSet<E> set, final Predicate<? super E> predicate) {
        return PredicatedSortedSet.predicatedSortedSet(set, predicate);
    }

    /**
     * Returns a transformed sorted set backed by the given set.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * Set. It is important not to use the original set after invoking this
     * method, as it is a backdoor for adding untransformed objects.
     * <p>
     * Existing entries in the specified set will not be transformed.
     * If you want that behaviour, see {@link TransformedSortedSet#transformedSortedSet}.
     *
     * @param <E> the element type
     * @param set  the set to transform, must not be null
     * @param transformer  the transformer for the set, must not be null
     * @return a transformed set backed by the given set
     * @throws IllegalArgumentException  if the Set or Transformer is null
     */
    public static <E> SortedSet<E> transformedSortedSet(final SortedSet<E> set,
                                                        final Transformer<? super E, ? extends E> transformer) {
        return TransformedSortedSet.transformingSortedSet(set, transformer);
    }

}
