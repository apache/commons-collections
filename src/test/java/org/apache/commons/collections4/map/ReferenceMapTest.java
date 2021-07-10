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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.map.AbstractHashedMap.HashEntry;
import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceEntry;
import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;

import junit.framework.Test;

/**
 * Tests for ReferenceMap.
 *
 */
public class ReferenceMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    public ReferenceMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(ReferenceMapTest.class);
    }

    @Override
    public ReferenceMap<K, V> makeObject() {
        return new ReferenceMap<>(ReferenceStrength.WEAK, ReferenceStrength.WEAK);
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isAllowNullValue() {
        return false;
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/ReferenceMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/ReferenceMap.fullCollection.version4.obj");
//    }

    @SuppressWarnings("unchecked")
    public void testNullHandling() {
        resetFull();
        assertEquals(null, map.get(null));
        assertEquals(false, map.containsKey(null));
        assertEquals(false, map.containsValue(null));
        assertEquals(null, map.remove(null));
        assertEquals(false, map.entrySet().contains(null));
        assertEquals(false, map.keySet().contains(null));
        assertEquals(false, map.values().contains(null));
        try {
            map.put(null, null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            map.put((K) new Object(), null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            map.put(null, (V) new Object());
            fail();
        } catch (final NullPointerException ex) {}
    }

/*
    // Tests often fail because gc is uncontrollable

    public void testPurge() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < hard.length; i++) {
            hard[i] = new Object();
            map.put(hard[i], new Object());
        }
        gc();
        assertTrue("map should be empty after purge of weak values", map.isEmpty());

        for (int i = 0; i < hard.length; i++) {
            map.put(new Object(), hard[i]);
        }
        gc();
        assertTrue("map should be empty after purge of weak keys", map.isEmpty());

        for (int i = 0; i < hard.length; i++) {
            map.put(new Object(), hard[i]);
            map.put(hard[i], new Object());
        }

        gc();
        assertTrue("map should be empty after purge of weak keys and values", map.isEmpty());
    }


    public void testGetAfterGC() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        for (int i = 0; i < 10; i++) {
            map.put(Integer.valueOf(i), Integer.valueOf(i));
        }

        gc();
        for (int i = 0; i < 10; i++) {
            Integer I = Integer.valueOf(i);
            assertTrue("map.containsKey should return false for GC'd element", !map.containsKey(I));
            assertTrue("map.get should return null for GC'd element", map.get(I) == null);
        }
    }


    public void testEntrySetIteratorAfterGC() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = Integer.valueOf(10 + i);
            map.put(Integer.valueOf(i), Integer.valueOf(i));
            map.put(hard[i], hard[i]);
        }

        gc();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer key = (Integer)entry.getKey();
            Integer value = (Integer)entry.getValue();
            assertTrue("iterator should skip GC'd keys", key.intValue() >= 10);
            assertTrue("iterator should skip GC'd values", value.intValue() >= 10);
        }

    }

    public void testMapIteratorAfterGC() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = Integer.valueOf(10 + i);
            map.put(Integer.valueOf(i), Integer.valueOf(i));
            map.put(hard[i], hard[i]);
        }

        gc();
        MapIterator iterator = map.mapIterator();
        while (iterator.hasNext()) {
            Object key1 = iterator.next();
            Integer key = (Integer) iterator.getKey();
            Integer value = (Integer) iterator.getValue();
            assertTrue("iterator keys should match", key == key1);
            assertTrue("iterator should skip GC'd keys", key.intValue() >= 10);
            assertTrue("iterator should skip GC'd values", value.intValue() >= 10);
        }

    }

    public void testMapIteratorAfterGC2() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = Integer.valueOf(10 + i);
            map.put(Integer.valueOf(i), Integer.valueOf(i));
            map.put(hard[i], hard[i]);
        }

        MapIterator iterator = map.mapIterator();
        while (iterator.hasNext()) {
            Object key1 = iterator.next();
            gc();
            Integer key = (Integer) iterator.getKey();
            Integer value = (Integer) iterator.getValue();
            assertTrue("iterator keys should match", key == key1);
            assertTrue("iterator should skip GC'd keys", key.intValue() >= 10);
            assertTrue("iterator should skip GC'd values", value.intValue() >= 10);
        }

    }
*/

    WeakReference<K> keyReference;
    WeakReference<V> valueReference;

    @SuppressWarnings("unchecked")
    public Map<K, V> buildRefMap() {
        final K key = (K) new Object();
        final V value = (V) new Object();

        keyReference = new WeakReference<>(key);
        valueReference = new WeakReference<>(value);

        final Map<K, V> testMap = new ReferenceMap<>(ReferenceStrength.WEAK, ReferenceStrength.HARD, true);
        testMap.put(key, value);

        assertEquals("In map", value, testMap.get(key));
        assertNotNull("Weak reference released early (1)", keyReference.get());
        assertNotNull("Weak reference released early (2)", valueReference.get());
        return testMap;
    }

    /** Tests whether purge values setting works */
    public void testPurgeValues() throws Exception {
        // many thanks to Juozas Baliuka for suggesting this method
        final Map<K, V> testMap = buildRefMap();

        int iterations = 0;
        int bytz = 2;
        while (true) {
            System.gc();
            if (iterations++ > 50) {
                fail("Max iterations reached before resource released.");
            }
            testMap.isEmpty();
            if (keyReference.get() == null && valueReference.get() == null) {
                break;

            }
            // create garbage:
            @SuppressWarnings("unused")
            final byte[] b = new byte[bytz];
            bytz = bytz * 2;
        }
    }

    public void testCustomPurge() {
        final List<Integer> expiredValues = new ArrayList<>();
        @SuppressWarnings("unchecked")
        final Consumer<Integer> consumer = (Consumer<Integer> & Serializable) v -> expiredValues.add(v);
        final Map<Integer, Integer> map = new ReferenceMap<Integer, Integer>(ReferenceStrength.WEAK, ReferenceStrength.HARD, false) {
            private static final long serialVersionUID = 1L;

            @Override
            protected ReferenceEntry<Integer, Integer> createEntry(final HashEntry<Integer, Integer> next, final int hashCode, final Integer key, final Integer value) {
                return new AccessibleEntry<>(this, next, hashCode, key, value, consumer);
            }
        };
        for (int i = 100000; i < 100010; i++) {
            map.put(Integer.valueOf(i), Integer.valueOf(i));
        }
        int iterations = 0;
        int bytz = 2;
        while (true) {
            System.gc();
            if (iterations++ > 50 || bytz < 0) {
                fail("Max iterations reached before resource released.");
            }
            map.isEmpty();
            if (!expiredValues.isEmpty()) {
                break;
            }
            // create garbage:
            @SuppressWarnings("unused")
            final byte[] b = new byte[bytz];
            bytz = bytz * 2;
        }
        assertFalse("Value should be stored", expiredValues.isEmpty());
    }

    /**
     * Test whether after serialization the "data" HashEntry array is the same size as the original.<p>
     *
     * See <a href="https://issues.apache.org/jira/browse/COLLECTIONS-599">COLLECTIONS-599: HashEntry array object naming data initialized with double the size during deserialization</a>
     */
    public void testDataSizeAfterSerialization() throws IOException, ClassNotFoundException {

        final ReferenceMap<String, String> serializeMap = new ReferenceMap<>(ReferenceStrength.WEAK, ReferenceStrength.WEAK, true);
        serializeMap.put("KEY", "VALUE");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(serializeMap);
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (ObjectInputStream in = new ObjectInputStream(bais)) {
            @SuppressWarnings("unchecked")
            final ReferenceMap<String, String> deserializedMap = (ReferenceMap<String, String>) in.readObject();
            assertEquals(1, deserializedMap.size());
            assertEquals(serializeMap.data.length, deserializedMap.data.length);
        }

    }

    @SuppressWarnings("unused")
    private static void gc() {
        try {
            // trigger GC
            final byte[][] tooLarge = new byte[1000000000][1000000000];
            fail("you have too much RAM");
        } catch (final OutOfMemoryError ex) {
            System.gc(); // ignore
        }
    }

    private static class AccessibleEntry<K, V> extends ReferenceEntry<K, V> {
        final AbstractReferenceMap<K, V> parent;
        final Consumer<V> consumer;

        AccessibleEntry(final AbstractReferenceMap<K, V> parent, final HashEntry<K, V> next, final int hashCode, final K key, final V value, final Consumer<V> consumer) {
            super(parent, next, hashCode, key, value);
            this.parent = parent;
            this.consumer = consumer;
        }

        @Override
        protected void onPurge() {
            if (parent.isValueType(ReferenceStrength.HARD)) {
                consumer.accept(getValue());
            }
        }
    }
}
