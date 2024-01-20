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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.BitSet;

import org.apache.commons.collections4.bloomfilter.CellProducer.CellConsumer;
import org.junit.jupiter.api.Test;

public abstract class AbstractCellProducerTest extends AbstractIndexProducerTest {

    /**
     * A testing CellConsumer that always returns true.
     */
    private static final CellConsumer TRUE_CONSUMER = (i, j) -> true;
    /**
     * A testing CellConsumer that always returns false.
     */
    private static final CellConsumer FALSE_CONSUMER = (i, j) -> false;

    /**
     * Creates a producer without data.
     * @return a producer that has no data.
     */
    @Override
    protected abstract CellProducer createEmptyProducer();

    /**
     * Creates a producer with some data.
     * @return a producer with some data
     */
    @Override
    protected abstract CellProducer createProducer();

    @Override
    protected final int getAsIndexArrayBehaviour() {
        return ORDERED | DISTINCT;
    }

    /**
     * Creates an array of expected values that aligns with the expected indices entries.
     * @return an array of expected values.
     * @see AbstractIndexProducerTest#getExpectedIndices()
     */
    protected abstract int[] getExpectedValues();

    /**
     * Test the behavior of {@link CellProducer#forEachCell(CellConsumer)} with respect
     * to ordered and distinct indices. Currently the behavior is assumed to be the same as
     * {@link IndexProducer#forEachIndex(java.util.function.IntPredicate)}.
     */
    @Test
    public final void testBehaviourForEachCell() {
        final IntList list = new IntList();
        createProducer().forEachCell((i, j) -> list.add(i));
        final int[] actual = list.toArray();
        // check order
        final int[] expected = Arrays.stream(actual).sorted().toArray();
        assertArrayEquals(expected, actual);
        // check distinct
        final long count = Arrays.stream(actual).distinct().count();
        assertEquals(count, actual.length);
    }

    @Test
    public final void testEmptyCellProducer() {
        final CellProducer empty = createEmptyProducer();
        final int[] ary = empty.asIndexArray();
        assertEquals(0, ary.length);
        assertTrue(empty.forEachCell((i, j) -> {
            fail("forEachCell consumer should not be called");
            return false;
        }));
    }

    @Test
    public void testForEachCellEarlyExit() {
        final int[] passes = new int[1];
        assertTrue(createEmptyProducer().forEachCell((i, j) -> {
            passes[0]++;
            return false;
        }));
        assertEquals(0, passes[0]);

        assertFalse(createProducer().forEachCell((i, j) -> {
            passes[0]++;
            return false;
        }));
        assertEquals(1, passes[0]);
    }

    @Test
    public final void testForEachCellPredicates() {
        final CellProducer populated = createProducer();
        final CellProducer empty = createEmptyProducer();

        assertFalse(populated.forEachCell(FALSE_CONSUMER), "non-empty should be false");
        assertTrue(empty.forEachCell(FALSE_CONSUMER), "empty should be true");

        assertTrue(populated.forEachCell(TRUE_CONSUMER), "non-empty should be true");
        assertTrue(empty.forEachCell(TRUE_CONSUMER), "empty should be true");
    }

    @Test
    public void testForEachCellValues() {
        final int[] expectedIdx = getExpectedIndices();
        final int[] expectedValue = getExpectedValues();
        assertEquals(expectedIdx.length, expectedValue.length, "expected index length and value length do not match");
        final int[] idx = {0};
        createProducer().forEachCell((i, j) -> {
            assertEquals(expectedIdx[idx[0]], i, "bad index at " + idx[0]);
            assertEquals(expectedValue[idx[0]], j, "bad value at " + idx[0]);
            idx[0]++;
            return true;
        });
    }

    @Test
    public final void testIndexConsistency() {
        final CellProducer producer = createProducer();
        final BitSet bs1 = new BitSet();
        final BitSet bs2 = new BitSet();
        producer.forEachIndex(i -> {
            bs1.set(i);
            return true;
        });
        producer.forEachCell((i, j) -> {
            bs2.set(i);
            return true;
        });
        assertEquals(bs1, bs2);
    }
}

