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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link java.util.Queue}.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your {@link Queue} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Queue} fails or override one of the
 * protected methods from AbstractCollectionTest.
 */
public abstract class AbstractQueueTest<E> extends AbstractCollectionTest<E> {

    /**
     * Returns the {@link #collection} field cast to a {@link Queue}.
     *
     * @return the collection field as a Queue
     */
    @Override
    public Queue<E> getCollection() {
        return (Queue<E>) super.getCollection();
    }

    /**
     *  Returns true if the collections produced by
     *  {@link #makeObject()} and {@link #makeFullCollection()}
     *  support the <code>set operation.<p>
     *  Default implementation returns true.  Override if your collection
     *  class does not support set.
     */
    public boolean isSetSupported() {
        return true;
    }

    /**
     * Returns an empty {@link ArrayList}.
     */
    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<>();
    }

    /**
     * Returns a full {@link ArrayList}.
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        return new ArrayList<>(Arrays.asList(getFullElements()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Queue<E> makeFullCollection() {
        // only works if queue supports optional "addAll(Collection)"
        final Queue<E> queue = makeObject();
        queue.addAll(Arrays.asList(getFullElements()));
        return queue;
    }

    /**
     * Returns {@link #makeObject()}.
     *
     * @return an empty queue to be used for testing
     */
    @Override
    public abstract Queue<E> makeObject();

    /**
     * Compare the current serialized form of the Queue
     * against the canonical version in SCM.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyQueueCompatibility() throws IOException, ClassNotFoundException {
        /*
         * Create canonical objects with this code
        Queue queue = makeEmptyQueue();
        if (!(queue instanceof Serializable)) return;

        writeExternalFormToDisk((Serializable) queue, getCanonicalEmptyCollectionName(queue));
        */

        // test to make sure the canonical form has been preserved
        final Queue<E> queue = makeObject();
        if (queue instanceof Serializable && !skipSerializedCanonicalTests()
                && isTestSerialization()) {
            final Queue<E> queue2 = (Queue<E>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(queue));
            assertEquals(0, queue2.size(), "Queue is empty");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyQueueSerialization() throws IOException, ClassNotFoundException {
        final Queue<E> queue = makeObject();
        if (!(queue instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] object = writeExternalFormToBytes((Serializable) queue);
        final Queue<E> queue2 = (Queue<E>) readExternalFormFromBytes(object);

        assertEquals(0, queue.size(), "Both queues are empty");
        assertEquals(0, queue2.size(), "Both queues are empty");
    }

    /**
     * Compare the current serialized form of the Queue
     * against the canonical version in SCM.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testFullQueueCompatibility() throws IOException, ClassNotFoundException {
        /*
         * Create canonical objects with this code
        Queue queue = makeFullQueue();
        if (!(queue instanceof Serializable)) return;

        writeExternalFormToDisk((Serializable) queue, getCanonicalFullCollectionName(queue));
        */

        // test to make sure the canonical form has been preserved
        final Queue<E> queue = makeFullCollection();
        if (queue instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final Queue<E> queue2 = (Queue<E>) readExternalFormFromDisk(getCanonicalFullCollectionName(queue));
            assertEquals(queue.size(), queue2.size(), "Queues are not the right size");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFullQueueSerialization() throws IOException, ClassNotFoundException {
        final Queue<E> queue = makeFullCollection();
        final int size = getFullElements().length;
        if (!(queue instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] object = writeExternalFormToBytes((Serializable) queue);
        final Queue<E> queue2 = (Queue<E>) readExternalFormFromBytes(object);

        assertEquals(size, queue.size(), "Both queues are same size");
        assertEquals(size, queue2.size(), "Both queues are same size");
    }

    /**
     *  Tests {@link Queue#element()}.
     */
    @Test
    public void testQueueElement() {
        resetEmpty();

        assertThrows(NoSuchElementException.class, () -> getCollection().element(),
                "Queue.element should throw NoSuchElementException");

        resetFull();

        assertTrue(getConfirmed().contains(getCollection().element()));

        if (!isRemoveSupported()) {
            return;
        }

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            final E element = getCollection().element();

            if (!isNullSupported()) {
                assertNotNull(element);
            }

            assertTrue(getConfirmed().contains(element));

            getCollection().remove(element);
            getConfirmed().remove(element);

            verify();
        }

        assertThrows(NoSuchElementException.class, () -> getCollection().element(),
                "Queue.element should throw NoSuchElementException");
    }

    /**
     *  Tests {@link Queue#offer(Object)}.
     */
    @Test
    public void testQueueOffer() {
        if (!isAddSupported()) {
            return;
        }

        final E[] elements = getFullElements();
        for (final E element : elements) {
            resetEmpty();
            final boolean r = getCollection().offer(element);
            getConfirmed().add(element);
            verify();
            assertTrue(r, "Empty queue changed after add");
            assertEquals(1, getCollection().size(), "Queue size is 1 after first add");
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
            assertEquals(size, getCollection().size(), "Queue size should grow after add");
            assertTrue(getCollection().contains(element), "Queue should contain added element");
        }
    }

    /**
     *  Tests {@link Queue#peek()}.
     */
    @Test
    public void testQueuePeek() {
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
     *  Tests {@link Queue#poll()}.
     */
    @Test
    public void testQueuePoll() {
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
            assertTrue(success, "poll should return correct element");
            verify();
        }

        element = getCollection().poll();
        assertNull(element);
    }

    /**
     *  Tests {@link Queue#remove()}.
     */
    @Test
    public void testQueueRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        assertThrows(NoSuchElementException.class, () -> getCollection().remove(),
                "Queue.remove should throw NoSuchElementException");

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            final E element = getCollection().remove();
            final boolean success = getConfirmed().remove(element);
            assertTrue(success, "remove should return correct element");
            verify();
        }

        assertThrows(NoSuchElementException.class, () -> getCollection().element(),
                "Queue.remove should throw NoSuchElementException");
    }

    /**
     *  Verifies that the test queue implementation matches the confirmed queue
     *  implementation.
     */
    @Override
    public void verify() {
        super.verify();
        final Iterator<E> iterator1 = getCollection().iterator();
        for (final E e : getConfirmed()) {
            assertTrue(iterator1.hasNext());
            final Object o1 = iterator1.next();
            final Object o2 = e;
            assertEquals(o1, o2);
        }
    }

}
