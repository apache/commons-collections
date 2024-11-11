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
package org.apache.commons.collections4.multiset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractMultiSetTest} for exercising the
 * {@link PredicatedMultiSet} implementation.
 */
public class PredicatedMultiSetTest<T> extends AbstractMultiSetTest<T> {

    protected Predicate<T> truePredicate = TruePredicate.<T>truePredicate();

    protected MultiSet<T> decorateMultiSet(final HashMultiSet<T> multiset, final Predicate<T> predicate) {
        return PredicatedMultiSet.predicatedMultiSet(multiset, predicate);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.1";
    }

    @Override
    protected int getIterationBehaviour() {
        return UNORDERED;
    }

    @Override
    public MultiSet<T> makeObject() {
        return decorateMultiSet(new HashMultiSet<>(), truePredicate);
    }

    protected MultiSet<T> makeTestMultiSet() {
        return decorateMultiSet(new HashMultiSet<>(), stringPredicate());
    }

    protected Predicate<T> stringPredicate() {
        return String.class::isInstance;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        final MultiSet<T> multiset = makeTestMultiSet();
        final Integer i = Integer.valueOf(3);
        assertThrows(IllegalArgumentException.class, () -> multiset.add((T) i),
                "Integer should fail string predicate.");
        assertFalse(multiset.contains(i), "Collection shouldn't contain illegal element");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalDecorate() {
        final HashMultiSet<Object> elements = new HashMultiSet<>();
        elements.add("one");
        elements.add("two");
        elements.add(Integer.valueOf(3));
        elements.add("four");
        assertThrows(IllegalArgumentException.class, () -> decorateMultiSet((HashMultiSet<T>) elements, stringPredicate()),
                "MultiSet contains an element that should fail the predicate.");
        assertThrows(NullPointerException.class, () -> decorateMultiSet(new HashMultiSet<>(), null),
                "Expecting NullPointerException for null predicate.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLegalAddRemove() {
        final MultiSet<T> multiset = makeTestMultiSet();
        assertEquals(0, multiset.size());
        final T[] els = (T[]) new Object[] { "1", "3", "5", "7", "2", "4", "1" };
        for (int i = 0; i < els.length; i++) {
            multiset.add(els[i]);
            assertEquals(i + 1, multiset.size());
            assertTrue(multiset.contains(els[i]));
        }
        Set<T> set = ((PredicatedMultiSet<T>) multiset).uniqueSet();
        assertTrue(set.contains(els[0]), "Unique set contains the first element");
        assertTrue(multiset.remove(els[0]));
        set = ((PredicatedMultiSet<T>) multiset).uniqueSet();
        assertTrue(set.contains(els[0]),
            "Unique set does not contain anymore the first element");
    }

//    public void testCreate() throws Exception {
//        MultiSet<T> multiset = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/data/test/PredicatedMultiSet.emptyCollection.version4.1.obj");
//        multiset = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/data/test/PredicatedMultiSet.fullCollection.version4.1.obj");
//    }

}
