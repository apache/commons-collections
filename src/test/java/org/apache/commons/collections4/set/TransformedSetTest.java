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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollectionTest;

/**
 * Extension of {@link AbstractSetTest} for exercising the {@link TransformedSet}
 * implementation.
 *
 * @since 3.0
 * @version $Id$
 */
public class TransformedSetTest<E> extends AbstractSetTest<E> {

    public TransformedSetTest(final String testName) {
        super(testName);
    }

    @Override
    public Set<E> makeConfirmedCollection() {
        return new HashSet<E>();
    }

    @Override
    public Set<E> makeConfirmedFullCollection() {
        final Set<E> set = new HashSet<E>();
        set.addAll(Arrays.asList(getFullElements()));
        return set;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<E> makeObject() {
        return TransformedSet.transformingSet(new HashSet<E>(),
                (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<E> makeFullCollection() {
        final Set<E> list = new HashSet<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return TransformedSet.transformingSet(list,
                (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @SuppressWarnings("unchecked")
    public void testTransformedSet() {
        final Set<E> set = TransformedSet.transformingSet(new HashSet<E>(),
                (Transformer<E, E>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, set.size());
        final E[] els = (E[]) new Object[] { "1", "3", "5", "7", "2", "4", "6" };
        for (int i = 0; i < els.length; i++) {
            set.add(els[i]);
            assertEquals(i + 1, set.size());
            assertEquals(true, set.contains(new Integer((String) els[i])));
            assertEquals(false, set.contains(els[i]));
        }

        assertEquals(false, set.remove(els[0]));
        assertEquals(true, set.remove(new Integer((String) els[0])));

    }

    public void testTransformedSet_decorateTransform() {
        final Set<Object> originalSet = new HashSet<Object>();
        final Object[] els = new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        for (final Object el : els) {
            originalSet.add(el);
        }
        final Set<?> set = TransformedSet.transformedSet(originalSet, TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, set.size());
        for (final Object el : els) {
            assertEquals(true, set.contains(new Integer((String) el)));
            assertEquals(false, set.contains(el));
        }
        
        assertEquals(false, set.remove(els[0]));
        assertEquals(true, set.remove(new Integer((String) els[0])));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    public void testCreate() throws Exception {
        resetEmpty();
        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedSet.emptyCollection.version4.obj");
        resetFull();
        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedSet.fullCollection.version4.obj");
    }

}
