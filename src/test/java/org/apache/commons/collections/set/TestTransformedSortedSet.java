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
package org.apache.commons.collections.set;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.Set;
import java.util.SortedSet;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.collection.TestTransformedCollection;

/**
 * Extension of {@link AbstractTestSortedSet} for exercising the {@link TransformedSortedSet}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public class TestTransformedSortedSet<E> extends AbstractTestSortedSet<E> {

    public TestTransformedSortedSet(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestTransformedSortedSet.class);
    }

    //-----------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
    public SortedSet<E> makeObject() {
        return TransformedSortedSet.transformingSortedSet(new TreeSet<E>(), (Transformer<E, E>) TestTransformedCollection.NOOP_TRANSFORMER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedSet<E> makeFullCollection() {
        SortedSet<E> set = new TreeSet<E>();
        set.addAll(Arrays.asList(getFullElements()));
        return TransformedSortedSet.transformingSortedSet(set, (Transformer<E, E>) TestTransformedCollection.NOOP_TRANSFORMER);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testTransformedSet() {
        SortedSet<E> set = TransformedSortedSet.transformingSortedSet(new TreeSet<E>(),
                (Transformer<E, E>) TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, set.size());
        E[] els = (E[]) new Object[] { "1", "3", "5", "7", "2", "4", "6" };
        for (int i = 0; i < els.length; i++) {
            set.add(els[i]);
            assertEquals(i + 1, set.size());
            assertEquals(true, set.contains(new Integer((String) els[i])));
        }

        assertEquals(true, set.remove(new Integer((String) els[0])));
    }

    public void testTransformedSet_decorateTransform() {
        Set<Object> originalSet = new TreeSet<Object>();
        Object[] els = new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        for (int i = 0; i < els.length; i++) {
            originalSet.add(els[i]);
        }
        Set<?> set = TransformedSortedSet.transformedSet(originalSet, TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, set.size());
        for (int i = 0; i < els.length; i++) {
            assertEquals(true, set.contains(new Integer((String) els[i])));
        }
        
        assertEquals(true, set.remove(new Integer((String) els[0])));
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/TransformedSortedSet.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/TransformedSortedSet.fullCollection.version3.1.obj");
//    }

}
