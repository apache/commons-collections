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
import java.util.concurrent.TimeUnit;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.map.PassiveExpiringMap.ExpirationPolicy;

/**
 * JUnit tests.
 *
 * @since 4.0
 */
public class PassiveExpiringMapTest<K, V> extends AbstractMapTest<K, V> {

    private static class TestExpirationPolicy
        implements ExpirationPolicy<Integer, String> {

        private static final long serialVersionUID = 1L;

        @Override
        public long expirationTime(final Integer key, final String value) {
            // odd keys expire immediately, even keys never expire
            if (key == null) {
                return 0;
            }

            if (key.intValue() % 2 == 0) {
                return -1;
            }

            return 0;
        }
    }

    public static Test suite() {
        return BulkTest.makeSuite(PassiveExpiringMapTest.class);
    }

    public PassiveExpiringMapTest(final String testName) {
        super(testName);
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/PassiveExpiringMap.emptyCollection.version4.obj");
//
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/PassiveExpiringMap.fullCollection.version4.obj");
//    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    private Map<Integer, String> makeDecoratedTestMap() {
        final Map<Integer, String> m = new HashMap<>();
        m.put(Integer.valueOf(1), "one");
        m.put(Integer.valueOf(2), "two");
        m.put(Integer.valueOf(3), "three");
        m.put(Integer.valueOf(4), "four");
        m.put(Integer.valueOf(5), "five");
        m.put(Integer.valueOf(6), "six");
        return new PassiveExpiringMap<>(new TestExpirationPolicy(), m);
    }

    @Override
    public Map<K, V> makeObject() {
        return new PassiveExpiringMap<>();
    }

    private Map<Integer, String> makeTestMap() {
        final Map<Integer, String> m =
                new PassiveExpiringMap<>(new TestExpirationPolicy());
        m.put(Integer.valueOf(1), "one");
        m.put(Integer.valueOf(2), "two");
        m.put(Integer.valueOf(3), "three");
        m.put(Integer.valueOf(4), "four");
        m.put(Integer.valueOf(5), "five");
        m.put(Integer.valueOf(6), "six");
        return m;
    }

    public void testConstructors() {
        try {
            final Map<String, String> map = null;
            new PassiveExpiringMap<>(map);
            fail("constructor - exception should have been thrown.");
        } catch (final NullPointerException ex) {
            // success
        }

        try {
            final ExpirationPolicy<String, String> policy = null;
            new PassiveExpiringMap<>(policy);
            fail("constructor - exception should have been thrown.");
        } catch (final NullPointerException ex) {
            // success
        }

        try {
            final TimeUnit unit = null;
            new PassiveExpiringMap<String, String>(10L, unit);
            fail("constructor - exception should have been thrown.");
        } catch (final NullPointerException ex) {
            // success
        }
    }

    public void testContainsKey() {
        final Map<Integer, String> m = makeTestMap();
        assertFalse(m.containsKey(Integer.valueOf(1)));
        assertFalse(m.containsKey(Integer.valueOf(3)));
        assertFalse(m.containsKey(Integer.valueOf(5)));
        assertTrue(m.containsKey(Integer.valueOf(2)));
        assertTrue(m.containsKey(Integer.valueOf(4)));
        assertTrue(m.containsKey(Integer.valueOf(6)));
    }

    public void testContainsValue() {
        final Map<Integer, String> m = makeTestMap();
        assertFalse(m.containsValue("one"));
        assertFalse(m.containsValue("three"));
        assertFalse(m.containsValue("five"));
        assertTrue(m.containsValue("two"));
        assertTrue(m.containsValue("four"));
        assertTrue(m.containsValue("six"));
    }

    public void testDecoratedMap() {
        // entries shouldn't expire
        final Map<Integer, String> m = makeDecoratedTestMap();
        assertEquals(6, m.size());
        assertEquals("one", m.get(Integer.valueOf(1)));

        // removing a single item shouldn't affect any other items
        assertEquals("two", m.get(Integer.valueOf(2)));
        m.remove(Integer.valueOf(2));
        assertEquals(5, m.size());
        assertEquals("one", m.get(Integer.valueOf(1)));
        assertNull(m.get(Integer.valueOf(2)));

        // adding a single, even item shouldn't affect any other items
        assertNull(m.get(Integer.valueOf(2)));
        m.put(Integer.valueOf(2), "two");
        assertEquals(6, m.size());
        assertEquals("one", m.get(Integer.valueOf(1)));
        assertEquals("two", m.get(Integer.valueOf(2)));

        // adding a single, odd item (one that expires) shouldn't affect any
        // other items
        // put the entry expires immediately
        m.put(Integer.valueOf(1), "one-one");
        assertEquals(5, m.size());
        assertNull(m.get(Integer.valueOf(1)));
        assertEquals("two", m.get(Integer.valueOf(2)));
    }

    public void testEntrySet() {
        final Map<Integer, String> m = makeTestMap();
        assertEquals(3, m.entrySet().size());
    }

    public void testGet() {
        final Map<Integer, String> m = makeTestMap();
        assertNull(m.get(Integer.valueOf(1)));
        assertEquals("two", m.get(Integer.valueOf(2)));
        assertNull(m.get(Integer.valueOf(3)));
        assertEquals("four", m.get(Integer.valueOf(4)));
        assertNull(m.get(Integer.valueOf(5)));
        assertEquals("six", m.get(Integer.valueOf(6)));
    }

    public void testIsEmpty() {
        Map<Integer, String> m = makeTestMap();
        assertFalse(m.isEmpty());

        // remove just evens
        m = makeTestMap();
        m.remove(Integer.valueOf(2));
        m.remove(Integer.valueOf(4));
        m.remove(Integer.valueOf(6));
        assertTrue(m.isEmpty());
    }

    public void testKeySet() {
        final Map<Integer, String> m = makeTestMap();
        assertEquals(3, m.keySet().size());
    }

    public void testSize() {
        final Map<Integer, String> m = makeTestMap();
        assertEquals(3, m.size());
    }

    public void testValues() {
        final Map<Integer, String> m = makeTestMap();
        assertEquals(3, m.values().size());
    }

    public void testZeroTimeToLive() {
        // item should not be available
        final PassiveExpiringMap<String, String> m = new PassiveExpiringMap<>(0L);
        m.put("a", "b");
        assertNull(m.get("a"));
    }
    
    public void testExpiration() {
        validateExpiration(new PassiveExpiringMap<String, String>(500), 500);
        validateExpiration(new PassiveExpiringMap<String, String>(1000), 1000);
        validateExpiration(new PassiveExpiringMap<>(
                new PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy<String, String>(500)), 500);
        validateExpiration(new PassiveExpiringMap<>(
                new PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy<String, String>(1, TimeUnit.SECONDS)), 1000);
    }

    private void validateExpiration(final Map<String, String> map, final long timeout) {
        map.put("a", "b");
        
        assertNotNull(map.get("a"));
        
        try {
            Thread.sleep(2 * timeout);
        } catch (InterruptedException e) {
            fail();
        }

        assertNull(map.get("a"));
    }
    
}
