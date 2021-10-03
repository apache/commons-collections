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
import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.Consumer;

import org.apache.commons.collections4.bloomfilter.Shape;


/**
 * A Hasher ithat implemente combinatorial hashing.
 * @since 4.5
 */
public final class SimpleHasher implements Hasher {

    private final long initial;
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
     * Gets an iterator of integers that are the bits to enable in the Bloom
     * filter based on the shape.  The iterator will not return the same value multiple
     * times.  Values will be returned in ascending order.
     *
     * @param shape {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public OfInt iterator(final Shape shape) {
        return new Iterator(shape);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void forEach(Consumer<Hasher> consumer) {
        consumer.accept( this );
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
        private final long m;

        /** The index of the next item. */
        private long next;
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
            next = SimpleHasher.this.initial;
            functionCount = 0;
        }

        @Override
        public boolean hasNext() {
            return functionCount < k;
        }

        @SuppressWarnings("cast") // Cast to long to workaround a bug in animal-sniffer.
        @Override
        public int nextInt() {
            if (hasNext()) {
                int result = (int) Long.remainderUnsigned( next, m );
                functionCount++;
                next += SimpleHasher.this.increment;
                return result;
            }
            throw new NoSuchElementException();
        }
    }

}
