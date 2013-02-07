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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import junit.framework.Test;

import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.list.PredicatedList;

/**
 * Tests for ListUtils.
 *
 * @version $Id$
 */
public class ListUtilsTest extends BulkTest {

    private static final String a = "a";
    private static final String b = "b";
    private static final String c = "c";
    private static final String d = "d";
    private static final String e = "e";
    private static final String x = "x";

    private String[] fullArray;
    private List<String> fullList;

    public ListUtilsTest(final String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(ListUtilsTest.class);
    }

    @Override
    public void setUp() {
        fullArray = new String[]{a, b, c, d, e};
        fullList = new ArrayList<String>(Arrays.asList(fullArray));
    }

    public void testNothing() {
    }

    /**
     * Tests intersecting a non-empty list with an empty list.
     */
    public void testIntersectNonEmptyWithEmptyList() {
        final List<String> empty = Collections.<String>emptyList();
        assertTrue("result not empty", ListUtils.intersection(empty, fullList).isEmpty());
    }

    /**
     * Tests intersecting a non-empty list with an empty list.
     */
    public void testIntersectEmptyWithEmptyList() {
        final List<?> empty = Collections.EMPTY_LIST;
        assertTrue("result not empty", ListUtils.intersection(empty, empty).isEmpty());
    }

    /**
     * Tests intersecting a non-empty list with an subset of iteself.
     */
    public void testIntersectNonEmptySubset() {
        // create a copy
        final List<String> other = new ArrayList<String>(fullList);

        // remove a few items
        assertNotNull(other.remove(0));
        assertNotNull(other.remove(1));

        // make sure the intersection is equal to the copy
        assertEquals(other, ListUtils.intersection(fullList, other));
    }

    /**
     * Tests intersecting a non-empty list with an subset of iteself.
     */
    public void testIntersectListWithNoOverlapAndDifferentTypes() {
        @SuppressWarnings("boxing")
        final List<Integer> other = Arrays.asList(1, 23);
        assertTrue(ListUtils.intersection(fullList, other).isEmpty());
    }

    /**
     * Tests intersecting a non-empty list with iteself.
     */
    public void testIntersectListWithSelf() {
        assertEquals(fullList, ListUtils.intersection(fullList, fullList));
    }

    /**
     * Tests intersecting two lists in different orders.
     */
    public void testIntersectionOrderInsensitivity() {
        final List<String> one = new ArrayList<String>();
        final List<String> two = new ArrayList<String>();
        one.add("a");
        one.add("b");
        two.add("a");
        two.add("a");
        two.add("b");
        two.add("b");
        assertEquals(ListUtils.intersection(one,two),ListUtils.intersection(two, one));
    }

    public void testPredicatedList() {
        final Predicate<Object> predicate = new Predicate<Object>() {
            public boolean evaluate(final Object o) {
                return o instanceof String;
            }
        };
        List<Object> list = ListUtils.predicatedList(new ArrayStack<Object>(), predicate);
        assertTrue("returned object should be a PredicatedList", list instanceof PredicatedList);
        try {
            list = ListUtils.predicatedList(new ArrayStack<Object>(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
        try {
            list = ListUtils.predicatedList(null, predicate);
            fail("Expecting IllegalArgumentException for null list.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testLazyList() {
        final List<Integer> list = ListUtils.lazyList(new ArrayList<Integer>(), new Factory<Integer>() {

            private int index;

            public Integer create() {
                index++;
                return new Integer(index);
            }
        });

        assertNotNull(list.get(5));
        assertEquals(6, list.size());

        assertNotNull(list.get(5));
        assertEquals(6, list.size());
    }

    public void testEmptyIfNull() {
        assertTrue(ListUtils.emptyIfNull(null).isEmpty());
        
        final List<Long> list = new ArrayList<Long>();
        assertSame(list, ListUtils.emptyIfNull(list));
    }
    
    public void testEquals() {
        final Collection<String> data = Arrays.asList( new String[] { "a", "b", "c" });

        final List<String> a = new ArrayList<String>( data );
        final List<String> b = new ArrayList<String>( data );

        assertEquals(true, a.equals(b));
        assertEquals(true, ListUtils.isEqualList(a, b));
        a.clear();
        assertEquals(false, ListUtils.isEqualList(a, b));
        assertEquals(false, ListUtils.isEqualList(a, null));
        assertEquals(false, ListUtils.isEqualList(null, b));
        assertEquals(true, ListUtils.isEqualList(null, null));
    }

    public void testHashCode() {
        final Collection<String> data = Arrays.asList( new String[] { "a", "b", "c" });

        final List<String> a = new ArrayList<String>(data);
        final List<String> b = new ArrayList<String>(data);

        assertEquals(true, a.hashCode() == b.hashCode());
        assertEquals(true, a.hashCode() == ListUtils.hashCodeForList(a));
        assertEquals(true, b.hashCode() == ListUtils.hashCodeForList(b));
        assertEquals(true, ListUtils.hashCodeForList(a) == ListUtils.hashCodeForList(b));
        a.clear();
        assertEquals(false, ListUtils.hashCodeForList(a) == ListUtils.hashCodeForList(b));
        assertEquals(0, ListUtils.hashCodeForList(null));
    }

    public void testRetainAll() {
        final List<String> sub = new ArrayList<String>();
        sub.add(a);
        sub.add(b);
        sub.add(x);

        final List<String> retained = ListUtils.retainAll(fullList, sub);
        assertTrue(retained.size() == 2);
        sub.remove(x);
        assertTrue(retained.equals(sub));
        fullList.retainAll(sub);
        assertTrue(retained.equals(fullList));

        try {
            ListUtils.retainAll(null, null);
            fail("expecting NullPointerException");
        } catch(final NullPointerException npe){} // this is what we want
    }

    public void testRemoveAll() {
        final List<String> sub = new ArrayList<String>();
        sub.add(a);
        sub.add(b);
        sub.add(x);

        final List<String> remainder = ListUtils.removeAll(fullList, sub);
        assertTrue(remainder.size() == 3);
        fullList.removeAll(sub);
        assertTrue(remainder.equals(fullList));

        try {
            ListUtils.removeAll(null, null);
            fail("expecting NullPointerException");
        } catch(final NullPointerException npe) {} // this is what we want
    }

    public void testSubtract() {
        final List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(b);
        list.add(a);
        list.add(x);

        final List<String> sub = new ArrayList<String>();
        sub.add(a);

        final List<String> result = ListUtils.subtract(list, sub);
        assertTrue(result.size() == 3);
        
        final List<String> expected = new ArrayList<String>();
        expected.add(b);
        expected.add(a);
        expected.add(x);

        assertEquals(expected, result);
        
        try {
            ListUtils.subtract(list, null);
            fail("expecting NullPointerException");
        } catch(final NullPointerException npe) {} // this is what we want
    }

    public void testSubtractNullElement() {
        final List<String> list = new ArrayList<String>();
        list.add(a);
        list.add(null);
        list.add(null);
        list.add(x);

        final List<String> sub = new ArrayList<String>();
        sub.add(null);

        final List<String> result = ListUtils.subtract(list, sub);
        assertTrue(result.size() == 3);
        
        final List<String> expected = new ArrayList<String>();
        expected.add(a);
        expected.add(null);
        expected.add(x);

        assertEquals(expected, result);
    }

    /**
     * Tests the <code>indexOf</code> method in <code>ListUtils</code> class..
     */
    public void testIndexOf() {
        Predicate<String> testPredicate = EqualPredicate.equalPredicate("d");
        int index = ListUtils.indexOf(fullList, testPredicate);
        assertEquals(d, fullList.get(index));

        testPredicate = EqualPredicate.equalPredicate("de");
        index = ListUtils.indexOf(fullList, testPredicate);
        assertEquals(index, -1);
        
        assertEquals(ListUtils.indexOf(null,testPredicate), -1);
        assertEquals(ListUtils.indexOf(fullList, null), -1);
    }
    
    public void testPartition() {
        final List<Integer> strings = new ArrayList<Integer>();
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
        } catch (final IllegalArgumentException e) {}
        
        try {
            ListUtils.partition(strings, 0);
            fail("failed to check for size argument");
        } catch (final IllegalArgumentException e) {}
        
        try {
            ListUtils.partition(strings, -10);
            fail("failed to check for size argument");
        } catch (final IllegalArgumentException e) {}
        
    }
    
    private static Predicate<Number> EQUALS_TWO = new Predicate<Number>() {
        public boolean evaluate(final Number input) {
            return input.intValue() == 2;
        }
    };

    public void testSelect() {
        final List<Integer> list = new ArrayList<Integer>();
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

    public void testSelectRejected() {
        final List<Long> list = new ArrayList<Long>();
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
