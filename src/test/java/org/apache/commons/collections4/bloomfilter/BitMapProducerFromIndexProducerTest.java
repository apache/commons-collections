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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BitMapProducerFromIndexProducerTest extends AbstractBitMapProducerTest {

    @Override
    protected BitMapProducer createProducer() {
        final IndexProducer iProducer = consumer -> consumer.test(0) && consumer.test(1) && consumer.test(63) && consumer.test(64)
                && consumer.test(127) && consumer.test(128);
        return BitMapProducer.fromIndexProducer(iProducer, 200);
    }

    @Override
    protected BitMapProducer createEmptyProducer() {
        final IndexProducer iProducer = consumer -> true;
        return BitMapProducer.fromIndexProducer(iProducer, 200);
    }

    @Test
    public final void testFromIndexProducer() {
        final List<Long> lst = new ArrayList<>();
        createProducer().forEachBitMap(lst::add);
        final long[] buckets = lst.stream().mapToLong(Long::longValue).toArray();
        assertTrue(BitMap.contains(buckets, 0));
        assertTrue(BitMap.contains(buckets, 1));
        assertTrue(BitMap.contains(buckets, 63));
        assertTrue(BitMap.contains(buckets, 64));
        assertTrue(BitMap.contains(buckets, 127));
        assertTrue(BitMap.contains(buckets, 128));
    }
}
