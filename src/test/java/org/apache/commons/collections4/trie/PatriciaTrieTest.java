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
import org.junit.Assert;

/**
 * JUnit tests for the PatriciaTrie.
 *
 * @since 4.0
 * @version $Id$
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
        return new PatriciaTrie<V>();
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    //-----------------------------------------------------------------------

    public void testPrefixMap() {
        final PatriciaTrie<String> trie = new PatriciaTrie<String>();

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
        Assert.assertEquals(8, map.size());
        Assert.assertEquals("Alabama", map.firstKey());
        Assert.assertEquals("Alliese", map.lastKey());
        Assert.assertEquals("Albertoo", map.get("Albertoo"));
        Assert.assertNotNull(trie.get("Xavier"));
        Assert.assertNull(map.get("Xavier"));
        Assert.assertNull(trie.get("Alice"));
        Assert.assertNull(map.get("Alice"));
        iterator = map.values().iterator();
        Assert.assertEquals("Alabama", iterator.next());
        Assert.assertEquals("Albert", iterator.next());
        Assert.assertEquals("Alberto", iterator.next());
        Assert.assertEquals("Albertoo", iterator.next());
        Assert.assertEquals("Alberts", iterator.next());
        Assert.assertEquals("Alien", iterator.next());
        Assert.assertEquals("Allie", iterator.next());
        Assert.assertEquals("Alliese", iterator.next());
        Assert.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Albert");
        iterator = map.keySet().iterator();
        Assert.assertEquals("Albert", iterator.next());
        Assert.assertEquals("Alberto", iterator.next());
        Assert.assertEquals("Albertoo", iterator.next());
        Assert.assertEquals("Alberts", iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(4, map.size());
        Assert.assertEquals("Albert", map.firstKey());
        Assert.assertEquals("Alberts", map.lastKey());
        Assert.assertNull(trie.get("Albertz"));
        map.put("Albertz", "Albertz");
        Assert.assertEquals("Albertz", trie.get("Albertz"));
        Assert.assertEquals(5, map.size());
        Assert.assertEquals("Albertz", map.lastKey());
        iterator = map.keySet().iterator();
        Assert.assertEquals("Albert", iterator.next());
        Assert.assertEquals("Alberto", iterator.next());
        Assert.assertEquals("Albertoo", iterator.next());
        Assert.assertEquals("Alberts", iterator.next());
        Assert.assertEquals("Albertz", iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals("Albertz", map.remove("Albertz"));

        map = trie.prefixMap("Alberto");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("Alberto", map.firstKey());
        Assert.assertEquals("Albertoo", map.lastKey());
        entryIterator = map.entrySet().iterator();
        entry = entryIterator.next();
        Assert.assertEquals("Alberto", entry.getKey());
        Assert.assertEquals("Alberto", entry.getValue());
        entry = entryIterator.next();
        Assert.assertEquals("Albertoo", entry.getKey());
        Assert.assertEquals("Albertoo", entry.getValue());
        Assert.assertFalse(entryIterator.hasNext());
        trie.put("Albertoad", "Albertoad");
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("Alberto", map.firstKey());
        Assert.assertEquals("Albertoo", map.lastKey());
        entryIterator = map.entrySet().iterator();
        entry = entryIterator.next();
        Assert.assertEquals("Alberto", entry.getKey());
        Assert.assertEquals("Alberto", entry.getValue());
        entry = entryIterator.next();
        Assert.assertEquals("Albertoad", entry.getKey());
        Assert.assertEquals("Albertoad", entry.getValue());
        entry = entryIterator.next();
        Assert.assertEquals("Albertoo", entry.getKey());
        Assert.assertEquals("Albertoo", entry.getValue());
        Assert.assertFalse(entryIterator.hasNext());
        Assert.assertEquals("Albertoo", trie.remove("Albertoo"));
        Assert.assertEquals("Alberto", map.firstKey());
        Assert.assertEquals("Albertoad", map.lastKey());
        Assert.assertEquals(2, map.size());
        entryIterator = map.entrySet().iterator();
        entry = entryIterator.next();
        Assert.assertEquals("Alberto", entry.getKey());
        Assert.assertEquals("Alberto", entry.getValue());
        entry = entryIterator.next();
        Assert.assertEquals("Albertoad", entry.getKey());
        Assert.assertEquals("Albertoad", entry.getValue());
        Assert.assertFalse(entryIterator.hasNext());
        Assert.assertEquals("Albertoad", trie.remove("Albertoad"));
        trie.put("Albertoo", "Albertoo");

        map = trie.prefixMap("X");
        Assert.assertEquals(2, map.size());
        Assert.assertFalse(map.containsKey("Albert"));
        Assert.assertTrue(map.containsKey("Xavier"));
        Assert.assertFalse(map.containsKey("Xalan"));
        iterator = map.values().iterator();
        Assert.assertEquals("Xavier", iterator.next());
        Assert.assertEquals("XyZ", iterator.next());
        Assert.assertFalse(iterator.hasNext());

        map = trie.prefixMap("An");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("Anna", map.firstKey());
        Assert.assertEquals("Anna", map.lastKey());
        iterator = map.keySet().iterator();
        Assert.assertEquals("Anna", iterator.next());
        Assert.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Ban");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("Banane", map.firstKey());
        Assert.assertEquals("Banane", map.lastKey());
        iterator = map.keySet().iterator();
        Assert.assertEquals("Banane", iterator.next());
        Assert.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Am");
        Assert.assertFalse(map.isEmpty());
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("Amber", trie.remove("Amber"));
        iterator = map.keySet().iterator();
        Assert.assertEquals("Amma", iterator.next());
        Assert.assertEquals("Ammun", iterator.next());
        Assert.assertFalse(iterator.hasNext());
        iterator = map.keySet().iterator();
        map.put("Amber", "Amber");
        Assert.assertEquals(3, map.size());
        try {
            iterator.next();
            Assert.fail("CME expected");
        } catch(final ConcurrentModificationException expected) {}
        Assert.assertEquals("Amber", map.firstKey());
        Assert.assertEquals("Ammun", map.lastKey());

        map = trie.prefixMap("Ak\0");
        Assert.assertTrue(map.isEmpty());

        map = trie.prefixMap("Ak");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("Akka", map.firstKey());
        Assert.assertEquals("Akko", map.lastKey());
        map.put("Ak", "Ak");
        Assert.assertEquals("Ak", map.firstKey());
        Assert.assertEquals("Akko", map.lastKey());
        Assert.assertEquals(3, map.size());
        trie.put("Al", "Al");
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("Ak", map.remove("Ak"));
        Assert.assertEquals("Akka", map.firstKey());
        Assert.assertEquals("Akko", map.lastKey());
        Assert.assertEquals(2, map.size());
        iterator = map.keySet().iterator();
        Assert.assertEquals("Akka", iterator.next());
        Assert.assertEquals("Akko", iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals("Al", trie.remove("Al"));

        map = trie.prefixMap("Akka");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("Akka", map.firstKey());
        Assert.assertEquals("Akka", map.lastKey());
        iterator = map.keySet().iterator();
        Assert.assertEquals("Akka", iterator.next());
        Assert.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Ab");
        Assert.assertTrue(map.isEmpty());
        Assert.assertEquals(0, map.size());
        try {
            final Object o = map.firstKey();
            Assert.fail("got a first key: " + o);
        } catch(final NoSuchElementException nsee) {}
        try {
            final Object o = map.lastKey();
            Assert.fail("got a last key: " + o);
        } catch(final NoSuchElementException nsee) {}
        iterator = map.values().iterator();
        Assert.assertFalse(iterator.hasNext());

        map = trie.prefixMap("Albertooo");
        Assert.assertTrue(map.isEmpty());
        Assert.assertEquals(0, map.size());
        try {
            final Object o = map.firstKey();
            Assert.fail("got a first key: " + o);
        } catch(final NoSuchElementException nsee) {}
        try {
            final Object o = map.lastKey();
            Assert.fail("got a last key: " + o);
        } catch(final NoSuchElementException nsee) {}
        iterator = map.values().iterator();
        Assert.assertFalse(iterator.hasNext());

        map = trie.prefixMap("");
        Assert.assertSame(trie, map); // stricter than necessary, but a good check

        map = trie.prefixMap("\0");
        Assert.assertTrue(map.isEmpty());
        Assert.assertEquals(0, map.size());
        try {
            final Object o = map.firstKey();
            Assert.fail("got a first key: " + o);
        } catch(final NoSuchElementException nsee) {}
        try {
            final Object o = map.lastKey();
            Assert.fail("got a last key: " + o);
        } catch(final NoSuchElementException nsee) {}
        iterator = map.values().iterator();
        Assert.assertFalse(iterator.hasNext());
    }

    public void testPrefixMapRemoval() {
        final PatriciaTrie<String> trie = new PatriciaTrie<String>();

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
        Assert.assertEquals(8, map.size());
        Iterator<String> iter = map.keySet().iterator();
        Assert.assertEquals("Alabama", iter.next());
        Assert.assertEquals("Albert", iter.next());
        Assert.assertEquals("Alberto", iter.next());
        Assert.assertEquals("Albertoo", iter.next());
        Assert.assertEquals("Alberts", iter.next());
        Assert.assertEquals("Alien", iter.next());
        iter.remove();
        Assert.assertEquals(7, map.size());
        Assert.assertEquals("Allie", iter.next());
        Assert.assertEquals("Alliese", iter.next());
        Assert.assertFalse(iter.hasNext());

        map = trie.prefixMap("Ak");
        Assert.assertEquals(2, map.size());
        iter = map.keySet().iterator();
        Assert.assertEquals("Akka", iter.next());
        iter.remove();
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("Akko", iter.next());
        if(iter.hasNext()) {
            Assert.fail("shouldn't have next (but was: " + iter.next() + ")");
        }
        Assert.assertFalse(iter.hasNext());
    }

    public void testPrefixMapSizes() {
        // COLLECTIONS-525
        PatriciaTrie<String> aTree = new PatriciaTrie<String>();
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

        final PatriciaTrie<String> trie = new PatriciaTrie<String>();
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
        Trie<String, Integer> trie = new PatriciaTrie<Integer>();
        trie.put("Anna", 1);
        trie.put("Anael", 2);
        trie.put("Analu", 3);
        trie.put("Andreas", 4);
        trie.put("Andrea", 5);
        trie.put("Andres", 6);
        trie.put("Anatole", 7);
        SortedMap<String, Integer> prefixMap = trie.prefixMap("And");
        assertEquals(new HashSet<String>(Arrays.asList("Andrea", "Andreas", "Andres")), prefixMap.keySet());
        assertEquals(Arrays.asList(5, 4, 6), new ArrayList<Integer>(prefixMap.values()));

        prefixMap.clear();
        assertTrue(prefixMap.isEmpty());
        assertTrue(prefixMap.keySet().isEmpty());
        assertTrue(prefixMap.values().isEmpty());
        assertEquals(new HashSet<String>(Arrays.asList("Anael", "Analu", "Anatole", "Anna")), trie.keySet());
        assertEquals(Arrays.asList(2, 3, 7, 1), new ArrayList<Integer>(trie.values()));
    }

    public void testPrefixMapClearNothing() {
        Trie<String, Integer> trie = new PatriciaTrie<Integer>();
        SortedMap<String, Integer> prefixMap = trie.prefixMap("And");
        assertEquals(new HashSet<String>(), prefixMap.keySet());
        assertEquals(new ArrayList<Integer>(0), new ArrayList<Integer>(prefixMap.values()));

        prefixMap.clear();
        assertTrue(prefixMap.isEmpty());
        assertTrue(prefixMap.keySet().isEmpty());
        assertTrue(prefixMap.values().isEmpty());
        assertEquals(new HashSet<String>(), trie.keySet());
        assertEquals(new ArrayList<Integer>(0), new ArrayList<Integer>(trie.values()));
    }

    public void testPrefixMapClearUsingRemove() {
        Trie<String, Integer> trie = new PatriciaTrie<Integer>();
        trie.put("Anna", 1);
        trie.put("Anael", 2);
        trie.put("Analu", 3);
        trie.put("Andreas", 4);
        trie.put("Andrea", 5);
        trie.put("Andres", 6);
        trie.put("Anatole", 7);
        SortedMap<String, Integer> prefixMap = trie.prefixMap("And");
        assertEquals(new HashSet<String>(Arrays.asList("Andrea", "Andreas", "Andres")), prefixMap.keySet());
        assertEquals(Arrays.asList(5, 4, 6), new ArrayList<Integer>(prefixMap.values()));

        Set<String> keys = new HashSet<String>(prefixMap.keySet());
        for (final String key : keys) {
            prefixMap.remove(key);
        }
        assertTrue(prefixMap.keySet().isEmpty());
        assertTrue(prefixMap.values().isEmpty());
        assertEquals(new HashSet<String>(Arrays.asList("Anael", "Analu", "Anatole", "Anna")), trie.keySet());
        assertEquals(Arrays.asList(2, 3, 7, 1), new ArrayList<Integer>(trie.values()));
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
