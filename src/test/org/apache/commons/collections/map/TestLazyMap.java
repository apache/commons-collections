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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;

/**
 * Extension of {@link TestMap} for exercising the 
 * {@link LazyMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.7 $ $Date: 2004/04/09 09:39:16 $
 * 
 * @author Phil Steitz
 */
public class TestLazyMap extends AbstractTestMap {
    
    protected static final Factory oneFactory = FactoryUtils.constantFactory("One");
    protected static final Factory nullFactory = FactoryUtils.nullFactory();
    
    public TestLazyMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestLazyMap.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestLazyMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    //-----------------------------------------------------------------------    
    protected Map decorateMap(Map map, Factory factory) {
        return LazyMap.decorate(map, factory);
    }
    
    public Map makeEmptyMap() {
        return decorateMap(new HashMap(), nullFactory);
    }
    
    protected Map makeTestMap(Factory factory) {
        return decorateMap(new HashMap(), factory);
    }

    //-----------------------------------------------------------------------    
    public void testMapGet() {
        Map map = makeTestMap(oneFactory);
        assertEquals(0, map.size());
        String s1 = (String) map.get("Five");
        assertEquals("One", s1);
        assertEquals(1, map.size());
        String s2 = (String) map.get(new String(new char[] {'F','i','v','e'}));
        assertEquals("One", s2);
        assertEquals(1, map.size());
        assertSame(s1, s2);
        
        map = makeTestMap(nullFactory);
        Object o = map.get("Five");
        assertEquals(null,o);
        assertEquals(1, map.size());
        
    }
    
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/LazyMap.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/LazyMap.fullCollection.version3.1.obj");
//    }
}