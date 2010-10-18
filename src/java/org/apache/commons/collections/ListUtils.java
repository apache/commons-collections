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
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.list.FixedSizeList;
import org.apache.commons.collections.list.LazyList;
import org.apache.commons.collections.list.PredicatedList;
import org.apache.commons.collections.list.SynchronizedList;
import org.apache.commons.collections.list.TransformedList;
import org.apache.commons.collections.list.UnmodifiableList;

/**
 * Provides utility methods and decorators for {@link List} instances.
 *
 * @since Commons Collections 1.0
 * @version $Revision$ $Date$
 *
 * @author Federico Barbieri
 * @author Peter Donald
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author Neil O'Toole
 * @author Matthew Hawthorne
 * @author Dave Meikle
 */
public class ListUtils {

    /**
     * An empty unmodifiable list.
     * This uses the {@link Collections Collections} implementation 
     * and is provided for completeness.
     */
    public static final List<Object> EMPTY_LIST = Collections.<Object>emptyList();

    /**
     * <code>ListUtils</code> should not normally be instantiated.
     */
    public ListUtils() {
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new list containing all elements that are contained in
     * both given lists.
     *
     * @param list1  the first list
     * @param list2  the second list
     * @return  the intersection of those two lists
     * @throws NullPointerException if either list is null
     */
    public static <E> List<E> intersection(final List<? extends E> list1, final List<? extends E> list2) {
        final List<E> result = new ArrayList<E>();

        List<? extends E> smaller = list1;
        List<? extends E> larger = list2;
        if (list1.size() > list2.size()) {
            smaller = list2;
            larger = list1;
        }
        
        HashSet<E> hashSet = new HashSet<E>(smaller);

        for (E e : larger) {
            if (hashSet.contains(e)) {
                result.add(e);
                hashSet.remove(e);
            }
        }
        return result;
    }

    /**
     * Subtracts all elements in the second list from the first list,
     * placing the results in a new list.
     * <p>
     * This differs from {@link List#removeAll(Collection)} in that
     * cardinality is respected; if <Code>list1</Code> contains two
     * occurrences of <Code>null</Code> and <Code>list2</Code> only
     * contains one occurrence, then the returned list will still contain
     * one occurrence.
     *
     * @param list1  the list to subtract from
     * @param list2  the list to subtract
     * @return  a new list containing the results
     * @throws NullPointerException if either list is null
     */
    public static <E> List<E> subtract(final List<E> list1, final List<? extends E> list2) {
        final ArrayList<E> result = new ArrayList<E>(list1);
        for (E e : list2) {
            result.remove(e);
        }
        return result;
    }

    /**
     * Returns the sum of the given lists.  This is their intersection
     * subtracted from their union.
     *
     * @param list1  the first list 
     * @param list2  the second list
     * @return  a new list containing the sum of those lists
     * @throws NullPointerException if either list is null
     */ 
    public static <E> List<E> sum(final List<? extends E> list1, final List<? extends E> list2) {
        return subtract(union(list1, list2), intersection(list1, list2));
    }

    /**
     * Returns a new list containing the second list appended to the
     * first list.  The {@link List#addAll(Collection)} operation is
     * used to append the two given lists into a new list.
     *
     * @param list1  the first list 
     * @param list2  the second list
     * @return  a new list containing the union of those lists
     * @throws NullPointerException if either list is null
     */
    public static <E> List<E> union(final List<? extends E> list1, final List<? extends E> list2) {
        final ArrayList<E> result = new ArrayList<E>(list1);
        result.addAll(list2);
        return result;
    }

    /**
     * Tests two lists for value-equality as per the equality contract in
     * {@link java.util.List#equals(java.lang.Object)}.
     * <p>
     * This method is useful for implementing <code>List</code> when you cannot
     * extend AbstractList. The method takes Collection instances to enable other
     * collection types to use the List implementation algorithm.
     * <p>
     * The relevant text (slightly paraphrased as this is a static method) is:
     * <blockquote>
     * Compares the two list objects for equality.  Returns
     * <tt>true</tt> if and only if both
     * lists have the same size, and all corresponding pairs of elements in
     * the two lists are <i>equal</i>.  (Two elements <tt>e1</tt> and
     * <tt>e2</tt> are <i>equal</i> if <tt>(e1==null ? e2==null :
     * e1.equals(e2))</tt>.)  In other words, two lists are defined to be
     * equal if they contain the same elements in the same order.  This
     * definition ensures that the equals method works properly across
     * different implementations of the <tt>List</tt> interface.
     * </blockquote>
     *
     * <b>Note:</b> The behaviour of this method is undefined if the lists are
     * modified during the equals comparison.
     * 
     * @see java.util.List
     * @param list1  the first list, may be null
     * @param list2  the second list, may be null
     * @return whether the lists are equal by value comparison
     */
    public static boolean isEqualList(final Collection<?> list1, final Collection<?> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }

        Iterator<?> it1 = list1.iterator();
        Iterator<?> it2 = list2.iterator();
        Object obj1 = null;
        Object obj2 = null;

        while (it1.hasNext() && it2.hasNext()) {
            obj1 = it1.next();
            obj2 = it2.next();

            if (!(obj1 == null ? obj2 == null : obj1.equals(obj2))) {
                return false;
            }
        }

        return !(it1.hasNext() || it2.hasNext());
    }
    
    /**
     * Generates a hash code using the algorithm specified in 
     * {@link java.util.List#hashCode()}.
     * <p>
     * This method is useful for implementing <code>List</code> when you cannot
     * extend AbstractList. The method takes Collection instances to enable other
     * collection types to use the List implementation algorithm.
     * 
     * @see java.util.List#hashCode()
     * @param list  the list to generate the hashCode for, may be null
     * @return the hash code
     */
    public static <E> int hashCodeForList(final Collection<E> list) {
        if (list == null) {
            return 0;
        }
        int hashCode = 1;
        Iterator<E> it = list.iterator();
        
        while (it.hasNext()) {
            E obj = it.next();
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
        }
        return hashCode;
    }   

    //-----------------------------------------------------------------------
    /**
     * Returns a List containing all the elements in <code>collection</code>
     * that are also in <code>retain</code>. The cardinality of an element <code>e</code>
     * in the returned list is the same as the cardinality of <code>e</code>
     * in <code>collection</code> unless <code>retain</code> does not contain <code>e</code>, in which
     * case the cardinality is zero. This method is useful if you do not wish to modify
     * the collection <code>c</code> and thus cannot call <code>collection.retainAll(retain);</code>.
     * 
     * @param collection  the collection whose contents are the target of the #retailAll operation
     * @param retain  the collection containing the elements to be retained in the returned collection
     * @return a <code>List</code> containing all the elements of <code>c</code>
     * that occur at least once in <code>retain</code>.
     * @throws NullPointerException if either parameter is null
     * @since Commons Collections 3.2
     */
    public static <E> List<E> retainAll(Collection<E> collection, Collection<?> retain) {
        List<E> list = new ArrayList<E>(Math.min(collection.size(), retain.size()));

        for (E obj : collection) {
            if (retain.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * Removes the elements in <code>remove</code> from <code>collection</code>. That is, this
     * method returns a list containing all the elements in <code>c</code>
     * that are not in <code>remove</code>. The cardinality of an element <code>e</code>
     * in the returned collection is the same as the cardinality of <code>e</code>
     * in <code>collection</code> unless <code>remove</code> contains <code>e</code>, in which
     * case the cardinality is zero. This method is useful if you do not wish to modify
     * <code>collection</code> and thus cannot call <code>collection.removeAll(remove);</code>.
     * 
     * @param collection  the collection from which items are removed (in the returned collection)
     * @param remove  the items to be removed from the returned <code>collection</code>
     * @return a <code>List</code> containing all the elements of <code>c</code> except
     * any elements that also occur in <code>remove</code>.
     * @throws NullPointerException if either parameter is null
     * @since Commons Collections 3.2
     */
    public static <E> List<E> removeAll(Collection<E> collection, Collection<?> remove) {
        List<E> list = new ArrayList<E>();
        for (E obj : collection) {
            if (!remove.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized list backed by the given list.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to 
     * avoid non-deterministic behavior:
     *  
     * <pre>
     * List list = ListUtils.synchronizedList(myList);
     * synchronized (list) {
     *     Iterator i = list.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     * 
     * This method uses the implementation in the decorators subpackage.
     * 
     * @param list  the list to synchronize, must not be null
     * @return a synchronized list backed by the given list
     * @throws IllegalArgumentException  if the list is null
     */
    public static <E> List<E> synchronizedList(List<E> list) {
        return SynchronizedList.decorate(list);
    }

    /**
     * Returns an unmodifiable list backed by the given list.
     * <p>
     * This method uses the implementation in the decorators subpackage.
     *
     * @param list  the list to make unmodifiable, must not be null
     * @return an unmodifiable list backed by the given list
     * @throws IllegalArgumentException  if the list is null
     */
    public static <E> List<E> unmodifiableList(List<E> list) {
        return UnmodifiableList.decorate(list);
    }

    /**
     * Returns a predicated (validating) list backed by the given list.
     * <p>
     * Only objects that pass the test in the given predicate can be added to the list.
     * Trying to add an invalid object results in an IllegalArgumentException.
     * It is important not to use the original list after invoking this method,
     * as it is a backdoor for adding invalid objects.
     *
     * @param list  the list to predicate, must not be null
     * @param predicate  the predicate for the list, must not be null
     * @return a predicated list backed by the given list
     * @throws IllegalArgumentException  if the List or Predicate is null
     */
    public static <E> List<E> predicatedList(List<E> list, Predicate<E> predicate) {
        return PredicatedList.decorate(list, predicate);
    }

    /**
     * Returns a transformed list backed by the given list.
     * <p>
     * This method returns a new list (decorating the specified list) that
     * will transform any new entries added to it.
     * Existing entries in the specified list will not be transformed.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * List. It is important not to use the original list after invoking this 
     * method, as it is a backdoor for adding untransformed objects.
     * <p>
     * Existing entries in the specified list will not be transformed.
     * If you want that behaviour, see {@link TransformedList#decorateTransform}.
     *
     * @param list  the list to predicate, must not be null
     * @param transformer  the transformer for the list, must not be null
     * @return a transformed list backed by the given list
     * @throws IllegalArgumentException  if the List or Transformer is null
     */
    public static <E> List<E> transformedList(List<E> list, Transformer<? super E, ? extends E> transformer) {
        return TransformedList.decorate(list, transformer);
    }
    
    /**
     * Returns a "lazy" list whose elements will be created on demand.
     * <p>
     * When the index passed to the returned list's {@link List#get(int) get}
     * method is greater than the list's size, then the factory will be used
     * to create a new object and that object will be inserted at that index.
     * <p>
     * For instance:
     *
     * <pre>
     * Factory factory = new Factory() {
     *     public Object create() {
     *         return new Date();
     *     }
     * }
     * List lazy = ListUtils.lazyList(new ArrayList(), factory);
     * Object obj = lazy.get(3);
     * </pre>
     *
     * After the above code is executed, <code>obj</code> will contain
     * a new <code>Date</code> instance.  Furthermore, that <code>Date</code>
     * instance is the fourth element in the list.  The first, second, 
     * and third element are all set to <code>null</code>.
     *
     * @param list  the list to make lazy, must not be null
     * @param factory  the factory for creating new objects, must not be null
     * @return a lazy list backed by the given list
     * @throws IllegalArgumentException  if the List or Factory is null
     */
    public static <E> List<E> lazyList(List<E> list, Factory<? extends E> factory) {
        return LazyList.decorate(list, factory);
    }

    /**
     * Returns a fixed-sized list backed by the given list.
     * Elements may not be added or removed from the returned list, but 
     * existing elements can be changed (for instance, via the 
     * {@link List#set(int,Object)} method).
     *
     * @param list  the list whose size to fix, must not be null
     * @return a fixed-size list backed by that list
     * @throws IllegalArgumentException  if the List is null
     */
    public static <E> List<E> fixedSizeList(List<E> list) {
        return FixedSizeList.decorate(list);
    }

    /**
     * Finds the first index in the given List which matches the given predicate.
     * <p>
     * If the input List or predicate is null, or no element of the List
     * matches the predicate, -1 is returned.
     *
     * @param list the List to search, may be null
     * @param predicate  the predicate to use, may be null
     * @return the first index of an Object in the List which matches the predicate or -1 if none could be found
     */
    public static <E> int indexOf(List<E> list, Predicate<E> predicate) {
        if (list != null && predicate != null) {
            for (int i = 0; i < list.size(); i++) {
                E item = list.get(i);
                if (predicate.evaluate(item)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
}
