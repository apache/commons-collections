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

import java.util.Arrays;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;

/**
 * Extension of {@link AbstractNavigableSetTest} for exercising the
 * {@link PredicatedNavigableSet} implementation.
 *
 * @since 4.1
 * @version $Id$
 */
public class PredicatedNavigableSetTest<E> extends AbstractNavigableSetTest<E> {

    public PredicatedNavigableSetTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(PredicatedNavigableSetTest.class);
    }

    //-------------------------------------------------------------------

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    @Override
    public NavigableSet<E> makeObject() {
        return PredicatedNavigableSet.predicatedNavigableSet(new TreeSet<E>(), truePredicate);
    }

    @Override
    public NavigableSet<E> makeFullCollection() {
        final TreeSet<E> set = new TreeSet<>();
        set.addAll(Arrays.asList(getFullElements()));
        return PredicatedNavigableSet.predicatedNavigableSet(set, truePredicate);
    }

//--------------------------------------------------------------------
    protected Predicate<E> testPredicate =
        new Predicate<E>() {
            @Override
            public boolean evaluate(final E o) {
                return o instanceof String && ((String) o).startsWith("A");
            }
        };

    protected PredicatedNavigableSet<E> makeTestSet() {
        return PredicatedNavigableSet.predicatedNavigableSet(new TreeSet<E>(), testPredicate);
    }

    public void testGetSet() {
        final PredicatedNavigableSet<E> set = makeTestSet();
        assertTrue("returned set should not be null", set.decorated() != null);
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        final NavigableSet<E> set = makeTestSet();
        final String testString = "B";
        try {
            set.add((E) testString);
            fail("Should fail string predicate.");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element",
         !set.contains(testString));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAddAll() {
        final NavigableSet<E> set = makeTestSet();
        final Set<E> elements = new TreeSet<>();
        elements.add((E) "Aone");
        elements.add((E) "Atwo");
        elements.add((E) "Bthree");
        elements.add((E) "Afour");
        try {
            set.addAll(elements);
            fail("Should fail string predicate.");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        assertTrue("Set shouldn't contain illegal element", !set.contains("Aone"));
        assertTrue("Set shouldn't contain illegal element", !set.contains("Atwo"));
        assertTrue("Set shouldn't contain illegal element", !set.contains("Bthree"));
        assertTrue("Set shouldn't contain illegal element", !set.contains("Afour"));
    }

    public void testComparator() {
        final NavigableSet<E> set = makeTestSet();
        final Comparator<? super E> c = set.comparator();
        assertTrue("natural order, so comparator should be null", c == null);
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
