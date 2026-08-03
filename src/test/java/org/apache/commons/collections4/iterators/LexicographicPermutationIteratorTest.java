/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.iterators;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for LexicographicPermutationIterator.
 */
class LexicographicPermutationIteratorTest extends AbstractIteratorTest<List<Character>> {

    /**
     * A comparator that orders nothing, identified only by an id, used to check that
     * equal comparators make equal iterators.
     *
     * @param <T> the type of the objects compared
     */
    private static final class CustomComparator<T> implements Comparator<T> {

        private final int id;

        CustomComparator(final int id) {
            this.id = id;
        }

        @Override
        public int compare(final T o1, final T o2) {
            return 0;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final CustomComparator<?> cmp = (CustomComparator<?>) o;
            return id == cmp.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     * A value holder that deliberately does not implement {@link Comparable}, used to
     * check that a supplied comparator is honored.
     *
     * @param <T> the type of the wrapped value
     */
    private static final class NonComparableObject<T> {

        private final T value;

        NonComparableObject(final T value) {
            this.value = value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final NonComparableObject<?> that = (NonComparableObject<?>) o;
            return Objects.equals(value, that.value);
        }

        T getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    @SuppressWarnings("boxing") // OK in test code
    protected Character[] testArray = { 'A', 'B', 'C' };

    protected List<Character> testList;

    @Override
    public LexicographicPermutationIterator<Character> makeEmptyIterator() {
        return new LexicographicPermutationIterator<>(new ArrayList<>());
    }

    @Override
    public LexicographicPermutationIterator<Character> makeObject() {
        return new LexicographicPermutationIterator<>(testList);
    }

    @BeforeEach
    public void setUp() {
        testList = new ArrayList<>();
        testList.addAll(Arrays.asList(testArray));
    }

    @Override
    public boolean supportsEmptyIterator() {
        return false;
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    @Test
    void testCustomComparator() {
        final Iterator<List<Character>> permutationIterator = new LexicographicPermutationIterator<>(Arrays.asList('C', 'B', 'A'),
                Comparator.reverseOrder());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('C', 'B', 'A'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('C', 'A', 'B'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'C', 'A'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'A', 'C'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('A', 'C', 'B'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('A', 'B', 'C'), permutationIterator.next());

        assertFalse(permutationIterator.hasNext());
    }

    @Test
    void testCustomComparatorWithNonComparableObjects() {
        final Iterator<List<NonComparableObject<Character>>> permutationIterator =
                new LexicographicPermutationIterator<>(Arrays.asList(
                        new NonComparableObject<>('A'),
                        new NonComparableObject<>('B')), Comparator.comparing(NonComparableObject::getValue));

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList(
                new NonComparableObject<>('A'),
                new NonComparableObject<>('B')), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList(
                new NonComparableObject<>('B'),
                new NonComparableObject<>('A')), permutationIterator.next());

        assertFalse(permutationIterator.hasNext());
    }

    @Test
    void testDuplicatedPermutationsAreSkipped() {
        final Iterator<List<Character>> permutationIterator = new LexicographicPermutationIterator<>(Arrays.asList('A', 'A', 'B', 'B'));

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('A', 'A', 'B', 'B'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('A', 'B', 'A', 'B'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('A', 'B', 'B', 'A'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'A', 'A', 'B'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'A', 'B', 'A'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'B', 'A', 'A'), permutationIterator.next());

        assertFalse(permutationIterator.hasNext());
    }

    @Test
    void testEmptyCollection() {
        final Iterator<List<Character>> permutationIterator = makeEmptyIterator();

        // there is one permutation for an empty set: 0! = 1
        assertTrue(permutationIterator.hasNext());
        assertTrue(permutationIterator.next().isEmpty());

        assertFalse(permutationIterator.hasNext());
    }

    @Test
    void testEqualsForEqualCollections() {
        final Iterator<List<Short>> one = new LexicographicPermutationIterator<>(emptyList());
        final Iterator<List<Short>> another = new LexicographicPermutationIterator<>(emptyList());

        assertEquals(one, another);
    }

    @Test
    void testEqualsForEqualCollectionsAndComparators() {
        final Iterator<List<Short>> one = new LexicographicPermutationIterator<>(emptyList(), new CustomComparator<>(42));
        final Iterator<List<Short>> another = new LexicographicPermutationIterator<>(emptyList(), new CustomComparator<>(42));

        assertEquals(one, another);
    }

    @Test
    void testHashCodeForEqualCollections() {
        final Iterator<List<Short>> one = new LexicographicPermutationIterator<>(emptyList());
        final Iterator<List<Short>> another = new LexicographicPermutationIterator<>(emptyList());

        assertEquals(one.hashCode(), another.hashCode());
    }

    @Test
    void testHashCodeForEqualCollectionsAndComparators() {
        final Iterator<List<Short>> one = new LexicographicPermutationIterator<>(emptyList(), new CustomComparator<>(42));
        final Iterator<List<Short>> another = new LexicographicPermutationIterator<>(emptyList(), new CustomComparator<>(42));

        assertEquals(one.hashCode(), another.hashCode());
    }

    @Test
    void testNonComparableElementsThrow() {
        final Iterator<List<NonComparableObject<Character>>> permutationIterator = new LexicographicPermutationIterator<>(
                Arrays.asList(
                        new NonComparableObject<>('A'),
                        new NonComparableObject<>('B')));

        assertTrue(permutationIterator.hasNext());
        assertThrows(ClassCastException.class, permutationIterator::next);
    }

    @Test
    void testPermutationException() {
        final Iterator<List<Character>> permutationIterator = new LexicographicPermutationIterator<>(Arrays.asList('A', 'B'));

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('A', 'B'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'A'), permutationIterator.next());

        // asking for another permutation should throw an exception
        assertFalse(permutationIterator.hasNext());
        assertThrows(NoSuchElementException.class, permutationIterator::next);
    }

    /**
     * test checking that all the permutations are returned in lexicographical order
     */
    @Test
    void testPermutationExhaustivity() {
        final Iterator<List<Character>> permutationIterator = makeObject();

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('A', 'B', 'C'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('A', 'C', 'B'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'A', 'C'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'C', 'A'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('C', 'A', 'B'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('C', 'B', 'A'), permutationIterator.next());

        assertFalse(permutationIterator.hasNext());
    }

    @Test
    void testRemoveThrows() {
        final Iterator<List<Character>> permutationIterator = makeObject();

        assertTrue(permutationIterator.hasNext());
        assertThrows(UnsupportedOperationException.class, permutationIterator::remove);
    }

    @Test
    void testStreamOfPermutations() {
        final Iterable<List<Character>> iterable = this::makeObject;

        final List<List<Character>> allPermutations = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(
                Arrays.asList('A', 'B', 'C'),
                Arrays.asList('A', 'C', 'B'),
                Arrays.asList('B', 'A', 'C'),
                Arrays.asList('B', 'C', 'A'),
                Arrays.asList('C', 'A', 'B'),
                Arrays.asList('C', 'B', 'A')), allPermutations);
    }

}
