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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BitMapsTest {

    /**
     * Assert the {@link BitMaps#mod(long, int)} method functions as an unsigned modulus.
     *
     * @param dividend the dividend
     * @param divisor the divisor
     */
    private void assertMod(final long dividend, final int divisor) {
        assertTrue(divisor > 0 && divisor <= Integer.MAX_VALUE,
            "Incorrect usage. Divisor must be strictly positive.");
        assertEquals((int) Long.remainderUnsigned(dividend, divisor), BitMaps.mod(dividend, divisor),
            () -> String.format("failure with dividend=%s and divisor=%s.", dividend, divisor));
    }

    @Test
    public final void testContains() {
        final long[] bitMaps = new long[1];

        for (int i = 0; i < 64; i++) {
            bitMaps[0] = 0L;
            BitMaps.set(bitMaps, i);
            for (int j = 0; j < 64; j++) {
                if (j == i) {
                    assertTrue(BitMaps.contains(bitMaps, j), String.format("Failed at index: %d for %d", i, j));
                } else {
                    assertFalse(BitMaps.contains(bitMaps, j), String.format("Failed at index %d for %d", i, j));
                }
            }
        }

        // test boundary conditions
        long[] ary = new long[1];

        final long[] aryT = ary;
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> BitMaps.contains(aryT, -1));
        assertFalse(BitMaps.contains(ary, 0));
        ary[0] = 0x01;
        assertTrue(BitMaps.contains(ary, 0));

        assertFalse(BitMaps.contains(ary, 63));
        ary[0] = 1L << 63;
        assertTrue(BitMaps.contains(ary, 63));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> BitMaps.contains(aryT, 64));

        ary = new long[2];
        assertFalse(BitMaps.contains(ary, 64));
        ary[1] = 1;
        assertTrue(BitMaps.contains(ary, 64));
    }

    @Test
    public final void testGetLongBit() {
        assertEquals(1, BitMaps.getLongBit(0));
        assertEquals(0x8000000000000000L, BitMaps.getLongBit(63));
        assertEquals(1, BitMaps.getLongBit(64));
        assertEquals(0x8000000000000000L, BitMaps.getLongBit(127));
        assertEquals(1, BitMaps.getLongBit(128));
    }

    @Test
    public final void testGetLongIndex() {
        assertEquals(0, BitMaps.getLongIndex(0));
        assertEquals(0, BitMaps.getLongIndex(63));
        assertEquals(1, BitMaps.getLongIndex(64));
        assertEquals(1, BitMaps.getLongIndex(127));
        assertEquals(2, BitMaps.getLongIndex(128));
    }

    @Test
    public void testMod() {
        for (final long dividend : new long[] {0, -1, -2, -3, -6378683, -23567468136887892L,
            Long.MIN_VALUE, 345, 678686, 67868768686878924L, Long.MAX_VALUE, Long.MAX_VALUE - 1}) {
            for (final int divisor : new int[] {1, 2, 3, 5, 13, Integer.MAX_VALUE, Integer.MAX_VALUE - 1}) {
                assertMod(dividend, divisor);
            }
        }
    }

    @Test
    public void testModEdgeCases() {
        for (final long dividend : new long[] {0, -1, 1, Long.MAX_VALUE}) {
            assertThrows(ArithmeticException.class, () -> BitMaps.mod(dividend, 0));
        }
        assertNotEquals(Math.floorMod(5, -1), BitMaps.mod(5, -1));
    }

    @Test
    public final void testNumberOfBitMaps() {
        assertEquals(0, BitMaps.numberOfBitMaps(0), "Number of bits 0");
        for (int i = 1; i < 65; i++) {
            assertEquals(1, BitMaps.numberOfBitMaps(i), String.format("Number of bits %d", i));
        }
        for (int i = 65; i < 129; i++) {
            assertEquals(2, BitMaps.numberOfBitMaps(i), String.format("Number of bits %d", i));
        }
        assertEquals(3, BitMaps.numberOfBitMaps(129), "Number of bits 129");
    }

    @Test
    public final void testSet() {
        final long[] bitMaps = new long[BitMaps.numberOfBitMaps(129)];
        for (int i = 0; i < 129; i++) {
            BitMaps.set(bitMaps, i);
            assertTrue(BitMaps.contains(bitMaps, i), String.format("Failed at index: %d", i));
        }
        assertEquals(0xFFFFFFFFFFFFFFFFL, bitMaps[0]);
        assertEquals(0xFFFFFFFFFFFFFFFFL, bitMaps[1]);
        assertEquals(1L, bitMaps[2]);
    }
}
