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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;

public abstract class AbstractIndexProducerTest {

    public static final IntPredicate TRUE_PREDICATE = new IntPredicate() {

        @Override
        public boolean test(int arg0) {
            return true;
        }
    };

    public static final IntPredicate FALSE_PREDICATE = new IntPredicate() {

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

    @Test
    public final void testForEachIndex() {

        IndexProducer populated = createProducer();
        IndexProducer empty = createEmptyProducer();
        assertFalse(populated.forEachIndex(FALSE_PREDICATE), "non-empty should be false");

        assertTrue(empty.forEachIndex(FALSE_PREDICATE), "empty should be true");

        assertTrue(populated.forEachIndex(TRUE_PREDICATE), "non-empty should be true");
        assertTrue(empty.forEachIndex(TRUE_PREDICATE), "empty should be true");
    }

    @Test
    public final void testAsIndexArray() {
        int ary[] = createEmptyProducer().asIndexArray();
        assertEquals(0, ary.length);

        IndexProducer producer = createProducer();
        List<Integer> lst = new ArrayList<Integer>();
        for (int i :  producer.asIndexArray()) {
            lst.add( i );
        }
        assertTrue(producer.forEachIndex(new IntPredicate() {

            @Override
            public boolean test(int value) {
                assertTrue( lst.remove( Integer.valueOf(value) ), String.format("Instance of  %d was not found in lst", value));
                return true;
            }
        }));
    }
}
