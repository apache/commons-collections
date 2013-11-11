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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MultiMap;

/**
 * TestMultiValueMap.
 *
 * @since 3.2
 * @version $Id$
 */
public class MultiValueMapTest<K, V> extends AbstractObjectTest {

    public MultiValueMapTest(final String testName) {
        super(testName);
    }

    public void testNoMappingReturnsNull() {
        final MultiValueMap<K, V> map = createTestMap();
        assertNull(map.get("whatever"));
    }

    @SuppressWarnings("unchecked")
    public void testValueCollectionType() {
        final MultiValueMap<K, V> map = createTestMap(LinkedList.class);
        assertTrue(map.get("one") instanceof LinkedList);
    }

    @SuppressWarnings("unchecked")
    public void testMultipleValues() {
        final MultiValueMap<K, V> map = createTestMap(HashSet.class);
        final HashSet<V> expected = new HashSet<V>();
        expected.add((V) "uno");
        expected.add((V) "un");
        assertEquals(expected, map.get("one"));
    }

    @SuppressWarnings("unchecked")
    public void testContainsValue() {
        final MultiValueMap<K, V> map = createTestMap(HashSet.class);
        assertTrue(map.containsValue("uno"));
        assertTrue(map.containsValue("un"));
        assertTrue(map.containsValue("dos"));
        assertTrue(map.containsValue("deux"));
        assertTrue(map.containsValue("tres"));
        assertTrue(map.containsValue("trois"));
        assertFalse(map.containsValue("quatro"));
    }

    @SuppressWarnings("unchecked")
    public void testKeyContainsValue() {
        final MultiValueMap<K, V> map = createTestMap(HashSet.class);
        assertTrue(map.containsValue("one", "uno"));
        assertTrue(map.containsValue("one", "un"));
        assertTrue(map.containsValue("two", "dos"));
        assertTrue(map.containsValue("two", "deux"));
        assertTrue(map.containsValue("three", "tres"));
        assertTrue(map.containsValue("three", "trois"));
        assertFalse(map.containsValue("four", "quatro"));
    }

    @SuppressWarnings("unchecked")
    public void testValues() {
        final MultiValueMap<K, V> map = createTestMap(HashSet.class);
        final HashSet<V> expected = new HashSet<V>();
        expected.add((V) "uno");
        expected.add((V) "dos");
        expected.add((V) "tres");
        expected.add((V) "un");
        expected.add((V) "deux");
        expected.add((V) "trois");
        final Collection<Object> c = map.values();
        assertEquals(6, c.size());
        assertEquals(expected, new HashSet<Object>(c));
    }

    @SuppressWarnings("unchecked")
    private MultiValueMap<K, V> createTestMap() {
        return createTestMap(ArrayList.class);
    }

    @SuppressWarnings("unchecked")
    private <C extends Collection<V>> MultiValueMap<K, V> createTestMap(final Class<C> collectionClass) {
        final MultiValueMap<K, V> map = MultiValueMap.multiValueMap(new HashMap<K, C>(), collectionClass);
        map.put((K) "one", (V) "uno");
        map.put((K) "one", (V) "un");
        map.put((K) "two", (V) "dos");
        map.put((K) "two", (V) "deux");
        map.put((K) "three", (V) "tres");
        map.put((K) "three", (V) "trois");
        return map;
    }

    public void testKeyedIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        final ArrayList<Object> actual = new ArrayList<Object>(IteratorUtils.toList(map.iterator("one")));
        final ArrayList<Object> expected = new ArrayList<Object>(Arrays.asList(new String[]{ "uno", "un" }));
        assertEquals(expected, actual);
    }

    public void testRemoveAllViaIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        for (final Iterator<?> i = map.values().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertTrue(map.isEmpty());
    }

    public void testRemoveAllViaKeyedIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        for (final Iterator<?> i = map.iterator("one"); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertEquals(4, map.totalSize());
    }

    public void testIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        @SuppressWarnings("unchecked")
        Collection<V> values = new ArrayList<V>((Collection<V>) map.values());
        Iterator<Map.Entry<K, V>> iterator = map.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
            assertTrue(map.containsValue(entry.getKey(), entry.getValue()));
            assertTrue(values.contains(entry.getValue()));
            assertTrue(values.remove(entry.getValue()));
        }
        assertTrue(values.isEmpty());
    }

    public void testRemoveAllViaEntryIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        for (final Iterator<?> i = map.iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertEquals(0, map.totalSize());
    }

    public void testTotalSizeA() {
        assertEquals(6, createTestMap().totalSize());
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testMapEquals() {
        final MultiValueMap<K, V> one = new MultiValueMap<K, V>();
        final Integer value = Integer.valueOf(1);
        one.put((K) "One", value);
        one.remove("One", value);

        final MultiValueMap<K, V> two = new MultiValueMap<K, V>();
        assertEquals(two, one);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testGetCollection() {
        final MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        map.put((K) "A", "AA");
        assertSame(map.get("A"), map.getCollection("A"));
    }

    @SuppressWarnings("unchecked")
    public void testTotalSize() {
        final MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        assertEquals(0, map.totalSize());
        map.put((K) "A", "AA");
        assertEquals(1, map.totalSize());
        map.put((K) "B", "BA");
        assertEquals(2, map.totalSize());
        map.put((K) "B", "BB");
        assertEquals(3, map.totalSize());
        map.put((K) "B", "BC");
        assertEquals(4, map.totalSize());
        map.remove("A");
        assertEquals(3, map.totalSize());
        map.remove("B", "BC");
        assertEquals(2, map.totalSize());
    }

    @SuppressWarnings("unchecked")
    public void testSize() {
        final MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        assertEquals(0, map.size());
        map.put((K) "A", "AA");
        assertEquals(1, map.size());
        map.put((K) "B", "BA");
        assertEquals(2, map.size());
        map.put((K) "B", "BB");
        assertEquals(2, map.size());
        map.put((K) "B", "BC");
        assertEquals(2, map.size());
        map.remove("A");
        assertEquals(1, map.size());
        map.remove("B", "BC");
        assertEquals(1, map.size());
    }

    @SuppressWarnings("unchecked")
    public void testSize_Key() {
        final MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        assertEquals(0, map.size("A"));
        assertEquals(0, map.size("B"));
        map.put((K) "A", "AA");
        assertEquals(1, map.size("A"));
        assertEquals(0, map.size("B"));
        map.put((K) "B", "BA");
        assertEquals(1, map.size("A"));
        assertEquals(1, map.size("B"));
        map.put((K) "B", "BB");
        assertEquals(1, map.size("A"));
        assertEquals(2, map.size("B"));
        map.put((K) "B", "BC");
        assertEquals(1, map.size("A"));
        assertEquals(3, map.size("B"));
        map.remove("A");
        assertEquals(0, map.size("A"));
        assertEquals(3, map.size("B"));
        map.remove("B", "BC");
        assertEquals(0, map.size("A"));
        assertEquals(2, map.size("B"));
    }

    @SuppressWarnings("unchecked")
    public void testIterator_Key() {
        final MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        assertEquals(false, map.iterator("A").hasNext());
        map.put((K) "A", "AA");
        final Iterator<?> it = map.iterator("A");
        assertEquals(true, it.hasNext());
        it.next();
        assertEquals(false, it.hasNext());
    }

    @SuppressWarnings("unchecked")
    public void testContainsValue_Key() {
        final MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        assertEquals(false, map.containsValue("A", "AA"));
        assertEquals(false, map.containsValue("B", "BB"));
        map.put((K) "A", "AA");
        assertEquals(true, map.containsValue("A", "AA"));
        assertEquals(false, map.containsValue("A", "AB"));
    }

    @SuppressWarnings("unchecked")
    public void testPutWithList() {
        final MultiValueMap<K, V> test = MultiValueMap.multiValueMap(new HashMap<K, Collection>(), ArrayList.class);
        assertEquals("a", test.put((K) "A", "a"));
        assertEquals("b", test.put((K) "A", "b"));
        assertEquals(1, test.size());
        assertEquals(2, test.size("A"));
        assertEquals(2, test.totalSize());
    }

    @SuppressWarnings("unchecked")
    public void testPutWithSet() {
        final MultiValueMap<K, V> test = MultiValueMap.multiValueMap(new HashMap<K, HashSet>(), HashSet.class);
        assertEquals("a", test.put((K) "A", "a"));
        assertEquals("b", test.put((K) "A", "b"));
        assertEquals(null, test.put((K) "A", "a"));
        assertEquals(1, test.size());
        assertEquals(2, test.size("A"));
        assertEquals(2, test.totalSize());
    }

    @SuppressWarnings("unchecked")
    public void testPutAll_Map1() {
        final MultiMap<K, V> original = new MultiValueMap<K, V>();
        original.put((K) "key", "object1");
        original.put((K) "key", "object2");

        final MultiValueMap<K, V> test = new MultiValueMap<K, V>();
        test.put((K) "keyA", "objectA");
        test.put((K) "key", "object0");
        test.putAll(original);

        assertEquals(2, test.size());
        assertEquals(4, test.totalSize());
        assertEquals(1, test.getCollection("keyA").size());
        assertEquals(3, test.getCollection("key").size());
        assertEquals(true, test.containsValue("objectA"));
        assertEquals(true, test.containsValue("object0"));
        assertEquals(true, test.containsValue("object1"));
        assertEquals(true, test.containsValue("object2"));
    }

    @SuppressWarnings("unchecked")
    public void testPutAll_Map2() {
        final Map<K, V> original = new HashMap<K, V>();
        original.put((K) "keyX", (V) "object1");
        original.put((K) "keyY", (V) "object2");

        final MultiValueMap<K, V> test = new MultiValueMap<K, V>();
        test.put((K) "keyA", "objectA");
        test.put((K) "keyX", "object0");
        test.putAll(original);

        assertEquals(3, test.size());
        assertEquals(4, test.totalSize());
        assertEquals(1, test.getCollection("keyA").size());
        assertEquals(2, test.getCollection("keyX").size());
        assertEquals(1, test.getCollection("keyY").size());
        assertEquals(true, test.containsValue("objectA"));
        assertEquals(true, test.containsValue("object0"));
        assertEquals(true, test.containsValue("object1"));
        assertEquals(true, test.containsValue("object2"));
    }

    @SuppressWarnings("unchecked")
    public void testPutAll_KeyCollection() {
        final MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        Collection<V> coll = (Collection<V>) Arrays.asList(new Object[] { "X", "Y", "Z" });

        assertEquals(true, map.putAll((K) "A", coll));
        assertEquals(3, map.size("A"));
        assertEquals(true, map.containsValue("A", "X"));
        assertEquals(true, map.containsValue("A", "Y"));
        assertEquals(true, map.containsValue("A", "Z"));

        assertEquals(false, map.putAll((K) "A", null));
        assertEquals(3, map.size("A"));
        assertEquals(true, map.containsValue("A", "X"));
        assertEquals(true, map.containsValue("A", "Y"));
        assertEquals(true, map.containsValue("A", "Z"));

        assertEquals(false, map.putAll((K) "A", new ArrayList<V>()));
        assertEquals(3, map.size("A"));
        assertEquals(true, map.containsValue("A", "X"));
        assertEquals(true, map.containsValue("A", "Y"));
        assertEquals(true, map.containsValue("A", "Z"));

        coll = (Collection<V>) Arrays.asList(new Object[] { "M" });
        assertEquals(true, map.putAll((K) "A", coll));
        assertEquals(4, map.size("A"));
        assertEquals(true, map.containsValue("A", "X"));
        assertEquals(true, map.containsValue("A", "Y"));
        assertEquals(true, map.containsValue("A", "Z"));
        assertEquals(true, map.containsValue("A", "M"));
    }

    @SuppressWarnings("unchecked")
    public void testRemove_KeyItem() {
        final MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        map.put((K) "A", "AA");
        map.put((K) "A", "AB");
        map.put((K) "A", "AC");
        assertEquals(null, map.remove("C", "CA"));
        assertEquals(null, map.remove("A", "AD"));
        assertEquals("AC", map.remove("A", "AC"));
        assertEquals("AB", map.remove("A", "AB"));
        assertEquals("AA", map.remove("A", "AA"));
        assertEquals(new MultiValueMap<K, V>(), map);
    }

    //-----------------------------------------------------------------------
    // Manual serialization testing as this class cannot easily 
    // extend the AbstractTestMap
    //-----------------------------------------------------------------------

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public Object makeObject() {
        final Map m = makeEmptyMap();
        m.put("a", "1");
        m.put("a", "1b");
        m.put("b", "2");
        m.put("c", "3");
        m.put("c", "3b");
        m.put("d", "4");
        return m;
    }

    private Map makeEmptyMap() {
        return new MultiValueMap();
    }

    public void testEmptyMapCompatibility() throws Exception {
        final Map<?,?> map = makeEmptyMap();
        final Map<?,?> map2 = (Map<?,?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(map));
        assertEquals("Map is empty", 0, map2.size());
    }
    public void testFullMapCompatibility() throws Exception {
        final Map<?,?> map = (Map<?,?>) makeObject();
        final Map<?,?> map2 = (Map<?,?>) readExternalFormFromDisk(getCanonicalFullCollectionName(map));
        assertEquals("Map is the right size", map.size(), map2.size());
        for (final Object key : map.keySet()) {
            assertEquals( "Map had inequal elements", map.get(key), map2.get(key) );
            map2.remove(key);
        }
        assertEquals("Map had extra values", 0, map2.size());
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk(
//            (java.io.Serializable) makeEmptyMap(),
//            "src/test/resources/data/test/MultiValueMap.emptyCollection.version4.obj");
//
//        writeExternalFormToDisk(
//            (java.io.Serializable) makeObject(),
//            "src/test/resources/data/test/MultiValueMap.fullCollection.version4.obj");
//    }

}
