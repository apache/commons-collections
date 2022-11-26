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
package org.apache.commons.collections4.keyvalue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Abstract tests that can be extended to test any Map.Entry implementation.
 * Subclasses must implement {@link #makeMapEntry(Object, Object)} to return
 * a new Map.Entry of the type being tested. Subclasses must also implement
 * {@link #testConstructors()} to test the constructors of the Map.Entry
 * type being tested.
 *
 * @since 3.0
 */
public abstract class AbstractMapEntryTest<K, V> {

    protected final String key = "name";
    protected final String value = "duke";

    /**
     * Make an instance of Map.Entry with the default (null) key and value.
     * This implementation simply calls {@link #makeMapEntry(Object, Object)}
     * with null for key and value. Subclasses can override this method if desired.
     */
    public Map.Entry<K, V> makeMapEntry() {
        return makeMapEntry(null, null);
    }

    /**
     * Make an instance of Map.Entry with the specified key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    public abstract Map.Entry<K, V> makeMapEntry(K key, V value);

    /**
     * Makes a Map.Entry of a type that's known to work correctly.
     */
    public Map.Entry<K, V> makeKnownMapEntry() {
        return makeKnownMapEntry(null, null);
    }

    /**
     * Makes a Map.Entry of a type that's known to work correctly.
     */
    public Map.Entry<K, V> makeKnownMapEntry(final K key, final V value) {
        final Map<K, V> map = new HashMap<>(1);
        map.put(key, value);
        final Map.Entry<K, V> entry = map.entrySet().iterator().next();
        return entry;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAccessorsAndMutators() {
        Map.Entry<K, V> entry = makeMapEntry((K) key, (V) value);

        assertSame(key, entry.getKey());

        entry.setValue((V) value);
        assertSame(value, entry.getValue());

        // check that null doesn't do anything funny
        entry = makeMapEntry(null, null);
        assertNull(entry.getKey());

        entry.setValue(null);
        assertNull(entry.getValue());
    }

    /**
     * Subclasses should override this method to test the
     * desired behavior of the class with respect to
     * handling of self-references.
     *
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testSelfReferenceHandling() {
        // test that #setValue does not permit
        //  the MapEntry to contain itself (and thus cause infinite recursion
        //  in #hashCode and #toString)

        final Map.Entry<K, V> entry = makeMapEntry();

        assertThrows(IllegalArgumentException.class, () -> entry.setValue((V) entry));

        // check that the KVP's state has not changed
        assertTrue(entry.getKey() == null && entry.getValue() == null);
    }

    /**
     * Subclasses should provide tests for their constructors.
     *
     */
    public abstract void testConstructors();

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsAndHashCode() {
        // 1. test with object data
        Map.Entry<K, V> e1 = makeMapEntry((K) key, (V) value);
        Map.Entry<K, V> e2 = makeKnownMapEntry((K) key, (V) value);

        assertEquals(e1, e1);
        assertEquals(e2, e1);
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());

        // 2. test with nulls
        e1 = makeMapEntry();
        e2 = makeKnownMapEntry();

        assertEquals(e1, e1);
        assertEquals(e2, e1);
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToString() {
        Map.Entry<K, V> entry = makeMapEntry((K) key, (V) value);
        assertEquals(entry.toString(), entry.getKey() + "=" + entry.getValue());

        // test with nulls
        entry = makeMapEntry();
        assertEquals(entry.toString(), entry.getKey() + "=" + entry.getValue());
    }

}
