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

import java.util.Arrays;
import java.util.Deque;

import java.util.LinkedList;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.NotNullPredicate;
import org.apache.commons.collections4.functors.TruePredicate;

/**
 * Extension of {@link AbstractDequeTest} for exercising the
 * {@link PredicatedDeque} implementation.
 *
 * @since 4.5
 */
public class PredicatedDequeTest<E> extends AbstractDequeTest<E> {
    /**
     * JUnit constructor.
     *
     * @param testName the test class name
     */
    public PredicatedDequeTest(String testName) {
        super(testName);
    }

    protected Predicate<E> notNullPredicate = NotNullPredicate.<E>notNullPredicate();

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();


    protected Deque<E> decorateDeque(final Deque<E> queue, final Predicate<E> predicate) {
        return PredicatedDeque.predicatedDeque(queue, predicate);
    }

    @Override
    public Deque<E> makeObject() {
        return decorateDeque(new LinkedList<E>(), truePredicate);
    }

    @Override
    public Deque<E> makeFullCollection() {
        final Deque<E> deque = new LinkedList<>();
        deque.addAll(Arrays.asList(getFullElements()));
        return decorateDeque(deque, truePredicate);
    }

    public Deque<E> makeTestDeque() {
        return decorateDeque(new LinkedList<E>(), notNullPredicate);
    }

    @SuppressWarnings("unchecked")
    public void testPredicatedDeque() {
        final Deque<E> deque = makeTestDeque();

        assertNull(deque.peek());

        try {
            deque.addFirst(null);
            fail("Deque.addFirst should throw IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // expected
        }

        deque.addFirst((E) "one");
        deque.addFirst((E) "two");

        assertEquals("Deque get", "two", deque.pollFirst());
        assertEquals("Deque get", "one", deque.pollFirst());
        assertNull(deque.pollFirst());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.5";
    }

}
