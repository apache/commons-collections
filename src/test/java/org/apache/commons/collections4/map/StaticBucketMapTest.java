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

import org.junit.jupiter.api.Test;

/**
 * Unit tests.
 * {@link StaticBucketMap}.
 *
 */
public class StaticBucketMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    public StaticBucketMapTest() {
        super(StaticBucketMapTest.class.getSimpleName());
    }

    @Override
    public StaticBucketMap<K, V> makeObject() {
        return new StaticBucketMap<>(30);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFailFastExpected() {
        return false;
    }

    @Override
    public String[] ignoredTests() {
        final String pre = "StaticBucketMapTest.bulkTestMap";
        final String post = ".testCollectionIteratorFailFast";
        return new String[] {
            pre + "EntrySet" + post,
            pre + "KeySet" + post,
            pre + "Values" + post
        };
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
            assertNull("String: " + str, map.get(str));
        }
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
            assertFalse("String: " + str, map.containsKey(str));
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
            assertFalse("String: " + str, map.containsValue(str));
        }
    }

}
