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
package org.apache.commons.collections4.set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractNavigableSetTest} for exercising the
 * {@link PredicatedNavigableSet} implementation.
 */
public class PredicatedNavigableSetTest<E> extends AbstractNavigableSetTest<E> {

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    protected Predicate<E> testPredicate =
        o -> o instanceof String && ((String) o).startsWith("A");

    @Override
    public String getCompatibilityVersion() {
        return "4.1";
    }

    @Override
    public NavigableSet<E> makeFullCollection() {
        final TreeSet<E> set = new TreeSet<>(Arrays.asList(getFullElements()));
        return PredicatedNavigableSet.predicatedNavigableSet(set, truePredicate);
    }

    @Override
    public NavigableSet<E> makeObject() {
        return PredicatedNavigableSet.predicatedNavigableSet(new TreeSet<>(), truePredicate);
    }

    protected PredicatedNavigableSet<E> makeTestSet() {
        return PredicatedNavigableSet.predicatedNavigableSet(new TreeSet<>(), testPredicate);
    }

    @Test
    public void testComparator() {
        final NavigableSet<E> set = makeTestSet();
        final Comparator<? super E> c = set.comparator();
        assertNull(c, "natural order, so comparator should be null");
    }

    @Test
    public void testGetSet() {
        final PredicatedNavigableSet<E> set = makeTestSet();
        assertNotNull(set.decorated(), "returned set should not be null");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        final NavigableSet<E> set = makeTestSet();
        final String testString = "B";
        assertThrows(IllegalArgumentException.class, () -> set.add((E) testString),
                "Should fail string predicate.");
        assertFalse(set.contains(testString), "Collection shouldn't contain illegal element");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalAddAll() {
        final NavigableSet<E> set = makeTestSet();
        final Set<E> elements = new TreeSet<>();
        elements.add((E) "Aone");
        elements.add((E) "Atwo");
        elements.add((E) "Bthree");
        elements.add((E) "Afour");
        assertThrows(IllegalArgumentException.class, () -> set.addAll(elements),
                "Should fail string predicate.");
        assertFalse(set.contains("Aone"), "Set shouldn't contain illegal element");
        assertFalse(set.contains("Atwo"), "Set shouldn't contain illegal element");
        assertFalse(set.contains("Bthree"), "Set shouldn't contain illegal element");
        assertFalse(set.contains("Afour"), "Set shouldn't contain illegal element");
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedNavigableSet.emptyCollection.version4.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedNavigableSet.fullCollection.version4.1.obj");
//    }

}
