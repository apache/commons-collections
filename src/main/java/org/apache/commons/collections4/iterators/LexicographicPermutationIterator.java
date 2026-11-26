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
 * NOTE: the elements must be mutually comparable under the ordering in use, and a
 * {@code null} element is permitted only with a comparator that permits it. The
 * constructor rejects other input with a {@code ClassCastException} or a
 * {@code NullPointerException}.
 * </p>
 * <p>
 * NOTE: {@link PermutationIterator} always returns exactly n! permutations: the
 * order of the input collection does not matter, and equal elements are treated
 * as distinct. The two iterators are therefore not interchangeable.
 * </p>
 *
 * @param <E>  the type of the objects being permuted
 * @since 4.7.0
 * @see PermutationIterator
 */
public class LexicographicPermutationIterator<E> implements Iterator<List<E>> {

    /**
     * The comparator used to define order of generation,
     * or null if it uses the natural ordering.
     */
    private final Comparator<? super E> comparator;

    /**
     * Next permutation to return, or null when there is none left. When a permutation
     * is requested a copy of this list is provided, and the list is then rearranged
     * in place into the next one.
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
     * @throws ClassCastException if the elements are not mutually {@link Comparable}
     * @throws NullPointerException if collection is null or contains a null element
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
     * <p>
     * The elements are checked against the ordering in use, so that input it rejects is
     * refused here and not after some permutations have been returned. Each element is
     * compared with its successor and the last one with itself, so an ordering that
     * rejects only some other pair of elements is detected later, by {@code next()}.
     * </p>
     *
     * @param collection  The collection to generate permutations for
     * @param comparator  The comparator used to define the order of generation,
     *                    or null to use the natural ordering of the elements
     * @throws ClassCastException if the elements are not mutually comparable under the
     *         ordering in use
     * @throws NullPointerException if collection is null, or if it contains a null element
     *         that the ordering in use does not permit
     */
    public LexicographicPermutationIterator(final Collection<? extends E> collection, final Comparator<? super E> comparator) {
        Objects.requireNonNull(collection, "collection");
        nextPermutation = new ArrayList<>(collection);
        this.comparator = comparator;
        checkElements(nextPermutation);
    }

    /**
     * Indicates if there are more permutations available.
     *
     * @return true if there are more permutations, otherwise false
     */
    @Override
    public boolean hasNext() {
        return nextPermutation != null;
    }

    /**
     * Returns the next permutation of the input collection.
     * <p>
     * The returned list is a mutable copy taken before the iterator advances, never the
     * iterator's own state, and it belongs to the caller. It may therefore be modified in
     * any way and at any time without affecting the remaining permutations.
     * </p>
     * <p>
     * The constructor has already compared each element with its successor, so a comparison
     * fails here only if the ordering in use rejects some other pair of elements.
     * </p>
     *
     * @return A list of the permutator's elements representing a permutation
     * @throws NoSuchElementException if there are no more permutations
     * @throws ClassCastException if the ordering in use cannot compare two of the elements
     * @throws NullPointerException if the ordering in use rejects a {@code null} element
     *         that it accepted when the constructor checked the elements
     */
    @Override
    public List<E> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // the copy is what keeps the caller's list apart from this iterator's own list,
        // which is rearranged in place below
        final List<E> result = new ArrayList<>(nextPermutation);
        if (!advance(nextPermutation)) {
            nextPermutation = null;
        }
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
     * Rearranges the given permutation in place into the smallest arrangement greater
     * than it. The elements are compared before any of them is moved, so the permutation
     * is left as it was if a comparison fails, or if it is already the largest arrangement.
     *
     * @param permutation  The arrangement to advance
     * @return true if the permutation was advanced, false if it was already the largest
     *         arrangement in lexicographical order
     * @throws ClassCastException if no comparator was supplied and the elements are
     *         not mutually {@link Comparable}
     * @throws NullPointerException if the ordering in use rejects a {@code null} element
     *         that it accepted when the constructor checked the elements
     */
    private boolean advance(final List<E> permutation) {
        final int size = permutation.size();

        // find the pivot: the rightmost element that is smaller than its successor.
        // if there is none the given permutation is the last one in lexicographical order
        int i = size - 2;
        while (i >= 0 && compareElements(permutation.get(i), permutation.get(i + 1)) >= 0) {
            --i;
        }

        if (i < 0) {
            return false;
        }

        // find the rightmost element greater than the pivot; the tail is descending,
        // so this is the pivot's successor in the remaining elements. the search needs
        // no bound, as the element following the pivot is greater than it
        int j = size - 1;
        while (compareElements(permutation.get(i), permutation.get(j)) >= 0) {
            --j;
        }

        // swap the pivot with its successor, then reverse the descending tail
        // into ascending order to obtain the smallest larger permutation
        Collections.swap(permutation, i, j);
        Collections.reverse(permutation.subList(i + 1, size));
        return true;
    }

    /**
     * Checks that the ordering in use accepts the given elements, by comparing each one
     * with its successor and the last one with itself. Every element is thus the first
     * operand of one comparison, which is enough for the natural ordering to reject a
     * null or non-comparable element wherever it is.
     *
     * @param elements  The elements to check
     * @throws ClassCastException if no comparator was supplied and the elements are
     *         not mutually {@link Comparable}
     * @throws NullPointerException if a compared element is {@code null} and no comparator
     *         was supplied, or the supplied comparator does not permit {@code null} elements
     */
    private void checkElements(final List<E> elements) {
        final int last = elements.size() - 1;
        for (int i = 0; i < last; i++) {
            compareElements(elements.get(i), elements.get(i + 1));
        }

        if (last >= 0) {
            compareElements(elements.get(last), elements.get(last));
        }
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
     * @throws NullPointerException if a compared element is {@code null} and no comparator
     *         was supplied, or the supplied comparator does not permit {@code null} elements
     */
    @SuppressWarnings("unchecked")
    private int compareElements(final E e1, final E e2) {
        return comparator == null
                ? ((Comparable<? super E>) e1).compareTo(e2)
                : comparator.compare(e1, e2);
    }

}
