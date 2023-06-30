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
 * Represents a function that accepts a two Bloom argument and produces a boolean result.
 * This is the boolean returning specialization for {@code BiPredicate}.
 *
 * This is a functional interface whose functional method is {@code test(BloomFilter,BloomFilter)}.
 *
 * @since 4.5
 */
@FunctionalInterface
public interface BloomFilterBiPredicate {

    /**
     * A function that takes to long arguments and returns a boolean.
     * @param x the first Bloom filter argument.
     * @param y the second Bloom filter argument.
     * @return true or false.
     */
    boolean test(BloomFilter x, BloomFilter y);
}
