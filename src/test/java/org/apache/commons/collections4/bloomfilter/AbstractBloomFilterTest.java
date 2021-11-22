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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.HasherCollection;
import org.apache.commons.collections4.bloomfilter.hasher.SimpleHasher;
import org.junit.jupiter.api.Test;

/**
 * Test standard methods in the {@link BloomFilter} interface.
 */
public abstract class AbstractBloomFilterTest<T extends BloomFilter> {

    protected final SimpleHasher from1 = new SimpleHasher(1, 1);
    protected final long from1Value = 0x3FFFEL;
    protected final SimpleHasher from11 = new SimpleHasher(11, 1);
    protected final long from11Value = 0xFFFF800L;
    protected final HasherCollection bigHasher = new HasherCollection(from1, from11);
    protected final long bigHashValue = 0xFFFFFFEL;
    protected final HasherCollection fullHasher = new HasherCollection(new SimpleHasher(0, 1)/* 0-16 */,
            new SimpleHasher(17, 1)/* 17-33 */, new SimpleHasher(33, 1)/* 33-49 */, new SimpleHasher(50, 1)/* 50-66 */,
            new SimpleHasher(67, 1)/* 67-83 */
    );
    protected final long[] fullHashValue = { 0xFFFFFFFFFFFFFFFFL, 0xFFFFFL };

    /**
     * The shape of the Bloom filters for testing
     */
    protected Shape shape = new Shape(17, 72);

    /**
     * Create an empty version of the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @return a BloomFilter implementation.
     */
    protected abstract T createEmptyFilter(Shape shape);

    /**
     * Create the BloomFilter implementation we are testing.
     *
     * @param hasher the hasher to use to create the filter.
     * @param shape the shape of the filter.
     * @return a BloomFilter implementation.
     */
    protected abstract T createFilter(Shape shape, Hasher hasher);

    /**
     * Tests that the andCardinality calculations are correct.
     *
     * @param filterFactory the factory function to create the filter
     */
    @Test
    public void containsTest() {
        final BloomFilter bf = createFilter(shape, from1);
        final BloomFilter bf2 = createFilter(shape, bigHasher);

        assertTrue("BF Should contain itself", bf.contains(bf));
        assertTrue("BF2 Should contain itself", bf2.contains(bf2));
        assertFalse("BF should not contain BF2", bf.contains(bf2));
        assertTrue("BF2 should contain BF", bf2.contains(bf));
    }

    @Test
    public void containsTest_Hasher() {
        final BloomFilter bf = createFilter(shape, bigHasher);

        assertTrue("BF Should contain this hasher", bf.contains(new SimpleHasher(1, 1)));
        assertFalse("BF Should not contain this hasher", bf.contains(new SimpleHasher(1, 3)));
    }

    /**
     * Tests that the andCardinality calculations are correct.
     *
     * @param filterFactory the factory function to create the filter
     */
    @Test
    public void estimateIntersectionTest() {

        final BloomFilter bf = createFilter(shape, from1);
        final BloomFilter bf2 = createFilter(shape, bigHasher);

        assertEquals(1.0, bf.estimateIntersection(bf2));
        assertEquals(1.0, bf2.estimateIntersection(bf));
    }

    @Test
    public void estimateIntersectionTest_empty() {
        final BloomFilter bf = createFilter(shape, from1);
        final BloomFilter bf2 = createEmptyFilter(shape);

        assertEquals(0.0, bf.estimateIntersection(bf2));
        assertEquals(0.0, bf2.estimateIntersection(bf));
    }

    /**
     * Tests that the andCardinality calculations are correct.
     *
     * @param filterFactory the factory function to create the filter
     */
    @Test
    public void estimateUnionTest() {
        final BloomFilter bf = createFilter(shape, from1);

        final BloomFilter bf2 = createFilter(shape, from11);

        assertEquals(2.0, bf.estimateUnion(bf2));
        assertEquals(2.0, bf2.estimateUnion(bf));
    }

    @Test
    public void estimateUnionTest_empty() {
        final BloomFilter bf = createFilter(shape, from1);
        final BloomFilter bf2 = createEmptyFilter(shape);

        assertEquals(1.0, bf.estimateUnion(bf2));
        assertEquals(1.0, bf2.estimateUnion(bf));
    }

    /**
     * Tests that the size estimate is correctly calculated.
     */
    @Test
    public void estimateNTest() {
        // build a filter
        BloomFilter filter1 = new SimpleBloomFilter(shape, from1);
        assertEquals(1, filter1.estimateN());

        // the data provided above do not generate an estimate that is equivalent to the
        // actual.
        filter1.mergeInPlace(new SimpleHasher(4, 1));

        assertEquals(1, filter1.estimateN());

        filter1.mergeInPlace(new SimpleHasher(17, 1));

        assertEquals(3, filter1.estimateN());
    }

    /**
     * Tests that creating an empty hasher works as expected.
     */
    @Test
    public final void constructorTest_Empty() {

        final BloomFilter bf = createEmptyFilter(shape);
        final long[] lb = BloomFilter.asBitMapArray(bf);
        assertEquals(0, lb.length);
    }

    /**
     * Tests that creating a filter with a hasher works as expected.
     */
    @Test
    public final void constructorTest_Hasher() {
        Hasher hasher = new SimpleHasher(0, 1);

        final BloomFilter bf = createFilter(shape, hasher);
        final long[] lb = BloomFilter.asBitMapArray(bf);
        assertEquals(0x1FFFF, lb[0]);
        assertEquals(1, lb.length);
    }

    /**
     * Tests that getBits() works correctly when multiple long values are returned.
     */
    @Test
    public final void getBitsTest_SpanLong() {

        final SimpleHasher hasher = new SimpleHasher(63, 1);
        final BloomFilter bf = createFilter(new Shape(2, 72), hasher);
        final long[] lb = BloomFilter.asBitMapArray(bf);
        assertEquals(2, lb.length);
        assertEquals(0x8000000000000000L, lb[0]);
        assertEquals(0x1, lb[1]);
    }

    /**
     * Tests that isFull() returns the proper values.
     */
    @Test
    public final void isFullTest() {

        // create empty filter
        BloomFilter filter = createEmptyFilter(shape);
        assertFalse("Should not be full", filter.isFull(shape));

        filter = createFilter(shape, fullHasher);
        assertTrue("Should be full", filter.isFull(shape));

        filter = createFilter(shape, new SimpleHasher(1, 3));
        assertFalse("Should not be full", filter.isFull(shape));
    }

    /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
    @Test
    public final void mergeTest_Bloomfilter() {

        final BloomFilter bf1 = createFilter(shape, from1);

        final BloomFilter bf2 = createFilter(shape, from11);

        final BloomFilter bf3 = bf1.merge(bf2);
        assertTrue("Should contain", bf3.contains(bf1));
        assertTrue("Should contain", bf3.contains(bf2));

        final BloomFilter bf4 = bf2.merge(bf1);
        assertTrue("Should contain", bf4.contains(bf1));
        assertTrue("Should contain", bf4.contains(bf2));
        assertTrue("Should contain", bf4.contains(bf3));
        assertTrue("Should contain", bf3.contains(bf4));
    }

    @Test
    public final void mergeTest_Hasher() {

        final BloomFilter bf1 = createFilter(shape, from1);
        final BloomFilter bf2 = createFilter(shape, from11);

        final BloomFilter bf3 = bf1.merge(from11);
        assertTrue("Should contain", bf3.contains(bf1));
        assertTrue("Should contain", bf3.contains(bf2));
    }

    /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
    @Test
    public final void mergeInPlaceTest_Bloomfilter() {

        final BloomFilter bf1 = createFilter(shape, from1);

        final BloomFilter bf2 = createFilter(shape, from11);

        final BloomFilter bf3 = bf1.merge(bf2);

        bf1.mergeInPlace(bf2);

        assertTrue("Should contain", bf1.contains(bf2));
        assertTrue("Should contain", bf1.contains(bf3));

    }

    @Test
    public final void mergeInPlaceTest_Hasher() {

        final BloomFilter bf1 = createFilter(shape, from1);

        final BloomFilter bf2 = createFilter(shape, from11);

        final BloomFilter bf3 = bf1.merge(bf2);

        bf1.mergeInPlace(from11);

        assertTrue("Should contain Bf2", bf1.contains(bf2));
        assertTrue("Should contain Bf3", bf1.contains(bf3));
    }

}
