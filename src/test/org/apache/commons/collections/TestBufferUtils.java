/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestBufferUtils.java,v 1.7 2003/10/02 22:14:28 scolebourne Exp $
 * $Revision: 1.7 $
 * $Date: 2003/10/02 22:14:28 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 *    permission of the Apache Group.
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

import org.apache.commons.collections.decorators.PredicatedBuffer;

/**
 *  Tests for BufferUtils.
 */
public class TestBufferUtils extends BulkTest {

    public TestBufferUtils(String name) {
        super(name);
    }


    public static Test suite() {
        return BulkTest.makeSuite(TestBufferUtils.class);
    }

    public void testNothing() {
    }

    public void testpredicatedBuffer() {
        Predicate predicate = new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
        Buffer buffer = BufferUtils.predicatedBuffer(new ArrayStack(), predicate);
        assertTrue("returned object should be a PredicatedBuffer",
            buffer instanceof PredicatedBuffer);
        try {
            buffer = BufferUtils.predicatedBuffer(new ArrayStack(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            buffer = BufferUtils.predicatedBuffer(null, predicate);
            fail("Expecting IllegalArgumentException for null buffer.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }


    public BulkTest bulkTestUnmodifiableBuffer() {
        return new AbstractTestCollection("") {
            public boolean isAddSupported() {
                return false;
            }

            public boolean isRemoveSupported() {
                return false;
            }

            public Collection makeCollection() {
                return BufferUtils.unmodifiableBuffer(new ArrayStack());
            }

            public Collection makeFullCollection() {
                ArrayStack a = new ArrayStack();
                a.addAll(Arrays.asList(getFullElements()));
                return BufferUtils.unmodifiableBuffer(a);
            }


            public Collection makeConfirmedCollection() {
                return new ArrayStack();
            }

            public Collection makeConfirmedFullCollection() {
                ArrayStack a = new ArrayStack();
                a.addAll(Arrays.asList(getFullElements()));
                return a;
            }

        };
    }


}


