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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfInt;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.IntConsumer;

import javax.swing.event.ListSelectionEvent;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.SimpleHasher;

/**
 * A bloom filter using a Java BitSet to track enabled bits. This is a standard
 * implementation and should work well for most Bloom filters.
 * @since 4.5
 */
public class SparseBloomFilter implements BloomFilter {

    /**
     * The bitSet that defines this BloomFilter.
     */
    private final TreeSet<Integer> indices;
    private final Shape shape;

    /**
     * Constructs an empty BitSetBloomFilter.
     *
     */
    public SparseBloomFilter(Shape shape) {
        this.shape = shape;
        this.indices = new TreeSet<Integer>();
    }

    public SparseBloomFilter(final Shape shape, Hasher hasher) {
        this( shape );
        hasher.iterator(shape).forEachRemaining( (IntConsumer) i -> indices.add( i ));
    }

    public SparseBloomFilter(Shape shape, List<Integer> indices) {
        this(shape);
        this.indices.addAll( indices );
    }

    @Override
    public boolean mergeInPlace(Hasher hasher) {
        PrimitiveIterator.OfInt iter =  hasher.iterator(shape);
        while (iter.hasNext()) {
            indices.add( iter.next() );
        }
        return true;
    }

    @Override
    public boolean mergeInPlace(BloomFilter other) {
        for (int i : other.getIndices()) {
            indices.add(i);
        }
        return true;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean isSparse() {
        return true;
    }

    @Override
    public int cardinality() {
        return indices.size();
    }

    @Override
    public long[] getBits() {
        if (cardinality() == 0) {
            return new long[0];
        }
        long[] result = new long[ BitMap.numberOfBuckets( indices.last() )];
        for (Integer idx : indices)
        {
            result[ BitMap.getLongIndex( idx.intValue()) ] |= BitMap.getLongBit(idx.intValue());
        }
        return result;
    }

    @Override
    public int[] getIndices() {
        int[] result = new int[ indices.size() ];
        int i=0;
        for (int value : indices ) {
            result[i++]=value;
        }
        return result;
    }


 }
