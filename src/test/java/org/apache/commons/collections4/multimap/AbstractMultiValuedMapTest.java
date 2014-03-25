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

import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.bag.HashBag;

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

    protected MultiValuedMap<K, V> makeFullMap() {
        final MultiValuedMap<K, V> map = makeObject();
        addSampleMappings(map);
        return map;
    }

    @SuppressWarnings("unchecked")
    protected void addSampleMappings(MultiValuedMap<? super K, ? super V> map) {
        map.put((K) "one", (V) "uno");
        map.put((K) "one", (V) "un");
        map.put((K) "two", (V) "dos");
        map.put((K) "two", (V) "deux");
        map.put((K) "three", (V) "tres");
        map.put((K) "three", (V) "trois");
    }

    public void testNoMappingReturnsNull() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertNull(map.get("whatever"));
    }

    public void testMultipleValues() {
        final MultiValuedMap<K, V> map = makeFullMap();
        Collection<V> col = map.get("one");
        assertTrue(col.contains("uno"));
        assertTrue(col.contains("un"));
    }

    public void testGet() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertTrue(map.get("one").contains("uno"));
        assertTrue(map.get("one").contains("un"));
        assertTrue(map.get("two").contains("dos"));
        assertTrue(map.get("two").contains("deux"));
        assertTrue(map.get("three").contains("tres"));
        assertTrue(map.get("three").contains("trois"));
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

    public void testRemoveAllViaIterator() {
        if (!isRemoveSupported()) {
            return;
        }
        final MultiValuedMap<K, V> map = makeFullMap();
        for (final Iterator<?> i = map.values().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertTrue(map.isEmpty());
    }

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

    public void testRemoveAllViaEntriesIterator() {
        if (!isRemoveSupported()) {
            return;
        }
        final MultiValuedMap<K, V> map = makeFullMap();
        for (final Iterator<?> i = map.entries().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
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
        one.removeMapping((K) "One", (V) value);

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
        map.removeMapping((K) "B", (V) "BC");
        assertEquals(2, map.size());
    }

    public void testKeySetSize() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertEquals(3, map.keySet().size());
    }

    @SuppressWarnings("unchecked")
    public void testSize_Key() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertEquals(2, map.get("one").size());
        assertEquals(2, map.get("two").size());
        assertEquals(2, map.get("three").size());
        if (!isAddSupported()) {
            return;
        }
        map.put((K) "A", (V) "AA");
        assertEquals(1, map.get("A").size());
        //assertEquals(0, map.get("B").size());
        map.put((K) "B", (V) "BA");
        assertEquals(1, map.get("A").size());
        assertEquals(1, map.get("B").size());
        map.put((K) "B", (V) "BB");
        assertEquals(1, map.get("A").size());
        assertEquals(2, map.get("B").size());
        map.put((K) "B", (V) "BC");
        assertEquals(1, map.get("A").size());
        assertEquals(3, map.get("B").size());
        if (!isRemoveSupported()) {
            return;
        }
        map.remove("A");
        //assertEquals(0, map.get("A").size());
        assertEquals(3, map.get("B").size());
        map.removeMapping((K) "B", (V) "BC");
        //assertEquals(0, map.get("A").size());
        assertEquals(2, map.get("B").size());
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
        assertEquals(true, map.containsMapping("one", (V) "uno"));
        assertEquals(false, map.containsMapping("two", (V) "2"));
        if (!isAddSupported()) {
            return;
        }
        map.put((K) "A", (V) "AA");
        assertEquals(true, map.containsMapping("A", (V) "AA"));
        assertEquals(false, map.containsMapping("A", (V) "AB"));
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
        assertEquals(1, test.get("keyA").size());
        assertEquals(3, test.get("key").size());
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
        assertEquals(1, test.get("keyA").size());
        assertEquals(2, test.get("keyX").size());
        assertEquals(1, test.get("keyY").size());
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
        assertEquals(3, map.get("A").size());
        assertEquals(true, map.containsMapping("A", (V) "X"));
        assertEquals(true, map.containsMapping("A", (V) "Y"));
        assertEquals(true, map.containsMapping("A", (V) "Z"));

        assertEquals(false, map.putAll((K) "A", null));
        assertEquals(3, map.get("A").size());
        assertEquals(true, map.containsMapping("A", (V) "X"));
        assertEquals(true, map.containsMapping("A", (V) "Y"));
        assertEquals(true, map.containsMapping("A", (V) "Z"));

        assertEquals(false, map.putAll((K) "A", new ArrayList<V>()));
        assertEquals(3, map.get("A").size());
        assertEquals(true, map.containsMapping("A", (V) "X"));
        assertEquals(true, map.containsMapping("A", (V) "Y"));
        assertEquals(true, map.containsMapping("A", (V) "Z"));

        coll = (Collection<V>) Arrays.asList("M");
        assertEquals(true, map.putAll((K) "A", coll));
        assertEquals(4, map.get("A").size());
        assertEquals(true, map.containsMapping("A", (V) "X"));
        assertEquals(true, map.containsMapping("A", (V) "Y"));
        assertEquals(true, map.containsMapping("A", (V) "Z"));
        assertEquals(true, map.containsMapping("A", (V) "M"));
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
        assertEquals(false, map.removeMapping((K) "C", (V) "CA"));
        assertEquals(false, map.removeMapping((K) "A", (V) "AD"));
        assertEquals(true, map.removeMapping((K) "A", (V) "AC"));
        assertEquals(true, map.removeMapping((K) "A", (V) "AB"));
        assertEquals(true, map.removeMapping((K) "A", (V) "AA"));
        //assertEquals(new MultiValuedHashMap<K, V>(), map);
    }

    public void testKeysBag() {
        MultiValuedMap<K, V> map = makeFullMap();
        Bag<K> keyBag = map.keys();
        assertEquals(2, keyBag.getCount("one"));
        assertEquals(2, keyBag.getCount("two"));
        assertEquals(2, keyBag.getCount("three"));
        assertEquals(6, keyBag.size());
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
        Bag<K> keyBag = map.keys();
        Collection<K> col = (Collection<K>) Arrays.asList("one", "two", "three", "one", "two", "three");
        assertTrue(keyBag.containsAll(col));
    }

//    public void testMapEqulas() {
//        MultiValuedMap<K, V> map1 = makeFullMap();
//        MultiValuedMap<K, V> map2 = makeFullMap();
//        assertEquals(true, map1.equals(map2));
//    }

    // -----------------------------------------------------------------------
    // Manual serialization testing as this class cannot easily
    // extend the AbstractTestMap
    // -----------------------------------------------------------------------

    public void testEmptyMapCompatibility() throws Exception {
        final MultiValuedMap<?, ?> map = makeObject();
        final MultiValuedMap<?, ?> map2 = (MultiValuedMap<?, ?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(map));
        assertEquals("Map is empty", 0, map2.size());
    }

    public void testFullMapCompatibility() throws Exception {
        final MultiValuedMap<?, ?> map = (MultiValuedMap<?, ?>) makeFullMap();
        final MultiValuedMap<?, ?> map2 = (MultiValuedMap<?, ?>) readExternalFormFromDisk(getCanonicalFullCollectionName(map));
        assertEquals("Map is the right size", map.size(), map2.size());
        for (final Object key : map.keySet()) {
            assertEquals("Map had inequal elements", map.get(key), map2.get(key));
            if (isRemoveSupported()) {
                map2.remove(key);
            }
        }
        if (isRemoveSupported()) {
            assertEquals("Map had extra values", 0, map2.size());
        }
    }

}
