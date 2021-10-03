package org.apache.commons.collections4.bloomfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class BitMaptTest {

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
    public void containsTest() {
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
    public void numberOfBucketsTest() {
        for (int i = 0;i<64;i++) {
            assertEquals( 1, BitMap.numberOfBuckets(i));
        }
        for (int i = 64;i<128;i++) {
            assertEquals( 2, BitMap.numberOfBuckets(i));
        }
        assertEquals( 3, BitMap.numberOfBuckets(128));

    }
}
