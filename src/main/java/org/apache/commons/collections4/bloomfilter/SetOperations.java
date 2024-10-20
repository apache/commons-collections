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

/**
 * Implementations of set operations on BitMapExtractors.
 *
 * @since 4.5.0-M1
 */
public final class SetOperations {

    /**
     * Calculates the cardinality of the logical {@code AND} of the bit maps for the two filters.
     *
     * @param first  the first BitMapExtractor.
     * @param second the second BitMapExtractor
     * @return the cardinality of the {@code AND} of the filters.
     */
    public static int andCardinality(final BitMapExtractor first, final BitMapExtractor second) {
        return cardinality(first, second, (x, y) -> x & y);
    }

    /**
     * Calculates the cardinality of a BitMapExtractor. By necessity this method will visit each bit map created by the bitMapExtractor.
     *
     * @param bitMapExtractor the extractor to calculate the cardinality for.
     * @return the cardinality of the bit maps produced by the bitMapExtractor.
     */
    public static int cardinality(final BitMapExtractor bitMapExtractor) {
        final int[] cardinality = new int[1];
        bitMapExtractor.processBitMaps(l -> {
            cardinality[0] += Long.bitCount(l);
            return true;
        });
        return cardinality[0];
    }

    /**
     * Calculates the cardinality of the result of a LongBinaryOperator using the {@code BitMapExtractor.makePredicate} method.
     *
     * @param first  the first BitMapExtractor
     * @param second the second BitMapExtractor
     * @param op     a long binary operation on where x = {@code first} and y = {@code second} bitmap extractors.
     * @return the calculated cardinality.
     */
    private static int cardinality(final BitMapExtractor first, final BitMapExtractor second, final LongBinaryOperator op) {
        final int[] cardinality = new int[1];

        first.processBitMapPairs(second, (x, y) -> {
            cardinality[0] += Long.bitCount(op.applyAsLong(x, y));
            return true;
        });
        return cardinality[0];
    }

    /**
     * Calculates the Cosine distance between two BitMapExtractor.
     * <p>
     * Cosine distance is defined as {@code 1 - Cosine similarity}
     * </p>
     *
     * @param first  the first BitMapExtractor.
     * @param second the second BitMapExtractor.
     * @return the jaccard distance.
     */
    public static double cosineDistance(final BitMapExtractor first, final BitMapExtractor second) {
        return 1.0 - cosineSimilarity(first, second);
    }

    /**
     * Calculates the Cosine similarity between two BitMapExtractors.
     * <p>
     * Also known as Orchini similarity and the Tucker coefficient of congruence or Ochiai similarity.
     * </p>
     * <p>
     * If either extractor is empty the result is 0 (zero)
     * </p>
     *
     * @param first  the first BitMapExtractor.
     * @param second the second BitMapExtractor.
     * @return the Cosine similarity.
     */
    public static double cosineSimilarity(final BitMapExtractor first, final BitMapExtractor second) {
        final int numerator = andCardinality(first, second);
        // Given that the cardinality is an int then the product as a double will not
        // overflow, we can use one sqrt:
        return numerator == 0 ? 0 : numerator / Math.sqrt(cardinality(first) * cardinality(second));
    }

    /**
     * Calculates the Cosine similarity between two Bloom filters.
     * <p>
     * Also known as Orchini similarity and the Tucker coefficient of congruence or Ochiai similarity.
     * </p>
     * <p>
     * If either filter is empty (no enabled bits) the result is 0 (zero)
     * </p>
     * <p>
     * This is a version of cosineSimilarity optimized for Bloom filters.
     * </p>
     *
     * @param first  the first Bloom filter.
     * @param second the second Bloom filter.
     * @return the Cosine similarity.
     */
    public static double cosineSimilarity(final BloomFilter<?> first, final BloomFilter<?> second) {
        final int numerator = andCardinality(first, second);
        // Given that the cardinality is an int then the product as a double will not
        // overflow, we can use one sqrt:
        return numerator == 0 ? 0 : numerator / Math.sqrt(first.cardinality() * second.cardinality());
    }

    /**
     * Calculates the Hamming distance between two BitMapExtractors.
     *
     * @param first  the first BitMapExtractor.
     * @param second the second BitMapExtractor.
     * @return the Hamming distance.
     */
    public static int hammingDistance(final BitMapExtractor first, final BitMapExtractor second) {
        return xorCardinality(first, second);
    }

    /**
     * Calculates the Jaccard distance between two BitMapExtractor.
     * <p>
     * Jaccard distance is defined as {@code 1 - Jaccard similarity}
     * </p>
     *
     * @param first  the first BitMapExtractor.
     * @param second the second BitMapExtractor.
     * @return the Jaccard distance.
     */
    public static double jaccardDistance(final BitMapExtractor first, final BitMapExtractor second) {
        return 1.0 - jaccardSimilarity(first, second);
    }

    /**
     * Calculates the Jaccard similarity between two BitMapExtractor.
     * <p>
     * Also known as Jaccard index, Intersection over Union, and Jaccard similarity coefficient
     * </p>
     *
     * @param first  the first BitMapExtractor.
     * @param second the second BitMapExtractor.
     * @return the Jaccard similarity.
     */
    public static double jaccardSimilarity(final BitMapExtractor first, final BitMapExtractor second) {
        final int[] cardinality = new int[2];
        first.processBitMapPairs(second, (x, y) -> {
            cardinality[0] += Long.bitCount(x & y);
            cardinality[1] += Long.bitCount(x | y);
            return true;
        });
        final int intersection = cardinality[0];
        return intersection == 0 ? 0 : intersection / (double) cardinality[1];
    }

    /**
     * Calculates the cardinality of the logical {@code OR} of the bit maps for the two filters.
     *
     * @param first  the first BitMapExtractor.
     * @param second the second BitMapExtractor
     * @return the cardinality of the {@code OR} of the filters.
     */
    public static int orCardinality(final BitMapExtractor first, final BitMapExtractor second) {
        return cardinality(first, second, (x, y) -> x | y);
    }

    /**
     * Calculates the cardinality of the logical {@code XOR} of the bit maps for the two filters.
     *
     * @param first  the first BitMapExtractor.
     * @param second the second BitMapExtractor
     * @return the cardinality of the {@code XOR} of the filters.
     */
    public static int xorCardinality(final BitMapExtractor first, final BitMapExtractor second) {
        return cardinality(first, second, (x, y) -> x ^ y);
    }

    /**
     * Do not instantiate.
     */
    private SetOperations() {
    }
}
