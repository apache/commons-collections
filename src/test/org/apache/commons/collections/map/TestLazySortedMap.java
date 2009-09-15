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

import static org.apache.commons.collections.map.LazySortedMap.getLazySortedMap;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.junit.Test;

/**
 * Extension of {@link TestLazyMap} for exercising the 
 * {@link LazySortedMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Phil Steitz
 */
public class TestLazySortedMap<K, V> extends AbstractTestSortedMap<K, V> {
    
    private static final Factory<Integer> oneFactory = FactoryUtils.constantFactory(1);
   
    public TestLazySortedMap(String testName) {
        super(testName);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestLazySortedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    @Override
    public SortedMap<K,V> makeObject() {
        return getLazySortedMap(new TreeMap<K,V>(), FactoryUtils.<V>nullFactory());
    }
    
    @Override
    public boolean isSubMapViewsSerializable() {
        // TODO TreeMap sub map views have a bug in deserialization.
        return false;
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    // from TestLazyMap
    //-----------------------------------------------------------------------
    @Override
    public void testMapGet() {
        //TODO eliminate need for this via superclass - see svn history.
    }
    
    @Test
    public void mapGet() {
        Map<Integer, Number> map = getLazySortedMap(new TreeMap<Integer,Number>(), oneFactory);
        assertEquals(0, map.size());
        Number i1 = map.get(5);
        assertEquals(1, i1);
        assertEquals(1, map.size());

        map = getLazySortedMap(new TreeMap<Integer,Number>(), FactoryUtils.<Number>nullFactory());
        Number o = map.get(5);
        assertEquals(null,o);
        assertEquals(1, map.size());
        
    }
    
    //-----------------------------------------------------------------------
    public void testSortOrder() {
        SortedMap<String, Number> map = getLazySortedMap(new TreeMap<String,Number>(), oneFactory);
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
        
        Comparator<?> c = map.comparator();
        assertTrue("natural order, so comparator should be null", 
            c == null);      
    } 
    
    public void testTransformerDecorate() {
        Transformer<Object, Integer> transformer = TransformerUtils.asTransformer(oneFactory);
        SortedMap<Integer, Number> map = getLazySortedMap(new TreeMap<Integer, Number>(), transformer);     
        assertTrue(map instanceof LazySortedMap);  
         try {
            map = getLazySortedMap(new TreeMap<Integer, Number>(), (Transformer<Integer, Number>) null);
            fail("Expecting IllegalArgumentException for null transformer");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            map = getLazySortedMap((SortedMap<Integer,Number>) null, transformer);
            fail("Expecting IllegalArgumentException for null map");
        } catch (IllegalArgumentException e) {
            // expected
        } 
    }
    
    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/LazySortedMap.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/LazySortedMap.fullCollection.version3.1.obj");
//    }
}
