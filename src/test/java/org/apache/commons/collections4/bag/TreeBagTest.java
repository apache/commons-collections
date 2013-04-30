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

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.SortedBag;
import org.apache.commons.collections4.bag.TreeBag;

/**
 * Extension of {@link AbstractBagTest} for exercising the {@link TreeBag}
 * implementation.
 *
 * @version $Id$
 */
public class TreeBagTest<T> extends AbstractSortedBagTest<T> {

    public TreeBagTest(final String testName) {
        super(testName);
    }

    @Override
    public SortedBag<T> makeObject() {
        return new TreeBag<T>();
    }

    @SuppressWarnings("unchecked")
    public SortedBag<T> setupBag() {
        final SortedBag<T> bag = makeObject();
        bag.add((T) "C");
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "D");
        return bag;
    }

    public void testCollections265() {
        final Bag<Object> bag = new TreeBag<Object>();
        try {
            bag.add(new Object());
            fail("IllegalArgumentException expected");
        } catch(final IllegalArgumentException iae) {
            // expected;
        }
    }

    public void testOrdering() {
        final Bag<T> bag = setupBag();
        assertEquals("Should get elements in correct order", "A", bag.toArray()[0]);
        assertEquals("Should get elements in correct order", "B", bag.toArray()[1]);
        assertEquals("Should get elements in correct order", "C", bag.toArray()[2]);
        assertEquals("Should get first key", "A", ((SortedBag<T>) bag).first());
        assertEquals("Should get last key", "D", ((SortedBag<T>) bag).last());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        Bag bag = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/TreeBag.emptyCollection.version4.obj");
//        bag = makeObject();
//        bag.add("A");
//        bag.add("A");
//        bag.add("B");
//        bag.add("B");
//        bag.add("C");
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/TreeBag.fullCollection.version4.obj");
//    }
}
