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

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link HasherCollection}.
 */

public class HasherCollectionTest extends AbstractHasherTest {
    @Override
    protected HasherCollection createHasher() {
        return new HasherCollection(new IncrementingHasher(1, 1),
                new IncrementingHasher(2, 2));
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 2, 4, 6, 8, 10, 12, 14, 16, 18,
            20, 22, 24, 26, 28, 30, 32, 34};
    }

    @Override
    protected HasherCollection createEmptyHasher() {
        return new HasherCollection();
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        // Allows duplicates and may be unordered
        return 0;
    }

    @Override
    protected int getHasherSize(Hasher hasher) {
        return ((HasherCollection) hasher).getHashers().size();
    }

    @Test
    public void testAdd() {
        HasherCollection hasher = createHasher();
        hasher.add(new IncrementingHasher(2, 2));
        assertEquals(3, hasher.getHashers().size());

        hasher.add(Arrays.asList(new IncrementingHasher(3, 2), new IncrementingHasher(4, 2)));
        assertEquals(5, hasher.getHashers().size());
    }

    @Test
    void testHasherCollection() {
        Hasher h1 = new IncrementingHasher(13, 4678);
        Hasher h2 = new IncrementingHasher(42, 987);
        Hasher h3 = new IncrementingHasher(454, 2342);

        HasherCollection hc1 = new HasherCollection(Arrays.asList(h1, h1));
        HasherCollection hc2 = new HasherCollection(Arrays.asList(h2, h3));
        HasherCollection hc3 = new HasherCollection(Arrays.asList(hc1, hc2));

        ArrayCountingBloomFilter bf = new ArrayCountingBloomFilter(Shape.fromKM(5, 10000));

        // Should add h1, h1, h2, h3
        Assertions.assertTrue(bf.merge(hc3));
        Assertions.assertTrue(bf.remove(h1));
        Assertions.assertTrue(bf.remove(h1));
        Assertions.assertNotEquals(0, bf.cardinality());
        Assertions.assertTrue(bf.remove(hc2));
        Assertions.assertEquals(0, bf.cardinality());
    }

    @Test
    public void testAbsoluteUniqueIndices() {
        int[] actual = new HasherCollection(
            new IncrementingHasher(1, 1),
            new IncrementingHasher(10, 1)
        ).absoluteUniqueIndices(Shape.fromKM(5, 1000)).asIndexArray();
        int[] expected = IntStream.concat(
                IntStream.range(1, 1 + 5),
                IntStream.range(10, 10 + 5)
            ).toArray();
        Assertions.assertArrayEquals(expected, actual);
    }
}
