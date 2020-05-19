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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;

/**
 * Tests for UnmodifiableMultiValuedMap
 *
 * @since 4.1
 */
public class UnmodifiableMultiValuedMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    public UnmodifiableMultiValuedMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(UnmodifiableMultiValuedMapTest.class);
    }

    // -----------------------------------------------------------------------
    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public MultiValuedMap<K, V> makeObject() {
        return UnmodifiableMultiValuedMap.<K, V>unmodifiableMultiValuedMap(
                new ArrayListValuedHashMap<K, V>());
    }

    @Override
    protected MultiValuedMap<K, V> makeFullMap() {
        final MultiValuedMap<K, V> map = new ArrayListValuedHashMap<>();
        addSampleMappings(map);
        return UnmodifiableMultiValuedMap.<K, V>unmodifiableMultiValuedMap(map);
    }

    // -----------------------------------------------------------------------
    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    public void testDecorateFactory() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(map));
    }

    public void testDecoratorFactoryNullMap() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(null);
        });
        assertTrue(exception.getMessage().contains("map"));
    }

    @SuppressWarnings("unchecked")
    public void testAddException() {
        final MultiValuedMap<K, V> map = makeObject();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            map.put((K) "one", (V) "uno");
        });
        assertNull(exception.getMessage());
    }

    public void testRemoveException() {
        final MultiValuedMap<K, V> map = makeFullMap();
        // expected, not support remove() method
        // UnmodifiableMultiValuedMap does not support change
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            map.remove("one");
        });
        assertNull(exception.getMessage());
        assertEquals("{one=[uno, un], two=[dos, deux], three=[tres, trois]}", map.toString());
    }

    public void testRemoveMappingException() {
        final MultiValuedMap<K, V> map = makeFullMap();
        // expected, not support removeMapping() method
        // UnmodifiableMultiValuedMap does not support change
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            map.removeMapping("one", "uno");
        });
        assertNull(exception.getMessage());
        assertEquals("{one=[uno, un], two=[dos, deux], three=[tres, trois]}", map.toString());
    }

    public void testClearException() {
        final MultiValuedMap<K, V> map = makeFullMap();
        // expected, not support clear() method
        // UnmodifiableMultiValuedMap does not support change
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            map.clear();
        });
        assertNull(exception.getMessage());
        assertEquals("{one=[uno, un], two=[dos, deux], three=[tres, trois]}", map.toString());
    }

    public void testPutAllException() {
        final MultiValuedMap<K, V> map = makeObject();
        final MultiValuedMap<K, V> original = new ArrayListValuedHashMap<>();
        final Map<K, V> originalMap = new HashMap<>();
        final Collection<V> coll = (Collection<V>) Arrays.asList("X", "Y", "Z");
        original.put((K) "key", (V) "object1");
        original.put((K) "key", (V) "object2");
        originalMap.put((K) "keyX", (V) "object1");
        originalMap.put((K) "keyY", (V) "object2");

        // expected, not support putAll() method
        // UnmodifiableMultiValuedMap does not support change
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            map.putAll(original);
        });
        assertNull(exception.getMessage());
        assertEquals("{}", map.toString());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            map.putAll(originalMap);
        });
        assertNull(exception.getMessage());
        assertEquals("{}", map.toString());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            map.putAll((K) "A", coll);
        });
        assertNull(exception.getMessage());
        assertEquals("{}", map.toString());
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableEntries() {
        resetFull();
        final Collection<Entry<K, V>> entries = getMap().entries();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            entries.clear();
        });
        assertNull(exception.getMessage());

        final Iterator<Entry<K, V>> it = entries.iterator();
        final Entry<K, V> entry = it.next();
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            it.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            entry.setValue((V) "three");
        });
        assertNull(exception.getMessage());
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableMapIterator() {
        resetFull();
        final MapIterator<K, V> mapIt = getMap().mapIterator();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            mapIt.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            mapIt.setValue((V) "three");
        });
        assertTrue(exception.getMessage().contains("setValue() is not supported"));
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableKeySet() {
        resetFull();
        final Set<K> keySet = getMap().keySet();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            keySet.add((K) "four");
        });
        assertNull(exception.getMessage());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            keySet.remove("four");
        });
        assertNull(exception.getMessage());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            keySet.clear();
        });
        assertNull(exception.getMessage());

        final Iterator<K> it = keySet.iterator();
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            it.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableValues() {
        resetFull();
        final Collection<V> values = getMap().values();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            values.add((V) "four");
        });
        assertNull(exception.getMessage());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            values.remove("four");
        });
        assertNull(exception.getMessage());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            values.clear();
        });
        assertNull(exception.getMessage());

        final Iterator<V> it = values.iterator();
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            it.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableAsMap() {
        resetFull();
        final Map<K, Collection<V>> mapCol = getMap().asMap();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            mapCol.put((K) "four", (Collection<V>) Arrays.asList("four"));
        });
        assertNull(exception.getMessage());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            mapCol.remove("four");
        });
        assertNull(exception.getMessage());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            mapCol.clear();
        });
        assertNull(exception.getMessage());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            mapCol.clear();
        });
        assertNull(exception.getMessage());
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableKeys() {
        resetFull();
        final MultiSet<K> keys = getMap().keys();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            keys.add((K) "four");
        });
        assertNull(exception.getMessage());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            keys.remove("four");
        });
        assertNull(exception.getMessage());

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            keys.clear();
        });
        assertNull(exception.getMessage());

        final Iterator<K> it = keys.iterator();
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            it.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.fullCollection.version4.1.obj");
//    }

}
