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
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;
import java.util.Objects;

import org.apache.commons.collections4.bloomfilter.BloomFilterConfiguration;
import org.apache.commons.collections4.bloomfilter.BloomFilterConfiguration;
import org.junit.Test;

public class BloomFilterConfigurationTest {

    /*
     * values from https://hur.st/bloomfilter/?n=5&p=.1&m=&k=
     *
     * n = 5
     *
     * p = 0.100375138 (1 in 10)
     *
     * m = 24 (3B)
     *
     * k = 3
     */
    BloomFilterConfiguration filterConfig = new BloomFilterConfiguration(5, 0.1);

    @Test
    public void constructor_items_probability_Test() {

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.100375138, filterConfig.getProbability(), 0.000001);

    }

    @Test
    public void constructor_items_probability_NumberOfBitsOverflowTest() {
        try {
            new BloomFilterConfiguration(Integer.MAX_VALUE, 1.0 / 10);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructor_items_probability_BadNumberOfItemsTest() {
        try {
            new BloomFilterConfiguration(0, 1.0 / 10);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructor_items_probability_BadProbabilityTest() {
        try {
            new BloomFilterConfiguration(10, 0.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }

        try {
            new BloomFilterConfiguration(10, 1.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructor_items_bitsTest() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24
         */
        BloomFilterConfiguration filterConfig = new BloomFilterConfiguration(5, 24);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.100375138, filterConfig.getProbability(), 0.000001);

    }

    @Test
    public void constructor_items_bits_BadNumberOfItemsTest() {
        try {
            new BloomFilterConfiguration(0, 24);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_BadNumberOfBitsTest() {
        try {
            new BloomFilterConfiguration(5, 6);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_BadNumberOfHashFunctionsTest() {
        try {
            new BloomFilterConfiguration(16,8);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_hashTest() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24&k=4
         */
        BloomFilterConfiguration filterConfig = new BloomFilterConfiguration(5, 24, 4);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(4, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.102194782, filterConfig.getProbability(), 0.000001);

    }

    @Test
    public void constructor_items_bits_hash_BadNumberOfItemsTest() {
        try {
            new BloomFilterConfiguration(0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_hash_BadNumberOfBitsTest() {
        try {
            new BloomFilterConfiguration(5, 6, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_hash_BadNumberOfHashFunctionsTest() {
        try {
            new BloomFilterConfiguration(5, 24, 0);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }


    @Test
    public void constructor_items_bits_hash_BadProbabilityTest() {
        try {
            new BloomFilterConfiguration(4000,8,1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_probability_bits_hashTest() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&p=.1&m=&k=
         */
        BloomFilterConfiguration filterConfig = new BloomFilterConfiguration(0.1, 24, 3);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.100375138, filterConfig.getProbability(), 0.000001);
    }

    @Test
    public void constructor__probability_bits_hash_BadProbabilityTest() {
        try {
            new BloomFilterConfiguration(0.0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }

        try {
            new BloomFilterConfiguration(-1.0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
        try {
            new BloomFilterConfiguration(1.0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }

        try {
            new BloomFilterConfiguration(2.0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor__probability_bits_hash__BadNumberOfBitsTest() {
        try {
            new BloomFilterConfiguration(0.5, 6, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_probability_bits_hash_BadNumberOfHashFunctionsTest() {
        try {
            new BloomFilterConfiguration(0.5, 24, 0);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void equalsTest() {

        assertEquals(new BloomFilterConfiguration(5, 1.0 / 10), filterConfig);
        assertNotEquals(new BloomFilterConfiguration(5, 1.0 / 11), filterConfig);
        assertNotEquals(new BloomFilterConfiguration(4, 1.0 / 10), filterConfig);
        assertFalse(filterConfig.equals(filterConfig.toString()));

    }

    @Test
    public void hashCodeTest() {
        int hashCode = Objects.hash(24, 3, 5, 1.0 / 10);

        assertEquals(hashCode, filterConfig.hashCode());
        // forces path throgh previously calcualted hashCode
        assertNotEquals(hashCode + 1, filterConfig.hashCode());

    }

    @Test
    public void estimateSizeTest() {
        // build a filter
        ProtoBloomFilter proto1 = ProtoBloomFilter.builder().with("Hello").build("World");
        BloomFilter filter1 = new StandardBloomFilter(proto1, filterConfig);
        long estimate = filterConfig.estimateSize(filter1);
        // the data provided above do not generate an estimate that is equivalent to the
        // actual.
        assertEquals(1, estimate);

        ProtoBloomFilter proto2 = ProtoBloomFilter.builder().with("Goodbyte").with("Cruel").build("World");
        BloomFilter filter2 = new StandardBloomFilter(proto2, filterConfig);
        estimate = filterConfig.estimateSize(filter2);
        assertEquals(3, estimate);

        BloomFilter filter3 = filter1.merge(filter2);
        estimate = filterConfig.estimateSize(filter3);
        assertEquals(3, estimate);
    }

    @Test
    public void estimateUnionSizeTest() {
        // build a filter
        ProtoBloomFilter proto1 = ProtoBloomFilter.builder().with("Hello").build("World");
        BloomFilter filter1 = new StandardBloomFilter(proto1, filterConfig);

        ProtoBloomFilter proto2 = ProtoBloomFilter.builder().with("Goodbyte").with("Cruel").build("World");
        BloomFilter filter2 = new StandardBloomFilter(proto2, filterConfig);
        long estimate = filterConfig.estimateUnionSize(filter1, filter2);
        assertEquals(3, estimate);

    }

    @Test
    public void estimateIntersectionSizeTest() {
        // build a filter
        ProtoBloomFilter proto1 = ProtoBloomFilter.builder().with("Hello").build("World");
        BloomFilter filter1 = new StandardBloomFilter(proto1, filterConfig);

        ProtoBloomFilter proto2 = ProtoBloomFilter.builder().with("Goodbyte").with("Cruel").build("World");
        BloomFilter filter2 = new StandardBloomFilter(proto2, filterConfig);
        long estimate = filterConfig.estimateIntersectionSize(filter1, filter2);
        assertEquals(1, estimate);

    }
}
