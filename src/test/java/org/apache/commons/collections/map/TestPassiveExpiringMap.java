package org.apache.commons.collections.map;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.map.PassiveExpiringMap.ExpirationPolicy;

public class TestPassiveExpiringMap<K, V>
    extends AbstractTestMap<K, V> {

    private static class TestExpirationPolicy
        implements ExpirationPolicy<Integer, String> {

        private static final long serialVersionUID = 1L;

        public long expirationTime(Integer key, String value) {
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
        return BulkTest.makeSuite(TestPassiveExpiringMap.class);
    }

    public TestPassiveExpiringMap(String testName) {
        super(testName);
    }

    // public void testCreate() throws Exception {
    // writeExternalFormToDisk((java.io.Serializable) makeObject(),
    // "PassiveExpiringMap.emptyCollection.version4.obj");
    //
    // writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
    // "PassiveExpiringMap.fullCollection.version4.obj");
    // }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    private Map<Integer, String> makeDecoratedTestMap() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(Integer.valueOf(1), "one");
        m.put(Integer.valueOf(2), "two");
        m.put(Integer.valueOf(3), "three");
        m.put(Integer.valueOf(4), "four");
        m.put(Integer.valueOf(5), "five");
        m.put(Integer.valueOf(6), "six");
        return new PassiveExpiringMap<Integer, String>(
                                                       new TestExpirationPolicy(),
                                                       m);
    }

    @Override
    public Map<K, V> makeObject() {
        return new PassiveExpiringMap<K, V>();
    }

    private Map<Integer, String> makeTestMap() {
        Map<Integer, String> m = new PassiveExpiringMap<Integer, String>(
                                                                         new TestExpirationPolicy());
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
            Map<String, String> map = null;
            new PassiveExpiringMap<String, String>(map);
            fail("constructor - exception should have been thrown.");
        } catch (IllegalArgumentException ex) {
            // success
        }

        try {
            ExpirationPolicy<String, String> policy = null;
            new PassiveExpiringMap<String, String>(policy);
            fail("constructor - exception should have been thrown.");
        } catch (IllegalArgumentException ex) {
            // success
        }

        try {
            TimeUnit unit = null;
            new PassiveExpiringMap<String, String>(10L, unit);
            fail("constructor - exception should have been thrown.");
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testContainsKey() {
        Map<Integer, String> m = makeTestMap();
        assertFalse(m.containsKey(Integer.valueOf(1)));
        assertFalse(m.containsKey(Integer.valueOf(3)));
        assertFalse(m.containsKey(Integer.valueOf(5)));
        assertTrue(m.containsKey(Integer.valueOf(2)));
        assertTrue(m.containsKey(Integer.valueOf(4)));
        assertTrue(m.containsKey(Integer.valueOf(6)));
    }

    public void testContainsValue() {
        Map<Integer, String> m = makeTestMap();
        assertFalse(m.containsValue("one"));
        assertFalse(m.containsValue("three"));
        assertFalse(m.containsValue("five"));
        assertTrue(m.containsValue("two"));
        assertTrue(m.containsValue("four"));
        assertTrue(m.containsValue("six"));
    }

    public void testDecoratedMap() {
        // entries shouldn't expire
        Map<Integer, String> m = makeDecoratedTestMap();
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
        Map<Integer, String> m = makeTestMap();
        assertEquals(3, m.entrySet().size());
    }

    public void testGet() {
        Map<Integer, String> m = makeTestMap();
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
        Map<Integer, String> m = makeTestMap();
        assertEquals(3, m.keySet().size());
    }

    public void testSize() {
        Map<Integer, String> m = makeTestMap();
        assertEquals(3, m.size());
    }

    public void testValues() {
        Map<Integer, String> m = makeTestMap();
        assertEquals(3, m.values().size());
    }

    public void testZeroTimeToLive() {
        // item should not be available
        PassiveExpiringMap<String, String> m = new PassiveExpiringMap<String, String>(
                                                                                      0L);
        m.put("a", "b");
        assertNull(m.get("a"));
    }
}
