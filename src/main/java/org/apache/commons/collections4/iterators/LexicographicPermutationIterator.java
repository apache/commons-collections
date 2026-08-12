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
package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This iterator creates permutations of an input collection, using the
 * lexicographical order.
 * <p>
 * Iteration starts at the arrangement in which the elements are given, and each
 * call to {@code next()} advances to the smallest arrangement greater than the
 * current one. Iteration therefore ends at the largest arrangement, and the ones
 * preceding the given arrangement are never returned: only a collection already
 * sorted according to the ordering in use yields the complete set of
 * permutations. Callers wanting the complete set must sort the collection
 * beforehand, just as callers of
 * {@link java.util.Collections#binarySearch(java.util.List, Object) binarySearch}
 * must. Callers wanting to enumerate one part of the set, to resume from a
 * previously reached arrangement or to split the work, may start anywhere.
 * </p>
 * <p>
 * The starting arrangement is the iteration order of the given collection, so
 * collections whose iteration order is unspecified, such as {@link java.util.HashSet},
 * make poor input: which permutations are returned is then unspecified too.
 * </p>
 * <p>
 * The iterator might return fewer than n! permutations of the input collection,
 * either because the collection was not sorted according to the ordering in use,
 * as described above, or because duplicated permutations are skipped: equal
 * elements are not distinguished from one another.
 * The {@code remove()} operation is not supported, and will throw an
 * {@code UnsupportedOperationException}.
 * </p>
 * <p>
 * NOTE: in case an empty collection is provided, the iterator will
 * return exactly one empty list as result, as 0! = 1.
 * </p>
 * <p>
 * NOTE: {@link PermutationIterator} differs on both counts. It returns exactly n!
 * permutations whatever the order of the input collection. The two iterators are
 * therefore not interchangeable.
 * </p>
 *
 * @param <E>  the type of the objects being permuted
 * @see PermutationIterator
 * @since 4.6.0
 */
public class LexicographicPermutationIterator<E> implements Iterator<List<E>> {

    /**
     * The comparator used to define order of generation,
     * or null if it uses the natural ordering.
     */
    private final Comparator<? super E> comparator;

    /**
     * Next permutation to return. When a permutation is requested
     * this instance is provided and the next one is computed.
     */
    private List<E> nextPermutation;

    /**
     * Standard constructor for this class, using the natural ordering of the elements.
     * <p>
     * Iteration starts at the arrangement in which the collection iterates its
     * elements; sort the collection first to obtain the complete set of permutations.
     * </p>
     *
     * @param collection  The collection to generate permutations for
     * @throws NullPointerException if collection is null
     */
    public LexicographicPermutationIterator(final Collection<? extends E> collection) {
        this(collection, null);
    }

    /**
     * Constructs an instance using the given comparator to order the elements.
     * <p>
     * Iteration starts at the arrangement in which the collection iterates its
     * elements; sort the collection with the same comparator first to obtain the
     * complete set of permutations.
     * </p>
     *
     * @param collection  The collection to generate permutations for
     * @param comparator  The comparator used to define the order of generation,
     *                    or null to use the natural ordering of the elements
     * @throws NullPointerException if collection is null
     */
    public LexicographicPermutationIterator(final Collection<? extends E> collection, final Comparator<? super E> comparator) {
        Objects.requireNonNull(collection, "collection");
        nextPermutation = new ArrayList<>(collection);
        this.comparator = comparator;
    }

    /**
     * Indicates if there are more permutation available.
     *
     * @return true if there are more permutations, otherwise false
     */
    @Override
    public boolean hasNext() {
        return nextPermutation != null;
    }

    /**
     * Returns the next permutation of the input collection.
     *
     * @return A list of the permutator's elements representing a permutation
     * @throws NoSuchElementException if there are no more permutations
     * @throws ClassCastException if no comparator was supplied and the elements are
     *         not mutually {@link Comparable}
     */
    @Override
    public List<E> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        final int size = nextPermutation.size();
        List<E> nextP = null;

        // find the pivot: the rightmost element that is smaller than its successor.
        // if there is none the current permutation is the last one in lexicographical order
        int i = size - 2;
        while (i >= 0 && compareElements(nextPermutation.get(i), nextPermutation.get(i + 1)) >= 0) {
            --i;
        }

        if (i >= 0) {
            // find the rightmost element greater than the pivot; the tail is descending,
            // so this is the pivot's successor in the remaining elements
            int j = size - 1;
            while (j >= i && compareElements(nextPermutation.get(i), nextPermutation.get(j)) >= 0) {
                --j;
            }

            // swap the pivot with its successor, then reverse the descending tail
            // into ascending order to obtain the smallest larger permutation
            nextP = new ArrayList<>(nextPermutation);
            Collections.swap(nextP, i, j);
            final List<E> subList = nextP.subList(i + 1, nextP.size());
            Collections.reverse(subList);
        }

        final List<E> result = nextPermutation;
        nextPermutation = nextP;
        return result;
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }

    /**
     * Compares this iterator to another for equality. Two iterators are equal when
     * they use equal comparators and are positioned at an equal next permutation.
     *
     * @param o  The object to compare to this instance
     * @return true if the given object is an equal iterator, otherwise false
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LexicographicPermutationIterator<?> that = (LexicographicPermutationIterator<?>) o;
        return Objects.equals(comparator, that.comparator) && Objects.equals(nextPermutation, that.nextPermutation);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}. Note that the
     * hash code changes as the iterator advances.
     *
     * @return A hash code for this instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(comparator, nextPermutation);
    }

    /**
     * Compares two elements using the comparator, or their natural ordering if no
     * comparator was supplied.
     *
     * @param e1  The first element to compare
     * @param e2  The second element to compare
     * @return a negative integer, zero, or a positive integer as the first element
     *         is less than, equal to, or greater than the second
     * @throws ClassCastException if no comparator was supplied and the elements are
     *         not mutually {@link Comparable}
     */
    @SuppressWarnings("unchecked")
    private int compareElements(final E e1, final E e2) {
        return comparator == null
                ? ((Comparable<? super E>) e1).compareTo(e2)
                : comparator.compare(e1, e2);
    }

}
