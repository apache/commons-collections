/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestCursorableLinkedList.java,v 1.1 2001/04/14 15:39:44 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 15:39:44 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.collections;

import junit.framework.*;
import java.util.*;

/**
 * @author Rodney Waldhoff
 * @version $Id: TestCursorableLinkedList.java,v 1.1 2001/04/14 15:39:44 rwaldhoff Exp $
 */
public class TestCursorableLinkedList extends TestList {
    public TestCursorableLinkedList(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestCursorableLinkedList.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestCursorableLinkedList.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    private CursorableLinkedList list = null;

    public void setUp() {
        list = new CursorableLinkedList();
        setList(list);
    }

    public void testAdd() {
        assertEquals("[]",list.toString());
        assert(list.add(new Integer(1)));
        assertEquals("[1]",list.toString());
        assert(list.add(new Integer(2)));
        assertEquals("[1, 2]",list.toString());
        assert(list.add(new Integer(3)));
        assertEquals("[1, 2, 3]",list.toString());
        assert(list.addFirst(new Integer(0)));
        assertEquals("[0, 1, 2, 3]",list.toString());
        assert(list.addLast(new Integer(4)));
        assertEquals("[0, 1, 2, 3, 4]",list.toString());
        list.add(0,new Integer(-2));
        assertEquals("[-2, 0, 1, 2, 3, 4]",list.toString());
        list.add(1,new Integer(-1));
        assertEquals("[-2, -1, 0, 1, 2, 3, 4]",list.toString());
        list.add(7,new Integer(5));
        assertEquals("[-2, -1, 0, 1, 2, 3, 4, 5]",list.toString());

        java.util.List list2 = new java.util.LinkedList();
        list2.add("A");
        list2.add("B");
        list2.add("C");

        assert(list.addAll(list2));
        assertEquals("[-2, -1, 0, 1, 2, 3, 4, 5, A, B, C]",list.toString());
        assert(list.addAll(3,list2));
        assertEquals("[-2, -1, 0, A, B, C, 1, 2, 3, 4, 5, A, B, C]",list.toString());
    }

    public void testClear() {
        assertEquals(0,list.size());
        assert(list.isEmpty());
        list.clear();
        assertEquals(0,list.size());
        assert(list.isEmpty());

        list.add("element");
        assertEquals(1,list.size());
        assert(!list.isEmpty());

        list.clear();
        assertEquals(0,list.size());
        assert(list.isEmpty());

        list.add("element1");
        list.add("element2");
        assertEquals(2,list.size());
        assert(!list.isEmpty());

        list.clear();
        assertEquals(0,list.size());
        assert(list.isEmpty());

        for(int i=0;i<1000;i++) {
            list.add(new Integer(i));
        }
        assertEquals(1000,list.size());
        assert(!list.isEmpty());

        list.clear();
        assertEquals(0,list.size());
        assert(list.isEmpty());
    }

    public void testContains() {
        assert(!list.contains("A"));
        assert(list.add("A"));
        assert(list.contains("A"));
        assert(list.add("B"));
        assert(list.contains("A"));
        assert(list.addFirst("a"));
        assert(list.contains("A"));
        assert(list.remove("a"));
        assert(list.contains("A"));
        assert(list.remove("A"));
        assert(!list.contains("A"));
    }

    public void testContainsAll() {
        assert(list.containsAll(list));
        java.util.List list2 = new java.util.LinkedList();
        assert(list.containsAll(list2));
        list2.add("A");
        assert(!list.containsAll(list2));
        list.add("B");
        list.add("A");
        assert(list.containsAll(list2));
        list2.add("B");
        assert(list.containsAll(list2));
        list2.add("C");
        assert(!list.containsAll(list2));
        list.add("C");
        assert(list.containsAll(list2));
        list2.add("C");
        assert(list.containsAll(list2));
        assert(list.containsAll(list));
    }

    public void testCursorNavigation() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        CursorableLinkedList.Cursor it = list.cursor();
        assert(it.hasNext());
        assert(!it.hasPrevious());
        assertEquals("1",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("1",it.previous());
        assert(it.hasNext());
        assert(!it.hasPrevious());
        assertEquals("1",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("2",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("2",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("2",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("3",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("4",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("5",it.next());
        assert(!it.hasNext());
        assert(it.hasPrevious());
        assertEquals("5",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("4",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("3",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("2",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals("1",it.previous());
        assert(it.hasNext());
        assert(!it.hasPrevious());
        it.close();
    }

    public void testCursorSet() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        CursorableLinkedList.Cursor it = list.cursor();
        assertEquals("1",it.next());
        it.set("a");
        assertEquals("a",it.previous());
        it.set("A");
        assertEquals("A",it.next());
        assertEquals("2",it.next());
        it.set("B");
        assertEquals("3",it.next());
        assertEquals("4",it.next());
        it.set("D");
        assertEquals("5",it.next());
        it.set("E");
        assertEquals("[A, B, 3, D, E]",list.toString());
        it.close();
    }

    public void testCursorRemove() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        CursorableLinkedList.Cursor it = list.cursor();
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
        assert(!it.hasPrevious());
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
        it.close();
    }

    public void testCursorAdd() {
        CursorableLinkedList.Cursor it = list.cursor();
        it.add("1");
        assertEquals("[1]",list.toString());
        it.add("3");
        assertEquals("[1, 3]",list.toString());
        it.add("5");
        assertEquals("[1, 3, 5]",list.toString());
        assertEquals("5",it.previous());
        it.add("4");
        assertEquals("[1, 3, 4, 5]",list.toString());
        assertEquals("4",it.previous());
        assertEquals("3",it.previous());
        it.add("2");
        assertEquals("[1, 2, 3, 4, 5]",list.toString());
        it.close();
    }

    public void testEqualsAndHashCode() {
        assert(list.equals(list));
        assertEquals(list.hashCode(),list.hashCode());
        list.add("A");
        assert(list.equals(list));
        assertEquals(list.hashCode(),list.hashCode());

        CursorableLinkedList list2 = new CursorableLinkedList();
        assert(!list.equals(list2));
        assert(!list2.equals(list));

        java.util.List list3 = new java.util.LinkedList();
        assert(!list.equals(list3));
        assert(!list3.equals(list));
        assert(list2.equals(list3));
        assert(list3.equals(list2));
        assertEquals(list2.hashCode(),list3.hashCode());

        list2.add("A");
        assert(list.equals(list2));
        assert(list2.equals(list));
        assert(!list2.equals(list3));
        assert(!list3.equals(list2));

        list3.add("A");
        assert(list2.equals(list3));
        assert(list3.equals(list2));
        assertEquals(list2.hashCode(),list3.hashCode());

        list.add("B");
        assert(list.equals(list));
        assert(!list.equals(list2));
        assert(!list2.equals(list));
        assert(!list.equals(list3));
        assert(!list3.equals(list));

        list2.add("B");
        list3.add("B");
        assert(list.equals(list));
        assert(list.equals(list2));
        assert(list2.equals(list));
        assert(list2.equals(list3));
        assert(list3.equals(list2));
        assertEquals(list2.hashCode(),list3.hashCode());

        list.add("C");
        list2.add("C");
        list3.add("C");
        assert(list.equals(list));
        assert(list.equals(list2));
        assert(list2.equals(list));
        assert(list2.equals(list3));
        assert(list3.equals(list2));
        assertEquals(list.hashCode(),list2.hashCode());
        assertEquals(list2.hashCode(),list3.hashCode());

        list.add("D");
        list2.addFirst("D");
        assert(list.equals(list));
        assert(!list.equals(list2));
        assert(!list2.equals(list));
    }

    public void testGet() {
        try {
            list.get(0);
            fail("shouldn't get here");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        assert(list.add("A"));
        assertEquals("A",list.get(0));
        assert(list.add("B"));
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

    public void testIndexOf() {
        assertEquals(-1,list.indexOf("A"));
        assertEquals(-1,list.lastIndexOf("A"));
        list.add("A");
        assertEquals(0,list.indexOf("A"));
        assertEquals(0,list.lastIndexOf("A"));
        assertEquals(-1,list.indexOf("B"));
        assertEquals(-1,list.lastIndexOf("B"));
        list.add("B");
        assertEquals(0,list.indexOf("A"));
        assertEquals(0,list.lastIndexOf("A"));
        assertEquals(1,list.indexOf("B"));
        assertEquals(1,list.lastIndexOf("B"));
        list.addFirst("B");
        assertEquals(1,list.indexOf("A"));
        assertEquals(1,list.lastIndexOf("A"));
        assertEquals(0,list.indexOf("B"));
        assertEquals(2,list.lastIndexOf("B"));
    }

    public void testIsEmpty() {
        assert(list.isEmpty());
        list.add("element");
        assert(!list.isEmpty());
        list.remove("element");
        assert(list.isEmpty());
        list.add("element");
        assert(!list.isEmpty());
        list.clear();
        assert(list.isEmpty());
    }

    public void testIterator() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        Iterator it = list.iterator();
        assert(it.hasNext());
        assertEquals("1",it.next());
        assert(it.hasNext());
        assertEquals("2",it.next());
        assert(it.hasNext());
        assertEquals("3",it.next());
        assert(it.hasNext());
        assertEquals("4",it.next());
        assert(it.hasNext());
        assertEquals("5",it.next());
        assert(!it.hasNext());

        it = list.iterator();
        assert(it.hasNext());
        assertEquals("1",it.next());
        it.remove();
        assertEquals("[2, 3, 4, 5]",list.toString());
        assert(it.hasNext());
        assertEquals("2",it.next());
        it.remove();
        assertEquals("[3, 4, 5]",list.toString());
        assert(it.hasNext());
        assertEquals("3",it.next());
        it.remove();
        assertEquals("[4, 5]",list.toString());
        assert(it.hasNext());
        assertEquals("4",it.next());
        it.remove();
        assertEquals("[5]",list.toString());
        assert(it.hasNext());
        assertEquals("5",it.next());
        it.remove();
        assertEquals("[]",list.toString());
        assert(!it.hasNext());
    }

    public void testListIteratorNavigation() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        ListIterator it = list.listIterator();
        assert(it.hasNext());
        assert(!it.hasPrevious());
        assertEquals(-1,it.previousIndex());
        assertEquals(0,it.nextIndex());
        assertEquals("1",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(0,it.previousIndex());
        assertEquals(1,it.nextIndex());
        assertEquals("1",it.previous());
        assert(it.hasNext());
        assert(!it.hasPrevious());
        assertEquals(-1,it.previousIndex());
        assertEquals(0,it.nextIndex());
        assertEquals("1",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(0,it.previousIndex());
        assertEquals(1,it.nextIndex());
        assertEquals("2",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(1,it.previousIndex());
        assertEquals(2,it.nextIndex());
        assertEquals("2",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(0,it.previousIndex());
        assertEquals(1,it.nextIndex());
        assertEquals("2",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(1,it.previousIndex());
        assertEquals(2,it.nextIndex());
        assertEquals("3",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(2,it.previousIndex());
        assertEquals(3,it.nextIndex());
        assertEquals("4",it.next());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(3,it.previousIndex());
        assertEquals(4,it.nextIndex());
        assertEquals("5",it.next());
        assert(!it.hasNext());
        assert(it.hasPrevious());
        assertEquals(4,it.previousIndex());
        assertEquals(5,it.nextIndex());
        assertEquals("5",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(3,it.previousIndex());
        assertEquals(4,it.nextIndex());
        assertEquals("4",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(2,it.previousIndex());
        assertEquals(3,it.nextIndex());
        assertEquals("3",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(1,it.previousIndex());
        assertEquals(2,it.nextIndex());
        assertEquals("2",it.previous());
        assert(it.hasNext());
        assert(it.hasPrevious());
        assertEquals(0,it.previousIndex());
        assertEquals(1,it.nextIndex());
        assertEquals("1",it.previous());
        assert(it.hasNext());
        assert(!it.hasPrevious());
        assertEquals(-1,it.previousIndex());
        assertEquals(0,it.nextIndex());
    }

    public void testListIteratorSet() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        ListIterator it = list.listIterator();
        assertEquals("1",it.next());
        it.set("a");
        assertEquals("a",it.previous());
        it.set("A");
        assertEquals("A",it.next());
        assertEquals("2",it.next());
        it.set("B");
        assertEquals("3",it.next());
        assertEquals("4",it.next());
        it.set("D");
        assertEquals("5",it.next());
        it.set("E");
        assertEquals("[A, B, 3, D, E]",list.toString());
    }

    public void testListIteratorRemove() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        ListIterator it = list.listIterator();
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
        assert(!it.hasPrevious());
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

    public void testListIteratorAdd() {
        ListIterator it = list.listIterator();
        it.add("1");
        assertEquals("[1]",list.toString());
        it.add("3");
        assertEquals("[1, 3]",list.toString());
        it.add("5");
        assertEquals("[1, 3, 5]",list.toString());
        assertEquals("5",it.previous());
        it.add("4");
        assertEquals("[1, 3, 4, 5]",list.toString());
        assertEquals("4",it.previous());
        assertEquals("3",it.previous());
        it.add("2");
        assertEquals("[1, 2, 3, 4, 5]",list.toString());
    }

    public void testRemoveAll() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        HashSet set = new HashSet();
        set.add("A");
        set.add("2");
        set.add("C");
        set.add("4");
        set.add("D");

        assert(list.removeAll(set));
        assertEquals("[1, 3, 5]",list.toString());
        assert(!list.removeAll(set));
    }

    public void testRemoveByIndex() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        assertEquals("[1, 2, 3, 4, 5]",list.toString());
        assertEquals("1",list.remove(0));
        assertEquals("[2, 3, 4, 5]",list.toString());
        assertEquals("3",list.remove(1));
        assertEquals("[2, 4, 5]",list.toString());
        assertEquals("4",list.remove(1));
        assertEquals("[2, 5]",list.toString());
        assertEquals("5",list.remove(1));
        assertEquals("[2]",list.toString());
        assertEquals("2",list.remove(0));
        assertEquals("[]",list.toString());
    }

    public void testRemove() {
        list.add("1");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        assertEquals("[1, 1, 2, 3, 4, 5, 2, 3, 4, 5]",list.toString());
        assert(!list.remove("6"));
        assert(list.remove("5"));
        assertEquals("[1, 1, 2, 3, 4, 2, 3, 4, 5]",list.toString());
        assert(list.remove("5"));
        assertEquals("[1, 1, 2, 3, 4, 2, 3, 4]",list.toString());
        assert(!list.remove("5"));
        assert(list.remove("1"));
        assertEquals("[1, 2, 3, 4, 2, 3, 4]",list.toString());
        assert(list.remove("1"));
        assertEquals("[2, 3, 4, 2, 3, 4]",list.toString());
        assert(list.remove("2"));
        assertEquals("[3, 4, 2, 3, 4]",list.toString());
        assert(list.remove("2"));
        assertEquals("[3, 4, 3, 4]",list.toString());
        assert(list.remove("3"));
        assertEquals("[4, 3, 4]",list.toString());
        assert(list.remove("3"));
        assertEquals("[4, 4]",list.toString());
        assert(list.remove("4"));
        assertEquals("[4]",list.toString());
        assert(list.remove("4"));
        assertEquals("[]",list.toString());
    }

    public void testRetainAll() {
        list.add("1");
        list.add("1");
        list.add("2");
        list.add("2");
        list.add("3");
        list.add("3");
        list.add("4");
        list.add("4");
        list.add("5");
        list.add("5");

        HashSet set = new HashSet();
        set.add("A");
        set.add("2");
        set.add("C");
        set.add("4");
        set.add("D");

        assert(list.retainAll(set));
        assertEquals("[2, 2, 4, 4]",list.toString());
        assert(!list.retainAll(set));
    }

    public void testSet() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        assertEquals("[1, 2, 3, 4, 5]",list.toString());
        list.set(0,"A");
        assertEquals("[A, 2, 3, 4, 5]",list.toString());
        list.set(1,"B");
        assertEquals("[A, B, 3, 4, 5]",list.toString());
        list.set(2,"C");
        assertEquals("[A, B, C, 4, 5]",list.toString());
        list.set(3,"D");
        assertEquals("[A, B, C, D, 5]",list.toString());
        list.set(4,"E");
        assertEquals("[A, B, C, D, E]",list.toString());
    }

    public void testSubList() {
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");

        assertEquals("[A, B, C, D, E]",list.toString());
        assertEquals("[A, B, C, D, E]",list.subList(0,5).toString());
        assertEquals("[B, C, D, E]",list.subList(1,5).toString());
        assertEquals("[C, D, E]",list.subList(2,5).toString());
        assertEquals("[D, E]",list.subList(3,5).toString());
        assertEquals("[E]",list.subList(4,5).toString());
        assertEquals("[]",list.subList(5,5).toString());
    }

    public void testSubListAddEnd() {
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");

        List sublist = list.subList(5,5);
        sublist.add("F");
        assertEquals("[A, B, C, D, E, F]",list.toString());
        assertEquals("[F]",sublist.toString());
        sublist.add("G");
        assertEquals("[A, B, C, D, E, F, G]",list.toString());
        assertEquals("[F, G]",sublist.toString());
    }

    public void testSubListAddBegin() {
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");

        List sublist = list.subList(0,0);
        sublist.add("a");
        assertEquals("[a, A, B, C, D, E]",list.toString());
        assertEquals("[a]",sublist.toString());
        sublist.add("b");
        assertEquals("[a, b, A, B, C, D, E]",list.toString());
        assertEquals("[a, b]",sublist.toString());
    }

    public void testSubListAddMiddle() {
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");

        List sublist = list.subList(1,3);
        sublist.add("a");
        assertEquals("[A, B, C, a, D, E]",list.toString());
        assertEquals("[B, C, a]",sublist.toString());
        sublist.add("b");
        assertEquals("[A, B, C, a, b, D, E]",list.toString());
        assertEquals("[B, C, a, b]",sublist.toString());
    }

    public void testSubListRemove() {
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");

        List sublist = list.subList(1,4);
        assertEquals("[B, C, D]",sublist.toString());
        assertEquals("[A, B, C, D, E]",list.toString());
        sublist.remove("C");
        assertEquals("[B, D]",sublist.toString());
        assertEquals("[A, B, D, E]",list.toString());
        sublist.remove(1);
        assertEquals("[B]",sublist.toString());
        assertEquals("[A, B, E]",list.toString());
        sublist.clear();
        assertEquals("[]",sublist.toString());
        assertEquals("[A, E]",list.toString());
    }

    public void testToArray() {
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        Object[] elts = list.toArray();
        assertEquals("1",elts[0]);
        assertEquals("2",elts[1]);
        assertEquals("3",elts[2]);
        assertEquals("4",elts[3]);
        assertEquals("5",elts[4]);
        assertEquals(5,elts.length);

        String[] elts2 = (String[])(list.toArray(new String[0]));
        assertEquals("1",elts2[0]);
        assertEquals("2",elts2[1]);
        assertEquals("3",elts2[2]);
        assertEquals("4",elts2[3]);
        assertEquals("5",elts2[4]);
        assertEquals(5,elts2.length);

        String[] elts3 = new String[5];
        assertSame(elts3,list.toArray(elts3));
        assertEquals("1",elts3[0]);
        assertEquals("2",elts3[1]);
        assertEquals("3",elts3[2]);
        assertEquals("4",elts3[3]);
        assertEquals("5",elts3[4]);
        assertEquals(5,elts3.length);

        String[] elts4 = new String[3];
        String[] elts4b = (String[])(list.toArray(elts4));
        assert(elts4 != elts4b);
        assertEquals("1",elts4b[0]);
        assertEquals("2",elts4b[1]);
        assertEquals("3",elts4b[2]);
        assertEquals("4",elts4b[3]);
        assertEquals("5",elts4b[4]);
        assertEquals(5,elts4b.length);
    }

    public void testSerialization() throws Exception {
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");

        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(buf);
        out.writeObject(list);
        out.flush();
        out.close();

        java.io.ByteArrayInputStream bufin = new java.io.ByteArrayInputStream(buf.toByteArray());
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(bufin);
        Object list2 = in.readObject();

        assert(list != list2);
        assert(list2.equals(list));
        assert(list.equals(list2));
    }

    public void testLongSerialization() throws Exception {
        // recursive serialization will cause a stack
        // overflow exception with long lists
        for(int i=0;i<10000;i++) {
            list.add(new Integer(i));
        }

        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(buf);
        out.writeObject(list);
        out.flush();
        out.close();

        java.io.ByteArrayInputStream bufin = new java.io.ByteArrayInputStream(buf.toByteArray());
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(bufin);
        Object list2 = in.readObject();

        assert(list != list2);
        assert(list2.equals(list));
        assert(list.equals(list2));
    }

}