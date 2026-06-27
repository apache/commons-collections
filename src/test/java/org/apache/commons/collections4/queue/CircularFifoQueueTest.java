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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.junit.jupiter.api.Test;

/**
 * Test cases for CircularFifoQueue.
 */
public class CircularFifoQueueTest<E> extends AbstractQueueTest<E> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CircularFifoQueue<E> getCollection() {
        return (CircularFifoQueue<E>) super.getCollection();
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    /**
     * Overridden because CircularFifoQueue isn't fail fast.
     * @return false
     */
    @Override
    public boolean isFailFastSupported() {
        return false;
    }

    /**
     * Overridden because CircularFifoQueue doesn't allow null elements.
     * @return false
     */
    @Override
    public boolean isNullSupported() {
        return false;
    }

    /**
     * Returns an empty ArrayList.
     *
     * @return an empty ArrayList
     */
    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<>();
    }

    /**
     * Returns a full ArrayList.
     *
     * @return a full ArrayList
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final Collection<E> c = makeConfirmedCollection();
        c.addAll(Arrays.asList(getFullElements()));
        return c;
    }

    /**
     * Returns an empty CircularFifoQueue that won't overflow.
     *
     * @return an empty CircularFifoQueue
     */
    @Override
    public Queue<E> makeObject() {
        return new CircularFifoQueue<>(100);
    }

    @Test
    void testAddNull() {
        final CircularFifoQueue<E> b = new CircularFifoQueue<>(2);
        assertThrows(NullPointerException.class, () -> b.add(null));
    }

    /**
     * Tests that the removal operation actually removes the first element.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testCircularFifoQueueCircular() {
        final List<E> list = new ArrayList<>();
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        final Queue<E> queue = new CircularFifoQueue<>(list);

        assertTrue(queue.contains("A"));
        assertTrue(queue.contains("B"));
        assertTrue(queue.contains("C"));

        queue.add((E) "D");

        assertFalse(queue.contains("A"));
        assertTrue(queue.contains("B"));
        assertTrue(queue.contains("C"));
        assertTrue(queue.contains("D"));

        assertEquals("B", queue.peek());
        assertEquals("B", queue.remove());
        assertEquals("C", queue.remove());
        assertEquals("D", queue.remove());
    }

    /**
     * Tests that the removal operation actually removes the first element.
     */
    @Test
    void testCircularFifoQueueRemove() {
        resetFull();
        final int size = getConfirmed().size();
        for (int i = 0; i < size; i++) {
            final Object o1 = getCollection().remove();
            final Object o2 = ((List<?>) getConfirmed()).remove(0);
            assertEquals(o1, o2, "Removed objects should be equal");
            verify();
        }

        assertThrows(NoSuchElementException.class, () -> getCollection().remove(),
                "Empty queue should raise Underflow.");
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    @Test
    void testConstructorException1() {
        assertThrows(IllegalArgumentException.class, () -> new CircularFifoQueue<E>(0));
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    @Test
    void testConstructorException2() {
        assertThrows(IllegalArgumentException.class, () -> new CircularFifoQueue<E>(-20));
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    @Test
    void testConstructorException3() {
        assertThrows(NullPointerException.class, () -> new CircularFifoQueue<E>(null));
    }

    @Test
    void testDefaultSizeAndGetError1() {
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>();
        assertEquals(32, fifo.maxSize());
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");
        assertEquals(5, fifo.size());
        assertThrows(NoSuchElementException.class, () -> fifo.get(5));
    }

    @Test
    void testDefaultSizeAndGetError2() {
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>();
        assertEquals(32, fifo.maxSize());
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");
        assertEquals(5, fifo.size());
        assertThrows(NoSuchElementException.class, () -> fifo.get(-2));
    }

    @Test
    void testGetIndex() {
        resetFull();

        final CircularFifoQueue<E> queue = getCollection();
        final List<E> confirmed = (List<E>) getConfirmed();
        for (int i = 0; i < confirmed.size(); i++) {
            assertEquals(confirmed.get(i), queue.get(i));
        }

        // remove the first two elements and check again
        queue.remove();
        queue.remove();

        for (int i = 0; i < queue.size(); i++) {
            assertEquals(confirmed.get(i + 2), queue.get(i));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveError1() throws Exception {
        // based on bug 33071
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>(5);
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");

        assertEquals("[1, 2, 3, 4, 5]", fifo.toString());

        fifo.remove("3");
        assertEquals("[1, 2, 4, 5]", fifo.toString());

        fifo.remove("4");
        assertEquals("[1, 2, 5]", fifo.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveError2() throws Exception {
        // based on bug 33071
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>(5);
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");
        fifo.add((E) "6");

        assertEquals(5, fifo.size());
        assertEquals("[2, 3, 4, 5, 6]", fifo.toString());

        fifo.remove("3");
        assertEquals("[2, 4, 5, 6]", fifo.toString());

        fifo.remove("4");
        assertEquals("[2, 5, 6]", fifo.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveError3() throws Exception {
        // based on bug 33071
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>(5);
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");

        assertEquals("[1, 2, 3, 4, 5]", fifo.toString());

        fifo.remove("3");
        assertEquals("[1, 2, 4, 5]", fifo.toString());

        fifo.add((E) "6");
        fifo.add((E) "7");
        assertEquals("[2, 4, 5, 6, 7]", fifo.toString());

        fifo.remove("4");
        assertEquals("[2, 5, 6, 7]", fifo.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveError4() throws Exception {
        // based on bug 33071
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>(5);
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");  // end=0
        fifo.add((E) "6");  // end=1
        fifo.add((E) "7");  // end=2

        assertEquals("[3, 4, 5, 6, 7]", fifo.toString());

        fifo.remove("4");  // remove element in middle of array, after start
        assertEquals("[3, 5, 6, 7]", fifo.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveError5() throws Exception {
        // based on bug 33071
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>(5);
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");  // end=0
        fifo.add((E) "6");  // end=1
        fifo.add((E) "7");  // end=2

        assertEquals("[3, 4, 5, 6, 7]", fifo.toString());

        fifo.remove("5");  // remove element at last pos in array
        assertEquals("[3, 4, 6, 7]", fifo.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveError6() throws Exception {
        // based on bug 33071
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>(5);
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");  // end=0
        fifo.add((E) "6");  // end=1
        fifo.add((E) "7");  // end=2

        assertEquals("[3, 4, 5, 6, 7]", fifo.toString());

        fifo.remove("6");  // remove element at position zero in array
        assertEquals("[3, 4, 5, 7]", fifo.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveError7() throws Exception {
        // based on bug 33071
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>(5);
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");  // end=0
        fifo.add((E) "6");  // end=1
        fifo.add((E) "7");  // end=2

        assertEquals("[3, 4, 5, 6, 7]", fifo.toString());

        fifo.remove("7");  // remove element at position one in array
        assertEquals("[3, 4, 5, 6]", fifo.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveError8() throws Exception {
        // based on bug 33071
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>(5);
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");  // end=0
        fifo.add((E) "6");  // end=1
        fifo.add((E) "7");  // end=2
        fifo.add((E) "8");  // end=3

        assertEquals("[4, 5, 6, 7, 8]", fifo.toString());

        fifo.remove("7");  // remove element at position one in array, need to shift 8
        assertEquals("[4, 5, 6, 8]", fifo.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveError9() throws Exception {
        // based on bug 33071
        final CircularFifoQueue<E> fifo = new CircularFifoQueue<>(5);
        fifo.add((E) "1");
        fifo.add((E) "2");
        fifo.add((E) "3");
        fifo.add((E) "4");
        fifo.add((E) "5");  // end=0
        fifo.add((E) "6");  // end=1
        fifo.add((E) "7");  // end=2
        fifo.add((E) "8");  // end=3

        assertEquals("[4, 5, 6, 7, 8]", fifo.toString());

        fifo.remove("8");  // remove element at position two in array
        assertEquals("[4, 5, 6, 7]", fifo.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRepeatedSerialization() throws Exception {
        // bug 31433
        final CircularFifoQueue<E> b = new CircularFifoQueue<>(2);
        b.add((E) "a");
        assertEquals(1, b.size());
        assertTrue(b.contains("a"));
        final CircularFifoQueue<E> b2 = serializeDeserialize(b);
        assertEquals(1, b2.size());
        assertTrue(b2.contains("a"));
        b2.add((E) "b");
        assertEquals(2, b2.size());
        assertTrue(b2.contains("a"));
        assertTrue(b2.contains("b"));
        final CircularFifoQueue<E> b3 = serializeDeserialize(b2);
        assertEquals(2, b3.size());
        assertTrue(b3.contains("a"));
        assertTrue(b3.contains("b"));
        b3.add((E) "c");
        assertEquals(2, b3.size());
        assertTrue(b3.contains("b"));
        assertTrue(b3.contains("c"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testDeserializeRejectsCorruptSize() throws Exception {
        // a stored size larger than maxElements would write past the backing array
        final CircularFifoQueue<E> full = new CircularFifoQueue<>(7);
        for (int i = 0; i < 7; i++) {
            full.add((E) ("x" + i));
        }
        final byte[] tooLarge = serialize(full);
        // first 0x00000007 is maxElements; shrink it so size (7) now exceeds it
        patchInt(tooLarge, indexOfInt(tooLarge, 7), 2);
        assertThrows(InvalidObjectException.class, () -> deserialize(tooLarge));

        // a negative stored size leaves the queue in an inconsistent state
        final CircularFifoQueue<E> partial = new CircularFifoQueue<>(7);
        partial.add((E) "a");
        partial.add((E) "b");
        final byte[] negative = serialize(partial);
        patchInt(negative, indexOfInt(negative, 2), -1);
        assertThrows(InvalidObjectException.class, () -> deserialize(negative));
    }

    private static int indexOfInt(final byte[] data, final int value) {
        final byte[] needle = {(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
        for (int i = 0; i <= data.length - 4; i++) {
            if (data[i] == needle[0] && data[i + 1] == needle[1]
                    && data[i + 2] == needle[2] && data[i + 3] == needle[3]) {
                return i;
            }
        }
        throw new IllegalStateException("value not found in stream");
    }

    private static void patchInt(final byte[] data, final int pos, final int value) {
        data[pos] = (byte) (value >>> 24);
        data[pos + 1] = (byte) (value >>> 16);
        data[pos + 2] = (byte) (value >>> 8);
        data[pos + 3] = (byte) value;
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/CircularFifoQueue.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/CircularFifoQueue.fullCollection.version4.obj");
//    }

    /**
     *  Runs through the regular verifications, but also verifies that
     *  the buffer contains the same elements in the same sequence as the
     *  list.
     */
    @Override
    public void verify() {
        super.verify();
        final Iterator<E> iterator1 = getCollection().iterator();
        for (final E e : getConfirmed()) {
            assertTrue(iterator1.hasNext());
            final Object o1 = iterator1.next();
            final Object o2 = e;
            assertEquals(o1, o2);
        }
    }

}
