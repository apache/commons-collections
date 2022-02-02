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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link SimpleHasher}.
 */
public class SimpleHasherTest {

    private SimpleHasher hasher = new SimpleHasher(1, 1);

    @Test
    public void testSize() {
        assertEquals(1, hasher.size());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(hasher.isEmpty());
    }

    @Test
    public void testIterator() {
        Shape shape = Shape.fromKM(5, 10);
        Integer[] expected = { 1, 2, 3, 4, 5 };
        List<Integer> lst = new ArrayList<>();
        IndexProducer producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }
    }

    private void assertConstructorBuffer(Shape shape, byte[] buffer, Integer[] expected) {
        SimpleHasher hasher = new SimpleHasher(buffer);
        List<Integer> lst = new ArrayList<>();
        IndexProducer producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }
    }

    @Test
    public void testConstructor() {

        Shape shape = Shape.fromKM(5, 10);
        assertConstructorBuffer(shape, new byte[] { 1, 1 }, new Integer[] { 1, 2, 3, 4, 5 });
        assertConstructorBuffer(shape, new byte[] { 1 }, new Integer[] { 0, 1, 2, 3, 4 });
        assertConstructorBuffer(shape, new byte[] { 1, 0, 1 }, new Integer[] { 1, 2, 3, 4, 5 });
        assertConstructorBuffer(shape, new byte[] { 0, 1, 0, 1 }, new Integer[] { 1, 2, 3, 4, 5 });
        assertConstructorBuffer(shape, new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1 },
                new Integer[] { 1, 2, 3, 4, 5 });
        assertConstructorBuffer(shape, new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 5, 5, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5 },
                new Integer[] { 1, 2, 3, 4, 5 });
        ;
        assertConstructorBuffer(shape, new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 5, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5 },
                new Integer[] { 1, 2, 3, 4, 5 });

        // test empty buffer
        assertThrows(IllegalArgumentException.class, () -> new SimpleHasher(new byte[0]));
}

}
