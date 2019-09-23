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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;

import org.apache.commons.collections4.bloomfilter.FilterConfiguration;
import org.apache.commons.collections4.bloomfilter.FilterConfiguration;
import org.apache.commons.collections4.bloomfilter.ProtoBloomFilter;
import org.apache.commons.collections4.bloomfilter.ProtoBloomFilter.Hash;
import org.junit.Test;

public class ProtoBloomFilterTest {

    /// Tests for enclosed Hash class
    /**
     * Verify that the hash properly populates a BitSet given a FilterConfig.
     */
    @Test
    public void hashPopulateTest() {
        Hash hash = new Hash(1, 2);
        // n = 1
        // p = 0.091848839 (1 in 11)
        // m = 5 (1B)
        // k = 3
        FilterConfiguration fc = new FilterConfiguration(1, 1.0/11);
        BitSet bs = new BitSet();

        hash.populate(bs, fc);
        // there are 3 iterations so
        // 1 mod 5 = 1
        // 3 mod 5 = 3
        // 5 mod 5 = 0
        // so bits 1,3, and 0 should be on
        assertEquals(3, bs.cardinality());
        assertTrue(bs.get(0));
        assertTrue(bs.get(1));
        assertTrue(bs.get(3));
    }

    @Test
    public void hashHashCodeTest() {
        Hash hash = new Hash(1, 2);
        assertEquals(Objects.hash(1L, 2L), hash.hashCode());
        assertNotEquals(Objects.hash(2L, 1L), hash.hashCode());
    }

    @Test
    public void hashequalsTest() {
        Hash h1 = new Hash(1, 2);
        Hash h2 = new Hash(1, 3);
        Hash h3 = new Hash(2, 2);

        assertTrue(h1.equals(h1));
        assertFalse(h1.equals(h2));
        assertFalse(h1.equals(h3));
    }

    /// ProtoBloomFilter tests

    @Test
    public void compareTo_SingleValueTest() {
        Hash h1 = new Hash(1, 2);
        Hash h2 = new Hash(1, 3);
        ProtoBloomFilter pbf1 = new ProtoBloomFilter(Arrays.asList(h1));
        ProtoBloomFilter pbf2 = new ProtoBloomFilter(Arrays.asList(h2));

        assertEquals(0, pbf1.compareTo(pbf1));
        assertEquals(-1, pbf1.compareTo(pbf2));
        assertEquals(1, pbf2.compareTo(pbf1));
    }

    @Test
    public void compareTo_MultiValueTest() {
        Hash h1 = new Hash(1, 2);
        Hash h2 = new Hash(1, 3);
        Hash h3 = new Hash(2, 3);

        ProtoBloomFilter pbf1 = new ProtoBloomFilter(Arrays.asList(h1, h2));
        ProtoBloomFilter pbf2 = new ProtoBloomFilter(Arrays.asList(h2, h1));

        assertEquals(0, pbf1.compareTo(pbf1));
        // order of arguments is not important
        assertEquals(0, pbf1.compareTo(pbf2));
        assertEquals(0, pbf2.compareTo(pbf1));

        ProtoBloomFilter pbf3 = new ProtoBloomFilter(Arrays.asList(h1, h3));
        assertEquals(-1, pbf1.compareTo(pbf3));
        assertEquals(1, pbf3.compareTo(pbf1));

    }

    @Test
    public void itemCountTest() {
        Hash h1 = new Hash(1, 2);
        Hash h2 = new Hash(1, 3);
        Hash h3 = new Hash(2, 3);

        ProtoBloomFilter pbf1 = new ProtoBloomFilter(Arrays.asList(h1));
        assertEquals(1, pbf1.getItemCount());

        pbf1 = new ProtoBloomFilter(Arrays.asList(h1, h2, h3));
        assertEquals(3, pbf1.getItemCount());

        // verify duplicate is counted
        pbf1 = new ProtoBloomFilter(Arrays.asList(h1, h2, h3, h2));
        assertEquals(4, pbf1.getItemCount());

        // verify duplicate is not counted
        pbf1 = new ProtoBloomFilter(Arrays.asList(h1, h2, h3, h2));
        assertEquals(3, pbf1.getUniqueItemCount());

    }
}
