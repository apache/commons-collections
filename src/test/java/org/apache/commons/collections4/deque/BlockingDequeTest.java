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
import java.util.LinkedList;
import java.util.Objects;

/**
 * Extension of {@link AbstractDequeTest} for exercising the
 * {@link BlockingDeque} implementation.
 *
 * @since 4.5
 */
public class BlockingDequeTest<E> extends AbstractDequeTest<E> {
    /**
     * JUnit constructor.
     *
     * @param testName the test class name
     */
    public BlockingDequeTest(String testName) {
        super(testName);
    }

    @Override
    public Deque<E> makeObject() {
        return new BlockingDeque(new LinkedList<E>());
    }

    public void testPutFirst() {
        if (!isAddSupported()) {
            return;
        }

        BlockingDeque<E> deque = (BlockingDeque<E>) makeObject();

        final E[] elements = getFullElements();
        int size = 0;
        try {
            for (final E element : elements) {
                deque.putFirst(element);
                size++;
                assertEquals("Deque size should grow after add", size, deque.size());
                assertTrue("Deque should contain added element", Objects.equals(element, deque.getFirst()));
            }

        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    public void testPutLast() {
        if (!isAddSupported()) {
            return;
        }

        BlockingDeque<E> deque = (BlockingDeque<E>) makeObject();

        final E[] elements = getFullElements();
        int size = 0;
        try {
            for (final E element : elements) {
                deque.putLast(element);
                size++;
                assertEquals("Deque size should grow after add", size, deque.size());
                assertTrue("Deque should contain added element", Objects.equals(element, deque.getLast()));
            }

        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    public void testTakeFirst() {
        if (!isRemoveSupported()) {
            return;
        }

        BlockingDeque<E> deque = new BlockingDeque<E>(makeFullCollection());
        int size = deque.size();

        assertTrue("Deque should contain elements",size > 0);

        final E[] elements = getFullElements();
        try {
            for (final E element : elements) {
                E e = deque.takeFirst();
                size--;
                assertEquals("Deque size should decrease",size, deque.size());
                assertTrue(Objects.equals(element, e));
            }
            assertEquals("Deque should be empty", 0, deque.size());

        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    public void testTakeLast() {
        if (!isRemoveSupported()) {
            return;
        }

        BlockingDeque<E> deque = new BlockingDeque<E>(makeFullCollection());
        int size = deque.size();

        assertTrue("Deque should contain elements",size > 0);

        final E[] elements = getFullElements();
        try {
            for (int i = size-1; i >= 0; i--) {
                E e = deque.takeLast();
                assertEquals("Deque size should decrease", i, deque.size());
                assertTrue(Objects.equals(elements[i], e));
            }
            assertEquals("Deque should be empty", 0, deque.size());

        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }
    }
    @Override
    public String getCompatibilityVersion() {
        return "4.5";
    }
}
