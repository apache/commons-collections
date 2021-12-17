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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

import org.junit.Test;

public class BitMapProducerTest {

    @Test
    public void fromIndexProducer() {
        IndexProducer iProducer = new IndexProducer() {

            @Override
            public boolean forEachIndex(IntPredicate consumer) {
                return consumer.test(0) && consumer.test(1) && consumer.test(63) && consumer.test(64)
                        && consumer.test(127) && consumer.test(128);
            }
        };
        BitMapProducer producer = BitMapProducer.fromIndexProducer(iProducer, new Shape(1, 200));
        List<Long> lst = new ArrayList<Long>();
        producer.forEachBitMap(lst::add);
        long[] buckets = lst.stream().mapToLong(l -> l.longValue()).toArray();
        assertTrue(BitMap.contains(buckets, 0));
        assertTrue(BitMap.contains(buckets, 1));
        assertTrue(BitMap.contains(buckets, 63));
        assertTrue(BitMap.contains(buckets, 64));
        assertTrue(BitMap.contains(buckets, 127));
        assertTrue(BitMap.contains(buckets, 128));
    }

    @Test
    public void fromLongArrayTest() {
        long[] ary = new long[] { 1L, 2L, 3L, 4L, 5L };
        BitMapProducer producer = BitMapProducer.fromLongArray(ary);
        List<Long> lst = new ArrayList<Long>();
        producer.forEachBitMap(lst::add);
        assertEquals(Long.valueOf(1), lst.get(0));
        assertEquals(Long.valueOf(2), lst.get(1));
        assertEquals(Long.valueOf(3), lst.get(2));
        assertEquals(Long.valueOf(4), lst.get(3));
        assertEquals(Long.valueOf(5), lst.get(4));

    }

    @Test
    public void arrayBuilderTest() {
        assertThrows(IllegalArgumentException.class,
                () -> new BitMapProducer.ArrayBuilder(new Shape(1, 4), new long[] { 1L, 2L, 3L, 4L, 5L }));
    }

}
