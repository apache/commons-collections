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
package org.apache.commons.collections4.bloomfilter.hasher;

import java.util.PrimitiveIterator;

/**
 * A Hasher represents items of arbitrary byte size as a byte representation of
 * fixed size (a hash). The hash representations can be used to create indexes
 * for a Bloom filter.
 *
 * <p>The hash for each item is created using a hash function; use of different
 * seeds allows generation of different hashes for the same item. The hashes can
 * be dynamically converted into the bit index representation used by a Bloom
 * filter. The shape of the Bloom filter defines the number of indexes per item
 * and the range of the indexes. The hasher can generate the correct number of
 * indexes in the range required by the Bloom filter for each item it
 * represents.
 *
 * <p>Note that the process of generating hashes and mapping them to a Bloom
 * filter shape may create duplicate indexes. The hasher may generate fewer than
 * the required number of hash functions per item if duplicates have been
 * removed. Implementations of {@code iterator()} may return duplicate values
 * and may return values in a random order. See implementation javadoc notes as
 * to the guarantees provided by the specific implementation.
 *
 * <p>Hashers have an identity based on the hashing algorithm used.
 *
 * @since 4.5
 */
public interface Hasher {

    /**
     * Gets an iterator of integers that are the bits to enable in the Bloom
     * filter based on the shape.
     *
     * <p>The iterator will create indexes within the range defined by the number of bits in
     * the shape. The total number of indexes will respect the number of hash functions per item
     * defined by the shape. However the count of indexes may not be a multiple of the number of
     * hash functions if the implementation has removed duplicates.
     *
     * <p>No guarantee is made as to order of values.
     *
     * @param shape the shape of the desired Bloom filter
     * @return the iterator of integers
     * @throws IllegalArgumentException if the hasher cannot generate indexes for
     * the specified @{@code shape}
     */
    PrimitiveIterator.OfInt iterator(Shape shape);

    /**
     * Gets the number of items that will be hashed by the iterator.
     * @return The number of items that will be hashed by the iterator.
     */
    int size();


}
