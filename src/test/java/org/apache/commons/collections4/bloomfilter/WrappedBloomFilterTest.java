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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class WrappedBloomFilterTest extends AbstractBloomFilterTest<WrappedBloomFilter> {

    private static class Fixture extends WrappedBloomFilter {

        Fixture(final BloomFilter wrapped) {
            super(wrapped);
        }

        @Override
        public WrappedBloomFilter copy() {
            return new Fixture(getWrapped().copy());
        }

    }

    @Override
    protected WrappedBloomFilter createEmptyFilter(final Shape shape) {
        return new Fixture(new DefaultBloomFilterTest.SparseDefaultBloomFilter(shape));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 34})
    public void testCharacteristics(final int characteristics) {
        final Shape shape = getTestShape();
        final BloomFilter inner = new DefaultBloomFilterTest.SparseDefaultBloomFilter(shape) {
            @Override
            public int characteristics() {
                return characteristics;
            }
        };
        final WrappedBloomFilter underTest = new Fixture(inner);
        assertEquals(characteristics, underTest.characteristics());
    }

}
