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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests.
 * {@link StaticBucketMap}.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class StaticBucketMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFailFastExpected() {
        return false;
    }

    @Override
    public StaticBucketMap<K, V> makeObject() {
        return new StaticBucketMap<>(30);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_containsKey_nullMatchesIncorrectly() {
        final StaticBucketMap<K, V> map = new StaticBucketMap<>(17);
        map.put(null, (V) "A");
        assertTrue(map.containsKey(null));
        // loop so we find a string that is in the same bucket as the null
        for (int i = 'A'; i <= 'Z'; i++) {
            final String str = String.valueOf((char) i);
            assertFalse(map.containsKey(str), "String: " + str);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_containsValue_nullMatchesIncorrectly() {
        final StaticBucketMap<K, V> map = new StaticBucketMap<>(17);
        map.put((K) "A", null);
        assertTrue(map.containsValue(null));
        // loop so we find a string that is in the same bucket as the null
        for (int i = 'A'; i <= 'Z'; i++) {
            final String str = String.valueOf((char) i);
            assertFalse(map.containsValue(str), "String: " + str);
        }
    }

    // Bugzilla 37567
    @Test
    @SuppressWarnings("unchecked")
    public void test_get_nullMatchesIncorrectly() {
        final StaticBucketMap<K, V> map = new StaticBucketMap<>(17);
        map.put(null, (V) "A");
        assertEquals("A", map.get(null));
        // loop so we find a string that is in the same bucket as the null
        for (int i = 'A'; i <= 'Z'; i++) {
            final String str = String.valueOf((char) i);
            assertNull(map.get(str), "String: " + str);
        }
    }

}
