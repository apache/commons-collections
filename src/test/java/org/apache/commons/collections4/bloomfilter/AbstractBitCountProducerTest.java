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

import org.junit.Test;
import org.apache.commons.collections4.bloomfilter.BitCountProducer.BitCountConsumer;

public abstract class AbstractBitCountProducerTest {

    /**
     * Creates a producer with some data.
     * @return a producer with some data
     */
    protected abstract BitCountProducer createProducer();

    /**
     * Creates an producer without data.
     * @return a producer that has no data.
     */
    protected abstract BitCountProducer createEmptyProducer();

    /**
     * Determines if empty tests should be run.  Some producers do not implement an empty
     * version.  Tests for those classes should return false.
     * @return
     */
    protected boolean supportsEmpty() {
        return true;
    }

    @Test
    public void forEachCount_test_false() {

        BitCountConsumer consumer = new BitCountConsumer() {

            @Override
            public boolean test(int index, int count) {
                return false;
            }
        };

        assertFalse("non-empty should be false", createProducer().forEachCount(consumer));
        if (supportsEmpty()) {
            assertTrue("empty should be true", createEmptyProducer().forEachCount(consumer));
        }
    }

    @Test
    public void forEachCount_test_true() {
        BitCountConsumer consumer = new BitCountConsumer() {

            @Override
            public boolean test(int index, int count) {
                return true;
            }
        };

        assertTrue("non-empty should be true", createProducer().forEachCount(consumer));
        if (supportsEmpty()) {
            assertTrue("empty should be true", createEmptyProducer().forEachCount(consumer));
        }
    }
}
