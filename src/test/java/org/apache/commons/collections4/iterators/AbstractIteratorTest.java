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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.AbstractObjectTest;
import org.junit.Test;

/**
 * Abstract class for testing the Iterator interface.
 * <p>
 * This class provides a framework for testing an implementation of Iterator.
 * Concrete subclasses must provide the iterator to be tested.
 * They must also specify certain details of how the iterator operates by
 * overriding the supportsXxx() methods if necessary.
 *
 * @since 3.0
 */
public abstract class AbstractIteratorTest<E> extends AbstractObjectTest {

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractIteratorTest(final String testName) {
        super(testName);
    }

    /**
     * Implement this method to return an iterator over an empty collection.
     *
     * @return an empty iterator
     */
    public abstract Iterator<E> makeEmptyIterator();

    /**
     * Implements the abstract superclass method to return the full iterator.
     *
     * @return a full iterator
     */
    @Override
    public abstract Iterator<E> makeObject();

    /**
     * Whether or not we are testing an iterator that can be empty.
     * Default is true.
     *
     * @return true if Iterator can be empty
     */
    public boolean supportsEmptyIterator() {
        return true;
    }

    /**
     * Whether or not we are testing an iterator that can contain elements.
     * Default is true.
     *
     * @return true if Iterator can be full
     */
    public boolean supportsFullIterator() {
        return true;
    }

    /**
     * Whether or not we are testing an iterator that supports remove().
     * Default is true.
     *
     * @return true if Iterator supports remove
     */
    public boolean supportsRemove() {
        return true;
    }

    /**
     * Allows subclasses to add complex cross verification
     */
    public void verify() {
        // do nothing
    }

    /**
     * Test the empty iterator.
     */
    @Test
    public void testEmptyIterator() {
        if (!supportsEmptyIterator()) {
            return;
        }

        final Iterator<E> it = makeEmptyIterator();

        // hasNext() should return false
        assertFalse("hasNext() should return false for empty iterators", it.hasNext());

        // next() should throw a NoSuchElementException
        assertThrows(NoSuchElementException.class, () -> it.next(),
                "NoSuchElementException must be thrown when Iterator is exhausted");
        verify();

        assertNotNull(it.toString());
    }

    /**
     * Test normal iteration behavior.
     */
    @Test
    public void testFullIterator() {
        if (!supportsFullIterator()) {
            return;
        }

        final Iterator<E> it = makeObject();

        // hasNext() must be true (ensure makeFullIterator is correct!)
        assertTrue("hasNext() should return true for at least one element", it.hasNext());

        // next() must not throw exception (ensure makeFullIterator is correct!)
        try {
            it.next();
        } catch (final NoSuchElementException e) {
            fail("Full iterators must have at least one element");
        }

        // iterate through
        while (it.hasNext()) {
            it.next();
            verify();
        }

        // next() must throw NoSuchElementException now
        assertThrows(NoSuchElementException.class, () -> it.next(),
                "NoSuchElementException must be thrown when Iterator is exhausted");

        assertNotNull(it.toString());
    }

    /**
     * Test remove behavior.
     */
    @Test
    public void testRemove() {
        final Iterator<E> it = makeObject();

        if (!supportsRemove()) {
            // check for UnsupportedOperationException if not supported
            try {
                it.remove();
            } catch (final UnsupportedOperationException ex) {}
            return;
        }

        // should throw IllegalStateException before next() called
        assertThrows(IllegalStateException.class, () -> it.remove());
        verify();

        // remove after next should be fine
        it.next();
        it.remove();

        // should throw IllegalStateException for second remove()
        assertThrows(IllegalStateException.class, () -> it.remove());
    }

}
