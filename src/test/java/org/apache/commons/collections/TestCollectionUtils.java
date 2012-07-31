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
package org.apache.commons.collections;

import static junit.framework.Assert.assertFalse;
import static org.apache.commons.collections.functors.EqualPredicate.equalPredicate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.*;

import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.collections.buffer.BoundedFifoBuffer;
import org.apache.commons.collections.collection.PredicatedCollection;
import org.apache.commons.collections.collection.SynchronizedCollection;
import org.apache.commons.collections.collection.TransformedCollection;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for CollectionUtils.
 *
 * @author Rodney Waldhoff
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 * @author Phil Steitz
 * @author Steven Melzer
 * @author Neil O'Toole
 * @author Stephen Smith
 * @author Stephen Kestle
 *
 * @version $Revision$
 */
@SuppressWarnings("boxing")
public class TestCollectionUtils extends MockTestCase {

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

    @Before
    public void setUp() {
        collectionA = new ArrayList<Integer>();
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
        collectionB = new LinkedList<Long>();
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

        collectionC = new ArrayList<Integer>();
        for (Long l : collectionB) {
            collectionC.add(l.intValue());
        }

        iterableA = collectionA;
        iterableB = collectionB;
        iterableC = collectionC;
        collectionA2 = new ArrayList<Number>(collectionA);
        collectionB2 = new LinkedList<Number>(collectionB);
        collectionC2 = new LinkedList<Number>(collectionC);
        iterableA2 = collectionA2;
        iterableB2 = collectionB2;
    }

    @Test
    public void getCardinalityMap() {
        Map<Number, Integer> freqA = CollectionUtils.<Number>getCardinalityMap(iterableA);
        assertEquals(1, (int) freqA.get(1));
        assertEquals(2, (int) freqA.get(2));
        assertEquals(3, (int) freqA.get(3));
        assertEquals(4, (int) freqA.get(4));
        assertNull(freqA.get(5));

        Map<Long, Integer> freqB = CollectionUtils.getCardinalityMap(iterableB);
        assertNull(freqB.get(1L));
        assertEquals(4, (int) freqB.get(2L));
        assertEquals(3, (int) freqB.get(3L));
        assertEquals(2, (int) freqB.get(4L));
        assertEquals(1, (int) freqB.get(5L));
    }

    @Test
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

        Set<String> set = new HashSet<String>();
        set.add("A");
        set.add("C");
        set.add("E");
        set.add("E");
        assertEquals(1, CollectionUtils.cardinality("A", set));
        assertEquals(0, CollectionUtils.cardinality("B", set));
        assertEquals(1, CollectionUtils.cardinality("C", set));
        assertEquals(0, CollectionUtils.cardinality("D", set));
        assertEquals(1, CollectionUtils.cardinality("E", set));

        Bag<String> bag = new HashBag<String>();
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
    public void cardinalityOfNull() {
        List<String> list = new ArrayList<String>();
        assertEquals(0, CollectionUtils.cardinality(null, list));
        {
            Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertNull(freq.get(null));
        }
        list.add("A");
        assertEquals(0, CollectionUtils.cardinality(null, list));
        {
            Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertNull(freq.get(null));
        }
        list.add(null);
        assertEquals(1, CollectionUtils.cardinality(null, list));
        {
            Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(1), freq.get(null));
        }
        list.add("B");
        assertEquals(1, CollectionUtils.cardinality(null, list));
        {
            Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(1), freq.get(null));
        }
        list.add(null);
        assertEquals(2, CollectionUtils.cardinality(null, list));
        {
            Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(2), freq.get(null));
        }
        list.add("B");
        assertEquals(2, CollectionUtils.cardinality(null, list));
        {
            Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(2), freq.get(null));
        }
        list.add(null);
        assertEquals(3, CollectionUtils.cardinality(null, list));
        {
            Map<String, Integer> freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(Integer.valueOf(3), freq.get(null));
        }
    }

    @Test
    public void containsAny() {
        Collection<String> empty = new ArrayList<String>(0);
        Collection<String> one = new ArrayList<String>(1);
        one.add("1");
        Collection<String> two = new ArrayList<String>(1);
        two.add("2");
        Collection<String> three = new ArrayList<String>(1);
        three.add("3");
        Collection<String> odds = new ArrayList<String>(2);
        odds.add("1");
        odds.add("3");

        assertTrue("containsAny({1},{1,3}) should return true.", CollectionUtils.containsAny(one, odds));
        assertTrue("containsAny({1,3},{1}) should return true.", CollectionUtils.containsAny(odds, one));
        assertTrue("containsAny({3},{1,3}) should return true.", CollectionUtils.containsAny(three, odds));
        assertTrue("containsAny({1,3},{3}) should return true.", CollectionUtils.containsAny(odds, three));
        assertTrue("containsAny({2},{2}) should return true.", CollectionUtils.containsAny(two, two));
        assertTrue("containsAny({1,3},{1,3}) should return true.", CollectionUtils.containsAny(odds, odds));

        assertTrue("containsAny({2},{1,3}) should return false.", !CollectionUtils.containsAny(two, odds));
        assertTrue("containsAny({1,3},{2}) should return false.", !CollectionUtils.containsAny(odds, two));
        assertTrue("containsAny({1},{3}) should return false.", !CollectionUtils.containsAny(one, three));
        assertTrue("containsAny({3},{1}) should return false.", !CollectionUtils.containsAny(three, one));
        assertTrue("containsAny({1,3},{}) should return false.", !CollectionUtils.containsAny(odds, empty));
        assertTrue("containsAny({},{1,3}) should return false.", !CollectionUtils.containsAny(empty, odds));
        assertTrue("containsAny({},{}) should return false.", !CollectionUtils.containsAny(empty, empty));
    }

    @Test
    public void union() {
        Collection<Integer> col = CollectionUtils.union(iterableA, iterableC);
        Map<Integer, Integer> freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq.get(1));
        assertEquals(Integer.valueOf(4), freq.get(2));
        assertEquals(Integer.valueOf(3), freq.get(3));
        assertEquals(Integer.valueOf(4), freq.get(4));
        assertEquals(Integer.valueOf(1), freq.get(5));

        Collection<Number> col2 = CollectionUtils.union(collectionC2, iterableA);
        Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(Integer.valueOf(1), freq2.get(1));
        assertEquals(Integer.valueOf(4), freq2.get(2));
        assertEquals(Integer.valueOf(3), freq2.get(3));
        assertEquals(Integer.valueOf(4), freq2.get(4));
        assertEquals(Integer.valueOf(1), freq2.get(5));
    }

    @Test
    public void intersection() {
        Collection<Integer> col = CollectionUtils.intersection(iterableA, iterableC);
        Map<Integer, Integer> freq = CollectionUtils.getCardinalityMap(col);
        assertNull(freq.get(1));
        assertEquals(Integer.valueOf(2), freq.get(2));
        assertEquals(Integer.valueOf(3), freq.get(3));
        assertEquals(Integer.valueOf(2), freq.get(4));
        assertNull(freq.get(5));

        Collection<Number> col2 = CollectionUtils.intersection(collectionC2, collectionA);
        Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col2);
        assertNull(freq2.get(1));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertEquals(Integer.valueOf(3), freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(4));
        assertNull(freq2.get(5));
    }

    @Test
    public void disjunction() {
        Collection<Integer> col = CollectionUtils.disjunction(iterableA, iterableC);
        Map<Integer, Integer> freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq.get(1));
        assertEquals(Integer.valueOf(2), freq.get(2));
        assertNull(freq.get(3));
        assertEquals(Integer.valueOf(2), freq.get(4));
        assertEquals(Integer.valueOf(1), freq.get(5));

        Collection<Number> col2 = CollectionUtils.disjunction(collectionC2, collectionA);
        Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(Integer.valueOf(1), freq2.get(1));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertNull(freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(4));
        assertEquals(Integer.valueOf(1), freq2.get(5));
    }

    @Test
    public void testDisjunctionAsUnionMinusIntersection() {
        Collection<Number> dis = CollectionUtils.<Number>disjunction(collectionA, collectionC);
        Collection<Number> un = CollectionUtils.<Number>union(collectionA, collectionC);
        Collection<Number> inter = CollectionUtils.<Number>intersection(collectionA, collectionC);
        assertTrue(CollectionUtils.isEqualCollection(dis, CollectionUtils.subtract(un, inter)));
    }

    @Test
    public void testDisjunctionAsSymmetricDifference() {
        Collection<Number> dis = CollectionUtils.<Number>disjunction(collectionA, collectionC);
        Collection<Number> amb = CollectionUtils.<Number>subtract(collectionA, collectionC);
        Collection<Number> bma = CollectionUtils.<Number>subtract(collectionC, collectionA);
        assertTrue(CollectionUtils.isEqualCollection(dis, CollectionUtils.union(amb, bma)));
    }

    @Test
    public void testSubtract() {
        Collection<Integer> col = CollectionUtils.subtract(iterableA, iterableC);
        Map<Integer, Integer> freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(Integer.valueOf(1), freq.get(1));
        assertNull(freq.get(2));
        assertNull(freq.get(3));
        assertEquals(Integer.valueOf(2), freq.get(4));
        assertNull(freq.get(5));

        Collection<Number> col2 = CollectionUtils.subtract(collectionC2, collectionA);
        Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(Integer.valueOf(1), freq2.get(5));
        assertNull(freq2.get(4));
        assertNull(freq2.get(3));
        assertEquals(Integer.valueOf(2), freq2.get(2));
        assertNull(freq2.get(1));
    }

    @Test
    public void testSubtractWithPredicate() {
        // greater than 3
        Predicate<Number> predicate = new Predicate<Number>() {
            public boolean evaluate(Number n) {
                return n.longValue() > 3L;
            }
        };
        
        Collection<Number> col = CollectionUtils.subtract(iterableA, collectionC, predicate);
        Map<Number, Integer> freq2 = CollectionUtils.getCardinalityMap(col);
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
        assertTrue(!CollectionUtils.isSubCollection(collectionA, collectionC));
        assertTrue(!CollectionUtils.isSubCollection(collectionC, collectionA));
    }

    @Test
    public void testIsSubCollection2() {
        Collection<Integer> c = new ArrayList<Integer>();
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(1);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(2);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(2);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(3);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(3);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(3);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(4);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(4);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(4);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(!CollectionUtils.isSubCollection(collectionA, c));
        c.add(4);
        assertTrue(CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(CollectionUtils.isSubCollection(collectionA, c));
        c.add(5);
        assertTrue(!CollectionUtils.isSubCollection(c, collectionA));
        assertTrue(CollectionUtils.isSubCollection(collectionA, c));
    }

    @Test
    public void testIsEqualCollectionToSelf() {
        assertTrue(CollectionUtils.isEqualCollection(collectionA, collectionA));
        assertTrue(CollectionUtils.isEqualCollection(collectionB, collectionB));
    }

    @Test
    public void testIsEqualCollection() {
        assertTrue(!CollectionUtils.isEqualCollection(collectionA, collectionC));
        assertTrue(!CollectionUtils.isEqualCollection(collectionC, collectionA));
    }

    @Test
    public void testIsEqualCollectionReturnsFalse() {
        List<Integer> b = new ArrayList<Integer>(collectionA);
        // remove an extra '2', and add a 5.  This will increase the size of the cardinality
        b.remove(1);
        b.add(5);
        assertFalse(CollectionUtils.isEqualCollection(collectionA, b));
        assertFalse(CollectionUtils.isEqualCollection(b, collectionA));
    }

    @Test
    public void testIsEqualCollection2() {
        Collection<String> a = new ArrayList<String>();
        Collection<String> b = new ArrayList<String>();
        assertTrue(CollectionUtils.isEqualCollection(a, b));
        assertTrue(CollectionUtils.isEqualCollection(b, a));
        a.add("1");
        assertTrue(!CollectionUtils.isEqualCollection(a, b));
        assertTrue(!CollectionUtils.isEqualCollection(b, a));
        b.add("1");
        assertTrue(CollectionUtils.isEqualCollection(a, b));
        assertTrue(CollectionUtils.isEqualCollection(b, a));
        a.add("2");
        assertTrue(!CollectionUtils.isEqualCollection(a, b));
        assertTrue(!CollectionUtils.isEqualCollection(b, a));
        b.add("2");
        assertTrue(CollectionUtils.isEqualCollection(a, b));
        assertTrue(CollectionUtils.isEqualCollection(b, a));
        a.add("1");
        assertTrue(!CollectionUtils.isEqualCollection(a, b));
        assertTrue(!CollectionUtils.isEqualCollection(b, a));
        b.add("1");
        assertTrue(CollectionUtils.isEqualCollection(a, b));
        assertTrue(CollectionUtils.isEqualCollection(b, a));
    }

    @Test
    public void testIsProperSubCollection() {
        Collection<String> a = new ArrayList<String>();
        Collection<String> b = new ArrayList<String>();
        assertTrue(!CollectionUtils.isProperSubCollection(a, b));
        b.add("1");
        assertTrue(CollectionUtils.isProperSubCollection(a, b));
        assertTrue(!CollectionUtils.isProperSubCollection(b, a));
        assertTrue(!CollectionUtils.isProperSubCollection(b, b));
        assertTrue(!CollectionUtils.isProperSubCollection(a, a));
        a.add("1");
        a.add("2");
        b.add("2");
        assertTrue(!CollectionUtils.isProperSubCollection(b, a));
        assertTrue(!CollectionUtils.isProperSubCollection(a, b));
        a.add("1");
        assertTrue(CollectionUtils.isProperSubCollection(b, a));
        assertTrue(CollectionUtils.isProperSubCollection(CollectionUtils.intersection(collectionA, collectionC), collectionA));
        assertTrue(CollectionUtils.isProperSubCollection(CollectionUtils.subtract(a, b), a));
        assertTrue(!CollectionUtils.isProperSubCollection(a, CollectionUtils.subtract(a, b)));
    }

    @Test
    public void find() {
        Predicate<Number> testPredicate = equalPredicate((Number) 4);
        Integer test = CollectionUtils.find(collectionA, testPredicate);
        assertTrue(test.equals(4));
        testPredicate = equalPredicate((Number) 45);
        test = CollectionUtils.find(collectionA, testPredicate);
        assertTrue(test == null);
        assertNull(CollectionUtils.find(null,testPredicate));
        assertNull(CollectionUtils.find(collectionA, null));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void forAllDoCollection() {
        Closure<List<? extends Number>> testClosure = ClosureUtils.invokerClosure("clear");
        Collection<List<? extends Number>> col = new ArrayList<List<? extends Number>>();
        col.add(collectionA);
        col.add(collectionB);
        Closure<List<? extends Number>> resultClosure = CollectionUtils.forAllDo(col, testClosure);
        assertSame(testClosure, resultClosure);
        assertTrue(collectionA.isEmpty() && collectionB.isEmpty());
        resultClosure = CollectionUtils.forAllDo(col, null);
        assertNull(resultClosure);
        assertTrue(collectionA.isEmpty() && collectionB.isEmpty());
        resultClosure = CollectionUtils.forAllDo((Collection) null, testClosure);
        col.add(null);
        // null should be OK
        CollectionUtils.forAllDo(col, testClosure);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void forAllDoIterator() {
        Closure<List<? extends Number>> testClosure = ClosureUtils.invokerClosure("clear");
        Collection<List<? extends Number>> col = new ArrayList<List<? extends Number>>();
        col.add(collectionA);
        col.add(collectionB);
        Closure<List<? extends Number>> resultClosure = CollectionUtils.forAllDo(col.iterator(), testClosure);
        assertSame(testClosure, resultClosure);
        assertTrue(collectionA.isEmpty() && collectionB.isEmpty());
        resultClosure = CollectionUtils.forAllDo(col.iterator(), null);
        assertNull(resultClosure);
        assertTrue(collectionA.isEmpty() && collectionB.isEmpty());
        resultClosure = CollectionUtils.forAllDo((Iterator) null, testClosure);
        col.add(null);
        // null should be OK
        CollectionUtils.forAllDo(col.iterator(), testClosure);
    }
    
    @Test(expected = FunctorException.class)
    public void forAllDoFailure() {
        Closure<String> testClosure = ClosureUtils.invokerClosure("clear");
        Collection<String> col = new ArrayList<String>();
        col.add("x");
        CollectionUtils.forAllDo(col, testClosure);
    }

    @Test
    public void getFromMap() {
        // Unordered map, entries exist
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("zeroKey", "zero");
        expected.put("oneKey", "one");

        Map<String, String> found = new HashMap<String, String>();
        Map.Entry<String, String> entry = CollectionUtils.get(expected, 0);
        found.put(entry.getKey(), entry.getValue());
        entry = CollectionUtils.get(expected, 1);
        found.put(entry.getKey(), entry.getValue());
        assertEquals(expected, found);

        // Map index out of range
        try {
            CollectionUtils.get(expected, 2);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            CollectionUtils.get(expected, -2);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        // Sorted map, entries exist, should respect order
        SortedMap<String, String> map = new TreeMap<String, String>();
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
        List<String> list = createMock(List.class);
        expect(list.get(0)).andReturn("zero");
        expect(list.get(1)).andReturn("one");
        replay();
        String string = CollectionUtils.get(list, 0);
        assertEquals("zero", string);
        assertEquals("one", CollectionUtils.get(list, 1));
        // list, non-existent entry -- IndexOutOfBoundsException
        CollectionUtils.get(new ArrayList<Object>(), 2);
    }

    @Test
    public void getFromIterator() throws Exception {
        // Iterator, entry exists
        Iterator<Integer> iterator = iterableA.iterator();
        assertEquals(1, (int) CollectionUtils.get(iterator, 0));
        iterator = iterableA.iterator();
        assertEquals(2, (int) CollectionUtils.get(iterator, 1));

        // Iterator, non-existent entry
        try {
            CollectionUtils.get(iterator, 10);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        assertTrue(!iterator.hasNext());
    }

    @Test
    public void getFromEnumeration() throws Exception {
        // Enumeration, entry exists
        Vector<String> vector = new Vector<String>();
        vector.addElement("zero");
        vector.addElement("one");
        Enumeration<String> en = vector.elements();
        assertEquals("zero", CollectionUtils.get(en, 0));
        en = vector.elements();
        assertEquals("one", CollectionUtils.get(en, 1));

        // Enumerator, non-existent entry
        try {
            CollectionUtils.get(en, 3);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        assertTrue(!en.hasMoreElements());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFromIterable() throws Exception {
        // Collection, entry exists
        Bag<String> bag = new HashBag<String>();
        bag.add("element", 1);
        assertEquals("element", CollectionUtils.get(bag, 0));

        // Collection, non-existent entry
        CollectionUtils.get(bag, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFromObjectArray() throws Exception {
        // Object array, entry exists
        Object[] objArray = new Object[2];
        objArray[0] = "zero";
        objArray[1] = "one";
        assertEquals("zero", CollectionUtils.get(objArray, 0));
        assertEquals("one", CollectionUtils.get(objArray, 1));

        // Object array, non-existent entry --
        // ArrayIndexOutOfBoundsException
        CollectionUtils.get(objArray, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getFromPrimativeArray() throws Exception {
        // Primitive array, entry exists
        int[] array = new int[2];
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
        Object obj = new Object();
        CollectionUtils.get(obj, 0);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSize_List() {
        List<String> list = null;
        assertEquals(0, CollectionUtils.size(list));
        list = new ArrayList<String>();
        assertEquals(0, CollectionUtils.size(list));
        list.add("a");
        assertEquals(1, CollectionUtils.size(list));
        list.add("b");
        assertEquals(2, CollectionUtils.size(list));
    }

    @Test
    public void testSize_Map() {
        Map<String, String> map = new HashMap<String, String>();
        assertEquals(0, CollectionUtils.size(map));
        map.put("1", "a");
        assertEquals(1, CollectionUtils.size(map));
        map.put("2", "b");
        assertEquals(2, CollectionUtils.size(map));
    }

    @Test
    public void testSize_Array() {
        Object[] objectArray = new Object[0];
        assertEquals(0, CollectionUtils.size(objectArray));

        String[] stringArray = new String[3];
        assertEquals(3, CollectionUtils.size(stringArray));
        stringArray[0] = "a";
        stringArray[1] = "b";
        stringArray[2] = "c";
        assertEquals(3, CollectionUtils.size(stringArray));
    }

    @Test
    public void testSize_PrimitiveArray() {
        int[] intArray = new int[0];
        assertEquals(0, CollectionUtils.size(intArray));

        double[] doubleArray = new double[3];
        assertEquals(3, CollectionUtils.size(doubleArray));
        doubleArray[0] = 0.0d;
        doubleArray[1] = 1.0d;
        doubleArray[2] = 2.5d;
        assertEquals(3, CollectionUtils.size(doubleArray));
    }

    @Test
    public void testSize_Enumeration() {
        Vector<String> list = new Vector<String>();
        assertEquals(0, CollectionUtils.size(list.elements()));
        list.add("a");
        assertEquals(1, CollectionUtils.size(list.elements()));
        list.add("b");
        assertEquals(2, CollectionUtils.size(list.elements()));
    }

    @Test
    public void testSize_Iterator() {
        List<String> list = new ArrayList<String>();
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
        assertEquals(true, CollectionUtils.sizeIsEmpty(null));
    }

    @Test
    public void testSizeIsEmpty_List() {
        List<String> list = new ArrayList<String>();
        assertEquals(true, CollectionUtils.sizeIsEmpty(list));
        list.add("a");
        assertEquals(false, CollectionUtils.sizeIsEmpty(list));
    }

    @Test
    public void testSizeIsEmpty_Map() {
        Map<String, String> map = new HashMap<String, String>();
        assertEquals(true, CollectionUtils.sizeIsEmpty(map));
        map.put("1", "a");
        assertEquals(false, CollectionUtils.sizeIsEmpty(map));
    }

    @Test
    public void testSizeIsEmpty_Array() {
        Object[] objectArray = new Object[0];
        assertEquals(true, CollectionUtils.sizeIsEmpty(objectArray));

        String[] stringArray = new String[3];
        assertEquals(false, CollectionUtils.sizeIsEmpty(stringArray));
        stringArray[0] = "a";
        stringArray[1] = "b";
        stringArray[2] = "c";
        assertEquals(false, CollectionUtils.sizeIsEmpty(stringArray));
    }

    @Test
    public void testSizeIsEmpty_PrimitiveArray() {
        int[] intArray = new int[0];
        assertEquals(true, CollectionUtils.sizeIsEmpty(intArray));

        double[] doubleArray = new double[3];
        assertEquals(false, CollectionUtils.sizeIsEmpty(doubleArray));
        doubleArray[0] = 0.0d;
        doubleArray[1] = 1.0d;
        doubleArray[2] = 2.5d;
        assertEquals(false, CollectionUtils.sizeIsEmpty(doubleArray));
    }

    @Test
    public void testSizeIsEmpty_Enumeration() {
        Vector<String> list = new Vector<String>();
        assertEquals(true, CollectionUtils.sizeIsEmpty(list.elements()));
        list.add("a");
        assertEquals(false, CollectionUtils.sizeIsEmpty(list.elements()));
        Enumeration<String> en = list.elements();
        en.nextElement();
        assertEquals(true, CollectionUtils.sizeIsEmpty(en));
    }

    @Test
    public void testSizeIsEmpty_Iterator() {
        List<String> list = new ArrayList<String>();
        assertEquals(true, CollectionUtils.sizeIsEmpty(list.iterator()));
        list.add("a");
        assertEquals(false, CollectionUtils.sizeIsEmpty(list.iterator()));
        Iterator<String> it = list.iterator();
        it.next();
        assertEquals(true, CollectionUtils.sizeIsEmpty(it));
    }

    @Test
    public void testSizeIsEmpty_Other() {
        try {
            CollectionUtils.sizeIsEmpty("not a list");
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIsEmptyWithEmptyCollection() {
        Collection<Object> coll = new ArrayList<Object>();
        assertEquals(true, CollectionUtils.isEmpty(coll));
    }

    @Test
    public void testIsEmptyWithNonEmptyCollection() {
        Collection<String> coll = new ArrayList<String>();
        coll.add("item");
        assertEquals(false, CollectionUtils.isEmpty(coll));
    }

    @Test
    public void testIsEmptyWithNull() {
        Collection<?> coll = null;
        assertEquals(true, CollectionUtils.isEmpty(coll));
    }

    @Test
    public void testIsNotEmptyWithEmptyCollection() {
        Collection<Object> coll = new ArrayList<Object>();
        assertEquals(false, CollectionUtils.isNotEmpty(coll));
    }

    @Test
    public void testIsNotEmptyWithNonEmptyCollection() {
        Collection<String> coll = new ArrayList<String>();
        coll.add("item");
        assertEquals(true, CollectionUtils.isNotEmpty(coll));
    }

    @Test
    public void testIsNotEmptyWithNull() {
        Collection<?> coll = null;
        assertEquals(false, CollectionUtils.isNotEmpty(coll));
    }

    // -----------------------------------------------------------------------
    private static Predicate<Number> EQUALS_TWO = new Predicate<Number>() {
        public boolean evaluate(Number input) {
            return (input.intValue() == 2);
        }
    };

//Up to here
    @SuppressWarnings("cast")
    @Test
    public void filter() {
        List<Integer> ints = new ArrayList<Integer>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        ints.add(3);
        Iterable<Integer> iterable = ints;
        assertTrue(CollectionUtils.filter(iterable, EQUALS_TWO));
        assertEquals(1, (int) ints.size());
        assertEquals(2, (int) ints.get(0));
    }

    @Test
    public void filterNullParameters() throws Exception {
        List<Long> longs = Collections.nCopies(4, 10L);
        assertFalse(CollectionUtils.filter(longs, null));
        assertEquals(4, longs.size());
        assertFalse(CollectionUtils.filter(null, EQUALS_TWO));
        assertEquals(4, longs.size());
        assertFalse(CollectionUtils.filter(null, null));
        assertEquals(4, longs.size());
    }

    @Test
    public void countMatches() {
        assertEquals(4, CollectionUtils.countMatches(iterableB, EQUALS_TWO));
        assertEquals(0, CollectionUtils.countMatches(iterableA, null));
        assertEquals(0, CollectionUtils.countMatches(null, EQUALS_TWO));
        assertEquals(0, CollectionUtils.countMatches(null, null));
    }

    @Test
    public void exists() {
        List<Integer> list = new ArrayList<Integer>();
        assertFalse(CollectionUtils.exists(null, null));
        assertFalse(CollectionUtils.exists(list, null));
        assertFalse(CollectionUtils.exists(null, EQUALS_TWO));
        assertFalse(CollectionUtils.exists(list, EQUALS_TWO));
        list.add(1);
        list.add(3);
        list.add(4);
        assertFalse(CollectionUtils.exists(list, EQUALS_TWO));

        list.add(2);
        assertEquals(true, CollectionUtils.exists(list, EQUALS_TWO));
    }

    @Test
    public void select() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        // Ensure that the collection is the input type or a super type
        Collection<Integer> output1 = CollectionUtils.select(list, EQUALS_TWO);
        Collection<Number> output2 = CollectionUtils.<Number>select(list, EQUALS_TWO);
        HashSet<Number> output3 = CollectionUtils.select(list, EQUALS_TWO, new HashSet<Number>());
        assertTrue(CollectionUtils.isEqualCollection(output1, output3));
        assertEquals(4, list.size());
        assertEquals(1, output1.size());
        assertEquals(2, output2.iterator().next());
    }

    @Test
    public void selectRejected() {
        List<Long> list = new ArrayList<Long>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        list.add(4L);
        Collection<Long> output1 = CollectionUtils.selectRejected(list, EQUALS_TWO);
        Collection<? extends Number> output2 = CollectionUtils.selectRejected(list, EQUALS_TWO);
        HashSet<Number> output3 = CollectionUtils.selectRejected(list, EQUALS_TWO, new HashSet<Number>());
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
        Transformer<Number, Long> transformer = TransformerUtils.constantTransformer(2L);
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

        int size = collectionA.size();
        collectionB = CollectionUtils.collect((Collection<Integer>) null, transformer, collectionB);
        assertTrue(collectionA.size() == size && collectionA.contains(1));
        CollectionUtils.collect(collectionB, null, collectionA);
        assertTrue(collectionA.size() == size && collectionA.contains(1));

    }

    private void assertCollectResult(Collection<Number> collection) {
        assertTrue(collectionA.contains(1) && !collectionA.contains(2L));
        assertTrue(collection.contains(2L) && !collection.contains(1));
    }

    Transformer<Object, Integer> TRANSFORM_TO_INTEGER = new Transformer<Object, Integer>() {
        public Integer transform(Object input) {
            return new Integer(((Long)input).intValue());
        }
    };

    @Test
    public void transform1() {
        List<Number> list = new ArrayList<Number>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        CollectionUtils.transform(list, TRANSFORM_TO_INTEGER);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));

        list = new ArrayList<Number>();
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
        Set<Number> set = new HashSet<Number>();
        set.add(1L);
        set.add(2L);
        set.add(3L);
        CollectionUtils.transform(set, new Transformer<Object, Integer>() {
            public Integer transform(Object input) {
                return 4;
            }
        });
        assertEquals(1, set.size());
        assertEquals(4, set.iterator().next());
    }

    // -----------------------------------------------------------------------
    @Test
    public void addIgnoreNull() {
        Set<String> set = new HashSet<String>();
        set.add("1");
        set.add("2");
        set.add("3");
        assertFalse(CollectionUtils.addIgnoreNull(set, null));
        assertEquals(3, set.size());
        assertFalse(CollectionUtils.addIgnoreNull(set, "1"));
        assertEquals(3, set.size());
        assertEquals(true, CollectionUtils.addIgnoreNull(set, "4"));
        assertEquals(4, set.size());
        assertEquals(true, set.contains("4"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void predicatedCollection() {
        Predicate<Object> predicate = PredicateUtils.instanceofPredicate(Integer.class);
        Collection<Number> collection = CollectionUtils.predicatedCollection(new ArrayList<Number>(), predicate);
        assertTrue("returned object should be a PredicatedCollection", collection instanceof PredicatedCollection);
        try {
            collection = CollectionUtils.predicatedCollection(new ArrayList<Number>(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            CollectionUtils.predicatedCollection(null, predicate);
            fail("Expecting IllegalArgumentException for null collection.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void isFull() {
        Set<String> set = new HashSet<String>();
        set.add("1");
        set.add("2");
        set.add("3");
        try {
            CollectionUtils.isFull(null);
            fail();
        } catch (NullPointerException ex) {
        }
        assertFalse(CollectionUtils.isFull(set));

        BoundedFifoBuffer<String> buf = new BoundedFifoBuffer<String>(set);
        assertEquals(true, CollectionUtils.isFull(buf));
        buf.remove("2");
        assertFalse(CollectionUtils.isFull(buf));
        buf.add("2");
        assertEquals(true, CollectionUtils.isFull(buf));

        Buffer<String> buf2 = BufferUtils.synchronizedBuffer(buf);
        assertEquals(true, CollectionUtils.isFull(buf2));
        buf2.remove("2");
        assertFalse(CollectionUtils.isFull(buf2));
        buf2.add("2");
        assertEquals(true, CollectionUtils.isFull(buf2));
    }

    @Test
    public void isEmpty() {
        assertFalse(CollectionUtils.isNotEmpty(null));
        assertTrue(CollectionUtils.isNotEmpty(collectionA));
    }

    @Test
    public void maxSize() {
        Set<String> set = new HashSet<String>();
        set.add("1");
        set.add("2");
        set.add("3");
        try {
            CollectionUtils.maxSize(null);
            fail();
        } catch (NullPointerException ex) {
        }
        assertEquals(-1, CollectionUtils.maxSize(set));

        Buffer<String> buf = new BoundedFifoBuffer<String>(set);
        assertEquals(3, CollectionUtils.maxSize(buf));
        buf.remove("2");
        assertEquals(3, CollectionUtils.maxSize(buf));
        buf.add("2");
        assertEquals(3, CollectionUtils.maxSize(buf));

        Buffer<String> buf2 = BufferUtils.synchronizedBuffer(buf);
        assertEquals(3, CollectionUtils.maxSize(buf2));
        buf2.remove("2");
        assertEquals(3, CollectionUtils.maxSize(buf2));
        buf2.add("2");
        assertEquals(3, CollectionUtils.maxSize(buf2));
    }

    @Test
    public void intersectionUsesMethodEquals() {
        // Let elta and eltb be objects...
        Integer elta = new Integer(17);
        Integer eltb = new Integer(17);

        // ...which are equal...
        assertEquals(elta, eltb);
        assertEquals(eltb, elta);

        // ...but not the same (==).
        assertTrue(elta != eltb);

        // Let cola and colb be collections...
        Collection<Number> cola = new ArrayList<Number>();
        Collection<Integer> colb = new ArrayList<Integer>();

        // ...which contain elta and eltb,
        // respectively.
        cola.add(elta);
        colb.add(eltb);

        // Then the intersection of the two
        // should contain one element.
        Collection<Number> intersection = CollectionUtils.intersection(cola, colb);
        assertEquals(1, intersection.size());

        // In practice, this element will be the same (==) as elta
        // or eltb, although this isn't strictly part of the
        // contract.
        Object eltc = intersection.iterator().next();
        assertTrue((eltc == elta && eltc != eltb) || (eltc != elta && eltc == eltb));

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
        List<String> base = new ArrayList<String>();
        base.add("A");
        base.add("B");
        base.add("C");
        List<Object> sub = new ArrayList<Object>();
        sub.add("A");
        sub.add("C");
        sub.add("X");

        Collection<String> result = CollectionUtils.retainAll(base, sub);
        assertEquals(2, result.size());
        assertEquals(true, result.contains("A"));
        assertFalse(result.contains("B"));
        assertEquals(true, result.contains("C"));
        assertEquals(3, base.size());
        assertEquals(true, base.contains("A"));
        assertEquals(true, base.contains("B"));
        assertEquals(true, base.contains("C"));
        assertEquals(3, sub.size());
        assertEquals(true, sub.contains("A"));
        assertEquals(true, sub.contains("C"));
        assertEquals(true, sub.contains("X"));

        try {
            CollectionUtils.retainAll(null, null);
            fail("expecting NullPointerException");
        } catch (NullPointerException npe) {
        } // this is what we want
    }

    @Test
    public void testRemoveAll() {
        List<String> base = new ArrayList<String>();
        base.add("A");
        base.add("B");
        base.add("C");
        List<String> sub = new ArrayList<String>();
        sub.add("A");
        sub.add("C");
        sub.add("X");

        Collection<String> result = CollectionUtils.removeAll(base, sub);
        assertEquals(1, result.size());
        assertFalse(result.contains("A"));
        assertEquals(true, result.contains("B"));
        assertFalse(result.contains("C"));
        assertEquals(3, base.size());
        assertEquals(true, base.contains("A"));
        assertEquals(true, base.contains("B"));
        assertEquals(true, base.contains("C"));
        assertEquals(3, sub.size());
        assertEquals(true, sub.contains("A"));
        assertEquals(true, sub.contains("C"));
        assertEquals(true, sub.contains("X"));

        try {
            CollectionUtils.removeAll(null, null);
            fail("expecting NullPointerException");
        } catch (NullPointerException npe) {
        } // this is what we want
    }

    // -----------------------------------------------------------------------
    @Test
    public void testTransformedCollection() {
        Transformer<Object, Object> transformer = TransformerUtils.nopTransformer();
        Collection<Object> collection = CollectionUtils.transformingCollection(new ArrayList<Object>(), transformer);
        assertTrue("returned object should be a TransformedCollection", collection instanceof TransformedCollection);
        try {
            collection = CollectionUtils.transformingCollection(new ArrayList<Object>(), null);
            fail("Expecting IllegalArgumentException for null transformer.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            collection = CollectionUtils.transformingCollection(null, transformer);
            fail("Expecting IllegalArgumentException for null collection.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testTransformedCollection_2() {
        List<Object> list = new ArrayList<Object>();
        list.add("1");
        list.add("2");
        list.add("3");
        Collection<Object> result = CollectionUtils.transformingCollection(list, TRANSFORM_TO_INTEGER);
        assertEquals(true, result.contains("1")); // untransformed
        assertEquals(true, result.contains("2")); // untransformed
        assertEquals(true, result.contains("3")); // untransformed
    }

    @Test
    public void testSynchronizedCollection() {
        Collection<Object> col = CollectionUtils.synchronizedCollection(new ArrayList<Object>());
        assertTrue("Returned object should be a SynchronizedCollection.", col instanceof SynchronizedCollection);
        try {
            col = CollectionUtils.synchronizedCollection(null);
            fail("Expecting IllegalArgumentException for null collection.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testUnmodifiableCollection() {
        Collection<Object> col = CollectionUtils.unmodifiableCollection(new ArrayList<Object>());
        assertTrue("Returned object should be a UnmodifiableCollection.", col instanceof UnmodifiableCollection);
        try {
            col = CollectionUtils.unmodifiableCollection(null);
            fail("Expecting IllegalArgumentException for null collection.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void emptyCollection() throws Exception {
        Collection<Number> coll = CollectionUtils.emptyCollection();
        assertEquals(CollectionUtils.EMPTY_COLLECTION, coll);
    }

    /**
     * This test ensures that {@link Iterable}s are supported by {@link CollectionUtils}.
     * Specifically, it uses mocks to ensure that if the passed in
     * {@link Iterable} is a {@link Collection} then
     * {@link Collection#addAll(Collection)} is called instead of iterating.
     */
    @Test
    public void addAllForIterable() {
        Collection<Integer> inputCollection = createMock(Collection.class);
        Iterable<Integer> inputIterable = inputCollection;
        Iterable<Long> iterable = createMock(Iterable.class);
        Iterator<Long> iterator = createMock(Iterator.class);
        Collection<Number> c = createMock(Collection.class);

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
        Hashtable<Integer, Integer> h = new Hashtable<Integer, Integer>();
        h.put(5, 5);
        Enumeration<? extends Integer> enumeration = h.keys();
        CollectionUtils.addAll(collectionA, enumeration);
        assertTrue(collectionA.contains(5));
    }

    @Test
    public void addAllForElements() {
        CollectionUtils.addAll(collectionA, new Integer[]{5});
        assertTrue(collectionA.contains(5));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void getNegative() {
        CollectionUtils.get((Object)collectionA, -3);
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void getPositiveOutOfBounds() {
        CollectionUtils.get((Object)collectionA.iterator(), 30);
    }

    @Test(expected=IllegalArgumentException.class)
    public void get1() {
        CollectionUtils.get((Object)null, 0);
    }

    @Test
    public void get() {
        assertEquals(2, CollectionUtils.get((Object)collectionA, 2));
        assertEquals(2, CollectionUtils.get((Object)collectionA.iterator(), 2));
        Map<Integer, Integer> map = CollectionUtils.getCardinalityMap(collectionA);
        assertEquals(map.entrySet().iterator().next(), CollectionUtils.get(
                (Object)map, 0));
    }

    /**
     * TODO: Should {@link CollectionUtils} be able to be extended? If it is extended, subclasses must 'override' the static methods with
     * call-throughs anyhow, otherwise java compiler warnings will result
     */
    @Test
    public void ensureCollectionUtilsCanBeExtended() {
        new CollectionUtils() {};
    }

    @Test
    public void reverse() {
        CollectionUtils.reverseArray(new Object[] {});
        Integer[] a = collectionA.toArray(new Integer[collectionA.size()]);
        CollectionUtils.reverseArray(a);
        // assume our implementation is correct if it returns the same order as the Java function
        Collections.reverse(collectionA);
        assertEquals(collectionA, Arrays.asList(a));
    }

    @Test
    public void extractSingleton() {
        ArrayList<String> coll = null;
        try {
            CollectionUtils.extractSingleton(coll);
            fail("expected IllegalArgumentException from extractSingleton(null)");
        } catch (IllegalArgumentException e) {
        }
        coll = new ArrayList<String>();
        try {
            CollectionUtils.extractSingleton(coll);
            fail("expected IllegalArgumentException from extractSingleton(empty)");
        } catch (IllegalArgumentException e) {
        }
        coll.add("foo");
        assertEquals("foo", CollectionUtils.extractSingleton(coll));
        coll.add("bar");
        try {
            CollectionUtils.extractSingleton(coll);
            fail("expected IllegalArgumentException from extractSingleton(size == 2)");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Records the next object returned for a mock iterator
     */
    private <T> void next(Iterator<T> iterator, T t) {
        expect(iterator.hasNext()).andReturn(true);
        expect(iterator.next()).andReturn(t);
    }
}
