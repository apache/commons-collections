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

import java.util.function.LongPredicate;

import org.junit.jupiter.api.Test;

public class DefaultBitMapProducerTest extends AbstractBitMapProducerTest {

    @Override
    protected BitMapProducer createProducer() {
        return new DefaultBitMapProducer(new long[] { 1L, 2L });
    }

    @Override
    protected BitMapProducer createEmptyProducer() {
        return new DefaultBitMapProducer(new long[0]);
    }

    @Override
    protected boolean emptyIsZeroLength() {
        return true;
    }

    class DefaultBitMapProducer implements BitMapProducer {
        long[] bitMaps;

        DefaultBitMapProducer(long[] bitMaps) {
            this.bitMaps = bitMaps;
        }

        @Override
        public boolean forEachBitMap(LongPredicate predicate) {
            for (long bitmap : bitMaps) {
                if (!predicate.test(bitmap)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    @Test
    public void testDefaultExpansion() {
        BitMapProducer bmp = BitMapProducer.fromBitMapArray(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L);
        long[] ary = bmp.asBitMapArray();
        assertEquals( 17,  ary.length);
        for (int i=0;i<17;i++)
        {
            assertEquals((long)i, ary[i]);
        }
    }
    
    @Test
    public void testFromIndexProducer() {
        IndexProducer ip = IndexProducer.fromIndexArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        long[] ary = BitMapProducer.fromIndexProducer(ip, 16).asBitMapArray();
        assertEquals(1, ary.length);
        assertEquals(0x07ffL, ary[0]);
    }
    
    @Test
    public void testFromBitMapArray() {
        long[] ary = BitMapProducer.fromBitMapArray(0x07ffL).asBitMapArray();
        assertEquals(1, ary.length);
        assertEquals(0x07ffL, ary[0]);
    }
}
