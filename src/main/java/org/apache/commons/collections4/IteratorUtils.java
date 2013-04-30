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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.collections4.iterators.ArrayListIterator;
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
import org.apache.commons.collections4.iterators.ListIteratorWrapper;
import org.apache.commons.collections4.iterators.LoopingIterator;
import org.apache.commons.collections4.iterators.LoopingListIterator;
import org.apache.commons.collections4.iterators.NodeListIterator;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.collections4.iterators.ObjectArrayListIterator;
import org.apache.commons.collections4.iterators.ObjectGraphIterator;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.collections4.iterators.SingletonListIterator;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides static utility methods and decorators for {@link Iterator}
 * instances. The implementations are provided in the iterators subpackage.
 * <p>
 * WARNING: Due to human error certain binary incompatibilities were introduced
 * between Commons Collections 2.1 and 3.0. The class remained source and test
 * compatible, so if you can recompile all your classes and dependencies
 * everything is OK. Those methods which are binary incompatible are marked as
 * such, together with alternate solutions that are binary compatible
 * against versions 2.1.1 and 3.1.
 *
 * @since 2.1
 * @version $Id$
 */
public class IteratorUtils {
    // validation is done in this class in certain cases because the
    // public classes allow invalid states

    /**
     * An iterator over no elements.
     * <p>
     * WARNING: This constant is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>EmptyIterator.INSTANCE</code> for compatibility with Commons Collections 2.1.1.
     */
    public static final ResettableIterator<Object> EMPTY_ITERATOR = EmptyIterator.RESETTABLE_INSTANCE;

    /**
     * A list iterator over no elements.
     * <p>
     * WARNING: This constant is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>EmptyListIterator.INSTANCE</code> for compatibility with Commons Collections 2.1.1.
     */
    public static final ResettableListIterator<Object> EMPTY_LIST_ITERATOR = EmptyListIterator.RESETTABLE_INSTANCE;

    /**
     * An ordered iterator over no elements.
     */
    public static final OrderedIterator<Object> EMPTY_ORDERED_ITERATOR = EmptyOrderedIterator.INSTANCE;

    /**
     * A map iterator over no elements.
     */
    public static final MapIterator<Object, Object> EMPTY_MAP_ITERATOR = EmptyMapIterator.INSTANCE;

    /**
     * An ordered map iterator over no elements.
     */
    public static final OrderedMapIterator<Object, Object> EMPTY_ORDERED_MAP_ITERATOR =
            EmptyOrderedMapIterator.INSTANCE;

    /**
     * IteratorUtils is not normally instantiated.
     */
    private IteratorUtils() {}

    // Empty
    //-----------------------------------------------------------------------
    /**
     * Gets an empty iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over
     * nothing.
     * <p>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>EmptyIterator.INSTANCE</code> for compatibility with Commons Collections 2.1.1.
     *
     * @param <E>  the element type
     * @return  an iterator over nothing
     */
    public static <E> ResettableIterator<E> emptyIterator() {
        return EmptyIterator.<E>resettableEmptyIterator();
    }

    /**
     * Gets an empty list iterator.
     * <p>
     * This iterator is a valid list iterator object that will iterate
     * over nothing.
     * <p>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>EmptyListIterator.INSTANCE</code> for compatibility with Commons Collections 2.1.1.
     *
     * @param <E>  the element type
     * @return  a list iterator over nothing
     */
    public static <E> ResettableListIterator<E> emptyListIterator() {
        return EmptyListIterator.<E>resettableEmptyListIterator();
    }

    /**
     * Gets an empty ordered iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate
     * over nothing.
     *
     * @param <E>  the element type
     * @return  an ordered iterator over nothing
     */
    public static <E> OrderedIterator<E> emptyOrderedIterator() {
        return EmptyOrderedIterator.<E>emptyOrderedIterator();
    }

    /**
     * Gets an empty map iterator.
     * <p>
     * This iterator is a valid map iterator object that will iterate
     * over nothing.
     *
     * @param <K>  the key type
     * @param <V>  the value type
     * @return  a map iterator over nothing
     */
    public static <K, V> MapIterator<K, V> emptyMapIterator() {
        return EmptyMapIterator.<K, V>emptyMapIterator();
    }

    /**
     * Gets an empty ordered map iterator.
     * <p>
     * This iterator is a valid map iterator object that will iterate
     * over nothing.
     *
     * @param <K>  the key type
     * @param <V>  the value type
     * @return  a map iterator over nothing
     */
    public static <K, V> OrderedMapIterator<K, V> emptyOrderedMapIterator() {
        return EmptyOrderedMapIterator.<K, V>emptyOrderedMapIterator();
    }

    // Singleton
    //-----------------------------------------------------------------------
    /**
     * Gets a singleton iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over
     * the specified object.
     * <p>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>new SingletonIterator(object)</code> for compatibility.
     *
     * @param <E>  the element type
     * @param object  the single object over which to iterate
     * @return  a singleton iterator over the object
     */
    public static <E> ResettableIterator<E> singletonIterator(final E object) {
        return new SingletonIterator<E>(object);
    }

    /**
     * Gets a singleton list iterator.
     * <p>
     * This iterator is a valid list iterator object that will iterate over
     * the specified object.
     *
     * @param <E>  the element type
     * @param object  the single object over which to iterate
     * @return  a singleton list iterator over the object
     */
    public static <E> ListIterator<E> singletonListIterator(final E object) {
        return new SingletonListIterator<E>(object);
    }

    // Arrays
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator over an object array.
     * <p>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>new ArrayIterator(array)</code> for compatibility.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @return  an iterator over the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final E[] array) {
        return new ObjectArrayIterator<E>(array);
    }

    /**
     * Gets an iterator over an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @return  an iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final Object array) {
        return new ArrayIterator<E>(array);
    }

    /**
     * Gets an iterator over the end part of an object array.
     * <p>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>new ArrayIterator(array,start)</code> for compatibility.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero or greater
     *   than the length of the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final E[] array, final int start) {
        return new ObjectArrayIterator<E>(array, start);
    }

    /**
     * Gets an iterator over the end part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero or greater
     *   than the length of the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final Object array, final int start) {
        return new ArrayIterator<E>(array, start);
    }

    /**
     * Gets an iterator over part of an object array.
     * <p>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>new ArrayIterator(array,start,end)</code> for compatibility.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException if end is before start
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final E[] array, final int start, final int end) {
        return new ObjectArrayIterator<E>(array, start, end);
    }

    /**
     * Gets an iterator over part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array or end is before start
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final Object array, final int start, final int end) {
        return new ArrayIterator<E>(array, start, end);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a list iterator over an object array.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @return  a list iterator over the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final E[] array) {
        return new ObjectArrayListIterator<E>(array);
    }

    /**
     * Gets a list iterator over an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @return  a list iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array) {
        return new ArrayListIterator<E>(array);
    }

    /**
     * Gets a list iterator over the end part of an object array.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final E[] array, final int start) {
        return new ObjectArrayListIterator<E>(array, start);
    }

    /**
     * Gets a list iterator over the end part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array, final int start) {
        return new ArrayListIterator<E>(array, start);
    }

    /**
     * Gets a list iterator over part of an object array.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException if end is before start
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final E[] array, final int start, final int end) {
        return new ObjectArrayListIterator<E>(array, start, end);
    }

    /**
     * Gets a list iterator over part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>  the element type
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array or end is before start
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array, final int start, final int end) {
        return new ArrayListIterator<E>(array, start, end);
    }

    // Unmodifiable
    //-----------------------------------------------------------------------
    /**
     * Gets an immutable version of an {@link Iterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove} method.
     *
     * @param <E>  the element type
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
     * @param <E>  the element type
     * @param listIterator  the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static <E> ListIterator<E> unmodifiableListIterator(final ListIterator<E> listIterator) {
        return UnmodifiableListIterator.umodifiableListIterator(listIterator);
    }

    /**
     * Gets an immutable version of a {@link MapIterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove}, {@link MapIterator#setValue(Object)} methods.
     *
     * @param <K>  the key type
     * @param <V>  the value type
     * @param mapIterator  the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static <K, V> MapIterator<K, V> unmodifiableMapIterator(final MapIterator<K, V> mapIterator) {
        return UnmodifiableMapIterator.unmodifiableMapIterator(mapIterator);
    }

    // Chained
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that iterates through two {@link Iterator}s
     * one after another.
     *
     * @param <E>  the element type
     * @param iterator1  the first iterator to use, not null
     * @param iterator2  the second iterator to use, not null
     * @return a combination iterator over the iterators
     * @throws NullPointerException if either iterator is null
     */
    public static <E> Iterator<E> chainedIterator(final Iterator<? extends E> iterator1,
                                                  final Iterator<? extends E> iterator2) {
        return new IteratorChain<E>(iterator1, iterator2);
    }

    /**
     * Gets an iterator that iterates through an array of {@link Iterator}s
     * one after another.
     *
     * @param <E>  the element type
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators array is null or contains a null
     */
    public static <E> Iterator<E> chainedIterator(final Iterator<? extends E>[] iterators) {
        return new IteratorChain<E>(iterators);
    }

    /**
     * Gets an iterator that iterates through a collections of {@link Iterator}s
     * one after another.
     *
     * @param <E>  the element type
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators collection is null or contains a null
     * @throws ClassCastException if the iterators collection contains the wrong object type
     */
    public static <E> Iterator<E> chainedIterator(final Collection<Iterator<? extends E>> iterators) {
        return new IteratorChain<E>(iterators);
    }

    // Collated
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that provides an ordered iteration over the elements
     * contained in a collection of ordered {@link Iterator}s.
     * <p>
     * Given two ordered {@link Iterator}s <code>A</code> and <code>B</code>,
     * the {@link Iterator#next()} method will return the lesser of
     * <code>A.next()</code> and <code>B.next()</code>.
     * <p>
     * The comparator is optional. If null is specified then natural order is used.
     *
     * @param <E>  the element type
     * @param comparator  the comparator to use, may be null for natural order
     * @param iterator1  the first iterators to use, not null
     * @param iterator2  the first iterators to use, not null
     * @return a combination iterator over the iterators
     * @throws NullPointerException if either iterator is null
     */
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator,
                                                   final Iterator<? extends E> iterator1,
                                                   final Iterator<? extends E> iterator2) {
        return new CollatingIterator<E>(comparator, iterator1, iterator2);
    }

    /**
     * Gets an iterator that provides an ordered iteration over the elements
     * contained in an array of {@link Iterator}s.
     * <p>
     * Given two ordered {@link Iterator}s <code>A</code> and <code>B</code>,
     * the {@link Iterator#next()} method will return the lesser of
     * <code>A.next()</code> and <code>B.next()</code> and so on.
     * <p>
     * The comparator is optional. If null is specified then natural order is used.
     *
     * @param <E>  the element type
     * @param comparator  the comparator to use, may be null for natural order
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators array is null or contains a null value
     */
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator,
                                                   final Iterator<? extends E>[] iterators) {
        return new CollatingIterator<E>(comparator, iterators);
    }

    /**
     * Gets an iterator that provides an ordered iteration over the elements
     * contained in a collection of {@link Iterator}s.
     * <p>
     * Given two ordered {@link Iterator}s <code>A</code> and <code>B</code>,
     * the {@link Iterator#next()} method will return the lesser of
     * <code>A.next()</code> and <code>B.next()</code> and so on.
     * <p>
     * The comparator is optional. If null is specified then natural order is used.
     *
     * @param <E>  the element type
     * @param comparator  the comparator to use, may be null for natural order
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators collection is null or contains a null
     * @throws ClassCastException if the iterators collection contains the wrong object type
     */
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator,
            final Collection<Iterator<? extends E>> iterators) {
        return new CollatingIterator<E>(comparator, iterators);
    }

    // Object Graph
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that operates over an object graph.
     * <p>
     * This iterator can extract multiple objects from a complex tree-like object graph.
     * The iteration starts from a single root object.
     * It uses a <code>Transformer</code> to extract the iterators and elements.
     * Its main benefit is that no intermediate <code>List</code> is created.
     * <p>
     * For example, consider an object graph:
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
     * The following <code>Transformer</code>, used in this class, will extract all
     * the Leaf objects without creating a combined intermediate list:
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
     * <p>
     * Under many circumstances, linking Iterators together in this manner is
     * more efficient (and convenient) than using nested for loops to extract a list.
     *
     * @param <E>  the element type
     * @param root  the root object to start iterating from, null results in an empty iterator
     * @param transformer  the transformer to use, see above, null uses no effect transformer
     * @return a new object graph iterator
     * @since 3.1
     */
    public static <E> Iterator<E> objectGraphIterator(final E root,
            final Transformer<? super E, ? extends E> transformer) {
        return new ObjectGraphIterator<E>(root, transformer);
    }

    // Transformed
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that transforms the elements of another iterator.
     * <p>
     * The transformation occurs during the next() method and the underlying
     * iterator is unaffected by the transformation.
     *
     * @param <I>  the input type
     * @param <O>  the output type
     * @param iterator  the iterator to use, not null
     * @param transform  the transform to use, not null
     * @return a new transforming iterator
     * @throws NullPointerException if either parameter is null
     */
    public static <I, O> Iterator<O> transformedIterator(final Iterator<? extends I> iterator,
            final Transformer<? super I, ? extends O> transform) {

        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (transform == null) {
            throw new NullPointerException("Transformer must not be null");
        }
        return new TransformIterator<I, O>(iterator, transform);
    }

    // Filtered
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that filters another iterator.
     * <p>
     * The returned iterator will only return objects that match the specified
     * filtering predicate.
     *
     * @param <E>  the element type
     * @param iterator  the iterator to use, not null
     * @param predicate  the predicate to use as a filter, not null
     * @return a new filtered iterator
     * @throws NullPointerException if either parameter is null
     */
    public static <E> Iterator<E> filteredIterator(final Iterator<? extends E> iterator,
                                                   final Predicate<? super E> predicate) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterIterator<E>(iterator, predicate);
    }

    /**
     * Gets a list iterator that filters another list iterator.
     * <p>
     * The returned iterator will only return objects that match the specified
     * filtering predicate.
     *
     * @param <E>  the element type
     * @param listIterator  the list iterator to use, not null
     * @param predicate  the predicate to use as a filter, not null
     * @return a new filtered iterator
     * @throws NullPointerException if either parameter is null
     */
    public static <E> ListIterator<E> filteredListIterator(final ListIterator<? extends E> listIterator,
            final Predicate<? super E> predicate) {

        if (listIterator == null) {
            throw new NullPointerException("ListIterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterListIterator<E>(listIterator, predicate);
    }

    // Looping
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that loops continuously over the supplied collection.
     * <p>
     * The iterator will only stop looping if the remove method is called
     * enough times to empty the collection, or if the collection is empty
     * to start with.
     *
     * @param <E>  the element type
     * @param coll  the collection to iterate over, not null
     * @return a new looping iterator
     * @throws NullPointerException if the collection is null
     */
    public static <E> ResettableIterator<E> loopingIterator(final Collection<? extends E> coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new LoopingIterator<E>(coll);
    }

    /**
     * Gets an iterator that loops continuously over the supplied list.
     * <p>
     * The iterator will only stop looping if the remove method is called
     * enough times to empty the list, or if the list is empty to start with.
     *
     * @param <E>  the element type
     * @param list  the list to iterate over, not null
     * @return a new looping iterator
     * @throws NullPointerException if the list is null
     * @since 3.2
     */
    public static <E> ResettableListIterator<E> loopingListIterator(final List<E> list) {
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        return new LoopingListIterator<E>(list);
    }

    // org.w3c.dom.NodeList iterators
    //-----------------------------------------------------------------------
    /**
     * Gets an {@link Iterator} that wraps the specified {@link NodeList}.
     * The returned {@link Iterator} can be used for a single iteration.
     *
     * @param nodeList the node list to use, not null
     * @return a new, single use {@link Iterator}
     * @throws NullPointerException if nodeList is null
     * @since 4.0
     */
    public static NodeListIterator nodeListIterator(final NodeList nodeList) {
        if (nodeList == null) {
            throw new NullPointerException("NodeList must not be null");
        }
        return new NodeListIterator(nodeList);
    }

    /**
     * Gets an {@link Iterator} that wraps the specified node's childNodes.
     * The returned {@link Iterator} can be used for a single iteration.
     * <p>
     * Convenience method, allows easy iteration over NodeLists:
     * <pre>
     *   Iterator&lt;Node&gt; iterator = IteratorUtils.nodeListIterator(node);
     *   for(Node childNode : IteratorUtils.asIterable(iterator)) {
     *     ...
     *   }
     * </pre>
     *
     * @param node the node to use, not null
     * @return a new, single use {@link Iterator}
     * @throws NullPointerException if node is null
     * @since 4.0
     */
    public static NodeListIterator nodeListIterator(final Node node) {
        if (node == null) {
            throw new NullPointerException("Node must not be null");
        }
        return new NodeListIterator(node);
    }

    // Views
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that provides an iterator view of the given enumeration.
     *
     * @param <E>  the element type
     * @param enumeration  the enumeration to use
     * @return a new iterator
     */
    public static <E> Iterator<E> asIterator(final Enumeration<? extends E> enumeration) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        return new EnumerationIterator<E>(enumeration);
    }

    /**
     * Gets an iterator that provides an iterator view of the given enumeration
     * that will remove elements from the specified collection.
     *
     * @param <E>  the element type
     * @param enumeration  the enumeration to use
     * @param removeCollection  the collection to remove elements from
     * @return a new iterator
     */
    public static <E> Iterator<E> asIterator(final Enumeration<? extends E> enumeration,
                                             final Collection<? super E> removeCollection) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        if (removeCollection == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new EnumerationIterator<E>(enumeration, removeCollection);
    }

    /**
     * Gets an enumeration that wraps an iterator.
     *
     * @param <E>  the element type
     * @param iterator  the iterator to use, not null
     * @return a new enumeration
     * @throws NullPointerException if iterator is null
     */
    public static <E> Enumeration<E> asEnumeration(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorEnumeration<E>(iterator);
    }

    /**
     * Gets an {@link Iterable} that wraps an iterator.  The returned {@link Iterable} can be
     * used for a single iteration.
     *
     * @param <E>  the element type
     * @param iterator  the iterator to use, not null
     * @return a new, single use {@link Iterable}
     * @throws NullPointerException if iterator is null
     */
    public static <E> Iterable<E> asIterable(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorIterable<E>(iterator, false);
    }

    /**
     * Gets an iterable that wraps an iterator.  The returned iterable can be
     * used for multiple iterations.
     *
     * @param <E>  the element type
     * @param iterator  the iterator to use, not null
     * @return a new, multiple use iterable
     * @throws NullPointerException if iterator is null
     */
    public static <E> Iterable<E> asMultipleUseIterable(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorIterable<E>(iterator, true);
    }

    /**
     * Gets a list iterator based on a simple iterator.
     * <p>
     * As the wrapped Iterator is traversed, a LinkedList of its values is
     * cached, permitting all required operations of ListIterator.
     *
     * @param <E>  the element type
     * @param iterator  the iterator to use, not null
     * @return a new iterator
     * @throws NullPointerException if iterator parameter is null
     */
    public static <E> ListIterator<E> toListIterator(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new ListIteratorWrapper<E>(iterator);
    }

    /**
     * Gets an array based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, this is converted to an array.
     *
     * @param iterator  the iterator to use, not null
     * @return an array of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     */
    public static Object[] toArray(final Iterator<?> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        final List<?> list = toList(iterator, 100);
        return list.toArray();
    }

    /**
     * Gets an array based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, this is converted to an array.
     *
     * @param <E>  the element type
     * @param iterator  the iterator to use, not null
     * @param arrayClass  the class of array to create
     * @return an array of the iterator contents
     * @throws NullPointerException if iterator parameter or arrayClass is null
     * @throws ClassCastException if the arrayClass is invalid
     */
    public static <E> E[] toArray(final Iterator<? extends E> iterator, final Class<E> arrayClass) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (arrayClass == null) {
            throw new NullPointerException("Array class must not be null");
        }
        final List<E> list = toList(iterator, 100);
        @SuppressWarnings("unchecked") // as per Javadoc, will throw CCE if class is wrong
        final E[] array = (E[]) Array.newInstance(arrayClass, list.size());
        return list.toArray(array);
    }

    /**
     * Gets a list based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, the list is returned.
     *
     * @param <E>  the element type
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
     *
     * @param <E>  the element type
     * @param iterator  the iterator to use, not null
     * @param estimatedSize  the initial size of the ArrayList
     * @return a list of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     * @throws IllegalArgumentException if the size is less than 1
     */
    public static <E> List<E> toList(final Iterator<? extends E> iterator, final int estimatedSize) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (estimatedSize < 1) {
            throw new IllegalArgumentException("Estimated size must be greater than 0");
        }
        final List<E> list = new ArrayList<E>(estimatedSize);
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * Gets a suitable Iterator for the given object.
     * <p>
     * This method can handle objects as follows
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
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).iterator();
        }
        if (obj instanceof Object[]) {
            return new ObjectArrayIterator<Object>((Object[]) obj);
        }
        if (obj instanceof Enumeration) {
            return new EnumerationIterator<Object>((Enumeration<?>) obj);
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
            return new EnumerationIterator<Object>(((Dictionary<?, ?>) obj).elements());
        } else if (obj.getClass().isArray()) {
            return new ArrayIterator<Object>(obj);
        }
        try {
            final Method method = obj.getClass().getMethod("iterator", (Class[]) null);
            if (Iterator.class.isAssignableFrom(method.getReturnType())) {
                final Iterator<?> it = (Iterator<?>) method.invoke(obj, (Object[]) null);
                if (it != null) {
                    return it;
                }
            }
        } catch (final RuntimeException e) { // NOPMD
            // ignore
        } catch (final NoSuchMethodException e) { // NOPMD
            // ignore
        } catch (final IllegalAccessException e) { // NOPMD
            // ignore
        } catch (final InvocationTargetException e) { // NOPMD
            // ignore
        }
        return singletonIterator(obj);
    }

}
