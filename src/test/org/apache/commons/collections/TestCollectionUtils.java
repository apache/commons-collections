/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import junit.framework.*;
import java.util.*;

/**
 * @author Rodney Waldhoff
 * @version $Id: TestCollectionUtils.java,v 1.6.2.1 2004/05/22 12:14:05 scolebourne Exp $
 */
public class TestCollectionUtils extends TestCase {
    public TestCollectionUtils(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestCollectionUtils.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestCollectionUtils.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    private Collection _a = null;
    private Collection _b = null;

    public void setUp() {
        _a = new ArrayList();
        _a.add("a");
        _a.add("b");
        _a.add("b");
        _a.add("c");
        _a.add("c");
        _a.add("c");
        _a.add("d");
        _a.add("d");
        _a.add("d");
        _a.add("d");
        _b = new LinkedList();
        _b.add("e");
        _b.add("d");
        _b.add("d");
        _b.add("c");
        _b.add("c");
        _b.add("c");
        _b.add("b");
        _b.add("b");
        _b.add("b");
        _b.add("b");

    }

    public void testGetCardinalityMap() {
        Map freq = CollectionUtils.getCardinalityMap(_a);
        assertEquals(new Integer(1),freq.get("a"));
        assertEquals(new Integer(2),freq.get("b"));
        assertEquals(new Integer(3),freq.get("c"));
        assertEquals(new Integer(4),freq.get("d"));
        assertTrue(null == freq.get("e"));

        freq = CollectionUtils.getCardinalityMap(_b);
        assertTrue(null == freq.get("a"));
        assertEquals(new Integer(4),freq.get("b"));
        assertEquals(new Integer(3),freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assertEquals(new Integer(1),freq.get("e"));
    }

    public void testCardinality() {
        assertEquals(1,CollectionUtils.cardinality("a",_a));
        assertEquals(2,CollectionUtils.cardinality("b",_a));
        assertEquals(3,CollectionUtils.cardinality("c",_a));
        assertEquals(4,CollectionUtils.cardinality("d",_a));
        assertEquals(0,CollectionUtils.cardinality("e",_a));

        assertEquals(0,CollectionUtils.cardinality("a",_b));
        assertEquals(4,CollectionUtils.cardinality("b",_b));
        assertEquals(3,CollectionUtils.cardinality("c",_b));
        assertEquals(2,CollectionUtils.cardinality("d",_b));
        assertEquals(1,CollectionUtils.cardinality("e",_b));
    }

    public void testContainsAny() {
        Collection empty = new ArrayList(0);
        Collection one = new ArrayList(1);
        one.add("1");
        Collection two = new ArrayList(1);
        two.add("2");
        Collection three = new ArrayList(1);
        three.add("3");
        Collection odds = new ArrayList(2);
        odds.add("1");
        odds.add("3");
        
        assertTrue("containsAny({1},{1,3}) should return true.",
            CollectionUtils.containsAny(one,odds));
        assertTrue("containsAny({1,3},{1}) should return true.",
            CollectionUtils.containsAny(odds,one));
        assertTrue("containsAny({3},{1,3}) should return true.",
            CollectionUtils.containsAny(three,odds));
        assertTrue("containsAny({1,3},{3}) should return true.",
            CollectionUtils.containsAny(odds,three));
        assertTrue("containsAny({2},{2}) should return true.",
            CollectionUtils.containsAny(two,two));
        assertTrue("containsAny({1,3},{1,3}) should return true.",
            CollectionUtils.containsAny(odds,odds));
        
        assertTrue("containsAny({2},{1,3}) should return false.",
            !CollectionUtils.containsAny(two,odds));
        assertTrue("containsAny({1,3},{2}) should return false.",
            !CollectionUtils.containsAny(odds,two));
        assertTrue("containsAny({1},{3}) should return false.",
            !CollectionUtils.containsAny(one,three));
        assertTrue("containsAny({3},{1}) should return false.",
            !CollectionUtils.containsAny(three,one));
        assertTrue("containsAny({1,3},{}) should return false.",
            !CollectionUtils.containsAny(odds,empty));
        assertTrue("containsAny({},{1,3}) should return false.",
            !CollectionUtils.containsAny(empty,odds));
        assertTrue("containsAny({},{}) should return false.",
            !CollectionUtils.containsAny(empty,empty));
    }

    public void testUnion() {
        Collection col = CollectionUtils.union(_a,_b);
        Map freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(new Integer(1),freq.get("a"));
        assertEquals(new Integer(4),freq.get("b"));
        assertEquals(new Integer(3),freq.get("c"));
        assertEquals(new Integer(4),freq.get("d"));
        assertEquals(new Integer(1),freq.get("e"));

        Collection col2 = CollectionUtils.union(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(new Integer(1),freq2.get("a"));
        assertEquals(new Integer(4),freq2.get("b"));
        assertEquals(new Integer(3),freq2.get("c"));
        assertEquals(new Integer(4),freq2.get("d"));
        assertEquals(new Integer(1),freq2.get("e"));
    }

    public void testIntersection() {
        Collection col = CollectionUtils.intersection(_a,_b);
        Map freq = CollectionUtils.getCardinalityMap(col);
        assertTrue(null == freq.get("a"));
        assertEquals(new Integer(2),freq.get("b"));
        assertEquals(new Integer(3),freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assertTrue(null == freq.get("e"));

        Collection col2 = CollectionUtils.intersection(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assertTrue(null == freq2.get("a"));
        assertEquals(new Integer(2),freq2.get("b"));
        assertEquals(new Integer(3),freq2.get("c"));
        assertEquals(new Integer(2),freq2.get("d"));
        assertTrue(null == freq2.get("e"));
    }

    public void testDisjunction() {
        Collection col = CollectionUtils.disjunction(_a,_b);
        Map freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(new Integer(1),freq.get("a"));
        assertEquals(new Integer(2),freq.get("b"));
        assertTrue(null == freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assertEquals(new Integer(1),freq.get("e"));

        Collection col2 = CollectionUtils.disjunction(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(new Integer(1),freq2.get("a"));
        assertEquals(new Integer(2),freq2.get("b"));
        assertTrue(null == freq2.get("c"));
        assertEquals(new Integer(2),freq2.get("d"));
        assertEquals(new Integer(1),freq2.get("e"));
    }

    public void testDisjunctionAsUnionMinusIntersection() {
        Collection dis = CollectionUtils.disjunction(_a,_b);
        Collection un = CollectionUtils.union(_a,_b);
        Collection inter = CollectionUtils.intersection(_a,_b);
        assertTrue(CollectionUtils.isEqualCollection(dis,CollectionUtils.subtract(un,inter)));
    }

    public void testDisjunctionAsSymmetricDifference() {
        Collection dis = CollectionUtils.disjunction(_a,_b);
        Collection amb = CollectionUtils.subtract(_a,_b);
        Collection bma = CollectionUtils.subtract(_b,_a);
        assertTrue(CollectionUtils.isEqualCollection(dis,CollectionUtils.union(amb,bma)));
    }

    public void testSubtract() {
        Collection col = CollectionUtils.subtract(_a,_b);
        Map freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(new Integer(1),freq.get("a"));
        assertTrue(null == freq.get("b"));
        assertTrue(null == freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assertTrue(null == freq.get("e"));

        Collection col2 = CollectionUtils.subtract(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(new Integer(1),freq2.get("e"));
        assertTrue(null == freq2.get("d"));
        assertTrue(null == freq2.get("c"));
        assertEquals(new Integer(2),freq2.get("b"));
        assertTrue(null == freq2.get("a"));
    }

    public void testIsSubCollectionOfSelf() {
        assertTrue(CollectionUtils.isSubCollection(_a,_a));
        assertTrue(CollectionUtils.isSubCollection(_b,_b));
    }

    public void testIsSubCollection() {
        assertTrue(!CollectionUtils.isSubCollection(_a,_b));
        assertTrue(!CollectionUtils.isSubCollection(_b,_a));
    }

    public void testIsSubCollection2() {
        Collection c = new ArrayList();
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("a");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("b");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("b");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("c");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("c");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("c");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("d");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("d");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("d");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(!CollectionUtils.isSubCollection(_a,c));
        c.add("d");
        assertTrue(CollectionUtils.isSubCollection(c,_a));
        assertTrue(CollectionUtils.isSubCollection(_a,c));
        c.add("e");
        assertTrue(!CollectionUtils.isSubCollection(c,_a));
        assertTrue(CollectionUtils.isSubCollection(_a,c));
    }

    public void testIsEqualCollectionToSelf() {
        assertTrue(CollectionUtils.isEqualCollection(_a,_a));
        assertTrue(CollectionUtils.isEqualCollection(_b,_b));
    }

    public void testIsEqualCollection() {
        assertTrue(!CollectionUtils.isEqualCollection(_a,_b));
        assertTrue(!CollectionUtils.isEqualCollection(_b,_a));
    }

    public void testIsEqualCollection2() {
        Collection a = new ArrayList();
        Collection b = new ArrayList();
        assertTrue(CollectionUtils.isEqualCollection(a,b));
        assertTrue(CollectionUtils.isEqualCollection(b,a));
        a.add("1");
        assertTrue(!CollectionUtils.isEqualCollection(a,b));
        assertTrue(!CollectionUtils.isEqualCollection(b,a));
        b.add("1");
        assertTrue(CollectionUtils.isEqualCollection(a,b));
        assertTrue(CollectionUtils.isEqualCollection(b,a));
        a.add("2");
        assertTrue(!CollectionUtils.isEqualCollection(a,b));
        assertTrue(!CollectionUtils.isEqualCollection(b,a));
        b.add("2");
        assertTrue(CollectionUtils.isEqualCollection(a,b));
        assertTrue(CollectionUtils.isEqualCollection(b,a));
        a.add("1");
        assertTrue(!CollectionUtils.isEqualCollection(a,b));
        assertTrue(!CollectionUtils.isEqualCollection(b,a));
        b.add("1");
        assertTrue(CollectionUtils.isEqualCollection(a,b));
        assertTrue(CollectionUtils.isEqualCollection(b,a));
    }


    public void testIndex() {
        Map map = new HashMap();
        map.put(new Integer(0), "element");
        Object test = CollectionUtils.index(map, 0);
        assertTrue(test.equals("element"));

        List list = new ArrayList();
        list.add("element");
        test = CollectionUtils.index(list, 0);
        assertTrue(test.equals("element"));

        Bag bag = new HashBag();
        bag.add("element", 1);
        test = CollectionUtils.index(bag, 0);
        assertTrue(test.equals("element"));
    }


    public void testFilter() {
        List list = new ArrayList();
        list.add("One");
        list.add("Two");
        list.add("Three");
        list.add("Four");
        CollectionUtils.filter(list, new Predicate() {
            public boolean evaluate(Object input) {
                return (input.equals("Two"));
            }
        });
        assertEquals(1, list.size());
        assertEquals("Two", list.get(0));
    }

    public void testTransform1() {
        List list = new ArrayList();
        list.add("1");
        list.add("2");
        list.add("3");
        CollectionUtils.transform(list, new Transformer() {
            public Object transform(Object input) {
                return new Integer((String) input);
            }
        });
        assertEquals(3, list.size());
        assertEquals(new Integer(1), list.get(0));
        assertEquals(new Integer(2), list.get(1));
        assertEquals(new Integer(3), list.get(2));
    }
    
    public void testTransform2() {
        Set set = new HashSet();
        set.add("1");
        set.add("2");
        set.add("3");
        CollectionUtils.transform(set, new Transformer() {
            public Object transform(Object input) {
                return new Integer(4);
            }
        });
        assertEquals(1, set.size());
        assertEquals(new Integer(4), set.iterator().next());
    }


    public BulkTest bulkTestPredicatedCollection1() {
        return new TestPredicatedCollection("") {
            public Collection predicatedCollection() {
                Predicate p = getPredicate();
                return CollectionUtils.predicatedCollection(new ArrayList(), p);
            }

            public BulkTest bulkTestAll() {
                return new TestCollection("") {
                    public Collection makeCollection() {
                        return predicatedCollection();
                    }

                    public Collection makeConfirmedCollection() {
                        return new ArrayList();
                    }

                    public Collection makeConfirmedFullCollection() {
                        ArrayList list = new ArrayList();
                        list.addAll(java.util.Arrays.asList(getFullElements()));
                        return list;
                    }

                    public Object[] getFullElements() {
                        return getFullNonNullStringElements();
                    }

                    public Object[] getOtherElements() {
                        return getOtherNonNullStringElements();
                    }

                };
            }
        };
    }

}
