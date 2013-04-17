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
package org.apache.commons.collections4.map;

import static org.apache.commons.collections4.map.LazyMap.lazyMap;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.map.LazyMap;
import org.junit.Test;

/**
 * Extension of {@link AbstractMapTest} for exercising the 
 * {@link LazyMap} implementation.
 *
 * @since 3.0
 * @version $Id$
 */
@SuppressWarnings("boxing")
public class LazyMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    private static final Factory<Integer> oneFactory = FactoryUtils.constantFactory(1);

    public LazyMapTest(final String testName) {
        super(testName);
    }

    @Override
    public LazyMap<K,V> makeObject() {
        return lazyMap(new HashMap<K,V>(), FactoryUtils.<V>nullFactory());
    }

    //-----------------------------------------------------------------------
    @Override
    public void testMapGet() {
        //TODO eliminate need for this via superclass - see svn history.
    }

    @Test
    public void mapGetWithFactory() {
        Map<Integer, Number> map = lazyMap(new HashMap<Integer,Number>(), oneFactory);
        assertEquals(0, map.size());
        final Number i1 = map.get("Five");
        assertEquals(1, i1);
        assertEquals(1, map.size());
        final Number i2 = map.get(new String(new char[] {'F','i','v','e'}));
        assertEquals(1, i2);
        assertEquals(1, map.size());
        assertSame(i1, i2);

        map = lazyMap(new HashMap<Integer,Number>(), FactoryUtils.<Long>nullFactory());
        final Object o = map.get("Five");
        assertEquals(null,o);
        assertEquals(1, map.size());
    }

    @Test
    public void mapGetWithTransformer() {
        final Transformer<Number, Integer> intConverter = new Transformer<Number, Integer>(){
            public Integer transform(final Number input) {
                return input.intValue();
            }
        };
        final Map<Long, Number> map = lazyMap(new HashMap<Long,Number>(), intConverter );
        assertEquals(0, map.size());
        final Number i1 = map.get(123L);
        assertEquals(123, i1);
        assertEquals(1, map.size());
    }


    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/LazyMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/LazyMap.fullCollection.version4.obj");
//    }

}
