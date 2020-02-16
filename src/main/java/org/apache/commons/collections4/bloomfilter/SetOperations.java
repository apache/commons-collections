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
package org.apache.commons.collections4.bloomfilter;

import org.apache.commons.collections4.bloomfilter.hasher.Shape;

/**
 * Implementations of set operations on Bloom filters.
 *
 */
public final class SetOperations {

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
        return 1.0 - cosineSimilarity(first,second);
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
        verifyShape(first,second);
        final int numerator = first.andCardinality(second);

        return numerator==0?0:numerator / (Math.sqrt(first.cardinality()) * Math.sqrt(second.cardinality()));
    }

    /**
     * Estimates the number of items in the intersection of the sets represented by two
     * Bloom filters.
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return an estimate of the size of the intersection between the two filters.
     */
    public static long estimateIntersectionSize(final BloomFilter first, final BloomFilter second) {
        verifyShape(first,second);
        // do subtraction early to avoid Long overflow.
        return estimateSize(first) - estimateUnionSize(first,second) + estimateSize(second);
    }


    /**
     * Estimates the number of items in the Bloom filter based on the shape and the number
     * of bits that are enabled.
     *
     * @param filter the Bloom filter to estimate size for.
     * @return an estimate of the number of items that were placed in the Bloom filter.
     */
    public static long estimateSize(final BloomFilter filter) {
        final Shape shape = filter.getShape();
        final double estimate = -(shape.getNumberOfBits() *
            Math.log(1.0 - filter.cardinality() * 1.0 / shape.getNumberOfBits())) /
            shape.getNumberOfHashFunctions();
        return Math.round(estimate);
    }

    /**
     * Estimates the number of items in the union of the sets represented by two
     * Bloom filters.
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return an estimate of the size of the union between the two filters.
     */
    public static long estimateUnionSize(final BloomFilter first, final BloomFilter second) {
        verifyShape(first,second);
        final Shape shape = first.getShape();
        final double estimate = -(shape.getNumberOfBits() *
            Math.log(1.0 - first.orCardinality(second) * 1.0 / shape.getNumberOfBits())) /
            shape.getNumberOfHashFunctions();
        return Math.round(estimate);
    }

    /**
     * Calculates the Hamming distance between two Bloom filters.
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return the Hamming distance.
     */
    public static int hammingDistance(final BloomFilter first, final BloomFilter second) {
        verifyShape(first,second);
        return first.xorCardinality(second);
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
        return 1.0 - jaccardSimilarity(first,second);
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
        verifyShape(first,second);
        final int orCard = first.orCardinality(second);
        // if the orCard is zero then the hamming distance will also be zero.
        return orCard==0?0:hammingDistance(first,second) / (double) orCard;
    }

    /**
     * Verifies the Bloom filters have the same shape.
     *
     * @param first the first filter to check.
     * @param second the second filter to check.
     * @throws IllegalArgumentException if the shapes are not the same.
     */
    private static void verifyShape(final BloomFilter first, final BloomFilter second) {
        if (!first.getShape().equals(second.getShape())) {
            throw new IllegalArgumentException(String.format("Shape %s is not the same as %s",
                first.getShape(), second.getShape()));
        }
    }

    /**
     * Do not instantiate.
     */
    private SetOperations() {}
}
