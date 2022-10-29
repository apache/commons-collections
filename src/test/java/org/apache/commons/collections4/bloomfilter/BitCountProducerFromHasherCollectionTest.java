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

public class BitCountProducerFromHasherCollectionTest extends AbstractBitCountProducerTest {

    @Override
    protected BitCountProducer createProducer() {
        // hasher has collisions and wraps
        return BitCountProducer.from(new HasherCollection(
                new IncrementingHasher(0, 1),
                new IncrementingHasher(0, 2)).indices(Shape.fromKM(17, 72)));
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return BitCountProducer.from(NullHasher.INSTANCE.indices(Shape.fromKM(17, 72)));
    }

    @Override
    protected int getBehaviour() {
        return 0;
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32};
    }

    @Override
    protected int[][] getExpectedBitCount() {
        return new int[][]{{0, 2}, {1, 1}, {2, 2}, {3, 1}, {4, 2}, {5, 1}, {6, 2}, {7, 1}, {8, 2},
            {9, 1}, {10, 2}, {11, 1}, {12, 2}, {13, 1}, {14, 2}, {15, 1}, {16, 2}, {18, 1}, {20, 1},
            {22, 1}, {24, 1}, {26, 1}, {28, 1}, {30, 1}, {32, 1}};
    }
}
