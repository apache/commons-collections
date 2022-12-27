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
 *
 * @since 4.1
 */
public class PredicatedNavigableSetTest<E> extends AbstractNavigableSetTest<E> {

    public PredicatedNavigableSetTest() {
        super(PredicatedNavigableSetTest.class.getSimpleName());
    }

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    @Override
    public NavigableSet<E> makeObject() {
        return PredicatedNavigableSet.predicatedNavigableSet(new TreeSet<E>(), truePredicate);
    }

    @Override
    public NavigableSet<E> makeFullCollection() {
        final TreeSet<E> set = new TreeSet<>(Arrays.asList(getFullElements()));
        return PredicatedNavigableSet.predicatedNavigableSet(set, truePredicate);
    }

    protected Predicate<E> testPredicate =
        o -> o instanceof String && ((String) o).startsWith("A");

    protected PredicatedNavigableSet<E> makeTestSet() {
        return PredicatedNavigableSet.predicatedNavigableSet(new TreeSet<E>(), testPredicate);
    }

    @Test
    public void testGetSet() {
        final PredicatedNavigableSet<E> set = makeTestSet();
        assertNotNull("returned set should not be null", set.decorated());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        final NavigableSet<E> set = makeTestSet();
        final String testString = "B";
        assertThrows(IllegalArgumentException.class, () -> set.add((E) testString),
                "Should fail string predicate.");
        assertFalse("Collection shouldn't contain illegal element", set.contains(testString));
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
        assertFalse("Set shouldn't contain illegal element", set.contains("Aone"));
        assertFalse("Set shouldn't contain illegal element", set.contains("Atwo"));
        assertFalse("Set shouldn't contain illegal element", set.contains("Bthree"));
        assertFalse("Set shouldn't contain illegal element", set.contains("Afour"));
    }

    @Test
    public void testComparator() {
        final NavigableSet<E> set = makeTestSet();
        final Comparator<? super E> c = set.comparator();
        assertNull("natural order, so comparator should be null", c);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedNavigableSet.emptyCollection.version4.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedNavigableSet.fullCollection.version4.1.obj");
//    }

}
