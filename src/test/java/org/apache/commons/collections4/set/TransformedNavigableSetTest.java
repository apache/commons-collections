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
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollectionTest;

/**
 * Extension of {@link AbstractNavigableSetTest} for exercising the
 * {@link TransformedNavigableSet} implementation.
 *
 * @since 4.1
 * @version $Id$
 */
public class TransformedNavigableSetTest<E> extends AbstractNavigableSetTest<E> {

    public TransformedNavigableSetTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TransformedNavigableSetTest.class);
    }

    //-----------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
    public NavigableSet<E> makeObject() {
        return TransformedNavigableSet.transformingNavigableSet(new TreeSet<E>(),
                (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public NavigableSet<E> makeFullCollection() {
        final NavigableSet<E> set = new TreeSet<>();
        set.addAll(Arrays.asList(getFullElements()));
        return TransformedNavigableSet.transformingNavigableSet(set,
                (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testTransformedSet() {
        final NavigableSet<E> set = TransformedNavigableSet.transformingNavigableSet(new TreeSet<E>(),
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
        final Set<?> set = TransformedSet.transformedSet(originalSet,
                TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, set.size());
        for (final Object el : els) {
            assertEquals(true, set.contains(Integer.valueOf((String) el)));
        }

        assertEquals(true, set.remove(Integer.valueOf((String) els[0])));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedNavigableSet.emptyCollection.version4.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedNavigableSet.fullCollection.version4.1.obj");
//    }

}
