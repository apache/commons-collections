/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.SetUtils.SetView;
import org.apache.commons.collections4.set.PredicatedSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for SetUtils.
 */
class SetUtilsTest {

    private Set<Integer> setA;
    private Set<Integer> setB;

    @BeforeEach
    public void setUp() {
        setA = new HashSet<>();
        setA.add(1);
        setA.add(2);
        setA.add(3);
        setA.add(4);
        setA.add(5);

        setB = new HashSet<>();
        setB.add(3);
        setB.add(4);
        setB.add(5);
        setB.add(6);
        setB.add(7);
    }

    @Test
    void testDifference() {
        final SetView<Integer> set = SetUtils.difference(setA, setB);
        assertEquals(2, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        for (final Integer i : setB) {
            assertFalse(set.contains(i));
        }

        final Set<Integer> set2 = SetUtils.difference(setA, SetUtils.<Integer>emptySet());
        assertEquals(setA, set2);

        assertThrows(NullPointerException.class, () -> SetUtils.difference(setA, null));
        assertThrows(NullPointerException.class, () -> SetUtils.difference(null, setA));
    }

    @Test
    void testDisjunction() {
        final SetView<Integer> set = SetUtils.disjunction(setA, setB);
        assertEquals(4, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(6));
        assertTrue(set.contains(7));
        assertFalse(set.contains(3));
        assertFalse(set.contains(4));
        assertFalse(set.contains(5));

        final Set<Integer> set2 = SetUtils.disjunction(setA, SetUtils.<Integer>emptySet());
        assertEquals(setA, set2);

        assertThrows(NullPointerException.class, () -> SetUtils.disjunction(setA, null));
        assertThrows(NullPointerException.class, () -> SetUtils.disjunction(null, setA));
    }

    @Test
    void testEmptyIfNull() {
        assertTrue(SetUtils.emptyIfNull(null).isEmpty());

        final Set<Long> set = new HashSet<>();
        assertSame(set, SetUtils.emptyIfNull(set));
    }

    @Test
    void testEquals() {
        final Collection<String> data = Arrays.asList("a", "b", "c");

        final Set<String> a = new HashSet<>(data);
        final Set<String> b = new HashSet<>(data);

        assertEquals(a, b);
        assertTrue(SetUtils.isEqualSet(a, b));
        a.clear();
        assertFalse(SetUtils.isEqualSet(a, b));
        assertFalse(SetUtils.isEqualSet(a, null));
        assertFalse(SetUtils.isEqualSet(null, b));
        assertTrue(SetUtils.isEqualSet(null, null));
    }

    @Test
    void testHashCode() {
        final Collection<String> data = Arrays.asList("a", "b", "c");

        final Set<String> a = new HashSet<>(data);
        final Set<String> b = new HashSet<>(data);

        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), SetUtils.hashCodeForSet(a));
        assertEquals(b.hashCode(), SetUtils.hashCodeForSet(b));
        assertEquals(SetUtils.hashCodeForSet(a), SetUtils.hashCodeForSet(b));
        a.clear();
        assertNotEquals(SetUtils.hashCodeForSet(a), SetUtils.hashCodeForSet(b));
        assertEquals(0, SetUtils.hashCodeForSet(null));
    }

    @Test
    void testHashSet() {
        final Set<?> set1 = SetUtils.unmodifiableSet();
        assertTrue(set1.isEmpty(), "set is empty");

        final Set<Integer> set2 = SetUtils.hashSet(1, 2, 2, 3);
        assertEquals(3, set2.size(), "set has 3 elements");
        assertTrue(set2.contains(1), "set contains 1");
        assertTrue(set2.contains(2), "set contains 2");
        assertTrue(set2.contains(3), "set contains 3");

        final Set<String> set3 = SetUtils.hashSet("1", "2", "2", "3");
        assertEquals(3, set3.size(), "set has 3 elements");
        assertTrue(set3.contains("1"), "set contains 1");
        assertTrue(set3.contains("2"), "set contains 2");
        assertTrue(set3.contains("3"), "set contains 3");

        final Set<?> set4 = SetUtils.hashSet(null, null);
        assertEquals(1, set4.size(), "set has 1 element");
        assertTrue(set4.contains(null), "set contains null");

        final Set<?> set5 = SetUtils.hashSet((Object[]) null);
        assertNull(set5, "set is null");
    }

    @Test
    void testIntersection() {
        final SetView<Integer> set = SetUtils.intersection(setA, setB);
        assertEquals(3, set.size());
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertTrue(set.contains(5));
        assertFalse(set.contains(1));
        assertFalse(set.contains(2));
        assertFalse(set.contains(6));
        assertFalse(set.contains(7));

        final Set<Integer> set2 = SetUtils.intersection(setA, SetUtils.<Integer>emptySet());
        assertEquals(SetUtils.<Integer>emptySet(), set2);

        assertThrows(NullPointerException.class, () -> SetUtils.intersection(setA, null));
        assertThrows(NullPointerException.class, () -> SetUtils.intersection(null, setA));
    }

    @Test
    void testNewIdentityHashSet() {
        final Set<String> set = SetUtils.newIdentityHashSet();
        final String a = new String("a");
        set.add(a);
        set.add(new String("b"));
        set.add(a);

        assertEquals(2, set.size());

        set.add(new String("a"));
        assertEquals(3, set.size());

        set.remove(a);
        assertEquals(2, set.size());
    }

    @Test
    void testpredicatedSet() {
        final Predicate<Object> predicate = String.class::isInstance;
        final Set<Object> set = SetUtils.predicatedSet(new HashSet<>(), predicate);
        assertInstanceOf(PredicatedSet.class, set, "returned object should be a PredicatedSet");
        assertThrows(NullPointerException.class, () -> SetUtils.predicatedSet(new HashSet<>(), null), "Expecting NullPointerException for null predicate.");
        assertThrows(NullPointerException.class, () -> SetUtils.predicatedSet(null, predicate), "Expecting NullPointerException for null set.");
    }

    @Test
    void testUnion() {
        final SetView<Integer> set = SetUtils.union(setA, setB);
        assertEquals(7, set.size());
        assertTrue(set.containsAll(setA));
        assertTrue(set.containsAll(setB));
        final Set<Integer> set2 = SetUtils.union(setA, SetUtils.<Integer>emptySet());
        assertEquals(setA, set2);
        assertThrows(NullPointerException.class, () -> SetUtils.union(setA, null));
        assertThrows(NullPointerException.class, () -> SetUtils.union(null, setA));
    }

    @Test
    void testUnmodifiableSet() {
        final Set<?> set1 = SetUtils.unmodifiableSet();
        assertTrue(set1.isEmpty(), "set is empty");

        final Set<Integer> set2 = SetUtils.unmodifiableSet(1, 2, 2, 3);
        assertEquals(3, set2.size(), "set has 3 elements");
        assertTrue(set2.contains(1), "set contains 1");
        assertTrue(set2.contains(2), "set contains 2");
        assertTrue(set2.contains(3), "set contains 3");

        final Set<String> set3 = SetUtils.unmodifiableSet("1", "2", "2", "3");
        assertEquals(3, set3.size(), "set has 3 elements");
        assertTrue(set3.contains("1"), "set contains 1");
        assertTrue(set3.contains("2"), "set contains 2");
        assertTrue(set3.contains("3"), "set contains 3");

        final Set<?> set4 = SetUtils.unmodifiableSet(null, null);
        assertEquals(1, set4.size(), "set has 1 element");
        assertTrue(set4.contains(null), "set contains null");

        final Set<?> set5 = SetUtils.unmodifiableSet((Object[]) null);
        assertNull(set5, "set is null");
    }

    @Test
    void testUnmodifiableSetWrap() {
        final Set<Integer> set1 = SetUtils.unmodifiableSet(1, 2, 2, 3);
        final Set<Integer> set2 = SetUtils.unmodifiableSet(set1);
        assertSame(set1, set2);
    }

}
