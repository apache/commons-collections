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
package org.apache.commons.collections4.deque;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Extension of {@link AbstractDequeTest} for exercising the
 * {@link CircularDeque} implementation.
 *
 * @since 4.5
 */
public class CircularDequeTest<E> extends AbstractDequeTest<E> {
    /**
     * JUnit constructor.
     *
     * @param testName the test class name
     */
    public CircularDequeTest(String testName) {
        super(testName);
    }

    /**
     * Returns an empty CircularDeque that won't overflow.
     *
     * @return an empty CircularDeque
     */
    @Override
    public Deque<E> makeObject() {
        return new CircularDeque<>(100);
    }

    /**
     * Tests that the removal operation actually removes the first element.
     */
    @SuppressWarnings("unchecked")
    public void testCircularDequeCircular() {
        final List<E> list = new LinkedList<>();
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        final Deque<E> deque = new CircularDeque<>(list);

        assertEquals(true, deque.contains("A"));
        assertEquals(true, deque.contains("B"));
        assertEquals(true, deque.contains("C"));

        deque.add((E) "D");

        assertEquals(false, deque.contains("A"));
        assertEquals(true, deque.contains("B"));
        assertEquals(true, deque.contains("C"));
        assertEquals(true, deque.contains("D"));

        assertEquals("B", deque.peek());
        assertEquals("B", deque.remove());
        assertEquals("C", deque.remove());
        assertEquals("D", deque.remove());
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException1() {
        try {
            new CircularDeque<E>(0);
        } catch (final IllegalArgumentException ex) {
            return;
        }
        fail();
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException2() {
        try {
            new CircularDeque<E>(-20);
        } catch (final IllegalArgumentException ex) {
            return;
        }
        fail();
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException3() {
        try {
            new CircularDeque<E>(null);
        } catch (final NullPointerException ex) {
            return;
        }
        fail();
    }

    /**
     * Tests that the removal operation using method removeFirstOccurrence() and removeFirstOccurrence().
     */
    public void testRemoveElement() {
        final List<E> list = new LinkedList<>();
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");
        final Deque<E> deque = new CircularDeque<>(list);
        assertEquals(5, ((CircularDeque<E>) deque).maxSize());

        deque.addLast((E) "F");
        deque.addLast((E) "G");
        deque.addLast((E) "H");

        assertEquals(false, deque.removeFirstOccurrence("A"));
        assertEquals(false, deque.removeFirstOccurrence("B"));
        assertEquals(false, deque.removeLastOccurrence("C"));
        assertEquals("[D, E, F, G, H]", deque.toString());

        assertEquals(true, deque.removeLastOccurrence("H"));
        assertEquals("[D, E, F, G]", deque.toString());

        assertEquals(true, deque.removeLastOccurrence("E"));
        assertEquals("[D, F, G]", deque.toString());

        assertEquals(true, deque.removeLastOccurrence("F"));
        assertEquals("[D, G]", deque.toString());

        assertEquals("D", deque.removeFirst());
        assertEquals("G", deque.removeLast());
    }

    public void testDescendingIterator() {
        final List<E> list = new LinkedList<>();
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");
        final Deque<E> deque = new CircularDeque<>(list);

        final Iterator<E> iterator = deque.descendingIterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        assertTrue(deque.isEmpty());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.5";
    }
}
