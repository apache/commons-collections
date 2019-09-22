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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.BitSet;

import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter.Hash;
import org.junit.Test;

public class CountingBloomFilterTest {

    @Test
    public void constructorTest() {
        Hash hash = new Hash(1, 2);
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        ProtoBloomFilter proto = new ProtoBloomFilter(Arrays.asList(hash));
        CountingBloomFilter bf = new CountingBloomFilter(proto, fc);

        assertEquals(3, bf.getHammingWeight());
        BitSet bs = bf.getBitSet();
        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(3));
        Integer count = Integer.valueOf(1);
        assertEquals(count, bf.counts.get(0));
        assertEquals(count, bf.counts.get(1));
        assertEquals(count, bf.counts.get(3));
    }

    @Test
    public void mergeTest_Duplicate() {
        // produces 0,1,3
        Hash hash = new Hash(1, 2);
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        ProtoBloomFilter proto = new ProtoBloomFilter(Arrays.asList(hash));
        CountingBloomFilter bf1 = new CountingBloomFilter(proto, fc);
        CountingBloomFilter bf2 = new CountingBloomFilter(proto, fc);
        CountingBloomFilter bf = bf1.merge(bf2);

        assertEquals(3, bf.getHammingWeight());
        BitSet bs = bf.getBitSet();
        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(3));
        Integer count = Integer.valueOf(2);
        assertEquals(count, bf.counts.get(0));
        assertEquals(count, bf.counts.get(1));
        assertEquals(count, bf.counts.get(3));
    }

    @Test
    public void mergeTest_Different() {
        // produces 0,1,3
        Hash hash1 = new Hash(1, 2);
        ProtoBloomFilter proto1 = new ProtoBloomFilter(Arrays.asList(hash1));
        // produces 1,2,4
        Hash hash2 = new Hash(1, 3);
        ProtoBloomFilter proto2 = new ProtoBloomFilter(Arrays.asList(hash2));
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        CountingBloomFilter bf1 = new CountingBloomFilter(proto1, fc);
        CountingBloomFilter bf2 = new CountingBloomFilter(proto2, fc);
        CountingBloomFilter bf = bf1.merge(bf2);

        assertEquals(5, bf.getHammingWeight());
        BitSet bs = bf.getBitSet();
        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(2));
        assertTrue(bs.get(3));
        assertTrue(bs.get(4));
        Integer count = Integer.valueOf(1);
        assertEquals(count, bf.counts.get(0));
        assertEquals(Integer.valueOf(2), bf.counts.get(1));
        assertEquals(count, bf.counts.get(2));
        assertEquals(count, bf.counts.get(3));
        assertEquals(count, bf.counts.get(4));
    }

    @Test
    public void mergeTest_Overflow() {
        // produces 0,1,3
        Hash hash1 = new Hash(1, 2);
        ProtoBloomFilter proto1 = new ProtoBloomFilter(Arrays.asList(hash1));       
        ProtoBloomFilter proto2 = new ProtoBloomFilter(Arrays.asList(hash1));
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        CountingBloomFilter bf1 = new CountingBloomFilter(proto1, fc);
        
        CountingBloomFilter bf2 = new CountingBloomFilter(proto2, fc);
        bf2.counts.put( 0, Integer.MAX_VALUE );
        
        try {
            CountingBloomFilter bf = bf1.merge(bf2);
            fail( "Should have thrown IllegalStateException");
        }
        catch (IllegalStateException expected)
        {
            // do nothing
        }
        
        
        bf1 = new CountingBloomFilter(proto1, fc);
        bf1.counts.put( 0, Integer.MAX_VALUE );
        
        bf2 = new CountingBloomFilter(proto2, fc);
        
        try {
            CountingBloomFilter bf = bf1.merge(bf2);
            fail( "Should have thrown IllegalStateException");
        }
        catch (IllegalStateException expected)
        {
            // do nothing
        }    }

    @Test
    public void mergeTest_Standard() {
        // merge a standard bloom filter.
        // produces 0,1,3
        Hash hash = new Hash(1, 2);
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        ProtoBloomFilter proto = new ProtoBloomFilter(Arrays.asList(hash));
        StandardBloomFilter bf1 = new StandardBloomFilter(proto, fc);
        CountingBloomFilter bf2 = new CountingBloomFilter(proto, fc);
        CountingBloomFilter bf = bf2.merge(bf1);

        assertEquals(3, bf.getHammingWeight());
        BitSet bs = bf.getBitSet();
        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(3));
        Integer count = Integer.valueOf(2);
        assertEquals(count, bf.counts.get(0));
        assertEquals(count, bf.counts.get(1));
        assertEquals(count, bf.counts.get(3));
    }

    @Test
    public void removeTest_Counting() {
        // produces 0,1,3
        Hash hash1 = new Hash(1, 2);

        // produces 1,2,4
        Hash hash2 = new Hash(1, 3);

        ProtoBloomFilter proto1 = new ProtoBloomFilter(Arrays.asList(hash1, hash2));
        ProtoBloomFilter proto2 = new ProtoBloomFilter(Arrays.asList(hash2));
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        CountingBloomFilter bf1 = new CountingBloomFilter(proto1, fc);
        CountingBloomFilter bf2 = new CountingBloomFilter(proto2, fc);
        CountingBloomFilter bf = bf1.remove(bf2);

        assertEquals(3, bf.getHammingWeight());
        BitSet bs = bf.getBitSet();
        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(3));
        Integer count = Integer.valueOf(1);
        assertEquals(count, bf.counts.get(0));
        assertEquals(count, bf.counts.get(1));
        assertNull(bf.counts.get(2));
        assertEquals(count, bf.counts.get(3));
        assertNull(bf.counts.get(4));

    }

    @Test
    public void removeTest_Standard() {
        // produces 0,1,3
        Hash hash1 = new Hash(1, 2);

        // produces 1,2,4
        Hash hash2 = new Hash(1, 3);

        ProtoBloomFilter proto1 = new ProtoBloomFilter(Arrays.asList(hash1, hash2));
        ProtoBloomFilter proto2 = new ProtoBloomFilter(Arrays.asList(hash2));
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        CountingBloomFilter bf1 = new CountingBloomFilter(proto1, fc);
        StandardBloomFilter bf2 = new StandardBloomFilter(proto2, fc);
        CountingBloomFilter bf = bf1.remove(bf2);

        assertEquals(3, bf.getHammingWeight());
        BitSet bs = bf.getBitSet();
        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(3));
        Integer count = Integer.valueOf(1);
        assertEquals(count, bf.counts.get(0));
        assertEquals(count, bf.counts.get(1));
        assertNull(bf.counts.get(2));
        assertEquals(count, bf.counts.get(3));
        assertNull(bf.counts.get(4));

    }
    
    @Test
    public void testBitSetCopy() {
        Hash hash = new Hash(1, 2);
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        ProtoBloomFilter proto = new ProtoBloomFilter(Arrays.asList(hash));
        CountingBloomFilter bf = new CountingBloomFilter(proto, fc);               
        BitSet bs = bf.getBitSet();
        BitSet bs2 = bf.getBitSet();
        assertEquals( bs, bs2 );        
        bs2.set(20);
        assertNotEquals( bs, bs2 );

    }
    
    @Test
    public void testEquals() {
        Hash hash = new Hash(1, 2);
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        ProtoBloomFilter proto = new ProtoBloomFilter(Arrays.asList(hash));
        CountingBloomFilter bf = new CountingBloomFilter(proto, fc);
        CountingBloomFilter bf2 = new CountingBloomFilter(proto, fc);
        
        assertEquals( bf, bf2 );
        
        FilterConfig fc2 = new FilterConfig(1, 12);
        bf2 = new CountingBloomFilter(proto, fc2);
        assertNotEquals( bf, bf2 );
        
        Hash hash2 = new Hash(1, 3);
        ProtoBloomFilter proto2 = new ProtoBloomFilter(Arrays.asList(hash2));
        bf2 = new CountingBloomFilter(proto2, fc);
        assertNotEquals( bf, bf2 );
        
        BitSet bitSet = bf.getBitSet();
        StandardBloomFilter sbf = new StandardBloomFilter( bitSet );
        assertEquals( bf, sbf );
        assertEquals( sbf, bf );
        
        bitSet.flip(2);
        sbf = new StandardBloomFilter( bitSet );
        assertNotEquals( bf, sbf );
        assertNotEquals( sbf, bf );
        
    }
    
    @Test
    public void hashCodeTest() {
        Hash hash = new Hash(1, 2);
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfig fc = new FilterConfig(1, 11);
        ProtoBloomFilter proto = new ProtoBloomFilter(Arrays.asList(hash));
        CountingBloomFilter bf = new CountingBloomFilter(proto, fc);
        BitSet bs = bf.getBitSet();
        assertEquals( bs.hashCode(), bf.hashCode());
    }
}
