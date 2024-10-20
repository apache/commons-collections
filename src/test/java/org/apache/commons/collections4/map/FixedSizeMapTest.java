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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.IterableMap;

/**
 * Extension of {@link AbstractMapTest} for exercising the {@link FixedSizeMap}
 * implementation.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class FixedSizeMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    public FixedSizeMapTest() {
        super(FixedSizeMapTest.class.getSimpleName());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public boolean isPutAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public IterableMap<K, V> makeFullMap() {
        final Map<K, V> map = new HashMap<>();
        addSampleMappings(map);
        return FixedSizeMap.fixedSizeMap(map);
    }

    @Override
    public IterableMap<K, V> makeObject() {
        return FixedSizeMap.fixedSizeMap(new HashMap<>());
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/FixedSizeMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/FixedSizeMap.fullCollection.version4.obj");
//    }

}
