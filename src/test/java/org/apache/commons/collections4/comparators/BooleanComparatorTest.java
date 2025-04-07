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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BooleanComparator}.
 */
@SuppressWarnings("boxing")
public class BooleanComparatorTest extends AbstractComparatorTest<Boolean> {

    protected void allTests(final boolean trueFirst, final BooleanComparator comp) {
        orderIndependentTests(comp);
        if (trueFirst) {
            trueFirstTests(comp);
        } else {
            falseFirstTests(comp);
        }
    }

    protected void falseFirstTests(final BooleanComparator comp) {
        assertNotNull(comp);
        assertEquals(0, comp.compare(true, true));
        assertEquals(0, comp.compare(false, false));
        assertTrue(comp.compare(false, true) < 0);
        assertTrue(comp.compare(true, false) > 0);
    }

    @Override
    public List<Boolean> getComparableObjectsOrdered() {
        return new ArrayList<>(Arrays.asList(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE,
                true));
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(), "src/test/resources/data/test/BooleanComparator.version4.obj");
//    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public Comparator<Boolean> makeObject() {
        return new BooleanComparator();
    }

    protected void nullArgumentTests(final BooleanComparator comp) {
        assertNotNull(comp);
        assertThrows(NullPointerException.class, () -> comp.compare(null, null));
        assertThrows(NullPointerException.class, () -> comp.compare(Boolean.TRUE, null));
        assertThrows(NullPointerException.class, () -> comp.compare(Boolean.FALSE, null));
        assertThrows(NullPointerException.class, () -> comp.compare(null, Boolean.TRUE));
        assertThrows(NullPointerException.class, () -> comp.compare(null, Boolean.FALSE));
    }

    protected void orderIndependentTests(final BooleanComparator comp) {
        nullArgumentTests(comp);
    }

    @Test
    public void testConstructors() {
        allTests(false, new BooleanComparator());
        allTests(false, new BooleanComparator(false));
        allTests(true, new BooleanComparator(true));
    }

    @Test
    public void testEqualsCompatibleInstance() {
        assertEquals(new BooleanComparator(), new BooleanComparator(false));
        assertEquals(new BooleanComparator(false), new BooleanComparator(false));
        assertEquals(new BooleanComparator(false), BooleanComparator.getFalseFirstComparator());
        assertSame(BooleanComparator.getFalseFirstComparator(), BooleanComparator.booleanComparator(false));

        assertEquals(new BooleanComparator(true), new BooleanComparator(true));
        assertEquals(new BooleanComparator(true), BooleanComparator.getTrueFirstComparator());
        assertSame(BooleanComparator.getTrueFirstComparator(), BooleanComparator.booleanComparator(true));

        assertNotEquals(new BooleanComparator(), new BooleanComparator(true));
        assertNotEquals(new BooleanComparator(true), new BooleanComparator(false));
    }

    @Test
    public void testStaticFactoryMethods() {
        allTests(false, BooleanComparator.getFalseFirstComparator());
        allTests(false, BooleanComparator.booleanComparator(false));
        allTests(true, BooleanComparator.getTrueFirstComparator());
        allTests(true, BooleanComparator.booleanComparator(true));
    }

    protected void trueFirstTests(final BooleanComparator comp) {
        assertNotNull(comp);
        assertEquals(0, comp.compare(true, true));
        assertEquals(0, comp.compare(false, false));
        assertTrue(comp.compare(false, true) > 0);
        assertTrue(comp.compare(true, false) < 0);
    }

}
