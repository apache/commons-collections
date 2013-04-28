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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

/**
 * Tests for the {@link CaseInsensitiveMap} implementation.
 *
 * @version $Id$
 */
public class CaseInsensitiveMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    public CaseInsensitiveMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(CaseInsensitiveMapTest.class);
    }

    @Override
    public CaseInsensitiveMap<K, V> makeObject() {
        return new CaseInsensitiveMap<K, V>();
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    //-------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void testCaseInsensitive() {
        final Map<K, V> map = makeObject();
        map.put((K) "One", (V) "One");
        map.put((K) "Two", (V) "Two");
        assertEquals("One", map.get("one"));
        assertEquals("One", map.get("oNe"));
        map.put((K) "two", (V) "Three");
        assertEquals("Three", map.get("Two"));
    }

    @SuppressWarnings("unchecked")
    public void testNullHandling() {
        final Map<K, V> map = makeObject();
        map.put((K) "One", (V) "One");
        map.put((K) "Two", (V) "Two");
        map.put(null, (V) "Three");
        assertEquals("Three", map.get(null));
        map.put(null, (V) "Four");
        assertEquals("Four", map.get(null));
        final Set<K> keys = map.keySet();
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains(null));
        assertEquals(3, keys.size());
    }

    public void testPutAll() {
        final Map<Object, String> map = new HashMap<Object, String>();
        map.put("One", "One");
        map.put("Two", "Two");
        map.put("one", "Three");
        map.put(null, "Four");
        map.put(Integer.valueOf(20), "Five");
        final Map<Object, String> caseInsensitiveMap = new CaseInsensitiveMap<Object, String>(map);
        assertEquals(4, caseInsensitiveMap.size()); // ones collapsed
        final Set<Object> keys = caseInsensitiveMap.keySet();
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains(null));
        assertTrue(keys.contains(Integer.toString(20)));
        assertEquals(4, keys.size());
        assertTrue(!caseInsensitiveMap.containsValue("One")
            || !caseInsensitiveMap.containsValue("Three")); // ones collaped
        assertEquals("Four", caseInsensitiveMap.get(null));
    }

    @SuppressWarnings("unchecked")
    public void testClone() {
        final CaseInsensitiveMap<K, V> map = new CaseInsensitiveMap<K, V>(10);
        map.put((K) "1", (V) "1");
        final CaseInsensitiveMap<K, V> cloned = map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get("1"), cloned.get("1"));
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/CaseInsensitiveMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/CaseInsensitiveMap.fullCollection.version4.obj");
//    }

    // COLLECTIONS-294
    public void testLocaleIndependence() {
        final Locale orig = Locale.getDefault();

        final Locale[] locales = { Locale.ENGLISH, new Locale("tr", "", ""), Locale.getDefault() };

        final String[][] data = { 
            { "i", "I" },
            { "\u03C2", "\u03C3" },
            { "\u03A3", "\u03C2" },
            { "\u03A3", "\u03C3" },
        };

        try {
            for (final Locale locale : locales) {
                Locale.setDefault(locale);
                for (int j = 0; j < data.length; j++) {
                    assertTrue("Test data corrupt: " + j, data[j][0].equalsIgnoreCase(data[j][1]));
                    final CaseInsensitiveMap<String, String> map = new CaseInsensitiveMap<String, String>();
                    map.put(data[j][0], "value");
                    assertEquals(Locale.getDefault() + ": " + j, "value", map.get(data[j][1]));
                }
            }
        } finally {
            Locale.setDefault(orig);
        }
    }

    /**
     * Test for <a href="https://issues.apache.org/jira/browse/COLLECTIONS-323">COLLECTIONS-323</a>.
     */
    public void testInitialCapacityZero() {
        final CaseInsensitiveMap<String,String> map = new CaseInsensitiveMap<String,String>(0);
        assertEquals(1, map.data.length);
    }
}
