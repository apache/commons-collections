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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;

/**
 * Tests for UnmodifiableMultiValuedMap
 * 
 * @since 4.1
 * @version $Id$
 */
public class UnmodifiableMultiValuedMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    public UnmodifiableMultiValuedMapTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(UnmodifiableMultiValuedMapTest.class);
    }
    
    public boolean isAddSupported() {
        return false;
    }

    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public MultiValuedMap<K, V> makeObject() {
        return UnmodifiableMultiValuedMap.<K, V> unmodifiableMultiValuedMap(new MultiValuedHashMap<K, V>());
    }

    protected MultiValuedMap<K, V> makeFullMap() {
        final MultiValuedMap<K, V> map = new MultiValuedHashMap<K, V>();
        addSampleMappings(map);
        return UnmodifiableMultiValuedMap.<K, V> unmodifiableMultiValuedMap(map);
    }

    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    public void testDecorateFactory() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(map));
    }

    public void testDecoratorFactoryNullMap() {
        try {
            UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(null);
            fail("map must not be null");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testAddException() {
        MultiValuedMap<K, V> map = makeObject();
        try {
            map.put((K) "one", (V) "uno");
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableEntries() {
        resetFull();
        Collection<Entry<K, V>> entries = getMap().entries();
        try {
            entries.clear();
            fail();
        } catch (UnsupportedOperationException e) {
        }

        Iterator<Entry<K, V>> it = entries.iterator();
        Entry<K, V> entry = it.next();
        try {
            it.remove();
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            entry.setValue((V) "three");
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableMapIterator() {
        resetFull();
        MapIterator<K, V> mapIt = getMap().mapIterator();
        try {
            mapIt.remove();
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            mapIt.setValue((V) "three");
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableKeySet() {
        resetFull();
        Set<K> keySet = getMap().keySet();
        try {
            keySet.add((K) "four");
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            keySet.remove("four");
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            keySet.clear();
            fail();
        } catch (UnsupportedOperationException e) {
        }

        Iterator<K> it = keySet.iterator();
        try {
            it.remove();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableValues() {
        resetFull();
        Collection<V> values = getMap().values();
        try {
            values.add((V) "four");
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            values.remove("four");
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            values.clear();
            fail();
        } catch (UnsupportedOperationException e) {
        }

        Iterator<V> it = values.iterator();
        try {
            it.remove();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableAsMap() {
        resetFull();
        Map<K, Collection<V>> mapCol = getMap().asMap();
        try {
            mapCol.put((K) "four", (Collection<V>) Arrays.asList("four"));
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            mapCol.remove("four");
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            mapCol.clear();
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            mapCol.clear();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableKeys() {
        resetFull();
        Bag<K> keys = getMap().keys();
        try {
            keys.add((K) "four");
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            keys.remove("four");
            fail();
        } catch (UnsupportedOperationException e) {
        }

        try {
            keys.clear();
            fail();
        } catch (UnsupportedOperationException e) {
        }

        Iterator<K> it = keys.iterator();
        try {
            it.remove();
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.fullCollection.version4.1.obj");
//    }

}
