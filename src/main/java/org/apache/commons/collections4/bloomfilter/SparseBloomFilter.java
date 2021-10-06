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

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import org.apache.commons.collections4.bloomfilter.exceptions.NoMatchException;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

/**
 * A bloom filter using a TreeSet of integers to track enabled bits. This is a standard
 * implementation and should work well for most low cardinality Bloom filters.
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
        Objects.requireNonNull( shape, "shape");
        this.shape = shape;
        this.indices = new TreeSet<Integer>();
    }

    /**
     * Constructs a populated Bloom filter.
     * @param shape the shape for the bloom filter.
     * @param hasher the hasher to provide the initial data.
     */
    public SparseBloomFilter(final Shape shape, Hasher hasher) {
        this( shape );
        Objects.requireNonNull( hasher, "hasher");
        hasher.indices(shape).forEachIndex( this.indices::add );
    }

    /**
     * Constructs a populated Bloom filter.
     * @param shape the shape of the filter.
     * @param indices a list of indices to to enable.
     * @throws IllegalArgumentException if indices contains a value greater than the number
     * of bits in the shape.
     */
    public SparseBloomFilter(Shape shape, List<Integer> indices) {
        this(shape);
        Objects.requireNonNull( indices, "indices");
        this.indices.addAll( indices );
        if (this.indices.last() >= shape.getNumberOfBits()) {
            throw new IllegalArgumentException(
                    String.format( "Value in list {} is greater than maximum value ({})",
                            this.indices.last(), shape.getNumberOfBits()));
        }
    }

    @Override
    public boolean mergeInPlace(Hasher hasher) {
        Objects.requireNonNull( hasher, "hasher");
        hasher.indices(shape).forEachIndex( this.indices::add );
        return true;
    }

    @Override
    public boolean mergeInPlace(BloomFilter other) {
        Objects.requireNonNull( other, "other");
        other.forEachIndex( indices::add );
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
    public void forEachIndex(IntConsumer consumer) {
        Objects.requireNonNull( consumer, "consumer");
        for (int value : indices ) {
            consumer.accept( value );
        }
    }

    @Override
    public void forEachBitMap(LongConsumer consumer) {
        Objects.requireNonNull( consumer, "consumer");
        if (cardinality() == 0) {
            return;
        }
        BitMapProducer.fromIndexProducer( this, shape).forEachBitMap(consumer);
    }

    @Override
    public boolean contains(IndexProducer indexProducer) {
        try {
            indexProducer.forEachIndex( idx -> { if (!indices.contains(idx)) { throw new NoMatchException(); }});
            return true;
        } catch (NoMatchException e) {
            return false;
        }
    }

    @Override
    public boolean contains(BitMapProducer bitMapProducer) {
        return contains( IndexProducer.fromBitMapProducer(bitMapProducer));
    }


}
