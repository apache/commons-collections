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
 * @version $Id: TestIteratorChain.java,v 1.2.2.1 2004/05/22 12:14:04 scolebourne Exp $
 */
public class TestIteratorChain extends TestIterator {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List list1 = null;
    protected List list2 = null;
    protected List list3 = null;

    public static Test suite() {
        return new TestSuite(TestIteratorChain.class);
    }

    public TestIteratorChain(String testName) {
        super(testName);
    }

    public void setUp() {
        list1 = new ArrayList();
        list1.add("One");
        list1.add("Two");
        list1.add("Three");
        list2 = new ArrayList();
        list2.add("Four");
        list3 = new ArrayList();
        list3.add("Five");
        list3.add("Six");        
    }

    public Iterator makeEmptyIterator() {
        ArrayList list = new ArrayList();
        return new IteratorChain(list.iterator());
    }

    public Iterator makeFullIterator() {
        IteratorChain chain = new IteratorChain();

        Iterator i = list1.iterator();

        chain.addIterator(list1.iterator());
        chain.addIterator(list2.iterator());
        chain.addIterator(list3.iterator());
        return chain;
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
            fail("Calling remove before the first call to next() should throw an exception");
        } catch (IllegalStateException e) {

        }

        for ( int i = 0; i < testArray.length; i++ ) {
            Object testValue = testArray[i];            
            Object iterValue = iter.next();

            assertEquals( "Iteration value is correct", testValue, iterValue );

            if (! iterValue.equals("Four")) {
                iter.remove();
            }
        }

        assertTrue("List is empty",list1.size() == 0);
        assertTrue("List is empty",list2.size() == 1);
        assertTrue("List is empty",list3.size() == 0);
    }

}

