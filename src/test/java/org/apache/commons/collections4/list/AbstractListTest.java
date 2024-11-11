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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.iterators.AbstractListIteratorTest;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link java.util.List}.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * </p>
 * <p>
 * If your {@link List} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link List} fails or override one of the
 * protected methods from AbstractCollectionTest.
 * </p>
 *
 * @param <E> the type of elements returned by this iterator
 */
public abstract class AbstractListTest<E> extends AbstractCollectionTest<E> {

    public static class BulkTestSubList<E> extends AbstractListTest<E> {

        private final AbstractListTest<E> outer;

        public BulkTestSubList(final AbstractListTest<E> outer) {
            this.outer = outer;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E[] getFullElements() {
            final List<E> l = Arrays.asList(outer.getFullElements());
            return (E[]) l.subList(3, l.size() - 3).toArray();
        }

        @Override
        public E[] getOtherElements() {
            return outer.getOtherElements();
        }

        @Override
        public boolean isAddSupported() {
            return outer.isAddSupported();
        }

        @Override
        public boolean isRemoveSupported() {
            return outer.isRemoveSupported();
        }

        @Override
        public boolean isSetSupported() {
            return outer.isSetSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public List<E> makeFullCollection() {
            final int size = getFullElements().length;
            return outer.makeFullCollection().subList(3, size - 3);
        }

        @Override
        public List<E> makeObject() {
            return outer.makeFullCollection().subList(4, 4);
        }

        @Override
        public void resetEmpty() {
            outer.resetFull();
            setCollection(outer.getCollection().subList(4, 4));
            setConfirmed(outer.getConfirmed().subList(4, 4));
        }

        @Override
        public void resetFull() {
            outer.resetFull();
            final int size = outer.getConfirmed().size();
            setCollection(outer.getCollection().subList(3, size - 3));
            setConfirmed(outer.getConfirmed().subList(3, size - 3));
        }

        @Override
        public void verify() {
            super.verify();
            outer.verify();
        }
    }

    public class TestListIterator extends AbstractListIteratorTest<E> {

        @Override
        public E addSetValue() {
            return getOtherElements()[0];
        }

        @Override
        public ListIterator<E> makeEmptyIterator() {
            resetEmpty();
            return AbstractListTest.this.getCollection().listIterator();
        }

        @Override
        public ListIterator<E> makeObject() {
            resetFull();
            return AbstractListTest.this.getCollection().listIterator();
        }

        @Override
        public boolean supportsAdd() {
            return isAddSupported();
        }

        @Override
        public boolean supportsRemove() {
            return isRemoveSupported();
        }

        @Override
        public boolean supportsSet() {
            return AbstractListTest.this.isSetSupported();
        }
    }

    /**
     *  Traverses to the beginning of the given iterator.
     *
     *  @param iter  the iterator to traverse
     *  @param i     the starting index
     */
    private void backwardTest(final ListIterator<E> iter, int i) {
        final List<E> list = getCollection();

        while (i > 0) {
            assertTrue(iter.hasPrevious(),
                "Iterator should have previous, i:" + i);
            assertEquals(i, iter.nextIndex(),
                "Iterator.nextIndex should work, i:" + i);
            assertEquals(i - 1, iter.previousIndex(),
                "Iterator.previousIndex should work, i:" + i);
            final E o = iter.previous();
            assertEquals(list.get(i - 1), o,
                "Iterator returned correct element");
            i--;
        }

        assertFalse(iter.hasPrevious(), "Iterator shouldn't have previous");
        final int nextIndex = iter.nextIndex();
        assertEquals(0, nextIndex, "nextIndex should be 0");
        final int prevIndex = iter.previousIndex();
        assertEquals(-1, prevIndex, "previousIndex should be -1");

        assertThrows(NoSuchElementException.class, () -> iter.previous(),
                "Exhausted iterator should raise NoSuchElement");
    }

    public BulkTest bulkTestListIterator() {
        return new TestListIterator();
    }

    /**
     *  Returns a {@link BulkTest} for testing {@link List#subList(int,int)}.
     *  The returned bulk test will run through every {@code TestList}
     *  method, <em>including</em> another {@code bulkTestSubList}.
     *  Sublists are tested until the size of the sublist is less than 10.
     *  Each sublist is 6 elements smaller than its parent list.
     *  (By default this means that two rounds of sublists will be tested).
     *  The verify() method is overloaded to test that the original list is
     *  modified when the sublist is.
     */
    public BulkTest bulkTestSubList() {
        if (getFullElements().length - 6 < 10) {
            return null;
        }
        return new BulkTestSubList<>(this);
    }

    /**
     * Invokes all the methods on the given sublist to make sure they raise
     * a {@link java.util.ConcurrentModificationException ConcurrentModificationException}.
     */
    protected void failFastAll(final List<E> list) {
        final Method[] methods = List.class.getMethods();
        for (final Method method : methods) {
            failFastMethod(list, method);
        }
    }

    /**
     * Invokes the given method on the given sublist to make sure it raises
     * a {@link java.util.ConcurrentModificationException ConcurrentModificationException}.
     *
     * Unless the method happens to be the equals() method, in which case
     * the test is skipped. There seems to be a bug in
     * java.util.AbstractList.subList(int,int).equals(Object) -- it never
     * raises a ConcurrentModificationException.
     *
     * @param list the sublist to test
     * @param m the method to invoke
     */
    protected void failFastMethod(final List<E> list, final Method m) {
        if (m.getName().equals("equals")) {
            return;
        }

        final E element = getOtherElements()[0];
        final Collection<E> c = Collections.singleton(element);

        final Class<?>[] types = m.getParameterTypes();
        final Object[] params = new Object[types.length];
        for (int i = 0; i < params.length; i++) {
            if (types[i] == Integer.TYPE) {
                params[i] = Integer.valueOf(0);
            } else if (types[i] == Collection.class) {
                params[i] = c;
            } else if (types[i] == Object.class) {
                params[i] = element;
            } else if (types[i] == Object[].class) {
                params[i] = new Object[0];
            }
        }

        final InvocationTargetException thrown = assertThrows(InvocationTargetException.class, () -> m.invoke(list, params),
                m.getName() + " should raise ConcurrentModification");
        assertTrue(thrown.getTargetException() instanceof ConcurrentModificationException,
                m.getName() + " raised unexpected " + thrown.getTargetException());
    }

    /**
     *  Traverses to the end of the given iterator.
     *
     *  @param iter  the iterator to traverse
     *  @param i     the starting index
     */
    private void forwardTest(final ListIterator<E> iter, int i) {
        final List<E> list = getCollection();
        final int max = getFullElements().length;

        while (i < max) {
            assertTrue(iter.hasNext(), "Iterator should have next");
            assertEquals(i, iter.nextIndex(),
                "Iterator.nextIndex should work");
            assertEquals(i - 1, iter.previousIndex(),
                "Iterator.previousIndex should work");
            final Object o = iter.next();
            assertEquals(list.get(i), o, "Iterator returned correct element");
            i++;
        }

        assertFalse(iter.hasNext(), "Iterator shouldn't have next");
        assertEquals(max, iter.nextIndex(), "nextIndex should be size");
        assertEquals(max - 1, iter.previousIndex(), "previousIndex should be size - 1");

        assertThrows(NoSuchElementException.class, () -> iter.next(),
                "Exhausted iterator should raise NoSuchElement");
    }

    /**
     * Returns the {@link #collection} field cast to a {@link List}.
     *
     * @return the collection field as a List
     */
    @Override
    public List<E> getCollection() {
        return (List<E>) super.getCollection();
    }

    /**
     * Returns the {@link #confirmed} field cast to a {@link List}.
     *
     * @return the confirmed field as a List
     */
    @Override
    public List<E> getConfirmed() {
        return (List<E>) super.getConfirmed();
    }

    /**
     * List equals method is defined.
     */
    @Override
    public boolean isEqualsCheckable() {
        return true;
    }

    /**
     *  Returns true if the collections produced by
     *  {@link #makeObject()} and {@link #makeFullCollection()}
     *  support the <code>set operation.<p>
     *  Default implementation returns true.  Override if your collection
     *  class does not support set.
     */
    public boolean isSetSupported() {
        return true;
    }

    /**
     * Returns an empty {@link ArrayList}.
     */
    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<>();
    }

    /**
     * Returns a full {@link ArrayList}.
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        return new ArrayList<>(Arrays.asList(getFullElements()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> makeFullCollection() {
        // only works if list supports optional "addAll(Collection)"
        final List<E> list = makeObject();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    /**
     * Returns {@link #makeObject()}.
     *
     * @return an empty list to be used for testing
     */
    @Override
    public abstract List<E> makeObject();

    /**
     * Compare the current serialized form of the List
     * against the canonical version in SCM.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyListCompatibility() throws IOException, ClassNotFoundException {
        /*
         * Create canonical objects with this code
        List list = makeEmptyList();
        if (!(list instanceof Serializable)) return;

        writeExternalFormToDisk((Serializable) list, getCanonicalEmptyCollectionName(list));
        */

        // test to make sure the canonical form has been preserved
        final List<E> list = makeObject();
        if (list instanceof Serializable && !skipSerializedCanonicalTests()
                && isTestSerialization()) {
            final List<E> list2 = (List<E>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(list));
            assertEquals(0, list2.size(), "List is empty");
            assertEquals(list, list2);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyListSerialization() throws IOException, ClassNotFoundException {
        final List<E> list = makeObject();
        if (!(list instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] object = writeExternalFormToBytes((Serializable) list);
        final List<E> list2 = (List<E>) readExternalFormFromBytes(object);

        assertEquals(0, list.size(), "Both lists are empty");
        assertEquals(0, list2.size(), "Both lists are empty");
    }

    /**
     * Compare the current serialized form of the List
     * against the canonical version in SCM.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testFullListCompatibility() throws IOException, ClassNotFoundException {
        /*
         * Create canonical objects with this code
        List list = makeFullList();
        if (!(list instanceof Serializable)) return;

        writeExternalFormToDisk((Serializable) list, getCanonicalFullCollectionName(list));
        */

        // test to make sure the canonical form has been preserved
        final List<E> list = makeFullCollection();
        if (list instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final List<E> list2 = (List<E>) readExternalFormFromDisk(getCanonicalFullCollectionName(list));
            if (list2.size() == 4) {
                // old serialized tests
                return;
            }
            assertEquals(list.size(), list2.size(), "List is the right size");
            assertEquals(list, list2);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFullListSerialization() throws IOException, ClassNotFoundException {
        final List<E> list = makeFullCollection();
        final int size = getFullElements().length;
        if (!(list instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] object = writeExternalFormToBytes((Serializable) list);
        final List<E> list2 = (List<E>) readExternalFormFromBytes(object);

        assertEquals(size, list.size(), "Both lists are same size");
        assertEquals(size, list2.size(), "Both lists are same size");
    }

    /**
     *  Tests {@link List#add(int,Object)}.
     */
    @Test
    public void testListAddByIndex() {
        if (!isAddSupported()) {
            return;
        }

        final E element = getOtherElements()[0];
        final int max = getFullElements().length;

        for (int i = 0; i <= max; i++) {
            resetFull();
            getCollection().add(i, element);
            getConfirmed().add(i, element);
            verify();
        }
    }

    /**
     *  Tests bounds checking for {@link List#add(int, Object)} on an
     *  empty list.
     */
    @Test
    public void testListAddByIndexBoundsChecking() {
        if (!isAddSupported()) {
            return;
        }

        final E element = getOtherElements()[0];

        final List<E> finalList0 = makeObject();
        assertThrows(IndexOutOfBoundsException.class, () -> finalList0.add(Integer.MIN_VALUE, element),
                "List.add should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");

        final List<E> finalList1 = makeObject();
        assertThrows(IndexOutOfBoundsException.class, () -> finalList1.add(-1, element),
                "List.add should throw IndexOutOfBoundsException [-1]");

        final List<E> finalList2 = makeObject();
        assertThrows(IndexOutOfBoundsException.class, () -> finalList2.add(1, element),
                "List.add should throw IndexOutOfBoundsException [1]");

        final List<E> finalList3 = makeObject();
        assertThrows(IndexOutOfBoundsException.class, () -> finalList3.add(Integer.MAX_VALUE, element),
                "List.add should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
    }

    /**
     *  Tests bounds checking for {@link List#add(int, Object)} on a
     *  full list.
     */
    @Test
    public void testListAddByIndexBoundsChecking2() {
        if (!isAddSupported()) {
            return;
        }

        final E element = getOtherElements()[0];

        final List<E> finalList0 = makeFullCollection();
        assertThrows(IndexOutOfBoundsException.class, () -> finalList0.add(Integer.MIN_VALUE, element),
                "List.add should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");

        final List<E> finalList1 = makeFullCollection();
        assertThrows(IndexOutOfBoundsException.class, () -> finalList1.add(-1, element),
                "List.add should throw IndexOutOfBoundsException [-1]");

        final List<E> finalList2 = makeFullCollection();
        assertThrows(IndexOutOfBoundsException.class, () -> finalList2.add(finalList2.size() + 1, element),
                "List.add should throw IndexOutOfBoundsException [size + 1]");

        final List<E> finalList3 = makeFullCollection();
        assertThrows(IndexOutOfBoundsException.class, () -> finalList3.add(Integer.MAX_VALUE, element),
                "List.add should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
    }

    /**
     *  Tests {@link List#equals(Object)}.
     */
    @Test
    public void testListEquals() {
        resetEmpty();
        List<E> list = getCollection();
        assertTrue(list.equals(getConfirmed()), "Empty lists should be equal");
        verify();
        assertTrue(list.equals(list), "Empty list should equal self");
        verify();

        List<E> list2 = Arrays.asList(getFullElements());
        assertFalse(list.equals(list2), "Empty list shouldn't equal full");
        verify();

        list2 = Arrays.asList(getOtherElements());
        assertFalse(list.equals(list2), "Empty list shouldn't equal other");
        verify();

        resetFull();
        list = getCollection();
        assertTrue(list.equals(getConfirmed()), "Full lists should be equal");
        verify();
        assertTrue(list.equals(list), "Full list should equal self");
        verify();

        list2 = makeObject();
        assertFalse(list.equals(list2), "Full list shouldn't equal empty");
        verify();

        list2 = Arrays.asList(getOtherElements());
        assertFalse(list.equals(list2), "Full list shouldn't equal other");
        verify();

        list2 = Arrays.asList(getFullElements());
        if (list2.size() < 2 && isAddSupported()) {
            // main list is only size 1, so let's add other elements to get a better list
            list.addAll(Arrays.asList(getOtherElements()));
            getConfirmed().addAll(Arrays.asList(getOtherElements()));
            list2 = new ArrayList<>(list2);
            list2.addAll(Arrays.asList(getOtherElements()));
        }
        if (list2.size() > 1) {
            Collections.reverse(list2);
            assertFalse(list.equals(list2), "Full list shouldn't equal full list with same elements but different order");
            verify();
        }

        resetFull();
        list = getCollection();
        assertFalse(list.isEmpty(), "List shouldn't equal String");
        verify();

        final List<E> listForC = Arrays.asList(getFullElements());
        final Collection<E> c = new AbstractCollection<E>() {
            @Override
            public Iterator<E> iterator() {
                return listForC.iterator();
            }

            @Override
            public int size() {
                return listForC.size();
            }
        };

        assertFalse(list.equals(c), "List shouldn't equal nonlist with same elements in same order");
        verify();
    }

    /**
     *  Tests {@link List#get(int)}.
     */
    @Test
    public void testListGetByIndex() {
        resetFull();
        final List<E> list = getCollection();
        final E[] elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            assertEquals(elements[i], list.get(i), "List should contain correct elements");
            verify();
        }
    }

    /**
     *  Tests bounds checking for {@link List#get(int)} on an
     *  empty list.
     */
    @Test
    public void testListGetByIndexBoundsChecking() {
        final List<E> list = makeObject();

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(Integer.MIN_VALUE),
                "List.get should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1),
                "List.get should throw IndexOutOfBoundsException [-1]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0),
                "List.get should throw IndexOutOfBoundsException [0]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1),
                "List.get should throw IndexOutOfBoundsException [1]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(Integer.MAX_VALUE),
                "List.get should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
    }

    /**
     *  Tests bounds checking for {@link List#get(int)} on a
     *  full list.
     */
    @Test
    public void testListGetByIndexBoundsChecking2() {
        final List<E> list = makeFullCollection();

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(Integer.MIN_VALUE),
                "List.get should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1),
                "List.get should throw IndexOutOfBoundsException [-1]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(getFullElements().length),
                "List.get should throw IndexOutOfBoundsException [size]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(Integer.MAX_VALUE),
                "List.get should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
    }

    /**
     *  Tests {@link List#hashCode()}.
     */
    @Test
    public void testListHashCode() {
        resetEmpty();
        int hash1 = getCollection().hashCode();
        int hash2 = getConfirmed().hashCode();
        assertEquals(hash1, hash2, "Empty lists should have equal hashCodes");
        verify();

        resetFull();
        hash1 = getCollection().hashCode();
        hash2 = getConfirmed().hashCode();
        assertEquals(hash1, hash2, "Full lists should have equal hashCodes");
        verify();
    }

    /**
     *  Tests {@link List#indexOf}.
     */
    @Test
    public void testListIndexOf() {
        resetFull();
        final List<E> list1 = getCollection();
        final List<E> list2 = getConfirmed();

        for (final E element : list2) {
            assertEquals(list1.indexOf(element),
                    list2.indexOf(element), "indexOf should return correct result");
            verify();
        }

        final E[] other = getOtherElements();
        for (final E element : other) {
            assertEquals(-1, list1.indexOf(element),
                    "indexOf should return -1 for nonexistent element");
            verify();
        }
    }

    /**
     *  Tests the {@link ListIterator#add(Object)} method of the list
     *  iterator.
     */
    @Test
    public void testListIteratorAdd() {
        if (!isAddSupported()) {
            return;
        }

        resetEmpty();
        final List<E> list1 = getCollection();
        final List<E> list2 = getConfirmed();

        final E[] elements = getFullElements();
        ListIterator<E> iter1 = list1.listIterator();
        ListIterator<E> iter2 = list2.listIterator();

        for (final E element : elements) {
            iter1.add(element);
            iter2.add(element);
            verify();
        }

        resetFull();
        iter1 = getCollection().listIterator();
        iter2 = getConfirmed().listIterator();
        for (final E element : elements) {
            iter1.next();
            iter2.next();
            iter1.add(element);
            iter2.add(element);
            verify();
        }
    }

    /**
     *  Tests the {@link ListIterator#set(Object)} method of the list
     *  iterator.
     */
    @Test
    public void testListIteratorSet() {
        if (!isSetSupported()) {
            return;
        }

        final E[] elements = getFullElements();

        resetFull();
        final ListIterator<E> iter1 = getCollection().listIterator();
        final ListIterator<E> iter2 = getConfirmed().listIterator();
        for (final E element : elements) {
            iter1.next();
            iter2.next();
            iter1.set(element);
            iter2.set(element);
            verify();
        }
    }

    /**
     *  Tests {@link List#lastIndexOf}.
     */
    @Test
    public void testListLastIndexOf() {
        resetFull();
        final List<E> list1 = getCollection();
        final List<E> list2 = getConfirmed();

        for (final E element : list2) {
            assertEquals(list1.lastIndexOf(element), list2.lastIndexOf(element),
                    "lastIndexOf should return correct result");
            verify();
        }

        final E[] other = getOtherElements();
        for (final E element : other) {
            assertEquals(-1, list1.lastIndexOf(element),
                    "lastIndexOf should return -1 for nonexistent " + "element");
            verify();
        }
    }

    /**
     *  Tests the read-only bits of {@link List#listIterator()}.
     */
    @Test
    public void testListListIterator() {
        resetFull();
        forwardTest(getCollection().listIterator(), 0);
        backwardTest(getCollection().listIterator(), 0);
    }

    /**
     *  Tests the read-only bits of {@link List#listIterator(int)}.
     */
    @Test
    public void testListListIteratorByIndex() {
        resetFull();
        assertThrows(IndexOutOfBoundsException.class, () -> getCollection().listIterator(-1));
        resetFull();
        assertThrows(IndexOutOfBoundsException.class, () -> getCollection().listIterator(getCollection().size() + 1));
        resetFull();
        for (int i = 0; i <= getConfirmed().size(); i++) {
            forwardTest(getCollection().listIterator(i), i);
            backwardTest(getCollection().listIterator(i), i);
        }
        resetFull();
        for (int i = 0; i <= getConfirmed().size(); i++) {
            backwardTest(getCollection().listIterator(i), i);
        }
    }

    /**
     * Tests remove on list iterator is correct.
     */
    @Test
    public void testListListIteratorNextRemoveNext() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        if (getCollection().size() < 4) {
            return;
        }
        final ListIterator<E> it = getCollection().listIterator();
        final E zero = it.next();
        final E one = it.next();
        final E two = it.next();
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        assertEquals(two, getCollection().get(2));
        final E three = getCollection().get(3);

        it.remove(); // removed element at index 2 (two)
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        final E three2 = it.next();  // do next after remove
        assertEquals(three, three2);
        assertEquals(getCollection().size() > 3, it.hasNext());
        assertTrue(it.hasPrevious());
    }

    /**
     * Tests remove on list iterator is correct.
     */
    @Test
    public void testListListIteratorNextRemovePrevious() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        if (getCollection().size() < 4) {
            return;
        }
        final ListIterator<E> it = getCollection().listIterator();
        final E zero = it.next();
        final E one = it.next();
        final E two = it.next();
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        assertEquals(two, getCollection().get(2));

        it.remove(); // removed element at index 2 (two)
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        final E one2 = it.previous();  // do previous after remove
        assertEquals(one, one2);
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
    }

    /**
     * Tests remove on list iterator is correct.
     */
    @Test
    public void testListListIteratorPreviousRemoveNext() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        if (getCollection().size() < 4) {
            return;
        }
        final ListIterator<E> it = getCollection().listIterator();
        final E zero = it.next();
        final E one = it.next();
        final E two = it.next();
        final E two2 = it.previous();
        final E one2 = it.previous();
        assertEquals(one, one2);
        assertEquals(two, two2);
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        assertEquals(two, getCollection().get(2));

        it.remove(); // removed element at index 1 (one)
        assertEquals(zero, getCollection().get(0));
        assertEquals(two, getCollection().get(1));
        final E two3 = it.next();  // do next after remove
        assertEquals(two, two3);
        assertEquals(getCollection().size() > 2, it.hasNext());
        assertTrue(it.hasPrevious());
    }

    /**
     * Tests remove on list iterator is correct.
     */
    @Test
    public void testListListIteratorPreviousRemovePrevious() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        if (getCollection().size() < 4) {
            return;
        }
        final ListIterator<E> it = getCollection().listIterator();
        final E zero = it.next();
        final E one = it.next();
        final E two = it.next();
        final E two2 = it.previous();
        final E one2 = it.previous();
        assertEquals(one, one2);
        assertEquals(two, two2);
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        assertEquals(two, getCollection().get(2));

        it.remove(); // removed element at index 1 (one)
        assertEquals(zero, getCollection().get(0));
        assertEquals(two, getCollection().get(1));
        final E zero3 = it.previous();  // do previous after remove
        assertEquals(zero, zero3);
        assertFalse(it.hasPrevious());
        assertEquals(getCollection().size() > 2, it.hasNext());
    }

    /**
     *  Tests {@link List#remove(int)}.
     */
    @Test
    public void testListRemoveByIndex() {
        if (!isRemoveSupported()) {
            return;
        }

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            resetFull();
            final E o1 = getCollection().remove(i);
            final E o2 = getConfirmed().remove(i);
            assertEquals(o1, o2, "remove should return correct element");
            verify();
        }
    }

    /**
     *  Tests bounds checking for {@link List#remove(int)} on an
     *  empty list.
     */
    @Test
    public void testListRemoveByIndexBoundsChecking() {
        if (!isRemoveSupported()) {
            return;
        }

        final List<E> list = makeObject();

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(Integer.MIN_VALUE),
                "List.remove should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1),
                "List.remove should throw IndexOutOfBoundsException [-1]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0),
                "List.remove should throw IndexOutOfBoundsException [0]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1),
                "List.remove should throw IndexOutOfBoundsException [1]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(Integer.MAX_VALUE),
                "List.remove should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
    }

    /**
     *  Tests bounds checking for {@link List#remove(int)} on a
     *  full list.
     */
    @Test
    public void testListRemoveByIndexBoundsChecking2() {
        if (!isRemoveSupported()) {
            return;
        }

        final List<E> list = makeFullCollection();

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(Integer.MIN_VALUE),
                "List.remove should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1),
                "List.remove should throw IndexOutOfBoundsException [-1]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(getFullElements().length),
                "List.remove should throw IndexOutOfBoundsException [size]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(Integer.MAX_VALUE),
                "List.remove should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
    }

    /**
     *  Test {@link List#set(int,Object)}.
     */
    @Test
    public void testListSetByIndex() {
        if (!isSetSupported()) {
            return;
        }

        resetFull();
        final E[] elements = getFullElements();
        final E[] other = getOtherElements();

        for (int i = 0; i < elements.length; i++) {
            final E n = other[i % other.length];
            final E v = getCollection().set(i, n);
            assertEquals(elements[i], v, "Set should return correct element");
            getConfirmed().set(i, n);
            verify();
        }
    }

    /**
     *  Tests bounds checking for {@link List#set(int,Object)} on an
     *  empty list.
     */
    @Test
    public void testListSetByIndexBoundsChecking() {
        if (!isSetSupported()) {
            return;
        }

        final List<E> list = makeObject();
        final E element = getOtherElements()[0];

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(Integer.MIN_VALUE, element),
                "List.set should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, element),
                "List.set should throw IndexOutOfBoundsException [-1]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(0, element),
                "List.set should throw IndexOutOfBoundsException [0]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, element),
                "List.set should throw IndexOutOfBoundsException [1]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(Integer.MAX_VALUE, element),
                "List.set should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
    }

    /**
     *  Tests bounds checking for {@link List#set(int,Object)} on a
     *  full list.
     */
    @Test
    public void testListSetByIndexBoundsChecking2() {
        if (!isSetSupported()) {
            return;
        }

        final List<E> list = makeFullCollection();
        final E element = getOtherElements()[0];

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(Integer.MIN_VALUE, element),
                "List.set should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, element),
                "List.set should throw IndexOutOfBoundsException [-1]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(getFullElements().length, element),
                "List.set should throw IndexOutOfBoundsException [size]");

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(Integer.MAX_VALUE, element),
                "List.set should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
    }

    /**
     * Tests that a sublist raises a {@link java.util.ConcurrentModificationException ConcurrentModificationException}
     * if elements are added to the original list.
     */
    @Test
    public void testListSubListFailFastOnAdd() {
        if (!isFailFastSupported()) {
            return;
        }
        if (!isAddSupported()) {
            return;
        }

        resetFull();
        final int size = getCollection().size();
        List<E> sub = getCollection().subList(1, size);
        getCollection().add(getOtherElements()[0]);
        failFastAll(sub);

        resetFull();
        sub = getCollection().subList(1, size);
        getCollection().add(0, getOtherElements()[0]);
        failFastAll(sub);

        resetFull();
        sub = getCollection().subList(1, size);
        getCollection().addAll(Arrays.asList(getOtherElements()));
        failFastAll(sub);

        resetFull();
        sub = getCollection().subList(1, size);
        getCollection().addAll(0, Arrays.asList(getOtherElements()));
        failFastAll(sub);
    }

    /**
     * Tests that a sublist raises a {@link java.util.ConcurrentModificationException ConcurrentModificationException}
     * if elements are removed from the original list.
     */
    @Test
    public void testListSubListFailFastOnRemove() {
        if (!isFailFastSupported()) {
            return;
        }
        if (!isRemoveSupported()) {
            return;
        }

        resetFull();
        final int size = getCollection().size();
        List<E> sub = getCollection().subList(1, size);
        getCollection().remove(0);
        failFastAll(sub);

        resetFull();
        sub = getCollection().subList(1, size);
        getCollection().remove(getFullElements()[2]);
        failFastAll(sub);

        resetFull();
        sub = getCollection().subList(1, size);
        getCollection().removeAll(Arrays.asList(getFullElements()));
        failFastAll(sub);

        resetFull();
        sub = getCollection().subList(1, size);
        getCollection().retainAll(Arrays.asList(getOtherElements()));
        failFastAll(sub);

        resetFull();
        sub = getCollection().subList(1, size);
        getCollection().clear();
        failFastAll(sub);
    }

    /**
     *  If {@link #isSetSupported()} returns false, tests that set operation
     *  raises <Code>UnsupportedOperationException.
     */
    @Test
    public void testUnsupportedSet() {
        if (isSetSupported()) {
            return;
        }

        resetFull();
        assertThrows(UnsupportedOperationException.class, () -> getCollection().set(0, getFullElements()[0]),
                "Empty collection should not support set.");
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();
    }

    /**
     *  Verifies that the test list implementation matches the confirmed list
     *  implementation.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void verify() {
        super.verify();

        final List<E> list1 = getCollection();
        final List<E> list2 = getConfirmed();

        assertEquals(list1, list2, "List should equal confirmed");
        assertEquals(list2, list1, "Confirmed should equal list");

        assertEquals(list1.hashCode(), list2.hashCode(), "Hash codes should be equal");

        int i = 0;
        final Iterator<E> iterator1 = list1.iterator();
        final E[] array = (E[]) list1.toArray();
        for (Object o2 : list2) {
            assertTrue(iterator1.hasNext(), "List iterator should have next");
            final Object o1 = iterator1.next();
            assertEquals(o1, o2, "Iterator elements should be equal");
            o2 = list1.get(i);
            assertEquals(o1, o2, "get should return correct element");
            o2 = array[i];
            assertEquals(o1, o2, "toArray should have correct element");
            i++;
        }
    }

}
