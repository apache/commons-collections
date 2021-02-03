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
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Arrays;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;
import org.junit.jupiter.api.Test;

/**
 * Test {@link SetOperations}.
 */
public class SetOperationsTest {

    private final HashFunctionIdentity testFunction = new HashFunctionIdentity() {

        @Override
        public String getName() {
            return "Test Function";
        }

        @Override
        public ProcessType getProcessType() {
            return ProcessType.CYCLIC;
        }

        @Override
        public String getProvider() {
            return "Apache Commons Collection Tests";
        }

        @Override
        public long getSignature() {
            return 0;
        }

        @Override
        public Signedness getSignedness() {
            return Signedness.SIGNED;
        }
    };

    private final Shape shape = new Shape(testFunction, 3, 72, 17);

    @Test
    public void testDifferentShapesThrows() {
        final List<Integer> lst = Arrays.asList(1, 2);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter1 = new HasherBloomFilter(hasher, shape);

        final Shape shape2 = new Shape(testFunction, 3, 72, 18);
        final List<Integer> lst2 = Arrays.asList(2, 3);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape2);
        final BloomFilter filter2 = new HasherBloomFilter(hasher2, shape2);

        try {
            SetOperations.cosineDistance(filter1, filter2);
            fail("Expected an IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // Ignore
        }
    }

    /**
     * Tests that the Cosine similarity is correctly calculated.
     */
    @Test
    public final void cosineDistanceTest() {
        List<Integer> lst = Arrays.asList(1, 2);
        Hasher hasher = new StaticHasher(lst.iterator(), shape);
        BloomFilter filter1 = new HasherBloomFilter(hasher, shape);

        List<Integer> lst2 = Arrays.asList(2, 3);
        Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        BloomFilter filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(0.5, SetOperations.cosineDistance(filter1, filter2), 0.0001);
        assertEquals(0.5, SetOperations.cosineDistance(filter2, filter1), 0.0001);

        lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        hasher = new StaticHasher(lst.iterator(), shape);
        filter1 = new HasherBloomFilter(hasher, shape);

        lst2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        hasher2 = new StaticHasher(lst2.iterator(), shape);
        filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(0.0, SetOperations.cosineDistance(filter1, filter2), 0.0001);
        assertEquals(0.0, SetOperations.cosineDistance(filter2, filter1), 0.0001);

        lst2 = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);
        hasher2 = new StaticHasher(lst2.iterator(), shape);
        filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(0.514928749927334, SetOperations.cosineDistance(filter1, filter2), 0.000000000000001);
        assertEquals(0.514928749927334, SetOperations.cosineDistance(filter2, filter1), 0.000000000000001);
    }

    /**
     * Tests that the Cosine distance is correctly calculated when one or
     * both filters are empty
     */
    @Test
    public final void cosineDistanceTest_NoValues() {
        final BloomFilter filter1 = new HasherBloomFilter(shape);
        final BloomFilter filter2 = new HasherBloomFilter(shape);
        // build a filter
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter3 = new HasherBloomFilter(hasher, shape);

        assertEquals(1.0, SetOperations.cosineDistance(filter1, filter2), 0.0001);
        assertEquals(1.0, SetOperations.cosineDistance(filter2, filter1), 0.0001);
        assertEquals(1.0, SetOperations.cosineDistance(filter1, filter3), 0.0001);
        assertEquals(1.0, SetOperations.cosineDistance(filter3, filter1), 0.0001);
    }

    /**
     * Tests that the Cosine similarity is correctly calculated.
     */
    @Test
    public final void cosineSimilarityTest() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter1 = new HasherBloomFilter(hasher, shape);

        List<Integer> lst2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        BloomFilter filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(1.0, SetOperations.cosineSimilarity(filter1, filter2), 0.0001);
        assertEquals(1.0, SetOperations.cosineSimilarity(filter2, filter1), 0.0001);

        lst2 = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);
        hasher2 = new StaticHasher(lst2.iterator(), shape);
        filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(0.485071250072666, SetOperations.cosineSimilarity(filter1, filter2), 0.000000000000001);
        assertEquals(0.485071250072666, SetOperations.cosineSimilarity(filter2, filter1), 0.000000000000001);
    }

    /**
     * Tests that the Cosine similarity is correctly calculated when one or
     * both filters are empty
     */
    @Test
    public final void cosineSimilarityTest_NoValues() {
        final BloomFilter filter1 = new HasherBloomFilter(shape);
        final BloomFilter filter2 = new HasherBloomFilter(shape);
        // build a filter
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter3 = new HasherBloomFilter(hasher, shape);

        assertEquals(0.0, SetOperations.cosineSimilarity(filter1, filter2), 0.0001);
        assertEquals(0.0, SetOperations.cosineSimilarity(filter2, filter1), 0.0001);
        assertEquals(0.0, SetOperations.cosineSimilarity(filter1, filter3), 0.0001);
        assertEquals(0.0, SetOperations.cosineSimilarity(filter3, filter1), 0.0001);
    }

    /**
     * Tests that the intersection size estimate is correctly calculated.
     */
    @Test
    public final void estimateIntersectionSizeTest() {
        // build a filter
        List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter1 = new HasherBloomFilter(hasher, shape);

        lst = Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40);
        final Hasher hasher2 = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter2 = new HasherBloomFilter(hasher2, shape);

        final long estimate = SetOperations.estimateIntersectionSize(filter1, filter2);
        assertEquals(1, estimate);
    }

    /**
     * Tests that the size estimate is correctly calculated.
     */
    @Test
    public final void estimateSizeTest() {
        // build a filter
        List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        Hasher hasher = new StaticHasher(lst.iterator(), shape);
        BloomFilter filter1 = new HasherBloomFilter(hasher, shape);
        assertEquals(1, SetOperations.estimateSize(filter1));

        // the data provided above do not generate an estimate that is equivalent to the
        // actual.
        lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        hasher = new StaticHasher(lst.iterator(), shape);
        filter1 = new HasherBloomFilter(hasher, shape);
        assertEquals(1, SetOperations.estimateSize(filter1));

        lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
            26, 27, 28, 29, 30, 31, 32, 33);
        final Hasher hasher2 = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(3, SetOperations.estimateSize(filter2));
    }

    /**
     * Tests that the union size estimate is correctly calculated.
     */
    @Test
    public final void estimateUnionSizeTest() {
        // build a filter
        List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter1 = new HasherBloomFilter(hasher, shape);

        lst = Arrays.asList(17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
            40);
        final Hasher hasher2 = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter2 = new HasherBloomFilter(hasher2, shape);

        final long estimate = SetOperations.estimateUnionSize(filter1, filter2);
        assertEquals(3, estimate);
    }

    /**
     * Tests that the Hamming distance is correctly calculated.
     */
    @Test
    public final void hammingDistanceTest() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter1 = new HasherBloomFilter(hasher, shape);

        List<Integer> lst2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        BloomFilter filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(0, SetOperations.hammingDistance(filter1, filter2));
        assertEquals(0, SetOperations.hammingDistance(filter2, filter1));

        lst2 = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);
        hasher2 = new StaticHasher(lst2.iterator(), shape);
        filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(17, SetOperations.hammingDistance(filter1, filter2));
        assertEquals(17, SetOperations.hammingDistance(filter2, filter1));
    }

    /**
     * Tests that the Jaccard distance is correctly calculated.
     */
    @Test
    public final void jaccardDistanceTest() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter1 = new HasherBloomFilter(hasher, shape);

        List<Integer> lst2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        BloomFilter filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(1.0, SetOperations.jaccardDistance(filter1, filter2), 0.0001);
        assertEquals(1.0, SetOperations.jaccardDistance(filter2, filter1), 0.0001);

        lst2 = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);
        hasher2 = new StaticHasher(lst2.iterator(), shape);
        filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(0.32, SetOperations.jaccardDistance(filter1, filter2), 0.001);
        assertEquals(0.32, SetOperations.jaccardDistance(filter2, filter1), 0.001);
    }

    /**
     * Tests that the Jaccard distance is correctly calculated when one or
     * both filters are empty
     */
    @Test
    public final void jaccardDistanceTest_NoValues() {
        final BloomFilter filter1 = new HasherBloomFilter(shape);
        final BloomFilter filter2 = new HasherBloomFilter(shape);
        // build a filter
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter3 = new HasherBloomFilter(hasher, shape);

        assertEquals(1.0, SetOperations.jaccardDistance(filter1, filter2), 0.0001);
        assertEquals(1.0, SetOperations.jaccardDistance(filter2, filter1), 0.0001);
        assertEquals(0.0, SetOperations.jaccardDistance(filter1, filter3), 0.0001);
        assertEquals(0.0, SetOperations.jaccardDistance(filter3, filter1), 0.0001);
    }

    /**
     * Tests that the Jaccard similarity is correctly calculated.
     */
    @Test
    public final void jaccardSimilarityTest() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter1 = new HasherBloomFilter(hasher, shape);

        List<Integer> lst2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        BloomFilter filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(0.0, SetOperations.jaccardSimilarity(filter1, filter2), 0.0001);
        assertEquals(0.0, SetOperations.jaccardSimilarity(filter2, filter1), 0.0001);

        lst2 = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);
        hasher2 = new StaticHasher(lst2.iterator(), shape);
        filter2 = new HasherBloomFilter(hasher2, shape);

        assertEquals(0.68, SetOperations.jaccardSimilarity(filter1, filter2), 0.001);
        assertEquals(0.68, SetOperations.jaccardSimilarity(filter2, filter1), 0.001);
    }

    /**
     * Tests that the Jaccard similarity is correctly calculated when one or
     * both filters are empty
     */
    @Test
    public final void jaccardSimilarityTest_NoValues() {
        final BloomFilter filter1 = new HasherBloomFilter(shape);
        final BloomFilter filter2 = new HasherBloomFilter(shape);
        // build a filter
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter filter3 = new HasherBloomFilter(hasher, shape);

        assertEquals(0.0, SetOperations.jaccardSimilarity(filter1, filter2), 0.0001);
        assertEquals(0.0, SetOperations.jaccardSimilarity(filter2, filter1), 0.0001);
        assertEquals(1.0, SetOperations.jaccardSimilarity(filter1, filter3), 0.0001);
        assertEquals(1.0, SetOperations.jaccardSimilarity(filter3, filter1), 0.0001);
    }
}
