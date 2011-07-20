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
package org.apache.commons.collections.map;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;

/**
 * Extension of {@link TestPredicatedMap} for exercising the
 * {@link PredicatedSortedMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Phil Steitz
 */
public class TestPredicatedSortedMap<K, V> extends AbstractTestSortedMap<K, V> {

    protected static final Predicate<Object> truePredicate = TruePredicate.truePredicate();

    protected static final Predicate<Object> testPredicate = new Predicate<Object>() {
        public boolean evaluate(Object o) {
            return (o instanceof String);
        }
    };

    public TestPredicatedSortedMap(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    protected SortedMap<K, V> decorateMap(SortedMap<K, V> map, Predicate<? super K> keyPredicate,
        Predicate<? super V> valuePredicate) {
        return PredicatedSortedMap.predicatedSortedMap(map, keyPredicate, valuePredicate);
    }

    @Override
    public SortedMap<K, V> makeObject() {
        return decorateMap(new TreeMap<K, V>(), truePredicate, truePredicate);
    }

    public SortedMap<K, V> makeTestMap() {
        return decorateMap(new TreeMap<K, V>(), testPredicate, testPredicate);
    }

    @Override
    public boolean isSubMapViewsSerializable() {
        // TreeMap sub map views have a bug in deserialization.
        return false;
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    // from TestPredicatedMap
    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testEntrySet() {
        SortedMap<K, V> map = makeTestMap();
        assertTrue("returned entryset should not be null",
            map.entrySet() != null);
        map = decorateMap(new TreeMap<K, V>(), null, null);
        map.put((K) "oneKey", (V) "oneValue");
        assertTrue("returned entryset should contain one entry",
            map.entrySet().size() == 1);
        map = decorateMap(map, null, null);
    }

    @SuppressWarnings("unchecked")
    public void testPut() {
        Map<K, V> map = makeTestMap();
        try {
            map.put((K) "Hi", (V) new Integer(3));
            fail("Illegal value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            map.put((K) new Integer(3), (V) "Hi");
            fail("Illegal key should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        assertTrue(!map.containsKey(new Integer(3)));
        assertTrue(!map.containsValue(new Integer(3)));

        Map<K, V> map2 = new HashMap<K, V>();
        map2.put((K) "A", (V) "a");
        map2.put((K) "B", (V) "b");
        map2.put((K) "C", (V) "c");
        map2.put((K) "c", (V) new Integer(3));

        try {
            map.putAll(map2);
            fail("Illegal value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        map.put((K) "E", (V) "e");
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        try {
            Map.Entry<K, V> entry = iterator.next();
            entry.setValue((V) new Integer(3));
            fail("Illegal value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        map.put((K) "F", (V) "f");
        iterator = map.entrySet().iterator();
        Map.Entry<K, V> entry = iterator.next();
        entry.setValue((V) "x");

    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testSortOrder() {
        SortedMap<K, V> map = makeTestMap();
        map.put((K) "A",  (V) "a");
        map.put((K) "B", (V) "b");
        try {
            map.put(null, (V) "c");
            fail("Null key should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }
        map.put((K) "C", (V) "c");
        try {
            map.put((K) "D", null);
            fail("Null value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals("First key should be A", "A", map.firstKey());
        assertEquals("Last key should be C", "C", map.lastKey());
        assertEquals("First key in tail map should be B",
            "B", map.tailMap((K) "B").firstKey());
        assertEquals("Last key in head map should be B",
            "B", map.headMap((K) "C").lastKey());
        assertEquals("Last key in submap should be B",
           "B", map.subMap((K) "A",(K) "C").lastKey());

        Comparator<? super K> c = map.comparator();
        assertTrue("natural order, so comparator should be null",
            c == null);
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/PredicatedSortedMap.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/PredicatedSortedMap.fullCollection.version3.1.obj");
//    }
}
