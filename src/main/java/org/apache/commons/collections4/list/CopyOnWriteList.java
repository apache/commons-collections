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
package org.apache.commons.collections4.list;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;

/** The list which copies its original, shared list on
 * modifications and then operates on copy.
 * <p>
 * Modifications made to this list copies original list, so
 * subsequent read and write operates on copied list. From the
 * other hand as long as there is no modification the {@code CopyOnWriteList}
 * and {@code originalList} shares same list. After modification new
 * backing list is created which can be modified without affecting state of
 * {@code originalList} or other {@code CopyOnWriteList} created from
 * {@code originalList}.
 * </p>
 * <p>
 * Following snippet shows how {@link CopyOnWriteList} can be used:
 * </p>
 * <pre>{@code
 * //Original list build on application startup
 * private static List<E> originalList = buildOriginalList();
 *
 * // Per instance list
 * private List<E> list = new CopyOnWriteList<>(originalList);
 *
 * // Setup instance
 * private void setup(String condition) {
 *   if ("rare".equals(condition)) { // Very rare case
 *      // list shares memory with original list
 *      list.add(new VerySpecialElement());
 *      // list has private backing list, which is copy of originalList
 *      // with new element added
 *   }
 * }
 * }</pre>
 * <i>Notes:</i>
 * <ul>
 *  <li>
 *      behavior of this class is different than CopyOnWriteArrayList from JDK,
 *  </li>
 *  <li>
 *      the changes in original list are not watched and will not trigger
 *      copying elements,
 *  </li>
 *  <li>
 *      this class doesn't clone elements,
 *  </li>
 *  <li>
 *      this class is partly thread safe - copying original list
 *      is synchronised as other instances wrapping same {@code originalList}
 *      can do same from other threads.
 *  <li>
 * </ul>
 *
 * @param <E> the type of elements
 *
 * @author Radek Smogura
 * @since 4.5
 * @serial
 */
public class CopyOnWriteList<E> extends AbstractListDecorator<E> {
    private static final long serialVersionUID = -8926547289582L;

    /** Holds default collection type. */
    public static final Class<?> DEFAULT_COLLECTION_TYPE = ArrayList.class;

    private Class<? extends List<E>> copiedCollectionType;

    /** Lock used to synchronise copying of original list. */
    private Object copyLock;

    /** Copies original list.
     *
     * @return instance the instance of the new collection of given type
     */
    protected List<E> copyOriginalList() {
        // Instantiate new collection of given type;
        List<E> instance;
        try {
            instance = copiedCollectionType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Can't instantiate class " + copiedCollectionType, e);
        }
        instance.addAll(decorated());
        return instance;
    }

    /** Copies original collection.
     *  @return {@code true} if copy happened
     *          {@code false} if does not (already copied)
     */
    protected boolean copyAndReplaceOriginalList() {
        if (copiedCollectionType == null) {
            return false;
        }

        // Do copy, if needed synchronise
        final List<E> copy;
        if (copyLock != null) {
            synchronized(copyLock) {
                copy = copyOriginalList();
            }
        } else {
            copy = copyOriginalList();
        }

        // Replace original list with copy
        setCollection(copy);

        // Prevent future copying and save GC
        copiedCollectionType = null;
        copyLock = null;

        return true;
    }

    /** Creates new copy on write list with default type.
     * This constructor is equivalent to
     * {@code CopyOnWriteList(originalList, originalList,
     * CopyOnWriteList.DEFAULT_COLLECTION_TYPE}
     *
     * @param originalList collection
     */
    public CopyOnWriteList(List<E> originalList) {
        this(originalList, originalList, (Class<? extends List<E>>) DEFAULT_COLLECTION_TYPE);
    }

    /** Creates new copy on write list with default type.
     *
     * @param originalList collection
     * @param copyLock the lock used to synchronise copying of original list
     *                 (if {@code null} no synchronisation takes place)
     * @param copiedCollectionType the type of new list which should be used
     *        as backing list
     */
    public CopyOnWriteList(List<E> originalList,
                           Object copyLock,
                           Class<? extends List<E>> copiedCollectionType) {
        super(originalList);
        if (copiedCollectionType == null) {
            throw new IllegalArgumentException("Parameter copiedCollectionType can't be null");
        }
        this.copyLock = copyLock;
        this.copiedCollectionType = copiedCollectionType;
    }

    @Override
    public boolean add(E object) {
        copyAndReplaceOriginalList();
        return super.add(object);
    }

    @Override
    public void add(int index, E object) {
        copyAndReplaceOriginalList();
        super.add(index, object);
    }

    @Override
    public boolean addAll(Collection<? extends E> coll) {
        copyAndReplaceOriginalList();
        return super.addAll(coll);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> coll) {
        copyAndReplaceOriginalList();
        return super.addAll(index, coll);
    }

    @Override
    public void clear() {
        copyAndReplaceOriginalList();
        super.clear();
    }

    @Override
    public E remove(int index) {
        copyAndReplaceOriginalList();
        return super.remove(index);
    }

    @Override
    public boolean remove(Object object) {
        copyAndReplaceOriginalList();
        return super.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        copyAndReplaceOriginalList();
        return super.removeAll(coll);
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        copyAndReplaceOriginalList();
        return super.retainAll(coll);
    }

    @Override
    public E set(int index, E object) {
        copyAndReplaceOriginalList();
        return super.set(index, object);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        final List<E> sub = decorated().subList(fromIndex, toIndex);
        return new CopyOnWriteList<>(sub);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        copyAndReplaceOriginalList();
        super.sort(c);
    }

    @Override
    public ListIterator<E> listIterator() {
        ListIterator<E> originalIterator = decorated().listIterator();

        return new CopyOnWriteIterator(originalIterator);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        ListIterator<E> originalIterator = decorated().listIterator(index);

        return new CopyOnWriteIterator(originalIterator);
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    /** Copy-on-write iterator which copies list on modifications and
     * uses copied list
     */
    private class CopyOnWriteIterator extends AbstractListIteratorDecorator<E> {
        /** Iterators are bit more complicated, we have to record which element
         * according to position is visited
         * In case of modifications we copy list and we replace original
         * iterator with new list's iterator and we skip stored number of
         * elements (actually... it's high level description)

         * We can do this as list is set of elements each with assigned position
         * and list contract requires that iterator will return elements
         * in "proper order".
         * This doesn't work for general collections as we can't assign
         * position to elements, elements can be duplicated, and iterators
         * may not return elements in same order
         */

        /** Holds backed list to detect concurrent changes. */
        private List<E> backedList;

        /** Checks if there was a change to list outside this iterator. */
        private void checkConcurrentChanges() {
            if (backedList != decorated()) {
                throw new ConcurrentModificationException("The list has been changed outside iterator");
            }
        }

        /** Copies original list and creates new iterator replacing
         * originally decorated iterator.
         *
         * If copy happened earlier this method is no-op.
         *
         * @return {@code true} if copy happened
         *         {@code false} if does not (already copied)
         */
        private boolean copyAndReplaceIterator() {
            if (copyAndReplaceOriginalList()) {
                int currentElement = this.previousIndex();
                // List is replaced, decorated() returns replacement
                List<E> copiedList = decorated();

                // TODO In case of exception we will be in slightly inconsistent state
                ListIterator<E> iteratorNew = copiedList.listIterator(currentElement);
                iteratorNew.next();
                this.backedList = copiedList;

                setListIterator(iteratorNew);
                return true;
            } else {
                return false;
            }
        }

        public CopyOnWriteIterator(ListIterator<E> iterator) {
            super(iterator);
            this.backedList = decorated();
        }

        @Override
        public void add(E obj) {
            checkConcurrentChanges();
            copyAndReplaceIterator();
            super.add(obj);
        }

        @Override
        public void set(E obj) {
            checkConcurrentChanges();
            copyAndReplaceIterator();
            super.set(obj);
        }

        @Override
        public void remove() {
            checkConcurrentChanges();
            copyAndReplaceIterator();
            super.remove();
        }

        @Override
        public E next() {
            checkConcurrentChanges();
            return super.next();
        }
    }

}