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
package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;

/**
 * Extension of {@link TestList} for exercising the
 * {@link PredicatedList} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Phil Steitz
 */
public class TestPredicatedList<E> extends AbstractTestList<E> {

    public TestPredicatedList(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestPredicatedList.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestPredicatedList.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

 //-------------------------------------------------------------------

    protected Predicate<E> truePredicate = TruePredicate.<E>truePredicate();

    protected List<E> decorateList(List<E> list, Predicate<E> predicate) {
        return PredicatedList.decorate(list, predicate);
    }

    public List<E> makeObject() {
        return decorateList(new ArrayList<E>(), truePredicate);
    }

    @SuppressWarnings("unchecked")
    public E[] getFullElements() {
        return (E[]) new Object[] { "1", "3", "5", "7", "2", "4", "6" };
    }

//--------------------------------------------------------------------

    protected Predicate<E> testPredicate =
        new Predicate<E>() {
            public boolean evaluate(E o) {
                return o instanceof String;
            }
        };

    public List<E> makeTestList() {
        return decorateList(new ArrayList<E>(), testPredicate);
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        List<E> list = makeTestList();
        Integer i = new Integer(3);
        try {
            list.add((E) i);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element",
         !list.contains(i));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAddAll() {
        List<E> list = makeTestList();
        List<E> elements = new ArrayList<E>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) new Integer(3));
        elements.add((E) "four");
        try {
            list.addAll(0, elements);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("List shouldn't contain illegal element",
         !list.contains("one"));
        assertTrue("List shouldn't contain illegal element",
         !list.contains("two"));
        assertTrue("List shouldn't contain illegal element",
         !list.contains(new Integer(3)));
        assertTrue("List shouldn't contain illegal element",
         !list.contains("four"));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalSet() {
        List<E> list = makeTestList();
        try {
            list.set(0, (E) new Integer(3));
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testLegalAddAll() {
        List<E> list = makeTestList();
        list.add((E) "zero");
        List<E> elements = new ArrayList<E>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) "three");
        list.addAll(1,elements);
        assertTrue("List should contain legal element",
         list.contains("zero"));
        assertTrue("List should contain legal element",
         list.contains("one"));
        assertTrue("List should contain legal element",
         list.contains("two"));
        assertTrue("List should contain legal element",
         list.contains("three"));
    }

    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/PredicatedList.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/PredicatedList.fullCollection.version3.1.obj");
//    }

}
