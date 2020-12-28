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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.SetValuedMap;

/**
 * Test HashSetValuedHashMap
 *
 * @since 4.1
 */
public class HashSetValuedHashMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    public HashSetValuedHashMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(HashSetValuedHashMapTest.class);
    }

    // -----------------------------------------------------------------------
    @Override
    public SetValuedMap<K, V> makeObject() {
        return new HashSetValuedHashMap<>();
    }

    @Override
    public MultiValuedMap<K, V> makeConfirmedMap() {
        return new HashSetValuedHashMap<>();
    }

    @Override
    public boolean isHashSetValue() {
        return true;
    }

    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testSetValuedMapAdd() {
        final SetValuedMap<K, V> setMap = makeObject();
        assertTrue(setMap.get((K) "whatever") instanceof Set);

        final Set<V> set = setMap.get((K) "A");
        assertTrue(set.add((V) "a1"));
        assertTrue(set.add((V) "a2"));
        assertFalse(set.add((V) "a1"));
        assertEquals(2, setMap.size());
        assertTrue(setMap.containsKey("A"));
    }

    @SuppressWarnings("unchecked")
    public void testSetValuedMapRemove() {
        final SetValuedMap<K, V> setMap = makeObject();
        assertTrue(setMap.get((K) "whatever") instanceof Set);

        final Set<V> set = setMap.get((K) "A");
        assertTrue(set.add((V) "a1"));
        assertTrue(set.add((V) "a2"));
        assertFalse(set.add((V) "a1"));
        assertEquals(2, setMap.size());
        assertTrue(setMap.containsKey("A"));

        assertTrue(set.remove("a1"));
        assertTrue(set.remove("a2"));
        assertFalse(set.remove("a1"));

        assertEquals(0, setMap.size());
        assertFalse(setMap.containsKey("A"));
    }

    @SuppressWarnings("unchecked")
    public void testSetValuedMapRemoveViaIterator() {
        final SetValuedMap<K, V> setMap = makeObject();
        assertTrue(setMap.get((K) "whatever") instanceof Set);

        final Set<V> set = setMap.get((K) "A");
        set.add((V) "a1");
        set.add((V) "a2");
        set.add((V) "a1");

        final Iterator<V> it = set.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        assertEquals(0, setMap.size());
        assertFalse(setMap.containsKey("A"));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testSetValuedMapEqualsHashCodeContract() {
        final SetValuedMap map1 = makeObject();
        final SetValuedMap map2 = makeObject();

        map1.put("a", "a1");
        map1.put("a", "a2");
        map2.put("a", "a2");
        map2.put("a", "a1");
        assertEquals(map1, map2);
        assertEquals(map1.hashCode(), map2.hashCode());

        map2.put("a", "a2");
        assertEquals(map1, map2);
        assertEquals(map1.hashCode(), map2.hashCode());

        map2.put("a", "a3");
        assertNotSame(map1, map2);
        assertNotSame(map1.hashCode(), map2.hashCode());
    }

    public void testHashSetValueHashMap() {
        final SetValuedMap<K, V> setMap = new HashSetValuedHashMap<>(4);
        assertEquals(0, setMap.get((K) "whatever").size());

        final Set<V> set = setMap.get((K) "A");
        set.add((V) "W");
        set.add((V) "X");
        set.add((V) "F");
        assertEquals(3, setMap.get((K) "A").size());
    }

    public void testHashSetValueHashMap_1() {
        final MultiValuedMap<K, V> map = new ArrayListValuedHashMap<>();
        final SetValuedMap<K, V> map1;
        final SetValuedMap<K, V> map2 = makeObject();
        final SetValuedMap<K, V> map3;

        map.put((K) "A", (V) "W");
        map.put((K) "A", (V) "X");
        map.put((K) "A", (V) "F");
        map1 = new HashSetValuedHashMap<>(map);
        assertEquals(3, map1.get((K) "A").size());
        map2.put((K) "A", (V) "X");
        map2.put((K) "A", (V) "F");
        map2.put((K) "A", (V) "W");
        assertEquals(map1, map2);
        assertEquals(map1.hashCode(), map2.hashCode());

        map.remove("A");
        map3 = new HashSetValuedHashMap<>(map);
        assertEquals("{}", map3.toString());
    }

    public void testHashSetValuedHashMap_2(){
        final Map<K, V> map = new HashMap<>();
        final SetValuedMap<K, V> map1;
        final SetValuedMap<K, V> map2;

        map.put((K) "A", (V) "W");
        map.put((K) "B", (V) "X");
        map.put((K) "C", (V) "F");
        map1 = new HashSetValuedHashMap<>(map);
        assertEquals(1, map1.get((K) "A").size());

        map.remove("A");
        map.remove("B");
        map.remove("C");
        map2 = new HashSetValuedHashMap<>(map);
        assertEquals("{}", map2.toString());
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/HashSetValuedHashMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/HashSetValuedHashMap.fullCollection.version4.1.obj");
//    }

}
