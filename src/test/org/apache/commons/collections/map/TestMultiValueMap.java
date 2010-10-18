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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.MultiMap;

import org.apache.commons.collections.AbstractTestObject;

/**
 * TestMultiValueMap.
 *
 * @author <a href="mailto:jcarman@apache.org">James Carman</a>
 * @author Stephen Colebourne
 * @since Commons Collections 3.2
 */
public class TestMultiValueMap<K, V> extends AbstractTestObject {

    public TestMultiValueMap(String testName) {
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
        assertTrue(map.containsValue((K) "one", "uno"));
        assertTrue(map.containsValue((K) "one", "un"));
        assertTrue(map.containsValue((K) "two", "dos"));
        assertTrue(map.containsValue((K) "two", "deux"));
        assertTrue(map.containsValue((K) "three", "tres"));
        assertTrue(map.containsValue((K) "three", "trois"));
        assertFalse(map.containsValue((K) "four", "quatro"));
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
    private <C extends Collection<V>> MultiValueMap<K, V> createTestMap(Class<C> collectionClass) {
        final MultiValueMap<K, V> map = MultiValueMap.decorate(new HashMap<K, C>(), collectionClass);
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
        for (Iterator<?> i = map.values().iterator(); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertTrue(map.isEmpty());
    }

    public void testRemoveAllViaKeyedIterator() {
        final MultiValueMap<K, V> map = createTestMap();
        for (Iterator<?> i = map.iterator("one"); i.hasNext();) {
            i.next();
            i.remove();
        }
        assertNull(map.get("one"));
        assertEquals(4, map.totalSize());
    }

    public void testTotalSizeA() {
        assertEquals(6, createTestMap().totalSize());
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testMapEquals() {
        MultiValueMap<K, V> one = new MultiValueMap<K, V>();
        Integer value = new Integer(1);
        one.put((K) "One", value);
        one.remove("One", value);

        MultiValueMap<K, V> two = new MultiValueMap<K, V>();
        assertEquals(two, one);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testGetCollection() {
        MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        map.put((K) "A", "AA");
        assertSame(map.get("A"), map.getCollection("A"));
    }

    @SuppressWarnings("unchecked")
    public void testTotalSize() {
        MultiValueMap<K, V> map = new MultiValueMap<K, V>();
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
        MultiValueMap<K, V> map = new MultiValueMap<K, V>();
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
        MultiValueMap<K, V> map = new MultiValueMap<K, V>();
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
        MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        assertEquals(false, map.iterator("A").hasNext());
        map.put((K) "A", "AA");
        Iterator<?> it = map.iterator("A");
        assertEquals(true, it.hasNext());
        it.next();
        assertEquals(false, it.hasNext());
    }

    @SuppressWarnings("unchecked")
    public void testContainsValue_Key() {
        MultiValueMap<K, V> map = new MultiValueMap<K, V>();
        assertEquals(false, map.containsValue("A", "AA"));
        assertEquals(false, map.containsValue("B", "BB"));
        map.put((K) "A", "AA");
        assertEquals(true, map.containsValue("A", "AA"));
        assertEquals(false, map.containsValue("A", "AB"));
    }

    @SuppressWarnings("unchecked")
    public void testPutWithList() {
        MultiValueMap<K, V> test = MultiValueMap.decorate(new HashMap<K, Collection>(), ArrayList.class);
        assertEquals("a", test.put((K) "A", "a"));
        assertEquals("b", test.put((K) "A", "b"));
        assertEquals(1, test.size());
        assertEquals(2, test.size("A"));
        assertEquals(2, test.totalSize());
    }

    @SuppressWarnings("unchecked")
    public void testPutWithSet() {
        MultiValueMap<K, V> test = MultiValueMap.decorate(new HashMap<K, HashSet>(), HashSet.class);
        assertEquals("a", test.put((K) "A", "a"));
        assertEquals("b", test.put((K) "A", "b"));
        assertEquals(null, test.put((K) "A", "a"));
        assertEquals(1, test.size());
        assertEquals(2, test.size("A"));
        assertEquals(2, test.totalSize());
    }

    @SuppressWarnings("unchecked")
    public void testPutAll_Map1() {
        MultiMap<K, V> original = new MultiValueMap<K, V>();
        original.put((K) "key", "object1");
        original.put((K) "key", "object2");

        MultiValueMap<K, V> test = new MultiValueMap<K, V>();
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
        Map<K, V> original = new HashMap<K, V>();
        original.put((K) "keyX", (V) "object1");
        original.put((K) "keyY", (V) "object2");

        MultiValueMap<K, V> test = new MultiValueMap<K, V>();
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
        MultiValueMap<K, V> map = new MultiValueMap<K, V>();
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
        MultiValueMap<K, V> map = new MultiValueMap<K, V>();
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
        return "3.3";
    }

    @Override
    public Object makeObject() {
        Map m = makeEmptyMap();
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

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk(
//            (java.io.Serializable) makeEmptyMap(),
//            "/tmp/MultiValueMap.emptyCollection.version3.3.obj");
//
//        writeExternalFormToDisk(
//            (java.io.Serializable) makeObject(),
//            "/tmp/MultiValueMap.fullCollection.version3.3.obj");
//    }

    public void testEmptyMapCompatibility() throws Exception {
        Map map = makeEmptyMap();
        Map map2 = (Map) readExternalFormFromDisk(getCanonicalEmptyCollectionName(map));
        assertEquals("Map is empty", 0, map2.size());
    }
    public void testFullMapCompatibility() throws Exception {
        Map map = (Map) makeObject();
        Map map2 = (Map) readExternalFormFromDisk(getCanonicalFullCollectionName(map));
        assertEquals("Map is the right size", map.size(), map2.size());
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            assertEquals( "Map had inequal elements", map.get(key), map2.get(key) );
            map2.remove(key);
        }
        assertEquals("Map had extra values", 0, map2.size());
    }

}
