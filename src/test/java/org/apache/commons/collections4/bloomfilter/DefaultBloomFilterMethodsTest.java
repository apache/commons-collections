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

import java.util.BitSet;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;

/**
 * Test all the default implementations of the BloomFilter in {@link AbstractBloomFilter}.
 */
public class DefaultBloomFilterMethodsTest extends AbstractBloomFilterTest {

    /**
     * A testing class that implements only the abstract methods from BloomFilter.
     *
     */
    private static class BF extends AbstractBloomFilter {

        /**
         * The bitset that defines this BloomFilter.
         */
        private final BitSet bitSet;

        /**
         * Constructs a BitSetBloomFilter from a hasher and a shape.
         *
         * @param hasher the Hasher to use.
         * @param shape the desired shape of the filter.
         */
        BF(final Hasher hasher, final Shape shape) {
            this(shape);
            verifyHasher(hasher);
            hasher.getBits(shape).forEachRemaining((IntConsumer) bitSet::set);
        }

        /**
         * Constructs an empty BitSetBloomFilter.
         *
         * @param shape the desired shape of the filter.
         */
        BF(final Shape shape) {
            super(shape);
            this.bitSet = new BitSet();
        }

        @Override
        public long[] getBits() {
            return bitSet.toLongArray();
        }

        @Override
        public StaticHasher getHasher() {
            return new StaticHasher(bitSet.stream().iterator(), getShape());
        }

        @Override
        public boolean merge(final BloomFilter other) {
            verifyShape(other);
            bitSet.or(BitSet.valueOf(other.getBits()));
            return true;
        }

        @Override
        public boolean merge(final Hasher hasher) {
            verifyHasher(hasher);
            hasher.getBits(getShape()).forEachRemaining((IntConsumer) bitSet::set);
            return true;
        }
    }

    @Override
    protected AbstractBloomFilter createEmptyFilter(final Shape shape) {
        return new BF(shape);
    }

    @Override
    protected AbstractBloomFilter createFilter(final Hasher hasher, final Shape shape) {
        return new BF(hasher, shape);
    }
}
