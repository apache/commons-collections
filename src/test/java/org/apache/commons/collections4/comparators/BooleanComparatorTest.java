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
package org.apache.commons.collections4.comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

/**
 * Tests for {@link BooleanComparator}.
 *
 */
@SuppressWarnings("boxing")
public class BooleanComparatorTest extends AbstractComparatorTest<Boolean> {

    // conventional
    // ------------------------------------------------------------------------

    public BooleanComparatorTest(final String testName) {
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
        final List<Boolean> list = new ArrayList<>();
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
        return "4";
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(), "src/test/resources/data/test/BooleanComparator.version4.obj");
//    }

    // tests
    // ------------------------------------------------------------------------

    @Test
    public void testConstructors() {
        allTests(false,new BooleanComparator());
        allTests(false,new BooleanComparator(false));
        allTests(true,new BooleanComparator(true));
    }

    @Test

    public void testStaticFactoryMethods() {
        allTests(false,BooleanComparator.getFalseFirstComparator());
        allTests(false,BooleanComparator.booleanComparator(false));
        allTests(true,BooleanComparator.getTrueFirstComparator());
        allTests(true,BooleanComparator.booleanComparator(true));
    }

    @Test
    public void testEqualsCompatibleInstance() {
        assertEquals(new BooleanComparator(),new BooleanComparator(false));
        assertEquals(new BooleanComparator(false),new BooleanComparator(false));
        assertEquals(new BooleanComparator(false),BooleanComparator.getFalseFirstComparator());
        assertSame(BooleanComparator.getFalseFirstComparator(),BooleanComparator.booleanComparator(false));

        assertEquals(new BooleanComparator(true),new BooleanComparator(true));
        assertEquals(new BooleanComparator(true),BooleanComparator.getTrueFirstComparator());
        assertSame(BooleanComparator.getTrueFirstComparator(),BooleanComparator.booleanComparator(true));

        assertTrue(!new BooleanComparator().equals(new BooleanComparator(true)));
        assertTrue(!new BooleanComparator(true).equals(new BooleanComparator(false)));
    }

    // utilities
    // ------------------------------------------------------------------------

    protected void allTests(final boolean trueFirst, final BooleanComparator comp) {
        orderIndependentTests(comp);
        if (trueFirst) {
            trueFirstTests(comp);
        } else {
            falseFirstTests(comp);
        }
    }

    protected void trueFirstTests(final BooleanComparator comp) {
        assertNotNull(comp);
        assertEquals(0,comp.compare(true, true));
        assertEquals(0,comp.compare(false, false));
        assertTrue(comp.compare(false, true) > 0);
        assertTrue(comp.compare(true, false) < 0);
    }

    protected void falseFirstTests(final BooleanComparator comp) {
        assertNotNull(comp);
        assertEquals(0,comp.compare(true, true));
        assertEquals(0,comp.compare(false, false));
        assertTrue(comp.compare(false, true) < 0);
        assertTrue(comp.compare(true, false) > 0);
    }

    protected void orderIndependentTests(final BooleanComparator comp) {
        nullArgumentTests(comp);
    }

    protected void nullArgumentTests(final BooleanComparator comp) {
        assertNotNull(comp);
        try {
            comp.compare(null,null);
            fail("Expected NullPointerException");
        } catch(final NullPointerException e) {
            // expected
        }
        try {
            comp.compare(Boolean.TRUE,null);
            fail("Expected NullPointerException");
        } catch(final NullPointerException e) {
            // expected
        }
        try {
            comp.compare(Boolean.FALSE,null);
            fail("Expected NullPointerException");
        } catch(final NullPointerException e) {
            // expected
        }
        try {
            comp.compare(null,Boolean.TRUE);
            fail("Expected NullPointerException");
        } catch(final NullPointerException e) {
            // expected
        }
        try {
            comp.compare(null,Boolean.FALSE);
            fail("Expected NullPointerException");
        } catch(final NullPointerException e) {
            // expected
        }
    }

}
