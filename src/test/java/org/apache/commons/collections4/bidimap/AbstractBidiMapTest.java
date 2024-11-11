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
package org.apache.commons.collections4.bidimap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.iterators.AbstractMapIteratorTest;
import org.apache.commons.collections4.map.AbstractIterableMapTest;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link BidiMap}.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public abstract class AbstractBidiMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    public class TestBidiMapEntrySet extends TestMapEntrySet {

        public TestBidiMapEntrySet() {
        }

        @Test
        public void testMapEntrySetIteratorEntrySetValueCrossCheck() {
            final K key1 = getSampleKeys()[0];
            final K key2 = getSampleKeys()[1];
            final V newValue1 = getNewSampleValues()[0];
            final V newValue2 = getNewSampleValues()[1];

            resetFull();
            // explicitly get entries as sample values/keys are connected for some maps
            // such as BeanMap
            Iterator<Map.Entry<K, V>> it = TestBidiMapEntrySet.this.getCollection().iterator();
            final Map.Entry<K, V> entry1 = getEntry(it, key1);
            it = TestBidiMapEntrySet.this.getCollection().iterator();
            final Map.Entry<K, V> entry2 = getEntry(it, key2);
            Iterator<Map.Entry<K, V>> itConfirmed = TestBidiMapEntrySet.this.getConfirmed().iterator();
            final Map.Entry<K, V> entryConfirmed1 = getEntry(itConfirmed, key1);
            itConfirmed = TestBidiMapEntrySet.this.getConfirmed().iterator();
            final Map.Entry<K, V> entryConfirmed2 = getEntry(itConfirmed, key2);
            TestBidiMapEntrySet.this.verify();

            if (!isSetValueSupported()) {
                assertThrows(UnsupportedOperationException.class, () -> entry1.setValue(newValue1));
                return;
            }

            // these checked in superclass
            entry1.setValue(newValue1);
            entryConfirmed1.setValue(newValue1);
            entry2.setValue(newValue2);
            entryConfirmed2.setValue(newValue2);

            // at this point
            // key1=newValue1, key2=newValue2
            try {
                entry2.setValue(newValue1);  // should remove key1
            } catch (final IllegalArgumentException ex) {
                return;  // simplest way of dealing with tricky situation
            }
            entryConfirmed2.setValue(newValue1);
            AbstractBidiMapTest.this.getConfirmed().remove(key1);
            assertEquals(newValue1, entry2.getValue());
            assertTrue(AbstractBidiMapTest.this.getMap().containsKey(entry2.getKey()));
            assertTrue(AbstractBidiMapTest.this.getMap().containsValue(newValue1));
            assertEquals(newValue1, AbstractBidiMapTest.this.getMap().get(entry2.getKey()));
            assertFalse(AbstractBidiMapTest.this.getMap().containsKey(key1));
            assertFalse(AbstractBidiMapTest.this.getMap().containsValue(newValue2));
            TestBidiMapEntrySet.this.verify();

            // check for ConcurrentModification
            it.next();  // if you fail here, maybe you should be throwing an IAE, see above
            if (isRemoveSupported()) {
                it.remove();
            }
        }

    }

    public class TestBidiMapIterator extends AbstractMapIteratorTest<K, V> {

        @Override
        public V[] addSetValues() {
            return getNewSampleValues();
        }

        @Override
        public Map<K, V> getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return getConfirmed();
        }

        @Override
        public BidiMap<K, V> getMap() {
            // assumes makeFullMapIterator() called first
            return AbstractBidiMapTest.this.getMap();
        }

        @Override
        public MapIterator<K, V> makeEmptyIterator() {
            resetEmpty();
            return AbstractBidiMapTest.this.getMap().mapIterator();
        }

        @Override
        public MapIterator<K, V> makeObject() {
            resetFull();
            return AbstractBidiMapTest.this.getMap().mapIterator();
        }

        @Override
        public boolean supportsRemove() {
            return isRemoveSupported();
        }

        @Override
        public boolean supportsSetValue() {
            return isSetValueSupported();
        }

        @Override
        public void verify() {
            super.verify();
            AbstractBidiMapTest.this.verify();
        }

    }

    public class TestInverseBidiMap extends AbstractBidiMapTest<V, K> {

        final AbstractBidiMapTest<K, V> main;

        public TestInverseBidiMap(final AbstractBidiMapTest<K, V> main) {
            this.main = main;
        }

        @Override
        public String getCompatibilityVersion() {
            return main.getCompatibilityVersion();
        }

        @Override
        protected int getIterationBehaviour() {
            return main.getIterationBehaviour();
        }

        @Override
        public V[] getSampleKeys() {
            return main.getSampleValues();
        }

        @Override
        public K[] getSampleValues() {
            return main.getSampleKeys();
        }

        @Override
        public boolean isAllowNullKey() {
            return main.isAllowNullKey();
        }

        @Override
        public boolean isAllowNullValue() {
            return main.isAllowNullValue();
        }

        @Override
        public boolean isAllowNullValueGet() {
            return main.isAllowNullValueGet();
        }

        @Override
        public boolean isAllowNullValuePut() {
            return main.isAllowNullValuePut();
        }

        @Override
        public boolean isPutAddSupported() {
            return main.isPutAddSupported();
        }

        @Override
        public boolean isPutChangeSupported() {
            return main.isPutChangeSupported();
        }

        @Override
        public boolean isRemoveSupported() {
            return main.isRemoveSupported();
        }

        @Override
        public boolean isSetValueSupported() {
            return main.isSetValueSupported();
        }

        @Override
        public BidiMap<V, K> makeFullMap() {
            return main.makeFullMap().inverseBidiMap();
        }

        @Override
        public BidiMap<V, K> makeObject() {
            return main.makeObject().inverseBidiMap();
        }
    }

    public BulkTest bulkTestBidiMapIterator() {
        return new TestBidiMapIterator();
    }

    public BulkTest bulkTestInverseMap() {
        return new TestInverseBidiMap(this);
    }

    @Override
    public BulkTest bulkTestMapEntrySet() {
        return new TestBidiMapEntrySet();
    }

    private void doTestGetKey(final BidiMap<?, ?> map, final Object key, final Object value) {
        assertEquals(value, map.get(key), "Value not found for key.");
        assertEquals(key, map.getKey(value), "Key not found for value.");
    }

    /**
     * Override as DualHashBidiMap didn't exist until version 3.
     */
    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BidiMap<K, V> getMap() {
        return (BidiMap<K, V>) super.getMap();
    }

    /**
     * Override to indicate to AbstractTestMap this is a BidiMap.
     */
    @Override
    public boolean isAllowDuplicateValues() {
        return false;
    }

    /**
     * Override to create a full {@code BidiMap} other than the default.
     *
     * @return a full {@code BidiMap} implementation.
     */
    @Override
    public BidiMap<K, V> makeFullMap() {
        return (BidiMap<K, V>) super.makeFullMap();
    }

    /**
     * Override to return the empty BidiMap.
     */
    @Override
    public abstract BidiMap<K, V> makeObject();

    @SuppressWarnings("unchecked")
    private <T> void modifyEntrySet(final BidiMap<?, T> map) {
        // Gets first entry
        final Map.Entry<?, T> entry = map.entrySet().iterator().next();

        // Gets key and value
        final Object key = entry.getKey();
        final Object oldValue = entry.getValue();

        // Sets new value
        final Object newValue = "newValue";
        entry.setValue((T) newValue);

        assertEquals(
            newValue,
            map.get(key),
                "Modifying entrySet did not affect underlying Map.");

        assertNull(
            map.getKey(oldValue),
                "Modifying entrySet did not affect inverse Map.");
    }

    private void remove(final BidiMap<?, ?> map, final Object key) {
        final Object value = map.remove(key);
        assertFalse(map.containsKey(key), "Key was not removed.");
        assertNull(map.getKey(value), "Value was not removed.");
    }

    private void removeByEntrySet(final BidiMap<?, ?> map, final Object key, final Object value) {
        final Map<Object, Object> temp = new HashMap<>();
        temp.put(key, value);
        map.entrySet().remove(temp.entrySet().iterator().next());

        assertFalse(map.containsKey(key), "Key was not removed.");
        assertFalse(map.containsValue(value), "Value was not removed.");

        assertFalse(map.inverseBidiMap().containsValue(key), "Key was not removed from inverse map.");
        assertFalse(map.inverseBidiMap().containsKey(value), "Value was not removed from inverse map.");
    }

    private void removeByKeySet(final BidiMap<?, ?> map, final Object key, final Object value) {
        map.remove(key);

        assertFalse(map.containsKey(key), "Key was not removed.");
        assertFalse(map.containsValue(value), "Value was not removed.");

        assertFalse(map.inverseBidiMap().containsValue(key), "Key was not removed from inverse map.");
        assertFalse(map.inverseBidiMap().containsKey(value), "Value was not removed from inverse map.");
    }

    private void removeValue(final BidiMap<?, ?> map, final Object value) {
        final Object key = map.removeValue(value);
        assertFalse(map.containsKey(key), "Key was not removed.");
        assertNull(map.getKey(value), "Value was not removed.");
    }

    @Test
    public void testBidiClear() {
        if (!isRemoveSupported()) {
            assertThrows(UnsupportedOperationException.class, () -> makeFullMap().clear());
            return;
        }

        BidiMap<?, ?> map = makeFullMap();
        map.clear();
        assertTrue(map.isEmpty(), "Map was not cleared.");
        assertTrue(map.inverseBidiMap().isEmpty(), "Inverse map was not cleared.");

        // Tests clear on inverse
        map = makeFullMap().inverseBidiMap();
        map.clear();
        assertTrue(map.isEmpty(), "Map was not cleared.");
        assertTrue(map.inverseBidiMap().isEmpty(), "Inverse map was not cleared.");
    }

    // testGetKey
    @Test
    public void testBidiGetKey() {
        doTestGetKey(makeFullMap(), getSampleKeys()[0], getSampleValues()[0]);
    }

    @Test
    public void testBidiGetKeyInverse() {
        doTestGetKey(
            makeFullMap().inverseBidiMap(),
            getSampleValues()[0],
            getSampleKeys()[0]);
    }

    // testInverse
    @Test
    public void testBidiInverse() {
        final BidiMap<K, V> map = makeFullMap();
        final BidiMap<V, K> inverseMap = map.inverseBidiMap();

        assertSame(
                map,
                inverseMap.inverseBidiMap(),
                "Inverse of inverse is not equal to original.");

        assertEquals(
                getSampleKeys()[0],
                inverseMap.get(getSampleValues()[0]),
                "Value not found for key.");

        assertEquals(
                getSampleValues()[0],
                inverseMap.getKey(getSampleKeys()[0]),
                "Key not found for value.");
    }

    @Test
    public void testBidiKeySetValuesOrder() {
        // Skip if collection is unordered
        Assumptions.assumeFalse((getIterationBehaviour() & AbstractCollectionTest.UNORDERED) != 0);
        resetFull();
        final Iterator<K> keys = map.keySet().iterator();
        final Iterator<V> values = map.values().iterator();
        while (keys.hasNext() && values.hasNext()) {
            final K key = keys.next();
            final V value = values.next();
            assertSame(map.get(key), value);
        }
        assertFalse(keys.hasNext());
        assertFalse(values.hasNext());
    }

    @Test
    public void testBidiMapIteratorSet() {
        final V newValue1 = getOtherValues()[0];
        final V newValue2 = getOtherValues()[1];

        resetFull();
        final BidiMap<K, V> bidi = getMap();
        final MapIterator<K, V> it = bidi.mapIterator();
        assertTrue(it.hasNext());
        final K key1 = it.next();

        if (!isSetValueSupported()) {
            assertThrows(UnsupportedOperationException.class, () -> it.setValue(newValue1));
            return;
        }

        it.setValue(newValue1);
        confirmed.put(key1, newValue1);
        assertSame(key1, it.getKey());
        assertSame(newValue1, it.getValue());
        assertTrue(bidi.containsKey(key1));
        assertTrue(bidi.containsValue(newValue1));
        assertEquals(newValue1, bidi.get(key1));
        verify();

        it.setValue(newValue1);  // same value - should be OK
        confirmed.put(key1, newValue1);
        assertSame(key1, it.getKey());
        assertSame(newValue1, it.getValue());
        assertTrue(bidi.containsKey(key1));
        assertTrue(bidi.containsValue(newValue1));
        assertEquals(newValue1, bidi.get(key1));
        verify();

        final K key2 = it.next();
        it.setValue(newValue2);
        confirmed.put(key2, newValue2);
        assertSame(key2, it.getKey());
        assertSame(newValue2, it.getValue());
        assertTrue(bidi.containsKey(key2));
        assertTrue(bidi.containsValue(newValue2));
        assertEquals(newValue2, bidi.get(key2));
        verify();

        // at this point
        // key1=newValue1, key2=newValue2
        assertThrows(IllegalArgumentException.class, () -> it.setValue(newValue1));  // should remove key1
        // below code was previously never executed
//        confirmed.put(key2, newValue1);
//        AbstractBidiMapTest.this.getConfirmed().remove(key1);
//        assertEquals(newValue1, it.getValue());
//        assertTrue(bidi.containsKey(it.getKey()));
//        assertTrue(bidi.containsValue(newValue1));
//        assertEquals(newValue1, bidi.get(it.getKey()));
//        assertFalse(bidi.containsKey(key1));
//        assertFalse(bidi.containsValue(newValue2));
//        verify();
//
//        // check for ConcurrentModification
//        it.next();  // if you fail here, maybe you should be throwing an IAE, see above
//        if (isRemoveSupported()) {
//            it.remove();
//        }
    }

    @Test
    public void testBidiModifyEntrySet() {
        if (!isSetValueSupported()) {
            return;
        }

        modifyEntrySet(makeFullMap());
        modifyEntrySet(makeFullMap().inverseBidiMap());
    }

    // BidiPut
    @Test
    @SuppressWarnings("unchecked")
    public void testBidiPut() {
        if (!isPutAddSupported() || !isPutChangeSupported()) {
            return;
        }

        final BidiMap<K, V> map = makeObject();
        final BidiMap<V, K> inverse = map.inverseBidiMap();
        assertEquals(0, map.size());
        assertEquals(map.size(), inverse.size());

        map.put((K) "A", (V) "B");
        assertEquals(1, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("B", map.get("A"));
        assertEquals("A", inverse.get("B"));

        map.put((K) "A", (V) "C");
        assertEquals(1, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("C", map.get("A"));
        assertEquals("A", inverse.get("C"));

        map.put((K) "B", (V) "C");
        assertEquals(1, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("C", map.get("B"));
        assertEquals("B", inverse.get("C"));

        map.put((K) "E", (V) "F");
        assertEquals(2, map.size());
        assertEquals(map.size(), inverse.size());
        assertEquals("F", map.get("E"));
        assertEquals("E", inverse.get("F"));
    }

    @Test
    public void testBidiRemove() {
        if (!isRemoveSupported()) {
            assertThrows(UnsupportedOperationException.class, () -> makeFullMap().remove(getSampleKeys()[0]));

            assertThrows(UnsupportedOperationException.class, () -> makeFullMap().removeValue(getSampleValues()[0]));

            return;
        }

        remove(makeFullMap(), getSampleKeys()[0]);
        remove(makeFullMap().inverseBidiMap(), getSampleValues()[0]);

        removeValue(makeFullMap(), getSampleValues()[0]);
        removeValue(makeFullMap().inverseBidiMap(), getSampleKeys()[0]);

        assertNull(makeFullMap().removeValue("NotPresent"));
    }

    @Test
    public void testBidiRemoveByEntrySet() {
        if (!isRemoveSupported()) {
            return;
        }

        removeByEntrySet(makeFullMap(), getSampleKeys()[0], getSampleValues()[0]);
        removeByEntrySet(makeFullMap().inverseBidiMap(), getSampleValues()[0], getSampleKeys()[0]);
    }

    @Test
    public void testBidiRemoveByKeySet() {
        if (!isRemoveSupported()) {
            return;
        }

        removeByKeySet(makeFullMap(), getSampleKeys()[0], getSampleValues()[0]);
        removeByKeySet(makeFullMap().inverseBidiMap(), getSampleValues()[0], getSampleKeys()[0]);
    }

    /**
     * Verifies that {@link #map} is still equal to {@link #confirmed}.
     * <p>
     * This implementation checks the inverse map as well.
     */
    @Override
    public void verify() {
        verifyInverse();
        super.verify();
    }

    public void verifyInverse() {
        assertEquals(map.size(), ((BidiMap<K, V>) map).inverseBidiMap().size());
        final Map<K, V> map1 = new HashMap<>(map);
        final Map<V, K> map2 = new HashMap<>(((BidiMap<K, V>) map).inverseBidiMap());
        final Set<K> keys1 = map1.keySet();
        final Set<V> keys2 = map2.keySet();
        final Collection<V> values1 = map1.values();
        final Collection<K> values2 = map2.values();
        assertTrue(keys1.containsAll(values2));
        assertTrue(values2.containsAll(keys1));
        assertTrue(values1.containsAll(keys2));
        assertTrue(keys2.containsAll(values1));
    }

}
