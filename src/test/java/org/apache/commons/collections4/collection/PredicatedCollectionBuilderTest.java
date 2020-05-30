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
package org.apache.commons.collections4.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Test;

/**
 * Tests the PredicatedCollection.Builder class.
 *
 * @since 4.1
 */
public class PredicatedCollectionBuilderTest {

    /**
     * Verify that passing the Predicate means ending up in the buffer.
     */
    @Test
    public void addPass() {
        final PredicatedCollection.Builder<String> builder = PredicatedCollection.notNullBuilder();
        builder.add("test");
        assertEquals(builder.createPredicatedList().size(), 1);
    }

    /**
     * Verify that failing the Predicate means NOT ending up in the buffer.
     */
    @Test
    public void addFail() {
        final PredicatedCollection.Builder<String> builder = PredicatedCollection.notNullBuilder();
        builder.add((String) null);
        assertTrue(builder.createPredicatedList().isEmpty());

        assertEquals(1, builder.rejectedElements().size());
    }

    /**
     * Verify that only items that pass the Predicate end up in the buffer.
     */
    @Test
    public void addAllPass() {
        final PredicatedCollection.Builder<String> builder = PredicatedCollection.notNullBuilder();
        builder.addAll(Arrays.asList("test1", null, "test2"));
        assertEquals(builder.createPredicatedList().size(), 2);
    }

    @Test
    public void createPredicatedCollectionWithNotNullPredicate() {
        final PredicatedCollection.Builder<String> builder = PredicatedCollection.notNullBuilder();
        builder.add("test1");
        builder.add((String) null);

        final List<String> predicatedList = builder.createPredicatedList();
        checkPredicatedCollection1(predicatedList);

        final Set<String> predicatedSet = builder.createPredicatedSet();
        checkPredicatedCollection1(predicatedSet);

        final Bag<String> predicatedBag = builder.createPredicatedBag();
        checkPredicatedCollection1(predicatedBag);

        final Queue<String> predicatedQueue = builder.createPredicatedQueue();
        checkPredicatedCollection1(predicatedQueue);
    }

    private void checkPredicatedCollection1(final Collection<String> collection) {
        assertEquals(1, collection.size());

        collection.add("test2");
        assertEquals(2, collection.size());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            collection.add(null);
        });
        assertNotNull(exception.getMessage());
    }

    @Test
    public void createPredicatedCollectionWithPredicate() {
        final OddPredicate p = new OddPredicate();
        final PredicatedCollection.Builder<Integer> builder = PredicatedCollection.builder(p);

        builder.add(1);
        builder.add(2);
        builder.add(3);

        final List<Integer> predicatedList = builder.createPredicatedList();
        checkPredicatedCollection2(predicatedList);

        final Set<Integer> predicatedSet = builder.createPredicatedSet();
        checkPredicatedCollection2(predicatedSet);

        final Bag<Integer> predicatedBag = builder.createPredicatedBag();
        checkPredicatedCollection2(predicatedBag);

        final Queue<Integer> predicatedQueue = builder.createPredicatedQueue();
        checkPredicatedCollection2(predicatedQueue);
    }

    private void checkPredicatedCollection2(final Collection<Integer> collection) {
        assertEquals(2, collection.size());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            collection.add(4);
        });
        assertNotNull(exception.getMessage());
        assertEquals(2, collection.size());

        collection.add(5);
        assertEquals(3, collection.size());
    }

    private static class OddPredicate implements Predicate<Integer> {
        @Override
        public boolean evaluate(final Integer value) {
            return value % 2 == 1;
        }
    }
}
