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
package org.apache.commons.collections.set;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;

/**
 * Extension of {@link AbstractTestSortedSet} for exercising the
 * {@link PredicatedSortedSet} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Phil Steitz
 */
public class TestPredicatedSortedSet<E> extends AbstractTestSortedSet<E> {

    public TestPredicatedSortedSet(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestPredicatedSortedSet.class);
    }

 //-------------------------------------------------------------------

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    @Override
    public SortedSet<E> makeObject() {
        return PredicatedSortedSet.predicatedSortedSet(new TreeSet<E>(), truePredicate);
    }

    @Override
    public SortedSet<E> makeFullCollection() {
        TreeSet<E> set = new TreeSet<E>();
        set.addAll(Arrays.asList(getFullElements()));
        return PredicatedSortedSet.predicatedSortedSet(set, truePredicate);
    }

//--------------------------------------------------------------------
    protected Predicate<E> testPredicate =
        new Predicate<E>() {
            public boolean evaluate(E o) {
                return (o instanceof String) && (((String) o).startsWith("A"));
            }
        };

    protected PredicatedSortedSet<E> makeTestSet() {
        return (PredicatedSortedSet<E>) PredicatedSortedSet.predicatedSortedSet(new TreeSet<E>(), testPredicate);
    }

    public void testGetSet() {
        PredicatedSortedSet<E> set = makeTestSet();
        assertTrue("returned set should not be null", set.decorated() != null);
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        SortedSet<E> set = makeTestSet();
        String testString = "B";
        try {
            set.add((E) testString);
            fail("Should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element",
         !set.contains(testString));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAddAll() {
        SortedSet<E> set = makeTestSet();
        Set<E> elements = new TreeSet<E>();
        elements.add((E) "Aone");
        elements.add((E) "Atwo");
        elements.add((E) "Bthree");
        elements.add((E) "Afour");
        try {
            set.addAll(elements);
            fail("Should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Set shouldn't contain illegal element", !set.contains("Aone"));
        assertTrue("Set shouldn't contain illegal element", !set.contains("Atwo"));
        assertTrue("Set shouldn't contain illegal element", !set.contains("Bthree"));
        assertTrue("Set shouldn't contain illegal element", !set.contains("Afour"));
    }

    public void testComparator() {
        SortedSet<E> set = makeTestSet();
        Comparator<? super E> c = set.comparator();
        assertTrue("natural order, so comparator should be null", c == null);
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/PredicatedSortedSet.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/PredicatedSortedSet.fullCollection.version3.1.obj");
//    }

}
