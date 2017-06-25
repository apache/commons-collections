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

import org.apache.commons.collections4.comparators.ComparableComparator;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

import static org.easymock.EasyMock.mock;


/**
 * Unit test suite for {@link CollatingIterator}.
 *
 * @version $Id$
 */
@SuppressWarnings("boxing")
public class CollatingIteratorTest extends AbstractIteratorTest<Integer> {

    //------------------------------------------------------------ Conventional

    public CollatingIteratorTest(final String testName) {
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
        final CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(evens.iterator());
        iter.addIterator(odds.iterator());
        iter.addIterator(fib.iterator());
        return iter;
    }

    //------------------------------------------------------------------- Tests

    public void testGetSetComparator() {
        final CollatingIterator<Integer> iter = new CollatingIterator<Integer>();
        assertNull(iter.getComparator());
        iter.setComparator(comparator);
        assertSame(comparator, iter.getComparator());
        iter.setComparator(null);
        assertNull(iter.getComparator());
    }

    public void testIterateEven() {
        final CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(evens.iterator());
        for (int i = 0; i < evens.size(); i++) {
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i), iter.next());
            assertEquals(0,iter.getIteratorIndex());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateEvenOdd() {
        final CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator, evens.iterator(), odds.iterator());
        for (int i = 0; i < 20; i++) {
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(i), iter.next());
            assertEquals(i % 2,iter.getIteratorIndex());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateOddEven() {
        final CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator, odds.iterator(), evens.iterator());
        for (int i = 0; i < 20; i++) {
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(i),iter.next());
            assertEquals(i % 2 == 0 ? 1 : 0,iter.getIteratorIndex());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateEvenEven() {
        final CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
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
        final CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(fib.iterator());
        iter.addIterator(evens.iterator());
        iter.addIterator(odds.iterator());

        assertEquals(Integer.valueOf(0),iter.next());  // even   0
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(1),iter.next());  // fib    1
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(1),iter.next());  // fib    1
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(1),iter.next());  // odd    1
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(2),iter.next());  // fib    2
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(2),iter.next());  // even   2
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(3),iter.next());  // fib    3
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(3),iter.next());  // odd    3
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(4),iter.next());  // even   4
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(5),iter.next());  // fib    5
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(5),iter.next());  // odd    5
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(6),iter.next());  // even   6
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(7),iter.next());  // odd    7
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(8),iter.next());  // fib    8
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(8),iter.next());  // even   8
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(9),iter.next());  // odd    9
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(10),iter.next()); // even  10
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(11),iter.next()); // odd   11
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(12),iter.next()); // even  12
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(13),iter.next()); // fib   13
        assertEquals(0,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(13),iter.next()); // odd   13
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(14),iter.next()); // even  14
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(15),iter.next()); // odd   15
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(16),iter.next()); // even  16
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(17),iter.next()); // odd   17
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(18),iter.next()); // even  18
        assertEquals(1,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(19),iter.next()); // odd   19
        assertEquals(2,iter.getIteratorIndex());
        assertEquals(Integer.valueOf(21),iter.next()); // fib   21
        assertEquals(0,iter.getIteratorIndex());

        assertTrue(!iter.hasNext());
    }

    public void testRemoveFromSingle() {
        final CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(evens.iterator());
        int expectedSize = evens.size();
        while (iter.hasNext()) {
            final Object o = iter.next();
            final Integer val = (Integer) o;
            if (val.intValue() % 4 == 0) {
                expectedSize--;
                iter.remove();
            }
        }
        assertEquals(expectedSize,evens.size());
    }

    public void testRemoveFromDouble() {
        final CollatingIterator<Integer> iter = new CollatingIterator<Integer>(comparator);
        iter.addIterator(evens.iterator());
        iter.addIterator(odds.iterator());
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

    public void testNullComparator() {
       final List<Integer> l1 = Arrays.asList(1, 3, 5);
       final List<Integer> l2 = Arrays.asList(2, 4, 6);

       final CollatingIterator<Integer> collatingIterator1 = new CollatingIterator<Integer>(null, l1.iterator(), l2.iterator());
       try {
           collatingIterator1.next();
       } catch (final NullPointerException e) {
           assertTrue(e.getMessage().startsWith("You must invoke setComparator"));
       }

       int i = 0;
       final CollatingIterator<Integer> collatingIterator2 = new CollatingIterator<Integer>(null, l1.iterator(), l2.iterator());
       collatingIterator2.setComparator(new ComparableComparator<Integer>());
       for ( ; collatingIterator2.hasNext(); i++ ) {
          final Integer n = collatingIterator2.next();
          assertEquals("wrong order", (int)n, i + 1);
       }
       assertEquals("wrong size", i, l1.size() + l2.size());
    }

    @Test
    public void testSetComparatorThrowsIllegalStateException() {

        CollatingIterator<Iterator<String>> collatingIterator = new CollatingIterator<Iterator<String>>(null, 1);

        assertFalse(collatingIterator.hasNext());


        try {
            collatingIterator.setComparator(null);
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("Can't do that after next or hasNext has been called.",e.getMessage());
            assertEquals(CollatingIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testSetIteratorThrowsNullPointerException() {

        Comparator<Iterator<String>> comparator = (Comparator<Iterator<String>>) mock(Comparator.class);
        CollatingIterator<Iterator<String>> collatingIterator = new CollatingIterator<Iterator<String>>(comparator);

        try {
            collatingIterator.setIterator(1, null);
            fail("Expecting exception: NullPointerException");
        } catch(NullPointerException e) {
            assertEquals("Iterator must not be null",e.getMessage());
            assertEquals(CollatingIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testSetIteratorThrowsArrayIndexOutOfBoundsException() {

        CollatingIterator<Integer> collatingIterator = new CollatingIterator<Integer>();

        try {
            collatingIterator.setIterator(-12, collatingIterator);
            fail("Expecting exception: ArrayIndexOutOfBoundsException");

        } catch(ArrayIndexOutOfBoundsException e) {

        }

    }


    @Test
    public void testCreatesCollatingIteratorTakingThreeArguments() {

        Comparator<Object> comparator = (Comparator<Object>) mock(Comparator.class);
        CollatingIterator<Integer> collatingIterator = new CollatingIterator<Integer>(comparator);
        collatingIterator.addIterator(collatingIterator);
        List<Iterator<? extends Integer>> list = collatingIterator.getIterators();

        assertEquals(1, list.size());

        Comparator<Object> comparatorTwo = (Comparator<Object>) mock(Comparator.class);
        CollatingIterator<Integer> collatingIteratorTwo = new CollatingIterator<Integer>(comparatorTwo, list);

        assertEquals(1, list.size());

    }


    @Test
    public void testFailsToCreateCollatingIteratorTakingThreeArgumentsThrowsNullPointerException() {

        Iterator<Iterator<String>>[] iteratorArray = (Iterator<Iterator<String>>[]) Array.newInstance(Iterator.class, 2);
        CollatingIterator<Iterator<String>> collatingIterator = null;

        try {
            collatingIterator = new CollatingIterator<Iterator<String>>(null, iteratorArray);
            fail("Expecting exception: NullPointerException");
        } catch(NullPointerException e) {
            assertEquals("Iterator must not be null",e.getMessage());
            assertEquals(CollatingIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testGetIteratorIndexThrowsIllegalStateException() {

        Comparator<Object> comparator = (Comparator<Object>) mock(Comparator.class);
        Iterator<Iterator<String>>[] iteratorArray = (Iterator<Iterator<String>>[]) Array.newInstance(Iterator.class, 0);
        CollatingIterator<Object> collatingIterator = new CollatingIterator<Object>(comparator, iteratorArray);

        try {
            collatingIterator.getIteratorIndex();
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("No value has been returned yet",e.getMessage());
            assertEquals(CollatingIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


}

