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

import org.apache.commons.collections4.bloomfilter.CellExtractor.CellPredicate;
import org.junit.jupiter.api.Test;

public abstract class AbstractCellExtractorTest extends AbstractIndexExtractorTest {

    /**
     * A testing CellConsumer that always returns true.
     */
    private static final CellPredicate TRUE_CONSUMER = (i, j) -> true;
    /**
     * A testing CellConsumer that always returns false.
     */
    private static final CellPredicate FALSE_CONSUMER = (i, j) -> false;

    /**
     * Creates a CellExtractor without data.
     * @return a cell extractor that has no data.
     */
    @Override
    protected abstract CellExtractor createEmptyExtractor();

    /**
     * Creates a CellExtractor with some data.
     * @return a cell extractor with some data
     */
    @Override
    protected abstract CellExtractor createExtractor();

    @Override
    protected final int getAsIndexArrayBehaviour() {
        return ORDERED | DISTINCT;
    }

    /**
     * Creates an array of expected values that aligns with the expected indices entries.
     * @return an array of expected values.
     * @see AbstractIndexExtractorTest#getExpectedIndices()
     */
    protected abstract int[] getExpectedValues();

    /**
     * Test the behavior of {@link CellExtractor#processCells(CellPredicate)} with respect
     * to ordered and distinct indices. Currently the behavior is assumed to be the same as
     * {@link IndexExtractor#processIndices(java.util.function.IntPredicate)}.
     */
    @Test
    public final void testBehaviourForEachCell() {
        final IntList list = new IntList();
        createExtractor().processCells((i, j) -> list.add(i));
        final int[] actual = list.toArray();
        // check order
        final int[] expected = Arrays.stream(actual).sorted().toArray();
        assertArrayEquals(expected, actual);
        // check distinct
        final long count = Arrays.stream(actual).distinct().count();
        assertEquals(count, actual.length);
    }

    @Test
    public final void testEmptyCellExtractor() {
        final CellExtractor empty = createEmptyExtractor();
        final int[] ary = empty.asIndexArray();
        assertEquals(0, ary.length);
        assertTrue(empty.processCells((i, j) -> {
            fail("forEachCell consumer should not be called");
            return false;
        }));
    }

    @Test
    public void testForEachCellEarlyExit() {
        final int[] passes = new int[1];
        assertTrue(createEmptyExtractor().processCells((i, j) -> {
            passes[0]++;
            return false;
        }));
        assertEquals(0, passes[0]);

        assertFalse(createExtractor().processCells((i, j) -> {
            passes[0]++;
            return false;
        }));
        assertEquals(1, passes[0]);
    }

    @Test
    public final void testForEachCellPredicates() {
        final CellExtractor populated = createExtractor();
        final CellExtractor empty = createEmptyExtractor();

        assertFalse(populated.processCells(FALSE_CONSUMER), "non-empty should be false");
        assertTrue(empty.processCells(FALSE_CONSUMER), "empty should be true");

        assertTrue(populated.processCells(TRUE_CONSUMER), "non-empty should be true");
        assertTrue(empty.processCells(TRUE_CONSUMER), "empty should be true");
    }

    @Test
    public void testForEachCellValues() {
        final int[] expectedIdx = getExpectedIndices();
        final int[] expectedValue = getExpectedValues();
        assertEquals(expectedIdx.length, expectedValue.length, "expected index length and value length do not match");
        final int[] idx = {0};
        createExtractor().processCells((i, j) -> {
            assertEquals(expectedIdx[idx[0]], i, "bad index at " + idx[0]);
            assertEquals(expectedValue[idx[0]], j, "bad value at " + idx[0]);
            idx[0]++;
            return true;
        });
    }

    @Test
    public final void testIndexConsistency() {
        final CellExtractor extractor = createExtractor();
        final BitSet bs1 = new BitSet();
        final BitSet bs2 = new BitSet();
        extractor.processIndices(i -> {
            bs1.set(i);
            return true;
        });
        extractor.processCells((i, j) -> {
            bs2.set(i);
            return true;
        });
        assertEquals(bs1, bs2);
    }
}

