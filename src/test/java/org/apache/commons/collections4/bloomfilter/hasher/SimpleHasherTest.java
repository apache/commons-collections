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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void constructor_byteTest() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleHasher(new byte[0]));
    }

    @Test
    public void sizeTest() {
        assertEquals(1, hasher.size());
    }

    @Test
    public void isEmptyTest() {
        assertFalse(hasher.isEmpty());
    }

    @Test
    public void testIterator() {
        Shape shape = new Shape(5, 10);
        Integer[] expected = { 1, 2, 3, 4, 5 };
        List<Integer> lst = new ArrayList<Integer>();
        IndexProducer producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }
    }

    @Test
    public void constructorBufferTest() {
        Shape shape = new Shape(5, 10);
        byte[] buffer = { 1, 1 };
        SimpleHasher hasher = new SimpleHasher(buffer);
        Integer[] expected = { 1, 2, 3, 4, 5 };
        List<Integer> lst = new ArrayList<Integer>();
        IndexProducer producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }

        buffer = new byte[] { 1 };
        hasher = new SimpleHasher(buffer);
        expected = new Integer[] { 0, 1, 2, 3, 4 };
        lst = new ArrayList<Integer>();
        producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }

        buffer = new byte[] { 1, 0, 1 };
        hasher = new SimpleHasher(buffer);
        expected = new Integer[] { 1, 2, 3, 4, 5 };
        lst = new ArrayList<Integer>();
        producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }

        buffer = new byte[] { 0, 1, 0, 1 };
        hasher = new SimpleHasher(buffer);
        expected = new Integer[] { 1, 2, 3, 4, 5 };
        lst = new ArrayList<Integer>();
        producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }

        buffer = new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1 };
        hasher = new SimpleHasher(buffer);
        expected = new Integer[] { 1, 2, 3, 4, 5 };
        lst = new ArrayList<Integer>();
        producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }

        buffer = new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 5, 5, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5 };
        hasher = new SimpleHasher(buffer);
        expected = new Integer[] { 1, 2, 3, 4, 5 };
        lst = new ArrayList<Integer>();
        producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }

        buffer = new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 5, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5 };
        hasher = new SimpleHasher(buffer);
        expected = new Integer[] { 1, 2, 3, 4, 5 };
        lst = new ArrayList<Integer>();
        producer = hasher.indices(shape);
        producer.forEachIndex(lst::add);
        assertEquals(expected.length, lst.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], lst.get(i));
        }
    }

}
