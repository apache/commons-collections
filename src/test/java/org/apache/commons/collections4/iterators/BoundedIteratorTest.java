/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * A unit test to test the basic functions of {@link BoundedIterator}.
 *
 */
public class BoundedIteratorTest<E> extends AbstractIteratorTest<E> {

    /** Test array of size 7 */
    private final String[] testArray = {
        "a", "b", "c", "d", "e", "f", "g"
    };

    private List<E> testList;

    public BoundedIteratorTest(final String testName) {
        super(testName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setUp()
        throws Exception {
        super.setUp();
        testList = Arrays.asList((E[]) testArray);
    }

    @Override
    public Iterator<E> makeEmptyIterator() {
        return new BoundedIterator<>(Collections.<E>emptyList().iterator(), 0, 10);
    }

    @Override
    public Iterator<E> makeObject() {
        return new BoundedIterator<>(new ArrayList<>(testList).iterator(), 1, testList.size() - 1);
    }

    // ---------------- Tests ---------------------

    /**
     * Test a decorated iterator bounded such that the first element returned is
     * at an index greater its first element, and the last element returned is
     * at an index less than its last element.
     */
    @Test
    public void testBounded() {
        Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 2, 4);

        assertTrue(iter.hasNext());
        assertEquals("c", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("d", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("e", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("f", iter.next());

        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException nsee) { /* Success case */
        }
    }

    /**
     * Test a decorated iterator bounded such that the <code>offset</code> is
     * zero and the <code>max</code> is its size, in that the BoundedIterator
     * should return all the same elements as its decorated iterator.
     */
    @Test
    public void testSameAsDecorated() {
        Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 0,
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
        try {
            iter.next();
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException nsee) { /* Success case */
        }
    }

    /**
     * Test a decorated iterator bounded to a <code>max</code> of 0. The
     * BoundedIterator should behave as if there are no more elements to return,
     * since it is technically an empty iterator.
     */
    @Test
    public void testEmptyBounded() {
        Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 3, 0);
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException nsee) { /* Success case */
        }
    }

    /**
     * Test the case if a negative <code>offset</code> is passed to the
     * constructor. {@link IllegalArgumentException} is expected.
     */
    @Test
    public void testNegativeOffset() {
        try {
            new BoundedIterator<>(testList.iterator(), -1, 4);
            fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException iae) { /* Success case */
        }
    }

    /**
     * Test the case if a negative <code>max</code> is passed to the
     * constructor. {@link IllegalArgumentException} is expected.
     */
    @Test
    public void testNegativeMax() {
        try {
            new BoundedIterator<>(testList.iterator(), 3, -1);
            fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException iae) { /* Success case */
        }
    }

    /**
     * Test the case if the <code>offset</code> passed to the constructor is
     * greater than the decorated iterator's size. The BoundedIterator should
     * behave as if there are no more elements to return.
     */
    @Test
    public void testOffsetGreaterThanSize() {
        Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 10, 4);
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException nsee) { /* Success case */
        }
    }

    /**
     * Test the case if the <code>max</code> passed to the constructor is
     * greater than the size of the decorated iterator. The last element
     * returned should be the same as the last element of the decorated
     * iterator.
     */
    @Test
    public void testMaxGreaterThanSize() {
        Iterator<E> iter = new BoundedIterator<>(testList.iterator(), 1, 10);

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
        try {
            iter.next();
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException nsee) { /* Success case */
        }
    }

    /**
     * Test the <code>remove()</code> method being called without
     * <code>next()</code> being called first.
     */
    @Test
    public void testRemoveWithoutCallingNext() {
        List<E> testListCopy = new ArrayList<>(testList);
        Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

        try {
            iter.remove();
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException ise) { /* Success case */
        }
    }

    /**
     * Test the <code>remove()</code> method being called twice without calling
     * <code>next()</code> in between.
     */
    @Test
    public void testRemoveCalledTwice() {
        List<E> testListCopy = new ArrayList<>(testList);
        Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        iter.remove();

        try {
            iter.remove();
            fail("Expected IllegalStateException.");
        } catch (IllegalStateException ise) { /* Success case */
        }
    }

    /**
     * Test removing the first element. Verify that the element is removed from
     * the underlying collection.
     */
    @Test
    public void testRemoveFirst() {
        List<E> testListCopy = new ArrayList<>(testList);
        Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

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
        try {
            iter.next();
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException nsee) { /* Success case */
        }
    }

    /**
     * Test removing an element in the middle of the iterator. Verify that the
     * element is removed from the underlying collection.
     */
    @Test
    public void testRemoveMiddle() {
        List<E> testListCopy = new ArrayList<>(testList);
        Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

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
        try {
            iter.next();
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException nsee) { /* Success case */
        }
    }

    /**
     * Test removing the last element. Verify that the element is removed from
     * the underlying collection.
     */
    @Test
    public void testRemoveLast() {
        List<E> testListCopy = new ArrayList<>(testList);
        Iterator<E> iter = new BoundedIterator<>(testListCopy.iterator(), 1, 5);

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
        try {
            iter.next();
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException nsee) { /* Success case */
        }

        iter.remove();
        assertFalse(testListCopy.contains("f"));

        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException nsee) { /* Success case */
        }
    }

    /**
     * Test the case if the decorated iterator does not support the
     * <code>remove()</code> method and throws an {@link UnsupportedOperationException}.
     */
    @Test
    public void testRemoveUnsupported() {
        Iterator<E> mockIterator = new AbstractIteratorDecorator<E>(testList.iterator()) {
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        Iterator<E> iter = new BoundedIterator<>(mockIterator, 1, 5);
        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        try {
            iter.remove();
            fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException usoe) { /* Success case */
        }
    }
}
