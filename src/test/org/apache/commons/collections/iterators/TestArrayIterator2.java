/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @version $Id: TestArrayIterator2.java,v 1.1.2.1 2004/05/22 12:14:04 scolebourne Exp $
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

