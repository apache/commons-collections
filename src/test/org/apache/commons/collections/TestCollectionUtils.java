/*
 * $Id: TestCollectionUtils.java,v 1.17 2003/08/31 17:28:43 scolebourne Exp $
 * $Revision: 1.17 $
 * $Date: 2003/08/31 17:28:43 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for CollectionUtils.
 * 
 * @author Rodney Waldhoff
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 * 
 * @version $Revision: 1.17 $ $Date: 2003/08/31 17:28:43 $
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
        assertNull(freq.get("e"));

        freq = CollectionUtils.getCardinalityMap(_b);
        assertNull(freq.get("a"));
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
    
    public void testCardinalityOfNull() {
        List list = new ArrayList();
        assertEquals(0,CollectionUtils.cardinality(null,list));
        {
            Map freq = CollectionUtils.getCardinalityMap(list);
            assertNull(freq.get(null));
        }
        list.add("A");
        assertEquals(0,CollectionUtils.cardinality(null,list));
        {
            Map freq = CollectionUtils.getCardinalityMap(list);
            assertNull(freq.get(null));
        }
        list.add(null);
        assertEquals(1,CollectionUtils.cardinality(null,list));
        {
            Map freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(new Integer(1),freq.get(null));
        }
        list.add("B");
        assertEquals(1,CollectionUtils.cardinality(null,list));
        {
            Map freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(new Integer(1),freq.get(null));
        }
        list.add(null);
        assertEquals(2,CollectionUtils.cardinality(null,list));
        {
            Map freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(new Integer(2),freq.get(null));
        }
        list.add("B");
        assertEquals(2,CollectionUtils.cardinality(null,list));
        {
            Map freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(new Integer(2),freq.get(null));
        }
        list.add(null);
        assertEquals(3,CollectionUtils.cardinality(null,list));
        {
            Map freq = CollectionUtils.getCardinalityMap(list);
            assertEquals(new Integer(3),freq.get(null));
        }
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
        assertNull(freq.get("a"));
        assertEquals(new Integer(2),freq.get("b"));
        assertEquals(new Integer(3),freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assertNull(freq.get("e"));

        Collection col2 = CollectionUtils.intersection(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assertNull(freq2.get("a"));
        assertEquals(new Integer(2),freq2.get("b"));
        assertEquals(new Integer(3),freq2.get("c"));
        assertEquals(new Integer(2),freq2.get("d"));
        assertNull(freq2.get("e"));
    }

    public void testDisjunction() {
        Collection col = CollectionUtils.disjunction(_a,_b);
        Map freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(new Integer(1),freq.get("a"));
        assertEquals(new Integer(2),freq.get("b"));
        assertNull(freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assertEquals(new Integer(1),freq.get("e"));

        Collection col2 = CollectionUtils.disjunction(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(new Integer(1),freq2.get("a"));
        assertEquals(new Integer(2),freq2.get("b"));
        assertNull(freq2.get("c"));
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
        assertNull(freq.get("b"));
        assertNull(freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assertNull(freq.get("e"));

        Collection col2 = CollectionUtils.subtract(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(new Integer(1),freq2.get("e"));
        assertNull(freq2.get("d"));
        assertNull(freq2.get("c"));
        assertEquals(new Integer(2),freq2.get("b"));
        assertNull(freq2.get("a"));
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


    private static Predicate EQUALS_TWO = new Predicate() {
        public boolean evaluate(Object input) {
            return (input.equals("Two"));
        }
    };
    
    public void testFilter() {
        List list = new ArrayList();
        list.add("One");
        list.add("Two");
        list.add("Three");
        list.add("Four");
        CollectionUtils.filter(list, EQUALS_TWO);
        assertEquals(1, list.size());
        assertEquals("Two", list.get(0));
        
        list = new ArrayList();
        list.add("One");
        list.add("Two");
        list.add("Three");
        list.add("Four");
        CollectionUtils.filter(list, null);
        assertEquals(4, list.size());
        CollectionUtils.filter(null, EQUALS_TWO);
        assertEquals(4, list.size());
        CollectionUtils.filter(null, null);
        assertEquals(4, list.size());
    }

    public void testCountMatches() {
        List list = new ArrayList();
        list.add("One");
        list.add("Two");
        list.add("Three");
        list.add("Four");
        int count = CollectionUtils.countMatches(list, EQUALS_TWO);
        assertEquals(4, list.size());
        assertEquals(1, count);
        assertEquals(0, CollectionUtils.countMatches(list, null));
        assertEquals(0, CollectionUtils.countMatches(null, EQUALS_TWO));
        assertEquals(0, CollectionUtils.countMatches(null, null));
    }

    public void testExists() {
        List list = new ArrayList();
        assertEquals(false, CollectionUtils.exists(null, null));
        assertEquals(false, CollectionUtils.exists(list, null));
        assertEquals(false, CollectionUtils.exists(null, EQUALS_TWO));
        assertEquals(false, CollectionUtils.exists(list, EQUALS_TWO));
        list.add("One");
        list.add("Three");
        list.add("Four");
        assertEquals(false, CollectionUtils.exists(list, EQUALS_TWO));

        list.add("Two");
        assertEquals(true, CollectionUtils.exists(list, EQUALS_TWO));
    }
    
    public void testSelect() {
        List list = new ArrayList();
        list.add("One");
        list.add("Two");
        list.add("Three");
        list.add("Four");
        Collection output = CollectionUtils.select(list, EQUALS_TWO);
        assertEquals(4, list.size());
        assertEquals(1, output.size());
        assertEquals("Two", output.iterator().next());
    }

    public void testSelectRejected() {
        List list = new ArrayList();
        list.add("One");
        list.add("Two");
        list.add("Three");
        list.add("Four");
        Collection output = CollectionUtils.selectRejected(list, EQUALS_TWO);
        assertEquals(4, list.size());
        assertEquals(3, output.size());
        assertTrue(output.contains("One"));
        assertTrue(output.contains("Three"));
        assertTrue(output.contains("Four"));
    }

    Transformer TRANSFORM_TO_INTEGER = new Transformer() {
        public Object transform(Object input) {
            return new Integer((String) input);
        }
    };
    
    public void testTransform1() {
        List list = new ArrayList();
        list.add("1");
        list.add("2");
        list.add("3");
        CollectionUtils.transform(list, TRANSFORM_TO_INTEGER);
        assertEquals(3, list.size());
        assertEquals(new Integer(1), list.get(0));
        assertEquals(new Integer(2), list.get(1));
        assertEquals(new Integer(3), list.get(2));
        
        list = new ArrayList();
        list.add("1");
        list.add("2");
        list.add("3");
        CollectionUtils.transform(null, TRANSFORM_TO_INTEGER);
        assertEquals(3, list.size());
        CollectionUtils.transform(list, null);
        assertEquals(3, list.size());
        CollectionUtils.transform(null, null);
        assertEquals(3, list.size());
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

    public BulkTest bulkTestTypedCollection() {
        return new TestTypedCollection("") {
            public Collection typedCollection() {
                return CollectionUtils.typedCollection(
                    new ArrayList(),
                    super.getType());
            }
 
            public BulkTest bulkTestAll() {
                return new TestCollection("") {
                    public Collection makeCollection() {
                        return typedCollection();
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
    
    public void testIsFull() {
        Set set = new HashSet();
        set.add("1");
        set.add("2");
        set.add("3");
        try {
            CollectionUtils.isFull(null);
            fail();
        } catch (NullPointerException ex) {}
        assertEquals(false, CollectionUtils.isFull(set));
        
        BoundedFifoBuffer buf = new BoundedFifoBuffer(set);
        assertEquals(true, CollectionUtils.isFull(buf));
        buf.remove("2");
        assertEquals(false, CollectionUtils.isFull(buf));
        buf.add("2");
        assertEquals(true, CollectionUtils.isFull(buf));
        
        Buffer buf2 = BufferUtils.synchronizedBuffer(buf);
        assertEquals(true, CollectionUtils.isFull(buf2));
        buf2.remove("2");
        assertEquals(false, CollectionUtils.isFull(buf2));
        buf2.add("2");
        assertEquals(true, CollectionUtils.isFull(buf2));
    }

    public void testMaxSize() {
        Set set = new HashSet();
        set.add("1");
        set.add("2");
        set.add("3");
        try {
            CollectionUtils.maxSize(null);
            fail();
        } catch (NullPointerException ex) {}
        assertEquals(-1, CollectionUtils.maxSize(set));
        
        BoundedFifoBuffer buf = new BoundedFifoBuffer(set);
        assertEquals(3, CollectionUtils.maxSize(buf));
        buf.remove("2");
        assertEquals(3, CollectionUtils.maxSize(buf));
        buf.add("2");
        assertEquals(3, CollectionUtils.maxSize(buf));
        
        Buffer buf2 = BufferUtils.synchronizedBuffer(buf);
        assertEquals(3, CollectionUtils.maxSize(buf2));
        buf2.remove("2");
        assertEquals(3, CollectionUtils.maxSize(buf2));
        buf2.add("2");
        assertEquals(3, CollectionUtils.maxSize(buf2));
    }

    public void testIntersectionUsesMethodEquals() {
        // Let elta and eltb be objects...
        Object elta = new Integer(17);
        Object eltb = new Integer(17);
        
        // ...which are equal...
        assertEquals(elta,eltb);
        assertEquals(eltb,elta);
        
        // ...but not the same (==).
        assertTrue(elta != eltb);
        
        // Let cola and colb be collections...
        Collection cola = new ArrayList();
        Collection colb = new ArrayList();
        
        // ...which contain elta and eltb, 
        // respectively.
        cola.add(elta);
        colb.add(eltb);
        
        // Then the intersection of the two
        // should contain one element.
        Collection intersection = CollectionUtils.intersection(cola,colb);
        assertEquals(1,intersection.size());
        
        // In practice, this element will be the same (==) as elta
        // or eltb, although this isn't strictly part of the
        // contract.
        Object eltc = intersection.iterator().next();
        assertTrue((eltc == elta  && eltc != eltb) || (eltc != elta  && eltc == eltb));
        
        // In any event, this element remains equal,
        // to both elta and eltb.
        assertEquals(elta,eltc);
        assertEquals(eltc,elta);
        assertEquals(eltb,eltc);
        assertEquals(eltc,eltb);
    }
}
