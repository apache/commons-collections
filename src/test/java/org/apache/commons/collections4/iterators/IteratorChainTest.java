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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    protected List<String> list1;
    protected List<String> list2;
    protected List<String> list3;

    public List<String> getList1() {
        return list1;
    }

    public List<String> getList2() {
        return list2;
    }

    public List<String> getList3() {
        return list3;
    }

    public String[] getTestArray() {
        return testArray;
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
    }

    @Test
    public void testConstructList() {
        final List<Iterator<String>> list = new ArrayList<>();
        list.add(list1.iterator());
        list.add(list2.iterator());
        list.add(list3.iterator());
        final List<String> expected = new ArrayList<>(list1);
        expected.addAll(list2);
        expected.addAll(list3);
        final IteratorChain<String> iter = new IteratorChain<>(list);
        final List<String> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(actual, expected);
    }

    @Test
    public void testEmptyChain() {
        final IteratorChain<Object> chain = new IteratorChain<>();
        assertFalse(chain.hasNext());
        assertThrows(NoSuchElementException.class, () -> chain.next());
        assertThrows(IllegalStateException.class, () -> chain.remove());
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
        assertTrue(chain.hasNext(), "should have next");
        assertEquals("A", chain.next());
        assertTrue(chain.hasNext(), "should have next");
        assertEquals("B", chain.next());
        assertTrue(chain.hasNext(), "should have next");
        assertEquals("C", chain.next());
        assertFalse(chain.hasNext(), "should not have next");
    }

    @Test
    public void testIterator() {
        final Iterator<String> iter = makeObject();
        for (final String testValue : testArray) {
            final Object iterValue = iter.next();
            assertEquals(testValue, iterValue, "Iteration value is correct");
        }
        assertFalse(iter.hasNext(), "Iterator should now be empty");
        assertThrows(NoSuchElementException.class, iter::next);
    }

    @Test
    @Override
    public void testRemove() {
        final Iterator<String> iter = makeObject();
        assertThrows(IllegalStateException.class, () -> iter.remove(), "Calling remove before the first call to next() should throw an exception");
        for (final String testValue : testArray) {
            final String iterValue = iter.next();
            assertEquals(testValue, iterValue, "Iteration value is correct");
            if (!iterValue.equals("Four")) {
                iter.remove();
            }
        }
        assertTrue(list1.isEmpty(), "List is empty");
        assertEquals(1, list2.size(), "List is empty");
        assertTrue(list3.isEmpty(), "List is empty");
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

}
