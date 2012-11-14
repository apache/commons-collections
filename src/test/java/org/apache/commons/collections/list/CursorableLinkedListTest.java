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

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;

/**
 * Test class.
 *
 * @version $Revision$
 *
 * @author Rodney Waldhoff
 * @author Simon Kitching
 */
public class CursorableLinkedListTest<E> extends AbstractLinkedListTest<E> {
    public CursorableLinkedListTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(CursorableLinkedListTest.class);
    }

    private CursorableLinkedList<E> list;

    @Override
    public void setUp() {
        list = new CursorableLinkedList<E>();
    }

    @Override
    public CursorableLinkedList<E> makeObject() {
        return new CursorableLinkedList<E>();
    }

    @SuppressWarnings("unchecked")
    public void testAdd() {
        assertEquals("[]",list.toString());
        assertTrue(list.add((E) new Integer(1)));
        assertEquals("[1]",list.toString());
        assertTrue(list.add((E) new Integer(2)));
        assertEquals("[1, 2]",list.toString());
        assertTrue(list.add((E) new Integer(3)));
        assertEquals("[1, 2, 3]",list.toString());
        assertTrue(list.addFirst((E) new Integer(0)));
        assertEquals("[0, 1, 2, 3]",list.toString());
        assertTrue(list.addLast((E) new Integer(4)));
        assertEquals("[0, 1, 2, 3, 4]",list.toString());
        list.add(0,(E) new Integer(-2));
        assertEquals("[-2, 0, 1, 2, 3, 4]",list.toString());
        list.add(1,(E) new Integer(-1));
        assertEquals("[-2, -1, 0, 1, 2, 3, 4]",list.toString());
        list.add(7,(E) new Integer(5));
        assertEquals("[-2, -1, 0, 1, 2, 3, 4, 5]",list.toString());

        java.util.List<E> list2 = new java.util.LinkedList<E>();
        list2.add((E) "A");
        list2.add((E) "B");
        list2.add((E) "C");

        assertTrue(list.addAll(list2));
        assertEquals("[-2, -1, 0, 1, 2, 3, 4, 5, A, B, C]",list.toString());
        assertTrue(list.addAll(3,list2));
        assertEquals("[-2, -1, 0, A, B, C, 1, 2, 3, 4, 5, A, B, C]",list.toString());
    }

    @SuppressWarnings("unchecked")
    public void testClear() {
        assertEquals(0,list.size());
        assertTrue(list.isEmpty());
        list.clear();
        assertEquals(0,list.size());
        assertTrue(list.isEmpty());

        list.add((E) "element");
        assertEquals(1,list.size());
        assertTrue(!list.isEmpty());

        list.clear();
        assertEquals(0,list.size());
        assertTrue(list.isEmpty());

        list.add((E) "element1");
        list.add((E) "element2");
        assertEquals(2,list.size());
        assertTrue(!list.isEmpty());

        list.clear();
        assertEquals(0,list.size());
        assertTrue(list.isEmpty());

        for (int i = 0; i < 1000; i++) {
            list.add((E) new Integer(i));
        }
        assertEquals(1000, list.size());
        assertTrue(!list.isEmpty());

        list.clear();
        assertEquals(0,list.size());
        assertTrue(list.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public void testContains() {
        assertTrue(!list.contains("A"));
        assertTrue(list.add((E) "A"));
        assertTrue(list.contains("A"));
        assertTrue(list.add((E) "B"));
        assertTrue(list.contains("A"));
        assertTrue(list.addFirst((E) "a"));
        assertTrue(list.contains("A"));
        assertTrue(list.remove("a"));
        assertTrue(list.contains("A"));
        assertTrue(list.remove("A"));
        assertTrue(!list.contains("A"));
    }

    @SuppressWarnings("unchecked")
    public void testContainsAll() {
        assertTrue(list.containsAll(list));
        java.util.List<E> list2 = new java.util.LinkedList<E>();
        assertTrue(list.containsAll(list2));
        list2.add((E) "A");
        assertTrue(!list.containsAll(list2));
        list.add((E) "B");
        list.add((E) "A");
        assertTrue(list.containsAll(list2));
        list2.add((E) "B");
        assertTrue(list.containsAll(list2));
        list2.add((E) "C");
        assertTrue(!list.containsAll(list2));
        list.add((E) "C");
        assertTrue(list.containsAll(list2));
        list2.add((E) "C");
        assertTrue(list.containsAll(list2));
        assertTrue(list.containsAll(list));
    }

    @SuppressWarnings("unchecked")
    public void testCursorNavigation() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        CursorableLinkedList.Cursor<E> it = list.cursor();
        assertTrue(it.hasNext());
        assertTrue(!it.hasPrevious());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("1", it.previous());
        assertTrue(it.hasNext());
        assertTrue(!it.hasPrevious());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("2", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("3", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("4", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("5", it.next());
        assertTrue(!it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("5", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("4", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("3", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("2", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("1", it.previous());
        assertTrue(it.hasNext());
        assertTrue(!it.hasPrevious());
        it.close();
    }

    @SuppressWarnings("unchecked")
    public void testCursorSet() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        CursorableLinkedList.Cursor<E> it = list.cursor();
        assertEquals("1", it.next());
        it.set((E) "a");
        assertEquals("a", it.previous());
        it.set((E) "A");
        assertEquals("A", it.next());
        assertEquals("2", it.next());
        it.set((E) "B");
        assertEquals("3", it.next());
        assertEquals("4", it.next());
        it.set((E) "D");
        assertEquals("5", it.next());
        it.set((E) "E");
        assertEquals("[A, B, 3, D, E]", list.toString());
        it.close();
    }

    @SuppressWarnings("unchecked")
    public void testCursorRemove() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        CursorableLinkedList.Cursor<E> it = list.cursor();
        try {
            it.remove();
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
        assertEquals("1", it.next());
        assertEquals("2", it.next());
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        it.remove();
        assertEquals("[1, 3, 4, 5]", list.toString());
        assertEquals("3", it.next());
        assertEquals("3", it.previous());
        assertEquals("1", it.previous());
        it.remove();
        assertEquals("[3, 4, 5]", list.toString());
        assertTrue(!it.hasPrevious());
        assertEquals("3", it.next());
        it.remove();
        assertEquals("[4, 5]", list.toString());
        try {
            it.remove();
        } catch (IllegalStateException e) {
            // expected
        }
        assertEquals("4", it.next());
        assertEquals("5", it.next());
        it.remove();
        assertEquals("[4]", list.toString());
        assertEquals("4", it.previous());
        it.remove();
        assertEquals("[]", list.toString());
        it.close();
    }

    @SuppressWarnings("unchecked")
    public void testCursorAdd() {
        CursorableLinkedList.Cursor<E> it = list.cursor();
        it.add((E) "1");
        assertEquals("[1]", list.toString());
        it.add((E) "3");
        assertEquals("[1, 3]", list.toString());
        it.add((E) "5");
        assertEquals("[1, 3, 5]", list.toString());
        assertEquals("5", it.previous());
        it.add((E) "4");
        assertEquals("[1, 3, 4, 5]", list.toString());
        assertEquals("4", it.previous());
        assertEquals("3", it.previous());
        it.add((E) "2");
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        it.close();
    }

    @SuppressWarnings("unchecked")
    public void testCursorConcurrentModification() {
        // this test verifies that cursors remain valid when the list
        // is modified via other means.
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");
        list.add((E) "7");
        list.add((E) "9");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        CursorableLinkedList.Cursor<E> c2 = list.cursor();
        Iterator<E> li = list.iterator();

        // test cursors remain valid when list modified by std Iterator
        // test cursors skip elements removed via ListIterator
        assertEquals("1", li.next());
        assertEquals("2", li.next());
        li.remove();
        assertEquals("3", li.next());
        assertEquals("1", c1.next());
        assertEquals("3", c1.next());
        assertEquals("1", c2.next());

        // test cursor c1 can remove elements from previously modified list
        // test cursor c2 skips elements removed via different cursor
        c1.remove();
        assertEquals("5", c2.next());
        c2.add((E) "6");
        assertEquals("5", c1.next());
        assertEquals("6", c1.next());
        assertEquals("7", c1.next());

        // test cursors remain valid when list mod via CursorableLinkedList
        // test cursor remains valid when elements inserted into list before
        // the current position of the cursor.
        list.add(0, (E) "0");

        // test cursor remains valid when element inserted immediately after
        // current element of a cursor, and the element is seen on the
        // next call to the next method of that cursor.
        list.add(5, (E) "8");

        assertEquals("8", c1.next());
        assertEquals("9", c1.next());
        c1.add((E) "10");
        assertEquals("7", c2.next());
        assertEquals("8", c2.next());
        assertEquals("9", c2.next());
        assertEquals("10", c2.next());

        try {
            c2.next();
            fail();
        } catch (NoSuchElementException nse) {
        }

        try {
            li.next();
            fail();
        } catch (ConcurrentModificationException cme) {
        }

        c1.close(); // not necessary
        c2.close(); // not necessary
    }

    @SuppressWarnings("unchecked")
    public void testCursorNextIndexMid() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        Iterator<E> li = list.iterator();

        // test cursors remain valid when list modified by std Iterator
        // test cursors skip elements removed via ListIterator
        assertEquals("1", li.next());
        assertEquals("2", li.next());
        li.remove();
        assertEquals(0, c1.nextIndex());
        assertEquals("1", c1.next());
        assertEquals(1, c1.nextIndex());
        assertEquals("3", c1.next());
    }

    @SuppressWarnings("unchecked")
    public void testCursorNextIndexFirst() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();

        assertEquals(0, c1.nextIndex());
        list.remove(0);
        assertEquals(0, c1.nextIndex());
        assertEquals("2", c1.next());
        assertEquals(1, c1.nextIndex());
        assertEquals("3", c1.next());
    }

    @SuppressWarnings("unchecked")
    public void testCursorNextIndexAddBefore() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();

        assertEquals(0, c1.nextIndex());
        assertEquals("1", c1.next());
        list.add(0, (E) "0");
        assertEquals(2, c1.nextIndex());
        assertEquals("2", c1.next());
    }

    @SuppressWarnings("unchecked")
    public void testCursorNextIndexAddNext() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();

        assertEquals(0, c1.nextIndex());
        list.add(0, (E) "0");
        assertEquals(0, c1.nextIndex());
        assertEquals("0", c1.next());
        assertEquals(1, c1.nextIndex());
        assertEquals("1", c1.next());
    }

    @SuppressWarnings("unchecked")
    public void testCursorNextIndexAddAfter() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();

        assertEquals(0, c1.nextIndex());
        list.add(1, (E) "0");
        assertEquals(0, c1.nextIndex());
        assertEquals("1", c1.next());
        assertEquals(1, c1.nextIndex());
        assertEquals("0", c1.next());
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextPreviousRemoveIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        assertEquals("B", list.remove(1));

        assertEquals(true, c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals(true, c1.currentRemovedByAnother);
        assertEquals(null, c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextRemoveIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());

        assertEquals("B", list.remove(1));

        assertEquals(true, c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals(false, c1.currentRemovedByAnother);
        assertEquals("A", c1.current.value);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextRemoveIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        assertEquals("B", list.remove(1));

        assertEquals(true, c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals(true, c1.currentRemovedByAnother);
        assertEquals(null, c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextNextRemoveIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("C", c1.next());

        assertEquals("B", list.remove(1));

        assertEquals(false, c1.nextIndexValid);
        assertEquals(false, c1.currentRemovedByAnother);
        assertEquals("C", c1.current.value);
        assertEquals("D", c1.next.value);

        assertEquals("[A, C, D]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, D]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextPreviousRemoveByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        c1.remove();

        assertEquals(true, c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals(false, c1.currentRemovedByAnother);
        assertEquals(null, c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextRemoveByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        c1.remove();

        assertEquals(true, c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals(false, c1.currentRemovedByAnother);
        assertEquals(null, c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextPreviousAddIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        list.add(1, (E) "Z");

        assertEquals(true, c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals("B", c1.current.value);
        assertEquals("Z", c1.next.value);

        assertEquals("[A, Z, B, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, Z, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextAddIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());

        list.add(1, (E) "Z");

        assertEquals(true, c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals("A", c1.current.value);
        assertEquals("Z", c1.next.value);

        assertEquals("[A, Z, B, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[Z, B, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextAddIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        list.add(1, (E) "Z");

        assertEquals(false, c1.nextIndexValid);
        assertEquals("B", c1.current.value);
        assertEquals("C", c1.next.value);

        assertEquals("[A, Z, B, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, Z, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextPreviousAddByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        c1.add((E) "Z");

        assertEquals(true, c1.nextIndexValid);
        assertEquals(2, c1.nextIndex);
        assertEquals(null, c1.current);
        assertEquals("B", c1.next.value);

        assertEquals("[A, Z, B, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextAddByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        c1.add((E) "Z");

        assertEquals(true, c1.nextIndexValid);
        assertEquals(3, c1.nextIndex);
        assertEquals(false, c1.currentRemovedByAnother);
        assertEquals(null, c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, B, Z, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextRemoveByListSetByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        list.remove(1);

        assertEquals(true, c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals(null, c1.current);
        assertEquals("C", c1.next.value);
        assertEquals("[A, C]", list.toString());

        try {
            c1.set((E) "Z");
            fail();
        } catch (IllegalStateException ex) {}
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextPreviousSetByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        c1.set((E) "Z");

        assertEquals(true, c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals("Z", c1.current.value);
        assertEquals("Z", c1.next.value);

        assertEquals("[A, Z, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    @SuppressWarnings("unchecked")
    public void testInternalState_CursorNextNextSetByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        c1.set((E) "Z");

        assertEquals(true, c1.nextIndexValid);
        assertEquals(2, c1.nextIndex);
        assertEquals("Z", c1.current.value);
        assertEquals("C", c1.next.value);

        assertEquals("[A, Z, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, C]", list.toString());
        try {
            c1.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testEqualsAndHashCode() {
        assertTrue(list.equals(list));
        assertEquals(list.hashCode(),list.hashCode());
        list.add((E) "A");
        assertTrue(list.equals(list));
        assertEquals(list.hashCode(),list.hashCode());

        CursorableLinkedList<E> list2 = new CursorableLinkedList<E>();
        assertTrue(!list.equals(list2));
        assertTrue(!list2.equals(list));

        java.util.List<E> list3 = new java.util.LinkedList<E>();
        assertTrue(!list.equals(list3));
        assertTrue(!list3.equals(list));
        assertTrue(list2.equals(list3));
        assertTrue(list3.equals(list2));
        assertEquals(list2.hashCode(),list3.hashCode());

        list2.add((E) "A");
        assertTrue(list.equals(list2));
        assertTrue(list2.equals(list));
        assertTrue(!list2.equals(list3));
        assertTrue(!list3.equals(list2));

        list3.add((E) "A");
        assertTrue(list2.equals(list3));
        assertTrue(list3.equals(list2));
        assertEquals(list2.hashCode(),list3.hashCode());

        list.add((E) "B");
        assertTrue(list.equals(list));
        assertTrue(!list.equals(list2));
        assertTrue(!list2.equals(list));
        assertTrue(!list.equals(list3));
        assertTrue(!list3.equals(list));

        list2.add((E) "B");
        list3.add((E) "B");
        assertTrue(list.equals(list));
        assertTrue(list.equals(list2));
        assertTrue(list2.equals(list));
        assertTrue(list2.equals(list3));
        assertTrue(list3.equals(list2));
        assertEquals(list2.hashCode(),list3.hashCode());

        list.add((E) "C");
        list2.add((E) "C");
        list3.add((E) "C");
        assertTrue(list.equals(list));
        assertTrue(list.equals(list2));
        assertTrue(list2.equals(list));
        assertTrue(list2.equals(list3));
        assertTrue(list3.equals(list2));
        assertEquals(list.hashCode(),list2.hashCode());
        assertEquals(list2.hashCode(),list3.hashCode());

        list.add((E) "D");
        list2.addFirst((E) "D");
        assertTrue(list.equals(list));
        assertTrue(!list.equals(list2));
        assertTrue(!list2.equals(list));
    }

    @SuppressWarnings("unchecked")
    public void testGet() {
        try {
            list.get(0);
            fail("shouldn't get here");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        assertTrue(list.add((E) "A"));
        assertEquals("A",list.get(0));
        assertTrue(list.add((E) "B"));
        assertEquals("A",list.get(0));
        assertEquals("B",list.get(1));

        try {
            list.get(-1);
            fail("shouldn't get here");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(2);
            fail("shouldn't get here");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testIndexOf() {
        assertEquals(-1,list.indexOf("A"));
        assertEquals(-1,list.lastIndexOf("A"));
        list.add((E) "A");
        assertEquals(0,list.indexOf("A"));
        assertEquals(0,list.lastIndexOf("A"));
        assertEquals(-1,list.indexOf("B"));
        assertEquals(-1,list.lastIndexOf("B"));
        list.add((E) "B");
        assertEquals(0,list.indexOf("A"));
        assertEquals(0,list.lastIndexOf("A"));
        assertEquals(1,list.indexOf("B"));
        assertEquals(1,list.lastIndexOf("B"));
        list.addFirst((E) "B");
        assertEquals(1,list.indexOf("A"));
        assertEquals(1,list.lastIndexOf("A"));
        assertEquals(0,list.indexOf("B"));
        assertEquals(2,list.lastIndexOf("B"));
    }

    @SuppressWarnings("unchecked")
    public void testIsEmpty() {
        assertTrue(list.isEmpty());
        list.add((E) "element");
        assertTrue(!list.isEmpty());
        list.remove("element");
        assertTrue(list.isEmpty());
        list.add((E) "element");
        assertTrue(!list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public void testIterator() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        Iterator<E> it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertEquals("3", it.next());
        assertTrue(it.hasNext());
        assertEquals("4", it.next());
        assertTrue(it.hasNext());
        assertEquals("5", it.next());
        assertTrue(!it.hasNext());

        it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("1", it.next());
        it.remove();
        assertEquals("[2, 3, 4, 5]", list.toString());
        assertTrue(it.hasNext());
        assertEquals("2", it.next());
        it.remove();
        assertEquals("[3, 4, 5]", list.toString());
        assertTrue(it.hasNext());
        assertEquals("3", it.next());
        it.remove();
        assertEquals("[4, 5]", list.toString());
        assertTrue(it.hasNext());
        assertEquals("4", it.next());
        it.remove();
        assertEquals("[5]", list.toString());
        assertTrue(it.hasNext());
        assertEquals("5", it.next());
        it.remove();
        assertEquals("[]", list.toString());
        assertTrue(!it.hasNext());
    }

    @SuppressWarnings("unchecked")
    public void testListIteratorNavigation() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        ListIterator<E> it = list.listIterator();
        assertTrue(it.hasNext());
        assertTrue(!it.hasPrevious());
        assertEquals(-1, it.previousIndex());
        assertEquals(0, it.nextIndex());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(0, it.previousIndex());
        assertEquals(1, it.nextIndex());
        assertEquals("1", it.previous());
        assertTrue(it.hasNext());
        assertTrue(!it.hasPrevious());
        assertEquals(-1, it.previousIndex());
        assertEquals(0, it.nextIndex());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(0, it.previousIndex());
        assertEquals(1, it.nextIndex());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(1, it.previousIndex());
        assertEquals(2, it.nextIndex());
        assertEquals("2", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(0, it.previousIndex());
        assertEquals(1, it.nextIndex());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(1, it.previousIndex());
        assertEquals(2, it.nextIndex());
        assertEquals("3", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(2, it.previousIndex());
        assertEquals(3, it.nextIndex());
        assertEquals("4", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(3, it.previousIndex());
        assertEquals(4, it.nextIndex());
        assertEquals("5", it.next());
        assertTrue(!it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(4, it.previousIndex());
        assertEquals(5, it.nextIndex());
        assertEquals("5", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(3, it.previousIndex());
        assertEquals(4, it.nextIndex());
        assertEquals("4", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(2, it.previousIndex());
        assertEquals(3, it.nextIndex());
        assertEquals("3", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(1, it.previousIndex());
        assertEquals(2, it.nextIndex());
        assertEquals("2", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(0, it.previousIndex());
        assertEquals(1, it.nextIndex());
        assertEquals("1", it.previous());
        assertTrue(it.hasNext());
        assertTrue(!it.hasPrevious());
        assertEquals(-1, it.previousIndex());
        assertEquals(0, it.nextIndex());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void testListIteratorSet() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        ListIterator<E> it = list.listIterator();
        assertEquals("1", it.next());
        it.set((E) "a");
        assertEquals("a", it.previous());
        it.set((E) "A");
        assertEquals("A", it.next());
        assertEquals("2", it.next());
        it.set((E) "B");
        assertEquals("3", it.next());
        assertEquals("4", it.next());
        it.set((E) "D");
        assertEquals("5", it.next());
        it.set((E) "E");
        assertEquals("[A, B, 3, D, E]", list.toString());
    }

    @SuppressWarnings("unchecked")
    public void testListIteratorRemove() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        ListIterator<E> it = list.listIterator();
        try {
            it.remove();
        } catch(IllegalStateException e) {
            // expected
        }
        assertEquals("1",it.next());
        assertEquals("2",it.next());
        assertEquals("[1, 2, 3, 4, 5]",list.toString());
        it.remove();
        assertEquals("[1, 3, 4, 5]",list.toString());
        assertEquals("3",it.next());
        assertEquals("3",it.previous());
        assertEquals("1",it.previous());
        it.remove();
        assertEquals("[3, 4, 5]",list.toString());
        assertTrue(!it.hasPrevious());
        assertEquals("3",it.next());
        it.remove();
        assertEquals("[4, 5]",list.toString());
        try {
            it.remove();
        } catch(IllegalStateException e) {
            // expected
        }
        assertEquals("4",it.next());
        assertEquals("5",it.next());
        it.remove();
        assertEquals("[4]",list.toString());
        assertEquals("4",it.previous());
        it.remove();
        assertEquals("[]",list.toString());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void testListIteratorAdd() {
        ListIterator<E> it = list.listIterator();
        it.add((E) "1");
        assertEquals("[1]", list.toString());
        it.add((E) "3");
        assertEquals("[1, 3]", list.toString());
        it.add((E) "5");
        assertEquals("[1, 3, 5]", list.toString());
        assertEquals("5", it.previous());
        it.add((E) "4");
        assertEquals("[1, 3, 4, 5]", list.toString());
        assertEquals("4", it.previous());
        assertEquals("3", it.previous());
        it.add((E) "2");
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
    }

    @SuppressWarnings("unchecked")
    public void testRemoveAll() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        HashSet<E> set = new HashSet<E>();
        set.add((E) "A");
        set.add((E) "2");
        set.add((E) "C");
        set.add((E) "4");
        set.add((E) "D");

        assertTrue(list.removeAll(set));
        assertEquals("[1, 3, 5]", list.toString());
        assertTrue(!list.removeAll(set));
    }

    @SuppressWarnings("unchecked")
    public void testRemoveByIndex() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        assertEquals("1", list.remove(0));
        assertEquals("[2, 3, 4, 5]", list.toString());
        assertEquals("3", list.remove(1));
        assertEquals("[2, 4, 5]", list.toString());
        assertEquals("4", list.remove(1));
        assertEquals("[2, 5]", list.toString());
        assertEquals("5", list.remove(1));
        assertEquals("[2]", list.toString());
        assertEquals("2", list.remove(0));
        assertEquals("[]", list.toString());
    }

    @SuppressWarnings("unchecked")
    public void testRemove() {
        list.add((E) "1");
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        assertEquals("[1, 1, 2, 3, 4, 5, 2, 3, 4, 5]", list.toString());
        assertTrue(!list.remove("6"));
        assertTrue(list.remove("5"));
        assertEquals("[1, 1, 2, 3, 4, 2, 3, 4, 5]", list.toString());
        assertTrue(list.remove("5"));
        assertEquals("[1, 1, 2, 3, 4, 2, 3, 4]", list.toString());
        assertTrue(!list.remove("5"));
        assertTrue(list.remove("1"));
        assertEquals("[1, 2, 3, 4, 2, 3, 4]", list.toString());
        assertTrue(list.remove("1"));
        assertEquals("[2, 3, 4, 2, 3, 4]", list.toString());
        assertTrue(list.remove("2"));
        assertEquals("[3, 4, 2, 3, 4]", list.toString());
        assertTrue(list.remove("2"));
        assertEquals("[3, 4, 3, 4]", list.toString());
        assertTrue(list.remove("3"));
        assertEquals("[4, 3, 4]", list.toString());
        assertTrue(list.remove("3"));
        assertEquals("[4, 4]", list.toString());
        assertTrue(list.remove("4"));
        assertEquals("[4]", list.toString());
        assertTrue(list.remove("4"));
        assertEquals("[]", list.toString());
    }

    @SuppressWarnings("unchecked")
    public void testRetainAll() {
        list.add((E) "1");
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "4");
        list.add((E) "5");
        list.add((E) "5");

        HashSet<E> set = new HashSet<E>();
        set.add((E) "A");
        set.add((E) "2");
        set.add((E) "C");
        set.add((E) "4");
        set.add((E) "D");

        assertTrue(list.retainAll(set));
        assertEquals("[2, 2, 4, 4]", list.toString());
        assertTrue(!list.retainAll(set));
    }

    @SuppressWarnings("unchecked")
    public void testSet() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        list.set(0, (E) "A");
        assertEquals("[A, 2, 3, 4, 5]", list.toString());
        list.set(1, (E) "B");
        assertEquals("[A, B, 3, 4, 5]", list.toString());
        list.set(2, (E) "C");
        assertEquals("[A, B, C, 4, 5]", list.toString());
        list.set(3, (E) "D");
        assertEquals("[A, B, C, D, 5]", list.toString());
        list.set(4, (E) "E");
        assertEquals("[A, B, C, D, E]", list.toString());
    }

    @SuppressWarnings("unchecked")
    public void testSubList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        assertEquals("[A, B, C, D, E]", list.toString());
        assertEquals("[A, B, C, D, E]", list.subList(0, 5).toString());
        assertEquals("[B, C, D, E]", list.subList(1, 5).toString());
        assertEquals("[C, D, E]", list.subList(2, 5).toString());
        assertEquals("[D, E]", list.subList(3, 5).toString());
        assertEquals("[E]", list.subList(4, 5).toString());
        assertEquals("[]", list.subList(5, 5).toString());
    }

    @SuppressWarnings("unchecked")
    public void testSubListAddEnd() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        List<E> sublist = list.subList(5, 5);
        sublist.add((E) "F");
        assertEquals("[A, B, C, D, E, F]", list.toString());
        assertEquals("[F]", sublist.toString());
        sublist.add((E) "G");
        assertEquals("[A, B, C, D, E, F, G]", list.toString());
        assertEquals("[F, G]", sublist.toString());
    }

    @SuppressWarnings("unchecked")
    public void testSubListAddBegin() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        List<E> sublist = list.subList(0, 0);
        sublist.add((E) "a");
        assertEquals("[a, A, B, C, D, E]", list.toString());
        assertEquals("[a]", sublist.toString());
        sublist.add((E) "b");
        assertEquals("[a, b, A, B, C, D, E]", list.toString());
        assertEquals("[a, b]", sublist.toString());
    }

    @SuppressWarnings("unchecked")
    public void testSubListAddMiddle() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        List<E> sublist = list.subList(1, 3);
        sublist.add((E) "a");
        assertEquals("[A, B, C, a, D, E]", list.toString());
        assertEquals("[B, C, a]", sublist.toString());
        sublist.add((E) "b");
        assertEquals("[A, B, C, a, b, D, E]", list.toString());
        assertEquals("[B, C, a, b]", sublist.toString());
    }

    @SuppressWarnings("unchecked")
    public void testSubListRemove() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        List<E> sublist = list.subList(1, 4);
        assertEquals("[B, C, D]", sublist.toString());
        assertEquals("[A, B, C, D, E]", list.toString());
        sublist.remove("C");
        assertEquals("[B, D]", sublist.toString());
        assertEquals("[A, B, D, E]", list.toString());
        sublist.remove(1);
        assertEquals("[B]", sublist.toString());
        assertEquals("[A, B, E]", list.toString());
        sublist.clear();
        assertEquals("[]", sublist.toString());
        assertEquals("[A, E]", list.toString());
    }

    @SuppressWarnings("unchecked")
    public void testToArray() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        Object[] elts = list.toArray();
        assertEquals("1", elts[0]);
        assertEquals("2", elts[1]);
        assertEquals("3", elts[2]);
        assertEquals("4", elts[3]);
        assertEquals("5", elts[4]);
        assertEquals(5, elts.length);

        String[] elts2 = (list.toArray(new String[0]));
        assertEquals("1", elts2[0]);
        assertEquals("2", elts2[1]);
        assertEquals("3", elts2[2]);
        assertEquals("4", elts2[3]);
        assertEquals("5", elts2[4]);
        assertEquals(5, elts2.length);

        String[] elts3 = new String[5];
        assertSame(elts3, list.toArray(elts3));
        assertEquals("1", elts3[0]);
        assertEquals("2", elts3[1]);
        assertEquals("3", elts3[2]);
        assertEquals("4", elts3[3]);
        assertEquals("5", elts3[4]);
        assertEquals(5, elts3.length);

        String[] elts4 = new String[3];
        String[] elts4b = (list.toArray(elts4));
        assertTrue(elts4 != elts4b);
        assertEquals("1", elts4b[0]);
        assertEquals("2", elts4b[1]);
        assertEquals("3", elts4b[2]);
        assertEquals("4", elts4b[3]);
        assertEquals("5", elts4b[4]);
        assertEquals(5, elts4b.length);
    }

    @SuppressWarnings("unchecked")
    public void testSerialization() throws Exception {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(buf);
        out.writeObject(list);
        out.flush();
        out.close();

        java.io.ByteArrayInputStream bufin = new java.io.ByteArrayInputStream(buf.toByteArray());
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(bufin);
        Object list2 = in.readObject();

        assertTrue(list != list2);
        assertTrue(list2.equals(list));
        assertTrue(list.equals(list2));
    }

    @SuppressWarnings("unchecked")
    public void testSerializationWithOpenCursor() throws Exception {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(buf);
        out.writeObject(list);
        out.flush();
        out.close();

        java.io.ByteArrayInputStream bufin = new java.io.ByteArrayInputStream(buf.toByteArray());
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(bufin);
        Object list2 = in.readObject();

        assertTrue(list != list2);
        assertTrue(list2.equals(list));
        assertTrue(list.equals(list2));
    }

    @SuppressWarnings("unchecked")
    public void testLongSerialization() throws Exception {
        // recursive serialization will cause a stack
        // overflow exception with long lists
        for (int i = 0; i < 10000; i++) {
            list.add((E) new Integer(i));
        }

        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(buf);
        out.writeObject(list);
        out.flush();
        out.close();

        java.io.ByteArrayInputStream bufin = new java.io.ByteArrayInputStream(buf.toByteArray());
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(bufin);
        Object list2 = in.readObject();

        assertTrue(list != list2);
        assertTrue(list2.equals(list));
        assertTrue(list.equals(list2));
    }

    /**
     *  Ignore the serialization tests for sublists and sub-sublists.
     *
     *  @return an array of sublist serialization test names
     */
    @Override
    public String[] ignoredTests() {
        ArrayList<String> list = new ArrayList<String>();
        String prefix = "CursorableLinkedListTest";
        String bulk = ".bulkTestSubList";
        String[] ignored = new String[] {
                ".testEmptyListSerialization",
                ".testFullListSerialization",
                ".testEmptyListCompatibility",
                ".testFullListCompatibility",
                ".testSimpleSerialization",
                ".testCanonicalEmptyCollectionExists",
                ".testCanonicalFullCollectionExists",
                ".testSerializeDeserializeThenCompare"
        };
        for (int i = 0; i < ignored.length; i++) {
            list.add(prefix + bulk + ignored[i]);
            list.add(prefix + bulk + bulk + ignored[i]);
        }
        return list.toArray(new String[0]);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.0";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "C:/commons/collections/data/test/CursorableLinkedList.emptyCollection.version4.0.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "C:/commons/collections/data/test/CursorableLinkedList.fullCollection.version4.0.obj");
//    }

}
