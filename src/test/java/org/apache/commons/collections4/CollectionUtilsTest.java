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
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.collection.PredicatedCollection;
import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.functors.DefaultEquator;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for CollectionUtils.
 *
 */
@SuppressWarnings("boxing")
public class CollectionUtilsTest extends MockTestCase {

    /**
     * Collection of {@link Integer}s
     */
    private List<Integer> collectionA = null;

    /**
     * Collection of {@link Long}s
     */
    private List<Long> collectionB = null;

    /**
     * Collection of {@link Integer}s that are equivalent to the Longs in
     * collectionB.
     */
    private Collection<Integer> collectionC = null;

    /**
     * Sorted Collection of {@link Integer}s
     */
    private Collection<Integer> collectionD = null;

    /**
     * Sorted Collection of {@link Integer}s
     */
    private Collection<Integer> collectionE = null;

    /**
     * Collection of {@link Integer}s, bound as {@link Number}s
     */
    private Collection<Number> collectionA2 = null;

    /**
     * Collection of {@link Long}s, bound as {@link Number}s
     */
    private Collection<Number> collectionB2 = null;

    /**
     * Collection of {@link Integer}s (cast as {@link Number}s) that are
     * equivalent to the Longs in collectionB.
     */
    private Collection<Number> collectionC2 = null;

    private Iterable<Integer> iterableA = null;

    private Iterable<Long> iterableB = null;

    private Iterable<Integer> iterableC = null;

    private Iterable<Number> iterableA2 = null;

    private Iterable<Number> iterableB2 = null;

    private final Collection<Integer> emptyCollection = new ArrayList<>(1);

    @Before
    public void setUp() {
        collectionA = new ArrayList<>();
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
        collectionB = new LinkedList<>();
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

        collectionC = new ArrayList<>();
        for (final Long l : collectionB) {
            collectionC.add(l.intValue());
        }

        iterableA = collectionA;
        iterableB = collectionB;
        iterableC = collectionC;
        collectionA2 = new ArrayList<>(collectionA);
        collectionB2 = new LinkedList<>(collectionB);
        collectionC2 = new LinkedList<>(collectionC);
        iterableA2 = collectionA2;
        iterableB2 = collectionB2;

        collectionD = new ArrayList<>();
        collectionD.add(1);
        collectionD.add(3);
        collectionD.add(3);
        collectionD.add(3);
        collectionD.add(5);
        collectionD.add(7);
        collectionD.add(7);
        collectionD.add(10);

        collectionE = new ArrayList<>();
        collectionE.add(2);
        collectionE.add(4);
        collectionE.add(4);
        collectionE.add(5);
        collectionE.add(6);
        collectionE.add(6);
        collectionE.add(9);
    }

    @Test
    public void getCardinalityMap() {
        final Map<Number, Integer> freqA = CollectionUtils.<Number>getCardinalityMap(iterableA);
        assertEquals(1, (int) freqA.get(1));
        assertEquals(2, (int) freqA.get(2));
        assertEquals(3, (int) freqA.get(3));
        assertEquals(4, (int) freqA.get(4));
        assertNull(freqA.get(5));

        final Map<Long, Integer> freqB = CollectionUtils.getCardinalityMap(iterableB);
        assertNull(freqB.get(1L));
        assertEquals(4, (int) freqB.get(2L));
        assertEquals(3, (int) freqB.get(3L));
        assertEquals(2, (int) freqB.get(4L));
        assertEquals(1, (int) freqB.get(5L));
    }

    @Test(expected = NullPointerException.class)
    public void testGetCardinalityMapNull() {
        CollectionUtils.getCardinalityMap(null);
    }

    @Test
    @Deprecated
    public void cardinality() {
        assertEquals(1, CollectionUtils.cardinality(1, iterableA));
        assertEquals(2, CollectionUtils.cardinality(2, iterableA));
        assertEquals(3, CollectionUtils.cardinality(3, iterableA));
        assertEquals(4, CollectionUtils.cardinality(4, iterableA));
        assertEquals(0, CollectionUtils.cardinality(5, iterableA));

        assertEquals(0, CollectionUtils.cardinality(1L, iterableB));
        assertEquals(4, CollectionUtils.cardinality(2L, iterableB));
        assertEquals(3, CollectionUtils.cardinality(3L, iterableB));
        assertEquals(2, CollectionUtils.cardinality(4L, iterableB));
        assertEquals(1, CollectionUtils.cardinality(5L, iterableB));

        // Ensure that generic bounds accept valid parameters, but return
        // expected results
        // e.g. no longs in the "int" Iterable<Number>, and vice versa.
        assertEquals(0, CollectionUtils.cardinality(2L, iterableA2));
        assertEquals(0, CollectionUtils.cardinality(2, iterableB2));

        final Set<String> set = new HashSet<>();
        set.add("A");
        set.add("C");
        set.add("E");
        set.add("E");
        assertEquals(1, CollectionUtils.cardinality("A", set));
        assertEquals(0, CollectionUtils.cardinality("B", set));
        assertEquals(1, CollectionUtils.cardinality("C", set));
        assertEquals(0, CollectionUtils.cardinality("D", set));
        assertEquals(1, CollectionUtils.cardinality("E", set));

        final Bag<String> bag = new HashBag<>();
        bag.add("A", 3);
        bag.add("C");
        bag.add("E");
        bag.add("E");
        assertEquals(3, CollectionUtils.cardinality("A", bag));
        assertEquals(0, CollectionUtils.cardinality("B", bag));
        assertEquals(1, CollectionUtils.cardinality("C", bag));
        assertEquals(0, CollectionUtils.cardinality("D", bag));
        assertEquals(2, CollectionUtils.cardinality("E", bag));
    }

    @Test
    @Deprecated
    public void cardinalityOfNull() {
        final List<String> list = new ArrayList<>();
        assertEquals(0, CollectionUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertNull(freq.get(null));
        }
        list.add("A");
        assertEquals(0, CollectionUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertNull(freq.get(null));
        }
        list.add(null);
        assertEquals(1, CollectionUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(1), freq.get(null));
        }
        list.add("B");
        assertEquals(1, CollectionUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(1), freq.get(null));
        }
        list.add(null);
        assertEquals(2, CollectionUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(2), freq.get(null));
        }
        list.add("B");
        assertEquals(2, CollectionUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(2), freq.get(null));
        }
        list.add(null);
        assertEquals(3, CollectionUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(3), freq.get(null));
        }
    }

    @Test
    public void containsAll() {
        final Collection<String> empty = new ArrayList<>(0);
        final Collection<String> one = new ArrayList<>(1);
        one.add("1");
        final Collection<String> two = new ArrayList<>(1);
        two.add("2");
        final Collection<String> three = new ArrayList<>(1);
        three.add("3");
        final Collection<String> odds = new ArrayList<>(2);
        odds.add("1");
        odds.add("3");
        final Collection<String> multiples = new ArrayList<>(3);
        multiples.add("1");
        multiples.add("3");
        multiples.add("1");

        assertFalse("containsAll({1},{1,3}) should return false.", CollectionUtils.containsAll(one, odds));
        assertTrue(CollectionUtils.containsAll(odds, one));
        assertFalse("containsAll({3},{1,3}) should return false.", CollectionUtils.containsAll(three, odds));
        assertTrue(CollectionUtils.containsAll(odds, three));
        assertTrue(CollectionUtils.containsAll(two, two));
        assertTrue(CollectionUtils.containsAll(odds, odds));

        assertFalse("containsAll({2},{1,3}) should return false.", CollectionUtils.containsAll(two, odds));
        assertFalse("containsAll({1,3},{2}) should return false.", CollectionUtils.containsAll(odds, two));
        assertFalse("containsAll({1},{3}) should return false.", CollectionUtils.containsAll(one, three));
        assertFalse("containsAll({3},{1}) should return false.", CollectionUtils.containsAll(three, one));
        assertTrue(CollectionUtils.containsAll(odds, empty));
        assertFalse("containsAll({},{1,3}) should return false.", CollectionUtils.containsAll(empty, odds));
        assertTrue(CollectionUtils.containsAll(empty, empty));

        assertTrue(CollectionUtils.containsAll(odds, multiples));
        assertTrue(CollectionUtils.containsAll(odds, odds));
    }

    @Test
    public void containsAnyInCollection() {
        final Collection<String> empty = new ArrayList<>(0);
        final Collection<String> one = new ArrayList<>(1);
        one.add("1");
        final Collection<String> two = new ArrayList<>(1);
        two.add("2");
        final Collection<String> three = new ArrayList<>(1);
        three.add("3");
        final Collection<String> odds = new ArrayList<>(2);
        odds.add("1");
        odds.add("3");

        assertTrue(CollectionUtils.containsAny(one, odds));
        assertTrue(CollectionUtils.containsAny(odds, one));
        assertTrue(CollectionUtils.containsAny(three, odds));
        assertTrue(CollectionUtils.containsAny(odds, three));
        assertTrue(CollectionUtils.containsAny(two, two));
        assertTrue(CollectionUtils.containsAny(odds, odds));

        assertFalse("containsAny({2},{1,3}) should return false.", CollectionUtils.containsAny(two, odds));
        assertFalse("containsAny({1,3},{2}) should return false.", CollectionUtils.containsAny(odds, two));
        assertFalse("containsAny({1},{3}) should return false.", CollectionUtils.containsAny(one, three));
        assertFalse("containsAny({3},{1}) should return false.", CollectionUtils.containsAny(three, one));
        assertFalse("containsAny({1,3},{}) should return false.", CollectionUtils.containsAny(odds, empty));
        assertFalse("containsAny({},{1,3}) should return false.", CollectionUtils.containsAny(empty, odds));
        assertFalse("containsAny({},{}) should return false.", CollectionUtils.containsAny(empty, empty));
    }

    @Test(expected = NullPointerException.class)
    public void testContainsAnyNullColl1() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.containsAny(null, list);
    }

    @Test(expected = NullPointerException.class)
    public void testContainsAnyNullColl2() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        final Collection<String> list2 = null;
        CollectionUtils.containsAny(list, list2);
    }

    @Test(expected = NullPointerException.class)
    public void testContainsAnyNullColl3() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        final String[] array = null;
        CollectionUtils.containsAny(list, array);
    }

    @Test
    public void containsAnyInArray() {
        final Collection<String> empty = new ArrayList<>(0);
        final String[] emptyArr = {};
        final Collection<String> one = new ArrayList<>(1);
        one.add("1");
        final String[] oneArr = {"1"};
        final Collection<String> two = new ArrayList<>(1);
        two.add("2");
        final String[] twoArr = {"2"};
        final Collection<String> three = new ArrayList<>(1);
        three.add("3");
        final String[] threeArr = {"3"};
        final Collection<String> odds = new ArrayList<>(2);
        odds.add("1");
        odds.add("3");
        final String[] oddsArr = {"1", "3"};

        assertTrue(CollectionUtils.containsAny(one, oddsArr));
        assertTrue(CollectionUtils.containsAny(odds, oneArr));
        assertTrue(CollectionUtils.containsAny(three, oddsArr));
        assertTrue(CollectionUtils.containsAny(odds, threeArr));
        assertTrue(CollectionUtils.containsAny(two, twoArr));
        assertTrue(CollectionUtils.containsAny(odds, oddsArr));

        assertFalse("containsAny({2},{1,3}) should return false.", CollectionUtils.containsAny(two, oddsArr));
        assertFalse("containsAny({1,3},{2}) should return false.", CollectionUtils.containsAny(odds, twoArr));
        assertFalse("containsAny({1},{3}) should return false.", CollectionUtils.containsAny(one, threeArr));
        assertFalse("containsAny({3},{1}) should return false.", CollectionUtils.containsAny(three, oneArr));
        assertFalse("containsAny({1,3},{}) should return false.", CollectionUtils.containsAny(odds, emptyArr));
        assertFalse("containsAny({},{1,3}) should return false.", CollectionUtils.containsAny(empty, oddsArr));
        assertFalse("containsAny({},{}) should return false.", CollectionUtils.containsAny(empty, emptyArr));
    }

    @Test(expected = NullPointerException.class)
    public void testContainsAnyInArrayNullColl1() {
        final String[] oneArr = {"1"};
        CollectionUtils.containsAny(null, oneArr);
    }

    @Test(expected = NullPointerException.class)
    public void testContainsAnyInArrayNullColl2() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        final Collection<String> list2 = null;
        CollectionUtils.containsAny(list, list2);
    }

    @Test(expected = NullPointerException.class)
    public void testContainsAnyInArrayNullArray() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        final String[] array = null;
        CollectionUtils.containsAny(list, array);
    }

    @Test
    public void union() {
        final Collection<Integer> col = CollectionUtils.union(iterableA, iterableC);
        final Map<Integer, Integer> freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq.get(1));
        assertEquals(Integer.valueOf(4), freq.get(2));
        assertEquals(Integer.valueOf(3), freq.get(3));
        assertEquals(Integer.valueOf(4), freq.get(4));
        assertEquals(Integer.valueOf(1), freq.get(5));

        final Collection<Number> col2 = CollectionUtils.union(collectionC2, iterableA);
        final Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(Integer.valueOf(1), freq2.get(1));
        assertEquals(Integer.valueOf(4), freq2.get(2));
        assertEquals(Integer.valueOf(3), freq2.get(3));
        assertEquals(Integer.valueOf(4), freq2.get(4));
        assertEquals(Integer.valueOf(1), freq2.get(5));
    }

    @Test(expected = NullPointerException.class)
    public void testUnionNullColl1() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.union(null, list);
    }

    @Test(expected = NullPointerException.class)
    public void testUnionNullColl2() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.union(list, null);
    }

    @Test
    public void intersection() {
        final Collection<Integer> col = CollectionUtils.intersection(iterableA, iterableC);
        final Map<Integer, Integer> freq = CollectionUtils.getCardinalityMap(col);
        assertNull(freq.get(1));
        assertEquals(Integer.valueOf(2), freq.get(2));
        assertEquals(Integer.valueOf(3), freq.get(3));
        assertEquals(Integer.valueOf(2), freq.get(4));
        assertNull(freq.get(5));

        final Collection<Number> col2 = CollectionUtils.intersection(collectionC2, collectionA);
        final Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col2);
        assertNull(freq2.get(1));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertEquals(Integer.valueOf(3), freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(4));
        assertNull(freq2.get(5));
    }

    @Test(expected = NullPointerException.class)
    public void testIntersectionNullColl1() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.intersection(null, list);
    }

    @Test(expected = NullPointerException.class)
    public void testIntersectionNullColl2() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.intersection(list, null);
    }

    @Test
    public void disjunction() {
        final Collection<Integer> col = CollectionUtils.disjunction(iterableA, iterableC);
        final Map<Integer, Integer> freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq.get(1));
        assertEquals(Integer.valueOf(2), freq.get(2));
        assertNull(freq.get(3));
        assertEquals(Integer.valueOf(2), freq.get(4));
        assertEquals(Integer.valueOf(1), freq.get(5));

        final Collection<Number> col2 = CollectionUtils.disjunction(collectionC2, collectionA);
        final Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(Integer.valueOf(1), freq2.get(1));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertNull(freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(4));
        assertEquals(Integer.valueOf(1), freq2.get(5));
    }

    @Test(expected = NullPointerException.class)
    public void testDisjunctionNullColl1() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.disjunction(null, list);
    }

    @Test(expected = NullPointerException.class)
    public void testDisjunctionNullColl2() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.disjunction(list, null);
    }

    @Test
    public void testDisjunctionAsUnionMinusIntersection() {
        final Collection<Number> dis = CollectionUtils.<Number>disjunction(collectionA, collectionC);
        final Collection<Number> un = CollectionUtils.<Number>union(collectionA, collectionC);
        final Collection<Number> inter = CollectionUtils.<Number>intersection(collectionA, collectionC);
        assertTrue(CollectionUtils.isEqualCollection(dis, CollectionUtils.subtract(un, inter)));
    }

    @Test
    public void testDisjunctionAsSymmetricDifference() {
        final Collection<Number> dis = CollectionUtils.<Number>disjunction(collectionA, collectionC);
        final Collection<Number> amb = CollectionUtils.<Number>subtract(collectionA, collectionC);
        final Collection<Number> bma = CollectionUtils.<Number>subtract(collectionC, collectionA);
        assertTrue(CollectionUtils.isEqualCollection(dis, CollectionUtils.union(amb, bma)));
    }

    @Test
    public void testSubtract() {
        final Collection<Integer> col = CollectionUtils.subtract(iterableA, iterableC);
        final Map<Integer, Integer> freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq.get(1));
        assertNull(freq.get(2));
        assertNull(freq.get(3));
        assertEquals(Integer.valueOf(2), freq.get(4));
        assertNull(freq.get(5));

        final Collection<Number> col2 = CollectionUtils.subtract(collectionC2, collectionA);
        final Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(Integer.valueOf(1), freq2.get(5));
        assertNull(freq2.get(4));
        assertNull(freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertNull(freq2.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void testSubtractNullColl1() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.subtract(null, list);
    }

    @Test(expected = NullPointerException.class)
    public void testSubtractNullColl2() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.subtract(list, null);
    }

    @Test
    public void testSubtractWithPredicate() {
        // greater than 3
        final Predicate<Number> predicate = n -> n.longValue() > 3L;

        final Collection<Number> col = CollectionUtils.subtract(iterableA, collectionC, predicate);
        final Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq2.get(1));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertEquals(Integer.valueOf(3), freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(4));
        assertNull(freq2.get(5));
    }

    @Test
    public void testIsSubCollectionOfSelf() {
        assertTrue(CollectionUtils.isSubCollection(collectionA, collectionA));
        assertTrue(CollectionUtils.isSubCollection(collectionB, collectionB));
    }

    @Test
    public void testIsSubCollection() {
        assertFalse(CollectionUtils.isSubCollection(collectionA, collectionC));
        assertFalse(CollectionUtils.isSubCollection(collectionC, collectionA));
    }

    @Test
    public void testIsSubCollection2() {
        final Collection<Integer> c = new ArrayList<>();
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(1);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(2);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(2);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(3);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(3);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(3);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(4);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(4);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(4);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertFalse(CollectionUtils.isSubCollection(collectionA, c));
        c.add(4);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(CollectionUtils.isSubCollection(collectionA, c));
        c.add(5);
        assertFalse(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(CollectionUtils.isSubCollection(collectionA, c));
    }

    @Test(expected = NullPointerException.class)
    public void testIsSubCollectionNullColl1() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.isSubCollection(null, list);
    }

    @Test(expected = NullPointerException.class)
    public void testIsSubCollectionNullColl2() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.isSubCollection(list, null);
    }

    @Test
    public void testIsEqualCollectionToSelf() {
        assertTrue(CollectionUtils.isEqualCollection(collectionA, collectionA));
        assertTrue(CollectionUtils.isEqualCollection(collectionB, collectionB));
    }

    @Test
    public void testIsEqualCollection() {
        assertFalse(CollectionUtils.isEqualCollection(collectionA, collectionC));
        assertFalse(CollectionUtils.isEqualCollection(collectionC, collectionA));
    }

    @Test
    public void testIsEqualCollectionReturnsFalse() {
        final List<Integer> b = new ArrayList<>(collectionA);
        // remove an extra '2', and add a 5.  This will increase the size of the cardinality
        b.remove(1);
        b.add(5);
        assertFalse(CollectionUtils.isEqualCollection(collectionA, b));
        assertFalse(CollectionUtils.isEqualCollection(b, collectionA));
    }

    @Test
    public void testIsEqualCollection2() {
        final Collection<String> a = new ArrayList<>();
        final Collection<String> b = new ArrayList<>();
        assertTrue(CollectionUtils.isEqualCollection(a, b));
        assertTrue(CollectionUtils.isEqualCollection(b, a));
        a.add("1");
        assertFalse(CollectionUtils.isEqualCollection(a, b));
        assertFalse(CollectionUtils.isEqualCollection(b, a));
        b.add("1");
        assertTrue(CollectionUtils.isEqualCollection(a, b));
        assertTrue(CollectionUtils.isEqualCollection(b, a));
        a.add("2");
        assertFalse(CollectionUtils.isEqualCollection(a, b));
        assertFalse(CollectionUtils.isEqualCollection(b, a));
        b.add("2");
        assertTrue(CollectionUtils.isEqualCollection(a, b));
        assertTrue(CollectionUtils.isEqualCollection(b, a));
        a.add("1");
        assertFalse(CollectionUtils.isEqualCollection(a, b));
        assertFalse(CollectionUtils.isEqualCollection(b, a));
        b.add("1");
        assertTrue(CollectionUtils.isEqualCollection(a, b));
        assertTrue(CollectionUtils.isEqualCollection(b, a));
    }

    @Test
    public void testIsEqualCollectionEquator() {
        final Collection<Integer> collB = CollectionUtils.collect(collectionB, TRANSFORM_TO_INTEGER);

        // odd / even equator
        final Equator<Integer> e = new Equator<Integer>() {
            @Override
            public boolean equate(final Integer o1, final Integer o2) {
                if (o1.intValue() % 2 == 0 ^ o2.intValue() % 2 == 0) {
                    return false;
                }
                return true;
            }

            @Override
            public int hash(final Integer o) {
                return o.intValue() % 2 == 0 ? Integer.valueOf(0).hashCode() : Integer.valueOf(1).hashCode();
            }
        };

        assertTrue(CollectionUtils.isEqualCollection(collectionA, collectionA, e));
        assertTrue(CollectionUtils.isEqualCollection(collectionA, collB, e));
        assertTrue(CollectionUtils.isEqualCollection(collB, collectionA, e));

        final Equator<Number> defaultEquator = DefaultEquator.defaultEquator();
        assertFalse(CollectionUtils.isEqualCollection(collectionA, collectionB, defaultEquator));
        assertFalse(CollectionUtils.isEqualCollection(collectionA, collB, defaultEquator));
    }

    @Test(expected=NullPointerException.class)
    public void testIsEqualCollectionNullEquator() {
        CollectionUtils.isEqualCollection(collectionA, collectionA, null);
    }

    @Test(expected = NullPointerException.class)
    public void testIsEqualCollectionNullColl1() {
        final Collection<Integer> list = new ArrayList<>(1);
        list.add(1);

        final Equator<Integer> e = new Equator<Integer>() {
            @Override
            public boolean equate(final Integer o1, final Integer o2) {
                if (o1.intValue() % 2 == 0 ^ o2.intValue() % 2 == 0) {
                    return false;
                }
                return true;
            }

            @Override
            public int hash(final Integer o) {
                return o.intValue() % 2 == 0 ? Integer.valueOf(0).hashCode() : Integer.valueOf(1).hashCode();
            }
        };

        CollectionUtils.isEqualCollection(null, list, e);
    }

    @Test(expected = NullPointerException.class)
    public void testIsEqualCollectionNullColl2() {
        final Collection<Integer> list = new ArrayList<>(1);
        list.add(1);

        final Equator<Integer> e = new Equator<Integer>() {
            @Override
            public boolean equate(final Integer o1, final Integer o2) {
                if (o1.intValue() % 2 == 0 ^ o2.intValue() % 2 == 0) {
                    return false;
                }
                return true;
            }

            @Override
            public int hash(final Integer o) {
                return o.intValue() % 2 == 0 ? Integer.valueOf(0).hashCode() : Integer.valueOf(1).hashCode();
            }
        };

        CollectionUtils.isEqualCollection(list, null, e);
    }

    @Test
    public void testIsProperSubCollection() {
        final Collection<String> a = new ArrayList<>();
        final Collection<String> b = new ArrayList<>();
        assertFalse(CollectionUtils.isProperSubCollection(a, b));
        b.add("1");
        assertTrue(CollectionUtils.isProperSubCollection(a, b));
        assertFalse(CollectionUtils.isProperSubCollection(b, a));
        assertFalse(CollectionUtils.isProperSubCollection(b, b));
        assertFalse(CollectionUtils.isProperSubCollection(a, a));
        a.add("1");
        a.add("2");
        b.add("2");
        assertFalse(CollectionUtils.isProperSubCollection(b, a));
        assertFalse(CollectionUtils.isProperSubCollection(a, b));
        a.add("1");
        assertTrue(CollectionUtils.isProperSubCollection(b, a));
        assertTrue(CollectionUtils.isProperSubCollection(CollectionUtils.intersection(collectionA, collectionC), collectionA));
        assertTrue(CollectionUtils.isProperSubCollection(CollectionUtils.subtract(a, b), a));
        assertFalse(CollectionUtils.isProperSubCollection(a, CollectionUtils.subtract(a, b)));
    }

    @Test(expected = NullPointerException.class)
    public void testIsProperSubCollectionNullColl1() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.isProperSubCollection(null, list);
    }

    @Test(expected = NullPointerException.class)
    public void testIsProperSubCollectionNullColl2() {
        final Collection<String> list = new ArrayList<>(1);
        list.add("1");
        CollectionUtils.isProperSubCollection(list, null);
    }

    @Test
    @Deprecated
    public void find() {
        Predicate<Number> testPredicate = equalPredicate((Number) 4);
        Integer test = CollectionUtils.find(collectionA, testPredicate);
        assertEquals(4, (int) test);
        testPredicate = equalPredicate((Number) 45);
        test = CollectionUtils.find(collectionA, testPredicate);
        assertNull(test);
        assertNull(CollectionUtils.find(null, testPredicate));
        assertNull(CollectionUtils.find(collectionA, null));
    }

    @Test
    @Deprecated
    public void forAllDoCollection() {
        final Closure<Collection<Integer>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<Collection<Integer>> col = new ArrayList<>();
        col.add(collectionA);
        col.add(collectionC);
        Closure<Collection<Integer>> resultClosure = CollectionUtils.forAllDo(col, testClosure);
        assertSame(testClosure, resultClosure);
        assertTrue(collectionA.isEmpty() && collectionC.isEmpty());
        // fix for various java 1.6 versions: keep the cast
        resultClosure = CollectionUtils.forAllDo(col, (Closure<Collection<Integer>>) null);
        assertNull(resultClosure);
        assertTrue(collectionA.isEmpty() && collectionC.isEmpty());
        resultClosure = CollectionUtils.forAllDo((Collection<Collection<Integer>>) null, testClosure);
        col.add(null);
        // null should be OK
        CollectionUtils.forAllDo(col, testClosure);
    }

    @Test
    @Deprecated
    public void forAllDoIterator() {
        final Closure<Collection<Integer>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<Collection<Integer>> col = new ArrayList<>();
        col.add(collectionA);
        col.add(collectionC);
        Closure<Collection<Integer>> resultClosure = CollectionUtils.forAllDo(col.iterator(), testClosure);
        assertSame(testClosure, resultClosure);
        assertTrue(collectionA.isEmpty() && collectionC.isEmpty());
        // fix for various java 1.6 versions: keep the cast
        resultClosure = CollectionUtils.forAllDo(col.iterator(), (Closure<Collection<Integer>>) null);
        assertNull(resultClosure);
        assertTrue(collectionA.isEmpty() && collectionC.isEmpty());
        resultClosure = CollectionUtils.forAllDo((Iterator<Collection<Integer>>) null, testClosure);
        col.add(null);
        // null should be OK
        CollectionUtils.forAllDo(col.iterator(), testClosure);
    }

    @Test(expected = FunctorException.class)
    @Deprecated
    public void forAllDoFailure() {
        final Closure<String> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<String> col = new ArrayList<>();
        col.add("x");
        CollectionUtils.forAllDo(col, testClosure);
    }

    @Test
    @Deprecated
    public void forAllButLastDoCollection() {
        final Closure<List<? extends Number>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<? extends Number>> col = new ArrayList<>();
        col.add(collectionA);
        col.add(collectionB);
        List<? extends Number> lastElement = CollectionUtils.forAllButLastDo(col, testClosure);
        assertSame(lastElement, collectionB);
        assertTrue(collectionA.isEmpty() && !collectionB.isEmpty());

        col.clear();
        col.add(collectionB);
        lastElement = CollectionUtils.forAllButLastDo(col, testClosure);
        assertSame(lastElement, collectionB);
        assertFalse(collectionB.isEmpty());

        col.clear();
        lastElement = CollectionUtils.forAllButLastDo(col, testClosure);
        assertNull(lastElement);

        final Collection<String> strings = Arrays.asList("a", "b", "c");
        final StringBuffer result = new StringBuffer();
        result.append(CollectionUtils.forAllButLastDo(strings, (Closure<String>) input -> result.append(input+";")));
        assertEquals("a;b;c", result.toString());

        final Collection<String> oneString = Arrays.asList("a");
        final StringBuffer resultOne = new StringBuffer();
        resultOne.append(CollectionUtils.forAllButLastDo(oneString, (Closure<String>) input -> resultOne.append(input+";")));
        assertEquals("a", resultOne.toString());
        assertNull(CollectionUtils.forAllButLastDo(strings, (Closure<String>) null)); // do not remove cast
        assertNull(CollectionUtils.forAllButLastDo((Collection<String>) null, (Closure<String>) null)); // do not remove cast
    }

    @Test
    @Deprecated
    public void forAllButLastDoIterator() {
        final Closure<List<? extends Number>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<? extends Number>> col = new ArrayList<>();
        col.add(collectionA);
        col.add(collectionB);
        final List<? extends Number> lastElement = CollectionUtils.forAllButLastDo(col.iterator(), testClosure);
        assertSame(lastElement, collectionB);
        assertTrue(collectionA.isEmpty() && !collectionB.isEmpty());

        assertNull(CollectionUtils.forAllButLastDo(col.iterator(), (Closure<List<? extends Number>>) null));
        assertNull(CollectionUtils.forAllButLastDo((Iterator<String>) null, (Closure<String>) null)); // do not remove cast
    }

    @Test
    public void getFromMap() {
        // Unordered map, entries exist
        final Map<String, String> expected = new HashMap<>();
        expected.put("zeroKey", "zero");
        expected.put("oneKey", "one");

        final Map<String, String> found = new HashMap<>();
        Map.Entry<String, String> entry = CollectionUtils.get(expected, 0);
        found.put(entry.getKey(), entry.getValue());
        entry = CollectionUtils.get(expected, 1);
        found.put(entry.getKey(), entry.getValue());
        assertEquals(expected, found);

        // Map index out of range
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            CollectionUtils.get(expected, 2);
        });
        assertTrue(exception.getMessage().contains("Entry does not exist: 0"));

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            CollectionUtils.get(expected, -2);
        });
        assertTrue(exception.getMessage().contains("Index cannot be negative: -2"));

        // Sorted map, entries exist, should respect order
        final SortedMap<String, String> map = new TreeMap<>();
        map.put("zeroKey", "zero");
        map.put("oneKey", "one");
        Map.Entry<String, String> test = CollectionUtils.get(map, 1);
        assertEquals("zeroKey", test.getKey());
        assertEquals("zero", test.getValue());
        test = CollectionUtils.get(map, 0);
        assertEquals("oneKey", test.getKey());
        assertEquals("one", test.getValue());
    }

    /**
     * Tests that {@link List}s are handled correctly - e.g. using
     * {@link List#get(int)}.
     */
    @Test(expected=IndexOutOfBoundsException.class)
    public void getFromList() throws Exception {
        // List, entry exists
        final List<String> list = createMock(List.class);
        expect(list.get(0)).andReturn("zero");
        expect(list.get(1)).andReturn("one");
        replay();
        final String string = CollectionUtils.get(list, 0);
        assertEquals("zero", string);
        assertEquals("one", CollectionUtils.get(list, 1));
        // list, non-existent entry -- IndexOutOfBoundsException
        CollectionUtils.get(new ArrayList<>(), 2);
    }

    @Test
    @Deprecated
    public void getFromIterator() throws Exception {
        // Iterator, entry exists
        Iterator<Integer> iterator = iterableA.iterator();
        assertEquals(1, (int) CollectionUtils.get(iterator, 0));
        iterator = iterableA.iterator();
        assertEquals(2, (int) CollectionUtils.get(iterator, 1));

        // Iterator, non-existent entry
        final Iterator<Integer> iteratorInteger = iterableA.iterator();
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            CollectionUtils.get(iteratorInteger, 10);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Entry does not exist: 0"));
        assertFalse(iteratorInteger.hasNext());
    }

    @Test
    @Deprecated
    public void getFromEnumeration() throws Exception {
        // Enumeration, entry exists
        final Vector<String> vector = new Vector<>();
        vector.addElement("zero");
        vector.addElement("one");
        Enumeration<String> en = vector.elements();
        assertEquals("zero", CollectionUtils.get(en, 0));
        en = vector.elements();
        assertEquals("one", CollectionUtils.get(en, 1));

        // Enumerator, non-existent entry
        Enumeration<String> enumerator = vector.elements();
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            CollectionUtils.get(enumerator, 3);
        });
        assertTrue(exception.getMessage().contains("Entry does not exist:"));
        assertFalse(enumerator.hasMoreElements());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Deprecated
    public void getFromIterable() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<>();
        bag.add("element", 1);
        assertEquals("element", CollectionUtils.get(bag, 0));

        // Collection, non-existent entry
        CollectionUtils.get(bag, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFromObjectArray() throws Exception {
        // Object array, entry exists
        final Object[] objArray = new Object[2];
        objArray[0] = "zero";
        objArray[1] = "one";
        assertEquals("zero", CollectionUtils.get(objArray, 0));
        assertEquals("one", CollectionUtils.get(objArray, 1));

        // Object array, non-existent entry --
        // ArrayIndexOutOfBoundsException
        CollectionUtils.get(objArray, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFromPrimitiveArray() throws Exception {
        // Primitive array, entry exists
        final int[] array = new int[2];
        array[0] = 10;
        array[1] = 20;
        assertEquals(10, CollectionUtils.get(array, 0));
        assertEquals(20, CollectionUtils.get(array, 1));

        // Object array, non-existent entry --
        // ArrayIndexOutOfBoundsException
        CollectionUtils.get(array, 2);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getFromObject() throws Exception {
        // Invalid object
        final Object obj = new Object();
        CollectionUtils.get(obj, 0);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSize_List() {
        List<String> list = null;
        assertEquals(0, CollectionUtils.size(list));
        list = new ArrayList<>();
        assertEquals(0, CollectionUtils.size(list));
        list.add("a");
        assertEquals(1, CollectionUtils.size(list));
        list.add("b");
        assertEquals(2, CollectionUtils.size(list));
    }

    @Test
    public void testSize_Map() {
        final Map<String, String> map = new HashMap<>();
        assertEquals(0, CollectionUtils.size(map));
        map.put("1", "a");
        assertEquals(1, CollectionUtils.size(map));
        map.put("2", "b");
        assertEquals(2, CollectionUtils.size(map));
    }

    @Test
    public void testSize_Array() {
        final Object[] objectArray = new Object[0];
        assertEquals(0, CollectionUtils.size(objectArray));

        final String[] stringArray = new String[3];
        assertEquals(3, CollectionUtils.size(stringArray));
        stringArray[0] = "a";
        stringArray[1] = "b";
        stringArray[2] = "c";
        assertEquals(3, CollectionUtils.size(stringArray));
    }

    @Test
    public void testSize_PrimitiveArray() {
        final int[] intArray = new int[0];
        assertEquals(0, CollectionUtils.size(intArray));

        final double[] doubleArray = new double[3];
        assertEquals(3, CollectionUtils.size(doubleArray));
        doubleArray[0] = 0.0d;
        doubleArray[1] = 1.0d;
        doubleArray[2] = 2.5d;
        assertEquals(3, CollectionUtils.size(doubleArray));
    }

    @Test
    public void testSize_Enumeration() {
        final Vector<String> list = new Vector<>();
        assertEquals(0, CollectionUtils.size(list.elements()));
        list.add("a");
        assertEquals(1, CollectionUtils.size(list.elements()));
        list.add("b");
        assertEquals(2, CollectionUtils.size(list.elements()));
    }

    @Test
    public void testSize_Iterator() {
        final List<String> list = new ArrayList<>();
        assertEquals(0, CollectionUtils.size(list.iterator()));
        list.add("a");
        assertEquals(1, CollectionUtils.size(list.iterator()));
        list.add("b");
        assertEquals(2, CollectionUtils.size(list.iterator()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSize_Other() {
        CollectionUtils.size("not a list");
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSizeIsEmpty_Null() {
        assertTrue(CollectionUtils.sizeIsEmpty(null));
    }

    @Test
    public void testSizeIsEmpty_List() {
        final List<String> list = new ArrayList<>();
        assertTrue(CollectionUtils.sizeIsEmpty(list));
        list.add("a");
        assertFalse(CollectionUtils.sizeIsEmpty(list));
    }

    @Test
    public void testSizeIsEmpty_Map() {
        final Map<String, String> map = new HashMap<>();
        assertEquals(true, CollectionUtils.sizeIsEmpty(map));
        map.put("1", "a");
        assertEquals(false, CollectionUtils.sizeIsEmpty(map));
    }

    @Test
    public void testSizeIsEmpty_Array() {
        final Object[] objectArray = new Object[0];
        assertTrue(CollectionUtils.sizeIsEmpty(objectArray));

        final String[] stringArray = new String[3];
        assertFalse(CollectionUtils.sizeIsEmpty(stringArray));
        stringArray[0] = "a";
        stringArray[1] = "b";
        stringArray[2] = "c";
        assertFalse(CollectionUtils.sizeIsEmpty(stringArray));
    }

    @Test
    public void testSizeIsEmpty_PrimitiveArray() {
        final int[] intArray = new int[0];
        assertTrue(CollectionUtils.sizeIsEmpty(intArray));

        final double[] doubleArray = new double[3];
        assertFalse(CollectionUtils.sizeIsEmpty(doubleArray));
        doubleArray[0] = 0.0d;
        doubleArray[1] = 1.0d;
        doubleArray[2] = 2.5d;
        assertFalse(CollectionUtils.sizeIsEmpty(doubleArray));
    }

    @Test
    public void testSizeIsEmpty_Enumeration() {
        final Vector<String> list = new Vector<>();
        assertTrue(CollectionUtils.sizeIsEmpty(list.elements()));
        list.add("a");
        assertFalse(CollectionUtils.sizeIsEmpty(list.elements()));
        final Enumeration<String> en = list.elements();
        en.nextElement();
        assertTrue(CollectionUtils.sizeIsEmpty(en));
    }

    @Test
    public void testSizeIsEmpty_Iterator() {
        final List<String> list = new ArrayList<>();
        assertTrue(CollectionUtils.sizeIsEmpty(list.iterator()));
        list.add("a");
        assertFalse(CollectionUtils.sizeIsEmpty(list.iterator()));
        final Iterator<String> it = list.iterator();
        it.next();
        assertTrue(CollectionUtils.sizeIsEmpty(it));
    }

    @Test
    public void testSizeIsEmpty_Other() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CollectionUtils.sizeIsEmpty("not a list");
        });
        assertTrue(exception.getMessage().contains("Unsupported object type: java.lang.String"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIsEmptyWithEmptyCollection() {
        assertTrue(CollectionUtils.isEmpty(new ArrayList<>()));
    }

    @Test
    public void testIsEmptyWithNonEmptyCollection() {
        assertFalse(CollectionUtils.isEmpty(Collections.singletonList("item")));
    }

    @Test
    public void testIsEmptyWithNull() {
        assertTrue(CollectionUtils.isEmpty(null));
    }

    @Test
    public void testIsNotEmptyWithEmptyCollection() {
        assertFalse(CollectionUtils.isNotEmpty(new ArrayList<>()));
    }

    @Test
    public void testIsNotEmptyWithNonEmptyCollection() {
        assertTrue(CollectionUtils.isNotEmpty(Collections.singletonList("item")));
    }

    @Test
    public void testIsNotEmptyWithNull() {
        assertFalse(CollectionUtils.isNotEmpty(null));
    }

    // -----------------------------------------------------------------------
    private static Predicate<Number> EQUALS_TWO = input -> input.intValue() == 2;

//Up to here
    @Test
    public void filter() {
        final List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        ints.add(3);
        final Iterable<Integer> iterable = ints;
        assertTrue(CollectionUtils.filter(iterable, EQUALS_TWO));
        assertEquals(1, ints.size());
        assertEquals(2, (int) ints.get(0));
    }

    @Test
    public void filterNullParameters() throws Exception {
        final List<Long> longs = Collections.nCopies(4, 10L);
        assertFalse(CollectionUtils.filter(longs, null));
        assertEquals(4, longs.size());
        assertFalse(CollectionUtils.filter(null, EQUALS_TWO));
        assertEquals(4, longs.size());
        assertFalse(CollectionUtils.filter(null, null));
        assertEquals(4, longs.size());
    }

    @Test
    public void filterInverse() {
        final List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        ints.add(3);
        final Iterable<Integer> iterable = ints;
        assertTrue(CollectionUtils.filterInverse(iterable, EQUALS_TWO));
        assertEquals(3, ints.size());
        assertEquals(1, (int) ints.get(0));
        assertEquals(3, (int) ints.get(1));
        assertEquals(3, (int) ints.get(2));
    }

    @Test
    public void filterInverseNullParameters() throws Exception {
        final List<Long> longs = Collections.nCopies(4, 10L);
        assertFalse(CollectionUtils.filterInverse(longs, null));
        assertEquals(4, longs.size());
        assertFalse(CollectionUtils.filterInverse(null, EQUALS_TWO));
        assertEquals(4, longs.size());
        assertFalse(CollectionUtils.filterInverse(null, null));
        assertEquals(4, longs.size());
    }

    @Test
    @Deprecated
    public void countMatches() {
        assertEquals(4, CollectionUtils.countMatches(iterableB, EQUALS_TWO));
        assertEquals(0, CollectionUtils.countMatches(iterableA, null));
        assertEquals(0, CollectionUtils.countMatches(null, EQUALS_TWO));
        assertEquals(0, CollectionUtils.countMatches(null, null));
    }

    @Test
    @Deprecated
    public void exists() {
        final List<Integer> list = new ArrayList<>();
        assertFalse(CollectionUtils.exists(null, null));
        assertFalse(CollectionUtils.exists(list, null));
        assertFalse(CollectionUtils.exists(null, EQUALS_TWO));
        assertFalse(CollectionUtils.exists(list, EQUALS_TWO));
        list.add(1);
        list.add(3);
        list.add(4);
        assertFalse(CollectionUtils.exists(list, EQUALS_TWO));

        list.add(2);
        assertTrue(CollectionUtils.exists(list, EQUALS_TWO));
    }

    @Test
    public void select() {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        // Ensure that the collection is the input type or a super type
        final Collection<Integer> output1 = CollectionUtils.select(list, EQUALS_TWO);
        final Collection<Number> output2 = CollectionUtils.<Number>select(list, EQUALS_TWO);
        final HashSet<Number> output3 = CollectionUtils.select(list, EQUALS_TWO, new HashSet<Number>());
        assertTrue(CollectionUtils.isEqualCollection(output1, output3));
        assertEquals(4, list.size());
        assertEquals(1, output1.size());
        assertEquals(2, output2.iterator().next());
    }

    @Test
    public void selectWithOutputCollections() {
        final List<Integer> input = new ArrayList<>();
        input.add(1);
        input.add(2);
        input.add(3);
        input.add(4);

        final List<Integer> output = new ArrayList<>();
        final List<Integer> rejected = new ArrayList<>();

        CollectionUtils.select(input, EQUALS_TWO, output, rejected);

        // output contains 2
        assertEquals(1, output.size());
        assertEquals(2, CollectionUtils.extractSingleton(output).intValue());

        // rejected contains 1, 3, and 4
        final Integer[] expected = {1, 3, 4};
        Assert.assertArrayEquals(expected, rejected.toArray());

        output.clear();
        rejected.clear();
        CollectionUtils.select((List<Integer>) null, EQUALS_TWO, output, rejected);
        assertTrue(output.isEmpty());
        assertTrue(rejected.isEmpty());
    }

    @Test
    public void selectRejected() {
        final List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        list.add(4L);
        final Collection<Long> output1 = CollectionUtils.selectRejected(list, EQUALS_TWO);
        final Collection<? extends Number> output2 = CollectionUtils.selectRejected(list, EQUALS_TWO);
        final HashSet<Number> output3 = CollectionUtils.selectRejected(list, EQUALS_TWO, new HashSet<Number>());
        assertTrue(CollectionUtils.isEqualCollection(output1, output2));
        assertTrue(CollectionUtils.isEqualCollection(output1, output3));
        assertEquals(4, list.size());
        assertEquals(3, output1.size());
        assertTrue(output1.contains(1L));
        assertTrue(output1.contains(3L));
        assertTrue(output1.contains(4L));
    }

    @Test
    public void collect() {
        final Transformer<Number, Long> transformer = TransformerUtils.constantTransformer(2L);
        Collection<Number> collection = CollectionUtils.<Integer, Number>collect(iterableA, transformer);
        assertTrue(collection.size() == collectionA.size());
        assertCollectResult(collection);

        ArrayList<Number> list;
        list = CollectionUtils.collect(collectionA, transformer, new ArrayList<Number>());
        assertTrue(list.size() == collectionA.size());
        assertCollectResult(list);

        Iterator<Integer> iterator = null;
        list = CollectionUtils.collect(iterator, transformer, new ArrayList<Number>());

        iterator = iterableA.iterator();
        list = CollectionUtils.collect(iterator, transformer, list);
        assertTrue(collection.size() == collectionA.size());
        assertCollectResult(collection);

        iterator = collectionA.iterator();
        collection = CollectionUtils.<Integer, Number>collect(iterator, transformer);
        assertTrue(collection.size() == collectionA.size());
        assertTrue(collection.contains(2L) && !collection.contains(1));
        collection = CollectionUtils.collect((Iterator<Integer>) null, (Transformer<Integer, Number>) null);
        assertTrue(collection.size() == 0);

        final int size = collectionA.size();
        collectionB = CollectionUtils.collect((Collection<Integer>) null, transformer, collectionB);
        assertTrue(collectionA.size() == size && collectionA.contains(1));
        CollectionUtils.collect(collectionB, null, collectionA);
        assertTrue(collectionA.size() == size && collectionA.contains(1));

    }

    private void assertCollectResult(final Collection<Number> collection) {
        assertTrue(collectionA.contains(1) && !collectionA.contains(2L));
        assertTrue(collection.contains(2L) && !collection.contains(1));
    }

    Transformer<Object, Integer> TRANSFORM_TO_INTEGER = input -> Integer.valueOf(((Long) input).intValue());

    @Test
    public void transform1() {
        List<Number> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        CollectionUtils.transform(list, TRANSFORM_TO_INTEGER);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));

        list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        CollectionUtils.transform(null, TRANSFORM_TO_INTEGER);
        assertEquals(3, list.size());
        CollectionUtils.transform(list, null);
        assertEquals(3, list.size());
        CollectionUtils.transform(null, null);
        assertEquals(3, list.size());
    }

    @Test
    public void transform2() {
        final Set<Number> set = new HashSet<>();
        set.add(1L);
        set.add(2L);
        set.add(3L);
        CollectionUtils.transform(set, input -> 4);
        assertEquals(1, set.size());
        assertEquals(4, set.iterator().next());
    }

    // -----------------------------------------------------------------------
    @Test
    public void addIgnoreNull() {
        final Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        assertFalse(CollectionUtils.addIgnoreNull(set, null));
        assertEquals(3, set.size());
        assertFalse(CollectionUtils.addIgnoreNull(set, "1"));
        assertEquals(3, set.size());
        assertTrue(CollectionUtils.addIgnoreNull(set, "4"));
        assertEquals(4, set.size());
        assertTrue(set.contains("4"));
    }

    @Test(expected = NullPointerException.class)
    public void testAddIgnoreNullNullColl() {
        CollectionUtils.addIgnoreNull(null, "1");
    }

    // -----------------------------------------------------------------------
    @Test
    public void predicatedCollection() {
        final Predicate<Object> predicate = PredicateUtils.instanceofPredicate(Integer.class);
        final Collection<Number> collection = CollectionUtils.predicatedCollection(new ArrayList<Number>(), predicate);
        assertTrue(collection instanceof PredicatedCollection);
    }

    @Test(expected = NullPointerException.class)
    public void testPredicatedCollectionNullColl() {
        final Predicate<Object> predicate = PredicateUtils.instanceofPredicate(Integer.class);
        CollectionUtils.predicatedCollection(null, predicate);
    }

    @Test(expected = NullPointerException.class)
    public void testPredicatedCollectionNullPredicate() {
        final Collection<Integer> list = new ArrayList<>();
        CollectionUtils.predicatedCollection(list, null);
    }

    @Test
    public void isFull() {
        final Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        assertFalse(CollectionUtils.isFull(set));

        final CircularFifoQueue<String> buf = new CircularFifoQueue<>(set);
        assertFalse(CollectionUtils.isFull(buf));
        buf.remove("2");
        assertFalse(CollectionUtils.isFull(buf));
        buf.add("2");
        assertFalse(CollectionUtils.isFull(buf));
    }

    @Test(expected = NullPointerException.class)
    public void testIsFullNullColl() {
        CollectionUtils.isFull(null);
    }

    @Test
    public void isEmpty() {
        assertFalse(CollectionUtils.isNotEmpty(null));
        assertTrue(CollectionUtils.isNotEmpty(collectionA));
    }

    @Test
    public void maxSize() {
        final Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        assertEquals(-1, CollectionUtils.maxSize(set));

        final Queue<String> buf = new CircularFifoQueue<>(set);
        assertEquals(3, CollectionUtils.maxSize(buf));
        buf.remove("2");
        assertEquals(3, CollectionUtils.maxSize(buf));
        buf.add("2");
        assertEquals(3, CollectionUtils.maxSize(buf));
    }

    @Test(expected = NullPointerException.class)
    public void testMaxSizeNullColl() {
        CollectionUtils.maxSize(null);
    }

    @Test
    public void intersectionUsesMethodEquals() {
        // Let elta and eltb be objects...
        final Integer elta = 17;
        final Integer eltb = 17;

        // ...which are the same (==)
        assertSame(elta, eltb);

        // Let cola and colb be collections...
        final Collection<Number> cola = new ArrayList<>();
        final Collection<Integer> colb = new ArrayList<>();

        // ...which contain elta and eltb,
        // respectively.
        cola.add(elta);
        colb.add(eltb);

        // Then the intersection of the two
        // should contain one element.
        final Collection<Number> intersection = CollectionUtils.intersection(cola, colb);
        assertEquals(1, intersection.size());

        // In practice, this element will be the same (==) as elta
        // or eltb, although this isn't strictly part of the
        // contract.
        final Object eltc = intersection.iterator().next();
        assertTrue(eltc == elta && eltc == eltb);

        // In any event, this element remains equal,
        // to both elta and eltb.
        assertEquals(elta, eltc);
        assertEquals(eltc, elta);
        assertEquals(eltb, eltc);
        assertEquals(eltc, eltb);
    }

    // -----------------------------------------------------------------------
    //Up to here
    @Test
    public void testRetainAll() {
        final List<String> base = new ArrayList<>();
        base.add("A");
        base.add("B");
        base.add("C");
        final List<Object> sub = new ArrayList<>();
        sub.add("A");
        sub.add("C");
        sub.add("X");

        final Collection<String> result = CollectionUtils.retainAll(base, sub);
        assertEquals(2, result.size());
        assertTrue(result.contains("A"));
        assertFalse(result.contains("B"));
        assertTrue(result.contains("C"));
        assertEquals(3, base.size());
        assertTrue(base.contains("A"));
        assertTrue(base.contains("B"));
        assertTrue(base.contains("C"));
        assertEquals(3, sub.size());
        assertTrue(sub.contains("A"));
        assertTrue(sub.contains("C"));
        assertTrue(sub.contains("X"));
    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNullBaseColl() {
        final List<Object> sub = new ArrayList<>();
        sub.add("A");
        CollectionUtils.retainAll(null, sub);
    }

    @Test(expected = NullPointerException.class)
    public void testRetainAllNullSubColl() {
        final List<String> base = new ArrayList<>();
        base.add("A");
        CollectionUtils.retainAll(base, null);
    }

    @Test
    public void testRemoveRange() {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        Collection<Integer> result = CollectionUtils.removeRange(list, 0, 0);
        assertEquals(1, list.size());
        assertEquals(0, result.size());

        list.add(2);
        list.add(3);
        result = CollectionUtils.removeRange(list, 1, 3);
        assertEquals(1, list.size());
        assertEquals(1, (int) list.get(0));
        assertEquals(2, result.size());
        assertTrue(result.contains(2));
        assertTrue(result.contains(3));
    }

    @Test(expected=NullPointerException.class)
    public void testRemoveRangeNull() {
        final Collection<Integer> list = null;
        CollectionUtils.removeRange(list, 0, 0);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testRemoveRangeStartIndexNegative() {
        final Collection<Integer> list = new ArrayList<>();
        list.add(1);
        CollectionUtils.removeRange(list, -1, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRemoveRangeEndIndexNegative() {
        final Collection<Integer> list = new ArrayList<>();
        list.add(1);
        CollectionUtils.removeRange(list, 0, -1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRemoveRangeEndLowStart() {
        final Collection<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        CollectionUtils.removeRange(list, 1, 0);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testRemoveRangeWrongEndIndex() {
        final Collection<Integer> list = new ArrayList<>();
        list.add(1);
        CollectionUtils.removeRange(list, 0, 2);
    }

    @Test
    public void testRemoveCount() {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Collection<Integer> result = CollectionUtils.removeCount(list, 0, 0);
        assertEquals(4, list.size());
        assertEquals(0, result.size());

        result = CollectionUtils.removeCount(list, 0, 1);
        assertEquals(3, list.size());
        assertEquals(2, (int) list.get(0));
        assertEquals(1, result.size());
        assertTrue(result.contains(1));

        list.add(5);
        list.add(6);
        result = CollectionUtils.removeCount(list, 1, 3);

        assertEquals(2, list.size());
        assertEquals(2, (int) list.get(0));
        assertEquals(6, (int) list.get(1));
        assertEquals(3, result.size());
        assertTrue(result.contains(3));
        assertTrue(result.contains(4));
        assertTrue(result.contains(5));
    }

    @Test(expected=NullPointerException.class)
    public void testRemoveCountWithNull() {
        final Collection<Integer> list = null;
        CollectionUtils.removeCount(list, 0, 1);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testRemoveCountStartNegative() {
        final Collection<Integer> list = new ArrayList<>();
        CollectionUtils.removeCount(list, -1, 1);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testRemoveCountNegative() {
        final Collection<Integer> list = new ArrayList<>();
        CollectionUtils.removeCount(list, 0, -1);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testRemoveCountWrongCount() {
        final Collection<Integer> list = new ArrayList<>();
        list.add(1);
        CollectionUtils.removeCount(list, 0, 2);
    }

    @Test
    public void testRemoveAll() {
        final List<String> base = new ArrayList<>();
        base.add("A");
        base.add("B");
        base.add("C");
        final List<String> sub = new ArrayList<>();
        sub.add("A");
        sub.add("C");
        sub.add("X");

        final Collection<String> result = CollectionUtils.removeAll(base, sub);
        assertEquals(1, result.size());
        assertFalse(result.contains("A"));
        assertTrue(result.contains("B"));
        assertFalse(result.contains("C"));
        assertEquals(3, base.size());
        assertTrue(base.contains("A"));
        assertTrue(base.contains("B"));
        assertTrue(base.contains("C"));
        assertEquals(3, sub.size());
        assertTrue(sub.contains("A"));
        assertTrue(sub.contains("C"));
        assertTrue(sub.contains("X"));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNullBaseColl() {
        final List<String> sub = new ArrayList<>();
        sub.add("A");
        CollectionUtils.removeAll(null, sub);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNullSubColl() {
        final List<String> base = new ArrayList<>();
        base.add("A");
        CollectionUtils.removeAll(base, null);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testTransformedCollection() {
        final Transformer<Object, Object> transformer = TransformerUtils.nopTransformer();
        final Collection<Object> collection = CollectionUtils.transformingCollection(new ArrayList<>(), transformer);
        assertTrue(collection instanceof TransformedCollection);
    }

    @Test(expected = NullPointerException.class)
    public void testTransformingCollectionNullColl() {
        final Transformer<Object, Object> transformer = TransformerUtils.nopTransformer();
        CollectionUtils.transformingCollection(null, transformer);
    }

    @Test(expected = NullPointerException.class)
    public void testTransformingCollectionNullTransformer() {
        final List<String> list = new ArrayList<>();
        CollectionUtils.transformingCollection(list, null);
    }

    @Test
    public void testTransformedCollection_2() {
        final List<Object> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        final Collection<Object> result = CollectionUtils.transformingCollection(list, TRANSFORM_TO_INTEGER);
        assertTrue(result.contains("1")); // untransformed
        assertTrue(result.contains("2")); // untransformed
        assertTrue(result.contains("3")); // untransformed
    }

    @Test
    @Deprecated
    public void testSynchronizedCollection() {
        final Collection<Object> col = CollectionUtils.synchronizedCollection(new ArrayList<>());
        assertTrue(col instanceof SynchronizedCollection);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            CollectionUtils.synchronizedCollection(null);
        });
        assertTrue(exception.getMessage().contains("collection"));
    }

    @Test
    @Deprecated
    public void testUnmodifiableCollection() {
        final Collection<Object> col = CollectionUtils.unmodifiableCollection(new ArrayList<>());
        assertTrue(col instanceof UnmodifiableCollection);
        Exception exception = assertThrows(NullPointerException.class, () -> {
            CollectionUtils.unmodifiableCollection(null);
        });
        assertTrue(exception.getMessage().contains("collection"));
    }

    @Test
    public void emptyCollection() throws Exception {
        final Collection<Number> coll = CollectionUtils.emptyCollection();
        assertEquals(CollectionUtils.EMPTY_COLLECTION, coll);
    }

    @Test
    public void emptyIfNull() {
        assertTrue(CollectionUtils.emptyIfNull(null).isEmpty());
        final Collection<Object> collection = new ArrayList<>();
        assertSame(collection, CollectionUtils.emptyIfNull(collection));
    }

    /**
     * This test ensures that {@link Iterable}s are supported by {@link CollectionUtils}.
     * Specifically, it uses mocks to ensure that if the passed in
     * {@link Iterable} is a {@link Collection} then
     * {@link Collection#addAll(Collection)} is called instead of iterating.
     */
    @Test
    public void addAllForIterable() {
        final Collection<Integer> inputCollection = createMock(Collection.class);
        final Iterable<Integer> inputIterable = inputCollection;
        final Iterable<Long> iterable = createMock(Iterable.class);
        final Iterator<Long> iterator = createMock(Iterator.class);
        final Collection<Number> c = createMock(Collection.class);

        expect(iterable.iterator()).andReturn(iterator);
        next(iterator, 1L);
        next(iterator, 2L);
        next(iterator, 3L);
        expect(iterator.hasNext()).andReturn(false);
        expect(c.add(1L)).andReturn(true);
        expect(c.add(2L)).andReturn(true);
        expect(c.add(3L)).andReturn(true);
        // Check that the collection is added using
        // Collection.addAll(Collection)
        expect(c.addAll(inputCollection)).andReturn(true);

        // Ensure the method returns false if nothing is added
        expect(iterable.iterator()).andReturn(iterator);
        next(iterator, 1L);
        expect(iterator.hasNext()).andReturn(false);
        expect(c.add(1L)).andReturn(false);
        expect(c.addAll(inputCollection)).andReturn(false);

        replay();
        assertTrue(CollectionUtils.addAll(c, iterable));
        assertTrue(CollectionUtils.addAll(c, inputIterable));

        assertFalse(CollectionUtils.addAll(c, iterable));
        assertFalse(CollectionUtils.addAll(c, inputIterable));
        verify();
    }

    @Test
    public void addAllForEnumeration() {
        final Hashtable<Integer, Integer> h = new Hashtable<>();
        h.put(5, 5);
        final Enumeration<? extends Integer> enumeration = h.keys();
        CollectionUtils.addAll(collectionA, enumeration);
        assertTrue(collectionA.contains(5));
    }

    @Test
    public void addAllForElements() {
        CollectionUtils.addAll(collectionA, 5);
        assertTrue(collectionA.contains(5));
    }

    @Test(expected = NullPointerException.class)
    public void testaddAllNullColl1() {
        final List<Integer> list = new ArrayList<>();
        CollectionUtils.addAll(null, list);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllNullColl2() {
        final List<Integer> list = new ArrayList<>();
        final Iterable<Integer> list2 = null;
        CollectionUtils.addAll(list, list2);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllNullColl3() {
        final List<Integer> list = new ArrayList<>();
        final Iterator<Integer> list2 = null;
        CollectionUtils.addAll(list, list2);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllNullColl4() {
        final List<Integer> list = new ArrayList<>();
        final Enumeration<Integer> enumArray = null;
        CollectionUtils.addAll(list, enumArray);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllNullColl5() {
        final List<Integer> list = new ArrayList<>();
        final Integer[] array = null;
        CollectionUtils.addAll(list, array);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getNegative() {
        CollectionUtils.get((Object) collectionA, -3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getPositiveOutOfBounds() {
        CollectionUtils.get((Object) collectionA.iterator(), 30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void get1() {
        CollectionUtils.get((Object) null, 0);
    }

    @Test
    public void get() {
        assertEquals(2, CollectionUtils.get((Object) collectionA, 2));
        assertEquals(2, CollectionUtils.get((Object) collectionA.iterator(), 2));
        final Map<Integer, Integer> map = CollectionUtils.getCardinalityMap(collectionA);
        assertEquals(map.entrySet().iterator().next(), CollectionUtils.get((Object) map, 0));
    }

    @Test
    public void getIterator() {
        final Iterator<Integer> it = collectionA.iterator();
        assertEquals(Integer.valueOf(2), CollectionUtils.get((Object) it, 2));
        assertTrue(it.hasNext());
        assertEquals(Integer.valueOf(4), CollectionUtils.get((Object) it, 6));
        assertFalse(it.hasNext());
    }

    @Test
    public void getEnumeration() {
        final Vector<Integer> vectorA = new Vector<>(collectionA);
        final Enumeration<Integer> e = vectorA.elements();
        assertEquals(Integer.valueOf(2), CollectionUtils.get(e, 2));
        assertTrue(e.hasMoreElements());
        assertEquals(Integer.valueOf(4), CollectionUtils.get(e, 6));
        assertFalse(e.hasMoreElements());
    }

    @Test
    public void reverse() {
        CollectionUtils.reverseArray(new Object[] {});
        final Integer[] a = collectionA.toArray(new Integer[collectionA.size()]);
        CollectionUtils.reverseArray(a);
        // assume our implementation is correct if it returns the same order as the Java function
        Collections.reverse(collectionA);
        assertEquals(collectionA, Arrays.asList(a));
    }

    @Test(expected = NullPointerException.class)
    public void testReverseArrayNull() {
        CollectionUtils.reverseArray(null);
    }

    @Test
    public void extractSingleton() {
        final ArrayList<String> coll = null;
        Exception exception = assertThrows(NullPointerException.class, () -> {
            CollectionUtils.extractSingleton(coll);
        });
        assertTrue(exception.getMessage().contains("collection"));

        final ArrayList<String> coll2 = new ArrayList<>();
        exception = assertThrows(IllegalArgumentException.class, () -> {
            CollectionUtils.extractSingleton(coll2);
        });
        assertTrue(exception.getMessage().contains("Can extract singleton only when collection size == 1"));

        coll2.add("foo");
        assertEquals("foo", CollectionUtils.extractSingleton(coll2));
        coll2.add("bar");
        exception = assertThrows(IllegalArgumentException.class, () -> {
            CollectionUtils.extractSingleton(coll2);
        });
        assertTrue(exception.getMessage().contains("Can extract singleton only when collection size == 1"));
    }

    /**
     * Records the next object returned for a mock iterator
     */
    private <T> void next(final Iterator<T> iterator, final T t) {
        expect(iterator.hasNext()).andReturn(true);
        expect(iterator.next()).andReturn(t);
    }

    @Test(expected=NullPointerException.class)
    public void collateException0() {
        CollectionUtils.collate(null, collectionC);
    }

    @Test(expected=NullPointerException.class)
    public void collateException1() {
        CollectionUtils.collate(collectionA, null);
    }

    @Test(expected=NullPointerException.class)
    public void collateException2() {
        CollectionUtils.collate(collectionA, collectionC, null);
    }

    @Test
    public void testCollate() {
        List<Integer> result = CollectionUtils.collate(emptyCollection, emptyCollection);
        assertEquals("Merge empty with empty", 0, result.size());

        result = CollectionUtils.collate(collectionA, emptyCollection);
        assertEquals("Merge empty with non-empty", collectionA, result);

        List<Integer> result1 = CollectionUtils.collate(collectionD, collectionE);
        List<Integer> result2 = CollectionUtils.collate(collectionE, collectionD);
        assertEquals("Merge two lists 1", result1, result2);

        final List<Integer> combinedList = new ArrayList<>();
        combinedList.addAll(collectionD);
        combinedList.addAll(collectionE);
        Collections.sort(combinedList);

        assertEquals("Merge two lists 2", combinedList, result2);

        final Comparator<Integer> reverseComparator =
                ComparatorUtils.reversedComparator(ComparatorUtils.<Integer>naturalComparator());

        result = CollectionUtils.collate(emptyCollection, emptyCollection, reverseComparator);
        assertEquals("Comparator Merge empty with empty", 0, result.size());

        Collections.reverse((List<Integer>) collectionD);
        Collections.reverse((List<Integer>) collectionE);
        Collections.reverse(combinedList);

        result1 = CollectionUtils.collate(collectionD, collectionE, reverseComparator);
        result2 = CollectionUtils.collate(collectionE, collectionD, reverseComparator);
        assertEquals("Comparator Merge two lists 1", result1, result2);
        assertEquals("Comparator Merge two lists 2", combinedList, result2);
    }

    @Test
    public void testCollateIgnoreDuplicates() {
        final List<Integer> result1 = CollectionUtils.collate(collectionD, collectionE, false);
        final List<Integer> result2 = CollectionUtils.collate(collectionE, collectionD, false);
        assertEquals("Merge two lists 1 - ignore duplicates", result1, result2);

        final Set<Integer> combinedSet = new HashSet<>();
        combinedSet.addAll(collectionD);
        combinedSet.addAll(collectionE);
        final List<Integer> combinedList = new ArrayList<>(combinedSet);
        Collections.sort(combinedList);

        assertEquals("Merge two lists 2 - ignore duplicates", combinedList, result2);
    }

    @Test(expected=NullPointerException.class)
    public void testPermutationsWithNullCollection() {
        CollectionUtils.permutations(null);
    }

    @Test
    public void testPermutations() {
        final List<Integer> sample = collectionA.subList(0, 5);
        final Collection<List<Integer>> permutations = CollectionUtils.permutations(sample);

        // result size = n!
        final int collSize = sample.size();
        int factorial = 1;
        for (int i = 1; i <= collSize; i++) {
            factorial *= i;
        }
        assertEquals(factorial, permutations.size());
    }

    @Test
    @Deprecated
    public void testMatchesAll() {
        assertFalse(CollectionUtils.matchesAll(null, null));
        assertFalse(CollectionUtils.matchesAll(collectionA, null));

        final Predicate<Integer> lessThanFive = object -> object < 5;
        assertTrue(CollectionUtils.matchesAll(collectionA, lessThanFive));

        final Predicate<Integer> lessThanFour = object -> object < 4;
        assertFalse(CollectionUtils.matchesAll(collectionA, lessThanFour));

        assertTrue(CollectionUtils.matchesAll(null, lessThanFour));
        assertTrue(CollectionUtils.matchesAll(emptyCollection, lessThanFour));
    }

    @Test
    public void testRemoveAllWithEquator() {
        final List<String> base = new ArrayList<>();
        base.add("AC");
        base.add("BB");
        base.add("CA");

        final List<String> remove = new ArrayList<>();
        remove.add("AA");
        remove.add("CX");
        remove.add("XZ");

        // use an equator which compares the second letter only
        final Collection<String> result = CollectionUtils.removeAll(base, remove, new Equator<String>() {

            @Override
            public boolean equate(final String o1, final String o2) {
                return o1.charAt(1) == o2.charAt(1);
            }

            @Override
            public int hash(final String o) {
                return o.charAt(1);
            }
        });

        assertEquals(2, result.size());
        assertTrue(result.contains("AC"));
        assertTrue(result.contains("BB"));
        assertFalse(result.contains("CA"));
        assertEquals(3, base.size());
        assertTrue(base.contains("AC"));
        assertTrue(base.contains("BB"));
        assertTrue(base.contains("CA"));
        assertEquals(3, remove.size());
        assertTrue(remove.contains("AA"));
        assertTrue(remove.contains("CX"));
        assertTrue(remove.contains("XZ"));

        Exception exception = assertThrows(NullPointerException.class, () -> {
            CollectionUtils.removeAll(null, null, DefaultEquator.defaultEquator());
        });
        assertTrue(exception.getMessage().contains("collection"));

        exception = assertThrows(NullPointerException.class, () -> {
            CollectionUtils.removeAll(base, remove, null);
        });
        assertTrue(exception.getMessage().contains("equator"));
    }

    @Test
    public void testRetainAllWithEquator() {
        final List<String> base = new ArrayList<>();
        base.add("AC");
        base.add("BB");
        base.add("CA");

        final List<String> retain = new ArrayList<>();
        retain.add("AA");
        retain.add("CX");
        retain.add("XZ");

        // use an equator which compares the second letter only
        final Collection<String> result = CollectionUtils.retainAll(base, retain, new Equator<String>() {

            @Override
            public boolean equate(final String o1, final String o2) {
                return o1.charAt(1) == o2.charAt(1);
            }

            @Override
            public int hash(final String o) {
                return o.charAt(1);
            }
        });
        assertEquals(1, result.size());
        assertTrue(result.contains("CA"));
        assertFalse(result.contains("BB"));
        assertFalse(result.contains("AC"));

        assertEquals(3, base.size());
        assertTrue(base.contains("AC"));
        assertTrue(base.contains("BB"));
        assertTrue(base.contains("CA"));

        assertEquals(3, retain.size());
        assertTrue(retain.contains("AA"));
        assertTrue(retain.contains("CX"));
        assertTrue(retain.contains("XZ"));

        Exception exception = assertThrows(NullPointerException.class, () -> {
            CollectionUtils.retainAll(null, null, null);
        });
        assertTrue(exception.getMessage().contains("collection"));

        exception = assertThrows(NullPointerException.class, () -> {
            CollectionUtils.retainAll(base, retain, null);
        });
        assertTrue(exception.getMessage().contains("equator"));
    }

}
