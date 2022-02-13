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

/**
 * Implementations of set operations on BitMapProducers.
 *
 * @since 4.5
 */
public final class SetOperations {

    /**
     * Calculates the cardinality using the {@code BitMapProducer.makePredicate} method.
     * @param first the first BitMapProducer
     * @param second the second BitMapProducer
     * @param op a long binary operation on where x = first and y = second bitmap producers.
     * @return the calculated cardinality.
     */
    private static int cardinality(BitMapProducer first, BitMapProducer second, LongBinaryOperator op) {
        int[] cardinality = new int[1];

        LongPredicate lp = first.makePredicate((x, y) -> {
            cardinality[0] += Long.bitCount(op.applyAsLong(x, y));
            return true;
        });
        second.forEachBitMap(lp);
        return cardinality[0];
    }

    public static int cardinality(BitMapProducer producer) {
        int[] cardinality = new int[1];
        producer.forEachBitMap(l -> {
            cardinality[0] += Long.bitCount(l);
            return true;
        });
        return cardinality[0];
    }

    /**
     * Calculates the cardinality of the logical {@code AND} of the bit maps for the two filters.
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer
     * @return the cardinality of the {@code AND} of the filters.
     */
    public static int andCardinality(final BitMapProducer first, final BitMapProducer second) {
        return cardinality(first, second, (x, y) -> x & y);
    }

    /**
     * Calculates the cardinality of the logical {@code OR} of the bit maps for the two filters.
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer
     * @return the cardinality of the {@code OR} of the filters.
     */
    public static int orCardinality(final BitMapProducer first, final BitMapProducer second) {
        return cardinality(first, second, (x, y) -> x | y);
    }

    /**
     * Calculates the cardinality of the logical {@code XOR} of the bit maps for the two filters.
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer
     * @return the cardinality of the {@code XOR} of the filters.
     */
    public static int xorCardinality(final BitMapProducer first, final BitMapProducer second) {
        return cardinality(first, second, (x, y) -> x ^ y);
    }

    /**
     * Calculates the Cosine distance between two BitMapProducer.
     *
     * <p>Cosine distance is defined as {@code 1 - Cosine similarity}</p>
     *
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer.
     * @return the jaccard distance.
     */
    public static double cosineDistance(final BitMapProducer first, final BitMapProducer second) {
        return 1.0 - cosineSimilarity(first, second);
    }

    /**
     * Calculates the Cosine similarity between two BitMapProducers.
     * <p> Also known as Orchini similarity and the Tucker coefficient of congruence or
     * Ochiai similarity.</p>
     *
     * <p>If either producer is empty the result is 0 (zero)</p>
     *
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer.
     * @return the Cosine similarity.
     */
    public static double cosineSimilarity(final BitMapProducer first, final BitMapProducer second) {
        final int numerator = andCardinality(first, second);
        // Given that the cardinality is an int then the product as a double will not
        // overflow, we can use one sqrt:
        return numerator == 0 ? 0 : numerator / Math.sqrt(cardinality(first) * cardinality(second));
    }

    /**
     * Calculates the Cosine similarity between two Bloom filters.
     * <p> Also known as Orchini similarity and the Tucker coefficient of congruence or
     * Ochiai similarity.</p>
     *
     * <p>If either filter is empty (no enabled bits) the result is 0 (zero)</p>
     *
     * <p>This is a version of cosineSimilarity optimized for Bloom filters.</p>
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return the Cosine similarity.
     */
    public static double cosineSimilarity(final BloomFilter first, final BloomFilter second) {
        final int numerator = andCardinality(first, second);
        // Given that the cardinality is an int then the product as a double will not
        // overflow, we can use one sqrt:
        return numerator == 0 ? 0 : numerator / Math.sqrt(first.cardinality() * second.cardinality());
    }

    /**
     * Calculates the Hamming distance between two BitMapProducers.
     *
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer.
     * @return the Hamming distance.
     */
    public static int hammingDistance(final BitMapProducer first, final BitMapProducer second) {
        return xorCardinality(first, second);
    }

    /**
     * Calculates the Jaccard distance between two BitMapProducer.
     *
     * <p>Jaccard distance is defined as {@code 1 - Jaccard similarity}</p>
     *
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer.
     * @return the Jaccard distance.
     */
    public static double jaccardDistance(final BitMapProducer first, final BitMapProducer second) {
        return 1.0 - jaccardSimilarity(first, second);
    }

    /**
     * Calculates the Jaccard similarity between two BitMapProducer.
     *
     * <p>Also known as Jaccard index, Intersection over Union, and Jaccard similarity coefficient</p>
     *
     * @param first the first BitMapProducer.
     * @param second the second BitMapProducer.
     * @return the Jaccard similarity.
     */
    public static double jaccardSimilarity(final BitMapProducer first, final BitMapProducer second) {
        final int intersection = andCardinality(first, second);
        return intersection == 0 ? 0 : intersection / (double) orCardinality(first, second);
    }

    /**
     * Do not instantiate.
     */
    private SetOperations() {
    }
}
