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
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.ProcessType;

/**
 * An implementation of Hasher that attempts to leak as little data as possible.
 * Each item in the hasher is represented by two (2) longs.  So this Hasher will
 * still indicate how many items are in the hasher but will not leak the buffers
 * that are being hashed as the {@code DynamicHasher} does.
 * <p>
 * This hasher only accepts HashFunctions that are cyclic in nature.
 * </p>
 * @see DynamicHasher
 * @see ProcessType
 */
public class CachingHasher implements Hasher {

    /**
     * The list of byte arrays that are to be hashed.
     */
    private final List<long[]> buffers;

    /**
     * The hash function identity
     */
    private final HashFunctionIdentity functionIdentity;

    /**
     * Constructs a CachingHasher from a list of arrays of hash values.
     * <p>
     * The list of hash values comprises a {@code List&lt;long[]&gt;} where each {@code long[]}
     * is comprises two (2) values that are the result of hashing the original buffer.  Thus a
     * CachingHasher that was built from five (5) buffers will have five arrays of two {@code longs}
     * each.
     * </p>
     * @param functionIdentity The identity of the function.
     * @param buffers          a list of {@code long} arrays comprising two values.
     * @throws IllegalArgumentException if the name does not indicate a cyclic
     *                                  hashing function.
     */
    public CachingHasher(HashFunctionIdentity functionIdentity, List<long[]> buffers) {
        this.functionIdentity = checkIdentity(functionIdentity);
        this.buffers = new ArrayList<long[]>(buffers);
    }

    /**
     * Constructs a CachingHasher from an array of arrays of hash values.
     *
     * @param functionIdentity The identity of the function.
     * @param buffers          An array of {@code long} arrays comprising two (2) values.
     * @throws IllegalArgumentException if the name does not indicate a cyclic
     *                                  hashing function.
     */
    public CachingHasher(HashFunctionIdentity functionIdentity, long[][] buffers) {
        this.functionIdentity = checkIdentity(functionIdentity);
        this.buffers = Arrays.asList(buffers);
    }

    /**
     * Checks that the name is valid for this hasher.
     *
     * @param functionIdentity the Function Identity to check.
     */
    private static HashFunctionIdentity checkIdentity(HashFunctionIdentity functionIdentity) {
        if (functionIdentity.getProcessType() != ProcessType.CYCLIC) {
            throw new IllegalArgumentException("Only cyclic hash functions may be used in a caching hasher");
        }
        return functionIdentity;
    }

    @Override
    public HashFunctionIdentity getHashFunctionIdentity() {
        return functionIdentity;
    }

    @Override
    public boolean isEmpty() {
        return buffers.isEmpty();
    }


    @Override
    public PrimitiveIterator.OfInt getBits(Shape shape) {
        HashFunctionValidator.checkAreEqual(getHashFunctionIdentity(),
            shape.getHashFunctionIdentity());
        return new IntIterator(shape);
    }

    /**
     * Gets the long representations of the buffers.
     * <p>
     * This method returns the long representations of the buffers.  This is commonly used
     * to transmit the Hasher from one system to another.
     * </p><p>
     *  the List&lt;long[]&gt; will contains zero or more entries, each entry is a non-null array
     *  of length two containing the 64-bit pair output from the cyclic hash function.
     *  </p>
     *
     * @return a copy if the long buffer representation.
     */
    public List<long[]> getBuffers() {
        return new ArrayList<long[]>( buffers );
    }

    /**
     * The iterator of integers.
     */
    private class IntIterator implements PrimitiveIterator.OfInt {
        private int buffer = 0;
        private int funcCount = 0;
        private final Shape shape;
        private long accumulator;

        /**
         * Creates iterator with the specified shape.
         *
         * @param shape
         */
        private IntIterator(Shape shape) {
            this.shape = shape;
            this.accumulator = buffers.isEmpty() ? 0 : buffers.get(0)[0];
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
                    accumulator = buffers.get(buffer)[0];
                }
                int result = (int) Math.floorMod(accumulator, (long) shape.getNumberOfBits());
                funcCount++;
                accumulator += buffers.get(buffer)[1];
                return result;
            }
            throw new NoSuchElementException();
        }

    }

    /**
     * The builder for CachingHashers.
     *
     * @since 4.5
     */
    public static class Builder implements Hasher.Builder {
        /**
         * The list of byte[] that are to be hashed.
         */
        private List<byte[]> buffers;

        /**
         * The function that the resulting DynamicHasher will use.
         */
        private HashFunction function;

        /**
         * Constructs a CachingHashers builder.
         *
         * @param function the function implementation.
         * @throws IllegalArgumentException if the function is not a cyclic implementation.
         */
        public Builder(HashFunction function) {
            checkIdentity(function);
            this.function = function;
            this.buffers = new ArrayList<byte[]>();

        }

        /**
         * Builds the hasher.
         *
         * @return A CachingHashers with the specified name, function and buffers.
         */
        @Override
        public final CachingHasher build() throws IllegalArgumentException {
            List<long[]> cache = new ArrayList<long[]>();
            for (byte[] buff : buffers) {
                long[] result = new long[2];
                result[0] = function.apply(buff, 0);
                result[1] = function.apply(buff, 1) - result[0];
                cache.add(result);
            }
            return new CachingHasher(function, cache);
        }

        @Override
        public final Builder with(byte property) {
            return with(new byte[] { property });
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
