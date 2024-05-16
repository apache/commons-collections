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

import java.util.concurrent.ThreadLocalRandom;

/**
 * A collection of methods and statics that represent standard hashers in testing.
 */
public class TestingHashers {
    /**
     * Hasher that increments from 1.
     */
    public static final Hasher FROM1 = new IncrementingHasher(1, 1);

    /**
     * Hasher that increments from 11.
     */
    public static final Hasher FROM11 = new IncrementingHasher(11, 1);

    /**
     * Merge several Hashers together into a single Bloom filter.
     * @param <T> The type of bloom filter.
     * @param filter The Bloom filter to populate
     * @param hashers The hashers to merge
     * @return {@code filter} for chaining
     */
    public static <T extends BloomFilter> T mergeHashers(final T filter, final Hasher... hashers) {
        for (final Hasher h : hashers) {
            filter.merge(h);
        }
        return filter;
    }

    /**
     * Enables all bits in the filter.
     * @param <T> the Bloom filter type.
     * @param filter the Bloom filter to populate
     * @return {@code filter} for chaining
     */
    public static <T extends BloomFilter> T populateEntireFilter(final T filter) {
        return populateRange(filter, 0, filter.getShape().getNumberOfBits() - 1);
    }

    /**
     * Merge {@code from1} and {@code from11} into a single Bloom filter.
     * @param <T> The type of bloom filter.
     * @param filter The Bloom filter to populate
     * @return {@code filter} for chaining
     */
    public static <T extends BloomFilter> T populateFromHashersFrom1AndFrom11(final T filter) {
        return mergeHashers(filter, FROM1, FROM11);
    }

    /**
     * Enables all bits in a range (inclusive).
     * @param <T> the Bloom filter type.
     * @param filter the Bloom filter to populate
     * @param start the starting bit to enable.
     * @param end the last bit to enable.
     * @return {@code filter} for chaining
     */
    public static <T extends BloomFilter> T populateRange(final T filter, final int start, final int end) {
        filter.merge((IndexExtractor) p -> {
            for (int i = start; i <= end; i++) {
                if (!p.test(i)) {
                    return false;
                }
            }
            return true;
        });
        return filter;
    }

    /**
     * Creates an EnhancedDoubleHasher hasher from 2 random longs.
     */
    public static Hasher randomHasher() {
        return new EnhancedDoubleHasher(ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong());
    }

    /**
     * Do not instantiate.
     */
    private TestingHashers() {
        // empty
    }

}
