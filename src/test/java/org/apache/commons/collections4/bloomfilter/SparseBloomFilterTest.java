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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongPredicate;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link SparseBloomFilter}.
 */
public class SparseBloomFilterTest extends AbstractBloomFilterTest<SparseBloomFilter> {
    @Override
    protected SparseBloomFilter createEmptyFilter(final Shape shape) {
        return new SparseBloomFilter(shape);
    }

    @Override
    protected SparseBloomFilter createFilter(final Shape shape, final Hasher hasher) {
        return new SparseBloomFilter(shape, hasher);
    }

    @Test
    public void testSparseBloomFilterSpecificConstructor() {
        Shape shape = Shape.fromKM(1, 5);
        List<Integer> lst = new ArrayList<>();

     // test no values

        new SparseBloomFilter(shape, lst);
        // test index out of range

        lst.add(5);
        assertThrows(IllegalArgumentException.class, () -> new SparseBloomFilter(shape, lst));

        lst.clear();
        lst.add(-1);
        assertThrows(IllegalArgumentException.class, () -> new SparseBloomFilter(shape, lst));


    }

    private void assertConstructor(Shape shape, int[] values, int[] expected) {
        IndexProducer indices = IndexProducer.fromIntArray(values);
        SparseBloomFilter filter = new SparseBloomFilter(shape, indices);
        List<Integer> lst = new ArrayList<>();
        filter.forEachIndex(x -> {
            lst.add(x);
            return true;
        });
        assertEquals(expected.length, lst.size());
        for (int value : expected) {
            assertTrue(lst.contains(Integer.valueOf(value)), "Missing " + value);
        }
    }

    private void assertFailedConstructor(Shape shape, int[] values) {
        IndexProducer indices = IndexProducer.fromIntArray(values);
        assertThrows(IllegalArgumentException.class, () -> new SparseBloomFilter(shape, indices));
    }

    @Test
    public void producer_constructor() {
        Shape shape = Shape.fromKM(5, 10);

        assertConstructor( shape, new int[] { 0, 2, 4, 6, 8 }, new int[] {0,2,4,6,8 } );
        // test duplicate values
        assertConstructor( shape, new int[] { 0, 2, 4, 2, 8 }, new int[] {0,2,4,8 } );
       // test negative values
        assertFailedConstructor( shape, new int[] { 0, 2, 4, -2, 8 } );
        // test index too large
        assertFailedConstructor( shape, new int[] { 0, 2, 4, 12, 8 } );
        // test no indicies
        assertConstructor( shape, new int[0], new int[0] );
    }

    @Test
    public void testForEachBitMapEarlyExit() {

        Shape shape = Shape.fromKM(5, 100);
        IndexProducer indices = IndexProducer.fromIntArray(new int[] { 66, 67 });
        BloomFilter filter = new SparseBloomFilter(shape, indices);
        EarlyExitTestPredicate consumer = new EarlyExitTestPredicate();
        assertFalse(filter.forEachBitMap(consumer));
        assertEquals( 1, consumer.passes );
    }

    class EarlyExitTestPredicate implements LongPredicate {
        int passes = 0;

        @Override
        public boolean test(long arg0) {
            passes++;
            return false;
        }
    }

}
