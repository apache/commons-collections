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
package org.apache.commons.collections4.bloomfilter.hasher;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

/**
 * The class that performs hashing on demand.
 * @since 4.5
 */
public class DynamicHasher implements Hasher {

    /**
     * The list of byte arrays that are to be hashed.
     */
    private final List<byte[]> buffers;

    /**
     * The function to hash the buffers.
     */
    private final HashFunction function;

    /**
     * Constructs a DynamicHasher.
     *
     * @param function the function to use.
     * @param buffers the byte buffers that will be hashed.
     */
    public DynamicHasher(HashFunction function, List<byte[]> buffers) {
        this.buffers = new ArrayList<>(buffers);
        this.function = function;
    }

    @Override
    public HashFunctionIdentity getHashFunctionIdentity() {
        return function;
    }

    @Override
    public boolean isEmpty() {
        return buffers.isEmpty();
    }

    /**
     * Return an iterator of integers that are the bits to enable in the Bloom filter
     * based on the shape. The iterator may return the same value multiple times. There is
     * no guarantee made as to the order of the integers.
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the Iterator of integers;
     * @throws IllegalArgumentException if {@code shape.getHasherName()} does not equal
     * {@code getName()}
     */
    @Override
    public PrimitiveIterator.OfInt getBits(Shape shape) {
        if (HashFunctionIdentity.COMMON_COMPARATOR.compare(getHashFunctionIdentity(),
            shape.getHashFunctionIdentity()) != 0) {
            throw new IllegalArgumentException(
                String.format("Shape hasher %s is not %s",
                    HashFunctionIdentity.asCommonString(shape.getHashFunctionIdentity()),
                    HashFunctionIdentity.asCommonString(getHashFunctionIdentity())));
        }
        return new Iterator(shape);
    }

    /**
     * The iterator of integers.
     */
    private class Iterator implements PrimitiveIterator.OfInt {
        private int buffer = 0;
        private int funcCount = 0;
        private final Shape shape;

        /**
         * Creates iterator with the specified shape.
         *
         * @param shape
         */
        private Iterator(Shape shape) {
            this.shape = shape;
        }

        @Override
        public boolean hasNext() {
            if (buffers.isEmpty()) {
                return false;
            }
            return buffer < buffers.size() - 1 || funcCount < shape.getNumberOfHashFunctions();
        }

        @Override
        public int nextInt() {
            if (hasNext()) {
                if (funcCount >= shape.getNumberOfHashFunctions()) {
                    funcCount = 0;
                    buffer++;
                }
                return (int) Math.floorMod(function.apply(buffers.get(buffer), funcCount++),
                    shape.getNumberOfBits());
            }
            throw new NoSuchElementException();
        }
    }

    /**
     * The builder for DynamicHashers.
     * @since 4.5
     */
    public static class Builder implements Hasher.Builder {
        /**
         * The list of byte[] that are to be hashed.
         */
        private final List<byte[]> buffers;

        /**
         * The function that the resulting DynamicHasher will use.
         */
        private final HashFunction function;

        /**
         * Constructs a DynamicHasher builder.
         *
         * @param function the function implementation.
         */
        public Builder(HashFunction function) {
            this.function = function;
            this.buffers = new ArrayList<>();

        }

        /**
         * Builds the hasher.
         *
         * @return A DynamicHasher with the specified name, function and buffers.
         */
        @Override
        public DynamicHasher build() throws IllegalArgumentException {
            return new DynamicHasher(function, buffers);
        }

        @Override
        public final Builder with(byte property) {
            return with(new byte[] {property});
        }

        @Override
        public final Builder with(byte[] property) {
            buffers.add(property);
            return this;
        }

        @Override
        public final Builder with(String property) {
            return with(property.getBytes(StandardCharsets.UTF_8));
        }

    }

}
