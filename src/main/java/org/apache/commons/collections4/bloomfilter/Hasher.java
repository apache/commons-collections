/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter;

import java.util.PrimitiveIterator;
import java.util.Set;

import org.apache.commons.collections4.bloomfilter.BloomFilter.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.DynamicHasher;


/**
 * The class that performs hashing.
 * <p>
 * Hashers have a Unique name based on the hashing algorithm used.
 * </p><p>
 * Implementations of {@code getBits()} may return duplicate values and may return
 * values in a random order.  See implementation javadoc notes as to the guarantees
 * provided by the specific implementation.
 */
public interface Hasher {

    /**
     * Gets the name of the function.
     *
     * @return the name of the function being used.
     */
    String getName();

    /**
     * Return an iterator of integers that are the bits to enable in the Bloom
     * filter based on the shape.  No guarantee is made as to order
     * or duplication of values.
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the Iterator of integers;
     * @throws IllegalArgumentException if {@code shape.getHasherName()} does not
     *                                  equal {@code getName()}
     */
    PrimitiveIterator.OfInt getBits(Shape shape);

    /**
     * A factory that creates Hasher.Builder instances for well known algorithms.
     * <p>
     * The factory can create Builders for various well known hash algorithms.
     * </p>
     */
    interface Factory {

        /**
         * the default implementation of the HasherFactory
         */
        static Factory DEFAULT = new DynamicHasher.Factory();

        /**
         * Lists all registered Hash function names.
         *
         * @return the list of all registered Hasher names.
         */
        Set<String> listFunctionNames();

        /**
         * Creates a Builder that that uses the specified hashing function.
         *
         * @param name the name of the hash function to use.
         * @return A builder using the specified hash function.
         * @throws IllegalArgumentException if the funcName is not known.
         * @throws IllegalStateException    if the function can not be instantiated.
         */
        Builder useFunction(String name) throws IllegalArgumentException;

    }

    /**
     * A builder to build a hasher.
     *
     */
    interface Builder {
        /**
         * Build the hasher.
         * @return the fully constructed hasher.
         */
        Hasher  build();

        /**
         * Adds a byte to the hasher.
         *
         * @param property the byte to add
         * @return {@code this} for chaining.
         * @throws IllegalStateException if the Hasher is locked.
         * @see #getBits(Shape)
         */
        Builder with(byte property);

        /**
         * Adds an array of bytes to the hasher.
         *
         * @param property the array of bytes to add.
         * @return {@code this} for chaining.
         * @throws IllegalStateException if the Hasher is locked.
         * @see #getBits(Shape)
         */
        Builder with(byte[] property);

        /**
         * Adds a string to the hasher. The string is converted to a byte array using
         * the UTF-8 Character set.
         *
         * @param property the string to add.
         * @return {@code this} for chaining.
         * @throws IllegalStateException if the Hasher is locked.
         * @see #getBits(Shape)
         */
        Builder with(String property);

    }
}
