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
package org.apache.commons.collections4.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractListTest} for exercising the
 * {@link PredicatedList} implementation.
 */
public class PredicatedListTest<E> extends AbstractListTest<E> {

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    protected Predicate<E> testPredicate =
        String.class::isInstance;

    protected List<E> decorateList(final List<E> list, final Predicate<E> predicate) {
        return PredicatedList.predicatedList(list, predicate);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] getFullElements() {
        return (E[]) new Object[] { "1", "3", "5", "7", "2", "4", "6" };
    }

    @Override
    public List<E> makeObject() {
        return decorateList(new ArrayList<>(), truePredicate);
    }

    public List<E> makeTestList() {
        return decorateList(new ArrayList<>(), testPredicate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        final List<E> list = makeTestList();
        final Integer i = Integer.valueOf(3);

        assertThrows(IllegalArgumentException.class, () -> list.add((E) i),
                "Integer should fail string predicate.");

        assertFalse(list.contains(i), "Collection shouldn't contain illegal element");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalAddAll() {
        final List<E> list = makeTestList();
        final List<E> elements = new ArrayList<>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) Integer.valueOf(3));
        elements.add((E) "four");

        assertThrows(IllegalArgumentException.class, () -> list.addAll(0, elements),
                "Integer should fail string predicate.");

        assertFalse(list.contains("one"), "List shouldn't contain illegal element");
        assertFalse(list.contains("two"), "List shouldn't contain illegal element");
        assertFalse(list.contains(Integer.valueOf(3)), "List shouldn't contain illegal element");
        assertFalse(list.contains("four"), "List shouldn't contain illegal element");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalSet() {
        final List<E> list = makeTestList();
        assertThrows(IllegalArgumentException.class, () -> list.set(0, (E) Integer.valueOf(3)),
                "Integer should fail string predicate.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLegalAddAll() {
        final List<E> list = makeTestList();
        list.add((E) "zero");
        final List<E> elements = new ArrayList<>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) "three");
        list.addAll(1, elements);
        assertTrue(list.contains("zero"), "List should contain legal element");
        assertTrue(list.contains("one"), "List should contain legal element");
        assertTrue(list.contains("two"), "List should contain legal element");
        assertTrue(list.contains("three"), "List should contain legal element");
    }

    @Test
    public void testSubList() {
        final List<E> list = makeTestList();
        list.add((E) "zero");
        //subList without any element of list
        List<E> subList = list.subList(0, 0);
        assertNotNull(subList);
        assertEquals(0, subList.size());

        //subList with one element oif list
        subList = list.subList(0, 1);
        assertEquals(1, subList.size());

        final List<E> elements = new ArrayList<>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) "three");
        list.addAll(1, elements);
        //subList with all elements of list
        subList = list.subList(0, list.size());
        assertEquals(list.size(), subList.size());
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedList.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedList.fullCollection.version4.obj");
//    }

}
