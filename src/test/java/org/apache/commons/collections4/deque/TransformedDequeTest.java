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

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollectionTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Extension of {@link AbstractDequeTest} for exercising the
 * {@link TransformedDeque} implementation.
 *
 * @since 4.5
 */
public class TransformedDequeTest<E> extends AbstractDequeTest<E>  {

    /**
     * JUnit constructor.
     *
     * @param testName the test class name
     */
    public TransformedDequeTest(final String testName) {
        super(testName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Deque<E> makeObject() {
        return TransformedDeque.transformingDeque(new LinkedList<E>(),
                (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Deque<E> makeFullCollection() {
        final Deque<E> list = new LinkedList<>();
        list.addAll(Arrays.asList(getFullElements()));
        return TransformedDeque.transformingDeque(list, (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    public void testTransformedDeque() {
        final Deque<Object> deque = TransformedDeque.transformingDeque(new LinkedList<>(),
                TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, deque.size());
        final Object[] elements = new Object[] { "1", "3", "5", "7", "2", "4", "6" };
        for (int i = 0; i < elements.length; i++) {
            deque.add(elements[i]);
            assertEquals(i + 1, deque.size());
            assertEquals(true, deque.contains(Integer.valueOf((String) elements[i])));
            assertEquals(false, deque.contains(elements[i]));
        }

        assertEquals(false, deque.remove(elements[0]));
        assertEquals(true, deque.remove(Integer.valueOf((String) elements[0])));

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testTransformedDeque_decorateTransform() {
        final Deque originalDeque = new LinkedList();
        final Object[] elements = new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        Collections.addAll(originalDeque, elements);
        final Deque<?> deque = TransformedDeque.transformedDeque(originalDeque,
                TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(elements.length, deque.size());
        for (final Object el : elements) {
            assertEquals(true, deque.contains(Integer.valueOf((String) el)));
            assertEquals(false, deque.contains(el));
        }

        assertEquals(false, deque.remove(elements[0]));
        assertEquals(true, deque.remove(Integer.valueOf((String) elements[0])));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.5";
    }
}
