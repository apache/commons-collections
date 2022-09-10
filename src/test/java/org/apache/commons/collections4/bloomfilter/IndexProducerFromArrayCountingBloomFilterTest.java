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

public class IndexProducerFromArrayCountingBloomFilterTest extends AbstractIndexProducerTest {

    protected Shape shape = Shape.fromKM(17, 72);

    @Override
    protected IndexProducer createProducer() {
        ArrayCountingBloomFilter filter = new ArrayCountingBloomFilter(shape);
        filter.merge(new IncrementingHasher(0, 1));
        filter.merge(new IncrementingHasher(5, 1));
        return filter;
    }

    @Override
    protected IndexProducer createEmptyProducer() {
        return new ArrayCountingBloomFilter(shape);
    }

    @Override
    protected int getBehaviour() {
        return FOR_EACH_DISTINCT | FOR_EACH_ORDERED | AS_ARRAY_DISTINCT | AS_ARRAY_ORDERED;
    }


    @Override
    protected int[] getExpectedIndex() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
    }
}
