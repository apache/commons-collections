/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/IteratorUtils.java,v 1.4.2.1 2004/05/22 11:32:06 scolebourne Exp $
 * $Revision: 1.4.2.1 $
 * $Date: 2004/05/22 11:32:06 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
import org.apache.commons.collections.iterators.CollatingIterator;
import org.apache.commons.collections.iterators.EnumerationIterator;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.iterators.FilterListIterator;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.apache.commons.collections.iterators.SingletonIterator;
import org.apache.commons.collections.iterators.SingletonListIterator;
import org.apache.commons.collections.iterators.TransformIterator;
/**
 * Provides static utility methods and decorators for {@link Iterator} 
 * instances. The implementations are provided in the 
 * <code>org.apache.commons.collections.iterators</code> subpackage.
 *
 * @author <a href="mailto:scolebourne@joda.org">Stephen Colebourne</a>
 * @version $Id: IteratorUtils.java,v 1.4.2.1 2004/05/22 11:32:06 scolebourne Exp $
 * @since 2.1
 */
public class IteratorUtils {
    // validation is done in this class in certain cases because the
    // public classes allow invalid states

    /**
     * An iterator over no elements.
     * @deprecated Use <code>EmptyIterator.INSTANCE</code>
     */    
    public static final Iterator EMPTY_ITERATOR = new EmptyIterator();
    /**
     * A list iterator over no elements
     * @deprecated Use <code>EmptyListIterator.INSTANCE</code>
     */    
    public static final ListIterator EMPTY_LIST_ITERATOR = new EmptyListIterator();

    /**
     * Prevents instantiation.
     */
    private IteratorUtils() {
    }

    /**
     * Gets an empty iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over
     * nothing.
     *
     * @return  an iterator over nothing
     * @deprecated Use <code>EmptyIterator.INSTANCE</code>
     */
    public static Iterator emptyIterator() {
        return EMPTY_ITERATOR;
    }

    /**
     * Gets an empty list iterator.
     * <p>
     * This iterator is a valid list iterator object that will iterate 
     * over nothing.
     *
     * @return  a list iterator over nothing
     * @deprecated Use <code>EmptyListIterator.INSTANCE</code>
     */
    public static ListIterator emptyListIterator() {
        return EMPTY_LIST_ITERATOR;
    }

    /**
     * Gets a singleton iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over
     * the specified object.
     *
     * @param object  the single object over which to iterate
     * @return  a singleton iterator over the object
     * @deprecated Use <code>new SingletonIterator(object)</code>
     */
    public static Iterator singletonIterator(Object object) {
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

    /**
     * Gets an iterator over an array.
     *
     * @param array  the array over which to iterate
     * @return  an iterator over the array
     * @throws NullPointerException if array is null
     * @deprecated Use <code>new ArrayIterator(array)</code>
     */
    public static Iterator arrayIterator(Object[] array) {
        return new ArrayIterator(array);
    }

    /**
     * Gets an iterator over the end part of an array.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException if array bounds are invalid
     * @throws NullPointerException if array is null
     * @deprecated Use <code>new ArrayIterator(array,start)</code>
     */
    public static Iterator arrayIterator(Object[] array, int start) {
        return new ArrayIterator(array, start);
    }

    /**
     * Gets an iterator over part of an array.
     *
     * @param array  the array over which to iterate
     * @param start  the index to start iterating at
     * @param end  the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException if array bounds are invalid
     * @throws NullPointerException if array is null
     * @deprecated Use <code>new ArrayIterator(array,start,end)</code>
     */
    public static Iterator arrayIterator(Object[] array, int start, int end) {
        return new ArrayIterator(array, start, end);
    }

//    /**
//     * Gets a list iterator over an array.
//     *
//     * @param array  the array over which to iterate
//     * @return  a list iterator over the array
//     * @throws NullPointerException if array is null
//     */
//    public static ListIterator arrayListIterator(Object[] array) {
//        return new ArrayListIterator(array);
//    }
//
//    /**
//     * Gets a list iterator over the end part of an array.
//     *
//     * @param array  the array over which to iterate
//     * @param start  the index to start iterating at
//     * @return a list iterator over part of the array
//     * @throws IllegalArgumentException if array bounds are invalid
//     * @throws NullPointerException if array is null
//     */
//    public static ListIterator arrayListIterator(Object[] array, int start) {
//        return new ArrayListIterator(array, start);
//    }
//
//    /**
//     * Gets a list iterator over part of an array.
//     *
//     * @param array  the array over which to iterate
//     * @param start  the index to start iterating at
//     * @param end  the index to finish iterating at
//     * @return a list iterator over part of the array
//     * @throws IllegalArgumentException if array bounds are invalid
//     * @throws NullPointerException if array is null
//     */
//    public static ListIterator arrayListIterator(Object[] array, int start, int end) {
//        return new ArrayListIterator(array, start, end);
//    }

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
     * @param collection  the collection to remove elements form
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
            return new ArrayIterator(obj);
            
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
    
    /**
     * EmptyIterator class
     */
    static class EmptyIterator implements Iterator {
        
        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return false;
        }

        /**
         * @see java.util.Iterator#next()
         */
        public Object next() {
            throw new NoSuchElementException();
        }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("remove() not supported for empty Iterator");
        }

    }
    
    /**
     * EmptyListIterator class
     */
    static class EmptyListIterator extends EmptyIterator implements ListIterator {
        
        /**
         * @see java.util.ListIterator#hasPrevious()
         */
        public boolean hasPrevious() {
            return false;
        }

        /**
         * @see java.util.ListIterator#previous()
         */
        public Object previous() {
            throw new NoSuchElementException();
        }

        /**
         * @see java.util.ListIterator#nextIndex()
         */
        public int nextIndex() {
            return 0;
        }

        /**
         * @see java.util.ListIterator#previousIndex()
         */
        public int previousIndex() {
            return -1;
        }

        /**
         * @see java.util.ListIterator#add(Object)
         */
        public void add(Object o) {
            throw new UnsupportedOperationException("add() not supported for empty Iterator");
        }

        /**
         * @see java.util.ListIterator#set(Object)
         */
        public void set(Object o) {
            throw new UnsupportedOperationException("set() not supported for empty Iterator");
        }

    }
    
}
