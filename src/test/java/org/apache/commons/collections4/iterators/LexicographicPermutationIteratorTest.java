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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
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

    /**
     * Advances the given iterator until it holds no further permutation.
     *
     * @param iterator  the iterator to exhaust
     */
    private static void exhaust(final Iterator<?> iterator) {
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

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

    /**
     * test checking that a collection whose elements are all equal yields a single
     * permutation, equal elements not being distinguished from one another: the n!
     * arrangements are all duplicates of each other and collapse into one.
     */
    @Test
    void testAllEqualElementsYieldSinglePermutation() {
        final Iterator<List<Character>> permutationIterator = new LexicographicPermutationIterator<>(Arrays.asList('A', 'A', 'A'));

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('A', 'A', 'A'), permutationIterator.next());

        assertFalse(permutationIterator.hasNext());
        assertThrows(NoSuchElementException.class, permutationIterator::next);
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
    void testEqualsForDifferentComparators() {
        final Iterator<List<Short>> one = new LexicographicPermutationIterator<>(emptyList(), new CustomComparator<>(42));
        final Iterator<List<Short>> another = new LexicographicPermutationIterator<>(emptyList(), new CustomComparator<>(7));
        final Iterator<List<Short>> natural = new LexicographicPermutationIterator<>(emptyList());

        assertNotEquals(one, another);
        assertNotEquals(one, natural);
    }

    @Test
    void testEqualsForDifferentPositions() {
        final Iterator<List<Character>> one = new LexicographicPermutationIterator<>(Arrays.asList('A', 'B'));
        final Iterator<List<Character>> another = new LexicographicPermutationIterator<>(Arrays.asList('A', 'B'));

        assertEquals(one, another);

        // advancing one of them leaves them at different permutations
        one.next();
        assertNotEquals(one, another);

        // advancing the other brings them back to the same permutation
        another.next();
        assertEquals(one, another);

        // an exhausted iterator differs from one still holding a permutation
        one.next();
        assertFalse(one.hasNext());
        assertNotEquals(one, another);
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

    /**
     * test checking that the comparator keeps being compared once both iterators are
     * exhausted, so that two iterators which will both emit nothing are still unequal.
     * The position is spent, but it is not the only part of the equality contract.
     */
    @Test
    void testEqualsForExhaustedIteratorsWithDifferentComparators() {
        final Iterator<List<Character>> one = new LexicographicPermutationIterator<>(Arrays.asList('A', 'B'), new CustomComparator<>(42));
        final Iterator<List<Character>> another = new LexicographicPermutationIterator<>(Arrays.asList('A', 'B'), new CustomComparator<>(7));

        exhaust(one);
        exhaust(another);

        assertFalse(one.hasNext());
        assertFalse(another.hasNext());
        assertNotEquals(one, another);
    }

    /**
     * test checking that forEachRemaining resumes at the current position rather than at
     * the first permutation, the already returned ones being no longer remaining, and
     * that it leaves the iterator exhausted.
     */
    @Test
    void testForEachRemainingResumesFromCurrentPosition() {
        final Iterator<List<Character>> permutationIterator = makeObject();
        final List<List<Character>> permutations = new ArrayList<>();

        assertEquals(Arrays.asList('A', 'B', 'C'), permutationIterator.next());
        assertEquals(Arrays.asList('A', 'C', 'B'), permutationIterator.next());

        permutationIterator.forEachRemaining(permutations::add);

        assertEquals(Arrays.asList(
                Arrays.asList('B', 'A', 'C'),
                Arrays.asList('B', 'C', 'A'),
                Arrays.asList('C', 'A', 'B'),
                Arrays.asList('C', 'B', 'A')), permutations);

        assertFalse(permutationIterator.hasNext());
    }

    /**
     * test checking that forEachRemaining hands the permutations to the action in the
     * same lexicographical order as next() returns them.
     */
    @Test
    void testForEachRemainingYieldsLexicographicOrder() {
        final Iterator<List<Character>> permutationIterator = makeObject();
        final List<List<Character>> permutations = new ArrayList<>();

        permutationIterator.forEachRemaining(permutations::add);

        assertEquals(Arrays.asList(
                Arrays.asList('A', 'B', 'C'),
                Arrays.asList('A', 'C', 'B'),
                Arrays.asList('B', 'A', 'C'),
                Arrays.asList('B', 'C', 'A'),
                Arrays.asList('C', 'A', 'B'),
                Arrays.asList('C', 'B', 'A')), permutations);

        assertFalse(permutationIterator.hasNext());
    }

    /**
     * test checking the documented behavior that the hash code changes as the iterator
     * advances, and stays consistent with equals: iterators at the same position hash
     * alike again.
     */
    @Test
    void testHashCodeChangesAsIteratorAdvances() {
        final Iterator<List<Character>> one = new LexicographicPermutationIterator<>(Arrays.asList('A', 'B'));
        final Iterator<List<Character>> another = new LexicographicPermutationIterator<>(Arrays.asList('A', 'B'));

        assertEquals(one.hashCode(), another.hashCode());

        one.next();
        assertNotEquals(one.hashCode(), another.hashCode());

        another.next();
        assertEquals(one.hashCode(), another.hashCode());
    }

    @Test
    void testHashCodeForDifferentComparators() {
        final Iterator<List<Short>> one = new LexicographicPermutationIterator<>(emptyList(), new CustomComparator<>(42));
        final Iterator<List<Short>> another = new LexicographicPermutationIterator<>(emptyList(), new CustomComparator<>(7));

        assertNotEquals(one.hashCode(), another.hashCode());
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

    /**
     * test checking that exhausted iterators hash alike whatever they were built from.
     * Equality is defined on the comparator and the next permutation, as documented on
     * {@link LexicographicPermutationIterator#equals(Object)}, and the input collection
     * is not part of it; two exhausted iterators are therefore equal even when they were
     * built from different collections, which makes the equal hash codes required rather
     * than incidental.
     */
    @Test
    void testEqualsAndHashCodeForExhaustedIterators() {
        final Iterator<List<Character>> one = new LexicographicPermutationIterator<>(Arrays.asList('A', 'B'));
        final Iterator<List<Character>> another = new LexicographicPermutationIterator<>(Arrays.asList('X', 'Y', 'Z'));

        exhaust(one);
        exhaust(another);

        assertEquals(one, another);
        assertEquals(one.hashCode(), another.hashCode());
    }

    /**
     * test checking that the lists handed out by next() belong to the caller: the next
     * permutation is computed and copied before the current one is returned, so mutating
     * a returned list, structurally or not, leaves the remaining iteration untouched.
     */
    @Test
    void testMutatingReturnedListDoesNotAffectIteration() {
        final Iterator<List<Character>> permutationIterator = makeObject();

        final List<Character> first = permutationIterator.next();
        assertEquals(Arrays.asList('A', 'B', 'C'), first);
        first.set(0, 'Z');
        first.add('Q');

        // the damage is not merely deferred by one step, so mutate the next one too
        final List<Character> second = permutationIterator.next();
        assertEquals(Arrays.asList('A', 'C', 'B'), second);
        second.clear();

        assertEquals(Arrays.asList('B', 'A', 'C'), permutationIterator.next());
        assertEquals(Arrays.asList('B', 'C', 'A'), permutationIterator.next());
        assertEquals(Arrays.asList('C', 'A', 'B'), permutationIterator.next());
        assertEquals(Arrays.asList('C', 'B', 'A'), permutationIterator.next());

        assertFalse(permutationIterator.hasNext());
    }

    /**
     * test checking the first permutation specifically, it being the one the constructor
     * builds rather than next(): it must be a list of the iterator's own rather than the
     * given collection, so that later changes to that collection never reach the
     * iteration. The two copies live in different places, so losing one of them would
     * corrupt only part of the sequence.
     */
    @Test
    void testMutatingSourceCollectionDoesNotAffectIteration() {
        final List<Character> source = new ArrayList<>(Arrays.asList('A', 'B', 'C'));
        final Iterator<List<Character>> permutationIterator = new LexicographicPermutationIterator<>(source);

        source.set(0, 'X');
        source.add('Y');

        final List<Character> first = permutationIterator.next();
        assertNotSame(source, first);
        assertEquals(Arrays.asList('A', 'B', 'C'), first);

        // the copy is no view of the collection either, so iteration carries on intact
        assertEquals(Arrays.asList('A', 'C', 'B'), permutationIterator.next());
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
    void testNullCollectionThrows() {
        assertThrows(NullPointerException.class, () -> new LexicographicPermutationIterator<>(null));
        assertThrows(NullPointerException.class, () -> new LexicographicPermutationIterator<>(null, Comparator.<Character>reverseOrder()));
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

    /**
     * test checking that iteration starts at the given arrangement rather than at the
     * smallest one, so that a collection which is not sorted yields only the
     * permutations that follow it. Sorting the input is the caller's responsibility,
     * as it is for binarySearch.
     */
    @Test
    void testUnsortedCollectionStartsAtGivenArrangement() {
        final Iterator<List<Character>> permutationIterator = new LexicographicPermutationIterator<>(Arrays.asList('B', 'A', 'C'));

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'A', 'C'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('B', 'C', 'A'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('C', 'A', 'B'), permutationIterator.next());

        assertTrue(permutationIterator.hasNext());
        assertEquals(Arrays.asList('C', 'B', 'A'), permutationIterator.next());

        // the two permutations starting with 'A' precede the given arrangement
        // and are therefore never returned
        assertFalse(permutationIterator.hasNext());
    }

    /**
     * test checking that the starting arrangement is honoured for a supplied comparator
     * too, the permutations preceding it under that comparator being left out.
     */
    @Test
    void testUnsortedCollectionStartsAtGivenArrangementWithComparator() {
        final Iterator<List<Character>> permutationIterator = new LexicographicPermutationIterator<>(Arrays.asList('B', 'C', 'A'),
                Comparator.reverseOrder());

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

}
