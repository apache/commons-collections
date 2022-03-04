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
package org.apache.commons.collections4.trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.map.AbstractSortedMapTest;
import org.junit.jupiter.api.Assertions;

/**
 * JUnit tests for the PatriciaTrie.
 *
 * @since 4.0
 */
public class PatriciaTrieTest<V> extends AbstractSortedMapTest<String, V> {

    public PatriciaTrieTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(PatriciaTrieTest.class);
    }

    @Override
    public SortedMap<String, V> makeObject() {
        return new PatriciaTrie<>();
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    public void testPrefixMap() {
        final PatriciaTrie<String> trie = new PatriciaTrie<>();

        final String[] keys = {
            "",
            "Albert", "Xavier", "XyZ", "Anna", "Alien", "Alberto",
            "Alberts", "Allie", "Alliese", "Alabama", "Banane",
            "Blabla", "Amber", "Ammun", "Akka", "Akko", "Albertoo",
            "Amma"
        };

        for (final String key : keys) {
            trie.put(key, key);
        }

        SortedMap<String, String> map;
        Iterator<String> iterator;
        Iterator<Map.Entry<String, String>> entryIterator;
        Map.Entry<String, String> entry;

        map = trie.prefixMap("Al");
        Assertions.assertEquals(8, map.size());
        Assertions.assertEquals("Alabama", map.firstKey());
        Assertions.assertEquals("Alliese", map.lastKey());
        Assertions.assertEquals("Albertoo", map.get("Albertoo"));
        Assertions.assertNotNull(trie.get("Xavier"));
        Assertions.assertNull(map.get("Xavier"));
        Assertions.assertNull(trie.get("Alice"));
        Assertions.assertNull(map.get("Alice"));
        iterator = map.values().iterator();
        Assertions.assertEquals("Alabama", iterator.next());
        Assertions.assertEquals("Albert", iterator.next());
        Assertions.assertEquals("Alberto", iterator.next());
        Assertions.assertEquals("Albertoo", iterator.next());
        Assertions.assertEquals("Alberts", iterator.next());
        Assertions.assertEquals("Alien", iterator.next());
        Assertions.assertEquals("Allie", iterator.next());
        Assertions.assertEquals("Alliese", iterator.next());
        Assertions.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Albert");
        iterator = map.keySet().iterator();
        Assertions.assertEquals("Albert", iterator.next());
        Assertions.assertEquals("Alberto", iterator.next());
        Assertions.assertEquals("Albertoo", iterator.next());
        Assertions.assertEquals("Alberts", iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertEquals(4, map.size());
        Assertions.assertEquals("Albert", map.firstKey());
        Assertions.assertEquals("Alberts", map.lastKey());
        Assertions.assertNull(trie.get("Albertz"));
        map.put("Albertz", "Albertz");
        Assertions.assertEquals("Albertz", trie.get("Albertz"));
        Assertions.assertEquals(5, map.size());
        Assertions.assertEquals("Albertz", map.lastKey());
        iterator = map.keySet().iterator();
        Assertions.assertEquals("Albert", iterator.next());
        Assertions.assertEquals("Alberto", iterator.next());
        Assertions.assertEquals("Albertoo", iterator.next());
        Assertions.assertEquals("Alberts", iterator.next());
        Assertions.assertEquals("Albertz", iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertEquals("Albertz", map.remove("Albertz"));

        map = trie.prefixMap("Alberto");
        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals("Alberto", map.firstKey());
        Assertions.assertEquals("Albertoo", map.lastKey());
        entryIterator = map.entrySet().iterator();
        entry = entryIterator.next();
        Assertions.assertEquals("Alberto", entry.getKey());
        Assertions.assertEquals("Alberto", entry.getValue());
        entry = entryIterator.next();
        Assertions.assertEquals("Albertoo", entry.getKey());
        Assertions.assertEquals("Albertoo", entry.getValue());
        Assertions.assertFalse(entryIterator.hasNext());
        trie.put("Albertoad", "Albertoad");
        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals("Alberto", map.firstKey());
        Assertions.assertEquals("Albertoo", map.lastKey());
        entryIterator = map.entrySet().iterator();
        entry = entryIterator.next();
        Assertions.assertEquals("Alberto", entry.getKey());
        Assertions.assertEquals("Alberto", entry.getValue());
        entry = entryIterator.next();
        Assertions.assertEquals("Albertoad", entry.getKey());
        Assertions.assertEquals("Albertoad", entry.getValue());
        entry = entryIterator.next();
        Assertions.assertEquals("Albertoo", entry.getKey());
        Assertions.assertEquals("Albertoo", entry.getValue());
        Assertions.assertFalse(entryIterator.hasNext());
        Assertions.assertEquals("Albertoo", trie.remove("Albertoo"));
        Assertions.assertEquals("Alberto", map.firstKey());
        Assertions.assertEquals("Albertoad", map.lastKey());
        Assertions.assertEquals(2, map.size());
        entryIterator = map.entrySet().iterator();
        entry = entryIterator.next();
        Assertions.assertEquals("Alberto", entry.getKey());
        Assertions.assertEquals("Alberto", entry.getValue());
        entry = entryIterator.next();
        Assertions.assertEquals("Albertoad", entry.getKey());
        Assertions.assertEquals("Albertoad", entry.getValue());
        Assertions.assertFalse(entryIterator.hasNext());
        Assertions.assertEquals("Albertoad", trie.remove("Albertoad"));
        trie.put("Albertoo", "Albertoo");

        map = trie.prefixMap("X");
        Assertions.assertEquals(2, map.size());
        Assertions.assertFalse(map.containsKey("Albert"));
        Assertions.assertTrue(map.containsKey("Xavier"));
        Assertions.assertFalse(map.containsKey("Xalan"));
        iterator = map.values().iterator();
        Assertions.assertEquals("Xavier", iterator.next());
        Assertions.assertEquals("XyZ", iterator.next());
        Assertions.assertFalse(iterator.hasNext());

        map = trie.prefixMap("An");
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals("Anna", map.firstKey());
        Assertions.assertEquals("Anna", map.lastKey());
        iterator = map.keySet().iterator();
        Assertions.assertEquals("Anna", iterator.next());
        Assertions.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Ban");
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals("Banane", map.firstKey());
        Assertions.assertEquals("Banane", map.lastKey());
        iterator = map.keySet().iterator();
        Assertions.assertEquals("Banane", iterator.next());
        Assertions.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Am");
        Assertions.assertFalse(map.isEmpty());
        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals("Amber", trie.remove("Amber"));
        iterator = map.keySet().iterator();
        Assertions.assertEquals("Amma", iterator.next());
        Assertions.assertEquals("Ammun", iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        iterator = map.keySet().iterator();
        map.put("Amber", "Amber");
        Assertions.assertEquals(3, map.size());

        final Iterator<String> iterator1 = iterator;
        Assertions.assertThrows(ConcurrentModificationException.class, () -> iterator1.next());

        Assertions.assertEquals("Amber", map.firstKey());
        Assertions.assertEquals("Ammun", map.lastKey());

        map = trie.prefixMap("Ak\0");
        Assertions.assertTrue(map.isEmpty());

        map = trie.prefixMap("Ak");
        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals("Akka", map.firstKey());
        Assertions.assertEquals("Akko", map.lastKey());
        map.put("Ak", "Ak");
        Assertions.assertEquals("Ak", map.firstKey());
        Assertions.assertEquals("Akko", map.lastKey());
        Assertions.assertEquals(3, map.size());
        trie.put("Al", "Al");
        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals("Ak", map.remove("Ak"));
        Assertions.assertEquals("Akka", map.firstKey());
        Assertions.assertEquals("Akko", map.lastKey());
        Assertions.assertEquals(2, map.size());
        iterator = map.keySet().iterator();
        Assertions.assertEquals("Akka", iterator.next());
        Assertions.assertEquals("Akko", iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertEquals("Al", trie.remove("Al"));

        map = trie.prefixMap("Akka");
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals("Akka", map.firstKey());
        Assertions.assertEquals("Akka", map.lastKey());
        iterator = map.keySet().iterator();
        Assertions.assertEquals("Akka", iterator.next());
        Assertions.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Ab");
        Assertions.assertTrue(map.isEmpty());
        Assertions.assertEquals(0, map.size());

        final SortedMap<String, String> map1 = map;
        Assertions.assertThrows(NoSuchElementException.class, () -> map1.firstKey());

        final SortedMap<String, String> map2 = map;
        Assertions.assertThrows(NoSuchElementException.class, () -> map2.lastKey());

        iterator = map.values().iterator();
        Assertions.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Albertooo");
        Assertions.assertTrue(map.isEmpty());
        Assertions.assertEquals(0, map.size());

        final SortedMap<String, String> map3 = map;
        Assertions.assertThrows(NoSuchElementException.class, () -> map3.firstKey(),
                () -> "got a first key: " + map3.firstKey());

        final SortedMap<String, String> map4 = map;
        Assertions.assertThrows(NoSuchElementException.class, () -> map4.lastKey(),
                () -> "got a last key: " + map4.lastKey());

        iterator = map.values().iterator();
        Assertions.assertFalse(iterator.hasNext());

        map = trie.prefixMap("");
        Assertions.assertSame(trie, map); // stricter than necessary, but a good check

        map = trie.prefixMap("\0");
        Assertions.assertTrue(map.isEmpty());
        Assertions.assertEquals(0, map.size());

        final SortedMap<String, String> map5 = map;
        Assertions.assertThrows(NoSuchElementException.class, () -> map5.firstKey(),
                () -> "got a first key: " + map5.firstKey());

        final SortedMap<String, String> map6 = map;
        Assertions.assertThrows(NoSuchElementException.class, () -> map6.lastKey(),
                () -> "got a last key: " + map6.lastKey());

        iterator = map.values().iterator();
        Assertions.assertFalse(iterator.hasNext());
    }

    public void testPrefixMapRemoval() {
        final PatriciaTrie<String> trie = new PatriciaTrie<>();

        final String[] keys = {
            "Albert", "Xavier", "XyZ", "Anna", "Alien", "Alberto",
            "Alberts", "Allie", "Alliese", "Alabama", "Banane",
            "Blabla", "Amber", "Ammun", "Akka", "Akko", "Albertoo",
            "Amma"
        };

        for (final String key : keys) {
            trie.put(key, key);
        }

        SortedMap<String, String> map = trie.prefixMap("Al");
        Assertions.assertEquals(8, map.size());
        Iterator<String> iter = map.keySet().iterator();
        Assertions.assertEquals("Alabama", iter.next());
        Assertions.assertEquals("Albert", iter.next());
        Assertions.assertEquals("Alberto", iter.next());
        Assertions.assertEquals("Albertoo", iter.next());
        Assertions.assertEquals("Alberts", iter.next());
        Assertions.assertEquals("Alien", iter.next());
        iter.remove();
        Assertions.assertEquals(7, map.size());
        Assertions.assertEquals("Allie", iter.next());
        Assertions.assertEquals("Alliese", iter.next());
        Assertions.assertFalse(iter.hasNext());

        map = trie.prefixMap("Ak");
        Assertions.assertEquals(2, map.size());
        iter = map.keySet().iterator();
        Assertions.assertEquals("Akka", iter.next());
        iter.remove();
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals("Akko", iter.next());

        final Iterator<String> iter1 = iter;
        Assertions.assertFalse(iter.hasNext(), () -> "shouldn't have next (but was: " + iter1.next() + ")");

        Assertions.assertFalse(iter.hasNext());
    }

    public void testPrefixMapSizes() {
        // COLLECTIONS-525
        final PatriciaTrie<String> aTree = new PatriciaTrie<>();
        aTree.put("点评", "测试");
        aTree.put("书评", "测试");
        assertTrue(aTree.prefixMap("点").containsKey("点评"));
        assertEquals("测试", aTree.prefixMap("点").get("点评"));
        assertFalse(aTree.prefixMap("点").isEmpty());
        assertEquals(1, aTree.prefixMap("点").size());
        assertEquals(1, aTree.prefixMap("点").size());
        assertEquals(1, aTree.prefixMap("点").entrySet().size());
        assertEquals(1, aTree.prefixMap("点评").size());

        aTree.clear();
        aTree.put("点评", "联盟");
        aTree.put("点版", "定向");
        assertEquals(2, aTree.prefixMap("点").size());
        assertEquals(2, aTree.prefixMap("点").size());
    }

    public void testPrefixMapSizes2() {
        final char u8000 = Character.toChars(32768)[0]; // U+8000 (1000000000000000)
        final char char_b = 'b'; // 1100010

        final PatriciaTrie<String> trie = new PatriciaTrie<>();
        final String prefixString = "" + char_b;
        final String longerString = prefixString + u8000;

        assertEquals(1, prefixString.length());
        assertEquals(2, longerString.length());

        assertTrue(longerString.startsWith(prefixString));

        trie.put(prefixString, "prefixString");
        trie.put(longerString, "longerString");

        assertEquals(2, trie.prefixMap(prefixString).size());
        assertTrue(trie.prefixMap(prefixString).containsKey(longerString));
    }

    public void testPrefixMapClear() {
        final Trie<String, Integer> trie = new PatriciaTrie<>();
        trie.put("Anna", 1);
        trie.put("Anael", 2);
        trie.put("Analu", 3);
        trie.put("Andreas", 4);
        trie.put("Andrea", 5);
        trie.put("Andres", 6);
        trie.put("Anatole", 7);
        final SortedMap<String, Integer> prefixMap = trie.prefixMap("And");
        assertEquals(new HashSet<>(Arrays.asList("Andrea", "Andreas", "Andres")), prefixMap.keySet());
        assertEquals(Arrays.asList(5, 4, 6), new ArrayList<>(prefixMap.values()));

        prefixMap.clear();
        assertTrue(prefixMap.isEmpty());
        assertTrue(prefixMap.isEmpty());
        assertTrue(prefixMap.isEmpty());
        assertEquals(new HashSet<>(Arrays.asList("Anael", "Analu", "Anatole", "Anna")), trie.keySet());
        assertEquals(Arrays.asList(2, 3, 7, 1), new ArrayList<>(trie.values()));
    }

    public void testPrefixMapClearNothing() {
        final Trie<String, Integer> trie = new PatriciaTrie<>();
        final SortedMap<String, Integer> prefixMap = trie.prefixMap("And");
        assertEquals(new HashSet<String>(), prefixMap.keySet());
        assertEquals(new ArrayList<Integer>(0), new ArrayList<>(prefixMap.values()));

        prefixMap.clear();
        assertTrue(prefixMap.isEmpty());
        assertTrue(prefixMap.isEmpty());
        assertTrue(prefixMap.isEmpty());
        assertEquals(new HashSet<String>(), trie.keySet());
        assertEquals(new ArrayList<Integer>(0), new ArrayList<>(trie.values()));
    }

    public void testPrefixMapClearUsingRemove() {
        final Trie<String, Integer> trie = new PatriciaTrie<>();
        trie.put("Anna", 1);
        trie.put("Anael", 2);
        trie.put("Analu", 3);
        trie.put("Andreas", 4);
        trie.put("Andrea", 5);
        trie.put("Andres", 6);
        trie.put("Anatole", 7);
        final SortedMap<String, Integer> prefixMap = trie.prefixMap("And");
        assertEquals(new HashSet<>(Arrays.asList("Andrea", "Andreas", "Andres")), prefixMap.keySet());
        assertEquals(Arrays.asList(5, 4, 6), new ArrayList<>(prefixMap.values()));

        final Set<String> keys = new HashSet<>(prefixMap.keySet());
        for (final String key : keys) {
            prefixMap.remove(key);
        }
        assertTrue(prefixMap.isEmpty());
        assertTrue(prefixMap.isEmpty());
        assertEquals(new HashSet<>(Arrays.asList("Anael", "Analu", "Anatole", "Anna")), trie.keySet());
        assertEquals(Arrays.asList(2, 3, 7, 1), new ArrayList<>(trie.values()));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/PatriciaTrie.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/PatriciaTrie.fullCollection.version4.obj");
//    }

}
