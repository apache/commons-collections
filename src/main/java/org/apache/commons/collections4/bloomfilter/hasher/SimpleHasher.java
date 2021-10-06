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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfInt;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;


/**
 * A Hasher that implements combinatorial hashing.
 * @since 4.5
 */
public final class SimpleHasher implements Hasher {

    /**
     * The initial hash value.
     */
    private final long initial;

    /**
     * The value to increment the hash value by.
     */
    private final long increment;


    /**
     * Constructs the SimpleHasher from 2 longs.  The long values will be interpreted as unsigned values.
     * @param initial The initial value for the hasher..
     * @param increment The value to increment the hash by on each iteration.
     */
    public SimpleHasher(long initial, long increment) {
        this.initial = initial;
        this.increment = increment;
    }


    /**
     * Gets an IndexProducer that produces indices based on the shape.
     * The iterator will not return the same value multiple
     * times.  Values will be returned in ascending order.
     *
     * @param shape {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public IndexProducer indices(final Shape shape) {
        Objects.requireNonNull( shape, "shape");

        return new IndexProducer() {
            /** The number of hash functions per item. */
            private final int k = shape.getNumberOfHashFunctions();
            /** The number of bits in the shape. */
            private final long m =  shape.getNumberOfBits();

            /** The index of the next item. */
            private long next = SimpleHasher.this.initial;
            /** The count of hash functions for the current item. */
            private int functionCount = 0;

            @Override
            public void forEachIndex(IntConsumer consumer) {
                Objects.requireNonNull( consumer, "consumer");
                TreeSet<Integer> seen = new TreeSet<Integer>();
                while (functionCount < k) {
                    seen.add((int) Long.remainderUnsigned( next, m ));
                    functionCount++;
                    next += SimpleHasher.this.increment;
                }
                seen.stream().mapToInt( s -> s.intValue() ).forEach(consumer);
            }
        };
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void forEach(Consumer<Hasher> consumer) {
        Objects.requireNonNull( consumer, "consumer");
        consumer.accept( this );
    }
}
