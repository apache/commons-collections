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
package org.apache.commons.collections4;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.apache.commons.collections4.queue.SynchronizedQueue;
import org.apache.commons.collections4.queue.TransformedQueue;
import org.apache.commons.collections4.queue.UnmodifiableQueue;
import org.junit.jupiter.api.Test;

/**
 * Tests for QueueUtils factory methods.
 */
public class QueueUtilsTest {

    protected Predicate<Object> truePredicate = TruePredicate.truePredicate();
    protected Transformer<Object, Object> nopTransformer = TransformerUtils.nopTransformer();

    @Test
    public void testSynchronizedQueue() {
        final Queue<Object> queue = QueueUtils.synchronizedQueue(new LinkedList<>());
        assertTrue(queue instanceof SynchronizedQueue, "Returned object should be a SynchronizedQueue.");

        assertThrows(NullPointerException.class, () -> QueueUtils.synchronizedQueue(null),
                "Expecting NullPointerException for null queue.");
    }

    @Test
    public void testUnmodifiableQueue() {
        final Queue<Object> queue = QueueUtils.unmodifiableQueue(new LinkedList<>());
        assertTrue(queue instanceof UnmodifiableQueue, "Returned object should be an UnmodifiableQueue.");

        assertThrows(NullPointerException.class, () -> QueueUtils.unmodifiableQueue(null),
                "Expecting NullPointerException for null queue.");

        assertSame(queue, QueueUtils.unmodifiableQueue(queue), "UnmodifiableQueue shall not be decorated");
    }

    @Test
    public void testPredicatedQueue() {
        final Queue<Object> queue = QueueUtils.predicatedQueue(new LinkedList<>(), truePredicate);
        assertTrue(queue instanceof PredicatedQueue, "Returned object should be a PredicatedQueue.");

        assertThrows(NullPointerException.class, () -> QueueUtils.predicatedQueue(null, truePredicate),
                "Expecting NullPointerException for null queue.");

        assertThrows(NullPointerException.class, () -> QueueUtils.predicatedQueue(new LinkedList<>(), null),
                "Expecting NullPointerException for null predicate.");
    }

    @Test
    public void testTransformedQueue() {
        final Queue<Object> queue = QueueUtils.transformingQueue(new LinkedList<>(), nopTransformer);
        assertTrue(queue instanceof TransformedQueue, "Returned object should be an TransformedQueue.");

        assertThrows(NullPointerException.class, () -> QueueUtils.transformingQueue(null, nopTransformer),
                "Expecting NullPointerException for null queue.");

        assertThrows(NullPointerException.class, () -> QueueUtils.transformingQueue(new LinkedList<>(), null),
                "Expecting NullPointerException for null transformer.");
    }

    @Test
    public void testEmptyQueue() {
        final Queue<Object> queue = QueueUtils.emptyQueue();
        assertTrue(queue instanceof UnmodifiableQueue, "Returned object should be an UnmodifiableQueue.");
        assertTrue(queue.isEmpty(), "Returned queue is not empty.");

        assertThrows(UnsupportedOperationException.class, () -> queue.add(new Object()),
                "Expecting UnsupportedOperationException for empty queue.");
    }

}
