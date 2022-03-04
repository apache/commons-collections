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
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.ResettableListIterator;

/**
 * Tests the ReverseListIterator.
 *
 */
public class ReverseListIteratorTest<E> extends AbstractListIteratorTest<E> {

    protected String[] testArray = { "One", "Two", "Three", "Four" };

    public ReverseListIteratorTest(final String testName) {
        super(testName);
    }

    @Override
    public ListIterator<E> makeEmptyIterator() {
        return new ReverseListIterator<>(new ArrayList<E>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public ReverseListIterator<E> makeObject() {
        final List<E> list = new ArrayList<>(Arrays.asList((E[]) testArray));
        return new ReverseListIterator<>(list);
    }

    // overrides
    @Override
    public void testEmptyListIteratorIsIndeedEmpty() {
        final ListIterator<E> it = makeEmptyIterator();

        assertFalse(it.hasNext());
        assertEquals(-1, it.nextIndex());  // reversed index
        assertFalse(it.hasPrevious());
        assertEquals(0, it.previousIndex());  // reversed index

        // next() should throw a NoSuchElementException
        try {
            it.next();
            fail("NoSuchElementException must be thrown from empty ListIterator");
        } catch (final NoSuchElementException e) {
        }

        // previous() should throw a NoSuchElementException
        try {
            it.previous();
            fail("NoSuchElementException must be thrown from empty ListIterator");
        } catch (final NoSuchElementException e) {
        }
    }

    @Override
    public void testWalkForwardAndBack() {
        final ArrayList<E> list = new ArrayList<>();
        final ListIterator<E> it = makeObject();
        while (it.hasNext()) {
            list.add(it.next());
        }

        // check state at end
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());

        // this had to be commented out, as there is a bug in the JDK before JDK1.5
        // where calling previous at the start of an iterator would push the cursor
        // back to an invalid negative value
//        try {
//            it.next();
//            fail("NoSuchElementException must be thrown from next at end of ListIterator");
//        } catch (NoSuchElementException e) {
//        }

        // loop back through comparing
        for (int i = list.size() - 1; i >= 0; i--) {
            assertEquals("" + i, list.size() - i - 2, it.nextIndex());  // reversed index
            assertEquals(list.size() - i - 1, it.previousIndex());  // reversed index

            final Object obj = list.get(i);
            assertEquals(obj, it.previous());
        }

        // check state at start
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        try {
            it.previous();
            fail("NoSuchElementException must be thrown from previous at start of ListIterator");
        } catch (final NoSuchElementException e) {
        }
    }

    public void testReverse() {
        final ListIterator<E> it = makeObject();
        assertTrue(it.hasNext());
        assertEquals(3, it.nextIndex());
        assertFalse(it.hasPrevious());
        assertEquals(4, it.previousIndex());
        assertEquals("Four", it.next());
        assertEquals(2, it.nextIndex());
        assertTrue(it.hasNext());
        assertEquals(3, it.previousIndex());
        assertTrue(it.hasPrevious());
        assertEquals("Three", it.next());
        assertTrue(it.hasNext());
        assertEquals(1, it.nextIndex());
        assertTrue(it.hasPrevious());
        assertEquals(2, it.previousIndex());
        assertEquals("Two", it.next());
        assertTrue(it.hasNext());
        assertEquals(0, it.nextIndex());
        assertTrue(it.hasPrevious());
        assertEquals(1, it.previousIndex());
        assertEquals("One", it.next());
        assertFalse(it.hasNext());
        assertEquals(-1, it.nextIndex());
        assertTrue(it.hasPrevious());
        assertEquals(0, it.previousIndex());
        assertEquals("One", it.previous());
        assertEquals("Two", it.previous());
        assertEquals("Three", it.previous());
        assertEquals("Four", it.previous());
    }

    public void testReset() {
        final ResettableListIterator<E> it = makeObject();
        assertEquals("Four", it.next());
        it.reset();
        assertEquals("Four", it.next());
        it.next();
        it.next();
        it.reset();
        assertEquals("Four", it.next());
    }

}
