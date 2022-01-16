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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.LongPredicate;

import org.junit.Test;

public abstract class AbstractBitMapProducerTest {

    /**
     * A testing consumer that always returns false.
     */
    public static LongPredicate FALSE_CONSUMER = new LongPredicate() {

        @Override
        public boolean test(long arg0) {
            return false;
        }
    };

    /**
     * A testing consumer that always returns true.
     */
    public static LongPredicate TRUE_CONSUMER = new LongPredicate() {

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
    public void forEachBitMap_test_false() {

        assertFalse("non-empty should be false", createProducer().forEachBitMap(FALSE_CONSUMER));
        assertTrue("empty should be true", createEmptyProducer().forEachBitMap(FALSE_CONSUMER));

    }

    @Test
    public void forEachBitMap_test_true() {
        assertTrue("non-empty should be true", createProducer().forEachBitMap(TRUE_CONSUMER));
        assertTrue("empty should be true", createEmptyProducer().forEachBitMap(TRUE_CONSUMER));

    }
}
