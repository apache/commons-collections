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

import java.util.LinkedList;
import java.util.Queue;

import junit.framework.Test;

import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.apache.commons.collections4.queue.TransformedQueue;
import org.apache.commons.collections4.queue.UnmodifiableQueue;

/**
 * Tests for QueueUtils factory methods.
 * 
 * @version $Id$
 */
public class QueueUtilsTest extends BulkTest {

    public QueueUtilsTest(final String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(QueueUtilsTest.class);
    }

    // ----------------------------------------------------------------------

    protected Class<?> stringClass = this.getName().getClass();
    protected Predicate<Object> truePredicate = TruePredicate.truePredicate();
    protected Transformer<Object, Object> nopTransformer = TransformerUtils.nopTransformer();

    // ----------------------------------------------------------------------

    public void testUnmodifiableQueue() {
        Queue<Object> queue = QueueUtils.unmodifiableQueue(new LinkedList<Object>());
        assertTrue("Returned object should be an UnmodifiableQueue.", queue instanceof UnmodifiableQueue);
        try {
            QueueUtils.unmodifiableQueue(null);
            fail("Expecting IllegalArgumentException for null queue.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testPredicatedQueue() {
        Queue<Object> queue = QueueUtils.predicatedQueue(new LinkedList<Object>(), truePredicate);
        assertTrue("Returned object should be a PredicatedQueue.", queue instanceof PredicatedQueue);
        try {
            QueueUtils.predicatedQueue(null, truePredicate);
            fail("Expecting IllegalArgumentException for null queue.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
        try {
            QueueUtils.predicatedQueue(new LinkedList<Object>(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testTransformedQueue() {
        Queue<Object> queue = QueueUtils.transformingQueue(new LinkedList<Object>(), nopTransformer);
        assertTrue("Returned object should be an TransformedQueue.", queue instanceof TransformedQueue);
        try {
            QueueUtils.transformingQueue(null, nopTransformer);
            fail("Expecting IllegalArgumentException for null queue.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
        try {
            QueueUtils.transformingQueue(new LinkedList<Object>(), null);
            fail("Expecting IllegalArgumentException for null transformer.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

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
