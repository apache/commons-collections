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
package org.apache.commons.collections.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.collections.OrderedIterator;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.list.UnmodifiableList;

/**
 * Decorates another <code>Set</code> to ensure that the order of addition
 * is retained and used by the iterator.
 * <p>
 * If an object is added to the set for a second time, it will remain in the
 * original position in the iteration.
 * The order can be observed from the set via the iterator or toArray methods.
 * <p>
 * The ListOrderedSet also has various useful direct methods. These include many
 * from <code>List</code>, such as <code>get(int)</code>, <code>remove(int)</code>
 * and <code>indexOf(int)</code>. An unmodifiable <code>List</code> view of
 * the set can be obtained via <code>asList()</code>.
 * <p>
 * This class cannot implement the <code>List</code> interface directly as
 * various interface methods (notably equals/hashCode) are incompatable with a set.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 * @author Henning P. Schmiedehausen
 */
public class ListOrderedSet<E> extends AbstractSerializableSetDecorator<E> implements Set<E> {

    /** Serialization version */
    private static final long serialVersionUID = -228664372470420141L;

    /** Internal list to hold the sequence of objects */
    protected final List<E> setOrder;

    /**
     * Factory method to create an ordered set specifying the list and set to use.
     * <p>
     * The list and set must both be empty.
     *
     * @param set  the set to decorate, must be empty and not null
     * @param list  the list to decorate, must be empty and not null
     * @throws IllegalArgumentException if set or list is null
     * @throws IllegalArgumentException if either the set or list is not empty
     * @since Commons Collections 3.1
     */
    public static <E> ListOrderedSet<E> decorate(Set<E> set, List<E> list) {
        if (set == null) {
            throw new IllegalArgumentException("Set must not be null");
        }
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        if (set.size() > 0 || list.size() > 0) {
            throw new IllegalArgumentException("Set and List must be empty");
        }
        return new ListOrderedSet<E>(set, list);
    }

    /**
     * Factory method to create an ordered set.
     * <p>
     * An <code>ArrayList</code> is used to retain order.
     *
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static <E> ListOrderedSet<E> decorate(Set<E> set) {
        return new ListOrderedSet<E>(set);
    }

    /**
     * Factory method to create an ordered set using the supplied list to retain order.
     * <p>
     * A <code>HashSet</code> is used for the set behaviour.
     * <p>
     * NOTE: If the list contains duplicates, the duplicates are removed,
     * altering the specified list.
     *
     * @param list  the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    public static <E> ListOrderedSet<E> decorate(List<E> list) {
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        Set<E> set = new HashSet<E>(list);
        list.retainAll(set);

        return new ListOrderedSet<E>(set, list);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs a new empty <code>ListOrderedSet</code> using
     * a <code>HashSet</code> and an <code>ArrayList</code> internally.
     *
     * @since Commons Collections 3.1
     */
    public ListOrderedSet() {
        super(new HashSet<E>());
        setOrder = new ArrayList<E>();
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected ListOrderedSet(Set<E> set) {
        super(set);
        setOrder = new ArrayList<E>(set);
    }

    /**
     * Constructor that wraps (not copies) the Set and specifies the list to use.
     * <p>
     * The set and list must both be correctly initialised to the same elements.
     *
     * @param set  the set to decorate, must not be null
     * @param list  the list to decorate, must not be null
     * @throws IllegalArgumentException if set or list is null
     */
    protected ListOrderedSet(Set<E> set, List<E> list) {
        super(set);
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        setOrder = list;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an unmodifiable view of the order of the Set.
     *
     * @return an unmodifiable list view
     */
    public List<E> asList() {
        return UnmodifiableList.decorate(setOrder);
    }

    //-----------------------------------------------------------------------
    public void clear() {
        collection.clear();
        setOrder.clear();
    }

    public OrderedIterator<E> iterator() {
        return new OrderedSetIterator<E>(setOrder.listIterator(), collection);
    }

    public boolean add(E object) {
        if (collection.add(object)) {
            setOrder.add(object);
            return true;
        }
        return false;
    }

    public boolean addAll(Collection<? extends E> coll) {
        boolean result = false;
        for (E e : coll) {
            result |= add(e);
        }
        return result;
    }

    public boolean remove(Object object) {
        boolean result = collection.remove(object);
        setOrder.remove(object);
        return result;
    }

    public boolean removeAll(Collection<?> coll) {
        boolean result = false;
        for (Iterator<?> it = coll.iterator(); it.hasNext();) {
            result |= remove(it.next());
        }
        return result;
    }

    public boolean retainAll(Collection<?> coll) {
        boolean result = collection.retainAll(coll);
        if (result == false) {
            return false;
        }
        if (collection.size() == 0) {
            setOrder.clear();
        } else {
            for (Iterator<E> it = setOrder.iterator(); it.hasNext();) {
                if (!collection.contains(it.next())) {
                    it.remove();
                }
            }
        }
        return result;
    }

    public Object[] toArray() {
        return setOrder.toArray();
    }

    public <T> T[] toArray(T a[]) {
        return setOrder.toArray(a);
    }

    //-----------------------------------------------------------------------
    public E get(int index) {
        return setOrder.get(index);
    }

    public int indexOf(Object object) {
        return setOrder.indexOf(object);
    }

    public void add(int index, E object) {
        if (!contains(object)) {
            collection.add(object);
            setOrder.add(index, object);
        }
    }

    public boolean addAll(int index, Collection<? extends E> coll) {
        boolean changed = false;
        for (E e : coll) {
            if (contains(e)) {
                continue;
            }
            collection.add(e);
            setOrder.add(index++, e);
            changed = true;
        }
        return changed;
    }

    public Object remove(int index) {
        Object obj = setOrder.remove(index);
        remove(obj);
        return obj;
    }

    /**
     * Uses the underlying List's toString so that order is achieved.
     * This means that the decorated Set's toString is not used, so
     * any custom toStrings will be ignored.
     */
    // Fortunately List.toString and Set.toString look the same
    public String toString() {
        return setOrder.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Internal iterator handle remove.
     */
    static class OrderedSetIterator<E> extends AbstractIteratorDecorator<E> implements OrderedIterator<E> {

        /** Object we iterate on */
        protected final Collection<E> set;

        /** Last object retrieved */
        protected E last;

        private OrderedSetIterator(ListIterator<E> iterator, Collection<E> set) {
            super(iterator);
            this.set = set;
        }

        public E next() {
            last = iterator.next();
            return last;
        }

        public void remove() {
            set.remove(last);
            iterator.remove();
            last = null;
        }

        public boolean hasPrevious() {
            return ((ListIterator<E>) iterator).hasPrevious();
        }

        public E previous() {
            last = ((ListIterator<E>) iterator).previous();
            return last;
        }
    }

}
