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
 * @version $Revision: 1.8 $ $Date: 2004/05/12 19:51:28 $
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
    
    public void testRemoveLRU() {
        MockLRUMapSubclass map = new MockLRUMapSubclass(2);
        assertNull(map.entry);
        map.put("A", "a");
        assertNull(map.entry);
        map.put("B", "b");
        assertNull(map.entry);
        map.put("C", "c");  // removes oldest, which is A=a
        assertNotNull(map.entry);
        assertEquals("A", map.key);
        assertEquals("a", map.value);
        assertEquals("C", map.entry.getKey());  // entry is reused
        assertEquals("c", map.entry.getValue());  // entry is reused
        assertEquals(false, map.containsKey("A"));
        assertEquals(true, map.containsKey("B"));
        assertEquals(true, map.containsKey("C"));
    }
    
    static class MockLRUMapSubclass extends LRUMap {
        LinkEntry entry;
        Object key;
        Object value;

        MockLRUMapSubclass(int size) {
            super(size);
        }

        protected boolean removeLRU(LinkEntry entry) {
            this.entry = entry;
            this.key = entry.getKey();
            this.value = entry.getValue();
            return true;
        }
    }
    
    public void testRemoveLRUBlocksRemove() {
        MockLRUMapSubclassBlocksRemove map = new MockLRUMapSubclassBlocksRemove(2, false);
        assertEquals(0, map.size());
        map.put("A", "a");
        assertEquals(1, map.size());
        map.put("B", "b");
        assertEquals(2, map.size());
        map.put("C", "c");  // should remove oldest, which is A=a, but this is blocked
        assertEquals(3, map.size());
        assertEquals(2, map.maxSize());
        assertEquals(true, map.containsKey("A"));
        assertEquals(true, map.containsKey("B"));
        assertEquals(true, map.containsKey("C"));
    }

    public void testRemoveLRUBlocksRemoveScan() {
        MockLRUMapSubclassBlocksRemove map = new MockLRUMapSubclassBlocksRemove(2, true);
        assertEquals(0, map.size());
        map.put("A", "a");
        assertEquals(1, map.size());
        map.put("B", "b");
        assertEquals(2, map.size());
        map.put("C", "c");  // should remove oldest, which is A=a, but this is blocked
        assertEquals(3, map.size());
        assertEquals(2, map.maxSize());
        assertEquals(true, map.containsKey("A"));
        assertEquals(true, map.containsKey("B"));
        assertEquals(true, map.containsKey("C"));
    }
    
    static class MockLRUMapSubclassBlocksRemove extends LRUMap {
        MockLRUMapSubclassBlocksRemove(int size, boolean scanUntilRemove) {
            super(size, scanUntilRemove);
        }

        protected boolean removeLRU(LinkEntry entry) {
            return false;
        }
    }
    
    public void testRemoveLRUFirstBlocksRemove() {
        MockLRUMapSubclassFirstBlocksRemove map = new MockLRUMapSubclassFirstBlocksRemove(2);
        assertEquals(0, map.size());
        map.put("A", "a");
        assertEquals(1, map.size());
        map.put("B", "b");
        assertEquals(2, map.size());
        map.put("C", "c");  // should remove oldest, which is A=a  but this is blocked - so advance to B=b
        assertEquals(2, map.size());
        assertEquals(2, map.maxSize());
        assertEquals(true, map.containsKey("A"));
        assertEquals(false, map.containsKey("B"));
        assertEquals(true, map.containsKey("C"));
    }

    static class MockLRUMapSubclassFirstBlocksRemove extends LRUMap {
        MockLRUMapSubclassFirstBlocksRemove(int size) {
            super(size, true);
        }

        protected boolean removeLRU(LinkEntry entry) {
            if ("a".equals(entry.getValue())) {
                return false;
            } else {
                return true;
            }
        }
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LRUMap.emptyCollection.version3.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/LRUMap.fullCollection.version3.obj");
//    }
}
