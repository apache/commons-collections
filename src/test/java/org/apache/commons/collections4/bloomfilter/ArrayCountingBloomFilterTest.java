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

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

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
        ArrayCountingBloomFilter filter = createEmptyFilter(shape);
        filter.add(BitCountProducer.from(hasher.indices(shape)));
        return filter;
    }

}
