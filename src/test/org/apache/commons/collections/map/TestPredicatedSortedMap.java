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

import org.apache.commons.collections.Predicate;

/**
 * Extension of {@link TestPredicatedMap} for exercising the 
 * {@link PredicatedSortedMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2004/02/18 01:20:38 $
 * 
 * @author Phil Steitz
 */
public class TestPredicatedSortedMap extends TestPredicatedMap{
    
    public TestPredicatedSortedMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestPredicatedSortedMap.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestPredicatedSortedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
 //-------------------------------------------------------------------    
    
    protected SortedMap decorateMap(SortedMap map, Predicate keyPredicate, 
        Predicate valuePredicate) {
        return PredicatedSortedMap.decorate(map, keyPredicate, valuePredicate);
    }
    
    public Map makeEmptyMap() {
        return decorateMap(new TreeMap(), truePredicate, truePredicate);
    }
   
    public Map makeTestMap() {
        return decorateMap(new TreeMap(), testPredicate, testPredicate);
    } 
    
    public boolean isAllowNullKey() {
        return false;
    }
    
//--------------------------------------------------------------------   
    
    public SortedMap makeTestSortedMap() {
        return decorateMap(new TreeMap(), testPredicate, testPredicate);
    }
    
    public void testSortOrder() {
        SortedMap map = makeTestSortedMap();
        map.put("A",  "a");
        map.put("B", "b");
        try {
            map.put(null, "c");
            fail("Null key should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }
        map.put("C", "c");
        try {
            map.put("D", null);
            fail("Null value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }
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
        
}