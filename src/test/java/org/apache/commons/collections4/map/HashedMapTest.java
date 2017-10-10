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

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;

/**
 * JUnit tests.
 *
 */
public class HashedMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    public HashedMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(HashedMapTest.class);
    }

    @Override
    public HashedMap<K, V> makeObject() {
        return new HashedMap<>();
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @SuppressWarnings("unchecked")
    public void testClone() {
        final HashedMap<K, V> map = new HashedMap<>(10);
        map.put((K) "1", (V) "1");
        final HashedMap<K, V> cloned = map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get("1"), cloned.get("1"));
    }

    public void testInternalState() {
        final HashedMap<K, V> map = new HashedMap<>(42, 0.75f);
        assertEquals(0.75f, map.loadFactor, 0.1f);
        assertEquals(0, map.size);
        assertEquals(64, map.data.length);
        assertEquals(48, map.threshold);
        assertEquals(0, map.modCount);
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/HashedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/HashedMap.fullCollection.version4.obj");
//    }

    /**
     * Test for <a href="https://issues.apache.org/jira/browse/COLLECTIONS-323">COLLECTIONS-323</a>.
     */
    public void testInitialCapacityZero() {
        final HashedMap<String,String> map = new HashedMap<>(0);
        assertEquals(1, map.data.length);
    }
}
