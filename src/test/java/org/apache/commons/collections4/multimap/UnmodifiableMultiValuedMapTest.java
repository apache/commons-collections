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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for UnmodifiableMultiValuedMap
 *
 * @since 4.1
 */
public class UnmodifiableMultiValuedMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    public UnmodifiableMultiValuedMapTest() {
        super(UnmodifiableMultiValuedMapTest.class.getSimpleName());
    }

    /**
     * Assert the given map contains all added values after it was initialized
     * with makeFullMap(). See COLLECTIONS-769.
     * @param map the MultiValuedMap<K, V> to check
     */
    private void assertMapContainsAllValues(final MultiValuedMap<K, V> map) {
        assertEquals("[uno, un]", map.get((K) "one").toString());
        assertEquals("[dos, deux]", map.get((K) "two").toString());
        assertEquals("[tres, trois]", map.get((K) "three").toString());
    }

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

    @Override
    protected int getIterationBehaviour() {
        return AbstractCollectionTest.UNORDERED;
    }

    @Test
    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    @Test
    public void testDecorateFactory() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(map));
    }

    @Test
    public void testDecoratorFactoryNullMap() {
        assertThrows(NullPointerException.class, () -> UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(null),
                "map must not be null");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddException() {
        final MultiValuedMap<K, V> map = makeObject();
        assertThrows(UnsupportedOperationException.class, () -> map.put((K) "one", (V) "uno"));
    }

    @Test
    public void testRemoveException() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertThrows(UnsupportedOperationException.class, () -> map.remove("one"),
                "not support remove() method UnmodifiableMultiValuedMap does not support change");
        this.assertMapContainsAllValues(map);
    }

    @Test
    public void testRemoveMappingException() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertThrows(UnsupportedOperationException.class, () -> map.removeMapping("one", "uno"),
                "expected, not support removeMapping() method UnmodifiableMultiValuedMap does not support change");
        this.assertMapContainsAllValues(map);
    }

    @Test
    public void testClearException() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertThrows(UnsupportedOperationException.class, () -> map.clear(),
                "expected, not support clear() method UnmodifiableMultiValuedMap does not support change");
        this.assertMapContainsAllValues(map);
    }

    @Test
    public void testPutAllException() {
        final MultiValuedMap<K, V> map = makeObject();
        final MultiValuedMap<K, V> original = new ArrayListValuedHashMap<>();
        final Map<K, V> originalMap = new HashMap<>();
        final Collection<V> coll = (Collection<V>) Arrays.asList("X", "Y", "Z");
        original.put((K) "key", (V) "object1");
        original.put((K) "key", (V) "object2");
        originalMap.put((K) "keyX", (V) "object1");
        originalMap.put((K) "keyY", (V) "object2");

        assertThrows(UnsupportedOperationException.class, () -> map.putAll(original),
                "expected, not support putAll() method UnmodifiableMultiValuedMap does not support change");
        assertEquals("{}", map.toString());

        assertThrows(UnsupportedOperationException.class, () -> map.putAll(originalMap));
        assertEquals("{}", map.toString());

        assertThrows(UnsupportedOperationException.class, () -> map.putAll((K) "A", coll));
        assertEquals("{}", map.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmodifiableEntries() {
        resetFull();
        final Collection<Entry<K, V>> entries = getMap().entries();
        assertThrows(UnsupportedOperationException.class, () -> entries.clear());

        final Iterator<Entry<K, V>> it = entries.iterator();
        final Entry<K, V> entry = it.next();
        assertThrows(UnsupportedOperationException.class, () -> it.remove());

        assertThrows(UnsupportedOperationException.class, () -> entry.setValue((V) "three"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmodifiableMapIterator() {
        resetFull();
        final MapIterator<K, V> mapIt = getMap().mapIterator();
        assertThrows(UnsupportedOperationException.class, () -> mapIt.remove());

        assertThrows(UnsupportedOperationException.class, () -> mapIt.setValue((V) "three"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmodifiableKeySet() {
        resetFull();
        final Set<K> keySet = getMap().keySet();
        assertThrows(UnsupportedOperationException.class, () -> keySet.add((K) "four"));

        assertThrows(UnsupportedOperationException.class, () -> keySet.remove("four"));

        assertThrows(UnsupportedOperationException.class, () -> keySet.clear());

        final Iterator<K> it = keySet.iterator();
        assertThrows(UnsupportedOperationException.class, () -> it.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmodifiableValues() {
        resetFull();
        final Collection<V> values = getMap().values();
        assertThrows(UnsupportedOperationException.class, () -> values.add((V) "four"));

        assertThrows(UnsupportedOperationException.class, () -> values.remove("four"));

        assertThrows(UnsupportedOperationException.class, () -> values.clear());

        final Iterator<V> it = values.iterator();
        assertThrows(UnsupportedOperationException.class, () -> it.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmodifiableAsMap() {
        resetFull();
        final Map<K, Collection<V>> mapCol = getMap().asMap();
        assertThrows(UnsupportedOperationException.class, () -> mapCol.put((K) "four", (Collection<V>) Arrays.asList("four")));

        assertThrows(UnsupportedOperationException.class, () -> mapCol.remove("four"));

        assertThrows(UnsupportedOperationException.class, () -> mapCol.clear());

        assertThrows(UnsupportedOperationException.class, () -> mapCol.clear());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmodifiableKeys() {
        resetFull();
        final MultiSet<K> keys = getMap().keys();
        assertThrows(UnsupportedOperationException.class, () -> keys.add((K) "four"));

        assertThrows(UnsupportedOperationException.class, () -> keys.remove("four"));

        assertThrows(UnsupportedOperationException.class, () -> keys.clear());

        final Iterator<K> it = keys.iterator();
        assertThrows(UnsupportedOperationException.class, () -> it.remove());
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.fullCollection.version4.1.obj");
//    }

}
