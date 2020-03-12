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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tests for the {@link BloomFilterIndexer}.
 */
public class BloomFilterIndexerTest {

    @Test(expected = IndexOutOfBoundsException.class)
    public void testCheckPositiveThrows() {
        BloomFilterIndexer.checkPositive(-1);
    }

    @Test
    public void testGetLongIndex() {
        Assert.assertEquals(0, BloomFilterIndexer.getLongIndex(0));

        for (final int index : getIndexes()) {
            // getLongIndex is expected to identify a block of 64-bits (starting from zero)
            Assert.assertEquals(index / Long.SIZE, BloomFilterIndexer.getLongIndex(index));

            // Verify the behaviour for negatives. It should produce a negative (invalid)
            // as a simple trip for incorrect usage.
            Assert.assertTrue(BloomFilterIndexer.getLongIndex(-index) < 0);

            // If index is not zero then when negated this is what a signed shift
            // of 6-bits actually does
            Assert.assertEquals(((1 - index) / Long.SIZE) - 1,
                    BloomFilterIndexer.getLongIndex(-index));
        }
    }

    @Test
    public void testGetLongBit() {
        Assert.assertEquals(1L, BloomFilterIndexer.getLongBit(0));

        for (final int index : getIndexes()) {
            // getLongBit is expected to identify a single bit in a 64-bit block
            Assert.assertEquals(1L << (index % Long.SIZE), BloomFilterIndexer.getLongBit(index));

            // Verify the behaviour for negatives
            Assert.assertEquals(1L << (64 - (index & 0x3f)), BloomFilterIndexer.getLongBit(-index));
        }
    }

    /**
     * Gets non-zero positive indexes for testing.
     *
     * @return the indices
     */
    private static int[] getIndexes() {
        final Random rng = ThreadLocalRandom.current();
        ArrayList<Integer> indexes = new ArrayList<>(40);
        for (int i = 0; i < 10; i++) {
            // random positive numbers
            indexes.add(rng.nextInt() >>> 1);
            indexes.add(rng.nextInt(23647826));
            indexes.add(rng.nextInt(245));
        }
        // Quickly remove zeros (as these cannot be negated)
        indexes.removeIf(i -> i == 0);
        // Add edge cases here
        indexes.add(1);
        indexes.add(2);
        indexes.add(63);
        indexes.add(64);
        indexes.add(Integer.MAX_VALUE);
        return indexes.stream().mapToInt(Integer::intValue).toArray();
    }
}
