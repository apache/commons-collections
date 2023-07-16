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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Arrays;
import java.util.BitSet;

import org.apache.commons.collections4.bag.TreeBag;
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
     * Creates an array of integer pairs comprising the index and the expected cell for the index.
     * The order of the cells for each index is dependent upon the producer created by the {@code createProducer()}
     * method.
     * By default returns the each {@code getExpectedIndices()} value paired with 1 (one).
     * @return an array of integer pairs comprising the index and the expected cell for the index.
     */
    protected int[][] getExpectedCells() {
        return Arrays.stream(getExpectedIndices()).mapToObj(x -> new int[] {x, 1}).toArray(int[][]::new);
    }

    /**
     * Creates a producer with some data.
     * @return a producer with some data
     */
    @Override
    protected abstract CellProducer createProducer();

    /**
     * Creates a producer without data.
     * @return a producer that has no data.
     */
    @Override
    protected abstract CellProducer createEmptyProducer();

    /**
     * Gets the behavior of the {@link CellProducer#forEachCell(CellConsumer)} method.
     * By default returns the value of {@code getAsIndexArrayBehaviour()} method.
     * @return the behavior.
     */
    protected int getForEachCellBehaviour() {
        return getAsIndexArrayBehaviour();
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
    public final void testEmptyCellProducer() {
        final CellProducer empty = createEmptyProducer();
        final int ary[] = empty.asIndexArray();
        assertEquals(0, ary.length);
        assertTrue(empty.forEachCell((i, j) -> {
            fail("forEachCell consumer should not be called");
            return false;
        }));
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

    @Test
    public void testForEachCellValues() {
        // Assumes the collections bag works. Could be replaced with Map<Integer,Integer> with more work.
        final TreeBag<Integer> expected = new TreeBag<>();
        Arrays.stream(getExpectedCells()).forEach(c -> expected.add(c[0], c[1]));
        final TreeBag<Integer> actual = new TreeBag<>();
        // can not return actual.add as it returns false on duplicate 'i'
        createProducer().forEachCell((i, j) -> {
            actual.add(i, j);
            return true;
        });
        assertEquals(expected, actual);
    }

    /**
     * Test the behavior of {@link CellProducer#forEachCell(CellConsumer)} with respect
     * to ordered and distinct indices. Currently the behavior is assumed to be the same as
     * {@link IndexProducer#forEachIndex(java.util.function.IntPredicate)}.
     */
    @Test
    public final void testBehaviourForEachCell() {
        final int flags = getForEachCellBehaviour();
        assumeTrue((flags & (ORDERED | DISTINCT)) != 0);
        final IntList list = new IntList();
        createProducer().forEachCell((i, j) -> list.add(i));
        final int[] actual = list.toArray();
        if ((flags & ORDERED) != 0) {
            final int[] expected = Arrays.stream(actual).sorted().toArray();
            assertArrayEquals(expected, actual);
        }
        if ((flags & DISTINCT) != 0) {
            final long count = Arrays.stream(actual).distinct().count();
            assertEquals(count, actual.length);
        }
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
}
