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

import static org.apache.commons.collections4.map.LazySortedMap.*;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.TransformerUtils;
import org.junit.Test;

/**
 * Extension of {@link LazyMapTest} for exercising the
 * {@link LazySortedMap} implementation.
 *
 * @since 3.0
 */
@SuppressWarnings("boxing")
public class LazySortedMapTest<K, V> extends AbstractSortedMapTest<K, V> {

    private static final Factory<Integer> oneFactory = FactoryUtils.constantFactory(1);

    public LazySortedMapTest(final String testName) {
        super(testName);
    }

    @Override
    public SortedMap<K,V> makeObject() {
        return lazySortedMap(new TreeMap<K,V>(), FactoryUtils.<V>nullFactory());
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    // from LazyMapTest
    //-----------------------------------------------------------------------
    @Override
    public void testMapGet() {
        //TODO eliminate need for this via superclass - see svn history.
    }

    @Test
    public void mapGet() {
        Map<Integer, Number> map = lazySortedMap(new TreeMap<Integer,Number>(), oneFactory);
        assertEquals(0, map.size());
        final Number i1 = map.get(5);
        assertEquals(1, i1);
        assertEquals(1, map.size());

        map = lazySortedMap(new TreeMap<Integer,Number>(), FactoryUtils.<Number>nullFactory());
        final Number o = map.get(5);
        assertEquals(null,o);
        assertEquals(1, map.size());

    }

    //-----------------------------------------------------------------------
    public void testSortOrder() {
        final SortedMap<String, Number> map = lazySortedMap(new TreeMap<String,Number>(), oneFactory);
        map.put("A",  5);
        map.get("B"); // Entry with value "One" created
        map.put("C", 8);
        assertEquals("First key should be A", "A", map.firstKey());
        assertEquals("Last key should be C", "C", map.lastKey());
        assertEquals("First key in tail map should be B",
            "B", map.tailMap("B").firstKey());
        assertEquals("Last key in head map should be B",
            "B", map.headMap("C").lastKey());
        assertEquals("Last key in submap should be B",
            "B", map.subMap("A","C").lastKey());

        final Comparator<?> c = map.comparator();
        assertTrue("natural order, so comparator should be null",
            c == null);
    }

    public void testTransformerDecorate() {
        final Transformer<Object, Integer> transformer = TransformerUtils.asTransformer(oneFactory);
        SortedMap<Integer, Number> map = lazySortedMap(new TreeMap<Integer, Number>(), transformer);
        assertTrue(map instanceof LazySortedMap);
         try {
            map = lazySortedMap(new TreeMap<Integer, Number>(), (Transformer<Integer, Number>) null);
            fail("Expecting NullPointerException for null transformer");
        } catch (final NullPointerException e) {
            // expected
        }
        try {
            map = lazySortedMap((SortedMap<Integer,Number>) null, transformer);
            fail("Expecting NullPointerException for null map");
        } catch (final NullPointerException e) {
            // expected
        }
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
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
