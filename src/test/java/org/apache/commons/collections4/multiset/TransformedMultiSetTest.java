/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.multiset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractMultiSetTest} for exercising the
 * {@link TransformedMultiSet} implementation.
 */
public class TransformedMultiSetTest<T> extends AbstractMultiSetTest<T> {

    @Override
    public String getCompatibilityVersion() {
        return "4.6";
    }

    @Override
    protected int getIterationBehaviour() {
        return UNORDERED;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultiSet<T> makeObject() {
        return TransformedMultiSet.transformingMultiSet(new HashMultiSet<>(),
                (Transformer<T, T>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSetCount() {
        //T had better be Object!
        final MultiSet<T> multiset = TransformedMultiSet.transformingMultiSet(new HashMultiSet<>(),
                (Transformer<T, T>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        multiset.setCount((T) "3", 4);
        assertEquals(4, multiset.getCount(Integer.valueOf(3)));
        assertEquals(0, multiset.getCount("3"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTransformedMultiSet() {
        //T had better be Object!
        final MultiSet<T> multiset = TransformedMultiSet.transformingMultiSet(new HashMultiSet<>(),
                (Transformer<T, T>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertTrue(multiset.isEmpty());
        final Object[] els = {"1", "3", "5", "7", "2", "4", "6"};
        for (int i = 0; i < els.length; i++) {
            multiset.add((T) els[i]);
            assertEquals(i + 1, multiset.size());
            assertTrue(multiset.contains(Integer.valueOf((String) els[i])));
            assertFalse(multiset.contains(els[i]));
        }

        assertFalse(multiset.remove(els[0]));
        assertTrue(multiset.remove(Integer.valueOf((String) els[0])));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTransformedMultiSet_decorateTransform() {
        final MultiSet<T> originalMultiSet = new HashMultiSet<>();
        final Object[] els = {"1", "3", "5", "7", "2", "4", "6"};
        for (final Object el : els) {
            originalMultiSet.add((T) el);
        }
        final MultiSet<T> multiset = TransformedMultiSet.transformedMultiSet(originalMultiSet,
                (Transformer<T, T>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, multiset.size());
        for (final Object el : els) {
            assertTrue(multiset.contains(Integer.valueOf((String) el)));
            assertFalse(multiset.contains(el));
        }

        assertFalse(multiset.remove(els[0]));
        assertTrue(multiset.remove(Integer.valueOf((String) els[0])));
    }

//    void testCreate() throws Exception {
//        MultiSet<T> multiset = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/TransformedMultiSet.emptyCollection.version4.6.obj");
//        multiset = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/TransformedMultiSet.fullCollection.version4.6.obj");
//    }

}
