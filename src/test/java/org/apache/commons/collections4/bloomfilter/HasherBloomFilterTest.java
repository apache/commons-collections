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

import org.apache.commons.collections4.bloomfilter.hasher.DynamicHasher;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.function.MD5Cyclic;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.PrimitiveIterator.OfInt;

/**
 * Tests the {@link HasherBloomFilter}.
 */
public class HasherBloomFilterTest extends AbstractBloomFilterTest {

    /**
     * Tests that the constructor works correctly.
     */
    @Test
    public void constructorTest_NonStatic() {
        final Shape shape = new Shape(new MD5Cyclic(), 3, 72, 17);
        final DynamicHasher hasher = new DynamicHasher.Builder(new MD5Cyclic()).with("Hello").build();
        final HasherBloomFilter filter = createFilter(hasher, shape);
        final long[] lb = filter.getBits();
        assertEquals(2, lb.length);
        assertEquals(0x6203101001888c44L, lb[0]);
        assertEquals(0x60L, lb[1]);
    }

    @Override
    protected AbstractBloomFilter createEmptyFilter(final Shape shape) {
        return new HasherBloomFilter(shape);
    }

    @Override
    protected HasherBloomFilter createFilter(final Hasher hasher, final Shape shape) {
        return new HasherBloomFilter(hasher, shape);
    }

    /**
     * Test the edge case where the filter is empty and the getBits() function returns a
     * zero length array.
     */
    @Test
    public void getBitsTest_Empty() {
        BloomFilter filter = createEmptyFilter(shape);
        Assert.assertArrayEquals(new long[0], filter.getBits());
    }

    /**
     * Test the edge case where the filter has only 1 bit in the lowest index and the getBits()
     * function returns an array of length 1.
     */
    @Test
    public void getBitsTest_LowestBitOnly() {
        BloomFilter filter = createEmptyFilter(shape);
        // Set the lowest bit index only.
        filter.merge(new Hasher() {
            @Override
            public OfInt getBits(Shape shape) {
                return Arrays.stream(new int[] {0}).iterator();
            }

            @Override
            public HashFunctionIdentity getHashFunctionIdentity() {
                return shape.getHashFunctionIdentity();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });
        Assert.assertArrayEquals(new long[] {1L}, filter.getBits());
    }
}
