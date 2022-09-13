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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public abstract class AbstractHasherTest extends AbstractIndexProducerTest {

    protected abstract Hasher createHasher();

    protected abstract Hasher createEmptyHasher();

    /**
     * A method to get the number of items in a hasher.  Mostly applies to
     * Collections of hashers.
     * @param hasher the hasher to check.
     * @return the number of hashers in the hasher
     */
    protected abstract int getHasherSize(Hasher hasher);

    /**
     * The shape of the Hashers filters for testing.
     * <ul>
     *  <li>Hash functions (k) = 17
     *  <li>Number of bits (m) = 72
     * </ul>
     * @return the testing shape.
     */
    protected final Shape getTestShape() {
        return Shape.fromKM(17, 72);
    }

    @Override
    protected IndexProducer createProducer() {
        return createHasher().indices(getTestShape());
    }

    @Override
    protected IndexProducer createEmptyProducer() {
        return createEmptyHasher().indices(getTestShape());
    }

    @ParameterizedTest
    @CsvSource({ "17, 72", "3, 14", "5, 67868", "75, 10"})
    public void testHashing(int k, int m) {
        int[] count = { 0 };
        Hasher hasher = createHasher();
        hasher.indices(Shape.fromKM(k, m)).forEachIndex(i -> {
            assertTrue(i >= 0 && i < m, () -> "Out of range: " + i + ", m=" + m);
            count[0]++;
            return true;
        });
        assertEquals(k * getHasherSize(hasher), count[0],
                () -> String.format("Did not produce k=%d * m=%d indices", k, getHasherSize(hasher)));

        // test early exit
        count[0] = 0;
        hasher.indices(Shape.fromKM(k, m)).forEachIndex(i -> {
            assertTrue(i >= 0 && i < m, () -> "Out of range: " + i + ", m=" + m);
            count[0]++;
            return false;
        });
        assertEquals(1, count[0], "did not exit early");
    }

    @Test
    public void testUniqueIndex() {
        // generating 11 numbers in the range of [0,9] will yield at least on collision.
        Shape shape = Shape.fromKM(11, 10);
        Hasher hasher = createHasher();
        IndexProducer producer = hasher.indices(shape);
        List<Integer> full = Arrays.stream(producer.asIndexArray()).boxed().collect(Collectors.toList());
        producer = hasher.uniqueIndices(shape);
        List<Integer> unique = Arrays.stream(producer.asIndexArray()).boxed().collect(Collectors.toList());
        assertTrue(full.size() > unique.size());
        Set<Integer> set = new HashSet<>(unique);
        assertEquals(set.size(), unique.size());
    }
}
