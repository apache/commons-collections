/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/**
 * Contains static utility methods and decorators for {@link List} 
 * instances.
 *
 * @since 1.0
 * @author  <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author  <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author Paul Jack
 * @author Stephen Colebourne
 */
public class ListUtils {

    /**
     *  Please don't ever instantiate a <Code>ListUtils</Code>.
     */
    public ListUtils() {
    }

    /**
     * Returns a new list containing all elements that are contained in
     * both given lists.
     *
     * @param list1  the first list
     * @param list2  the second list
     * @return  the intersection of those two lists
     * @throws NullPointerException if either list is null
     */
    public static List intersection(final List list1, final List list2) {
        final ArrayList result = new ArrayList();
        final Iterator iterator = list2.iterator();

        while (iterator.hasNext()) {
            final Object o = iterator.next();

            if (list1.contains(o)) {
                result.add(o);
            }
        }

        return result;
    }

    /**
     * Subtracts all elements in the second list from the first list,
     * placing the results in a new list.
     * This differs from {@link List#removeAll(Collection)} in that
     * cardinality is respected; if <Code>list1</Code> contains two
     * occurrences of <Code>null</Code> and <Code>list2</Code> only
     * contains one occurrence, then the returned list will still contain
     * one occurrence.
     *
     * @param list1  the list to subtract from
     * @param list2  the lsit to subtract
     * @return  a new list containing the results
     * @throws NullPointerException if either list is null
     */
    public static List subtract(final List list1, final List list2) {
        final ArrayList result = new ArrayList(list1);
        final Iterator iterator = list2.iterator();

        while (iterator.hasNext()) {
            result.remove(iterator.next());
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
    public static List sum(final List list1, final List list2) {
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
    public static List union(final List list1, final List list2) {
        final ArrayList result = new ArrayList(list1);
        result.addAll(list2);
        return result;
    }


    static class ListIteratorWrapper 
            implements ListIterator {

        final protected ListIterator iterator;

        public ListIteratorWrapper(ListIterator iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Object next() {
            return iterator.next();
        }

        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        public Object previous() {
            return iterator.previous();
        }

        public int nextIndex() {
            return iterator.nextIndex();
        }

        public int previousIndex() {
            return iterator.previousIndex();
        }

        public void remove() {
            iterator.remove();
        }

        public void set(Object o) {
            iterator.set(o);
        }

        public void add(Object o) {
            iterator.add(o);
        }

    }


    static class PredicatedList 
            extends CollectionUtils.PredicatedCollection
            implements List {

        public PredicatedList(List list, Predicate p) {
            super(list, p);
        }

        public boolean addAll(int i, Collection c) {
            for (Iterator iter = c.iterator(); iter.hasNext(); ) {
                validate(iter.next());
            }
            return getList().addAll(i, c);
        }

        public Object get(int i) {
            return getList().get(i);
        }

        public Object set(int i, Object o) {
            validate(o);
            return getList().set(i, o);
        }

        public void add(int i, Object o) {
            validate(o);
            getList().add(i, o);
        }

        public Object remove(int i) {
            return getList().remove(i);
        }

        public int indexOf(Object o) {
            return getList().indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return getList().lastIndexOf(o);
        }

        public ListIterator listIterator() {
            return listIterator(0);
        }

        public ListIterator listIterator(int i) {
            return new ListIteratorWrapper(getList().listIterator(i)) {
                public void add(Object o) {
                    validate(o);
                    iterator.add(o);
                }

                public void set(Object o) {
                    validate(o);
                    iterator.set(o);
                }
            };
        }

        public List subList(int i1, int i2) {
            List sub = getList().subList(i1, i2);
            return new PredicatedList(sub, predicate);
        }

        private List getList() {
            return (List)collection;
        }

    }


    static class FixedSizeList 
            extends CollectionUtils.UnmodifiableCollection
            implements List {

        public FixedSizeList(List list) {
            super(list);
        }

        public boolean addAll(int i, Collection c) {
            throw new UnsupportedOperationException();
        }

        public Object get(int i) {
            return getList().get(i);
        }

        public Object set(int i, Object o) {
            return getList().set(i, o);
        }

        public void add(int i, Object o) {
            throw new UnsupportedOperationException();
        }

        public Object remove(int i) {
            throw new UnsupportedOperationException();
        }

        public int indexOf(Object o) {
            return getList().indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return getList().lastIndexOf(o);
        }

        public ListIterator listIterator() {
            return listIterator(0);
        }

        public ListIterator listIterator(int i) {
            return new ListIteratorWrapper(getList().listIterator(i)) {
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public void add(Object o) {
                    throw new UnsupportedOperationException();
                }

                public void remove(Object o) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public List subList(int i1, int i2) {
            List sub = getList().subList(i1, i2);
            return new FixedSizeList(sub);
        }

        private List getList() {
            return (List)collection;
        }

    }


    static class LazyList 
            extends CollectionUtils.CollectionWrapper 
            implements List {

        protected final Factory factory;

        public LazyList(List list, Factory factory) {
            super(list);
            if (factory == null) {
                throw new IllegalArgumentException("Factory must not be null");
            }
            this.factory = factory;
        }

        
        /* Proxy method to the impl's get method. With the exception that if it's out
         * of bounds, then the collection will grow, leaving place-holders in its
         * wake, so that an item can be set at any given index. Later the
         * place-holders are removed to return to a pure collection.
         *
         * If there's a place-holder at the index, then it's replaced with a proper
         * object to be used.
         */
        public Object get(int index) {
            Object obj;
            if (index < (getList().size())) {
            /* within bounds, get the object */
                obj = getList().get(index);
                if (obj == null) {
                    /* item is a place holder, create new one, set and return */
                    obj = this.factory.create();
                    this.getList().set(index, obj);
                    return obj;
                } else {
                    /* good and ready to go */
                    return obj;
                }
            } else {
                /* we have to grow the list */
                for (int i = getList().size(); i < index; i++) {
                    getList().add(null);
                }
                /* create our last object, set and return */
                obj = this.factory.create();
                getList().add(obj);
                return obj;
            }
        }


        /* proxy the call to the provided list implementation. */
        public List subList(int fromIndex, int toIndex) {
            /* wrap the returned sublist so it can continue the functionality */
            return new LazyList(getList().subList(fromIndex, toIndex), factory);
        }

        public boolean addAll(int i, Collection c) {
            return getList().addAll(i, c);
        }

        public Object set(int i, Object o) {
            return getList().set(i, o);
        }

        public void add(int i, Object o) {
            getList().add(i, o);
        }

        public Object remove(int i) {
            return getList().remove(i);
        }

        public int indexOf(Object o) {
            return getList().indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return getList().lastIndexOf(o);
        }

        public ListIterator listIterator() {
            return getList().listIterator();
        }

        public ListIterator listIterator(int i) {
            return getList().listIterator(i);
        }

        private List getList() {
            return (List)collection;
        }

    }


    /**
     * Returns a predicated list backed by the given list.  Only objects
     * that pass the test in the given predicate can be added to the list.
     * It is important not to use the original list after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param list  the list to predicate, must not be null
     * @param predicate  the predicate for the list, must not be null
     * @return a predicated list backed by the given list
     * @throws IllegalArgumentException  if the List or Predicate is null
     */
    public static List predicatedList(List list, Predicate predicate) {
        return new PredicatedList(list, predicate);
    }

    /**
     * Returns a "lazy" list whose elements will be created on demand.<P>
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
    public static List lazyList(List list, Factory factory) {
        return new LazyList(list, factory);
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
    public static List fixedSizeList(List list) {
        return new FixedSizeList(list);
    }

}
