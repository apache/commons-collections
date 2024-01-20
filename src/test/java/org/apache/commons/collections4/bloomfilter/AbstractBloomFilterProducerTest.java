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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BiPredicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractBloomFilterProducerTest {
    private Shape shape = Shape.fromKM(17, 72);

    BloomFilter one = new SimpleBloomFilter(shape);
    BloomFilter two = new SimpleBloomFilter(shape);
    int[] nullCount = { 0, 0 };
    int[] equalityCount = { 0 };
    BiPredicate<BloomFilter, BloomFilter> counter = (x, y) -> {
        if (x == null) {
            nullCount[0]++;
        }
        if (y == null) {
            nullCount[1]++;
        }
        if (x != null && y != null && x.cardinality() == y.cardinality()) {
            equalityCount[0]++;
        }
        return true;
    };

    private BloomFilterProducer createUnderTest() {
        return createUnderTest(one, two);
    }

    /**
     * Creates a BloomFilterProducer that returns the filters (or their copy) in the order presented.
     * @param filters The filters to return.
     * @return A BloomFilterProducer that returns the filters in order.
     */
    protected abstract BloomFilterProducer createUnderTest(BloomFilter... filters);

    /**
     * The shape of the Bloom filters for testing.
     * <ul>
     *  <li>Hash functions (k) = 17
     *  <li>Number of bits (m) = 72
     * </ul>
     * @return the testing shape.
     */
    protected Shape getTestShape() {
        return shape;
    }

    @BeforeEach
    public void setup() {
        one.clear();
        one.merge(IndexProducer.fromIndexArray(1));
        two.clear();
        two.merge(IndexProducer.fromIndexArray(2, 3));
        nullCount[0] = 0;
        nullCount[1] = 0;
        equalityCount[0] = 0;
    }

    @Test
    public void testAsBloomFilterArray() {
        BloomFilter[] result = createUnderTest().asBloomFilterArray();
        assertEquals(2, result.length);
        assertEquals(1, result[0].cardinality());
        assertEquals(2, result[1].cardinality());
    }

    @Test
    public void testFlatten() {
        BloomFilter underTest = createUnderTest().flatten();
        BloomFilter expected = new SimpleBloomFilter(shape);
        expected.merge(IndexProducer.fromIndexArray(1, 2, 3));
        assertArrayEquals(expected.asBitMapArray(), underTest.asBitMapArray());
    }

    @Test
    public void testForEachPairArrayTooLong() {
        assertTrue(createUnderTest().forEachBloomFilterPair(BloomFilterProducer.fromBloomFilterArray(one, two, one),
                counter));
        assertEquals(1, nullCount[0]);
        assertEquals(0, nullCount[1]);
        assertEquals(2, equalityCount[0]);
    }

    @Test
    public void testForEachPairArrayTooShort() {
        assertTrue(createUnderTest().forEachBloomFilterPair(BloomFilterProducer.fromBloomFilterArray(one), counter));
        assertEquals(0, nullCount[0]);
        assertEquals(1, nullCount[1]);
        assertEquals(1, equalityCount[0]);
    }

    @Test
    public void testForEachPairCompleteMatch() {
        assertTrue(createUnderTest().forEachBloomFilterPair(createUnderTest(), counter));
        assertArrayEquals(new int[] { 0, 0 }, nullCount);
        assertEquals(2, equalityCount[0]);
    }

    @Test
    public void testForEachPairReturnFalseEarly() {
        assertFalse(createUnderTest().forEachBloomFilterPair(BloomFilterProducer.fromBloomFilterArray(one, two, one),
                (x, y) -> false));
    }

    @Test
    public void testForEachPairReturnFalseLate() {
        assertFalse(createUnderTest().forEachBloomFilterPair(BloomFilterProducer.fromBloomFilterArray(one, two, one),
                counter.and((x, y) -> x != null && y != null)));
        assertEquals(1, nullCount[0]);
        assertEquals(0, nullCount[1]);
        assertEquals(2, equalityCount[0]);
    }

    @Test
    public void testForEachPairReturnFalseLateShortArray() {
        assertFalse(createUnderTest().forEachBloomFilterPair(BloomFilterProducer.fromBloomFilterArray(one),
                counter.and((x, y) -> x != null && y != null)));
        assertEquals(0, nullCount[0]);
        assertEquals(1, nullCount[1]);
        assertEquals(1, equalityCount[0]);
    }
}
