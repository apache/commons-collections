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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

/**
 * Tests ComparatorUtils.
 */
public class ComparatorUtilsTest {

    @Test
    public void booleanComparator() {
        Comparator<Boolean> comp = ComparatorUtils.booleanComparator(true);
        assertTrue(comp.compare(Boolean.TRUE, Boolean.FALSE) < 0);
        assertEquals(0, comp.compare(Boolean.TRUE, Boolean.TRUE));
        assertTrue(comp.compare(Boolean.FALSE, Boolean.TRUE) > 0);

        comp = ComparatorUtils.booleanComparator(false);
        assertTrue(comp.compare(Boolean.TRUE, Boolean.FALSE) > 0);
        assertEquals(0, comp.compare(Boolean.TRUE, Boolean.TRUE));
        assertTrue(comp.compare(Boolean.FALSE, Boolean.TRUE) < 0);
    }

    @Test
    public void chainedComparator() {
        // simple test: chain 2 natural comparators
        final Comparator<Integer> comp = ComparatorUtils.chainedComparator(ComparatorUtils.<Integer>naturalComparator(),
                ComparatorUtils.naturalComparator());
        assertTrue(comp.compare(1, 2) < 0);
        assertEquals(0, comp.compare(1, 1));
        assertTrue(comp.compare(2, 1) > 0);
    }

    @Test
    public void max() {
        final Comparator<Integer> reversed =
                ComparatorUtils.reversedComparator(ComparatorUtils.<Integer>naturalComparator());

        assertEquals(Integer.valueOf(10), ComparatorUtils.max(1, 10, null));
        assertEquals(Integer.valueOf(10), ComparatorUtils.max(10, -10, null));

        assertEquals(Integer.valueOf(1), ComparatorUtils.max(1, 10, reversed));
        assertEquals(Integer.valueOf(-10), ComparatorUtils.max(10, -10, reversed));
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> ComparatorUtils.max(1, null, null),
                        "expecting NullPointerException"),
                () -> assertThrows(NullPointerException.class, () -> ComparatorUtils.max(null, 10, null),
                        "expecting NullPointerException")
        );
    }

    @Test
    public void min() {
        final Comparator<Integer> reversed =
                ComparatorUtils.reversedComparator(ComparatorUtils.<Integer>naturalComparator());

        assertEquals(Integer.valueOf(1), ComparatorUtils.min(1, 10, null));
        assertEquals(Integer.valueOf(-10), ComparatorUtils.min(10, -10, null));

        assertEquals(Integer.valueOf(10), ComparatorUtils.min(1, 10, reversed));
        assertEquals(Integer.valueOf(10), ComparatorUtils.min(10, -10, reversed));
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> ComparatorUtils.min(1, null, null),
                        "expecting NullPointerException"),
                () -> assertThrows(NullPointerException.class, () -> ComparatorUtils.min(null, 10, null),
                        "expecting NullPointerException")
        );
    }

    @Test
    public void nullLowComparator() {
        final Comparator<Integer> comp = ComparatorUtils.nullLowComparator(null);
        assertTrue(comp.compare(null, 10) < 0);
        assertEquals(0, comp.compare(null, null));
        assertTrue(comp.compare(10, null) > 0);
    }

    @Test
    public void nullHighComparator() {
        final Comparator<Integer> comp = ComparatorUtils.nullHighComparator(null);
        assertTrue(comp.compare(null, 10) > 0);
        assertEquals(0, comp.compare(null, null));
        assertTrue(comp.compare(10, null) < 0);
    }
}
