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

public class BitCountProducerFromArrayCountingBloomFilterTest extends AbstractBitCountProducerTest {

    protected Shape shape = Shape.fromKM(17, 72);

    @Override
    protected BitCountProducer createProducer() {
        final ArrayCountingBloomFilter filter = new ArrayCountingBloomFilter(shape);
        filter.merge(new IncrementingHasher(0, 1));
        filter.merge(new IncrementingHasher(5, 1));
        return filter;
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return new ArrayCountingBloomFilter(shape);
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        // CountingBloomFilter based on an array will be distinct and ordered
        return DISTINCT | ORDERED;
    }

    @Override
    protected int[][] getExpectedBitCount() {
        return new int[][] {{0, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 2}, {6, 2}, {7, 2},
            {8, 2}, {9, 2}, {10, 2}, {11, 2}, {12, 2}, {13, 2}, {14, 2}, {15, 2}, {16, 2},
            {17, 1}, {18, 1}, {19, 1}, {20, 1}, {21, 1}};
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
    }
}
