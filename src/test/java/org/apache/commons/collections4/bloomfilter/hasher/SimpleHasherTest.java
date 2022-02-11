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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

//    @Test
//    void testModEdgeCases() {
//        for (long dividend : new long[] {-1, -2, -3, -6378683, -23567468136887892L, Long.MIN_VALUE,
//                345, 678686, 67868768686878924L, Long.MAX_VALUE}) {
//            for (int divisor : new int[] {1, 2, 3, 5, 13, Integer.MAX_VALUE}) {
//                        SimpleHasher.mod(dividend, divisor),
//                        () -> String.format("failure with dividend=%s and divisor=%s.", dividend, divisor));
//            }
//        }
//    }

    @Override
    public void testUniqueIndex() {
        // create a hasher that produces duplicates with the specified shape.
        // this setup produces 5, 17, 29, 41, 53, 65 two times
        Shape shape = Shape.fromKM( 12, 72 );
        Hasher hasher = new SimpleHasher( 5, 12 );
        Set<Integer> set = new HashSet<>();
        assertTrue( hasher.uniqueIndices( shape ).forEachIndex( set::add ), "Duplicate detected");
        assertEquals( 6, set.size() );
    }

}
