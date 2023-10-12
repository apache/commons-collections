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

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongPredicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class IndexProducerTest {

    @Test
    public void fromBitMapProducerTest() {
        TestingBitMapProducer producer = new TestingBitMapProducer(new long[] {1L, 2L, 3L});
        IndexProducer underTest = IndexProducer.fromBitMapProducer(producer);
        List<Integer> lst = new ArrayList<>();

        underTest.forEachIndex(lst::add);
        assertEquals(4, lst.size());
        assertEquals(Integer.valueOf(0), lst.get(0));
        assertEquals(Integer.valueOf(1 + 64), lst.get(1));
        assertEquals(Integer.valueOf(0 + 128), lst.get(2));
        assertEquals(Integer.valueOf(1 + 128), lst.get(3));

        producer = new TestingBitMapProducer(new long[] {0xFFFFFFFFFFFFFFFFL});
        underTest = IndexProducer.fromBitMapProducer(producer);
        lst = new ArrayList<>();

        underTest.forEachIndex(lst::add);

        assertEquals(64, lst.size());
        for (int i = 0; i < 64; i++) {
            assertEquals(Integer.valueOf(i), lst.get(i));
        }
    }

    private static final class TestingBitMapProducer implements BitMapProducer {
        long[] values;

        TestingBitMapProducer(final long[] values) {
            this.values = values;
        }

        @Override
        public boolean forEachBitMap(final LongPredicate consumer) {
            for (final long l : values) {
                if (!consumer.test(l)) {
                    return false;
                }
            }
            return true;
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {32, 33})
    void testAsIndexArray(final int n) {
        final IndexProducer ip = i -> {
            for (int j = 0; j < n; j++) {
                // Always test index zero
                i.test(0);
            }
            return true;
        };
        Assertions.assertArrayEquals(new int[n], ip.asIndexArray());
    }
}
