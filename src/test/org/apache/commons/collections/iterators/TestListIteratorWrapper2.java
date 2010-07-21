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
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.collections.ResettableListIterator;

/**
 * Tests the ListIteratorWrapper to insure that it behaves as expected when wrapping a ListIterator.
 *
 * @version $Revision$ $Date$
 *
 * @author Morgan Delagrange
 */
public class TestListIteratorWrapper2<E> extends AbstractTestIterator<E> {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List<E> list1 = null;

    public static Test suite() {
        return new TestSuite(TestListIteratorWrapper2.class);
    }

    public TestListIteratorWrapper2(String testName) {
        super(testName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setUp() {
        list1 = new ArrayList<E>();
        list1.add((E) "One");
        list1.add((E) "Two");
        list1.add((E) "Three");
        list1.add((E) "Four");
        list1.add((E) "Five");
        list1.add((E) "Six");
    }

    @Override
    public ResettableListIterator<E> makeEmptyIterator() {
        ArrayList<E> list = new ArrayList<E>();
        return new ListIteratorWrapper<E>(list.listIterator());
    }

    @Override
    public ResettableListIterator<E> makeObject() {
        return new ListIteratorWrapper<E>(list1.listIterator());
    }

    public void testIterator() {
        ListIterator<E> iter = makeObject();
        for (int i = 0; i < testArray.length; i++) {
            Object testValue = testArray[i];
            Object iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

        assertTrue("Iterator should now be empty", !iter.hasNext());

        try {
            iter.next();
        } catch (Exception e) {
            assertTrue("NoSuchElementException must be thrown",
                       e.getClass().equals((new NoSuchElementException()).getClass()));
        }

        // now, read it backwards
        for (int i = testArray.length - 1; i > -1; --i) {
            Object testValue = testArray[i];
            E iterValue = iter.previous();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        try {
            iter.previous();
        } catch (Exception e) {
            assertTrue("NoSuchElementException must be thrown",
                       e.getClass().equals((new NoSuchElementException()).getClass()));
        }

        // now, read it forwards again
        for (int i = 0; i < testArray.length; i++) {
            Object testValue = testArray[i];
            Object iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

    }

    @Override
    public void testRemove() {
        ListIterator<E> iter = makeObject();

        //initial state:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        try {
            iter.remove();
            fail("ListIteratorWrapper#remove() should fail; must be initially positioned first");
        } catch (IllegalStateException e) {
        }

        //no change from invalid op:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        //establish size:
        int sz = list1.size();

        //verify initial next() call:
        assertEquals(list1.get(0), iter.next());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());

        //verify remove():
        iter.remove();
        assertEquals(--sz, list1.size());
        //like we never started iterating:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());
 
        try {
            iter.remove();
            fail("ListIteratorWrapper#remove() should fail; must be repositioned first");
        } catch (IllegalStateException e) {
        }

        //no change from invalid op:
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        //two consecutive next() calls:
        assertEquals(list1.get(0), iter.next());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());

        assertEquals(list1.get(1), iter.next());
        assertEquals(1, iter.previousIndex());
        assertEquals(2, iter.nextIndex());

        //call previous():
        assertEquals(list1.get(1), iter.previous());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());

        //should support remove() after calling previous() once from tip because we haven't changed the underlying iterator's position:
        iter.remove();
        assertEquals(--sz, list1.size());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());

        //this would dig into cache on a plain Iterator, but forwards directly to wrapped ListIterator:
        assertEquals(list1.get(0), iter.previous());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        //here's the proof; remove() still works:
        iter.remove();
        assertEquals(--sz, list1.size());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());

        //further testing would be fairly meaningless:
    }

    public void testReset() {
        ResettableListIterator<E> iter = makeObject();
        E first = iter.next();
        E second = iter.next();

        iter.reset();

        // after reset, there shouldn't be any previous elements
        assertFalse("No previous elements after reset()", iter.hasPrevious());

        // after reset, the results should be the same as before
        assertEquals("First element should be the same", first, iter.next());
        assertEquals("Second elment should be the same", second, iter.next());

        // after passing the point, where we resetted, continuation should work as expected
        for (int i = 2; i < testArray.length; i++) {
            Object testValue = testArray[i];
            E iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }
    }

}
