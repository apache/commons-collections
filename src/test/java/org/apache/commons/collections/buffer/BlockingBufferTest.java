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

import org.apache.commons.collections.AbstractObjectTest;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Extension of {@link AbstractObjectTest} for exercising the
 * {@link BlockingBuffer} implementation.
 *
 * @since 3.0
 * @version $Id$
 */
public class BlockingBufferTest<E> extends AbstractObjectTest {

    public BlockingBufferTest(final String testName) {
        super(testName);
    }

    @Override
    public Buffer<E> makeObject() {
        return BlockingBuffer.blockingBuffer(new MyBuffer<E>());
    }

    @Override
    public boolean isEqualsCheckable() {
        return false;
    }

    //-----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    protected E makeElement() {
        return (E) new Object();
    }
    
    /**
     * Tests {@link BlockingBuffer#get()} in combination with
     * {@link BlockingBuffer#add(Object)}.
     */
    public void testGetWithAdd() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();
        new DelayedAdd<E>(blockingBuffer, obj).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.get());
    }

    public void testGetWithAddTimeout() {
        final Buffer<E> blockingBuffer = BlockingBuffer.blockingBuffer(new MyBuffer<E>(), 500);
        final E obj = makeElement();
        new DelayedAdd<E>(blockingBuffer, obj, 100).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.get());
    }

    //-----------------------------------------------------------------------

    /**
     * Tests {@link BlockingBuffer#get()} in combination with
     * {@link BlockingBuffer#addAll(java.util.Collection)}.
     */
    public void testGetWithAddAll() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();
        new DelayedAddAll<E>(blockingBuffer, obj).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.get());
    }

    public void testGetWithAddAllTimeout() {
        final Buffer<E> blockingBuffer = BlockingBuffer.blockingBuffer(new MyBuffer<E>(), 500);
        final E obj = makeElement();
        new DelayedAddAll<E>(blockingBuffer, obj, 100).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.get());
    }

    //-----------------------------------------------------------------------

    /**
     * Tests {@link BlockingBuffer#remove()} in combination with
     * {@link BlockingBuffer#add(Object)}.
     */
    public void testRemoveWithAdd() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();
        new DelayedAdd<E>(blockingBuffer, obj).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.remove());
    }

    public void testRemoveWithAddTimeout() {
        final Buffer<E> blockingBuffer = BlockingBuffer.blockingBuffer(new MyBuffer<E>(), 100);
        final E obj = makeElement();
        new DelayedAdd<E>(blockingBuffer, obj, 500).start();
        try {
            blockingBuffer.remove();
        } catch (final BufferUnderflowException e) {
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Tests {@link BlockingBuffer#remove()} in combination with
     * {@link BlockingBuffer#addAll(java.util.Collection)}.
     */
    public void testRemoveWithAddAll() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();
        new DelayedAddAll<E>(blockingBuffer, obj).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.remove());
    }

    public void testRemoveWithAddAllTimeout() {
        final Buffer<E> blockingBuffer = BlockingBuffer.blockingBuffer(new MyBuffer<E>(), 100);
        final E obj = makeElement();
        new DelayedAddAll<E>(blockingBuffer, obj, 500).start();
        try {
            blockingBuffer.remove();
        } catch (final BufferUnderflowException e) {
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Tests {@link BlockingBuffer#get()} in combination with
     * {@link BlockingBuffer#add(Object)} using multiple read threads. <p/> Two
     * read threads should block on an empty buffer until one object is added
     * then both threads should complete.
     */
    public void testBlockedGetWithAdd() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();

        // run methods will get and compare -- must wait for add
        final Thread thread1 = new ReadThread<E>(blockingBuffer, obj);
        final Thread thread2 = new ReadThread<E>(blockingBuffer, obj);
        thread1.start();
        thread2.start();

        // give hungry read threads ample time to hang
        delay();

        // notifyAll should allow both read threads to complete
        blockingBuffer.add(obj);

        // allow notified threads to complete 
        delay();

        // There should not be any threads waiting.
        if (thread1.isAlive() || thread2.isAlive()) {
            fail("Live thread(s) when both should be dead.");
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Tests {@link BlockingBuffer#get()} in combination with
     * {@link BlockingBuffer#addAll(java.util.Collection)} using multiple read
     * threads. <p/> Two read threads should block on an empty buffer until a
     * singleton is added then both threads should complete.
     */
    public void testBlockedGetWithAddAll() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();

        // run methods will get and compare -- must wait for addAll
        final Thread thread1 = new ReadThread<E>(blockingBuffer, obj);
        final Thread thread2 = new ReadThread<E>(blockingBuffer, obj);
        thread1.start();
        thread2.start();

        // give hungry read threads ample time to hang
        delay();

        // notifyAll should allow both read threads to complete
        blockingBuffer.addAll(Collections.singleton(obj));

        // allow notified threads to complete 
        delay();

        // There should not be any threads waiting.
        if (thread1.isAlive() || thread2.isAlive()) {
            fail("Live thread(s) when both should be dead.");
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Tests interrupted {@link BlockingBuffer#get()}.
     */
    public void testInterruptedGet() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();

        // spawn a read thread to wait on the empty buffer
        final ArrayList<String> exceptionList = new ArrayList<String>();
        final Thread thread = new ReadThread<E>(blockingBuffer, obj, exceptionList);
        thread.start();

        // Interrupting the thread should cause it to throw BufferUnderflowException
        thread.interrupt();

        // Chill, so thread can throw and add message to exceptionList
        delay();
        assertTrue("Thread interrupt should have led to underflow", exceptionList
                .contains("BufferUnderFlow"));
        if (thread.isAlive()) {
            fail("Read thread has hung.");
        }

    }

    //-----------------------------------------------------------------------

    /**
     * Tests {@link BlockingBuffer#remove()} in combination with
     * {@link BlockingBuffer#add(Object)} using multiple read threads. <p/> Two
     * read threads should block on an empty buffer until one object is added
     * then one thread should complete. The remaining thread should complete
     * after the addition of a second object.
     */
    public void testBlockedRemoveWithAdd() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();

        // run methods will remove and compare -- must wait for add
        final Thread thread1 = new ReadThread<E>(blockingBuffer, obj, null, "remove");
        final Thread thread2 = new ReadThread<E>(blockingBuffer, obj, null, "remove");
        thread1.start();
        thread2.start();

        // give hungry read threads ample time to hang
        delay();
        blockingBuffer.add(obj);

        // allow notified threads to complete 
        delay();

        // There should be one thread waiting.
        assertTrue("There is one thread waiting", thread1.isAlive() ^ thread2.isAlive());
        blockingBuffer.add(obj);

        // allow notified thread to complete 
        delay();

        // There should not be any threads waiting.
        if (thread1.isAlive() || thread2.isAlive()) {
            fail("Live thread(s) when both should be dead.");
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Tests {@link BlockingBuffer#remove()} in combination with
     * {@link BlockingBuffer#addAll(java.util.Collection)} using multiple read
     * threads. <p/> Two read threads should block on an empty buffer until a
     * singleton collection is added then one thread should complete. The
     * remaining thread should complete after the addition of a second
     * singleton.
     */
    public void testBlockedRemoveWithAddAll1() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();

        // run methods will remove and compare -- must wait for addAll
        final Thread thread1 = new ReadThread<E>(blockingBuffer, obj, null, "remove");
        final Thread thread2 = new ReadThread<E>(blockingBuffer, obj, null, "remove");
        thread1.start();
        thread2.start();

        // give hungry read threads ample time to hang
        delay();
        blockingBuffer.addAll(Collections.singleton(obj));

        // allow notified threads to complete 
        delay();

        // There should be one thread waiting.
        assertTrue("There is one thread waiting", thread1.isAlive() ^ thread2.isAlive());
        blockingBuffer.addAll(Collections.singleton(obj));

        // allow notified thread to complete 
        delay();

        // There should not be any threads waiting.
        if (thread1.isAlive() || thread2.isAlive()) {
            fail("Live thread(s) when both should be dead.");
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Tests {@link BlockingBuffer#remove()} in combination with
     * {@link BlockingBuffer#addAll(java.util.Collection)} using multiple read
     * threads. <p/> Two read threads should block on an empty buffer until a
     * collection with two distinct objects is added then both threads should
     * complete. Each thread should have read a different object.
     */
    public void testBlockedRemoveWithAddAll2() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj1 = makeElement();
        final E obj2 = makeElement();
        final Set<E> objs = Collections.synchronizedSet(new HashSet<E>());
        objs.add(obj1);
        objs.add(obj2);

        // run methods will remove and compare -- must wait for addAll
        final Thread thread1 = new ReadThread<E>(blockingBuffer, objs, "remove");
        final Thread thread2 = new ReadThread<E>(blockingBuffer, objs, "remove");
        thread1.start();
        thread2.start();

        // give hungry read threads ample time to hang
        delay();
        blockingBuffer.addAll(objs);

        // allow notified threads to complete 
        delay();
        assertEquals("Both objects were removed", 0, objs.size());

        // There should not be any threads waiting.
        if (thread1.isAlive() || thread2.isAlive()) {
            fail("Live thread(s) when both should be dead.");
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Tests interrupted remove.
     */
    public void testInterruptedRemove() {
        final Buffer<E> blockingBuffer = makeObject();
        final E obj = makeElement();

        // spawn a read thread to wait on the empty buffer
        final ArrayList<String> exceptionList = new ArrayList<String>();
        final Thread thread = new ReadThread<E>(blockingBuffer, obj, exceptionList, "remove");
        thread.start();

        // Interrupting the thread should cause it to throw BufferUnderflowException
        thread.interrupt();

        // Chill, so thread can throw and add message to exceptionList
        delay();
        assertTrue("Thread interrupt should have led to underflow", exceptionList
                .contains("BufferUnderFlow"));
        if (thread.isAlive()) {
            fail("Read thread has hung.");
        }

    }

    public void testTimeoutGet() {
        final BlockingBuffer<E> buffer = new BlockingBuffer<E>(new MyBuffer<E>());
        try {
            buffer.get(100);
            fail("Get should have timed out.");
        } catch (final BufferUnderflowException e) {
        }
    }

    public void testTimeoutRemove() {
        final BlockingBuffer<E> buffer = new BlockingBuffer<E>(new MyBuffer<E>());
        try {
            buffer.remove(100);
            fail("Get should have timed out.");
        } catch (final BufferUnderflowException e) {
        }
    }

    protected static class DelayedAdd<E> extends Thread {

        Buffer<E> buffer;

        E obj;

        long delay = 1000;

        public DelayedAdd(final Buffer<E> buffer, final E obj, final long delay) {
            this.buffer = buffer;
            this.obj = obj;
            this.delay = delay;
        }

        DelayedAdd(final Buffer<E> buffer, final E obj) {
            super();
            this.buffer = buffer;
            this.obj = obj;
        }

        @Override
        public void run() {
            try {
                // wait for other thread to block on get() or remove()
                Thread.sleep(delay);
            } catch (final InterruptedException e) {
            }
            buffer.add(obj);
        }
    }

    protected static class DelayedAddAll<E> extends Thread {

        Buffer<E> buffer;

        E obj;

        long delay = 100;

        public DelayedAddAll(final Buffer<E> buffer, final E obj, final long delay) {
            this.buffer = buffer;
            this.obj = obj;
            this.delay = delay;
        }

        DelayedAddAll(final Buffer<E> buffer, final E obj) {
            super();
            this.buffer = buffer;
            this.obj = obj;
        }

        @Override
        public void run() {
            try {
                // wait for other thread to block on get() or remove()
                Thread.sleep(delay);
            } catch (final InterruptedException e) {
            }
            buffer.addAll(Collections.singleton(obj));
        }
    }

    protected static class ReadThread<E> extends Thread {

        Buffer<E> buffer;

        Object obj;

        ArrayList<String> exceptionList = null;

        String action = "get";

        Set<E> objs;

        ReadThread(final Buffer<E> buffer, final Object obj) {
            super();
            this.buffer = buffer;
            this.obj = obj;
        }

        ReadThread(final Buffer<E> buffer, final Object obj, final ArrayList<String> exceptionList) {
            super();
            this.buffer = buffer;
            this.obj = obj;
            this.exceptionList = exceptionList;
        }

        ReadThread(final Buffer<E> buffer, final Object obj, final ArrayList<String> exceptionList, final String action) {
            super();
            this.buffer = buffer;
            this.obj = obj;
            this.exceptionList = exceptionList;
            this.action = action;
        }

        ReadThread(final Buffer<E> buffer, final Set<E> objs, final String action) {
            super();
            this.buffer = buffer;
            this.objs = objs;
            this.action = action;
        }

        @Override
        public void run() {
            try {
                if (action == "get") {
                    assertSame(obj, buffer.get());
                } else {
                    if (null != obj) {
                        assertSame(obj, buffer.remove());
                    } else {
                        assertTrue(objs.remove(buffer.remove()));
                    }
                }
            } catch (final BufferUnderflowException ex) {
                exceptionList.add("BufferUnderFlow");
            }
        }
    }

    @SuppressWarnings("serial")
    protected static class MyBuffer<E> extends LinkedList<E> implements Buffer<E> {

        /**
         * Generated serial version ID.
         */
        private static final long serialVersionUID = -1772433262105175184L;

        public E get() {
            if (isEmpty()) {
                throw new BufferUnderflowException();
            }
            return get(0);
        }

        @Override
        public E remove() {
            if (isEmpty()) {
                throw new BufferUnderflowException();
            }
            return remove(0);
        }
    }

    private void delay() {
        try {
            Thread.sleep( 200 );
        } catch (final InterruptedException e) {
        }
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

    //    public void testCreate() throws Exception {
    //        Buffer buffer = BlockingBuffer.decorate(new UnboundedFifoBuffer());
    //        writeExternalFormToDisk((java.io.Serializable) buffer,
    //        "D:/dev/collections/data/test/BlockingBuffer.emptyCollection.version3.1.obj");
    //        buffer = BlockingBuffer.decorate(new UnboundedFifoBuffer());
    //        buffer.add("A");
    //        buffer.add("B");
    //        buffer.add("C");
    //        writeExternalFormToDisk((java.io.Serializable) buffer,
    //        "D:/dev/collections/data/test/BlockingBuffer.fullCollection.version3.1.obj");
    //    }
}
