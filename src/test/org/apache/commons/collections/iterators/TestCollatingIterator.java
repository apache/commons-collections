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
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Comparator;

import org.apache.commons.collections.comparators.ComparableComparator;

/**
 * Unit test suite for {@link CollatingIterator}.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestCollatingIterator extends AbstractTestIterator<Integer> {

    //------------------------------------------------------------ Conventional

    public TestCollatingIterator(String testName) {
        super(testName);
    }

    //--------------------------------------------------------------- Lifecycle

    private Comparator<Integer> comparator = null;
    private ArrayList<Integer> evens = null;
    private ArrayList<Integer> odds = null;
    private ArrayList<Integer> fib = null;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        comparator = new ComparableComparator<Integer>();
        evens = new ArrayList<Integer>();
        odds = new ArrayList<Integer>();
        for (int i = 0; i < 20; i++) {
            if (0 == i % 2) {
                evens.add(i);
            } else {
                odds.add(i);
            }
        }
        fib = new ArrayList<Integer>();
        fib.add(1);
        fib.add(1);
        fib.add(2);
        fib.add(3);
        fib.add(5);
        fib.add(8);
        fib.add(13);
        fib.add(21);
    }

    //---------------------------------------------------- TestIterator Methods

    @Override
    public CollatingIterator<Integer> makeEmptyIterator() {
        return new CollatingIterator<Integer>(comparator);
    }

    @Override
    public CollatingIterator<Integer> makeObject() {
        CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(evens.iterator());
        iter.addIterator(odds.iterator());
        iter.addIterator(fib.iterator());
        return iter;
    }

    //------------------------------------------------------------------- Tests

    public void testGetSetComparator() {
        CollatingIterator<Integer> iter = new CollatingIterator<Integer>();
        assertNull(iter.getComparator());
        iter.setComparator(comparator);
        assertSame(comparator, iter.getComparator());
        iter.setComparator(null);
        assertNull(iter.getComparator());
    }

    public void testIterateEven() {
        CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(evens.iterator());
        for (int i = 0; i < evens.size(); i++) {
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i), iter.next());
            assertEquals(0,iter.getIteratorIndex());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateEvenOdd() {
        CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator, evens.iterator(), odds.iterator());
        for (int i = 0; i < 20; i++) {
            assertTrue(iter.hasNext());
            assertEquals(new Integer(i), iter.next());
            assertEquals(i % 2,iter.getIteratorIndex());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateOddEven() {
        CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator, odds.iterator(), evens.iterator());
        for (int i = 0; i < 20; i++) {
            assertTrue(iter.hasNext());
            assertEquals(new Integer(i),iter.next());
            assertEquals((i % 2) == 0 ? 1 : 0,iter.getIteratorIndex());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateEvenEven() {
        CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(evens.iterator());
        iter.addIterator(evens.iterator());
        for (int i = 0; i < evens.size(); i++) {
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i), iter.next());
            assertEquals(0,iter.getIteratorIndex());
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i), iter.next());
            assertEquals(1,iter.getIteratorIndex());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateFibEvenOdd() {
        CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(fib.iterator());
        iter.addIterator(evens.iterator());
        iter.addIterator(odds.iterator());

        assertEquals(new Integer(0),iter.next());  // even   0
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(1),iter.next());  // fib    1
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(new Integer(1),iter.next());  // fib    1
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(new Integer(1),iter.next());  // odd    1
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(2),iter.next());  // fib    2
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(new Integer(2),iter.next());  // even   2
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(3),iter.next());  // fib    3
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(new Integer(3),iter.next());  // odd    3
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(4),iter.next());  // even   4
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(5),iter.next());  // fib    5
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(new Integer(5),iter.next());  // odd    5
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(6),iter.next());  // even   6
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(7),iter.next());  // odd    7
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(8),iter.next());  // fib    8
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(new Integer(8),iter.next());  // even   8
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(9),iter.next());  // odd    9
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(10),iter.next()); // even  10
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(11),iter.next()); // odd   11
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(12),iter.next()); // even  12
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(13),iter.next()); // fib   13
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(new Integer(13),iter.next()); // odd   13
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(14),iter.next()); // even  14
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(15),iter.next()); // odd   15
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(16),iter.next()); // even  16
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(17),iter.next()); // odd   17
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(18),iter.next()); // even  18
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(new Integer(19),iter.next()); // odd   19
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(new Integer(21),iter.next()); // fib   21
        assertEquals(0,iter.getIteratorIndex());

        assertTrue(!iter.hasNext());
    }

    public void testRemoveFromSingle() {
        CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(evens.iterator());
        int expectedSize = evens.size();
        while (iter.hasNext()) {
            Object o = iter.next();
            Integer val = (Integer) o;
            if (val.intValue() % 4 == 0) {
                expectedSize--;
                iter.remove();
            }
        }
        assertEquals(expectedSize,evens.size());
    }

    public void testRemoveFromDouble() {
        CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(evens.iterator());
        iter.addIterator(odds.iterator());
        int expectedSize = evens.size() + odds.size();
        while (iter.hasNext()) {
            Object o = iter.next();
            Integer val = (Integer) o;
            if (val.intValue() % 4 == 0 || val.intValue() % 3 == 0) {
                expectedSize--;
                iter.remove();
            }
        }
        assertEquals(expectedSize, (evens.size() + odds.size()));
    }

}

