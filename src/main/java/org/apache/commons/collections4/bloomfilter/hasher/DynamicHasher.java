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

import java.nio.charset.Charset;
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
     * The builder for DynamicHashers.
     * @since 4.5
     */
    public static class Builder implements Hasher.Builder {

        /**
         * The list of items (each as a byte[]) that are to be hashed.
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
        public Builder(final HashFunction function) {
            this.function = function;
            this.buffers = new ArrayList<>();
        }

        @Override
        public DynamicHasher build() throws IllegalArgumentException {
            // Assumes the hasher will create a copy of the buffers
            final DynamicHasher hasher = new DynamicHasher(function, buffers);
            // Reset for further use
            buffers.clear();
            return hasher;
        }

        @Override
        public final DynamicHasher.Builder with(final byte[] property) {
            buffers.add(property);
            return this;
        }

        @Override
        public DynamicHasher.Builder with(final CharSequence item, final Charset charset) {
            Hasher.Builder.super.with(item, charset);
            return this;
        }

        @Override
        public DynamicHasher.Builder withUnencoded(final CharSequence item) {
            Hasher.Builder.super.withUnencoded(item);
            return this;
        }
    }

    /**
     * The iterator of integers.
     *
     * <p>This assumes that the list of buffers is not empty.
     */
    private class Iterator implements PrimitiveIterator.OfInt {
        /** The number of hash functions per item. */
        private final int k;
        /** The number of bits in the shape. */
        private final int m;
        /** The current item. */
        private byte[] item;
        /** The index of the next item. */
        private int nextItem;
        /** The count of hash functions for the current item. */
        private int functionCount;

        /**
         * Constructs iterator with the specified shape.
         *
         * @param shape
         */
        private Iterator(final Shape shape) {
            // Assumes that shape returns non-zero positive values for hash functions and bits
            k = shape.getNumberOfHashFunctions();
            m = shape.getNumberOfBits();
            // Assume non-empty
            item = buffers.get(0);
            nextItem = 1;
        }

        @Override
        public boolean hasNext() {
            if (functionCount != k) {
                return true;
            }
            // Reached the number of hash functions for the current item.
            // Try and advance to the next item.
            if (nextItem != buffers.size()) {
                item = buffers.get(nextItem++);
                functionCount = 0;
                return true;
            }
            // Finished.
            // functionCount == shape.getNumberOfHashFunctions()
            // nextItem == buffers.size()
            return false;
        }

        @SuppressWarnings("cast") // Cast to long to workaround a bug in animal-sniffer.
        @Override
        public int nextInt() {
            if (hasNext()) {
                return (int) Math.floorMod(function.apply(item, functionCount++),
                    // Cast to long to workaround a bug in animal-sniffer.
                    (long) m);
            }
            throw new NoSuchElementException();
        }
    }

    /**
     * An iterator of integers to use when there are no values.
     */
    private static class NoValuesIterator implements PrimitiveIterator.OfInt {
        /** The singleton instance. */
        private static final NoValuesIterator INSTANCE = new NoValuesIterator();

        /**
         * Empty constructor.
         */
        private NoValuesIterator() {}

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public int nextInt() {
            throw new NoSuchElementException();
        }
    }

    /**
     * The list of byte arrays that are to be hashed.
     * Package private for access by the iterator.
     */
    final List<byte[]> buffers;

    /**
     * The function to hash the buffers.
     * Package private for access by the iterator.
     */
    final HashFunction function;

    /**
     * Constructs a DynamicHasher.
     *
     * @param function the function to use.
     * @param buffers the byte buffers that will be hashed.
     */
    public DynamicHasher(final HashFunction function, final List<byte[]> buffers) {
        this.buffers = new ArrayList<>(buffers);
        this.function = function;
    }

    @Override
    public PrimitiveIterator.OfInt iterator(final Shape shape) {
        HashFunctionValidator.checkAreEqual(getHashFunctionIdentity(),
                                            shape.getHashFunctionIdentity());
        // Use optimised iterator for no values
        return buffers.isEmpty() ? NoValuesIterator.INSTANCE : new Iterator(shape);
    }

    @Override
    public HashFunctionIdentity getHashFunctionIdentity() {
        return function;
    }
}
