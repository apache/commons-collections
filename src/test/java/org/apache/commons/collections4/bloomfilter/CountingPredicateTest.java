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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CountingPredicateTest {

    @Test
    public void testAryShort() {
        CountingPredicate<Integer> cp = new CountingPredicate<>(new Integer[0], (x, y) -> x == null);
        assertTrue(cp.test(Integer.valueOf(1)));
    }

    @Test
    public void testAryLong() {
        Integer[] ary = { Integer.valueOf(1), Integer.valueOf(2) };
        CountingPredicate<Integer> cp = new CountingPredicate<>(ary, (x, y) -> y == null);
        assertTrue(cp.forEachRemaining());

        // test last item not checked
        cp = new CountingPredicate<>(ary, (x, y) -> y == Integer.valueOf(2));
        assertFalse(cp.forEachRemaining());

        // test last item fails
        cp = new CountingPredicate<>(ary, (x, y) -> y == Integer.valueOf(1));
        assertFalse(cp.forEachRemaining());
    }
}
