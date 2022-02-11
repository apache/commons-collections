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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.bloomfilter.Filter;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.apache.commons.collections4.bloomfilter.Filter.ArrayTracker;
import org.apache.commons.collections4.bloomfilter.Filter.BitMapTracker;
import org.junit.jupiter.api.Test;

/**
 * Tests the Filter class.
 */
public class FilterTest {

    @Test
    public void testFiltering() {
        Set<Integer> tracker = new HashSet<Integer>();
        Shape shape = Shape.fromKM(3, 12);
        List<Integer> consumer = new ArrayList<Integer>();
        Filter filter = new Filter(shape, consumer::add, (i) -> {
            return !tracker.add(i);
        });

        for (int i = 0; i < 12; i++) {
            assertTrue(filter.test(i));
        }
        assertEquals(12, tracker.size());
        assertEquals(12, consumer.size());

        for (int i = 0; i < 12; i++) {
            assertTrue(filter.test(i));
        }
        assertEquals(12, tracker.size());
        assertEquals(12, consumer.size());

        assertThrows(IndexOutOfBoundsException.class, () -> filter.test(12));
        assertThrows(IndexOutOfBoundsException.class, () -> filter.test(-1));
    }

    @Test
    public void testConstructor()
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Field tracker = Filter.class.getDeclaredField("tracker");
        tracker.setAccessible(true);
        List<Integer> consumer = new ArrayList<Integer>();

        // test even split
        int k = 2;
        int m = Long.SIZE;
        Shape shape = Shape.fromKM(k, m);
        Filter filter = new Filter(shape, consumer::add);
        assertTrue(tracker.get(filter) instanceof ArrayTracker);

        // test k ints < longs for m
        k = 1;
        shape = Shape.fromKM(k, m);
        filter = new Filter(shape, consumer::add);
        assertTrue(tracker.get(filter) instanceof ArrayTracker);

        // test k ints > longs for m
        k = 3;
        shape = Shape.fromKM(k, m);
        filter = new Filter(shape, consumer::add);
        assertTrue(tracker.get(filter) instanceof BitMapTracker);
    }
}
