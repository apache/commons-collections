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
package org.apache.commons.collections4.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractListTest} for exercising the {@link TransformedList}
 * implementation.
 *
 * @since 3.0
 */
public class TransformedListTest<E> extends AbstractListTest<E> {

    public TransformedListTest() {
        super(TransformedListTest.class.getSimpleName());
    }

    @Override
    public List<E> makeConfirmedCollection() {
        return new ArrayList<>();
    }

    @Override
    public List<E> makeConfirmedFullCollection() {
        return new ArrayList<>(Arrays.asList(getFullElements()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> makeObject() {
        return TransformedList.transformingList(new ArrayList<E>(), (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> makeFullCollection() {
        final List<E> list = new ArrayList<>(Arrays.asList(getFullElements()));
        return TransformedList.transformingList(list, (Transformer<E, E>) TransformedCollectionTest.NOOP_TRANSFORMER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTransformedList() {
        final List<E> list = TransformedList.transformingList(new ArrayList<E>(), (Transformer<E, E>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, list.size());
        final E[] els = (E[]) new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        for (int i = 0; i < els.length; i++) {
            list.add(els[i]);
            assertEquals(i + 1, list.size());
            assertTrue(list.contains(Integer.valueOf((String) els[i])));
            assertFalse(list.contains(els[i]));
        }

        assertFalse(list.remove(els[0]));
        assertTrue(list.remove(Integer.valueOf((String) els[0])));

        list.clear();
        for (int i = 0; i < els.length; i++) {
            list.add(0, els[i]);
            assertEquals(i + 1, list.size());
            assertEquals(Integer.valueOf((String) els[i]), list.get(0));
        }

        list.set(0, (E) "22");
        assertEquals(Integer.valueOf(22), list.get(0));

        final ListIterator<E> it = list.listIterator();
        it.next();
        it.set((E) "33");
        assertEquals(Integer.valueOf(33), list.get(0));
        it.add((E) "44");
        assertEquals(Integer.valueOf(44), list.get(1));

        final List<E> adds = new ArrayList<>();
        adds.add((E) "1");
        adds.add((E) "2");
        list.clear();
        list.addAll(adds);
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));

        adds.clear();
        adds.add((E) "3");
        list.addAll(1, adds);
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(3), list.get(1));
        assertEquals(Integer.valueOf(2), list.get(2));
    }

    @Test
    public void testTransformedList_decorateTransform() {
        final List<Object> originalList = new ArrayList<>();
        final Object[] els = {"1", "3", "5", "7", "2", "4", "6"};
        for (final Object el : els) {
            originalList.add(el);
        }
        final List<?> list = TransformedList.transformedList(originalList, TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, list.size());
        for (final Object el : els) {
            assertTrue(list.contains(Integer.valueOf((String) el)));
            assertFalse(list.contains(el));
        }

        assertFalse(list.remove(els[0]));
        assertTrue(list.remove(Integer.valueOf((String) els[0])));
    }

    @Test
    public void testSubList() {
        final List<E> list = makeObject();
        List<E> subList = list.subList(0, 0);
        assertNotNull(subList);
        list.add((E) "zero");
        //subList without any element of list
        subList = list.subList(0, 0);
        assertNotNull(subList);
        assertEquals(0, subList.size());

        //subList with one element oif list
        subList = list.subList(0, 1);
        assertEquals(1, subList.size());

        final List<E> elements = new ArrayList<>();
        elements.add((E) "one");
        elements.add((E) "two");
        elements.add((E) "three");
        list.addAll(1, elements);
        //subList with all elements of list
        subList = list.subList(0, list.size());
        assertEquals(list.size(), subList.size());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedList.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/TransformedList.fullCollection.version4.obj");
//    }

}
