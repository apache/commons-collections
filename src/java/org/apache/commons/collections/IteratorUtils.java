/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/IteratorUtils.java,v 1.20 2004/01/08 22:18:16 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections;

import java.lang.reflect.Array;
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
import java.util.NoSuchElementException;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.ArrayListIterator;
import org.apache.commons.collections.iterators.CollatingIterator;
import org.apache.commons.collections.iterators.EnumerationIterator;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.iterators.FilterListIterator;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.apache.commons.collections.iterators.LoopingIterator;
import org.apache.commons.collections.iterators.ObjectArrayIterator;
import org.apache.commons.collections.iterators.ObjectArrayListIterator;
import org.apache.commons.collections.iterators.SingletonIterator;
import org.apache.commons.collections.iterators.SingletonListIterator;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import org.apache.commons.collections.iterators.UnmodifiableListIterator;
import org.apache.commons.collections.iterators.UnmodifiableMapIterator;

/**
 * Provides static utility methods and decorators for {@link Iterator} 
 * instances. The implementations are provided in the iterators subpackage.
 *
 * @since Commons Collections 2.1
 * @version $Revision: 1.20 $ $Date: 2004/01/08 22:18:16 $
 * 
 * @author Stephen Colebourne
 * @author Phil Steitz
 */
public class IteratorUtils {
    // validation is done in this class in certain cases because the
    // public classes allow invalid states

    /**
     * An iterator over no elements
     */    
    public static final ResettableIterator EMPTY_ITERATOR = new EmptyIterator();
    /**
     * A list iterator over no elements
     */    
    public static final ResettableListIterator EMPTY_LIST_ITERATOR = new EmptyListIterator();
    /**
     * An ordered iterator over no elements
     */    
    public static final OrderedIterator EMPTY_ORDERED_ITERATOR = new EmptyOrderedIterator();
    /**
     * A map iterator over no elements
     */    
    public static final MapIterator EMPTY_MAP_ITERATOR = new EmptyMapIterator();
    /**
     * An ordered map iterator over no elements
     */    
    public static final OrderedMapIterator EMPTY_ORDERED_MAP_ITERATOR = new EmptyOrderedMapIterator();

    /**
     * IteratorUtils is not normally instantiated.
     */
    public IteratorUtils() {
    }

    // Empty
    //-----------------------------------------------------------------------
    /**
     * Gets an empty iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over
     * nothing.
     *
     * @return  an iterator over nothing
     */
    public static ResettableIterator emptyIterator() {
        return EMPTY_ITERATOR;
    }

    /**
     * Gets an empty list iterator.
     * <p>
     * This iterator is a valid list iterator object that will iterate 
     * over nothing.
     *
     * @return  a list iterator over nothing
     */
    public static ResettableListIterator emptyListIterator() {
        return EMPTY_LIST_ITERATOR;
    }

    /**
     * Gets an empty ordered iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate 
     * over nothing.
     *
     * @return  an ordered iterator over nothing
     */
    public static OrderedIterator emptyOrderedIterator() {
        return EMPTY_ORDERED_ITERATOR;
    }

    /**
     * Gets an empty map iterator.
     * <p>
     * This iterator is a valid map iterator object that will iterate 
     * over nothing.
     *
     * @return  a map iterator over nothing
     */
    public static MapIterator emptyMapIterator() {
        return EMPTY_MAP_ITERATOR;
    }

    /**
     * Gets an empty ordered map iterator.
     * <p>
     * This iterator is a valid map iterator object that will iterate 
     * over nothing.
     *
     * @return  a map iterator over nothing
     */
    public static OrderedMapIterator emptyOrderedMapIterator() {
        return EMPTY_ORDERED_MAP_ITERATOR;
    }

    // Singleton
    //-----------------------------------------------------------------------
    /**
     * Gets a singleton iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over
     * the specified object.
     *
     * @param object  the single object over which to iterate
     * @return  a singleton iterator over the object
     */
    public static ResettableIterator singletonIterator(Object object) {
        return new SingletonIterator(object);
    }

    /**
     * Gets a singleton list iterator.
     * <p>
     * This iterator is a valid list iterator object that will iterate over
     * the specified object.
     *
     * @param object  the single object over which to iterate
     * @return  a singleton list iterator over the object
     */
    public static ListIterator singletonListIterator(Object object) {
        return new SingletonListIterator(object);
    }

    // Arrays
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator over an object array.
     *
     * @param array  the array over which to iterate
     * @return  an iterator over the array
     * @throws NullPointerException if array is null
     */
    public static ResettableIterator arrayIterator(Object[] array) {
        return new ObjectArrayIterator(array);
    }

    /**
     * Gets an iterator over an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array  the array over which to iterate
     * @return  an iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException if array is null
     */
    public static ResettableIterator arrayIterator(Object array) {
        return new ArrayIterator(array);
    }

    /**
     * Gets an iterator over the end part of an object array.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero or greater
     *  than the length of the array
     * @throws NullPointerException if array is null
     */
    public static ResettableIterator arrayIterator(Object[] array, int start) {
        return new ObjectArrayIterator(array, start);
    }

    /**
     * Gets an iterator over the end part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero or greater
     *  than the length of the array
     * @throws NullPointerException if array is null
     */
    public static ResettableIterator arrayIterator(Object array, int start) {
        return new ArrayIterator(array, start);
    }

    /**
     * Gets an iterator over part of an object array.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException if end is before start
     * @throws NullPointerException if array is null
     */
    public static ResettableIterator arrayIterator(Object[] array, int start, int end) {
        return new ObjectArrayIterator(array, start, end);
    }

    /**
     * Gets an iterator over part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException if end is before start
     * @throws NullPointerException if array is null
     */
    public static ResettableIterator arrayIterator(Object array, int start, int end) {
        return new ArrayIterator(array, start, end);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a list iterator over an object array.
     *
     * @param array  the array over which to iterate
     * @return  a list iterator over the array
     * @throws NullPointerException if array is null
     */
    public static ResettableListIterator arrayListIterator(Object[] array) {
        return new ObjectArrayListIterator(array);
    }

    /**
     * Gets a list iterator over an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array  the array over which to iterate
     * @return  a list iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException if array is null
     */
    public static ResettableListIterator arrayListIterator(Object array) {
        return new ArrayListIterator(array);
    }

    /**
     * Gets a list iterator over the end part of an object array.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException if array is null
     */
    public static ResettableListIterator arrayListIterator(Object[] array, int start) {
        return new ObjectArrayListIterator(array, start);
    }

    /**
     * Gets a list iterator over the end part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException if array is null
     */
    public static ResettableListIterator arrayListIterator(Object array, int start) {
        return new ArrayListIterator(array, start);
    }

    /**
     * Gets a list iterator over part of an object array.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException if end is before start
     * @throws NullPointerException if array is null
     */
    public static ResettableListIterator arrayListIterator(Object[] array, int start, int end) {
        return new ObjectArrayListIterator(array, start, end);
    }
    
    /**
     * Gets a list iterator over part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException if end is before start
     * @throws NullPointerException if array is null
     */
    public static ResettableListIterator arrayListIterator(Object array, int start, int end) {
        return new ArrayListIterator(array, start, end);
    }
    
    // Unmodifiable
    //-----------------------------------------------------------------------
    /**
     * Gets an immutable version of an {@link Iterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove} method.
     *
     * @param iterator  the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static Iterator unmodifiableIterator(Iterator iterator) {
        return UnmodifiableIterator.decorate(iterator);
    }
    
    /**
     * Gets an immutable version of a {@link ListIterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove}, {@link ListIterator#add} and
     * {@link ListIterator#set} methods.
     *
     * @param listIterator  the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static ListIterator unmodifiableListIterator(ListIterator listIterator) {
        return UnmodifiableListIterator.decorate(listIterator);
    }

    /**
     * Gets an immutable version of a {@link MapIterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove}, {@link MapIterator#setValue(Object)} methods.
     *
     * @param mapIterator  the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static MapIterator unmodifiableMapIterator(MapIterator mapIterator) {
        return UnmodifiableMapIterator.decorate(mapIterator);
    }

    // Chained
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that iterates through two {@link Iterator}s 
     * one after another.
     *
     * @param iterator1  the first iterators to use, not null
     * @param iterator2  the first iterators to use, not null
     * @return a combination iterator over the iterators
     * @throws NullPointerException if either iterator is null
     */
    public static Iterator chainedIterator(Iterator iterator1, Iterator iterator2) {
        return new IteratorChain(iterator1, iterator2);
    }

    /**
     * Gets an iterator that iterates through an array of {@link Iterator}s 
     * one after another.
     *
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators array is null or contains a null
     */
    public static Iterator chainedIterator(Iterator[] iterators) {
        return new IteratorChain(iterators);
    }

    /**
     * Gets an iterator that iterates through a collections of {@link Iterator}s 
     * one after another.
     *
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators collection is null or contains a null
     * @throws ClassCastException if the iterators collection contains the wrong object type
     */
    public static Iterator chainedIterator(Collection iterators) {
        return new IteratorChain(iterators);
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
     * @param comparator  the comparator to use, may be null for natural order
     * @param iterator1  the first iterators to use, not null
     * @param iterator2  the first iterators to use, not null
     * @return a combination iterator over the iterators
     * @throws NullPointerException if either iterator is null
     */
    public static Iterator collatedIterator(Comparator comparator, Iterator iterator1, Iterator iterator2) {
        return new CollatingIterator(comparator, iterator1, iterator2);
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
     * @param comparator  the comparator to use, may be null for natural order
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators array is null or contains a null
     */
    public static Iterator collatedIterator(Comparator comparator, Iterator[] iterators) {
        return new CollatingIterator(comparator, iterators);
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
     * @param comparator  the comparator to use, may be null for natural order
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators collection is null or contains a null
     * @throws ClassCastException if the iterators collection contains the wrong object type
     */
    public static Iterator collatedIterator(Comparator comparator, Collection iterators) {
        return new CollatingIterator(comparator, iterators);
    }

    // Transformed
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that transforms the elements of another iterator.
     * <p>
     * The transformation occurs during the next() method and the underlying
     * iterator is unaffected by the transformation.
     *
     * @param iterator  the iterator to use, not null
     * @param transform  the transform to use, not null
     * @throws NullPointerException if either parameter is null
     */
    public static Iterator transformedIterator(Iterator iterator, Transformer transform) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (transform == null) {
            throw new NullPointerException("Transformer must not be null");
        }
        return new TransformIterator(iterator, transform);
    }
    
    // Filtered
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that filters another iterator.
     * <p>
     * The returned iterator will only return objects that match the specified
     * filtering predicate.
     *
     * @param iterator  the iterator to use, not null
     * @param predicate  the predicate to use as a filter, not null
     * @throws NullPointerException if either parameter is null
     */
    public static Iterator filteredIterator(Iterator iterator, Predicate predicate) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterIterator(iterator, predicate);
    }
    
    /**
     * Gets a list iterator that filters another list iterator.
     * <p>
     * The returned iterator will only return objects that match the specified
     * filtering predicate.
     *
     * @param listIterator  the list iterator to use, not null
     * @param predicate  the predicate to use as a filter, not null
     * @throws NullPointerException if either parameter is null
     */
    public static ListIterator filteredListIterator(ListIterator listIterator, Predicate predicate) {
        if (listIterator == null) {
            throw new NullPointerException("ListIterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterListIterator(listIterator, predicate);
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
     * @param coll  the collection to iterate over, not null
     * @throws NullPointerException if the collection is null
     */
    public static ResettableIterator loopingIterator(Collection coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new LoopingIterator(coll);
    }
    
    // Views
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that provides an iterator view of the given enumeration.
     *
     * @param enumeration  the enumeration to use
     */
    public static Iterator asIterator(Enumeration enumeration) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        return new EnumerationIterator(enumeration);
    }

    /**
     * Gets an iterator that provides an iterator view of the given enumeration 
     * that will remove elements from the specified collection.
     *
     * @param enumeration  the enumeration to use
     * @param removeCollection  the collection to remove elements from
     */
    public static Iterator asIterator(Enumeration enumeration, Collection removeCollection) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        if (removeCollection == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new EnumerationIterator(enumeration, removeCollection);
    }
    
    /**
     * Gets an enumeration that wraps an iterator.
     *
     * @param iterator  the iterator to use, not null
     * @throws NullPointerException if iterator is null
     */
    public static Enumeration asEnumeration(Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorEnumeration(iterator);
    }
    
    /**
     * Gets a list iterator based on a simple iterator.
     * <p>
     * As the wrapped Iterator is traversed, a LinkedList of its values is
     * cached, permitting all required operations of ListIterator.
     *
     * @param iterator  the iterator to use, not null
     * @throws NullPointerException if iterator parameter is null
     */
    public static ListIterator toListIterator(Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new ListIteratorWrapper(iterator);
    }
    
    /**
     * Gets an array based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, this is converted to an array.
     *
     * @param iterator  the iterator to use, not null
     * @throws NullPointerException if iterator parameter is null
     */
    public static Object[] toArray(Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        List list = toList(iterator, 100);
        return list.toArray();
    }
    
    /**
     * Gets an array based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, this is converted to an array.
     *
     * @param iterator  the iterator to use, not null
     * @param arrayClass  the class of array to create
     * @throws NullPointerException if iterator parameter is null
     * @throws NullPointerException if arrayClass is null
     * @throws ClassCastException if the arrayClass is invalid
     */
    public static Object[] toArray(Iterator iterator, Class arrayClass) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (arrayClass == null) {
            throw new NullPointerException("Array class must not be null");
        }
        List list = toList(iterator, 100);
        return list.toArray((Object[]) Array.newInstance(arrayClass, list.size()));
    }
    
    /**
     * Gets a list based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, the list is returned.
     *
     * @param iterator  the iterator to use, not null
     * @throws NullPointerException if iterator parameter is null
     */
    public static List toList(Iterator iterator) {
        return toList(iterator, 10);
    }
    
    /**
     * Gets a list based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, the list is returned.
     *
     * @param iterator  the iterator to use, not null
     * @param estimatedSize  the initial size of the ArrayList
     * @throws NullPointerException if iterator parameter is null
     * @throws IllegalArgumentException if the size is less than 1
     */
    public static List toList(Iterator iterator, int estimatedSize) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (estimatedSize < 1) {
            throw new IllegalArgumentException("Estimated size must be greater than 0");
        }
        List list = new ArrayList(estimatedSize);
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
    
    /** 
     * Gets a suitable Iterator for the given object.
     * <p>
     * This method can handles objects as follows
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
     * </ul>
     * 
     * @param obj  the object to convert to an iterator
     * @return a suitable iterator, never null
     */
    public static Iterator getIterator(Object obj) {
        if (obj == null) {
            return emptyIterator();
            
        } else if (obj instanceof Iterator) {
            return (Iterator) obj;
            
        } else if (obj instanceof Collection) {
            return ((Collection) obj).iterator();
            
        } else if (obj instanceof Object[]) {
            return new ObjectArrayIterator((Object[]) obj);
            
        } else if (obj instanceof Enumeration) {
            return new EnumerationIterator((Enumeration) obj);
            
        } else if (obj instanceof Map) {
            return ((Map) obj).values().iterator();
            
        } else if (obj instanceof Dictionary) {
            return new EnumerationIterator(((Dictionary) obj).elements());
            
        } else if (obj != null && obj.getClass().isArray()) {
            return new ArrayIterator(obj);
            
        } else {
            try {
                Method method = obj.getClass().getMethod("iterator", null);
                if (Iterator.class.isAssignableFrom(method.getReturnType())) {
                    Iterator it = (Iterator) method.invoke(obj, null);
                    if (it != null) {
                        return it;
                    }
                }
            } catch (Exception ex) {
                // ignore
            }
            return singletonIterator(obj);
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * EmptyIterator class
     */
    static class EmptyIterator implements ResettableIterator {
        
        EmptyIterator() {
            super();
        }

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException("Iterator contains no elements");
        }

        public void remove() {
            throw new IllegalStateException("Iterator contains no elements");
        }
        
        public void reset() {
            // do nothing
        }
    }
    
    //-----------------------------------------------------------------------    
    /**
     * EmptyListIterator class
     */
    static class EmptyListIterator extends EmptyIterator implements ResettableListIterator {
        
        EmptyListIterator() {
            super();
        }

        public boolean hasPrevious() {
            return false;
        }

        public Object previous() {
            throw new NoSuchElementException("Iterator contains no elements");
        }

        public int nextIndex() {
            return 0;
        }

        public int previousIndex() {
            return -1;
        }

        public void add(Object o) {
            throw new UnsupportedOperationException("add() not supported for empty Iterator");
        }

        public void set(Object o) {
            throw new IllegalStateException("Iterator contains no elements");
        }
    }

    //-----------------------------------------------------------------------    
    /**
     * EmptyOrderedIterator class
     */
    static class EmptyOrderedIterator extends EmptyIterator implements OrderedIterator, ResettableIterator {
        
        EmptyOrderedIterator() {
            super();
        }
        
        public boolean hasPrevious() {
            return false;
        }
        
        public Object previous() {
            throw new NoSuchElementException("Iterator contains no elements");
        }
    }

    //-----------------------------------------------------------------------    
    /**
     * EmptyMapIterator class
     */
    static class EmptyMapIterator extends EmptyIterator implements MapIterator, ResettableIterator {
        
        EmptyMapIterator() {
            super();
        }
        
        public Object getKey() {
            throw new IllegalStateException("Iterator contains no elements");
        }

        public Object getValue() {
            throw new IllegalStateException("Iterator contains no elements");
        }

        public Object setValue(Object value) {
            throw new IllegalStateException("Iterator contains no elements");
        }
    }

    //-----------------------------------------------------------------------    
    /**
     * EmptyOrderedMapIterator class
     */
    static class EmptyOrderedMapIterator extends EmptyMapIterator implements OrderedMapIterator, ResettableIterator {
        
        EmptyOrderedMapIterator() {
            super();
        }
        
        public boolean hasPrevious() {
            return false;
        }
        
        public Object previous() {
            throw new NoSuchElementException("Iterator contains no elements");
        }
    }

}
