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
        // hasher has collisions and wraps
        return BitCountProducer.from(new IncrementingHasher(4, 8).indices(Shape.fromKM(17, 72)));
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return BitCountProducer.from(NullHasher.INSTANCE.indices(Shape.fromKM(17, 72)));
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        // Hasher allows duplicates and may be unordered
        return 0;
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[] {4, 12, 20, 28, 36, 44, 52, 60, 68, 4, 12, 20, 28, 36, 44, 52, 60};
    }

    @Override
    protected int[][] getExpectedBitCount() {
        return new int[][] {{4, 2}, {12, 2}, {20, 2}, {28, 2}, {36, 2}, {44, 2}, {52, 2}, {60, 2}, {68, 1}};
    }
}
