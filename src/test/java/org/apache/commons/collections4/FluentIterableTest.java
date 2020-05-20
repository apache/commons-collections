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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for FluentIterable.
 *
 * @since 4.1
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
        final Collection<Integer> collectionA = new ArrayList<>();
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

        final Collection<Long> collectionB = new LinkedList<>();
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

    private static Predicate<Number> EVEN = input -> input.intValue() % 2 == 0;

    // -----------------------------------------------------------------------
    @Test
    public void factoryMethodOf() {
        FluentIterable<Integer> iterable = FluentIterable.of(1, 2, 3, 4, 5);
        List<Integer> result = iterable.toList();
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result);

        iterable = FluentIterable.of(1);
        assertEquals(1, iterable.size());
        assertFalse(iterable.isEmpty());
        assertEquals(Arrays.asList(1), iterable.toList());

        result = FluentIterable.of(new Integer[0]).toList();
        assertTrue(result.isEmpty());

        final Iterable<Integer> it = null;
        Exception exception = assertThrows(NullPointerException.class, () -> {
            FluentIterable.of(it).toList();
        });
        assertTrue(exception.getMessage().contains("iterable"));
    }

    @Test
    public void appendElements() {
        final FluentIterable<Integer> it = FluentIterable.of(iterableA).append(10, 20, 30);
        assertEquals(IterableUtils.size(iterableA) + 3, IterableUtils.size(it));
        assertTrue(IterableUtils.contains(it, 1));
        assertTrue(IterableUtils.contains(it, 10));
        assertTrue(IterableUtils.contains(it, 20));
        assertTrue(IterableUtils.contains(it, 30));
        assertFalse(IterableUtils.contains(it, 40));

        final FluentIterable<Integer> empty = FluentIterable.of(emptyIterable).append();
        assertTrue(IterableUtils.isEmpty(empty));
    }

    @Test
    public void appendIterable() {
        final List<Integer> listB = Arrays.asList(10, 20, 30);
        final FluentIterable<Integer> it = FluentIterable.of(iterableA).append(listB);
        assertEquals(IterableUtils.size(iterableA) + listB.size(), IterableUtils.size(it));
        assertTrue(IterableUtils.contains(it, 1));
        assertTrue(IterableUtils.contains(it, 10));
        assertTrue(IterableUtils.contains(it, 20));
        assertTrue(IterableUtils.contains(it, 30));
        assertFalse(IterableUtils.contains(it, 40));
    }

    @Test
    public void collate() {
        final List<Integer> result = FluentIterable.of(iterableOdd).collate(iterableEven).toList();
        final List<Integer> combinedList = new ArrayList<>();
        CollectionUtils.addAll(combinedList, iterableOdd);
        CollectionUtils.addAll(combinedList, iterableEven);
        Collections.sort(combinedList);
        assertEquals(combinedList, result);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            FluentIterable.of(iterableOdd).collate(null).toList();
        });
        assertTrue(exception.getMessage().contains("iterable"));
    }

    @Test
    public void collateWithComparator() {
        List<Integer> result =
                FluentIterable
                    .of(iterableOdd)
                    .collate(iterableEven, ComparatorUtils.<Integer>naturalComparator())
                    .toList();

        final List<Integer> combinedList = new ArrayList<>();
        CollectionUtils.addAll(combinedList, iterableOdd);
        CollectionUtils.addAll(combinedList, iterableEven);
        Collections.sort(combinedList);
        assertEquals(combinedList, result);

        // null comparator is equivalent to natural ordering
        result = FluentIterable.of(iterableOdd).collate(iterableEven, null).toList();
        assertEquals(combinedList, result);
    }

    @Test
    public void filter() {
        final Predicate<Integer> smallerThan3 = object -> object.intValue() < 3;
        List<Integer> result = FluentIterable.of(iterableA).filter(smallerThan3).toList();
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(1, 2, 2), result);

        // empty iterable
        result = FluentIterable.of(emptyIterable).filter(smallerThan3).toList();
        assertEquals(0, result.size());

        Exception exception = assertThrows(NullPointerException.class, () -> {
            FluentIterable.of(iterableA).filter(null).toList();
        });
        assertTrue(exception.getMessage().contains("predicate"));
    }

    @Test
    public void forEach() {
        final AtomicInteger sum = new AtomicInteger(0);
        final Closure<Integer> closure = input -> sum.addAndGet(input);

        FluentIterable.of(iterableA).forEach(closure);
        int expectedSum = 0;
        for (final Integer i : iterableA) {
            expectedSum += i;
        }
        assertEquals(expectedSum, sum.get());

        Exception exception = assertThrows(NullPointerException.class, () -> {
            FluentIterable.of(iterableA).forEach((Closure<Integer>) null);
        });
        assertTrue(exception.getMessage().contains("closure"));
    }

    @Test
    public void limit() {
        List<Integer> result = FluentIterable.of(iterableA).limit(3).toList();
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(1, 2, 2), result);

        // limit larger than input
        result = FluentIterable.of(iterableA).limit(100).toList();
        final List<Integer> expected = IterableUtils.toList(iterableA);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);

        // limit is 0
        result = FluentIterable.of(iterableA).limit(0).toList();
        assertEquals(0, result.size());

        // empty iterable
        result = FluentIterable.of(emptyIterable).limit(3).toList();
        assertEquals(0, result.size());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FluentIterable.of(iterableA).limit(-2).toList();
        });
        assertTrue(exception.getMessage().contains("MaxSize parameter must not be negative."));
    }

    @Test
    public void reverse() {
        List<Integer> result = FluentIterable.of(iterableA).reverse().toList();
        final List<Integer> expected = IterableUtils.toList(iterableA);
        Collections.reverse(expected);
        assertEquals(expected, result);

        // empty iterable
        result = FluentIterable.of(emptyIterable).reverse().toList();
        assertEquals(0, result.size());
    }

    @Test
    public void skip() {
        List<Integer> result = FluentIterable.of(iterableA).skip(4).toList();
        assertEquals(6, result.size());
        assertEquals(Arrays.asList(3, 3, 4, 4, 4, 4), result);

        // skip larger than input
        result = FluentIterable.of(iterableA).skip(100).toList();
        assertEquals(0, result.size());

        // skip 0 elements
        result = FluentIterable.of(iterableA).skip(0).toList();
        final List<Integer> expected = IterableUtils.toList(iterableA);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);

        // empty iterable
        result = FluentIterable.of(emptyIterable).skip(3).toList();
        assertEquals(0, result.size());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FluentIterable.of(iterableA).skip(-4).toList();
        });
        assertTrue(exception.getMessage().contains("ElementsToSkip parameter must not be negative"));
    }

    @Test
    public void transform() {
        final Transformer<Integer, Integer> squared = object -> object * object;
        List<Integer> result = FluentIterable.of(iterableA).transform(squared).toList();
        assertEquals(10, result.size());
        assertEquals(Arrays.asList(1, 4, 4, 9, 9, 9, 16, 16, 16, 16), result);

        // empty iterable
        result = FluentIterable.of(emptyIterable).transform(squared).toList();
        assertEquals(0, result.size());

        Exception exception = assertThrows(NullPointerException.class, () -> {
            FluentIterable.of(iterableA).transform(null).toList();
        });
        assertTrue(exception.getMessage().contains("transformer"));
    }

    @Test
    public void unique() {
        List<Integer> result = FluentIterable.of(iterableA).unique().toList();
        assertEquals(4, result.size());
        assertEquals(Arrays.asList(1, 2, 3, 4), result);

        // empty iterable
        result = FluentIterable.of(emptyIterable).unique().toList();
        assertEquals(0, result.size());
    }

    @Test
    public void unmodifiable() {
        final FluentIterable<Integer> iterable1 = FluentIterable.of(iterableA).unmodifiable();
        final Iterator<Integer> it = iterable1.iterator();
        assertEquals(1, it.next().intValue());
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            it.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));

        // calling unmodifiable on an already unmodifiable iterable shall return the same instance
        final FluentIterable<Integer> iterable2 = iterable1.unmodifiable();
        assertSame(iterable1, iterable2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void zip() {
        List<Integer> result = FluentIterable.of(iterableOdd).zip(iterableEven).toList();
        List<Integer> combinedList = new ArrayList<>();
        CollectionUtils.addAll(combinedList, iterableOdd);
        CollectionUtils.addAll(combinedList, iterableEven);
        Collections.sort(combinedList);
        assertEquals(combinedList, result);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            FluentIterable.of(iterableOdd).zip((Iterable<Integer>) null).toList();
        });
        assertTrue(exception.getMessage().contains("iterable"));

        result = FluentIterable
                    .of(Arrays.asList(1, 4, 7))
                    .zip(Arrays.asList(2, 5, 8), Arrays.asList(3, 6, 9))
                    .toList();
        combinedList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(combinedList, result);
    }

    @Test
    public void asEnumeration() {
        Enumeration<Long> enumeration = FluentIterable.of(iterableB).asEnumeration();
        final List<Long> result = EnumerationUtils.toList(enumeration);
        assertEquals(iterableB, result);

        enumeration = FluentIterable.<Long>empty().asEnumeration();
        assertFalse(enumeration.hasMoreElements());
    }

    @Test
    public void allMatch() {
        assertTrue(FluentIterable.of(iterableEven).allMatch(EVEN));
        assertFalse(FluentIterable.of(iterableOdd).allMatch(EVEN));
        assertFalse(FluentIterable.of(iterableA).allMatch(EVEN));

        Exception exception = assertThrows(NullPointerException.class, () -> {
            FluentIterable.of(iterableEven).allMatch(null);
        });
        assertTrue(exception.getMessage().contains("predicate"));
    }

    @Test
    public void anyMatch() {
        assertTrue(FluentIterable.of(iterableEven).anyMatch(EVEN));
        assertFalse(FluentIterable.of(iterableOdd).anyMatch(EVEN));
        assertTrue(FluentIterable.of(iterableA).anyMatch(EVEN));

        Exception exception = assertThrows(NullPointerException.class, () -> {
            FluentIterable.of(iterableEven).anyMatch(null);
        });
        assertTrue(exception.getMessage().contains("predicate"));
    }

    @Test
    public void isEmpty() {
        assertTrue(FluentIterable.of(emptyIterable).isEmpty());
        assertFalse(FluentIterable.of(iterableOdd).isEmpty());
    }

    @Test
    public void size() {
        assertEquals(0, FluentIterable.of(emptyIterable).size());
        assertEquals(IterableUtils.toList(iterableOdd).size(), FluentIterable.of(iterableOdd).size());
    }

    @Test
    public void eval() {
        final List<Integer> listNumbers = new ArrayList<>();
        listNumbers.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        final FluentIterable<Integer> iterable = FluentIterable.of(listNumbers).filter(EVEN);
        final FluentIterable<Integer> materialized = iterable.eval();

        listNumbers.addAll(Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
        assertEquals(5, materialized.size());
        assertEquals(10, iterable.size());

        assertEquals(Arrays.asList(2, 4, 6, 8, 10), materialized.toList());
        assertEquals(Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20), iterable.toList());
    }

    @Test
    public void contains() {
        assertTrue(FluentIterable.of(iterableEven).contains(2));
        assertFalse(FluentIterable.of(iterableEven).contains(1));
        assertFalse(FluentIterable.of(iterableEven).contains(null));
        assertTrue(FluentIterable.of(iterableEven).append((Integer) null).contains(null));
    }

    @Test
    public void copyInto() {
        List<Integer> result = new ArrayList<>();
        FluentIterable.of(iterableA).copyInto(result);

        List<Integer> expected = IterableUtils.toList(iterableA);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);

        result = new ArrayList<>();
        result.add(10);
        result.add(9);
        result.add(8);
        FluentIterable.of(iterableA).copyInto(result);

        expected = new ArrayList<>();
        expected.addAll(Arrays.asList(10, 9, 8));
        expected.addAll(IterableUtils.toList(iterableA));
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            FluentIterable.of(iterableA).copyInto(null);
        });
        assertTrue(exception.getMessage().contains("collection"));
    }

    @Test
    public void iterator() {
        Iterator<Integer> iterator = FluentIterable.of(iterableA).iterator();
        assertTrue(iterator.hasNext());

        iterator = FluentIterable.<Integer>empty().iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void get() {
        assertEquals(2, FluentIterable.of(iterableEven).get(0).intValue());

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            FluentIterable.of(iterableEven).get(-1);
        });
        assertTrue(exception.getMessage().contains("Index cannot be negative: -1"));

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            FluentIterable.of(iterableEven).get(IterableUtils.size(iterableEven));
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("6"));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void toArray() {
        final Long[] arr = new Long[] {1L, 2L, 3L, 4L, 5L};
        final Long[] result = FluentIterable.of(arr).toArray(Long.class);
        assertNotNull(result);
        assertArrayEquals(arr, result);

        try {
            FluentIterable.of(arr).toArray((Class) String.class);
        } catch (final ArrayStoreException ase) {
            // expected
        }
    }

    @Test
    public void testToString() {
        String result = FluentIterable.of(iterableA).toString();
        assertEquals(iterableA.toString(), result);

        result = FluentIterable.empty().toString();
        assertEquals("[]", result);
    }
}
