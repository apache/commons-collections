/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.ResettableIterator;

/**
 * JUnit tests.
 * 
 * @version $Revision: 1.6 $ $Date: 2004/02/27 00:25:14 $
 * 
 * @author Stephen Colebourne
 */
public class TestLRUMap extends AbstractTestOrderedMap {

    public TestLRUMap(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestLRUMap.class);
    }

    public Map makeEmptyMap() {
        return new LRUMap();
    }

    public boolean isGetStructuralModify() {
        return true;
    }
    
    public String getCompatibilityVersion() {
        return "3";
    }

    //-----------------------------------------------------------------------
    public void testLRU() {
        if (isPutAddSupported() == false || isPutChangeSupported() == false) return;
        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();
        Iterator it = null;
        
        LRUMap map = new LRUMap(2);
        assertEquals(0, map.size());
        assertEquals(false, map.isFull());
        assertEquals(2, map.maxSize());
        
        map.put(keys[0], values[0]);
        assertEquals(1, map.size());
        assertEquals(false, map.isFull());
        assertEquals(2, map.maxSize());
        
        map.put(keys[1], values[1]);
        assertEquals(2, map.size());
        assertEquals(true, map.isFull());
        assertEquals(2, map.maxSize());
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[1], it.next());

        map.put(keys[2], values[2]);
        assertEquals(2, map.size());
        assertEquals(true, map.isFull());
        assertEquals(2, map.maxSize());
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[2], it.next());
        it = map.values().iterator();
        assertSame(values[1], it.next());
        assertSame(values[2], it.next());

        map.put(keys[2], values[0]);
        assertEquals(2, map.size());
        assertEquals(true, map.isFull());
        assertEquals(2, map.maxSize());
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[2], it.next());
        it = map.values().iterator();
        assertSame(values[1], it.next());
        assertSame(values[0], it.next());

        map.put(keys[1], values[3]);
        assertEquals(2, map.size());
        assertEquals(true, map.isFull());
        assertEquals(2, map.maxSize());
        it = map.keySet().iterator();
        assertSame(keys[2], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[3], it.next());
    }
    
    //-----------------------------------------------------------------------    
    public void testReset() {
        resetEmpty();
        OrderedMap ordered = (OrderedMap) map;
        ((ResettableIterator) ordered.mapIterator()).reset();
        
        resetFull();
        ordered = (OrderedMap) map;
        List list = new ArrayList(ordered.keySet());
        ResettableIterator it = (ResettableIterator) ordered.mapIterator();
        assertSame(list.get(0), it.next());
        assertSame(list.get(1), it.next());
        it.reset();
        assertSame(list.get(0), it.next());
    }
    
    //-----------------------------------------------------------------------
    public void testAccessOrder() {
        if (isPutAddSupported() == false || isPutChangeSupported() == false) return;
        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();
        Iterator it = null;
        
        resetEmpty();
        map.put(keys[0], values[0]);
        map.put(keys[1], values[1]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[1], it.next());

        // no change to order
        map.put(keys[1], values[1]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[1], it.next());

        // no change to order
        map.put(keys[1], values[2]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[0], it.next());
        assertSame(values[2], it.next());

        // change to order
        map.put(keys[0], values[3]);
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[0], it.next());
        it = map.values().iterator();
        assertSame(values[2], it.next());
        assertSame(values[3], it.next());

        // change to order
        map.get(keys[1]);
        it = map.keySet().iterator();
        assertSame(keys[0], it.next());
        assertSame(keys[1], it.next());
        it = map.values().iterator();
        assertSame(values[3], it.next());
        assertSame(values[2], it.next());

        // change to order
        map.get(keys[0]);
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[0], it.next());
        it = map.values().iterator();
        assertSame(values[2], it.next());
        assertSame(values[3], it.next());

        // no change to order
        map.get(keys[0]);
        it = map.keySet().iterator();
        assertSame(keys[1], it.next());
        assertSame(keys[0], it.next());
        it = map.values().iterator();
        assertSame(values[2], it.next());
        assertSame(values[3], it.next());
    }
    
    public void testClone() {
        LRUMap map = new LRUMap(10);
        map.put("1", "1");
        Map cloned = (Map) map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get("1"), cloned.get("1"));
    }
    
//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LRUMap.emptyCollection.version3.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LRUMap.fullCollection.version3.obj");
//    }
}
