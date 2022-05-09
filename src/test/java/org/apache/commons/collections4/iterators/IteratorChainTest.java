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
package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the IteratorChain class.
 */
public class IteratorChainTest extends AbstractIteratorTest<String> {

    protected String[] testArray = {
            "One", "Two", "Three", "Four", "Five", "Six"
    };
    protected String[] testArray1234 = {
            "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight"
    };

    protected List<String> list1 = null;
    protected List<String> list2 = null;
    protected List<String> list3 = null;
    protected List<String> list4 = null;

    public IteratorChainTest() {
        super(IteratorChainTest.class.getSimpleName());
    }

    @BeforeEach
    public void setUp() {
        list1 = new ArrayList<>();
        list1.add("One");
        list1.add("Two");
        list1.add("Three");
        list2 = new ArrayList<>();
        list2.add("Four");
        list3 = new ArrayList<>();
        list3.add("Five");
        list3.add("Six");
        list4 = new ArrayList<>();
        list4.add("Seven");
        list4.add("Eight");
    }

    @Override
    public IteratorChain<String> makeEmptyIterator() {
        final ArrayList<String> list = new ArrayList<>();
        return new IteratorChain<>(list.iterator());
    }

    @Override
    public IteratorChain<String> makeObject() {
        final IteratorChain<String> chain = new IteratorChain<>();

        chain.addIterator(list1.iterator());
        chain.addIterator(list2.iterator());
        chain.addIterator(list3.iterator());
        return chain;
    }

    @Test
    public void testIterator() {
        final Iterator<String> iter = makeObject();
        for (final String testValue : testArray) {
            final Object iterValue = iter.next();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        assertFalse("Iterator should now be empty", iter.hasNext());

        try {
            iter.next();
        } catch (final Exception e) {
            assertEquals("NoSuchElementException must be thrown", e.getClass(), new NoSuchElementException().getClass());
        }
    }

    @Test
    public void testRemoveFromFilteredIterator() {

        final Predicate<Integer> myPredicate = i -> i.compareTo(Integer.valueOf(4)) < 0;

        final List<Integer> list1 = new ArrayList<>();
        final List<Integer> list2 = new ArrayList<>();

        list1.add(Integer.valueOf(1));
        list1.add(Integer.valueOf(2));
        list2.add(Integer.valueOf(3));
        list2.add(Integer.valueOf(4)); // will be ignored by the predicate

        final Iterator<Integer> it1 = IteratorUtils.filteredIterator(list1.iterator(), myPredicate);
        final Iterator<Integer> it2 = IteratorUtils.filteredIterator(list2.iterator(), myPredicate);

        final Iterator<Integer> it = IteratorUtils.chainedIterator(it1, it2);
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        assertEquals(0, list1.size());
        assertEquals(1, list2.size());
    }

    @Test
    @Override
    public void testRemove() {
        final Iterator<String> iter = makeObject();

        assertThrows(IllegalStateException.class, () -> iter.remove(),
                "Calling remove before the first call to next() should throw an exception");

        for (final String testValue : testArray) {
            final String iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);

            if (!iterValue.equals("Four")) {
                iter.remove();
            }
        }

        assertTrue("List is empty", list1.isEmpty());
        assertEquals("List is empty", 1, list2.size());
        assertTrue("List is empty", list3.isEmpty());
    }

    @Test
    public void testFirstIteratorIsEmptyBug() {
        final List<String> empty = new ArrayList<>();
        final List<String> notEmpty = new ArrayList<>();
        notEmpty.add("A");
        notEmpty.add("B");
        notEmpty.add("C");
        final IteratorChain<String> chain = new IteratorChain<>();
        chain.addIterator(empty.iterator());
        chain.addIterator(notEmpty.iterator());
        assertTrue("should have next", chain.hasNext());
        assertEquals("A", chain.next());
        assertTrue("should have next", chain.hasNext());
        assertEquals("B", chain.next());
        assertTrue("should have next", chain.hasNext());
        assertEquals("C", chain.next());
        assertFalse("should not have next", chain.hasNext());
    }

    @Test
    public void testEmptyChain() {
        final IteratorChain<Object> chain = new IteratorChain<>();
        assertFalse(chain.hasNext());
        assertAll(
                () -> assertThrows(NoSuchElementException.class, () -> chain.next()),
                () -> assertThrows(IllegalStateException.class, () -> chain.remove())
        );
    }

    @Test
    public void testChainOfChains() {
        final Iterator<String> iteratorChain1 = new IteratorChain<>(list1.iterator(), list2.iterator());
        final Iterator<String> iteratorChain2 = new IteratorChain<>(list3.iterator(), list4.iterator());
        final Iterator<String> iteratorChainOfChains = new IteratorChain<>(iteratorChain1, iteratorChain2);

        for (final String testValue : testArray1234) {
            final Object iterValue = iteratorChainOfChains.next();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        assertFalse("Iterator should now be empty", iteratorChainOfChains.hasNext());

        try {
            iteratorChainOfChains.next();
        } catch (final Exception e) {
            assertEquals("NoSuchElementException must be thrown", e.getClass(), NoSuchElementException.class);
        }
    }

    @Test
    public void testChainOfUnmodifiableChains() {
        final Iterator<String> iteratorChain1 = new IteratorChain<>(list1.iterator(), list2.iterator());
        final Iterator<String> unmodifiableChain1 = IteratorUtils.unmodifiableIterator(iteratorChain1);
        final Iterator<String> iteratorChain2 = new IteratorChain<>(list3.iterator(), list4.iterator());
        final Iterator<String> unmodifiableChain2 = IteratorUtils.unmodifiableIterator(iteratorChain2);
        final Iterator<String> iteratorChainOfChains = new IteratorChain<>(unmodifiableChain1, unmodifiableChain2);

        for (final String testValue : testArray1234) {
            final Object iterValue = iteratorChainOfChains.next();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        assertFalse("Iterator should now be empty", iteratorChainOfChains.hasNext());

        try {
            iteratorChainOfChains.next();
        } catch (final Exception e) {
            assertEquals("NoSuchElementException must be thrown", e.getClass(), NoSuchElementException.class);
        }

    }

    @Test
    public void testChainOfUnmodifiableChainsRetainsUnmodifiableBehaviourOfNestedIterator() {
        final Iterator<String> iteratorChain1 = new IteratorChain<>(list1.iterator(), list2.iterator());
        final Iterator<String> unmodifiableChain1 = IteratorUtils.unmodifiableIterator(iteratorChain1);
        final Iterator<String> iteratorChain2 = new IteratorChain<>(list3.iterator(), list4.iterator());
        final Iterator<String> unmodifiableChain2 = IteratorUtils.unmodifiableIterator(iteratorChain2);
        final Iterator<String> iteratorChainOfChains = new IteratorChain<>(unmodifiableChain1, unmodifiableChain2);

        iteratorChainOfChains.next();
        assertThrows(UnsupportedOperationException.class, iteratorChainOfChains::remove,
                "Calling remove must fail when nested iterator is unmodifiable");    }
}
