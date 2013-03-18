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

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.map.AbstractReferenceMap.ReferenceStrength;

/**
 * Tests for ReferenceIdentityMap.
 *
 * @version $Id$
 */
public class ReferenceIdentityMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    private static final Integer I1A = new Integer(1);
    private static final Integer I1B = new Integer(1);
    private static final Integer I2A = new Integer(2);
    private static final Integer I2B = new Integer(2);

    public ReferenceIdentityMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(ReferenceIdentityMapTest.class);
    }

    @Override
    public ReferenceIdentityMap<K, V> makeObject() {
        return new ReferenceIdentityMap<K, V>(ReferenceStrength.WEAK, ReferenceStrength.WEAK);
    }

    @Override
    public Map<K, V> makeConfirmedMap() {
        // Testing against another [collections] class generally isn't a good idea,
        // but the closest alternative is IdentityHashMap, which propagates reference-equality down to keySet and values.
        // arguably ReferenceIdentityMap should do the same but that's a later discussion.
        return new IdentityMap<K, V>();
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
        return "3.1";
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testBasics() {
        final IterableMap<K, V> map = new ReferenceIdentityMap<K, V>(ReferenceStrength.HARD, ReferenceStrength.HARD);
        assertEquals(0, map.size());

        map.put((K) I1A, (V) I2A);
        assertEquals(1, map.size());
        assertSame(I2A, map.get(I1A));
        assertSame(null, map.get(I1B));
        assertEquals(true, map.containsKey(I1A));
        assertEquals(false, map.containsKey(I1B));
        assertEquals(true, map.containsValue(I2A));
        assertEquals(false, map.containsValue(I2B));

        map.put((K) I1A, (V) I2B);
        assertEquals(1, map.size());
        assertSame(I2B, map.get(I1A));
        assertSame(null, map.get(I1B));
        assertEquals(true, map.containsKey(I1A));
        assertEquals(false, map.containsKey(I1B));
        assertEquals(false, map.containsValue(I2A));
        assertEquals(true, map.containsValue(I2B));

        map.put((K) I1B, (V) I2B);
        assertEquals(2, map.size());
        assertSame(I2B, map.get(I1A));
        assertSame(I2B, map.get(I1B));
        assertEquals(true, map.containsKey(I1A));
        assertEquals(true, map.containsKey(I1B));
        assertEquals(false, map.containsValue(I2A));
        assertEquals(true, map.containsValue(I2B));
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testHashEntry() {
        final IterableMap<K, V> map = new ReferenceIdentityMap<K, V>(ReferenceStrength.HARD, ReferenceStrength.HARD);

        map.put((K) I1A, (V) I2A);
        map.put((K) I1B, (V) I2A);

        final Map.Entry<K, V> entry1 = map.entrySet().iterator().next();
        final Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        final Map.Entry<K, V> entry2 = it.next();
        final Map.Entry<K, V> entry3 = it.next();

        assertEquals(true, entry1.equals(entry2));
        assertEquals(true, entry2.equals(entry1));
        assertEquals(false, entry1.equals(entry3));
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testNullHandling() {
        resetFull();
        assertEquals(null, getMap().get(null));
        assertEquals(false, getMap().containsKey(null));
        assertEquals(false, getMap().containsValue(null));
        assertEquals(null, getMap().remove(null));
        assertEquals(false, getMap().entrySet().contains(null));
        assertEquals(false, getMap().keySet().contains(null));
        assertEquals(false, getMap().values().contains(null));
        try {
            getMap().put(null, null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            getMap().put((K) new Object(), null);
            fail();
        } catch (final NullPointerException ex) {}
        try {
            getMap().put(null, (V) new Object());
            fail();
        } catch (final NullPointerException ex) {}
    }

    //-----------------------------------------------------------------------
/*
    // Tests often fail because gc is uncontrollable

    public void testPurge() {
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
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
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
        for (int i = 0; i < 10; i++) {
            map.put(new Integer(i), new Integer(i));
        }

        gc();
        for (int i = 0; i < 10; i++) {
            Integer I = new Integer(i);
            assertTrue("map.containsKey should return false for GC'd element", !map.containsKey(I));
            assertTrue("map.get should return null for GC'd element", map.get(I) == null);
        }
    }


    public void testEntrySetIteratorAfterGC() {
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = new Integer(10 + i);
            map.put(new Integer(i), new Integer(i));
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
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = new Integer(10 + i);
            map.put(new Integer(i), new Integer(i));
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
        ReferenceIdentityMap map = new ReferenceIdentityMap(ReferenceIdentityMap.WEAK, ReferenceIdentityMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = new Integer(10 + i);
            map.put(new Integer(i), new Integer(i));
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
    private Map<K, V> buildRefMap() {
        final K key = (K) new Object();
        final V value = (V) new Object();

        keyReference = new WeakReference<K>(key);
        valueReference = new WeakReference<V>(value);

        final Map<K, V> testMap = new ReferenceIdentityMap<K, V>(ReferenceStrength.WEAK, ReferenceStrength.HARD, true);
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
            if (
                keyReference.get() == null &&
                valueReference.get() == null) {
                break;

            } else {
                // create garbage:
                @SuppressWarnings("unused")
                final byte[] b =  new byte[bytz];
                bytz = bytz * 2;
            }
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

}
