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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.list.PredicatedList;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ListUtils.
 *
 */
public class ListUtilsTest {

    private static final String a = "a";
    private static final String b = "b";
    private static final String c = "c";
    private static final String d = "d";
    private static final String e = "e";
    private static final String x = "x";

    private String[] fullArray;
    private List<String> fullList;

    @Before
    public void setUp() {
        fullArray = new String[]{a, b, c, d, e};
        fullList = new ArrayList<>(Arrays.asList(fullArray));
    }

    /**
     * Tests intersecting a non-empty list with an empty list.
     */
    @Test
    public void testIntersectNonEmptyWithEmptyList() {
        final List<String> empty = Collections.<String>emptyList();
        assertTrue("result not empty", ListUtils.intersection(empty, fullList).isEmpty());
    }

    /**
     * Tests intersecting a non-empty list with an empty list.
     */
    @Test
    public void testIntersectEmptyWithEmptyList() {
        final List<?> empty = Collections.EMPTY_LIST;
        assertTrue("result not empty", ListUtils.intersection(empty, empty).isEmpty());
    }

    /**
     * Tests intersecting a non-empty list with an subset of itself.
     */
    @Test
    public void testIntersectNonEmptySubset() {
        // create a copy
        final List<String> other = new ArrayList<>(fullList);

        // remove a few items
        assertNotNull(other.remove(0));
        assertNotNull(other.remove(1));

        // make sure the intersection is equal to the copy
        assertEquals(other, ListUtils.intersection(fullList, other));
    }

    /**
     * Tests intersecting a non-empty list with an subset of itself.
     */
    @Test
    public void testIntersectListWithNoOverlapAndDifferentTypes() {
        @SuppressWarnings("boxing")
        final List<Integer> other = Arrays.asList(1, 23);
        assertTrue(ListUtils.intersection(fullList, other).isEmpty());
    }

    /**
     * Tests intersecting a non-empty list with itself.
     */
    @Test
    public void testIntersectListWithSelf() {
        assertEquals(fullList, ListUtils.intersection(fullList, fullList));
    }

    /**
     * Tests intersecting two lists in different orders.
     */
    @Test
    public void testIntersectionOrderInsensitivity() {
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

    @Test
    public void testPredicatedList() {
        final Predicate<Object> predicate = o -> o instanceof String;
        final List<Object> list = ListUtils.predicatedList(new ArrayList<>(), predicate);
        assertTrue("returned object should be a PredicatedList", list instanceof PredicatedList);
        try {
            ListUtils.predicatedList(new ArrayList<>(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (final NullPointerException ex) {
            // expected
        }
        try {
            ListUtils.predicatedList(null, predicate);
            fail("Expecting IllegalArgumentException for null list.");
        } catch (final NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testLazyFactoryList() {
        final List<Integer> list = ListUtils.lazyList(new ArrayList<Integer>(), new Factory<Integer>() {

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
    public void testLazyTransformerList() {
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
    public void testEmptyIfNull() {
        assertTrue(ListUtils.emptyIfNull(null).isEmpty());

        final List<Long> list = new ArrayList<>();
        assertSame(list, ListUtils.emptyIfNull(list));
    }

    @Test
    public void testDefaultIfNull() {
        assertTrue(ListUtils.defaultIfNull(null, Collections.emptyList()).isEmpty());

        final List<Long> list = new ArrayList<>();
        assertSame(list, ListUtils.defaultIfNull(list, Collections.<Long>emptyList()));
    }

    @Test
    public void testEquals() {
        final Collection<String> data = Arrays.asList("a", "b", "c");

        final List<String> a = new ArrayList<>( data );
        final List<String> b = new ArrayList<>( data );

        assertEquals(a, b);
        assertTrue(ListUtils.isEqualList(a, b));
        a.clear();
        assertFalse(ListUtils.isEqualList(a, b));
        assertFalse(ListUtils.isEqualList(a, null));
        assertFalse(ListUtils.isEqualList(null, b));
        assertTrue(ListUtils.isEqualList(null, null));
    }

    @Test
    public void testHashCode() {
        final Collection<String> data = Arrays.asList("a", "b", "c");

        final List<String> a = new ArrayList<>(data);
        final List<String> b = new ArrayList<>(data);

        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), ListUtils.hashCodeForList(a));
        assertEquals(b.hashCode(), ListUtils.hashCodeForList(b));
        assertEquals(ListUtils.hashCodeForList(a), ListUtils.hashCodeForList(b));
        a.clear();
        assertNotEquals(ListUtils.hashCodeForList(a), ListUtils.hashCodeForList(b));
        assertEquals(0, ListUtils.hashCodeForList(null));
    }

    @Test
    public void testRetainAll() {
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

        try {
            ListUtils.retainAll(null, null);
            fail("expecting NullPointerException");
        } catch(final NullPointerException npe){} // this is what we want
    }

    @Test
    public void testRemoveAll() {
        final List<String> sub = new ArrayList<>();
        sub.add(a);
        sub.add(b);
        sub.add(x);

        final List<String> remainder = ListUtils.removeAll(fullList, sub);
        assertEquals(3, remainder.size());
        fullList.removeAll(sub);
        assertEquals(remainder, fullList);

        try {
            ListUtils.removeAll(null, null);
            fail("expecting NullPointerException");
        } catch(final NullPointerException npe) {} // this is what we want
    }

    @Test
    public void testSubtract() {
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

        try {
            ListUtils.subtract(list, null);
            fail("expecting NullPointerException");
        } catch(final NullPointerException npe) {} // this is what we want
    }

    @Test
    public void testSubtractNullElement() {
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

    /**
     * Tests the {@code indexOf} method in {@code ListUtils} class..
     */
    @Test
    public void testIndexOf() {
        Predicate<String> testPredicate = EqualPredicate.equalPredicate("d");
        int index = ListUtils.indexOf(fullList, testPredicate);
        assertEquals(d, fullList.get(index));

        testPredicate = EqualPredicate.equalPredicate("de");
        index = ListUtils.indexOf(fullList, testPredicate);
        assertEquals(index, -1);

        assertEquals(ListUtils.indexOf(null, testPredicate), -1);
        assertEquals(ListUtils.indexOf(fullList, null), -1);
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testLongestCommonSubsequence() {

        try {
            ListUtils.longestCommonSubsequence((List<?>) null, null);
            fail("failed to check for null argument");
        } catch (final NullPointerException e) {}

        try {
            ListUtils.longestCommonSubsequence(Arrays.asList('A'), null);
            fail("failed to check for null argument");
        } catch (final NullPointerException e) {}

        try {
            ListUtils.longestCommonSubsequence(null, Arrays.asList('A'));
            fail("failed to check for null argument");
        } catch (final NullPointerException e) {}

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
    public void testLongestCommonSubsequenceWithString() {

        try {
            ListUtils.longestCommonSubsequence((String) null, null);
            fail("failed to check for null argument");
        } catch (final NullPointerException e) {}

        try {
            ListUtils.longestCommonSubsequence("A", null);
            fail("failed to check for null argument");
        } catch (final NullPointerException e) {}

        try {
            ListUtils.longestCommonSubsequence(null, "A");
            fail("failed to check for null argument");
        } catch (final NullPointerException e) {}

        String lcs = ListUtils.longestCommonSubsequence("", "");
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
    public void testPartition() {
        final List<Integer> strings = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            strings.add(i);
        }

        final List<List<Integer>> partition = ListUtils.partition(strings, 3);

        assertNotNull(partition);
        assertEquals(3, partition.size());
        assertEquals(1, partition.get(2).size());

        try {
            ListUtils.partition(null, 3);
            fail("failed to check for null argument");
        } catch (final NullPointerException e) {}

        try {
            ListUtils.partition(strings, 0);
            fail("failed to check for size argument");
        } catch (final IllegalArgumentException e) {}

        try {
            ListUtils.partition(strings, -10);
            fail("failed to check for size argument");
        } catch (final IllegalArgumentException e) {}

        final List<List<Integer>> partitionMax = ListUtils.partition(strings, Integer.MAX_VALUE);
        assertEquals(1, partitionMax.size());
        assertEquals(strings.size(), partitionMax.get(0).size());
        assertEquals(strings, partitionMax.get(0));
    }

    private static Predicate<Number> EQUALS_TWO = input -> input.intValue() == 2;

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testSelect() {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        // Ensure that the collection is the input type or a super type
        final List<Integer> output1 = ListUtils.select(list, EQUALS_TWO);
        final List<Number> output2 = ListUtils.<Number>select(list, EQUALS_TWO);
        final HashSet<Number> output3 = CollectionUtils.select(list, EQUALS_TWO, new HashSet<Number>());
        assertTrue(CollectionUtils.isEqualCollection(output1, output3));
        assertEquals(4, list.size());
        assertEquals(1, output1.size());
        assertEquals(2, output2.iterator().next());
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testSelectRejected() {
        final List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        list.add(4L);
        final List<Long> output1 = ListUtils.selectRejected(list, EQUALS_TWO);
        final List<? extends Number> output2 = ListUtils.selectRejected(list, EQUALS_TWO);
        final HashSet<Number> output3 = CollectionUtils.selectRejected(list, EQUALS_TWO, new HashSet<Number>());
        assertTrue(CollectionUtils.isEqualCollection(output1, output2));
        assertTrue(CollectionUtils.isEqualCollection(output1, output3));
        assertEquals(4, list.size());
        assertEquals(3, output1.size());
        assertTrue(output1.contains(1L));
        assertTrue(output1.contains(3L));
        assertTrue(output1.contains(4L));
    }
}
