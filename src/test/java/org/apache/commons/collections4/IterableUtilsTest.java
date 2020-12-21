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
package org.apache.commons.collections4;

import static org.apache.commons.collections4.functors.EqualPredicate.equalPredicate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.bag.HashBag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for IterableUtils.
 *
 * @since 4.1
 */
public class IterableUtilsTest {

    /**
     * Iterable of {@link Integer}s
     */
    private Iterable<Integer> iterableA = null;

    /**
     * Iterable of {@link Long}s
     */
    private Iterable<Long> iterableB = null;

    /**
     * An empty Iterable.
     */
    private Iterable<Integer> emptyIterable = null;

    @Before
    public void setUp() {
        final Collection<Integer> collectionA = new ArrayList<>();
        collectionA.add(1);
        collectionA.add(2);
        collectionA.add(2);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
        iterableA = collectionA;

        final Collection<Long> collectionB = new LinkedList<>();
        collectionB.add(5L);
        collectionB.add(4L);
        collectionB.add(4L);
        collectionB.add(3L);
        collectionB.add(3L);
        collectionB.add(3L);
        collectionB.add(2L);
        collectionB.add(2L);
        collectionB.add(2L);
        collectionB.add(2L);
        iterableB = collectionB;

        emptyIterable = Collections.emptyList();
    }

    private static final Predicate<Number> EQUALS_TWO = input -> input.intValue() == 2;

    private static final Predicate<Number> EVEN = input -> input.intValue() % 2 == 0;

    // -----------------------------------------------------------------------
    @Test
    public void forEach() {
        final List<Integer> listA = new ArrayList<>();
        listA.add(1);

        final List<Integer> listB = new ArrayList<>();
        listB.add(2);

        final Closure<List<Integer>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<Integer>> col = new ArrayList<>();
        col.add(listA);
        col.add(listB);
        IterableUtils.forEach(col, testClosure);
        assertTrue(listA.isEmpty() && listB.isEmpty());
        try {
            IterableUtils.forEach(col, null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
            // expected
        }

        IterableUtils.forEach(null, testClosure);

        // null should be OK
        col.add(null);
        IterableUtils.forEach(col, testClosure);
    }

    @Test(expected = FunctorException.class)
    public void forEachFailure() {
        final Closure<String> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<String> col = new ArrayList<>();
        col.add("x");
        IterableUtils.forEach(col, testClosure);
    }

    @Test
    public void forEachButLast() {
        final List<Integer> listA = new ArrayList<>();
        listA.add(1);

        final List<Integer> listB = new ArrayList<>();
        listB.add(2);

        final Closure<List<Integer>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<Integer>> col = new ArrayList<>();
        col.add(listA);
        col.add(listB);
        List<Integer> last = IterableUtils.forEachButLast(col, testClosure);
        assertTrue(listA.isEmpty() && !listB.isEmpty());
        assertSame(listB, last);

        try {
            IterableUtils.forEachButLast(col, null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
            // expected
        }

        IterableUtils.forEachButLast(null, testClosure);

        // null should be OK
        col.add(null);
        col.add(null);
        last = IterableUtils.forEachButLast(col, testClosure);
        assertNull(last);
    }

    @Test
    public void containsWithEquator() {
        final List<String> base = new ArrayList<>();
        base.add("AC");
        base.add("BB");
        base.add("CA");

        final Equator<String> secondLetterEquator = new Equator<String>() {

            @Override
            public boolean equate(final String o1, final String o2) {
                return o1.charAt(1) == o2.charAt(1);
            }

            @Override
            public int hash(final String o) {
                return o.charAt(1);
            }

        };

        assertFalse(base.contains("CC"));
        assertTrue(IterableUtils.contains(base, "AC", secondLetterEquator));
        assertTrue(IterableUtils.contains(base, "CC", secondLetterEquator));
        assertFalse(IterableUtils.contains(base, "CX", secondLetterEquator));
        assertFalse(IterableUtils.contains(null, null, secondLetterEquator));

        try {
            IterableUtils.contains(base, "AC", null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
        } // this is what we want
    }

    @Test
    public void frequency() {
        // null iterable test
        assertEquals(0, IterableUtils.frequency(null, 1));

        assertEquals(1, IterableUtils.frequency(iterableA, 1));
        assertEquals(2, IterableUtils.frequency(iterableA, 2));
        assertEquals(3, IterableUtils.frequency(iterableA, 3));
        assertEquals(4, IterableUtils.frequency(iterableA, 4));
        assertEquals(0, IterableUtils.frequency(iterableA, 5));

        assertEquals(0, IterableUtils.frequency(iterableB, 1L));
        assertEquals(4, IterableUtils.frequency(iterableB, 2L));
        assertEquals(3, IterableUtils.frequency(iterableB, 3L));
        assertEquals(2, IterableUtils.frequency(iterableB, 4L));
        assertEquals(1, IterableUtils.frequency(iterableB, 5L));

        // Ensure that generic bounds accept valid parameters, but return
        // expected results
        // e.g. no longs in the "int" Iterable<Number>, and vice versa.
        final Iterable<Number> iterableIntAsNumber = Arrays.<Number>asList(1, 2, 3, 4, 5);
        final Iterable<Number> iterableLongAsNumber = Arrays.<Number>asList(1L, 2L, 3L, 4L, 5L);
        assertEquals(0, IterableUtils.frequency(iterableIntAsNumber, 2L));
        assertEquals(0, IterableUtils.frequency(iterableLongAsNumber, 2));

        final Set<String> set = new HashSet<>();
        set.add("A");
        set.add("C");
        set.add("E");
        set.add("E");
        assertEquals(1, IterableUtils.frequency(set, "A"));
        assertEquals(0, IterableUtils.frequency(set, "B"));
        assertEquals(1, IterableUtils.frequency(set, "C"));
        assertEquals(0, IterableUtils.frequency(set, "D"));
        assertEquals(1, IterableUtils.frequency(set, "E"));

        final Bag<String> bag = new HashBag<>();
        bag.add("A", 3);
        bag.add("C");
        bag.add("E");
        bag.add("E");
        assertEquals(3, IterableUtils.frequency(bag, "A"));
        assertEquals(0, IterableUtils.frequency(bag, "B"));
        assertEquals(1, IterableUtils.frequency(bag, "C"));
        assertEquals(0, IterableUtils.frequency(bag, "D"));
        assertEquals(2, IterableUtils.frequency(bag, "E"));
    }

    @Test
    public void frequencyOfNull() {
        final List<String> list = new ArrayList<>();
        assertEquals(0, IterableUtils.frequency(list, null));
        list.add("A");
        assertEquals(0, IterableUtils.frequency(list, null));
        list.add(null);
        assertEquals(1, IterableUtils.frequency(list, null));
        list.add("B");
        assertEquals(1, IterableUtils.frequency(list, null));
        list.add(null);
        assertEquals(2, IterableUtils.frequency(list, null));
        list.add("B");
        assertEquals(2, IterableUtils.frequency(list, null));
        list.add(null);
        assertEquals(3, IterableUtils.frequency(list, null));
    }

    @Test
    public void find() {
        Predicate<Number> testPredicate = equalPredicate(4);
        Integer test = IterableUtils.find(iterableA, testPredicate);
        assertEquals(4, (int) test);
        testPredicate = equalPredicate(45);
        test = IterableUtils.find(iterableA, testPredicate);
        assertNull(test);
        assertNull(IterableUtils.find(null, testPredicate));
        try {
            IterableUtils.find(iterableA, null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void indexOf() {
        Predicate<Number> testPredicate = equalPredicate((Number) 4);
        int index = IterableUtils.indexOf(iterableA, testPredicate);
        assertEquals(6, index);
        testPredicate = equalPredicate((Number) 45);
        index = IterableUtils.indexOf(iterableA, testPredicate);
        assertEquals(-1, index);
        assertEquals(-1, IterableUtils.indexOf(null, testPredicate));
        try {
            IterableUtils.indexOf(iterableA, null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void countMatches() {
        assertEquals(4, IterableUtils.countMatches(iterableB, EQUALS_TWO));
        assertEquals(0, IterableUtils.countMatches(null, EQUALS_TWO));

        try {
            assertEquals(0, IterableUtils.countMatches(iterableA, null));
            fail("predicate must not be null");
        } catch (final NullPointerException ex) {
            // expected
        }

        try {
            assertEquals(0, IterableUtils.countMatches(null, null));
            fail("predicate must not be null");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void matchesAny() {
        final List<Integer> list = new ArrayList<>();

        try {
            assertFalse(IterableUtils.matchesAny(null, null));
            fail("predicate must not be null");
        } catch (final NullPointerException ex) {
            // expected
        }

        try {
            assertFalse(IterableUtils.matchesAny(list, null));
            fail("predicate must not be null");
        } catch (final NullPointerException ex) {
            // expected
        }

        assertFalse(IterableUtils.matchesAny(null, EQUALS_TWO));
        assertFalse(IterableUtils.matchesAny(list, EQUALS_TWO));
        list.add(1);
        list.add(3);
        list.add(4);
        assertFalse(IterableUtils.matchesAny(list, EQUALS_TWO));

        list.add(2);
        assertTrue(IterableUtils.matchesAny(list, EQUALS_TWO));
    }

    @Test
    public void matchesAll() {
        try {
            assertFalse(IterableUtils.matchesAll(null, null));
            fail("predicate must not be null");
        } catch (final NullPointerException ex) {
            // expected
        }

        try {
            assertFalse(IterableUtils.matchesAll(iterableA, null));
            fail("predicate must not be null");
        } catch (final NullPointerException ex) {
            // expected
        }

        final Predicate<Integer> lessThanFive = object -> object < 5;
        assertTrue(IterableUtils.matchesAll(iterableA, lessThanFive));

        final Predicate<Integer> lessThanFour = object -> object < 4;
        assertFalse(IterableUtils.matchesAll(iterableA, lessThanFour));

        assertTrue(IterableUtils.matchesAll(null, lessThanFour));
        assertTrue(IterableUtils.matchesAll(emptyIterable, lessThanFour));
    }

    public void getFromIterable() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<>();
        bag.add("element", 1);
        assertEquals("element", IterableUtils.get(bag, 0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFromIterableIndexOutOfBoundsException() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<>();
        bag.add("element", 1);
        // Collection, non-existent entry
        IterableUtils.get(bag, 1);
    }

    public void firstFromIterable() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<>();
        bag.add("element", 1);
        assertEquals("element", IterableUtils.first(bag));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void firstFromIterableIndexOutOfBoundsException() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<>();
        // Collection, non-existent entry
        IterableUtils.first(bag);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void partition() {
        final List<Integer> input = new ArrayList<>();
        input.add(1);
        input.add(2);
        input.add(3);
        input.add(4);
        List<List<Integer>> partitions = IterableUtils.partition(input, EQUALS_TWO);
        assertEquals(2, partitions.size());

        // first partition contains 2
        Collection<Integer> partition = partitions.get(0);
        assertEquals(1, partition.size());
        assertEquals(2, CollectionUtils.extractSingleton(partition).intValue());

        // second partition contains 1, 3, and 4
        final Integer[] expected = {1, 3, 4};
        partition = partitions.get(1);
        Assert.assertArrayEquals(expected, partition.toArray());

        partitions = IterableUtils.partition((List<Integer>) null, EQUALS_TWO);
        assertEquals(2, partitions.size());
        assertTrue(partitions.get(0).isEmpty());
        assertTrue(partitions.get(1).isEmpty());

        partitions = IterableUtils.partition(input);
        assertEquals(1, partitions.size());
        assertEquals(input, partitions.get(0));

        try {
            IterableUtils.partition(input, (Predicate<Integer>) null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void partitionMultiplePredicates() {
        final List<Integer> input = new ArrayList<>();
        input.add(1);
        input.add(2);
        input.add(3);
        input.add(4);
        final List<List<Integer>> partitions = IterableUtils.partition(input, EQUALS_TWO, EVEN);

        // first partition contains 2
        Collection<Integer> partition = partitions.get(0);
        assertEquals(1, partition.size());
        assertEquals(2, partition.iterator().next().intValue());

        // second partition contains 4
        partition = partitions.get(1);
        assertEquals(1, partition.size());
        assertEquals(4, partition.iterator().next().intValue());

        // third partition contains 1 and 3
        final Integer[] expected = {1, 3};
        partition = partitions.get(2);
        Assert.assertArrayEquals(expected, partition.toArray());

        try {
            IterableUtils.partition(input, EQUALS_TWO, null);
        } catch (final NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testToString() {
        String result = IterableUtils.toString(iterableA);
        assertEquals("[1, 2, 2, 3, 3, 3, 4, 4, 4, 4]", result);

        result = IterableUtils.toString(new ArrayList<Integer>());
        assertEquals("[]", result);

        result = IterableUtils.toString(null);
        assertEquals("[]", result);

        result = IterableUtils.toString(iterableA, input -> Integer.toString(input * 2));
        assertEquals("[2, 4, 4, 6, 6, 6, 8, 8, 8, 8]", result);

        result = IterableUtils.toString(new ArrayList<Integer>(), input -> {
            fail("not supposed to reach here");
            return "";
        });
        assertEquals("[]", result);

        result = IterableUtils.toString(null, input -> {
            fail("not supposed to reach here");
            return "";
        });
        assertEquals("[]", result);
    }

    @Test
    public void testToStringDelimiter() {

        final Transformer<Integer, String> transformer = input -> Integer.toString(input * 2);

        String result = IterableUtils.toString(iterableA, transformer, "", "", "");
        assertEquals("2446668888", result);

        result = IterableUtils.toString(iterableA, transformer, ",", "", "");
        assertEquals("2,4,4,6,6,6,8,8,8,8", result);

        result = IterableUtils.toString(iterableA, transformer, "", "[", "]");
        assertEquals("[2446668888]", result);

        result = IterableUtils.toString(iterableA, transformer, ",", "[", "]");
        assertEquals("[2,4,4,6,6,6,8,8,8,8]", result);

        result = IterableUtils.toString(iterableA, transformer, ",", "[[", "]]");
        assertEquals("[[2,4,4,6,6,6,8,8,8,8]]", result);

        result = IterableUtils.toString(iterableA, transformer, ",,", "[", "]");
        assertEquals("[2,,4,,4,,6,,6,,6,,8,,8,,8,,8]", result);

        result = IterableUtils.toString(iterableA, transformer, ",,", "((", "))");
        assertEquals("((2,,4,,4,,6,,6,,6,,8,,8,,8,,8))", result);

        result = IterableUtils.toString(new ArrayList<Integer>(), transformer, "", "(", ")");
        assertEquals("()", result);

        result = IterableUtils.toString(new ArrayList<Integer>(), transformer, "", "", "");
        assertEquals("", result);
    }

    @Test
    public void testToStringWithNullArguments() {
        final String result = IterableUtils.toString(null, input -> {
            fail("not supposed to reach here");
            return "";
        }, "", "(", ")");
        assertEquals("()", result);

        try {
            IterableUtils.toString(new ArrayList<Integer>(), null, "", "(", ")");
            fail("expecting NullPointerException");
        } catch (final NullPointerException ex) {
            // expected
        }

        try {
            IterableUtils.toString(new ArrayList<Integer>(), input -> {
                fail("not supposed to reach here");
                return "";
            }, null, "(", ")");
            fail("expecting NullPointerException");
        } catch (final NullPointerException ex) {
            // expected
        }

        try {
            IterableUtils.toString(new ArrayList<Integer>(), input -> {
                fail("not supposed to reach here");
                return "";
            }, "", null, ")");
            fail("expecting NullPointerException");
        } catch (final NullPointerException ex) {
            // expected
        }

        try {
            IterableUtils.toString(new ArrayList<Integer>(), input -> {
                fail("not supposed to reach here");
                return "";
            }, "", "(", null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void size() {
        assertEquals(0, IterableUtils.size(null));
    }
}
