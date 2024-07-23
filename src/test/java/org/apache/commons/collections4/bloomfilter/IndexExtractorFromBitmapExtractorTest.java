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

import org.junit.jupiter.api.Test;

public class IndexExtractorFromBitmapExtractorTest extends AbstractIndexExtractorTest {

    private static final class TestingBitMapExtractor implements BitMapExtractor {
        long[] values;

        TestingBitMapExtractor(final long[] values) {
            this.values = values;
        }

        @Override
        public boolean processBitMaps(final LongPredicate consumer) {
            for (final long l : values) {
                if (!consumer.test(l)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected IndexExtractor createEmptyExtractor() {
        final TestingBitMapExtractor testingBitMapExtractor = new TestingBitMapExtractor(new long[0]);
        return IndexExtractor.fromBitMapExtractor(testingBitMapExtractor);
    }

    @Override
    protected IndexExtractor createExtractor() {
        /* Creates an index testingBitMapExtractor that produces the values:
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
        final TestingBitMapExtractor testingBitMapExtractor = new TestingBitMapExtractor(new long[] {1L, 2L, 3L});
        return IndexExtractor.fromBitMapExtractor(testingBitMapExtractor);
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        // Bit maps will be distinct. Conversion to indices should be ordered.
        return DISTINCT | ORDERED;
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[] {0, 65, 128, 129};
    }

    @Test
    public final void testFromBitMapExtractor() {
        IndexExtractor underTest = createExtractor();
        List<Integer> lst = new ArrayList<>();

        underTest.processIndices(lst::add);
        assertEquals(4, lst.size());
        assertEquals(Integer.valueOf(0), lst.get(0));
        assertEquals(Integer.valueOf(1 + 64), lst.get(1));
        assertEquals(Integer.valueOf(0 + 128), lst.get(2));
        assertEquals(Integer.valueOf(1 + 128), lst.get(3));

        final BitMapExtractor bitMapExtractor = new TestingBitMapExtractor(new long[] {0xFFFFFFFFFFFFFFFFL});
        underTest = IndexExtractor.fromBitMapExtractor(bitMapExtractor);
        lst = new ArrayList<>();

        underTest.processIndices(lst::add);

        assertEquals(64, lst.size());
        for (int i = 0; i < 64; i++) {
            assertEquals(Integer.valueOf(i), lst.get(i));
        }
    }
}
