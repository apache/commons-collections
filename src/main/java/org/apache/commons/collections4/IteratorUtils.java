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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.collections4.iterators.ArrayListIterator;
import org.apache.commons.collections4.iterators.BoundedIterator;
import org.apache.commons.collections4.iterators.CollatingIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyListIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.collections4.iterators.FilterListIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.ListIteratorWrapper;
import org.apache.commons.collections4.iterators.LoopingIterator;
import org.apache.commons.collections4.iterators.LoopingListIterator;
import org.apache.commons.collections4.iterators.NodeListIterator;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.collections4.iterators.ObjectArrayListIterator;
import org.apache.commons.collections4.iterators.ObjectGraphIterator;
import org.apache.commons.collections4.iterators.PeekingIterator;
import org.apache.commons.collections4.iterators.PushbackIterator;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.collections4.iterators.SingletonListIterator;
import org.apache.commons.collections4.iterators.SkippingIterator;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.iterators.ZippingIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides static utility methods and decorators for {@link Iterator}
 * instances. The implementations are provided in the iterators subpackage.
 *
 * @since 2.1
 */
public class IteratorUtils {
    // validation is done in this class in certain cases because the
    // public classes allow invalid states

    /**
     * An iterator over no elements.
     */
    @SuppressWarnings("rawtypes")
    public static final ResettableIterator EMPTY_ITERATOR = EmptyIterator.RESETTABLE_INSTANCE;

    /**
     * A list iterator over no elements.
     */
    @SuppressWarnings("rawtypes")
    public static final ResettableListIterator EMPTY_LIST_ITERATOR = EmptyListIterator.RESETTABLE_INSTANCE;

    /**
     * An ordered iterator over no elements.
     */
    @SuppressWarnings("rawtypes")
    public static final OrderedIterator EMPTY_ORDERED_ITERATOR = EmptyOrderedIterator.INSTANCE;

    /**
     * A map iterator over no elements.
     */
    @SuppressWarnings("rawtypes")
    public static final MapIterator EMPTY_MAP_ITERATOR = EmptyMapIterator.INSTANCE;

    /**
     * An ordered map iterator over no elements.
     */
    @SuppressWarnings("rawtypes")
    public static final OrderedMapIterator EMPTY_ORDERED_MAP_ITERATOR = EmptyOrderedMapIterator.INSTANCE;
    /**
     * Default delimiter used to delimit elements while converting an Iterator
     * to its String representation.
     */
    private static final String DEFAULT_TOSTRING_DELIMITER = ", ";

    private static <E, C extends Collection<E>> C addAll(final Iterator<? extends E> iterator, final C list) {
        Objects.requireNonNull(iterator, "iterator");
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * Gets an iterator over an object array.
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @return an iterator over the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final E... array) {
        return new ObjectArrayIterator<>(array);
    }

    /**
     * Gets an iterator over the end part of an object array.
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero or greater
     *   than the length of the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final E[] array, final int start) {
        return new ObjectArrayIterator<>(array, start);
    }

    /**
     * Gets an iterator over part of an object array.
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException if end is before start
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final E[] array, final int start, final int end) {
        return new ObjectArrayIterator<>(array, start, end);
    }

    /**
     * Gets an iterator over an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     * </p>
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @return an iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final Object array) {
        return new ArrayIterator<>(array);
    }

    /**
     * Gets an iterator over the end part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     * </p>
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero or greater
     *   than the length of the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final Object array, final int start) {
        return new ArrayIterator<>(array, start);
    }

    /**
     * Gets an iterator over part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     * </p>
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array or end is before start
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final Object array, final int start, final int end) {
        return new ArrayIterator<>(array, start, end);
    }

    /**
     * Gets a list iterator over an object array.
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @return a list iterator over the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final E... array) {
        return new ObjectArrayListIterator<>(array);
    }

    /**
     * Gets a list iterator over the end part of an object array.
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final E[] array, final int start) {
        return new ObjectArrayListIterator<>(array, start);
    }

    /**
     * Gets a list iterator over part of an object array.
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException if end is before start
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final E[] array, final int start, final int end) {
        return new ObjectArrayListIterator<>(array, start, end);
    }

    /**
     * Gets a list iterator over an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     * </p>
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @return a list iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array) {
        return new ArrayListIterator<>(array);
    }

    /**
     * Gets a list iterator over the end part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     * </p>
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array, final int start) {
        return new ArrayListIterator<>(array, start);
    }

    /**
     * Gets a list iterator over part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     * </p>
     *
     * @param <E> the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array or end is before start
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array, final int start, final int end) {
        return new ArrayListIterator<>(array, start, end);
    }

    /**
     * Gets an enumeration that wraps an iterator.
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, may not be null
     * @return a new enumeration
     * @throws NullPointerException if iterator is null
     */
    public static <E> Enumeration<E> asEnumeration(final Iterator<? extends E> iterator) {
        return new IteratorEnumeration<>(Objects.requireNonNull(iterator, "iterator"));
    }

    /**
     * Gets an {@link Iterable} that wraps an iterator.  The returned {@link Iterable} can be
     * used for a single iteration.
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, may not be null
     * @return a new, single use {@link Iterable}
     * @throws NullPointerException if iterator is null
     */
    public static <E> Iterable<E> asIterable(final Iterator<? extends E> iterator) {
        Objects.requireNonNull(iterator, "iterator");
        return new IteratorIterable<>(iterator, false);
    }

    /**
     * Gets an iterator that provides an iterator view of the given enumeration.
     *
     * @param <E> the element type
     * @param enumeration  the enumeration to use, may not be null
     * @return a new iterator
     * @throws NullPointerException if enumeration is null
     */
    public static <E> Iterator<E> asIterator(final Enumeration<? extends E> enumeration) {
        return new EnumerationIterator<>(Objects.requireNonNull(enumeration, "enumeration"));
    }

    /**
     * Gets an iterator that provides an iterator view of the given enumeration
     * that will remove elements from the specified collection.
     *
     * @param <E> the element type
     * @param enumeration  the enumeration to use, may not be null
     * @param removeCollection  the collection to remove elements from, may not be null
     * @return a new iterator
     * @throws NullPointerException if enumeration or removeCollection is null
     */
    public static <E> Iterator<E> asIterator(final Enumeration<? extends E> enumeration,
                                             final Collection<? super E> removeCollection) {
        return new EnumerationIterator<>(Objects.requireNonNull(enumeration, "enumeration"),
                Objects.requireNonNull(removeCollection, "removeCollection"));
    }

    /**
     * Gets an iterable that wraps an iterator.  The returned iterable can be
     * used for multiple iterations.
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, may not be null
     * @return a new, multiple use iterable
     * @throws NullPointerException if iterator is null
     */
    public static <E> Iterable<E> asMultipleUseIterable(final Iterator<? extends E> iterator) {
        Objects.requireNonNull(iterator, "iterator");
        return new IteratorIterable<>(iterator, true);
    }

    // Bounded
    /**
     * Decorates the specified iterator to return at most the given number
     * of elements.
     *
     * @param <E> the element type
     * @param iterator  the iterator to decorate
     * @param max  the maximum number of elements returned by this iterator
     * @return a new bounded iterator
     * @throws NullPointerException if the iterator is null
     * @throws IllegalArgumentException if max is negative
     * @since 4.1
     */
    public static <E> BoundedIterator<E> boundedIterator(final Iterator<? extends E> iterator, final long max) {
        return boundedIterator(iterator, 0, max);
    }

    /**
     * Decorates the specified iterator to return at most the given number
     * of elements, skipping all elements until the iterator reaches the
     * position at {@code offset}.
     * <p>
     * The iterator is immediately advanced until it reaches the position at
     * {@code offset}, incurring O(n) time.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to decorate
     * @param offset  the index of the first element of the decorated iterator to return
     * @param max  the maximum number of elements returned by this iterator
     * @return a new bounded iterator
     * @throws NullPointerException if the iterator is null
     * @throws IllegalArgumentException if either offset or max is negative
     * @since 4.1
     */
    public static <E> BoundedIterator<E> boundedIterator(final Iterator<? extends E> iterator,
                                                         final long offset, final long max) {
        return new BoundedIterator<>(iterator, offset, max);
    }

    /**
     * Gets an iterator that iterates through a collections of {@link Iterator}s
     * one after another.
     *
     * @param <E> the element type
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators collection is null or contains a null
     * @throws ClassCastException if the iterators collection contains the wrong object type
     */
    public static <E> Iterator<E> chainedIterator(final Collection<? extends Iterator<? extends E>> iterators) {
        return new IteratorChain<>(iterators);
    }

    /**
     * Gets an iterator that iterates through an array of {@link Iterator}s
     * one after another.
     *
     * @param <E> the element type
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators array is null or contains a null
     */
    public static <E> Iterator<E> chainedIterator(final Iterator<? extends E>... iterators) {
        return new IteratorChain<>(iterators);
    }

    /**
     * Gets an iterator that iterates through two {@link Iterator}s
     * one after another.
     *
     * @param <E> the element type
     * @param iterator1  the first iterator to use, not null
     * @param iterator2  the second iterator to use, not null
     * @return a combination iterator over the iterators
     * @throws NullPointerException if either iterator is null
     */
    public static <E> Iterator<E> chainedIterator(final Iterator<? extends E> iterator1,
                                                  final Iterator<? extends E> iterator2) {
        // keep a version with two iterators to avoid the following warning in client code (Java 5 & 6)
        // "A generic array of E is created for a varargs parameter"
        return new IteratorChain<>(iterator1, iterator2);
    }

    /**
     * Gets an iterator that iterates through an {@link Iterator} of Iterators one after another.
     *
     * @param <E>       the element type
     * @param iterators the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators collection is null or contains a null
     * @throws ClassCastException   if the iterators collection contains the wrong object type
     * @since 4.5.0-M3
     */
    public static <E> Iterator<E> chainedIterator(final Iterator<? extends Iterator<? extends E>> iterators) {
        return new LazyIteratorChain<E>() {

            @Override
            protected Iterator<? extends E> nextIterator(final int count) {
                return iterators.hasNext() ? iterators.next() : null;
            }

        };
    }

    /**
     * Gets an iterator that provides an ordered iteration over the elements
     * contained in a collection of {@link Iterator}s.
     * <p>
     * Given two ordered {@link Iterator}s {@code A} and {@code B},
     * the {@link Iterator#next()} method will return the lesser of
     * {@code A.next()} and {@code B.next()} and so on.
     * </p>
     * <p>
     * The comparator is optional. If null is specified then natural order is used.
     * </p>
     *
     * @param <E> the element type
     * @param comparator  the comparator to use, may be null for natural order
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators collection is null or contains a null
     * @throws ClassCastException if the iterators collection contains the wrong object type
     */
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator,
                                                   final Collection<Iterator<? extends E>> iterators) {
        @SuppressWarnings("unchecked")
        final Comparator<E> comp =
            comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : (Comparator<E>) comparator;
        return new CollatingIterator<>(comp, iterators);
    }

    /**
     * Gets an iterator that provides an ordered iteration over the elements
     * contained in an array of {@link Iterator}s.
     * <p>
     * Given two ordered {@link Iterator}s {@code A} and {@code B},
     * the {@link Iterator#next()} method will return the lesser of
     * {@code A.next()} and {@code B.next()} and so on.
     * </p>
     * <p>
     * The comparator is optional. If null is specified then natural order is used.
     * </p>
     *
     * @param <E> the element type
     * @param comparator  the comparator to use, may be null for natural order
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators array is null or contains a null value
     */
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator,
                                                   final Iterator<? extends E>... iterators) {
        @SuppressWarnings("unchecked")
        final Comparator<E> comp =
            comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : (Comparator<E>) comparator;
        return new CollatingIterator<>(comp, iterators);
    }

    /**
     * Gets an iterator that provides an ordered iteration over the elements
     * contained in a collection of ordered {@link Iterator}s.
     * <p>
     * Given two ordered {@link Iterator}s {@code A} and {@code B},
     * the {@link Iterator#next()} method will return the lesser of
     * {@code A.next()} and {@code B.next()}.
     * </p>
     * <p>
     * The comparator is optional. If null is specified then natural order is used.
     * </p>
     *
     * @param <E> the element type
     * @param comparator  the comparator to use, may be null for natural order
     * @param iterator1  the first iterators to use, not null
     * @param iterator2  the first iterators to use, not null
     * @return a combination iterator over the iterators
     * @throws NullPointerException if either iterator is null
     */
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator,
                                                   final Iterator<? extends E> iterator1,
                                                   final Iterator<? extends E> iterator2) {
        @SuppressWarnings("unchecked")
        final Comparator<E> comp =
            comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : (Comparator<E>) comparator;
        return new CollatingIterator<>(comp, iterator1, iterator2);
    }

    /**
     * Checks if the object is contained in the given iterator.
     * <p>
     * A {@code null} or empty iterator returns false.
     * </p>
     *
     * @param <E> the type of object the {@link Iterator} contains
     * @param iterator  the iterator to check, may be null
     * @param object  the object to check
     * @return true if the object is contained in the iterator, false otherwise
     * @since 4.1
     */
    public static <E> boolean contains(final Iterator<E> iterator, final Object object) {
        return matchesAny(iterator, EqualPredicate.equalPredicate(object));
    }

    /**
     * Gets an empty iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over nothing.
     * </p>
     *
     * @param <E> the element type
     * @return an iterator over nothing
     */
    public static <E> ResettableIterator<E> emptyIterator() {
        return EmptyIterator.<E>resettableEmptyIterator();
    }

    /**
     * Gets an empty list iterator.
     * <p>
     * This iterator is a valid list iterator object that will iterate
     * over nothing.
     * </p>
     *
     * @param <E> the element type
     * @return a list iterator over nothing
     */
    public static <E> ResettableListIterator<E> emptyListIterator() {
        return EmptyListIterator.<E>resettableEmptyListIterator();
    }

    /**
     * Gets an empty map iterator.
     * <p>
     * This iterator is a valid map iterator object that will iterate
     * over nothing.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a map iterator over nothing
     */
    public static <K, V> MapIterator<K, V> emptyMapIterator() {
        return EmptyMapIterator.<K, V>emptyMapIterator();
    }

    /**
     * Gets an empty ordered iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate
     * over nothing.
     * </p>
     *
     * @param <E> the element type
     * @return an ordered iterator over nothing
     */
    public static <E> OrderedIterator<E> emptyOrderedIterator() {
        return EmptyOrderedIterator.<E>emptyOrderedIterator();
    }

    /**
     * Gets an empty ordered map iterator.
     * <p>
     * This iterator is a valid map iterator object that will iterate
     * over nothing.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a map iterator over nothing
     */
    public static <K, V> OrderedMapIterator<K, V> emptyOrderedMapIterator() {
        return EmptyOrderedMapIterator.<K, V>emptyOrderedMapIterator();
    }

    /**
     * Gets an iterator that filters another iterator.
     * <p>
     * The returned iterator will only return objects that match the specified
     * filtering predicate.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, not null
     * @param predicate  the predicate to use as a filter, not null
     * @return a new filtered iterator
     * @throws NullPointerException if either parameter is null
     */
    public static <E> Iterator<E> filteredIterator(final Iterator<? extends E> iterator,
                                                   final Predicate<? super E> predicate) {
        Objects.requireNonNull(iterator, "iterator");
        Objects.requireNonNull(predicate, "predicate");
        return new FilterIterator<>(iterator, predicate);
    }

    /**
     * Gets a list iterator that filters another list iterator.
     * <p>
     * The returned iterator will only return objects that match the specified
     * filtering predicate.
     * </p>
     *
     * @param <E> the element type
     * @param listIterator  the list iterator to use, not null
     * @param predicate  the predicate to use as a filter, not null
     * @return a new filtered iterator
     * @throws NullPointerException if either parameter is null
     */
    public static <E> ListIterator<E> filteredListIterator(final ListIterator<? extends E> listIterator,
            final Predicate<? super E> predicate) {

        Objects.requireNonNull(listIterator, "listIterator");
        Objects.requireNonNull(predicate, "predicate");
        return new FilterListIterator<>(listIterator, predicate);
    }

    /**
     * Finds the first element in the given iterator which matches the given predicate.
     * <p>
     * A {@code null} or empty iterator returns null.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to search, may be null
     * @param predicate  the predicate to use, must not be null
     * @return the first element of the iterator which matches the predicate or null if none could be found
     * @throws NullPointerException if predicate is null
     * @since 4.1
     */
    public static <E> E find(final Iterator<E> iterator, final Predicate<? super E> predicate) {
        return find(iterator, predicate, null);
    }

    /**
     * Finds the first element in the given iterator which matches the given predicate.
     * <p>
     * A {@code null} or empty iterator returns {@code defaultValue}.
     * </p>
     *
     * @param <E>          the element type.
     * @param iterator     the iterator to search, may be null.
     * @param predicate    the predicate to use, must not be null.
     * @param defaultValue the default value, may be null.
     * @return the first element of the iterator which matches the predicate or null if none could be found.
     * @throws NullPointerException if predicate is null.
     */
    private static <E> E find(final Iterator<E> iterator, final Predicate<? super E> predicate, final E defaultValue) {
        Objects.requireNonNull(predicate, "predicate");
        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                if (predicate.test(element)) {
                    return element;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Shortcut for {@code get(iterator, 0)}.
     * <p>
     * Returns the {@code first} value in {@link Iterator}, throwing
     * {@code IndexOutOfBoundsException} if there is no such element.
     * </p>
     * <p>
     * The Iterator is advanced to {@code 0} (or to the end, if
     * {@code 0} exceeds the number of entries) as a side effect of this method.
     * </p>
     * @param <E> the type of object in the {@link Iterator}
     * @param iterator the iterator to get a value from
     * @return the first object
     * @throws IndexOutOfBoundsException if the request is invalid
     * @since 4.2
     */
    public static <E> E first(final Iterator<E> iterator) {
        return get(iterator, 0);
    }

    /**
     * Applies the closure to each element of the provided iterator.
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, may be null
     * @param closure  the closure to apply to each element, may not be null
     * @throws NullPointerException if closure is null
     * @since 4.1
     */
    public static <E> void forEach(final Iterator<E> iterator, final Closure<? super E> closure) {
        Objects.requireNonNull(closure, "closure");
        if (iterator != null) {
            while (iterator.hasNext()) {
                closure.accept(iterator.next());
            }
        }
    }

    /**
     * Executes the given closure on each but the last element in the iterator.
     * <p>
     * If the input iterator is null no change is made.
     * </p>
     *
     * @param <E> the type of object the {@link Iterator} contains
     * @param iterator  the iterator to get the input from, may be null
     * @param closure  the closure to perform, may not be null
     * @return the last element in the iterator, or null if iterator is null or empty
     * @throws NullPointerException if closure is null
     * @since 4.1
     */
    public static <E> E forEachButLast(final Iterator<E> iterator, final Closure<? super E> closure) {
        Objects.requireNonNull(closure, "closure");

        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                if (!iterator.hasNext()) {
                    return element;
                }
                closure.accept(element);
            }
        }
        return null;
    }

    /**
     * Gets the {@code index}-th value in {@link Iterator}, throwing {@code IndexOutOfBoundsException} if there is no such element.
     * <p>
     * The Iterator is advanced to {@code index} (or to the end, if {@code index} exceeds the number of entries) as a side effect of this method.
     * </p>
     *
     * @param <E>      the type of object in the {@link Iterator}.
     * @param iterator the iterator to get a value from.
     * @param index    the index to get, 0-based.
     * @return the object at the specified index.
     * @throws IndexOutOfBoundsException if the index is invalid.
     * @since 4.1
     */
    public static <E> E get(final Iterator<E> iterator, final int index) {
        return get(iterator, index, ioob -> {
            throw new IndexOutOfBoundsException("Entry does not exist: " + ioob);
        });
    }

    /**
     * Gets the {@code index}-th value in {@link Iterator}, throwing {@code IndexOutOfBoundsException} if there is no such element.
     * <p>
     * The Iterator is advanced to {@code index} (or to the end, if {@code index} exceeds the number of entries) as a side effect of this method.
     * </p>
     *
     * @param <E>             the type of object in the {@link Iterator}
     * @param iterator        the iterator to get a value from
     * @param index           the index to get, 0-based.
     * @param defaultSupplier supplies a default value at an index.
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    static <E> E get(final Iterator<E> iterator, final int index, final IntFunction<E> defaultSupplier) {
        int i = index;
        CollectionUtils.checkIndexBounds(i);
        while (iterator.hasNext()) {
            i--;
            if (i == -1) {
                return iterator.next();
            }
            iterator.next();
        }
        return defaultSupplier.apply(i);
    }

    /**
     * Gets a suitable Iterator for the given object.
     * <p>
     * This method can handle objects as follows
     * </p>
     * <ul>
     * <li>null - empty iterator
     * <li>Iterator - returned directly
     * <li>Enumeration - wrapped
     * <li>Collection - iterator from collection returned
     * <li>Map - values iterator returned
     * <li>Dictionary - values (elements) enumeration returned as iterator
     * <li>array - iterator over array returned
     * <li>object with iterator() public method accessed by reflection
     * <li>object - singleton iterator
     * <li>NodeList - iterator over the list
     * <li>Node - iterator over the child nodes
     * </ul>
     *
     * @param obj  the object to convert to an iterator
     * @return a suitable iterator, never null
     */
    public static Iterator<?> getIterator(final Object obj) {
        if (obj == null) {
            return emptyIterator();
        }
        if (obj instanceof Iterator) {
            return (Iterator<?>) obj;
        }
        if (obj instanceof Iterable) {
            return ((Iterable<?>) obj).iterator();
        }
        if (obj instanceof Object[]) {
            return new ObjectArrayIterator<>((Object[]) obj);
        }
        if (obj instanceof Enumeration) {
            return new EnumerationIterator<>((Enumeration<?>) obj);
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).values().iterator();
        }
        if (obj instanceof NodeList) {
            return new NodeListIterator((NodeList) obj);
        }
        if (obj instanceof Node) {
            return new NodeListIterator((Node) obj);
        }
        if (obj instanceof Dictionary) {
            return new EnumerationIterator<>(((Dictionary<?, ?>) obj).elements());
        }
        if (obj.getClass().isArray()) {
            return new ArrayIterator<>(obj);
        }
        try {
            final Method method = obj.getClass().getMethod("iterator", (Class[]) null);
            if (Iterator.class.isAssignableFrom(method.getReturnType())) {
                final Iterator<?> it = (Iterator<?>) method.invoke(obj, (Object[]) null);
                if (it != null) {
                    return it;
                }
            }
        } catch (final RuntimeException | ReflectiveOperationException ignore) { // NOPMD
            // ignore
        }
        return singletonIterator(obj);
    }

    /**
     * Returns the index of the first element in the specified iterator that
     * matches the given predicate.
     * <p>
     * A {@code null} or empty iterator returns -1.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to search, may be null
     * @param predicate  the predicate to use, may not be null
     * @return the index of the first element which matches the predicate or -1 if none matches
     * @throws NullPointerException if predicate is null
     * @since 4.1
     */
    public static <E> int indexOf(final Iterator<E> iterator, final Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate, "predicate");

        if (iterator != null) {
            for (int index = 0; iterator.hasNext(); index++) {
                final E element = iterator.next();
                if (predicate.test(element)) {
                    return index;
                }
            }
        }
        return CollectionUtils.INDEX_NOT_FOUND;
    }

    /**
     * Checks if the given iterator is empty.
     * <p>
     * A {@code null} or empty iterator returns true.
     * </p>
     *
     * @param iterator  the {@link Iterator} to use, may be null
     * @return true if the iterator is exhausted or null, false otherwise
     * @since 4.1
     */
    public static boolean isEmpty(final Iterator<?> iterator) {
        return iterator == null || !iterator.hasNext();
    }

    /**
     * Gets an iterator that loops continuously over the supplied collection.
     * <p>
     * The iterator will only stop looping if the remove method is called
     * enough times to empty the collection, or if the collection is empty
     * to start with.
     * </p>
     *
     * @param <E> the element type
     * @param collection  the collection to iterate over, not null
     * @return a new looping iterator
     * @throws NullPointerException if the collection is null
     */
    public static <E> ResettableIterator<E> loopingIterator(final Collection<? extends E> collection) {
        return new LoopingIterator<>(Objects.requireNonNull(collection, "collection"));
    }

    /**
     * Gets an iterator that loops continuously over the supplied list.
     * <p>
     * The iterator will only stop looping if the remove method is called
     * enough times to empty the list, or if the list is empty to start with.
     * </p>
     *
     * @param <E> the element type
     * @param list  the list to iterate over, not null
     * @return a new looping iterator
     * @throws NullPointerException if the list is null
     * @since 3.2
     */
    public static <E> ResettableListIterator<E> loopingListIterator(final List<E> list) {
        return new LoopingListIterator<>(Objects.requireNonNull(list, "list"));
    }

    /**
     * Answers true if a predicate is true for every element of an iterator.
     * <p>
     * A {@code null} or empty iterator returns true.
     * </p>
     *
     * @param <E> the type of object the {@link Iterator} contains
     * @param iterator  the {@link Iterator} to use, may be null
     * @param predicate  the predicate to use, may not be null
     * @return true if every element of the collection matches the predicate or if the
     *   collection is empty, false otherwise
     * @throws NullPointerException if predicate is null
     * @since 4.1
     */
    public static <E> boolean matchesAll(final Iterator<E> iterator, final Predicate<? super E> predicate) {
        Objects.requireNonNull(predicate, "predicate");

        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                if (!predicate.test(element)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Answers true if a predicate is true for any element of the iterator.
     * <p>
     * A {@code null} or empty iterator returns false.
     * </p>
     *
     * @param <E> the type of object the {@link Iterator} contains
     * @param iterator  the {@link Iterator} to use, may be null
     * @param predicate  the predicate to use, may not be null
     * @return true if any element of the collection matches the predicate, false otherwise
     * @throws NullPointerException if predicate is null
     * @since 4.1
     */
    public static <E> boolean matchesAny(final Iterator<E> iterator, final Predicate<? super E> predicate) {
        return indexOf(iterator, predicate) != -1;
    }

    /**
     * Gets an {@link Iterator} that wraps the specified node's childNodes.
     * The returned {@link Iterator} can be used for a single iteration.
     * <p>
     * Convenience method, allows easy iteration over NodeLists:
     * </p>
     * <pre>
     *   Iterator&lt;Node&gt; iterator = IteratorUtils.nodeListIterator(node);
     *   for (Node childNode : IteratorUtils.asIterable(iterator)) {
     *     ...
     *   }
     * </pre>
     *
     * @param node  the node to use, may not be null
     * @return a new, single use {@link Iterator}
     * @throws NullPointerException if node is null
     * @since 4.0
     */
    public static NodeListIterator nodeListIterator(final Node node) {
        return new NodeListIterator(Objects.requireNonNull(node, "node"));
    }

    /**
     * Gets an {@link Iterator} that wraps the specified {@link NodeList}.
     * The returned {@link Iterator} can be used for a single iteration.
     *
     * @param nodeList  the node list to use, may not be null
     * @return a new, single use {@link Iterator}
     * @throws NullPointerException if nodeList is null
     * @since 4.0
     */
    public static NodeListIterator nodeListIterator(final NodeList nodeList) {
        return new NodeListIterator(Objects.requireNonNull(nodeList, "nodeList"));
    }

    /**
     * Gets an iterator that operates over an object graph.
     * <p>
     * This iterator can extract multiple objects from a complex tree-like object graph.
     * The iteration starts from a single root object.
     * It uses a {@code Transformer} to extract the iterators and elements.
     * Its main benefit is that no intermediate {@code List} is created.
     * </p>
     * <p>
     * For example, consider an object graph:
     * </p>
     * <pre>
     *                 |- Branch -- Leaf
     *                 |         \- Leaf
     *         |- Tree |         /- Leaf
     *         |       |- Branch -- Leaf
     *  Forest |                 \- Leaf
     *         |       |- Branch -- Leaf
     *         |       |         \- Leaf
     *         |- Tree |         /- Leaf
     *                 |- Branch -- Leaf
     *                 |- Branch -- Leaf</pre>
     * <p>
     * The following {@code Transformer}, used in this class, will extract all
     * the Leaf objects without creating a combined intermediate list:
     * </p>
     * <pre>
     * public Object transform(Object input) {
     *   if (input instanceof Forest) {
     *     return ((Forest) input).treeIterator();
     *   }
     *   if (input instanceof Tree) {
     *     return ((Tree) input).branchIterator();
     *   }
     *   if (input instanceof Branch) {
     *     return ((Branch) input).leafIterator();
     *   }
     *   if (input instanceof Leaf) {
     *     return input;
     *   }
     *   throw new ClassCastException();
     * }</pre>
     * <p>
     * Internally, iteration starts from the root object. When next is called,
     * the transformer is called to examine the object. The transformer will return
     * either an iterator or an object. If the object is an Iterator, the next element
     * from that iterator is obtained and the process repeats. If the element is an object
     * it is returned.
     * </p>
     * <p>
     * Under many circumstances, linking Iterators together in this manner is
     * more efficient (and convenient) than using nested for loops to extract a list.
     * </p>
     *
     * @param <E> the element type
     * @param root  the root object to start iterating from, null results in an empty iterator
     * @param transformer  the transformer to use, see above, null uses no effect transformer
     * @return a new object graph iterator
     * @since 3.1
     */
    public static <E> Iterator<E> objectGraphIterator(final E root,
            final Transformer<? super E, ? extends E> transformer) {
        return new ObjectGraphIterator<>(root, transformer);
    }

    /**
     * Gets an iterator that supports one-element lookahead.
     *
     * @param <E> the element type
     * @param iterator  the iterator to decorate, not null
     * @return a peeking iterator
     * @throws NullPointerException if the iterator is null
     * @since 4.0
     */
    public static <E> Iterator<E> peekingIterator(final Iterator<? extends E> iterator) {
        return PeekingIterator.peekingIterator(iterator);
    }

    /**
     * Gets an iterator that supports pushback of elements.
     *
     * @param <E> the element type
     * @param iterator  the iterator to decorate, not null
     * @return a pushback iterator
     * @throws NullPointerException if the iterator is null
     * @since 4.0
     */
    public static <E> Iterator<E> pushbackIterator(final Iterator<? extends E> iterator) {
        return PushbackIterator.pushbackIterator(iterator);
    }

    /**
     * Gets a singleton iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over
     * the specified object.
     * </p>
     *
     * @param <E> the element type
     * @param object  the single object over which to iterate
     * @return a singleton iterator over the object
     */
    public static <E> ResettableIterator<E> singletonIterator(final E object) {
        return new SingletonIterator<>(object);
    }

    /**
     * Gets a singleton list iterator.
     * <p>
     * This iterator is a valid list iterator object that will iterate over
     * the specified object.
     * </p>
     *
     * @param <E> the element type
     * @param object  the single object over which to iterate
     * @return a singleton list iterator over the object
     */
    public static <E> ListIterator<E> singletonListIterator(final E object) {
        return new SingletonListIterator<>(object);
    }

    /**
     * Returns the number of elements contained in the given iterator.
     * <p>
     * A {@code null} or empty iterator returns {@code 0}.
     * </p>
     *
     * @param iterator  the iterator to check, may be null
     * @return the number of elements contained in the iterator
     * @since 4.1
     */
    public static int size(final Iterator<?> iterator) {
        int size = 0;
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
        }
        return size;
    }

    /**
     * Decorates the specified iterator to skip the first N elements.
     *
     * @param <E> the element type
     * @param iterator  the iterator to decorate
     * @param offset  the first number of elements to skip
     * @return a new skipping iterator
     * @throws NullPointerException if the iterator is null
     * @throws IllegalArgumentException if offset is negative
     * @since 4.1
     */
    public static <E> SkippingIterator<E> skippingIterator(final Iterator<E> iterator, final long offset) {
        return new SkippingIterator<>(iterator, offset);
    }

    /**
     * Creates a stream on the given Iterable.
     *
     * @param <E> the type of elements in the Iterable.
     * @param iterable the Iterable to stream or null.
     * @return a new Stream or {@link Stream#empty()} if the Iterable is null.
     * @since 4.5.0-M3
     */
    public static <E> Stream<E> stream(final Iterable<E> iterable) {
        return iterable == null ? Stream.empty() : StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Creates a stream on the given Iterator.
     *
     * @param <E> the type of elements in the Iterator.
     * @param iterator the Iterator to stream or null.
     * @return a new Stream or {@link Stream#empty()} if the Iterator is null.
     * @since 4.5.0-M3
     */
    public static <E> Stream<E> stream(final Iterator<E> iterator) {
        return iterator == null ? Stream.empty() : StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    /**
     * Gets an array based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, this is converted to an array.
     * </p>
     *
     * @param iterator  the iterator to use, not null
     * @return an array of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     */
    public static Object[] toArray(final Iterator<?> iterator) {
        Objects.requireNonNull(iterator, "iterator");
        final List<?> list = toList(iterator, 100);
        return list.toArray();
    }

    /**
     * Gets an array based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, this is converted to an array.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, not null
     * @param arrayClass  the class of array to create
     * @return an array of the iterator contents
     * @throws NullPointerException if iterator parameter or arrayClass is null
     * @throws ArrayStoreException if the arrayClass is invalid
     */
    public static <E> E[] toArray(final Iterator<? extends E> iterator, final Class<E> arrayClass) {
        Objects.requireNonNull(iterator, "iterator");
        Objects.requireNonNull(arrayClass, "arrayClass");
        final List<E> list = toList(iterator, 100);
        @SuppressWarnings("unchecked")
        final E[] array = (E[]) Array.newInstance(arrayClass, list.size());
        return list.toArray(array);
    }

    /**
     * Gets a list based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, the list is returned.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, not null
     * @return a list of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     */
    public static <E> List<E> toList(final Iterator<? extends E> iterator) {
        return toList(iterator, 10);
    }

    /**
     * Gets a list based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, the list is returned.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, not null
     * @param estimatedSize  the initial size of the ArrayList
     * @return a list of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     * @throws IllegalArgumentException if the size is less than 1
     */
    public static <E> List<E> toList(final Iterator<? extends E> iterator, final int estimatedSize) {
        if (estimatedSize < 1) {
            throw new IllegalArgumentException("Estimated size must be greater than 0");
        }
        return addAll(iterator, new ArrayList<>(estimatedSize));
    }

    /**
     * Gets a list iterator based on a simple iterator.
     * <p>
     * As the wrapped Iterator is traversed, a LinkedList of its values is
     * cached, permitting all required operations of ListIterator.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, may not be null
     * @return a new iterator
     * @throws NullPointerException if iterator parameter is null
     */
    public static <E> ListIterator<E> toListIterator(final Iterator<? extends E> iterator) {
        Objects.requireNonNull(iterator, "iterator");
        return new ListIteratorWrapper<>(iterator);
    }

    /**
     * Gets a set based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, a HashSet of its values is
     * created. At the end, the set is returned.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, not null
     * @return a set of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     * @since 4.5.0-M4
     */
    public static <E> Set<E> toSet(final Iterator<? extends E> iterator) {
        return toSet(iterator, 10);
    }

    /**
     * Gets a set based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, a HashSet of its values is
     * created. At the end, the set is returned.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to use, not null
     * @param estimatedSize  the initial size of the HashSet
     * @return a list of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     * @throws IllegalArgumentException if the size is less than 1
     * @since 4.5.0-M4
     */
    public static <E> Set<E> toSet(final Iterator<? extends E> iterator, final int estimatedSize) {
        if (estimatedSize < 1) {
            throw new IllegalArgumentException("Estimated size must be greater than 0");
        }
        return addAll(iterator, new HashSet<>(estimatedSize));
    }

    /**
     * Returns a string representation of the elements of the specified iterator.
     * <p>
     * The string representation consists of a list of the iterator's elements,
     * enclosed in square brackets ({@code "[]"}). Adjacent elements are separated
     * by the characters {@code ", "} (a comma followed by a space). Elements are
     * converted to strings as by {@code String.valueOf(Object)}.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to convert to a string, may be null
     * @return a string representation of {@code iterator}
     * @since 4.1
     */
    public static <E> String toString(final Iterator<E> iterator) {
        return toString(iterator, TransformerUtils.stringValueTransformer(),
                        DEFAULT_TOSTRING_DELIMITER, CollectionUtils.DEFAULT_TOSTRING_PREFIX,
                CollectionUtils.DEFAULT_TOSTRING_SUFFIX);
    }

    /**
     * Returns a string representation of the elements of the specified iterator.
     * <p>
     * The string representation consists of a list of the iterable's elements,
     * enclosed in square brackets ({@code "[]"}). Adjacent elements are separated
     * by the characters {@code ", "} (a comma followed by a space). Elements are
     * converted to strings as by using the provided {@code transformer}.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to convert to a string, may be null
     * @param transformer  the transformer used to get a string representation of an element
     * @return a string representation of {@code iterator}
     * @throws NullPointerException if {@code transformer} is null
     * @since 4.1
     */
    public static <E> String toString(final Iterator<E> iterator,
                                      final Transformer<? super E, String> transformer) {
        return toString(iterator, transformer, DEFAULT_TOSTRING_DELIMITER,
                CollectionUtils.DEFAULT_TOSTRING_PREFIX, CollectionUtils.DEFAULT_TOSTRING_SUFFIX);
    }

    /**
     * Returns a string representation of the elements of the specified iterator.
     * <p>
     * The string representation consists of a list of the iterator's elements,
     * enclosed by the provided {@code prefix} and {@code suffix}. Adjacent elements
     * are separated by the provided {@code delimiter}. Elements are converted to
     * strings as by using the provided {@code transformer}.
     * </p>
     *
     * @param <E> the element type
     * @param iterator  the iterator to convert to a string, may be null
     * @param transformer  the transformer used to get a string representation of an element
     * @param delimiter  the string to delimit elements
     * @param prefix  the prefix, prepended to the string representation
     * @param suffix  the suffix, appended to the string representation
     * @return a string representation of {@code iterator}
     * @throws NullPointerException if either transformer, delimiter, prefix or suffix is null
     * @since 4.1
     */
    public static <E> String toString(final Iterator<E> iterator,
                                      final Transformer<? super E, String> transformer,
                                      final String delimiter,
                                      final String prefix,
                                      final String suffix) {
        Objects.requireNonNull(transformer, "transformer");
        Objects.requireNonNull(delimiter, "delimiter");
        Objects.requireNonNull(prefix, "prefix");
        Objects.requireNonNull(suffix, "suffix");
        final StringBuilder stringBuilder = new StringBuilder(prefix);
        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                stringBuilder.append(transformer.apply(element));
                stringBuilder.append(delimiter);
            }
            if (stringBuilder.length() > prefix.length()) {
                stringBuilder.setLength(stringBuilder.length() - delimiter.length());
            }
        }
        stringBuilder.append(suffix);
        return stringBuilder.toString();
    }

    /**
     * Gets an iterator that transforms the elements of another iterator.
     * <p>
     * The transformation occurs during the next() method and the underlying
     * iterator is unaffected by the transformation.
     * </p>
     *
     * @param <I> the input type
     * @param <O> the output type
     * @param iterator  the iterator to use, not null
     * @param transformer  the transform to use, not null
     * @return a new transforming iterator
     * @throws NullPointerException if either parameter is null
     */
    public static <I, O> Iterator<O> transformedIterator(final Iterator<? extends I> iterator,
            final Transformer<? super I, ? extends O> transformer) {

        Objects.requireNonNull(iterator, "iterator");
        Objects.requireNonNull(transformer, "transformer");
        return new TransformIterator<>(iterator, transformer);
    }

    /**
     * Gets an immutable version of an {@link Iterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove} method.
     *
     * @param <E> the element type
     * @param iterator  the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static <E> Iterator<E> unmodifiableIterator(final Iterator<E> iterator) {
        return UnmodifiableIterator.unmodifiableIterator(iterator);
    }

    /**
     * Gets an immutable version of a {@link ListIterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove}, {@link ListIterator#add} and
     * {@link ListIterator#set} methods.
     *
     * @param <E> the element type
     * @param listIterator  the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static <E> ListIterator<E> unmodifiableListIterator(final ListIterator<E> listIterator) {
        return UnmodifiableListIterator.unmodifiableListIterator(listIterator);
    }

    /**
     * Gets an immutable version of a {@link MapIterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove}, {@link MapIterator#setValue(Object)} methods.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param mapIterator  the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static <K, V> MapIterator<K, V> unmodifiableMapIterator(final MapIterator<K, V> mapIterator) {
        return UnmodifiableMapIterator.unmodifiableMapIterator(mapIterator);
    }

    /**
     * Returns an iterator that interleaves elements from the decorated iterators.
     *
     * @param <E> the element type
     * @param iterators  the array of iterators to interleave
     * @return an iterator, interleaving the decorated iterators
     * @throws NullPointerException if any iterator is null
     * @since 4.1
     */
    public static <E> ZippingIterator<E> zippingIterator(final Iterator<? extends E>... iterators) {
        return new ZippingIterator<>(iterators);
    }

    /**
     * Returns an iterator that interleaves elements from the decorated iterators.
     *
     * @param <E> the element type
     * @param a  the first iterator to interleave
     * @param b  the second iterator to interleave
     * @return an iterator, interleaving the decorated iterators
     * @throws NullPointerException if any iterator is null
     * @since 4.1
     */
    public static <E> ZippingIterator<E> zippingIterator(final Iterator<? extends E> a,
                                                         final Iterator<? extends E> b) {
        return new ZippingIterator<>(a, b);
    }

    /**
     * Returns an iterator that interleaves elements from the decorated iterators.
     *
     * @param <E> the element type
     * @param a  the first iterator to interleave
     * @param b  the second iterator to interleave
     * @param c  the third iterator to interleave
     * @return an iterator, interleaving the decorated iterators
     * @throws NullPointerException if any iterator is null
     * @since 4.1
     */
    public static <E> ZippingIterator<E> zippingIterator(final Iterator<? extends E> a,
                                                         final Iterator<? extends E> b,
                                                         final Iterator<? extends E> c) {
        return new ZippingIterator<>(a, b, c);
    }

    /**
     * Don't allow instances.
     */
    private IteratorUtils() {
        // empty
    }

}
