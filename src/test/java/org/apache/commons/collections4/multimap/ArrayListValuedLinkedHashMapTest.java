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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ArrayListValuedLinkedHashMap}.
 */
public class ArrayListValuedLinkedHashMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    @Override
    public String getCompatibilityVersion() {
        return "4.5"; // ArrayListValuedLinkedHashMap has been added in version 4.5
    }

    @Override
    protected int getIterationBehaviour() {
        return AbstractCollectionTest.UNORDERED;
    }

    @Override
    public ListValuedMap<K, V> makeObject() {
        return new ArrayListValuedLinkedHashMap<>();
    }

    @Test
    public void testArrayListValuedLinkedHashMap() {
        final ListValuedMap<K, V> listMap;
        final ListValuedMap<K, V> listMap1;
        final Map<K, V> map = new HashMap<>();
        final Map<K, V> map1 = new HashMap<>();
        map.put((K) "A", (V) "W");
        map.put((K) "B", (V) "X");
        map.put((K) "C", (V) "F");

        listMap = new ArrayListValuedLinkedHashMap<>(map);
        assertEquals(1, listMap.get((K) "A").size());
        assertEquals(1, listMap.get((K) "B").size());
        assertEquals(1, listMap.get((K) "C").size());

        listMap1 = new ArrayListValuedLinkedHashMap<>(map1);
        assertEquals("{}", listMap1.toString());
    }

    @Test
    public void testCopyConstructorWithMultiValuedMap() {
        final ListValuedMap<K, V> map = makeObject();
        map.put((K) "key", (V) "sleutel");
        final ListValuedMap<K, V> copy = new ArrayListValuedLinkedHashMap<>(map);
        assertEquals(map, copy);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testEqualsHashCodeContract() {
        final MultiValuedMap map1 = makeObject();
        final MultiValuedMap map2 = makeObject();

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

    @Test
    @SuppressWarnings("unchecked")
    public void testListValuedMapAdd() {
        final ListValuedMap<K, V> listMap = makeObject();
        assertTrue(listMap.get((K) "whatever") instanceof List);
        final List<V> list = listMap.get((K) "A");
        list.add((V) "a1");
        assertEquals(1, listMap.size());
        assertTrue(listMap.containsKey("A"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListValuedMapAddViaListIterator() {
        final ListValuedMap<K, V> listMap = makeObject();
        final ListIterator<V> listIt = listMap.get((K) "B").listIterator();
        assertFalse(listIt.hasNext());
        listIt.add((V) "b1");
        listIt.add((V) "b2");
        listIt.add((V) "b3");
        assertEquals(3, listMap.size());
        assertTrue(listMap.containsKey("B"));
        // As ListIterator always adds before the current cursor
        assertFalse(listIt.hasNext());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testListValuedMapEqualsHashCodeContract() {
        final ListValuedMap map1 = makeObject();
        final ListValuedMap map2 = makeObject();

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

    @Test
    @SuppressWarnings("unchecked")
    public void testListValuedMapRemove() {
        final ListValuedMap<K, V> listMap = makeObject();
        final List<V> list = listMap.get((K) "A");
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

    @Test
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

    @Test
    public void testPreservesKeyInsertionOrder() {
        final ListValuedMap<K, V> map = makeObject();
        map.put((K) Integer.valueOf(5), (V) "five");
        map.put((K) Integer.valueOf(1), (V) "one");
        map.put((K) Integer.valueOf(5), (V) "vijf"); // "vijf" = "five" in Dutch
        final MapIterator<K, V> mapIterator = map.mapIterator();
        assertEquals(5, mapIterator.next());
        assertEquals("five", mapIterator.getValue());
        assertEquals(5, mapIterator.next());
        assertEquals("vijf", mapIterator.getValue());
        assertEquals(1, mapIterator.next());
        assertEquals("one", mapIterator.getValue());
        assertFalse(mapIterator.hasNext());
    }

    @Test
    public void testTrimToSize() {
        final ArrayListValuedLinkedHashMap<K, V> listMap = new ArrayListValuedLinkedHashMap<>(4);

        assertEquals("{}", listMap.toString());
        listMap.put((K) "A", (V) "W");
        listMap.put((K) "A", (V) "X");
        listMap.put((K) "B", (V) "F");
        assertEquals(2, listMap.get((K) "A").size());
        assertEquals(1, listMap.get((K) "B").size());

        listMap.trimToSize();
        assertEquals(2, listMap.get((K) "A").size());
        assertEquals(1, listMap.get((K) "B").size());
    }

    @Test
    public void testValuesListIteratorMethods() {
        final ListValuedMap<K, V> listMap = makeObject();
        final List<V> listA = listMap.get((K) "A");
        final List<V> list = Arrays.asList((V) "W", (V) "X", (V) "F", (V) "Q", (V) "Q", (V) "F");
        listA.addAll(0, list);
        final ListIterator<V> it = listMap.get((K) "A").listIterator(1);
        assertTrue(it.hasNext());
        assertEquals("X", it.next());
        assertEquals("F", it.next());
        assertTrue(it.hasPrevious());
        assertEquals("F", it.previous());
        assertEquals(2, it.nextIndex());
        assertEquals(1, it.previousIndex());
        it.set((V) "Z");
        assertEquals("Z", it.next());
        assertEquals("Q", it.next());
    }

    @Test
    public void testWrappedListAdd() {
        final ListValuedMap<K, V> listMap = makeObject();
        final List<V> listA = listMap.get((K) "A");
        listA.add(0, (V) "W");
        listA.add(1, (V) "X");
        listA.add(2, (V) "F");
        assertEquals("{A=[W, X, F]}", listMap.toString());
        listMap.get((K) "A").set(1, (V) "Q");
        assertEquals("{A=[W, Q, F]}", listMap.toString());
    }

    @Test
    public void testWrappedListAddAll() {
        final ListValuedMap<K, V> listMap = makeObject();
        final List<V> listA = listMap.get((K) "A");
        final List<V> list = Arrays.asList((V) "W", (V) "X", (V) "F");
        listA.addAll(0, list);
        assertEquals("{A=[W, X, F]}", listMap.toString());

        final List<V> list1 = Arrays.asList((V) "Q", (V) "Q", (V) "L");
        listA.addAll(3, list1);
        assertEquals("{A=[W, X, F, Q, Q, L]}", listMap.toString());
        assertEquals("W", listMap.get((K) "A").get(0));
        assertEquals("X", listMap.get((K) "A").get(1));
        assertEquals("F", listMap.get((K) "A").get(2));
        assertEquals("Q", listMap.get((K) "A").get(3));
        assertEquals("Q", listMap.get((K) "A").get(4));
        assertEquals("L", listMap.get((K) "A").get(5));
        assertEquals(0, listMap.get((K) "A").indexOf("W"));
        assertEquals(2, listMap.get((K) "A").indexOf("F"));
        assertEquals(-1, listMap.get((K) "A").indexOf("C"));
        assertEquals(3, listMap.get((K) "A").indexOf("Q"));
        assertEquals(4, listMap.get((K) "A").lastIndexOf("Q"));
        assertEquals(-1, listMap.get((K) "A").lastIndexOf("A"));

        final List<V> list2 = new ArrayList<>();
        listMap.get((K) "B").addAll(0, list2);
        assertEquals("{A=[W, X, F, Q, Q, L]}", listMap.toString());
        final List<V> list3 = listMap.get((K) "A").subList(1, 4);
        assertEquals(3, list3.size());
        assertEquals("Q", list3.get(2));
    }

//    @Test
//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/org/apache/commons/collections4/data/test/ArrayListValuedLinkedHashMap.emptyCollection.version4.5.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/org/apache/commons/collections4/data/test/ArrayListValuedLinkedHashMap.fullCollection.version4.5.obj");
//    }

}
