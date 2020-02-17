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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.PrimitiveIterator.OfInt;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;
import org.junit.Test;

/**
 * Test standard methods in the {@link BloomFilter} interface.
 */
public abstract class AbstractBloomFilterTest {

    /**
     * A HashFunctionIdentity for testing.
     */
    protected HashFunctionIdentity testFunction = new HashFunctionIdentity() {

        @Override
        public String getName() {
            return "Test Function";
        }

        @Override
        public ProcessType getProcessType() {
            return ProcessType.CYCLIC;
        }

        @Override
        public String getProvider() {
            return "Apache Commons Collection Tests";
        }

        @Override
        public long getSignature() {
            return 0;
        }

        @Override
        public Signedness getSignedness() {
            return Signedness.SIGNED;
        }
    };

    /**
     * A second HashFunctionIdentity for testing.
     */
    protected HashFunctionIdentity testFunctionX = new HashFunctionIdentity() {

        @Override
        public String getName() {
            return "Test FunctionX";
        }

        @Override
        public ProcessType getProcessType() {
            return ProcessType.CYCLIC;
        }

        @Override
        public String getProvider() {
            return "Apache Commons Collection Tests";
        }

        @Override
        public long getSignature() {
            return 1;
        }

        @Override
        public Signedness getSignedness() {
            return Signedness.SIGNED;
        }
    };

    /**
     * The shape of the Bloom filters for testing
     */
    protected Shape shape = new Shape(testFunction, 3, 72, 17);

    /**
     * Tests that the andCardinality calculations are correct.
     */
    @Test
    public final void andCardinalityTest() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);

        final BloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals(7, bf.andCardinality(bf2));
    }

    /**
     * Tests that the andCardinality calculations are correct when there are more than Long.LENGTH bits.
     */
    @Test
    public final void andCardinalityTest_ExtraLongs() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);

        final BloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals(7, bf.andCardinality(bf2));
        assertEquals(7, bf2.andCardinality(bf));
    }

    /**
     * Compare 2 static hashers to verify they have the same bits enabled.
     *
     * @param hasher1 the first static hasher.
     * @param hasher2 the second static hasher.
     */
    private void assertSameBits(final StaticHasher hasher1, final StaticHasher hasher2) {
        final OfInt iter1 = hasher1.getBits(shape);
        final OfInt iter2 = hasher2.getBits(shape);

        while (iter1.hasNext()) {
            assertTrue("Not enough data in second hasher", iter2.hasNext());
            assertEquals(iter1.nextInt(), iter2.nextInt());
        }
        assertFalse("Too much data in second hasher", iter2.hasNext());
    }

    /**
     * Tests that cardinality is correct.
     */
    @Test
    public final void cardinalityTest() {

        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);
        assertEquals(17, bf.cardinality());
    }

    /**
     * Tests that creating an empty hasher works as expected.
     */
    @Test
    public final void constructorTest_Empty() {

        final BloomFilter bf = createEmptyFilter(shape);
        final long[] lb = bf.getBits();
        assertEquals(0, lb.length);
    }

    /**
     * Tests that creating a filter with a hasher works as expected.
     */
    @Test
    public final void constructorTest_Hasher() {
        final List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);
        final long[] lb = bf.getBits();
        assertEquals(0x1FFFF, lb[0]);
        assertEquals(1, lb.length);
    }

    /**
     * Tests that creating a Bloom filter with a Static hasher that has one shape and a
     * different specified shape fails.
     */
    @Test
    public final void constructorTest_WrongShape() {
        final Shape anotherShape = new Shape(testFunctionX, 3, 72, 17);

        final List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        final Hasher hasher = new StaticHasher(lst.iterator(), anotherShape);
        try {
            createFilter(hasher, shape);
            fail("Should throw IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
    }

    /**
     * Tests that contains() with a Bloom filter argument returns the proper results.
     */
    @Test
    public final void containsTest_BloomFilter() {
        final List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(4, 5, 6, 7, 8, 9, 10);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        final BloomFilter bf2 = createFilter(hasher2, shape);
        assertTrue(bf.contains(bf2));
        assertFalse(bf2.contains(bf));
    }

    /**
     * Tests that contains() fails properly if the other Bloom filter is not of the proper shape.
     */
    @Test
    public final void containsTest_BloomFilter_WrongShape() {
        final List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter bf = createFilter(hasher, shape);

        final Shape anotherShape = new Shape(testFunctionX, 3, 72, 17);
        final Hasher hasher2 = new StaticHasher(lst.iterator(), anotherShape);
        final BloomFilter bf2 = createFilter(hasher2, anotherShape);
        try {
            bf.contains(bf2);
            fail("Should throw IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
    }

    /**
     * Tests that contains() with a Hasher argument returns the proper results.
     */
    @Test
    public final void containsTest_Hasher() {
        final List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter bf = createFilter(hasher, shape);

        List<Integer> lst2 = Arrays.asList(4, 5, 6, 7, 8, 9, 10);
        Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        assertTrue(bf.contains(hasher2));

        lst2 = Arrays.asList(17, 18, 19, 20);
        hasher2 = new StaticHasher(lst2.iterator(), shape);
        assertFalse(bf.contains(hasher2));

        lst2 = Arrays.asList(10, 11, 12, 17, 18, 19, 20);
        hasher2 = new StaticHasher(lst2.iterator(), shape);
        assertFalse(bf.contains(hasher2));
    }

    /**
     * Tests that contains() fails properly if the hasher is not of the proper shape.
     */
    @Test
    public final void containsTest_Hasher_WrongShape() {
        final List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter bf = createFilter(hasher, shape);

        final Shape anotherShape = new Shape(testFunctionX, 3, 72, 17);

        final List<Integer> lst2 = Arrays.asList(4, 5, 6, 7, 8, 9, 10);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), anotherShape);
        try {
            bf.contains(hasher2);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing
        }
    }

    /**
     * Create an empty version of the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @return a BloomFilter implementation.
     */
    protected abstract AbstractBloomFilter createEmptyFilter(Shape shape);

    /**
     * Create the BloomFilter implementation we are testing.
     *
     * @param hasher the hasher to use to create the filter..
     * @param shape the shape of the filter.
     * @return a BloomFilter implementation.
     */
    protected abstract AbstractBloomFilter createFilter(Hasher hasher, Shape shape);

    /**
     * Tests that getBits() works correctly when multiple long values are returned.
     */
    @Test
    public final void getBitsTest_SpanLong() {
        final List<Integer> lst = Arrays.asList(63, 64);
        final StaticHasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter bf = createFilter(hasher, shape);
        final long[] lb = bf.getBits();
        assertEquals(2, lb.length);
        assertEquals(0x8000000000000000L, lb[0]);
        assertEquals(0x1, lb[1]);
    }

    /**
     * Tests that the the hasher returned from getHasher() works correctly.
     */
    @Test
    public final void getHasherTest() {
        final List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        final StaticHasher hasher = new StaticHasher(lst.iterator(), shape);
        final BloomFilter bf = createFilter(hasher, shape);

        final StaticHasher hasher2 = bf.getHasher();

        assertEquals(shape, hasher2.getShape());
        assertSameBits(hasher, hasher2);
    }

    /**
     * Tests that isFull() returns the proper values.
     */
    @Test
    public final void isFullTest() {

        // create empty filter
        AbstractBloomFilter filter = createEmptyFilter(shape);
        assertFalse(filter.isFull());

        final List<Integer> values = new ArrayList<>(shape.getNumberOfBits());
        for (int i = 0; i < shape.getNumberOfBits(); i++) {
            values.add(i);
        }

        StaticHasher hasher2 = new StaticHasher(values.iterator(), shape);
        filter = createFilter(hasher2, shape);

        assertTrue(filter.isFull());

        final int mid = shape.getNumberOfBits() / 2;
        values.remove(Integer.valueOf(mid));
        hasher2 = new StaticHasher(values.iterator(), shape);
        filter = createFilter(hasher2, shape);
        assertFalse(filter.isFull());
    }

    /**
     * Tests that merging bloom filters works as expected.
     */
    @Test
    public final void mergeTest_BloomFilter() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        final BloomFilter bf2 = createFilter(hasher2, shape);

        bf.merge(bf2);
        assertEquals(27, bf.cardinality());
    }

    /**
     * Tests that merging bloom filters with different shapes fails properly
     */
    @Test
    public final void mergeTest_BloomFilter_WrongShape() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);

        final Shape anotherShape = new Shape(testFunctionX, 3, 72, 17);
        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), anotherShape);
        final BloomFilter bf2 = createFilter(hasher2, anotherShape);

        try {
            bf.merge(bf2);
            fail("Should throw IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
    }

    /**
     * Tests that merging a hasher into a Bloom filter works as expected
     */
    @Test
    public final void mergeTest_Hasher() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);

        bf.merge(hasher2);
        assertEquals(27, bf.cardinality());
    }

    /**
     * Tests that merging a static hasher with the wrong shape into a Bloom filter fails as expected
     */
    @Test
    public final void mergeTest_Hasher_WrongShape() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);

        final Shape anotherShape = new Shape(testFunctionX, 3, 72, 17);
        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), anotherShape);

        try {
            bf.merge(hasher2);
            fail("Should throw IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
    }

    /**
     * Tests that the orCardinality calculations are correct.
     */
    @Test
    public final void orCardinalityTest() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final AbstractBloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);

        final BloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals(27, bf.orCardinality(bf2));
    }

    /**
     * Tests that the orCardinality calculations are correct when there are more than Long.LENGTH bits.
     */
    @Test
    public final void orCardinalityTest_ExtraLongs() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final AbstractBloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);

        final AbstractBloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals(27, bf.orCardinality(bf2));
        assertEquals(27, bf2.orCardinality(bf));
    }

    /**
     * Tests that the zorCardinality calculations are correct.
     */
    @Test
    public final void xorCardinalityTest() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        final BloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals(20, bf.xorCardinality(bf2));
    }

    /**
     * Tests that the xorCardinality calculations are correct when there are more than Long.LENGTH bits.
     */
    @Test
    public final void xorCardinalityTest_ExtraLongs() {
        final List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final Hasher hasher = new StaticHasher(lst.iterator(), shape);

        final BloomFilter bf = createFilter(hasher, shape);

        final List<Integer> lst2 = Arrays.asList(11, 12, 13, 14, 15, 16, 17, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69);
        final Hasher hasher2 = new StaticHasher(lst2.iterator(), shape);
        final BloomFilter bf2 = createFilter(hasher2, shape);

        assertEquals(20, bf.xorCardinality(bf2));
        assertEquals(20, bf2.xorCardinality(bf));
    }
}
