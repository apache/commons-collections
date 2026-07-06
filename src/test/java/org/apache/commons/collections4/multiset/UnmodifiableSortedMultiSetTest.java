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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.apache.commons.collections4.SortedMultiSet;
import org.apache.commons.collections4.Unmodifiable;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractSortedMultiSetTest} for exercising the
 * {@link UnmodifiableSortedMultiSet} implementation.
 */
public class UnmodifiableSortedMultiSetTest<E> extends AbstractSortedMultiSetTest<E> {

    @Override
    public String getCompatibilityVersion() {
        return "4.6";
    }

    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public SortedMultiSet<E> makeFullCollection() {
        final SortedMultiSet<E> multiset = new TreeMultiSet<>();
        multiset.addAll(Arrays.asList(getFullElements()));
        return UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(multiset);
    }

    @Override
    public SortedMultiSet<E> makeObject() {
        return UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(new TreeMultiSet<>());
    }

    @Test
    void testAdd() {
        final SortedMultiSet<E> multiset = makeFullCollection();
        final SortedMultiSet<E> unmodifiableMultiSet = UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(multiset);
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableMultiSet.add((E) "One", 1));
    }

    @Test
    void testDecorateFactory() {
        final SortedMultiSet<E> multiset = makeFullCollection();
        assertSame(multiset, UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(multiset));

        assertThrows(NullPointerException.class, () -> UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(null));
    }

    @Test
    void testEntrySet() {
        final SortedMultiSet<E> multiset = makeFullCollection();
        final SortedMultiSet<E> unmodifiableMultiSet = UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(multiset);
        assertEquals(multiset.entrySet(), unmodifiableMultiSet.entrySet());
    }

    @Test
    void testRemove() {
        final SortedMultiSet<E> multiset = makeFullCollection();
        final SortedMultiSet<E> unmodifiableMultiSet = UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(multiset);
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableMultiSet.remove("One", 1));
    }

    @Test
    void testSetCount() {
        final SortedMultiSet<E> multiset = makeFullCollection();
        final SortedMultiSet<E> unmodifiableMultiSet = UnmodifiableSortedMultiSet.unmodifiableSortedMultiSet(multiset);
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableMultiSet.setCount((E) "One", 2));
    }

    @Test
    void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullCollection() instanceof Unmodifiable);
    }

//    void testCreate() throws Exception {
//        MultiSet<E> multiset = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/UnmodifiableSortedMultiSet.emptyCollection.version4.6.obj");
//        multiset = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/UnmodifiableSortedMultiSet.fullCollection.version4.6.obj");
//    }

}
