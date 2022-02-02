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
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link SingleItemHasherCollection}.
 */
public class SingleItemHasherCollectionTest {

    private SimpleHasher hasher1 = new SimpleHasher(1, 1);
    private SimpleHasher hasher2 = new SimpleHasher(2, 2);

    @Test
    public void sizeTest() {
        SingleItemHasherCollection hasher = new SingleItemHasherCollection();
        assertEquals(0, hasher.size());
        hasher.add(new Hasher() {

            @Override
            public IndexProducer indices(Shape shape) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }
        });
        assertEquals(0, hasher.size());
        hasher.add(hasher1);
        hasher.add(hasher2);
        assertEquals(1, hasher.size());
        HasherCollection hasher3 = new SingleItemHasherCollection(hasher, new SimpleHasher(3, 3));
        assertEquals(1, hasher3.size());

    }

    @Test
    public void isEmptyTest() {
        SingleItemHasherCollection hasher = new SingleItemHasherCollection();
        assertTrue(hasher.isEmpty());
        hasher.add(new Hasher() {

            @Override
            public IndexProducer indices(Shape shape) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }
        });
        assertTrue(hasher.isEmpty());
        hasher.add(hasher1);
        assertFalse(hasher.isEmpty());
    }

    @Test
    public void testIndices() {
        HasherCollection hasher = new SingleItemHasherCollection(hasher1, hasher2);
        Shape shape = Shape.fromKM(5, 10);
        Integer[] expected = { 1, 2, 3, 4, 5, 6, 8, 0 };
        List<Integer> lst = new ArrayList<Integer>();
        IndexProducer producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }
    }

    @Test
    public void testConstructor_with_list() {
        HasherCollection hasher = new SingleItemHasherCollection(Arrays.asList(new Hasher[] { hasher1, hasher2 }));
        Shape shape = Shape.fromKM(5, 10);
        Integer[] expected = { 1, 2, 3, 4, 5, 6, 8, 0 };
        List<Integer> lst = new ArrayList<Integer>();
        IndexProducer producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }
    }

    @Test
    public void testAdd_collection() {
        HasherCollection hasher = new SingleItemHasherCollection();
        hasher.add(Arrays.asList(hasher1, hasher2));
        assertEquals(1, hasher.size());
        Integer[] expected = { 1, 2, 3, 4, 5, 6, 8, 0 };
        List<Integer> lst = new ArrayList<Integer>();
        IndexProducer producer = hasher.indices(Shape.fromKM(5, 10));
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }
    }
}
