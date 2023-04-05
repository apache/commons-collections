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

import org.junit.jupiter.api.Test;

public class DefaultIndexProducerTest extends AbstractIndexProducerTest {

    /** Make forEachIndex unordered and contain duplicates. */
    private final int[] values = {10, 1, 10, 1};

    @Override
    protected int[] getExpectedIndices() {
        return values;
    }

    @Override
    protected IndexProducer createProducer() {
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
    protected IndexProducer createEmptyProducer() {
        return predicate -> {
            Objects.requireNonNull(predicate);
            return true;
        };
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        // The default method streams a BitSet so is distinct and ordered.
        return DISTINCT | ORDERED;
    }

    @Override
    protected int getForEachIndexBehaviour() {
        // the forEachIndex implementation returns unordered duplicates.
        return 0;
    }

    /**
     * Generates an array of integers.
     * @param size the size of the array
     * @param bound the upper bound (exclusive) of the values in the array.
     * @return an array of int.
     */
    static int[] generateIntArray(final int size, final int bound) {
        return ThreadLocalRandom.current().ints(size, 0, bound).toArray();
    }

    /**
     * Creates a BitSet of indices.
     * @param ary the array
     * @return the set.
     */
    static BitSet uniqueSet(final int[] ary) {
        final BitSet bs = new BitSet();
        Arrays.stream(ary).forEach(bs::set);
        return bs;
    }

    /**
     * Creates a sorted unique array of ints.
     * @param ary the array to sort and make unique
     * @return the sorted unique array.
     */
    static int[] unique(final int[] ary) {
        return Arrays.stream(ary).distinct().sorted().toArray();
    }

    @Test
    public void testFromBitMapProducer() {
        for (int i = 0; i < 5; i++) {
            final int[] expected = generateIntArray(7, 256);
            final long[] bits = new long[BitMap.numberOfBitMaps(256)];
            for (final int bitIndex : expected) {
                BitMap.set(bits, bitIndex);
            }
            final IndexProducer ip = IndexProducer.fromBitMapProducer(BitMapProducer.fromBitMapArray(bits));
            assertArrayEquals(unique(expected), ip.asIndexArray());
        }
    }

    @Test
    public void testFromIndexArray() {
        for (int i = 0; i < 5; i++) {
            final int[] expected = generateIntArray(10, 256);
            final IndexProducer ip = IndexProducer.fromIndexArray(expected);
            assertArrayEquals(expected, ip.asIndexArray());
        }
    }
}
