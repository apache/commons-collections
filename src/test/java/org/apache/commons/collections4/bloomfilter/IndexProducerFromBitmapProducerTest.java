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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongPredicate;

import org.junit.jupiter.api.Test;

public class IndexProducerFromBitmapProducerTest extends AbstractIndexProducerTest {

    @Override
    protected IndexProducer createEmptyProducer() {
        TestingBitMapProducer producer = new TestingBitMapProducer(new long[0]);
        return IndexProducer.fromBitMapProducer(producer);
    }

    @Override
    protected IndexProducer createProducer() {
        /* Creates an index producer that produces the values:
         * 0, 65, 128, and 129
         @formatter:off
                Index2    Index1     Index0
         bit       128        64          0
                     |         |          |
         1L =>       |         |    ...0001
         2L =>       |   ...0010
         3L => ...0011
         @formatter:on
         */
        TestingBitMapProducer producer = new TestingBitMapProducer(new long[] { 1L, 2L, 3L });
        return IndexProducer.fromBitMapProducer(producer);
    }

    @Override
    protected int getBehaviour() {
        // Bit maps will be distinct. Conversion to indices should be ordered.
        return FOR_EACH_DISTINCT | FOR_EACH_ORDERED | AS_ARRAY_DISTINCT | AS_ARRAY_ORDERED;
    }

    @Test
    public final void testFromBitMapProducerTest() {
        IndexProducer underTest = createProducer();
        List<Integer> lst = new ArrayList<>();

        underTest.forEachIndex(lst::add);
        assertEquals(4, lst.size());
        assertEquals(Integer.valueOf(0), lst.get(0));
        assertEquals(Integer.valueOf(1 + 64), lst.get(1));
        assertEquals(Integer.valueOf(0 + 128), lst.get(2));
        assertEquals(Integer.valueOf(1 + 128), lst.get(3));

        BitMapProducer producer = new TestingBitMapProducer(new long[] { 0xFFFFFFFFFFFFFFFFL });
        underTest = IndexProducer.fromBitMapProducer(producer);
        lst = new ArrayList<>();

        underTest.forEachIndex(lst::add);

        assertEquals(64, lst.size());
        for (int i = 0; i < 64; i++) {
            assertEquals(Integer.valueOf(i), lst.get(i));
        }
    }

    private class TestingBitMapProducer implements BitMapProducer {
        long[] values;

        TestingBitMapProducer(long[] values) {
            this.values = values;
        }

        @Override
        public boolean forEachBitMap(LongPredicate consumer) {
            for (long l : values) {
                if (!consumer.test(l)) {
                    return false;
                }
            }
            return true;
        }
    }
}
