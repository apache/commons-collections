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
 * The class that performs hashing.
 * <p>
 * Hashers have a Unique name based on the hashing algorithm used.
 * </p>
 * <p>
 * Implementations of {@code getBits()} may return duplicate values and may return
 * values in a random order.  See implementation javadoc notes as to the guarantees
 * provided by the specific implementation.
 * </p>
 * @since 4.5
 */
public interface Hasher {

    /**
     * A builder to build a hasher.
     * @since 4.5
     */
    interface Builder {

        /**
         * Builds the hasher.
         * @return the fully constructed hasher
         */
        Hasher build();

        /**
         * Adds a byte to the hasher.
         *
         * @param property the byte to add
         * @return a reference to this object
         * @throws IllegalStateException if the Hasher is locked
         * @see #getBits(Shape)
         */
        Builder with(byte property);

        /**
         * Adds an array of bytes to the hasher.
         *
         * @param property the array of bytes to add
         * @return a reference to this object
         * @throws IllegalStateException if the Hasher is locked
         * @see #getBits(Shape)
         */
        Builder with(byte[] property);

        /**
         * Adds a string to the hasher.
         *
         * @param property the string to add
         * @return a reference to this object
         * @throws IllegalStateException if the Hasher is locked
         * @see #getBits(Shape)
         */
        Builder with(String property);
    }

    /**
     * Gets an iterator of integers that are the bits to enable in the Bloom
     * filter based on the shape. No guarantee is made as to order
     * or duplication of values.
     *
     * @param shape the shape of the desired Bloom filter
     * @return the iterator of integers
     * @throws IllegalArgumentException if the hasher cannot generate indexes for
     * the specified @{@code shape}
     */
    PrimitiveIterator.OfInt getBits(Shape shape);

    /**
     * Gets the identify of the hash function used by the the hasher.
     *
     * @return the identity of the hash function
     */
    HashFunctionIdentity getHashFunctionIdentity();
}
