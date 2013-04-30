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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.collections4.Trie.Cursor;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests.
 *
 * FIXME: add serialization support
 *
 * @since 4.0
 * @version $Id$
 */
public class PatriciaTrieTest {

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testSimple() {
        final PatriciaTrie<Integer, String> intTrie = new PatriciaTrie<Integer, String>(new IntegerKeyAnalyzer());
        Assert.assertTrue(intTrie.isEmpty());
        Assert.assertEquals(0, intTrie.size());

        intTrie.put(1, "One");
        Assert.assertFalse(intTrie.isEmpty());
        Assert.assertEquals(1, intTrie.size());

        Assert.assertEquals("One", intTrie.remove(1));
        Assert.assertNull(intTrie.remove(1));
        Assert.assertTrue(intTrie.isEmpty());
        Assert.assertEquals(0, intTrie.size());

        intTrie.put(1, "One");
        Assert.assertEquals("One", intTrie.get(1));
        Assert.assertEquals("One", intTrie.put(1, "NotOne"));
        Assert.assertEquals(1, intTrie.size());
        Assert.assertEquals("NotOne", intTrie.get(1));
        Assert.assertEquals("NotOne", intTrie.remove(1));
        Assert.assertNull(intTrie.put(1, "One"));
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testCeilingEntry() {
        final PatriciaTrie<Character, String> charTrie
            = new PatriciaTrie<Character, String>(new CharacterKeyAnalyzer());
        charTrie.put('c', "c");
        charTrie.put('p', "p");
        charTrie.put('l', "l");
        charTrie.put('t', "t");
        charTrie.put('k', "k");
        charTrie.put('a', "a");
        charTrie.put('y', "y");
        charTrie.put('r', "r");
        charTrie.put('u', "u");
        charTrie.put('o', "o");
        charTrie.put('w', "w");
        charTrie.put('i', "i");
        charTrie.put('e', "e");
        charTrie.put('x', "x");
        charTrie.put('q', "q");
        charTrie.put('b', "b");
        charTrie.put('j', "j");
        charTrie.put('s', "s");
        charTrie.put('n', "n");
        charTrie.put('v', "v");
        charTrie.put('g', "g");
        charTrie.put('h', "h");
        charTrie.put('m', "m");
        charTrie.put('z', "z");
        charTrie.put('f', "f");
        charTrie.put('d', "d");

        final Object[] results = new Object[] {
            'a', "a", 'b', "b", 'c', "c", 'd', "d", 'e', "e",
            'f', "f", 'g', "g", 'h', "h", 'i', "i", 'j', "j",
            'k', "k", 'l', "l", 'm', "m", 'n', "n", 'o', "o",
            'p', "p", 'q', "q", 'r', "r", 's', "s", 't', "t",
            'u', "u", 'v', "v", 'w', "w", 'x', "x", 'y', "y",
            'z', "z"
        };

        for(int i = 0; i < results.length; i++) {
            final Map.Entry<Character, String> found = charTrie.ceilingEntry((Character)results[i]);
            Assert.assertNotNull(found);
            Assert.assertEquals(results[i], found.getKey());
            Assert.assertEquals(results[++i], found.getValue());
        }

        // Remove some & try again...
        charTrie.remove('a');
        charTrie.remove('z');
        charTrie.remove('q');
        charTrie.remove('l');
        charTrie.remove('p');
        charTrie.remove('m');
        charTrie.remove('u');

        Map.Entry<Character, String> found = charTrie.ceilingEntry('u');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'v', found.getKey());

        found = charTrie.ceilingEntry('a');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'b', found.getKey());

        found = charTrie.ceilingEntry('z');
        Assert.assertNull(found);

        found = charTrie.ceilingEntry('q');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'r', found.getKey());

        found = charTrie.ceilingEntry('l');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'n', found.getKey());

        found = charTrie.ceilingEntry('p');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'r', found.getKey());

        found = charTrie.ceilingEntry('m');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'n', found.getKey());

        found = charTrie.ceilingEntry('\0');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'b', found.getKey());

        charTrie.put('\0', "");
        found = charTrie.ceilingEntry('\0');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'\0', found.getKey());
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testLowerEntry() {
        final PatriciaTrie<Character, String> charTrie = new PatriciaTrie<Character, String>(new CharacterKeyAnalyzer());
        charTrie.put('c', "c");
        charTrie.put('p', "p");
        charTrie.put('l', "l");
        charTrie.put('t', "t");
        charTrie.put('k', "k");
        charTrie.put('a', "a");
        charTrie.put('y', "y");
        charTrie.put('r', "r");
        charTrie.put('u', "u");
        charTrie.put('o', "o");
        charTrie.put('w', "w");
        charTrie.put('i', "i");
        charTrie.put('e', "e");
        charTrie.put('x', "x");
        charTrie.put('q', "q");
        charTrie.put('b', "b");
        charTrie.put('j', "j");
        charTrie.put('s', "s");
        charTrie.put('n', "n");
        charTrie.put('v', "v");
        charTrie.put('g', "g");
        charTrie.put('h', "h");
        charTrie.put('m', "m");
        charTrie.put('z', "z");
        charTrie.put('f', "f");
        charTrie.put('d', "d");

        final Object[] results = new Object[] {
            'a', "a", 'b', "b", 'c', "c", 'd', "d", 'e', "e",
            'f', "f", 'g', "g", 'h', "h", 'i', "i", 'j', "j",
            'k', "k", 'l', "l", 'm', "m", 'n', "n", 'o', "o",
            'p', "p", 'q', "q", 'r', "r", 's', "s", 't', "t",
            'u', "u", 'v', "v", 'w', "w", 'x', "x", 'y', "y",
            'z', "z"
        };

        for(int i = 0; i < results.length; i+=2) {
            //System.out.println("Looking for: " + results[i]);
            final Map.Entry<Character, String> found = charTrie.lowerEntry((Character)results[i]);
            if(i == 0) {
                Assert.assertNull(found);
            } else {
                Assert.assertNotNull(found);
                Assert.assertEquals(results[i-2], found.getKey());
                Assert.assertEquals(results[i-1], found.getValue());
            }
        }

        Map.Entry<Character, String> found = charTrie.lowerEntry((char)('z' + 1));
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'z', found.getKey());

        // Remove some & try again...
        charTrie.remove('a');
        charTrie.remove('z');
        charTrie.remove('q');
        charTrie.remove('l');
        charTrie.remove('p');
        charTrie.remove('m');
        charTrie.remove('u');

        found = charTrie.lowerEntry('u');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'t', found.getKey());

        found = charTrie.lowerEntry('v');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'t', found.getKey());

        found = charTrie.lowerEntry('a');
        Assert.assertNull(found);

        found = charTrie.lowerEntry('z');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'y', found.getKey());

        found = charTrie.lowerEntry((char)('z'+1));
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'y', found.getKey());

        found = charTrie.lowerEntry('q');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'o', found.getKey());

        found = charTrie.lowerEntry('r');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'o', found.getKey());

        found = charTrie.lowerEntry('p');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'o', found.getKey());

        found = charTrie.lowerEntry('l');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'k', found.getKey());

        found = charTrie.lowerEntry('m');
        Assert.assertNotNull(found);
        Assert.assertEquals((Character)'k', found.getKey());

        found = charTrie.lowerEntry('\0');
        Assert.assertNull(found);

        charTrie.put('\0', "");
        found = charTrie.lowerEntry('\0');
        Assert.assertNull(found);
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testIteration() {
        final PatriciaTrie<Integer, String> intTrie = new PatriciaTrie<Integer, String>(new IntegerKeyAnalyzer());
        intTrie.put(1, "One");
        intTrie.put(5, "Five");
        intTrie.put(4, "Four");
        intTrie.put(2, "Two");
        intTrie.put(3, "Three");
        intTrie.put(15, "Fifteen");
        intTrie.put(13, "Thirteen");
        intTrie.put(14, "Fourteen");
        intTrie.put(16, "Sixteen");

        TestCursor cursor = new TestCursor(
                1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five", 13, "Thirteen",
                14, "Fourteen", 15, "Fifteen", 16, "Sixteen");

        cursor.starting();
        intTrie.traverse(cursor);
        cursor.finished();

        cursor.starting();
        for (final Map.Entry<Integer, String> entry : intTrie.entrySet()) {
            cursor.select(entry);
        }
        cursor.finished();

        cursor.starting();
        for (final Integer integer : intTrie.keySet()) {
            cursor.checkKey(integer);
        }
        cursor.finished();

        cursor.starting();
        for (final String string : intTrie.values()) {
            cursor.checkValue(string);
        }
        cursor.finished();

        final PatriciaTrie<Character, String> charTrie = new PatriciaTrie<Character, String>(new CharacterKeyAnalyzer());
        charTrie.put('c', "c");
        charTrie.put('p', "p");
        charTrie.put('l', "l");
        charTrie.put('t', "t");
        charTrie.put('k', "k");
        charTrie.put('a', "a");
        charTrie.put('y', "y");
        charTrie.put('r', "r");
        charTrie.put('u', "u");
        charTrie.put('o', "o");
        charTrie.put('w', "w");
        charTrie.put('i', "i");
        charTrie.put('e', "e");
        charTrie.put('x', "x");
        charTrie.put('q', "q");
        charTrie.put('b', "b");
        charTrie.put('j', "j");
        charTrie.put('s', "s");
        charTrie.put('n', "n");
        charTrie.put('v', "v");
        charTrie.put('g', "g");
        charTrie.put('h', "h");
        charTrie.put('m', "m");
        charTrie.put('z', "z");
        charTrie.put('f', "f");
        charTrie.put('d', "d");
        cursor = new TestCursor('a', "a", 'b', "b", 'c', "c", 'd', "d", 'e', "e",
                'f', "f", 'g', "g", 'h', "h", 'i', "i", 'j', "j",
                'k', "k", 'l', "l", 'm', "m", 'n', "n", 'o', "o",
                'p', "p", 'q', "q", 'r', "r", 's', "s", 't', "t",
                'u', "u", 'v', "v", 'w', "w", 'x', "x", 'y', "y",
                'z', "z");

        cursor.starting();
        charTrie.traverse(cursor);
        cursor.finished();

        cursor.starting();
        for (final Map.Entry<Character, String> entry : charTrie.entrySet()) {
            cursor.select(entry);
        }
        cursor.finished();

        cursor.starting();
        for (final Character character : charTrie.keySet()) {
            cursor.checkKey(character);
        }
        cursor.finished();

        cursor.starting();
        for (final String string : charTrie.values()) {
            cursor.checkValue(string);
        }
        cursor.finished();
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testSelect() {
        final PatriciaTrie<Character, String> charTrie = new PatriciaTrie<Character, String>(new CharacterKeyAnalyzer());
        charTrie.put('c', "c");
        charTrie.put('p', "p");
        charTrie.put('l', "l");
        charTrie.put('t', "t");
        charTrie.put('k', "k");
        charTrie.put('a', "a");
        charTrie.put('y', "y");
        charTrie.put('r', "r");
        charTrie.put('u', "u");
        charTrie.put('o', "o");
        charTrie.put('w', "w");
        charTrie.put('i', "i");
        charTrie.put('e', "e");
        charTrie.put('x', "x");
        charTrie.put('q', "q");
        charTrie.put('b', "b");
        charTrie.put('j', "j");
        charTrie.put('s', "s");
        charTrie.put('n', "n");
        charTrie.put('v', "v");
        charTrie.put('g', "g");
        charTrie.put('h', "h");
        charTrie.put('m', "m");
        charTrie.put('z', "z");
        charTrie.put('f', "f");
        charTrie.put('d', "d");
        final TestCursor cursor = new TestCursor(
                'd', "d", 'e', "e", 'f', "f", 'g', "g",
                'a', "a", 'b', "b", 'c', "c",
                'l', "l", 'm', "m", 'n', "n", 'o', "o",
                'h', "h", 'i', "i", 'j', "j", 'k', "k",
                't', "t", 'u', "u", 'v', "v", 'w', "w",
                'p', "p", 'q', "q", 'r', "r", 's', "s",
                'x', "x", 'y', "y", 'z', "z");

        Assert.assertEquals(26, charTrie.size());

        cursor.starting();
        charTrie.select('d', cursor);
        cursor.finished();
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testTraverseCursorRemove() {
        final PatriciaTrie<Character, String> charTrie = new PatriciaTrie<Character, String>(new CharacterKeyAnalyzer());
        charTrie.put('c', "c");
        charTrie.put('p', "p");
        charTrie.put('l', "l");
        charTrie.put('t', "t");
        charTrie.put('k', "k");
        charTrie.put('a', "a");
        charTrie.put('y', "y");
        charTrie.put('r', "r");
        charTrie.put('u', "u");
        charTrie.put('o', "o");
        charTrie.put('w', "w");
        charTrie.put('i', "i");
        charTrie.put('e', "e");
        charTrie.put('x', "x");
        charTrie.put('q', "q");
        charTrie.put('b', "b");
        charTrie.put('j', "j");
        charTrie.put('s', "s");
        charTrie.put('n', "n");
        charTrie.put('v', "v");
        charTrie.put('g', "g");
        charTrie.put('h', "h");
        charTrie.put('m', "m");
        charTrie.put('z', "z");
        charTrie.put('f', "f");
        charTrie.put('d', "d");
        final TestCursor cursor = new TestCursor('a', "a", 'b', "b", 'c', "c", 'd', "d", 'e', "e",
                'f', "f", 'g', "g", 'h', "h", 'i', "i", 'j', "j",
                'k', "k", 'l', "l", 'm', "m", 'n', "n", 'o', "o",
                'p', "p", 'q', "q", 'r', "r", 's', "s", 't', "t",
                'u', "u", 'v', "v", 'w', "w", 'x', "x", 'y', "y",
                'z', "z");

        cursor.starting();
        charTrie.traverse(cursor);
        cursor.finished();

        // Test removing both an internal & external node.
        // 'm' is an example External node in this Trie, and 'p' is an internal.

        Assert.assertEquals(26, charTrie.size());

        final Object[] toRemove = new Object[] { 'g', 'd', 'e', 'm', 'p', 'q', 'r', 's' };
        cursor.addToRemove(toRemove);

        cursor.starting();
        charTrie.traverse(cursor);
        cursor.finished();

        Assert.assertEquals(26 - toRemove.length, charTrie.size());

        cursor.starting();
        charTrie.traverse(cursor);
        cursor.finished();

        cursor.starting();
        for (final Entry<Character, String> entry : charTrie.entrySet()) {
            cursor.select(entry);
            if (Arrays.asList(toRemove).contains(entry.getKey())) {
                Assert.fail("got an: " + entry);
            }
        }
        cursor.finished();
    }

    @Test
    @SuppressWarnings("boxing") // OK in test code
    public void testIteratorRemove() {
        final PatriciaTrie<Character, String> charTrie = new PatriciaTrie<Character, String>(new CharacterKeyAnalyzer());
        charTrie.put('c', "c");
        charTrie.put('p', "p");
        charTrie.put('l', "l");
        charTrie.put('t', "t");
        charTrie.put('k', "k");
        charTrie.put('a', "a");
        charTrie.put('y', "y");
        charTrie.put('r', "r");
        charTrie.put('u', "u");
        charTrie.put('o', "o");
        charTrie.put('w', "w");
        charTrie.put('i', "i");
        charTrie.put('e', "e");
        charTrie.put('x', "x");
        charTrie.put('q', "q");
        charTrie.put('b', "b");
        charTrie.put('j', "j");
        charTrie.put('s', "s");
        charTrie.put('n', "n");
        charTrie.put('v', "v");
        charTrie.put('g', "g");
        charTrie.put('h', "h");
        charTrie.put('m', "m");
        charTrie.put('z', "z");
        charTrie.put('f', "f");
        charTrie.put('d', "d");
        final TestCursor cursor = new TestCursor('a', "a", 'b', "b", 'c', "c", 'd', "d", 'e', "e",
                'f', "f", 'g', "g", 'h', "h", 'i', "i", 'j', "j",
                'k', "k", 'l', "l", 'm', "m", 'n', "n", 'o', "o",
                'p', "p", 'q', "q", 'r', "r", 's', "s", 't', "t",
                'u', "u", 'v', "v", 'w', "w", 'x', "x", 'y', "y",
                'z', "z");

        // Test removing both an internal & external node.
        // 'm' is an example External node in this Trie, and 'p' is an internal.

        Assert.assertEquals(26, charTrie.size());

        final Object[] toRemove = new Object[] { 'e', 'm', 'p', 'q', 'r', 's' };

        cursor.starting();
        for(final Iterator<Map.Entry<Character, String>> i = charTrie.entrySet().iterator(); i.hasNext(); ) {
            final Map.Entry<Character,String> entry = i.next();
            cursor.select(entry);
            if(Arrays.asList(toRemove).contains(entry.getKey())) {
                i.remove();
            }
        }
        cursor.finished();

        Assert.assertEquals(26 - toRemove.length, charTrie.size());

        cursor.remove(toRemove);

        cursor.starting();
        for (final Entry<Character, String> entry : charTrie.entrySet()) {
            cursor.select(entry);
            if (Arrays.asList(toRemove).contains(entry.getKey())) {
                Assert.fail("got an: " + entry);
            }
        }
        cursor.finished();
    }

    @Test
    public void testHamlet() throws Exception {
        // Make sure that Hamlet is read & stored in the same order as a SortedSet.
        final List<String> original = new ArrayList<String>();
        final List<String> control = new ArrayList<String>();
        final SortedMap<String, String> sortedControl = new TreeMap<String, String>();
        final PatriciaTrie<String, String> trie = new PatriciaTrie<String, String>(new StringKeyAnalyzer());

        final InputStream in = getClass().getResourceAsStream("hamlet.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String read = null;
        while( (read = reader.readLine()) != null) {
            final StringTokenizer st = new StringTokenizer(read);
            while(st.hasMoreTokens()) {
                final String token = st.nextToken();
                original.add(token);
                sortedControl.put(token, token);
                trie.put(token, token);
            }
        }
        control.addAll(sortedControl.values());

        Assert.assertEquals(control.size(), sortedControl.size());
        Assert.assertEquals(sortedControl.size(), trie.size());
        Iterator<String> iter = trie.values().iterator();
        for (final String aControl : control) {
            Assert.assertEquals(aControl, iter.next());
        }

        final Random rnd = new Random();
        int item = 0;
        iter = trie.values().iterator();
        int removed = 0;
        for(; item < control.size(); item++) {
            Assert.assertEquals(control.get(item), iter.next());
            if(rnd.nextBoolean()) {
                iter.remove();
                removed++;
            }
        }

        Assert.assertEquals(control.size(), item);
        Assert.assertTrue(removed > 0);
        Assert.assertEquals(control.size(), trie.size() + removed);

        // reset hamlet
        trie.clear();
        for (final String anOriginal : original) {
            trie.put(anOriginal, anOriginal);
        }

        assertEqualArrays(sortedControl.values().toArray(), trie.values().toArray());
        assertEqualArrays(sortedControl.keySet().toArray(), trie.keySet().toArray());
        assertEqualArrays(sortedControl.entrySet().toArray(), trie.entrySet().toArray());

        Assert.assertEquals(sortedControl.firstKey(), trie.firstKey());
        Assert.assertEquals(sortedControl.lastKey(), trie.lastKey());

        SortedMap<String, String> sub = trie.headMap(control.get(523));
        Assert.assertEquals(523, sub.size());
        for(int i = 0; i < control.size(); i++) {
            if(i < 523) {
                Assert.assertTrue(sub.containsKey(control.get(i)));
            } else {
                Assert.assertFalse(sub.containsKey(control.get(i)));
            }
        }
        // Too slow to check values on all, so just do a few.
        Assert.assertTrue(sub.containsValue(control.get(522)));
        Assert.assertFalse(sub.containsValue(control.get(523)));
        Assert.assertFalse(sub.containsValue(control.get(524)));

        try {
            sub.headMap(control.get(524));
            Assert.fail("should have thrown IAE");
        } catch(final IllegalArgumentException expected) {}

        Assert.assertEquals(sub.lastKey(), control.get(522));
        Assert.assertEquals(sub.firstKey(), control.get(0));

        sub = sub.tailMap(control.get(234));
        Assert.assertEquals(289, sub.size());
        Assert.assertEquals(control.get(234), sub.firstKey());
        Assert.assertEquals(control.get(522), sub.lastKey());
        for(int i = 0; i < control.size(); i++) {
            if(i < 523 && i > 233) {
                Assert.assertTrue(sub.containsKey(control.get(i)));
            } else {
                Assert.assertFalse(sub.containsKey(control.get(i)));
            }
        }

        try {
            sub.tailMap(control.get(232));
            Assert.fail("should have thrown IAE");
        } catch(final IllegalArgumentException expected) {}

        sub = sub.subMap(control.get(300), control.get(400));
        Assert.assertEquals(100, sub.size());
        Assert.assertEquals(control.get(300), sub.firstKey());
        Assert.assertEquals(control.get(399), sub.lastKey());

        for(int i = 0; i < control.size(); i++) {
            if(i < 400 && i > 299) {
                Assert.assertTrue(sub.containsKey(control.get(i)));
            } else {
                Assert.assertFalse(sub.containsKey(control.get(i)));
            }
        }
    }

    @Test
    public void testPrefixedBy() {
        final PatriciaTrie<String, String> trie
            = new PatriciaTrie<String, String>(new StringKeyAnalyzer());

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

        map = trie.getPrefixedBy("Al");
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

        map = trie.getPrefixedBy("Albert");
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

        map = trie.getPrefixedBy("Alberto");
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

        map = trie.getPrefixedBy("X");
        Assert.assertEquals(2, map.size());
        Assert.assertFalse(map.containsKey("Albert"));
        Assert.assertTrue(map.containsKey("Xavier"));
        Assert.assertFalse(map.containsKey("Xalan"));
        iterator = map.values().iterator();
        Assert.assertEquals("Xavier", iterator.next());
        Assert.assertEquals("XyZ", iterator.next());
        Assert.assertFalse(iterator.hasNext());

        map = trie.getPrefixedBy("An");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("Anna", map.firstKey());
        Assert.assertEquals("Anna", map.lastKey());
        iterator = map.keySet().iterator();
        Assert.assertEquals("Anna", iterator.next());
        Assert.assertFalse(iterator.hasNext());

        map = trie.getPrefixedBy("Ban");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("Banane", map.firstKey());
        Assert.assertEquals("Banane", map.lastKey());
        iterator = map.keySet().iterator();
        Assert.assertEquals("Banane", iterator.next());
        Assert.assertFalse(iterator.hasNext());

        map = trie.getPrefixedBy("Am");
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

        map = trie.getPrefixedBy("Ak\0");
        Assert.assertTrue(map.isEmpty());

        map = trie.getPrefixedBy("Ak");
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

        map = trie.getPrefixedBy("Akka");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("Akka", map.firstKey());
        Assert.assertEquals("Akka", map.lastKey());
        iterator = map.keySet().iterator();
        Assert.assertEquals("Akka", iterator.next());
        Assert.assertFalse(iterator.hasNext());

        map = trie.getPrefixedBy("Ab");
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

        map = trie.getPrefixedBy("Albertooo");
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

        map = trie.getPrefixedBy("");
        Assert.assertSame(trie, map); // stricter than necessary, but a good check

        map = trie.getPrefixedBy("\0");
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

    @Test
    public void testPrefixByOffsetAndLength() {
        final PatriciaTrie<String, String> trie
            = new PatriciaTrie<String, String>(new StringKeyAnalyzer());

        final String[] keys = new String[]{
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

        map = trie.getPrefixedBy("Alice", 2);
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

        map = trie.getPrefixedBy("BAlice", 1, 2);
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
    }

    @Test
    public void testPrefixedByRemoval() {
        final PatriciaTrie<String, String> trie
            = new PatriciaTrie<String, String>(new StringKeyAnalyzer());

        final String[] keys = new String[]{
                "Albert", "Xavier", "XyZ", "Anna", "Alien", "Alberto",
                "Alberts", "Allie", "Alliese", "Alabama", "Banane",
                "Blabla", "Amber", "Ammun", "Akka", "Akko", "Albertoo",
                "Amma"
        };

        for (final String key : keys) {
            trie.put(key, key);
        }

        SortedMap<String, String> map = trie.getPrefixedBy("Al");
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

        map = trie.getPrefixedBy("Ak");
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

    @Test
    public void testTraverseWithAllNullBitKey() {
        final PatriciaTrie<String, String> trie
            = new PatriciaTrie<String, String>(new StringKeyAnalyzer());

        //
        // One entry in the Trie
        // Entry is stored at the root
        //

        // trie.put("", "All Bits Are Zero");
        trie.put("\0", "All Bits Are Zero");

        //
        //  / ("")   <-- root
        //  \_/  \
        //       null
        //

        final List<String> strings = new ArrayList<String>();
        trie.traverse(new Cursor<String, String>() {
            public Decision select(final Entry<? extends String, ? extends String> entry) {
                strings.add(entry.getValue());
                return Decision.CONTINUE;
            }
        });

        Assert.assertEquals(1, strings.size());

        strings.clear();
        for (final String s : trie.values()) {
            strings.add(s);
        }
        Assert.assertEquals(1, strings.size());
    }

    @Test
    public void testSelectWithAllNullBitKey() {
        final PatriciaTrie<String, String> trie
            = new PatriciaTrie<String, String>(new StringKeyAnalyzer());

        // trie.put("", "All Bits Are Zero");
        trie.put("\0", "All Bits Are Zero");

        final List<String> strings = new ArrayList<String>();
        trie.select("Hello", new Cursor<String, String>() {
            public Decision select(final Entry<? extends String, ? extends String> entry) {
                strings.add(entry.getValue());
                return Decision.CONTINUE;
            }
        });
        Assert.assertEquals(1, strings.size());
    }

    private static class TestCursor implements Cursor<Object, Object> {
        private final List<Object> keys;
        private final List<Object> values;
        private Object selectFor;
        private List<Object> toRemove;
        private int index = 0;

        TestCursor(final Object... objects) {
            if(objects.length % 2 != 0) {
                throw new IllegalArgumentException("must be * 2");
            }

            keys = new ArrayList<Object>(objects.length / 2);
            values = new ArrayList<Object>(keys.size());
            toRemove = Collections.emptyList();
            for(int i = 0; i < objects.length; i++) {
                keys.add(objects[i]);
                values.add(objects[++i]);
            }
        }

        void selectFor(final Object object) {
            selectFor = object;
        }

        void addToRemove(final Object... objects) {
            toRemove = new ArrayList<Object>(Arrays.asList(objects));
        }

        void remove(final Object... objects) {
            for (final Object object : objects) {
                final int idx = keys.indexOf(object);
                keys.remove(idx);
                values.remove(idx);
            }
        }

        void starting() {
            index = 0;
        }

        public void checkKey(final Object k) {
            Assert.assertEquals(keys.get(index++), k);
        }

        public void checkValue(final Object o) {
            Assert.assertEquals(values.get(index++), o);
        }

        public Decision select(final Entry<?, ?> entry) {
          //  System.out.println("Scanning: " + entry.getKey());
            Assert.assertEquals(keys.get(index), entry.getKey());
            Assert.assertEquals(values.get(index), entry.getValue());
            index++;

            if(toRemove.contains(entry.getKey())) {
              // System.out.println("Removing: " + entry.getKey());
                index--;
                keys.remove(index);
                values.remove(index);
                toRemove.remove(entry.getKey());
                return Decision.REMOVE;
            }

            if(selectFor != null && selectFor.equals(entry.getKey())) {
                return Decision.EXIT;
            } else {
                return Decision.CONTINUE;
            }
        }

        void finished() {
            Assert.assertEquals(keys.size(), index);
        }
    }

    private static void assertEqualArrays(final Object[] a, final Object[] b) {
        Assert.assertTrue(Arrays.equals(a, b));
    }
}
