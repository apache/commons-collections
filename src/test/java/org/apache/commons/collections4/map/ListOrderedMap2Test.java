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
package org.apache.commons.collections4.map;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.list.AbstractListTest;
import org.junit.Test;

/**
 * Extension of {@link AbstractOrderedMapTest} for exercising the {@link ListOrderedMap}
 * implementation.
 *
 * @since 3.1
 */
public class ListOrderedMap2Test<K, V> extends AbstractOrderedMapTest<K, V> {

    public ListOrderedMap2Test(final String testName) {
        super(testName);
    }

    public static junit.framework.Test suite() {
        return BulkTest.makeSuite(ListOrderedMap2Test.class);
    }

    @Override
    public ListOrderedMap<K, V> makeObject() {
        return new ListOrderedMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListOrderedMap<K, V> makeFullMap() {
        return (ListOrderedMap<K, V>) super.makeFullMap();
    }

    @Test
    public void testGetByIndex() {
        resetEmpty();
        ListOrderedMap<K, V> lom = getMap();
        try {
            lom.get(0);
        } catch (final IndexOutOfBoundsException ex) {}
        try {
            lom.get(-1);
        } catch (final IndexOutOfBoundsException ex) {}

        resetFull();
        lom = getMap();
        try {
            lom.get(-1);
        } catch (final IndexOutOfBoundsException ex) {}
        try {
            lom.get(lom.size());
        } catch (final IndexOutOfBoundsException ex) {}

        int i = 0;
        for (final MapIterator<K, V> it = lom.mapIterator(); it.hasNext(); i++) {
            assertSame(it.next(), lom.get(i));
        }
    }

    @Test
    public void testGetValueByIndex() {
        resetEmpty();
        ListOrderedMap<K, V> lom = getMap();
        try {
            lom.getValue(0);
        } catch (final IndexOutOfBoundsException ex) {}
        try {
            lom.getValue(-1);
        } catch (final IndexOutOfBoundsException ex) {}

        resetFull();
        lom = getMap();
        try {
            lom.getValue(-1);
        } catch (final IndexOutOfBoundsException ex) {}
        try {
            lom.getValue(lom.size());
        } catch (final IndexOutOfBoundsException ex) {}

        int i = 0;
        for (final MapIterator<K, V> it = lom.mapIterator(); it.hasNext(); i++) {
            it.next();
            assertSame(it.getValue(), lom.getValue(i));
        }
    }

    @Test
    public void testIndexOf() {
        resetEmpty();
        ListOrderedMap<K, V> lom = getMap();
        assertEquals(-1, lom.indexOf(getOtherKeys()));

        resetFull();
        lom = getMap();
        final List<K> list = new ArrayList<>();
        for (final MapIterator<K, V> it = lom.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            assertEquals(i, lom.indexOf(list.get(i)));
        }
    }

    @Test
    public void testRemoveByIndex() {
        resetEmpty();
        ListOrderedMap<K, V> lom = getMap();
        try {
            lom.remove(0);
        } catch (final IndexOutOfBoundsException ex) {}
        try {
            lom.remove(-1);
        } catch (final IndexOutOfBoundsException ex) {}

        resetFull();
        lom = getMap();
        try {
            lom.remove(-1);
        } catch (final IndexOutOfBoundsException ex) {}
        try {
            lom.remove(lom.size());
        } catch (final IndexOutOfBoundsException ex) {}

        final List<K> list = new ArrayList<>();
        for (final MapIterator<K, V> it = lom.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            final Object key = list.get(i);
            final Object value = lom.get(key);
            assertEquals(value, lom.remove(i));
            list.remove(i);
            assertFalse(lom.containsKey(key));
        }
    }

    public BulkTest bulkTestListView() {
        return new TestListView();
    }

    public class TestListView extends AbstractListTest<K> {

        TestListView() {
            super("TestListView");
        }

        @Override
        public List<K> makeObject() {
            return ListOrderedMap2Test.this.makeObject().asList();
        }

        @Override
        public List<K> makeFullCollection() {
            return ListOrderedMap2Test.this.makeFullMap().asList();
        }

        @Override
        public K[] getFullElements() {
            return ListOrderedMap2Test.this.getSampleKeys();
        }
        @Override
        public boolean isAddSupported() {
            return false;
        }
        @Override
        public boolean isRemoveSupported() {
            return false;
        }
        @Override
        public boolean isSetSupported() {
            return false;
        }
        @Override
        public boolean isNullSupported() {
            return ListOrderedMap2Test.this.isAllowNullKey();
        }
        @Override
        public boolean isTestSerialization() {
            return false;
        }
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/ListOrderedMap.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/ListOrderedMap.fullCollection.version3.1.obj");
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListOrderedMap<K, V> getMap() {
        return (ListOrderedMap<K, V>) super.getMap();
    }
}
