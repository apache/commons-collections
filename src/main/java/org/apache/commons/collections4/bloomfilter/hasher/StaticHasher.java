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

import java.util.Arrays;
import java.util.Iterator;
import java.util.PrimitiveIterator.OfInt;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.BloomFilter.Shape;
import org.apache.commons.collections4.bloomfilter.Hasher;

/**
 * A Hasher implementation that contains the index for all enabled bits for a specific
 * Shape.
 * @since 4.5
 */
public final class StaticHasher implements Hasher {

    /**
     * The shape of this hasher
     */
    private final BloomFilter.Shape shape;
    /**
     * The ordered set of values that this hasher will return.
     */
    private final int[] values;

    /**
     * Constructs the StaticHasher from a StaticHasher and a Shape.
     * @param hasher the StaticHasher to read.
     * @param shape the Shape for the resulting values.
     * @throws IllegalArgumentException if the shape of the hasher and the shape parameter are not the same.
     */
    public StaticHasher(StaticHasher hasher, BloomFilter.Shape shape) {
        if (!hasher.shape.equals(shape)) {
            throw new IllegalArgumentException(String.format("Hasher shape (%s) is not the same as shape (%s)",
                hasher.getShape().toString(), shape.toString()));
        }
        this.shape = shape;
        this.values = hasher.values;
    }

    /**
     * Constructs the StaticHasher from a Hasher and a Shape.
     * @param hasher the Hasher to read.
     * @param shape the Shape for the resulting values.
     * @throws IllegalArgumentException if the hasher function and the shape function are not the same.
     */
    public StaticHasher(Hasher hasher, BloomFilter.Shape shape) {
        this( hasher.getBits(shape), shape);
        if (
            HashFunctionIdentity.COMMON_COMPARATOR.compare(
            hasher.getHashFunctionIdentity(), shape.getHashFunctionIdentity()) != 0) {
            throw new IllegalArgumentException(String.format("Hasher (%s) is not the same as for shape (%s)",
                HashFunctionIdentity.asCommonString( hasher.getHashFunctionIdentity()),
                shape.toString()));
        }
    }

    /**
     * Constructs a StaticHasher from an Iterator of Integers and a Shape.
     * @param iter the Iterator of Integers.
     * @param shape the Shape that the integers were generated for.
     * @throws IllegalArgumentException if any Integer is outside the range [0,shape.getNumberOfBits())
     */
    public StaticHasher(Iterator<Integer> iter, BloomFilter.Shape shape) {
        this.shape = shape;
        Set<Integer> workingValues = new TreeSet<Integer>();
        iter.forEachRemaining( idx -> {
            if (idx >= this.shape.getNumberOfBits())
            {
                throw new IllegalArgumentException( String.format( "Bit index (%s) is too big for %s", idx, shape ));
            }
            if (idx < 0 ) {
                throw new IllegalArgumentException( String.format( "Bit index (%s) may not be less than zero", idx ));
            }
            workingValues.add( idx );
        });
        this.values = new int[workingValues.size()];
        int i=0;
        for (Integer value : workingValues)
        {
            values[i++] = value.intValue();
        }
    }

    /**
     * Gets the shape this static hasher was created with.
     *
     * @return the Shape of this hasher.
     */
    public BloomFilter.Shape getShape() {
        return shape;
    }

    @Override
    public HashFunctionIdentity getHashFunctionIdentity() {
        return shape.getHashFunctionIdentity();
    }

    /**
     * Gets the the number of unique values in this hasher.
     * @return the number of unique values.
     */
    public int size() {
        return values.length;
    }

    /**
     * Returns an iterator of integers that are the bits to enable in the Bloom
     * filter based on the shape.  The iterator will not return the same value multiple
     * times.  Values will be returned in ascending order.
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the Iterator of integers;
     * @throws IllegalArgumentException if {@code shape.getHasherName()} does not
     *                                  equal {@code getName()}
     */
    @Override
    public OfInt getBits(BloomFilter.Shape shape) {
        if (!this.shape.equals(shape)) {
            throw new IllegalArgumentException(
                String.format("shape (%s) does not match internal shape (%s)", shape, this.shape));
        }
        return Arrays.stream( values ).iterator();
    }
}
