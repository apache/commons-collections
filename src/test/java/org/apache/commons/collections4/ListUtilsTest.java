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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.list.PredicatedList;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for ListUtils.
 */
class ListUtilsTest {

    private static final String a = "a";
    private static final String b = "b";
    private static final String c = "c";
    private static final String d = "d";
    private static final String e = "e";
    private static final String x = "x";

    private static final Predicate<Number> EQUALS_TWO = input -> input.intValue() == 2;
    private String[] fullArray;

    private List<String> fullList;

    @BeforeEach
    public void setUp() {
        fullArray = new String[]{a, b, c, d, e};
        fullList = new ArrayList<>(Arrays.asList(fullArray));
    }

    @Test
    void testDefaultIfNull() {
        assertTrue(ListUtils.defaultIfNull(null, Collections.emptyList()).isEmpty());

        final List<Long> list = new ArrayList<>();
        assertSame(list, ListUtils.defaultIfNull(list, Collections.<Long>emptyList()));
    }

    @Test
    void testEmptyIfNull() {
        assertTrue(ListUtils.emptyIfNull(null).isEmpty());

        final List<Long> list = new ArrayList<>();
        assertSame(list, ListUtils.emptyIfNull(list));
    }

    @Test
    void testEquals() {
        final Collection<String> data = Arrays.asList("a", "b", "c");

        final List<String> list1 = new ArrayList<>(data);
        final List<String> list2 = new ArrayList<>(data);

        assertEquals(list1, list2);
        assertTrue(ListUtils.isEqualList(list1, list2));
        list1.clear();
        assertFalse(ListUtils.isEqualList(list1, list2));
        assertFalse(ListUtils.isEqualList(list1, null));
        assertFalse(ListUtils.isEqualList(null, list2));
        assertTrue(ListUtils.isEqualList(null, null));

        list2.clear();
        list1.add("a");
        list2.add("b");
        assertFalse(ListUtils.isEqualList(list1, list2));

        list1.add("b");
        list2.add("a");
        assertFalse(ListUtils.isEqualList(list1, list2));
    }

    @Test
    void testGetFirst() {
        assertEquals(a, ListUtils.getFirst(fullList));
        assertThrows(NullPointerException.class, () -> ListUtils.getFirst(null));
        assertThrows(IndexOutOfBoundsException.class, () -> ListUtils.getFirst(new ArrayList<>()));
    }

    @Test
    void testGetLast() {
        assertEquals(e, ListUtils.getLast(fullList));
        assertThrows(NullPointerException.class, () -> ListUtils.getFirst(null));
        assertThrows(IndexOutOfBoundsException.class, () -> ListUtils.getFirst(new ArrayList<>()));
    }

    @Test
    void testHashCode() {
        final Collection<String> data = Arrays.asList("a", "b", "c");

        final List<String> list1 = new ArrayList<>(data);
        final List<String> list2 = new ArrayList<>(data);

        assertEquals(list1.hashCode(), list2.hashCode());
        assertEquals(list1.hashCode(), ListUtils.hashCodeForList(list1));
        assertEquals(list2.hashCode(), ListUtils.hashCodeForList(list2));
        assertEquals(ListUtils.hashCodeForList(list1), ListUtils.hashCodeForList(list2));
        list1.clear();
        assertNotEquals(ListUtils.hashCodeForList(list1), ListUtils.hashCodeForList(list2));
        assertEquals(0, ListUtils.hashCodeForList(null));

        list1.add(null);
        assertEquals(31, ListUtils.hashCodeForList(list1));
    }

    /**
     * Tests the {@code indexOf} method in {@code ListUtils} class.
     */
    @Test
    void testIndexOf() {
        Predicate<String> testPredicate = EqualPredicate.equalPredicate("d");
        int index = ListUtils.indexOf(fullList, testPredicate);
        assertEquals(d, fullList.get(index));

        testPredicate = EqualPredicate.equalPredicate("de");
        index = ListUtils.indexOf(fullList, testPredicate);
        assertEquals(index, -1);

        assertEquals(ListUtils.indexOf(null, testPredicate), -1);
        assertEquals(ListUtils.indexOf(fullList, null), -1);
    }

    /**
     * Tests intersecting a non-empty list with an empty list.
     */
    @Test
    void testIntersectEmptyWithEmptyList() {
        final List<?> empty = Collections.EMPTY_LIST;
        assertTrue(ListUtils.intersection(empty, empty).isEmpty(), "result not empty");
    }

    /**
     * Tests intersecting two lists in different orders.
     */
    @Test
    void testIntersectionOrderInsensitivity() {
        final List<String> one = new ArrayList<>();
        final List<String> two = new ArrayList<>();
        one.add("a");
        one.add("b");
        two.add("a");
        two.add("a");
        two.add("b");
        two.add("b");
        assertEquals(ListUtils.intersection(one, two), ListUtils.intersection(two, one));
    }

    /**
     * Tests intersecting a non-empty list with a subset of itself.
     */
    @Test
    void testIntersectListWithNoOverlapAndDifferentTypes() {
        @SuppressWarnings("boxing")
        final List<Integer> other = Arrays.asList(1, 23);
        assertTrue(ListUtils.intersection(fullList, other).isEmpty());
    }

    /**
     * Tests intersecting a non-empty list with itself.
     */
    @Test
    void testIntersectListWithSelf() {
        assertEquals(fullList, ListUtils.intersection(fullList, fullList));
    }

    /**
     * Tests intersecting a non-empty list with a subset of itself.
     */
    @Test
    void testIntersectNonEmptySubset() {
        // create a copy
        final List<String> other = new ArrayList<>(fullList);

        // remove a few items
        assertNotNull(other.remove(0));
        assertNotNull(other.remove(1));

        // make sure the intersection is equal to the copy
        assertEquals(other, ListUtils.intersection(fullList, other));
    }

    /**
     * Tests intersecting a non-empty list with an empty list.
     */
    @Test
    void testIntersectNonEmptyWithEmptyList() {
        final List<String> empty = Collections.<String>emptyList();
        assertTrue(ListUtils.intersection(empty, fullList).isEmpty(), "result not empty");
    }

    @Test
    void testLazyFactoryList() {
        final List<Integer> list = ListUtils.lazyList(new ArrayList<>(), new Factory<Integer>() {

            private int index;

            @Override
            public Integer create() {
                index++;
                return Integer.valueOf(index);
            }
        });

        assertNotNull(list.get(5));
        assertEquals(6, list.size());

        assertNotNull(list.get(5));
        assertEquals(6, list.size());
    }

    @Test
    void testLazyTransformerList() {
        final List<Integer> offsets = Arrays.asList(3, 5, 1, 5, 3, 6);
        final List<Integer> list = ListUtils.lazyList(new ArrayList<>(), new Transformer<Integer, Integer>() {

            private int index;

            @Override
            public Integer transform(final Integer input) {
                return offsets.get(input) + index++;
            }

        });

        assertNotNull(list.get(5));
        assertEquals(6, list.size());

        assertNotNull(list.get(5));
        assertEquals(6, list.size());
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    void testLongestCommonSubsequence() {
        assertThrows(NullPointerException.class, () -> ListUtils.longestCommonSubsequence((List<?>) null, null), "failed to check for null argument");
        assertThrows(NullPointerException.class, () -> ListUtils.longestCommonSubsequence(Arrays.asList('A'), null), "failed to check for null argument");
        assertThrows(NullPointerException.class, () -> ListUtils.longestCommonSubsequence(null, Arrays.asList('A')), "failed to check for null argument");
        @SuppressWarnings("unchecked")
        List<Character> lcs = ListUtils.longestCommonSubsequence(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        assertEquals(0, lcs.size());

        final List<Character> list1 = Arrays.asList('B', 'A', 'N', 'A', 'N', 'A');
        final List<Character> list2 = Arrays.asList('A', 'N', 'A', 'N', 'A', 'S');
        lcs = ListUtils.longestCommonSubsequence(list1, list2);

        List<Character> expected = Arrays.asList('A', 'N', 'A', 'N', 'A');
        assertEquals(expected, lcs);

        final List<Character> list3 = Arrays.asList('A', 'T', 'A', 'N', 'A');
        lcs = ListUtils.longestCommonSubsequence(list1, list3);

        expected = Arrays.asList('A', 'A', 'N', 'A');
        assertEquals(expected, lcs);

        final List<Character> listZorro = Arrays.asList('Z', 'O', 'R', 'R', 'O');
        lcs = ListUtils.longestCommonSubsequence(list1, listZorro);

        assertTrue(lcs.isEmpty());
    }

    @Test
    void testLongestCommonSubsequenceWithString() {
        assertThrows(NullPointerException.class, () -> ListUtils.longestCommonSubsequence((String) null, null), "failed to check for null argument");
        assertThrows(NullPointerException.class, () -> ListUtils.longestCommonSubsequence("A", null), "failed to check for null argument");
        assertThrows(NullPointerException.class, () -> ListUtils.longestCommonSubsequence(null, "A"), "failed to check for null argument");
        String lcs = ListUtils.longestCommonSubsequence(StringUtils.EMPTY, StringUtils.EMPTY);
        assertEquals(0, lcs.length());

        final String banana = "BANANA";
        final String ananas = "ANANAS";
        lcs = ListUtils.longestCommonSubsequence(banana, ananas);

        assertEquals("ANANA", lcs);

        final String atana = "ATANA";
        lcs = ListUtils.longestCommonSubsequence(banana, atana);

        assertEquals("AANA", lcs);

        final String zorro = "ZORRO";
        lcs = ListUtils.longestCommonSubsequence(banana, zorro);

        assertEquals(0, lcs.length());
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    void testPartition() {
        final List<Integer> strings = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            strings.add(i);
        }

        final List<List<Integer>> partition = ListUtils.partition(strings, 3);

        assertNotNull(partition);
        assertEquals(3, partition.size());
        assertEquals(1, partition.get(2).size());

        assertThrows(IndexOutOfBoundsException.class, () -> partition.get(-1), "Index -1 must not be negative");
        assertThrows(IndexOutOfBoundsException.class, () -> partition.get(3), "Index " + 3 + " must be less than size " + partition.size());
        assertThrows(NullPointerException.class, () -> ListUtils.partition(null, 3), "failed to check for null argument");
        assertThrows(IllegalArgumentException.class, () -> ListUtils.partition(strings, 0), "failed to check for size argument");
        assertThrows(IllegalArgumentException.class, () -> ListUtils.partition(strings, -10), "failed to check for size argument");
        final List<List<Integer>> partitionMax = ListUtils.partition(strings, Integer.MAX_VALUE);
        assertEquals(1, partitionMax.size());
        assertEquals(strings.size(), partitionMax.get(0).size());
        assertEquals(strings, partitionMax.get(0));
    }

    @Test
    void testPredicatedList() {
        final Predicate<Object> predicate = String.class::isInstance;
        final List<Object> list = ListUtils.predicatedList(new ArrayList<>(), predicate);
        assertInstanceOf(PredicatedList.class, list, "returned object should be a PredicatedList");
        assertThrows(NullPointerException.class, () -> ListUtils.predicatedList(new ArrayList<>(), null),
                "Expecting IllegalArgumentException for null predicate.");
        assertThrows(NullPointerException.class, () -> ListUtils.predicatedList(null, predicate), "Expecting IllegalArgumentException for null list.");
    }

    @Test
    void testRemoveAll() {
        final List<String> sub = new ArrayList<>();
        sub.add(a);
        sub.add(b);
        sub.add(x);

        final List<String> remainder = ListUtils.removeAll(fullList, sub);
        assertEquals(3, remainder.size());
        fullList.removeAll(sub);
        assertEquals(remainder, fullList);

        assertThrows(NullPointerException.class, () -> ListUtils.removeAll(null, null),
                "expecting NullPointerException");

        assertThrows(NullPointerException.class, () -> ListUtils.removeAll(null, new ArrayList<>()),
                "expecting NullPointerException");

        assertThrows(NullPointerException.class, () -> ListUtils.removeAll(new ArrayList<>(), null),
                "expecting NullPointerException");
    }

    @Test
    void testRetainAll() {
        final List<String> sub = new ArrayList<>();
        sub.add(a);
        sub.add(b);
        sub.add(x);

        final List<String> retained = ListUtils.retainAll(fullList, sub);
        assertEquals(2, retained.size());
        sub.remove(x);
        assertEquals(retained, sub);
        fullList.retainAll(sub);
        assertEquals(retained, fullList);

        assertThrows(NullPointerException.class, () -> ListUtils.retainAll(null, null),
                "expecting NullPointerException");
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    void testSelect() {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        // Ensure that the collection is the input type or a super type
        final List<Integer> output1 = ListUtils.select(list, EQUALS_TWO);
        final List<Number> output2 = ListUtils.<Number>select(list, EQUALS_TWO);
        final HashSet<Number> output3 = CollectionUtils.select(list, EQUALS_TWO, new HashSet<>());
        assertTrue(CollectionUtils.isEqualCollection(output1, output3));
        assertEquals(4, list.size());
        assertEquals(1, output1.size());
        assertEquals(2, output2.iterator().next());
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    void testSelectRejected() {
        final List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        list.add(4L);
        final List<Long> output1 = ListUtils.selectRejected(list, EQUALS_TWO);
        final List<? extends Number> output2 = ListUtils.selectRejected(list, EQUALS_TWO);
        final HashSet<Number> output3 = CollectionUtils.selectRejected(list, EQUALS_TWO, new HashSet<>());
        assertTrue(CollectionUtils.isEqualCollection(output1, output2));
        assertTrue(CollectionUtils.isEqualCollection(output1, output3));
        assertEquals(4, list.size());
        assertEquals(3, output1.size());
        assertTrue(output1.contains(1L));
        assertTrue(output1.contains(3L));
        assertTrue(output1.contains(4L));
    }

    @Test
    void testSubtract() {
        final List<String> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        list.add(a);
        list.add(x);

        final List<String> sub = new ArrayList<>();
        sub.add(a);

        final List<String> result = ListUtils.subtract(list, sub);
        assertEquals(3, result.size());

        final List<String> expected = new ArrayList<>();
        expected.add(b);
        expected.add(a);
        expected.add(x);

        assertEquals(expected, result);

        assertThrows(NullPointerException.class, () -> ListUtils.subtract(list, null),
                "expecting NullPointerException");
    }

    @Test
    void testSubtractNullElement() {
        final List<String> list = new ArrayList<>();
        list.add(a);
        list.add(null);
        list.add(null);
        list.add(x);

        final List<String> sub = new ArrayList<>();
        sub.add(null);

        final List<String> result = ListUtils.subtract(list, sub);
        assertEquals(3, result.size());

        final List<String> expected = new ArrayList<>();
        expected.add(a);
        expected.add(null);
        expected.add(x);

        assertEquals(expected, result);
    }

    @Test
    void testSum() {
        final List<String> list1 = new ArrayList<>();
        list1.add(a);
        final List<String> list2 = new ArrayList<>();
        list2.add(b);
        final List<String> expected1 = new ArrayList<>();
        expected1.add(a);
        expected1.add(b);
        final List<String> result1 = ListUtils.sum(list1, list2);
        assertEquals(2, result1.size());
        assertEquals(expected1, result1);
    }

    @Test
    void testUnion() {
        final List<String> list1 = new ArrayList<>();
        list1.add(a);
        final List<String> list2 = new ArrayList<>();
        list2.add(b);
        final List<String> result1 = ListUtils.union(list1, list2);
        final List<String> expected1 = new ArrayList<>();
        expected1.add(a);
        expected1.add(b);
        assertEquals(2, result1.size());
        assertEquals(expected1, result1);

        final List<String> list3 = new ArrayList<>();
        list3.add(a);
        final List<String> result2 = ListUtils.union(list1, list3);
        final List<String> expected2 = new ArrayList<>();
        expected2.add(a);
        expected2.add(a);
        assertEquals(2, result1.size());
        assertEquals(expected2, result2);

        list1.add(null);
        final List<String> result3 = ListUtils.union(list1, list2);
        final List<String> expected3 = new ArrayList<>();
        expected3.add(a);
        expected3.add(null);
        expected3.add(b);
        assertEquals(3, result3.size());
        assertEquals(expected3, result3);

        list2.add(null);
        final List<String> result4 = ListUtils.union(list1, list2);
        final List<String> expected4 = new ArrayList<>();
        expected4.add(a);
        expected4.add(null);
        expected4.add(b);
        expected4.add(null);
        assertEquals(4, result4.size());
        assertEquals(expected4, result4);
    }
}
