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

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.collections.AbstractObjectTest;
import org.apache.commons.collections.IterableMap;

/**
 * JUnit tests.
 *
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public class IdentityMapTest<K, V> extends AbstractObjectTest {

    private static final Integer I1A = new Integer(1);
    private static final Integer I1B = new Integer(1);
    private static final Integer I2A = new Integer(2);
    private static final Integer I2B = new Integer(2);

    public IdentityMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(IdentityMapTest.class);
//        return BulkTest.makeSuite(TestIdentityMap.class);  // causes race condition!
    }

    @Override
    public IdentityMap<K, V> makeObject() {
        return new IdentityMap<K, V>();
    }

    @Override
    public String getCompatibilityVersion() {
        return "3";
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testBasics() {
        final IterableMap<K, V> map = new IdentityMap<K, V>();
        assertEquals(0, map.size());

        map.put((K) I1A, (V) I2A);
        assertEquals(1, map.size());
        assertSame(I2A, map.get(I1A));
        assertSame(null, map.get(I1B));
        assertEquals(true, map.containsKey(I1A));
        assertEquals(false, map.containsKey(I1B));
        assertEquals(true, map.containsValue(I2A));
        assertEquals(false, map.containsValue(I2B));

        map.put((K) I1A, (V) I2B);
        assertEquals(1, map.size());
        assertSame(I2B, map.get(I1A));
        assertSame(null, map.get(I1B));
        assertEquals(true, map.containsKey(I1A));
        assertEquals(false, map.containsKey(I1B));
        assertEquals(false, map.containsValue(I2A));
        assertEquals(true, map.containsValue(I2B));

        map.put((K) I1B, (V) I2B);
        assertEquals(2, map.size());
        assertSame(I2B, map.get(I1A));
        assertSame(I2B, map.get(I1B));
        assertEquals(true, map.containsKey(I1A));
        assertEquals(true, map.containsKey(I1B));
        assertEquals(false, map.containsValue(I2A));
        assertEquals(true, map.containsValue(I2B));
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testHashEntry() {
        final IterableMap<K, V> map = new IdentityMap<K, V>();

        map.put((K) I1A, (V) I2A);
        map.put((K) I1B, (V) I2A);

        final Map.Entry<K, V> entry1 = map.entrySet().iterator().next();
        final Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        final Map.Entry<K, V> entry2 = it.next();
        final Map.Entry<K, V> entry3 = it.next();

        assertEquals(true, entry1.equals(entry2));
        assertEquals(true, entry2.equals(entry1));
        assertEquals(false, entry1.equals(entry3));
    }

    /**
     * Compare the current serialized form of the Map
     * against the canonical version in SVN.
     */
    public void testEmptyMapCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final Map<K, V> map = makeObject();
        if (map instanceof Serializable && !skipSerializedCanonicalTests()) {
            @SuppressWarnings("unchecked")
            final
            Map<K, V> map2 = (Map<K, V>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(map));
            assertEquals("Map is empty", 0, map2.size());
        }
    }

    @SuppressWarnings("unchecked")
    public void testClone() {
        final IdentityMap<K, V> map = new IdentityMap<K, V>(10);
        map.put((K) "1", (V) "1");
        final Map<K, V> cloned = map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get("1"), cloned.get("1"));
    }

//    public void testCreate() throws Exception {
//        Map map = new IdentityMap();
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/IdentityMap.emptyCollection.version3.obj");
//        map = new IdentityMap();
//        map.put(I1A, I2A);
//        map.put(I1B, I2A);
//        map.put(I2A, I2B);
//        writeExternalFormToDisk((java.io.Serializable) map, "D:/dev/collections/data/test/IdentityMap.fullCollection.version3.obj");
//    }

    /**
     * Test for <a href="https://issues.apache.org/jira/browse/COLLECTIONS-323">COLLECTIONS-323</a>.
     */
    public void testInitialCapacityZero() {
        final IdentityMap<String,String> map = new IdentityMap<String,String>(0);
        assertEquals(1, map.data.length);
    }
}
