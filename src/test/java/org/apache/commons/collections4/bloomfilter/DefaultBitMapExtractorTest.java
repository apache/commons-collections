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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongPredicate;

import org.junit.jupiter.api.Test;

public class DefaultBitMapExtractorTest extends AbstractBitMapExtractorTest {

    class DefaultBitMapExtractor implements BitMapExtractor {
        long[] bitMaps;

        DefaultBitMapExtractor(final long[] bitMaps) {
            this.bitMaps = bitMaps;
        }

        @Override
        public boolean forEachBitMap(final LongPredicate predicate) {
            for (final long bitmap : bitMaps) {
                if (!predicate.test(bitmap)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Generates an array of random long values.
     * @param size the number of values to generate
     * @return the array of random values.
     */
    static long[] generateLongArray(final int size) {
        return ThreadLocalRandom.current().longs(size).toArray();
    }

    long[] values = generateLongArray(5);

    @Override
    protected BitMapExtractor createEmptyProducer() {
        return new DefaultBitMapExtractor(new long[0]);
    }

    @Override
    protected BitMapExtractor createProducer() {
        return new DefaultBitMapExtractor(values);
    }

    @Override
    protected boolean emptyIsZeroLength() {
        return true;
    }

    @Test
    public void testAsBitMapArrayLargeArray() {
        final long[] expected = generateLongArray(32);
        final BitMapExtractor producer = predicate -> {
            for (final long l : expected) {
                if (!predicate.test(l)) {
                    return false;
                }
            }
            return true;
        };
        final long[] ary = producer.asBitMapArray();
        assertArrayEquals(expected, ary);
    }

    @Test
    public void testFromBitMapArray() {
        final int nOfBitMaps = BitMaps.numberOfBitMaps(256);
        final long[] expected = generateLongArray(nOfBitMaps);
        final long[] ary = BitMapExtractor.fromBitMapArray(expected).asBitMapArray();
        assertArrayEquals(expected, ary);
    }

    @Test
    public void testFromIndexProducer() {
        final int[] expected = DefaultIndexExtractorTest.generateIntArray(10, 256);
        final IndexExtractor ip = IndexExtractor.fromIndexArray(expected);
        final long[] ary = BitMapExtractor.fromIndexProducer(ip, 256).asBitMapArray();
        for (final int idx : expected) {
            assertTrue(BitMaps.contains(ary, idx));
        }
    }
}
