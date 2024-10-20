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

public class BitMapExtractorFromWrappedBloomFilterTest extends AbstractBitMapExtractorTest {

    protected Shape shape = Shape.fromKM(17, 72);

    @Override
    protected BitMapExtractor createEmptyExtractor() {
        return new WrappedBloomFilter(new DefaultBloomFilterTest.SparseDefaultBloomFilter(shape)) {
            @Override
            public DefaultBloomFilterTest.SparseDefaultBloomFilter copy() {
                final DefaultBloomFilterTest.SparseDefaultBloomFilter result = new DefaultBloomFilterTest.SparseDefaultBloomFilter(shape);
                result.merge(getWrapped());
                return result;
            }
        };
    }

    @Override
    protected BitMapExtractor createExtractor() {
        final Hasher hasher = new IncrementingHasher(0, 1);
        final WrappedBloomFilter bf = new WrappedBloomFilter(new DefaultBloomFilterTest.SparseDefaultBloomFilter(shape)) {
            @Override
            public DefaultBloomFilterTest.SparseDefaultBloomFilter copy() {
                final DefaultBloomFilterTest.SparseDefaultBloomFilter result = new DefaultBloomFilterTest.SparseDefaultBloomFilter(shape);
                result.merge(getWrapped());
                return result;
            }
        };
        bf.merge(hasher);
        return bf;
    }

}
