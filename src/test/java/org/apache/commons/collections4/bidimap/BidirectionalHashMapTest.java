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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections4.bidimap.BidirectionalHashMap.Mapping;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class tests the bidirectional, bijective hash map.
 *
 * @author rodionefremov
 * @author Chen Guoping
 */
public class BidirectionalHashMapTest {

    private BidirectionalHashMap<Integer, String> map;
    private BidirectionalHashMap<String, Integer> map2;
    private Map<String, Integer> inverse;
    private Map<Integer, String> inverse2;

    private final String[] strings = {"Hello", "World", "How", "Is", "It", "Going", "?"};

    @Before
    public void setUp() {
        map = new BidirectionalHashMap<>(0.5f);
        map2 = new BidirectionalHashMap<>(0.5f);
        inverse = map.inverse();
        inverse2 = map2.inverse();
    }

    @Test
    public void testInit() {
        map = new BidirectionalHashMap<>();
        map2 = new BidirectionalHashMap<>(16);
        map2.getCurrentLoadFactor();
    }

    @Test
    public void testSize() {
        for (int i = 0; i < strings.length; ++i) {
            assertEquals(i, map.size());
            map.put(i, strings[i]);
            assertEquals(i + 1, map.size());
        }

        for (int i = strings.length - 1; i >= 0; --i) {
            assertEquals(i + 1, map.size());
            map.remove(i);
            assertEquals(i, map.size());
        }
    }

    @Test
    public void testIsEmpty() {
        assertTrue(map.isEmpty());

        for (int i = 0; i < 3; ++i) {
            map.put(i, strings[i]);
            assertFalse(map.isEmpty());
        }
    }

    @Test
    public void testInverse() {
        assertNotNull(map.inverse());
    }

    @Test
    public void testContainsKey() {
        assertFalse(map.containsKey(1));
        map.put(1, "yeah");
        assertTrue(map.containsKey(1));
        map.put(2, "yep");
        map.remove(1);
        assertFalse(map.containsKey(1));
        assertTrue(map.containsKey(2));
        map.remove(2);
        assertFalse(map.containsKey(2));
    }

    @Test
    public void testContainsValue() {
        map.put(10, "Come");
        map.put(20, "on");
        assertTrue(map.containsValue("Come"));
        assertTrue(map.containsValue("on"));
        map.remove(10);
        assertFalse(map.containsValue("Come"));
        assertTrue(map.containsValue("on"));
        map.remove(20);
        assertFalse(map.containsValue("on"));

        map.put(50, "50");
        assertTrue(map.containsValue("50"));
        map.put(50, "51");
        assertFalse(map.containsValue("50"));
    }

    @Test
    public void testGet() {
        map.put(100, "100");
        map.put(200, "200");

        assertEquals(map.get(100), "100");
        assertEquals(map.get(200), "200");
        assertEquals(map.get(300), null);
        map.put(200, "blah");

        assertEquals(map.get(200), "blah");
    }

    @Test
    public void testPut() {
        map.put(1000, "1000");
        assertTrue(map.containsKey(1000));
        assertTrue(map.containsValue("1000"));

        assertFalse(map.containsKey(1001));
        assertFalse(map.containsValue("1001"));

        map.remove(1000);

        assertFalse(map.containsKey(1000));
        assertFalse(map.containsValue("1000"));

        assertNull(map.put(2000, "2000"));
        assertEquals("2000", map.put(2000, "2001"));
        assertEquals("2001", map.put(2000, "2002"));

        map.put(20, "A");
        assertEquals("A", map.get(20));
        assertEquals("A", map.put(20, "B"));
        assertEquals("B", map.remove(20));
    }

    @Test
    public void testRemove() {
        assertNull(map.remove(1000));

        for (int i = 0; i < strings.length; ++i) {
            assertNull(map.put(i, strings[i]));
        }

        for (int i = 0; i < strings.length; ++i) {
            assertEquals(strings[i], map.remove(i));
        }
    }

    @Test
    public void testPutAll() {
        for (int i = 0; i < strings.length; ++i) {
            map.put(i + 10, strings[i]);
        }

        Map<Integer, String> addMap = new TreeMap<>();
        addMap.put(11, strings[1]);
        addMap.put(14, strings[4]);

        assertEquals(strings.length, map.size());

        addMap.put(100, "100");

        map.putAll(addMap);
        assertEquals(strings.length + 1, map.size());
    }

    @Test
    public void testClear() {
        assertTrue(map.isEmpty());

        for (int i = 0; i < strings.length; ++i) {
            map.put(i, strings[i]);
            assertFalse(map.isEmpty());
        }

        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void testKeySet() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");
        map.put(5, "five");

        Set<Integer> keySet = map.keySet();

        int num = 1;

        for (Integer i : keySet) {
            assertEquals(Integer.valueOf(num++), i);
        }

        Iterator<Integer> iterator = keySet.iterator();

        try {
            iterator.remove();
            fail("iterator should have thrown IllegalStateException!");
        } catch (IllegalStateException ex) {

        }

        assertTrue(iterator.hasNext());
        // Remove 1, 3 and 5:
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.next();
        iterator.remove();

        iterator = keySet.iterator();

        // Check that 2 and 4 are still in the key set:
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(4), iterator.next());
        assertFalse(iterator.hasNext());

        try {
            iterator.next();
            fail("BidirectionalHashMap should throw NoSuchElementException here!");
        } catch (NoSuchElementException ex) {

        }
    }

    @Test(expected = IllegalStateException.class)
    public void testKeySetIteratorThrowsOnConcurrentModificationWhenFirstRemoving() {
        map.put(1, "1");
        map.put(4, "4");
        map.put(7, "7");

        Iterator<Integer> keySetIterator = map.keySet().iterator();
        keySetIterator.remove();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testKeySetIteratorThrowsOnConcurrentModificationWhenIterating() {
        map.put(1, "1");
        map.put(4, "4");
        map.put(7, "7");

        Iterator<Integer> keySetIterator = map.keySet().iterator();
        map.put(1000, "1000");
        keySetIterator.next();
    }

    @Test
    public void testValues() {
        try {
            map.values();
        } catch (UnsupportedOperationException expected) {
            //expected
        }
    }

    @Test
    public void testMappingSetValue() {
        Mapping<Integer, String> keyPair = new Mapping<>(1, "nope");
        try {
            keyPair.setValue("diy");
            fail("UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            //expected
        }
    }

    @Test
    public void testMappingEqualNull() {
        Mapping<Integer, String> keyPair = new Mapping<>(1, "nope");
        assertFalse(keyPair.equals(null));
    }

    @Test
    public void testMappingEqualOtherClass() {
        Mapping<Integer, String> keyPair = new Mapping<>(1, "nope");
        Map<String, String> keyPair2 = new HashMap<String, String>();
        assertFalse(keyPair.equals(keyPair2));
    }

    @Test
    public void testEntrySet() {
        for (int i = 0; i < strings.length; ++i) {
            map2.put(strings[i], i);
        }

        int index = 0;

        for (Map.Entry<String, Integer> e : map2.entrySet()) {
            assertEquals(strings[index], e.getKey());
            assertEquals(Integer.valueOf(index), e.getValue());
            index++;
        }
    }

    @Test
    public void testCompact() {
        BidirectionalHashMap<Integer, String> myMap =
                new BidirectionalHashMap<>(0.5f);

        for (int i = 0; i < 100; ++i) {
            myMap.put(i, "" + i);
        }

        for (int i = 10; i < 100; ++i) {
            myMap.remove(i);
        }

        myMap.compact();

        assertEquals(10, myMap.size());

        for (int i = 0; i < 10; ++i) {
            assertEquals("" + i, myMap.get(i));
        }
    }

    @Test
    public void testKeySetSize() {
        for (int i = 0; i < strings.length; ++i) {
            assertEquals(i, map.size());
            map.put(i, strings[i]);
            assertEquals(i + 1, map.size());
        }
    }

    @Test
    public void testKeySetIsEmpty() {
        assertTrue(map.isEmpty());

        for (int i = 0; i < 3; ++i) {
            map.put(i, strings[i]);
            assertFalse(map.keySet().isEmpty());
        }

        for (int i = 0; i < 3; ++i) {
            assertFalse(map.keySet().isEmpty());
            map.remove(i);
        }
        assertTrue(map.keySet().isEmpty());
        assertTrue(map.isEmpty());
    }

    @Test
    public void testKeySetContains() {
        for (int i = 2; i < 5; ++i) {
            assertFalse(map.containsKey(i));
        }

        for (int i = 2; i < 5; ++i) {
            assertNull(map.put(i, strings[i]));
        }

        for (int i = 2; i < 5; ++i) {
            assertTrue(map.containsKey(i));
        }
    }

    @Test
    public void testKeySetToArray() {
        Object[] arr = map.keySet().toArray();

        assertEquals(0, arr.length);

        for (int test = 0; test < 4; ++test) {
            map.clear();

            for (int i = 0; i <= test; ++i) {
                map.put(i, strings[i]);
            }

            assertEquals(test + 1, map.size());
            arr = map.keySet().toArray();
            assertEquals(test + 1, arr.length);

            Iterator<Integer> iterator = map.keySet().iterator();

            for (int index = 0; index < map.keySet().size(); ++index) {
                assertEquals(iterator.next(), arr[index]);
            }
        }
    }

    @Test
    public void testKeySetGenericToArray() {
        Integer[] arr = new Integer[3];
        Integer[] arrRet = null;

        for (int i = 0; i < 3; ++i) {
            map.put(i, strings[i]);
        }

        arrRet = map.keySet().toArray(arr);

        assertTrue(arrRet == arr);

        Iterator<Integer> iterator = map.keySet().iterator();

        for (int i = 0; i < 3; ++i) {
            assertEquals(iterator.next(), arr[i]);
        }

        map.put(3, strings[3]);
        arrRet = map.keySet().toArray(arr);

        assertNotNull(arrRet);
        assertTrue(arrRet != arr);

        iterator = map.keySet().iterator();

        for (int i = 0; i < 3; ++i) {
            assertEquals(iterator.next(), arr[i]);
        }

        assertTrue(arr != arrRet);
        iterator = map.keySet().iterator();

        for (int i = 0; i < map.keySet().size(); ++i) {
            assertEquals(iterator.next(), arrRet[i]);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testKeySetAddThrows() {
        map.keySet().add(1);
    }

    @Test
    public void testKeySetRemove() {
        Set<Integer> keySet = map.keySet();
        assertFalse(keySet.remove(1));
        map.put(1, "yea");
        assertTrue("yea", keySet.remove(1));
        assertFalse(keySet.remove(10));
        assertFalse("yea", keySet.remove(1));
    }

    @Test
    public void testKeySetContainsAll() {
        for (int i = 0; i < strings.length; ++i) {
            map.put(i, strings[i]);
        }

        List<Integer> aux = Arrays.asList(1, 4, 5);

        assertTrue(map.keySet().containsAll(aux));

        aux = Arrays.asList();

        assertTrue(map.keySet().containsAll(aux));

        aux = Arrays.asList(1, 2, -1);

        assertFalse(map.keySet().containsAll(aux));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testKeySetAddAll() {
        map.keySet().addAll(Arrays.asList());
    }

    @Test
    public void testKeySetRetainAll() {
        for (int i = 1; i < 6; ++i) {
            map.put(i, strings[i]);
        }

        Collection<Integer> retainCol = new ArrayList<>();

        for (int i = 0; i < 7; ++i) {
            retainCol.add(i);
        }

        assertFalse(map.keySet().retainAll(retainCol));

        retainCol.remove(2);

        assertTrue(map.keySet().retainAll(retainCol));

        retainCol.clear();

        assertTrue(map.keySet().retainAll(retainCol));

        assertFalse(map.keySet().retainAll(retainCol));
    }

    @Test
    public void testKeySetRemoveAll() {
        List<Integer> aux = new ArrayList<>();

        assertFalse(map.keySet().removeAll(aux));

        for (int i = 0; i < strings.length; ++i) {
            map.put(i, strings[i]);
        }

        assertFalse(map.keySet().removeAll(aux));

        aux.add(-1);
        assertFalse(map.keySet().removeAll(aux));

        aux.add(1);
        aux.add(2);

        assertTrue(map.keySet().removeAll(aux));

        assertEquals(strings.length - 2, map.keySet().size());
    }

    @Test
    public void testKeySetClear() {
        assertEquals(0, map.keySet().size());

        for (int i = 0; i < strings.length; ++i) {
            map.put(i, strings[i]);
            assertEquals(i + 1, map.keySet().size());
        }

        map.keySet().clear();
        assertTrue(map.isEmpty());
        assertTrue(map.keySet().isEmpty());
        assertEquals(0, map.size());
        assertEquals(0, map.keySet().size());
    }

    @Test
    public void testEntrySetSize() {
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();

        for (int i = 0; i < strings.length; ++i) {
            assertEquals(i, entrySet.size());
            entrySet.add(new Mapping<>(i, strings[i]));
            assertEquals(i + 1, entrySet.size());
        }

        entrySet.remove(new Mapping<>(2, "yeah"));
        assertEquals(strings.length - 1, entrySet.size());
    }

    @Test
    public void testEntrySetIsEmpty() {
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();

        assertTrue(entrySet.isEmpty());

        for (int i = 0; i < strings.length; ++i) {
            entrySet.add(new Mapping<>(i, strings[i]));
            assertFalse(entrySet.isEmpty());
        }

        entrySet.clear();

        assertTrue(entrySet.isEmpty());
    }

    @Test
    public void testEntrySetContains() {
        for (int i = 0; i < strings.length; ++i) {
            map.put(i, strings[i]);
        }

        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        Mapping<Integer, String> keyPair = new Mapping<>(1, "nope");
        assertFalse(entrySet.contains(keyPair));

        keyPair = new Mapping<>(2, "World");
        assertFalse(entrySet.contains(keyPair));

        for (int i = 0; i < strings.length; ++i) {
            keyPair = new Mapping<>(i, strings[i]);
            assertTrue(entrySet.contains(keyPair));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testEntrySetIteratorThrowsOnConcurrentModificationWhenFirstRemoving() {
        map.put(1, "1");
        map.put(4, "4");
        map.put(7, "7");

        Iterator<Map.Entry<Integer, String>> entrySetIterator =
                map.entrySet().iterator();

        entrySetIterator.remove();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testEntrySetIteratorThrowsOnConcurrentModificationWhenIterating() {
        map.put(1, "1");
        map.put(4, "4");
        map.put(7, "7");

        Iterator<Map.Entry<Integer, String>> entrySetIterator =
                map.entrySet().iterator();
        map.put(1000, "1000");
        entrySetIterator.next();
    }

    @Test
    public void testEntrySetIterator() {
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");
        map.put(5, "five");

        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        Iterator<Map.Entry<Integer, String>> iterator = entrySet.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(new Mapping<>(1, "one"), iterator.next());
        assertEquals(5, entrySet.size());

        iterator.remove();

        assertEquals(4, entrySet.size());
        assertTrue(iterator.hasNext());
        assertEquals(new Mapping<>(2, "two"), iterator.next());

        assertEquals(4, entrySet.size());

        assertTrue(iterator.hasNext());
        assertEquals(new Mapping<>(3, "three"), iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(new Mapping<>(4, "four"), iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(new Mapping<>(5, "five"), iterator.next());

        assertFalse(iterator.hasNext());

        iterator.remove();

        try {
            iterator.remove();
            fail("Iterator should have thrown IllegalStateException!");
        } catch (IllegalStateException ex) {

        }

        try {
            iterator.next();
            fail("Iterator should have thrown NoSuchElementException!");
        } catch (NoSuchElementException ex) {

        }
    }

    @Test
    public void testEntrySetToArray() {
        loadMap();
        Object[] arr = map.entrySet().toArray();

        assertEquals(strings.length, arr.length);
        Iterator<Map.Entry<Integer, String>> iterator =
                map.entrySet().iterator();

        for (int i = 0; i < strings.length; ++i) {
            assertEquals(iterator.next(), arr[i]);
        }
    }

    @Test
    public void testEntrySetGenericToArray() {
        loadMap();
        Map.Entry<Integer, String>[] arr = new Map.Entry[strings.length - 1];
        Map.Entry<Integer, String>[] retArr;

        retArr = map.entrySet().toArray(arr);
        assertFalse(retArr == arr);
        Iterator<Map.Entry<Integer, String>> iterator =
                map.entrySet().iterator();

        for (int i = 0; i < retArr.length; ++i) {
            Map.Entry<Integer, String> entry =
                    (Map.Entry<Integer, String>) retArr[i];
            assertEquals(iterator.next(), entry);
        }

        arr = new Map.Entry[strings.length];
        retArr = map.entrySet().toArray(arr);
        assertTrue(retArr == arr);
        iterator = map.entrySet().iterator();

        for (int i = 0; i < retArr.length; ++i) {
            Map.Entry<Integer, String> entry =
                    (Map.Entry<Integer, String>) retArr[i];
            assertEquals(iterator.next(), entry);
        }
    }

    @Test
    public void testEntrySetThrowsOnNullGenericArray() {
        String[] a = null;
        try {
            map.entrySet().toArray(a);
            fail("NullPointerException");
        } catch (NullPointerException expected) {
            //expected
        }
    }

    @Test
    public void testKeySetThrowsOnNullGenericArray() {
        String[] a = null;
        try {
            map.keySet().toArray(a);
            fail("NullPointerException");
        } catch (NullPointerException expected) {
            //expected
        }
    }

    @Test
    public void testEntrySetAdd() {
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();

        for (int i = 0; i < strings.length; ++i) {
            assertTrue(entrySet.add(new Mapping<>(i, strings[i])));
        }

        for (int i = 0; i < strings.length; ++i) {
            // No duplicates allowed.
            assertFalse(entrySet.add(new Mapping<>(i, strings[i])));
        }

        assertTrue(entrySet.add(new Mapping<>(1, "new one")));
        assertFalse(entrySet.add(new Mapping<>(1, "new one")));
        assertTrue(entrySet.add(new Mapping<>(-1, "yo!")));
        assertFalse(entrySet.add(new Mapping<>(-1, "yo!")));
        assertEquals("new one", map.get(Integer.valueOf(1)));
    }

    @Test
    public void testEntrySetRemove() {
        loadMap();
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();

        for (int i = 0; i < strings.length; ++i) {
            assertTrue(entrySet.remove(new Mapping<>(i, strings[i])));
        }

        assertFalse(entrySet.remove(new Mapping<>(0, "none")));
        assertFalse(entrySet.remove(new Mapping<>(-1, "Hello")));

        for (int i = 0; i < strings.length; ++i) {
            // Cannot remove twice.
            assertFalse(entrySet.remove(new Mapping<>(i, strings[i])));
        }
    }

    @Test
    public void testEntrySetContainsAll() {
        for (int i = 0; i <= 5; ++i) {
            map.put(i, "" + i);
        }

        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        Set<Map.Entry<Integer, String>> set = new HashSet<>();

        set.add(new Mapping<>(1, "1"));
        set.add(new Mapping<>(2, "2"));
        set.add(new Mapping<>(3, "3"));
        set.add(new Mapping<>(4, "4"));

        assertTrue(entrySet.containsAll(set));

        set.add(new Mapping<>(-1, "1"));

        assertFalse(entrySet.containsAll(set));

        set.clear();
        assertTrue(entrySet.containsAll(set));
    }

    @Test
    public void testEntrySetAddAll() {
        Set<Map.Entry<Integer, String>> set = new HashSet<>();
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();

        assertFalse(entrySet.addAll(set));
        set.add(new Mapping<>(1, "1"));

        assertTrue(entrySet.addAll(set));
        assertEquals(1, entrySet.size());
        assertFalse(entrySet.addAll(set));

        set.add(new Mapping<>(2, "2"));
        set.add(new Mapping<>(3, "3"));

        assertTrue(entrySet.addAll(set));
        assertFalse(entrySet.addAll(set));

        assertTrue(entrySet.contains(new Mapping<>(1, "1")));
        assertTrue(entrySet.contains(new Mapping<>(2, "2")));
        assertTrue(entrySet.contains(new Mapping<>(3, "3")));
        assertFalse(entrySet.contains(new Mapping<>(2, "-2")));
    }

    @Test
    public void testEntrySetRetainAll() {
        loadMap();
        Set<Map.Entry<Integer, String>> set = new HashSet<>();
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();

        for (int i = 0; i < strings.length; ++i) {
            set.add(new Mapping<>(i, strings[i]));
        }

        assertFalse(entrySet.retainAll(set));
        assertTrue(entrySet.contains(new Mapping<>(1, "World")));
        set.remove(new Mapping<>(1, "World"));
        assertTrue(entrySet.retainAll(set));
        assertFalse(entrySet.contains(new Mapping<>(1, "World")));

        assertEquals(strings.length - 1, entrySet.size());

        assertFalse(entrySet.contains(new Mapping<>(1, "World")));
        assertTrue(entrySet.contains(new Mapping<>(4, "It")));
        set.remove(new Mapping<>(4, "It"));
        assertTrue(entrySet.retainAll(set));
        assertFalse(entrySet.retainAll(set));
        assertFalse(entrySet.contains(new Mapping<>(1, "World")));
        assertFalse(entrySet.contains(new Mapping<>(4, "It")));
    }

    @Test
    public void testEntrySetRemoveAll() {
        loadMap();
        Set<Map.Entry<Integer, String>> set = new HashSet<>();
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();

        assertFalse(entrySet.removeAll(set));

        set.add(new Mapping<>(1, "World"));

        assertTrue(entrySet.removeAll(set));
        assertFalse(entrySet.removeAll(set));

        set.add(new Mapping<>(100, "oh yeah"));

        assertFalse(entrySet.removeAll(set));
        assertEquals(strings.length - 1, entrySet.size());

        for (int i = 0; i < strings.length; ++i) {
            set.add(new Mapping<>(i, strings[i]));
        }

        assertTrue(entrySet.removeAll(set));
        assertFalse(entrySet.removeAll(set));
        assertTrue(entrySet.isEmpty());
    }

    @Test
    public void testEntrySetClear() {
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        assertEquals(0, entrySet.size());

        for (int i = 0; i < strings.length; ++i) {
            assertEquals(i, entrySet.size());
            entrySet.add(new Mapping<>(i, strings[i]));
            assertEquals(i + 1, entrySet.size());
        }

        entrySet.clear();
        assertEquals(0, entrySet.size());
    }

    @Test
    public void testInverseMapSize() {

        for (int i = 0; i < strings.length; ++i) {
            assertEquals(i, inverse.size());
            inverse.put(strings[i], i);
            assertEquals(i + 1, inverse.size());
        }
    }

    @Test
    public void testInverseMapIsEmpty() {
        assertTrue(inverse.isEmpty());

        for (int i = 0; i < strings.length; ++i) {
            assertNull(inverse.put(strings[i], i));
            assertFalse(inverse.isEmpty());
        }
    }

    @Test
    public void testInverseMapContainsKey() {
        loadMap();
        assertTrue(inverse.containsKey("World"));
        assertTrue(inverse.containsKey("Hello"));
        assertFalse(inverse.containsKey("no"));
        assertFalse(inverse.containsKey("neither"));
    }

    @Test
    public void testInverseMapContainsValue() {
        loadMap();
        assertTrue(inverse.containsValue(0));
        assertTrue(inverse.containsValue(1));
        assertFalse(inverse.containsValue(-1));
    }

    @Test
    public void testInverseMapGet() {
        loadMap();

        for (int i = 0; i < strings.length; ++i) {
            assertEquals(Integer.valueOf(i), inverse.get(strings[i]));
        }

        assertNull(inverse.get("Funky"));
        assertNull(inverse.get("not quite"));
    }

    @Test
    public void testInverseMapPut() {
        assertNull(inverse.put("Hey", 1));
        assertNull(inverse.put("yo!", 2));

        assertEquals(Integer.valueOf(1), inverse.put("Hey", 11));
        assertEquals(Integer.valueOf(2), inverse.put("yo!", 12));
        assertEquals(Integer.valueOf(12), inverse.put("yo!", 12));

        assertEquals(Integer.valueOf(12), inverse.remove("yo!"));
        assertEquals(Integer.valueOf(11), inverse.remove("Hey"));
    }

    @Test
    public void testInverseMapPutAll() {
        for (int i = 0; i < strings.length; ++i) {
            inverse2.put(i + 10, strings[i]);
        }

        Map<Integer, String> addMap = new TreeMap<>();
        addMap.put(11, strings[1]);
        addMap.put(14, strings[4]);

        assertEquals(strings.length, inverse2.size());

        addMap.put(100, "100");

        inverse2.putAll(addMap);
        assertEquals(strings.length + 1, inverse2.size());
    }

    @Test
    public void testInverseMapValues() {
        try {
            inverse.values();
            fail("UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            //expected
        }
    }

    @Test
    public void testInverseMapEntrySet() {
        loadMap();
        try {
            inverse.entrySet();
            fail("UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            //expected
        }
    }

    @Test
    public void testInverseMapClear() {
        loadMap();
        inverse.clear();
    }

    @Test
    public void testInverseMapKeySetSize() {
        for (int i = 0; i < strings.length; ++i) {
            assertEquals(i, inverse2.size());
            inverse2.put(i, strings[i]);
            assertEquals(i + 1, inverse2.size());
        }
    }

    @Test
    public void testInverseMapKeySetIsEmpty() {
        assertTrue(inverse2.isEmpty());

        for (int i = 0; i < 3; ++i) {
            inverse2.put(i, strings[i]);
            assertFalse(inverse2.keySet().isEmpty());
        }

        for (int i = 0; i < 3; ++i) {
            assertFalse(inverse2.keySet().isEmpty());
            inverse2.remove(i);
        }
        assertTrue(inverse2.keySet().isEmpty());
        assertTrue(inverse2.isEmpty());
    }

    @Test
    public void testInverseMapKeySetContains() {
        for (int i = 2; i < 5; ++i) {
            assertFalse(inverse.containsKey(i));
        }

        for (int i = 2; i < 5; ++i) {
            assertNull(inverse2.put(i, strings[i]));
        }

        for (int i = 2; i < 5; ++i) {
            assertTrue(inverse2.containsKey(i));
        }
    }

    @Test
    public void testInverseMapKeySetToArray() {
        Object[] arr = inverse2.keySet().toArray();

        assertEquals(0, arr.length);

        for (int test = 0; test < 4; ++test) {
            inverse2.clear();

            for (int i = 0; i <= test; ++i) {
                inverse2.put(i, strings[i]);
            }

            assertEquals(test + 1, inverse2.size());
            arr = inverse2.keySet().toArray();
            assertEquals(test + 1, arr.length);

            Iterator<Integer> iterator = inverse2.keySet().iterator();

            for (int index = 0; index < inverse2.keySet().size(); ++index) {
                assertEquals(iterator.next(), arr[index]);
            }
        }
    }

    @Test
    public void testInverseMapKeySetGenericToArray() {
        Integer[] arr = new Integer[3];
        Integer[] arrRet = null;

        for (int i = 0; i < 3; ++i) {
            inverse2.put(i, strings[i]);
        }

        arrRet = inverse2.keySet().toArray(arr);

        assertTrue(arrRet == arr);

        Iterator<Integer> iterator = inverse2.keySet().iterator();

        for (int i = 0; i < 3; ++i) {
            assertEquals(iterator.next(), arr[i]);
        }

        inverse2.put(3, strings[3]);
        arrRet = inverse2.keySet().toArray(arr);

        assertNotNull(arrRet);
        assertTrue(arrRet != arr);

        iterator = inverse2.keySet().iterator();

        for (int i = 0; i < 3; ++i) {
            assertEquals(iterator.next(), arr[i]);
        }

        assertTrue(arr != arrRet);
        iterator = inverse2.keySet().iterator();

        for (int i = 0; i < inverse2.keySet().size(); ++i) {
            assertEquals(iterator.next(), arrRet[i]);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInverseMapKeySetAddThrows() {
        inverse2.keySet().add(1);
    }

    @Test
    public void testInverseMapKeySetRemove() {
        Set<Integer> keySet = inverse2.keySet();
        assertFalse(keySet.remove(1));
        inverse2.put(1, "yea");
        assertTrue("yea", keySet.remove(1));
        assertFalse(keySet.remove(10));
    }

    @Test
    public void testInverseMapKeySetContainsAll() {
        for (int i = 0; i < strings.length; ++i) {
            inverse2.put(i, strings[i]);
        }

        List<Integer> aux = Arrays.asList(1, 4, 5);

        assertTrue(inverse2.keySet().containsAll(aux));

        aux = Arrays.asList();

        assertTrue(inverse2.keySet().containsAll(aux));

        aux = Arrays.asList(1, 2, -1);

        assertFalse(inverse2.keySet().containsAll(aux));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInverseMapKeySetAddAll() {
        inverse2.keySet().addAll(Arrays.asList());
    }

    @Test
    public void testInverseMapKeySetRetainAll() {
        for (int i = 1; i < 6; ++i) {
            inverse2.put(i, strings[i]);
        }

        Collection<Integer> retainCol = new ArrayList<>();

        for (int i = 0; i < 7; ++i) {
            retainCol.add(i);
        }

        assertFalse(inverse2.keySet().retainAll(retainCol));

        retainCol.remove(2);

        assertTrue(inverse2.keySet().retainAll(retainCol));

        retainCol.clear();

        assertTrue(inverse2.keySet().retainAll(retainCol));

        assertFalse(inverse2.keySet().retainAll(retainCol));
    }

    @Test
    public void testInverseMapKeySetRemoveAll() {
        List<Integer> aux = new ArrayList<>();

        try {
            assertFalse(inverse2.keySet().removeAll(aux));
            fail("UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            //expected
        }


    }

    @Test
    public void testInverseMapKeySetClear() {
        assertEquals(0, inverse2.keySet().size());

        for (int i = 0; i < strings.length; ++i) {
            inverse2.put(i, strings[i]);
            assertEquals(i + 1, inverse2.keySet().size());
        }

        inverse2.keySet().clear();
        assertTrue(inverse2.isEmpty());
        assertTrue(inverse2.keySet().isEmpty());
        assertEquals(0, inverse2.size());
        assertEquals(0, inverse2.keySet().size());
    }

    @Test
    public void testInverseMapKeySetIteratorNext() {
        try {
            inverse2.keySet().iterator().next();
            fail("NoSuchElementException");
        } catch (NoSuchElementException expected)
        {
            //expected
        }
    }

    @Test
    public void testInverseMapKeySetIteratorRemove() {
        try {
            inverse2.keySet().iterator().remove();
            fail("IllegalStateException");
        } catch (IllegalStateException expected)
        {
            //expected
        }
    }


    private void loadMap() {
        for (int i = 0; i < strings.length; ++i) {
            map.put(i, strings[i]);
        }
    }
}
