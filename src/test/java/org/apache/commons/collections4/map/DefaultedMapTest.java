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
package org.apache.commons.collections4.map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.TransformerUtils;
import org.apache.commons.collections4.functors.ConstantFactory;
import org.junit.Test;

/**
 * Extension of {@link AbstractMapTest} for exercising the
 * {@link DefaultedMap} implementation.
 *
 * @since 3.2
 */
public class DefaultedMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    protected final Factory<V> nullFactory = FactoryUtils.<V>nullFactory();
    protected final Transformer<K, V> nullTransformer = TransformerUtils.<K, V>nullTransformer();

    public DefaultedMapTest(final String testName) {
        super(testName);
    }

    @Override
    public IterableMap<K, V> makeObject() {
        return DefaultedMap.defaultedMap(new HashMap<K, V>(), nullFactory);
    }

    @Test
    @Override
    @SuppressWarnings("unchecked")
    public void testMapGet() {
        final Map<K, V> map = new DefaultedMap<>((V) "NULL");

        assertEquals(0, map.size());
        assertFalse(map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));

        map.put((K) "Key", (V) "Value");
        assertEquals(1, map.size());
        assertTrue(map.containsKey("Key"));
        assertEquals("Value", map.get("Key"));
        assertFalse(map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapGet2() {
        final HashMap<K, V> base = new HashMap<>();
        final Map<K, V> map = DefaultedMap.defaultedMap(base, (V) "NULL");

        assertEquals(0, map.size());
        assertEquals(0, base.size());
        assertFalse(map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));

        map.put((K) "Key", (V) "Value");
        assertEquals(1, map.size());
        assertEquals(1, base.size());
        assertTrue(map.containsKey("Key"));
        assertEquals("Value", map.get("Key"));
        assertFalse(map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapGet3() {
        final HashMap<K, V> base = new HashMap<>();
        final Map<K, V> map = DefaultedMap.defaultedMap(base, ConstantFactory.constantFactory((V) "NULL"));

        assertEquals(0, map.size());
        assertEquals(0, base.size());
        assertFalse(map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));

        map.put((K) "Key", (V) "Value");
        assertEquals(1, map.size());
        assertEquals(1, base.size());
        assertTrue(map.containsKey("Key"));
        assertEquals("Value", map.get("Key"));
        assertFalse(map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapGet4() {
        final HashMap<K, V> base = new HashMap<>();
        final Map<K, V> map = DefaultedMap.defaultedMap(base, (Transformer<K, V>) input -> {
            if (input instanceof String) {
                return (V) "NULL";
            }
            return (V) "NULL_OBJECT";
        });

        assertEquals(0, map.size());
        assertEquals(0, base.size());
        assertFalse(map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
        assertEquals("NULL_OBJECT", map.get(Integer.valueOf(0)));

        map.put((K) "Key", (V) "Value");
        assertEquals(1, map.size());
        assertEquals(1, base.size());
        assertTrue(map.containsKey("Key"));
        assertEquals("Value", map.get("Key"));
        assertFalse(map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
        assertEquals("NULL_OBJECT", map.get(Integer.valueOf(0)));
    }

    @Test
    public void testFactoryMethods() {
        final HashMap<K, V> base = new HashMap<>();
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> DefaultedMap.defaultedMap(null, (V) "DEFAULT_VALUE"),
                        "Expecting NullPointerException"),
                () -> assertThrows(NullPointerException.class, () -> DefaultedMap.defaultedMap((Map<K, V>) null, nullFactory),
                        "Expecting NullPointerException"),
                () -> assertThrows(NullPointerException.class, () -> DefaultedMap.defaultedMap(base, (Factory<V>) null),
                        "Expecting NullPointerException"),
                () -> assertThrows(NullPointerException.class, () -> DefaultedMap.defaultedMap((Map<K, V>) null, nullTransformer),
                        "Expecting NullPointerException"),
                () -> assertThrows(NullPointerException.class, () -> DefaultedMap.defaultedMap(base, (Transformer<K, V>) null),
                        "Expecting NullPointerException")
        );
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/DefaultedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/DefaultedMap.fullCollection.version4.obj");
//    }

}
