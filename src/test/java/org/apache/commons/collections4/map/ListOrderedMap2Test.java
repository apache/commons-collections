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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.list.AbstractListTest;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractOrderedMapTest} for exercising the {@link ListOrderedMap} implementation.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class ListOrderedMap2Test<K, V> extends AbstractOrderedMapTest<K, V> {

    public class TestListView extends AbstractListTest<K> {

        @Override
        public K[] getFullElements() {
            return getSampleKeys();
        }

        @Override
        public boolean isAddSupported() {
            return false;
        }

        @Override
        public boolean isNullSupported() {
            return isAllowNullKey();
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
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public List<K> makeFullCollection() {
            return ListOrderedMap2Test.this.makeFullMap().asList();
        }

        @Override
        public List<K> makeObject() {
            return ListOrderedMap2Test.this.makeObject().asList();
        }
    }

    public BulkTest bulkTestListView() {
        return new TestListView();
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListOrderedMap<K, V> getMap() {
        return (ListOrderedMap<K, V>) super.getMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListOrderedMap<K, V> makeFullMap() {
        return (ListOrderedMap<K, V>) super.makeFullMap();
    }

    @Override
    public ListOrderedMap<K, V> makeObject() {
        return new ListOrderedMap<>();
    }

    @Test
    public void testGetByIndex() {
        resetEmpty();
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().get(-1));
        resetFull();
        final ListOrderedMap<K, V> lom = getMap();
        assertThrows(IndexOutOfBoundsException.class, () -> lom.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lom.get(lom.size()));
        int i = 0;
        for (final MapIterator<K, V> it = lom.mapIterator(); it.hasNext(); i++) {
            assertSame(it.next(), lom.get(i));
        }
    }

    @Test
    public void testGetValueByIndex() {
        resetEmpty();
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().getValue(0));
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().getValue(-1));
        resetFull();
        final ListOrderedMap<K, V> lom = getMap();
        assertThrows(IndexOutOfBoundsException.class, () -> lom.getValue(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lom.getValue(lom.size()));
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

    @Test
    public void testRemoveByIndex() {
        resetEmpty();
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().remove(-1));
        resetFull();
        final ListOrderedMap<K, V> lom = getMap();
        assertThrows(IndexOutOfBoundsException.class, () -> lom.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lom.remove(lom.size()));
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
}
