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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.apache.commons.collections4.multiset.HashMultiSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for MultiSetUtils.
 */
class MultiSetUtilsTest {

    private String[] fullArray;
    private MultiSet<String> multiSet;

    @BeforeEach
    public void setUp() {
        fullArray = new String[]{
            "a", "a", "b", "c", "d", "d", "d"
        };
        multiSet = new HashMultiSet<>(Arrays.asList(fullArray));
    }

    /**
     * Tests {@link MultiSetUtils#containsOccurrences(MultiSet, MultiSet)}.
     */
    @Test
    void testContainsOccurrences() {
        assertTrue(MultiSetUtils.containsOccurrences(multiSet, new HashMultiSet<>()));
        assertTrue(MultiSetUtils.containsOccurrences(multiSet, new HashMultiSet<>(Arrays.asList("a", "a", "d"))));
        assertTrue(MultiSetUtils.containsOccurrences(multiSet, multiSet));

        assertFalse(MultiSetUtils.containsOccurrences(multiSet, new HashMultiSet<>(Arrays.asList("b", "b"))),
                "Only one occurrence of 'b' is present");
        assertFalse(MultiSetUtils.containsOccurrences(multiSet, new HashMultiSet<>(Arrays.asList("e"))),
                "'e' is not present");
        assertFalse(MultiSetUtils.containsOccurrences(new HashMultiSet<>(), multiSet));

        assertThrows(NullPointerException.class, () -> MultiSetUtils.containsOccurrences(null, multiSet),
                "Expecting NPE");
        assertThrows(NullPointerException.class, () -> MultiSetUtils.containsOccurrences(multiSet, null),
                "Expecting NPE");
    }

    /**
     * Tests {@link MultiSetUtils#emptyMultiSet()}.
     */
    @Test
    void testEmptyMultiSet() {
        final MultiSet<Integer> empty = MultiSetUtils.emptyMultiSet();
        assertEquals(0, empty.size());

        assertThrows(UnsupportedOperationException.class, () -> empty.add(55),
                "Empty multi set must be read-only");
    }

    /**
     * Tests {@link MultiSetUtils#predicatedMultiSet(org.apache.commons.collections4.MultiSet, org.apache.commons.collections4.Predicate)}.
     */
    @Test
    void testPredicatedMultiSet() {
        final Predicate<String> predicate = object -> object.length() == 1;
        final MultiSet<String> predicated = MultiSetUtils.predicatedMultiSet(multiSet, predicate);
        assertEquals(multiSet.size(), predicated.size());
        assertEquals(multiSet.getCount("a"), predicated.getCount("a"));

        assertThrows(NullPointerException.class, () -> MultiSetUtils.predicatedMultiSet(null, predicate),
                "Expecting NPE");

        assertThrows(NullPointerException.class, () -> MultiSetUtils.predicatedMultiSet(multiSet, null),
                "Expecting NPE");

        assertThrows(IllegalArgumentException.class, () -> MultiSetUtils.predicatedMultiSet(multiSet, object -> object.equals("a")),
                "Predicate is violated for all elements not being 'a'");
    }

    /**
     * Tests {@link MultiSetUtils#removeOccurrences(MultiSet, MultiSet)}.
     */
    @Test
    void testRemoveOccurrences() {
        assertFalse(MultiSetUtils.removeOccurrences(multiSet, new HashMultiSet<>()));
        assertFalse(MultiSetUtils.removeOccurrences(multiSet, new HashMultiSet<>(Arrays.asList("e"))),
                "'e' is not present, so nothing changes");

        assertTrue(MultiSetUtils.removeOccurrences(multiSet, new HashMultiSet<>(Arrays.asList("a", "d", "d", "d", "d"))));
        assertEquals(1, multiSet.getCount("a"), "One occurrence of 'a' should remain");
        assertEquals(0, multiSet.getCount("d"), "Removing more copies than present empties 'd'");
        assertEquals(1, multiSet.getCount("b"));
        assertEquals(1, multiSet.getCount("c"));

        assertTrue(MultiSetUtils.removeOccurrences(multiSet, multiSet), "Removing itself clears the multiset");
        assertEquals(0, multiSet.size());
        assertFalse(MultiSetUtils.removeOccurrences(multiSet, multiSet), "Empty multiset is unchanged");

        multiSet.addAll(Arrays.asList(fullArray));
        assertTrue(MultiSetUtils.removeOccurrences(multiSet, MultiSetUtils.unmodifiableMultiSet(multiSet)),
                "Removing a view of itself clears the multiset");
        assertEquals(0, multiSet.size());

        assertThrows(NullPointerException.class, () -> MultiSetUtils.removeOccurrences(null, multiSet),
                "Expecting NPE");
        assertThrows(NullPointerException.class, () -> MultiSetUtils.removeOccurrences(multiSet, null),
                "Expecting NPE");
    }

    /**
     * Tests {@link MultiSetUtils#retainOccurrences(MultiSet, MultiSet)}.
     */
    @Test
    void testRetainOccurrences() {
        assertFalse(MultiSetUtils.retainOccurrences(multiSet, multiSet), "Retaining itself changes nothing");
        assertFalse(MultiSetUtils.retainOccurrences(multiSet, new HashMultiSet<>(Arrays.asList(fullArray))),
                "Retaining an equal multiset changes nothing");

        assertTrue(MultiSetUtils.retainOccurrences(multiSet, new HashMultiSet<>(Arrays.asList("a", "d", "d", "e"))));
        assertEquals(1, multiSet.getCount("a"), "Only one occurrence of 'a' should be retained");
        assertEquals(2, multiSet.getCount("d"), "Only two occurrences of 'd' should be retained");
        assertEquals(0, multiSet.getCount("b"), "'b' should be removed entirely");
        assertEquals(0, multiSet.getCount("c"), "'c' should be removed entirely");

        assertTrue(MultiSetUtils.retainOccurrences(multiSet, new HashMultiSet<>()), "Retaining nothing clears the multiset");
        assertEquals(0, multiSet.size());

        assertThrows(NullPointerException.class, () -> MultiSetUtils.retainOccurrences(null, multiSet),
                "Expecting NPE");
        assertThrows(NullPointerException.class, () -> MultiSetUtils.retainOccurrences(multiSet, null),
                "Expecting NPE");
    }

    /**
     * Tests {@link MultiSetUtils#unmodifiableMultiSet(org.apache.commons.collections4.MultiSet) ()}.
     */
    @Test
    void testSynchronizedMultiSet() {
        final MultiSet<String> synced = MultiSetUtils.synchronizedMultiSet(multiSet);
        assertEquals(multiSet, synced);
        synced.add("a"); // ensure adding works
    }

    /**
     * Tests {@link MultiSetUtils#unmodifiableMultiSet(org.apache.commons.collections4.MultiSet) ()}.
     */
    @Test
    void testUnmodifiableMultiSet() {
        final MultiSet<String> unmodifiable = MultiSetUtils.unmodifiableMultiSet(multiSet);
        assertEquals(multiSet, unmodifiable);

        assertThrows(UnsupportedOperationException.class, () -> unmodifiable.add("a"),
                "Empty multi set must be read-only");

        assertThrows(NullPointerException.class, () -> MultiSetUtils.unmodifiableMultiSet(null),
                "Expecting NPE");
    }

}
