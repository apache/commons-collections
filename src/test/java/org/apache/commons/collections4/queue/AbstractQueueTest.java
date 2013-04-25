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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import org.apache.commons.collections4.collection.AbstractCollectionTest;

/**
 * Abstract test class for {@link java.util.Queue} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your {@link Queue} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Queue} fails or override one of the
 * protected methods from AbstractCollectionTest.
 *
 * TODO: add more tests for Queue interface
 *
 * @since 4.0
 * @version $Id$
 */
public abstract class AbstractQueueTest<E> extends AbstractCollectionTest<E> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractQueueTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
    /**
     *  Verifies that the test queue implementation matches the confirmed queue
     *  implementation.
     */
    @Override
    public void verify() {
        super.verify();
        final Iterator<E> iterator1 = getCollection().iterator();
        final Iterator<E> iterator2 = getConfirmed().iterator();
        while (iterator2.hasNext()) {
            assertTrue(iterator1.hasNext());
            final Object o1 = iterator1.next();
            final Object o2 = iterator2.next();
            assertEquals(o1, o2);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an empty {@link ArrayList}.
     */
    @Override
    public Collection<E> makeConfirmedCollection() {
        final ArrayList<E> list = new ArrayList<E>();
        return list;
    }

    /**
     * Returns a full {@link ArrayList}.
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final ArrayList<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    /**
     * Returns {@link #makeObject()}.
     *
     * @return an empty queue to be used for testing
     */
    @Override
    public abstract Queue<E> makeObject();

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

    //-----------------------------------------------------------------------
    /**
     * Returns the {@link #collection} field cast to a {@link Queue}.
     *
     * @return the collection field as a Queue
     */
    @Override
    public Queue<E> getCollection() {
        return (Queue<E>) super.getCollection();
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testEmptyQueueSerialization() throws IOException, ClassNotFoundException {
        final Queue<E> queue = makeObject();
        if (!(queue instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] objekt = writeExternalFormToBytes((Serializable) queue);
        final Queue<E> queue2 = (Queue<E>) readExternalFormFromBytes(objekt);

        assertEquals("Both queues are empty", 0, queue.size());
        assertEquals("Both queues are empty", 0, queue2.size());
    }

    @SuppressWarnings("unchecked")
    public void testFullQueueSerialization() throws IOException, ClassNotFoundException {
        final Queue<E> queue = makeFullCollection();
        final int size = getFullElements().length;
        if (!(queue instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] objekt = writeExternalFormToBytes((Serializable) queue);
        final Queue<E> queue2 = (Queue<E>) readExternalFormFromBytes(objekt);

        assertEquals("Both queues are same size", size, queue.size());
        assertEquals("Both queues are same size", size, queue2.size());
    }

    /**
     * Compare the current serialized form of the Queue
     * against the canonical version in SVN.
     */
    @SuppressWarnings("unchecked")
    public void testEmptyQueueCompatibility() throws IOException, ClassNotFoundException {
        /**
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
            assertEquals("Queue is empty", 0, queue2.size());
        }
    }

    /**
     * Compare the current serialized form of the Queue
     * against the canonical version in SVN.
     */
    @SuppressWarnings("unchecked")
    public void testFullQueueCompatibility() throws IOException, ClassNotFoundException {
        /**
         * Create canonical objects with this code
        Queue queue = makeFullQueue();
        if (!(queue instanceof Serializable)) return;

        writeExternalFormToDisk((Serializable) queue, getCanonicalFullCollectionName(queue));
        */

        // test to make sure the canonical form has been preserved
        final Queue<E> queue = makeFullCollection();
        if(queue instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final Queue<E> queue2 = (Queue<E>) readExternalFormFromDisk(getCanonicalFullCollectionName(queue));
            assertEquals("Queues is the right size",queue.size(), queue2.size());
        }
    }

}
