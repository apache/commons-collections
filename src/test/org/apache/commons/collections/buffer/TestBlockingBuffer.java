/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/buffer/TestBlockingBuffer.java,v 1.1 2003/11/16 00:05:46 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections.buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.AbstractTestObject;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;

/**
 * Extension of {@link TestObject} for exercising the {@link BlockingBuffer}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $
 * 
 * @author Janek Bogucki
 * @author Phil Steitz
 */
public class TestBlockingBuffer extends AbstractTestObject {

    public TestBlockingBuffer(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBlockingBuffer.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestBlockingBuffer.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    protected Object makeObject() {
        return BlockingBuffer.decorate(new MyBuffer());
    }

    //-----------------------------------------------------------------------
    /**
     *  Tests {@link BlockingBuffer#get()} in combination with {@link BlockingBuffer#add()}.
     */
    public void testGetWithAdd() {
      
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();

        new DelayedAdd(blockingBuffer, obj).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.get());
    }

    //-----------------------------------------------------------------------
    /**
     *  Tests {@link BlockingBuffer#get()} in combination with {@link BlockingBuffer#addAll()}.
     */
    public void testGetWithAddAll() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();

        new DelayedAddAll(blockingBuffer, obj).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.get());
    }

    //-----------------------------------------------------------------------
    /**
     *  Tests {@link BlockingBuffer#remove()} in combination with {@link BlockingBuffer#add()}.
     */
    public void testRemoveWithAdd() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();

        new DelayedAdd(blockingBuffer, obj).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.remove());
    }

    //-----------------------------------------------------------------------
    /**
     *  Tests {@link BlockingBuffer#remove()} in combination with {@link BlockingBuffer#addAll()}.
     */
    public void testRemoveWithAddAll() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();

        new DelayedAddAll(blockingBuffer, obj).start();

        // verify does not throw BufferUnderflowException; should block until other thread has added to the buffer .
        assertSame(obj, blockingBuffer.remove());
    }

    //-----------------------------------------------------------------------
    /**
     *  Tests {@link BlockingBuffer#get()} in combination with {@link BlockingBuffer#add()} using multiple read threads.
     *
     *  Two read threads should block on an empty buffer until one object
     *  is added then both threads should complete.
     */
    public void testBlockedGetWithAdd() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();
        
        // run methods will get and compare -- must wait for add
        Thread thread1 = new ReadThread(blockingBuffer, obj);
        Thread thread2 = new ReadThread(blockingBuffer, obj);
        thread1.start();
        thread2.start();
        
        // give hungry read threads ample time to hang
        delay();
           
        // notifyAll should allow both read threads to complete
        blockingBuffer.add(obj);
        
        // allow notified threads to complete 
        delay();
        
        // There should not be any threads waiting.
        if (thread1.isAlive() || thread2.isAlive())
            fail("Live thread(s) when both should be dead.");
    }
    
    //-----------------------------------------------------------------------
    /**
     *  Tests {@link BlockingBuffer#get()} in combination with {@link BlockingBuffer#addAll()} using multiple read threads.
     *
     *  Two read threads should block on an empty buffer until a
     *  singleton is added then both threads should complete.
     */
    public void testBlockedGetWithAddAll() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();
        
        // run methods will get and compare -- must wait for addAll
        Thread thread1 = new ReadThread(blockingBuffer, obj);
        Thread thread2 = new ReadThread(blockingBuffer, obj);
        thread1.start();
        thread2.start();
        
        // give hungry read threads ample time to hang
        delay();
           
        // notifyAll should allow both read threads to complete
        blockingBuffer.addAll(Collections.singleton(obj));
               
        // allow notified threads to complete 
        delay();
        
        // There should not be any threads waiting.
        if (thread1.isAlive() || thread2.isAlive())
            fail("Live thread(s) when both should be dead.");
    }
    
    //-----------------------------------------------------------------------
    /**
     *  Tests interrupted {@link BlockingBuffer#get()}.
     */
    public void testInterruptedGet() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();
        
        // spawn a read thread to wait on the empty buffer
        ArrayList exceptionList = new ArrayList();
        Thread thread = new ReadThread(blockingBuffer, obj, exceptionList);
        thread.start();
        
        // Interrupting the thread should cause it to throw BufferUnderflowException
        thread.interrupt();
        
        // Chill, so thread can throw and add message to exceptionList
        delay();
        
        assertTrue("Thread interrupt should have led to underflow", 
            exceptionList.contains("BufferUnderFlow"));
        
        if (thread.isAlive()) {
            fail("Read thread has hung.");
        }
        
    }
    
    //-----------------------------------------------------------------------
    /**
     *  Tests {@link BlockingBuffer#remove()} in combination with {@link BlockingBuffer#add()} using multiple read threads.
     *
     *  Two read threads should block on an empty buffer until one
     *  object is added then one thread should complete. The remaining
     *  thread should complete after the addition of a second object.
     */
    public void testBlockedRemoveWithAdd() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();
        
        // run methods will remove and compare -- must wait for add
        Thread thread1 = new ReadThread(blockingBuffer, obj, null, "remove");
        Thread thread2 = new ReadThread(blockingBuffer, obj, null, "remove");
        thread1.start();
        thread2.start();
        
        // give hungry read threads ample time to hang
        delay();
           
        blockingBuffer.add(obj);
        
        // allow notified threads to complete 
        delay();
        
        // There should be one thread waiting.
        assertTrue ("There is one thread waiting", thread1.isAlive() ^ thread2.isAlive());
           
        blockingBuffer.add(obj);
        
        // allow notified thread to complete 
        delay();

        // There should not be any threads waiting.
        if(thread1.isAlive() || thread2.isAlive())
            fail("Live thread(s) when both should be dead.");
    }

    //-----------------------------------------------------------------------
    /**
     *  Tests {@link BlockingBuffer#remove()} in combination with {@link BlockingBuffer#addAll()} using multiple read threads.
     *
     *  Two read threads should block on an empty buffer until a
     *  singleton collection is added then one thread should
     *  complete. The remaining thread should complete after the
     *  addition of a second singleton.
     */
    public void testBlockedRemoveWithAddAll1() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();
        
        // run methods will remove and compare -- must wait for addAll
        Thread thread1 = new ReadThread(blockingBuffer, obj, null, "remove");
        Thread thread2 = new ReadThread(blockingBuffer, obj, null, "remove");
        thread1.start();
        thread2.start();
        
        // give hungry read threads ample time to hang
        delay();
           
        blockingBuffer.addAll(Collections.singleton(obj));
        
        // allow notified threads to complete 
        delay();
        
        // There should be one thread waiting.
        assertTrue ("There is one thread waiting", thread1.isAlive() ^ thread2.isAlive());
           
        blockingBuffer.addAll(Collections.singleton(obj));
        
        // allow notified thread to complete 
        delay();

        // There should not be any threads waiting.
        if(thread1.isAlive() || thread2.isAlive())
            fail("Live thread(s) when both should be dead.");
    }

   
    //-----------------------------------------------------------------------
    /**
     *  Tests {@link BlockingBuffer#remove()} in combination with {@link BlockingBuffer#addAll()} using multiple read threads.
     *
     *  Two read threads should block on an empty buffer until a
     *  collection with two distinct objects is added then both
     *  threads should complete. Each thread should have read a
     *  different object.
     */
    public void testBlockedRemoveWithAddAll2() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj1 = new Object();
        Object obj2 = new Object();
        
        Set objs = Collections.synchronizedSet(new HashSet());
        objs.add(obj1);
        objs.add(obj2);

        // run methods will remove and compare -- must wait for addAll
        Thread thread1 = new ReadThread(blockingBuffer, objs, "remove");
        Thread thread2 = new ReadThread(blockingBuffer, objs, "remove");
        thread1.start();
        thread2.start();
        
        // give hungry read threads ample time to hang
        delay();
           
        blockingBuffer.addAll(objs);
        
        // allow notified threads to complete 
        delay();
        
        assertEquals("Both objects were removed", 0, objs.size());

        // There should not be any threads waiting.
        if(thread1.isAlive() || thread2.isAlive())
            fail("Live thread(s) when both should be dead.");
    }

    //-----------------------------------------------------------------------
    /**
     *  Tests interrupted remove.
     */
    public void testInterruptedRemove() {
        
        Buffer blockingBuffer = BlockingBuffer.decorate(new MyBuffer());
        Object obj = new Object();
        
        // spawn a read thread to wait on the empty buffer
        ArrayList exceptionList = new ArrayList();
        Thread thread = new ReadThread(blockingBuffer, obj, exceptionList, "remove");
        thread.start();
        
        // Interrupting the thread should cause it to throw BufferUnderflowException
        thread.interrupt();
        
        // Chill, so thread can throw and add message to exceptionList
        delay();
        
        assertTrue("Thread interrupt should have led to underflow", 
            exceptionList.contains("BufferUnderFlow"));
        
        if (thread.isAlive()) {
            fail("Read thread has hung.");
        }
        
    }
    
    protected static class DelayedAdd extends Thread {

        Buffer buffer;
        Object obj;

        DelayedAdd (Buffer buffer, Object obj) {
            super();
            this.buffer = buffer;
            this.obj = obj;
        }
                
        public void run() {

            try {
                // wait for other thread to block on get() or remove()
                Thread.currentThread().sleep(100);
            }
            catch (InterruptedException e) {}

            buffer.add(obj);
        }
    }
    
    protected static class DelayedAddAll extends Thread {

        Buffer buffer;
        Object obj;

        DelayedAddAll (Buffer buffer, Object obj) {
            super();
            this.buffer = buffer;
            this.obj = obj;
        }
                
        public void run() {

            try {
                // wait for other thread to block on get() or remove()
                Thread.currentThread().sleep(100);
            }
            catch (InterruptedException e) {}

            buffer.addAll(Collections.singleton(obj));
        }
    }
    
    protected static class ReadThread extends Thread {

        Buffer buffer;
        Object obj;
        ArrayList exceptionList = null;
        String action = "get";
        Set objs;
        
        ReadThread (Buffer buffer, Object obj) {
            super();
            this.buffer = buffer;
            this.obj = obj;
        }

        ReadThread (Buffer buffer, Object obj, ArrayList exceptionList) {
            super();
            this.buffer = buffer;
            this.obj = obj;
            this.exceptionList = exceptionList;
        }
        
        ReadThread (Buffer buffer, Object obj, ArrayList exceptionList, String action) {
            super();
            this.buffer = buffer;
            this.obj = obj;
            this.exceptionList = exceptionList;
            this.action = action;
        }
                
        ReadThread (Buffer buffer, Set objs, String action) {
            super();
            this.buffer = buffer;
            this.objs = objs;
            this.action = action;
        }
                
        public void run()  {
            try {
                if (action == "get") {
                    assertSame(obj, buffer.get());
                } else {
                    if (null != obj)
                        assertSame(obj, buffer.remove());
                    else
                        assertTrue(objs.remove(buffer.remove()));
                }
            } catch (BufferUnderflowException ex) {
                exceptionList.add("BufferUnderFlow");
            }
        }
    }
        

    protected static class MyBuffer extends LinkedList implements Buffer {

        public Object get() {
            if(isEmpty())
                throw new BufferUnderflowException();
            return get(0);
        }

        public Object remove() {
            if(isEmpty())
                throw new BufferUnderflowException();
            return remove(0);
        }
    }

    private void delay(){
        try {
            Thread.currentThread().sleep(100);
        } catch (InterruptedException e) {}
    }
}
