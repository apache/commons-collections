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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.SortedMultiSet;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractSortedMultiSetTest} for exercising the
 * {@link TreeMultiSet} implementation.
 */
public class TreeMultiSetTest<T> extends AbstractSortedMultiSetTest<T> {

    @Override
    public String getCompatibilityVersion() {
        return "4.6";
    }

    @Override
    public SortedMultiSet<T> makeObject() {
        return new TreeMultiSet<>();
    }

    @SuppressWarnings("unchecked")
    public SortedMultiSet<T> setupMultiSet() {
        final SortedMultiSet<T> multiset = makeObject();
        multiset.add((T) "C");
        multiset.add((T) "A");
        multiset.add((T) "B");
        multiset.add((T) "D");
        return multiset;
    }

    @Test
    void testAddNonComparable() {
        final MultiSet<Object> multiset = new TreeMultiSet<>();

        assertThrows(IllegalArgumentException.class, () -> multiset.add(new Object()));
    }

    @Test
    void testAddNull() {
        final MultiSet<Object> multiset = new TreeMultiSet<>();

        assertThrows(NullPointerException.class, () -> multiset.add(null));

        final MultiSet<String> multiset2 = new TreeMultiSet<>(String::compareTo);
        // jdk bug: adding null to an empty TreeMap works
        // thus ensure that the multiset is not empty before adding null
        multiset2.add("a");

        assertThrows(NullPointerException.class, () -> multiset2.add(null));
    }

    @Test
    void testComparator() {
        final SortedMultiSet<String> multiset = new TreeMultiSet<>();
        assertNull(multiset.comparator(), "natural order, so comparator should be null");

        final SortedMultiSet<String> multiset2 = new TreeMultiSet<>(String.CASE_INSENSITIVE_ORDER);
        assertEquals(String.CASE_INSENSITIVE_ORDER, multiset2.comparator());
    }

    @Test
    void testOrdering() {
        final SortedMultiSet<T> multiset = setupMultiSet();
        assertEquals("A", multiset.toArray()[0], "Should get elements in correct order");
        assertEquals("B", multiset.toArray()[1], "Should get elements in correct order");
        assertEquals("C", multiset.toArray()[2], "Should get elements in correct order");
        assertEquals("A", multiset.first(), "Should get first key");
        assertEquals("D", multiset.last(), "Should get last key");
    }

//    void testCreate() throws Exception {
//        MultiSet<T> multiset = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/TreeMultiSet.emptyCollection.version4.6.obj");
//        multiset = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/TreeMultiSet.fullCollection.version4.6.obj");
//    }

}
