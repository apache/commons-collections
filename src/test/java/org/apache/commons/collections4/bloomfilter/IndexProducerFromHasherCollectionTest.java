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

public class IndexProducerFromHasherCollectionTest extends AbstractIndexProducerTest {

    @Override
    protected IndexProducer createProducer() {
        return new HasherCollection(new IncrementingHasher(0, 1), new IncrementingHasher(0, 2)).indices(Shape.fromKM(17, 72));
    }

    @Override
    protected IndexProducer createEmptyProducer() {
        return new HasherCollection().indices(Shape.fromKM(17, 72));
    }

    @Override
    protected int[] getExpectedIndex() {
        return new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,
            0,2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32};
    }

    @Override
    protected int getBehaviour() {
        // HasherCollection allows duplicates and may be unordered
        return 0;
    }
}
