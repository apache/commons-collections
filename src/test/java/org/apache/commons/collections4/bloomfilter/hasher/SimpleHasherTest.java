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
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;
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
    }

    @Test
    public void testMod() {

        long dividend;
        int divisor;

        dividend = 4133050040864586807L;
        divisor = 1110442806;
        assertEquals(SimpleHasher.mod(dividend, divisor), (int) Long.remainderUnsigned(dividend, divisor),
                String.format("failure with dividend=%s and divisor=%s.", dividend, divisor));

        Random r = new Random();
        for (int i = 0; i < 10000; i++) {
            dividend = r.nextLong();
            divisor = Math.abs(r.nextInt());
            assertEquals(SimpleHasher.mod(dividend, divisor), (int) Long.remainderUnsigned(dividend, divisor),
                    String.format("failure with dividend=%s and divisor=%s.  Please correct and add to test cases",
                            dividend, divisor));
        }
    }
}
