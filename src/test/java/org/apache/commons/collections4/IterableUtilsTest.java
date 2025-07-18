/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for IterableUtils.
 */
class IterableUtilsTest {

    private static final Predicate<Number> EQUALS_TWO = input -> input.intValue() == 2;

    private static final Predicate<Number> EVEN = input -> input.intValue() % 2 == 0;

    /**
     * Iterable of {@link Integer}s
     */
    private Iterable<Integer> iterableA;

    /**
     * Iterable of {@link Long}s
     */
    private Iterable<Long> iterableB;

    /**
     * An empty Iterable.
     */
    private Iterable<Integer> emptyIterable;

    public void firstFromIterable() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<>();
        bag.add("element", 1);
        assertEquals("element", IterableUtils.first(bag));
    }

    public void getFromIterable() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<>();
        bag.add("element", 1);
        assertEquals("element", IterableUtils.get(bag, 0));
    }

    @BeforeEach
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

    @Test
    void testContainsWithEquator() {
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

        assertThrows(NullPointerException.class, () -> IterableUtils.contains(base, "AC", null), "expecting NullPointerException");
    }

    @Test
    void testCountMatches() {
        assertEquals(4, IterableUtils.countMatches(iterableB, EQUALS_TWO));
        assertEquals(0, IterableUtils.countMatches(null, EQUALS_TWO));
        assertThrows(NullPointerException.class, () -> assertEquals(0, IterableUtils.countMatches(iterableA, null)), "predicate must not be null");
        assertThrows(NullPointerException.class, () -> assertEquals(0, IterableUtils.countMatches(null, null)), "predicate must not be null");
    }

    @Test
    void testDuplicateListAllSameInList() {
        final List<Integer> input = Arrays.asList(5, 5, 5, 5);
        assertEquals(Arrays.asList(5), IterableUtils.duplicateList(input));
    }

    @Test
    void testDuplicateListEmptyDeque() {
        assertTrue(IterableUtils.duplicateList(new ArrayDeque<>()).isEmpty());
    }

    @Test
    void testDuplicateListEmptyList() {
        final List<Integer> input = Arrays.asList();
        assertTrue(IterableUtils.duplicateList(input).isEmpty());
    }

    @Test
    void testDuplicateListEmptySet() {
        assertTrue(IterableUtils.duplicateList(new HashSet<>()).isEmpty());
    }

    @Test
    void testDuplicateListMultipleDuplicatesInDeque() {
        final Deque<Integer> input = new ArrayDeque<>(Arrays.asList(1, 1, 2, 2, 3, 3, 4, 4));
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4);
        assertEquals(expected, IterableUtils.duplicateList(input));
    }

    @Test
    void testDuplicateListMultipleDuplicatesInDequeReverse() {
        // We want to make sure that the actual list is in the expected order
        final Deque<Integer> input = new ArrayDeque<>(Arrays.asList(4, 4, 3, 3, 2, 2, 1, 1));
        final List<Integer> expected = Arrays.asList(4, 3, 2, 1);
        assertEquals(expected, IterableUtils.duplicateList(input));
    }

    @Test
    void testDuplicateListMultipleDuplicatesInList() {
        final List<Integer> input = Arrays.asList(1, 1, 2, 2, 3, 3, 4, 4);
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4);
        assertEquals(expected, IterableUtils.duplicateList(input));
    }

    @Test
    void testDuplicateListMultipleDuplicatesInListReverse() {
        // We want to make sure that the actual list is in the expected order
        final List<Integer> input = Arrays.asList(4, 4, 3, 3, 2, 2, 1, 1);
        final List<Integer> expected = Arrays.asList(4, 3, 2, 1);
        assertEquals(expected, IterableUtils.duplicateList(input));
    }

    @Test
    void testDuplicateListNoDuplicates() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        assertTrue(IterableUtils.duplicateList(input).isEmpty());
    }

    @Test
    void testDuplicateListSingleElement() {
        final List<Integer> input = Arrays.asList(1);
        assertTrue(IterableUtils.duplicateList(input).isEmpty());
    }

    @Test
    void testDuplicateListWithDuplicates() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 2, 4, 5, 3);
        final List<Integer> expected = Arrays.asList(2, 3);
        assertEquals(expected, IterableUtils.duplicateList(input));
    }

    @Test
    void testDuplicateSequencedSetMultipleDuplicates() {
        final List<Integer> input = Arrays.asList(1, 1, 2, 2, 3, 3, 4, 4);
        final List<Integer> list = Arrays.asList(1, 2, 3, 4);
        assertEquals(list, new ArrayList<>(IterableUtils.duplicateSequencedSet(input)));
        assertEquals(new LinkedHashSet<>(list), IterableUtils.duplicateSequencedSet(input));
    }

    @Test
    void testDuplicateSetEmptyDeque() {
        assertTrue(IterableUtils.duplicateSet(new ArrayDeque<>()).isEmpty());
    }

    @Test
    void testDuplicateSetEmptyList() {
        final List<Integer> input = Arrays.asList();
        assertTrue(IterableUtils.duplicateSet(input).isEmpty());
    }

    @Test
    void testDuplicateSetEmptySet() {
        assertTrue(IterableUtils.duplicateSet(new HashSet<>()).isEmpty());
    }

    @Test
    void testDuplicateSetInSet() {
        // Sets don't have duplicates, so the result is always an empty set.
        final Set<Integer> input = new HashSet<>(Arrays.asList(5));
        assertTrue(IterableUtils.duplicateSet(input).isEmpty());
    }

    @Test
    void testDuplicateSetMultipleDuplicatesInDeque() {
        final Deque<Integer> input = new ArrayDeque<>(Arrays.asList(1, 1, 2, 2, 3, 3, 4, 4));
        final Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2, 3, 4));
        assertEquals(expected, IterableUtils.duplicateSet(input));
    }

    @Test
    void testDuplicateSetMultipleDuplicatesInList() {
        final List<Integer> input = Arrays.asList(1, 1, 2, 2, 3, 3, 4, 4);
        final Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2, 3, 4));
        assertEquals(expected, IterableUtils.duplicateSet(input));
    }

    @Test
    void testDuplicateSetNoDuplicates() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        assertTrue(IterableUtils.duplicateSet(input).isEmpty());
    }

    @Test
    void testDuplicateSetSingleElement() {
        final List<Integer> input = Arrays.asList(1);
        assertTrue(IterableUtils.duplicateSet(input).isEmpty());
    }

    @Test
    void testDuplicateSetWithDuplicates() {
        final List<Integer> input = Arrays.asList(1, 2, 3, 2, 4, 5, 3);
        final Set<Integer> expected = new HashSet<>(Arrays.asList(2, 3));
        assertEquals(expected, IterableUtils.duplicateSet(input));
    }

    @Test
    void testDuplicatListAllSameInDeque() {
        final Deque<Integer> input = new ArrayDeque<>(Arrays.asList(5, 5, 5, 5));
        assertEquals(Arrays.asList(5), IterableUtils.duplicateList(input));
    }

    @Test
    void testDuplicatSetAllSameInDeque() {
        final Deque<Integer> input = new ArrayDeque<>(Arrays.asList(5, 5, 5, 5));
        assertEquals(new HashSet<>(Arrays.asList(5)), IterableUtils.duplicateSet(input));
    }

    @Test
    void testFind() {
        Predicate<Number> testPredicate = EqualPredicate.equalPredicate(4);
        Integer test = IterableUtils.find(iterableA, testPredicate);
        assertEquals(4, (int) test);
        testPredicate = EqualPredicate.equalPredicate(45);
        test = IterableUtils.find(iterableA, testPredicate);
        assertNull(test);
        assertNull(IterableUtils.find(null, testPredicate));

        assertThrows(NullPointerException.class, () -> IterableUtils.find(iterableA, null), "expecting NullPointerException");
    }

    @Test
    void testFirstFromIterableIndexOutOfBoundsException() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<>();
        // Collection, non-existent entry
        assertThrows(IndexOutOfBoundsException.class, () -> IterableUtils.first(bag));
    }

    @Test
    void testForEach() {
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

        assertThrows(NullPointerException.class, () -> IterableUtils.forEach(col, null), "expecting NullPointerException");

        IterableUtils.forEach(null, testClosure);

        // null should be OK
        col.add(null);
        IterableUtils.forEach(col, testClosure);
    }

    @Test
    void testForEachButLast() {
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

        assertThrows(NullPointerException.class, () -> IterableUtils.forEachButLast(col, null), "expecting NullPointerException");

        IterableUtils.forEachButLast(null, testClosure);

        // null should be OK
        col.add(null);
        col.add(null);
        last = IterableUtils.forEachButLast(col, testClosure);
        assertNull(last);
    }

    @Test
    void testForEachFailure() {
        final Closure<String> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<String> col = new ArrayList<>();
        col.add("x");
        assertThrows(FunctorException.class, () -> IterableUtils.forEach(col, testClosure));
    }

    @Test
    void testFrequency() {
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
        // for example no longs in the "int" Iterable<Number>, and vice versa.
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
    void testFrequencyOfNull() {
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
    void testGetFromIterableIndexOutOfBoundsException() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<>();
        bag.add("element", 1);
        // Collection, non-existent entry
        assertThrows(IndexOutOfBoundsException.class, () -> IterableUtils.get(bag, 1));
    }

    @Test
    void testIndexOf() {
        Predicate<Number> testPredicate = EqualPredicate.equalPredicate((Number) 4);
        int index = IterableUtils.indexOf(iterableA, testPredicate);
        assertEquals(6, index);
        testPredicate = EqualPredicate.equalPredicate((Number) 45);
        index = IterableUtils.indexOf(iterableA, testPredicate);
        assertEquals(-1, index);
        assertEquals(-1, IterableUtils.indexOf(null, testPredicate));

        assertThrows(NullPointerException.class, () -> IterableUtils.indexOf(iterableA, null), "expecting NullPointerException");
    }

    @Test
    void testMatchesAll() {
        assertThrows(NullPointerException.class, () -> assertFalse(IterableUtils.matchesAll(null, null)), "predicate must not be null");

        assertThrows(NullPointerException.class, () -> assertFalse(IterableUtils.matchesAll(iterableA, null)), "predicate must not be null");

        final Predicate<Integer> lessThanFive = object -> object < 5;
        assertTrue(IterableUtils.matchesAll(iterableA, lessThanFive));

        final Predicate<Integer> lessThanFour = object -> object < 4;
        assertFalse(IterableUtils.matchesAll(iterableA, lessThanFour));

        assertTrue(IterableUtils.matchesAll(null, lessThanFour));
        assertTrue(IterableUtils.matchesAll(emptyIterable, lessThanFour));
    }

    @Test
    void testMatchesAny() {
        final List<Integer> list = new ArrayList<>();

        assertThrows(NullPointerException.class, () -> assertFalse(IterableUtils.matchesAny(null, null)), "predicate must not be null");

        assertThrows(NullPointerException.class, () -> assertFalse(IterableUtils.matchesAny(list, null)), "predicate must not be null");

        assertFalse(IterableUtils.matchesAny(null, EQUALS_TWO));
        assertFalse(IterableUtils.matchesAny(list, EQUALS_TWO));
        list.add(1);
        list.add(3);
        list.add(4);
        assertFalse(IterableUtils.matchesAny(list, EQUALS_TWO));

        list.add(2);
        assertTrue(IterableUtils.matchesAny(list, EQUALS_TWO));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testPartition() {
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
        final Integer[] expected = { 1, 3, 4 };
        partition = partitions.get(1);
        assertArrayEquals(expected, partition.toArray());

        partitions = IterableUtils.partition((List<Integer>) null, EQUALS_TWO);
        assertEquals(2, partitions.size());
        assertTrue(partitions.get(0).isEmpty());
        assertTrue(partitions.get(1).isEmpty());

        partitions = IterableUtils.partition(input);
        assertEquals(1, partitions.size());
        assertEquals(input, partitions.get(0));

        assertThrows(NullPointerException.class, () -> IterableUtils.partition(input, (Predicate<Integer>) null), "expecting NullPointerException");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testPartitionMultiplePredicates() {
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
        final Integer[] expected = { 1, 3 };
        partition = partitions.get(2);
        assertArrayEquals(expected, partition.toArray());

        assertThrows(NullPointerException.class, () -> IterableUtils.partition(input, EQUALS_TWO, null));
    }

    @Test
    void testSize() {
        assertEquals(0, IterableUtils.size(null));
    }

    @Test
    void testToString() {
        String result = IterableUtils.toString(iterableA);
        assertEquals("[1, 2, 2, 3, 3, 3, 4, 4, 4, 4]", result);

        result = IterableUtils.toString(new ArrayList<>());
        assertEquals("[]", result);

        result = IterableUtils.toString(null);
        assertEquals("[]", result);

        result = IterableUtils.toString(iterableA, input -> Integer.toString(input * 2));
        assertEquals("[2, 4, 4, 6, 6, 6, 8, 8, 8, 8]", result);

        result = IterableUtils.toString(new ArrayList<>(), input -> {
            fail("not supposed to reach here");
            return StringUtils.EMPTY;
        });
        assertEquals("[]", result);

        result = IterableUtils.toString(null, input -> {
            fail("not supposed to reach here");
            return StringUtils.EMPTY;
        });
        assertEquals("[]", result);
    }

    @Test
    void testToStringDelimiter() {

        final Transformer<Integer, String> transformer = input -> Integer.toString(input * 2);

        String result = IterableUtils.toString(iterableA, transformer, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
        assertEquals("2446668888", result);

        result = IterableUtils.toString(iterableA, transformer, ",", StringUtils.EMPTY, StringUtils.EMPTY);
        assertEquals("2,4,4,6,6,6,8,8,8,8", result);

        result = IterableUtils.toString(iterableA, transformer, StringUtils.EMPTY, "[", "]");
        assertEquals("[2446668888]", result);

        result = IterableUtils.toString(iterableA, transformer, ",", "[", "]");
        assertEquals("[2,4,4,6,6,6,8,8,8,8]", result);

        result = IterableUtils.toString(iterableA, transformer, ",", "[[", "]]");
        assertEquals("[[2,4,4,6,6,6,8,8,8,8]]", result);

        result = IterableUtils.toString(iterableA, transformer, ",,", "[", "]");
        assertEquals("[2,,4,,4,,6,,6,,6,,8,,8,,8,,8]", result);

        result = IterableUtils.toString(iterableA, transformer, ",,", "((", "))");
        assertEquals("((2,,4,,4,,6,,6,,6,,8,,8,,8,,8))", result);

        result = IterableUtils.toString(new ArrayList<>(), transformer, StringUtils.EMPTY, "(", ")");
        assertEquals("()", result);

        result = IterableUtils.toString(new ArrayList<>(), transformer, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    void testToStringWithNullArguments() {
        final String result = IterableUtils.toString(null, input -> {
            fail("not supposed to reach here");
            return StringUtils.EMPTY;
        }, StringUtils.EMPTY, "(", ")");
        assertEquals("()", result);
        assertThrows(NullPointerException.class, () -> IterableUtils.toString(new ArrayList<>(), null, StringUtils.EMPTY, "(", ")"));
        assertThrows(NullPointerException.class, () -> IterableUtils.toString(new ArrayList<>(), input -> {
            fail("not supposed to reach here");
            return StringUtils.EMPTY;
        }, null, "(", ")"));
        assertThrows(NullPointerException.class, () -> IterableUtils.toString(new ArrayList<>(), input -> {
            fail("not supposed to reach here");
            return StringUtils.EMPTY;
        }, StringUtils.EMPTY, null, ")"));
        assertThrows(NullPointerException.class, () -> IterableUtils.toString(new ArrayList<>(), input -> {
            fail("not supposed to reach here");
            return StringUtils.EMPTY;
        }, StringUtils.EMPTY, "(", null));
    }

}
