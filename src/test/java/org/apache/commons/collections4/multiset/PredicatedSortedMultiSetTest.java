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
package org.apache.commons.collections4.multiset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.SortedMultiSet;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractSortedMultiSetTest} for exercising the
 * {@link PredicatedSortedMultiSet} implementation.
 */
public class PredicatedSortedMultiSetTest<T> extends AbstractSortedMultiSetTest<T> {

    private final SortedMultiSet<T> nullMultiSet = null;

    protected Predicate<T> truePredicate = TruePredicate.<T>truePredicate();

    protected SortedMultiSet<T> decorateMultiSet(final SortedMultiSet<T> multiset, final Predicate<T> predicate) {
        return PredicatedSortedMultiSet.predicatedSortedMultiSet(multiset, predicate);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.6";
    }

    @Override
    public SortedMultiSet<T> makeObject() {
        return decorateMultiSet(new TreeMultiSet<>(), truePredicate);
    }

    protected SortedMultiSet<T> makeTestMultiSet() {
        return decorateMultiSet(new TreeMultiSet<>(), stringPredicate());
    }

    protected Predicate<T> stringPredicate() {
        return String.class::isInstance;
    }

    @Test
    void testDecorate() {
        final SortedMultiSet<T> multiset = decorateMultiSet(new TreeMultiSet<>(), stringPredicate());
        ((PredicatedSortedMultiSet<T>) multiset).decorated();

        assertThrows(NullPointerException.class, () -> decorateMultiSet(new TreeMultiSet<>(), null));

        assertThrows(NullPointerException.class, () -> decorateMultiSet(nullMultiSet, stringPredicate()));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIllegalAdd() {
        final SortedMultiSet<T> multiset = makeTestMultiSet();
        final Integer i = Integer.valueOf(3);
        assertThrows(IllegalArgumentException.class, () -> multiset.add((T) i),
                "Integer should fail string predicate.");
        assertFalse(multiset.contains(i), "Collection shouldn't contain illegal element");
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSortOrder() {
        final SortedMultiSet<T> multiset = decorateMultiSet(new TreeMultiSet<>(), stringPredicate());
        final String one = "one";
        final String two = "two";
        final String three = "three";
        multiset.add((T) one);
        multiset.add((T) two);
        multiset.add((T) three);
        assertEquals(multiset.first(), one, "first element");
        assertEquals(multiset.last(), two, "last element");
        final Comparator<? super T> c = multiset.comparator();
        assertNull(c, "natural order, so comparator should be null");
    }

//    void testCreate() throws Exception {
//        MultiSet<T> multiset = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/PredicatedSortedMultiSet.emptyCollection.version4.6.obj");
//        multiset = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/PredicatedSortedMultiSet.fullCollection.version4.6.obj");
//    }

}
