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
package org.apache.commons.collections.buffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;

import org.apache.commons.collections.BufferUnderflowException;
import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.collection.AbstractTestCollection;

/**
 * Test cases for BoundedFifoBuffer.
 *
 * @version $Revision$ $Date$
 *
 * @author Paul Jack
 */
public class TestBoundedFifoBuffer<E> extends AbstractTestCollection<E> {

    public TestBoundedFifoBuffer(String n) {
        super(n);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestBoundedFifoBuffer.class);
    }

    //-----------------------------------------------------------------------
    /**
     *  Runs through the regular verifications, but also verifies that
     *  the buffer contains the same elements in the same sequence as the
     *  list.
     */
    public void verify() {
        super.verify();
        Iterator<E> iterator1 = getCollection().iterator();
        Iterator<E> iterator2 = getConfirmed().iterator();
        while (iterator2.hasNext()) {
            assertTrue(iterator1.hasNext());
            E o1 = iterator1.next();
            E o2 = iterator2.next();
            assertEquals(o1, o2);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Overridden because UnboundedFifoBuffer doesn't allow null elements.
     * @return false
     */
    public boolean isNullSupported() {
        return false;
    }

    /**
     * Overridden because UnboundedFifoBuffer isn't fail fast.
     * @return false
     */
    public boolean isFailFastSupported() {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns an empty ArrayList.
     *
     *  @return an empty ArrayList
     */
    public List<E> makeConfirmedCollection() {
        return new ArrayList<E>();
    }

    /**
     *  Returns a full ArrayList.
     *
     *  @return a full ArrayList
     */
    public List<E> makeConfirmedFullCollection() {
        List<E> c = makeConfirmedCollection();
        c.addAll(java.util.Arrays.asList(getFullElements()));
        return c;
    }

    /**
     *  Returns an empty BoundedFifoBuffer that won't overflow.
     *
     *  @return an empty BoundedFifoBuffer
     */
    public BoundedFifoBuffer<E> makeObject() {
        return new BoundedFifoBuffer<E>(100);
    }

    //-----------------------------------------------------------------------
    /**
     * Tests that the removal operation actually removes the first element.
     */
    public void testBoundedFifoBufferRemove() {
        resetFull();
        int size = getConfirmed().size();
        for (int i = 0; i < size; i++) {
            E o1 = getCollection().remove();
            E o2 = getConfirmed().remove(0);
            assertEquals("Removed objects should be equal", o1, o2);
            verify();
        }

        try {
            getCollection().remove();
            fail("Empty buffer should raise Underflow.");
        } catch (BufferUnderflowException e) {
            // expected
        }
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException1() {
        try {
            new BoundedFifoBuffer<E>(0);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException2() {
        try {
            new BoundedFifoBuffer<E>(-20);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException3() {
        try {
            new BoundedFifoBuffer<E>(null);
        } catch (NullPointerException ex) {
            return;
        }
        fail();
    }

    public String getCompatibilityVersion() {
        return "3.1";
    }

    // BZ 33071 -- gets start=end=1 before removal of interior element
    @SuppressWarnings("unchecked")
    public void testShift() {
        BoundedFifoBuffer<E> fifo = new BoundedFifoBuffer<E>(3);
        fifo.add((E) "a");
        fifo.add((E) "b");
        fifo.add((E) "c");
        fifo.remove();
        fifo.add((E) "e");
        fifo.remove("c");
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/BoundedFifoBuffer.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/BoundedFifoBuffer.fullCollection.version3.1.obj");
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BoundedFifoBuffer<E> getCollection() {
        return (BoundedFifoBuffer<E>) super.getCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> getConfirmed() {
        return (List<E>) super.getConfirmed();
    }
}
