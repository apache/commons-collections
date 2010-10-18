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

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.Unmodifiable;

/**
 * Extension of {@link AbstractTestOrderedMap} for exercising the
 * {@link UnmodifiableOrderedMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public class TestUnmodifiableOrderedMap<K, V> extends AbstractTestOrderedMap<K, V> {

    public TestUnmodifiableOrderedMap(String testName) {
        super(testName);
    }

    //-------------------------------------------------------------------

    @Override
    public OrderedMap<K, V> makeObject() {
        return UnmodifiableOrderedMap.decorate(ListOrderedMap.decorate(new HashMap<K, V>()));
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
    public OrderedMap<K, V> makeFullMap() {
        OrderedMap<K, V> m = ListOrderedMap.decorate(new HashMap<K, V>());
        addSampleMappings(m);
        return UnmodifiableOrderedMap.decorate(m);
    }

    //-----------------------------------------------------------------------
    public void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

    public void testDecorateFactory() {
        OrderedMap<K, V> map = makeFullMap();
        assertSame(map, UnmodifiableOrderedMap.decorate(map));

        try {
            UnmodifiableOrderedMap.decorate(null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/UnmodifiableOrderedMap.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/UnmodifiableOrderedMap.fullCollection.version3.1.obj");
//    }
}
