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
package org.apache.commons.collections4;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.Flat3Map;

/**
 * {@code MapPerformanceTest} is designed to perform basic Map performance tests.
 *
 */
public class MapPerformance {

    /** The total number of runs for each test */
    private static final int RUNS = 20000000;

    /**
     * Main method
     */
    public static void main(final String[] args) {
        testAll();
    }

    private static void testAll() {
        final Map<String, String> dummyMap = new DummyMap<>();
        final Map<String, String> hashMap = new HashMap<>();
//        hashMap.put("Alpha", "A");
//        hashMap.put("Beta", "B");
//        hashMap.put("Gamma", "C");
//        hashMap.put("Delta", "D");
        final Map<String, String> flatMap = new Flat3Map<>(hashMap);
        System.out.println(flatMap);
//        Map<String, String> unmodHashMap = Collections.unmodifiableMap(new HashMap<String, String>(hashMap));
//        Map fastHashMap = new FastHashMap(hashMap);
//        Map<String, String> treeMap = new TreeMap<String, String>(hashMap);
//        Map linkedMap = new LinkedHashMap(hashMap);
//        Map syncMap = Collections.unmodifiableMap(new HashMap(hashMap));
//        Map bucketMap = new StaticBucketMap();
//        bucketMap.putAll(hashMap);
//        Map doubleMap = new DoubleOrderedMap(hashMap);

        // dummy is required as the VM seems to hotspot the first call to the
        // test method with the given type
        test(dummyMap,      "         Dummy ");
        test(dummyMap,      "         Dummy ");
        test(dummyMap,      "         Dummy ");
        test(flatMap,       "         Flat3 ");
        test(hashMap,       "       HashMap ");

        test(flatMap,       "         Flat3 ");
        test(flatMap,       "         Flat3 ");
        test(flatMap,       "         Flat3 ");

        test(hashMap,       "       HashMap ");
        test(hashMap,       "       HashMap ");
        test(hashMap,       "       HashMap ");

//        test(treeMap,       "       TreeMap ");
//        test(treeMap,       "       TreeMap ");
//        test(treeMap,       "       TreeMap ");

//        test(unmodHashMap,  "Unmod(HashMap) ");
//        test(unmodHashMap,  "Unmod(HashMap) ");
//        test(unmodHashMap,  "Unmod(HashMap) ");
//
//        test(syncMap,       " Sync(HashMap) ");
//        test(syncMap,       " Sync(HashMap) ");
//        test(syncMap,       " Sync(HashMap) ");
//
//        test(fastHashMap,   "   FastHashMap ");
//        test(fastHashMap,   "   FastHashMap ");
//        test(fastHashMap,   "   FastHashMap ");
//
//        test(seqMap,        "    SeqHashMap ");
//        test(seqMap,        "    SeqHashMap ");
//        test(seqMap,        "    SeqHashMap ");
//
//        test(linkedMap,     " LinkedHashMap ");
//        test(linkedMap,     " LinkedHashMap ");
//        test(linkedMap,     " LinkedHashMap ");
//
//        test(bucketMap,     "     BucketMap ");
//        test(bucketMap,     "     BucketMap ");
//        test(bucketMap,     "     BucketMap ");
//
//        test(doubleMap,     "     DoubleMap ");
//        test(doubleMap,     "     DoubleMap ");
//        test(doubleMap,     "     DoubleMap ");
    }

    private static void test(final Map<String, String> map, final String name) {
        long startMillis = 0, endMillis = 0;
//        int total = 0;
        startMillis = System.currentTimeMillis();
        for (int i = RUNS; i > 0; i--) {
//            if (map.get("Alpha") != null) total++;
//            if (map.get("Beta") != null) total++;
//            if (map.get("Gamma") != null) total++;
            map.put("Alpha", "A");
            map.put("Beta", "B");
            map.put("Beta", "C");
            map.put("Gamma", "D");
//            map.remove("Gamma");
//            map.remove("Beta");
//            map.remove("Alpha");
            map.put("Delta", "E");
            map.clear();
        }
        endMillis = System.currentTimeMillis();
        System.out.println(name + (endMillis - startMillis));
    }

    private static class DummyMap<K, V> implements Map<K, V> {
        @Override
        public void clear() {
        }
        @Override
        public boolean containsKey(final Object key) {
            return false;
        }
        @Override
        public boolean containsValue(final Object value) {
            return false;
        }
        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return null;
        }
        @Override
        public V get(final Object key) {
            return null;
        }
        @Override
        public boolean isEmpty() {
            return false;
        }
        @Override
        public Set<K> keySet() {
            return null;
        }
        @Override
        public V put(final K key, final V value) {
            return null;
        }
        @Override
        public void putAll(final Map<? extends K, ? extends V> t) {
        }
        @Override
        public V remove(final Object key) {
            return null;
        }
        @Override
        public int size() {
            return 0;
        }
        @Override
        public Collection<V> values() {
            return null;
        }
    }

}

