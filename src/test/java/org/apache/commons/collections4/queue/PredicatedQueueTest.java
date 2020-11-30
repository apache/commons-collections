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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.PredicatedCollectionTest;
import org.apache.commons.collections4.functors.TruePredicate;

/**
 * Extension of {@link PredicatedCollectionTest} for exercising the
 * {@link PredicatedQueue} implementation.
 *
 * @since 4.0
 */
public class PredicatedQueueTest<E> extends AbstractQueueTest<E> {

    public PredicatedQueueTest(final String testName) {
        super(testName);
    }

    //---------------------------------------------------------------

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    protected Queue<E> decorateCollection(final Queue<E> queue, final Predicate<E> predicate) {
        return PredicatedQueue.predicatedQueue(queue, predicate);
    }

    @Override
    public Queue<E> makeObject() {
        return decorateCollection(new LinkedList<E>(), truePredicate);
    }

    @Override
    public Queue<E> makeFullCollection() {
        final Queue<E> queue = new LinkedList<>(Arrays.asList(getFullElements()));
        return decorateCollection(queue, truePredicate);
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new LinkedList<>();
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final List<E> list = new LinkedList<>(Arrays.asList(getFullElements()));
        return list;
    }

    //------------------------------------------------------------

    protected Predicate<E> testPredicate = o -> o instanceof String;

    public Queue<E> makeTestQueue() {
        return decorateCollection(new LinkedList<E>(), testPredicate);
    }

    @SuppressWarnings("unchecked")
    public void testGet() {
        final Queue<E> queue = makeTestQueue();

        assertNull(queue.peek());

        queue.add((E) "one");
        queue.add((E) "two");
        queue.add((E) "three");
        assertEquals("Queue get", "one", queue.peek());
    }

    @SuppressWarnings("unchecked")
    public void testRemove() {
        final Queue<E> queue = makeTestQueue();
        queue.add((E) "one");
        assertEquals("Queue get", "one", queue.poll());
        assertNull(queue.peek());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedQueue.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedQueue.fullCollection.version4.obj");
//    }

}
