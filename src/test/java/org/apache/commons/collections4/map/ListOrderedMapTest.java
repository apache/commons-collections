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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.list.AbstractListTest;

/**
 * Extension of {@link AbstractOrderedMapTest} for exercising the {@link ListOrderedMap}
 * implementation.
 *
 * @since 3.0
 */
public class ListOrderedMapTest<K, V> extends AbstractOrderedMapTest<K, V> {

    public ListOrderedMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(ListOrderedMapTest.class);
    }

    @Override
    public ListOrderedMap<K, V> makeObject() {
        return ListOrderedMap.listOrderedMap(new HashMap<K, V>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListOrderedMap<K, V> makeFullMap() {
        return (ListOrderedMap<K, V>) super.makeFullMap();
    }

    //-----------------------------------------------------------------------
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

    @SuppressWarnings("unchecked")
    public void testSetValueByIndex() {
        resetEmpty();
        ListOrderedMap<K, V> lom = getMap();
        try {
            lom.setValue(0, (V) "");
        } catch (final IndexOutOfBoundsException ex) {}
        try {
            lom.setValue(-1, (V) "");
        } catch (final IndexOutOfBoundsException ex) {}

        resetFull();
        lom = getMap();
        try {
            lom.setValue(-1, (V) "");
        } catch (final IndexOutOfBoundsException ex) {}
        try {
            lom.setValue(lom.size(), (V) "");
        } catch (final IndexOutOfBoundsException ex) {}

        for (int i = 0; i < lom.size(); i++) {
            final V value = lom.getValue(i);
            final Object input = Integer.valueOf(i);
            assertEquals(value, lom.setValue(i, (V) input));
            assertEquals(input, lom.getValue(i));
        }
    }

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

    @SuppressWarnings("unchecked")
    public void testPut_intObjectObject() {
        resetEmpty();
        final ListOrderedMap<K, V> lom1 = getMap();

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            lom1.put(1, (K) "testInsert1", (V) "testInsert1v");
        });
        assertTrue(exception.getMessage().contains("Index: 1, Size: 0"));
        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            lom1.put(-1, (K) "testInsert-1", (V) "testInsert-1v");
        });
        assertTrue(exception.getMessage().contains("Index: -1, Size: 0"));

        ListOrderedMap<K, V> lom = getMap();
        // put where key doesn't exist
        lom.put(0, (K) "testInsert1", (V) "testInsert1v");
        assertEquals("testInsert1v", lom.getValue(0));

        lom.put((K) "testInsertPut", (V) "testInsertPutv");
        assertEquals("testInsert1v", lom.getValue(0));
        assertEquals("testInsertPutv", lom.getValue(1));

        lom.put(0, (K) "testInsert0", (V) "testInsert0v");
        assertEquals("testInsert0v", lom.getValue(0));
        assertEquals("testInsert1v", lom.getValue(1));
        assertEquals("testInsertPutv", lom.getValue(2));

        lom.put(3, (K) "testInsert3", (V) "testInsert3v");
        assertEquals("testInsert0v", lom.getValue(0));
        assertEquals("testInsert1v", lom.getValue(1));
        assertEquals("testInsertPutv", lom.getValue(2));
        assertEquals("testInsert3v", lom.getValue(3));

        // put in a full map
        resetFull();
        lom = getMap();
        final ListOrderedMap<K, V> lom2 = new ListOrderedMap<>();
        lom2.putAll(lom);

        lom2.put(0, (K) "testInsert0", (V) "testInsert0v");
        assertEquals("testInsert0v", lom2.getValue(0));
        for (int i = 0; i < lom.size(); i++) {
            assertEquals(lom2.getValue(i + 1), lom.getValue(i));
        }

        // put where key does exist
        final Integer i1 = Integer.valueOf(1);
        final Integer i1b = Integer.valueOf(1);
        final Integer i2 = Integer.valueOf(2);
        final Integer i3 = Integer.valueOf(3);

        resetEmpty();
        lom = getMap();
        lom.put((K) i1, (V) "1");
        lom.put((K) i2, (V) "2");
        lom.put((K) i3, (V) "3");
        lom.put(0, (K) i1, (V) "One");
        assertEquals(3, lom.size());
        assertEquals(3, lom.map.size());
        assertEquals(3, lom.keyList().size());
        assertEquals("One", lom.getValue(0));
        assertSame(i1, lom.get(0));

        resetEmpty();
        lom = getMap();
        lom.put((K) i1, (V) "1");
        lom.put((K) i2, (V) "2");
        lom.put((K) i3, (V) "3");
        lom.put(0, (K) i1b, (V) "One");
        assertEquals(3, lom.size());
        assertEquals(3, lom.map.size());
        assertEquals(3, lom.keyList().size());
        assertEquals("One", lom.getValue(0));
        assertEquals("2", lom.getValue(1));
        assertEquals("3", lom.getValue(2));
        assertSame(i1b, lom.get(0));

        resetEmpty();
        lom = getMap();
        lom.put((K) i1, (V) "1");
        lom.put((K) i2, (V) "2");
        lom.put((K) i3, (V) "3");
        lom.put(1, (K) i1b, (V) "One");
        assertEquals(3, lom.size());
        assertEquals(3, lom.map.size());
        assertEquals(3, lom.keyList().size());
        assertEquals("One", lom.getValue(0));
        assertEquals("2", lom.getValue(1));
        assertEquals("3", lom.getValue(2));

        resetEmpty();
        lom = getMap();
        lom.put((K) i1, (V) "1");
        lom.put((K) i2, (V) "2");
        lom.put((K) i3, (V) "3");
        lom.put(2, (K) i1b, (V) "One");
        assertEquals(3, lom.size());
        assertEquals(3, lom.map.size());
        assertEquals(3, lom.keyList().size());
        assertEquals("2", lom.getValue(0));
        assertEquals("One", lom.getValue(1));
        assertEquals("3", lom.getValue(2));

        resetEmpty();
        lom = getMap();
        lom.put((K) i1, (V) "1");
        lom.put((K) i2, (V) "2");
        lom.put((K) i3, (V) "3");
        lom.put(3, (K) i1b, (V) "One");
        assertEquals(3, lom.size());
        assertEquals(3, lom.map.size());
        assertEquals(3, lom.keyList().size());
        assertEquals("2", lom.getValue(0));
        assertEquals("3", lom.getValue(1));
        assertEquals("One", lom.getValue(2));
    }

    public void testPutAllWithIndex() {
        resetEmpty();
        @SuppressWarnings("unchecked")
        final ListOrderedMap<String, String> lom = (ListOrderedMap<String, String>) map;

        // Create Initial Data
        lom.put("testInsert0", "testInsert0v");
        lom.put("testInsert1", "testInsert1v");
        lom.put("testInsert2", "testInsert2v");
        assertEquals("testInsert0v", lom.getValue(0));
        assertEquals("testInsert1v", lom.getValue(1));
        assertEquals("testInsert2v", lom.getValue(2));

        // Create New Test Map and Add using putAll(int, Object, Object)
        final Map<String, String> values = new ListOrderedMap<>();
        values.put("NewInsert0", "NewInsert0v");
        values.put("NewInsert1", "NewInsert1v");
        lom.putAll(1, values);

        // Perform Asserts
        assertEquals("testInsert0v", lom.getValue(0));
        assertEquals("NewInsert0v", lom.getValue(1));
        assertEquals("NewInsert1v", lom.getValue(2));
        assertEquals("testInsert1v", lom.getValue(3));
        assertEquals("testInsert2v", lom.getValue(4));
    }

    @SuppressWarnings("unchecked")
    public void testPutAllWithIndexBug441() {
        // see COLLECTIONS-441
        resetEmpty();
        final ListOrderedMap<K, V> lom = getMap();

        final int size = 5;
        for (int i = 0; i < size; i++) {
            lom.put((K) Integer.valueOf(i), (V) Boolean.TRUE);
        }

        final Map<K, V> map = new TreeMap<>();
        for (int i = 0; i < size; i++) {
            map.put((K) Integer.valueOf(i), (V) Boolean.FALSE);
        }

        lom.putAll(3, map);

        final List<K> orderedList = lom.asList();
        for (int i = 0; i < size; i++) {
            assertEquals(Integer.valueOf(i), orderedList.get(i));
        }
    }

    //-----------------------------------------------------------------------
    public void testValueList_getByIndex() {
        resetFull();
        final ListOrderedMap<K, V> lom = getMap();
        for (int i = 0; i < lom.size(); i++) {
            final V expected = lom.getValue(i);
            assertEquals(expected, lom.valueList().get(i));
        }
    }

    @SuppressWarnings("unchecked")
    public void testValueList_setByIndex() {
        resetFull();
        final ListOrderedMap<K, V> lom = getMap();
        for (int i = 0; i < lom.size(); i++) {
            final Object input = Integer.valueOf(i);
            final V expected = lom.getValue(i);
            assertEquals(expected, lom.valueList().set(i, (V) input));
            assertEquals(input, lom.getValue(i));
            assertEquals(input, lom.valueList().get(i));
        }
    }

    public void testValueList_removeByIndex() {
        resetFull();
        final ListOrderedMap<K, V> lom = getMap();
        while (lom.size() > 1) {
            final V expected = lom.getValue(1);
            assertEquals(expected, lom.valueList().remove(1));
        }
    }

    public void testCOLLECTIONS_474_nullValues () {
        final Object key1 = new Object();
        final Object key2 = new Object();
        final HashMap<Object, Object> hmap = new HashMap<>();
        hmap.put(key1, null);
        hmap.put(key2, null);
        assertEquals("Should have two elements", 2, hmap.size());
        final ListOrderedMap<Object, Object> listMap = new ListOrderedMap<>();
        listMap.put(key1, null);
        listMap.put(key2, null);
        assertEquals("Should have two elements", 2, listMap.size());
        listMap.putAll(2, hmap);
    }

    public void testCOLLECTIONS_474_nonNullValues () {
        final Object key1 = new Object();
        final Object key2 = new Object();
        final HashMap<Object, Object> hmap = new HashMap<>();
        hmap.put(key1, "1");
        hmap.put(key2, "2");
        assertEquals("Should have two elements", 2, hmap.size());
        final ListOrderedMap<Object, Object> listMap = new ListOrderedMap<>();
        listMap.put(key1, "3");
        listMap.put(key2, "4");
        assertEquals("Should have two elements", 2, listMap.size());
        listMap.putAll(2, hmap);
    }

    //-----------------------------------------------------------------------
    public BulkTest bulkTestKeyListView() {
        return new TestKeyListView();
    }

    public BulkTest bulkTestValueListView() {
        return new TestValueListView();
    }

    //-----------------------------------------------------------------------
    public class TestKeyListView extends AbstractListTest<K> {
        TestKeyListView() {
            super("TestKeyListView");
        }

        @Override
        public List<K> makeObject() {
            return ListOrderedMapTest.this.makeObject().keyList();
        }
        @Override
        public List<K> makeFullCollection() {
            return ListOrderedMapTest.this.makeFullMap().keyList();
        }

        @Override
        public K[] getFullElements() {
            return ListOrderedMapTest.this.getSampleKeys();
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
            return ListOrderedMapTest.this.isAllowNullKey();
        }
        @Override
        public boolean isTestSerialization() {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    public class TestValueListView extends AbstractListTest<V> {
        TestValueListView() {
            super("TestValueListView");
        }

        @Override
        public List<V> makeObject() {
            return ListOrderedMapTest.this.makeObject().valueList();
        }
        @Override
        public List<V> makeFullCollection() {
            return ListOrderedMapTest.this.makeFullMap().valueList();
        }

        @Override
        public V[] getFullElements() {
            return ListOrderedMapTest.this.getSampleValues();
        }
        @Override
        public boolean isAddSupported() {
            return false;
        }
        @Override
        public boolean isRemoveSupported() {
            return true;
        }
        @Override
        public boolean isSetSupported() {
            return true;
        }
        @Override
        public boolean isNullSupported() {
            return ListOrderedMapTest.this.isAllowNullKey();
        }
        @Override
        public boolean isTestSerialization() {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/ListOrderedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/ListOrderedMap.fullCollection.version4.obj");
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListOrderedMap<K, V> getMap() {
        return (ListOrderedMap<K, V>) super.getMap();
    }
}
