/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/iterators/TestObjectArrayListIterator.java,v 1.1 2002/12/13 12:10:48 scolebourne Exp $
 * $Revision: 1.1 $
 * $Date: 2002/12/13 12:10:48 $
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * 
 * @author <a href="mailto:neilotoole@users.sourceforge.net">Neil O'Toole</a>
 * @version $Id: TestObjectArrayListIterator.java,v 1.1 2002/12/13 12:10:48 scolebourne Exp $
 */
public class TestObjectArrayListIterator extends TestObjectArrayIterator {

    public TestObjectArrayListIterator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestObjectArrayListIterator.class);
    }

    public Iterator makeEmptyIterator() {
        return new ObjectArrayListIterator(new Object[0]);
    }

    public Iterator makeFullIterator() {
        return new ObjectArrayListIterator(testArray);
    }

    public ListIterator makeArrayListIterator(Object[] array) {
        return new ObjectArrayListIterator(array);
    }

    /**
     * Test the basic ListIterator functionality - going backwards using
     * <code>previous()</code>.
     */
    public void testListIterator() {
        ListIterator iter = (ListIterator) makeFullIterator();

        // TestArrayIterator#testIterator() has already tested the iterator forward,
        //  now we need to test it in reverse

        // fast-forward the iterator to the end...
        while (iter.hasNext()) {
            iter.next();
        }

        for (int x = testArray.length - 1; x >= 0; x--) {
            Object testValue = testArray[x];
            Object iterValue = iter.previous();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

        assertTrue("Iterator should now be empty", !iter.hasPrevious());

        try {
            Object testValue = iter.previous();
        } catch (Exception e) {
            assertTrue(
                "NoSuchElementException must be thrown",
                e.getClass().equals((new NoSuchElementException()).getClass()));
        }

    }

    /**
     * Tests the {@link java.util.ListIterator#set} operation.
     */
    public void testListIteratorSet() {
        String[] testData = new String[] { "a", "b", "c" };

        String[] result = new String[] { "0", "1", "2" };

        ListIterator iter = (ListIterator) makeArrayListIterator(testData);
        int x = 0;

        while (iter.hasNext()) {
            iter.next();
            iter.set(Integer.toString(x));
            x++;
        }

        assertTrue("The two arrays should have the same value, i.e. {0,1,2}", Arrays.equals(testData, result));

        // a call to set() before a call to next() or previous() should throw an IllegalStateException
        iter = makeArrayListIterator(testArray);

        try {
            iter.set("should fail");
            fail("ListIterator#set should fail if next() or previous() have not yet been called.");
        } catch (IllegalStateException e) {
            // expected
        } catch (Throwable t) { // should never happen
            fail(t.toString());
        }

    }

}
