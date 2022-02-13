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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link SparseBloomFilter}.
 */
public class SparseBloomFilterTest extends AbstractBloomFilterTest<SparseBloomFilter> {
    @Override
    protected SparseBloomFilter createEmptyFilter(final Shape shape) {
        return new SparseBloomFilter(shape);
    }

    @Override
    protected SparseBloomFilter createFilter(final Shape shape, final Hasher hasher) {
        return new SparseBloomFilter(shape, hasher);
    }

    @Override
    protected SparseBloomFilter createFilter(final Shape shape, final BitMapProducer producer) {
        return new SparseBloomFilter(shape, producer);
    }

    @Override
    protected SparseBloomFilter createFilter(final Shape shape, final IndexProducer producer) {
        return new SparseBloomFilter(shape, producer);
    }

    private void executeNestedTest(SparseBloomFilterTest nestedTest) {
        nestedTest.testContains();
        nestedTest.testEstimateIntersection();
        nestedTest.testEstimateN();
        nestedTest.testEstimateUnion();
        nestedTest.testIsFull();
        nestedTest.testMerge();
        nestedTest.testMergeInPlace();
    }

    @Test
    public void testConstructors() {

        // copy of Sparse
        SparseBloomFilterTest nestedTest = new SparseBloomFilterTest() {

            @Override
            protected SparseBloomFilter createEmptyFilter(Shape shape) {
                return new SparseBloomFilter(new SparseBloomFilter(shape));
            }

            @Override
            protected SparseBloomFilter createFilter(Shape shape, Hasher hasher) {
                return new SparseBloomFilter(new SparseBloomFilter(shape, hasher));
            }
        };
        executeNestedTest(nestedTest);

        // copy of Simple
        nestedTest = new SparseBloomFilterTest() {

            @Override
            protected SparseBloomFilter createEmptyFilter(Shape shape) {
                return new SparseBloomFilter(new SimpleBloomFilter(shape));
            }

            @Override
            protected SparseBloomFilter createFilter(Shape shape, Hasher hasher) {
                return new SparseBloomFilter(new SimpleBloomFilter(shape, hasher));
            }
        };
        executeNestedTest(nestedTest);
    }

    @Test
    public void testBitMapProducerEdgeCases() {
        int[] values = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 65, 66, 67, 68, 69, 70, 71 };
        BloomFilter bf = createFilter(getTestShape(), IndexProducer.fromIntArray(values));

        // verify exit early at bitmap boundary
        int[] passes = new int[1];
        assertFalse(bf.forEachBitMap(l -> {
            boolean result = passes[0] == 0;
            if (result) {
                passes[0]++;
            }
            return result;
        }));
        assertEquals(1, passes[0]);

        // verify add extra if all values in first bitmap
        values = new int[] { 1, 2, 3, 4 };
        bf = createFilter(getTestShape(), IndexProducer.fromIntArray(values));
        passes[0] = 0;
        assertTrue(bf.forEachBitMap(l -> {
            passes[0]++;
            return true;
        }));
        assertEquals(2, passes[0]);

        // verify exit early if all values in first bitmap and predicate returns false
        // on 2nd block
        values = new int[] { 1, 2, 3, 4 };
        bf = createFilter(getTestShape(), IndexProducer.fromIntArray(values));
        passes[0] = 0;
        assertFalse(bf.forEachBitMap(l -> {
            boolean result = passes[0] == 0;
            if (result) {
                passes[0]++;
            }
            return result;
        }));
        assertEquals(1, passes[0]);

    }

}
