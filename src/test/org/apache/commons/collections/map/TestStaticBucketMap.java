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

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;

/**
 * Unit tests.
 * {@link StaticBucketMap}.
 *
 * @version $Revision$ $Date$
 *
 * @author Michael A. Smith
 */
public class TestStaticBucketMap<K, V> extends AbstractTestMap<K, V> {

    public TestStaticBucketMap(String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestStaticBucketMap.class);
    }

    public static void main(String[] args) {
        String[] testCaseName = { TestStaticBucketMap.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public StaticBucketMap<K, V> makeObject() {
        return new StaticBucketMap<K, V>(30);
    }

    public String[] ignoredTests() {
        String pre = "TestStaticBucketMap.bulkTestMap";
        String post = ".testCollectionIteratorFailFast";
        return new String[] {
            pre + "EntrySet" + post,
            pre + "KeySet" + post,
            pre + "Values" + post
        };
    }

    // Bugzilla 37567
    @SuppressWarnings("unchecked")
    public void test_get_nullMatchesIncorrectly() {
        StaticBucketMap<K, V> map = new StaticBucketMap<K, V>(17);
        map.put(null, (V) "A");
        assertEquals("A", map.get(null));
        // loop so we find a string that is in the same bucket as the null
        for (int i = 'A'; i <= 'Z'; i++) {
            String str = String.valueOf((char) i);
            assertEquals("String: " + str, null, map.get(str));
        }
    }

    @SuppressWarnings("unchecked")
    public void test_containsKey_nullMatchesIncorrectly() {
        StaticBucketMap<K, V> map = new StaticBucketMap<K, V>(17);
        map.put(null, (V) "A");
        assertEquals(true, map.containsKey(null));
        // loop so we find a string that is in the same bucket as the null
        for (int i = 'A'; i <= 'Z'; i++) {
            String str = String.valueOf((char) i);
            assertEquals("String: " + str, false, map.containsKey(str));
        }
    }

    @SuppressWarnings("unchecked")
    public void test_containsValue_nullMatchesIncorrectly() {
        StaticBucketMap<K, V> map = new StaticBucketMap<K, V>(17);
        map.put((K) "A", null);
        assertEquals(true, map.containsValue(null));
        // loop so we find a string that is in the same bucket as the null
        for (int i = 'A'; i <= 'Z'; i++) {
            String str = String.valueOf((char) i);
            assertEquals("String: " + str, false, map.containsValue(str));
        }
    }

}
