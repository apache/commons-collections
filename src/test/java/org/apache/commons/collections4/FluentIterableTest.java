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
package org.apache.commons.collections4;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for FluentIterable.
 *
 * @since 4.1
 * @version $Id$
 */
public class FluentIterableTest {

    /**
     * Iterable of {@link Integer}s
     */
    private Iterable<Integer> iterableA = null;

    /**
     * Iterable of {@link Long}s
     */
    private Iterable<Long> iterableB = null;

    /**
     * Collection of even {@link Integer}s
     */
    private Iterable<Integer> iterableEven = null;

    /**
     * Collection of odd {@link Integer}s
     */
    private Iterable<Integer> iterableOdd = null;

    /**
     * An empty Iterable.
     */
    private Iterable<Integer> emptyIterable = null;

    @Before
    public void setUp() {
        Collection<Integer> collectionA = new ArrayList<Integer>();
        collectionA.add(1);
        collectionA.add(2);
        collectionA.add(2);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
        iterableA = collectionA;

        Collection<Long> collectionB = new LinkedList<Long>();
        collectionB.add(5L);
        collectionB.add(4L);
        collectionB.add(4L);
        collectionB.add(3L);
        collectionB.add(3L);
        collectionB.add(3L);
        collectionB.add(2L);
        collectionB.add(2L);
        collectionB.add(2L);
        collectionB.add(2L);
        iterableB = collectionB;

        iterableEven = Arrays.asList(2, 4, 6, 8, 10, 12);
        iterableOdd = Arrays.asList(1, 3, 5, 7, 9, 11);

        emptyIterable = Collections.emptyList();
    }

    private static Predicate<Number> EVEN = new Predicate<Number>() {
        public boolean evaluate(final Number input) {
            return input.intValue() % 2 == 0;
        }
    };

    // -----------------------------------------------------------------------
    @Test
    public void allMatch() {
        assertTrue(FluentIterable.of(iterableEven).allMatch(EVEN));
        assertFalse(FluentIterable.of(iterableOdd).allMatch(EVEN));
        assertFalse(FluentIterable.of(iterableA).allMatch(EVEN));

        try {
            FluentIterable.of(iterableEven).allMatch(null);
            fail("expecting NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void anyMatch() {
        assertTrue(FluentIterable.of(iterableEven).anyMatch(EVEN));
        assertFalse(FluentIterable.of(iterableOdd).anyMatch(EVEN));
        assertTrue(FluentIterable.of(iterableA).anyMatch(EVEN));

        try {
            FluentIterable.of(iterableEven).anyMatch(null);
            fail("expecting NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void collate() {
        List<Integer> result = FluentIterable.of(iterableOdd).collate(iterableEven).toList();
        List<Integer> combinedList = new ArrayList<Integer>();
        CollectionUtils.addAll(combinedList, iterableOdd);
        CollectionUtils.addAll(combinedList, iterableEven);
        Collections.sort(combinedList);
        assertEquals(combinedList, result);

        result = FluentIterable.of(iterableOdd).collate(null).toList();
        List<Integer> expected = IterableUtils.toList(iterableOdd);
        assertEquals(expected, result);
    }

    @Test
    public void isEmpty() {
        assertTrue(FluentIterable.of(emptyIterable).isEmpty());
        assertFalse(FluentIterable.of(iterableOdd).isEmpty());
    }

    @Test
    public void contains() {
        assertTrue(FluentIterable.of(iterableEven).contains(2));
        assertFalse(FluentIterable.of(iterableEven).contains(1));
        assertFalse(FluentIterable.of(iterableEven).contains(null));
        assertTrue(FluentIterable.of(iterableEven).append((Integer) null).contains(null));
    }

    @Test
    public void get() {
        assertEquals(2, FluentIterable.of(iterableEven).get(0).intValue());

        try {
            FluentIterable.of(iterableEven).get(-1);
            fail("expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ioe) {
            // expected
        }

        try {
            FluentIterable.of(iterableEven).get(IterableUtils.size(iterableEven));
            fail("expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ioe) {
            // expected
        }
    }
}
