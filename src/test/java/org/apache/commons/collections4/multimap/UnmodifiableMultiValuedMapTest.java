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
package org.apache.commons.collections4.multimap;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;

/**
 * Tests for UnmodifiableMultiValuedMap
 * 
 * @since 4.1
 * @version $Id$
 */
public class UnmodifiableMultiValuedMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    public UnmodifiableMultiValuedMapTest(String testName) {
        super(testName);
    }

    public boolean isAddSupported() {
        return false;
    }

    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public MultiValuedMap<K, V> makeObject() {
        return UnmodifiableMultiValuedMap.<K, V> unmodifiableMultiValuedMap(new MultiValuedHashMap<K, V>());
    }

    protected MultiValuedMap<K, V> makeFullMap() {
        final MultiValuedMap<K, V> map = new MultiValuedHashMap<K, V>();
        addSampleMappings(map);
        return UnmodifiableMultiValuedMap.<K, V> unmodifiableMultiValuedMap(map);
    }

    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    public void testDecorateFactory() {
        final MultiValuedMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(map));
    }

    public void testDecoratorFactoryNullMap() {
        try {
            UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(null);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testAddException() {
        MultiValuedMap<K, V> map = makeObject();
        try {
            map.put((K) "one", (V) "uno");
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/UnmodifiableMultiValuedMap.fullCollection.version4.1.obj");
//    }

}
