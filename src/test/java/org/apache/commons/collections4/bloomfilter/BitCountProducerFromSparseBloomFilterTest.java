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

public class BitCountProducerFromSparseBloomFilterTest extends AbstractBitCountProducerTest {

    protected Shape shape = Shape.fromKM(17, 72);

    @Override
    protected BitCountProducer createProducer() {
        Hasher hasher = new IncrementingHasher(0, 1);
        BloomFilter bf = new SparseBloomFilter(shape);
        bf.merge(hasher);
        return BitCountProducer.from(bf);
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return BitCountProducer.from(new SparseBloomFilter(shape));
    }

    @Override
    protected int getBehaviour() {
        // A sparse BloomFilter will be distinct but it may not be ordered.
        // Currently the ordered behavior is asserted as the implementation uses
        // an ordered TreeSet. This may change in the future.
        return FOR_EACH_DISTINCT | FOR_EACH_ORDERED | AS_ARRAY_DISTINCT | AS_ARRAY_ORDERED;
    }

    @Override
    protected int[][] getExpectedBitCount() {
        return new int[][]{{0, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1}, {6, 1}, {7, 1}, {8, 1},
            {9, 1}, {10, 1}, {11, 1}, {12, 1}, {13, 1}, {14, 1}, {15, 1}, {16, 1}};
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    }
}
