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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.BitSet;
import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;

/**
 * Test for IndexProducer.
 */
public abstract class AbstractIndexProducerTest {

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
        boolean add(final int value) {
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
    private static final IntPredicate TRUE_PREDICATE = i -> true;

    private static final IntPredicate FALSE_PREDICATE = i -> false;
    /** Flag to indicate the indices are ordered, e.g. from {@link IndexProducer#forEachIndex(IntPredicate)}. */
    protected static final int ORDERED = 0x1;

    /** Flag to indicate the indices are distinct, e.g. from {@link IndexProducer#forEachIndex(IntPredicate)}. */
    protected static final int DISTINCT = 0x2;

    /**
     * Creates an producer without data.
     * @return a producer that has no data.
     */
    protected abstract IndexProducer createEmptyProducer();

    /**
     * Creates a producer with some data.
     * @return a producer with some data
     */
    protected abstract IndexProducer createProducer();

    /**
     * Gets the behavior of the {@link IndexProducer#asIndexArray()} method.
     * @return the behavior.
     * @see #ORDERED
     * @see #DISTINCT
     */
    protected abstract int getAsIndexArrayBehaviour();

    /**
     * Creates an array of expected indices.
     * The expected indices are dependent upon the producer created in the {@code createProducer()} method.
     * @return an array of expected indices.
     */
    protected abstract int[] getExpectedIndices();

    /**
     * Gets the behavior of the {@link IndexProducer#forEachIndex(IntPredicate)} method.
     * By default returns the value of {@code getAsIndexArrayBehaviour()} method.
     * @return the behavior.
     * @see #ORDERED
     * @see #DISTINCT
     */
    protected int getForEachIndexBehaviour() {
        return getAsIndexArrayBehaviour();
    }

    /**
     * Test to ensure that all expected values are generated at least once.
     */
    @Test
    public final void testAsIndexArrayValues() {
        final BitSet bs = new BitSet();
        Arrays.stream(createProducer().asIndexArray()).forEach(bs::set);
        for (final int i : getExpectedIndices()) {
            assertTrue(bs.get(i), () -> "Missing " + i);
        }
    }

    /**
     * Tests the behavior of {@code IndexProducer.asIndexArray()}.
     * The expected behavior is defined by the {@code getBehaviour()} method.
     * The index array may be Ordered, Distinct or both.
     * If the index array is not distinct then all elements returned by the {@code getExpectedIndices()}
     * method, including duplicates, are expected to be returned by the {@code asIndexArray()} method.
     */
    @Test
    public final void testBehaviourAsIndexArray() {
        final int flags = getAsIndexArrayBehaviour();
        final int[] actual = createProducer().asIndexArray();
        if ((flags & ORDERED) != 0) {
            final int[] expected = Arrays.stream(actual).sorted().toArray();
            assertArrayEquals(expected, actual);
        }
        if ((flags & DISTINCT) != 0) {
            final long count = Arrays.stream(actual).distinct().count();
            assertEquals(count, actual.length);
        } else {
            // if the array is not distinct all expected elements must be generated
            // This is modified so use a copy
            final int[] expected = getExpectedIndices().clone();
            Arrays.sort(expected);
            Arrays.sort(actual);
            assertArrayEquals(expected, actual);
        }
    }

    /**
     * Tests the behavior of {@code IndexProducer.forEachIndex()}.
     * The expected behavior is defined by the {@code getBehaviour()} method.
     * The order is assumed to follow the order produced by {@code IndexProducer.asIndexArray()}.
     */
    @Test
    public final void testBehaviourForEachIndex() {
        final int flags = getForEachIndexBehaviour();
        final IntList list = new IntList();
        createProducer().forEachIndex(list::add);
        final int[] actual = list.toArray();
        if ((flags & ORDERED) != 0) {
            final int[] expected = Arrays.stream(actual).sorted().toArray();
            assertArrayEquals(expected, actual);
        }
        if ((flags & DISTINCT) != 0) {
            final long count = Arrays.stream(actual).distinct().count();
            assertEquals(count, actual.length);
        } else {
            // if forEach is not distinct all expected elements must be generated
            final int[] expected = getExpectedIndices().clone();
            Arrays.sort(expected);
            Arrays.sort(actual);
            assertArrayEquals(expected, actual);
        }
    }

    /**
     * Test the distinct indices output from the producer are consistent.
     */
    @Test
    public final void testConsistency() {
        final IndexProducer producer = createProducer();
        final BitSet bs1 = new BitSet();
        final BitSet bs2 = new BitSet();
        Arrays.stream(producer.asIndexArray()).forEach(bs1::set);
        producer.forEachIndex(i -> {
            bs2.set(i);
            return true;
        });
        assertEquals(bs1, bs2);
    }

    @Test
    public final void testEmptyProducer() {
        final IndexProducer empty = createEmptyProducer();
        final int[] ary = empty.asIndexArray();
        assertEquals(0, ary.length);
        assertTrue(empty.forEachIndex(i -> {
            throw new AssertionError("forEach predictate should not be called");
        }));
    }

    /**
     * Test to ensure that for each index returns each expected index at least once.
     */
    @Test
    public final void testForEachIndex() {
        final BitSet bs1 = new BitSet();
        final BitSet bs2 = new BitSet();
        Arrays.stream(getExpectedIndices()).forEach(bs1::set);
        createProducer().forEachIndex(i -> {
            bs2.set(i);
            return true;
        });
        assertEquals(bs1, bs2);
    }

    @Test
    public void testForEachIndexEarlyExit() {
        final int[] passes = new int[1];
        assertFalse(createProducer().forEachIndex(i -> {
            passes[0]++;
            return false;
        }));
        assertEquals(1, passes[0]);

        passes[0] = 0;
        assertTrue(createEmptyProducer().forEachIndex(i -> {
            passes[0]++;
            return false;
        }));
        assertEquals(0, passes[0]);
    }

    @Test
    public final void testForEachIndexPredicates() {
        final IndexProducer populated = createProducer();
        final IndexProducer empty = createEmptyProducer();

        assertFalse(populated.forEachIndex(FALSE_PREDICATE), "non-empty should be false");
        assertTrue(empty.forEachIndex(FALSE_PREDICATE), "empty should be true");

        assertTrue(populated.forEachIndex(TRUE_PREDICATE), "non-empty should be true");
        assertTrue(empty.forEachIndex(TRUE_PREDICATE), "empty should be true");
    }

    @Test
    public void testUniqueReturnsSelf() {
        final IndexProducer expected = createProducer().uniqueIndices();
        assertSame(expected, expected.uniqueIndices());
    }
}
