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

import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DefaultIndexExtractorTest extends AbstractIndexExtractorTest {

    /**
     * Generates an array of integers.
     * @param size the size of the array
     * @param bound the upper bound (exclusive) of the values in the array.
     * @return an array of int.
     */
    public static int[] generateIntArray(final int size, final int bound) {
        return ThreadLocalRandom.current().ints(size, 0, bound).toArray();
    }

    /**
     * Creates a sorted unique array of ints.
     * @param ary the array to sort and make unique
     * @return the sorted unique array.
     */
    public static int[] unique(final int[] ary) {
        return Arrays.stream(ary).distinct().sorted().toArray();
    }

    /**
     * Creates a BitSet of indices.
     * @param ary the array
     * @return the set.
     */
    public static BitSet uniqueSet(final int[] ary) {
        final BitSet bs = new BitSet();
        Arrays.stream(ary).forEach(bs::set);
        return bs;
    }

    /** Make forEachIndex unordered and contain duplicates. */
    private final int[] values = {10, 1, 10, 1};

    @Override
    protected IndexExtractor createEmptyExtractor() {
        return predicate -> {
            Objects.requireNonNull(predicate);
            return true;
        };
    }

    @Override
    protected IndexExtractor createExtractor() {
        return predicate -> {
            Objects.requireNonNull(predicate);
            for (final int i : values) {
                if (!predicate.test(i)) {
                    return false;
                }
            }
            return true;
        };
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        return 0;
    }

    @Override
    protected int[] getExpectedIndices() {
        return values;
    }

    @Override
    protected int getForEachIndexBehaviour() {
        // the forEachIndex implementation returns unordered duplicates.
        return 0;
    }

    @ParameterizedTest
    @ValueSource(ints = {32, 33})
    public void testEntries(final int size) {
        final int[] values = IntStream.range(0, size).toArray();
        final IndexExtractor indexExtractor = predicate -> {
            Objects.requireNonNull(predicate);
            for (final int i : values) {
                if (!predicate.test(i)) {
                    return false;
                }
            }
            return true;
        };
        final int[] other = indexExtractor.asIndexArray();
        assertArrayEquals(values, other);
    }

    @Test
    public void testFromBitMapExtractor() {
        for (int i = 0; i < 5; i++) {
            final int[] expected = generateIntArray(7, 256);
            final long[] bits = BitMaps.newBitMap(256);
            for (final int bitIndex : expected) {
                BitMaps.set(bits, bitIndex);
            }
            final IndexExtractor ip = IndexExtractor.fromBitMapExtractor(BitMapExtractor.fromBitMapArray(bits));
            assertArrayEquals(unique(expected), ip.asIndexArray());
        }
    }

    @Test
    public void testFromIndexArray() {
        for (int i = 0; i < 5; i++) {
            final int[] expected = generateIntArray(10, 256);
            final IndexExtractor ip = IndexExtractor.fromIndexArray(expected);
            assertArrayEquals(expected, ip.asIndexArray());
        }
    }
}
