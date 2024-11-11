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
package org.apache.commons.collections4.multimap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.bag.AbstractBagTest;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.map.AbstractMapTest;
import org.apache.commons.collections4.multiset.AbstractMultiSetTest;
import org.apache.commons.collections4.set.AbstractSetTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Abstract test class for {@link MultiValuedMap} contract and methods.
 * <p>
 * To use, extend this class and implement the {@link #makeObject} method and if
 * necessary override the {@link #makeFullMap()} method.
 * </p>
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public abstract class AbstractMultiValuedMapTest<K, V> extends AbstractObjectTest {

    public class TestMultiValuedMapAsMap extends AbstractMapTest<Map<K, Collection<V>>, K, Collection<V>> {

        @Override
        public boolean areEqualElementsDistinguishable() {
            // work-around for a problem with the EntrySet: the entries contain
            // the wrapped collection, which will be automatically cleared
            // when the associated key is removed from the map as the collection
            // is not cached atm.
            return true;
        }

        @Override
        protected int getIterationBehaviour() {
            return AbstractMultiValuedMapTest.this.getIterationBehaviour();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<V>[] getNewSampleValues() {
            // See comment in getSampleValues() to understand why we are calling makeObject() and not
            // getMap(). See COLLECTIONS-661 for more.
            final boolean isSetValuedMap = AbstractMultiValuedMapTest.this.makeObject() instanceof SetValuedMap;
            final int maxV = getSampleTotalValueCount();
            final int maxK = getSampleKeySize();
            final V[] sampleValues = (V[]) new Object[maxV];
            for (int v = 0; v < maxV; v++) {
                // + something so that the key is different from an existing key.
                sampleValues[v] = makeValue(maxK + 9, v);
            }
            final int cpk = getSampleCountPerKey();
            final Collection<V>[] colArr = new Collection[maxK];
            for (int i = 0; i < maxK; i++) {
                final List<V> coll = new ArrayList<>(cpk);
                for (int j = 0; j < cpk; j++) {
                    coll.add(sampleValues[i * cpk + j]);
                }
                colArr[i] = isSetValuedMap ? new HashSet<>(coll) : coll;
            }
            return colArr;
        }

        @Override
        @SuppressWarnings("unchecked")
        public K[] getSampleKeys() {
            final K[] samplekeys = AbstractMultiValuedMapTest.this.getSampleKeys();
            final int maxK = getSampleKeySize();
            final int cpk = getSampleCountPerKey();
            final Object[] finalKeys = new Object[maxK];
            for (int i = 0; i < maxK; i++) {
                finalKeys[i] = samplekeys[i * cpk];
            }
            return (K[]) finalKeys;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<V>[] getSampleValues() {
            // Calling getMap() instead of makeObject() would make more sense, but due to concurrency
            // issues, this may lead to intermittent issues. See COLLECTIONS-661. A better solution
            // would be to re-design the tests, or add a boolean method to the parent.
            final boolean isSetValuedMap = AbstractMultiValuedMapTest.this.makeObject() instanceof SetValuedMap;
            final V[] sampleValues = AbstractMultiValuedMapTest.this.getSampleValues();
            final int maxK = getSampleKeySize();
            final int cpk = getSampleCountPerKey();
            final Collection<V>[] colArr = new Collection[maxK];
            for (int i = 0; i < maxK; i++) {
                final List<V> coll = new ArrayList<>(cpk);
                for (int j = 0; j < cpk; j++) {
                    coll.add(sampleValues[i * cpk + j]);
                }
                colArr[i] = isSetValuedMap ? new HashSet<>(coll) : coll;
            }
            return colArr;
        }

        @Override
        public boolean isAllowNullKey() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isPutAddSupported() {
            return false;
        }

        @Override
        public boolean isPutChangeSupported() {
            return false;
        }

        @Override
        public boolean isRemoveSupported() {
            return AbstractMultiValuedMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public Map<K, Collection<V>> makeFullMap() {
            return AbstractMultiValuedMapTest.this.makeFullMap().asMap();
        }

        @Override
        public Map<K, Collection<V>> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().asMap();
        }
    }

    public class TestMultiValuedMapEntries extends AbstractCollectionTest<Entry<K, V>> {

        @SuppressWarnings("unchecked")
        @Override
        public Entry<K, V>[] getFullElements() {
            return makeFullMap().entries().toArray(new Entry[0]);
        }

        @Override
        protected int getIterationBehaviour() {
            return AbstractMultiValuedMapTest.this.getIterationBehaviour();
        }

        @Override
        public boolean isAddSupported() {
            // Add not supported in entries view
            return false;
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isRemoveSupported() {
            return AbstractMultiValuedMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public Collection<Entry<K, V>> makeConfirmedCollection() {
            // never gets called, reset methods are overridden
            return null;
        }

        @Override
        public Collection<Entry<K, V>> makeConfirmedFullCollection() {
            // never gets called, reset methods are overridden
            return null;
        }

        @Override
        public Collection<Entry<K, V>> makeFullCollection() {
            return AbstractMultiValuedMapTest.this.makeFullMap().entries();
        }

        @Override
        public Collection<Entry<K, V>> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().entries();
        }

        @Override
        public void resetEmpty() {
            AbstractMultiValuedMapTest.this.resetEmpty();
            setCollection(AbstractMultiValuedMapTest.this.getMap().entries());
            TestMultiValuedMapEntries.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().entries());
        }

        @Override
        public void resetFull() {
            AbstractMultiValuedMapTest.this.resetFull();
            setCollection(AbstractMultiValuedMapTest.this.getMap().entries());
            TestMultiValuedMapEntries.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().entries());
        }

    }

    public class TestMultiValuedMapKeys extends AbstractMultiSetTest<K> {

        @Override
        public K[] getFullElements() {
            return getSampleKeys();
        }

        @Override
        protected int getIterationBehaviour() {
            return AbstractMultiValuedMapTest.this.getIterationBehaviour();
        }

        @Override
        public boolean isAddSupported() {
            return false;
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isRemoveSupported() {
            return false;
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public MultiSet<K> makeFullCollection() {
            return AbstractMultiValuedMapTest.this.makeFullMap().keys();
        }

        @Override
        public MultiSet<K> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().keys();
        }

        @Override
        public void resetEmpty() {
            AbstractMultiValuedMapTest.this.resetEmpty();
            setCollection(AbstractMultiValuedMapTest.this.getMap().keys());
            TestMultiValuedMapKeys.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().keys());
        }

        @Override
        public void resetFull() {
            AbstractMultiValuedMapTest.this.resetFull();
            setCollection(AbstractMultiValuedMapTest.this.getMap().keys());
            TestMultiValuedMapKeys.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().keys());
        }
    }

    public class TestMultiValuedMapKeySet extends AbstractSetTest<K> {

        @SuppressWarnings("unchecked")
        @Override
        public K[] getFullElements() {
            return (K[]) AbstractMultiValuedMapTest.this.makeFullMap().keySet().toArray();
        }

        @Override
        protected int getIterationBehaviour() {
            return AbstractMultiValuedMapTest.this.getIterationBehaviour();
        }

        @Override
        public boolean isAddSupported() {
            return false;
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isRemoveSupported() {
            return AbstractMultiValuedMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public Set<K> makeFullCollection() {
            return AbstractMultiValuedMapTest.this.makeFullMap().keySet();
        }

        @Override
        public Set<K> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().keySet();
        }
    }

    public class TestMultiValuedMapValues extends AbstractCollectionTest<V> {

        @Override
        public V[] getFullElements() {
            return getSampleValues();
        }

        @Override
        protected int getIterationBehaviour() {
            return AbstractMultiValuedMapTest.this.getIterationBehaviour();
        }

        @Override
        public boolean isAddSupported() {
            return false;
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isRemoveSupported() {
            return AbstractMultiValuedMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public Collection<V> makeConfirmedCollection() {
            // never gets called, reset methods are overridden
            return null;
        }

        @Override
        public Collection<V> makeConfirmedFullCollection() {
            // never gets called, reset methods are overridden
            return null;
        }

        @Override
        public Collection<V> makeFullCollection() {
            return AbstractMultiValuedMapTest.this.makeFullMap().values();
        }

        @Override
        public Collection<V> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().values();
        }

        @Override
        public void resetEmpty() {
            AbstractMultiValuedMapTest.this.resetEmpty();
            setCollection(AbstractMultiValuedMapTest.this.getMap().values());
            TestMultiValuedMapValues.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().values());
        }

        @Override
        public void resetFull() {
            AbstractMultiValuedMapTest.this.resetFull();
            setCollection(AbstractMultiValuedMapTest.this.getMap().values());
            TestMultiValuedMapValues.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().values());
        }
    }

    /** Map created by reset(). */
    protected MultiValuedMap<K, V> map;

    /** MultiValuedHashMap created by reset(). */
    protected MultiValuedMap<K, V> confirmed;

    protected void addSampleMappings(final MultiValuedMap<? super K, ? super V> map) {
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
    }

    public BulkTest bulkTestAsMap() {
        return new TestMultiValuedMapAsMap();
    }

    // Bulk Tests
    /**
     * Bulk test {@link MultiValuedMap#entries()}. This method runs through all
     * of the tests in {@link AbstractCollectionTest}. After modification
     * operations, {@link #verify()} is invoked to ensure that the map and the
     * other collection views are still valid.
     *
     * @return a {@link AbstractCollectionTest} instance for testing the map's
     *         values collection
     */
    public BulkTest bulkTestMultiValuedMapEntries() {
        return new TestMultiValuedMapEntries();
    }

    /**
     * Bulk test {@link MultiValuedMap#keys()}. This method runs through all of
     * the tests in {@link AbstractBagTest}. After modification operations,
     * {@link #verify()} is invoked to ensure that the map and the other
     * collection views are still valid.
     *
     * @return a {@link AbstractBagTest} instance for testing the map's values
     *         collection
     */
    public BulkTest bulkTestMultiValuedMapKeys() {
        return new TestMultiValuedMapKeys();
    }

    /**
     * Bulk test {@link MultiValuedMap#keySet()}. This method runs through all
     * of the tests in {@link AbstractSetTest}. After modification operations,
     * {@link #verify()} is invoked to ensure that the map and the other
     * collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing the map's key set
     */
    public BulkTest bulkTestMultiValuedMapKeySet() {
        return new TestMultiValuedMapKeySet();
    }

    /**
     * Bulk test {@link MultiValuedMap#values()}. This method runs through all
     * of the tests in {@link AbstractCollectionTest}. After modification
     * operations, {@link #verify()} is invoked to ensure that the map and the
     * other collection views are still valid.
     *
     * @return a {@link AbstractCollectionTest} instance for testing the map's
     *         values collection
     */
    public BulkTest bulkTestMultiValuedMapValues() {
        return new TestMultiValuedMapValues();
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.1"; // MultiValuedMap has been added in version 4.1
    }

    public MultiValuedMap<K, V> getConfirmed() {
        return confirmed;
    }

    /**
     * Gets a flag specifying the iteration behavior of the map.
     * This is used to change the assertions used by specific tests.
     * The default implementation returns 0 which indicates ordered iteration behavior.
     *
     * @return the iteration behavior
     * @see AbstractCollectionTest#UNORDERED
     */
    protected int getIterationBehaviour() {
        return 0;
    }

    public MultiValuedMap<K, V> getMap() {
        return map;
    }

    public int getSampleCountPerKey() {
        return 8;
    }

    /**
     * Returns the set of keys in the mappings used to test the map. This method
     * must return an array with the same length as {@link #getSampleValues()}
     * and all array elements must be different. The default implementation
     * constructs a set of String keys, and includes a single null key if
     * {@link #isAllowNullKey()} returns {@code true}.
     */
    @SuppressWarnings("unchecked")
    public K[] getSampleKeys() {
        final Object[] result = new Object[getSampleTotalValueCount()];
        final int cpk = getSampleCountPerKey();
        int k = 0;
        for (int i = 0; i < result.length; i += cpk, k++) {
            final K key = makeKey(k);
            for (int j = 0; j < cpk; j++) {
                result[i + j] = key;
            }
        }
        return (K[]) result;
    }

    public int getSampleKeySize() {
        return 256;
    }

    public int getSampleTotalValueCount() {
        return getSampleCountPerKey() * getSampleKeySize();
    }

    /**
     * Returns the set of values in the mappings used to test the map. This
     * method must return an array with the same length as
     * {@link #getSampleKeys()}. The default implementation constructs a set of
     * String values
     */
    @SuppressWarnings("unchecked")
    public V[] getSampleValues() {
        final Object[] result = new Object[getSampleTotalValueCount()];
        final int cpk = getSampleCountPerKey();
        int k = 0;
        for (int i = 0; i < result.length; i += cpk, k++) {
            for (int j = 0; j < cpk; j++) {
                result[i + j] = makeValue(k, j);
            }
        }
        return (V[]) result;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and
     * {@link #makeFullMap()} support the {@code put} and
     * {@code putAll} operations adding new mappings.
     * <p>
     * Default implementation returns true. Override if your collection class
     * does not support put adding.
     * </p>
     */
    public boolean isAddSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and
     * {@link #makeFullMap()} supports null keys.
     * <p>
     * Default implementation returns true. Override if your collection class
     * does not support null keys.
     * </p>
     */
    public boolean isAllowNullKey() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and
     * {@link #makeFullMap()} supports set value.
     * <p>
     * Default implementation returns false. Override if your collection class
     * supports set value.
     * </p>
     */
    public boolean isHashSetValue() {
        return false;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and
     * {@link #makeFullMap()} support the {@code remove} and
     * {@code clear} operations.
     * <p>
     * Default implementation returns true. Override if your collection class
     * does not support removal operations.
     * </p>
     */
    public boolean isRemoveSupported() {
        return true;
    }

    @Override
    public boolean isTestSerialization() {
        return true;
    }

    /**
     * Override to return a MultiValuedMap other than ArrayListValuedHashMap
     * as the confirmed map.
     *
     * @return a MultiValuedMap that is known to be valid
     */
    public MultiValuedMap<K, V> makeConfirmedMap() {
        return new ArrayListValuedHashMap<>();
    }

    protected MultiValuedMap<K, V> makeFullMap() {
        final MultiValuedMap<K, V> map = makeObject();
        addSampleMappings(map);
        return map;
    }

    <E> E makeKey(final int key) {
        return (E) new StringBuilder("k").append(key).toString();
    }

    @Override
    public abstract MultiValuedMap<K, V> makeObject();

    <E> E makeValue(final int key, final int value) {
        return (E) new StringBuilder("v").append(key).append('_').append(value).toString();
    }

    /**
     * Resets the {@link #map} and {@link #confirmed} fields to empty.
     */
    public void resetEmpty() {
        map = makeObject();
        confirmed = makeConfirmedMap();
    }

    /**
     * Resets the {@link #map} and {@link #confirmed} fields to full.
     */
    public void resetFull() {
        map = makeFullMap();
        confirmed = makeConfirmedMap();
        final K[] k = getSampleKeys();
        final V[] v = getSampleValues();
        for (int i = 0; i < k.length; i++) {
            confirmed.put(k[i], v[i]);
        }
    }

//    public void testKeyedIterator() {
//        final MultiValuedMap<K, V> map = makeFullMap();
//        final ArrayList<Object> actual = new ArrayList<Object>(IteratorUtils.toList(map.iterator("k0")));
//        final ArrayList<Object> expected = new ArrayList<Object>(Arrays.asList("v1_1", "v0_1"));
//        assertEquals(expected, actual);
//    }

    public void setConfirmed(final MultiValuedMap<K, V> confirmed) {
        this.confirmed = confirmed;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddMappingThroughGet() {
        assumeTrue(isAddSupported());
        resetEmpty();
        final MultiValuedMap<K, V> map = getMap();
        final Collection<V> col1 = map.get((K) "k0");
        final Collection<V> col2 = map.get((K) "k0");
        assertTrue(col1.isEmpty());
        assertTrue(col2.isEmpty());
        assertEquals(0, map.size());
        col1.add((V) "v1_1");
        col2.add((V) "v0_1");
        assertTrue(map.containsKey("k0"));
        assertTrue(map.containsMapping("k0", "v1_1"));
        assertTrue(map.containsMapping("k0", "v0_1"));
        assertTrue(map.containsValue("v1_1"));
        assertTrue(map.containsValue("v0_1"));
        assertTrue(col1.contains("v0_1"));
        assertTrue(col2.contains("v1_1"));
    }

    /*public void testRemoveViaGetCollectionRemove() {
        if (!isRemoveSupported()) {
            return;
        }
        final MultiValuedMap<K, V> map = makeFullMap();
        Collection<V> values = map.get("k0");
        values.remove("v1_1");
        values.remove("v0_1");
        assertFalse(map.containsKey("k0"));
        assertEquals(4, map.size());
    }*/

//    public void testRemoveAllViaKeyedIterator() {
//        if (!isRemoveSupported()) {
//            return;
//        }
//        final MultiValuedMap<K, V> map = makeFullMap();
//        for (final Iterator<?> i = map.iterator("k0"); i.hasNext();) {
//            i.next();
//            i.remove();
//        }
//        assertNull(map.get("k0"));
//        assertEquals(4, map.size());
//    }

    @Test
    public void testAsMapGet_Empty() {
        resetEmpty();
        final Map<K, Collection<V>> mapCol = getMap().asMap();
        assertNull(mapCol.get("k0"));
        assertEquals(0, mapCol.size());
    }

    @Test
    public void testAsMapGet_Full() {
        resetFull();
        final Map<K, Collection<V>> mapCol = getMap().asMap();
        final int maxK = getSampleKeySize();
        final int maxV = getSampleCountPerKey();
        for (int k = 0; k < maxK; k++) {
            final Collection<V> col = mapCol.get(makeKey(k));
            for (int v = 0; v < maxV; v++) {
                assertTrue(col.contains(makeValue(k, v)));
            }
        }
    }

    @Test
    public void testAsMapRemove() {
        assumeTrue(isRemoveSupported());
        resetFull();
        final Map<K, Collection<V>> mapCol = getMap().asMap();
        final int maxK = getSampleKeySize();
        int expectedSize = getMap().size();
        for (int k = 0; k < maxK; k++) {
            final K key = makeKey(k);
            mapCol.remove(key);
            assertFalse(getMap().containsKey(key));
            expectedSize -= getSampleCountPerKey();
            assertEquals(expectedSize, getMap().size());
        }
        assertFalse(getMap().containsKey("k0"));
        assertEquals(0, getMap().size());
    }

    @Test
    public void testContainsValue() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final int maxK = getSampleKeySize();
        final int maxV = getSampleCountPerKey();
        for (int k = 0; k < maxK; k++) {
            for (int v = 0; v < maxV; v++) {
                assertTrue(map.containsValue(makeValue(k, v)));
            }
        }
        assertFalse(map.containsValue("quatro"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContainsValue_Key() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final int maxK = getSampleKeySize();
        final int maxV = getSampleCountPerKey();
        for (int k = 0; k < maxK; k++) {
            for (int v = 0; v < maxV; v++) {
                assertTrue(map.containsMapping(makeKey(k), makeValue(k, v)));            }
        }
        assertFalse(map.containsMapping("k1", "2"));
        if (!isAddSupported()) {
            return;
        }
        map.put((K) "A", (V) "AA");
        assertTrue(map.containsMapping("A", "AA"));
        assertFalse(map.containsMapping("A", "AB"));
    }

    /**
     * Manual serialization testing as this class cannot easily extend the AbstractTestMap
     */
    @Test
    public void testEmptyMapCompatibility() throws Exception {
        final MultiValuedMap<?, ?> map = makeObject();
        final MultiValuedMap<?, ?> map2 =
                (MultiValuedMap<?, ?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(map));
        assertEquals(0, map2.size(), "Map is empty");
    }

    @Test
    public void testEntriesCollectionIterator() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final Collection<V> values = new ArrayList<>(map.values());
        for (final Entry<K, V> entry : map.entries()) {
            assertTrue(map.containsMapping(entry.getKey(), entry.getValue()));
            assertTrue(values.contains(entry.getValue()));
            if (isRemoveSupported()) {
                assertTrue(values.remove(entry.getValue()));
            }
        }
        if (isRemoveSupported()) {
            assertTrue(values.isEmpty());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @Disabled("There is no code to create this test fixture?")
    public void testFullMapCompatibility() throws Exception {
        final MultiValuedMap map = makeFullMap();
        final MultiValuedMap map2 =
                (MultiValuedMap) readExternalFormFromDisk(getCanonicalFullCollectionName(map));
        assertEquals(map.size(), map2.size(), "Map is the right size");
        for (final Object key : map.keySet()) {
            assertTrue(CollectionUtils.isEqualCollection(map.get(key), map2.get(key)),
                    "Map had inequal elements");
            if (isRemoveSupported()) {
                map2.remove(key);
            }
        }
        if (isRemoveSupported()) {
            assertEquals(0, map2.size(), "Map had extra values");
        }
    }

//    @SuppressWarnings("unchecked")
//    public void testIterator_Key() {
//        final MultiValuedMap<K, V> map = makeFullMap();
//        Iterator<V> it = map.iterator("k0");
//        assertTrue(it.hasNext());
//        Set<V> values = new HashSet<V>();
//        while (it.hasNext()) {
//            values.add(it.next());
//        }
//        assertTrue(values.contains("v0_1"));
//        assertTrue(values.contains("v1_1"));
//        assertFalse(map.iterator("A").hasNext());
//        assertFalse(map.iterator("A").hasNext());
//        if (!isAddSupported()) {
//            return;
//        }
//        map.put((K) "A", (V) "AA");
//        it = map.iterator("A");
//        assertTrue(it.hasNext());
//        it.next();
//        assertFalse(it.hasNext());
//    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final int maxK = getSampleKeySize();
        final int maxV = getSampleCountPerKey();
        for (int k = 0; k < maxK; k++) {
            for (int v = 0; v < maxV; v++) {
                assertTrue(map.get((K) makeKey(k)).contains(makeValue(k, v)));
            }
        }
    }

    @Test
    public void testKeyContainsValue() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final int maxK = getSampleKeySize();
        final int maxV = getSampleCountPerKey();
        for (int k = 0; k < maxK; k++) {
            for (int v = 0; v < maxV; v++) {
                assertTrue(map.containsMapping(makeKey(k), makeValue(k, v)));
            }
        }
        assertFalse(map.containsMapping("k", "v"));
    }

    @Test
    @SuppressWarnings({ "unchecked", "cast" })
    public void testKeysBagContainsAll() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final MultiSet<K> keyMultiSet = map.keys();

        final List<String> col = new ArrayList<>();
        final int maxK = getSampleKeySize();
        for (int k = 0; k < maxK; k++) {
            col.add(makeKey(k));
        }
        for (int k = 0; k < maxK; k++) {
            col.add(makeKey(k));
        }
        // Make sure we test Collection API
        assertTrue(keyMultiSet.containsAll((Collection<K>) col));
    }

    @Test
    public void testKeysBagIterator1() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final Collection<K> col = new ArrayList<>(map.keys());
        final Bag<K> bag = new HashBag<>(col);
        final int maxK = getSampleKeySize();
        for (int k = 0; k < maxK; k++) {
            assertEquals(getSampleCountPerKey(), bag.getCount(makeKey(k)));
        }
        assertEquals(getSampleTotalValueCount(), bag.size());
    }

    @Test
    public void testKeysBagIterator2() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final Iterable<K> iterable = new ArrayList<>(map.keys());
        final Bag<K> bag = new HashBag<>(iterable);
        final int maxK = getSampleKeySize();
        for (int k = 0; k < maxK; k++) {
            assertEquals(getSampleCountPerKey(), bag.getCount(makeKey(k)));
        }
        assertEquals(getSampleTotalValueCount(), bag.size());
    }

    @Test
    public void testKeySetSize() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertEquals(getSampleKeySize(), map.keySet().size());
    }

    @Test
    public void testKeysMultiSet() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final MultiSet<K> keyMultiSet = map.keys();
        final int maxK = getSampleKeySize();
        for (int k = 0; k < maxK; k++) {
            assertEquals(getSampleCountPerKey(), keyMultiSet.getCount(makeKey(k)));
        }
        assertEquals(0, keyMultiSet.getCount("conut"));
        assertEquals(getSampleTotalValueCount(), keyMultiSet.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapEquals() {
        assumeTrue(isAddSupported());
        final MultiValuedMap<K, V> one = makeObject();
        final Integer value = Integer.valueOf(1);
        one.put((K) "One", (V) value);
        one.removeMapping("One", value);

        final MultiValuedMap<K, V> two = makeObject();
        assertEquals(two, one);
    }

    @Test
    public void testMapIterator() {
        resetEmpty();
        MapIterator<K, V> mapIt  = getMap().mapIterator();
        assertFalse(mapIt.hasNext());

        resetFull();
        mapIt = getMap().mapIterator();
        while (mapIt.hasNext()) {
            final K key = mapIt.next();
            final V value = mapIt.getValue();
            assertTrue(getMap().containsMapping(key, value));
        }
    }

    @Test
    public void testMapIteratorRemove() {
        assumeTrue(isRemoveSupported());
        resetFull();
        final MapIterator<K, V> mapIt = getMap().mapIterator();
        while (mapIt.hasNext()) {
            mapIt.next();
            mapIt.remove();
        }
        assertTrue(getMap().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapIteratorUnsupportedSet() {
        resetFull();
        final MapIterator<K, V> mapIt = getMap().mapIterator();
        mapIt.next();
        assertThrows(UnsupportedOperationException.class, () -> mapIt.setValue((V) "some value"));
    }

    @Test
    public void testMultipleValues() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final int maxK = getSampleKeySize();
        final int maxV = getSampleCountPerKey();
        for (int k = 0; k < maxK; k++) {
            final Collection<V> col = map.get(makeKey(k));
            for (int v = 0; v < maxV; v++) {
                assertTrue(col.contains(makeValue(k, v)));
            }
        }

    }

    @Test
    public void testMultiValuedMapIterator() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final MapIterator<K, V> it = map.mapIterator();
        assertThrows(IllegalStateException.class, () -> it.getKey());
        assertThrows(IllegalStateException.class, () -> it.getValue());
        if (isAddSupported()) {
            assertThrows(IllegalStateException.class, () -> it.setValue((V) "V"));
        }
        if (!isHashSetValue() && isAddSupported()) {
            assertTrue(it.hasNext());
            final MultiValuedMap<K, V> dejaVu = makeObject();
            while (it.hasNext()) {
                final K next = it.next();
                assertNotNull(next);
                final K itKey = it.getKey();
                assertEquals(next, itKey);
                final V itValue = it.getValue();
                dejaVu.put(itKey, itValue);
                assertThrows(UnsupportedOperationException.class, () -> it.setValue((V) "threetrois"));
            }
            assertEquals(map, dejaVu);
            assertEquals(dejaVu, map);
            assertThrows(UnsupportedOperationException.class, () -> it.setValue((V) "threetrois"));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoMappingReturnsEmptyCol() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertTrue(map.get((K) "whatever").isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPutAll_KeyIterable() {
        assumeTrue(isAddSupported());
        final MultiValuedMap<K, V> map = makeObject();
        Collection<V> coll = (Collection<V>) Arrays.asList("X", "Y", "Z");

        assertTrue(map.putAll((K) "A", coll));
        assertEquals(3, map.get((K) "A").size());
        assertTrue(map.containsMapping("A", "X"));
        assertTrue(map.containsMapping("A", "Y"));
        assertTrue(map.containsMapping("A", "Z"));

        assertThrows(NullPointerException.class, () -> map.putAll((K) "A", null),
                "expecting NullPointerException");

        assertEquals(3, map.get((K) "A").size());
        assertTrue(map.containsMapping("A", "X"));
        assertTrue(map.containsMapping("A", "Y"));
        assertTrue(map.containsMapping("A", "Z"));

        assertFalse(map.putAll((K) "A", new ArrayList<>()));
        assertEquals(3, map.get((K) "A").size());
        assertTrue(map.containsMapping("A", "X"));
        assertTrue(map.containsMapping("A", "Y"));
        assertTrue(map.containsMapping("A", "Z"));

        coll = (Collection<V>) Arrays.asList("M");
        assertTrue(map.putAll((K) "A", coll));
        assertEquals(4, map.get((K) "A").size());
        assertTrue(map.containsMapping("A", "X"));
        assertTrue(map.containsMapping("A", "Y"));
        assertTrue(map.containsMapping("A", "Z"));
        assertTrue(map.containsMapping("A", "M"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPutAll_Map1() {
        assumeTrue(isAddSupported());
        final MultiValuedMap<K, V> original = makeObject();
        original.put((K) "key", (V) "object1");
        original.put((K) "key", (V) "object2");

        final MultiValuedMap<K, V> test = makeObject();
        test.put((K) "keyA", (V) "objectA");
        test.put((K) "key", (V) "object0");
        test.putAll(original);

        final MultiValuedMap<K, V> originalNull = null;
        assertThrows(NullPointerException.class, () -> test.putAll(originalNull),
                "expecting NullPointerException");

        assertEquals(2, test.keySet().size());
        assertEquals(4, test.size());
        assertEquals(1, test.get((K) "keyA").size());
        assertEquals(3, test.get((K) "key").size());
        assertTrue(test.containsValue("objectA"));
        assertTrue(test.containsValue("object0"));
        assertTrue(test.containsValue("object1"));
        assertTrue(test.containsValue("object2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPutAll_Map2() {
        assumeTrue(isAddSupported());
        final Map<K, V> original = new HashMap<>();
        original.put((K) "keyX", (V) "object1");
        original.put((K) "keyY", (V) "object2");

        final MultiValuedMap<K, V> test = makeObject();
        test.put((K) "keyA", (V) "objectA");
        test.put((K) "keyX", (V) "object0");
        test.putAll(original);

        final Map<K, V> originalNull = null;
        assertThrows(NullPointerException.class, () -> test.putAll(originalNull),
                "expecting NullPointerException");

        assertEquals(3, test.keySet().size());
        assertEquals(4, test.size());
        assertEquals(1, test.get((K) "keyA").size());
        assertEquals(2, test.get((K) "keyX").size());
        assertEquals(1, test.get((K) "keyY").size());
        assertTrue(test.containsValue("objectA"));
        assertTrue(test.containsValue("object0"));
        assertTrue(test.containsValue("object1"));
        assertTrue(test.containsValue("object2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemove_KeyItem() {
        assumeTrue(isAddSupported());
        assumeTrue(isRemoveSupported());
        final MultiValuedMap<K, V> map = makeObject();
        map.put((K) "A", (V) "AA");
        map.put((K) "A", (V) "AB");
        map.put((K) "A", (V) "AC");
        assertFalse(map.removeMapping("C", "CA"));
        assertFalse(map.removeMapping("A", "AD"));
        assertTrue(map.removeMapping("A", "AC"));
        assertTrue(map.removeMapping("A", "AB"));
        assertTrue(map.removeMapping("A", "AA"));
        //assertEquals(new MultiValuedHashMap<K, V>(), map);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveAllViaEntriesIterator() {
        assumeTrue(isRemoveSupported());
        final MultiValuedMap<K, V> map = makeFullMap();
        for (final Iterator<?> i = map.entries().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertTrue(map.get((K) "k0").isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveAllViaValuesIterator() {
        assumeTrue(isRemoveSupported());
        final MultiValuedMap<K, V> map = makeFullMap();
        for (final Iterator<?> i = map.values().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertTrue(map.get((K) "k0").isEmpty());
        assertTrue(map.isEmpty());
    }

    @Test
    public void testRemoveMappingThroughGet() {
        assumeTrue(isRemoveSupported());
        resetFull();
        final MultiValuedMap<K, V> map = getMap();
        final int cpk = getSampleCountPerKey();
        int expectedCount = getSampleTotalValueCount();
        assertEquals(expectedCount, map.size());
        for (int k = 0; k < getSampleKeySize(); k++) {
            final Object key = makeKey(k);
            @SuppressWarnings("unchecked")
            Collection<V> col = map.get((K) key);
            assertEquals(cpk, col.size());
            for (int i = 0; i < cpk; i++) {
                final Object value = makeValue(k, i);
                assertTrue(col.remove(value), () -> value.toString());
            }
            for (int i = 0; i < cpk; i++) {
                assertFalse(col.remove(makeValue(k, i)));
            }
            assertFalse(map.containsKey(key));
            for (int i = 0; i < cpk; i++) {
                assertFalse(map.containsMapping(key, i));
            }
            for (int i = 0; i < cpk; i++) {
                assertFalse(map.containsValue(makeValue(k, i)));
            }
            expectedCount -= cpk;
            assertEquals(expectedCount, map.size());
            col = map.remove(key);
            assertNotNull(col);
            assertEquals(0, col.size());
        }
    }

    @Test
    public void testRemoveMappingThroughGetIterator() {
        assumeTrue(isRemoveSupported());
        resetFull();
        final MultiValuedMap<K, V> map = getMap();
        int expectedSize = map.size();
        final int maxK = getSampleKeySize();
        for (int k = 0; k < maxK; k++) {
            final String key = makeKey(k);
            final int cpk = getSampleCountPerKey();
            @SuppressWarnings("unchecked")
            final Iterator<V> it = map.get((K) key).iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
            assertFalse(map.containsKey(key));
            for (int j = 0; j < cpk; j++) {
                assertFalse(map.containsMapping(key, makeValue(k + 1, j)));
                final Object value = makeValue(k, j);
                assertFalse(map.containsMapping(key, value));
                assertFalse(map.containsValue(value));
            }
            expectedSize -= cpk;
            assertEquals(expectedSize, map.size());
            final Collection<V> coll = map.remove("k0");
            assertNotNull(coll);
            assertEquals(0, coll.size());
        }
    }

    @Test
    public void testRemoveViaValuesRemove() {
        assumeTrue(isRemoveSupported());
        final MultiValuedMap<K, V> map = makeFullMap();
        final Collection<V> values = map.values();
        final int maxK = getSampleKeySize();
        final int maxV = getSampleCountPerKey();
        int expectedSize = map.size();
        for (int k = 0; k < maxK; k++) {
            for (int v = 0; v < maxV; v++) {
                values.remove(makeValue(k, v));
            }
            assertFalse(map.containsKey(makeKey(k)));
            expectedSize -= maxV;
            assertEquals(expectedSize, map.size());
        }
        assertEquals(0, map.size());
    }

    @Test
    public void testSize() {
        assertEquals(getSampleTotalValueCount(), makeFullMap().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSize_Key() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final int maxK = getSampleKeySize();
        for (int k = 0; k < maxK; k++) {
            assertEquals(getSampleCountPerKey(), map.get((K) makeKey(k)).size());
        }
        if (!isAddSupported()) {
            return;
        }
        map.put((K) "A", (V) "AA");
        assertEquals(1, map.get((K) "A").size());
        // assertEquals(0, map.get("B").size());
        map.put((K) "B", (V) "BA");
        assertEquals(1, map.get((K) "A").size());
        assertEquals(1, map.get((K) "B").size());
        map.put((K) "B", (V) "BB");
        assertEquals(1, map.get((K) "A").size());
        assertEquals(2, map.get((K) "B").size());
        map.put((K) "B", (V) "BC");
        assertEquals(1, map.get((K) "A").size());
        assertEquals(3, map.get((K) "B").size());
        if (!isRemoveSupported()) {
            return;
        }
        map.remove("A");
        // assertEquals(0, map.get("A").size());
        assertEquals(3, map.get((K) "B").size());
        map.removeMapping("B", "BC");
        // assertEquals(0, map.get("A").size());
        assertEquals(2, map.get((K) "B").size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSizeWithPutRemove() {
        assumeTrue(isAddSupported());
        assumeTrue(isRemoveSupported());
        final MultiValuedMap<K, V> map = makeObject();
        assertEquals(0, map.size());
        map.put((K) "A", (V) "AA");
        assertEquals(1, map.size());
        map.put((K) "B", (V) "BA");
        assertEquals(2, map.size());
        map.put((K) "B", (V) "BB");
        assertEquals(3, map.size());
        map.put((K) "B", (V) "BC");
        assertEquals(4, map.size());
        map.remove("A");
        assertEquals(3, map.size());
        map.removeMapping("B", "BC");
        assertEquals(2, map.size());
    }

    @Test
    public void testToString() {
        assumeTrue(isAddSupported());
        final MultiValuedMap<K, V> map = makeObject();
        map.put((K) "A", (V) "X");
        map.put((K) "A", (V) "Y");
        map.put((K) "A", (V) "Z");
        map.put((K) "B", (V) "U");
        map.put((K) "B", (V) "V");
        map.put((K) "B", (V) "W");
        assertTrue("{A=[X, Y, Z], B=[U, V, W]}".equals(map.toString()) || "{B=[U, V, W], A=[X, Y, Z]}".equals(map.toString()));

        final MultiValuedMap<K, V> originalNull = null;
        assertThrows(NullPointerException.class, () -> map.putAll(originalNull), "expecting NullPointerException");
        assertTrue("{A=[X, Y, Z], B=[U, V, W]}".equals(map.toString()) || "{B=[U, V, W], A=[X, Y, Z]}".equals(map.toString()));

        map.remove("A");
        map.remove("B");
        assertEquals("{}", map.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValues() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final HashSet<V> expected = new HashSet<>();
        final int maxK = getSampleKeySize();
        final int maxV = getSampleCountPerKey();
        for (int k = 0; k < maxK; k++) {
            for (int v = 0; v < maxV; v++) {
                expected.add((V) makeValue(k, v));
            }
        }
        final Collection<V> c = map.values();
        assertEquals(getSampleTotalValueCount(), c.size());
        assertEquals(expected, new HashSet<>(c));
    }

}
