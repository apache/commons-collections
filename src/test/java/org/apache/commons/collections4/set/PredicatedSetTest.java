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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.Test;

/**
 * Extension of {@link AbstractSetTest} for exercising the
 * {@link PredicatedSet} implementation.
 *
 * @since 3.0
 */
public class PredicatedSetTest<E> extends AbstractSetTest<E> {

    public PredicatedSetTest(final String testName) {
        super(testName);
    }

 //-------------------------------------------------------------------

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    protected PredicatedSet<E> decorateSet(final Set<E> set, final Predicate<? super E> predicate) {
        return PredicatedSet.predicatedSet(set, predicate);
    }

    @Override
    public PredicatedSet<E> makeObject() {
        return decorateSet(new HashSet<E>(), truePredicate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] getFullElements() {
        return (E[]) new Object[] {"1", "3", "5", "7", "2", "4", "6"};
    }

//--------------------------------------------------------------------

    protected Predicate<E> testPredicate =
        o -> o instanceof String;

    protected PredicatedSet<E> makeTestSet() {
        return decorateSet(new HashSet<E>(), testPredicate);
    }

    @Test
    public void testGetSet() {
        final PredicatedSet<E> set = makeTestSet();
        assertNotNull("returned set should not be null", set.decorated());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        final Set<E> set = makeTestSet();
        final Integer i = Integer.valueOf(3);
        assertThrows(IllegalArgumentException.class, () -> set.add((E) i),
                "Integer should fail string predicate.");
        assertFalse("Collection shouldn't contain illegal element", set.contains(i));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalAddAll() {
        final Set<E> set = makeTestSet();
        final Set<E> elements = new HashSet<>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) Integer.valueOf(3));
        elements.add((E) "four");
        assertThrows(IllegalArgumentException.class, () -> set.addAll(elements),
                "Integer should fail string predicate.");
        assertFalse("Set shouldn't contain illegal element", set.contains("one"));
        assertFalse("Set shouldn't contain illegal element", set.contains("two"));
        assertFalse("Set shouldn't contain illegal element", set.contains(Integer.valueOf(3)));
        assertFalse("Set shouldn't contain illegal element", set.contains("four"));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedSet.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedSet.fullCollection.version4.obj");
//    }

}
