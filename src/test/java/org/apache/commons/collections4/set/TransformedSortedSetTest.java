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
package org.apache.commons.collections4.set;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Set;
import java.util.SortedSet;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollectionTest;

/**
 * Extension of {@link AbstractSortedSetTest} for exercising the {@link TransformedSortedSet}
 * implementation.
 *
 * @since 3.0
 */
public class TransformedSortedSetTest<E> extends AbstractSortedSetTest<E> {

    public TransformedSortedSetTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TransformedSortedSetTest.class);
    }

    //-----------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
    public SortedSet<E> makeObject() {
        return TransformedSortedSet.transformingSortedSet(new TreeSet<E>(), (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedSet<E> makeFullCollection() {
        final SortedSet<E> set = new TreeSet<>();
        set.addAll(Arrays.asList(getFullElements()));
        return TransformedSortedSet.transformingSortedSet(set, (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testTransformedSet() {
        final SortedSet<E> set = TransformedSortedSet.transformingSortedSet(new TreeSet<E>(),
                (Transformer<E, E>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, set.size());
        final E[] els = (E[]) new Object[] { "1", "3", "5", "7", "2", "4", "6" };
        for (int i = 0; i < els.length; i++) {
            set.add(els[i]);
            assertEquals(i + 1, set.size());
            assertEquals(true, set.contains(Integer.valueOf((String) els[i])));
        }

        assertEquals(true, set.remove(Integer.valueOf((String) els[0])));
    }

    public void testTransformedSet_decorateTransform() {
        final Set<Object> originalSet = new TreeSet<>();
        final Object[] els = new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        Collections.addAll(originalSet, els);
        final Set<?> set = TransformedSet.transformedSet(originalSet, TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, set.size());
        for (final Object el : els) {
            assertEquals(true, set.contains(Integer.valueOf((String) el)));
        }

        assertEquals(true, set.remove(Integer.valueOf((String) els[0])));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedSortedSet.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedSortedSet.fullCollection.version4.obj");
//    }

}
