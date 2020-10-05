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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertFalse(it.hasNext());
        assertEquals(0, it.nextIndex());
        assertFalse(it.hasPrevious());
        assertEquals(-1, it.previousIndex());

        // next() should throw a NoSuchElementException
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            it.next();
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

        // previous() should throw a NoSuchElementException
        exception = assertThrows(NoSuchElementException.class, () -> {
            it.previous();
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
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
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            it.next();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("element"));
        }

        // loop back through comparing
        for (int i = list.size() - 1; i >= 0; i--) {
            assertEquals(i + 1, it.nextIndex());
            assertEquals(i, it.previousIndex());

            final Object obj = list.get(i);
            assertEquals(obj, it.previous());
        }

        // check state at start
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        exception = assertThrows(NoSuchElementException.class, () -> {
            it.previous();
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
    }

    /**
     * Test add behavior.
     */
    public void testAdd() {
        final ListIterator<E> it1 = makeObject();

        final E addValue = addSetValue();
        if (!supportsAdd()) {
            // check for UnsupportedOperationException if not supported
            Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
                it1.add(addValue);
            });
            if (null != exception.getMessage()) {
                assertTrue(exception.getMessage().contains("not support"));
            }
            return;
        }

        // add at start should be OK, added should be previous
        ListIterator<E> it = makeObject();
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
            Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
                it.set(addSetValue());
            });
            if (null != exception.getMessage()) {
                assertTrue(exception.getMessage().contains("not support"));
            }
            return;
        }

        // should throw IllegalStateException before next() called
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            it.set(addSetValue());
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }

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
            Exception exception = assertThrows(IllegalStateException.class, () -> {
                it.set(addSetValue());
            });
            assertNull(exception.getMessage());
        }
    }

    public void testAddThenSet() {
        final ListIterator<E> it = makeObject();
        // add then set
        if (supportsAdd() && supportsSet()) {
            it.next();
            it.add(addSetValue());
            Exception exception = assertThrows(IllegalStateException.class, () -> {
                it.set(addSetValue());
            });
            if (null != exception.getMessage()) {
                assertNotNull(exception.getMessage());
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
            Exception exception = assertThrows(IllegalStateException.class, () -> {
                it.remove();
            });
            if (null != exception.getMessage()) {
                assertNotNull(exception.getMessage());
            }
        }
    }

}
