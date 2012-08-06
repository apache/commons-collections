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
package org.apache.commons.collections.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.list.AbstractListTest;

/**
 * JUnit tests.
 *
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public class TestLinkedMap<K, V> extends AbstractTestOrderedMap<K, V> {

    public TestLinkedMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestLinkedMap.class);
    }

    @Override
    public LinkedMap<K, V> makeObject() {
        return new LinkedMap<K, V>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkedMap<K, V> makeFullMap() {
        return (LinkedMap<K, V>) super.makeFullMap();
    }

    @Override
    public String getCompatibilityVersion() {
        return "3";
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testReset() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        ((ResettableIterator<K>) ordered.mapIterator()).reset();

        resetFull();
        ordered = getMap();
        List<K> list = new ArrayList<K>(ordered.keySet());
        ResettableIterator<K> it = (ResettableIterator<K>) ordered.mapIterator();
        assertSame(list.get(0), it.next());
        assertSame(list.get(1), it.next());
        it.reset();
        assertSame(list.get(0), it.next());
    }

    //-----------------------------------------------------------------------
    public void testInsertionOrder() {
        if (isPutAddSupported() == false || isPutChangeSupported() == false) return;
        K[] keys = getSampleKeys();
        V[] values = getSampleValues();
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

    //-----------------------------------------------------------------------
    public void testGetByIndex() {
        resetEmpty();
        LinkedMap<K, V> lm = getMap();
        try {
            lm.get(0);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.get(-1);
        } catch (IndexOutOfBoundsException ex) {}

        resetFull();
        lm = getMap();
        try {
            lm.get(-1);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.get(lm.size());
        } catch (IndexOutOfBoundsException ex) {}

        int i = 0;
        for (MapIterator<K, V> it = lm.mapIterator(); it.hasNext(); i++) {
            assertSame(it.next(), lm.get(i));
        }
    }

    public void testGetValueByIndex() {
        resetEmpty();
        LinkedMap<K, V> lm = getMap();
        try {
            lm.getValue(0);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.getValue(-1);
        } catch (IndexOutOfBoundsException ex) {}

        resetFull();
        lm = getMap();
        try {
            lm.getValue(-1);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.getValue(lm.size());
        } catch (IndexOutOfBoundsException ex) {}

        int i = 0;
        for (MapIterator<K, V> it = lm.mapIterator(); it.hasNext(); i++) {
            it.next();
            assertSame(it.getValue(), lm.getValue(i));
        }
    }

    public void testIndexOf() {
        resetEmpty();
        LinkedMap<K, V> lm = getMap();
        assertEquals(-1, lm.indexOf(getOtherKeys()));

        resetFull();
        lm = getMap();
        List<K> list = new ArrayList<K>();
        for (MapIterator<K, V> it = lm.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            assertEquals(i, lm.indexOf(list.get(i)));
        }
    }

    public void testRemoveByIndex() {
        resetEmpty();
        LinkedMap<K, V> lm = getMap();
        try {
            lm.remove(0);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.remove(-1);
        } catch (IndexOutOfBoundsException ex) {}

        resetFull();
        lm = getMap();
        try {
            lm.remove(-1);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lm.remove(lm.size());
        } catch (IndexOutOfBoundsException ex) {}

        List<K> list = new ArrayList<K>();
        for (MapIterator<K, V> it = lm.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            Object key = list.get(i);
            Object value = lm.get(key);
            assertEquals(value, lm.remove(i));
            list.remove(i);
            assertEquals(false, lm.containsKey(key));
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
            return TestLinkedMap.this.makeObject().asList();
        }

        @Override
        public List<K> makeFullCollection() {
            return TestLinkedMap.this.makeFullMap().asList();
        }

        @Override
        public K[] getFullElements() {
            return TestLinkedMap.this.getSampleKeys();
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
            return TestLinkedMap.this.isAllowNullKey();
        }
        @Override
        public boolean isTestSerialization() {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public void testClone() {
        LinkedMap<K, V> map = new LinkedMap<K, V>(10);
        map.put((K) "1", (V) "1");
        Map<K, V> cloned = map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get("1"), cloned.get("1"));
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LinkedMap.emptyCollection.version3.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LinkedMap.fullCollection.version3.obj");
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkedMap<K, V> getMap() {
        return (LinkedMap<K, V>) super.getMap();
    }

    /**
     * Test for <a href="https://issues.apache.org/jira/browse/COLLECTIONS-323">COLLECTIONS-323</a>.
     */
    public void testInitialCapacityZero() {
        final LinkedMap<String,String> map = new LinkedMap<String,String>(0);
        assertEquals(1, map.data.length);
    }
}
