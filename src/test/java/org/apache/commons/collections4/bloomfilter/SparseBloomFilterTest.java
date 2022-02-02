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
    public void constructor_indexOutOfRange() {
        Shape shape = Shape.fromKM(1, 5);
        List<Integer> lst = new ArrayList<Integer>();
        lst.add(5);
        assertThrows(IllegalArgumentException.class, () -> new SparseBloomFilter(shape, lst));

        lst.clear();
        lst.add(-1);
        assertThrows(IllegalArgumentException.class, () -> new SparseBloomFilter(shape, lst));
    }

    @Test
    public void constructor_noValues() {
        Shape shape = Shape.fromKM(1, 5);
        List<Integer> lst = new ArrayList<Integer>();
        new SparseBloomFilter(shape, lst);
    }

    @Test
    public void producer_constructor() {
        int[] values = { 0, 2, 4, 6, 8 };
        Shape shape = Shape.fromKM(5, 10);
        IndexProducer indices = IndexProducer.fromIntArray(values);
        SparseBloomFilter filter = new SparseBloomFilter(shape, indices);
        List<Integer> lst = new ArrayList<Integer>();
        filter.forEachIndex(x -> {
            lst.add(x);
            return true;
        });
        assertEquals(5, lst.size());
        for (int value : values) {
            assertTrue(lst.contains(Integer.valueOf(value)), "Missing " + value);
        }
    }

    @Test
    public void producer_constructor_duplicate_values() {
        int[] values = { 0, 2, 4, 2, 8 };
        Shape shape = Shape.fromKM(5, 10);
        IndexProducer indices = IndexProducer.fromIntArray(values);
        SparseBloomFilter filter = new SparseBloomFilter(shape, indices);
        List<Integer> lst = new ArrayList<Integer>();
        filter.forEachIndex(x -> {
            lst.add(x);
            return true;
        });
        assertEquals(4, lst.size());
        for (int value : values) {
            assertTrue(lst.contains(Integer.valueOf(value)), "Missing " + value);
        }
    }

    @Test
    public void producer_constructor_negative_value() {
        int[] values = { 0, 2, 4, -2, 8 };
        Shape shape = Shape.fromKM(5, 10);
        IndexProducer indices = IndexProducer.fromIntArray(values);
        assertThrows(IllegalArgumentException.class, () -> new SparseBloomFilter(shape, indices));
    }

    @Test
    public void producer_constructor_value_too_large() {
        int[] values = { 0, 2, 4, 12, 8 };
        Shape shape = Shape.fromKM(5, 10);
        IndexProducer indices = IndexProducer.fromIntArray(values);
        assertThrows(IllegalArgumentException.class, () -> new SparseBloomFilter(shape, indices));
    }

    @Test
    public void producer_constructor_no_indices() {
        Shape shape = Shape.fromKM(5, 10);
        IndexProducer indices = IndexProducer.fromIntArray(new int[0]);
        BloomFilter filter = new SparseBloomFilter(shape, indices);
        assertEquals(0, filter.cardinality());
    }

    @Test
    public void for_each_bitmap_early_exit() {

        Shape shape = Shape.fromKM(5, 100);
        IndexProducer indices = IndexProducer.fromIntArray(new int[] { 66, 67 });
        BloomFilter filter = new SparseBloomFilter(shape, indices);
        assertFalse(filter.forEachBitMap(AbstractBitMapProducerTest.FALSE_CONSUMER));
    }

}
