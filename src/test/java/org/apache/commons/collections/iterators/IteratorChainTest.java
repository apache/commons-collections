/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

/**
 * Tests the IteratorChain class.
 *
 * @version $Id$
 */
public class IteratorChainTest extends AbstractIteratorTest<String> {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List<String> list1 = null;
    protected List<String> list2 = null;
    protected List<String> list3 = null;

    public IteratorChainTest(String testName) {
        super(testName);
    }

    @Override
    public void setUp() {
        list1 = new ArrayList<String>();
        list1.add("One");
        list1.add("Two");
        list1.add("Three");
        list2 = new ArrayList<String>();
        list2.add("Four");
        list3 = new ArrayList<String>();
        list3.add("Five");
        list3.add("Six");        
    }

    @Override
    public IteratorChain<String> makeEmptyIterator() {
        ArrayList<String> list = new ArrayList<String>();
        return new IteratorChain<String>(list.iterator());
    }

    @Override
    public IteratorChain<String> makeObject() {
        IteratorChain<String> chain = new IteratorChain<String>();

        chain.addIterator(list1.iterator());
        chain.addIterator(list2.iterator());
        chain.addIterator(list3.iterator());
        return chain;
    }

    public void testIterator() {
        Iterator<String> iter = makeObject();
        for (String testValue : testArray) {
            Object iterValue = iter.next();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        assertTrue("Iterator should now be empty", !iter.hasNext());

        try {
            iter.next();
        } catch (Exception e) {
            assertTrue("NoSuchElementException must be thrown", 
                       e.getClass().equals(new NoSuchElementException().getClass()));
        }
    }

    public void testRemoveFromFilteredIterator() {

        final Predicate<Integer> myPredicate = new Predicate<Integer>() {
            public boolean evaluate(Integer i) {
                return i.compareTo(new Integer(4)) < 0;
            }
        };

        List<Integer> list1 = new ArrayList<Integer>();
        List<Integer> list2 = new ArrayList<Integer>();

        list1.add(new Integer(1));
        list1.add(new Integer(2));
        list2.add(new Integer(3));
        list2.add(new Integer(4)); // will be ignored by the predicate

        Iterator<Integer> it1 = IteratorUtils.filteredIterator(list1.iterator(), myPredicate);
        Iterator<Integer> it2 = IteratorUtils.filteredIterator(list2.iterator(), myPredicate);

        Iterator<Integer> it = IteratorUtils.chainedIterator(it1, it2);
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        assertEquals(0, list1.size());
        assertEquals(1, list2.size());
    }
    
    @Override
    public void testRemove() {
        Iterator<String> iter = makeObject();

        try {
            iter.remove();
            fail("Calling remove before the first call to next() should throw an exception");
        } catch (IllegalStateException e) {

        }

        for (String testValue : testArray) {
            String iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);

            if (!iterValue.equals("Four")) {
                iter.remove();
            }
        }

        assertTrue("List is empty",list1.size() == 0);
        assertTrue("List is empty",list2.size() == 1);
        assertTrue("List is empty",list3.size() == 0);
    }

    public void testFirstIteratorIsEmptyBug() {
        List<String> empty = new ArrayList<String>();
        List<String> notEmpty = new ArrayList<String>();
        notEmpty.add("A");
        notEmpty.add("B");
        notEmpty.add("C");
        IteratorChain<String> chain = new IteratorChain<String>();
        chain.addIterator(empty.iterator());
        chain.addIterator(notEmpty.iterator());
        assertTrue("should have next",chain.hasNext());
        assertEquals("A",chain.next());
        assertTrue("should have next",chain.hasNext());
        assertEquals("B",chain.next());
        assertTrue("should have next",chain.hasNext());
        assertEquals("C",chain.next());
        assertTrue("should not have next",!chain.hasNext());
    }
    
    public void testEmptyChain() {
        IteratorChain<Object> chain = new IteratorChain<Object>();
        assertEquals(false, chain.hasNext());
        try {
            chain.next();
            fail();
        } catch (NoSuchElementException ex) {}
        try {
            chain.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }
        
}
