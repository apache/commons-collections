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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;

/**
 * Extension of {@link AbstractMapTest} for exercising the 
 * {@link PredicatedMap} implementation.
 *
 * @since 3.0
 * @version $Id$
 */
public class PredicatedMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    protected static final Predicate<Object> truePredicate = TruePredicate.<Object>truePredicate();

    protected static final Predicate<Object> testPredicate = new Predicate<Object>() {
        public boolean evaluate(final Object o) {
            return o instanceof String;
        }
    };

    public PredicatedMapTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    protected IterableMap<K, V> decorateMap(final Map<K, V> map, final Predicate<? super K> keyPredicate,
        final Predicate<? super V> valuePredicate) {
        return PredicatedMap.predicatedMap(map, keyPredicate, valuePredicate);
    }

    @Override
    public IterableMap<K, V> makeObject() {
        return decorateMap(new HashMap<K, V>(), truePredicate, truePredicate);
    }

    public IterableMap<K, V> makeTestMap() {
        return decorateMap(new HashMap<K, V>(), testPredicate, testPredicate);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testEntrySet() {
        Map<K, V> map = makeTestMap();
        assertTrue("returned entryset should not be null",
            map.entrySet() != null);
        map = decorateMap(new HashMap<K, V>(), null, null);
        map.put((K) "oneKey", (V) "oneValue");
        assertTrue("returned entryset should contain one entry",
            map.entrySet().size() == 1);
        map = decorateMap(map, null, null);
    }

    @SuppressWarnings("unchecked")
    public void testPut() {
        final Map<K, V> map = makeTestMap();
        try {
            map.put((K) "Hi", (V) Integer.valueOf(3));
            fail("Illegal value should raise IllegalArgument");
        } catch (final IllegalArgumentException e) {
            // expected
        }

        try {
            map.put((K) Integer.valueOf(3), (V) "Hi");
            fail("Illegal key should raise IllegalArgument");
        } catch (final IllegalArgumentException e) {
            // expected
        }

        assertTrue(!map.containsKey(Integer.valueOf(3)));
        assertTrue(!map.containsValue(Integer.valueOf(3)));

        final Map<K, V> map2 = new HashMap<K, V>();
        map2.put((K) "A", (V) "a");
        map2.put((K) "B", (V) "b");
        map2.put((K) "C", (V) "c");
        map2.put((K) "c", (V) Integer.valueOf(3));

        try {
            map.putAll(map2);
            fail("Illegal value should raise IllegalArgument");
        } catch (final IllegalArgumentException e) {
            // expected
        }

        map.put((K) "E", (V) "e");
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        try {
            final Map.Entry<K, V> entry = iterator.next();
            entry.setValue((V) Integer.valueOf(3));
            fail("Illegal value should raise IllegalArgument");
        } catch (final IllegalArgumentException e) {
            // expected
        }

        map.put((K) "F", (V) "f");
        iterator = map.entrySet().iterator();
        final Map.Entry<K, V> entry = iterator.next();
        entry.setValue((V) "x");

    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
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
