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

import java.util.function.LongBinaryOperator;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;

/**
 * Implementations of set operations on Bloom filters.
 *
 * @since 4.5
 */
public final class SetOperations {

    /**
     * A helper class that calculates cardinality as the cardinality of the result of an operation on a two BitMap arrays.
     *
     * <p>The first array is build in the constructor.  The second array is processed as a LongConsumer.  Whenever there are
     * two values the op2 operation is used.  Whenever the one array is longer than the other the op1 operation is used on the
     * bitMaps that do not have matching entries.</p>
     *
     * <p>The calculated cardinalities are summed to return the cardinality of the operation.</p>
     *
     */
    private static class CardCounter implements LongPredicate {
        /**
         * The calculated cardinality
         */
        private int cardinality = 0;
        /**
         * The index into the array of BitMaps
         */
        private int idx = 0;
        /**
         * The array of BitMaps
         */
        private long[] bitMaps;
        /**
         * The operator to execute for 2 BitMaps
         */
        private LongBinaryOperator op2;
        /**
         * The operator to execute for a single BitMap;
         */
        private LongUnaryOperator op1;

        /**
         * Constructor.
         * @param producer The the producer for the initial BitMaps.
         * @param The shape of the Bloom filter
         * @param op2 The operation to execute when there are two BitMaps to compare.
         * @param op1 The operation to execute when there is only one BitMap to compare.
         */
        CardCounter(BitMapProducer producer, Shape shape, LongBinaryOperator op2, LongUnaryOperator op1) {
            ArrayBuilder builder = new ArrayBuilder(shape);
            producer.forEachBitMap(builder);
            this.bitMaps = builder.getArray();
            this.op2 = op2;
            this.op1 = op1;
        }

        @Override
        public boolean test(long bitMap) {
            if (idx < bitMaps.length) {
                cardinality += Long.bitCount(op2.applyAsLong(bitMaps[idx++], bitMap));
            } else {
                cardinality += Long.bitCount(op1.applyAsLong(bitMap));
            }
            return true;
        }

        /**
         * Gets the cardinality value.
         * @return The accumulated cardinality.
         */
        int getCardinality() {
            for (; idx < bitMaps.length; idx++) {
                cardinality += Long.bitCount(op1.applyAsLong(bitMaps[idx]));
            }
            return cardinality;
        }
    }

    /**
     * Calculates the cardinality of the logical {@code AND} of the BitMaps for the two filters.
     * @param shape the shape of the filter
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer
     * @return the cardinality of the {@code AND} of the filters.
     */
    public static int andCardinality(final Shape shape, final BitMapProducer first, final BitMapProducer second) {
        CardCounter lc = new CardCounter(first, shape, (x, y) -> x & y, (x) -> 0);
        second.forEachBitMap(lc);
        return lc.getCardinality();
    }

    /**
     * Calculates the cardinality of the logical {@code OR} of the BitMaps for the two filters.
     * @param shape the shape of the filter
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer
     * @return the cardinality of the {@code OR} of the filters.
     */
    public static int orCardinality(final Shape shape, final BitMapProducer first, final BitMapProducer second) {
        CardCounter lc = new CardCounter(first, shape, (x, y) -> x | y, (x) -> x);
        second.forEachBitMap(lc);
        return lc.getCardinality();
    }

    /**
     * Calculates the cardinality of the logical {@code XOR} of the BitMaps for the two filters.
     * @param shape the shape of the filter
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer
     * @return the cardinality of the {@code XOR} of the filters.
     */
    public static int xorCardinality(final Shape shape, final BitMapProducer first, final BitMapProducer second) {
        CardCounter lc = new CardCounter(first, shape, (x, y) -> x ^ y, (x) -> x);
        second.forEachBitMap(lc);
        return lc.getCardinality();
    }

    /**
     * Calculates the Cosine distance between two Bloom filters.
     *
     * <p>Cosine distance is defined as {@code 1 - Cosine similarity}</p>
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return the jaccard distance.
     */
    public static double cosineDistance(final BloomFilter first, final BloomFilter second) {
        return 1.0 - cosineSimilarity(first, second);
    }

    /**
     * Calculates the Cosine similarity between two Bloom filters.
     * <p> Also known as Orchini similarity and the Tucker coefficient of congruence or
     * Ochiai similarity.</p>
     *
     * <p>If either filter is empty (no enabled bits) the result is 0 (zero)</p>
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return the Cosine similarity.
     */
    public static double cosineSimilarity(final BloomFilter first, final BloomFilter second) {
        final int numerator = andCardinality(first.getShape(), first, second);
        // Given that the cardinality is an int then the product as a double will not overflow, we can use one sqrt:
        return numerator == 0 ? 0 : numerator / (Math.sqrt(first.cardinality() * second.cardinality()));
    }

    /**
     * Calculates the Hamming distance between two Bloom filters.
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return the Hamming distance.
     */
    public static int hammingDistance(final BloomFilter first, final BloomFilter second) {
        return xorCardinality(first.getShape(), first, second);
    }

    /**
     * Calculates the Jaccard distance between two Bloom filters.
     *
     * <p>Jaccard distance is defined as {@code 1 - Jaccard similarity}</p>
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return the Jaccard distance.
     */
    public static double jaccardDistance(final BloomFilter first, final BloomFilter second) {
        return 1.0 - jaccardSimilarity(first, second);
    }

    /**
     * Calculates the Jaccard similarity between two Bloom filters.
     *
     * <p>Also known as Jaccard index, Intersection over Union, and Jaccard similarity coefficient</p>
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return the Jaccard similarity.
     */
    public static double jaccardSimilarity(final BloomFilter first, final BloomFilter second) {
        final int intersection = andCardinality(first.getShape(), first, second);
        return intersection == 0 ? 0 : intersection / (double) orCardinality(first.getShape(), first, second);
    }

    /**
     * Do not instantiate.
     */
    private SetOperations() {
    }
}
