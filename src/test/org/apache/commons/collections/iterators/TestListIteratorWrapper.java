/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the ListIteratorWrapper to insure that it simulates
 * a ListIterator correctly.
 *
 * @version $Revision: 1.6 $ $Date: 2004/02/18 01:20:33 $
 * 
 * @author Morgan Delagrange
 */
public class TestListIteratorWrapper extends AbstractTestIterator {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List list1 = null;

    public static Test suite() {
        return new TestSuite(TestListIteratorWrapper.class);
    }

    public TestListIteratorWrapper(String testName) {
        super(testName);
    }

    public void setUp() {
        list1 = new ArrayList();
        list1.add("One");
        list1.add("Two");
        list1.add("Three");
        list1.add("Four");
        list1.add("Five");
        list1.add("Six");
    }

    public Iterator makeEmptyIterator() {
        ArrayList list = new ArrayList();
        return new ListIteratorWrapper(list.iterator());
    }

    public Iterator makeFullIterator() {
        Iterator i = list1.iterator();

        return new ListIteratorWrapper(i);
    }

    public void testIterator() {
        ListIterator iter = (ListIterator) makeFullIterator();
        for ( int i = 0; i < testArray.length; i++ ) {
            Object testValue = testArray[i];            
            Object iterValue = iter.next();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        assertTrue("Iterator should now be empty", ! iter.hasNext() );

        try {
            Object testValue = iter.next();
        } catch (Exception e) {
            assertTrue("NoSuchElementException must be thrown", 
                       e.getClass().equals((new NoSuchElementException()).getClass()));
        }

        // now, read it backwards
        for (int i = testArray.length - 1; i > -1; --i) {
            Object testValue = testArray[i];
            Object iterValue = iter.previous();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        try {
            Object testValue = iter.previous();
        } catch (Exception e) {
            assertTrue("NoSuchElementException must be thrown", 
                       e.getClass().equals((new NoSuchElementException()).getClass()));
        }

        // now, read it forwards again
        for ( int i = 0; i < testArray.length; i++ ) {
            Object testValue = testArray[i];            
            Object iterValue = iter.next();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

    }

    public void testRemove() {
        Iterator iter = (Iterator) makeFullIterator();

        try {
            iter.remove();
            fail("FilterIterator does not support the remove() method");
        } catch (UnsupportedOperationException e) {

        }

    }

}

