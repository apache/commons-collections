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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Predicate;

/**
 * Tests the LazyIteratorChain class.
 *
 */
public class LazyIteratorChainTest extends AbstractIteratorTest<String> {

    protected String[] testArray = {
        "One", "Two", "Three", "Four", "Five", "Six"
    };

    protected List<String> list1 = null;
    protected List<String> list2 = null;
    protected List<String> list3 = null;

    public LazyIteratorChainTest(final String testName) {
        super(testName);
    }

    @Override
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

    @Override
    public LazyIteratorChain<String> makeEmptyIterator() {
        return new LazyIteratorChain<String>() {
            @Override
            protected Iterator<String> nextIterator(final int count) {
                return null;
            }
        };
    }

    @Override
    public LazyIteratorChain<String> makeObject() {
        final LazyIteratorChain<String> chain = new LazyIteratorChain<String>() {
            @Override
            protected Iterator<String> nextIterator(final int count) {
                switch (count) {
                case 1:
                    return list1.iterator();
                case 2:
                    return list2.iterator();
                case 3:
                    return list3.iterator();
                }
                return null;
            }
        };

        return chain;
    }

    public void testIterator() {
        final Iterator<String> iter = makeObject();
        for (final String testValue : testArray) {
            final Object iterValue = iter.next();

            assertEquals( "Iteration value is correct", testValue, iterValue );
        }

        assertTrue(!iter.hasNext());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            iter.next();
        });
        assertNull(exception.getMessage());
    }

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

    @Override
    public void testRemove() {
        final Iterator<String> iter = makeObject();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            iter.remove();
        });
        assertNull(exception.getMessage());

        for (final String testValue : testArray) {
            final String iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);

            if (!iterValue.equals("Four")) {
                iter.remove();
            }
        }

        assertTrue(list1.size() == 0);
        assertTrue(list2.size() == 1);
        assertTrue(list3.size() == 0);
    }

    public void testFirstIteratorIsEmptyBug() {
        final List<String> empty = new ArrayList<>();
        final List<String> notEmpty = new ArrayList<>();
        notEmpty.add("A");
        notEmpty.add("B");
        notEmpty.add("C");
        final LazyIteratorChain<String> chain = new LazyIteratorChain<String>() {
            @Override
            protected Iterator<String> nextIterator(final int count) {
                switch (count) {
                case 1:
                    return empty.iterator();
                case 2:
                    return notEmpty.iterator();
                }
                return null;
            }
        };
        assertTrue(chain.hasNext());
        assertEquals("A", chain.next());
        assertTrue(chain.hasNext());
        assertEquals("B", chain.next());
        assertTrue(chain.hasNext());
        assertEquals("C", chain.next());
        assertTrue(!chain.hasNext());
    }

    public void testEmptyChain() {
        final LazyIteratorChain<String> chain = makeEmptyIterator();
        assertEquals(false, chain.hasNext());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            chain.next();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            chain.remove();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
    }
}
