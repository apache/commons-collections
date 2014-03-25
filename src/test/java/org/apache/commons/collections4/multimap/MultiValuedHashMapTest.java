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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.collections4.MultiValuedMap;

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

    @Override
    public MultiValuedMap<K, V> makeObject() {
        final MultiValuedMap<K, V> m = new MultiValuedHashMap<K, V>();
        return m;
    }

    private <C extends Collection<V>> MultiValuedHashMap<K, V> createTestMap(final Class<C> collectionClass) {
        final MultiValuedHashMap<K, V> map =
                (MultiValuedHashMap<K, V>) MultiValuedHashMap.<K, V, C> multiValuedMap(collectionClass);
        addSampleMappings(map);
        return map;
    }

    @SuppressWarnings("unchecked")
    public void testValueCollectionType() {
        final MultiValuedHashMap<K, V> map = createTestMap(LinkedList.class);
        assertTrue(map.get("one") instanceof LinkedList);
    }

    @SuppressWarnings("unchecked")
    public void testPutWithList() {
        final MultiValuedHashMap<K, V> test = (MultiValuedHashMap<K, V>) MultiValuedHashMap.multiValuedMap(ArrayList.class);
        assertEquals("a", test.put((K) "A", (V) "a"));
        assertEquals("b", test.put((K) "A", (V) "b"));
        assertEquals(1, test.keySet().size());
        assertEquals(2, test.size("A"));
        assertEquals(2, test.size());
    }

    @SuppressWarnings("unchecked")
    public void testPutWithSet() {
        final MultiValuedHashMap<K, V> test = (MultiValuedHashMap<K, V>) MultiValuedHashMap.multiValuedMap(HashSet.class);
        assertEquals("a", test.put((K) "A", (V) "a"));
        assertEquals("b", test.put((K) "A", (V) "b"));
        assertEquals(null, test.put((K) "A", (V) "a"));
        assertEquals(1, test.keySet().size());
        assertEquals(2, test.size("A"));
        assertEquals(2, test.size());
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/MultiValuedHashMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/MultiValuedHashMap.fullCollection.version4.1.obj");
//    }

}
