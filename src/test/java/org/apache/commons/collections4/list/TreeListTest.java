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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;

/**
 * JUnit tests
 *
 * @since 3.1
 */
public class TreeListTest<E> extends AbstractListTest<E> {

    public TreeListTest(final String name) {
        super(name);
    }

//    public static void main(String[] args) {
//        junit.textui.TestRunner.run(suite());
//        System.out.println("         add; toArray; iterator; insert; get; indexOf; remove");
//        System.out.print("   TreeList = ");
//        benchmark(new TreeList());
//        System.out.print("\n  ArrayList = ");
//        benchmark(new java.util.ArrayList());
//        System.out.print("\n LinkedList = ");
//        benchmark(new java.util.LinkedList());
//        System.out.print("\n NodeCachingLinkedList = ");
//        benchmark(new NodeCachingLinkedList());
//    }

    public static Test suite() {
        return BulkTest.makeSuite(TreeListTest.class);
    }

    public static void benchmark(final List<? super Integer> l) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            l.add(Integer.valueOf(i));
        }
        System.out.print(System.currentTimeMillis() - startMillis + ";");

        startMillis = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            l.toArray();
        }
        System.out.print(System.currentTimeMillis() - startMillis + ";");

        startMillis = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            final java.util.Iterator<? super Integer> it = l.iterator();
            while (it.hasNext()) {
                it.next();
            }
        }
        System.out.print(System.currentTimeMillis() - startMillis + ";");

        startMillis = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            final int j = (int) (Math.random() * 100000);
            l.add(j, Integer.valueOf(-j));
        }
        System.out.print(System.currentTimeMillis() - startMillis + ";");

        startMillis = System.currentTimeMillis();
        for (int i = 0; i < 50000; i++) {
            final int j = (int) (Math.random() * 110000);
            l.get(j);
        }
        System.out.print(System.currentTimeMillis() - startMillis + ";");

        startMillis = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            final int j = (int) (Math.random() * 100000);
            l.indexOf(Integer.valueOf(j));
        }
        System.out.print(System.currentTimeMillis() - startMillis + ";");

        startMillis = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            final int j = (int) (Math.random() * 100000);
            l.remove(j);
        }
        System.out.print(System.currentTimeMillis() - startMillis + ";");
    }

    //-----------------------------------------------------------------------
    @Override
    public TreeList<E> makeObject() {
        return new TreeList<>();
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testAddMultiple() {
        final List<E> l = makeObject();
        l.add((E) "hugo");
        l.add((E) "erna");
        l.add((E) "daniel");
        l.add((E) "andres");
        l.add((E) "harald");
        l.add(0, null);
        assertEquals(null, l.get(0));
        assertEquals("hugo", l.get(1));
        assertEquals("erna", l.get(2));
        assertEquals("daniel", l.get(3));
        assertEquals("andres", l.get(4));
        assertEquals("harald", l.get(5));
    }

    @SuppressWarnings("unchecked")
    public void testRemove() {
        final List<E> l = makeObject();
        l.add((E) "hugo");
        l.add((E) "erna");
        l.add((E) "daniel");
        l.add((E) "andres");
        l.add((E) "harald");
        l.add(0, null);
        int i = 0;
        assertEquals(null, l.get(i++));
        assertEquals("hugo", l.get(i++));
        assertEquals("erna", l.get(i++));
        assertEquals("daniel", l.get(i++));
        assertEquals("andres", l.get(i++));
        assertEquals("harald", l.get(i++));

        l.remove(0);
        i = 0;
        assertEquals("hugo", l.get(i++));
        assertEquals("erna", l.get(i++));
        assertEquals("daniel", l.get(i++));
        assertEquals("andres", l.get(i++));
        assertEquals("harald", l.get(i++));

        i = 0;
        l.remove(1);
        assertEquals("hugo", l.get(i++));
        assertEquals("daniel", l.get(i++));
        assertEquals("andres", l.get(i++));
        assertEquals("harald", l.get(i++));

        i = 0;
        l.remove(2);
        assertEquals("hugo", l.get(i++));
        assertEquals("daniel", l.get(i++));
        assertEquals("harald", l.get(i++));
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

        l.set(1, (E) "0");
        assertEquals(0, l.indexOf("0"));

        l.set(3, (E) "3");
        assertEquals(3, l.indexOf("3"));
        l.set(2, (E) "3");
        assertEquals(2, l.indexOf("3"));
        l.set(1, (E) "3");
        assertEquals(1, l.indexOf("3"));
        l.set(0, (E) "3");
        assertEquals(0, l.indexOf("3"));
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

        final List<Integer> treelist = new TreeList<>();
        treelist.add(Integer.valueOf(0));
        treelist.add(Integer.valueOf(1));
        treelist.add(Integer.valueOf(2));
        treelist.add(Integer.valueOf(3));
        treelist.add(Integer.valueOf(4));

        // this cause inconsistence of ListIterator()
        treelist.remove(objectToRemove);

        final ListIterator<Integer> li = treelist.listIterator();
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
        final List<String> treeList = new TreeList<>();
        treeList.add("A");
        treeList.add("B");
        treeList.add("C");
        treeList.add("D");

        final ListIterator<String> li = treeList.listIterator();
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
        // when initializing the TreeList with another collection

        for (int size = 1; size < 1000; size++) {
            final List<Integer> other = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                other.add(i);
            }
            final TreeList<Integer> l = new TreeList<>(other);
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
        // when calling addAll on the TreeList

        // to simulate different cases in addAll, do different runs where
        // the number of elements already in the list and being added by addAll differ

        final int size = 1000;
        for (int i = 0; i < 100; i++) {
            final List<Integer> other = new ArrayList<>(size);
            for (int j = i; j < size; j++) {
                other.add(j);
            }
            final TreeList<Integer> l = new TreeList<>();
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

}
