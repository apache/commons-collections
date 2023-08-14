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

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BitMapTest {

    @Test
    public final void testGetLongBit() {
        assertEquals(1, BitMap.getLongBit(0));
        assertEquals(0x8000000000000000L, BitMap.getLongBit(63));
        assertEquals(1, BitMap.getLongBit(64));
        assertEquals(0x8000000000000000L, BitMap.getLongBit(127));
        assertEquals(1, BitMap.getLongBit(128));
    }

    @Test
    public final void testGetLongIndex() {
        assertEquals(0, BitMap.getLongIndex(0));
        assertEquals(0, BitMap.getLongIndex(63));
        assertEquals(1, BitMap.getLongIndex(64));
        assertEquals(1, BitMap.getLongIndex(127));
        assertEquals(2, BitMap.getLongIndex(128));
    }

    @Test
    public final void testNumberOfBitMaps() {
        assertEquals(0, BitMap.numberOfBitMaps(0), "Number of bits 0");
        for (int i = 1; i < 65; i++) {
            assertEquals(1, BitMap.numberOfBitMaps(i), String.format("Number of bits %d", i));
        }
        for (int i = 65; i < 129; i++) {
            assertEquals(2, BitMap.numberOfBitMaps(i), String.format("Number of bits %d", i));
        }
        assertEquals(3, BitMap.numberOfBitMaps(129), "Number of bits 129");
    }

    @Test
    public final void testSet() {
        final long[] bitMaps = new long[BitMap.numberOfBitMaps(129)];
        for (int i = 0; i < 129; i++) {
            BitMap.set(bitMaps, i);
            assertTrue(BitMap.contains(bitMaps, i), String.format("Failed at index: %d", i));
        }
        assertEquals(0xFFFFFFFFFFFFFFFFL, bitMaps[0]);
        assertEquals(0xFFFFFFFFFFFFFFFFL, bitMaps[1]);
        assertEquals(1L, bitMaps[2]);
    }

    @Test
    public final void testContains() {
        final long[] bitMaps = new long[1];

        for (int i = 0; i < 64; i++) {
            bitMaps[0] = 0L;
            BitMap.set(bitMaps, i);
            for (int j = 0; j < 64; j++) {
                if (j == i) {
                    assertTrue(BitMap.contains(bitMaps, j), String.format("Failed at index: %d for %d", i, j));
                } else {
                    assertFalse(BitMap.contains(bitMaps, j), String.format("Failed at index %d for %d", i, j));
                }
            }
        }

        // test boundary conditions
        long[] ary = new long[1];

        final long[] aryT = ary;
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> BitMap.contains(aryT, -1));
        assertFalse(BitMap.contains(ary, 0));
        ary[0] = 0x01;
        assertTrue(BitMap.contains(ary, 0));

        assertFalse(BitMap.contains(ary, 63));
        ary[0] = 1L << 63;
        assertTrue(BitMap.contains(ary, 63));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> BitMap.contains(aryT, 64));

        ary = new long[2];
        assertFalse(BitMap.contains(ary, 64));
        ary[1] = 1;
        assertTrue(BitMap.contains(ary, 64));
    }

    private void assertMod(long l, int i) {
        assertEquals(Math.floorMod(l, i), BitMap.mod(l, i));
    }

    @Test
    public final void testMod() {
        assertMod(Long.MAX_VALUE, Integer.MAX_VALUE);
        assertMod(Long.MAX_VALUE, Integer.MAX_VALUE-1);
        assertThrows(ArithmeticException.class, () -> BitMap.mod(Long.MAX_VALUE, 0));
        assertMod(Long.MAX_VALUE-1, Integer.MAX_VALUE);
        assertMod(Long.MAX_VALUE-1, Integer.MAX_VALUE-1);
        assertMod(0, Integer.MAX_VALUE);
        assertNotEquals(Math.floorMod(5, -1), BitMap.mod(5, -1));
    }
}
