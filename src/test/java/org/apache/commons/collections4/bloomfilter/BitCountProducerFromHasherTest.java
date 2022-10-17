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

public class BitCountProducerFromHasherTest extends AbstractBitCountProducerTest {

    @Override
    protected BitCountProducer createProducer() {
        return BitCountProducer.from(new IncrementingHasher(3, 2).indices(Shape.fromKM(17, 72)));
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return BitCountProducer.from(NullHasher.INSTANCE.indices(Shape.fromKM(17, 72)));
    }

    @Override
    protected int getBehaviour() {
        // Hasher allows duplicates and may be unordered
        return 0;
    }

    @Override
    protected int[][] getExpectedBitCount() {
        return new int[][]{
            {3, 1}, {5, 1}, {7, 1}, {9, 1}, {11, 1}, {13, 1}, {15, 1}, {17, 1}, {19, 1},
                {21, 1}, {23, 1}, {25, 1}, {27, 1}, {29, 1}, {31, 1}, {33, 1}, {35, 1}};
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[]{3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35};
    }
}
