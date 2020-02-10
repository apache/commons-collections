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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;
import org.junit.Test;

/**
 * Tests for the BitSetBloomFilter implementation.
 *
 */
public class BitSetBloomFilterTest extends AbstractBloomFilterTest {

    @Override
    protected BitSetBloomFilter createFilter(final Hasher hasher, final Shape shape) {
        return new BitSetBloomFilter( hasher, shape );
    }

    @Override
    protected BitSetBloomFilter createEmptyFilter(final Shape shape) {
        return new BitSetBloomFilter( shape );
    }

    /**
     * Test that andCardinality works for BitSetBloomFilter arguments.
     */
    @Test
    public void andCardinalityTest_BitSetBloomFilter() {
        final Hasher hasher = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ).iterator(), shape );

        final BitSetBloomFilter bf = createFilter(hasher, shape);

        Hasher hasher2 = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ).iterator(), shape );
        BitSetBloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals( 10, bf.andCardinality(bf2));
        assertEquals( 10, bf2.andCardinality(bf));

        hasher2 = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5 ).iterator(), shape );
        bf2 = createFilter(hasher2, shape);

        assertEquals( 5, bf.andCardinality(bf2));
        assertEquals( 5, bf2.andCardinality(bf));

        hasher2 = new StaticHasher( Arrays.asList( 11, 12, 13, 14, 15 ).iterator(), shape );
        bf2 = createFilter(hasher2, shape);
        assertEquals( 0, bf.andCardinality(bf2));
        assertEquals( 0, bf2.andCardinality(bf));


    }

    /**
     * Test that xorCardinality works for BitSetBloomFilter arguments.
     */
    @Test
    public void xorCardinalityTest_BitSetBloomFilter() {
        final Hasher hasher = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ).iterator(), shape );

        final BitSetBloomFilter bf = createFilter(hasher, shape);

        Hasher hasher2 = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ).iterator(), shape );
        BitSetBloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals( 0, bf.xorCardinality(bf2));
        assertEquals( 0, bf2.xorCardinality(bf));

        hasher2 = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5 ).iterator(), shape );
        bf2 = createFilter(hasher2, shape);

        assertEquals( 5, bf.xorCardinality(bf2));
        assertEquals( 5, bf2.xorCardinality(bf));

        hasher2 = new StaticHasher( Arrays.asList( 11, 12, 13, 14, 15 ).iterator(), shape );
        bf2 = createFilter(hasher2, shape);
        assertEquals( 15, bf.xorCardinality(bf2));
        assertEquals( 15, bf2.xorCardinality(bf));


    }

    /**
     * Test that merge() works for BitSetBloomFilter arguments.
     */
    @Test
    public void mergeTest_BitSetBloomFilter() {

        final List<Integer> lst = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17 );
        final Hasher hasher = new StaticHasher( lst.iterator(), shape );

        final BitSetBloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList( 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 ,26 ,27 );
        final Hasher hasher2 = new StaticHasher( lst2.iterator(), shape );
        final BloomFilter bf2 = new BitSetBloomFilter(hasher2, shape);

        bf.merge(bf2);

        assertEquals(27, bf.cardinality());


    }


}
