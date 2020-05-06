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
package org.apache.commons.collections4.queue;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.collection.AbstractCollectionTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Queue;
import java.util.NoSuchElementException;

/**
 * Test cases for AbstractQueueDecorator.
 *
 * @since 4.5
 */
public class QueueDecoratorTest<E> extends AbstractCollectionTest<E> {
    /**
     * JUnit constructor.
     *
     * @param testName the test class name
     */
    public QueueDecoratorTest(String testName) {
        super(testName);
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<>();
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final ArrayList<E> list = new ArrayList<>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    @Override
    public Queue<E> makeObject() {
        return new QueueDecorator<E>( new LinkedList<E>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queue<E> makeFullCollection() {
        final Queue<E> queue = makeObject();
        queue.addAll(Arrays.asList(getFullElements()));
        return queue;
    }

    /**
     * @return the collection field as a Queue
     */
    @Override
    public Queue<E> getCollection() {
        return (Queue<E>) super.getCollection();
    }

    /**
     *  Tests {@link AbstractQueueDecorator#offer(Object)}.
     */
    public void testAbstractQueueDecoratorOffer() {
        if (!isAddSupported()) {
            return;
        }
        final E[] elements = getFullElements();
        for (final E element : elements) {
            resetEmpty();
            final boolean r = getCollection().offer(element);
            getConfirmed().add(element);
            verify();
            assertTrue("Empty queue changed after add", r);
            assertEquals("Queue size is 1 after first add", 1, getCollection().size());
        }

        resetEmpty();
        int size = 0;
        for (final E element : elements) {
            final boolean r = getCollection().offer(element);
            getConfirmed().add(element);
            verify();
            if (r) {
                size++;
            }
            assertEquals("Queue size should grow after add", size, getCollection().size());
            assertTrue("Queue should contain added element", getCollection().contains(element));
        }
    }

    /**
     *  Tests {@link AbstractQueueDecorator#poll()}.
     */
    public void testAbstractQueueDecoratorPoll() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        E element = getCollection().poll();
        assertNull(element);

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            element = getCollection().poll();
            final boolean success = getConfirmed().remove(element);
            assertTrue("poll should return correct element", success);
            verify();
        }

        element = getCollection().poll();
        assertNull(element);
    }

    /**
     *  Tests {@link AbstractQueueDecorator#peek()}.
     */
    public void testAbstractQueueDecoratorPeek() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        E element = getCollection().peek();
        assertNull(element);

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            element = getCollection().peek();

            if (!isNullSupported()) {
                assertNotNull(element);
            }

            assertTrue(getConfirmed().contains(element));

            getCollection().remove(element);
            getConfirmed().remove(element);

            verify();
        }

        element = getCollection().peek();
        assertNull(element);
    }

    /**
     *  Tests {@link AbstractQueueDecorator#remove()}.
     */
    public void testAbstractQueueDecoratorRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            getCollection().remove();
        });
        assertNull(exception.getMessage());

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            final E element = getCollection().remove();
            final boolean success = getConfirmed().remove(element);
            assertTrue("remove should return correct element", success);
            verify();
        }

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> {
            getCollection().element();
        });
        assertNull(noSuchElementException.getMessage());
    }

    public void testCreate() throws Exception {
        resetEmpty();
        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/QueueDecorator.emptyCollection.version4.obj");
        resetFull();
        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/QueueDecorator.fullCollection.version4.obj");
    }

}

class QueueDecorator<E> extends AbstractQueueDecorator<E> {
    /**
     * Constructor that wraps (not copies).
     *
     * @param queue  the queue to decorate, must not be null
     * @throws NullPointerException if queue is null
     */
    protected QueueDecorator(final Queue<E> queue) {
        super(queue);
    }
}
