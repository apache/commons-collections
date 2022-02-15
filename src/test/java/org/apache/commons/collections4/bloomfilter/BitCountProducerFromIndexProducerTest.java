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

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class BitCountProducerFromIndexProducerTest extends AbstractBitCountProducerTest {

    @Override
    protected BitCountProducer createProducer() {
        return BitCountProducer.from(IndexProducer.fromIndexArray(new int[] { 0, 1, 63, 64, 127, 128 }));
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return BitCountProducer.from(IndexProducer.fromIndexArray(new int[0]));
    }

    @Test
    public final void testFromIndexProducer() {

        BitCountProducer producer = createProducer();
        Map<Integer, Integer> m = new HashMap<>();

        producer.forEachCount((i, v) -> {
            m.put(i, v);
            return true;
        });

        assertEquals(6, m.size());
        assertEquals(Integer.valueOf(1), m.get(0));
        assertEquals(Integer.valueOf(1), m.get(1));
        assertEquals(Integer.valueOf(1), m.get(63));
        assertEquals(Integer.valueOf(1), m.get(64));
        assertEquals(Integer.valueOf(1), m.get(127));
        assertEquals(Integer.valueOf(1), m.get(128));
    }
}
