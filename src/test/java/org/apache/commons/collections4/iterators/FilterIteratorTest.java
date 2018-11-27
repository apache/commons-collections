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

import static org.apache.commons.collections4.functors.TruePredicate.truePredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.NotNullPredicate;

/**
 * Test the filter iterator.
 *
 */
public class FilterIteratorTest<E> extends AbstractIteratorTest<E> {

    /** Creates new TestFilterIterator */
    public FilterIteratorTest(final String name) {
        super(name);
    }

    private String[] array;
    private List<E> list;
    private FilterIterator<E> iterator;

    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() {
        array = new String[] { "a", "b", "c" };
        initIterator();
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() throws Exception {
        iterator = null;
    }

    /**
     * Returns an full iterator wrapped in a
     * FilterIterator that blocks all the elements
     *
     * @return "empty" FilterIterator
     */
    @Override
    public FilterIterator<E> makeEmptyIterator() {
        return makeBlockAllFilter(new ArrayIterator<E>(array));
    }

    /**
     * Returns an array with elements wrapped in a pass-through
     * FilterIterator
     *
     * @return a filtered iterator
     */
    @Override
    @SuppressWarnings("unchecked")
    public FilterIterator<E> makeObject() {
        list = new ArrayList<>(Arrays.asList((E[]) array));
        return makePassThroughFilter(list.iterator());
    }

    public void testRepeatedHasNext() {
        for (int i = 0; i <= array.length; i++) {
            assertTrue(iterator.hasNext());
        }
    }

    @SuppressWarnings("unused")
    public void testRepeatedNext() {
        for (final String element : array) {
            iterator.next();
        }
        verifyNoMoreElements();
    }

    public void testReturnValues() {
        verifyElementsInPredicate(new String[0]);
        verifyElementsInPredicate(new String[] { "a" });
        verifyElementsInPredicate(new String[] { "b" });
        verifyElementsInPredicate(new String[] { "c" });
        verifyElementsInPredicate(new String[] { "a", "b" });
        verifyElementsInPredicate(new String[] { "a", "c" });
        verifyElementsInPredicate(new String[] { "b", "c" });
        verifyElementsInPredicate(new String[] { "a", "b", "c" });
    }

    /**
     * Test that when the iterator is changed, the hasNext method returns the
     * correct response for the new iterator.
     */
    @SuppressWarnings("unchecked")
    public void testSetIterator() {
        final Iterator<E> iter1 = Collections.singleton((E) new Object()).iterator();
        final Iterator<E> iter2 = Collections.<E>emptyList().iterator();

        final FilterIterator<E> filterIterator = new FilterIterator<>(iter1);
        filterIterator.setPredicate(truePredicate());
        // this iterator has elements
        assertEquals(true, filterIterator.hasNext());

        // this iterator has no elements
        filterIterator.setIterator(iter2);
        assertEquals(false, filterIterator.hasNext());
    }

    /**
     * Test that when the predicate is changed, the hasNext method returns the
     * correct response for the new predicate.
     */
    public void testSetPredicate() {
        final Iterator<E> iter = Collections.singleton((E) null).iterator();

        final FilterIterator<E> filterIterator = new FilterIterator<>(iter);
        filterIterator.setPredicate(truePredicate());
        // this predicate matches
        assertEquals(true, filterIterator.hasNext());

        // this predicate doesn't match
        filterIterator.setPredicate(NotNullPredicate.notNullPredicate());
        assertEquals(false, filterIterator.hasNext());
    }

    private void verifyNoMoreElements() {
        assertTrue(!iterator.hasNext());
        try {
            iterator.next();
            fail("NoSuchElementException expected");
        }
        catch (final NoSuchElementException e) {
            // success
        }
    }

    private void verifyElementsInPredicate(final String[] elements) {
        final Predicate<E> pred = new Predicate<E>() {
            @Override
            public boolean evaluate(final E x) {
                for (final String element : elements) {
                    if (element.equals(x)) {
                        return true;
                    }
                }
                return false;
            }
        };
        initIterator();
        iterator.setPredicate(pred);
        for (int i = 0; i < elements.length; i++) {
            final String s = (String)iterator.next();
            assertEquals(elements[i], s);
            assertTrue(i == elements.length - 1 ? !iterator.hasNext() : iterator.hasNext());
        }
        verifyNoMoreElements();

        // test removal
        initIterator();
        iterator.setPredicate(pred);
        if (iterator.hasNext()) {
            final Object last = iterator.next();
            iterator.remove();
            assertTrue("Base of FilterIterator still contains removed element.", !list.contains(last));
        }
    }

    private void initIterator() {
        iterator = makeObject();
    }

    /**
     * Returns a FilterIterator that does not filter
     * any of its elements
     *
     * @param i      the Iterator to "filter"
     * @return "filtered" iterator
     */
    protected FilterIterator<E> makePassThroughFilter(final Iterator<E> i) {
        final Predicate<E> pred = new Predicate<E>() {
                @Override
                public boolean evaluate(final E x) { return true; }
        };
        return new FilterIterator<>(i, pred);
    }

    /**
     * Returns a FilterIterator that blocks
     * all of its elements
     *
     * @param i      the Iterator to "filter"
     * @return "filtered" iterator
     */
    protected FilterIterator<E> makeBlockAllFilter(final Iterator<E> i) {
        final Predicate<E> pred = new Predicate<E>() {
                @Override
                public boolean evaluate(final E x) { return false; }
        };
        return new FilterIterator<>(i, pred);
    }
}

