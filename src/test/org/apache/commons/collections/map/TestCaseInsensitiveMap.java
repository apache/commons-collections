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
import java.util.Locale;
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
public class TestCaseInsensitiveMap extends AbstractTestIterableMap {

    public TestCaseInsensitiveMap(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestCaseInsensitiveMap.class);
    }

    public Map makeEmptyMap() {
        return new CaseInsensitiveMap();
    }
    
    public String getCompatibilityVersion() {
        return "3";
    }
   
    //-------------------------------------------------------------------------
    
    public void testCaseInsensitive() {
        Map map = new CaseInsensitiveMap();
        map.put("One", "One");
        map.put("Two", "Two");
        assertEquals("One", (String) map.get("one"));
        assertEquals("One", (String) map.get("oNe"));
        map.put("two", "Three");
        assertEquals("Three", (String) map.get("Two"));
    } 
    
    public void testNullHandling() {
        Map map = new CaseInsensitiveMap();
        map.put("One", "One");
        map.put("Two", "Two");
        map.put(null, "Three");
        assertEquals("Three", (String) map.get(null));
        map.put(null, "Four");
        assertEquals("Four", (String) map.get(null));
        Set keys = map.keySet();
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains(null));
        assertEquals(3, keys.size());
    }
        
    public void testPutAll() {
        Map map = new HashMap();
        map.put("One", "One");
        map.put("Two", "Two");
        map.put("one", "Three");
        map.put(null, "Four");
        map.put(new Integer(20), "Five");
        Map caseInsensitiveMap = new CaseInsensitiveMap(map);
        assertEquals(4, caseInsensitiveMap.size()); // ones collapsed
        Set keys = caseInsensitiveMap.keySet();
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains(null));
        assertTrue(keys.contains(Integer.toString(20)));
        assertEquals(4, keys.size());
        assertTrue(!caseInsensitiveMap.containsValue("One") 
            || !caseInsensitiveMap.containsValue("Three")); // ones collaped
        assertEquals("Four", caseInsensitiveMap.get(null));
    } 

    public void testClone() {
        CaseInsensitiveMap map = new CaseInsensitiveMap(10);
        map.put("1", "1");
        Map cloned = (Map) map.clone();
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

    // COLLECTIONS-294
    public void testLocaleIndependence() {
        Locale orig = Locale.getDefault();

        Locale[] locales = { Locale.ENGLISH, new Locale("tr", "", ""), Locale.getDefault() };

        String[][] data = { 
            { "i", "I" },
            { "\u03C2", "\u03C3" },
            { "\u03A3", "\u03C2" },
            { "\u03A3", "\u03C3" },
        };

        try {
            for (int i = 0; i < locales.length; i++) {
                Locale.setDefault(locales[i]);
                for (int j = 0; j < data.length; j++) {
                    assertTrue("Test data corrupt: " + j, data[j][0].equalsIgnoreCase(data[j][1]));
                    CaseInsensitiveMap map = new CaseInsensitiveMap();
                    map.put(data[j][0], "value");
                    assertEquals(Locale.getDefault() + ": " + j, "value", map.get(data[j][1]));
                }
            }
        } finally {
            Locale.setDefault(orig);
        }
    }

}
