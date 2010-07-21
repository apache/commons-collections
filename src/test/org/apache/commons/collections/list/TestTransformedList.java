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
package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.collection.TestTransformedCollection;

/**
 * Extension of {@link AbstractTestList} for exercising the {@link TransformedList}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public class TestTransformedList<E> extends AbstractTestList<E> {

    public TestTransformedList(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTransformedList.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestTransformedList.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    @Override
    public List<E> makeConfirmedCollection() {
        return new ArrayList<E>();
    }

    @Override
    public List<E> makeConfirmedFullCollection() {
        List<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> makeObject() {
        return TransformedList.decorate(new ArrayList<E>(), (Transformer<E, E>) TestTransformedCollection.NOOP_TRANSFORMER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> makeFullCollection() {
        List<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return TransformedList.decorate(list, (Transformer<E, E>) TestTransformedCollection.NOOP_TRANSFORMER);
    }

    @SuppressWarnings("unchecked")
    public void testTransformedList() {
        List<E> list = TransformedList.decorate(new ArrayList<E>(), (Transformer<E, E>) TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, list.size());
        E[] els = (E[]) new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        for (int i = 0; i < els.length; i++) {
            list.add(els[i]);
            assertEquals(i + 1, list.size());
            assertEquals(true, list.contains(new Integer((String) els[i])));
            assertEquals(false, list.contains(els[i]));
        }

        assertEquals(false, list.remove(els[0]));
        assertEquals(true, list.remove(new Integer((String) els[0])));

        list.clear();
        for (int i = 0; i < els.length; i++) {
            list.add(0, els[i]);
            assertEquals(i + 1, list.size());
            assertEquals(new Integer((String) els[i]), list.get(0));
        }

        list.set(0, (E) "22");
        assertEquals(new Integer(22), list.get(0));

        ListIterator<E> it = list.listIterator();
        it.next();
        it.set((E) "33");
        assertEquals(new Integer(33), list.get(0));
        it.add((E) "44");
        assertEquals(new Integer(44), list.get(1));

        List<E> adds = new ArrayList<E>();
        adds.add((E) "1");
        adds.add((E) "2");
        list.clear();
        list.addAll(adds);
        assertEquals(new Integer(1), list.get(0));
        assertEquals(new Integer(2), list.get(1));

        adds.clear();
        adds.add((E) "3");
        list.addAll(1, adds);
        assertEquals(new Integer(1), list.get(0));
        assertEquals(new Integer(3), list.get(1));
        assertEquals(new Integer(2), list.get(2));
    }

    public void testTransformedList_decorateTransform() {
        List originalList = new ArrayList();
        Object[] els = new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        for (int i = 0; i < els.length; i++) {
            originalList.add(els[i]);
        }
        List list = TransformedList.decorateTransform(originalList, TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, list.size());
        for (int i = 0; i < els.length; i++) {
            assertEquals(true, list.contains(new Integer((String) els[i])));
            assertEquals(false, list.contains(els[i]));
        }
        
        assertEquals(false, list.remove(els[0]));
        assertEquals(true, list.remove(new Integer((String) els[0])));
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/TransformedList.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/TransformedList.fullCollection.version3.1.obj");
//    }

}
