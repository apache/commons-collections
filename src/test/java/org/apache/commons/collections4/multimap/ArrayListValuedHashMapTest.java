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

import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.MultiValuedMap;

/**
 * Test ArrayListValuedHashMap
 *
 * @since 4.1
 */
public class ArrayListValuedHashMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    public ArrayListValuedHashMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(ArrayListValuedHashMapTest.class);
    }

    // -----------------------------------------------------------------------
    @Override
    public ListValuedMap<K, V> makeObject() {
        return new ArrayListValuedHashMap<>();
    }

    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testListValuedMapAdd() {
        final ListValuedMap<K, V> listMap = makeObject();
        assertTrue(listMap.get((K) "whatever") instanceof List);
        List<V> list = listMap.get((K) "A");
        list.add((V) "a1");
        assertEquals(1, listMap.size());
        assertTrue(listMap.containsKey("A"));
    }

    @SuppressWarnings("unchecked")
    public void testListValuedMapAddViaListIterator() {
        final ListValuedMap<K, V> listMap = makeObject();
        ListIterator<V> listIt = listMap.get((K) "B").listIterator();
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
        final ListValuedMap<K, V> listMap = makeObject();
        List<V> list = listMap.get((K) "A");
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
        final ListValuedMap<K, V> listMap = makeObject();
        ListIterator<V> listIt = listMap.get((K) "B").listIterator();
        listIt.add((V) "b1");
        listIt.add((V) "b2");
        assertEquals(2, listMap.size());
        assertTrue(listMap.containsKey("B"));
        listIt = listMap.get((K) "B").listIterator();
        while (listIt.hasNext()) {
            listIt.next();
            listIt.remove();
        }
        assertFalse(listMap.containsKey("B"));
        listIt.add((V) "b1");
        listIt.add((V) "b2");
        assertTrue(listMap.containsKey("B"));
        assertEquals(2, listMap.get((K) "B").size());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testEqualsHashCodeContract() {
        MultiValuedMap map1 = makeObject();
        MultiValuedMap map2 = makeObject();

        map1.put("a", "a1");
        map1.put("a", "a2");
        map2.put("a", "a1");
        map2.put("a", "a2");
        assertEquals(map1, map2);
        assertEquals(map1.hashCode(), map2.hashCode());

        map2.put("a", "a2");
        assertNotSame(map1, map2);
        assertNotSame(map1.hashCode(), map2.hashCode());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testListValuedMapEqualsHashCodeContract() {
        ListValuedMap map1 = makeObject();
        ListValuedMap map2 = makeObject();

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

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/ArrayListValuedHashMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/ArrayListValuedHashMap.fullCollection.version4.1.obj");
//    }

}
