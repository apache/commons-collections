/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractMapTest} for exercising the
 * {@link PredicatedMap} implementation.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class PredicatedMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    protected static final Predicate<Object> truePredicate = TruePredicate.<Object>truePredicate();

    protected static final Predicate<Object> testPredicate = String.class::isInstance;

    protected IterableMap<K, V> decorateMap(final Map<K, V> map, final Predicate<? super K> keyPredicate,
        final Predicate<? super V> valuePredicate) {
        return PredicatedMap.predicatedMap(map, keyPredicate, valuePredicate);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public IterableMap<K, V> makeObject() {
        return decorateMap(new HashMap<>(), truePredicate, truePredicate);
    }

    public IterableMap<K, V> makeTestMap() {
        return decorateMap(new HashMap<>(), testPredicate, testPredicate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testEntrySet() {
        Map<K, V> map = makeTestMap();
        assertNotNull(map.entrySet(), "returned entryset should not be null");
        map = decorateMap(new HashMap<>(), null, null);
        map.put((K) "oneKey", (V) "oneValue");
        assertEquals(1, map.entrySet().size(), "returned entryset should contain one entry");
        map = decorateMap(map, null, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPut() {
        final Map<K, V> map = makeTestMap();
        assertThrows(IllegalArgumentException.class, () -> map.put((K) "Hi", (V) Integer.valueOf(3)),
                "Illegal value should raise IllegalArgument");

        assertThrows(IllegalArgumentException.class, () -> map.put((K) Integer.valueOf(3), (V) "Hi"),
                "Illegal key should raise IllegalArgument");

        assertFalse(map.containsKey(Integer.valueOf(3)));
        assertFalse(map.containsValue(Integer.valueOf(3)));

        final Map<K, V> map2 = new HashMap<>();
        map2.put((K) "A", (V) "a");
        map2.put((K) "B", (V) "b");
        map2.put((K) "C", (V) "c");
        map2.put((K) "c", (V) Integer.valueOf(3));

        assertThrows(IllegalArgumentException.class, () -> map.putAll(map2),
                "Illegal value should raise IllegalArgument");

        map.put((K) "E", (V) "e");
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> entry = iterator.next();
        final Map.Entry<K, V> finalEntry = entry;
        assertThrows(IllegalArgumentException.class, () -> finalEntry.setValue((V) Integer.valueOf(3)),
                "Illegal value should raise IllegalArgument");

        map.put((K) "F", (V) "f");
        iterator = map.entrySet().iterator();
        entry = iterator.next();
        entry.setValue((V) "x");
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/PredicatedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/PredicatedMap.fullCollection.version4.obj");
//    }

}
