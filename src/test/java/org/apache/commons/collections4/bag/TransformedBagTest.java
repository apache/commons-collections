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
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollectionTest;
import org.junit.Test;

/**
 * Extension of {@link AbstractBagTest} for exercising the {@link TransformedBag}
 * implementation.
 *
 * @since 3.0
 */
public class TransformedBagTest<T> extends AbstractBagTest<T> {

    public TransformedBagTest(final String testName) {
        super(testName);
    }

    public static junit.framework.Test suite() {
        return BulkTest.makeSuite(TransformedBagTest.class);
    }


    @Override
    @SuppressWarnings("unchecked")
    public Bag<T> makeObject() {
        return TransformedBag.transformingBag(new HashBag<T>(),
                (Transformer<T, T>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTransformedBag() {
        //T had better be Object!
        final Bag<T> bag = TransformedBag.transformingBag(new HashBag<T>(),
                (Transformer<T, T>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertTrue(bag.isEmpty());
        final Object[] els = {"1", "3", "5", "7", "2", "4", "6"};
        for (int i = 0; i < els.length; i++) {
            bag.add((T) els[i]);
            assertEquals(i + 1, bag.size());
            assertTrue(bag.contains(Integer.valueOf((String) els[i])));
            assertFalse(bag.contains(els[i]));
        }

        assertFalse(bag.remove(els[0]));
        assertTrue(bag.remove(Integer.valueOf((String) els[0])));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTransformedBag_decorateTransform() {
        final Bag<T> originalBag = new HashBag<>();
        final Object[] els = {"1", "3", "5", "7", "2", "4", "6"};
        for (final Object el : els) {
            originalBag.add((T) el);
        }
        final Bag<T> bag = TransformedBag.transformedBag(originalBag,
                (Transformer<T, T>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, bag.size());
        for (final Object el : els) {
            assertTrue(bag.contains(Integer.valueOf((String) el)));
            assertFalse(bag.contains(el));
        }

        assertFalse(bag.remove(els[0]));
        assertTrue(bag.remove(Integer.valueOf((String) els[0])));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        Bag<T> bag = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/TransformedBag.emptyCollection.version4.obj");
//        bag = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/TransformedBag.fullCollection.version4.obj");
//    }

}
