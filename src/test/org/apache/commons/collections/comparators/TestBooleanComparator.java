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
package org.apache.commons.collections.comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Tests for {@link BooleanComparator}.
 *
 * @version $Revision$ $Date$
 *
 * @author Rodney Waldhoff
 */
public class TestBooleanComparator extends AbstractTestComparator<Boolean> {

    // conventional
    // ------------------------------------------------------------------------

    public TestBooleanComparator(String testName) {
        super(testName);
    }

    // collections testing framework
    // ------------------------------------------------------------------------

    @Override
    public Comparator<Boolean> makeObject() {
        return new BooleanComparator();
    }

    @Override
    public List<Boolean> getComparableObjectsOrdered() {
        List<Boolean> list = new ArrayList<Boolean>();
        list.add(new Boolean(false));
        list.add(Boolean.FALSE);
        list.add(new Boolean(false));
        list.add(Boolean.TRUE);
        list.add(new Boolean(true));
        list.add(true);
        return list;
    }

    @Override
    public String getCompatibilityVersion() {
        return "3";
    }

    // tests
    // ------------------------------------------------------------------------

    public void testConstructors() {
        allTests(false,new BooleanComparator());
        allTests(false,new BooleanComparator(false));
        allTests(true,new BooleanComparator(true));
    }

    public void testStaticFactoryMethods() {
        allTests(false,BooleanComparator.getFalseFirstComparator());
        allTests(false,BooleanComparator.getBooleanComparator(false));
        allTests(true,BooleanComparator.getTrueFirstComparator());
        allTests(true,BooleanComparator.getBooleanComparator(true));
    }

    public void testEqualsCompatibleInstance() {
        assertEquals(new BooleanComparator(),new BooleanComparator(false));
        assertEquals(new BooleanComparator(false),new BooleanComparator(false));
        assertEquals(new BooleanComparator(false),BooleanComparator.getFalseFirstComparator());
        assertSame(BooleanComparator.getFalseFirstComparator(),BooleanComparator.getBooleanComparator(false));

        assertEquals(new BooleanComparator(true),new BooleanComparator(true));
        assertEquals(new BooleanComparator(true),BooleanComparator.getTrueFirstComparator());
        assertSame(BooleanComparator.getTrueFirstComparator(),BooleanComparator.getBooleanComparator(true));

        assertTrue(!(new BooleanComparator().equals(new BooleanComparator(true))));
        assertTrue(!(new BooleanComparator(true).equals(new BooleanComparator(false))));
    }

    // utilities
    // ------------------------------------------------------------------------

    protected void allTests(boolean trueFirst, BooleanComparator comp) {
        orderIndependentTests(comp);
        if(trueFirst) {
            trueFirstTests(comp);
        } else {
            falseFirstTests(comp);
        }
    }

    protected void trueFirstTests(BooleanComparator comp) {
        assertNotNull(comp);
        assertEquals(0,comp.compare(true, true));
        assertEquals(0,comp.compare(false, false));
        assertTrue(comp.compare(false, true) > 0);
        assertTrue(comp.compare(true, false) < 0);
    }

    protected void falseFirstTests(BooleanComparator comp) {
        assertNotNull(comp);
        assertEquals(0,comp.compare(true, true));
        assertEquals(0,comp.compare(false, false));
        assertTrue(comp.compare(false, true) < 0);
        assertTrue(comp.compare(true, false) > 0);
    }

    protected void orderIndependentTests(BooleanComparator comp) {
        nullArgumentTests(comp);
    }

    protected void nullArgumentTests(BooleanComparator comp) {
        assertNotNull(comp);
        try {
            comp.compare(null,null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            comp.compare(Boolean.TRUE,null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            comp.compare(Boolean.FALSE,null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            comp.compare(null,Boolean.TRUE);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            comp.compare(null,Boolean.FALSE);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }

}
