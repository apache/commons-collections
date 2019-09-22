/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.BitSet;

import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter.Hash;
import org.junit.Test;

public class StandardBloomFilterTest {

    @Test
    public void constructorTest() {
        Hash hash = new Hash(1, 2);
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        ProtoBloomFilter pbf = new ProtoBloomFilter(Arrays.asList(hash));
        BloomFilter bf = new StandardBloomFilter(pbf, fc);

        assertEquals(3, bf.getHammingWeight());
        BitSet bs = bf.getBitSet();
        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(3));
    }

    @Test
    public void getLogTest_NoValues() {
        BitSet bs = new BitSet();
        BloomFilter bf = new StandardBloomFilter(bs);
        assertEquals(0.0, bf.getLog(), 0.0000001);
    }

    @Test
    public void getLogTest() {
        BitSet bs = new BitSet();
        bs.set(2);
        BloomFilter bf = new StandardBloomFilter(bs);
        assertEquals(2.0, bf.getLog(), 0.0000001);

        bs.set(20);
        bf = new StandardBloomFilter(bs);
        assertEquals(20.000003814697266, bf.getLog(), 0.000000000000001);
    }

    @Test
    public void getLogTest_largeBitSpacing() {
        BitSet bs = new BitSet();
        bs.set(2);
        bs.set(40);
        // show it is approximate bit 40 and bit 2 yeilds same as 40 alone
        BloomFilter bf = new StandardBloomFilter(bs);

        assertEquals(40.0, bf.getLog(), 0.000000000000001);
    }

    @Test
    public void getLogTest_FullBuffer() {
        BitSet bs = new BitSet();
        for (int i = 0; i < 28; i++) {
            bs.set(i);
        }

        BloomFilter bf = new StandardBloomFilter(bs);

        assertEquals(27.9999999, bf.getLog(), 0.0000001);
    }

    @Test
    public void getLog2xTest() {
        BitSet bs = new BitSet();
        bs.set(2);
        BloomFilter bf = new StandardBloomFilter(bs);
        assertEquals(2.0, bf.getLog(), 0.0000001);
        assertEquals(2.0, bf.getLog(), 0.0000001);
    }

    @Test
    public void getHammingWeight() {
        BitSet bs = new BitSet();
        bs.set(2);
        BloomFilter bf = new StandardBloomFilter(bs);
        assertEquals(1, bf.getHammingWeight());

        bs.set(20);
        bf = new StandardBloomFilter(bs);
        assertEquals(2, bf.getHammingWeight());

        bs.set(40);
        bf = new StandardBloomFilter(bs);
        assertEquals(3, bf.getHammingWeight());

    }

    @Test
    public void showBitSetChangeNotAffectFilter() {
        BitSet bs = new BitSet();
        bs.set(2);
        BloomFilter bf = new StandardBloomFilter(bs);
        assertEquals(1, bf.getHammingWeight());
        // changing the bit set after constructor does not impact filter.
        bs.set(20);
        assertEquals(1, bf.getHammingWeight());
    }

    @Test
    public void distanceTest_Not0() {
        BitSet bs = new BitSet();
        bs.set(2);
        StandardBloomFilter bf1 = new StandardBloomFilter(bs);
        bs.set(4);
        StandardBloomFilter bf2 = new StandardBloomFilter(bs);

        assertEquals(1, bf1.distance(bf2));
        assertEquals(1, bf2.distance(bf1));

        // show now shared bits
        bs = new BitSet();
        bs.set(4);
        bf2 = new StandardBloomFilter(bs);
        assertEquals(2, bf1.distance(bf2));
        assertEquals(2, bf2.distance(bf1));

    }

    @Test
    public void distanceTest_0() {
        BitSet bs = new BitSet();
        bs.set(2);
        StandardBloomFilter bf1 = new StandardBloomFilter(bs);
        StandardBloomFilter bf2 = new StandardBloomFilter(bs);

        assertEquals(0, bf1.distance(bf2));
        assertEquals(0, bf2.distance(bf1));
    }

    @Test
    public void equalityTest() {
        BitSet bs = new BitSet();
        bs.set(2);
        BloomFilter bf1 = new StandardBloomFilter(bs);
        StandardBloomFilter bf2 = new StandardBloomFilter(bs);

        assertEquals(bf1, bf2);
        assertEquals(0, bf1.distance(bf2));

        bs.set(40);
        bs.clear(2);
        bf2 = new StandardBloomFilter(bs);
        assertNotEquals(bf1, bf2);
        
        assertFalse(  bf1.equals(bf1.toString()) );
    }

    @Test
    public void hashCodeTest() {
        BitSet bs = new BitSet();
        bs.set(2);
        BloomFilter bf = new StandardBloomFilter(bs);
        assertEquals( bs.hashCode(), bf.hashCode());
    }
    
    @Test
    public void matchTest() {
        BitSet bs = new BitSet();
        bs.set(2);
        StandardBloomFilter bf1 = new StandardBloomFilter(bs);

        bs.set(40);
        StandardBloomFilter bf2 = new StandardBloomFilter(bs);
        assertFalse(bf2.match(bf1));
        assertTrue(bf1.match(bf2));
    }

    @Test
    public void inverseMatchTest() {
        BitSet bs = new BitSet();
        bs.set(2);
        BloomFilter bf1 = new StandardBloomFilter(bs);

        bs.set(40);
        BloomFilter bf2 = new StandardBloomFilter(bs);
        assertTrue(bf2.inverseMatch(bf1));
        assertFalse(bf1.inverseMatch(bf2));
    }

    @Test
    public void mergeTest() {
        BitSet bs = new BitSet();
        bs.set(2);
        StandardBloomFilter bf1 = new StandardBloomFilter(bs);

        bs.set(40);
        StandardBloomFilter bf2 = new StandardBloomFilter(bs);

        StandardBloomFilter bf3 = bf1.merge(bf2);

        assertTrue(bf1.match(bf3));
        assertTrue(bf2.match(bf3));

    }
    
    @Test
    public void testBitSetCopy() {
        BitSet bs = new BitSet();
        bs.set(2);
        StandardBloomFilter bf1 = new StandardBloomFilter(bs);
        BitSet bs2 = bf1.getBitSet();
        assertEquals( bs, bs2 );
        bs2.set(20);
        assertNotEquals( bs, bs2 );

    }
}
