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

import java.util.function.Function;

import org.apache.commons.collections4.bloomfilter.hasher.DynamicHasher;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.function.Murmur128x64Cyclic;

/**
 * A bloom filter that uses a BitSetBloomFilter to create a Bloom filter that
 * can merge instances of a specific class.
 *
 * @param <T> The Class to merge.
 * @since 4.5
 */
public class SimpleBloomFilter<T> extends BitSetBloomFilter implements BloomFilter {

    /**
     * The function that converts the instance of T to the SimpleBuilder.
     * <p>
     * If the object T is to be considered as a single item in the filter then
     * function must create the {@code SimpleBuilder} and only call a single {@code with()}
     * method.</p>
     * <p>
     * If the object T is to be considered as several items then the function must
     * create the {@code SimpleBuilder} and call the {@code with()} method once for each item.</p>
     */
    private Function<T, SimpleBuilder> func;

    /**
     * Constructs a SimpleBloomFilter from the shape and function.  This constructor
     * creates an empty Bloom filter.
     * @param hasher the Hasher to use.
     * @param shape the Shape of the Bloom filter.
     * @param func a Function to convert T to a SimpleBuilder.
     * @see #func
     */
    public SimpleBloomFilter(Hasher hasher, Shape shape, Function<T, SimpleBuilder> func) {
        super(hasher, shape);
        this.func = func;
    }

    /**
     * Constructs a SimpleBloomFilter from the shape and function.  This constructor
     * creates an empty Bloom filter.
     * @param shape the Shape of the Bloom filter.
     * @param func a Function to convert T to a SimpleBuilder.
     * @see #func
     */
    public SimpleBloomFilter(Shape shape, Function<T, SimpleBuilder> func) {
        super(shape);
        this.func = func;
    }

    /**
     * Constructs a SimpleBloomFilter from the shape, function and a data object.
     * This constructor creates an Bloom filter populated with the data from the
     * {@code data} parameter.
     * @param shape the Shape of the Bloom filter.
     * @param func a Function to convert T to a SimpleBuilder.
     * @param data the data object to populate the filter with.
     * @see #func
     */
    public SimpleBloomFilter(Shape shape, Function<T, SimpleBuilder> func, T data) {
        this(shape, func);
        this.merge( data );
    }

    /**
     * Merges a data object into this filter.
     *
     * <p>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter is not ensured to contain
     * the {@code data}.
     *
     * @param data the data to merge.
     * @return true if the merge was successful
     * @throws IllegalArgumentException if the shape of the other filter does not match
     * the shape of this filter
     */
    public boolean merge( T data ) {
        return this.merge( this.func.apply( data ).build() );
    }


    /**
     * Returns {@code true} if this filter contains the object.
     * Specifically this returns {@code true} if this filter is enabled for all bit indexes
     * identified by the hashing of the object {@code data}.
     *
     * @param data the data to check for.
     * @return true if this filter is enabled for all bits specified by the data object.
     */
    public boolean contains( T data ) {
        return this.contains( this.func.apply( data ).build() );
    }

    /**
     * A Hasher.Builder for the SimpleBloom filter.
     * This builder uses the Murmur 128 x64 cyclic hash.
     *
     * @see Murmur128x64Cyclic
     */
    public static class SimpleBuilder extends DynamicHasher.Builder {

        public SimpleBuilder() {
            super(new Murmur128x64Cyclic());
        }
    }
}
