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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

import org.junit.Test;

public class BitCountProducerTest {

    @Test
    public void fromIndexProducer() {
        IndexProducer iProducer = new IndexProducer() {

            @Override
            public boolean forEachIndex(IntPredicate consumer) {
                return consumer.test(0) && consumer.test(1) && consumer.test(63) && consumer.test(64)
                        && consumer.test(127) && consumer.test(128);
            }
        };
        BitCountProducer producer = BitCountProducer.from(iProducer);
        Map<Integer, Integer> m = new HashMap<Integer, Integer>();

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

    @Test
    public void forEachIndexTest() {
        BitCountProducer producer = new BitCountProducer() {

            @Override
            public boolean forEachCount(BitCountConsumer consumer) {
                return consumer.test(1, 11) && consumer.test(3, 13);
            }
        };

        List<Integer> lst = new ArrayList<Integer>();
        producer.forEachIndex(lst::add);
        assertEquals(2, lst.size());
        assertEquals(Integer.valueOf(1), lst.get(0));
        assertEquals(Integer.valueOf(3), lst.get(1));
    }

}
