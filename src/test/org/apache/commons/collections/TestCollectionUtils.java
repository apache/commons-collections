/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestCollectionUtils.java,v 1.1 2001/04/24 18:48:38 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/24 18:48:38 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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

import junit.framework.*;
import java.util.*;

/**
 * @author Rodney Waldhoff
 * @version $Id: TestCollectionUtils.java,v 1.1 2001/04/24 18:48:38 rwaldhoff Exp $
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
        assert(null == freq.get("e"));

        freq = CollectionUtils.getCardinalityMap(_b);
        assert(null == freq.get("a"));
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
        assert(null == freq.get("a"));
        assertEquals(new Integer(2),freq.get("b"));
        assertEquals(new Integer(3),freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assert(null == freq.get("e"));

        Collection col2 = CollectionUtils.intersection(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assert(null == freq2.get("a"));
        assertEquals(new Integer(2),freq2.get("b"));
        assertEquals(new Integer(3),freq2.get("c"));
        assertEquals(new Integer(2),freq2.get("d"));
        assert(null == freq2.get("e"));
    }

    public void testDisjunction() {
        Collection col = CollectionUtils.disjunction(_a,_b);
        Map freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(new Integer(1),freq.get("a"));
        assertEquals(new Integer(2),freq.get("b"));
        assert(null == freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assertEquals(new Integer(1),freq.get("e"));

        Collection col2 = CollectionUtils.disjunction(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(new Integer(1),freq2.get("a"));
        assertEquals(new Integer(2),freq2.get("b"));
        assert(null == freq2.get("c"));
        assertEquals(new Integer(2),freq2.get("d"));
        assertEquals(new Integer(1),freq2.get("e"));
    }

    public void testDisjunctionAsUnionMinusIntersection() {
        Collection dis = CollectionUtils.disjunction(_a,_b);
        Collection un = CollectionUtils.union(_a,_b);
        Collection inter = CollectionUtils.intersection(_a,_b);
        assert(CollectionUtils.isEqualCollection(dis,CollectionUtils.subtract(un,inter)));
    }

    public void testDisjunctionAsSymmetricDifference() {
        Collection dis = CollectionUtils.disjunction(_a,_b);
        Collection amb = CollectionUtils.subtract(_a,_b);
        Collection bma = CollectionUtils.subtract(_b,_a);
        assert(CollectionUtils.isEqualCollection(dis,CollectionUtils.union(amb,bma)));
    }

    public void testSubtract() {
        Collection col = CollectionUtils.subtract(_a,_b);
        Map freq = CollectionUtils.getCardinalityMap(col);
        assertEquals(new Integer(1),freq.get("a"));
        assert(null == freq.get("b"));
        assert(null == freq.get("c"));
        assertEquals(new Integer(2),freq.get("d"));
        assert(null == freq.get("e"));

        Collection col2 = CollectionUtils.subtract(_b,_a);
        Map freq2 = CollectionUtils.getCardinalityMap(col2);
        assertEquals(new Integer(1),freq2.get("e"));
        assert(null == freq2.get("d"));
        assert(null == freq2.get("c"));
        assertEquals(new Integer(2),freq2.get("b"));
        assert(null == freq2.get("a"));
    }

    public void testIsSubCollectionOfSelf() {
        assert(CollectionUtils.isSubCollection(_a,_a));
        assert(CollectionUtils.isSubCollection(_b,_b));
    }

    public void testIsSubCollection() {
        assert(!CollectionUtils.isSubCollection(_a,_b));
        assert(!CollectionUtils.isSubCollection(_b,_a));
    }

    public void testIsSubCollection2() {
        Collection c = new ArrayList();
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("a");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("b");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("b");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("c");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("c");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("c");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("d");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("d");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("d");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(!CollectionUtils.isSubCollection(_a,c));
        c.add("d");
        assert(CollectionUtils.isSubCollection(c,_a));
        assert(CollectionUtils.isSubCollection(_a,c));
        c.add("e");
        assert(!CollectionUtils.isSubCollection(c,_a));
        assert(CollectionUtils.isSubCollection(_a,c));
    }

    public void testIsEqualCollectionToSelf() {
        assert(CollectionUtils.isEqualCollection(_a,_a));
        assert(CollectionUtils.isEqualCollection(_b,_b));
    }

    public void testIsEqualCollection() {
        assert(!CollectionUtils.isEqualCollection(_a,_b));
        assert(!CollectionUtils.isEqualCollection(_b,_a));
    }

    public void testIsEqualCollection2() {
        Collection a = new ArrayList();
        Collection b = new ArrayList();
        assert(CollectionUtils.isEqualCollection(a,b));
        assert(CollectionUtils.isEqualCollection(b,a));
        a.add("1");
        assert(!CollectionUtils.isEqualCollection(a,b));
        assert(!CollectionUtils.isEqualCollection(b,a));
        b.add("1");
        assert(CollectionUtils.isEqualCollection(a,b));
        assert(CollectionUtils.isEqualCollection(b,a));
        a.add("2");
        assert(!CollectionUtils.isEqualCollection(a,b));
        assert(!CollectionUtils.isEqualCollection(b,a));
        b.add("2");
        assert(CollectionUtils.isEqualCollection(a,b));
        assert(CollectionUtils.isEqualCollection(b,a));
        a.add("1");
        assert(!CollectionUtils.isEqualCollection(a,b));
        assert(!CollectionUtils.isEqualCollection(b,a));
        b.add("1");
        assert(CollectionUtils.isEqualCollection(a,b));
        assert(CollectionUtils.isEqualCollection(b,a));
    }
}
