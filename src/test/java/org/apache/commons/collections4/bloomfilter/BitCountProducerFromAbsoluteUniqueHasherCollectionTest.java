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


public class BitCountProducerFromAbsoluteUniqueHasherCollectionTest extends AbstractBitCountProducerTest {

    @Override
    protected BitCountProducer createProducer() {
        // hasher has collisions and wraps
        return BitCountProducer.from(new HasherCollection(
                new IncrementingHasher(1, 1), 
                new IncrementingHasher(2, 2)).absoluteUniqueIndices(Shape.fromKM(11, 10)));
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return BitCountProducer.from(new HasherCollection().uniqueIndices(Shape.fromKM(11, 10)));
    }

    @Override
    protected int getBehaviour() {
        // Hasher allows duplicates and may be unordered
        return 0;
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
    }

    @Override
     protected int[][] getExpectedBitCount() {
         return new int[][]{{0, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1}, {6, 1}, {7, 1}, {8, 1},
             {9, 1}};
     }
}
