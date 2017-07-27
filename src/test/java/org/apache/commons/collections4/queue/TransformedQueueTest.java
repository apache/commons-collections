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
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.collection.TransformedCollectionTest;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link TransformedQueue} implementation.
 *
 * @since 4.0
 * @version $Id$
 */
public class TransformedQueueTest<E> extends AbstractQueueTest<E> {

    public TransformedQueueTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public Queue<E> makeConfirmedCollection() {
        return new LinkedList<>();
    }

    @Override
    public Queue<E> makeConfirmedFullCollection() {
        final Queue<E> list = new LinkedList<>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Queue<E> makeObject() {
        return TransformedQueue.transformingQueue(new LinkedList<E>(),
                (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Queue<E> makeFullCollection() {
        final Queue<E> list = new LinkedList<>();
        list.addAll(Arrays.asList(getFullElements()));
        return TransformedQueue.transformingQueue(list, (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    //-----------------------------------------------------------------------
    public void testTransformedQueue() {
        final Queue<Object> queue = TransformedQueue.transformingQueue(new LinkedList<>(),
                TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, queue.size());
        final Object[] elements = new Object[] { "1", "3", "5", "7", "2", "4", "6" };
        for (int i = 0; i < elements.length; i++) {
            queue.add(elements[i]);
            assertEquals(i + 1, queue.size());
            assertEquals(true, queue.contains(Integer.valueOf((String) elements[i])));
            assertEquals(false, queue.contains(elements[i]));
        }

        assertEquals(false, queue.remove(elements[0]));
        assertEquals(true, queue.remove(Integer.valueOf((String) elements[0])));

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testTransformedQueue_decorateTransform() {
        final Queue originalQueue = new LinkedList();
        final Object[] elements = new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        Collections.addAll(originalQueue, elements);
        final Queue<?> queue = TransformedQueue.transformedQueue(originalQueue,
                TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(elements.length, queue.size());
        for (final Object el : elements) {
            assertEquals(true, queue.contains(Integer.valueOf((String) el)));
            assertEquals(false, queue.contains(el));
        }

        assertEquals(false, queue.remove(elements[0]));
        assertEquals(true, queue.remove(Integer.valueOf((String) elements[0])));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//  public void testCreate() throws Exception {
//      resetEmpty();
//      writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedQueue.emptyCollection.version4.obj");
//      resetFull();
//      writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedQueue.fullCollection.version4.obj");
//  }

}
