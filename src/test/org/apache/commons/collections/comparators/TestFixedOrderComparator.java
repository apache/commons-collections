/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/comparators/TestFixedOrderComparator.java,v 1.2 2003/08/31 14:37:48 scolebourne Exp $
 * $Revision: 1.2 $
 * $Date: 2003/08/31 14:37:48 $
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
package org.apache.commons.collections.comparators;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test class for FixedOrderComparator.
 * 
 * @author David Leppik 
 * @author Stephen Colebourne
 */
public class TestFixedOrderComparator extends TestCase {


    /**
     * Top cities of the world, by population including metro areas.
     */
    public static final String topCities[] = new String[] {
        "Tokyo",
        "Mexico City",
        "Mumbai",
        "Sao Paulo",
        "New York",
        "Shanghai",
        "Lagos",
        "Los Angeles",
        "Calcutta",
        "Buenos Aires"
    };

    //
    // Initialization and busywork
    //

    public TestFixedOrderComparator(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestFixedOrderComparator.class);
    }

    public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
    }

    //
    // Set up and tear down
    //



    //
    // The tests
    //

    /** 
     * Tests that the constructor plus add method compares items properly. 
     */
    public void testConstructorPlusAdd() {
        FixedOrderComparator comparator = new FixedOrderComparator();
        for (int i = 0; i < topCities.length; i++) {
            comparator.add(topCities[i]);
        }
        String[] keys = (String[]) topCities.clone();
        assertComparatorYieldsOrder(keys, comparator);
    }

    /** 
     * Tests that the array constructor compares items properly. 
     */
    public void testArrayConstructor() {
        String[] keys = (String[]) topCities.clone();
        String[] topCitiesForTest = (String[]) topCities.clone();
        FixedOrderComparator comparator = new FixedOrderComparator(topCitiesForTest);
        assertComparatorYieldsOrder(keys, comparator);
        // test that changing input after constructor has no effect
        topCitiesForTest[0] = "Brighton";
        assertComparatorYieldsOrder(keys, comparator);
    }

    /**
     * Tests the list constructor. 
     */
    public void testListConstructor() {
        String[] keys = (String[]) topCities.clone();
        List topCitiesForTest = new LinkedList(Arrays.asList(topCities));
        FixedOrderComparator comparator = new FixedOrderComparator(topCitiesForTest);
        assertComparatorYieldsOrder(keys, comparator);
        // test that changing input after constructor has no effect
        topCitiesForTest.set(0, "Brighton");
        assertComparatorYieldsOrder(keys, comparator);
    }

    /**
     * Tests addAsEqual method.
     */
    public void testAddAsEqual() {
        FixedOrderComparator comparator = new FixedOrderComparator(topCities);
        comparator.addAsEqual("New York", "Minneapolis");
        assertEquals(0, comparator.compare("New York", "Minneapolis"));
        assertEquals(-1, comparator.compare("Tokyo", "Minneapolis"));
        assertEquals(1, comparator.compare("Shanghai", "Minneapolis"));
    }

    /** 
     * Tests whether or not updates are disabled after a comparison is made.
     */
    public void testLock() {
        FixedOrderComparator comparator = new FixedOrderComparator(topCities);
        assertEquals(false, comparator.isLocked());
        comparator.compare("New York", "Tokyo");
        assertEquals(true, comparator.isLocked());
        try {
            comparator.add("Minneapolis");
            fail("Should have thrown an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // success -- ignore
        }

        try {
            comparator.addAsEqual("New York", "Minneapolis");
            fail("Should have thrown an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // success -- ignore
        }
    }

    public void testUnknownObjectBehavior() {
        FixedOrderComparator comparator = new FixedOrderComparator(topCities);
        try {
            comparator.compare("New York", "Minneapolis");
            fail("Should have thrown a IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success-- ignore
        }
        try {
            comparator.compare("Minneapolis", "New York");
            fail("Should have thrown a IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success-- ignore
        }
        assertEquals(FixedOrderComparator.UNKNOWN_THROW_EXCEPTION, comparator.getUnknownObjectBehavior());

        comparator = new FixedOrderComparator(topCities);
        comparator.setUnknownObjectBehavior(FixedOrderComparator.UNKNOWN_BEFORE);
        assertEquals(FixedOrderComparator.UNKNOWN_BEFORE, comparator.getUnknownObjectBehavior());
        LinkedList keys = new LinkedList(Arrays.asList(topCities));
        keys.addFirst("Minneapolis");
        assertComparatorYieldsOrder(keys.toArray(new String[0]), comparator);
        
        assertEquals(-1, comparator.compare("Minneapolis", "New York"));
        assertEquals( 1, comparator.compare("New York", "Minneapolis"));
        assertEquals( 0, comparator.compare("Minneapolis", "St Paul"));

        comparator = new FixedOrderComparator(topCities);
        comparator.setUnknownObjectBehavior(FixedOrderComparator.UNKNOWN_AFTER);
        keys = new LinkedList(Arrays.asList(topCities));
        keys.add("Minneapolis");
        assertComparatorYieldsOrder(keys.toArray(new String[0]), comparator);
        
        assertEquals( 1, comparator.compare("Minneapolis", "New York"));
        assertEquals(-1, comparator.compare("New York", "Minneapolis"));
        assertEquals( 0, comparator.compare("Minneapolis", "St Paul"));
        
    }
    
    //
    // Helper methods
    //
    
    /** Shuffles the keys and asserts that the comparator sorts them back to
     * their original order.
     */
    private void assertComparatorYieldsOrder(Object[] orderedObjects, 
                                             Comparator comparator) {
        Object[] keys = (Object[]) orderedObjects.clone();
        
        // shuffle until the order changes.  It's extremely rare that
        // this requires more than one shuffle.

        boolean isInNewOrder = false;
        while (keys.length > 1 && isInNewOrder == false) {
            shuffle: {
                Random rand = new Random();
                for (int i = keys.length-1; i > 0; i--) {
                        Object swap = keys[i];
                        int j = rand.nextInt(i+1);
                        keys[i] = keys[j];
                        keys[j] = swap;     
                    }
            }
        
            testShuffle: {
                for (int i = 0; i < keys.length && !isInNewOrder; i++) {
                    if( !orderedObjects[i].equals(keys[i])) {
                        isInNewOrder = true;
                    }
                }
            }
        }
        
        // The real test:  sort and make sure they come out right.
        
        Arrays.sort(keys, comparator);

        for (int i = 0; i < orderedObjects.length; i++) {
            assertEquals(orderedObjects[i], keys[i]);
        }
    }
    
}
