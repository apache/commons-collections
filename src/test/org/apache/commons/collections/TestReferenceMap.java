/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.Test;


/**
 * Tests for ReferenceMap. 
 *
 * @author Paul Jack
 * @version $Id: TestReferenceMap.java,v 1.2.2.1 2004/05/22 12:14:05 scolebourne Exp $
 */
public class TestReferenceMap extends TestMap {


    private static Random random = new Random();


    private Object[] hardKeys;
    private Object[] hardValues;
    private HashMap refs = new HashMap();
    

    public TestReferenceMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestReferenceMap.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestReferenceMap.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        return map;
    }

    public boolean useNullKey() {
        return false;
    }

    public boolean useNullValue() {
        return false;
    }


/*
   // Unfortunately, these tests all rely on System.gc(), which is
   // not reliable across platforms.  Not sure how to code the tests
   // without using System.gc() though...
   // They all passed on my platform though. :)

    public void testPurge() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < hard.length; i++) {
            hard[i] = new Object();
            map.put(hard[i], new Object());
        }
        System.gc();
        assertTrue("map should be empty after purge of weak values", map.isEmpty());

        for (int i = 0; i < hard.length; i++) {
            map.put(new Object(), hard[i]);
        }
        System.gc();
        assertTrue("map should be empty after purge of weak keys", map.isEmpty());

        for (int i = 0; i < hard.length; i++) {
            map.put(new Object(), hard[i]);
            map.put(hard[i], new Object());
        }

        System.gc();
        assertTrue("map should be empty after purge of weak keys and values", map.isEmpty());
    }


    public void testGetAfterGC() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        for (int i = 0; i < 10; i++) {
            map.put(new Integer(i), new Integer(i));
        }

        System.gc();
        for (int i = 0; i < 10; i++) {
            Integer I = new Integer(i);
            assertTrue("map.containsKey should return false for GC'd element", !map.containsKey(I));
            assertTrue("map.get should return null for GC'd element", map.get(I) == null);
        }
    }


    public void testEntrySetIteratorAfterGC() {
        ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
        Object[] hard = new Object[10];
        for (int i = 0; i < 10; i++) {
            hard[i] = new Integer(10 + i);
            map.put(new Integer(i), new Integer(i));
            map.put(hard[i], hard[i]);
        }

        System.gc();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer key = (Integer)entry.getKey();
            Integer value = (Integer)entry.getValue();
            assertTrue("iterator should skip GC'd keys", key.intValue() >= 10);
            assertTrue("iterator should skip GC'd values", value.intValue() >= 10);
        }

    }
*/


/*
    // Uncomment to create test files in /data/test
    public void testCreateTestFiles() throws Exception {
        ReferenceMap m = (ReferenceMap)makeEmptyMap();
        writeExternalFormToDisk(m, getCanonicalEmptyCollectionName(m));
        m = (ReferenceMap)makeFullMap();
        writeExternalFormToDisk(m, getCanonicalFullCollectionName(m));
    }
*/


    public int getCompatibilityVersion() {
        return 2; // actually 2.1, but can't represent that as an int
    }


}
