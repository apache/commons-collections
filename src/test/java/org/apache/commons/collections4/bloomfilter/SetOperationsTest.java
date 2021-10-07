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

import java.util.Arrays;

import org.apache.commons.collections4.bloomfilter.hasher.HasherCollection;
import org.apache.commons.collections4.bloomfilter.hasher.SimpleHasher;
import org.junit.jupiter.api.Test;

/**
 * Test {@link SetOperations}.
 */
public class SetOperationsTest {


    protected final SimpleHasher from1 = new SimpleHasher( 1, 1 );
    protected final long from1Value = 0x3FFFEL;
    protected final SimpleHasher from11 = new SimpleHasher( 11, 1 );
    protected final long from11Value = 0xFFFF800L;
    protected final HasherCollection bigHasher = new HasherCollection( from1, from11 );
    protected final long bigHashValue = 0xFFFFFFEL;
    private final Shape shape = new Shape(17, 72);

    /**
     * Tests that the Cosine similarity is correctly calculated.
     */
    @Test
    public final void cosineDistanceTest() {

        BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);


        assertEquals(0.0, SetOperations.cosineDistance(filter1, filter2), 0.0001);
        assertEquals(0.0, SetOperations.cosineDistance(filter2, filter1), 0.0001);

        Shape shape2 = new Shape( 2, 72 );
        filter1 = new SimpleBloomFilter(shape2, from1);
        filter2 = new SimpleBloomFilter(shape2, new SimpleHasher( 2, 1 ));

        assertEquals(0.5, SetOperations.cosineDistance(filter1, filter2), 0.0001);
        assertEquals(0.5, SetOperations.cosineDistance(filter2, filter1), 0.0001);


        filter1 = new SimpleBloomFilter(shape, from1);
        filter2 = new SimpleBloomFilter(shape, from11);

        assertEquals(0.58823529, SetOperations.cosineDistance(filter1, filter2), 0.00000001);
        assertEquals(0.58823529, SetOperations.cosineDistance(filter2, filter1), 0.00000001);
    }

    /**
     * Tests that the Cosine distance is correctly calculated when one or
     * both filters are empty
     */
    @Test
    public final void cosineDistanceTest_NoValues() {
        BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape);
        BloomFilter filter3 = new SimpleBloomFilter(shape);


        assertEquals(1.0, SetOperations.cosineDistance(filter1, filter2), 0.0001);
        assertEquals(1.0, SetOperations.cosineDistance(filter2, filter1), 0.0001);
        assertEquals(1.0, SetOperations.cosineDistance(filter2, filter3), 0.0001);
        assertEquals(1.0, SetOperations.cosineDistance(filter3, filter2), 0.0001);
    }

    /**
     * Tests that the Cosine similarity is correctly calculated.
     */
    @Test
    public final void cosineSimilarityTest() {
        BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);


        assertEquals(1.0, SetOperations.cosineSimilarity(filter1, filter2), 0.0001);
        assertEquals(1.0, SetOperations.cosineSimilarity(filter2, filter1), 0.0001);

        filter2 = new SimpleBloomFilter(shape, from11);

        assertEquals(0.41176470, SetOperations.cosineSimilarity(filter1, filter2), 0.00000001);
        assertEquals(0.41176470, SetOperations.cosineSimilarity(filter2, filter1), 0.00000001);
    }

    /**
     * Tests that the Cosine similarity is correctly calculated when one or
     * both filters are empty
     */
    @Test
    public final void cosineSimilarityTest_NoValues() {
        final BloomFilter filter1 = new SimpleBloomFilter(shape);
        final BloomFilter filter2 = new SimpleBloomFilter(shape);
        // build a filter
        final BloomFilter filter3 = new SimpleBloomFilter(shape, from1);

        assertEquals(0.0, SetOperations.cosineSimilarity(filter1, filter2), 0.0001);
        assertEquals(0.0, SetOperations.cosineSimilarity(filter2, filter1), 0.0001);
        assertEquals(0.0, SetOperations.cosineSimilarity(filter1, filter3), 0.0001);
        assertEquals(0.0, SetOperations.cosineSimilarity(filter3, filter1), 0.0001);
    }



    /**
     * Tests that the Hamming distance is correctly calculated.
     */
    @Test
    public final void hammingDistanceTest() {
        final BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);

        assertEquals(0, SetOperations.hammingDistance(filter1, filter2));
        assertEquals(0, SetOperations.hammingDistance(filter2, filter1));

        filter2 = new SimpleBloomFilter( shape, from11);

        assertEquals(20, SetOperations.hammingDistance(filter1, filter2));
        assertEquals(20, SetOperations.hammingDistance(filter2, filter1));
    }

    /**
     * Tests that the Jaccard distance is correctly calculated.
     */
    @Test
    public final void jaccardDistanceTest() {
        final BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);

        assertEquals(1.0, SetOperations.jaccardDistance(filter1, filter2), 0.0001);
        assertEquals(1.0, SetOperations.jaccardDistance(filter2, filter1), 0.0001);


        filter2 = new SimpleBloomFilter(shape, from11);

        assertEquals(0.26, SetOperations.jaccardDistance(filter1, filter2), 0.001);
        assertEquals(0.26, SetOperations.jaccardDistance(filter2, filter1), 0.001);
    }

    /**
     * Tests that the Jaccard distance is correctly calculated when one or
     * both filters are empty
     */
    @Test
    public final void jaccardDistanceTest_NoValues() {
        final BloomFilter filter1 = new SimpleBloomFilter(shape);
        final BloomFilter filter2 = new SimpleBloomFilter(shape);
        final BloomFilter filter3 = new SimpleBloomFilter(shape, from1);

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
        final BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        BloomFilter filter2 = new SimpleBloomFilter(shape, from1);

        assertEquals(0.0, SetOperations.jaccardSimilarity(filter1, filter2), 0.0001);
        assertEquals(0.0, SetOperations.jaccardSimilarity(filter2, filter1), 0.0001);

        filter2 = new SimpleBloomFilter(shape, from11);

        assertEquals(0.74, SetOperations.jaccardSimilarity(filter1, filter2), 0.001);
        assertEquals(0.74, SetOperations.jaccardSimilarity(filter2, filter1), 0.001);
    }

    /**
     * Tests that the Jaccard similarity is correctly calculated when one or
     * both filters are empty
     */
    @Test
    public final void jaccardSimilarityTest_NoValues() {
        final BloomFilter filter1 = new SimpleBloomFilter(shape);
        final BloomFilter filter2 = new SimpleBloomFilter(shape);
        final BloomFilter filter3 = new SimpleBloomFilter(shape, from1);

        assertEquals(0.0, SetOperations.jaccardSimilarity(filter1, filter2), 0.0001);
        assertEquals(0.0, SetOperations.jaccardSimilarity(filter2, filter1), 0.0001);
        assertEquals(1.0, SetOperations.jaccardSimilarity(filter1, filter3), 0.0001);
        assertEquals(1.0, SetOperations.jaccardSimilarity(filter3, filter1), 0.0001);
    }

    @Test
    public final void orCardinalityTest() {
        Shape shape = new Shape( 3, 128);
        SparseBloomFilter filter1 = new SparseBloomFilter( shape,  Arrays.asList(1, 63, 64));
        SparseBloomFilter filter2 = new SparseBloomFilter( shape,  Arrays.asList(5, 64, 69));
        assertEquals( 5, SetOperations.orCardinality(filter1, filter2) );
        assertEquals( 5, SetOperations.orCardinality(filter2, filter1) );

        filter1 = new SparseBloomFilter( shape,  Arrays.asList(1, 63 ));
        filter2 = new SparseBloomFilter( shape,  Arrays.asList(5, 64, 69));
        assertEquals( 5, SetOperations.orCardinality(filter1, filter2) );
        assertEquals( 5, SetOperations.orCardinality(filter2, filter1) );

        filter1 = new SparseBloomFilter( shape,  Arrays.asList(5, 63 ));
        filter2 = new SparseBloomFilter( shape,  Arrays.asList(5, 64, 69));
        assertEquals( 4, SetOperations.orCardinality(filter1, filter2) );
        assertEquals( 4, SetOperations.orCardinality(filter2, filter1) );
    }

    @Test
    public final void andCardinalityTest() {
        Shape shape = new Shape( 3, 128);
        SparseBloomFilter filter1 = new SparseBloomFilter( shape,  Arrays.asList(1, 63, 64));
        SparseBloomFilter filter2 = new SparseBloomFilter( shape,  Arrays.asList(5, 64, 69));
        assertEquals( 1, SetOperations.andCardinality(filter1, filter2) );
        assertEquals( 1, SetOperations.andCardinality(filter2, filter1) );

        filter1 = new SparseBloomFilter( shape,  Arrays.asList(1, 63 ));
        filter2 = new SparseBloomFilter( shape,  Arrays.asList(5, 64, 69));
        assertEquals( 0, SetOperations.andCardinality(filter1, filter2) );
        assertEquals( 0, SetOperations.andCardinality(filter2, filter1) );

        filter1 = new SparseBloomFilter( shape,  Arrays.asList(5, 63 ));
        filter2 = new SparseBloomFilter( shape,  Arrays.asList(5, 64, 69));
        assertEquals( 1, SetOperations.andCardinality(filter1, filter2) );
        assertEquals( 1, SetOperations.andCardinality(filter2, filter1) );

    }

    @Test
    public final void xorCardinalityTest() {
        Shape shape = new Shape( 3, 128);
        SparseBloomFilter filter1 = new SparseBloomFilter( shape,  Arrays.asList(1, 63, 64));
        SparseBloomFilter filter2 = new SparseBloomFilter( shape,  Arrays.asList(5, 64, 69));
        assertEquals( 4, SetOperations.xorCardinality(filter1, filter2) );
        assertEquals( 4, SetOperations.xorCardinality(filter2, filter1) );

        filter1 = new SparseBloomFilter( shape,  Arrays.asList(1, 63 ));
        filter2 = new SparseBloomFilter( shape,  Arrays.asList(5, 64, 69));
        assertEquals( 5, SetOperations.xorCardinality(filter1, filter2) );
        assertEquals( 5, SetOperations.xorCardinality(filter2, filter1) );

        filter1 = new SparseBloomFilter( shape,  Arrays.asList(5, 63 ));
        filter2 = new SparseBloomFilter( shape,  Arrays.asList(5, 64, 69));
        assertEquals( 3, SetOperations.xorCardinality(filter1, filter2) );
        assertEquals( 3, SetOperations.xorCardinality(filter2, filter1) );

    }
}
