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

import static org.apache.commons.collections.map.LazyMap.getLazyMap;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.Transformer;
import org.junit.Test;

/**
 * Extension of {@link AbstractTestMap} for exercising the 
 * {@link LazyMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Phil Steitz
 */
public class TestLazyMap<K, V> extends AbstractTestIterableMap<K, V> {

    private static final Factory<Integer> oneFactory = FactoryUtils.constantFactory(1);

    public TestLazyMap(String testName) {
        super(testName);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestLazyMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    @Override
    public LazyMap<K,V> makeObject() {
        return getLazyMap(new HashMap<K,V>(), FactoryUtils.<V>nullFactory());
    }

    //-----------------------------------------------------------------------
    @Override
    public void testMapGet() {
        //TODO eliminate need for this via superclass - see svn history.
    }

    @Test
    public void mapGetWithFactory() {
        Map<Integer, Number> map = getLazyMap(new HashMap<Integer,Number>(), oneFactory);
        assertEquals(0, map.size());
        Number i1 = map.get("Five");
        assertEquals(1, i1);
        assertEquals(1, map.size());
        Number i2 = map.get(new String(new char[] {'F','i','v','e'}));
        assertEquals(1, i2);
        assertEquals(1, map.size());
        assertSame(i1, i2);

        map = getLazyMap(new HashMap<Integer,Number>(), FactoryUtils.<Long>nullFactory());
        Object o = map.get("Five");
        assertEquals(null,o);
        assertEquals(1, map.size());
    }

    @Test
    public void mapGetWithTransformer() {
        Transformer<Number, Integer> intConverter = new Transformer<Number, Integer>(){
            public Integer transform(Number input) {
                return input.intValue();
            }
        };
        Map<Long, Number> map = getLazyMap(new HashMap<Long,Number>(), intConverter );
        assertEquals(0, map.size());
        Number i1 = map.get(123L);
        assertEquals(123, i1);
        assertEquals(1, map.size());
    }


    @Override
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
