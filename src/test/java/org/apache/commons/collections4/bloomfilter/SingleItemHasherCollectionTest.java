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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests the {@link SingleItemHasherCollection}.
 */
public class SingleItemHasherCollectionTest extends HasherCollectionTest {

    private SimpleHasher hasher1 = new SimpleHasher(1, 1);
    private SimpleHasher hasher2 = new SimpleHasher(2, 2);

    @Override
    protected SingleItemHasherCollection createHasher() {
        return new SingleItemHasherCollection(hasher1, hasher2);
    }

    @Override
    protected SingleItemHasherCollection createEmptyHasher() {
        return new SingleItemHasherCollection();
    }

    @Override
    @Test
    public void testSize() {
        HasherCollection hasher = createHasher();
        assertEquals(1, hasher.size());
        assertEquals(2, hasher.getHashers().size());
        hasher = createEmptyHasher();
        assertEquals(0, createEmptyHasher().size());
        assertEquals(0, createEmptyHasher().getHashers().size());
    }

    @Override
    @Test
    public void testAdd() {
        SingleItemHasherCollection hasher = createHasher();
        hasher.add(new SimpleHasher(2, 2));
        assertEquals(1, hasher.size());
        assertEquals(3, hasher.getHashers().size());
        hasher.add(Arrays.asList(new SimpleHasher(3, 2), new SimpleHasher(4, 2)));
        assertEquals(1, hasher.size());
        assertEquals(5, hasher.getHashers().size());
    }

    @Override
    @Test
    public void testCollectionConstructor() {
        List<Hasher> lst = Arrays.asList(new SimpleHasher(3, 2), new SimpleHasher(4, 2));
        SingleItemHasherCollectionTest nestedTest = new SingleItemHasherCollectionTest() {
            @Override
            protected SingleItemHasherCollection createHasher() {
                return new SingleItemHasherCollection(lst);
            }

            @Override
            protected SingleItemHasherCollection createEmptyHasher() {
                return new SingleItemHasherCollection();
            }
        };
        nestedTest(nestedTest);

        nestedTest = new SingleItemHasherCollectionTest() {
            @Override
            protected SingleItemHasherCollection createHasher() {
                return new SingleItemHasherCollection(new SimpleHasher(3, 2), new SimpleHasher(4, 2));
            }

            @Override
            protected SingleItemHasherCollection createEmptyHasher() {
                return new SingleItemHasherCollection();
            }
        };
        nestedTest(nestedTest);
    }

    @Override
    public void testUniqueIndex() {
        // create a hasher that produces duplicates with the specified shape.
        // this setup produces 5, 17, 29, 41, 53, 65 two times
        Shape shape = Shape.fromKM(12, 72);
        Hasher hasher = new SimpleHasher(5, 12);
        Set<Integer> set = new HashSet<>();
        assertTrue(hasher.uniqueIndices(shape).forEachIndex(set::add), "Duplicate detected");
        assertEquals(6, set.size());
    }

    @Override
    @ParameterizedTest
    @CsvSource({ "17, 72", "3, 14", "5, 67868", })
    public void testHashing(int k, int m) {
        int[] count = { 0 };
        HasherCollection hasher = createHasher();
        hasher.indices(Shape.fromKM(k, m)).forEachIndex(i -> {
            assertTrue(i >= 0 && i < m, () -> "Out of range: " + i + ", m=" + m);
            count[0]++;
            return true;
        });
        assertEquals(k * hasher.getHashers().size(), count[0],
                () -> String.format("Did not produce k=%d * m=%d indices", k, hasher.size()));
    }
}
