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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link SimpleBloomFilter}.
 */
public class SimpleBloomFilterTest extends AbstractBloomFilterTest<SimpleBloomFilter> {
    @Override
    protected SimpleBloomFilter createEmptyFilter(final Shape shape) {
        return new SimpleBloomFilter(shape);
    }

    @Override
    protected SimpleBloomFilter createFilter(final Shape shape, final Hasher hasher) {
        return new SimpleBloomFilter(shape, hasher);
    }

    @Override
    protected SimpleBloomFilter createFilter(final Shape shape, final BitMapProducer producer) {
        return new SimpleBloomFilter(shape, producer);
    }

    @Override
    protected SimpleBloomFilter createFilter(final Shape shape, final IndexProducer producer) {
        return new SimpleBloomFilter(shape, producer);
    }

    private void executeNestedTest(SimpleBloomFilterTest nestedTest) {
        nestedTest.testAsBitMapArray();
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
        SimpleBloomFilterTest nestedTest = new SimpleBloomFilterTest() {

            @Override
            protected SimpleBloomFilter createEmptyFilter(Shape shape) {
                return new SimpleBloomFilter(new SparseBloomFilter(shape));
            }

            @Override
            protected SimpleBloomFilter createFilter(Shape shape, Hasher hasher) {
                return new SimpleBloomFilter(new SparseBloomFilter(shape, hasher));
            }
        };
        executeNestedTest(nestedTest);

        // copy of Simple
        nestedTest = new SimpleBloomFilterTest() {

            @Override
            protected SimpleBloomFilter createEmptyFilter(Shape shape) {
                return new SimpleBloomFilter(new SimpleBloomFilter(shape));
            }

            @Override
            protected SimpleBloomFilter createFilter(Shape shape, Hasher hasher) {
                return new SimpleBloomFilter(new SimpleBloomFilter(shape, hasher));
            }
        };
        executeNestedTest(nestedTest);
    }

}
