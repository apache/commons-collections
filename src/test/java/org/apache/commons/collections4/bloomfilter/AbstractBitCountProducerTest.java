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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.BitSet;

import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.bloomfilter.BitCountProducer.BitCountConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class AbstractBitCountProducerTest extends AbstractIndexProducerTest {

    /** Flag to indicate the {@link BitCountProducer#forEachCount(BitCountConsumer)} is ordered. */
    protected static final int FOR_EACH_COUNT_ORDERED = 0x10;
    /** Flag to indicate the {@link BitCountProducer#forEachCount(BitCountConsumer)} is distinct. */
    protected static final int FOR_EACH_COUNT_DISTINCT = 0x20;

    /**
     * A testing BitCountConsumer that always returns true.
     */
    private static final BitCountConsumer TRUE_CONSUMER = (i, j) -> true;
    /**
     * A testing BitCountConsumer that always returns false.
     */
    private static final BitCountConsumer FALSE_CONSUMER = (i, j) -> false;

    /**
     * Creates an array of integer pairs comprising the index and the expected count for the index.
     * @return an array of integer pairs comprising the index and the expected count for the index.
     */
    protected int[][] getExpectedBitCount() {
        return Arrays.stream(getExpectedIndices()).mapToObj(x -> new int[] {x, 1}).toArray(int[][]::new);
    }

    protected int[] getExpectedForEach() {
        return getExpectedIndices();
    }

    /**
     * Creates a producer with some data.
     * @return a producer with some data
     */
    @Override
    protected abstract BitCountProducer createProducer();

    /**
     * Creates an producer without data.
     * @return a producer that has no data.
     */
    @Override
    protected abstract BitCountProducer createEmptyProducer();

    @Test
    public final void testForEachCountPredicates() {
        BitCountProducer populated = createProducer();
        BitCountProducer empty = createEmptyProducer();

        assertFalse(populated.forEachCount(FALSE_CONSUMER), "non-empty should be false");
        assertTrue(empty.forEachCount(FALSE_CONSUMER), "empty should be true");

        assertTrue(populated.forEachCount(TRUE_CONSUMER), "non-empty should be true");
        assertTrue(empty.forEachCount(TRUE_CONSUMER), "empty should be true");
    }

    @Test
    public final void testEmptyBitCountProducer() {
        BitCountProducer empty = createEmptyProducer();
        int ary[] = empty.asIndexArray();
        assertEquals(0, ary.length);
        assertTrue(empty.forEachCount((i, j) -> {
            throw new AssertionError("forEachCount consumer should not be called");
        }));
    }

    @Test
    public final void testIndexConsistency() {
        BitCountProducer producer = createProducer();
        BitSet bs1 = new BitSet();
        BitSet bs2 = new BitSet();
        producer.forEachIndex(i -> {
            bs1.set(i);
            return true;
        });
        producer.forEachCount((i, j) -> {
            bs2.set(i);
            return true;
        });
        Assertions.assertEquals(bs1, bs2);
    }

    @Test
    public void testForEachCount() {
        // Assumes the collections bag works. Could be replaced with Map<Integer,Integer> with more work.
        final TreeBag<Integer> expected = new TreeBag<>();
        Arrays.stream(getExpectedBitCount()).forEach(c -> expected.add(c[0], c[1]));
        final TreeBag<Integer> actual = new TreeBag<>();
        // can not return actual.add as it returns false on duplicate 'i'
        createProducer().forEachCount((i, j) -> {
            actual.add(i, j);
            return true;
            }
        );
        assertEquals(expected, actual);
    }

    @Test
    public final void testBehaviourForEachCount() {
        int flags = getBehaviour();
        IntList list = new IntList();
        createProducer().forEachCount((i, j) -> list.add(i));
        int[] actual = list.toArray();
        if ((flags & FOR_EACH_COUNT_ORDERED) != 0) {
            int[] expected = Arrays.stream(actual).sorted().toArray();
            Assertions.assertArrayEquals(expected, actual);
        }
        if ((flags & FOR_EACH_COUNT_DISTINCT) != 0) {
            long count = Arrays.stream(actual).distinct().count();
            Assertions.assertEquals(count, actual.length);
        }
        int[] expected = getExpectedForEach();
        Assertions.assertArrayEquals( expected, actual);
    }

    @Test
    public void testForEachCountEarlyExit() {
        int[] passes = new int[1];
        assertTrue(createEmptyProducer().forEachCount((i, j) -> {
            passes[0]++;
            return false;
        }));
        Assertions.assertEquals(0, passes[0]);

        assertFalse(createProducer().forEachCount((i, j) -> {
            passes[0]++;
            return false;
        }));
        Assertions.assertEquals(1, passes[0]);
    }
}
