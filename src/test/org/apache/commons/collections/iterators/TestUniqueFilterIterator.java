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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the ArrayIterator to ensure that the next() method will actually
 * perform the iteration rather than the hasNext() method.
 * The code of this test was supplied by Mauricio S. Moura
 * 
 * @author James Strachan
 * @author Mauricio S. Moura
 * @author Morgan Delagrange
 * @version $Id: TestUniqueFilterIterator.java,v 1.2.2.1 2004/05/22 12:14:04 scolebourne Exp $
 */
public class TestUniqueFilterIterator extends TestIterator {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List list1 = null;

    public static Test suite() {
        return new TestSuite(TestUniqueFilterIterator.class);
    }

    public TestUniqueFilterIterator(String testName) {
        super(testName);
    }

    public void setUp() {
        list1 = new ArrayList();
        list1.add("One");
        list1.add("Two");
        list1.add("Three");
        list1.add("Two");
        list1.add("One");
        list1.add("Four");
        list1.add("Five");
        list1.add("Five");
        list1.add("Six");
        list1.add("Five");
    }

    public Iterator makeEmptyIterator() {
        ArrayList list = new ArrayList();
        return new UniqueFilterIterator(list.iterator());
    }

    public Iterator makeFullIterator() {
        Iterator i = list1.iterator();

        return new UniqueFilterIterator(i);
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

