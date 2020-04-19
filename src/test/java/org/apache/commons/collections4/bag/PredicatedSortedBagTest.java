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
package org.apache.commons.collections4.bag;

import java.util.Comparator;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.SortedBag;
import org.apache.commons.collections4.functors.TruePredicate;

/**
 * Extension of {@link AbstractSortedBagTest} for exercising the {@link PredicatedSortedBag}
 * implementation.
 *
 * @since 3.0
 */
public class PredicatedSortedBagTest<T> extends AbstractSortedBagTest<T> {

    private final SortedBag<T> nullBag = null;

    public PredicatedSortedBagTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(PredicatedSortedBagTest.class);
    }

    //--------------------------------------------------------------------------

    protected Predicate<T> stringPredicate() {
        return o -> o instanceof String;
    }

    protected Predicate<T> truePredicate = TruePredicate.<T>truePredicate();

    protected SortedBag<T> decorateBag(final SortedBag<T> bag, final Predicate<T> predicate) {
        return PredicatedSortedBag.predicatedSortedBag(bag, predicate);
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
        final SortedBag<T> bag = decorateBag(new TreeBag<T>(), stringPredicate());
        ((PredicatedSortedBag<T>) bag).decorated();
        try {
            decorateBag(new TreeBag<T>(), null);
            fail("Expecting NullPointerException for null predicate");
        } catch (final NullPointerException e) {}
        try {
            decorateBag(nullBag, stringPredicate());
            fail("Expecting NullPointerException for null bag");
        } catch (final NullPointerException e) {}
    }

    @SuppressWarnings("unchecked")
    public void testSortOrder() {
        final SortedBag<T> bag = decorateBag(new TreeBag<T>(), stringPredicate());
        final String one = "one";
        final String two = "two";
        final String three = "three";
        bag.add((T) one);
        bag.add((T) two);
        bag.add((T) three);
        assertEquals("first element", bag.first(), one);
        assertEquals("last element", bag.last(), two);
        final Comparator<? super T> c = bag.comparator();
        assertNull("natural order, so comparator should be null", c);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        Bag<T> bag = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/PredicatedSortedBag.emptyCollection.version4.obj");
//        bag = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/PredicatedSortedBag.fullCollection.version4.obj");
//    }

}
