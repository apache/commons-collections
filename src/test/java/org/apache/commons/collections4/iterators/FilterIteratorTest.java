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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.FalsePredicate;
import org.apache.commons.collections4.functors.NotNullPredicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the filter iterator.
 *
 * @param <E> the type of elements tested by this iterator.
 */
public class FilterIteratorTest<E> extends AbstractIteratorTest<E> {

    private static final List<Integer> collectionInts = Arrays.asList(1, 2, 3, 4, 5, 6);

    private String[] array;
    private List<E> list;
    private FilterIterator<E> iterator;

    private void initIterator() {
        iterator = makeObject();
    }

    /**
     * Returns a FilterIterator that blocks
     * all of its elements
     *
     * @param i      the Iterator to "filter"
     * @return "filtered" iterator
     */
    protected FilterIterator<E> makeBlockAllFilter(final Iterator<E> i) {
        final Predicate<E> pred = x -> false;
        return new FilterIterator<>(i, pred);
    }

    /**
     * Returns a full iterator wrapped in a
     * FilterIterator that blocks all the elements
     *
     * @return "empty" FilterIterator
     */
    @Override
    public FilterIterator<E> makeEmptyIterator() {
        return makeBlockAllFilter(new ArrayIterator<>(array));
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

    /**
     * Returns a FilterIterator that does not filter
     * any of its elements
     *
     * @param i      the Iterator to "filter"
     * @return "filtered" iterator
     */
    protected FilterIterator<E> makePassThroughFilter(final Iterator<E> i) {
        final Predicate<E> pred = x -> true;
        return new FilterIterator<>(i, pred);
    }

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {
        array = new String[] { "a", "b", "c" };
        initIterator();
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() throws Exception {
        iterator = null;
    }

    @Test
    public void testAddTo() {
        final List<E> expected = new ArrayList<>(list);
        expected.addAll(list);
        final FilterIterator<E> filterIterator = new FilterIterator<>(list.iterator());
        final List<E> actual = filterIterator.addTo(new ArrayList<>(list));
        assertEquals(expected, actual);
    }

    @Test
    public void testAddToCollection() {
        final List<E> expected = new ArrayList<>(list);
        expected.addAll(list);
        final FilterIterator<E> filterIterator = new FilterIterator<>(list.iterator());
        final List<E> actual = filterIterator.toCollection(() -> new ArrayList<>(list));
        assertEquals(expected, actual);
    }

    @Test
    public void testAddToEmpty() {
        final FilterIterator<E> filterIterator = makeEmptyIterator();
        final List<E> actual = filterIterator.addTo(new ArrayList<>(list));
        assertEquals(list, actual);
    }

    @Test
    public void testAddToEmptyToEmpty() {
        final FilterIterator<E> filterIterator = makeEmptyIterator();
        final List<E> actual = filterIterator.addTo(new ArrayList<>());
        assertTrue(actual.isEmpty());
    }

    /**
     * Tests a predicate that accepts some but not all elements.
     */
    @Test
    public void testConstructorPredicateFilterInts() {
        final List<Integer> expected = Arrays.asList(2, 4, 6);
        final Predicate<Integer> predicate = i -> i % 2 == 0;
        final FilterIterator<Integer> filter = new FilterIterator<>(collectionInts.iterator(), predicate);
        final List<Integer> actual = new ArrayList<>();
        filter.forEachRemaining(actual::add);
        assertEquals(expected, actual);
    }

    /**
     * Tests a predicate that accepts everything.
     */
    @Test
    public void testForEachRemainingAcceptAllCtor() {
        final List<E> expected = IteratorUtils.toList(makeObject());
        final FilterIterator<E> it = new FilterIterator<>(makeObject(), TruePredicate.truePredicate());
        final List<E> actual = new ArrayList<>();
        it.forEachRemaining(actual::add);
        assertEquals(expected, actual);
    }

    @Test
    public void testForEachRemainingDefaultCtor() {
        final List<E> expected = IteratorUtils.toList(makeObject());
        final FilterIterator<E> it = new FilterIterator<>();
        it.setIterator(expected.iterator());
        final List<E> actual = new ArrayList<>();
        it.forEachRemaining(actual::add);
        assertEquals(expected, actual);
    }

    /**
     * Tests a predicate that rejects everything.
     */
    @Test
    public void testForEachRemainingRejectAllCtor() {
        final List<E> expected = IteratorUtils.toList(makeObject());
        final FilterIterator<E> it = new FilterIterator<>(makeObject(), FalsePredicate.falsePredicate());
        final List<E> actual = new ArrayList<>();
        it.forEachRemaining(actual::add);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testRemoveNext() {
        final FilterIterator<E> iter = makeObject();
        final E i = iter.removeNext();
        assertFalse(list.contains(i));
        final List<E> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(list, actual);
    }

    @Test
    public void testRemoveNextEmpty() {
        final FilterIterator<E> empty = makeEmptyIterator();
        assertThrows(NoSuchElementException.class, empty::removeNext);
    }

    @Test
    public void testRepeatedHasNext() {
        for (int i = 0; i <= array.length; i++) {
            assertTrue(iterator.hasNext());
        }
    }

    @Test
    @SuppressWarnings("unused")
    public void testRepeatedNext() {
        for (final String element : array) {
            iterator.next();
        }
        verifyNoMoreElements();
    }

    @Test
    public void testReturnValues() {
        verifyElementsInPredicate(ArrayUtils.EMPTY_STRING_ARRAY);
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
    @Test
    @SuppressWarnings("unchecked")
    public void testSetIterator() {
        final Iterator<E> iter1 = Collections.singleton((E) new Object()).iterator();
        final Iterator<E> iter2 = Collections.<E>emptyList().iterator();

        final FilterIterator<E> filterIterator = new FilterIterator<>(iter1);
        filterIterator.setPredicate(TruePredicate.truePredicate());
        // this iterator has elements
        assertTrue(filterIterator.hasNext());

        // this iterator has no elements
        filterIterator.setIterator(iter2);
        assertFalse(filterIterator.hasNext());
    }

    /**
     * Test that when the predicate is changed, the hasNext method returns the
     * correct response for the new predicate.
     */
    @Test
    public void testSetPredicate() {
        final Iterator<E> iter = Collections.singleton((E) null).iterator();

        final FilterIterator<E> filterIterator = new FilterIterator<>(iter);
        filterIterator.setPredicate(TruePredicate.truePredicate());
        // this predicate matches
        assertTrue(filterIterator.hasNext());

        // this predicate doesn't match
        filterIterator.setPredicate(NotNullPredicate.notNullPredicate());
        assertFalse(filterIterator.hasNext());
    }

    @Test
    public void testToCollectionAsDeque() {
        final Deque<E> expected = new ArrayDeque<>(list);
        final FilterIterator<E> filterIterator = new FilterIterator<>(list.iterator());
        final Deque<E> actual = filterIterator.toCollection(ArrayDeque::new);
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    public void testToList() {
        final List<E> expected = new ArrayList<>(list);
        final FilterIterator<E> filterIterator = new FilterIterator<>(list.iterator());
        final List<E> actual = filterIterator.toList();
        assertEquals(expected, actual);
    }

    @Test
    public void testToSet() {
        final Set<E> expected = new HashSet<>(list);
        final FilterIterator<E> filterIterator = new FilterIterator<>(list.iterator());
        final Set<E> actual = filterIterator.toSet();
        assertEquals(expected, actual);
    }

    private void verifyElementsInPredicate(final String[] elements) {
        final Predicate<E> pred = x -> {
            for (final String element : elements) {
                if (element.equals(x)) {
                    return true;
                }
            }
            return false;
        };
        initIterator();
        iterator.setPredicate(pred);
        for (int i = 0; i < elements.length; i++) {
            final String s = (String) iterator.next();
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
            assertFalse(list.contains(last), "Base of FilterIterator still contains removed element.");
        }
    }

    private void verifyNoMoreElements() {
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

}
