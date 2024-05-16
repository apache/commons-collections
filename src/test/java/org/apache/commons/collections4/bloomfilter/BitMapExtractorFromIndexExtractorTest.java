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

public class BitMapExtractorFromIndexExtractorTest extends AbstractBitMapExtractorTest {

    @Override
    protected BitMapExtractor createEmptyExtractor() {
        final IndexExtractor indexExtractor = consumer -> true;
        return BitMapExtractor.fromIndexExtractor(indexExtractor, 200);
    }

    @Override
    protected BitMapExtractor createExtractor() {
        final IndexExtractor indexExtractor = consumer -> consumer.test(0) && consumer.test(1) && consumer.test(63) && consumer.test(64)
                && consumer.test(127) && consumer.test(128);
        return BitMapExtractor.fromIndexExtractor(indexExtractor, 200);
    }

    @Test
    public final void testFromIndexExtractor() {
        final List<Long> lst = new ArrayList<>();
        createExtractor().processBitMaps(lst::add);
        final long[] buckets = lst.stream().mapToLong(Long::longValue).toArray();
        assertTrue(BitMaps.contains(buckets, 0));
        assertTrue(BitMaps.contains(buckets, 1));
        assertTrue(BitMaps.contains(buckets, 63));
        assertTrue(BitMaps.contains(buckets, 64));
        assertTrue(BitMaps.contains(buckets, 127));
        assertTrue(BitMaps.contains(buckets, 128));
    }
}
