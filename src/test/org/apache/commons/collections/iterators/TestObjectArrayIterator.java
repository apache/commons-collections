/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/iterators/TestObjectArrayIterator.java,v 1.2 2003/08/31 17:28:40 scolebourne Exp $
 * $Revision: 1.2 $
 * $Date: 2003/08/31 17:28:40 $
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
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * Tests the ObjectArrayIterator.
 * 
 * @author James Strachan
 * @author Mauricio S. Moura
 * @author Morgan Delagrange
 * @author Stephen Colebourne
 * @version $Id: TestObjectArrayIterator.java,v 1.2 2003/08/31 17:28:40 scolebourne Exp $
 */
public class TestObjectArrayIterator extends TestIterator {

    protected String[] testArray = { "One", "Two", "Three" };

    public static Test suite() {
        return new TestSuite(TestObjectArrayIterator.class);
    }

    public TestObjectArrayIterator(String testName) {
        super(testName);
    }

    public Iterator makeEmptyIterator() {
        return new ObjectArrayIterator(new Object[0]);
    }

    public Iterator makeFullIterator() {
        return new ObjectArrayIterator(testArray);
    }

    public ObjectArrayIterator makeArrayIterator() {
        return new ObjectArrayIterator();
    }

    public ObjectArrayIterator makeArrayIterator(Object[] array) {
        return new ObjectArrayIterator(array);
    }

    public ObjectArrayIterator makeArrayIterator(Object[] array, int index) {
        return new ObjectArrayIterator(array, index);
    }

    public ObjectArrayIterator makeArrayIterator(Object[] array, int start, int end) {
        return new ObjectArrayIterator(array, start, end);
    }

    public boolean supportsRemove() {
        return false;
    }

    /**
     * Return a new, empty {@link Object} to used for testing.
     */
    public Object makeObject() {
        return makeFullIterator();
    }

    public void testIterator() {
        Iterator iter = (Iterator) makeFullIterator();
        for (int i = 0; i < testArray.length; i++) {
            Object testValue = testArray[i];
            Object iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

        assertTrue("Iterator should now be empty", !iter.hasNext());

        try {
            Object testValue = iter.next();
        } catch (Exception e) {
            assertTrue(
                "NoSuchElementException must be thrown",
                e.getClass().equals((new NoSuchElementException()).getClass()));
        }
    }

    public void testNullArray() {
        try {
            Iterator iter = makeArrayIterator(null);

            fail("Constructor should throw a NullPointerException when constructed with a null array");
        } catch (NullPointerException e) {
            // expected
        }

        ObjectArrayIterator iter = makeArrayIterator();
        try {
            iter.setArray(null);

            fail("setArray(null) should throw a NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testDoubleSet() {
        ObjectArrayIterator it = makeArrayIterator();
        it.setArray(new String[0]);
        try {
            it.setArray(new String[0]);
            fail();
        } catch (IllegalStateException ex) {
        }
    }

    public void testReset() {
        ObjectArrayIterator it = makeArrayIterator(testArray);
        it.next();
        it.reset();
        assertEquals("One", it.next());
    }

}
