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

import org.apache.commons.collections4.bloomfilter.BloomFilter.CheckBitMapCount;

/**
 * Tests for the {@link ArrayCountingBloomFilter}.
 */
public class ArrayCountingBloomFilterTest extends AbstractCountingBloomFilterTest<ArrayCountingBloomFilter> {

    @Override
    protected ArrayCountingBloomFilter createEmptyFilter(Shape shape) {
        return new ArrayCountingBloomFilter(shape);
    }

    @Override
    protected ArrayCountingBloomFilter createFilter(Shape shape, Hasher hasher) {
        return createFilter( shape, hasher.uniqueIndices(shape));
    }

    @Override
    protected ArrayCountingBloomFilter createFilter(Shape shape, BitMapProducer producer) {
        return createFilter( shape, IndexProducer.fromBitMapProducer(new CheckBitMapCount(producer, BitMap.numberOfBitMaps(shape.getNumberOfBits()))));
    }

    @Override
    protected ArrayCountingBloomFilter createFilter(Shape shape, IndexProducer producer) {
        ArrayCountingBloomFilter filter = createEmptyFilter(shape);
        try {
            filter.add(BitCountProducer.from(producer));
            return filter;
        } catch (ArrayIndexOutOfBoundsException e) {
            // since ArrayCountingBloomFilter does not ahave a constructor that takes a hasher
            // we have to duplicate the expected results here.
            throw new IllegalArgumentException( e );
        }
    }
}
