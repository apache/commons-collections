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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.collections.BulkTest;

/**
 * Tests for the {@link CaseInsensitiveMap} implementation.
 *
 * @version $Revision$ $Date$
 *
 * @author Commons-Collections team
 */
public class TestCaseInsensitiveMap<K, V> extends AbstractTestIterableMap<K, V> {

    public TestCaseInsensitiveMap(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestCaseInsensitiveMap.class);
    }

    public CaseInsensitiveMap<K, V> makeObject() {
        return new CaseInsensitiveMap<K, V>();
    }

    public String getCompatibilityVersion() {
        return "3";
    }

    //-------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void testCaseInsensitive() {
        Map<K, V> map = makeObject();
        map.put((K) "One", (V) "One");
        map.put((K) "Two", (V) "Two");
        assertEquals("One", map.get("one"));
        assertEquals("One", map.get("oNe"));
        map.put((K) "two", (V) "Three");
        assertEquals("Three", map.get("Two"));
    }

    @SuppressWarnings("unchecked")
    public void testNullHandling() {
        Map<K, V> map = makeObject();
        map.put((K) "One", (V) "One");
        map.put((K) "Two", (V) "Two");
        map.put(null, (V) "Three");
        assertEquals("Three", map.get(null));
        map.put(null, (V) "Four");
        assertEquals("Four", map.get(null));
        Set<K> keys = map.keySet();
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains(null));
        assertTrue(keys.size() == 3);
    }

    public void testPutAll() {
        Map<Object, String> map = new HashMap<Object, String>();
        map.put("One", "One");
        map.put("Two", "Two");
        map.put("one", "Three");
        map.put(null, "Four");
        map.put(new Integer(20), "Five");
        Map<Object, String> caseInsensitiveMap = new CaseInsensitiveMap<Object, String>(map);
        assertTrue(caseInsensitiveMap.size() == 4); // ones collapsed
        Set<Object> keys = caseInsensitiveMap.keySet();
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains(null));
        assertTrue(keys.contains(Integer.toString(20)));
        assertTrue(keys.size() == 4);
        assertTrue(!caseInsensitiveMap.containsValue("One")
            || !caseInsensitiveMap.containsValue("Three")); // ones collaped
        assertEquals(caseInsensitiveMap.get(null), "Four");
    }

    @SuppressWarnings("unchecked")
    public void testClone() {
        CaseInsensitiveMap<K, V> map = new CaseInsensitiveMap<K, V>(10);
        map.put((K) "1", (V) "1");
        CaseInsensitiveMap<K, V> cloned = map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get("1"), cloned.get("1"));
    }

    /*
    public void testCreate() throws Exception {
        resetEmpty();
        writeExternalFormToDisk((java.io.Serializable) map, "/home/phil/jakarta-commons/collections/data/test/CaseInsensitiveMap.emptyCollection.version3.obj");
        resetFull();
        writeExternalFormToDisk((java.io.Serializable) map, "/home/phil/jakarta-commons/collections/data/test/CaseInsensitiveMap.fullCollection.version3.obj");
    }
     */
}
