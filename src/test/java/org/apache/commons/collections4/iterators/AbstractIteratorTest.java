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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.IteratorUtils;
import org.junit.jupiter.api.Test;

/**
 * Abstract class for testing the Iterator interface.
 * <p>
 * This class provides a framework for testing an implementation of Iterator.
 * Concrete subclasses must provide the iterator to be tested.
 * They must also specify certain details of how the iterator operates by
 * overriding the supportsXxx() methods if necessary.
 * </p>
 *
 * @param <E> the type of elements tested by this iterator.
 */
public abstract class AbstractIteratorTest<E> extends AbstractObjectTest {

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
     * Tests whether or not we are testing an iterator that can be empty.
     * Default is true.
     *
     * @return true if Iterator can be empty
     */
    public boolean supportsEmptyIterator() {
        return true;
    }

    /**
     * Tests whether or not we are testing an iterator that can contain elements.
     * Default is true.
     *
     * @return true if Iterator can be full
     */
    public boolean supportsFullIterator() {
        return true;
    }

    /**
     * Tests whether or not we are testing an iterator that supports remove().
     * Default is true.
     *
     * @return true if Iterator supports remove
     */
    public boolean supportsRemove() {
        return true;
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
        assertFalse(it.hasNext(), "hasNext() should return false for empty iterators");

        // next() should throw a NoSuchElementException
        assertThrows(NoSuchElementException.class, () -> it.next(),
                "NoSuchElementException must be thrown when Iterator is exhausted");
        verify();

        assertNotNull(it.toString());
    }

    /**
     * Tests {@link Iterator#forEachRemaining(java.util.function.Consumer)}.
     */
    @Test
    public void testForEachRemaining() {
        final List<E> expected = IteratorUtils.toList(makeObject());
        final Iterator<E> it = makeObject();
        final List<E> actual = new ArrayList<>();
        it.forEachRemaining(actual::add);
        assertEquals(expected, actual);
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
        assertTrue(it.hasNext(), "hasNext() should return true for at least one element");

        // next() must not throw exception (ensure makeFullIterator is correct!)
        assertDoesNotThrow(it::next, "Full iterators must have at least one element");

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
            assertThrows(UnsupportedOperationException.class, it::remove);
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

    /**
     * Allows subclasses to add complex cross verification
     */
    public void verify() {
        // do nothing
    }

}
