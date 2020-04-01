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
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Abstract class for testing the ListIterator interface.
 * <p>
 * This class provides a framework for testing an implementation of ListIterator.
 * Concrete subclasses must provide the list iterator to be tested.
 * They must also specify certain details of how the list iterator operates by
 * overriding the supportsXxx() methods if necessary.
 *
 * @since 3.0
 */
public abstract class AbstractListIteratorTest<E> extends AbstractIteratorTest<E> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractListIteratorTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the abstract superclass method to return the list iterator.
     *
     * @return an empty iterator
     */
    @Override
    public abstract ListIterator<E> makeEmptyIterator();

    /**
     * Implements the abstract superclass method to return the list iterator.
     *
     * @return a full iterator
     */
    @Override
    public abstract ListIterator<E> makeObject();

    /**
     * Whether or not we are testing an iterator that supports add().
     * Default is true.
     *
     * @return true if Iterator supports add
     */
    public boolean supportsAdd() {
        return true;
    }

    /**
     * Whether or not we are testing an iterator that supports set().
     * Default is true.
     *
     * @return true if Iterator supports set
     */
    public boolean supportsSet() {
        return true;
    }

    /**
     * The value to be used in the add and set tests.
     * Default is null.
     */
    public E addSetValue() {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Test that the empty list iterator contract is correct.
     */
    public void testEmptyListIteratorIsIndeedEmpty() {
        if (!supportsEmptyIterator()) {
            return;
        }

        final ListIterator<E> it = makeEmptyIterator();

        assertEquals(false, it.hasNext());
        assertEquals(0, it.nextIndex());
        assertEquals(false, it.hasPrevious());
        assertEquals(-1, it.previousIndex());

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

    /**
     * Test navigation through the iterator.
     */
    public void testWalkForwardAndBack() {
        final ArrayList<E> list = new ArrayList<>();
        final ListIterator<E> it = makeObject();
        while (it.hasNext()) {
            list.add(it.next());
        }

        // check state at end
        assertEquals(false, it.hasNext());
        assertEquals(true, it.hasPrevious());
        try {
            it.next();
            fail("NoSuchElementException must be thrown from next at end of ListIterator");
        } catch (final NoSuchElementException e) {
        }

        // loop back through comparing
        for (int i = list.size() - 1; i >= 0; i--) {
            assertEquals(i + 1, it.nextIndex());
            assertEquals(i, it.previousIndex());

            final Object obj = list.get(i);
            assertEquals(obj, it.previous());
        }

        // check state at start
        assertEquals(true, it.hasNext());
        assertEquals(false, it.hasPrevious());
        try {
            it.previous();
            fail("NoSuchElementException must be thrown from previous at start of ListIterator");
        } catch (final NoSuchElementException e) {
        }
    }

    /**
     * Test add behavior.
     */
    public void testAdd() {
        ListIterator<E> it = makeObject();

        final E addValue = addSetValue();
        if (!supportsAdd()) {
            // check for UnsupportedOperationException if not supported
            try {
                it.add(addValue);
                fail("UnsupportedOperationException must be thrown from add of " + it.getClass().getSimpleName());
            } catch (final UnsupportedOperationException ex) {}
            return;
        }

        // add at start should be OK, added should be previous
        it = makeObject();
        it.add(addValue);
        assertEquals(addValue, it.previous());

        // add at start should be OK, added should not be next
        it = makeObject();
        it.add(addValue);
        assertTrue(addValue != it.next());

        // add in middle and at end should be OK
        it = makeObject();
        while (it.hasNext()) {
            it.next();
            it.add(addValue);
            // check add OK
            assertEquals(addValue, it.previous());
            it.next();
        }
    }

    /**
     * Test set behavior.
     */
    public void testSet() {
        final ListIterator<E> it = makeObject();

        if (!supportsSet()) {
            // check for UnsupportedOperationException if not supported
            try {
                it.set(addSetValue());
                fail("UnsupportedOperationException must be thrown from set in " + it.getClass().getSimpleName());
            } catch (final UnsupportedOperationException ex) {}
            return;
        }

        // should throw IllegalStateException before next() called
        try {
            it.set(addSetValue());
            fail();
        } catch (final IllegalStateException ex) {}

        // set after next should be fine
        it.next();
        it.set(addSetValue());

        // repeated set calls should be fine
        it.set(addSetValue());

    }

    public void testRemoveThenSet() {
        final ListIterator<E> it = makeObject();
        if (supportsRemove() && supportsSet()) {
            it.next();
            it.remove();
            try {
                it.set(addSetValue());
                fail("IllegalStateException must be thrown from set after remove");
            } catch (final IllegalStateException e) {
            }
        }
    }

    public void testAddThenSet() {
        final ListIterator<E> it = makeObject();
        // add then set
        if (supportsAdd() && supportsSet()) {
            it.next();
            it.add(addSetValue());
            try {
                it.set(addSetValue());
                fail("IllegalStateException must be thrown from set after add");
            } catch (final IllegalStateException e) {
            }
        }
    }

    /**
     * Test remove after add behavior.
     */
    public void testAddThenRemove() {
        final ListIterator<E> it = makeObject();

        // add then remove
        if (supportsAdd() && supportsRemove()) {
            it.next();
            it.add(addSetValue());
            try {
                it.remove();
                fail("IllegalStateException must be thrown from remove after add");
            } catch (final IllegalStateException e) {
            }
        }
    }

}
