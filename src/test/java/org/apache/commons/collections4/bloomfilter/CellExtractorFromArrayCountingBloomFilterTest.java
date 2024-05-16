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

public class CellExtractorFromArrayCountingBloomFilterTest extends AbstractCellExtractorTest {

    protected Shape shape = Shape.fromKM(17, 72);

    @Override
    protected CellExtractor createEmptyExtractor() {
        return new ArrayCountingBloomFilter(shape);
    }

    @Override
    protected CellExtractor createExtractor() {
        final ArrayCountingBloomFilter filter = new ArrayCountingBloomFilter(shape);
        filter.merge(new IncrementingHasher(0, 1));
        filter.merge(new IncrementingHasher(5, 1));
        return filter;
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
    }

    @Override
    protected int[] getExpectedValues() {
        return new int[] {1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1};
    }
}
