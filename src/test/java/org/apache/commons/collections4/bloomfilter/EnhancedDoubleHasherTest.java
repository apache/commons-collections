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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the {@link EnhancedDoubleHasher}.
 */
public class EnhancedDoubleHasherTest extends AbstractHasherTest {
    int[] expected = {1, 0, 71, 71, 1, 6, 15, 29, 49, 4, 39, 11, 65, 58, 63, 9, 41};

    @Override
    protected Hasher createHasher() {
        return new EnhancedDoubleHasher(1, 1);
    }

    @Override
    protected Hasher createEmptyHasher() {
        return NullHasher.INSTANCE;
    }

    @Override
    protected int[] getExpectedIndices() {
        return expected;
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        // Allows duplicates and may be unordered
        return 0;
    }

    @Override
    protected int getHasherSize(final Hasher hasher) {
        return 1;
    }

    @Test
    public void testByteConstructor() {
        // single value become increment.
        EnhancedDoubleHasher hasher = new EnhancedDoubleHasher(new byte[] {1});
        assertEquals(0, hasher.getInitial());
        assertEquals(0x01_00_00_00_00_00_00_00L, hasher.getIncrement());

        // 2 bytes become initial and increment.
        hasher = new EnhancedDoubleHasher(new byte[] {1, 2});
        assertEquals(0x01_00_00_00_00_00_00_00L, hasher.getInitial());
        assertEquals(0x02_00_00_00_00_00_00_00L, hasher.getIncrement());

        // odd values place extra byte in increment.
        hasher = new EnhancedDoubleHasher(new byte[] {1, 2, 3});
        assertEquals(0x01_00_00_00_00_00_00_00L, hasher.getInitial());
        assertEquals(0x02_03_00_00_00_00_00_00L, hasher.getIncrement());

        // even short split
        hasher = new EnhancedDoubleHasher(new byte[] {0, 1, 0, 2});
        assertEquals(0x01_00_00_00_00_00_00L, hasher.getInitial());
        assertEquals(0x02_00_00_00_00_00_00L, hasher.getIncrement());

        // longs are parse correctly
        hasher = new EnhancedDoubleHasher(new byte[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2});
        assertEquals(1, hasher.getInitial());
        assertEquals(2, hasher.getIncrement());

        // excess bytes are ignored before mid point and at end
        hasher = new EnhancedDoubleHasher(new byte[] {0, 0, 0, 0, 0, 0, 0, 1, 5, 5, 0, 0, 0, 0, 0, 0, 0, 2, 5, 5});
        assertEquals(1, hasher.getInitial());
        assertEquals(2, hasher.getIncrement());

        // odd extra bytes are accounted for correctly
        hasher = new EnhancedDoubleHasher(new byte[] {0, 0, 0, 0, 0, 0, 0, 1, 5, 1, 0, 0, 0, 0, 0, 0, 2, 5, 5});
        assertEquals(1, hasher.getInitial());
        assertEquals(0x01_00_00_00_00_00_00_02L, hasher.getIncrement());

        // test empty buffer
        assertThrows(IllegalArgumentException.class, () -> new EnhancedDoubleHasher(new byte[0]));
    }

    @Test
    void testModEdgeCases() {
        for (final long dividend : new long[] {-1, -2, -3, -6378683, -23567468136887892L, Long.MIN_VALUE, 345, 678686,
            67868768686878924L, Long.MAX_VALUE}) {
            for (final int divisor : new int[] {1, 2, 3, 5, 13, Integer.MAX_VALUE}) {
                assertEquals((int) Long.remainderUnsigned(dividend, divisor), EnhancedDoubleHasher.mod(dividend, divisor),
                        () -> String.format("failure with dividend=%s and divisor=%s.", dividend, divisor));
            }
        }
    }
}
