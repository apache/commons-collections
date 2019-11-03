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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Set;
import java.util.function.ToLongBiFunction;

import org.apache.commons.collections4.bloomfilter.BloomFilter.Shape;
import org.apache.commons.collections4.bloomfilter.Hasher;

/**
 * The class that performs hashing on demand. Items can be added to the hasher using the
 * {@code with()} methods. once {@code getBits()} method is called it is an error to call
 * {@code with()} again.
 */
public class DynamicHasher implements Hasher {

    /**
     * The list of byte arrays that are to be hashed.
     */
    private final List<byte[]> buffers;

    /**
     * The function to hash the buffers.
     */
    private final ToLongBiFunction<byte[], Integer> function;

    /**
     * The name of the hash function.
     */
    private final String name;

    /**
     * Constructs a DynamicHasher.
     *
     * @param name the name for the function.
     * @param function the function to use.
     * @param buffers the byte buffers that will be hashed.
     */
    public DynamicHasher(String name, ToLongBiFunction<byte[], Integer> function, List<byte[]> buffers) {
        this.buffers = new ArrayList<byte[]>(buffers);
        this.function = function;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
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
        if (!getName().equals(shape.getHashFunctionName())) {
            throw new IllegalArgumentException(
                String.format("Shape hasher %s is not %s", shape.getHashFunctionName(), getName()));
        }
        return new Iter(shape);
    }

    /**
     * The iterator of integers.
     */
    private class Iter implements PrimitiveIterator.OfInt {
        private int buffer = 0;
        private int funcCount = 0;
        private final Shape shape;

        /**
         * Creates iterator with the specified shape.
         *
         * @param shape
         */
        private Iter(Shape shape) {
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
                return (int) Math.floorMod(function.applyAsLong(buffers.get(buffer), funcCount++),
                    (long) shape.getNumberOfBits());
            }
            throw new NoSuchElementException();
        }
    }

    /**
     * A factory that produces DynamicHasher Builders.
     *
     */
    public static class Factory implements Hasher.Factory {

        /**
         * A map of functions names to functions.
         */
        private final Map<String, Constructor<? extends ToLongBiFunction<byte[], Integer>>> funcMap;

        /**
         * Constructs a factory with well known hash functions.
         */
        public Factory() {
            funcMap = new HashMap<String, Constructor<? extends ToLongBiFunction<byte[], Integer>>>();
            try {
                register(MD5.NAME, MD5.class);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException("Can not get MD5 constructor");
            }
            try {
                register(Murmur128.NAME, Murmur128.class);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException("Can not get Murmur128 constructor");
            }
            try {
                register(Murmur32.NAME, Murmur128.class);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException("Can not get Murmur128 constructor");
            }
            try {
                register(ObjectsHash.NAME, ObjectsHash.class);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException("Can not get ObjectsHash constructor");

            }
        }

        /**
         * Registers a Hash function implementation. After registration the name can be
         * used to retrieve the Hasher. <p> The function calculates the long value that is
         * used to turn on a bit in the Bloom filter. The first argument is a
         * {@code byte[]} containing the bytes to be indexed, the second argument is a
         * seed index. </p><p> On the first call to {@code applyAsLong} the seed index
         * will be 0 and the function should start the hash sequence. </p> <p> On
         * subsequent calls the hash function using the same buffer the seed index will be
         * incremented. The function should return a different calculated value on each
         * call. The function may use the seed as part of the calculation or simply use it
         * to detect when the buffer has changed. </p>
         *
         * @see #useFunction(String)
         * @param name The name of the hash function
         * @param functionClass The function class for the hasher to use. Must have a zero
         * argument constructor.
         * @throws SecurityException if the no argument constructor can not be accessed.
         * @throws NoSuchMethodException if functionClass does not have a no argument
         * constructor.
         */
        protected void register(String name, Class<? extends ToLongBiFunction<byte[], Integer>> functionClass)
            throws NoSuchMethodException, SecurityException {
            Constructor<? extends ToLongBiFunction<byte[], Integer>> c = functionClass.getConstructor();
            funcMap.put(name, c);
        }

        @Override
        public Set<String> listFunctionNames() {
            return Collections.unmodifiableSet(funcMap.keySet());
        }

        @Override
        public DynamicHasher.Builder useFunction(String name) {
            Constructor<? extends ToLongBiFunction<byte[], Integer>> c = funcMap.get(name);
            if (c == null) {
                throw new IllegalArgumentException("No function implementation named " + name);
            }
            try {
                return new Builder(name, c.newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Unable to call constructor for " + name, e);
            }
        }
    }

    /**
     * The builder for DyanamicHashers.
     *
     */
    public static class Builder implements Hasher.Builder {
        /**
         * The list of byte[] that are to be hashed.
         */
        private List<byte[]> buffers;

        /**
         * The function that the resulting DynamicHasher will use.
         */
        private ToLongBiFunction<byte[], Integer> function;

        /**
         * The name for the function.
         */
        private String name;

        /**
         * Constructs a DynamicHasher builder.
         *
         * @param name the name of the function.
         * @param function the function implementation.
         */
        public Builder(String name, ToLongBiFunction<byte[], Integer> function) {
            this.name = name;
            this.function = function;
            this.buffers = new ArrayList<byte[]>();

        }

        /**
         * Builds the hasher.
         *
         * @return A DynamicHasher with the specified name, function and buffers.
         */
        @Override
        public DynamicHasher build() throws IllegalArgumentException {
            return new DynamicHasher(name, function, buffers);
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
