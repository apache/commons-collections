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
package org.apache.commons.collections.list;

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

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.collection.AbstractCollectionTest;
import org.apache.commons.collections.iterators.AbstractTestListIterator;

/**
 * Abstract test class for {@link java.util.List} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your {@link List} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link List} fails or override one of the
 * protected methods from AbstractTestCollection.
 *
 * @version $Revision$
 *
 * @author Rodney Waldhoff
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author Neil O'Toole
 */
public abstract class AbstractTestList<E> extends AbstractCollectionTest<E> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractTestList(String testName) {
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

        List<E> list1 = getCollection();
        List<E> list2 = getConfirmed();

        assertEquals("List should equal confirmed", list1, list2);
        assertEquals("Confirmed should equal list", list2, list1);

        assertEquals("Hash codes should be equal", list1.hashCode(), list2.hashCode());

        int i = 0;
        Iterator<E> iterator1 = list1.iterator();
        Iterator<E> iterator2 = list2.iterator();
        E[] array = (E[]) list1.toArray();
        while (iterator2.hasNext()) {
            assertTrue("List iterator should have next", iterator1.hasNext());
            Object o1 = iterator1.next();
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
        ArrayList<E> list = new ArrayList<E>();
        return list;
    }

    /**
     * Returns a full {@link ArrayList}.
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        ArrayList<E> list = new ArrayList<E>();
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
        List<E> list = makeObject();
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

        List<E> list;
        E element = getOtherElements()[0];

        try {
            list = makeObject();
            list.add(Integer.MIN_VALUE, element);
            fail("List.add should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list = makeObject();
            list.add(-1, element);
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list = makeObject();
            list.add(1, element);
            fail("List.add should throw IndexOutOfBoundsException [1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list = makeObject();
            list.add(Integer.MAX_VALUE, element);
            fail("List.add should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
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

        List<E> list;
        E element = getOtherElements()[0];

        try {
            list = makeFullCollection();
            list.add(Integer.MIN_VALUE, element);
            fail("List.add should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list = makeFullCollection();
            list.add(-1, element);
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list = makeFullCollection();
            list.add(list.size() + 1, element);
            fail("List.add should throw IndexOutOfBoundsException [size + 1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list = makeFullCollection();
            list.add(Integer.MAX_VALUE, element);
            fail("List.add should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     *  Tests {@link List#add(int,Object)}.
     */
    public void testListAddByIndex() {
        if (!isAddSupported()) {
            return;
        }

        E element = getOtherElements()[0];
        int max = getFullElements().length;

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
            list2 = new ArrayList<E>(list2);
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
        Collection<E> c = new AbstractCollection<E>() {
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
        List<E> list = getCollection();
        E[] elements = getFullElements();
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
        List<E> list = makeObject();

        try {
            list.get(Integer.MIN_VALUE);
            fail("List.get should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(-1);
            fail("List.get should throw IndexOutOfBoundsException [-1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(0);
            fail("List.get should throw IndexOutOfBoundsException [0]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(1);
            fail("List.get should throw IndexOutOfBoundsException [1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(Integer.MAX_VALUE);
            fail("List.get should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     *  Tests bounds checking for {@link List#get(int)} on a
     *  full list.
     */
    public void testListGetByIndexBoundsChecking2() {
        List<E> list = makeFullCollection();

        try {
            list.get(Integer.MIN_VALUE);
            fail("List.get should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(-1);
            fail("List.get should throw IndexOutOfBoundsException [-1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(getFullElements().length);
            fail("List.get should throw IndexOutOfBoundsException [size]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(Integer.MAX_VALUE);
            fail("List.get should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     *  Tests {@link List#indexOf}.
     */
    public void testListIndexOf() {
        resetFull();
        List<E> list1 = getCollection();
        List<E> list2 = getConfirmed();

        Iterator<E> iterator = list2.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();
            assertEquals("indexOf should return correct result",
                list1.indexOf(element), list2.indexOf(element));
            verify();
        }

        E[] other = getOtherElements();
        for (int i = 0; i < other.length; i++) {
            assertEquals("indexOf should return -1 for nonexistent element",
                -1, list1.indexOf(other[i]));
            verify();
        }
    }

    /**
     *  Tests {@link List#lastIndexOf}.
     */
    public void testListLastIndexOf() {
        resetFull();
        List<E> list1 = getCollection();
        List<E> list2 = getConfirmed();

        Iterator<E> iterator = list2.iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();
            assertEquals("lastIndexOf should return correct result",
              list1.lastIndexOf(element), list2.lastIndexOf(element));
            verify();
        }

        E[] other = getOtherElements();
        for (int i = 0; i < other.length; i++) {
            assertEquals("lastIndexOf should return -1 for nonexistent " +
                "element", -1, list1.lastIndexOf(other[i]));
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

        List<E> list = makeObject();
        E element = getOtherElements()[0];

        try {
            list.set(Integer.MIN_VALUE, element);
            fail("List.set should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.set(-1, element);
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.set(0, element);
            fail("List.set should throw IndexOutOfBoundsException [0]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.set(1, element);
            fail("List.set should throw IndexOutOfBoundsException [1]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.set(Integer.MAX_VALUE, element);
            fail("List.set should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }


    /**
     *  Tests bounds checking for {@link List#set(int,Object)} on a
     *  full list.
     */
    public void testListSetByIndexBoundsChecking2() {
        if (!isSetSupported()) return;

        List<E> list = makeFullCollection();
        E element = getOtherElements()[0];

        try {
            list.set(Integer.MIN_VALUE, element);
            fail("List.set should throw IndexOutOfBoundsException " +
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.set(-1, element);
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.set(getFullElements().length, element);
            fail("List.set should throw IndexOutOfBoundsException [size]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.set(Integer.MAX_VALUE, element);
            fail("List.set should throw IndexOutOfBoundsException " +
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }


    /**
     *  Test {@link List#set(int,Object)}.
     */
    public void testListSetByIndex() {
        if (!isSetSupported()) return;

        resetFull();
        E[] elements = getFullElements();
        E[] other = getOtherElements();

        for (int i = 0; i < elements.length; i++) {
            E n = other[i % other.length];
            E v = (getCollection()).set(i, n);
            assertEquals("Set should return correct element", elements[i], v);
            (getConfirmed()).set(i, n);
            verify();
        }
    }

    /**
     *  If {@link #isSetSupported()} returns false, tests that set operation
     *  raises <Code>UnsupportedOperationException.
     */
    public void testUnsupportedSet() {
        if (isSetSupported()) return;

        resetFull();
        try {
            (getCollection()).set(0, getFullElements()[0]);
            fail("Emtpy collection should not support set.");
        } catch (UnsupportedOperationException e) {
            // expected
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
        if (!isRemoveSupported()) return;

        List<E> list = makeObject();

        try {
            list.remove(Integer.MIN_VALUE);
            fail("List.remove should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.remove(-1);
            fail("List.remove should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.remove(0);
            fail("List.remove should throw IndexOutOfBoundsException [0]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.remove(1);
            fail("List.remove should throw IndexOutOfBoundsException [1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.remove(Integer.MAX_VALUE);
            fail("List.remove should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     *  Tests bounds checking for {@link List#remove(int)} on a
     *  full list.
     */
    public void testListRemoveByIndexBoundsChecking2() {
        if (!isRemoveSupported()) return;

        List<E> list = makeFullCollection();

        try {
            list.remove(Integer.MIN_VALUE);
            fail("List.remove should throw IndexOutOfBoundsException " +
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.remove(-1);
            fail("List.remove should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.remove(getFullElements().length);
            fail("List.remove should throw IndexOutOfBoundsException [size]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.remove(Integer.MAX_VALUE);
            fail("List.remove should throw IndexOutOfBoundsException " +
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }


    /**
     *  Tests {@link List#remove(int)}.
     */
    public void testListRemoveByIndex() {
        if (!isRemoveSupported()) return;

        int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            resetFull();
            E o1 = (getCollection()).remove(i);
            E o2 = (getConfirmed()).remove(i);
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
        } catch (IndexOutOfBoundsException ex) {}
        resetFull();
        try {
            getCollection().listIterator(getCollection().size() + 1);
        } catch (IndexOutOfBoundsException ex) {}
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
        if (isRemoveSupported() == false) return;
        resetFull();
        if (getCollection().size() < 4) return;
        ListIterator<E> it = getCollection().listIterator();
        E zero = it.next();
        E one = it.next();
        E two = it.next();
        E two2 = it.previous();
        E one2 = it.previous();
        assertEquals(one, one2);
        assertEquals(two, two2);
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        assertEquals(two, getCollection().get(2));

        it.remove(); // removed element at index 1 (one)
        assertEquals(zero, getCollection().get(0));
        assertEquals(two, getCollection().get(1));
        E two3 = it.next();  // do next after remove
        assertEquals(two, two3);
        assertEquals(getCollection().size() > 2, it.hasNext());
        assertEquals(true, it.hasPrevious());
    }

    /**
     * Tests remove on list iterator is correct.
     */
    public void testListListIteratorPreviousRemovePrevious() {
        if (isRemoveSupported() == false) return;
        resetFull();
        if (getCollection().size() < 4) return;
        ListIterator<E> it = getCollection().listIterator();
        E zero = it.next();
        E one = it.next();
        E two = it.next();
        E two2 = it.previous();
        E one2 = it.previous();
        assertEquals(one, one2);
        assertEquals(two, two2);
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        assertEquals(two, getCollection().get(2));

        it.remove(); // removed element at index 1 (one)
        assertEquals(zero, getCollection().get(0));
        assertEquals(two, getCollection().get(1));
        E zero3 = it.previous();  // do previous after remove
        assertEquals(zero, zero3);
        assertEquals(false, it.hasPrevious());
        assertEquals(getCollection().size() > 2, it.hasNext());
    }

    /**
     * Tests remove on list iterator is correct.
     */
    public void testListListIteratorNextRemoveNext() {
        if (isRemoveSupported() == false) return;
        resetFull();
        if (getCollection().size() < 4) return;
        ListIterator<E> it = getCollection().listIterator();
        E zero = it.next();
        E one = it.next();
        E two = it.next();
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        assertEquals(two, getCollection().get(2));
        E three = getCollection().get(3);

        it.remove(); // removed element at index 2 (two)
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        E three2 = it.next();  // do next after remove
        assertEquals(three, three2);
        assertEquals(getCollection().size() > 3, it.hasNext());
        assertEquals(true, it.hasPrevious());
    }

    /**
     * Tests remove on list iterator is correct.
     */
    public void testListListIteratorNextRemovePrevious() {
        if (isRemoveSupported() == false) return;
        resetFull();
        if (getCollection().size() < 4) return;
        ListIterator<E> it = getCollection().listIterator();
        E zero = it.next();
        E one = it.next();
        E two = it.next();
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        assertEquals(two, getCollection().get(2));

        it.remove(); // removed element at index 2 (two)
        assertEquals(zero, getCollection().get(0));
        assertEquals(one, getCollection().get(1));
        E one2 = it.previous();  // do previous after remove
        assertEquals(one, one2);
        assertEquals(true, it.hasNext());
        assertEquals(true, it.hasPrevious());
    }

    //-----------------------------------------------------------------------
    /**
     *  Traverses to the end of the given iterator.
     *
     *  @param iter  the iterator to traverse
     *  @param i     the starting index
     */
    private void forwardTest(ListIterator<E> iter, int i) {
        List<E> list = getCollection();
        int max = getFullElements().length;

        while (i < max) {
            assertTrue("Iterator should have next", iter.hasNext());
            assertEquals("Iterator.nextIndex should work",
                i, iter.nextIndex());
            assertEquals("Iterator.previousIndex should work",
                i - 1, iter.previousIndex());
            Object o = iter.next();
            assertEquals("Iterator returned correct element", list.get(i), o);
            i++;
        }

        assertTrue("Iterator shouldn't have next", !iter.hasNext());
        assertEquals("nextIndex should be size", max, iter.nextIndex());
        assertEquals("previousIndex should be size - 1", max - 1, iter.previousIndex());

        try {
            iter.next();
            fail("Exhausted iterator should raise NoSuchElement");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    /**
     *  Traverses to the beginning of the given iterator.
     *
     *  @param iter  the iterator to traverse
     *  @param i     the starting index
     */
    private void backwardTest(ListIterator<E> iter, int i) {
        List<E> list = getCollection();

        while (i > 0) {
            assertTrue("Iterator should have previous, i:" + i,
                iter.hasPrevious());
            assertEquals("Iterator.nextIndex should work, i:" + i,
                i, iter.nextIndex());
            assertEquals("Iterator.previousIndex should work, i:" + i,
                i - 1, iter.previousIndex());
            E o = iter.previous();
            assertEquals("Iterator returned correct element",
                list.get(i - 1), o);
            i--;
        }

        assertTrue("Iterator shouldn't have previous", !iter.hasPrevious());
        int nextIndex = iter.nextIndex();
        assertEquals("nextIndex should be 0", 0, nextIndex);
        int prevIndex = iter.previousIndex();
        assertEquals("previousIndex should be -1", -1, prevIndex);

        try {
            iter.previous();
            fail("Exhausted iterator should raise NoSuchElement");
        } catch (NoSuchElementException e) {
            // expected
        }

    }


    /**
     *  Tests the {@link ListIterator#add(Object)} method of the list
     *  iterator.
     */
    public void testListIteratorAdd() {
        if (!isAddSupported()) return;

        resetEmpty();
        List<E> list1 = getCollection();
        List<E> list2 = getConfirmed();

        E[] elements = getFullElements();
        ListIterator<E> iter1 = list1.listIterator();
        ListIterator<E> iter2 = list2.listIterator();

        for (int i = 0; i < elements.length; i++) {
            iter1.add(elements[i]);
            iter2.add(elements[i]);
            verify();
        }

        resetFull();
        iter1 = getCollection().listIterator();
        iter2 = getConfirmed().listIterator();
        for (int i = 0; i < elements.length; i++) {
            iter1.next();
            iter2.next();
            iter1.add(elements[i]);
            iter2.add(elements[i]);
            verify();
        }
    }

    /**
     *  Tests the {@link ListIterator#set(Object)} method of the list
     *  iterator.
     */
    public void testListIteratorSet() {
        if (!isSetSupported()) return;

        E[] elements = getFullElements();

        resetFull();
        ListIterator<E> iter1 = getCollection().listIterator();
        ListIterator<E> iter2 = getConfirmed().listIterator();
        for (int i = 0; i < elements.length; i++) {
            iter1.next();
            iter2.next();
            iter1.set(elements[i]);
            iter2.set(elements[i]);
            verify();
        }
    }

    @SuppressWarnings("unchecked")
    public void testEmptyListSerialization() throws IOException, ClassNotFoundException {
        List<E> list = makeObject();
        if (!(list instanceof Serializable && isTestSerialization())) return;

        byte[] objekt = writeExternalFormToBytes((Serializable) list);
        List<E> list2 = (List<E>) readExternalFormFromBytes(objekt);

        assertEquals("Both lists are empty", 0, list.size());
        assertEquals("Both lists are empty", 0, list2.size());
    }

    @SuppressWarnings("unchecked")
    public void testFullListSerialization() throws IOException, ClassNotFoundException {
        List<E> list = makeFullCollection();
        int size = getFullElements().length;
        if (!(list instanceof Serializable && isTestSerialization())) return;

        byte[] objekt = writeExternalFormToBytes((Serializable) list);
        List<E> list2 = (List<E>) readExternalFormFromBytes(objekt);

        assertEquals("Both lists are same size", size, list.size());
        assertEquals("Both lists are same size", size, list2.size());
    }

    /**
     * Skip the serialized canonical tests for now.
     *
     * @return true
     *
     * TODO: store new serialized objects in CVS.
     */
    @Override
    protected boolean skipSerializedCanonicalTests() {
        return true;
    }

    /**
     * Compare the current serialized form of the List
     * against the canonical version in SVN.
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
        List<E> list = makeObject();
        if (list instanceof Serializable && !skipSerializedCanonicalTests()
                && isTestSerialization()) {
            List<E> list2 = (List<E>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(list));
            assertEquals("List is empty", 0, list2.size());
            assertEquals(list, list2);
        }
    }

    /**
     * Compare the current serialized form of the List
     * against the canonical version in SVN.
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
        List<E> list = makeFullCollection();
        if(list instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            List<E> list2 = (List<E>) readExternalFormFromDisk(getCanonicalFullCollectionName(list));
            if (list2.size() == 4) {
                // old serialized tests
                return;
            }
            assertEquals("List is the right size",list.size(), list2.size());
            assertEquals(list, list2);
        }
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns a {@link BulkTest} for testing {@link List#subList(int,int)}.
     *  The returned bulk test will run through every <code>TestList</code>
     *  method, <i>including</i> another <code>bulkTestSubList</code>.
     *  Sublists are tested until the size of the sublist is less than 10.
     *  Each sublist is 6 elements smaller than its parent list.
     *  (By default this means that two rounds of sublists will be tested).
     *  The verify() method is overloaded to test that the original list is
     *  modified when the sublist is.
     */
    public BulkTest bulkTestSubList() {
        if (getFullElements().length - 6 < 10) return null;
        return new BulkTestSubList<E>(this);
    }

   public static class BulkTestSubList<E> extends AbstractTestList<E> {

       private AbstractTestList<E> outer;

       public BulkTestSubList(AbstractTestList<E> outer) {
           super("");
           this.outer = outer;
       }

       @Override
       @SuppressWarnings("unchecked")
       public E[] getFullElements() {
           List<E> l = Arrays.asList(outer.getFullElements());
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
           int size = getFullElements().length;
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
           int size = outer.getConfirmed().size();
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
    *  Tests that a sublist raises a {@link java.util.ConcurrentModificationException ConcurrentModificationException}
    *  if elements are added to the original list.
    */
   public void testListSubListFailFastOnAdd() {
       if (!isFailFastSupported()) return;
       if (!isAddSupported()) return;

       resetFull();
       int size = getCollection().size();
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
    *  Tests that a sublist raises a {@link java.util.ConcurrentModificationException ConcurrentModificationException}
    *  if elements are removed from the original list.
    */
   public void testListSubListFailFastOnRemove() {
       if (!isFailFastSupported()) return;
       if (!isRemoveSupported()) return;

       resetFull();
       int size = getCollection().size();
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
    *  Invokes all the methods on the given sublist to make sure they raise
    *  a {@link java.util.ConcurrentModificationException ConcurrentModificationException}.
    */
   protected void failFastAll(List<E> list) {
       Method[] methods = List.class.getMethods();
       for (int i = 0; i < methods.length; i++) {
           failFastMethod(list, methods[i]);
       }
   }

   /**
    *  Invokes the given method on the given sublist to make sure it raises
    *  a {@link java.util.ConcurrentModificationException ConcurrentModificationException}.
    *
    *  Unless the method happens to be the equals() method, in which case
    *  the test is skipped.  There seems to be a bug in
    *  java.util.AbstractList.subList(int,int).equals(Object) -- it never
    *  raises a ConcurrentModificationException.
    *
    *  @param list  the sublist to test
    *  @param m     the method to invoke
    */
   protected void failFastMethod(List<E> list, Method m) {
       if (m.getName().equals("equals")) return;

       E element = getOtherElements()[0];
       Collection<E> c = Collections.singleton(element);

       Class<?>[] types = m.getParameterTypes();
       Object[] params = new Object[types.length];
       for (int i = 0; i < params.length; i++) {
           if (types[i] == Integer.TYPE) params[i] = new Integer(0);
           else if (types[i] == Collection.class) params[i] = c;
           else if (types[i] == Object.class) params[i] = element;
           else if (types[i] == Object[].class) params[i] = new Object[0];
       }

       try {
           m.invoke(list, params);
           fail(m.getName() + " should raise ConcurrentModification");
       } catch (IllegalAccessException e) {
           // impossible
       } catch (InvocationTargetException e) {
           Throwable t = e.getTargetException();
           if (t instanceof ConcurrentModificationException) {
               // expected
               return;
           } else {
               fail(m.getName() + " raised unexpected " + e);
           }
       }
   }

   //-----------------------------------------------------------------------
   public BulkTest bulkTestListIterator() {
       return new TestListIterator();
   }

   public class TestListIterator extends AbstractTestListIterator<E> {
       public TestListIterator() {
           super("TestListIterator");
       }

       @Override
       public E addSetValue() {
           return AbstractTestList.this.getOtherElements()[0];
       }

       @Override
       public boolean supportsRemove() {
           return AbstractTestList.this.isRemoveSupported();
       }

       @Override
       public boolean supportsAdd() {
           return AbstractTestList.this.isAddSupported();
       }

       @Override
       public boolean supportsSet() {
           return AbstractTestList.this.isSetSupported();
       }

       @Override
       public ListIterator<E> makeEmptyIterator() {
           resetEmpty();
           return AbstractTestList.this.getCollection().listIterator();
       }

       @Override
       public ListIterator<E> makeObject() {
           resetFull();
           return AbstractTestList.this.getCollection().listIterator();
       }
   }

}
