/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A unit test to test the basic functions of {@link BoundedIterator}.
 *
 * @param <E> the type of elements tested by this iterator.
 */
public class BoundedIteratorTest<E> extends AbstractIteratorTest<E> {

    /** Test array of size 7 */
    private final String[] testArray = {
        "a", "b", "c", "d", "e", "f", "g"
    };

    private List<E> testList;

    @Override
    public Iterator<E> makeEmptyIterator() {
        return new BoundedIterator<>(Collections.<E>emptyList().iterator(), 0, 10);
    }

    @Override
    public Iterator<E> makeObject() {
        return new BoundedIterator<>(new ArrayList<>(testList).iterator(), 1, testList.size() - 1);
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp()
        throws Exception {
        testList = Arrays.asList((E[]) testArray);
    }

    /**
     * Test a decorated iterator bounded such that the first element returned is
     * at an index greater its first element, and the last element returned is
     * at an index less than its last element.
     */
    @Test
    void testBounded() {
        final Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 2, 4);

        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("d", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("e", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("f", iter.next());

        assertFalse(iter.hasNext());

        assertThrows(NoSuchElementException.class, () -> iter.next(),
                "Expected NoSuchElementException.");
    }

    /**
     * Test a decorated iterator bounded to a {@code max} of 0. The
     * BoundedIterator should behave as if there are no more elements to return,
     * since it is technically an empty iterator.
     */
    @Test
    void testEmptyBounded() {
        final Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 3, 0);
        assertFalse(iter.hasNext());

        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    /**
     * Test the case if the {@code max} passed to the constructor is
     * greater than the size of the decorated iterator. The last element
     * returned should be the same as the last element of the decorated
     * iterator.
     */
    @Test
    void testMaxGreaterThanSize() {
        final Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 1, 10);

        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("d", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("e", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("f", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("g", iter.next());

        assertFalse(iter.hasNext());

        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    /**
     * Test the case if a negative {@code max} is passed to the
     * constructor. {@link IllegalArgumentException} is expected.
     */
    @Test
    void testNegativeMax() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new BoundedIterator<>(testList.iterator(), 3, -1));
        assertEquals("Max parameter must not be negative.", thrown.getMessage());
    }

    /**
     * Test the case if a negative {@code offset} is passed to the
     * constructor. {@link IllegalArgumentException} is expected.
     */
    @Test
    void testNegativeOffset() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new BoundedIterator<>(testList.iterator(), -1, 4));
        assertEquals("Offset parameter must not be negative.", thrown.getMessage());
    }

    /**
     * Test the case if the {@code offset} passed to the constructor is
     * greater than the decorated iterator's size. The BoundedIterator should
     * behave as if there are no more elements to return.
     */
    @Test
    void testOffsetGreaterThanSize() {
        final Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 10, 4);
        assertFalse(iter.hasNext());

        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    /**
     * Test the {@code remove()} method being called twice without calling
     * {@code next()} in between.
     */
    @Test
    void testRemoveCalledTwice() {
        final List<E> testListCopy = new ArrayList<>(testList);
        final Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        iter.remove();

        assertThrows(IllegalStateException.class, () -> iter.remove());
    }

    /**
     * Test removing the first element. Verify that the element is removed from
     * the underlying collection.
     */
    @Test
    void testRemoveFirst() {
        final List<E> testListCopy = new ArrayList<>(testList);
        final Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());

        iter.remove();
        assertFalse(testListCopy.contains("b"));

        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("d", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("e", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("f", iter.next());

        assertFalse(iter.hasNext());

        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    /**
     * Test removing the last element. Verify that the element is removed from
     * the underlying collection.
     */
    @Test
    void testRemoveLast() {
        final List<E> testListCopy = new ArrayList<>(testList);
        final Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("d", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("e", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("f", iter.next());

        assertFalse(iter.hasNext());

        final NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> iter.next());
        assertNull(thrown.getMessage());

        iter.remove();
        assertFalse(testListCopy.contains("f"));

        assertFalse(iter.hasNext());

        final NoSuchElementException thrown1 = assertThrows(NoSuchElementException.class, () -> iter.next());
        assertNull(thrown1.getMessage());
    }

    /**
     * Test removing an element in the middle of the iterator. Verify that the
     * element is removed from the underlying collection.
     */
    @Test
    void testRemoveMiddle() {
        final List<E> testListCopy = new ArrayList<>(testList);
        final Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("d", iter.next());

        iter.remove();
        assertFalse(testListCopy.contains("d"));

        assertTrue(iter.hasNext());
        assertEquals("e", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("f", iter.next());

        assertFalse(iter.hasNext());

        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    /**
     * Test the case if the decorated iterator does not support the
     * {@code remove()} method and throws an {@link UnsupportedOperationException}.
     */
    @Test
    void testRemoveUnsupported() {
        final Iterator<E> mockIterator = new AbstractIteratorDecorator<E>(testList.iterator()) {
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        final Iterator<E> iter = new BoundedIterator<>(mockIterator, 1, 5);
        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());

        final UnsupportedOperationException thrown = assertThrows(UnsupportedOperationException.class, () -> iter.remove());
        assertNull(thrown.getMessage());

    }

    /**
     * Test the {@code remove()} method being called without
     * {@code next()} being called first.
     */
    @Test
    void testRemoveWithoutCallingNext() {
        final List<E> testListCopy = new ArrayList<>(testList);
        final Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> iter.remove());
        assertEquals("remove() cannot be called before calling next()", thrown.getMessage());
    }

    /**
     * Test a decorated iterator bounded such that the {@code offset} is
     * zero and the {@code max} is its size, in that the BoundedIterator
     * should return all the same elements as its decorated iterator.
     */
    @Test
    void testSameAsDecorated() {
        final Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 0,
                                                  testList.size());

        assertTrue(iter.hasNext());
        assertEquals("a", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("d", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("e", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("f", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("g", iter.next());

        assertFalse(iter.hasNext());

        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

}
