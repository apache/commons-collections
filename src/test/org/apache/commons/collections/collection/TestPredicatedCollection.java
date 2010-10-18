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
package org.apache.commons.collections.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;

/**
 * Extension of {@link AbstractTestCollection} for exercising the 
 * {@link PredicatedCollection} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Phil Steitz
 */
public class TestPredicatedCollection<E> extends AbstractTestCollection<E> {

    public TestPredicatedCollection(String name) {
        super(name);
    }

   //------------------------------------------------------------------------
    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    protected Collection<E> decorateCollection(
                Collection<E> collection, Predicate<E> predicate) {
        return PredicatedCollection.decorate(collection, predicate);
    }

    @Override
    public Collection<E> makeObject() {
        return decorateCollection(new ArrayList<E>(), truePredicate);
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<E>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] getFullElements() {
        return (E[]) new Object[] { "1", "3", "5", "7", "2", "4", "6" };
    }

    @Override
    public Collection<E> makeFullCollection() {
        List<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return decorateCollection(list, truePredicate);
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        List<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    //-----------------------------------------------------------------------
    protected Predicate<E> testPredicate =
        new Predicate<E>() {
            public boolean evaluate(E o) {
                return o instanceof String;
            }
        };

    public Collection<E> makeTestCollection() {
        return decorateCollection(new ArrayList<E>(), testPredicate);
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        Collection<E> c = makeTestCollection();
        Integer i = new Integer(3);
        try {
            c.add((E) i);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element",
         !c.contains(i));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAddAll() {
        Collection<E> c = makeTestCollection();
        List<E> elements = new ArrayList<E>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) new Integer(3));
        elements.add((E) "four");
        try {
            c.addAll(elements);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", !c.contains("one"));
        assertTrue("Collection shouldn't contain illegal element", !c.contains("two"));
        assertTrue("Collection shouldn't contain illegal element", !c.contains(new Integer(3)));
        assertTrue("Collection shouldn't contain illegal element", !c.contains("four"));
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/PredicatedCollection.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/PredicatedCollection.fullCollection.version3.1.obj");
//    }

}
