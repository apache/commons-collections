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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.functors.DefaultEquator;
import org.junit.Assert;

/**
 * Tests for IterableUtils.
 *
 * @since 4.1
 * @version $Id$
 */
public class IterableUtilsTest extends BulkTest {

    /**
     * Iterable of {@link Integer}s
     */
    private Iterable<Integer> iterableA = null;
    
    /**
     * Iterable of {@link Long}s
     */
    private Iterable<Long> iterableB = null;

    /**
     * Iterable of {@link Integer}s
     */
    private Iterable<Integer> iterableC = null;

    /**
     * Iterable of {@link Number}s
     */
    private Iterable<Number> iterableC2 = null;

    /**
     * Iterable of {@link Number}s
     */
    private Iterable<Number> iterableA2 = null;

    /**
     * Iterable of {@link Number}s
     */
    private Iterable<Number> iterableB2 = null;

    /**
     * Iterable of {@link Integer}s
     */
    private Iterable<Integer> iterableD = null;

    /**
     * Iterable of {@link Integer}s
     */
    private Iterable<Integer> iterableE = null;

    private final Iterable<Integer> emptyIterable = new ArrayList<Integer>(1);

    private static Predicate<Number> EQUALS_TWO = new Predicate<Number>() {
        public boolean evaluate(final Number input) {
            return input.intValue() == 2;
        }
    };

    private static Predicate<Number> EVEN = new Predicate<Number>() {
        public boolean evaluate(final Number input) {
            return input.intValue() % 2 == 0;
        }
    };

    Transformer<Object, Integer> TRANSFORM_TO_INTEGER = new Transformer<Object, Integer>() {
        public Integer transform(final Object input) {
            return Integer.valueOf(((Long) input).intValue());
        }
    };

    public IterableUtilsTest(final String name){
        super(name);
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(IterableUtilsTest.class);
    }
    
    @Override
    public void setUp() {

        List<Integer> collectionA = new ArrayList<Integer>();
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

        List<Long> collectionB = new LinkedList<Long>();
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

        List<Integer> collectionC = new ArrayList<Integer>();
        for (final Long l : collectionB) {
            collectionC.add(l.intValue());
        }

        List<Number> collectionA2 = new ArrayList<Number>(collectionA);
        List<Number> collectionB2 = new LinkedList<Number>(collectionB);
        List<Number> collectionC2 = new LinkedList<Number>(collectionC);

        iterableA = collectionA;
        iterableB = collectionB;
        iterableC = collectionC;
        iterableA2 = collectionA2;
        iterableB2 = collectionB2;
        iterableC2 = collectionC2;

        List<Integer> collectionD = new ArrayList<Integer>();
        collectionD.add(1);
        collectionD.add(3);
        collectionD.add(3);
        collectionD.add(3);
        collectionD.add(5);
        collectionD.add(7);
        collectionD.add(7);
        collectionD.add(10);

        List<Integer> collectionE = new ArrayList<Integer>();
        collectionE.add(2);
        collectionE.add(4);
        collectionE.add(4);
        collectionE.add(5);
        collectionE.add(6);
        collectionE.add(6);
        collectionE.add(9);

        iterableD = collectionD;
        iterableE = collectionE;

    }
    
    public void testDisjunctionAsSymmetricDifference() {
        final Collection<Number> dis = IterableUtils.<Number> disjunction(iterableA, iterableC);
        final Collection<Number> amb = IterableUtils.<Number> subtract(iterableA, iterableC);
        final Collection<Number> bma = IterableUtils.<Number> subtract(iterableC, iterableA);
        assertTrue(IterableUtils.isEqualIterable(dis, IterableUtils.union(amb, bma)));
    }

    public void testDisjunctionAsUnionMinusIntersection() {
        final Collection<Number> dis = IterableUtils.<Number> disjunction(iterableA, iterableC);
        final Collection<Number> un = IterableUtils.<Number> union(iterableA, iterableC);
        final Collection<Number> inter = IterableUtils.<Number> intersection(iterableA, iterableC);
        assertTrue(IterableUtils.isEqualIterable(dis, IterableUtils.subtract(un, inter)));
    }

    public void testUnion() {
        final Collection<Integer> col = IterableUtils.union(iterableA, iterableC);
        final Map<Integer, Integer> freq = IterableUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq.get(1));
        assertEquals(Integer.valueOf(4), freq.get(2));
        assertEquals(Integer.valueOf(3), freq.get(3));
        assertEquals(Integer.valueOf(4), freq.get(4));
        assertEquals(Integer.valueOf(1), freq.get(5));

        final Collection<Number> col2 = IterableUtils.union(iterableC2, iterableA);
        final Map<Number, Integer> freq2 = IterableUtils.getCardinalityMap(col2);
        assertEquals(Integer.valueOf(1), freq2.get(1));
        assertEquals(Integer.valueOf(4), freq2.get(2));
        assertEquals(Integer.valueOf(3), freq2.get(3));
        assertEquals(Integer.valueOf(4), freq2.get(4));
        assertEquals(Integer.valueOf(1), freq2.get(5));
    }

    public void testIntersection() {
        final Collection<Integer> col = IterableUtils.intersection(iterableA, iterableC);
        final Map<Integer, Integer> freq = IterableUtils.getCardinalityMap(col);
        assertNull(freq.get(1));
        assertEquals(Integer.valueOf(2), freq.get(2));
        assertEquals(Integer.valueOf(3), freq.get(3));
        assertEquals(Integer.valueOf(2), freq.get(4));
        assertNull(freq.get(5));

        final Collection<Number> col2 = IterableUtils.intersection(iterableC2, iterableA);
        final Map<Number, Integer> freq2 = IterableUtils.getCardinalityMap(col2);
        assertNull(freq2.get(1));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertEquals(Integer.valueOf(3), freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(4));
        assertNull(freq2.get(5));
    }

    public void testIntersectionUsesMethodEquals() {
        // Let elta and eltb be objects...
        final Integer elta = new Integer(17); // Cannot use valueOf here
        final Integer eltb = new Integer(17);

        // ...which are equal...
        assertEquals(elta, eltb);
        assertEquals(eltb, elta);

        // ...but not the same (==).
        assertTrue(elta != eltb);

        // Let cola and colb be collections...
        final Collection<Number> cola = new ArrayList<Number>();
        final Collection<Integer> colb = new ArrayList<Integer>();

        // ...which contain elta and eltb,
        // respectively.
        cola.add(elta);
        colb.add(eltb);

        // Then the intersection of the two
        // should contain one element.
        final Collection<Number> intersection = IterableUtils.intersection(cola, colb);
        assertEquals(1, intersection.size());

        // In practice, this element will be the same (==) as elta
        // or eltb, although this isn't strictly part of the
        // contract.
        final Object eltc = intersection.iterator().next();
        assertTrue(eltc == elta && eltc != eltb || eltc != elta && eltc == eltb);

        // In any event, this element remains equal,
        // to both elta and eltb.
        assertEquals(elta, eltc);
        assertEquals(eltc, elta);
        assertEquals(eltb, eltc);
        assertEquals(eltc, eltb);
    }

    public void testDisjunction() {
        final Collection<Integer> col = IterableUtils.disjunction(iterableA, iterableC);
        final Map<Integer, Integer> freq = IterableUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq.get(1));
        assertEquals(Integer.valueOf(2), freq.get(2));
        assertNull(freq.get(3));
        assertEquals(Integer.valueOf(2), freq.get(4));
        assertEquals(Integer.valueOf(1), freq.get(5));

        final Collection<Number> col2 = IterableUtils.disjunction(iterableC2, iterableA);
        final Map<Number, Integer> freq2 = IterableUtils.getCardinalityMap(col2);
        assertEquals(Integer.valueOf(1), freq2.get(1));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertNull(freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(4));
        assertEquals(Integer.valueOf(1), freq2.get(5));
    }

    public void testSubtract() {
        final Collection<Integer> col = IterableUtils.subtract(iterableA, iterableC);
        final Map<Integer, Integer> freq = IterableUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq.get(1));
        assertNull(freq.get(2));
        assertNull(freq.get(3));
        assertEquals(Integer.valueOf(2), freq.get(4));
        assertNull(freq.get(5));

        final Collection<Number> col2 = IterableUtils.subtract(iterableC2, iterableA);
        final Map<Number, Integer> freq2 = IterableUtils.getCardinalityMap(col2);
        assertEquals(Integer.valueOf(1), freq2.get(5));
        assertNull(freq2.get(4));
        assertNull(freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertNull(freq2.get(1));
    }

    public void testSubtractWithPredicate() {
        // greater than 3
        final Predicate<Number> predicate = new Predicate<Number>() {
            public boolean evaluate(final Number n) {
                return n.longValue() > 3L;
            }
        };

        final Collection<Number> col = IterableUtils.subtract(iterableA, iterableC, predicate);
        final Map<Number, Integer> freq2 = IterableUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq2.get(1));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertEquals(Integer.valueOf(3), freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(4));
        assertNull(freq2.get(5));
    }

    public void testCardinality() {
        assertEquals(1, IterableUtils.cardinality(1, iterableA));
        assertEquals(2, IterableUtils.cardinality(2, iterableA));
        assertEquals(3, IterableUtils.cardinality(3, iterableA));
        assertEquals(4, IterableUtils.cardinality(4, iterableA));
        assertEquals(0, IterableUtils.cardinality(5, iterableA));

        assertEquals(0, IterableUtils.cardinality(1L, iterableB));
        assertEquals(4, IterableUtils.cardinality(2L, iterableB));
        assertEquals(3, IterableUtils.cardinality(3L, iterableB));
        assertEquals(2, IterableUtils.cardinality(4L, iterableB));
        assertEquals(1, IterableUtils.cardinality(5L, iterableB));

        // Ensure that generic bounds accept valid parameters, but return
        // expected results
        // e.g. no longs in the "int" Iterable<Number>, and vice versa.
        assertEquals(0, IterableUtils.cardinality(2L, iterableA2));
        assertEquals(0, IterableUtils.cardinality(2, iterableB2));

        final Set<String> set = new HashSet<String>();
        set.add("A");
        set.add("C");
        set.add("E");
        set.add("E");
        assertEquals(1, IterableUtils.cardinality("A", set));
        assertEquals(0, IterableUtils.cardinality("B", set));
        assertEquals(1, IterableUtils.cardinality("C", set));
        assertEquals(0, IterableUtils.cardinality("D", set));
        assertEquals(1, IterableUtils.cardinality("E", set));

        final Bag<String> bag = new HashBag<String>();
        bag.add("A", 3);
        bag.add("C");
        bag.add("E");
        bag.add("E");
        assertEquals(3, IterableUtils.cardinality("A", bag));
        assertEquals(0, IterableUtils.cardinality("B", bag));
        assertEquals(1, IterableUtils.cardinality("C", bag));
        assertEquals(0, IterableUtils.cardinality("D", bag));
        assertEquals(2, IterableUtils.cardinality("E", bag));
    }

    public void testCardinalityOfNull() {
        final List<String> list = new ArrayList<String>();
        assertEquals(0, IterableUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = IterableUtils.getCardinalityMap(list);
            assertNull(freq.get(null));
        }
        list.add("A");
        assertEquals(0, IterableUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = IterableUtils.getCardinalityMap(list);
            assertNull(freq.get(null));
        }
        list.add(null);
        assertEquals(1, IterableUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = IterableUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(1), freq.get(null));
        }
        list.add("B");
        assertEquals(1, IterableUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = IterableUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(1), freq.get(null));
        }
        list.add(null);
        assertEquals(2, IterableUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = IterableUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(2), freq.get(null));
        }
        list.add("B");
        assertEquals(2, IterableUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = IterableUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(2), freq.get(null));
        }
        list.add(null);
        assertEquals(3, IterableUtils.cardinality(null, list));
        {
            final Map<String, Integer> freq = IterableUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(3), freq.get(null));
        }
    }

    public void testGetCardinalityMap() {
        final Map<Number, Integer> freqA = IterableUtils.<Number> getCardinalityMap(iterableA);
        assertEquals(1, (int) freqA.get(1));
        assertEquals(2, (int) freqA.get(2));
        assertEquals(3, (int) freqA.get(3));
        assertEquals(4, (int) freqA.get(4));
        assertNull(freqA.get(5));

        final Map<Long, Integer> freqB = IterableUtils.getCardinalityMap(iterableB);
        assertNull(freqB.get(1L));
        assertEquals(4, (int) freqB.get(2L));
        assertEquals(3, (int) freqB.get(3L));
        assertEquals(2, (int) freqB.get(4L));
        assertEquals(1, (int) freqB.get(5L));
    }

    public void testFind() {
        Predicate<Number> testPredicate = equalPredicate((Number) 4);
        Integer test = IterableUtils.find(iterableA, testPredicate);
        assertTrue(test.equals(4));
        testPredicate = equalPredicate((Number) 45);
        test = IterableUtils.find(iterableA, testPredicate);
        assertTrue(test == null);
        assertNull(IterableUtils.find(null, testPredicate));
        assertNull(IterableUtils.find(iterableA, null));
    }

    public void testForAllDoCollection() {
        final Closure<List<? extends Number>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<? extends Number>> col = new ArrayList<List<? extends Number>>();
        col.add((List<? extends Number>) iterableA);
        col.add((List<? extends Number>) iterableB);
        Closure<List<? extends Number>> resultClosure = IterableUtils.forAllDo(col, testClosure);
        assertSame(testClosure, resultClosure);
        assertTrue(((Collection<Integer>) iterableA).isEmpty() && ((Collection<Long>) iterableB).isEmpty());
        // fix for various java 1.6 versions: keep the cast
        resultClosure = IterableUtils.forAllDo(col, (Closure<List<? extends Number>>) null);
        assertNull(resultClosure);
        assertTrue(((Collection<Integer>) iterableA).isEmpty() && ((Collection<Long>) iterableB).isEmpty());
        resultClosure = IterableUtils.forAllDo(null, testClosure);
        col.add(null);
        // null should be OK
        IterableUtils.forAllDo(col, testClosure);
    }

    public void testForAllDoFailure() {
        try {
            final Closure<String> testClosure = ClosureUtils.invokerClosure("clear");
            final Collection<String> col = new ArrayList<String>();
            col.add("x");
            IterableUtils.forAllDo(col, testClosure);
            fail("expecting FunctorException");
        } catch (FunctorException fe) {
        } // this is what we want
    }

    public void testForAllButLastDoCollection() {
        final Closure<List<? extends Number>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<? extends Number>> col = new ArrayList<List<? extends Number>>();
        col.add((List<? extends Number>) iterableA);
        col.add((List<? extends Number>) iterableB);
        List<? extends Number> lastElement = IterableUtils.forAllButLastDo(col, testClosure);
        assertSame(lastElement, iterableB);
        assertTrue(((List<Integer>) iterableA).isEmpty() && !((List<Long>) iterableB).isEmpty());

        col.clear();
        col.add((List<? extends Number>) iterableB);
        lastElement = IterableUtils.forAllButLastDo(col, testClosure);
        assertSame(lastElement, iterableB);
        assertTrue(!((List<Long>) iterableB).isEmpty());

        col.clear();
        lastElement = IterableUtils.forAllButLastDo(col, testClosure);
        assertNull(lastElement);

        Collection<String> strings = Arrays.asList("a", "b", "c");
        final StringBuffer result = new StringBuffer();
        result.append(IterableUtils.forAllButLastDo(strings, new Closure<String>() {
            public void execute(String input) {
                result.append(input + ";");
            }
        }));
        assertEquals("a;b;c", result.toString());

        Collection<String> oneString = Arrays.asList("a");
        final StringBuffer resultOne = new StringBuffer();
        resultOne.append(IterableUtils.forAllButLastDo(oneString, new Closure<String>() {
            public void execute(String input) {
                resultOne.append(input + ";");
            }
        }));
        assertEquals("a", resultOne.toString());
        assertNull(IterableUtils.forAllButLastDo(strings, (Closure<String>) null)); // do not remove cast
        assertNull(IterableUtils.forAllButLastDo((Collection<String>) null, (Closure<String>) null)); // do not remove cast
    }

    public void testFilter() {
        final List<Integer> ints = new ArrayList<Integer>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        ints.add(3);
        final Iterable<Integer> iterable = ints;
        assertTrue(IterableUtils.filter(iterable, EQUALS_TWO));
        assertEquals(1, ints.size());
        assertEquals(2, (int) ints.get(0));
    }

    public void testFilterNullParameters() throws Exception {
        final List<Long> longs = Collections.nCopies(4, 10L);
        assertFalse(IterableUtils.filter(longs, null));
        assertEquals(4, longs.size());
        assertFalse(IterableUtils.filter(null, EQUALS_TWO));
        assertEquals(4, longs.size());
        assertFalse(IterableUtils.filter(null, null));
        assertEquals(4, longs.size());
    }

    public void testFilterInverse() {
        final List<Integer> ints = new ArrayList<Integer>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        ints.add(3);
        final Iterable<Integer> iterable = ints;
        assertTrue(IterableUtils.filterInverse(iterable, EQUALS_TWO));
        assertEquals(3, ints.size());
        assertEquals(1, (int) ints.get(0));
        assertEquals(3, (int) ints.get(1));
        assertEquals(3, (int) ints.get(2));
    }

    public void testFilterInverseNullParameters() throws Exception {
        final List<Long> longs = Collections.nCopies(4, 10L);
        assertFalse(IterableUtils.filterInverse(longs, null));
        assertEquals(4, longs.size());
        assertFalse(IterableUtils.filterInverse(null, EQUALS_TWO));
        assertEquals(4, longs.size());
        assertFalse(IterableUtils.filterInverse(null, null));
        assertEquals(4, longs.size());
    }

    public void testCountMatches() {
        assertEquals(4, IterableUtils.countMatches(iterableB, EQUALS_TWO));
        assertEquals(0, IterableUtils.countMatches(iterableA, null));
        assertEquals(0, IterableUtils.countMatches(null, EQUALS_TWO));
        assertEquals(0, IterableUtils.countMatches(null, null));
    }

    public void testExists() {
        final List<Integer> list = new ArrayList<Integer>();
        assertFalse(IterableUtils.exists(null, null));
        assertFalse(IterableUtils.exists(list, null));
        assertFalse(IterableUtils.exists(null, EQUALS_TWO));
        assertFalse(IterableUtils.exists(list, EQUALS_TWO));
        list.add(1);
        list.add(3);
        list.add(4);
        assertFalse(IterableUtils.exists(list, EQUALS_TWO));

        list.add(2);
        assertEquals(true, IterableUtils.exists(list, EQUALS_TWO));
    }

    public void testMatchesAll() {
        assertFalse(IterableUtils.matchesAll(null, null));
        assertFalse(IterableUtils.matchesAll(iterableA, null));

        Predicate<Integer> lessThanFive = new Predicate<Integer>() {
            public boolean evaluate(Integer object) {
                return object < 5;
            }
        };
        assertTrue(IterableUtils.matchesAll(iterableA, lessThanFive));

        Predicate<Integer> lessThanFour = new Predicate<Integer>() {
            public boolean evaluate(Integer object) {
                return object < 4;
            }
        };
        assertFalse(IterableUtils.matchesAll(iterableA, lessThanFour));

        assertTrue(IterableUtils.matchesAll(null, lessThanFour));
        assertTrue(IterableUtils.matchesAll(emptyIterable, lessThanFour));
    }

    public void testSelect() {
        final List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        // Ensure that the collection is the input type or a super type
        final Collection<Integer> output1 = IterableUtils.select(list, EQUALS_TWO);
        final Collection<Number> output2 = IterableUtils.<Number> select(list, EQUALS_TWO);
        final HashSet<Number> output3 = IterableUtils.select(list, EQUALS_TWO, new HashSet<Number>());
        assertTrue(IterableUtils.isEqualIterable(output1, output3));
        assertEquals(4, list.size());
        assertEquals(1, output1.size());
        assertEquals(2, output2.iterator().next());
    }

    public void testSelectRejected() {
        final List<Long> list = new ArrayList<Long>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        list.add(4L);
        final Collection<Long> output1 = IterableUtils.selectRejected(list, EQUALS_TWO);
        final Collection<? extends Number> output2 = IterableUtils.selectRejected(list, EQUALS_TWO);
        final HashSet<Number> output3 = IterableUtils.selectRejected(list, EQUALS_TWO, new HashSet<Number>());
        assertTrue(IterableUtils.isEqualIterable(output1, output2));
        assertTrue(IterableUtils.isEqualIterable(output1, output3));
        assertEquals(4, list.size());
        assertEquals(3, output1.size());
        assertTrue(output1.contains(1L));
        assertTrue(output1.contains(3L));
        assertTrue(output1.contains(4L));
    }

    @SuppressWarnings("unchecked")
    public void testPartition() {
        List<Integer> input = new ArrayList<Integer>();
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
        Integer[] expected = { 1, 3, 4 };
        partition = partitions.get(1);
        Assert.assertArrayEquals(expected, partition.toArray());

        partitions = IterableUtils.partition((List<Integer>) null, EQUALS_TWO);
        assertTrue(partitions.isEmpty());

        partitions = IterableUtils.partition(input);
        assertEquals(1, partitions.size());
        assertEquals(input, partitions.get(0));
    }

    public void testPartitionWithOutputCollections() {
        List<Integer> input = new ArrayList<Integer>();
        input.add(1);
        input.add(2);
        input.add(3);
        input.add(4);

        List<Integer> output = new ArrayList<Integer>();
        List<Integer> rejected = new ArrayList<Integer>();

        IterableUtils.partition(input, EQUALS_TWO, output, rejected);

        // output contains 2
        assertEquals(1, output.size());
        assertEquals(2, CollectionUtils.extractSingleton(output).intValue());

        // rejected contains 1, 3, and 4
        Integer[] expected = { 1, 3, 4 };
        Assert.assertArrayEquals(expected, rejected.toArray());

        output.clear();
        rejected.clear();
        IterableUtils.partition((List<Integer>) null, EQUALS_TWO, output, rejected);
        assertTrue(output.isEmpty());
        assertTrue(rejected.isEmpty());
    }

    public void testPartitionMultiplePredicates() {
        List<Integer> input = new ArrayList<Integer>();
        input.add(1);
        input.add(2);
        input.add(3);
        input.add(4);
        @SuppressWarnings("unchecked")
        List<List<Integer>> partitions = IterableUtils.partition(input, EQUALS_TWO, EVEN);

        // first partition contains 2
        Collection<Integer> partition = partitions.get(0);
        assertEquals(1, partition.size());
        assertEquals(2, partition.iterator().next().intValue());

        // second partition contains 4
        partition = partitions.get(1);
        assertEquals(1, partition.size());
        assertEquals(4, partition.iterator().next().intValue());

        // third partition contains 1 and 3
        Integer[] expected = { 1, 3 };
        partition = partitions.get(2);
        Assert.assertArrayEquals(expected, partition.toArray());
    }

    public void testCollect() {
        Collection<Integer> collectionA = (Collection<Integer>) iterableA;
        Collection<Long> collectionB = (Collection<Long>) iterableB;
        final Transformer<Number, Long> transformer = TransformerUtils.constantTransformer(2L);
        Collection<Number> collection = IterableUtils.<Integer, Number> collect(iterableA, transformer);
        assertTrue(collection.size() == collectionA.size());
        assertCollectResult(collection);

        ArrayList<Number> list;
        list = IterableUtils.collect(collectionA, transformer, new ArrayList<Number>());
        assertTrue(list.size() == collectionA.size());
        assertCollectResult(list);

        Iterator<Integer> iterator = null;
        list = IteratorUtils.collect(iterator, transformer, new ArrayList<Number>());

        iterator = iterableA.iterator();
        list = IteratorUtils.collect(iterator, transformer, list);
        assertTrue(collection.size() == collectionA.size());
        assertCollectResult(collection);

        iterator = collectionA.iterator();
        collection = IteratorUtils.<Integer, Number> collect(iterator, transformer);
        assertTrue(collection.size() == collectionA.size());
        assertTrue(collection.contains(2L) && !collection.contains(1));
        collection = IteratorUtils.collect((Iterator<Integer>) null, (Transformer<Integer, Number>) null);
        assertTrue(collection.size() == 0);

        final int size = collectionA.size();
        collectionB = IterableUtils.collect((Collection<Integer>) null, transformer, collectionB);
        assertTrue(collectionA.size() == size && collectionA.contains(1));
        IterableUtils.collect(collectionB, null, collectionA);
        assertTrue(collectionA.size() == size && collectionA.contains(1));

    }

    public void testIsEqualCollectionEquator() {
        final Collection<Integer> collB = IterableUtils.collect(iterableB, TRANSFORM_TO_INTEGER);

        // odd / even equator
        final Equator<Integer> e = new Equator<Integer>() {
            public boolean equate(final Integer o1, final Integer o2) {
                if (o1.intValue() % 2 == 0 ^ o2.intValue() % 2 == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            public int hash(final Integer o) {
                return o.intValue() % 2 == 0 ? Integer.valueOf(0).hashCode() : Integer.valueOf(1).hashCode();
            }
        };

        assertTrue(IterableUtils.isEqualIterable(iterableA, iterableA, e));
        assertTrue(IterableUtils.isEqualIterable(iterableA, collB, e));
        assertTrue(IterableUtils.isEqualIterable(collB, iterableA, e));

        final Equator<Number> defaultEquator = DefaultEquator.defaultEquator();
        assertFalse(IterableUtils.isEqualIterable(iterableA, iterableB, defaultEquator));
        assertFalse(IterableUtils.isEqualIterable(iterableA, collB, defaultEquator));
    }
    
    public void testGetFromIterable() throws Exception {
        // Collection, entry exists
        final Bag<String> bag = new HashBag<String>();
        bag.add("element", 1);
        assertEquals("element", IterableUtils.get(bag, 0));

        // Collection, non-existent entry
        try {
            IterableUtils.get(bag, 1);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ioobe) {
        } //this is what we want
    }
    
    public void testGetFromList() throws Exception {
        // List, entry exists
        final List<String> list = new ArrayList<String>();
        list.add("zero");
        list.add("one");
        final String string = IterableUtils.get(list, 0);
        assertEquals("zero", string);
        assertEquals("one", IterableUtils.get(list, 1));
        // list, non-existent entry -- IndexOutOfBoundsException
        try {
            IterableUtils.get(new ArrayList<Object>(), 2);
            fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ioobe) {
        } // this is what we want
    }

    public void testCollateException1() {
        try {
            IterableUtils.collate(iterableA, null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
        } // this is what we want
    }

    public void testCollateException2() {
        try {
            IterableUtils.collate(iterableA, iterableC, null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
        } // this is what we want
    }

    public void testCollate() {
        List<Integer> result = IterableUtils.collate(emptyIterable, emptyIterable);
        assertEquals("Merge empty with empty", 0, result.size());

        result = IterableUtils.collate(iterableA, emptyIterable);
        assertEquals("Merge empty with non-empty", iterableA, result);

        List<Integer> result1 = IterableUtils.collate(iterableD, iterableE);
        List<Integer> result2 = IterableUtils.collate(iterableE, iterableD);
        assertEquals("Merge two lists 1", result1, result2);

        List<Integer> combinedList = new ArrayList<Integer>();
        combinedList.addAll((Collection<? extends Integer>) iterableD);
        combinedList.addAll((Collection<? extends Integer>) iterableE);
        Collections.sort(combinedList);

        assertEquals("Merge two lists 2", combinedList, result2);

        final Comparator<Integer> reverseComparator = ComparatorUtils.reversedComparator(ComparatorUtils
                .<Integer> naturalComparator());

        result = IterableUtils.collate(emptyIterable, emptyIterable, reverseComparator);
        assertEquals("Comparator Merge empty with empty", 0, result.size());

        Collections.reverse((List<Integer>) iterableD);
        Collections.reverse((List<Integer>) iterableE);
        Collections.reverse(combinedList);

        result1 = IterableUtils.collate(iterableD, iterableE, reverseComparator);
        result2 = IterableUtils.collate(iterableE, iterableD, reverseComparator);
        assertEquals("Comparator Merge two lists 1", result1, result2);
        assertEquals("Comparator Merge two lists 2", combinedList, result2);
    }

    public void testCollateIgnoreDuplicates() {
        List<Integer> result1 = IterableUtils.collate(iterableD, iterableE, false);
        List<Integer> result2 = IterableUtils.collate(iterableE, iterableD, false);
        assertEquals("Merge two lists 1 - ignore duplicates", result1, result2);

        Set<Integer> combinedSet = new HashSet<Integer>();
        combinedSet.addAll((Collection<? extends Integer>) iterableD);
        combinedSet.addAll((Collection<? extends Integer>) iterableE);
        List<Integer> combinedList = new ArrayList<Integer>(combinedSet);
        Collections.sort(combinedList);

        assertEquals("Merge two lists 2 - ignore duplicates", combinedList, result2);
    }

    public void testRetainAllWithEquator() {
        final List<String> base = new ArrayList<String>();
        base.add("AC");
        base.add("BB");
        base.add("CA");

        final List<String> retain = new ArrayList<String>();
        retain.add("AA");
        retain.add("CX");
        retain.add("XZ");

        // use an equator which compares the second letter only
        final Collection<String> result = IterableUtils.retainAll(base, retain, new Equator<String>() {

            public boolean equate(String o1, String o2) {
                return o1.charAt(1) == o2.charAt(1);
            }

            public int hash(String o) {
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

        try {
            IterableUtils.retainAll(null, null, null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
        } // this is what we want

        try {
            IterableUtils.retainAll(base, retain, null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
        } // this is what we want
    }

    public void testRemoveAllWithEquator() {
        final List<String> base = new ArrayList<String>();
        base.add("AC");
        base.add("BB");
        base.add("CA");

        final List<String> remove = new ArrayList<String>();
        remove.add("AA");
        remove.add("CX");
        remove.add("XZ");

        // use an equator which compares the second letter only
        final Collection<String> result = IterableUtils.removeAll(base, remove, new Equator<String>() {

            public boolean equate(String o1, String o2) {
                return o1.charAt(1) == o2.charAt(1);
            }

            public int hash(String o) {
                return o.charAt(1);
            }
        });

        assertEquals(2, result.size());
        assertTrue(result.contains("AC"));
        assertTrue(result.contains("BB"));
        assertFalse(result.contains("CA"));
        assertEquals(3, base.size());
        assertEquals(true, base.contains("AC"));
        assertEquals(true, base.contains("BB"));
        assertEquals(true, base.contains("CA"));
        assertEquals(3, remove.size());
        assertEquals(true, remove.contains("AA"));
        assertEquals(true, remove.contains("CX"));
        assertEquals(true, remove.contains("XZ"));

        try {
            IterableUtils.removeAll(null, null, DefaultEquator.defaultEquator());
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
        } // this is what we want

        try {
            IterableUtils.removeAll(base, remove, null);
            fail("expecting NullPointerException");
        } catch (final NullPointerException npe) {
        } // this is what we want
    }

    public void testToString(){
        String result = IterableUtils.toString(iterableA);
        assertEquals("[1,2,2,3,3,3,4,4,4,4]", result);
    }
    
    public void testToStringEmptyIterable(){
        String result = IterableUtils.toString(new ArrayList<Integer>());
        assertEquals("[]", result);
    }
    
    public void testToStringNullIterable(){
        try{
            IterableUtils.toString(null);
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringTransformer(){
        String result = IterableUtils.toString(iterableA, new Transformer<Integer, String>() {
            public String transform(Integer input) {
                return new Integer(input * 2).toString();
            }
        });
        assertEquals("[2,4,4,6,6,6,8,8,8,8]", result);
    }
    
    public void testToStringTransformerEmptyIterable(){
        String result = IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
            public String transform(Integer input) {
                fail("not supposed to reach here");
                return "";
            }
        });
        assertEquals("[]", result);
    }
    
    public void testToStringTransformerNullIterable(){
        try{
            IterableUtils.toString(null, new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            });
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiter(){
        
        Transformer<Integer, String> transformer = new Transformer<Integer, String>() {
            public String transform(Integer input) {
                return new Integer(input * 2).toString();
            }
        };
        
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
        
    }
    
    public void testToStringDelimiterEmptyIterable(){
        
        Transformer<Integer, String> transformer = new Transformer<Integer, String>() {
            public String transform(Integer input) {
                fail("not supposed to reach here");
                return "";
            }
        };
        
        String result = IterableUtils.toString(new ArrayList<Integer>(), transformer, "", "(", ")");
        assertEquals("()", result);
        
        result = IterableUtils.toString(new ArrayList<Integer>(), transformer, "", "", "");
        assertEquals("", result);
    }
    
    public void testToStringDelimiterNullIterable(){
        try{
            IterableUtils.toString(null, new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, "", "(", ")");
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiterNullTransformer(){
        try{
            IterableUtils.toString(new ArrayList<Integer>(), null, "", "(", ")");
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiterNullDelimiter(){
        try{
            IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, null, "(", ")");
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiterNullPrefix(){
        try{
            IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, "", null, ")");
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }
    
    public void testToStringDelimiterNullSuffix(){
        try{
            IterableUtils.toString(new ArrayList<Integer>(), new Transformer<Integer, String>() {
                public String transform(Integer input) {
                    fail("not supposed to reach here");
                    return "";
                }
            }, "", "(", null);
            fail("expecting IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
        } // this is what we want
    }

    private void assertCollectResult(final Collection<Number> collection) {
        assertTrue(((ArrayList<Integer>) iterableA).contains(1) && !((ArrayList<Integer>) iterableA).contains(2L));
        assertTrue(collection.contains(2L) && !collection.contains(1));
    }
}
