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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.list.AbstractListTest;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class LinkedMapTest<K, V> extends AbstractOrderedMapTest<K, V> {

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
            return LinkedMapTest.this.makeFullMap().asList();
        }

        @Override
        public List<K> makeObject() {
            return LinkedMapTest.this.makeObject().asList();
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
    public LinkedMap<K, V> getMap() {
        return (LinkedMap<K, V>) super.getMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkedMap<K, V> makeFullMap() {
        return (LinkedMap<K, V>) super.makeFullMap();
    }

    @Override
    public LinkedMap<K, V> makeObject() {
        return new LinkedMap<>();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClone() {
        final LinkedMap<K, V> map = new LinkedMap<>(10);
        map.put((K) "1", (V) "1");
        final Map<K, V> cloned = map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get("1"), cloned.get("1"));
    }

    @Test
    public void testGetByIndex() {
        resetEmpty();
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().get(-1));
        resetFull();
        final LinkedMap<K, V> lm = getMap();
        assertThrows(IndexOutOfBoundsException.class, () -> lm.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lm.get(lm.size()));
        int i = 0;
        for (final MapIterator<K, V> it = lm.mapIterator(); it.hasNext(); i++) {
            assertSame(it.next(), lm.get(i));
        }
    }

    @Test
    public void testGetValueByIndex() {
        resetEmpty();
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().getValue(0));
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().getValue(-1));
        resetFull();
        final LinkedMap<K, V> lm = getMap();
        assertThrows(IndexOutOfBoundsException.class, () -> lm.getValue(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lm.getValue(lm.size()));
        int i = 0;
        for (final MapIterator<K, V> it = lm.mapIterator(); it.hasNext(); i++) {
            it.next();
            assertSame(it.getValue(), lm.getValue(i));
        }
    }

    @Test
    public void testIndexOf() {
        resetEmpty();
        LinkedMap<K, V> lm = getMap();
        assertEquals(-1, lm.indexOf(getOtherKeys()));

        resetFull();
        lm = getMap();
        final List<K> list = new ArrayList<>();
        for (final MapIterator<K, V> it = lm.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            assertEquals(i, lm.indexOf(list.get(i)));
        }
    }

    /**
     * Test for <a href="https://issues.apache.org/jira/browse/COLLECTIONS-323">COLLECTIONS-323</a>.
     */
    @Test
    public void testInitialCapacityZero() {
        final LinkedMap<String, String> map = new LinkedMap<>(0);
        assertEquals(1, map.data.length);
    }

    @Test
    public void testInsertionOrder() {
        if (!isPutAddSupported() || !isPutChangeSupported()) {
            return;
        }
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        Iterator<K> keyIter;
        Iterator<V> valueIter;

        resetEmpty();
        map.put(keys[0], values[0]);
        map.put(keys[1], values[1]);
        keyIter = map.keySet().iterator();
        assertSame(keys[0], keyIter.next());
        assertSame(keys[1], keyIter.next());
        valueIter = map.values().iterator();
        assertSame(values[0], valueIter.next());
        assertSame(values[1], valueIter.next());

        // no change to order
        map.put(keys[1], values[1]);
        keyIter = map.keySet().iterator();
        assertSame(keys[0], keyIter.next());
        assertSame(keys[1], keyIter.next());
        valueIter = map.values().iterator();
        assertSame(values[0], valueIter.next());
        assertSame(values[1], valueIter.next());

        // no change to order
        map.put(keys[1], values[2]);
        keyIter = map.keySet().iterator();
        assertSame(keys[0], keyIter.next());
        assertSame(keys[1], keyIter.next());
        valueIter = map.values().iterator();
        assertSame(values[0], valueIter.next());
        assertSame(values[2], valueIter.next());

        // no change to order
        map.put(keys[0], values[3]);
        keyIter = map.keySet().iterator();
        assertSame(keys[0], keyIter.next());
        assertSame(keys[1], keyIter.next());
        valueIter = map.values().iterator();
        assertSame(values[3], valueIter.next());
        assertSame(values[2], valueIter.next());
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/LinkedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/LinkedMap.fullCollection.version4.obj");
//    }

    @Test
    public void testRemoveByIndex() {
        resetEmpty();
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> getMap().remove(-1));
        resetFull();
        final LinkedMap<K, V> lm = getMap();
        assertThrows(IndexOutOfBoundsException.class, () -> lm.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> lm.remove(lm.size()));
        final List<K> list = new ArrayList<>();
        for (final MapIterator<K, V> it = lm.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            final Object key = list.get(i);
            final Object value = lm.get(key);
            assertEquals(value, lm.remove(i));
            list.remove(i);
            assertFalse(lm.containsKey(key));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReset() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        ((ResettableIterator<K>) ordered.mapIterator()).reset();

        resetFull();
        ordered = getMap();
        final List<K> list = new ArrayList<>(ordered.keySet());
        final ResettableIterator<K> it = (ResettableIterator<K>) ordered.mapIterator();
        assertSame(list.get(0), it.next());
        assertSame(list.get(1), it.next());
        it.reset();
        assertSame(list.get(0), it.next());
    }
}
