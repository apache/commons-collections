/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestBoundedFifoBuffer2.java,v 1.6 2003/10/05 21:03:44 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Test;

/**
 * Runs tests against a full BoundedFifoBuffer, since many of the algorithms
 * differ depending on whether the fifo is full or not.
 * 
 * @version $Revision: 1.6 $ $Date: 2003/10/05 21:03:44 $
 * 
 * @author Unknown
 */
public class TestBoundedFifoBuffer2 extends TestBoundedFifoBuffer {

    public TestBoundedFifoBuffer2(String n) {
        super(n);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestBoundedFifoBuffer2.class);
    }

    /**
     *  Returns a BoundedFifoBuffer that's filled to capacity.
     *  Any attempt to add to the returned buffer will result in a 
     *  BufferOverflowException.
     *
     *  @return a full BoundedFifoBuffer
     */
    public Collection makeFullCollection() {
        return new BoundedFifoBuffer(Arrays.asList(getFullElements()));
    }


    /**
     *  Overridden to skip the add tests.  All of them would fail with a 
     *  BufferOverflowException.
     *
     *  @return false
     */
    public boolean isAddSupported() {
        return false;
    }


    /**
     *  Overridden because the add operations raise BufferOverflowException
     *  instead of UnsupportedOperationException.
     */
    public void testUnsupportedAdd() {
    }


    /**
     *  Tests to make sure the add operations raise BufferOverflowException.
     */
    public void testBufferOverflow() {
        resetFull();
        try {
            collection.add(getOtherElements()[0]);
            fail("add should raise BufferOverflow.");
        } catch (BufferOverflowException e) {
            // expected
        }
        verify();

        try {
            collection.addAll(Arrays.asList(getOtherElements()));
            fail("addAll should raise BufferOverflow.");
        } catch (BufferOverflowException e) {
            // expected
        }
        verify();
    }

    /**
     * Tests is full
     */
    public void testIsFull() {
        resetFull();
        assertEquals(true, ((BoundedCollection) collection).isFull());
        ((BoundedFifoBuffer) collection).remove();
        assertEquals(false, ((BoundedCollection) collection).isFull());
        ((BoundedFifoBuffer) collection).add("jj");
        assertEquals(true, ((BoundedCollection) collection).isFull());
    }

    /**
     * Tests max size
     */
    public void testMaxSize() {
        resetFull();
        assertEquals(getFullElements().length, ((BoundedCollection) collection).maxSize());
        ((BoundedFifoBuffer) collection).remove();
        assertEquals(getFullElements().length, ((BoundedCollection) collection).maxSize());
        ((BoundedFifoBuffer) collection).add("jj");
        assertEquals(getFullElements().length, ((BoundedCollection) collection).maxSize());
    }

}

