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

import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntBiFunction;

import org.junit.jupiter.api.Test;

/**
 * Test {@link SetOperations}.
 */
public class SetOperationsTest {

    protected final Hasher from1 = new IncrementingHasher(1, 1);
    protected final long from1Value = 0x3FFFEL;
    protected final Hasher from11 = new IncrementingHasher(11, 1);
    protected final long from11Value = 0xFFFF800L;
    protected final HasherCollection bigHasher = new HasherCollection(from1, from11);
    protected final long bigHashValue = 0xFFFFFFEL;
    private final Shape shape = Shape.fromKM(17, 72);


    private static void assertSymmetricOperation(int expected, ToIntBiFunction<BloomFilter, BloomFilter> operation,
            BloomFilter filter1, BloomFilter filter2) {
        assertEquals(expected, operation.applyAsInt(filter1, filter2), "op(filter1, filter2)");
        assertEquals(expected, operation.applyAsInt(filter2, filter1), "op(filter2, filter1)");
    }

    private static void assertSymmetricOperation(double expected, ToDoubleBiFunction<BloomFilter, BloomFilter> operation,
            BloomFilter filter1, BloomFilter filter2) {
        assertEquals(expected, operation.applyAsDouble(filter1, filter2), "op(filter1, filter2)");
        assertEquals(expected, operation.applyAsDouble(filter2, filter1), "op(filter2, filter1)");
    }

    private BloomFilter createFilter(Shape shape, Hasher hasher) {
        BloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(hasher);
        return bf;
    }

    private BloomFilter createFilter(Shape shape, IndexProducer producer) {
        BloomFilter bf = new SparseBloomFilter(shape);
        bf.merge(producer);
        return bf;
    }

    /**
     * Tests that the Cosine similarity is correctly calculated.
     */
    @Test
    public final void testCosineDistance() {

        BloomFilter filter1 = createFilter(shape, from1);
        BloomFilter filter2 = createFilter(shape, from1);

        // identical filters should have no distance.
        double expected =  0;
        assertSymmetricOperation(expected, SetOperations::cosineDistance, filter1, filter2);

        Shape shape2 = Shape.fromKM(2, 72);
        filter1 = createFilter(shape2, from1);
        filter2 = createFilter(shape2, new IncrementingHasher(2, 1));

        int dotProduct = /* [1,2] & [2,3] = [2] = */ 1;
        int cardinalityA = 2;
        int cardinalityB = 2;
        expected = 1 - (dotProduct / Math.sqrt(cardinalityA * cardinalityB));
        assertSymmetricOperation(expected, SetOperations::cosineDistance, filter1, filter2);

        filter1 = createFilter(shape, from1);
        filter2 = createFilter(shape, from11);
        dotProduct = /* [1..17] & [11..27] = [] = */ 7;
        cardinalityA = 17;
        cardinalityB = 17;
        expected = 1 - (dotProduct / Math.sqrt(cardinalityA * cardinalityB));
        assertSymmetricOperation(expected, SetOperations::cosineDistance, filter1, filter2);

        // test with no values
        filter1 = createFilter(shape, from1);
        filter2 = new SimpleBloomFilter(shape);

        dotProduct = /* [1,2] & [] = [] = */ 0;
        cardinalityA = 2;
        cardinalityB = 0;
        expected = /* 1 - (dotProduct/Math.sqrt(cardinalityA * cardinalityB)) = */ 1.0;
        assertSymmetricOperation(expected, SetOperations::cosineDistance, filter1, filter2);

        dotProduct = /* [] & [] = [] = */ 0;
        cardinalityA = 0;
        cardinalityB = 0;
        expected = /* 1 - (dotProduct/Math.sqrt(cardinalityA * cardinalityB)) = */ 1.0;
        assertSymmetricOperation(expected, SetOperations::cosineDistance, filter1, filter2);
    }

    /**
     * Tests that the Cosine similarity is correctly calculated.
     */
    @Test
    public final void testCosineSimilarity() {
        BloomFilter filter1 = createFilter(shape, from1);
        BloomFilter filter2 = createFilter(shape, from1);

        int dotProduct = /* [1..17] & [1..17] = [1..17] = */ 17;
        int cardinalityA = 17;
        int cardinalityB = 17;
        double expected = /* dotProduct/Sqrt(cardinalityA * cardinalityB) = */ 1.0;
        assertSymmetricOperation(expected, SetOperations::cosineSimilarity, filter1, filter2);

        dotProduct = /* [1..17] & [11..27] = [11..17] = */ 7;
        cardinalityA = 17;
        cardinalityB = 17;
        expected = dotProduct / Math.sqrt(cardinalityA * cardinalityB);
        filter2 = createFilter(shape, from11);
        assertSymmetricOperation(expected, SetOperations::cosineSimilarity, filter1, filter2);

        // test no values
        filter1 = new SimpleBloomFilter(shape);
        filter2 = new SimpleBloomFilter(shape);
        // build a filter
        BloomFilter filter3 = createFilter(shape, from1);
        assertSymmetricOperation(0.0, SetOperations::cosineSimilarity, filter1, filter2);
        assertSymmetricOperation(0.0, SetOperations::cosineSimilarity, filter1, filter3);
    }

    /**
     * Tests that the Hamming distance is correctly calculated.
     */
    @Test
    public final void testHammingDistance() {
        final BloomFilter filter1 = createFilter(shape, from1);
        BloomFilter filter2 = createFilter(shape, from1);

        int hammingDistance = /* [1..17] ^ [1..17] = [] = */ 0;
        assertSymmetricOperation(hammingDistance, SetOperations::hammingDistance, filter1, filter2);

        filter2 = createFilter(shape, from11);
        hammingDistance = /* [1..17] ^ [11..27] = [1..10][17-27] = */ 20;
        assertSymmetricOperation(hammingDistance, SetOperations::hammingDistance, filter1, filter2);
    }

    /**
     * Tests that the Jaccard distance is correctly calculated.
     */
    @Test
    public final void testJaccardDistance() {
        BloomFilter filter1 = createFilter(shape, from1);
        BloomFilter filter2 = createFilter(shape, from1);

        // 1 - jaccardSimilarity -- see jaccardSimilarityTest
        assertSymmetricOperation(0.0, SetOperations::jaccardDistance, filter1, filter2);

        filter2 = createFilter(shape, from11);
        double intersection = /* [1..17] & [11..27] = [11..17] = */ 7.0;
        int union = /* [1..17] | [11..27] = [1..27] = */ 27;
        double expected = 1 - (intersection / union);
        assertSymmetricOperation(expected, SetOperations::jaccardDistance, filter1, filter2);

        // test no values
        filter1 = new SimpleBloomFilter(shape);
        filter2 = new SimpleBloomFilter(shape);
        BloomFilter filter3 = createFilter(shape, from1);

        // 1 - jaccardSimilarity -- see jaccardSimilarityTest
        assertSymmetricOperation(1.0, SetOperations::jaccardDistance, filter1, filter2);
        assertSymmetricOperation(1.0, SetOperations::jaccardDistance, filter1, filter3);
    }

    /**
     * Tests that the Jaccard similarity is correctly calculated.
     */
    @Test
    public final void testJaccardSimilarity() {
        BloomFilter filter1 = createFilter(shape, from1);
        BloomFilter filter2 = createFilter(shape, from1);

        double intersection = /* [1..17] & [1..17] = [1..17] = */ 17.0;
        int union = /* [1..17] | [1..17] = [1..17] = */ 17;
        double expected = intersection / union;
        assertSymmetricOperation(expected, SetOperations::jaccardSimilarity, filter1, filter2);

        filter2 = createFilter(shape, from11);
        intersection = /* [1..17] & [11..27] = [11..17] = */ 7.0;
        union = /* [1..17] | [11..27] = [1..27] = */ 27;
        expected = intersection / union;
        assertSymmetricOperation(expected, SetOperations::jaccardSimilarity, filter1, filter2);

        // test no values
        filter1 = new SimpleBloomFilter(shape);
        filter2 = new SimpleBloomFilter(shape);
        assertSymmetricOperation(0.0, SetOperations::jaccardSimilarity, filter1, filter2);

        intersection = /* [] & [1..17] = [] = */ 0.0;
        union = /* [] | [1..17] = [] = */ 17;
        expected = intersection / union;
        assertSymmetricOperation(expected, SetOperations::jaccardSimilarity, filter1, filter2);
    }

    @Test
    public final void testOrCardinality() {
        Shape shape = Shape.fromKM(3, 128);
        BloomFilter filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63, 64 }));
        BloomFilter filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 64, 69 }));
        assertSymmetricOperation(5, SetOperations::orCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63 }));
        filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 64, 69 }));
        assertSymmetricOperation(5, SetOperations::orCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 63 }));
        filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 64, 69 }));
        assertSymmetricOperation(4, SetOperations::orCardinality, filter1, filter2);
    }

    @Test
    public final void testOrCardinalityWithDifferentLengthFilters() {
        Shape shape = Shape.fromKM(3, 128);
        Shape shape2 = Shape.fromKM(3, 192);
        BloomFilter filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63, 64 }));
        BloomFilter filter2 = createFilter(shape2, IndexProducer.fromIndexArray(new int[] { 5, 64, 169 }));
        assertSymmetricOperation(5, SetOperations::orCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63 }));
        filter2 = createFilter(shape2, IndexProducer.fromIndexArray(new int[] { 5, 64, 169 }));
        assertSymmetricOperation(5, SetOperations::orCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 63 }));
        filter2 = createFilter(shape2, IndexProducer.fromIndexArray(new int[] { 5, 64, 169 }));
        assertSymmetricOperation(4, SetOperations::orCardinality, filter1, filter2);
    }

    @Test
    public final void testAndCardinality() {
        Shape shape = Shape.fromKM(3, 128);
        BloomFilter filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63, 64 }));
        BloomFilter filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 64, 69 }));
        assertSymmetricOperation(1, SetOperations::andCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63 }));
        filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 64, 69 }));
        assertSymmetricOperation(0, SetOperations::andCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 63 }));
        filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 64, 69 }));
        assertSymmetricOperation(1, SetOperations::andCardinality, filter1, filter2);
    }

    @Test
    public final void testAndCardinalityWithDifferentLengthFilters() {
        Shape shape = Shape.fromKM(3, 128);
        Shape shape2 = Shape.fromKM(3, 192);
        BloomFilter filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63, 64 }));
        BloomFilter filter2 = createFilter(shape2, IndexProducer.fromIndexArray(new int[] { 5, 64, 169 }));
        assertSymmetricOperation(1, SetOperations::andCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63 }));
        filter2 = createFilter(shape2, IndexProducer.fromIndexArray(new int[] { 5, 64, 169 }));
        assertSymmetricOperation(0, SetOperations::andCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 63 }));
        filter2 = createFilter(shape2, IndexProducer.fromIndexArray(new int[] { 5, 64, 169 }));
        assertSymmetricOperation(1, SetOperations::andCardinality, filter1, filter2);
    }

    @Test
    public final void testXorCardinality() {
        Shape shape = Shape.fromKM(3, 128);
        BloomFilter filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63, 64 }));
        BloomFilter filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 64, 69 }));
        assertSymmetricOperation(4, SetOperations::xorCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63 }));
        filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 64, 69 }));
        assertSymmetricOperation(5, SetOperations::xorCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 63 }));
        filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 64, 69 }));
        assertSymmetricOperation(3, SetOperations::xorCardinality, filter1, filter2);

        Shape bigShape = Shape.fromKM(3, 192);
        filter1 = createFilter(bigShape, IndexProducer.fromIndexArray(new int[] { 1, 63, 185}));
        filter2 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 63, 69 }));
        assertSymmetricOperation(4, SetOperations::xorCardinality, filter1, filter2);
    }

    @Test
    public final void testXorCardinalityWithDifferentLengthFilters() {
        Shape shape = Shape.fromKM(3, 128);
        Shape shape2 = Shape.fromKM(3, 192);

        BloomFilter filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63, 64 }));
        BloomFilter filter2 = createFilter(shape2, IndexProducer.fromIndexArray(new int[] { 5, 64, 169 }));
        assertSymmetricOperation(4, SetOperations::xorCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 1, 63 }));
        filter2 = createFilter(shape2, IndexProducer.fromIndexArray(new int[] { 5, 64, 169 }));
        assertSymmetricOperation(5, SetOperations::xorCardinality, filter1, filter2);

        filter1 = createFilter(shape, IndexProducer.fromIndexArray(new int[] { 5, 63 }));
        filter2 = createFilter(shape2, IndexProducer.fromIndexArray(new int[] { 5, 64, 169 }));
        assertSymmetricOperation(3, SetOperations::xorCardinality, filter1, filter2);
    }


    @Test
    public final void testCommutativityOnMismatchedSizes() {
        BitMapProducer p1 = BitMapProducer.fromBitMapArray(new long[] { 0x3L, 0x5L });
        BitMapProducer p2 = BitMapProducer.fromBitMapArray(new long[] { 0x1L });

        assertEquals(SetOperations.orCardinality(p1, p2), SetOperations.orCardinality(p2, p1));
        assertEquals(SetOperations.xorCardinality(p1, p2), SetOperations.xorCardinality(p2, p1));
        assertEquals(SetOperations.andCardinality(p1, p2), SetOperations.andCardinality(p2, p1));
        assertEquals(SetOperations.hammingDistance(p1, p2), SetOperations.hammingDistance(p2, p1));
        assertEquals(SetOperations.cosineDistance(p1, p2), SetOperations.cosineDistance(p2, p1));
        assertEquals(SetOperations.cosineSimilarity(p1, p2), SetOperations.cosineSimilarity(p2, p1));
        assertEquals(SetOperations.jaccardDistance(p1, p2), SetOperations.jaccardDistance(p2, p1));
        assertEquals(SetOperations.jaccardSimilarity(p1, p2), SetOperations.jaccardSimilarity(p2, p1));
    }
}
