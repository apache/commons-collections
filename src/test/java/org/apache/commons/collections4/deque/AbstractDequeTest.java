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

import org.apache.commons.collections4.collection.AbstractCollectionTest;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Abstract test class for {@link java.util.Deque} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your {@link Deque} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Deque} fails or override one of the
 * protected methods from AbstractCollectionTest.
 *
 * @since 4.5
 */
public abstract class AbstractDequeTest<E> extends AbstractCollectionTest<E> {
    /**
     * JUnit constructor.
     *
     * @param testName the test class name
     */
    public AbstractDequeTest(final String testName) {
        super(testName);
    }

    /**
     *  Returns true if the collections produced by
     *  {@link #makeObject()} and {@link #makeFullCollection()}
     *  support the <code>set operation.<p>
     *  Default implementation returns true.  Override if your collection
     *  class does not support set.
     */
    public boolean isSetSupported() {
        return true;
    }


    /**
     *  Verifies that the test deque implementation matches the confirmed deque
     *  implementation.
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

    /**
     * Returns an empty {@link LinkedList}.
     */
    @Override
    public Collection<E> makeConfirmedCollection() {
        final LinkedList<E> list = new LinkedList<>();
        return list;
    }

    /**
     * Returns a full {@link LinkedList}.
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final LinkedList<E> list = new LinkedList<>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    /**
     * Returns {@link #makeObject()}.
     *
     * @return an empty deque to be used for testing
     */
    @Override
    public abstract Deque<E> makeObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public Deque<E> makeFullCollection() {
        // only works if Deque supports optional "addAll(Collection)"
        final Deque<E> deque = makeObject();
        deque.addAll(Arrays.asList(getFullElements()));
        return deque;
    }

    /**
     * Returns the {@link #collection} field cast to a {@link Deque}.
     *
     * @return the collection field as a Deque
     */
    @Override
    public Deque<E> getCollection() {
        return (Deque<E>) super.getCollection();
    }

    /**
     *  Tests {@link Deque#addFirst(E e)}.
     */
    public void testDequeAddFirst() {
        if (!isAddSupported()) {
            return;
        }

        final E[] elements = getFullElements();
        resetEmpty();
        int size = 0;

        for (final E element : elements) {
            getCollection().addFirst(element);
            getConfirmed().add(element);
            size++;
            assertEquals("Deque size should grow after add", size, getCollection().size());
            assertTrue("Deque should contain added element", getCollection().contains(element));
        }
        final Deque<E> deque = makeObject();
        for (int i = 0; i < size; i++) {
            deque.push(getCollection().pop());
        }
        setCollection(deque);
        verify();
    }

    /**
     *  Tests {@link Deque#addLast(E e)}.
     */
    public void testDequeAddLast() {
        if (!isAddSupported()) {
            return;
        }

        resetEmpty();
        int size = 0;
        final E[] elements = getFullElements();
        for (final E element : elements) {
            getCollection().addLast(element);
            getConfirmed().add(element);
            verify();
            size++;
            assertEquals("Deque size should grow after add", size, getCollection().size());
            assertTrue("Deque should contain added element", getCollection().contains(element));
        }
    }

    /**
     *  Tests {@link Deque#offerFirst(E e)}.
     */
    public void testDequeOfferFirst() {
        if (!isAddSupported()) {
            return;
        }

        resetEmpty();
        final E[] elements = getFullElements();
        int size = 0;
        for (final E element : elements) {
            final boolean r = getCollection().offerFirst(element);
            getConfirmed().add(element);
            if (r) {
                size++;
            }
            assertEquals("Deque size should grow after add", size, getCollection().size());
            assertTrue("Deque should contain added element", getCollection().contains(element));
        }
        final Deque<E> deque = makeObject();
        for (int i = 0; i < size; i++) {
            deque.push(getCollection().pop());
        }
        setCollection(deque);
        verify();
    }

    /**
     *  Tests {@link Deque#offerLast(E e)}.
     */
    public void testDequeOfferLast() {
        if (!isAddSupported()) {
            return;
        }

        resetEmpty();
        int size = 0;
        final E[] elements = getFullElements();
        for (final E element : elements) {
            final boolean r = getCollection().offerLast(element);
            getConfirmed().add(element);
            verify();
            if (r) {
                size++;
            }
            assertEquals("Deque size should grow after add", size, getCollection().size());
            assertTrue("Deque should contain added element", getCollection().contains(element));
        }
    }

    /**
     *  Tests {@link Deque#removeFirst()}.
     */
    public void testDequeRemoveFirst() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        try {
            getCollection().removeFirst();
            fail("Deque.removeFirst should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            final E element = getCollection().removeFirst();
            final boolean success = getConfirmed().remove(element);
            assertTrue("removeFirst should return correct element", success);
            verify();
        }

        try {
            getCollection().element();
            fail("Deque.removeFirst should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }
    }

    /**
     *  Tests {@link Deque#removeLast()}.
     */
    public void testDequeRemoveLast() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        try {
            getCollection().removeLast();
            fail("Deque.remove should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            final E element = getCollection().removeLast();
            LinkedList<E> list=(LinkedList<E>) getConfirmed();
            list.removeLast();
            verify();
        }

        try {
            getCollection().element();
            fail("Deque.remove should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }
    }

    /**
     *  Tests {@link Deque#pollFirst()}.
     */
    public void testDequePollFirst() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        E element = getCollection().pollFirst();
        assertNull(element);

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            element = getCollection().pollFirst();
            final boolean success = getConfirmed().remove(element);
            assertTrue("poll should return correct element", success);
            verify();
        }

        element = getCollection().pollFirst();
        assertNull(element);
    }

    /**
     *  Tests {@link Deque#pollLast()}.
     */
    public void testDequePollLast() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        E element = getCollection().pollLast();
        assertNull(element);

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            getCollection().pollLast();
            LinkedList<E> list = (LinkedList<E>) getConfirmed();
            list.removeLast();
            verify();
        }

        element = getCollection().pollLast();
        assertNull(element);
    }

    /**
     *  Tests {@link Deque#getFirst()}.
     */
    public void testDequeGetFirst() {
        resetEmpty();

        try {
            getCollection().getFirst();
            fail("Deque.element should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }

        resetFull();

        assertTrue(getConfirmed().contains(getCollection().getFirst()));

        if (!isRemoveSupported()) {
            return;
        }

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            final E element = getCollection().getFirst();

            if (!isNullSupported()) {
                assertNotNull(element);
            }

            assertTrue(getConfirmed().contains(element));

            getCollection().remove(element);
            getConfirmed().remove(element);

            verify();
        }

        try {
            getCollection().getFirst();
            fail("Deque.element should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }
    }

    /**
     *  Tests {@link Deque#getLast()}.
     */
    public void testDequeGetLast() {
        resetEmpty();

        try {
            getCollection().getLast();
            fail("Deque.element should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }

        resetFull();

        if (!isRemoveSupported()) {
            return;
        }

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            final E element = getCollection().getLast();

            if (!isNullSupported()) {
                assertNotNull(element);
            }
            assertTrue(getConfirmed().contains(element));
            getCollection().remove(element);
            getConfirmed().remove(element);
            verify();
        }

        try {
            getCollection().getLast();
            fail("Deque.element should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }
    }

    /**
     *  Tests {@link Deque#peekFirst()}.
     */
    public void testDequePeekFirst() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        E element = getCollection().peekFirst();
        assertNull(element);

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            element = getCollection().peekFirst();

            if (!isNullSupported()) {
                assertNotNull(element);
            }

            assertTrue(getConfirmed().contains(element));

            getCollection().remove(element);
            getConfirmed().remove(element);

            verify();
        }

        element = getCollection().peekFirst();
        assertNull(element);
    }

    /**
     *  Tests {@link Deque#peekLast()}.
     */
    public void testDequePeekLast() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        E element = getCollection().peekLast();
        assertNull(element);

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            element = getCollection().peekLast();

            if (!isNullSupported()) {
                assertNotNull(element);
            }

            assertTrue(getConfirmed().contains(element));

            getCollection().remove(element);
            getConfirmed().remove(element);

            verify();
        }

        element = getCollection().peekLast();
        assertNull(element);
    }
    /**
     *  Tests {@link Deque#offer(Object)}.
     */
    public void testDequeOffer() {
        if (!isAddSupported()) {
            return;
        }

        resetEmpty();
        int size = 0;
        final E[] elements = getFullElements();
        for (final E element : elements) {
            final boolean r = getCollection().offer(element);
            getConfirmed().add(element);
            verify();
            if (r) {
                size++;
            }
            assertEquals("Deque size should grow after add", size, getCollection().size());
            assertTrue("Deque should contain added element", getCollection().contains(element));
        }
    }

    /**
     *  Tests {@link Deque#element()}.
     */
    public void testDequeElement() {
        resetEmpty();

        try {
            getCollection().element();
            fail("Deque.element should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }

        resetFull();

        assertTrue(getConfirmed().contains(getCollection().element()));

        if (!isRemoveSupported()) {
            return;
        }

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            final E element = getCollection().element();

            if (!isNullSupported()) {
                assertNotNull(element);
            }

            assertTrue(getConfirmed().contains(element));

            getCollection().remove(element);
            getConfirmed().remove(element);

            verify();
        }

        try {
            getCollection().element();
            fail("Deque.element should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }
    }

    /**
     *  Tests {@link Deque#peek()}.
     */
    public void testDequePeek() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        E element = getCollection().peek();
        assertNull(element);

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            element = getCollection().peek();

            if (!isNullSupported()) {
                assertNotNull(element);
            }

            assertTrue(getConfirmed().contains(element));

            getCollection().remove(element);
            getConfirmed().remove(element);

            verify();
        }

        element = getCollection().peek();
        assertNull(element);
    }

    /**
     *  Tests {@link Deque#remove()}.
     */
    public void testDequeRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        try {
            getCollection().remove();
            fail("Deque.remove should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            final E element = getCollection().remove();
            final boolean success = getConfirmed().remove(element);
            assertTrue("remove should return correct element", success);
            verify();
        }

        try {
            getCollection().element();
            fail("Deque.remove should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }
    }

    /**
     *  Tests {@link Deque#poll()}.
     */
    public void testDequePoll() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();

        E element = getCollection().poll();
        assertNull(element);

        resetFull();

        final int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            element = getCollection().poll();
            final boolean success = getConfirmed().remove(element);
            assertTrue("poll should return correct element", success);
            verify();
        }

        element = getCollection().poll();
        assertNull(element);
    }

    /**
     *  Tests {@link Deque#removeFirstOccurrence(Object o)}.
     */
    public void testDequeRemoveFirstOccurrence() {
        if (!isRemoveSupported()) {
            return;
        }

        resetFull();

        final E[] elements = getFullElements();
        for (final E element : elements) {
            final boolean success = getCollection().removeFirstOccurrence(element);
            assertTrue("element should be removed successfully", success);
            getConfirmed().remove(element);
            verify();
        }

        try {
            getCollection().element();
            fail("Deque.remove should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }
    }

    /**
     *  Tests {@link Deque#removeLastOccurrence(Object o)}.
     */
    public void testDequeRemoveLastOccurrence() {
        if (!isRemoveSupported()) {
            return;
        }

        resetFull();

        final E[] elements = getFullElements();
        for (final E element : elements) {
            final boolean success = getCollection().removeLastOccurrence(element);
            assertTrue("element should be removed successfully", success);
            final LinkedList<E> list=(LinkedList<E>) getConfirmed();
            list.removeLastOccurrence(element);
            verify();
        }

        try {
            getCollection().element();
            fail("Deque.remove should throw NoSuchElementException");
        } catch (final NoSuchElementException e) {
            // expected
        }
    }

    /**
     *  Tests {@link Deque#descendingIterator()}.
     */
    public void testDescendingIterator() {
        if (!isAddSupported()) {
            return;
        }

        resetEmpty();
        final E[] elements = getFullElements();
        getCollection().addAll(Arrays.asList(elements));
        final Iterator<E> iter = getCollection().descendingIterator();
        final int max = getFullElements().length;
        for (int i = max-1; i >= 0; i--) {
            assertTrue(iter.hasNext());
            final Object o1 = iter.next();
            assertEquals(elements[i],o1);
        }
    }

    @SuppressWarnings("unchecked")
    public void testEmptyDequeSerialization() throws IOException, ClassNotFoundException {
        final Deque<E> deque = makeObject();
        if (!(deque instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] objekt = writeExternalFormToBytes((Serializable) deque);
        final Deque<E> deque2 = (Deque<E>) readExternalFormFromBytes(objekt);

        assertEquals("Both deques are empty", 0, deque.size());
        assertEquals("Both deques are empty", 0, deque2.size());
    }

    @SuppressWarnings("unchecked")
    public void testFullDequeSerialization() throws IOException, ClassNotFoundException {
        final Deque<E> deque = makeFullCollection();
        final int size = getFullElements().length;
        if (!(deque instanceof Serializable && isTestSerialization())) {
            return;
        }

        final byte[] objekt = writeExternalFormToBytes((Serializable) deque);
        final Deque<E> deque2 = (Deque<E>) readExternalFormFromBytes(objekt);

        assertEquals("Both deques are same size", size, deque.size());
        assertEquals("Both deques are same size", size, deque2.size());
    }

    /**
     * Compare the current serialized form of the Deque
     * against the canonical version in SVN.
     */
    @SuppressWarnings("unchecked")
    public void testEmptyDequeCompatibility() throws IOException, ClassNotFoundException {

        // test to make sure the canonical form has been preserved
        final Deque<E> deque = makeObject();
        if (deque instanceof Serializable && !skipSerializedCanonicalTests()
                && isTestSerialization()) {
            writeExternalFormToDisk((Serializable) deque, getCanonicalEmptyCollectionName(deque));
            final Deque<E> deque2 = (Deque<E>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(deque));
            assertEquals("Deque is empty", 0, deque2.size());
        }
    }

    /**
     * Compare the current serialized form of the Deque
     * against the canonical version in SVN.
     */
    @SuppressWarnings("unchecked")
    public void testFullDequeCompatibility() throws IOException, ClassNotFoundException {

        // test to make sure the canonical form has been preserved
        final Deque<E> deque = makeFullCollection();
        if(deque instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            writeExternalFormToDisk((Serializable) deque, getCanonicalFullCollectionName(deque));
            final Deque<E> deque2 = (Deque<E>) readExternalFormFromDisk(getCanonicalFullCollectionName(deque));
            assertEquals("Deques are not the right size", deque.size(), deque2.size());
        }
    }
}
