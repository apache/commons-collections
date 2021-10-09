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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;

/**
 * Extension of {@link AbstractListTest} for exercising the
 * {@link PredicatedList} implementation.
 *
 * @since 3.0
 */
public class PredicatedListTest<E> extends AbstractListTest<E> {

    public PredicatedListTest(final String testName) {
        super(testName);
    }

 //-------------------------------------------------------------------

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    protected List<E> decorateList(final List<E> list, final Predicate<E> predicate) {
        return PredicatedList.predicatedList(list, predicate);
    }

    @Override
    public List<E> makeObject() {
        return decorateList(new ArrayList<E>(), truePredicate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] getFullElements() {
        return (E[]) new Object[] { "1", "3", "5", "7", "2", "4", "6" };
    }

//--------------------------------------------------------------------

    protected Predicate<E> testPredicate =
        o -> o instanceof String;

    public List<E> makeTestList() {
        return decorateList(new ArrayList<E>(), testPredicate);
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        final List<E> list = makeTestList();
        final Integer i = Integer.valueOf(3);
        try {
            list.add((E) i);
            fail("Integer should fail string predicate.");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        assertFalse("Collection shouldn't contain illegal element", list.contains(i));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAddAll() {
        final List<E> list = makeTestList();
        final List<E> elements = new ArrayList<>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) Integer.valueOf(3));
        elements.add((E) "four");
        try {
            list.addAll(0, elements);
            fail("Integer should fail string predicate.");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        assertFalse("List shouldn't contain illegal element", list.contains("one"));
        assertFalse("List shouldn't contain illegal element", list.contains("two"));
        assertFalse("List shouldn't contain illegal element", list.contains(Integer.valueOf(3)));
        assertFalse("List shouldn't contain illegal element", list.contains("four"));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalSet() {
        final List<E> list = makeTestList();
        try {
            list.set(0, (E) Integer.valueOf(3));
            fail("Integer should fail string predicate.");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testLegalAddAll() {
        final List<E> list = makeTestList();
        list.add((E) "zero");
        final List<E> elements = new ArrayList<>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) "three");
        list.addAll(1, elements);
        assertTrue("List should contain legal element", list.contains("zero"));
        assertTrue("List should contain legal element", list.contains("one"));
        assertTrue("List should contain legal element", list.contains("two"));
        assertTrue("List should contain legal element", list.contains("three"));
    }

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

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedList.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/PredicatedList.fullCollection.version4.obj");
//    }

}
