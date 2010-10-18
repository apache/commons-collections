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

import java.util.Set;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;

/**
 * Extension of {@link AbstractTestBag} for exercising the {@link PredicatedBag}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Phil Steitz
 */
public class TestPredicatedBag<T> extends AbstractTestBag<T> {

    public TestPredicatedBag(String testName) {
        super(testName);
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

    protected Bag<T> decorateBag(HashBag<T> bag, Predicate<T> predicate) {
        return PredicatedBag.decorate(bag, predicate);
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
        Bag<T> bag = makeTestBag();
        assertEquals(0, bag.size());
        T[] els = (T[]) new Object[] { "1", "3", "5", "7", "2", "4", "1" };
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
        Bag<T> bag = makeTestBag();
        Integer i = new Integer(3);
        try {
            bag.add((T) i);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element",
         !bag.contains(i));
    }

    @SuppressWarnings("unchecked")
    public void testIllegalDecorate() {
        HashBag<Object> elements = new HashBag<Object>();
        elements.add("one");
        elements.add("two");
        elements.add(new Integer(3));
        elements.add("four");
        try {
            decorateBag((HashBag<T>) elements, stringPredicate());
            fail("Bag contains an element that should fail the predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            decorateBag(new HashBag<T>(), null);
            fail("Expectiing IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        Bag bag = makeBag();
//        writeExternalFormToDisk((java.io.Serializable) bag, "D:/dev/collections/data/test/PredicatedBag.emptyCollection.version3.1.obj");
//        bag = makeBag();
//        bag.add("A");
//        bag.add("A");
//        bag.add("B");
//        bag.add("B");
//        bag.add("C");
//        writeExternalFormToDisk((java.io.Serializable) bag, "D:/dev/collections/data/test/PredicatedBag.fullCollection.version3.1.obj");
//    }

}
