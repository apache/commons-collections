/*
 *  Copyright 2003-2004 The Apache Software Foundation
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

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;

/**
 * Extension of {@link TestLazyMap} for exercising the 
 * {@link LazySortedMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2004/02/18 01:20:38 $
 * 
 * @author Phil Steitz
 */
public class TestLazySortedMap extends TestLazyMap {
    
    public TestLazySortedMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestLazySortedMap.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestLazySortedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
 //-------------------------------------------------------------------
    
    protected SortedMap decorateMap(SortedMap map, Factory factory) {
        return LazySortedMap.decorate(map, factory);
    }
    
    public Map makeEmptyMap() {
        return decorateMap(new TreeMap(), nullFactory);
    }
    
    public boolean isAllowNullKey() {
        return false;
    }
    
//--------------------------------------------------------------------   
    
    protected SortedMap makeTestSortedMap(Factory factory) {
        return decorateMap(new TreeMap(), factory);
    }
    
    public void testSortOrder() {
        SortedMap map = makeTestSortedMap(oneFactory);
        map.put("A",  "a");
        map.get("B"); // Entry with value "One" created
        map.put("C", "c");
        assertEquals("First key should be A", map.firstKey(), "A");
        assertEquals("Last key should be C", map.lastKey(), "C");
        assertEquals("First key in tail map should be B", 
            map.tailMap("B").firstKey(), "B");
        assertEquals("Last key in head map should be B", 
            map.headMap("C").lastKey(), "B");
        assertEquals("Last key in submap should be B",
           map.subMap("A","C").lastKey(), "B");
        
        Comparator c = map.comparator();
        assertTrue("natural order, so comparator should be null", 
            c == null);      
    } 
    
    public void testTransformerDecorate() {
        Transformer transformer = TransformerUtils.asTransformer(oneFactory);
        SortedMap map = LazySortedMap.decorate(new TreeMap(), transformer);     
        assertTrue(map instanceof LazySortedMap);  
         try {
            map = LazySortedMap.decorate(new TreeMap(), (Transformer) null);
            fail("Expecting IllegalArgumentException for null transformer");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            map = LazySortedMap.decorate(null, transformer);
            fail("Expecting IllegalArgumentException for null map");
        } catch (IllegalArgumentException e) {
            // expected
        } 
    }
}