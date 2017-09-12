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

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.apache.commons.collections4.queue.TransformedQueue;
import org.apache.commons.collections4.queue.UnmodifiableQueue;
import org.junit.Test;

/**
 * Tests for QueueUtils factory methods.
 * 
 */
public class QueueUtilsTest {

    protected Predicate<Object> truePredicate = TruePredicate.truePredicate();
    protected Transformer<Object, Object> nopTransformer = TransformerUtils.nopTransformer();

    // ----------------------------------------------------------------------

    @Test
    public void testUnmodifiableQueue() {
        Queue<Object> queue = QueueUtils.unmodifiableQueue(new LinkedList<>());
        assertTrue("Returned object should be an UnmodifiableQueue.", queue instanceof UnmodifiableQueue);
        try {
            QueueUtils.unmodifiableQueue(null);
            fail("Expecting NullPointerException for null queue.");
        } catch (final NullPointerException ex) {
            // expected
        }
        
        assertSame("UnmodifiableQueue shall not be decorated", queue, QueueUtils.unmodifiableQueue(queue));
    }

    @Test
    public void testPredicatedQueue() {
        Queue<Object> queue = QueueUtils.predicatedQueue(new LinkedList<>(), truePredicate);
        assertTrue("Returned object should be a PredicatedQueue.", queue instanceof PredicatedQueue);
        try {
            QueueUtils.predicatedQueue(null, truePredicate);
            fail("Expecting NullPointerException for null queue.");
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            QueueUtils.predicatedQueue(new LinkedList<>(), null);
            fail("Expecting NullPointerException for null predicate.");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testTransformedQueue() {
        Queue<Object> queue = QueueUtils.transformingQueue(new LinkedList<>(), nopTransformer);
        assertTrue("Returned object should be an TransformedQueue.", queue instanceof TransformedQueue);
        try {
            QueueUtils.transformingQueue(null, nopTransformer);
            fail("Expecting NullPointerException for null queue.");
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            QueueUtils.transformingQueue(new LinkedList<>(), null);
            fail("Expecting NullPointerException for null transformer.");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testEmptyQueue() {
        Queue<Object> queue = QueueUtils.emptyQueue();
        assertTrue("Returned object should be an UnmodifiableQueue.", queue instanceof UnmodifiableQueue);
        assertTrue("Returned queue is not empty.", queue.isEmpty());

        try {
            queue.add(new Object());
            fail("Expecting UnsupportedOperationException for empty queue.");
        } catch (final UnsupportedOperationException ex) {
            // expected
        }
    }

}
