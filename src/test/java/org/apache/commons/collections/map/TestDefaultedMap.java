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

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantFactory;

/**
 * Extension of {@link AbstractTestMap} for exercising the 
 * {@link DefaultedMap} implementation.
 *
 * @since Commons Collections 3.2
 * @version $Revision: 155406 $ $Date$
 *
 * @author Stephen Colebourne
 */
public class TestDefaultedMap<K, V> extends AbstractTestIterableMap<K, V> {

    protected final Factory<V> nullFactory = FactoryUtils.<V>nullFactory();

    public TestDefaultedMap(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public IterableMap<K, V> makeObject() {
        return DefaultedMap.defaultedMap(new HashMap<K, V>(), nullFactory);
    }

    //-----------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
    public void testMapGet() {
        Map<K, V> map = new DefaultedMap<K, V>((V) "NULL");

        assertEquals(0, map.size());
        assertEquals(false, map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));

        map.put((K) "Key", (V) "Value");
        assertEquals(1, map.size());
        assertEquals(true, map.containsKey("Key"));
        assertEquals("Value", map.get("Key"));
        assertEquals(false, map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
    }

    @SuppressWarnings("unchecked")
    public void testMapGet2() {
        HashMap<K, V> base = new HashMap<K, V>();
        Map<K, V> map = DefaultedMap.defaultedMap(base, (V) "NULL");

        assertEquals(0, map.size());
        assertEquals(0, base.size());
        assertEquals(false, map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));

        map.put((K) "Key", (V) "Value");
        assertEquals(1, map.size());
        assertEquals(1, base.size());
        assertEquals(true, map.containsKey("Key"));
        assertEquals("Value", map.get("Key"));
        assertEquals(false, map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
    }

    @SuppressWarnings("unchecked")
    public void testMapGet3() {
        HashMap<K, V> base = new HashMap<K, V>();
        Map<K, V> map = DefaultedMap.defaultedMap(base, ConstantFactory.constantFactory((V) "NULL"));

        assertEquals(0, map.size());
        assertEquals(0, base.size());
        assertEquals(false, map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));

        map.put((K) "Key", (V) "Value");
        assertEquals(1, map.size());
        assertEquals(1, base.size());
        assertEquals(true, map.containsKey("Key"));
        assertEquals("Value", map.get("Key"));
        assertEquals(false, map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
    }

    @SuppressWarnings("unchecked")
    public void testMapGet4() {
        HashMap<K, V> base = new HashMap<K, V>();
        Map<K, V> map = DefaultedMap.defaultedMap(base, new Transformer<K, V>() {
            public V transform(K input) {
                if (input instanceof String) {
                    return (V) "NULL";
                }
                return (V) "NULL_OBJECT";
            }
        });

        assertEquals(0, map.size());
        assertEquals(0, base.size());
        assertEquals(false, map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
        assertEquals("NULL_OBJECT", map.get(new Integer(0)));

        map.put((K) "Key", (V) "Value");
        assertEquals(1, map.size());
        assertEquals(1, base.size());
        assertEquals(true, map.containsKey("Key"));
        assertEquals("Value", map.get("Key"));
        assertEquals(false, map.containsKey("NotInMap"));
        assertEquals("NULL", map.get("NotInMap"));
        assertEquals("NULL_OBJECT", map.get(new Integer(0)));
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.2";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "c:/commons/collections/data/test/DefaultedMap.emptyCollection.version3.2.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "c:/commons/collections/data/test/DefaultedMap.fullCollection.version3.2.obj");
//    }

}
