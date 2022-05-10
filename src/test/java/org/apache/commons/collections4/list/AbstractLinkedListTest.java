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
package org.apache.commons.collections4.list;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Test case for {@link AbstractLinkedList}.
 */
public abstract class AbstractLinkedListTest<E> extends AbstractListTest<E> {

    public AbstractLinkedListTest(final String testName) {
        super(testName);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveFirst() {
        resetEmpty();
        final AbstractLinkedList<E> list = getCollection();
        if (!isRemoveSupported()) {
            try {
                list.removeFirst();
            } catch (final UnsupportedOperationException ex) {}
        }

        list.addAll(Arrays.asList((E[]) new String[] { "value1", "value2" }));
        assertEquals("value1", list.removeFirst());
        checkNodes();
        list.addLast((E) "value3");
        checkNodes();
        assertEquals("value2", list.removeFirst());
        assertEquals("value3", list.removeFirst());
        checkNodes();
        list.addLast((E) "value4");
        checkNodes();
        assertEquals("value4", list.removeFirst());
        checkNodes();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveLast() {
        resetEmpty();
        final AbstractLinkedList<E> list = getCollection();
        if (!isRemoveSupported()) {
            try {
                list.removeLast();
            } catch (final UnsupportedOperationException ex) {}
        }

        list.addAll(Arrays.asList((E[]) new String[] { "value1", "value2" }));
        assertEquals("value2", list.removeLast());
        list.addFirst((E) "value3");
        checkNodes();
        assertEquals("value1", list.removeLast());
        assertEquals("value3", list.removeLast());
        list.addFirst((E) "value4");
        checkNodes();
        assertEquals("value4", list.removeFirst());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddNodeAfter() {
        resetEmpty();
        final AbstractLinkedList<E> list = getCollection();
        if (!isAddSupported()) {
            try {
                list.addFirst(null);
            } catch (final UnsupportedOperationException ex) {}
        }

        list.addFirst((E) "value1");
        list.addNodeAfter(list.getNode(0, false), (E) "value2");
        assertEquals("value1", list.getFirst());
        assertEquals("value2", list.getLast());
        list.removeFirst();
        checkNodes();
        list.addNodeAfter(list.getNode(0, false), (E) "value3");
        checkNodes();
        assertEquals("value2", list.getFirst());
        assertEquals("value3", list.getLast());
        list.addNodeAfter(list.getNode(0, false), (E) "value4");
        checkNodes();
        assertEquals("value2", list.getFirst());
        assertEquals("value3", list.getLast());
        assertEquals("value4", list.get(1));
        list.addNodeAfter(list.getNode(2, false), (E) "value5");
        checkNodes();
        assertEquals("value2", list.getFirst());
        assertEquals("value4", list.get(1));
        assertEquals("value3", list.get(2));
        assertEquals("value5", list.getLast());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveNode() {
        resetEmpty();
        if (!isAddSupported() || !isRemoveSupported()) {
            return;
        }
        final AbstractLinkedList<E> list = getCollection();

        list.addAll(Arrays.asList((E[]) new String[] { "value1", "value2" }));
        list.removeNode(list.getNode(0, false));
        checkNodes();
        assertEquals("value2", list.getFirst());
        assertEquals("value2", list.getLast());
        list.addFirst((E) "value1");
        list.addFirst((E) "value0");
        checkNodes();
        list.removeNode(list.getNode(1, false));
        assertEquals("value0", list.getFirst());
        assertEquals("value2", list.getLast());
        checkNodes();
        list.removeNode(list.getNode(1, false));
        assertEquals("value0", list.getFirst());
        assertEquals("value0", list.getLast());
        checkNodes();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetNode() {
        resetEmpty();
        final AbstractLinkedList<E> list = getCollection();
        // get marker
        assertEquals(list.getNode(0, true).previous, list.getNode(0, true).next);
        assertThrows(IndexOutOfBoundsException.class, () -> list.getNode(0, false),
                "Expecting IndexOutOfBoundsException.");
        list.addAll( Arrays.asList((E[]) new String[]{"value1", "value2"}));
        checkNodes();
        list.addFirst((E) "value0");
        checkNodes();
        list.removeNode(list.getNode(1, false));
        checkNodes();
        assertThrows(IndexOutOfBoundsException.class, () -> list.getNode(2, false),
                "Expecting IndexOutOfBoundsException.");
        assertThrows(IndexOutOfBoundsException.class, () -> list.getNode(-1, false),
                "Expecting IndexOutOfBoundsException.");
        assertThrows(IndexOutOfBoundsException.class, () -> list.getNode(3, true),
                "Expecting IndexOutOfBoundsException.");
    }

    protected void checkNodes() {
        final AbstractLinkedList<E> list = getCollection();
        for (int i = 0; i < list.size; i++) {
            assertEquals(list.getNode(i, false).next, list.getNode(i + 1, true));
            if (i < list.size - 1) {
                assertEquals(list.getNode(i + 1, false).previous,
                    list.getNode(i, false));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractLinkedList<E> getCollection() {
        return (AbstractLinkedList<E>) super.getCollection();
    }

}
