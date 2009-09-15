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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.Assert;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * Extension of {@link AbstractTestMap} for exercising the 
 * {@link CompositeMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Brian McCallister
 */
public class TestCompositeMap<K, V> extends AbstractTestIterableMap<K, V> {
    /** used as a flag in MapMutator tests */
    private boolean pass = false;
    
    public TestCompositeMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestCompositeMap.class);
    }
    
    public void setUp() throws Exception {
        super.setUp();
        this.pass = false;
    }
    
    public static void main(String args[]) {
        String[] testCaseName = {TestCompositeMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public CompositeMap<K, V> makeObject() {
        CompositeMap<K, V> map = new CompositeMap<K, V>();
        map.addComposited(new HashMap<K, V>());
        map.setMutator( new EmptyMapMutator() );
        return map;
    }
    
    @SuppressWarnings("unchecked")
    private Map<K, V> buildOne() {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put((K) "1", (V) "one");
        map.put((K) "2", (V) "two");
        return map;
    }
    
    @SuppressWarnings("unchecked")
    public Map<K, V> buildTwo() {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put((K) "3", (V) "three");
        map.put((K) "4", (V) "four");
        return map;
    }
    
    public void testGet() {
        CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        Assert.assertEquals("one", map.get("1"));
        Assert.assertEquals("four", map.get("4"));
    }
    
    @SuppressWarnings("unchecked")
    public void testAddComposited() {
        CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        HashMap<K, V> three = new HashMap<K, V>();
        three.put((K) "5", (V) "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));
        try {
            map.addComposited(three);
            fail("Expecting IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    @SuppressWarnings("unchecked")
    public void testRemoveComposited() {
        CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        HashMap<K, V> three = new HashMap<K, V>();
        three.put((K) "5", (V) "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));
        
        map.removeComposited(three);
        assertFalse(map.containsKey("5"));
        
        map.removeComposited(buildOne());
        assertFalse(map.containsKey("2"));
        
    }
    
    @SuppressWarnings("unchecked")
    public void testRemoveFromUnderlying() {
        CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        HashMap<K, V> three = new HashMap<K, V>();
        three.put((K) "5", (V) "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));
        
        //Now remove "5"
        three.remove("5");
        assertFalse(map.containsKey("5"));
    }
    
    @SuppressWarnings("unchecked")
    public void testRemoveFromComposited() {
        CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo());
        HashMap<K, V> three = new HashMap<K, V>();
        three.put((K) "5", (V) "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));
        
        //Now remove "5"
        map.remove("5");
        assertFalse(three.containsKey("5"));
    }
    
    public void testResolveCollision() {
        CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo(), 
            new CompositeMap.MapMutator<K, V>() {
            public void resolveCollision(CompositeMap<K, V> composite,
            Map<K, V> existing,
            Map<K, V> added,
            Collection<K> intersect) {
                pass = true;
            }
            
            public V put(CompositeMap<K, V> map, Map<K, V>[] composited, K key, 
                V value) {
                throw new UnsupportedOperationException();
            }
            
            public void putAll(CompositeMap<K, V> map, Map<K, V>[] composited, Map<? extends K, ? extends V> t) {
                throw new UnsupportedOperationException();
            }
        });
        
        map.addComposited(buildOne());
        assertTrue(pass);
    }
    
    @SuppressWarnings("unchecked")
    public void testPut() {
        CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo(), 
            new CompositeMap.MapMutator<K, V>() {
            public void resolveCollision(CompositeMap<K, V> composite,
            Map<K, V> existing,
            Map<K, V> added,
            Collection<K> intersect) {
                throw new UnsupportedOperationException();
            }
            
            public V put(CompositeMap<K, V> map, Map<K, V>[] composited, K key, 
                V value) {
                pass = true;
                return (V) "foo";
            }
            
            public void putAll(CompositeMap<K, V> map, Map<K, V>[] composited, Map<? extends K, ? extends V> t) {
                throw new UnsupportedOperationException();
            }
        });
        
        map.put((K) "willy", (V) "wonka");
        assertTrue(pass);
    }
    
    public void testPutAll() {
        CompositeMap<K, V> map = new CompositeMap<K, V>(buildOne(), buildTwo(), 
            new CompositeMap.MapMutator<K, V>() {
            public void resolveCollision(CompositeMap<K, V> composite,
            Map<K, V> existing,
            Map<K, V> added,
            Collection<K> intersect) {
                throw new UnsupportedOperationException();
            }
            
            public V put(CompositeMap<K, V> map, Map<K, V>[] composited, K key, 
                V value) {
                throw new UnsupportedOperationException();
            }
            
            public void putAll(CompositeMap<K, V> map, Map<K, V>[] composited, Map<? extends K, ? extends V> t) {
                pass = true;
            }
        });
        
        map.putAll(null);
        assertTrue(pass);
    }

    public String getCompatibilityVersion() {
        return "3.3";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "/tmp/CompositeMap.emptyCollection.version3.3.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "/tmp/CompositeMap.fullCollection.version3.3.obj");
//    }

}
