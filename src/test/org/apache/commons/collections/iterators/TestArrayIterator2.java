/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/iterators/TestArrayIterator2.java,v 1.1 2002/08/15 23:13:52 pjack Exp $
 * $Revision: 1.1 $
 * $Date: 2002/08/15 23:13:52 $
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

import junit.framework.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests the ArrayIterator with primitive type arrays
 * 
 * @author Morgan Delagrange
 * @author James Strachan
 * @version $Id: TestArrayIterator2.java,v 1.1 2002/08/15 23:13:52 pjack Exp $
 */
public class TestArrayIterator2 extends TestIterator {
    
    protected int[] testArray = {
        2, 4, 6, 8
    };
    
    public static Test suite() {
        return new TestSuite(TestArrayIterator2.class);
    }
    
    public TestArrayIterator2(String testName) {
        super(testName);
    }
    
    public Iterator makeEmptyIterator() {
        return new ArrayIterator(new int[0]);
    }

    public Iterator makeFullIterator() {
        return new ArrayIterator(testArray);
    }

    /**
     * Return a new, empty {@link Object} to used for testing.
     */
    public Object makeObject() {
        return makeFullIterator();
    }
    
    public void testIterator() {
        Iterator iter = (Iterator) makeFullIterator();
        for ( int i = 0; i < testArray.length; i++ ) {
            Integer testValue = new Integer( testArray[i] );            
            Number iterValue = (Number) iter.next();
            
            assertEquals( "Iteration value is correct", testValue, iterValue );
        }
        
        assertTrue("Iterator should now be empty", ! iter.hasNext() );

	try {
	    Object testValue = iter.next();
	} catch (Exception e) {
	  assertTrue("NoSuchElementException must be thrown", 
		 e.getClass().equals((new NoSuchElementException()).getClass()));
	}
    }

    // proves that an ArrayIterator set with the constructor has the same number of elements
    // as an ArrayIterator set with setArray(Object) 
    public void testSetArray() {
        Iterator iter1 = new ArrayIterator(testArray);
        int count1 = 0;
        while (iter1.hasNext()) {
            ++count1;
            iter1.next();
        }

        assertEquals("the count should be right using the constructor",
                     count1,testArray.length);

        ArrayIterator iter2 = new ArrayIterator();
        iter2.setArray(testArray);
        int count2 = 0;
        while (iter2.hasNext()) {
            ++count2;
            iter2.next();
        }

        assertEquals("the count should be right using setArray(Object)",
                     count2,testArray.length);
    }

    public void testIndexedArray() {
        Iterator iter = new ArrayIterator(testArray,2);
        int count = 0;
        while (iter.hasNext()) {
            ++count;
            iter.next();
        }

        assertEquals("the count should be right using ArrayIterator(Object,2) ",
                     count,testArray.length-2);

        iter = new ArrayIterator(testArray,1,testArray.length-1);
        count = 0;
        while (iter.hasNext()) {
            ++count;
            iter.next();
        }

        assertEquals("the count should be right using ArrayIterator(Object,1,"+
                     (testArray.length-1)+") ", count, testArray.length-2);

        try {
            iter = new ArrayIterator(testArray,-1);
            fail("new ArrayIterator(Object,-1) should throw an "+
                 "ArrayIndexOutOfBoundsException");
        } catch(ArrayIndexOutOfBoundsException aioobe) {
            // expected
        }

        try {
            iter = new ArrayIterator(testArray,testArray.length+1);
            fail("new ArrayIterator(Object,length+1) should throw an "+
                 "ArrayIndexOutOfBoundsException");
        } catch(ArrayIndexOutOfBoundsException aioobe) {
            // expected
        }

        try {
            iter = new ArrayIterator(testArray,0,-1);
            fail("new ArrayIterator(Object,0,-1) should throw an "+
                 "ArrayIndexOutOfBoundsException");
        } catch(ArrayIndexOutOfBoundsException aioobe) {
            // expected
        }

        try {
            iter = new ArrayIterator(testArray,0,testArray.length+1);
            fail("new ArrayIterator(Object,0,length+1) should throw an "+
                 "ArrayIndexOutOfBoundsException");
        } catch(ArrayIndexOutOfBoundsException aioobe) {
            // expected
        }

        try {
            iter = new ArrayIterator(testArray,1,1);
            fail("new ArrayIterator(Object,1,1) should throw an "+
                 "IllegalArgumentException");
        } catch(IllegalArgumentException iae) {
            // expected
        }

        try {
            iter = new ArrayIterator(testArray,testArray.length-1,testArray.length-2);
            fail("new ArrayIterator(Object,length-2,length-1) should throw an "+
                 "IllegalArgumentException");
        } catch(IllegalArgumentException iae) {
            // expected
        }
    }
}

