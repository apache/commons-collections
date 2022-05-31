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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.Unmodifiable;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractMapTest} for exercising the
 * {@link UnmodifiableMap} implementation.
 *
 * @since 3.0
 */
public class UnmodifiableMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    public UnmodifiableMapTest() {
        super(UnmodifiableMapTest.class.getSimpleName());
    }

    @Override
    public IterableMap<K, V> makeObject() {
        return (IterableMap<K, V>) UnmodifiableMap.unmodifiableMap(new HashMap<K, V>());
    }

    @Override
    public boolean isPutChangeSupported() {
        return false;
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
        final Map<K, V> m = new HashMap<>();
        addSampleMappings(m);
        return (IterableMap<K, V>) UnmodifiableMap.unmodifiableMap(m);
    }

    @Test
    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    @Test
    public void testDecorateFactory() {
        final Map<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableMap.unmodifiableMap(map));

        assertThrows(NullPointerException.class, () -> UnmodifiableMap.unmodifiableMap(null));
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/UnmodifiableMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/UnmodifiableMap.fullCollection.version4.obj");
//    }

}
