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
package org.apache.commons.collections4.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.functors.NonePredicate;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link CollectionBag}.
 * <p>
 * Note: This test is mainly for serialization support, the CollectionBag decorator
 * is extensively used and tested in AbstractBagTest.
 */
public class CollectionBagTest<T> extends AbstractCollectionTest<T> {

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    protected int getIterationBehaviour() {
        return UNORDERED;
    }

    /**
     * Returns an empty List for use in modification testing.
     *
     * @return a confirmed empty collection
     */
    @Override
    public Collection<T> makeConfirmedCollection() {
        return new ArrayList<>();
    }

    /**
     * Returns a full Set for use in modification testing.
     *
     * @return a confirmed full collection
     */
    @Override
    public Collection<T> makeConfirmedFullCollection() {
        final Collection<T> set = makeConfirmedCollection();
        set.addAll(Arrays.asList(getFullElements()));
        return set;
    }

    @Override
    public Bag<T> makeObject() {
        return CollectionBag.collectionBag(new HashBag<>());
    }

    @Test
    void testAdd_Predicate_ComparatorCustom() throws Throwable {
        final TreeBag<Predicate<Object>> treeBagOfPredicateOfObject = new TreeBag<>(Comparator.comparing(Predicate::toString));
        final CollectionBag<Predicate<Object>> collectionBagOfPredicateOfObject = new CollectionBag<>(treeBagOfPredicateOfObject);
        collectionBagOfPredicateOfObject.add(NonePredicate.nonePredicate(collectionBagOfPredicateOfObject), 24);
    }

    @Test
    void testAdd_Predicate_ComparatorDefault() throws Throwable {
        final TreeBag<Predicate<Object>> treeBagOfPredicateOfObject = new TreeBag<>();
        final CollectionBag<Predicate<Object>> collectionBagOfPredicateOfObject = new CollectionBag<>(treeBagOfPredicateOfObject);
        assertThrows(ClassCastException.class, () -> collectionBagOfPredicateOfObject.add(NonePredicate.nonePredicate(collectionBagOfPredicateOfObject), 24));
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/CollectionBag.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/CollectionBag.fullCollection.version4.obj");
//    }

    /**
     * Compares the current serialized form of the Bag
     * against the canonical version in SCM.
     */
    @Test
    void testEmptyBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final Bag<T> bag = makeObject();
        if (bag instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final Bag<?> bag2 = (Bag<?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(bag));
            assertTrue(bag2.isEmpty(), "Bag is empty");
            assertEquals(bag, bag2);
        }
    }

    /**
     * Compares the current serialized form of the Bag
     * against the canonical version in SCM.
     */
    @Test
    void testFullBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final Bag<T> bag = (Bag<T>) makeFullCollection();
        if (bag instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final Bag<?> bag2 = (Bag<?>) readExternalFormFromDisk(getCanonicalFullCollectionName(bag));
            assertEquals(bag.size(), bag2.size(), "Bag is the right size");
            assertEquals(bag, bag2);
        }
    }
}
