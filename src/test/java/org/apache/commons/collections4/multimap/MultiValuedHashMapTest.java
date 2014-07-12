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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.SetValuedMap;

/**
 * Test MultValuedHashMap
 *
 * @since 4.1
 * @version $Id$
 */
public class MultiValuedHashMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    public MultiValuedHashMapTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(MultiValuedHashMapTest.class);
    }

    @Override
    public MultiValuedMap<K, V> makeObject() {
        final MultiValuedMap<K, V> m = new MultiValuedHashMap<K, V>();
        return m;
    }

    @SuppressWarnings("unchecked")
    public void testSetValuedMapAdd() {
        final SetValuedMap<K, V> setMap = MultiValuedHashMap.setValuedHashMap();
        assertTrue(setMap.get("whatever") instanceof Set);

        Set<V> set = setMap.get("A");
        assertTrue(set.add((V) "a1"));
        assertTrue(set.add((V) "a2"));
        assertFalse(set.add((V) "a1"));
        assertEquals(2, setMap.size());
        assertTrue(setMap.containsKey("A"));
    }

    @SuppressWarnings("unchecked")
    public void testSetValuedMapRemove() {
        final SetValuedMap<K, V> setMap = MultiValuedHashMap.setValuedHashMap();
        assertTrue(setMap.get("whatever") instanceof Set);

        Set<V> set = setMap.get("A");
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
        final SetValuedMap<K, V> setMap = MultiValuedHashMap.setValuedHashMap();
        assertTrue(setMap.get("whatever") instanceof Set);

        Set<V> set = setMap.get("A");
        set.add((V) "a1");
        set.add((V) "a2");
        set.add((V) "a1");

        Iterator<V> it = set.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        assertEquals(0, setMap.size());
        assertFalse(setMap.containsKey("A"));
    }

    @SuppressWarnings("unchecked")
    public void testListValuedMapAdd() {
        final ListValuedMap<K, V> listMap = MultiValuedHashMap.listValuedHashMap();
        assertTrue(listMap.get("whatever") instanceof List);
        List<V> list = listMap.get("A");
        list.add((V) "a1");
        assertEquals(1, listMap.size());
        assertTrue(listMap.containsKey("A"));
    }

    @SuppressWarnings("unchecked")
    public void testListValuedMapAddViaListIterator() {
        final ListValuedMap<K, V> listMap = MultiValuedHashMap.listValuedHashMap();
        ListIterator<V> listIt = listMap.get("B").listIterator();
        assertFalse(listIt.hasNext());
        listIt.add((V) "b1");
        listIt.add((V) "b2");
        listIt.add((V) "b3");
        assertEquals(3, listMap.size());
        assertTrue(listMap.containsKey("B"));
        // As ListIterator always adds before the current cursor
        assertFalse(listIt.hasNext());
    }

    @SuppressWarnings("unchecked")
    public void testListValuedMapRemove() {
        final ListValuedMap<K, V> listMap = MultiValuedHashMap.listValuedHashMap();
        List<V> list = listMap.get("A");
        list.add((V) "a1");
        list.add((V) "a2");
        list.add((V) "a3");
        assertEquals(3, listMap.size());
        assertEquals("a1", list.remove(0));
        assertEquals(2, listMap.size());
        assertEquals("a2", list.remove(0));
        assertEquals(1, listMap.size());
        assertEquals("a3", list.remove(0));
        assertEquals(0, listMap.size());
        assertFalse(listMap.containsKey("A"));
    }

    @SuppressWarnings("unchecked")
    public void testListValuedMapRemoveViaListIterator() {
        final ListValuedMap<K, V> listMap = MultiValuedHashMap.listValuedHashMap();
        ListIterator<V> listIt = listMap.get("B").listIterator();
        listIt.add((V) "b1");
        listIt.add((V) "b2");
        assertEquals(2, listMap.size());
        assertTrue(listMap.containsKey("B"));
        listIt = listMap.get("B").listIterator();
        while (listIt.hasNext()) {
            listIt.next();
            listIt.remove();
        }
        assertFalse(listMap.containsKey("B"));
        listIt.add((V) "b1");
        listIt.add((V) "b2");
        assertTrue(listMap.containsKey("B"));
        assertEquals(2, listMap.get("B").size());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testEqualsHashCodeContract() {
        MultiValuedMap map1 = new MultiValuedHashMap();
        MultiValuedMap map2 = new MultiValuedHashMap();

        map1.put("a", "a1");
        map1.put("a", "a2");
        map2.put("a", "a2");
        map2.put("a", "a1");
        assertEquals(map1, map2);
        assertEquals(map1.hashCode(), map2.hashCode());

        map2.put("a", "a2");
        assertNotSame(map1, map2);
        assertNotSame(map1.hashCode(), map2.hashCode());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testListValuedMapEqualsHashCodeContract() {
        ListValuedMap map1 = MultiValuedHashMap.listValuedHashMap();
        ListValuedMap map2 = MultiValuedHashMap.listValuedHashMap();

        map1.put("a", "a1");
        map1.put("a", "a2");
        map2.put("a", "a1");
        map2.put("a", "a2");
        assertEquals(map1, map2);
        assertEquals(map1.hashCode(), map2.hashCode());

        map1.put("b", "b1");
        map1.put("b", "b2");
        map2.put("b", "b2");
        map2.put("b", "b1");
        assertNotSame(map1, map2);
        assertNotSame(map1.hashCode(), map2.hashCode());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testSetValuedMapEqualsHashCodeContract() {
        SetValuedMap map1 = MultiValuedHashMap.setValuedHashMap();
        SetValuedMap map2 = MultiValuedHashMap.setValuedHashMap();

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

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/MultiValuedHashMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/MultiValuedHashMap.fullCollection.version4.1.obj");
//    }

}
