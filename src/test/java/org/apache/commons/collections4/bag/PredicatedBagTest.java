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

import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;

/**
 * Extension of {@link AbstractBagTest} for exercising the {@link PredicatedBag}
 * implementation.
 *
 * @since 3.0
 */
public class PredicatedBagTest<T> extends AbstractBagTest<T> {

    public PredicatedBagTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(PredicatedBagTest.class);
    }

    //--------------------------------------------------------------------------

    protected Predicate<T> stringPredicate() {
        return new Predicate<T>() {
            @Override
            public boolean evaluate(final T o) {
                return o instanceof String;
            }
        };
    }

    protected Predicate<T> truePredicate = TruePredicate.<T>truePredicate();

    protected Bag<T> decorateBag(final HashBag<T> bag, final Predicate<T> predicate) {
        return PredicatedBag.predicatedBag(bag, predicate);
    }

    @Override
    public Bag<T> makeObject() {
        return decorateBag(new HashBag<T>(), truePredicate);
    }

    protected Bag<T> makeTestBag() {
        return decorateBag(new HashBag<T>(), stringPredicate());
    }

    //--------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void testlegalAddRemove() {
        final Bag<T> bag = makeTestBag();
        assertEquals(0, bag.size());
        final T[] els = (T[]) new Object[] { "1", "3", "5", "7", "2", "4", "1" };
        for (int i = 0; i < els.length; i++) {
            bag.add(els[i]);
            assertEquals(i + 1, bag.size());
            assertEquals(true, bag.contains(els[i]));
        }
        Set<T> set = ((PredicatedBag<T>) bag).uniqueSet();
        assertTrue("Unique set contains the first element",set.contains(els[0]));
        assertEquals(true, bag.remove(els[0]));
        set = ((PredicatedBag<T>) bag).uniqueSet();
        assertTrue("Unique set now does not contain the first element",
            !set.contains(els[0]));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalAdd() {
        final Bag<T> bag = makeTestBag();
        final Integer i = Integer.valueOf(3);
        try {
            bag.add((T) i);
            fail("Integer should fail string predicate.");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element",
         !bag.contains(i));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalDecorate() {
        final HashBag<Object> elements = new HashBag<>();
        elements.add("one");
        elements.add("two");
        elements.add(Integer.valueOf(3));
        elements.add("four");
        try {
            decorateBag((HashBag<T>) elements, stringPredicate());
            fail("Bag contains an element that should fail the predicate.");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            decorateBag(new HashBag<T>(), null);
            fail("Expecting NullPointerException for null predicate.");
        } catch (final NullPointerException e) {
            // expected
        }
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        Bag<T> bag = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/PredicatedBag.emptyCollection.version4.obj");
//        bag = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/PredicatedBag.fullCollection.version4.obj");
//    }

}
