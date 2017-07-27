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
package org.apache.commons.collections4.iterators;

import java.util.ArrayList;

import org.apache.commons.collections4.IteratorUtils;

/**
 * Unit test suite for {@link ZippingIterator}.
 *
 * @version $Id$
 */
@SuppressWarnings("boxing")
public class ZippingIteratorTest extends AbstractIteratorTest<Integer> {

    //------------------------------------------------------------ Conventional

    public ZippingIteratorTest(final String testName) {
        super(testName);
    }

    //--------------------------------------------------------------- Lifecycle

    private ArrayList<Integer> evens = null;
    private ArrayList<Integer> odds = null;
    private ArrayList<Integer> fib = null;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        evens = new ArrayList<>();
        odds = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (0 == i % 2) {
                evens.add(i);
            } else {
                odds.add(i);
            }
        }
        fib = new ArrayList<>();
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
    @SuppressWarnings("unchecked")
    public ZippingIterator<Integer> makeEmptyIterator() {
        return new ZippingIterator<>(IteratorUtils.<Integer>emptyIterator());
    }

    @Override
    public ZippingIterator<Integer> makeObject() {
        return new ZippingIterator<>(evens.iterator(), odds.iterator(), fib.iterator());
    }

    //------------------------------------------------------------------- Tests

    public void testIterateEven() {
        @SuppressWarnings("unchecked")
        final ZippingIterator<Integer> iter = new ZippingIterator<>(evens.iterator());
        for (int i = 0; i < evens.size(); i++) {
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i), iter.next());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateEvenOdd() {
        final ZippingIterator<Integer> iter = new ZippingIterator<>(evens.iterator(), odds.iterator());
        for (int i = 0; i < 20; i++) {
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(i), iter.next());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateOddEven() {
        final ZippingIterator<Integer> iter = new ZippingIterator<>(odds.iterator(), evens.iterator());
        for (int i = 0, j = 0; i < 20; i++) {
            assertTrue(iter.hasNext());
            int val = iter.next();
            if (i % 2 == 0) {
                assertEquals(odds.get(j).intValue(), val);
            } else {
                assertEquals(evens.get(j).intValue(), val);
                j++;
            }
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateEvenEven() {
        final ZippingIterator<Integer> iter = new ZippingIterator<>(evens.iterator(), evens.iterator());
        for (int i = 0; i < evens.size(); i++) {
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i), iter.next());
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i), iter.next());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateFibEvenOdd() {
        final ZippingIterator<Integer> iter = new ZippingIterator<>(fib.iterator(), evens.iterator(), odds.iterator());

        assertEquals(Integer.valueOf(1),iter.next());  // fib    1
        assertEquals(Integer.valueOf(0),iter.next());  // even   0
        assertEquals(Integer.valueOf(1),iter.next());  // odd    1
        assertEquals(Integer.valueOf(1),iter.next());  // fib    1
        assertEquals(Integer.valueOf(2),iter.next());  // even   2
        assertEquals(Integer.valueOf(3),iter.next());  // odd    3
        assertEquals(Integer.valueOf(2),iter.next());  // fib    2
        assertEquals(Integer.valueOf(4),iter.next());  // even   4
        assertEquals(Integer.valueOf(5),iter.next());  // odd    5
        assertEquals(Integer.valueOf(3),iter.next());  // fib    3
        assertEquals(Integer.valueOf(6),iter.next());  // even   6
        assertEquals(Integer.valueOf(7),iter.next());  // odd    7
        assertEquals(Integer.valueOf(5),iter.next());  // fib    5
        assertEquals(Integer.valueOf(8),iter.next());  // even   8
        assertEquals(Integer.valueOf(9),iter.next());  // odd    9
        assertEquals(Integer.valueOf(8),iter.next());  // fib    8
        assertEquals(Integer.valueOf(10),iter.next()); // even  10
        assertEquals(Integer.valueOf(11),iter.next()); // odd   11
        assertEquals(Integer.valueOf(13),iter.next()); // fib   13
        assertEquals(Integer.valueOf(12),iter.next()); // even  12
        assertEquals(Integer.valueOf(13),iter.next()); // odd   13
        assertEquals(Integer.valueOf(21),iter.next()); // fib   21
        assertEquals(Integer.valueOf(14),iter.next()); // even  14
        assertEquals(Integer.valueOf(15),iter.next()); // odd   15
        assertEquals(Integer.valueOf(16),iter.next()); // even  16
        assertEquals(Integer.valueOf(17),iter.next()); // odd   17
        assertEquals(Integer.valueOf(18),iter.next()); // even  18
        assertEquals(Integer.valueOf(19),iter.next()); // odd   19

        assertTrue(!iter.hasNext());
    }

    public void testRemoveFromSingle() {
        @SuppressWarnings("unchecked")
        final ZippingIterator<Integer> iter = new ZippingIterator<>(evens.iterator());
        int expectedSize = evens.size();
        while (iter.hasNext()) {
            final Object o = iter.next();
            final Integer val = (Integer) o;
            if (val.intValue() % 4 == 0) {
                expectedSize--;
                iter.remove();
            }
        }
        assertEquals(expectedSize, evens.size());
    }

    public void testRemoveFromDouble() {
        final ZippingIterator<Integer> iter = new ZippingIterator<>(evens.iterator(), odds.iterator());
        int expectedSize = evens.size() + odds.size();
        while (iter.hasNext()) {
            final Object o = iter.next();
            final Integer val = (Integer) o;
            if (val.intValue() % 4 == 0 || val.intValue() % 3 == 0) {
                expectedSize--;
                iter.remove();
            }
        }
        assertEquals(expectedSize, evens.size() + odds.size());
    }

}

