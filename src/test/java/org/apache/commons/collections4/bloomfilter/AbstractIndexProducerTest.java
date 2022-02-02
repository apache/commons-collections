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

import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;

public abstract class AbstractIndexProducerTest {

    public final static IntPredicate TRUE_PREDICATE = new IntPredicate() {

        @Override
        public boolean test(int arg0) {
            return true;
        }
    };

    public final static IntPredicate FALSE_PREDICATE = new IntPredicate() {

        @Override
        public boolean test(int arg0) {
            return false;
        }
    };
    /**
     * Creates a producer with some data.
     * @return a producer with some data
     */
    protected abstract IndexProducer createProducer();

    /**
     * Creates an producer without data.
     * @return a producer that has no data.
     */
    protected abstract IndexProducer createEmptyProducer();

    /**
     * Determines if empty tests should be run.  Some producers do not implement an empty
     * version.  Tests for those classes should return false.
     * @return
     */
    protected boolean supportsEmpty() {
        return true;
    }

    @Test
    public final void testForEachIndex() {

        assertFalse(createProducer().forEachIndex(FALSE_PREDICATE), "non-empty should be false");
        if (supportsEmpty()) {
            assertTrue(createEmptyProducer().forEachIndex(FALSE_PREDICATE), "empty should be true");
        }

        assertTrue(createProducer().forEachIndex(TRUE_PREDICATE), "non-empty should be true");
        if (supportsEmpty()) {
            assertTrue(createEmptyProducer().forEachIndex(TRUE_PREDICATE), "empty should be true");
        }
    }
}
