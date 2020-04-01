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

import java.util.Map;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;

/**
 * JUnit tests.
 *
 */
public class MultiKeyMapTest<K, V> extends AbstractIterableMapTest<MultiKey<? extends K>, V> {

    static final Integer I1 = Integer.valueOf(1);
    static final Integer I2 = Integer.valueOf(2);
    static final Integer I3 = Integer.valueOf(3);
    static final Integer I4 = Integer.valueOf(4);
    static final Integer I5 = Integer.valueOf(5);
    static final Integer I6 = Integer.valueOf(6);
    static final Integer I7 = Integer.valueOf(7);
    static final Integer I8 = Integer.valueOf(8);

    public MultiKeyMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(MultiKeyMapTest.class);
    }

    @Override
    public MultiKeyMap<K, V> makeObject() {
        return new MultiKeyMap<>();
    }

    @Override
    public MultiKey<K>[] getSampleKeys() {
        return getMultiKeyKeys();
    }

    @SuppressWarnings("unchecked")
    private MultiKey<K>[] getMultiKeyKeys() {
        return new MultiKey[] {
            new MultiKey<>(I1, I2),
            new MultiKey<>(I2, I3),
            new MultiKey<>(I3, I4),
            new MultiKey<>(I1, I1, I2),
            new MultiKey<>(I2, I3, I4),
            new MultiKey<>(I3, I7, I6),
            new MultiKey<>(I1, I1, I2, I3),
            new MultiKey<>(I2, I4, I5, I6),
            new MultiKey<>(I3, I6, I7, I8),
            new MultiKey<>(I1, I1, I2, I3, I4),
            new MultiKey<>(I2, I3, I4, I5, I6),
            new MultiKey<>(I3, I5, I6, I7, I8),
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public V[] getSampleValues() {
        return (V[]) new Object[] {
            "2A", "2B", "2C",
            "3D", "3E", "3F",
            "4G", "4H", "4I",
            "5J", "5K", "5L",
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public V[] getNewSampleValues() {
        return (V[]) new Object[] {
            "1a", "1b", "1c",
            "2d", "2e", "2f",
            "3g", "3h", "3i",
            "4j", "4k", "4l",
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultiKey<K>[] getOtherKeys() {
        return new MultiKey[] {
            new MultiKey<>(I1, I7),
            new MultiKey<>(I1, I8),
            new MultiKey<>(I2, I4),
            new MultiKey<>(I2, I5),
        };
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    //-----------------------------------------------------------------------
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
        assertEquals(null, map.put(new MultiKey<K>(null, null), null));
        try {
            map.put(null, (V) new Object());
            fail();
        } catch (final NullPointerException ex) {}
    }

    //-----------------------------------------------------------------------
    public void testMultiKeyGet() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        final MultiKey<K>[] keys = getMultiKeyKeys();
        final V[] values = getSampleValues();

        for (int i = 0; i < keys.length; i++) {
            final MultiKey<K> key = keys[i];
            final V value = values[i];

            switch (key.size()) {
            case 2:
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1)));
                assertEquals(null, multimap.get(null, key.getKey(1)));
                assertEquals(null, multimap.get(key.getKey(0), null));
                assertEquals(null, multimap.get(null, null));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), null));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), null, null));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), null, null, null));
                break;
            case 3:
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(null, multimap.get(null, key.getKey(1), key.getKey(2)));
                assertEquals(null, multimap.get(key.getKey(0), null, key.getKey(2)));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), null));
                assertEquals(null, multimap.get(null, null, null));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), null, null));
                break;
            case 4:
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(null, multimap.get(null, key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(null, multimap.get(key.getKey(0), null, key.getKey(2), key.getKey(3)));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), null, key.getKey(3)));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertEquals(null, multimap.get(null, null, null, null));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                break;
            case 5:
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(null, multimap.get(null, key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(null, multimap.get(key.getKey(0), null, key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), null, key.getKey(3), key.getKey(4)));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), null, key.getKey(4)));
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                assertEquals(null, multimap.get(null, null, null, null, null));
                break;
            default:
                fail("Invalid key size");
            }
        }
    }

    public void testMultiKeyContainsKey() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        final MultiKey<K>[] keys = getMultiKeyKeys();

        for (final MultiKey<K> key : keys) {
            switch (key.size()) {
            case 2:
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1)));
                assertEquals(false, multimap.containsKey(null, key.getKey(1)));
                assertEquals(false, multimap.containsKey(key.getKey(0), null));
                assertEquals(false, multimap.containsKey(null, null));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), null));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), null, null));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), null, null, null));
                break;
            case 3:
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(false, multimap.containsKey(null, key.getKey(1), key.getKey(2)));
                assertEquals(false, multimap.containsKey(key.getKey(0), null, key.getKey(2)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), null));
                assertEquals(false, multimap.containsKey(null, null, null));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), null, null));
                break;
            case 4:
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(false, multimap.containsKey(null, key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(false, multimap.containsKey(key.getKey(0), null, key.getKey(2), key.getKey(3)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), null, key.getKey(3)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertEquals(false, multimap.containsKey(null, null, null, null));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                break;
            case 5:
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(false, multimap.containsKey(null, key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(false, multimap.containsKey(key.getKey(0), null, key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), null, key.getKey(3), key.getKey(4)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), null, key.getKey(4)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                assertEquals(false, multimap.containsKey(null, null, null, null, null));
                break;
            default:
                fail("Invalid key size");
            }
        }
    }

    public void testMultiKeyPut() {
        final MultiKey<K>[] keys = getMultiKeyKeys();
        final V[] values = getSampleValues();

        for (int i = 0; i < keys.length; i++) {
            final MultiKeyMap<K, V> multimap = new MultiKeyMap<>();

            final MultiKey<K> key = keys[i];
            final V value = values[i];

            switch (key.size()) {
            case 2:
                assertEquals(null, multimap.put(key.getKey(0), key.getKey(1), value));
                assertEquals(1, multimap.size());
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1)));
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1)));
                assertEquals(true, multimap.containsKey(new MultiKey<>(key.getKey(0), key.getKey(1))));
                assertEquals(value, multimap.put(key.getKey(0), key.getKey(1), null));
                assertEquals(1, multimap.size());
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1)));
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1)));
                break;
            case 3:
                assertEquals(null, multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), value));
                assertEquals(1, multimap.size());
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(true, multimap.containsKey(new MultiKey<>(key.getKey(0), key.getKey(1), key.getKey(2))));
                assertEquals(value, multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertEquals(1, multimap.size());
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                break;
            case 4:
                assertEquals(null, multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), value));
                assertEquals(1, multimap.size());
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(true, multimap.containsKey(new MultiKey<>(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3))));
                assertEquals(value, multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                assertEquals(1, multimap.size());
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                break;
            case 5:
                assertEquals(null, multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4), value));
                assertEquals(1, multimap.size());
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(true, multimap.containsKey(new MultiKey<>(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4))));
                assertEquals(value, multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4), null));
                assertEquals(1, multimap.size());
                assertEquals(null, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                break;
            default:
                fail("Invalid key size");
            }
        }
    }

    public void testMultiKeyPutWithNullKey() {
        final MultiKeyMap<String, String> map = new MultiKeyMap<>();
        map.put("a", null, "value1");
        map.put("b", null, "value2");
        map.put("c", null, "value3");
        map.put("a", "z",  "value4");
        map.put("a", null, "value5");
        map.put(null, "a", "value6");
        map.put(null, null, "value7");

        assertEquals(6, map.size());
        assertEquals("value5", map.get("a", null));
        assertEquals("value4", map.get("a", "z"));
        assertEquals("value6", map.get(null, "a"));
    }

    public void testMultiKeyRemove() {
        final MultiKey<K>[] keys = getMultiKeyKeys();
        final V[] values = getSampleValues();

        for (int i = 0; i < keys.length; i++) {
            resetFull();
            final MultiKeyMap<K, V> multimap = getMap();
            final int size = multimap.size();

            final MultiKey<K> key = keys[i];
            final V value = values[i];

            switch (key.size()) {
            case 2:
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1)));
                assertEquals(value, multimap.removeMultiKey(key.getKey(0), key.getKey(1)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1)));
                assertEquals(size - 1, multimap.size());
                assertEquals(null, multimap.removeMultiKey(key.getKey(0), key.getKey(1)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1)));
                break;
            case 3:
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(value, multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(size - 1, multimap.size());
                assertEquals(null, multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                break;
            case 4:
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(value, multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(size - 1, multimap.size());
                assertEquals(null, multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                break;
            case 5:
                assertEquals(true, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(value, multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(size - 1, multimap.size());
                assertEquals(null, multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(false, multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                break;
            default:
                fail("Invalid key size");
            }
        }
    }

    public void testMultiKeyRemoveAll1() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        assertEquals(12, multimap.size());

        multimap.removeAll(I1);
        assertEquals(8, multimap.size());
        for (final MapIterator<MultiKey<? extends K>, V> it = multimap.mapIterator(); it.hasNext();) {
            final MultiKey<? extends K> key = it.next();
            assertEquals(false, I1.equals(key.getKey(0)));
        }
    }

    public void testMultiKeyRemoveAll2() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        assertEquals(12, multimap.size());

        multimap.removeAll(I2, I3);
        assertEquals(9, multimap.size());
        for (final MapIterator<MultiKey<? extends K>, V> it = multimap.mapIterator(); it.hasNext();) {
            final MultiKey<? extends K> key = it.next();
            assertEquals(false, I2.equals(key.getKey(0)) && I3.equals(key.getKey(1)));
        }
    }

    public void testMultiKeyRemoveAll3() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        assertEquals(12, multimap.size());

        multimap.removeAll(I1, I1, I2);
        assertEquals(9, multimap.size());
        for (final MapIterator<MultiKey<? extends K>, V> it = multimap.mapIterator(); it.hasNext();) {
            final MultiKey<? extends K> key = it.next();
            assertEquals(false, I1.equals(key.getKey(0)) && I1.equals(key.getKey(1)) && I2.equals(key.getKey(2)));
        }
    }

    public void testMultiKeyRemoveAll4() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        assertEquals(12, multimap.size());

        multimap.removeAll(I1, I1, I2, I3);
        assertEquals(10, multimap.size());
        for (final MapIterator<MultiKey<? extends K>, V> it = multimap.mapIterator(); it.hasNext();) {
            final MultiKey<? extends K> key = it.next();
            assertEquals(false, I1.equals(key.getKey(0)) && I1.equals(key.getKey(1)) && I2.equals(key.getKey(2)) && key.size() >= 4 && I3.equals(key.getKey(3)));
        }
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testClone() {
        final MultiKeyMap<K, V> map = new MultiKeyMap<>();
        map.put(new MultiKey<>((K) I1, (K) I2), (V) "1-2");
        final Map<MultiKey<? extends K>, V> cloned = map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get(new MultiKey<>((K) I1, (K) I2)), cloned.get(new MultiKey<>((K) I1, (K) I2)));
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testLRUMultiKeyMap() {
        final MultiKeyMap<K, V> map = MultiKeyMap.multiKeyMap(new LRUMap<MultiKey<? extends K>, V>(2));
        map.put((K) I1, (K) I2, (V) "1-2");
        map.put((K) I1, (K) I3, (V) "1-1");
        assertEquals(2, map.size());
        map.put((K) I1, (K) I4, (V) "1-4");
        assertEquals(2, map.size());
        assertEquals(true, map.containsKey(I1, I3));
        assertEquals(true, map.containsKey(I1, I4));
        assertEquals(false, map.containsKey(I1, I2));

        final MultiKeyMap<K, V> cloned = map.clone();
        assertEquals(2, map.size());
        assertEquals(true, cloned.containsKey(I1, I3));
        assertEquals(true, cloned.containsKey(I1, I4));
        assertEquals(false, cloned.containsKey(I1, I2));
        cloned.put((K) I1, (K) I5, (V) "1-5");
        assertEquals(2, cloned.size());
        assertEquals(true, cloned.containsKey(I1, I4));
        assertEquals(true, cloned.containsKey(I1, I5));
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
//            "src/test/resources/data/test/MultiKeyMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/MultiKeyMap.fullCollection.version4.obj");
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiKeyMap<K, V> getMap() {
        return (MultiKeyMap<K, V>) super.getMap();
    }
}
