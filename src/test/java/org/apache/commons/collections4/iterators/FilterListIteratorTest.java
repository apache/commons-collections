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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.list.GrowthList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the FilterListIterator class.
 *
 */
@SuppressWarnings("boxing")
public class FilterListIteratorTest {

    private ArrayList<Integer> list = null;
    private ArrayList<Integer> odds = null;
    private ArrayList<Integer> evens = null;
    private ArrayList<Integer> threes = null;
    private ArrayList<Integer> fours = null;
    private ArrayList<Integer> sixes = null;
    private Predicate<Integer> truePred = null;
    private Predicate<Integer> falsePred = null;
    private Predicate<Integer> evenPred = null;
    private Predicate<Integer> oddPred = null;
    private Predicate<Integer> threePred = null;
    private Predicate<Integer> fourPred = null;
    private final Random random = new Random();

    @BeforeEach
    public void setUp() {
        list = new ArrayList<>();
        odds = new ArrayList<>();
        evens = new ArrayList<>();
        threes = new ArrayList<>();
        fours = new ArrayList<>();
        sixes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(Integer.valueOf(i));
            if (i % 2 == 0) {
                evens.add(Integer.valueOf(i));
            }
            if (i % 2 != 0) {
                odds.add(Integer.valueOf(i));
            }
            if (i % 3 == 0) {
                threes.add(Integer.valueOf(i));
            }
            if (i % 4 == 0) {
                fours.add(Integer.valueOf(i));
            }
            if (i % 6 == 0) {
                sixes.add(Integer.valueOf(i));
            }
        }

        truePred = x -> true;

        falsePred = x -> true;

        evenPred = x -> x % 2 == 0;

        oddPred = x -> x % 2 != 0;

        threePred = x -> x % 3 == 0;

        fourPred = x -> x % 4 == 0;

    }

    @AfterEach
    public void tearDown() throws Exception {
        list = null;
        odds = null;
        evens = null;
        threes = null;
        fours = null;
        sixes = null;
        truePred = null;
        falsePred = null;
        evenPred = null;
        oddPred = null;
        threePred = null;
        fourPred = null;
    }

    @Test
    public void testWalkLists() {
        // this just confirms that our walkLists method works OK
        walkLists(list, list.listIterator());
    }

    @Test
    public void testManual() {
        // do this one "by hand" as a sanity check
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), threePred);

        assertEquals(Integer.valueOf(0), filtered.next());
        assertEquals(Integer.valueOf(3), filtered.next());
        assertEquals(Integer.valueOf(6), filtered.next());
        assertEquals(Integer.valueOf(9), filtered.next());
        assertEquals(Integer.valueOf(12), filtered.next());
        assertEquals(Integer.valueOf(15), filtered.next());
        assertEquals(Integer.valueOf(18), filtered.next());

        assertEquals(Integer.valueOf(18), filtered.previous());
        assertEquals(Integer.valueOf(15), filtered.previous());
        assertEquals(Integer.valueOf(12), filtered.previous());
        assertEquals(Integer.valueOf(9), filtered.previous());
        assertEquals(Integer.valueOf(6), filtered.previous());
        assertEquals(Integer.valueOf(3), filtered.previous());
        assertEquals(Integer.valueOf(0), filtered.previous());

        assertFalse(filtered.hasPrevious());

        assertEquals(Integer.valueOf(0), filtered.next());
        assertEquals(Integer.valueOf(3), filtered.next());
        assertEquals(Integer.valueOf(6), filtered.next());
        assertEquals(Integer.valueOf(9), filtered.next());
        assertEquals(Integer.valueOf(12), filtered.next());
        assertEquals(Integer.valueOf(15), filtered.next());
        assertEquals(Integer.valueOf(18), filtered.next());

        assertFalse(filtered.hasNext());

        assertEquals(Integer.valueOf(18), filtered.previous());
        assertEquals(Integer.valueOf(15), filtered.previous());
        assertEquals(Integer.valueOf(12), filtered.previous());
        assertEquals(Integer.valueOf(9), filtered.previous());
        assertEquals(Integer.valueOf(6), filtered.previous());
        assertEquals(Integer.valueOf(3), filtered.previous());
        assertEquals(Integer.valueOf(0), filtered.previous());

        assertEquals(Integer.valueOf(0), filtered.next());
        assertEquals(Integer.valueOf(0), filtered.previous());
        assertEquals(Integer.valueOf(0), filtered.next());

        assertEquals(Integer.valueOf(3), filtered.next());
        assertEquals(Integer.valueOf(6), filtered.next());
        assertEquals(Integer.valueOf(6), filtered.previous());
        assertEquals(Integer.valueOf(3), filtered.previous());
        assertEquals(Integer.valueOf(3), filtered.next());
        assertEquals(Integer.valueOf(6), filtered.next());

        assertEquals(Integer.valueOf(9), filtered.next());
        assertEquals(Integer.valueOf(12), filtered.next());
        assertEquals(Integer.valueOf(15), filtered.next());
        assertEquals(Integer.valueOf(15), filtered.previous());
        assertEquals(Integer.valueOf(12), filtered.previous());
        assertEquals(Integer.valueOf(9), filtered.previous());
    }

    @Test
    public void testTruePredicate() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), truePred);
        walkLists(list, filtered);
    }

    @Test
    public void testFalsePredicate() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), falsePred);
        walkLists(new ArrayList<Integer>(), filtered);
    }

    @Test
    public void testEvens() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), evenPred);
        walkLists(evens, filtered);
    }

    @Test
    public void testOdds() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), oddPred);
        walkLists(odds, filtered);
    }

    @Test
    public void testThrees() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), threePred);
        walkLists(threes, filtered);
    }

    @Test
    public void testFours() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), fourPred);
        walkLists(fours, filtered);
    }

    @Test
    public void testNestedSixes() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(
                                        new FilterListIterator<>(list.listIterator(), threePred),
                                        evenPred
                                      );
        walkLists(sixes, filtered);
    }

    @Test
    public void testNestedSixes2() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(
                                        new FilterListIterator<>(list.listIterator(), evenPred),
                                        threePred
                                      );
        walkLists(sixes, filtered);
    }

    @Test
    public void testNestedSixes3() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(
                                        new FilterListIterator<>(list.listIterator(), threePred),
                                        evenPred
                                      );
        walkLists(sixes, new FilterListIterator<>(filtered, truePred));
    }

    @Test
    public void testNextChangesPrevious() {
        {
            final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), threePred);
            nextNextPrevious(threes.listIterator(), filtered);
        }

        {
            final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), truePred);
            nextNextPrevious(list.listIterator(), filtered);
        }
    }

    @Test
    public void testPreviousChangesNext() {
        {
            final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), threePred);
            final ListIterator<Integer> expected = threes.listIterator();
            walkForward(expected, filtered);
            previousPreviousNext(expected, filtered);
        }
        {
            final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), truePred);
            final ListIterator<Integer> expected = list.listIterator();
            walkForward(expected, filtered);
            previousPreviousNext(expected, filtered);
        }
    }

    @Test
    public void testFailingHasNextBug() {
        final FilterListIterator<Integer> filtered = new FilterListIterator<>(list.listIterator(), fourPred);
        final ListIterator<Integer> expected = fours.listIterator();
        while (expected.hasNext()) {
            expected.next();
            filtered.next();
        }
        assertTrue(filtered.hasPrevious());
        assertFalse(filtered.hasNext());
        assertEquals(expected.previous(), filtered.previous());
    }

    /**
     * Test for {@link "https://issues.apache.org/jira/browse/COLLECTIONS-360 COLLECTIONS-360"}
     */
    @Test
    public void testCollections360() throws Throwable {
        final Collection<Predicate<Object>> var7 = new GrowthList<>();
        final Predicate<Object> var9 = PredicateUtils.anyPredicate(var7);
        final FilterListIterator<Object> var13 = new FilterListIterator<>(var9);
        assertFalse(var13.hasNext());
        final FilterListIterator<Object> var14 = new FilterListIterator<>(var9);
        assertFalse(var14.hasPrevious());
    }

    // Utilities

    private void walkForward(final ListIterator<?> expected, final ListIterator<?> testing) {
        while (expected.hasNext()) {
            assertEquals(expected.nextIndex(), testing.nextIndex());
            assertEquals(expected.previousIndex(), testing.previousIndex());
            assertTrue(testing.hasNext());
            assertEquals(expected.next(), testing.next());
        }
    }

    private void walkBackward(final ListIterator<?> expected, final ListIterator<?> testing) {
        while (expected.hasPrevious()) {
            assertEquals(expected.nextIndex(), testing.nextIndex());
            assertEquals(expected.previousIndex(), testing.previousIndex());
            assertTrue(testing.hasPrevious());
            assertEquals(expected.previous(), testing.previous());
        }
    }

    private void nextNextPrevious(final ListIterator<?> expected, final ListIterator<?> testing) {
        // calls to next() should change the value returned by previous()
        // even after previous() has been set by a call to hasPrevious()
        assertEquals(expected.next(), testing.next());
        assertEquals(expected.hasPrevious(), testing.hasPrevious());
        final Object expecteda = expected.next();
        final Object testinga = testing.next();
        assertEquals(expecteda, testinga);
        final Object expectedb = expected.previous();
        final Object testingb = testing.previous();
        assertEquals(expecteda, expectedb);
        assertEquals(testinga, testingb);
    }

    private void previousPreviousNext(final ListIterator<?> expected, final ListIterator<?> testing) {
        // calls to previous() should change the value returned by next()
        // even after next() has been set by a call to hasNext()
        assertEquals(expected.previous(), testing.previous());
        assertEquals(expected.hasNext(), testing.hasNext());
        final Object expecteda = expected.previous();
        final Object testinga = testing.previous();
        assertEquals(expecteda, testinga);
        final Object expectedb = expected.next();
        final Object testingb = testing.next();
        assertEquals(expecteda, testingb);
        assertEquals(expecteda, expectedb);
        assertEquals(testinga, testingb);
    }

    private <E> void walkLists(final List<E> list, final ListIterator<E> testing) {
        final ListIterator<E> expected = list.listIterator();

        // walk all the way forward
        walkForward(expected, testing);

        // walk all the way back
        walkBackward(expected, testing);

        // forward,back,forward
        while (expected.hasNext()) {
            assertEquals(expected.nextIndex(), testing.nextIndex());
            assertEquals(expected.previousIndex(), testing.previousIndex());
            assertTrue(testing.hasNext());
            assertEquals(expected.next(), testing.next());
            assertTrue(testing.hasPrevious());
            assertEquals(expected.previous(), testing.previous());
            assertTrue(testing.hasNext());
            assertEquals(expected.next(), testing.next());
        }

        // walk all the way back
        walkBackward(expected, testing);

        for (int i = 0; i < list.size(); i++) {
            // walk forward i
            for (int j = 0; j < i; j++) {
                assertEquals(expected.nextIndex(), testing.nextIndex());
                assertEquals(expected.previousIndex(), testing.previousIndex());
                assertTrue(expected.hasNext()); // if this one fails we've got a logic error in the test
                assertTrue(testing.hasNext());
                assertEquals(expected.next(), testing.next());
            }
            // walk back i/2
            for (int j = 0; j < i / 2; j++) {
                assertEquals(expected.nextIndex(), testing.nextIndex());
                assertEquals(expected.previousIndex(), testing.previousIndex());
                assertTrue(expected.hasPrevious()); // if this one fails we've got a logic error in the test
                assertTrue(testing.hasPrevious());
                assertEquals(expected.previous(), testing.previous());
            }
            // walk forward i/2
            for (int j = 0; j < i / 2; j++) {
                assertEquals(expected.nextIndex(), testing.nextIndex());
                assertEquals(expected.previousIndex(), testing.previousIndex());
                assertTrue(expected.hasNext()); // if this one fails we've got a logic error in the test
                assertTrue(testing.hasNext());
                assertEquals(expected.next(), testing.next());
            }
            // walk back i
            for (int j = 0; j < i; j++) {
                assertEquals(expected.nextIndex(), testing.nextIndex());
                assertEquals(expected.previousIndex(), testing.previousIndex());
                assertTrue(expected.hasPrevious()); // if this one fails we've got a logic error in the test
                assertTrue(testing.hasPrevious());
                assertEquals(expected.previous(), testing.previous());
            }
        }

        // random walk
        final StringBuilder walkdescr = new StringBuilder(500);
        for (int i = 0; i < 500; i++) {
            if (random.nextBoolean()) {
                // step forward
                walkdescr.append("+");
                if (expected.hasNext()) {
                    assertEquals(expected.next(), testing.next(), walkdescr.toString());
                }
            } else {
                // step backward
                walkdescr.append("-");
                if (expected.hasPrevious()) {
                    assertEquals(expected.previous(), testing.previous(), walkdescr.toString());
                }
            }
            assertEquals(expected.nextIndex(), testing.nextIndex(), walkdescr.toString());
            assertEquals(expected.previousIndex(), testing.previousIndex(), walkdescr.toString());
        }

    }

}
