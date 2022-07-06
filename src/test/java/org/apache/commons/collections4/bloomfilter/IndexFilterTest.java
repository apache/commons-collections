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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests the Filter class.
 */
public class IndexFilterTest {

    @Test
    public void testFiltering() {
        Shape shape = Shape.fromKM(3, 12);
        List<Integer> consumer = new ArrayList<Integer>();
        IntPredicate filter = IndexFilter.create(shape, consumer::add);

        for (int i = 0; i < 12; i++) {
            assertTrue(filter.test(i));
        }
        assertEquals(12, consumer.size());

        for (int i = 0; i < 12; i++) {
            assertTrue(filter.test(i));
        }
        assertEquals(12, consumer.size());
    }

    @ParameterizedTest
    @CsvSource({ "1, 64", "2, 64", "3, 64", "7, 357", "7, 17", })
    void testFilter(int k, int m) {
        Shape shape = Shape.fromKM(k, m);
        BitSet used = new BitSet(m);
        for (int n = 0; n < 10; n++) {
            used.clear();
            List<Integer> consumer = new ArrayList<>();
            IntPredicate filter = IndexFilter.create(shape, consumer::add);

            // Make random indices; these may be duplicates
            long seed = ThreadLocalRandom.current().nextLong();
            SplittableRandom rng = new SplittableRandom(seed);
            for (int i = Math.min(k, m / 2); i-- > 0;) {
                int bit = rng.nextInt(m);
                // duplicates should not alter the list size
                int newSize = consumer.size() + (used.get(bit) ? 0 : 1);
                assertTrue(filter.test(bit));
                assertEquals(newSize, consumer.size(), () -> String.format("Bad filter. Seed=%d, bit=%d", seed, bit));
                used.set(bit);
            }

            // The list should have unique entries
            assertArrayEquals(used.stream().toArray(), consumer.stream().mapToInt(i -> (int) i).sorted().toArray());
            final int size = consumer.size();

            // Second observations do not change the list size
            used.stream().forEach(bit -> {
                assertTrue(filter.test(bit));
                assertEquals(size, consumer.size(), () -> String.format("Bad filter. Seed=%d, bit=%d", seed, bit));
            });

            assertThrows(IndexOutOfBoundsException.class, () -> filter.test(m));
            assertThrows(IndexOutOfBoundsException.class, () -> filter.test(-1));
        }
    }
}
