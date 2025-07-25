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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.Unmodifiable;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractMultiSetTest} for exercising the
 * {@link UnmodifiableMultiSet} implementation.
 */
public class UnmodifiableMultiSetTest<E> extends AbstractMultiSetTest<E> {

    @Override
    public MultiSet<E> getCollection() {
        return super.getCollection();
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
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isNullSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public MultiSet<E> makeFullCollection() {
        final MultiSet<E> multiset = new HashMultiSet<>();
        multiset.addAll(Arrays.asList(getFullElements()));
        return UnmodifiableMultiSet.unmodifiableMultiSet(multiset);
    }

    @Override
    public MultiSet<E> makeObject() {
        return UnmodifiableMultiSet.unmodifiableMultiSet(new HashMultiSet<>());
    }

    @Test
    void testAdd() {
        final MultiSet<E> multiset = makeFullCollection();
        final MultiSet<E> unmodifiableMultiSet =  UnmodifiableMultiSet.unmodifiableMultiSet(multiset);
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableMultiSet.add((E) "One", 1));
    }

    @Test
    void testDecorateFactory() {
        final MultiSet<E> multiset = makeFullCollection();
        assertSame(multiset, UnmodifiableMultiSet.unmodifiableMultiSet(multiset));

        assertThrows(NullPointerException.class, () -> UnmodifiableMultiSet.unmodifiableMultiSet(null));
    }

    @Test
    void testEntrySet() {
        final MultiSet<E> multiset = makeFullCollection();
        final MultiSet<E> unmodifiableMultiSet = UnmodifiableMultiSet.unmodifiableMultiSet(multiset);
        assertSame(unmodifiableMultiSet.entrySet().size(), multiset.entrySet().size());
    }

    @Test
    void testRemove() {
        final MultiSet<E> multiset = makeFullCollection();
        final MultiSet<E> unmodifiableMultiSet =  UnmodifiableMultiSet.unmodifiableMultiSet(multiset);
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableMultiSet.remove("One", 1));
    }

    @Test
    void testSetCount() {
        final MultiSet<E> multiset = makeFullCollection();
        final MultiSet<E> unmodifiableMultiSet =  UnmodifiableMultiSet.unmodifiableMultiSet(multiset);
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableMultiSet.setCount((E) "One", 2));
    }

    @Test
    void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullCollection() instanceof Unmodifiable);
    }

//    void testCreate() throws Exception {
//        MultiSet<E> multiset = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/data/test/UnmodifiableMultiSet.emptyCollection.version4.1.obj");
//        multiset = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/data/test/UnmodifiableMultiSet.fullCollection.version4.1.obj");
//    }

}
