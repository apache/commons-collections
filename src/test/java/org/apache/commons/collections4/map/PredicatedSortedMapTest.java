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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link PredicatedMapTest} for exercising the
 * {@link PredicatedSortedMap} implementation.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class PredicatedSortedMapTest<K, V> extends AbstractSortedMapTest<K, V> {

    private final class ReverseStringComparator implements Comparator<K> {
        @Override
        public int compare(final K arg0, final K arg1) {
            return ((String) arg1).compareTo((String) arg0);
        }
    }

    protected static final Predicate<Object> truePredicate = TruePredicate.truePredicate();

    protected static final Predicate<Object> testPredicate = String.class::isInstance;

    protected final Comparator<K> reverseStringComparator = new ReverseStringComparator();

    protected SortedMap<K, V> decorateMap(final SortedMap<K, V> map, final Predicate<? super K> keyPredicate,
        final Predicate<? super V> valuePredicate) {
        return PredicatedSortedMap.predicatedSortedMap(map, keyPredicate, valuePredicate);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isSubMapViewsSerializable() {
        // TreeMap sub map views have a bug in deserialization.
        return false;
    }

    @Override
    public SortedMap<K, V> makeObject() {
        return decorateMap(new TreeMap<>(), truePredicate, truePredicate);
    }

    public SortedMap<K, V> makeTestMap() {
        return decorateMap(new TreeMap<>(), testPredicate, testPredicate);
    }

    public SortedMap<K, V> makeTestMapWithComparator() {
        return decorateMap(new ConcurrentSkipListMap<>(reverseStringComparator), testPredicate, testPredicate);
    }

    // from TestPredicatedMap
    @Test
    @SuppressWarnings("unchecked")
    public void testEntrySet() {
        SortedMap<K, V> map = makeTestMap();
        assertNotNull(map.entrySet(), "returned entryset should not be null");
        map = decorateMap(new TreeMap<>(), null, null);
        map.put((K) "oneKey", (V) "oneValue");
        assertEquals(1, map.entrySet().size(), "returned entryset should contain one entry");
        map = decorateMap(map, null, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPut() {
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

    @Test
    @SuppressWarnings("unchecked")
    public void testReverseSortOrder() {
        final SortedMap<K, V> map = makeTestMapWithComparator();
        map.put((K) "A",  (V) "a");
        map.put((K) "B", (V) "b");
        assertThrows(IllegalArgumentException.class, () -> map.put(null, (V) "c"),
                "Null key should raise IllegalArgument");
        map.put((K) "C", (V) "c");
        assertThrows(IllegalArgumentException.class, () -> map.put((K) "D", null),
                "Null value should raise IllegalArgument");
        assertEquals("A", map.lastKey(), "Last key should be A");
        assertEquals("C", map.firstKey(), "First key should be C");
        assertEquals("B", map.tailMap((K) "B").firstKey(),
                "First key in tail map should be B");
        assertEquals("B", map.headMap((K) "A").lastKey(),
                "Last key in head map should be B");
        assertEquals("B", map.subMap((K) "C", (K) "A").lastKey(),
                "Last key in submap should be B");

        final Comparator<? super K> c = map.comparator();
        assertSame(c, reverseStringComparator, "reverse order, so comparator should be reverseStringComparator");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSortOrder() {
        final SortedMap<K, V> map = makeTestMap();
        map.put((K) "A", (V) "a");
        map.put((K) "B", (V) "b");
        assertThrows(IllegalArgumentException.class, () -> map.put(null, (V) "c"),
                "Null key should raise IllegalArgument");
        map.put((K) "C", (V) "c");
        assertThrows(IllegalArgumentException.class, () -> map.put((K) "D", null),
                "Null value should raise IllegalArgument");
        assertEquals("A", map.firstKey(), "First key should be A");
        assertEquals("C", map.lastKey(), "Last key should be C");
        assertEquals("B", map.tailMap((K) "B").firstKey(),
                "First key in tail map should be B");
        assertEquals("B", map.headMap((K) "C").lastKey(),
                "Last key in head map should be B");
        assertEquals("B", map.subMap((K) "A", (K) "C").lastKey(),
                "Last key in submap should be B");

        final Comparator<? super K> c = map.comparator();
        assertNull(c, "natural order, so comparator should be null");
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/PredicatedSortedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/PredicatedSortedMap.fullCollection.version4.obj");
//    }

}
