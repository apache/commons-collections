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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    //-----------------------------------------------------------------------

    public void testPrefixMap() {
        final PatriciaTrie<String> trie = new PatriciaTrie<>();

        final String[] keys = new String[]{
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
        assertEquals(8, map.size());
        assertEquals("Alabama", map.firstKey());
        assertEquals("Alliese", map.lastKey());
        assertEquals("Albertoo", map.get("Albertoo"));
        assertNotNull(trie.get("Xavier"));
        assertNull(map.get("Xavier"));
        assertNull(trie.get("Alice"));
        assertNull(map.get("Alice"));
        iterator = map.values().iterator();
        assertEquals("Alabama", iterator.next());
        assertEquals("Albert", iterator.next());
        assertEquals("Alberto", iterator.next());
        assertEquals("Albertoo", iterator.next());
        assertEquals("Alberts", iterator.next());
        assertEquals("Alien", iterator.next());
        assertEquals("Allie", iterator.next());
        assertEquals("Alliese", iterator.next());
        assertFalse(iterator.hasNext());

        map = trie.prefixMap("Albert");
        iterator = map.keySet().iterator();
        assertEquals("Albert", iterator.next());
        assertEquals("Alberto", iterator.next());
        assertEquals("Albertoo", iterator.next());
        assertEquals("Alberts", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(4, map.size());
        assertEquals("Albert", map.firstKey());
        assertEquals("Alberts", map.lastKey());
        assertNull(trie.get("Albertz"));
        map.put("Albertz", "Albertz");
        assertEquals("Albertz", trie.get("Albertz"));
        assertEquals(5, map.size());
        assertEquals("Albertz", map.lastKey());
        iterator = map.keySet().iterator();
        assertEquals("Albert", iterator.next());
        assertEquals("Alberto", iterator.next());
        assertEquals("Albertoo", iterator.next());
        assertEquals("Alberts", iterator.next());
        assertEquals("Albertz", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals("Albertz", map.remove("Albertz"));

        map = trie.prefixMap("Alberto");
        assertEquals(2, map.size());
        assertEquals("Alberto", map.firstKey());
        assertEquals("Albertoo", map.lastKey());
        entryIterator = map.entrySet().iterator();
        entry = entryIterator.next();
        assertEquals("Alberto", entry.getKey());
        assertEquals("Alberto", entry.getValue());
        entry = entryIterator.next();
        assertEquals("Albertoo", entry.getKey());
        assertEquals("Albertoo", entry.getValue());
        assertFalse(entryIterator.hasNext());
        trie.put("Albertoad", "Albertoad");
        assertEquals(3, map.size());
        assertEquals("Alberto", map.firstKey());
        assertEquals("Albertoo", map.lastKey());
        entryIterator = map.entrySet().iterator();
        entry = entryIterator.next();
        assertEquals("Alberto", entry.getKey());
        assertEquals("Alberto", entry.getValue());
        entry = entryIterator.next();
        assertEquals("Albertoad", entry.getKey());
        assertEquals("Albertoad", entry.getValue());
        entry = entryIterator.next();
        assertEquals("Albertoo", entry.getKey());
        assertEquals("Albertoo", entry.getValue());
        assertFalse(entryIterator.hasNext());
        assertEquals("Albertoo", trie.remove("Albertoo"));
        assertEquals("Alberto", map.firstKey());
        assertEquals("Albertoad", map.lastKey());
        assertEquals(2, map.size());
        entryIterator = map.entrySet().iterator();
        entry = entryIterator.next();
        assertEquals("Alberto", entry.getKey());
        assertEquals("Alberto", entry.getValue());
        entry = entryIterator.next();
        assertEquals("Albertoad", entry.getKey());
        assertEquals("Albertoad", entry.getValue());
        assertFalse(entryIterator.hasNext());
        assertEquals("Albertoad", trie.remove("Albertoad"));
        trie.put("Albertoo", "Albertoo");

        map = trie.prefixMap("X");
        assertEquals(2, map.size());
        assertFalse(map.containsKey("Albert"));
        assertTrue(map.containsKey("Xavier"));
        assertFalse(map.containsKey("Xalan"));
        iterator = map.values().iterator();
        assertEquals("Xavier", iterator.next());
        assertEquals("XyZ", iterator.next());
        assertFalse(iterator.hasNext());

        map = trie.prefixMap("An");
        assertEquals(1, map.size());
        assertEquals("Anna", map.firstKey());
        assertEquals("Anna", map.lastKey());
        iterator = map.keySet().iterator();
        assertEquals("Anna", iterator.next());
        assertFalse(iterator.hasNext());

        map = trie.prefixMap("Ban");
        assertEquals(1, map.size());
        assertEquals("Banane", map.firstKey());
        assertEquals("Banane", map.lastKey());
        iterator = map.keySet().iterator();
        assertEquals("Banane", iterator.next());
        assertFalse(iterator.hasNext());

        map = trie.prefixMap("Am");
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertEquals("Amber", trie.remove("Amber"));
        iterator = map.keySet().iterator();
        assertEquals("Amma", iterator.next());
        assertEquals("Ammun", iterator.next());
        assertFalse(iterator.hasNext());
        final Iterator<String> iterator1 = map.keySet().iterator();
        map.put("Amber", "Amber");
        assertEquals(3, map.size());
        Exception exception = assertThrows(ConcurrentModificationException.class, () -> {
            iterator1.next();
        });
        assertNull(exception.getMessage());
        assertEquals("Amber", map.firstKey());
        assertEquals("Ammun", map.lastKey());

        map = trie.prefixMap("Ak\0");
        assertTrue(map.isEmpty());

        map = trie.prefixMap("Ak");
        assertEquals(2, map.size());
        assertEquals("Akka", map.firstKey());
        assertEquals("Akko", map.lastKey());
        map.put("Ak", "Ak");
        assertEquals("Ak", map.firstKey());
        assertEquals("Akko", map.lastKey());
        assertEquals(3, map.size());
        trie.put("Al", "Al");
        assertEquals(3, map.size());
        assertEquals("Ak", map.remove("Ak"));
        assertEquals("Akka", map.firstKey());
        assertEquals("Akko", map.lastKey());
        assertEquals(2, map.size());
        iterator = map.keySet().iterator();
        assertEquals("Akka", iterator.next());
        assertEquals("Akko", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals("Al", trie.remove("Al"));

        map = trie.prefixMap("Akka");
        assertEquals(1, map.size());
        assertEquals("Akka", map.firstKey());
        assertEquals("Akka", map.lastKey());
        iterator = map.keySet().iterator();
        assertEquals("Akka", iterator.next());
        assertFalse(iterator.hasNext());

        final SortedMap<String, String> map1 = trie.prefixMap("Ab");
        assertTrue(map1.isEmpty());
        assertEquals(0, map1.size());
        exception = assertThrows(NoSuchElementException.class, () -> {
            final Object o = map1.firstKey();
        });
        assertNull(exception.getMessage());
        exception = assertThrows(NoSuchElementException.class, () -> {
            final Object o = map1.lastKey();
        });
        assertNull(exception.getMessage());
        iterator = map1.values().iterator();
        assertFalse(iterator.hasNext());

        final SortedMap<String, String> map2 = trie.prefixMap("Albertooo");
        assertTrue(map2.isEmpty());
        assertEquals(0, map2.size());
        exception = assertThrows(NoSuchElementException.class, () -> {
            final Object o = map2.firstKey();
        });
        assertNull(exception.getMessage());
        exception = assertThrows(NoSuchElementException.class, () -> {
            final Object o = map2.lastKey();
        });
        assertNull(exception.getMessage());
        iterator = map2.values().iterator();
        assertFalse(iterator.hasNext());

        map = trie.prefixMap("");
        assertSame(trie, map); // stricter than necessary, but a good check

        final SortedMap<String, String> map3 = trie.prefixMap("\0");
        assertTrue(map3.isEmpty());
        assertEquals(0, map3.size());
        exception = assertThrows(NoSuchElementException.class, () -> {
            final Object o = map3.firstKey();
        });
        assertNull(exception.getMessage());
        exception = assertThrows(NoSuchElementException.class, () -> {
            final Object o = map3.lastKey();
        });
        assertNull(exception.getMessage());
        iterator = map3.values().iterator();
        assertFalse(iterator.hasNext());
    }

    public void testPrefixMapRemoval() {
        final PatriciaTrie<String> trie = new PatriciaTrie<>();

        final String[] keys = new String[]{
            "Albert", "Xavier", "XyZ", "Anna", "Alien", "Alberto",
            "Alberts", "Allie", "Alliese", "Alabama", "Banane",
            "Blabla", "Amber", "Ammun", "Akka", "Akko", "Albertoo",
            "Amma"
        };

        for (final String key : keys) {
            trie.put(key, key);
        }

        SortedMap<String, String> map = trie.prefixMap("Al");
        assertEquals(8, map.size());
        Iterator<String> iter = map.keySet().iterator();
        assertEquals("Alabama", iter.next());
        assertEquals("Albert", iter.next());
        assertEquals("Alberto", iter.next());
        assertEquals("Albertoo", iter.next());
        assertEquals("Alberts", iter.next());
        assertEquals("Alien", iter.next());
        iter.remove();
        assertEquals(7, map.size());
        assertEquals("Allie", iter.next());
        assertEquals("Alliese", iter.next());
        assertFalse(iter.hasNext());

        map = trie.prefixMap("Ak");
        assertEquals(2, map.size());
        iter = map.keySet().iterator();
        assertEquals("Akka", iter.next());
        iter.remove();
        assertEquals(1, map.size());
        assertEquals("Akko", iter.next());
        if (iter.hasNext()) {
            fail("shouldn't have next (but was: " + iter.next() + ")");
        }
        assertFalse(iter.hasNext());
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
        assertEquals(1, aTree.prefixMap("点").keySet().size());
        assertEquals(1, aTree.prefixMap("点").entrySet().size());
        assertEquals(1, aTree.prefixMap("点评").values().size());

        aTree.clear();
        aTree.put("点评", "联盟");
        aTree.put("点版", "定向");
        assertEquals(2, aTree.prefixMap("点").keySet().size());
        assertEquals(2, aTree.prefixMap("点").values().size());
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
        assertTrue(prefixMap.keySet().isEmpty());
        assertTrue(prefixMap.values().isEmpty());
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
        assertTrue(prefixMap.keySet().isEmpty());
        assertTrue(prefixMap.values().isEmpty());
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
        assertTrue(prefixMap.keySet().isEmpty());
        assertTrue(prefixMap.values().isEmpty());
        assertEquals(new HashSet<>(Arrays.asList("Anael", "Analu", "Anatole", "Anna")), trie.keySet());
        assertEquals(Arrays.asList(2, 3, 7, 1), new ArrayList<>(trie.values()));
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
//            "src/test/resources/data/test/PatriciaTrie.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/PatriciaTrie.fullCollection.version4.obj");
//    }
}
