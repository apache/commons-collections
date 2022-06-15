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
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link SimpleHasher}.
 */
public class SimpleHasherTest extends AbstractHasherTest {

    @Override
    protected Hasher createHasher() {
        return new SimpleHasher(1, 1);
    }

    @Override
    protected Hasher createEmptyHasher() {
        return NullHasher.INSTANCE;
    }

    @Override
    protected int getHasherSize(Hasher hasher) {
        return 1;
    }

    private void assertConstructorBuffer(Shape shape, byte[] buffer, Integer[] expected) {
        SimpleHasher hasher = new SimpleHasher(buffer);
        List<Integer> lst = new ArrayList<>();
        IndexProducer producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }
    }

    private void assertIncrement(SimpleHasher hasher, long defaultIncrement) {
        assertEquals(defaultIncrement, hasher.getDefaultIncrement());
        int[] values = hasher.indices(Shape.fromKM(2, Integer.MAX_VALUE)).asIndexArray();
        assertEquals(0, values[0]);
        assertEquals(Long.remainderUnsigned(defaultIncrement, Integer.MAX_VALUE), values[1]);
    }

    @Test
    public void testConstructor() {
        Shape shape = Shape.fromKM(5, 10);
        assertConstructorBuffer(shape, new byte[] { 1, 1 }, new Integer[] { 1, 2, 3, 4, 5 });
        assertConstructorBuffer(shape, new byte[] { 1 }, new Integer[] { 0, 1, 2, 3, 4 });
        assertConstructorBuffer(shape, new byte[] { 1, 0, 1 }, new Integer[] { 1, 2, 3, 4, 5 });
        assertConstructorBuffer(shape, new byte[] { 0, 1, 0, 1 }, new Integer[] { 1, 2, 3, 4, 5 });
        assertConstructorBuffer(shape, new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1 },
                new Integer[] { 1, 2, 3, 4, 5 });
        assertConstructorBuffer(shape, new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 5, 5, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5 },
                new Integer[] { 1, 2, 3, 4, 5 });
        assertConstructorBuffer(shape, new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 5, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5 },
                new Integer[] { 1, 2, 3, 4, 5 });

        // test empty buffer
        assertThrows(IllegalArgumentException.class, () -> new SimpleHasher(new byte[0]));

        // test zero incrementer gets default
        // default increment from SimpleHasher.
        long defaultIncrement = 0x9e3779b97f4a7c15L;
        SimpleHasher hasher = new SimpleHasher(0, 0);
        assertIncrement(new SimpleHasher(0, 0), defaultIncrement);
        assertIncrement(new SimpleHasher(new byte[2]), defaultIncrement);

        // test that changing default increment works
        defaultIncrement = 4;
        defaultIncrement = 4L;
        hasher = new SimpleHasher(0, 0) {
            @Override
            public long getDefaultIncrement() {
                return 4L;
            }
        };
        assertIncrement(hasher, defaultIncrement);
        hasher = new SimpleHasher(new byte[2]) {
            @Override
            public long getDefaultIncrement() {
                return 4L;
            }
        };

        assertEquals(defaultIncrement, hasher.getDefaultIncrement());
    }

    @Test
    void testModEdgeCases() {
        for (long dividend : new long[] { -1, -2, -3, -6378683, -23567468136887892L, Long.MIN_VALUE, 345, 678686,
            67868768686878924L, Long.MAX_VALUE }) {
            for (int divisor : new int[] { 1, 2, 3, 5, 13, Integer.MAX_VALUE }) {
                assertEquals((int) Long.remainderUnsigned(dividend, divisor), SimpleHasher.mod(dividend, divisor),
                        () -> String.format("failure with dividend=%s and divisor=%s.", dividend, divisor));
            }
        }
    }
}
