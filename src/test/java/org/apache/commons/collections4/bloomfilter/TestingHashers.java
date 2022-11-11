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
package org.apache.commons.collections4.bloomfilter;
/**
 * A collection of methods and statics that represent standard hashers in testing.
 */
public class TestingHashers {
    /**
     * Hasher that increments from 1
     */
    public static final Hasher from1 = new IncrementingHasher(1, 1);

    /**
     * Hasher that increments from 11
     */
    public static final Hasher from11 = new IncrementingHasher(11, 1);

    /**
     * Do not instantiate
     */
    private TestingHashers() {}

    /**
     * Merge several Hashers together into a single Bloom filter.
     * @param <T> The type of bloom filter.
     * @param filter The Bloom filter to populate
     * @param hashers The hashers to merge
     * @return {@code filter} for chaining
     */
    public static <T extends BloomFilter> T mergeHashers(T filter, Hasher...hashers) {
        for (Hasher h : hashers) {
            filter.merge(h);
        }
        return filter;
    }

    /**
     * Merge {@code from1} and {@code from11} into a single Bloom filter.
     * @param <T> The type of bloom filter.
     * @param filter The Bloom filter to populate
     * @return {@code filter} for chaining
     */
    public static <T extends BloomFilter> T bigHasher(T filter) {
        return mergeHashers(filter, from1, from11);
    }

    /**
     * Create a hasher that fills the entire range.
     * @param <T> the Bloom filter type.
     * @param filter the Bloom filter to populate
     * @return {@code filter} for chaining
     */
    public static <T extends BloomFilter> T fullHasher(T filter) {
        for (int i=0; i<filter.getShape().getNumberOfBits(); i+=filter.getShape().getNumberOfHashFunctions()) {
            filter.merge(new IncrementingHasher(i, 1));
        }
        return filter;
    }
}
