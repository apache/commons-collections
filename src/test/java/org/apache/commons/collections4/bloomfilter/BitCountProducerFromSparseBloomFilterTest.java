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
        final Hasher hasher = new IncrementingHasher(4, 7);
        final BloomFilter bf = new SparseBloomFilter(shape);
        bf.merge(hasher);
        return BitCountProducer.from(bf);
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return BitCountProducer.from(new SparseBloomFilter(shape));
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        // A sparse BloomFilter will be distinct but it may not be ordered.
        // Currently the ordered behavior is asserted as the implementation uses
        // an ordered TreeSet. This may change in the future.
        return DISTINCT | ORDERED;
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[] {2, 4, 9, 11, 16, 18, 23, 25, 30, 32, 37, 39, 44, 46, 53, 60, 67};
    }
}
