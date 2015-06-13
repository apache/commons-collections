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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.apache.commons.collections4.bag.AbstractBagTest;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.map.AbstractMapTest;
import org.apache.commons.collections4.multiset.AbstractMultiSetTest;
import org.apache.commons.collections4.set.AbstractSetTest;

/**
 * Abstract test class for {@link MultiValuedMap} contract and methods.
 * <p>
 * To use, extend this class and implement the {@link #makeObject} method and if
 * necessary override the {@link #makeFullMap()} method.
 *
 * @since 4.1
 * @version $Id$
 */
public abstract class AbstractMultiValuedMapTest<K, V> extends AbstractObjectTest {

    /** Map created by reset(). */
    protected MultiValuedMap<K, V> map;

    /** MultiValuedHashMap created by reset(). */
    protected MultiValuedMap<K, V> confirmed;

    public AbstractMultiValuedMapTest(String testName) {
        super(testName);
    }

    @Override
    abstract public MultiValuedMap<K, V> makeObject();

    @Override
    public String getCompatibilityVersion() {
        return "4.1"; // MultiValuedMap has been added in version 4.1
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and
     * {@link #makeFullMap()} support the <code>put</code> and
     * <code>putAll</code> operations adding new mappings.
     * <p>
     * Default implementation returns true. Override if your collection class
     * does not support put adding.
     */
    public boolean isAddSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and
     * {@link #makeFullMap()} support the <code>remove</code> and
     * <code>clear</code> operations.
     * <p>
     * Default implementation returns true. Override if your collection class
     * does not support removal operations.
     */
    public boolean isRemoveSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and
     * {@link #makeFullMap()} supports null keys.
     * <p>
     * Default implementation returns true. Override if your collection class
     * does not support null keys.
     */
    public boolean isAllowNullKey() {
        return true;
    }

    /**
     * Returns the set of keys in the mappings used to test the map. This method
     * must return an array with the same length as {@link #getSampleValues()}
     * and all array elements must be different. The default implementation
     * constructs a set of String keys, and includes a single null key if
     * {@link #isAllowNullKey()} returns <code>true</code>.
     */
    @SuppressWarnings("unchecked")
    public K[] getSampleKeys() {
        final Object[] result = new Object[] {
                "one", "one", "two", "two",
                "three", "three"
        };
        return (K[]) result;
    }

    /**
     * Returns the set of values in the mappings used to test the map. This
     * method must return an array with the same length as
     * {@link #getSampleKeys()}. The default implementation constructs a set of
     * String values
     */
    @SuppressWarnings("unchecked")
    public V[] getSampleValues() {
        final Object[] result = new Object[] {
                "uno", "un", "dos", "deux",
                "tres", "trois"
        };
        return (V[]) result;
    }

    protected MultiValuedMap<K, V> makeFullMap() {
        final MultiValuedMap<K, V> map = makeObject();
        addSampleMappings(map);
        return map;
    }

    protected void addSampleMappings(MultiValuedMap<? super K, ? super V> map) {
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
    }

    /**
     * Override to return a MultiValuedMap other than MultiValuedHashMap as the
     * confirmed map.
     *
     * @return a MultiValuedMap that is known to be valid
     */
    public MultiValuedMap<K, V> makeConfirmedMap() {
        return new MultiValuedHashMap<K, V>();
    }

    public MultiValuedMap<K, V> getConfirmed() {
        return this.confirmed;
    }

    public void setConfirmed(MultiValuedMap<K, V> map) {
        this.confirmed = map;
    }

    public MultiValuedMap<K, V> getMap() {
        return this.map;
    }

    /**
     * Resets the {@link #map} and {@link #confirmed} fields to empty.
     */
    public void resetEmpty() {
        this.map = makeObject();
        this.confirmed = makeConfirmedMap();
    }

    /**
     * Resets the {@link #map} and {@link #confirmed} fields to full.
     */
    public void resetFull() {
        this.map = makeFullMap();
        this.confirmed = makeConfirmedMap();
        final K[] k = getSampleKeys();
        final V[] v = getSampleValues();
        for (int i = 0; i < k.length; i++) {
            confirmed.put(k[i], v[i]);
        }
    }

    @SuppressWarnings("unchecked")
    public void testNoMappingReturnsEmptyCol() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertTrue(map.get((K) "whatever").isEmpty());
    }

    public void testMultipleValues() {
        final MultiValuedMap<K, V> map = makeFullMap();
        @SuppressWarnings("unchecked")
        Collection<V> col = map.get((K) "one");
        assertTrue(col.contains("uno"));
        assertTrue(col.contains("un"));
    }

    @SuppressWarnings("unchecked")
    public void testGet() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertTrue(map.get((K) "one").contains("uno"));
        assertTrue(map.get((K) "one").contains("un"));
        assertTrue(map.get((K) "two").contains("dos"));
        assertTrue(map.get((K) "two").contains("deux"));
        assertTrue(map.get((K) "three").contains("tres"));
        assertTrue(map.get((K) "three").contains("trois"));
    }

    @SuppressWarnings("unchecked")
    public void testAddMappingThroughGet(){
        if (!isAddSupported()) {
            return;
        }
        resetEmpty();
        final MultiValuedMap<K, V> map =  getMap();
        Collection<V> col1 = map.get((K) "one");
        Collection<V> col2 = map.get((K) "one");
        assertTrue(col1.isEmpty());
        assertTrue(col2.isEmpty());
        assertEquals(0, map.size());
        col1.add((V) "uno");
        col2.add((V) "un");
        assertTrue(map.containsKey("one"));
        assertTrue(map.containsMapping("one", "uno"));
        assertTrue(map.containsMapping("one", "un"));
        assertTrue(map.containsValue("uno"));
        assertTrue(map.containsValue("un"));
        assertTrue(col1.contains("un"));
        assertTrue(col2.contains("uno"));
    }

    public void testRemoveMappingThroughGet() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        final MultiValuedMap<K, V> map = getMap();
        @SuppressWarnings("unchecked")
        Collection<V> col = map.get((K) "one");
        assertEquals(2, col.size());
        assertEquals(6, map.size());
        col.remove("uno");
        col.remove("un");
        assertFalse(map.containsKey("one"));
        assertFalse(map.containsMapping("one", "uno"));
        assertFalse(map.containsMapping("one", "un"));
        assertFalse(map.containsValue("uno"));
        assertFalse(map.containsValue("un"));
        assertEquals(4, map.size());
        col = map.remove("one");
        assertNotNull(col);
        assertEquals(0, col.size());
    }

    public void testRemoveMappingThroughGetIterator() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        final MultiValuedMap<K, V> map = getMap();
        @SuppressWarnings("unchecked")
        Iterator<V> it = map.get((K) "one").iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        assertFalse(map.containsKey("one"));
        assertFalse(map.containsMapping("one", "uno"));
        assertFalse(map.containsMapping("one", "un"));
        assertFalse(map.containsValue("uno"));
        assertFalse(map.containsValue("un"));
        assertEquals(4, map.size());
        Collection<V> coll = map.remove("one");
        assertNotNull(coll);
        assertEquals(0, coll.size());
    }

    public void testContainsValue() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertTrue(map.containsValue("uno"));
        assertTrue(map.containsValue("un"));
        assertTrue(map.containsValue("dos"));
        assertTrue(map.containsValue("deux"));
        assertTrue(map.containsValue("tres"));
        assertTrue(map.containsValue("trois"));
        assertFalse(map.containsValue("quatro"));
    }

    public void testKeyContainsValue() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertTrue(map.containsMapping("one", "uno"));
        assertTrue(map.containsMapping("one", "un"));
        assertTrue(map.containsMapping("two", "dos"));
        assertTrue(map.containsMapping("two", "deux"));
        assertTrue(map.containsMapping("three", "tres"));
        assertTrue(map.containsMapping("three", "trois"));
        assertFalse(map.containsMapping("four", "quatro"));
    }

    @SuppressWarnings("unchecked")
    public void testValues() {
        final MultiValuedMap<K, V> map = makeFullMap();
        final HashSet<V> expected = new HashSet<V>();
        expected.add((V) "uno");
        expected.add((V) "dos");
        expected.add((V) "tres");
        expected.add((V) "un");
        expected.add((V) "deux");
        expected.add((V) "trois");
        final Collection<V> c = map.values();
        assertEquals(6, c.size());
        assertEquals(expected, new HashSet<V>(c));
    }

//    public void testKeyedIterator() {
//        final MultiValuedMap<K, V> map = makeFullMap();
//        final ArrayList<Object> actual = new ArrayList<Object>(IteratorUtils.toList(map.iterator("one")));
//        final ArrayList<Object> expected = new ArrayList<Object>(Arrays.asList("uno", "un"));
//        assertEquals(expected, actual);
//    }

    @SuppressWarnings("unchecked")
    public void testRemoveAllViaValuesIterator() {
        if (!isRemoveSupported()) {
            return;
        }
        final MultiValuedMap<K, V> map = makeFullMap();
        for (final Iterator<?> i = map.values().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertTrue(map.get((K) "one").isEmpty());
        assertTrue(map.isEmpty());
    }

    public void testRemoveViaValuesRemove() {
        if (!isRemoveSupported()) {
            return;
        }
        final MultiValuedMap<K, V> map = makeFullMap();
        Collection<V> values = map.values();
        values.remove("uno");
        values.remove("un");
        assertFalse(map.containsKey("one"));
        assertEquals(4, map.size());
    }

    /*public void testRemoveViaGetCollectionRemove() {
        if (!isRemoveSupported()) {
            return;
        }
        final MultiValuedMap<K, V> map = makeFullMap();
        Collection<V> values = map.get("one");
        values.remove("uno");
        values.remove("un");
        assertFalse(map.containsKey("one"));
        assertEquals(4, map.size());
    }*/

//    public void testRemoveAllViaKeyedIterator() {
//        if (!isRemoveSupported()) {
//            return;
//        }
//        final MultiValuedMap<K, V> map = makeFullMap();
//        for (final Iterator<?> i = map.iterator("one"); i.hasNext();) {
//            i.next();
//            i.remove();
//        }
//        assertNull(map.get("one"));
//        assertEquals(4, map.size());
//    }

    public void testEntriesCollectionIterator() {
        final MultiValuedMap<K, V> map = makeFullMap();
        Collection<V> values = new ArrayList<V>(map.values());
        Iterator<Map.Entry<K, V>> iterator = map.entries().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
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

    @SuppressWarnings("unchecked")
    public void testRemoveAllViaEntriesIterator() {
        if (!isRemoveSupported()) {
            return;
        }
        final MultiValuedMap<K, V> map = makeFullMap();
        for (final Iterator<?> i = map.entries().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertTrue(map.get((K) "one").isEmpty());
        assertEquals(0, map.size());
    }

    public void testSize() {
        assertEquals(6, makeFullMap().size());
    }

    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testMapEquals() {
        if (!isAddSupported()) {
            return;
        }
        final MultiValuedMap<K, V> one = makeObject();
        final Integer value = Integer.valueOf(1);
        one.put((K) "One", (V) value);
        one.removeMapping("One", value);

        final MultiValuedMap<K, V> two = makeObject();
        assertEquals(two, one);
    }

    @SuppressWarnings("unchecked")
    public void testSizeWithPutRemove() {
        if (!isRemoveSupported() || !isAddSupported()) {
            return;
        }
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

    public void testKeySetSize() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertEquals(3, map.keySet().size());
    }

    @SuppressWarnings("unchecked")
    public void testSize_Key() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertEquals(2, map.get((K) "one").size());
        assertEquals(2, map.get((K) "two").size());
        assertEquals(2, map.get((K) "three").size());
        if (!isAddSupported()) {
            return;
        }
        map.put((K) "A", (V) "AA");
        assertEquals(1, map.get((K) "A").size());
        //assertEquals(0, map.get("B").size());
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
        //assertEquals(0, map.get("A").size());
        assertEquals(3, map.get((K) "B").size());
        map.removeMapping("B", "BC");
        //assertEquals(0, map.get("A").size());
        assertEquals(2, map.get((K) "B").size());
    }

//    @SuppressWarnings("unchecked")
//    public void testIterator_Key() {
//        final MultiValuedMap<K, V> map = makeFullMap();
//        Iterator<V> it = map.iterator("one");
//        assertEquals(true, it.hasNext());
//        Set<V> values = new HashSet<V>();
//        while (it.hasNext()) {
//            values.add(it.next());
//        }
//        assertEquals(true, values.contains("un"));
//        assertEquals(true, values.contains("uno"));
//        assertEquals(false, map.iterator("A").hasNext());
//        assertEquals(false, map.iterator("A").hasNext());
//        if (!isAddSupported()) {
//            return;
//        }
//        map.put((K) "A", (V) "AA");
//        it = map.iterator("A");
//        assertEquals(true, it.hasNext());
//        it.next();
//        assertEquals(false, it.hasNext());
//    }

    @SuppressWarnings("unchecked")
    public void testContainsValue_Key() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertEquals(true, map.containsMapping("one", "uno"));
        assertEquals(false, map.containsMapping("two", "2"));
        if (!isAddSupported()) {
            return;
        }
        map.put((K) "A", (V) "AA");
        assertEquals(true, map.containsMapping("A", "AA"));
        assertEquals(false, map.containsMapping("A", "AB"));
    }

    @SuppressWarnings("unchecked")
    public void testPutAll_Map1() {
        if (!isAddSupported()) {
            return;
        }
        final MultiValuedMap<K, V> original = makeObject();
        original.put((K) "key", (V) "object1");
        original.put((K) "key", (V) "object2");

        final MultiValuedMap<K, V> test = makeObject();
        test.put((K) "keyA", (V) "objectA");
        test.put((K) "key", (V) "object0");
        test.putAll(original);

        assertEquals(2, test.keySet().size());
        assertEquals(4, test.size());
        assertEquals(1, test.get((K) "keyA").size());
        assertEquals(3, test.get((K) "key").size());
        assertEquals(true, test.containsValue("objectA"));
        assertEquals(true, test.containsValue("object0"));
        assertEquals(true, test.containsValue("object1"));
        assertEquals(true, test.containsValue("object2"));
    }

    @SuppressWarnings("unchecked")
    public void testPutAll_Map2() {
        if (!isAddSupported()) {
            return;
        }
        final Map<K, V> original = new HashMap<K, V>();
        original.put((K) "keyX", (V) "object1");
        original.put((K) "keyY", (V) "object2");

        final MultiValuedMap<K, V> test = makeObject();
        test.put((K) "keyA", (V) "objectA");
        test.put((K) "keyX", (V) "object0");
        test.putAll(original);

        assertEquals(3, test.keySet().size());
        assertEquals(4, test.size());
        assertEquals(1, test.get((K) "keyA").size());
        assertEquals(2, test.get((K) "keyX").size());
        assertEquals(1, test.get((K) "keyY").size());
        assertEquals(true, test.containsValue("objectA"));
        assertEquals(true, test.containsValue("object0"));
        assertEquals(true, test.containsValue("object1"));
        assertEquals(true, test.containsValue("object2"));
    }

    @SuppressWarnings("unchecked")
    public void testPutAll_KeyIterable() {
        if (!isAddSupported()) {
            return;
        }
        final MultiValuedMap<K, V> map = makeObject();
        Collection<V> coll = (Collection<V>) Arrays.asList("X", "Y", "Z");

        assertEquals(true, map.putAll((K) "A", coll));
        assertEquals(3, map.get((K) "A").size());
        assertEquals(true, map.containsMapping("A", "X"));
        assertEquals(true, map.containsMapping("A", "Y"));
        assertEquals(true, map.containsMapping("A", "Z"));

        try {
            map.putAll((K) "A", null);
            fail("expecting NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }

        assertEquals(3, map.get((K) "A").size());
        assertEquals(true, map.containsMapping("A", "X"));
        assertEquals(true, map.containsMapping("A", "Y"));
        assertEquals(true, map.containsMapping("A", "Z"));

        assertEquals(false, map.putAll((K) "A", new ArrayList<V>()));
        assertEquals(3, map.get((K) "A").size());
        assertEquals(true, map.containsMapping("A", "X"));
        assertEquals(true, map.containsMapping("A", "Y"));
        assertEquals(true, map.containsMapping("A", "Z"));

        coll = (Collection<V>) Arrays.asList("M");
        assertEquals(true, map.putAll((K) "A", coll));
        assertEquals(4, map.get((K) "A").size());
        assertEquals(true, map.containsMapping("A", "X"));
        assertEquals(true, map.containsMapping("A", "Y"));
        assertEquals(true, map.containsMapping("A", "Z"));
        assertEquals(true, map.containsMapping("A", "M"));
    }

    @SuppressWarnings("unchecked")
    public void testRemove_KeyItem() {
        if (!isRemoveSupported() || !isAddSupported()) {
            return;
        }
        final MultiValuedMap<K, V> map = makeObject();
        map.put((K) "A", (V) "AA");
        map.put((K) "A", (V) "AB");
        map.put((K) "A", (V) "AC");
        assertEquals(false, map.removeMapping("C", "CA"));
        assertEquals(false, map.removeMapping("A", "AD"));
        assertEquals(true, map.removeMapping("A", "AC"));
        assertEquals(true, map.removeMapping("A", "AB"));
        assertEquals(true, map.removeMapping("A", "AA"));
        //assertEquals(new MultiValuedHashMap<K, V>(), map);
    }

    public void testKeysMultiSet() {
        MultiValuedMap<K, V> map = makeFullMap();
        MultiSet<K> keyMultiSet = map.keys();
        assertEquals(2, keyMultiSet.getCount("one"));
        assertEquals(2, keyMultiSet.getCount("two"));
        assertEquals(2, keyMultiSet.getCount("three"));
        assertEquals(6, keyMultiSet.size());
    }

    public void testKeysBagIterator() {
        MultiValuedMap<K, V> map = makeFullMap();
        Collection<K> col = new ArrayList<K>();
        Iterator<K> it = map.keys().iterator();
        while (it.hasNext()) {
            col.add(it.next());
        }
        Bag<K> bag = new HashBag<K>(col);
        assertEquals(2, bag.getCount("one"));
        assertEquals(2, bag.getCount("two"));
        assertEquals(2, bag.getCount("three"));
        assertEquals(6, bag.size());
    }

    @SuppressWarnings("unchecked")
    public void testKeysBagContainsAll() {
        MultiValuedMap<K, V> map = makeFullMap();
        MultiSet<K> keyMultiSet = map.keys();
        Collection<K> col = (Collection<K>) Arrays.asList("one", "two", "three", "one", "two", "three");
        assertTrue(keyMultiSet.containsAll(col));
    }

    public void testAsMapGet() {
        resetEmpty();
        Map<K, Collection<V>> mapCol = getMap().asMap();
        assertNull(mapCol.get("one"));
        assertEquals(0, mapCol.size());

        resetFull();
        mapCol = getMap().asMap();
        Collection<V> col = mapCol.get("one");
        assertNotNull(col);
        assertTrue(col.contains("un"));
        assertTrue(col.contains("uno"));
    }

    @SuppressWarnings("unchecked")
    public void testAsMapPut() {
        if (!isAddSupported()) {
            return;
        }
        resetEmpty();
        Map<K, Collection<V>> mapCol = getMap().asMap();
        Collection<V> col = (Collection<V>) Arrays.asList("un", "uno");
        mapCol.put((K) "one", col);
        assertEquals(2, getMap().size());
        assertTrue(getMap().containsKey("one"));
        assertTrue(getMap().containsValue("un"));
        assertTrue(getMap().containsValue("uno"));

        resetFull();
        mapCol = getMap().asMap();
        col = mapCol.get("one");
        col.add((V) "one");
        assertEquals(7, getMap().size());
        assertTrue(getMap().containsValue("one"));
        assertTrue(getMap().containsValue("un"));
        assertTrue(getMap().containsValue("uno"));
    }

    public void testAsMapRemove() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        Map<K, Collection<V>> mapCol = getMap().asMap();
        mapCol.remove("one");
        assertFalse(getMap().containsKey("one"));
        assertEquals(4, getMap().size());
    }

    public void testMapIterator() {
        resetEmpty();
        MapIterator<K, V> mapIt  = getMap().mapIterator();
        assertFalse(mapIt.hasNext());

        resetFull();
        mapIt = getMap().mapIterator();
        while (mapIt.hasNext()) {
            K key = mapIt.next();
            V value = mapIt.getValue();
            assertTrue(getMap().containsMapping(key, value));
        }
    }

    public void testMapIteratorRemove() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        MapIterator<K, V> mapIt = getMap().mapIterator();
        while (mapIt.hasNext()) {
            mapIt.next();
            mapIt.remove();
        }
        assertTrue(getMap().isEmpty());
    }

    @SuppressWarnings("unchecked")
    public void testMapIteratorUnsupportedSet() {
        resetFull();
        MapIterator<K, V> mapIt = getMap().mapIterator();
        mapIt.next();
        try {
            mapIt.setValue((V) "some value");
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    // -----------------------------------------------------------------------
    // Manual serialization testing as this class cannot easily
    // extend the AbstractTestMap
    // -----------------------------------------------------------------------

    public void testEmptyMapCompatibility() throws Exception {
        final MultiValuedMap<?, ?> map = makeObject();
        final MultiValuedMap<?, ?> map2 =
                (MultiValuedMap<?, ?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(map));
        assertEquals("Map is empty", 0, map2.size());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testFullMapCompatibility() throws Exception {
        final MultiValuedMap map = makeFullMap();
        final MultiValuedMap map2 =
                (MultiValuedMap) readExternalFormFromDisk(getCanonicalFullCollectionName(map));
        assertEquals("Map is the right size", map.size(), map2.size());
        for (final Object key : map.keySet()) {
            assertTrue("Map had inequal elements",
                       CollectionUtils.isEqualCollection(map.get(key), map2.get(key)));
            if (isRemoveSupported()) {
                map2.remove(key);
            }
        }
        if (isRemoveSupported()) {
            assertEquals("Map had extra values", 0, map2.size());
        }
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

    public class TestMultiValuedMapEntries extends AbstractCollectionTest<Entry<K, V>> {
        public TestMultiValuedMapEntries() {
            super("");
        }

        @SuppressWarnings("unchecked")
        @Override
        public Entry<K, V>[] getFullElements() {
            return makeFullMap().entries().toArray(new Entry[0]);
        }

        @Override
        public Collection<Entry<K, V>> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().entries();
        }

        @Override
        public Collection<Entry<K, V>> makeFullCollection() {
            return AbstractMultiValuedMapTest.this.makeFullMap().entries();
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isAddSupported() {
            // Add not supported in entries view
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
        public void resetFull() {
            AbstractMultiValuedMapTest.this.resetFull();
            setCollection(AbstractMultiValuedMapTest.this.getMap().entries());
            TestMultiValuedMapEntries.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().entries());
        }

        @Override
        public void resetEmpty() {
            AbstractMultiValuedMapTest.this.resetEmpty();
            setCollection(AbstractMultiValuedMapTest.this.getMap().entries());
            TestMultiValuedMapEntries.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().entries());
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

    public class TestMultiValuedMapKeySet extends AbstractSetTest<K> {
        public TestMultiValuedMapKeySet() {
            super("");
        }

        @SuppressWarnings("unchecked")
        @Override
        public K[] getFullElements() {
            return (K[]) AbstractMultiValuedMapTest.this.makeFullMap().keySet().toArray();
        }

        @Override
        public Set<K> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().keySet();
        }

        @Override
        public Set<K> makeFullCollection() {
            return AbstractMultiValuedMapTest.this.makeFullMap().keySet();
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isAddSupported() {
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

    public class TestMultiValuedMapValues extends AbstractCollectionTest<V> {
        public TestMultiValuedMapValues() {
            super("");
        }

        @Override
        public V[] getFullElements() {
            return getSampleValues();
        }

        @Override
        public Collection<V> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().values();
        }

        @Override
        public Collection<V> makeFullCollection() {
            return AbstractMultiValuedMapTest.this.makeFullMap().values();
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isAddSupported() {
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
        public void resetFull() {
            AbstractMultiValuedMapTest.this.resetFull();
            setCollection(AbstractMultiValuedMapTest.this.getMap().values());
            TestMultiValuedMapValues.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().values());
        }

        @Override
        public void resetEmpty() {
            AbstractMultiValuedMapTest.this.resetEmpty();
            setCollection(AbstractMultiValuedMapTest.this.getMap().values());
            TestMultiValuedMapValues.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().values());
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

    public class TestMultiValuedMapKeys extends AbstractMultiSetTest<K> {

        public TestMultiValuedMapKeys() {
            super("");
        }

        @Override
        public K[] getFullElements() {
            return getSampleKeys();
        }

        @Override
        public MultiSet<K> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().keys();
        }

        @Override
        public MultiSet<K> makeFullCollection() {
            return AbstractMultiValuedMapTest.this.makeFullMap().keys();
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
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
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public void resetFull() {
            AbstractMultiValuedMapTest.this.resetFull();
            setCollection(AbstractMultiValuedMapTest.this.getMap().keys());
            TestMultiValuedMapKeys.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().keys());
        }

        @Override
        public void resetEmpty() {
            AbstractMultiValuedMapTest.this.resetEmpty();
            setCollection(AbstractMultiValuedMapTest.this.getMap().keys());
            TestMultiValuedMapKeys.this.setConfirmed(AbstractMultiValuedMapTest.this.getConfirmed().keys());
        }

    }

    public BulkTest bulkTestAsMap() {
        return new TestMultiValuedMapAsMap();
    }

    public class TestMultiValuedMapAsMap extends AbstractMapTest<K, Collection<V>> {

        public TestMultiValuedMapAsMap() {
            super("");
        }

        @Override
        public Map<K, Collection<V>> makeObject() {
            return AbstractMultiValuedMapTest.this.makeObject().asMap();
        }

        @Override
        public Map<K, Collection<V>> makeFullMap() {
            return AbstractMultiValuedMapTest.this.makeFullMap().asMap();
        }

        @Override
        @SuppressWarnings("unchecked")
        public K[] getSampleKeys() {
            K[] samplekeys = AbstractMultiValuedMapTest.this.getSampleKeys();
            Object[] finalKeys = new Object[3];
            for (int i = 0; i < 3; i++) {
                finalKeys[i] = samplekeys[i * 2];
            }
            return (K[]) finalKeys;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<V>[] getSampleValues() {
            V[] sampleValues = AbstractMultiValuedMapTest.this.getSampleValues();
            Collection<V>[] colArr = new Collection[3];
            for(int i = 0; i < 3; i++) {
                colArr[i] = Arrays.asList(sampleValues[i*2], sampleValues[i*2 + 1]);
            }
            return colArr;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<V>[] getNewSampleValues() {
            Object[] sampleValues = { "ein", "ek", "zwei", "duey", "drei", "teen" };
            Collection<V>[] colArr = new Collection[3];
            for (int i = 0; i < 3; i++) {
                colArr[i] = Arrays.asList((V) sampleValues[i * 2], (V) sampleValues[i * 2 + 1]);
            }
            return colArr;
        }

        @Override
        public boolean isAllowNullKey() {
            return AbstractMultiValuedMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isPutAddSupported() {
            return AbstractMultiValuedMapTest.this.isAddSupported();
        }

        @Override
        public boolean isPutChangeSupported() {
            return AbstractMultiValuedMapTest.this.isAddSupported();
        }

        @Override
        public boolean isRemoveSupported() {
            return AbstractMultiValuedMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

    }
}
