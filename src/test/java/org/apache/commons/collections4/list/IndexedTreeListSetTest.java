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

import junit.framework.Test;
import org.apache.commons.collections4.BulkTest;

import java.util.*;

/**
 * JUnit tests
 *
 * @since 3.1
 */
public class IndexedTreeListSetTest<E> extends AbstractListTest<E> {

    public IndexedTreeListSetTest(final String name) {
        super(name);
    }

//    public static void main(String[] args) {
//        junit.textui.TestRunner.run(suite());
//        System.out.println("         add; toArray; iterator; insert; get; indexOf; remove");
//        System.out.print("   TreeListSet = ");
//        benchmark(new TreeListSet());
//        System.out.print("\n  ArrayList = ");
//        benchmark(new java.util.ArrayList());
//        System.out.print("\n LinkedList = ");
//        benchmark(new java.util.LinkedList());
//        System.out.print("\n NodeCachingLinkedList = ");
//        benchmark(new NodeCachingLinkedList());
//    }

    public static Test suite() {
        return BulkTest.makeSuite(IndexedTreeListSetTest.class);
    }

    public static void benchmark(final List<? super Integer> l) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            l.add(Integer.valueOf(i));
        }
        System.out.print(System.currentTimeMillis() - start + ";");

        start = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            l.toArray();
        }
        System.out.print(System.currentTimeMillis() - start + ";");

        start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            final java.util.Iterator<? super Integer> it = l.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
        System.out.print(System.currentTimeMillis() - start + ";");

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            final int j = (int) (Math.random() * 100000);
            l.add(j, Integer.valueOf(-j));
        }
        System.out.print(System.currentTimeMillis() - start + ";");

        start = System.currentTimeMillis();
        for (int i = 0; i < 50000; i++) {
            final int j = (int) (Math.random() * 110000);
            l.get(j);
        }
        System.out.print(System.currentTimeMillis() - start + ";");

        start = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            final int j = (int) (Math.random() * 100000);
            l.indexOf(Integer.valueOf(j));
        }
        System.out.print(System.currentTimeMillis() - start + ";");

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            final int j = (int) (Math.random() * 100000);
            l.remove(j);
        }
        System.out.print(System.currentTimeMillis() - start + ";");
    }

    //-----------------------------------------------------------------------
    @Override
    public IndexedTreeListSet<E> makeObject() {
        return new IndexedTreeListSet<>();
    }

    @Override
    public boolean isNullSupported() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] getFullNonNullElements() {
        // override to avoid duplicate "One"
        return (E[]) new Object[] {
                new String(""),
                new String("One"),
                Integer.valueOf(2),
                "Three",
                Integer.valueOf(4),
                new Double(5),
                new Float(6),
                "Seven",
                "Eight",
                new String("Nine"),
                Integer.valueOf(10),
                new Short((short)11),
                new Long(12),
                "Thirteen",
                "14",
                "15",
                new Byte((byte)16)
        };
    }

    @Override
    public void testListIteratorAdd() {
        // Does not support ListIterator.add(obj)
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testAddMultiple() {
        final List<E> l = makeObject();
        l.add((E) "hugo");
        l.add((E) "erna");
        l.add((E) "daniel");
        l.add((E) "andres");
        l.add(0, (E) "harald");
        assertEquals("harald", l.get(0));
        assertEquals("hugo", l.get(1));
        assertEquals("erna", l.get(2));
        assertEquals("daniel", l.get(3));
        assertEquals("andres", l.get(4));
    }

    @SuppressWarnings("unchecked")
    public void testRemove() {
        final List<E> l = makeObject();
        l.add((E) "hugo");
        l.add((E) "erna");
        l.add((E) "daniel");
        l.add((E) "andres");
        l.add(0, (E) "harald");
        int i = 0;
        assertEquals("harald", l.get(i++));
        assertEquals("hugo", l.get(i++));
        assertEquals("erna", l.get(i++));
        assertEquals("daniel", l.get(i++));
        assertEquals("andres", l.get(i++));

        l.remove(0);
        i = 0;
        assertEquals("hugo", l.get(i++));
        assertEquals("erna", l.get(i++));
        assertEquals("daniel", l.get(i++));
        assertEquals("andres", l.get(i++));

        i = 0;
        l.remove(1);
        assertEquals("hugo", l.get(i++));
        assertEquals("daniel", l.get(i++));
        assertEquals("andres", l.get(i++));

        i = 0;
        l.remove(2);
        assertEquals("hugo", l.get(i++));
        assertEquals("daniel", l.get(i++));
    }

    @SuppressWarnings("unchecked")
    public void testInsertBefore() {
        final List<E> l = makeObject();
        l.add((E) "erna");
        l.add(0, (E) "hugo");
        assertEquals("hugo", l.get(0));
        assertEquals("erna", l.get(1));
    }

    @SuppressWarnings("unchecked")
    public void testIndexOf() {
        final List<E> l = makeObject();
        l.add((E) "0");
        l.add((E) "1");
        l.add((E) "2");
        l.add((E) "3");
        l.add((E) "4");
        l.add((E) "5");
        l.add((E) "6");
        assertEquals(0, l.indexOf("0"));
        assertEquals(1, l.indexOf("1"));
        assertEquals(2, l.indexOf("2"));
        assertEquals(3, l.indexOf("3"));
        assertEquals(4, l.indexOf("4"));
        assertEquals(5, l.indexOf("5"));
        assertEquals(6, l.indexOf("6"));
        assertEquals(7, l.size());

        l.set(2, (E) "0"); // Previous "0" at index 0 was removed
        assertEquals(1, l.indexOf("0"));
        assertEquals(-1, l.indexOf("2"));
        assertEquals(0, l.indexOf("1"));
        assertEquals(6, l.size());

        l.set(2, (E) "7");
        assertEquals(-1, l.indexOf("3"));
        assertEquals(2, l.indexOf("7"));
        assertEquals(6, l.size());

        l.set(2, (E) "5"); // Previous "5" at index 4 was removed
        assertEquals(2, l.indexOf("5"));
        assertEquals(4, l.indexOf("6"));
        assertEquals(5, l.size());

        l.remove(4);
        assertEquals(2, l.indexOf("5"));

        l.remove(1);
        assertEquals(1, l.indexOf("5"));
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

        for (int i = 0; i < other.length; i++) {
            final E n = other[i % other.length];
            final E v = getCollection().set(i % elements.length, n);
            assertEquals("Set should return correct element", elements[i], v);
            getConfirmed().set(i % elements.length, n);
            verify();
        }
    }

    @SuppressWarnings("unchecked")
    public void testAddAll() {
        final IndexedTreeListSet<E> lset = new IndexedTreeListSet<>();

        lset.addAll(Arrays.asList((E[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(1)}));

        assertEquals("Duplicate element was added.", 1, lset.size());
    }

    @Override
    public void testCollectionAddAll() {
        // override for set behaviour
        resetEmpty();
        E[] elements = getFullElements();
        boolean r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue("Empty collection should change after addAll", r);
        for (final E element : elements) {
            assertTrue("Collection should contain added element",
                    getCollection().contains(element));
        }

        resetFull();
        final int size = getCollection().size();
        elements = getOtherElements();
        r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue("Full collection should change after addAll", r);
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Full collection should contain added element " + i,
                    getCollection().contains(elements[i]));
        }
        assertEquals("Size should increase after addAll",
                size + elements.length, getCollection().size());
    }

//    public void testCheck() {
//        List l = makeEmptyList();
//        l.add("A1");
//        l.add("A2");
//        l.add("A3");
//        l.add("A4");
//        l.add("A5");
//        l.add("A6");
//    }

    public void testBug35258() {
        final Object objectToRemove = Integer.valueOf(3);

        final List<Integer> TreeListSet = new IndexedTreeListSet<>();
        TreeListSet.add(Integer.valueOf(0));
        TreeListSet.add(Integer.valueOf(1));
        TreeListSet.add(Integer.valueOf(2));
        TreeListSet.add(Integer.valueOf(3));
        TreeListSet.add(Integer.valueOf(4));

        // this cause inconsistence of ListIterator()
        TreeListSet.remove(objectToRemove);

        final ListIterator<Integer> li = TreeListSet.listIterator();
        assertEquals(Integer.valueOf(0), li.next());
        assertEquals(Integer.valueOf(0), li.previous());
        assertEquals(Integer.valueOf(0), li.next());
        assertEquals(Integer.valueOf(1), li.next());
        // this caused error in bug 35258
        assertEquals(Integer.valueOf(1), li.previous());
        assertEquals(Integer.valueOf(1), li.next());
        assertEquals(Integer.valueOf(2), li.next());
        assertEquals(Integer.valueOf(2), li.previous());
        assertEquals(Integer.valueOf(2), li.next());
        assertEquals(Integer.valueOf(4), li.next());
        assertEquals(Integer.valueOf(4), li.previous());
        assertEquals(Integer.valueOf(4), li.next());
        assertEquals(false, li.hasNext());
    }

    public void testBugCollections447() {
        final List<String> TreeListSet = new IndexedTreeListSet<>();
        TreeListSet.add("A");
        TreeListSet.add("B");
        TreeListSet.add("C");
        TreeListSet.add("D");

        final ListIterator<String> li = TreeListSet.listIterator();
        assertEquals("A", li.next());
        assertEquals("B", li.next());

        assertEquals("B", li.previous());

        li.remove(); // Deletes "B"

        // previous() after remove() should move to
        // the element before the one just removed
        assertEquals("A", li.previous());
    }

    @SuppressWarnings("boxing") // OK in test code
    public void testIterationOrder() {
        // COLLECTIONS-433:
        // ensure that the iteration order of elements is correct
        // when initializing the TreeListSet with another collection

        for (int size = 1; size < 1000; size++) {
            final List<Integer> other = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                other.add(i);
            }
            final IndexedTreeListSet<Integer> l = new IndexedTreeListSet<>(other);
            final ListIterator<Integer> it = l.listIterator();
            int i = 0;
            while (it.hasNext()) {
                final Integer val = it.next();
                assertEquals(i++, val.intValue());
            }

            while (it.hasPrevious()) {
                final Integer val = it.previous();
                assertEquals(--i, val.intValue());
            }
        }
    }

    @SuppressWarnings("boxing") // OK in test code
    public void testIterationOrderAfterAddAll() {
        // COLLECTIONS-433:
        // ensure that the iteration order of elements is correct
        // when calling addAll on the TreeListSet

        // to simulate different cases in addAll, do different runs where
        // the number of elements already in the list and being added by addAll differ

        final int size = 1000;
        for (int i = 0; i < 100; i++) {
            final List<Integer> other = new ArrayList<>(size);
            for (int j = i; j < size; j++) {
                other.add(j);
            }
            final IndexedTreeListSet<Integer> l = new IndexedTreeListSet<>();
            for (int j = 0; j < i; j++) {
                l.add(j);
            }

            l.addAll(other);

            final ListIterator<Integer> it = l.listIterator();
            int cnt = 0;
            while (it.hasNext()) {
                final Integer val = it.next();
                assertEquals(cnt++, val.intValue());
            }

            while (it.hasPrevious()) {
                final Integer val = it.previous();
                assertEquals(--cnt, val.intValue());
            }
        }
    }
//
//    /**
//     *  Tests the {@link ListIterator#set(Object)} method of the list
//     *  iterator.
//     */
//    public void testListIteratorSet() {
//        final E[] elements = getOtherElements();
//
//        resetFull();
//        final ListIterator<E> iter1 = getCollection().listIterator();
//        final ListIterator<E> iter2 = getConfirmed().listIterator();
//        for (final E element : elements) {
//            iter1.next();
//            iter2.next();
//            iter1.set(element);
//            iter2.set(element);
//            verify();
//        }
//    }

    //-----------------------------------------------------------------------
    public BulkTest bulkTestListIterator() {
        return new TestListIteratorNoAddSet();
    }

    public class TestListIteratorNoAddSet extends TestListIterator {
        @Override
        public boolean supportsAdd() {
            return false;
        }

        @Override
        public boolean supportsSet() {
            return false;
        }
    }
}
