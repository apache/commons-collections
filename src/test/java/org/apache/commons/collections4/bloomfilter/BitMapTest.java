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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class BitMapTest {

    @Test
    public void checkPositiveTest() {
        BitMap.checkPositive(0);
        BitMap.checkPositive(0);
        try {
            BitMap.checkPositive(-1);

        } catch (IndexOutOfBoundsException expected) {
            // do nothing
        }
    }



    @Test
    public void getLongBitTest() {
        assertEquals( 1, BitMap.getLongBit(0) );
        assertEquals( 0x8000000000000000L, BitMap.getLongBit( 63 ) );
        assertEquals( 1, BitMap.getLongBit( 64) );
        assertEquals( 0x8000000000000000L, BitMap.getLongBit( 127 ) );
        assertEquals( 1, BitMap.getLongBit( 128 ) );
    }

    @Test
    public void getLongIndexTest() {
        assertEquals( 0, BitMap.getLongIndex(0) );
        assertEquals( 0, BitMap.getLongIndex( 63 ) );
        assertEquals( 1, BitMap.getLongIndex( 64) );
        assertEquals( 1, BitMap.getLongIndex( 127 ) );
        assertEquals( 2, BitMap.getLongIndex( 128 ) );
    }


    @Test
    public void isSparseTest() {
        Shape shape = new Shape( 17, 64 );
        assertTrue( BitMap.isSparse(0, shape) );
        assertTrue( BitMap.isSparse(1, shape) );
        assertTrue( BitMap.isSparse(2, shape) );
        assertFalse( BitMap.isSparse(3, shape) );

        shape = new Shape( 17, 64*3 );

        for (int i=0;i<7; i++) {
            assertTrue( BitMap.isSparse(i, shape) );
        }
        assertFalse( BitMap.isSparse(7, shape) );
    }

    @Test
    public void numberOfBitMapsTest() {
        assertEquals( "Number of bits 0", 0, BitMap.numberOfBitMaps(0));
        for (int i = 1;i<65;i++) {
            assertEquals( String.format( "Number of bits %d", i ), 1, BitMap.numberOfBitMaps(i));
        }
        for (int i = 65;i<129;i++) {
            assertEquals( String.format( "Number of bits %d", i ),2, BitMap.numberOfBitMaps(i));
        }
        assertEquals( "Number of bits 129", 3, BitMap.numberOfBitMaps(129));

    }

    @Test
    public void setTest() {
        long[] bitMaps = new long[ BitMap.numberOfBitMaps(129)];
        for (int i=0;i<129;i++) {
            BitMap.set( bitMaps, i);
            assertTrue( String.format("Failed at index: %d",i), BitMap.contains( bitMaps, i));
        }
        assertEquals( 0xFFFFFFFFFFFFFFFFL, bitMaps[0] );
        assertEquals( 0xFFFFFFFFFFFFFFFFL, bitMaps[1] );
        assertEquals( 1L, bitMaps[2] );
    }

    @Test
    public void containsTest() {
        long[] bitMaps = new long[ 1 ];

        for (int i=0;i<64;i++) {
            bitMaps[0] = 0l;
            BitMap.set( bitMaps, i);
            for (int j=0;j<64;j++) {
                if (j==i) {
                    assertTrue( String.format("Failed at index: %d for %d",i,j), BitMap.contains( bitMaps, j));
                } else {
                    assertFalse( String.format("Failed at index %d for %d",i,j), BitMap.contains( bitMaps, j));
                }
            }

        }
    }

    @Test
    public void contains_boundaryConditionTest() {
        long[] ary = new long[1];

        assertFalse( BitMap.contains(ary, 0) );
        ary[0] = 0x01;
        assertTrue( BitMap.contains(ary, 0) );

        assertFalse( BitMap.contains(ary, 63) );
        ary[0] = (1L << 63);
        assertTrue( BitMap.contains(ary, 63) );

        ary = new long[2];
        assertFalse( BitMap.contains(ary, 64) );
        ary[1] = 1;
        assertTrue( BitMap.contains(ary, 64) );

    }
}
