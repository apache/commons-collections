/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.queue;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link UnmodifiableQueue} implementation.
 */
public class UnmodifiableQueueTest<E> extends AbstractQueueTest<E> {

    @Override
    public Queue<E> getCollection() {
        return super.getCollection();
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isNullSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new LinkedList<>();
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        return new LinkedList<>(Arrays.asList(getFullElements()));
    }

    @Override
    public Queue<E> makeFullCollection() {
        final Queue<E> queue = new LinkedList<>(Arrays.asList(getFullElements()));
        return UnmodifiableQueue.unmodifiableQueue(queue);
    }

    @Override
    public Queue<E> makeObject() {
        return UnmodifiableQueue.unmodifiableQueue(new LinkedList<>());
    }

    @Test
    void testDecorateFactory() {
        final Queue<E> queue = makeFullCollection();
        assertSame(queue, UnmodifiableQueue.unmodifiableQueue(queue));

        assertThrows(NullPointerException.class, () -> UnmodifiableQueue.unmodifiableQueue(null));
    }

    @Test
    void testOffer() {
        final Queue<E> queue = makeFullCollection();
        final E e = null;
        assertThrows(UnsupportedOperationException.class, () -> queue.offer(e));
    }

    @Test
    void testPoll() {
        final Queue<E> queue = makeFullCollection();
        assertThrows(UnsupportedOperationException.class, () -> queue.poll());
    }

    @Test
    @Override
    public void testQueueRemove() {
        resetEmpty();
        assertThrows(UnsupportedOperationException.class, () -> getCollection().remove());
    }

    @Test
    void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullCollection() instanceof Unmodifiable);
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/UnmodifiableQueue.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/UnmodifiableQueue.fullCollection.version4.obj");
//    }

}
