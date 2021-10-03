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
        return 1.0 - cosineSimilarity(first, second);
    }

    private static BitSet and(final BloomFilter first, final BloomFilter second) {
        BitSet result = BitSet.valueOf(first.getBits());
        result.and(BitSet.valueOf(second.getBits()));
        return result;
    }

    private static BitSet or(final BloomFilter first, final BloomFilter second) {
        BitSet result = BitSet.valueOf(first.getBits());
        result.or(BitSet.valueOf(second.getBits()));
        return result;
    }

    private static BitSet xor(final BloomFilter first, final BloomFilter second) {
        BitSet result = BitSet.valueOf(first.getBits());
        result.xor(BitSet.valueOf(second.getBits()));
        return result;
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
        final int numerator = and( first, second).cardinality();
        return numerator == 0 ? 0 : numerator / (Math.sqrt(first.cardinality()) * Math.sqrt(second.cardinality()));
    }



    /**
     * Calculates the Hamming distance between two Bloom filters.
     *
     * @param first the first Bloom filter.
     * @param second the second Bloom filter.
     * @return the Hamming distance.
     */
    public static int hammingDistance(final BloomFilter first, final BloomFilter second) {
        return xor(first,second).cardinality();
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
        final int orCard = or(first,second).cardinality();
        // if the orCard is zero then the hamming distance will also be zero.
        return orCard == 0 ? 0 : hammingDistance(first, second) / (double) orCard;
    }


    /**
     * Do not instantiate.
     */
    private SetOperations() {}
}

