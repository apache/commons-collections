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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public final void testForEachBitMap() {
        assertFalse(createProducer().forEachBitMap(FALSE_CONSUMER), "non-empty should be false");
        assertTrue(createEmptyProducer().forEachBitMap(FALSE_CONSUMER), "empty should be true");
        assertTrue(createProducer().forEachBitMap(TRUE_CONSUMER), "non-empty should be true");
        assertTrue(createEmptyProducer().forEachBitMap(TRUE_CONSUMER), "empty should be true");
    }
}
