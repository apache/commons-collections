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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.Transformer;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractMapTest} for exercising the
 * {@link LazyMap} implementation.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
@SuppressWarnings("boxing")
public class LazyMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    private static final Factory<Integer> oneFactory = FactoryUtils.constantFactory(1);

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    protected boolean isLazyMapTest() {
        return true;
    }

    @Override
    public LazyMap<K, V> makeObject() {
        return LazyMap.lazyMap(new HashMap<>(), FactoryUtils.<V>nullFactory());
    }

    @Test
    @Override
    public void testMapGet() {
        //TODO eliminate need for this via superclass - see svn history.
    }

    @Test
    public void testMapGetWithFactory() {
        Map<Integer, Number> map = LazyMap.lazyMap(new HashMap<>(), oneFactory);
        assertEquals(0, map.size());
        final Number i1 = map.get("Five");
        assertEquals(1, i1);
        assertEquals(1, map.size());
        final Number i2 = map.get(new String(new char[] {'F', 'i', 'v', 'e'}));
        assertEquals(1, i2);
        assertEquals(1, map.size());
        assertSame(i1, i2);

        map = LazyMap.lazyMap(new HashMap<>(), FactoryUtils.<Long>nullFactory());
        final Object o = map.get("Five");
        assertNull(o);
        assertEquals(1, map.size());
    }

    @Test
    public void testMapGetWithTransformer() {
        final Transformer<Number, Integer> intConverter = Number::intValue;
        final Map<Long, Number> map = LazyMap.lazyMap(new HashMap<>(), intConverter);
        assertEquals(0, map.size());
        final Number i1 = map.get(123L);
        assertEquals(123, i1);
        assertEquals(1, map.size());
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
