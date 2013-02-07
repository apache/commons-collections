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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.collection.AbstractCollectionTest;

/**
 * Test cases for UnboundedFifoBuffer.
 *
 * @version $Id$
 */
public class UnboundedFifoBufferTest<E> extends AbstractCollectionTest<E> {

    public UnboundedFifoBufferTest(final String n) {
        super(n);
    }

    public static Test suite() {
        return BulkTest.makeSuite(UnboundedFifoBufferTest.class);
    }

    //-----------------------------------------------------------------------
    /**
     *  Verifies that the ArrayList has the same elements in the same 
     *  sequence as the UnboundedFifoBuffer.
     */
    @Override
    public void verify() {
        super.verify();
        final Iterator<E> iterator1 = getCollection().iterator();
        final Iterator<E> iterator2 = getConfirmed().iterator();
        while (iterator2.hasNext()) {
            assertTrue(iterator1.hasNext());
            final Object o1 = iterator1.next();
            final Object o2 = iterator2.next();
            assertEquals(o1, o2);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Overridden because UnboundedFifoBuffer doesn't allow null elements.
     * @return false
     */
    @Override
    public boolean isNullSupported() {
        return false;
    }

    /**
     * Overridden because UnboundedFifoBuffer isn't fail fast.
     * @return false
     */
    @Override
    public boolean isFailFastSupported() {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns an empty ArrayList.
     *
     *  @return an empty ArrayList
     */
    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<E>();
    }

    /**
     *  Returns a full ArrayList.
     *
     *  @return a full ArrayList
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final Collection<E> c = makeConfirmedCollection();
        c.addAll(java.util.Arrays.asList(getFullElements()));
        return c;
    }

    /**
     *  Returns an empty UnboundedFifoBuffer with a small capacity.
     *
     *  @return an empty UnboundedFifoBuffer
     */
    @Override
    public Collection<E> makeObject() {
        return new UnboundedFifoBuffer<E>(5);
    }

    //-----------------------------------------------------------------------
    /**
     *  Tests that UnboundedFifoBuffer removes elements in the right order.
     */
    public void testUnboundedFifoBufferRemove() {
        resetFull();
        final int size = getConfirmed().size();
        for (int i = 0; i < size; i++) {
            final E o1 = getCollection().remove();
            final E o2 = getConfirmed().remove(0);
            assertEquals("Removed objects should be equal", o1, o2);
            verify();
        }
    }

    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException1() {
        try {
            new UnboundedFifoBuffer<E>(0);
        } catch (final IllegalArgumentException ex) {
            return;
        }
        fail();
    }
    
    /**
     * Tests that the constructor correctly throws an exception.
     */
    public void testConstructorException2() {
        try {
            new UnboundedFifoBuffer<E>(-20);
        } catch (final IllegalArgumentException ex) {
            return;
        }
        fail();
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testInternalStateAdd() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(2);
        assertEquals(3, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(0, test.tail);
        test.add((E) "A");
        assertEquals(3, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(1, test.tail);
        test.add((E) "B");
        assertEquals(3, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(2, test.tail);
        test.add((E) "C");  // forces buffer increase
        assertEquals(5, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(3, test.tail);
        test.add((E) "D");
        assertEquals(5, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(4, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateAddWithWrap() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(3);
        assertEquals(4, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(0, test.tail);
        test.add((E) "A");
        assertEquals(4, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(1, test.tail);
        test.add((E) "B");
        assertEquals(4, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(2, test.tail);
        test.add((E) "C");
        assertEquals(4, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(3, test.tail);
        test.remove("A");
        assertEquals(4, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(3, test.tail);
        test.remove("B");
        assertEquals(4, test.buffer.length);
        assertEquals(2, test.head);
        assertEquals(3, test.tail);
        test.add((E) "D");
        assertEquals(4, test.buffer.length);
        assertEquals(2, test.head);
        assertEquals(0, test.tail);
        test.add((E) "E");
        assertEquals(4, test.buffer.length);
        assertEquals(2, test.head);
        assertEquals(1, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateRemove1() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(4);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        assertEquals(5, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(3, test.tail);
        
        test.remove("A");
        assertEquals(5, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(3, test.tail);
        
        test.add((E) "D");
        assertEquals(5, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(4, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateRemove2() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(4);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        assertEquals(5, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(3, test.tail);
        
        test.remove("B");
        assertEquals(5, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(2, test.tail);
        
        test.add((E) "D");
        assertEquals(5, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(3, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateIteratorRemove1() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(4);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        assertEquals(5, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(3, test.tail);
        
        final Iterator<E> it = test.iterator();
        it.next();
        it.remove();
        assertEquals(5, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(3, test.tail);
        
        test.add((E) "D");
        assertEquals(5, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(4, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateIteratorRemove2() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(4);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        
        final Iterator<E> it = test.iterator();
        it.next();
        it.next();
        it.remove();
        assertEquals(5, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(2, test.tail);
        
        test.add((E) "D");
        assertEquals(5, test.buffer.length);
        assertEquals(0, test.head);
        assertEquals(3, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateIteratorRemoveWithTailAtEnd1() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(3);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        test.remove("A");
        test.add((E) "D");
        assertEquals(4, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(0, test.tail);
        
        final Iterator<E> it = test.iterator();
        assertEquals("B", it.next());
        it.remove();
        assertEquals(4, test.buffer.length);
        assertEquals(2, test.head);
        assertEquals(0, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateIteratorRemoveWithTailAtEnd2() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(3);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        test.remove("A");
        test.add((E) "D");
        assertEquals(4, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(0, test.tail);
        
        final Iterator<E> it = test.iterator();
        assertEquals("B", it.next());
        assertEquals("C", it.next());
        it.remove();
        assertEquals(4, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(3, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateIteratorRemoveWithTailAtEnd3() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(3);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        test.remove("A");
        test.add((E) "D");
        assertEquals(4, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(0, test.tail);
        
        final Iterator<E> it = test.iterator();
        assertEquals("B", it.next());
        assertEquals("C", it.next());
        assertEquals("D", it.next());
        it.remove();
        assertEquals(4, test.buffer.length);
        assertEquals(1, test.head);
        assertEquals(3, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateIteratorRemoveWithWrap1() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(3);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        test.remove("A");
        test.remove("B");
        test.add((E) "D");
        test.add((E) "E");
        assertEquals(4, test.buffer.length);
        assertEquals(2, test.head);
        assertEquals(1, test.tail);
        
        final Iterator<E> it = test.iterator();
        assertEquals("C", it.next());
        it.remove();
        assertEquals(4, test.buffer.length);
        assertEquals(3, test.head);
        assertEquals(1, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateIteratorRemoveWithWrap2() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(3);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        test.remove("A");
        test.remove("B");
        test.add((E) "D");
        test.add((E) "E");
        assertEquals(4, test.buffer.length);
        assertEquals(2, test.head);
        assertEquals(1, test.tail);
        
        final Iterator<E> it = test.iterator();
        assertEquals("C", it.next());
        assertEquals("D", it.next());
        it.remove();
        assertEquals(4, test.buffer.length);
        assertEquals(2, test.head);
        assertEquals(0, test.tail);
    }

    @SuppressWarnings("unchecked")
    public void testInternalStateIteratorRemoveWithWrap3() {
        final UnboundedFifoBuffer<E> test = new UnboundedFifoBuffer<E>(3);
        test.add((E) "A");
        test.add((E) "B");
        test.add((E) "C");
        test.remove("A");
        test.remove("B");
        test.add((E) "D");
        test.add((E) "E");
        assertEquals(4, test.buffer.length);
        assertEquals(2, test.head);
        assertEquals(1, test.tail);
        
        final Iterator<E> it = test.iterator();
        assertEquals("C", it.next());
        assertEquals("D", it.next());
        assertEquals("E", it.next());
        it.remove();
        assertEquals(4, test.buffer.length);
        assertEquals(2, test.head);
        assertEquals(0, test.tail);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testCollections220() throws Exception {
         UnboundedFifoBuffer<String> buffer = new UnboundedFifoBuffer<String>();
         
         buffer = (UnboundedFifoBuffer<String>) serializeDeserialize(buffer);

         // test size() gets incremented
         buffer.add("Foo");
         assertEquals(1, buffer.size());
    }

    //-----------------------------------------------------------------------
    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/UnboundedFifoBuffer.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/UnboundedFifoBuffer.fullCollection.version3.1.obj");
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnboundedFifoBuffer<E> getCollection() {
        return (UnboundedFifoBuffer<E>) super.getCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> getConfirmed() {
        return (List<E>) super.getConfirmed();
    }
}
