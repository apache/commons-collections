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
package org.apache.commons.collections.bag;

import java.util.Comparator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.functors.TruePredicate;

/**
 * Extension of {@link AbstractTestSortedBag} for exercising the {@link PredicatedSortedBag}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Phil Steitz
 */
public class TestPredicatedSortedBag<T> extends AbstractTestSortedBag<T> {

    private SortedBag<T> nullBag = null;

    public TestPredicatedSortedBag(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestPredicatedSortedBag.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestPredicatedSortedBag.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    //--------------------------------------------------------------------------

    protected Predicate<T> stringPredicate() {
        return new Predicate<T>() {
            public boolean evaluate(T o) {
                return o instanceof String;
            }
        };
    }

    protected Predicate<T> truePredicate = TruePredicate.<T>truePredicate();

    protected SortedBag<T> decorateBag(SortedBag<T> bag, Predicate<T> predicate) {
        return PredicatedSortedBag.decorate(bag, predicate);
    }

    @Override
    public SortedBag<T> makeObject() {
        return decorateBag(new TreeBag<T>(), truePredicate);
    }

    protected SortedBag<T> makeTestBag() {
        return decorateBag(new TreeBag<T>(), stringPredicate());
    }

    //--------------------------------------------------------------------------

    public void testDecorate() {
        SortedBag<T> bag = decorateBag(new TreeBag<T>(), stringPredicate());
        ((PredicatedSortedBag<T>) bag).decorated();
        try {
            decorateBag(new TreeBag<T>(), null);
            fail("Expecting IllegalArgumentException for null predicate");
        } catch (IllegalArgumentException e) {}
        try {
            decorateBag(nullBag, stringPredicate());
            fail("Expecting IllegalArgumentException for null bag");
        } catch (IllegalArgumentException e) {}
    }

    @SuppressWarnings("unchecked")
    public void testSortOrder() {
        SortedBag<T> bag = decorateBag(new TreeBag<T>(), stringPredicate());
        String one = "one";
        String two = "two";
        String three = "three";
        bag.add((T) one);
        bag.add((T) two);
        bag.add((T) three);
        assertEquals("first element", bag.first(), one);
        assertEquals("last element", bag.last(), two);
        Comparator<? super T> c = bag.comparator();
        assertTrue("natural order, so comparator should be null", c == null);
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        Bag bag = makeBag();
//        writeExternalFormToDisk((java.io.Serializable) bag, "D:/dev/collections/data/test/PredicatedSortedBag.emptyCollection.version3.1.obj");
//        bag = makeBag();
//        bag.add("A");
//        bag.add("A");
//        bag.add("B");
//        bag.add("B");
//        bag.add("C");
//        writeExternalFormToDisk((java.io.Serializable) bag, "D:/dev/collections/data/test/PredicatedSortedBag.fullCollection.version3.1.obj");
//    }

}
