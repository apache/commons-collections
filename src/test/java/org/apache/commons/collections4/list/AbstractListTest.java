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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.iterators.AbstractListIteratorTest;

/**
 * Abstract test class for {@link java.util.List} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your {@link List} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link List} fails or override one of the
 * protected methods from AbstractCollectionTest.
 *
 */
public abstract class AbstractListTest<E> extends AbstractCollectionTest<E> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractListTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
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

        assertEquals("List should equal confirmed", list1, list2);
        assertEquals("Confirmed should equal list", list2, list1);

        assertEquals("Hash codes should be equal", list1.hashCode(), list2.hashCode());

        int i = 0;
        final Iterator<E> iterator1 = list1.iterator();
        final Iterator<E> iterator2 = list2.iterator();
        final E[] array = (E[]) list1.toArray();
        while (iterator2.hasNext()) {
            assertTrue("List iterator should have next", iterator1.hasNext());
            final Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            assertEquals("Iterator elements should be equal", o1, o2);
            o2 = list1.get(i);
            assertEquals("get should return correct element", o1, o2);
            o2 = array[i];
            assertEquals("toArray should have correct element", o1, o2);
            i++;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * List equals method is defined.
     */
    @Override
    public boolean isEqualsCheckable() {
        return true;
    }

    /**
     * Returns an empty {@link ArrayList}.
     */
    @Override
    public Collection<E> makeConfirmedCollection() {
        final ArrayList<E> list = new ArrayList<>();
        return list;
    }

    /**
     * Returns a full {@link ArrayList}.
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final ArrayList<E> list = new ArrayList<>();
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
     * {@inheritDoc}
     */
    @Override
    public List<E> makeFullCollection() {
        // only works if list supports optional "addAll(Collection)"
        final List<E> list = makeObject();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    //-----------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
    /**
     *  Tests bounds checking for {@link List#add(int, Object)} on an
     *  empty list.
     */
    public void testListAddByIndexBoundsChecking() {
        if (!isAddSupported()) {
            return;
        }

        final List<E> list;
        final E element = getOtherElements()[0];

        list = makeObject();
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(Integer.MIN_VALUE, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(-1, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(1, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(Integer.MAX_VALUE, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
    }

    /**
     *  Tests bounds checking for {@link List#add(int, Object)} on a
     *  full list.
     */
    public void testListAddByIndexBoundsChecking2() {
        if (!isAddSupported()) {
            return;
        }

        final List<E> list = makeFullCollection();
        final E element = getOtherElements()[0];

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(Integer.MIN_VALUE, element);
        });
        assertNotNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(-1, element);
        });
        assertNotNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(list.size() + 1, element);
        });
        assertNotNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(Integer.MAX_VALUE, element);
        });
        assertNotNull(exception.getMessage());
    }

    /**
     *  Tests {@link List#add(int,Object)}.
     */
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
     *  Tests {@link List#equals(Object)}.
     */
    public void testListEquals() {
        resetEmpty();
        List<E> list = getCollection();
        assertEquals("Empty lists should be equal", true, list.equals(getConfirmed()));
        verify();
        assertEquals("Empty list should equal self", true, list.equals(list));
        verify();

        List<E> list2 = Arrays.asList(getFullElements());
        assertEquals("Empty list shouldn't equal full", false, list.equals(list2));
        verify();

        list2 = Arrays.asList(getOtherElements());
        assertEquals("Empty list shouldn't equal other", false, list.equals(list2));
        verify();

        resetFull();
        list = getCollection();
        assertEquals("Full lists should be equal", true, list.equals(getConfirmed()));
        verify();
        assertEquals("Full list should equal self", true, list.equals(list));
        verify();

        list2 = makeObject();
        assertEquals("Full list shouldn't equal empty", false, list.equals(list2));
        verify();

        list2 = Arrays.asList(getOtherElements());
        assertEquals("Full list shouldn't equal other", false, list.equals(list2));
        verify();

        list2 = Arrays.asList(getFullElements());
        if (list2.size() < 2 && isAddSupported()) {
            // main list is only size 1, so lets add other elements to get a better list
            list.addAll(Arrays.asList(getOtherElements()));
            getConfirmed().addAll(Arrays.asList(getOtherElements()));
            list2 = new ArrayList<>(list2);
            list2.addAll(Arrays.asList(getOtherElements()));
        }
        if (list2.size() > 1) {
            Collections.reverse(list2);
            assertEquals(
                "Full list shouldn't equal full list with same elements but different order",
                false, list.equals(list2));
            verify();
        }

        resetFull();
        list = getCollection();
        assertEquals("List shouldn't equal String", false, list.equals(""));
        verify();

        final List<E> listForC = Arrays.asList(getFullElements());
        final Collection<E> c = new AbstractCollection<E>() {
            @Override
            public int size() {
                return listForC.size();
            }

            @Override
            public Iterator<E> iterator() {
                return listForC.iterator();
            }
        };

        assertEquals("List shouldn't equal nonlist with same elements in same order", false, list.equals(c));
        verify();
    }

    /**
     *  Tests {@link List#hashCode()}.
     */
    public void testListHashCode() {
        resetEmpty();
        int hash1 = getCollection().hashCode();
        int hash2 = getConfirmed().hashCode();
        assertEquals("Empty lists should have equal hashCodes", hash1, hash2);
        verify();

        resetFull();
        hash1 = getCollection().hashCode();
        hash2 = getConfirmed().hashCode();
        assertEquals("Full lists should have equal hashCodes", hash1, hash2);
        verify();
    }

    /**
     *  Tests {@link List#get(int)}.
     */
    public void testListGetByIndex() {
        resetFull();
        final List<E> list = getCollection();
        final E[] elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            assertEquals("List should contain correct elements", elements[i], list.get(i));
            verify();
        }
    }

    /**
     *  Tests bounds checking for {@link List#get(int)} on an
     *  empty list.
     */
    public void testListGetByIndexBoundsChecking() {
        final List<E> list = makeObject();

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(Integer.MIN_VALUE);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(-1);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(0);
        });
        assertNotNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(1);
        });
        assertNotNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(Integer.MAX_VALUE);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
    }

    /**
     *  Tests bounds checking for {@link List#get(int)} on a
     *  full list.
     */
    public void testListGetByIndexBoundsChecking2() {
        final List<E> list = makeFullCollection();

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(Integer.MIN_VALUE);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(-1);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(getFullElements().length);
        });
        assertNotNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(Integer.MAX_VALUE);
        });
        assertNotNull(exception.getMessage());
    }

    /**
     *  Tests {@link List#indexOf}.
     */
    public void testListIndexOf() {
        resetFull();
        final List<E> list1 = getCollection();
        final List<E> list2 = getConfirmed();

        for (final E element : list2) {
            assertEquals("indexOf should return correct result",
                    list1.indexOf(element), list2.indexOf(element));
            verify();
        }

        final E[] other = getOtherElements();
        for (final E element : other) {
            assertEquals("indexOf should return -1 for nonexistent element",
                -1, list1.indexOf(element));
            verify();
        }
    }

    /**
     *  Tests {@link List#lastIndexOf}.
     */
    public void testListLastIndexOf() {
        resetFull();
        final List<E> list1 = getCollection();
        final List<E> list2 = getConfirmed();

        final Iterator<E> iterator = list2.iterator();
        while (iterator.hasNext()) {
            final E element = iterator.next();
            assertEquals("lastIndexOf should return correct result",
                list1.lastIndexOf(element), list2.lastIndexOf(element));
            verify();
        }

        final E[] other = getOtherElements();
        for (final E element : other) {
            assertEquals("lastIndexOf should return -1 for nonexistent " +
                "element", -1, list1.lastIndexOf(element));
            verify();
        }
    }

    /**
     *  Tests bounds checking for {@link List#set(int,Object)} on an
     *  empty list.
     */
    public void testListSetByIndexBoundsChecking() {
        if (!isSetSupported()) {
            return;
        }

        final List<E> list = makeObject();
        final E element = getOtherElements()[0];

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.set(Integer.MIN_VALUE, element);
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("-2147483648"));
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.set(-1, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.set(0, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.set(1, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.set(Integer.MAX_VALUE, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
    }


    /**
     *  Tests bounds checking for {@link List#set(int,Object)} on a
     *  full list.
     */
    public void testListSetByIndexBoundsChecking2() {
        if (!isSetSupported()) {
            return;
        }

        final List<E> list = makeFullCollection();
        final E element = getOtherElements()[0];

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.set(Integer.MIN_VALUE, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.set(-1, element);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.set(getFullElements().length, element);
        });
        assertNotNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.set(Integer.MAX_VALUE, element);
        });
        assertNotNull(exception.getMessage());
    }


    /**
     *  Test {@link List#set(int,Object)}.
     */
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
            assertEquals("Set should return correct element", elements[i], v);
            getConfirmed().set(i, n);
            verify();
        }
    }

    /**
     *  If {@link #isSetSupported()} returns false, tests that set operation
     *  raises <Code>UnsupportedOperationException.
     */
    public void testUnsupportedSet() {
        if (isSetSupported()) {
            return;
        }

        resetFull();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().set(0, getFullElements()[0]);
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("Index:"));
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();
    }

    /**
     *  Tests bounds checking for {@link List#remove(int)} on an
     *  empty list.
     */
    public void testListRemoveByIndexBoundsChecking() {
        if (!isRemoveSupported()) {
            return;
        }

        final List<E> list = makeObject();

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.remove(Integer.MIN_VALUE);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.remove(-1);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.remove(0);
        });
        assertNotNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.remove(1);
        });
        assertNotNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.remove(Integer.MAX_VALUE);
        });
        assertNotNull(exception.getMessage());
    }

    /**
     *  Tests bounds checking for {@link List#remove(int)} on a
     *  full list.
     */
    public void testListRemoveByIndexBoundsChecking2() {
        if (!isRemoveSupported()) {
            return;
        }

        final List<E> list = makeFullCollection();

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.remove(Integer.MIN_VALUE);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.remove(-1);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.remove(getFullElements().length);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            list.remove(Integer.MAX_VALUE);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
    }


    /**
     *  Tests {@link List#remove(int)}.
     */
    public void testListRemoveByIndex() {
        if (!isRemoveSupported()) {
            return;
        }

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            resetFull();
            final E o1 = getCollection().remove(i);
            final E o2 = getConfirmed().remove(i);
            assertEquals("remove should return correct element", o1, o2);
            verify();
        }
    }

    /**
     *  Tests the read-only bits of {@link List#listIterator()}.
     */
    public void testListListIterator() {
        resetFull();
        forwardTest(getCollection().listIterator(), 0);
        backwardTest(getCollection().listIterator(), 0);
    }

    /**
     *  Tests the read-only bits of {@link List#listIterator(int)}.
     */
    public void testListListIteratorByIndex() {
        resetFull();
        try {
            getCollection().listIterator(-1);
        } catch (final IndexOutOfBoundsException ex) {}
        resetFull();
        try {
            getCollection().listIterator(getCollection().size() + 1);
        } catch (final IndexOutOfBoundsException ex) {}
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

    //-----------------------------------------------------------------------
    /**
     * Tests remove on list iterator is correct.
     */
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
        assertEquals(true, it.hasPrevious());
    }

    /**
     * Tests remove on list iterator is correct.
     */
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
     * Tests remove on list iterator is correct.
     */
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

    //-----------------------------------------------------------------------
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
            assertTrue(iter.hasNext());
            assertEquals("Iterator.nextIndex should work",
                i, iter.nextIndex());
            assertEquals("Iterator.previousIndex should work",
                i - 1, iter.previousIndex());
            final Object o = iter.next();
            assertEquals("Iterator returned correct element", list.get(i), o);
            i++;
        }

        assertTrue(!iter.hasNext());
        assertEquals("nextIndex should be size", max, iter.nextIndex());
        assertEquals("previousIndex should be size - 1", max - 1, iter.previousIndex());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            iter.next();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("No element at index"));
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
            assertTrue(iter.hasPrevious());
            assertEquals("Iterator.nextIndex should work, i:" + i,
                i, iter.nextIndex());
            assertEquals("Iterator.previousIndex should work, i:" + i,
                i - 1, iter.previousIndex());
            final E o = iter.previous();
            assertEquals("Iterator returned correct element",
                list.get(i - 1), o);
            i--;
        }

        assertTrue(!iter.hasPrevious());
        final int nextIndex = iter.nextIndex();
        assertEquals("nextIndex should be 0", 0, nextIndex);
        final int prevIndex = iter.previousIndex();
        assertEquals("previousIndex should be -1", -1, prevIndex);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            iter.previous();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("Already at start of list."));
        }
    }


    /**
     *  Tests the {@link ListIterator#add(Object)} method of the list
     *  iterator.
     */
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

    @SuppressWarnings("unchecked")
    public void testEmptyListSerialization() throws IOException, ClassNotFoundException {
        final List<E> list = makeObject();
        if (!(list instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] objekt = writeExternalFormToBytes((Serializable) list);
        final List<E> list2 = (List<E>) readExternalFormFromBytes(objekt);

        assertEquals("Both lists are empty", 0, list.size());
        assertEquals("Both lists are empty", 0, list2.size());
    }

    @SuppressWarnings("unchecked")
    public void testFullListSerialization() throws IOException, ClassNotFoundException {
        final List<E> list = makeFullCollection();
        final int size = getFullElements().length;
        if (!(list instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] objekt = writeExternalFormToBytes((Serializable) list);
        final List<E> list2 = (List<E>) readExternalFormFromBytes(objekt);

        assertEquals("Both lists are same size", size, list.size());
        assertEquals("Both lists are same size", size, list2.size());
    }

    /**
     * Compare the current serialized form of the List
     * against the canonical version in SCM.
     */
    @SuppressWarnings("unchecked")
    public void testEmptyListCompatibility() throws IOException, ClassNotFoundException {
        /**
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
            assertEquals("List is empty", 0, list2.size());
            assertEquals(list, list2);
        }
    }

    /**
     * Compare the current serialized form of the List
     * against the canonical version in SCM.
     */
    @SuppressWarnings("unchecked")
    public void testFullListCompatibility() throws IOException, ClassNotFoundException {
        /**
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
            assertEquals("List is the right size", list.size(), list2.size());
            assertEquals(list, list2);
        }
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns a {@link BulkTest} for testing {@link List#subList(int,int)}.
     *  The returned bulk test will run through every {@code TestList}
     *  method, <i>including</i> another {@code bulkTestSubList}.
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

    public static class BulkTestSubList<E> extends AbstractListTest<E> {

        private final AbstractListTest<E> outer;

        public BulkTestSubList(final AbstractListTest<E> outer) {
            super("");
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
        public boolean isSetSupported() {
            return outer.isSetSupported();
        }

        @Override
        public boolean isRemoveSupported() {
            return outer.isRemoveSupported();
        }

        @Override
        public List<E> makeObject() {
            return outer.makeFullCollection().subList(4, 4);
        }

        @Override
        public List<E> makeFullCollection() {
            final int size = getFullElements().length;
            return outer.makeFullCollection().subList(3, size - 3);
        }

        @Override
        public void resetEmpty() {
            outer.resetFull();
            this.setCollection(outer.getCollection().subList(4, 4));
            this.setConfirmed(outer.getConfirmed().subList(4, 4));
        }

        @Override
        public void resetFull() {
            outer.resetFull();
            final int size = outer.getConfirmed().size();
            this.setCollection(outer.getCollection().subList(3, size - 3));
            this.setConfirmed(outer.getConfirmed().subList(3, size - 3));
        }

        @Override
        public void verify() {
            super.verify();
            outer.verify();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }
    }

    /**
     * Tests that a sublist raises a {@link java.util.ConcurrentModificationException ConcurrentModificationException}
     * if elements are added to the original list.
     */
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

        Exception exception = assertThrows(Exception.class, () -> {
            m.invoke(list, params);
        });
        assertNull(exception.getMessage());
    }

    // -----------------------------------------------------------------------
    public BulkTest bulkTestListIterator() {
        return new TestListIterator();
    }

    public class TestListIterator extends AbstractListIteratorTest<E> {
        public TestListIterator() {
            super("TestListIterator");
        }

        @Override
        public E addSetValue() {
            return AbstractListTest.this.getOtherElements()[0];
        }

        @Override
        public boolean supportsRemove() {
            return AbstractListTest.this.isRemoveSupported();
        }

        @Override
        public boolean supportsAdd() {
            return AbstractListTest.this.isAddSupported();
        }

        @Override
        public boolean supportsSet() {
            return AbstractListTest.this.isSetSupported();
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
    }
}
