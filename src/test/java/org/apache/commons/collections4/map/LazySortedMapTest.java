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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.TransformerUtils;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link LazyMapTest} for exercising the {@link LazySortedMap} implementation.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
@SuppressWarnings("boxing")
public class LazySortedMapTest<K, V> extends AbstractSortedMapTest<K, V> {

    private static final class ReverseStringComparator implements Comparator<String> {

        @Override
        public int compare(final String arg0, final String arg1) {
            return arg1.compareTo(arg0);
        }

    }

    private static final Factory<Integer> oneFactory = FactoryUtils.constantFactory(1);

    protected final Comparator<String> reverseStringComparator = new ReverseStringComparator();

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    protected boolean isLazyMapTest() {
        return true;
    }

    @Override
    public SortedMap<K, V> makeObject() {
        return LazySortedMap.lazySortedMap(new TreeMap<>(), FactoryUtils.<V>nullFactory());
    }

    @Override
    @Test
    public void testMapGet() {
        Map<Integer, Number> map = LazySortedMap.lazySortedMap(new TreeMap<>(), oneFactory);
        assertEquals(0, map.size());
        final Number i1 = map.get(5);
        assertEquals(1, i1);
        assertEquals(1, map.size());

        map = LazySortedMap.lazySortedMap(new TreeMap<>(), FactoryUtils.<Number>nullFactory());
        final Number o = map.get(5);
        assertNull(o);
        assertEquals(1, map.size());

    }

    @Test
    public void testReverseSortOrder() {
        final SortedMap<String, Number> map = LazySortedMap.lazySortedMap(new ConcurrentSkipListMap<>(reverseStringComparator), oneFactory);
        map.put("A", 5);
        map.get("B"); // Entry with value "One" created
        map.put("C", 8);
        assertEquals("A", map.lastKey(), "Last key should be A");
        assertEquals("C", map.firstKey(), "First key should be C");
        assertEquals("B", map.tailMap("B").firstKey(), "First key in tail map should be B");
        assertEquals("B", map.headMap("A").lastKey(), "Last key in head map should be B");
        assertEquals("B", map.subMap("C", "A").lastKey(), "Last key in submap should be B");

        final Comparator<?> c = map.comparator();
        assertSame(c, reverseStringComparator, "natural order, so comparator should be null");
    }

    @Test
    public void testSortOrder() {
        final SortedMap<String, Number> map = LazySortedMap.lazySortedMap(new TreeMap<>(), oneFactory);
        map.put("A", 5);
        map.get("B"); // Entry with value "One" created
        map.put("C", 8);
        assertEquals("A", map.firstKey(), "First key should be A");
        assertEquals("C", map.lastKey(), "Last key should be C");
        assertEquals("B", map.tailMap("B").firstKey(), "First key in tail map should be B");
        assertEquals("B", map.headMap("C").lastKey(), "Last key in head map should be B");
        assertEquals("B", map.subMap("A", "C").lastKey(), "Last key in submap should be B");

        final Comparator<?> c = map.comparator();
        assertNull(c, "natural order, so comparator should be null");
    }

    @Test
    public void testTransformerDecorate() {
        final Transformer<Object, Integer> transformer = TransformerUtils.asTransformer(oneFactory);
        final SortedMap<Integer, Number> map = LazySortedMap.lazySortedMap(new TreeMap<>(), transformer);
        assertInstanceOf(LazySortedMap.class, map);
        assertThrows(NullPointerException.class, () -> LazySortedMap.lazySortedMap(new TreeMap<>(), (Transformer<Integer, Number>) null),
                "Expecting NullPointerException for null transformer");
        assertThrows(NullPointerException.class, () -> LazySortedMap.lazySortedMap((SortedMap<Integer, Number>) null, transformer),
                "Expecting NullPointerException for null map");
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/LazySortedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/LazySortedMap.fullCollection.version4.obj");
//    }

}
