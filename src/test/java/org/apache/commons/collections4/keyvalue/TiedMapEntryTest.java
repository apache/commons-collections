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

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

/**
 * Test the TiedMapEntry class.
 *
 * @since 3.0
 */
public class TiedMapEntryTest<K, V> extends AbstractMapEntryTest<K, V> {

    //-----------------------------------------------------------------------
    /**
     * Gets the instance to test
     */
    @Override
    public Map.Entry<K, V> makeMapEntry(final K key, final V value) {
        final Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return new TiedMapEntry<>(map, key);
    }

    //-----------------------------------------------------------------------
    /**
     * Tests the constructors.
     */
    @Override
    @Test
    public void testConstructors() {
        // ignore
    }

    /**
     * Tests the constructors.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSetValue() {
        final Map<K, V> map = new HashMap<>();
        map.put((K) "A", (V) "a");
        map.put((K) "B", (V) "b");
        map.put((K) "C", (V) "c");
        Map.Entry<K, V> entry = new TiedMapEntry<>(map, (K) "A");
        assertSame("A", entry.getKey());
        assertSame("a", entry.getValue());
        assertSame("a", entry.setValue((V) "x"));
        assertSame("A", entry.getKey());
        assertSame("x", entry.getValue());

        entry = new TiedMapEntry<>(map, (K) "B");
        assertSame("B", entry.getKey());
        assertSame("b", entry.getValue());
        assertSame("b", entry.setValue((V) "y"));
        assertSame("B", entry.getKey());
        assertSame("y", entry.getValue());

        entry = new TiedMapEntry<>(map, (K) "C");
        assertSame("C", entry.getKey());
        assertSame("c", entry.getValue());
        assertSame("c", entry.setValue((V) "z"));
        assertSame("C", entry.getKey());
        assertSame("z", entry.getValue());
    }

}
