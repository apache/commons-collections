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

import java.util.function.LongPredicate;

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

}
