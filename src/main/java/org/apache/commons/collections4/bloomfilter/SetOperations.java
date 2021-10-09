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
import java.util.function.LongConsumer;
import java.util.function.LongUnaryOperator;

/**
 * Implementations of set operations on Bloom filters.
 *
 * @since 4.5
 */
public final class SetOperations {


    /**
     * Calculates cardinality from BitMaps.
     *
     * When there are 2 words to compare the op2 is executed and then the cardinality
     * of the resulting word is calculated.
     *
     * When there is only one word to execute on the op1 is executed and the cardinality
     * of the resulting word is caluclated.
     *
     * The calculated cardinalities are summed to return the cardinality of the operation.
     *
     */
    private static class CardCounter implements LongConsumer {
        /**
         * The calculated cardinality
         */
        private int cardinality = 0;
        /**
         * The index into the array of words
         */
        private int idx=0;
        /**
         * The array of words
         */
        private long[] words;
        /**
         * The operator to execute for 2 words
         */
        private LongBinaryOperator op2;
        /**
         * The operator to execute for a single word;
         */
        private LongUnaryOperator op1;

        /**
         * Constructor.
         * @param words The array of BitMap words for a Bloom filter
         * @param op2 The operation to execute when there are two words to compare.
         * @param op1 The operation to execute when there is only one word to cmpare.
         */
        public CardCounter( long[] words, LongBinaryOperator op2, LongUnaryOperator op1 ) {
            this.words = words;
            this.op2 = op2;
            this.op1 = op1;
        }

        @Override
        public void accept(long word) {
            if (idx<words.length) {
                cardinality += Long.bitCount( op2.applyAsLong( words[idx++], word ));
            } else {
                cardinality += Long.bitCount( op1.applyAsLong( word ));
            }
        }

        /**
         * Gets the cardinality value.
         * @return The accumulated cardinality.
         */
        int getCardinality() {
            for ( ;idx<words.length;idx++) {
                cardinality += Long.bitCount( op1.applyAsLong(words[idx]) );
            }
            return cardinality;
        }
    }


    /**
     * Calculates the cardinality of the logical AND of the BitMaps for the two filters.
     * @param first the first filter.
     * @param second the second filter
     * @return the cardinality of the AND of the filters.
     */
    public static int andCardinality(final BloomFilter first, final BloomFilter second) {
        BitMapProducer.ArrayBuilder builder = new BitMapProducer.ArrayBuilder( first.getShape());
        first.forEachBitMap( builder );
        CardCounter lc = new CardCounter(builder.getArray(), (x,y)->x&y, (x)->0);
        second.forEachBitMap(lc);
        return lc.getCardinality();
    }

    /**
     * Calculates the cardinality of the logical OR of the BitMaps for the two filters.
     * @param first the first filter.
     * @param second the second filter
     * @return the cardinality of the OR of the filters.
     */
    public static int orCardinality(final BloomFilter first, final BloomFilter second) {
        BitMapProducer.ArrayBuilder builder = new BitMapProducer.ArrayBuilder( first.getShape());
        first.forEachBitMap( builder );
        CardCounter lc = new CardCounter(builder.getArray(), (x,y)->x|y, (x)->x);
        second.forEachBitMap(lc);
        return lc.getCardinality();
    }

    /**
     * Calculates the cardinality of the logical XOR of the BitMaps for the two filters.
     * @param first the first filter.
     * @param second the second filter
     * @return the cardinality of the XOR of the filters.
     */
    public static int xorCardinality(final BloomFilter first, final BloomFilter second) {
        BitMapProducer.ArrayBuilder builder = new BitMapProducer.ArrayBuilder( first.getShape());
        first.forEachBitMap( builder );
        CardCounter lc = new CardCounter(builder.getArray(), (x,y)->x^y, (x)->x);
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
        final int numerator = andCardinality( first, second);
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
        return xorCardinality(first,second);
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
        final int orCard = orCardinality(first,second);
        // if the orCard is zero then the hamming distance will also be zero.
        return orCard == 0 ? 0 : hammingDistance(first, second) / (double) orCard;
    }


    /**
     * Do not instantiate.
     */
    private SetOperations() {}
}

