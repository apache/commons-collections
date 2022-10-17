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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntPredicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for IndexProducer.
 *
 */
public abstract class AbstractIndexProducerTest {

    private static final IntPredicate TRUE_PREDICATE = i -> true;
    private static final IntPredicate FALSE_PREDICATE = i -> false;

    /** Flag to indicate the {@link IndexProducer#forEachIndex(IntPredicate)} is ordered. */
    protected static final int FOR_EACH_ORDERED = 0x1;
    /** Flag to indicate the {@link IndexProducer#forEachIndex(IntPredicate)} is distinct. */
    protected static final int FOR_EACH_DISTINCT = 0x2;
    /** Flag to indicate the {@link IndexProducer#asIndexArray()} is ordered. */
    protected static final int AS_ARRAY_ORDERED = 0x4;
    /** Flag to indicate the {@link IndexProducer#asIndexArray()} is distinct. */
    protected static final int AS_ARRAY_DISTINCT = 0x8;

    /**
     * An expandable list of int values.
     */
    protected static class IntList {
        private int size;
        private int[] data = {0};

        /**
         * Adds the value to the list.
         *
         * @param value the value
         * @return true if the list was modified
         */
        boolean add(int value) {
            if (size == data.length) {
                data = Arrays.copyOf(data, size << 1);
            }
            data[size++] = value;
            return true;
        }

        /**
         * Convert to an array.
         *
         * @return the array
         */
        int[] toArray() {
            return Arrays.copyOf(data, size);
        }
    }

    /**
     * Creates a producer with some data.
     * @return a producer with some data
     */
    protected abstract IndexProducer createProducer();

    /**
     * Creates an producer without data.
     * @return a producer that has no data.
     */
    protected abstract IndexProducer createEmptyProducer();

    /**
     * Gets the behaviour flags.
     *
     * <p>The flags indicate if the methods {@link IndexProducer#forEachIndex(IntPredicate)}
     * and {@link IndexProducer#asIndexArray()} output sorted or distinct indices.
     *
     * @return the behaviour.
     */
    protected abstract int getBehaviour();

    /**
     * Creates an array of expected indices.
     * @return an array of expected indices.
     */
    protected abstract int[] getExpectedIndices();

    @Test
    public final void testAsIndexArrayValues() {
        List<Integer> lst = new ArrayList<>();
        Arrays.stream(createProducer().asIndexArray()).boxed().forEach( lst::add );
        for (int i : getExpectedIndices()) {
            assertTrue( lst.contains(i), "Missing "+i );
        }
    }

    @Test
    public final void testForEachIndex() {
        //IndexProducer producer = createProducer();
        BitSet bs1 = new BitSet();
        BitSet bs2 = new BitSet();
        Arrays.stream(getExpectedIndices()).forEach(bs1::set);
        createProducer().forEachIndex(i -> {
            bs2.set(i);
            return true;
        });
        Assertions.assertEquals(bs1, bs2);
    }

    @Test
    public final void testForEachIndexPredicates() {
        IndexProducer populated = createProducer();
        IndexProducer empty = createEmptyProducer();

        assertFalse(populated.forEachIndex(FALSE_PREDICATE), "non-empty should be false");
        assertTrue(empty.forEachIndex(FALSE_PREDICATE), "empty should be true");

        assertTrue(populated.forEachIndex(TRUE_PREDICATE), "non-empty should be true");
        assertTrue(empty.forEachIndex(TRUE_PREDICATE), "empty should be true");
    }

    @Test
    public final void testEmptyProducer() {
        IndexProducer empty = createEmptyProducer();
        int ary[] = empty.asIndexArray();
        Assertions.assertEquals(0, ary.length);
        assertTrue(empty.forEachIndex(i -> {
            throw new AssertionError("forEach predictate should not be called");
        }));
    }

    /**
     * Test the distinct indices output from the producer are consistent.
     */
    @Test
    public final void testConsistency() {
        IndexProducer producer = createProducer();
        BitSet bs1 = new BitSet();
        BitSet bs2 = new BitSet();
        Arrays.stream(producer.asIndexArray()).forEach(bs1::set);
        producer.forEachIndex(i -> {
            bs2.set(i);
            return true;
        });
        Assertions.assertEquals(bs1, bs2);
    }

    @Test
    public final void testBehaviourAsIndexArray() {
        int flags = getBehaviour();
        int[] actual = createProducer().asIndexArray();
        if ((flags & AS_ARRAY_ORDERED) != 0) {
            int[] expected = Arrays.stream(actual).sorted().toArray();
            Assertions.assertArrayEquals(expected, actual);
        }
        if ((flags & AS_ARRAY_DISTINCT) != 0) {
            long count = Arrays.stream(actual).distinct().count();
            Assertions.assertEquals(count, actual.length);
        }
    }

    @Test
    public final void testBehaviourForEach() {
        int flags = getBehaviour();
        IntList list = new IntList();
        createProducer().forEachIndex(list::add);
        int[] actual = list.toArray();
        if ((flags & FOR_EACH_ORDERED) != 0) {
            int[] expected = Arrays.stream(actual).sorted().toArray();
            Assertions.assertArrayEquals(expected, actual);
        }
        if ((flags & FOR_EACH_DISTINCT) != 0) {
            long count = Arrays.stream(actual).distinct().count();
            Assertions.assertEquals(count, actual.length);
        }
    }

    @Test
    public void testForEachIndexEarlyExit() {
        int[] passes = new int[1];
        assertFalse(createProducer().forEachIndex(i -> {
            passes[0]++;
            return false;
        }));
        Assertions.assertEquals(1, passes[0]);

        passes[0] = 0;
        assertTrue(createEmptyProducer().forEachIndex(i -> {
            passes[0]++;
            return false;
        }));
        Assertions.assertEquals(0, passes[0]);
    }
}
