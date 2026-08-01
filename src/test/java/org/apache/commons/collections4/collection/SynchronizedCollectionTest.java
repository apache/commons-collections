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
package org.apache.commons.collections4.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.bag.SynchronizedBag;
import org.apache.commons.collections4.bag.SynchronizedSortedBag;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.collections4.multiset.SynchronizedMultiSet;
import org.apache.commons.collections4.multiset.SynchronizedSortedMultiSet;
import org.apache.commons.collections4.multiset.TreeMultiSet;
import org.apache.commons.collections4.queue.SynchronizedQueue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link SynchronizedCollection} implementation.
 */
public class SynchronizedCollectionTest<E> extends AbstractCollectionTest<E> {

    /** The elements used to populate each decorator under test. */
    private static final List<String> ELEMENTS = Arrays.asList("a", "b");

    /**
     * Every decorator that inherits {@link SynchronizedCollection#forEach(java.util.function.Consumer)}.
     */
    static Stream<Arguments> getSynchronizedDecorators() {
        return Stream.of(
                arguments("SynchronizedCollection", SynchronizedCollection.synchronizedCollection(new ArrayList<>(ELEMENTS))),
                arguments("SynchronizedBag", SynchronizedBag.synchronizedBag(new HashBag<>(ELEMENTS))),
                arguments("SynchronizedSortedBag", SynchronizedSortedBag.synchronizedSortedBag(new TreeBag<>(ELEMENTS))),
                arguments("SynchronizedMultiSet", SynchronizedMultiSet.synchronizedMultiSet(new HashMultiSet<>(ELEMENTS))),
                arguments("SynchronizedSortedMultiSet", SynchronizedSortedMultiSet.synchronizedSortedMultiSet(new TreeMultiSet<>(ELEMENTS))),
                arguments("SynchronizedQueue", SynchronizedQueue.synchronizedQueue(new LinkedList<>(ELEMENTS))));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<>();
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        return new ArrayList<>(Arrays.asList(getFullElements()));
    }

    @Override
    public Collection<E> makeObject() {
        return SynchronizedCollection.synchronizedCollection(new ArrayList<>());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getSynchronizedDecorators")
    void testForEachHoldsLock(final String description, final Collection<String> decorator) {
        final List<String> visited = new ArrayList<>();
        decorator.forEach(element -> {
            assertTrue(Thread.holdsLock(decorator), () -> description + " ran forEach without holding its lock");
            visited.add(element);
        });
        assertEquals(ELEMENTS.size(), visited.size());
        assertTrue(visited.containsAll(ELEMENTS));
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/SynchronizedCollection.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/SynchronizedCollection.fullCollection.version4.obj");
//    }

}
