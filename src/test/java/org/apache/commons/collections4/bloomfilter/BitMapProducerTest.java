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

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import org.junit.Test;

public class BitMapProducerTest {

    @Test
    public void fromIndexProducer() {
        IndexProducer iProducer = new IndexProducer() {

            @Override
            public void forEachIndex(IntConsumer consumer) {
                consumer.accept( 0 );
                consumer.accept( 1 );
                consumer.accept( 63 );
                consumer.accept( 64 );
                consumer.accept( 127 );
                consumer.accept( 128 );
            }
        };
        BitMapProducer producer = BitMapProducer.fromIndexProducer(iProducer, new Shape( 1, 200 ));
        List<Long> lst = new ArrayList<Long>();
        producer.forEachBitMap( lst::add );
        long[] buckets = lst.stream().mapToLong( l -> l.longValue()).toArray();
        assertTrue( BitMap.contains( buckets, 0));
        assertTrue( BitMap.contains( buckets, 1));
        assertTrue( BitMap.contains( buckets, 63));
        assertTrue( BitMap.contains( buckets, 64));
        assertTrue( BitMap.contains( buckets, 127));
        assertTrue( BitMap.contains( buckets, 128));
    }

}
