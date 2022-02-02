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
 * Tests the {@link HasherCollection}.
 */
public class HasherCollectionTest {

    private SimpleHasher hasher1 = new SimpleHasher(1, 1);
    private SimpleHasher hasher2 = new SimpleHasher(2, 2);

    @Test
    public void sizeTest() {
        HasherCollection hasher = new HasherCollection(hasher1, hasher2);
        assertEquals(2, hasher.size());
        HasherCollection hasher3 = new HasherCollection(hasher, new SimpleHasher(3, 3));
        assertEquals(3, hasher3.size());
    }

    @Test
    public void isEmptyTest() {
        HasherCollection hasher = new HasherCollection();
        assertTrue(hasher.isEmpty());
        hasher.add(hasher1);
        assertFalse(hasher.isEmpty());
    }

    @Test
    public void testIndices() {
        // use Arrays.asList to test Collection constructor
        HasherCollection hasher = new HasherCollection(Arrays.asList(new Hasher[] { hasher1, hasher2 }));
        assertEquals(2, hasher.size());
        Shape shape = Shape.fromKM(5, 10);
        Integer[] expected = { 1, 2, 3, 4, 5, 2, 4, 6, 8, 0 };
        List<Integer> lst = new ArrayList<Integer>();
        IndexProducer producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals( expected[i], lst.get(i), String.format("error at position %d", i));
        }
    }

    @Test
    public void testAdd_collection() {
        HasherCollection hasher = new HasherCollection();
        hasher.add(Arrays.asList(hasher1, hasher2));
        assertEquals(2, hasher.size());
    }

}
