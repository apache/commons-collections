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

import org.junit.jupiter.api.Test;

public class BitMapExtractorFromLongArrayTest extends AbstractBitMapExtractorTest {

    @Test
    public void constructorTest() {
        final List<Long> lst = new ArrayList<>();
        createExtractor().processBitMaps(lst::add);
        assertEquals(Long.valueOf(1), lst.get(0));
        assertEquals(Long.valueOf(2), lst.get(1));
        assertEquals(Long.valueOf(3), lst.get(2));
        assertEquals(Long.valueOf(4), lst.get(3));
        assertEquals(Long.valueOf(5), lst.get(4));
    }

    @Override
    protected BitMapExtractor createEmptyExtractor() {
        return BitMapExtractor.fromBitMapArray();
    }

    @Override
    protected BitMapExtractor createExtractor() {
        final long[] ary = {1L, 2L, 3L, 4L, 5L};
        return BitMapExtractor.fromBitMapArray(ary);
    }

    @Override
    protected boolean emptyIsZeroLength() {
        return true;
    }

    @Test
    public void testFromIndexExtractor() {
        final int limit = Integer.SIZE + Long.SIZE;
        final IndexExtractor indexExtractor = consumer -> {
            for (int i = 0; i < limit; i++) {
                if (!consumer.test(i)) {
                    return false;
                }
            }
            return true;
        };
        final BitMapExtractor bitMapExtractor = BitMapExtractor.fromIndexExtractor(indexExtractor, limit);
        final List<Long> lst = new ArrayList<>();
        bitMapExtractor.processBitMaps(lst::add);
        long expected = ~0L;
        assertEquals(expected, lst.get(0).longValue());
        expected &= 0XFFFFFFFFL;
        assertEquals(expected, lst.get(1).longValue());
    }
}
