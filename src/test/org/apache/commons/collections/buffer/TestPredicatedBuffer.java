/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/buffer/TestPredicatedBuffer.java,v 1.1 2003/11/16 00:05:46 scolebourne Exp $
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

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.collection.TestPredicatedCollection;

/**
 * Extension of {@link TestPredicatedCollection} for exercising the 
 * {@link PredicatedBuffer} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/11/16 00:05:46 $
 * 
 * @author Phil Steitz
 */
public class TestPredicatedBuffer extends TestPredicatedCollection {
    
    public TestPredicatedBuffer(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestPredicatedBuffer.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestPredicatedBuffer.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    //---------------------------------------------------------------
    
    protected Buffer decorateBuffer(Buffer buffer, Predicate predicate) {
        return PredicatedBuffer.decorate(buffer, predicate);
    }
    
    public Collection makeCollection() {
        return decorateBuffer(new ArrayStack(), truePredicate);
    }
    
    public Collection makeConfirmedCollection() {
        return new ArrayStack();
    }
    
    public Collection makeConfirmedFullCollection() {
        ArrayStack list = new ArrayStack();
        list.addAll(java.util.Arrays.asList(getFullElements()));
        return list;
    }
    
    //------------------------------------------------------------
    
    public Buffer makeTestBuffer() {
        return decorateBuffer(new ArrayStack(), testPredicate);
    }
    
    public void testGet() {
        Buffer buffer = makeTestBuffer();
        try {
            Object o = buffer.get();
            fail("Expecting BufferUnderflowException");
        } catch (BufferUnderflowException ex) {
            // expected
        }
        buffer.add("one");
        buffer.add("two");
        buffer.add("three");
        assertEquals("Buffer get", buffer.get(), "three");
    }
    
    public void testRemove() {
        Buffer buffer = makeTestBuffer();
        buffer.add("one");
        assertEquals("Buffer get", buffer.remove(), "one");
        try {
            buffer.remove();
            fail("Expecting BufferUnderflowException");
        } catch (BufferUnderflowException ex) {
            // expected
        }      
    }
}
