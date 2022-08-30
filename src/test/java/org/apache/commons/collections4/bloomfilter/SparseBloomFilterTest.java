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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link SparseBloomFilter}.
 */
public class SparseBloomFilterTest extends AbstractBloomFilterTest<SparseBloomFilter> {
    @Override
    protected SparseBloomFilter createEmptyFilter(final Shape shape) {
        return new SparseBloomFilter(shape);
    }

    @Test
    public void testBitMapProducerEdgeCases() {
        int[] values = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 65, 66, 67, 68, 69, 70, 71 };
        BloomFilter bf = createFilter(getTestShape(), IndexProducer.fromIndexArray(values));

        // verify exit early before bitmap boundary
        int[] passes = new int[1];
        assertFalse(bf.forEachBitMap(l -> {
            passes[0]++;
            return false;
        }));
        assertEquals(1, passes[0]);

        // verify exit early at bitmap boundary
        bf = createFilter(getTestShape(), IndexProducer.fromIndexArray(values));
        passes[0] = 0;
        assertFalse(bf.forEachBitMap(l -> {
            boolean result = passes[0] == 0;
            if (result) {
                passes[0]++;
            }
            return result;
        }));
        assertEquals(1, passes[0]);

        // verify add extra if all values in first bitmap
        values = new int[] { 1, 2, 3, 4 };
        bf = createFilter(getTestShape(), IndexProducer.fromIndexArray(values));
        passes[0] = 0;
        assertTrue(bf.forEachBitMap(l -> {
            passes[0]++;
            return true;
        }));
        assertEquals(2, passes[0]);

        // verify exit early if all values in first bitmap and predicate returns false
        // on 2nd block
        values = new int[] { 1, 2, 3, 4 };
        bf = createFilter(getTestShape(), IndexProducer.fromIndexArray(values));
        passes[0] = 0;
        assertFalse(bf.forEachBitMap(l -> {
            boolean result = passes[0] == 0;
            if (result) {
                passes[0]++;
            }
            return result;
        }));
        assertEquals(1, passes[0]);
    }

    @Test
    public void testBloomFilterBasedMergeEdgeCases() {
        BloomFilter bf1 = createEmptyFilter(getTestShape());
        BloomFilter bf2 = new SimpleBloomFilter(getTestShape());
        bf2.merge(from1);
        bf1.merge(bf2);
        assertTrue(bf2.forEachBitMapPair(bf1, (x, y) -> x == y));
    }
}
