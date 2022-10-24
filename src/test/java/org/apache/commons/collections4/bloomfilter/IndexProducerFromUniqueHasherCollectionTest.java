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

public class IndexProducerFromUniqueHasherCollectionTest extends AbstractIndexProducerTest {

    // selecting 11 items from the range [0,9] will cause a collision
    private Shape shape = Shape.fromKM(11, 10);

    @Override
    protected IndexProducer createProducer() {
        return new HasherCollection(new IncrementingHasher(1, 1), new IncrementingHasher(2, 2)).uniqueIndices(shape);
    }

    @Override
    protected IndexProducer createEmptyProducer() {
        return new HasherCollection().uniqueIndices(shape);
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 4, 6, 8, 0};
    }

    @Override
    protected int getBehaviour() {
        // HasherCollection allows duplicates and may be unordered
        return 0;
    }
}
