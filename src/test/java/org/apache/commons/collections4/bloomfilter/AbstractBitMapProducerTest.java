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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.function.LongPredicate;

import org.junit.jupiter.api.Test;

public abstract class AbstractBitMapProducerTest {

    /**
     * A testing consumer that always returns false.
     */
    public static final LongPredicate FALSE_CONSUMER = new LongPredicate() {

        @Override
        public boolean test(long arg0) {
            return false;
        }
    };

    /**
     * A testing consumer that always returns true.
     */
    public static final LongPredicate TRUE_CONSUMER = new LongPredicate() {

        @Override
        public boolean test(long arg0) {
            return true;
        }
    };

    /**
     * Creates a producer with some data.
     * @return a producer with some data
     */
    protected abstract BitMapProducer createProducer();

    /**
     * Creates an producer without data.
     * @return a producer that has no data.
     */
    protected abstract BitMapProducer createEmptyProducer();

    protected boolean emptyIsZeroLength() {
        return false;
    }

    @Test
    public final void testForEachBitMap() {
        assertFalse(createProducer().forEachBitMap(FALSE_CONSUMER), "non-empty should be false");
        if (emptyIsZeroLength()) {
            assertTrue(createEmptyProducer().forEachBitMap(FALSE_CONSUMER), "empty should be true");
        } else {
            assertFalse(createEmptyProducer().forEachBitMap(FALSE_CONSUMER), "empty should be false");
        }

        assertTrue(createProducer().forEachBitMap(TRUE_CONSUMER), "non-empty should be true");
        assertTrue(createEmptyProducer().forEachBitMap(TRUE_CONSUMER), "empty should be true");
    }

    @Test
    public final void testAsBitMapArray() {
        long[] array = createEmptyProducer().asBitMapArray();
        for (int i = 0; i < array.length; i++) {
            assertEquals(0, array[i], "Wrong value at " + i);
        }

        array = createProducer().asBitMapArray();
        assertFalse(array.length == 0);
    }

    @Test
    public final void testMakePredicate() {
        LongPredicate predicate = createEmptyProducer().makePredicate((x, y) -> x == y);
        assertTrue(createEmptyProducer().forEachBitMap(predicate), "empty == empty failed");

        predicate = createEmptyProducer().makePredicate((x, y) -> x == y);
        assertFalse(createProducer().forEachBitMap(predicate), "empty == not_empty failed");

        predicate = createProducer().makePredicate((x, y) -> x == y);
        if (emptyIsZeroLength()) {
            assertTrue(createEmptyProducer().forEachBitMap(predicate), "not_empty == empty failed");
        } else {
            assertFalse(createEmptyProducer().forEachBitMap(predicate), "not_empty == empty passed");
        }

        predicate = createProducer().makePredicate((x, y) -> x == y);
        assertTrue(createProducer().forEachBitMap(predicate), "not_empty == not_empty failed");

        // test BitMapProducers of different length send 0 for missing values.
        int[] count = new int[3];
        LongBiPredicate lbp = new LongBiPredicate() {

            @Override
            public boolean test(long x, long y) {
                if (x == 0) {
                    count[0]++;
                }
                if (y == 0) {
                    count[1]++;
                }
                count[2]++;
                return true;
            }
        };
        predicate = createEmptyProducer().makePredicate(lbp);
        createProducer().forEachBitMap(predicate);
        assertEquals(count[2], count[0]);

        Arrays.fill(count, 0);
        predicate = createProducer().makePredicate(lbp);
        createEmptyProducer().forEachBitMap(predicate);
        assertEquals(count[2], count[1]);
    }

    @Test
    public void testForEachBitMapEarlyExit() {
        int[] passes = new int[1];
        assertFalse(createProducer().forEachBitMap(l -> {
            passes[0]++;
            return false;
        }));
        assertEquals(1, passes[0]);

        passes[0] = 0;
        if (emptyIsZeroLength()) {
            assertTrue(createEmptyProducer().forEachBitMap(l -> {
                passes[0]++;
                return false;
            }));
            assertEquals(0, passes[0]);
        } else {
            assertFalse(createEmptyProducer().forEachBitMap(l -> {
                passes[0]++;
                return false;
            }));
            assertEquals(1, passes[0]);
        }
    }

    @Test
    public void testMakePredicateEarlyExit() {

        // test BitMapProducers of different length send 0 for missing values.
        int[] count = new int[1];
        LongBiPredicate lbp = new LongBiPredicate() {

            @Override
            public boolean test(long x, long y) {
                count[0]++;
                return false;
            }
        };
        LongPredicate predicate = createEmptyProducer().makePredicate(lbp);
        createProducer().forEachBitMap(predicate);
        assertEquals(1, count[0]);

        Arrays.fill(count, 0);
        predicate = createProducer().makePredicate(lbp);
        createEmptyProducer().forEachBitMap(predicate);
        assertEquals(emptyIsZeroLength() ? 0 : 1, count[0]);

    }
}
