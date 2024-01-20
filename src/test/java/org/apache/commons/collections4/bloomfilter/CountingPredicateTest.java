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
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

public class CountingPredicateTest {

    private Integer[] ary = {Integer.valueOf(1), Integer.valueOf(2)};

    private BiPredicate<Integer, Integer> makeFunc(BiPredicate<Integer, Integer> inner, List<Pair<Integer, Integer>> result) {
        return (x, y) -> {
            if (inner.test(x, y)) {
                result.add(Pair.of(x, y));
                return true;
            }
            return false;
        };
    }

    /**
     * Test when the predicate array is longer than other array as determined by the number
     * of times cp.test() is called and all other values result in a true statement.
     */
    @Test
    public void testPredicateLonger() {
        List<Pair<Integer, Integer>> expected = new ArrayList<>();
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        expected.add(Pair.of(1, 3));

        CountingPredicate<Integer> cp = new CountingPredicate<>(ary, makeFunc((x, y) -> x!=null, result));
        assertTrue(cp.test(Integer.valueOf(3)));
        assertEquals(expected, result);
        expected.add(Pair.of(2, null));
        assertTrue(cp.forEachRemaining());
        assertEquals(expected, result);

        // if the other array is zero length then cp.test() will not be called so
        // we can just call cp.forEachRemaining() here.
        expected.clear();
        expected.add(Pair.of(1, null));
        expected.add(Pair.of(2, null));
        result.clear();
        cp = new CountingPredicate<>(ary, makeFunc((x, y) -> x!=null, result));
        assertTrue(cp.forEachRemaining());
        assertEquals( expected, result);

        // If a test fails then the result should be false and the rest of the list should
        // not be processed.
        expected.clear();
        expected.add(Pair.of(1, null));
        result.clear();
        cp = new CountingPredicate<>(ary,  makeFunc((x, y) -> x == Integer.valueOf(1), result));
        assertFalse(cp.forEachRemaining());
        assertEquals(expected, result);
    }

    /**
     * Test when the predicate array is shorter than other array as determined by the number
     * of times cp.test() is called and all other values result in a true statement.
     */
    @Test
    public void testPredicateSameLength() {
        List<Pair<Integer, Integer>> expected = new ArrayList<>();
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        expected.add( Pair.of(1, 3));
        expected.add( Pair.of(2, 3));
        CountingPredicate<Integer> cp = new CountingPredicate<>(ary, makeFunc((x, y) -> true, result));
        assertTrue(cp.test(3));
        assertTrue(cp.test(3));
        assertEquals(expected, result);
        assertTrue(cp.forEachRemaining());
        assertEquals(expected, result);
    }

    /**
     * Test when the predicate array is shorter than other array as determined by the number
     * of times cp.test() is called and all other values result in a true statement.
     */
    @Test
    public void testPredicateShorter() {
        List<Pair<Integer, Integer>> expected = new ArrayList<>();
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        Integer[] shortAry = {Integer.valueOf(3)};
        expected.add(Pair.of(3, 1));
        expected.add(Pair.of(null, 2));
        CountingPredicate<Integer> cp = new CountingPredicate<>(shortAry, makeFunc((x, y) -> true, result));
        for (Integer i : ary) {
            assertTrue(cp.test(i));
        }
        assertEquals(expected, result);
        assertTrue(cp.forEachRemaining());
        assertEquals(expected, result);
    }
}
