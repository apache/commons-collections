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
        try {
            UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(null);
            fail("map must not be null");
        } catch (final NullPointerException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testAddException() {
        final MultiValuedMap<K, V> map = makeObject();
        try {
            map.put((K) "one", (V) "uno");
            fail();
        } catch (final UnsupportedOperationException e) {
        }
    }

    public void testRemoveException() {
        final MultiValuedMap<K, V> map = makeFullMap();
        try {
            map.remove("one");
            fail();
        } catch (final UnsupportedOperationException e) {
            // expected, not support remove() method
            // UnmodifiableMultiValuedMap does not support change
        }
        this.assertMapContainsAllValues(map);
    }

    public void testRemoveMappingException() {
        final MultiValuedMap<K, V> map = makeFullMap();
        try {
            map.removeMapping("one", "uno");
            fail();
        } catch (final UnsupportedOperationException e) {
            // expected, not support removeMapping() method
            // UnmodifiableMultiValuedMap does not support change
        }
        this.assertMapContainsAllValues(map);
    }

    public void testClearException() {
        final MultiValuedMap<K, V> map = makeFullMap();
        try {
            map.clear();
            fail();
        } catch (final UnsupportedOperationException e) {
            // expected, not support clear() method
            // UnmodifiableMultiValuedMap does not support change
        }
        this.assertMapContainsAllValues(map);
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

        try {
            map.putAll(original);
            fail();
        } catch (final UnsupportedOperationException e) {
            // expected, not support putAll() method
            // UnmodifiableMultiValuedMap does not support change
        }
        assertEquals("{}", map.toString());

        try {
            map.putAll(originalMap);
            fail();
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        assertEquals("{}", map.toString());

        try {
            map.putAll((K) "A", coll);
            fail();
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        assertEquals("{}", map.toString());
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableEntries() {
        resetFull();
        final Collection<Entry<K, V>> entries = getMap().entries();
        try {
            entries.clear();
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        final Iterator<Entry<K, V>> it = entries.iterator();
        final Entry<K, V> entry = it.next();
        try {
            it.remove();
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            entry.setValue((V) "three");
            fail();
        } catch (final UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableMapIterator() {
        resetFull();
        final MapIterator<K, V> mapIt = getMap().mapIterator();
        try {
            mapIt.remove();
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            mapIt.setValue((V) "three");
            fail();
        } catch (final UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableKeySet() {
        resetFull();
        final Set<K> keySet = getMap().keySet();
        try {
            keySet.add((K) "four");
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            keySet.remove("four");
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            keySet.clear();
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        final Iterator<K> it = keySet.iterator();
        try {
            it.remove();
            fail();
        } catch (final UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableValues() {
        resetFull();
        final Collection<V> values = getMap().values();
        try {
            values.add((V) "four");
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            values.remove("four");
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            values.clear();
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        final Iterator<V> it = values.iterator();
        try {
            it.remove();
            fail();
        } catch (final UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableAsMap() {
        resetFull();
        final Map<K, Collection<V>> mapCol = getMap().asMap();
        try {
            mapCol.put((K) "four", (Collection<V>) Arrays.asList("four"));
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            mapCol.remove("four");
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            mapCol.clear();
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            mapCol.clear();
            fail();
        } catch (final UnsupportedOperationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testUnmodifiableKeys() {
        resetFull();
        final MultiSet<K> keys = getMap().keys();
        try {
            keys.add((K) "four");
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            keys.remove("four");
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        try {
            keys.clear();
            fail();
        } catch (final UnsupportedOperationException e) {
        }

        final Iterator<K> it = keys.iterator();
        try {
            it.remove();
            fail();
        } catch (final UnsupportedOperationException e) {
        }
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.fullCollection.version4.1.obj");
//    }

}
