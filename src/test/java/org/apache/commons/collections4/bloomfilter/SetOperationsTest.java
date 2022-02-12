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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link SetOperations}.
 */
public class SetOperationsTest {

    protected final SimpleHasher from1 = new SimpleHasher(1, 1);
    protected final long from1Value = 0x3FFFEL;
    protected final SimpleHasher from11 = new SimpleHasher(11, 1);
    protected final long from11Value = 0xFFFF800L;
    protected final HasherCollection bigHasher = new HasherCollection(from1, from11);
    protected final long bigHashValue = 0xFFFFFFEL;
    private final Shape shape = Shape.fromKM(17, 72);

    /**
     * Tests that the Cosine similarity is correctly calculated.
     */
    @Test
    public final void testCosineDistance() {

        BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);

        int dotProduct = /* [1..17] & [1..17] = [1..17] = */ 17;
        int cardinalityA = 17;
        int cardinalityB = 17;
        double expected = /* 1 - (dotProduct/Sqrt( cardinalityA * cardinalityB )) = */ 0;
        assertEquals(expected, SetOperations.cosineDistance(filter1, filter2));
        assertEquals(expected, SetOperations.cosineDistance(filter2, filter1));

        Shape shape2 = Shape.fromKM(2, 72);
        filter1 = new SimpleBloomFilter(shape2, from1);
        filter2 = new SimpleBloomFilter(shape2, new SimpleHasher(2, 1));

        dotProduct = /* [1,2] & [2,3] = [2] = */ 1;
        cardinalityA = 2;
        cardinalityB = 2;
        expected = 1 - (dotProduct / Math.sqrt(cardinalityA * cardinalityB));
        assertEquals(expected, SetOperations.cosineDistance(filter1, filter2));
        assertEquals(expected, SetOperations.cosineDistance(filter2, filter1));

        filter1 = new SimpleBloomFilter(shape, from1);
        filter2 = new SimpleBloomFilter(shape, from11);
        dotProduct = /* [1..17] & [11..27] = [] = */ 7;
        cardinalityA = 17;
        cardinalityB = 17;
        expected = 1 - (dotProduct / Math.sqrt(cardinalityA * cardinalityB));
        assertEquals(expected, SetOperations.cosineDistance(filter1, filter2));
        assertEquals(expected, SetOperations.cosineDistance(filter2, filter1));

        // test with no values
        filter1 = new SimpleBloomFilter(shape, from1);
        filter2 = new SimpleBloomFilter(shape);
        BloomFilter filter3 = new SimpleBloomFilter(shape);

        dotProduct = /* [1,2] & [] = [] = */ 0;
        cardinalityA = 2;
        cardinalityB = 0;
        expected = /* 1 - (dotProduct/Math.sqrt( cardinalityA * cardinalityB )) = */ 1.0;
        assertEquals(expected, SetOperations.cosineDistance(filter1, filter2));
        assertEquals(expected, SetOperations.cosineDistance(filter2, filter1));

        dotProduct = /* [] & [] = [] = */ 0;
        cardinalityA = 0;
        cardinalityB = 0;
        expected = /* 1 - (dotProduct/Math.sqrt( cardinalityA * cardinalityB )) = */ 1.0;
        assertEquals(1.0, SetOperations.cosineDistance(filter2, filter3));
        assertEquals(1.0, SetOperations.cosineDistance(filter3, filter2));
    }

    /**
     * Tests that the Cosine similarity is correctly calculated.
     */
    @Test
    public final void testCosineSimilarity() {
        BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);

        int dotProduct = /* [1..17] & [1..17] = [1..17] = */ 17;
        int cardinalityA = 17;
        int cardinalityB = 17;
        double expected = /* dotProduct/Sqrt( cardinalityA * cardinalityB ) = */ 1.0;
        assertEquals(expected, SetOperations.cosineSimilarity(filter1, filter2));
        assertEquals(expected, SetOperations.cosineSimilarity(filter2, filter1));

        dotProduct = /* [1..17] & [11..27] = [11..17] = */ 7;
        cardinalityA = 17;
        cardinalityB = 17;
        expected = dotProduct / Math.sqrt(cardinalityA * cardinalityB);
        filter2 = new SimpleBloomFilter(shape, from11);
        assertEquals(expected, SetOperations.cosineSimilarity(filter1, filter2));
        assertEquals(expected, SetOperations.cosineSimilarity(filter2, filter1));

        // test no values
        filter1 = new SimpleBloomFilter(shape);
        filter2 = new SimpleBloomFilter(shape);
        // build a filter
        BloomFilter filter3 = new SimpleBloomFilter(shape, from1);

        assertEquals(0.0, SetOperations.cosineSimilarity(filter1, filter2));
        assertEquals(0.0, SetOperations.cosineSimilarity(filter2, filter1));
        assertEquals(0.0, SetOperations.cosineSimilarity(filter1, filter3));
        assertEquals(0.0, SetOperations.cosineSimilarity(filter3, filter1));
    }

    /**
     * Tests that the Hamming distance is correctly calculated.
     */
    @Test
    public final void testHammingDistance() {
        final BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);

        int hammingDistance = /* [1..17] ^ [1..17] = [] = */ 0;
        assertEquals(hammingDistance, SetOperations.hammingDistance(filter1, filter2));
        assertEquals(hammingDistance, SetOperations.hammingDistance(filter2, filter1));

        filter2 = new SimpleBloomFilter(shape, from11);
        hammingDistance = /* [1..17] ^ [11..27] = [1..10][17-27] = */ 20;
        assertEquals(hammingDistance, SetOperations.hammingDistance(filter1, filter2));
        assertEquals(hammingDistance, SetOperations.hammingDistance(filter2, filter1));
    }

    /**
     * Tests that the Jaccard distance is correctly calculated.
     */
    @Test
    public final void testJaccardDistance() {
        BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);

        // 1 - jaccardSimilarity -- see jaccardSimilarityTest

        assertEquals(0.0, SetOperations.jaccardDistance(filter1, filter2));
        assertEquals(0.0, SetOperations.jaccardDistance(filter2, filter1));

        filter2 = new SimpleBloomFilter(shape, from11);
        double intersection = /* [1..17] & [11..27] = [11..17] = */ 7.0;
        int union = /* [1..17] | [11..27] = [1..27] = */ 27;
        assertEquals(1 - (intersection / union), SetOperations.jaccardDistance(filter1, filter2));
        assertEquals(1 - (intersection / union), SetOperations.jaccardDistance(filter2, filter1));

        // test no values
        filter1 = new SimpleBloomFilter(shape);
        filter2 = new SimpleBloomFilter(shape);
        BloomFilter filter3 = new SimpleBloomFilter(shape, from1);

        // 1 - jaccardSimilarity -- see jaccardSimilarityTest
        assertEquals(1.0, SetOperations.jaccardDistance(filter1, filter2));
        assertEquals(1.0, SetOperations.jaccardDistance(filter2, filter1));
        assertEquals(1.0, SetOperations.jaccardDistance(filter1, filter3));
        assertEquals(1.0, SetOperations.jaccardDistance(filter3, filter1));
    }

    /**
     * Tests that the Jaccard similarity is correctly calculated.
     */
    @Test
    public final void testJaccardSimilarity() {
        BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);

        double intersection = /* [1..17] & [1..17] = [1..17] = */ 17.0;
        int union = /* [1..17] | [1..17] = [1..17] = */ 17;

        assertEquals(intersection / union, SetOperations.jaccardSimilarity(filter1, filter2));
        assertEquals(intersection / union, SetOperations.jaccardSimilarity(filter2, filter1));

        filter2 = new SimpleBloomFilter(shape, from11);
        intersection = /* [1..17] & [11..27] = [11..17] = */ 7.0;
        union = /* [1..17] | [11..27] = [1..27] = */ 27;
        assertEquals(intersection / union, SetOperations.jaccardSimilarity(filter1, filter2));
        assertEquals(intersection / union, SetOperations.jaccardSimilarity(filter2, filter1));

        // test no values
        filter1 = new SimpleBloomFilter(shape);
        filter2 = new SimpleBloomFilter(shape);
        BloomFilter filter3 = new SimpleBloomFilter(shape, from1);

        assertEquals(0.0, SetOperations.jaccardSimilarity(filter1, filter2));
        assertEquals(0.0, SetOperations.jaccardSimilarity(filter2, filter1));

        intersection = /* [] & [1..17] = [] = */ 0.0;
        union = /* [] | [1..17] = [] = */ 17;
        assertEquals(intersection / union, SetOperations.jaccardSimilarity(filter1, filter3));
        assertEquals(intersection / union, SetOperations.jaccardSimilarity(filter3, filter1));
    }

    @Test
    public final void testOrCardinality() {
        Shape shape = Shape.fromKM(3, 128);
        SparseBloomFilter filter1 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 1, 63, 64 }));
        SparseBloomFilter filter2 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 64, 69 }));
        assertEquals(5, SetOperations.orCardinality(shape, filter1, filter2));
        assertEquals(5, SetOperations.orCardinality(shape, filter2, filter1));

        filter1 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 1, 63 }));
        filter2 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 64, 69 }));
        assertEquals(5, SetOperations.orCardinality(shape, filter1, filter2));
        assertEquals(5, SetOperations.orCardinality(shape, filter2, filter1));

        filter1 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 63 }));
        filter2 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 64, 69 }));
        assertEquals(4, SetOperations.orCardinality(shape, filter1, filter2));
        assertEquals(4, SetOperations.orCardinality(shape, filter2, filter1));
    }

    @Test
    public final void testAndCardinality() {
        Shape shape = Shape.fromKM(3, 128);
        SparseBloomFilter filter1 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 1, 63, 64 }));
        SparseBloomFilter filter2 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 64, 69 }));
        assertEquals(1, SetOperations.andCardinality(shape, filter1, filter2));
        assertEquals(1, SetOperations.andCardinality(shape, filter2, filter1));

        filter1 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 1, 63 }));
        filter2 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 64, 69 }));
        assertEquals(0, SetOperations.andCardinality(shape, filter1, filter2));
        assertEquals(0, SetOperations.andCardinality(shape, filter2, filter1));

        filter1 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 63 }));
        filter2 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 64, 69 }));
        assertEquals(1, SetOperations.andCardinality(shape, filter1, filter2));
        assertEquals(1, SetOperations.andCardinality(shape, filter2, filter1));
    }

    @Test
    public final void testXorCardinality() {
        Shape shape = Shape.fromKM(3, 128);
        SparseBloomFilter filter1 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 1, 63, 64 }));
        SparseBloomFilter filter2 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 64, 69 }));
        assertEquals(4, SetOperations.xorCardinality(shape, filter1, filter2));
        assertEquals(4, SetOperations.xorCardinality(shape, filter2, filter1));

        filter1 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 1, 63 }));
        filter2 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 64, 69 }));
        assertEquals(5, SetOperations.xorCardinality(shape, filter1, filter2));
        assertEquals(5, SetOperations.xorCardinality(shape, filter2, filter1));

        filter1 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 63 }));
        filter2 = new SparseBloomFilter(shape, IndexProducer.fromIntArray(new int[] { 5, 64, 69 }));
        assertEquals(3, SetOperations.xorCardinality(shape, filter1, filter2));
        assertEquals(3, SetOperations.xorCardinality(shape, filter2, filter1));
    }
}
