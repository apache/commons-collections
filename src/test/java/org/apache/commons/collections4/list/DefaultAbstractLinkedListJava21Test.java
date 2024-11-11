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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test case for {@link AbstractLinkedListJava21}.
 */
public class DefaultAbstractLinkedListJava21Test<E> extends AbstractListTest<E> {

    private static class DefaultAbstractLinkedListJava21<E> extends AbstractLinkedListJava21<E> {
        DefaultAbstractLinkedListJava21() {
            init();
        }

        /**
         * Deserializes an instance from an ObjectInputStream.
         *
         * @param in The source ObjectInputStream.
         * @throws IOException            Any of the usual Input/Output related exceptions.
         * @throws ClassNotFoundException A class of a serialized object cannot be found.
         */
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            doReadObject(in);
        }

        /**
         * Serializes this object to an ObjectOutputStream.
         *
         * @param out the target ObjectOutputStream.
         * @throws IOException thrown when an I/O errors occur writing to the target stream.
         */
        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            doWriteObject(out);
        }
    }

    protected void checkNodes() {
        final AbstractLinkedListJava21<E> list = getCollection();
        for (int i = 0; i < list.size; i++) {
            assertEquals(list.getNode(i, false).next, list.getNode(i + 1, true));
            if (i < list.size - 1) {
                assertEquals(list.getNode(i + 1, false).previous, list.getNode(i, false));
            }
        }
    }

    @Override
    public AbstractLinkedListJava21<E> getCollection() {
        return (AbstractLinkedListJava21<E>) super.getCollection();
    }

    @Override
    public String getCompatibilityVersion() {
        return null;
    }

    @Override
    public List<E> makeObject() {
        return new DefaultAbstractLinkedListJava21<>();
    }

    @Override
    protected boolean skipSerializedCanonicalTests() {
        return true;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddNodeAfter() {
        resetEmpty();
        final AbstractLinkedListJava21<E> list = getCollection();
        if (!isAddSupported()) {
            assertThrows(UnsupportedOperationException.class, () -> list.addFirst(null));
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
    public void testGetNode() {
        resetEmpty();
        final AbstractLinkedListJava21<E> list = getCollection();
        // get marker
        assertEquals(list.getNode(0, true).previous, list.getNode(0, true).next);
        assertThrows(IndexOutOfBoundsException.class, () -> list.getNode(0, false), "Expecting IndexOutOfBoundsException.");
        list.addAll(Arrays.asList((E[]) new String[] { "value1", "value2" }));
        checkNodes();
        list.addFirst((E) "value0");
        checkNodes();
        list.removeNode(list.getNode(1, false));
        checkNodes();
        assertThrows(IndexOutOfBoundsException.class, () -> list.getNode(2, false), "Expecting IndexOutOfBoundsException.");
        assertThrows(IndexOutOfBoundsException.class, () -> list.getNode(-1, false), "Expecting IndexOutOfBoundsException.");
        assertThrows(IndexOutOfBoundsException.class, () -> list.getNode(3, true), "Expecting IndexOutOfBoundsException.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveFirst() {
        resetEmpty();
        final AbstractLinkedListJava21<E> list = getCollection();
        if (!isRemoveSupported()) {
            assertThrows(UnsupportedOperationException.class, list::removeFirst);
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
        final AbstractLinkedListJava21<E> list = getCollection();
        if (!isRemoveSupported()) {
            assertThrows(UnsupportedOperationException.class, list::removeLast);
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
    public void testRemoveNode() {
        resetEmpty();
        if (!isAddSupported() || !isRemoveSupported()) {
            return;
        }
        final AbstractLinkedListJava21<E> list = getCollection();

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
    public void testSubList() {
        final List<E> list = makeObject();
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        assertEquals("[A, B, C, D, E]", list.toString());
        assertEquals("[A, B, C, D, E]", list.subList(0, 5).toString());
        assertEquals("[B, C, D, E]", list.subList(1, 5).toString());
        assertEquals("[C, D, E]", list.subList(2, 5).toString());
        assertEquals("[D, E]", list.subList(3, 5).toString());
        assertEquals("[E]", list.subList(4, 5).toString());
        assertEquals("[]", list.subList(5, 5).toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubListAddBegin() {
        final List<E> list = makeObject();
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        final List<E> sublist = list.subList(0, 0);
        sublist.add((E) "a");
        assertEquals("[a, A, B, C, D, E]", list.toString());
        assertEquals("[a]", sublist.toString());
        sublist.add((E) "b");
        assertEquals("[a, b, A, B, C, D, E]", list.toString());
        assertEquals("[a, b]", sublist.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubListAddEnd() {
        final List<E> list = makeObject();
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        final List<E> sublist = list.subList(5, 5);
        sublist.add((E) "F");
        assertEquals("[A, B, C, D, E, F]", list.toString());
        assertEquals("[F]", sublist.toString());
        sublist.add((E) "G");
        assertEquals("[A, B, C, D, E, F, G]", list.toString());
        assertEquals("[F, G]", sublist.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubListAddMiddle() {
        final List<E> list = makeObject();
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        final List<E> sublist = list.subList(1, 3);
        sublist.add((E) "a");
        assertEquals("[A, B, C, a, D, E]", list.toString());
        assertEquals("[B, C, a]", sublist.toString());
        sublist.add((E) "b");
        assertEquals("[A, B, C, a, b, D, E]", list.toString());
        assertEquals("[B, C, a, b]", sublist.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubListRemove() {
        final List<E> list = makeObject();
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        final List<E> sublist = list.subList(1, 4);
        assertEquals("[B, C, D]", sublist.toString());
        assertEquals("[A, B, C, D, E]", list.toString());
        sublist.remove("C");
        assertEquals("[B, D]", sublist.toString());
        assertEquals("[A, B, D, E]", list.toString());
        sublist.remove(1);
        assertEquals("[B]", sublist.toString());
        assertEquals("[A, B, E]", list.toString());
        sublist.clear();
        assertEquals("[]", sublist.toString());
        assertEquals("[A, E]", list.toString());
    }
}
