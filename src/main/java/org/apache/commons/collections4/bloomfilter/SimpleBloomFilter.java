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

import java.util.Arrays;
import java.util.BitSet;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;

/**
 * A bloom filter using a Java BitSet to track enabled bits. This is a standard
 * implementation and should work well for most Bloom filters.
 * @since 4.5
 */
public class SimpleBloomFilter implements BloomFilter {

    /**
     * The bitSet that defines this BloomFilter.
     */
    private final BitSet bitSet;
    private final Shape shape;

    /**
     * Constructs an empty BitSetBloomFilter.
     *
     */
    public SimpleBloomFilter(Shape shape) {
        this.shape = shape;
        this.bitSet = new BitSet();
    }

    public SimpleBloomFilter(final Shape shape, Hasher hasher) {
        this( shape );
        hasher.iterator(shape).forEachRemaining( (IntConsumer) i -> bitSet.set(i));
    }

    @Override
    public boolean mergeInPlace(BloomFilter other) {
        if (other.isSparse()) {
            Arrays.stream(other.getIndices()).forEach( s -> bitSet.set( s ));
        } else {
            bitSet.or( BitSet.valueOf(other.getBits() ));
        }
        return true;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public int cardinality() {
        return bitSet.cardinality();
    }

    @Override
    public long[] getBits() {
        return bitSet.toLongArray();
    }

    @Override
    public int[] getIndices() {
        int[] result = new int[ bitSet.cardinality() ];
        int idx = 0;
        for (int i=0;i<result.length;i++)
        {
            idx = bitSet.nextSetBit(idx);
            result[i] = idx;
            idx++;
        }
        return result;
    }


 }
