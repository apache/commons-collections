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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.bloomfilter.BloomFilter.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;
import org.junit.Test;

public class CountingBloomFilterTest extends BloomFilterTest {


    @Override
    protected CountingBloomFilter createFilter(Hasher hasher, Shape shape) {
        return new CountingBloomFilter( hasher, shape );
    }

    @Override
    protected CountingBloomFilter createEmptyFilter(Shape shape) {
        return new CountingBloomFilter( shape );
    }

    @Test
    public void ConstructorTest_HasherValues_CountsTest() {
        List<Integer> lst = Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 );
        Hasher hasher = new StaticHasher( lst.iterator(), shape );

        CountingBloomFilter bf = createFilter(hasher, shape);
        long[] lb = bf.getBits();
        assertEquals(0x1FFFF, lb[0]);
        assertEquals(1, lb.length);


        assertEquals(17, bf.getCounts().count());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).max(Integer::compare).get());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).min(Integer::compare).get());
    }

    @Test
    public void ConstructorTest_CountsTest() {
        List<Integer> lst = Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 );
        Hasher hasher = new StaticHasher( lst.iterator(), shape );

        CountingBloomFilter bf = createFilter(hasher, shape);
        assertEquals(17, bf.getCounts().count());
    }

    @Test
    public void mergeTest_Counts() {
        int[] expected = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0
        };
        List<Integer> lst = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17 );
        Hasher hasher = new StaticHasher( lst.iterator(), shape );

        CountingBloomFilter bf = createFilter(hasher, shape);

        List<Integer> lst2 = Arrays.asList( 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 ,26 ,27 );
        Hasher hasher2 = new StaticHasher( lst2.iterator(), shape );
        BloomFilter bf2 = createFilter(hasher2, shape);

        bf.merge(bf2);

        assertEquals(27, bf.getCounts().count());
        assertEquals(Integer.valueOf(2), bf.getCounts().map(Map.Entry::getValue).max(Integer::compare).get());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).min(Integer::compare).get());

        Map<Integer, Integer> m = new HashMap<Integer, Integer>();
        bf.getCounts().forEach(e -> m.put(e.getKey(), e.getValue()));
        for (int i=0;i<29;i++)
        {
            if (m.get(i) == null)
            {
                assertEquals( "Wrong value for "+i, expected[i], 0 );
            } else
            {
                assertEquals( "Wrong value for "+i, expected[i], m.get(i).intValue());
            }
        }
    }

    @Test
    public void mergeTest_Counts_BitSetFilter() {
        int[] expected = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0
        };
        List<Integer> lst = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17 );
        Hasher hasher = new StaticHasher( lst.iterator(), shape );

        CountingBloomFilter bf = createFilter(hasher, shape);

        List<Integer> lst2 = Arrays.asList( 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 ,26 ,27 );
        Hasher hasher2 = new StaticHasher( lst2.iterator(), shape );
        BloomFilter bf2 = new BitSetBloomFilter(hasher2, shape);

        bf.merge(bf2);

        assertEquals(27, bf.getCounts().count());
        assertEquals(Integer.valueOf(2), bf.getCounts().map(Map.Entry::getValue).max(Integer::compare).get());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).min(Integer::compare).get());

        Map<Integer, Integer> m = new HashMap<Integer, Integer>();
        bf.getCounts().forEach(e -> m.put(e.getKey(), e.getValue()));
        for (int i=0;i<29;i++)
        {
            if (m.get(i) == null)
            {
                assertEquals( "Wrong value for "+i, expected[i], 0 );
            } else
            {
                assertEquals( "Wrong value for "+i, expected[i], m.get(i).intValue());
            }
        }

    }

    @Test
    public void mergeTest_Shape_Hasher_Count() {
        int[] expected = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0
        };

        List<Integer> lst = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17 );
        Hasher hasher = new StaticHasher( lst.iterator(), shape );

        CountingBloomFilter bf = createFilter(hasher, shape);

        List<Integer> lst2 = Arrays.asList( 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 ,26 ,27 );
        Hasher hasher2 = new StaticHasher( lst2.iterator(), shape );

        bf.merge(hasher2);

        assertEquals(27, bf.getCounts().count());
        assertEquals(Integer.valueOf(2), bf.getCounts().map(Map.Entry::getValue).max(Integer::compare).get());
        assertEquals(Integer.valueOf(1), bf.getCounts().map(Map.Entry::getValue).min(Integer::compare).get());

        Map<Integer, Integer> m = new HashMap<Integer, Integer>();
        bf.getCounts().forEach(e -> m.put(e.getKey(), e.getValue()));
        for (int i=0;i<29;i++)
        {
            if (m.get(i) == null)
            {
                assertEquals( "Wrong value for "+i, expected[i], 0 );
            } else
            {
                assertEquals( "Wrong value for "+i, expected[i], m.get(i).intValue());
            }
        }
    }

    @Test
    public void mergeTest_Overflow() {

        List<Integer> lst = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17 );
        Hasher hasher = new StaticHasher( lst.iterator(), shape );

        CountingBloomFilter bf = createFilter(hasher, shape);


        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        bf.getCounts().forEach( e -> map.put( e.getKey(), e.getValue()));
        map.put(1, Integer.MAX_VALUE );

        CountingBloomFilter bf2 = new CountingBloomFilter(map, shape);

        // should not fail
        bf.merge(bf2);

        // try max int on other side of merge.
        bf2 = createFilter(hasher, shape);
        bf = new CountingBloomFilter(map, shape);

        try {
            bf.merge(bf2);
            fail( "Should have thrown IllegalStateException");
        }
        catch (IllegalStateException expected)
        {
            // do nothing
        }
    }

    @Test
    public void removeTest_Standard() {
        int[] values = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1
        };
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        for (int i=1;i<values.length;i++)
        {
            map.put( i, values[i] );
        }

        CountingBloomFilter bf = new CountingBloomFilter( map, shape );

        List<Integer> lst = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17 );
        Hasher hasher = new StaticHasher( lst.iterator(), shape );
        BitSetBloomFilter bf2 = new BitSetBloomFilter( hasher, shape );

        bf.remove( bf2 );
        assertEquals( 17, bf.hammingValue() );
        Map<Integer,Integer> map2 = new HashMap<Integer,Integer>();
        bf.getCounts().forEach( e -> map2.put( e.getKey(), e.getValue()));

        for (int i = 11; i<values.length; i++ )
        {
            assertNotNull( map2.get(i) );
            assertEquals( 1, map2.get(i).intValue());
        }

    }

    @Test
    public void removeTest_Counting() {
        int[] values = {
            0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 2, 2, 2, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1
        };
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        for (int i=1;i<values.length;i++)
        {
            map.put( i, values[i] );
        }

        CountingBloomFilter bf = new CountingBloomFilter( map, shape );

        List<Integer> lst = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17 );
        Hasher hasher = new StaticHasher( lst.iterator(), shape );
        BloomFilter bf2 = new CountingBloomFilter( hasher, shape );

        bf.remove( bf2 );
        assertEquals( 17, bf.hammingValue() );
        Map<Integer,Integer> map2 = new HashMap<Integer,Integer>();
        bf.getCounts().forEach( e -> map2.put( e.getKey(), e.getValue()));

        for (int i = 11; i<values.length; i++ )
        {
            assertNotNull( map2.get(i) );
            assertEquals( 1, map2.get(i).intValue());
        }

    }

    @Test
    public void removeTest_Overflow() {

        List<Integer> lst = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ,16 ,17 );
        Hasher hasher = new StaticHasher( lst.iterator(), shape );

        CountingBloomFilter bf = createFilter(hasher, shape);


        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        bf.getCounts().forEach( e -> map.put( e.getKey(), e.getValue()));
        map.remove(1);

        CountingBloomFilter bf2 = new CountingBloomFilter(map, shape);

        // should not fail
        bf.remove(bf2);

        // try max int on other side of merge.
        bf2 = createFilter(hasher, shape);
        bf = new CountingBloomFilter(map, shape);

        try {
            bf.remove(bf2);
            fail( "Should have thrown IllegalStateException");
        }
        catch (IllegalStateException expected)
        {
            // do nothing
        }
    }

    @Test
    public void andCardinalityTest_CountingBloomFilter() {
        Hasher hasher = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ).iterator(), shape );

        CountingBloomFilter bf = createFilter(hasher, shape);

        Hasher hasher2 = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ).iterator(), shape );
        CountingBloomFilter bf2 = createFilter(hasher2, shape);

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

    @Test
    public void orCardinalityTest_CountingBloomFilter() {
        Hasher hasher = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ).iterator(), shape );

        CountingBloomFilter bf = createFilter(hasher, shape);

        Hasher hasher2 = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ).iterator(), shape );
        CountingBloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals( 10, bf.orCardinality(bf2));
        assertEquals( 10, bf2.orCardinality(bf));

        hasher2 = new StaticHasher( Arrays.asList( 1, 2, 3, 4, 5 ).iterator(), shape );
        bf2 = createFilter(hasher2, shape);

        assertEquals( 10, bf.orCardinality(bf2));
        assertEquals( 10, bf2.orCardinality(bf));

        hasher2 = new StaticHasher( Arrays.asList( 11, 12, 13, 14, 15 ).iterator(), shape );
        bf2 = createFilter(hasher2, shape);

        assertEquals( 15, bf.orCardinality(bf2));
        assertEquals( 15, bf2.orCardinality(bf));

    }
}
