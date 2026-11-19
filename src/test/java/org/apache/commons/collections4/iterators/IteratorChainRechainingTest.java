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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests rechaining initialized iterators, including the regression in COLLECTIONS-900.
 */
class IteratorChainRechainingTest {

    private static Stream<Arguments> chainingStates() {
        final List<Arguments> arguments = new ArrayList<>();
        for (final boolean unmodifiable : new boolean[] { false, true }) {
            for (int consumed = 0; consumed <= 6; consumed++) {
                for (final boolean lookAhead : new boolean[] { false, true }) {
                    for (final boolean emptyChildren : new boolean[] { false, true }) {
                        arguments.add(Arguments.of(unmodifiable, consumed, lookAhead, emptyChildren));
                    }
                }
            }
        }
        return arguments.stream();
    }

    private static IteratorChain<Integer> createChain(final List<Integer> first, final List<Integer> second, final List<Integer> third,
            final boolean emptyChildren) {
        final IteratorChain<Integer> chain = new IteratorChain<>();
        for (final List<Integer> list : Arrays.asList(first, second, third)) {
            if (emptyChildren) {
                chain.addIterator(Collections.emptyIterator());
            }
            chain.addIterator(list.iterator());
        }
        if (emptyChildren) {
            chain.addIterator(Collections.emptyIterator());
        }
        return chain;
    }

    private static void exhaust(final Iterator<?> iterator) {
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
        assertFalse(iterator.hasNext());
    }

    private static void position(final Iterator<Integer> iterator, final int consumed, final boolean lookAhead) {
        for (int i = 1; i <= consumed; i++) {
            assertEquals(i, iterator.next());
        }
        if (lookAhead) {
            assertEquals(consumed < 6, iterator.hasNext());
            assertEquals(consumed < 6, iterator.hasNext());
        }
    }

    private static <E> Iterator<E> wrap(final Iterator<E> iterator, final boolean unmodifiable) {
        return unmodifiable ? UnmodifiableIterator.unmodifiableIterator(iterator) : iterator;
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testAddingDoesNotInvokeChild(final boolean unmodifiable) {
        @SuppressWarnings("unchecked")
        final Iterator<Integer> child = mock(Iterator.class);
        final IteratorChain<Integer> source = new IteratorChain<>(child);
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        verifyNoInteractions(child);
        when(child.hasNext()).thenReturn(true);
        when(child.next()).thenReturn(1);
        assertEquals(1, destination.next());
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testAddingInitializedChainDoesNotAdvanceChild(final boolean unmodifiable) {
        final Iterator<Integer> child = Arrays.asList(1, 2).iterator();
        final IteratorChain<Integer> source = new IteratorChain<>(child);
        assertTrue(source.hasNext());
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        assertEquals(Arrays.asList(1, 2), IteratorUtils.toList(destination));
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testConstructorAndFactoryVariants(final boolean unmodifiable) {
        // Each construction path receives a fresh initialized source chain.
        for (int variant = 0; variant < 6; variant++) {
            final IteratorChain<Integer> source = new IteratorChain<>(Arrays.asList(1, 2).iterator());
            assertTrue(source.hasNext());
            final Iterator<Integer> child = wrap(source, unmodifiable);
            final Iterator<Integer> tail = Collections.singletonList(3).iterator();
            final Iterator<Integer> destination;
            switch (variant) {
            case 0:
                final IteratorChain<Integer> added = new IteratorChain<>();
                added.addIterator(child);
                added.addIterator(tail);
                destination = added;
                break;
            case 1:
                final IteratorChain<Integer> single = new IteratorChain<>(child);
                single.addIterator(tail);
                destination = single;
                break;
            case 2:
                destination = new IteratorChain<>(child, tail);
                break;
            case 3:
                destination = new IteratorChain<>(Collections.emptyIterator(), child, tail);
                break;
            case 4:
                destination = new IteratorChain<>(Arrays.asList(child, tail));
                break;
            default:
                destination = IteratorUtils.chainedIterator(child, tail);
                break;
            }
            assertEquals(Arrays.asList(1, 2, 3), IteratorUtils.toList(destination), "Construction variant " + variant);
            exhaust(destination);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testEmptySource(final boolean unmodifiable) {
        final IteratorChain<Integer> source = new IteratorChain<>();
        exhaust(source);
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        assertThrows(IllegalStateException.class, destination::remove);
        exhaust(destination);
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testLockedDestinationRejectsAdd(final boolean unmodifiable) {
        final IteratorChain<Integer> destination = new IteratorChain<>(Collections.singletonList(1).iterator());
        final IteratorChain<Integer> source = new IteratorChain<>(Collections.singletonList(2).iterator());
        assertTrue(source.hasNext());
        assertTrue(destination.hasNext());
        assertThrows(UnsupportedOperationException.class, () -> destination.addIterator(wrap(source, unmodifiable)));
        assertEquals(1, destination.next());
        exhaust(destination);
        assertEquals(2, source.next());
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testNullElements(final boolean unmodifiable) {
        final IteratorChain<Integer> source = new IteratorChain<>(Arrays.asList(null, 1, null).iterator());
        assertTrue(source.hasNext());
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        assertEquals(Arrays.asList(null, 1, null), IteratorUtils.toList(destination));
        exhaust(destination);
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testRechainAfterRemove(final boolean unmodifiable) {
        final List<Integer> values = new ArrayList<>(Arrays.asList(1, 2, 3));
        final IteratorChain<Integer> source = new IteratorChain<>(values.iterator());
        assertEquals(1, source.next());
        source.remove();
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        assertThrows(IllegalStateException.class, destination::remove);
        assertEquals(Arrays.asList(2, 3), IteratorUtils.toList(destination));
        assertEquals(Arrays.asList(2, 3), values);
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testRechainAfterUnsuccessfulRemove(final boolean unmodifiable) {
        final IteratorChain<Integer> source = new IteratorChain<>(Arrays.asList(1, 2).iterator());
        assertThrows(IllegalStateException.class, source::remove);
        assertTrue(source.isLocked());
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        assertEquals(Arrays.asList(1, 2), IteratorUtils.toList(destination));
    }

    @ParameterizedTest
    @MethodSource("chainingStates")
    void testRemainingSequence(final boolean unmodifiable, final int consumed, final boolean lookAhead, final boolean emptyChildren) {
        final IteratorChain<Integer> source = createChain(Arrays.asList(1, 2, 3), Collections.singletonList(4), Arrays.asList(5, 6), emptyChildren);
        position(source, consumed, lookAhead);
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        destination.addIterator(Arrays.asList(7, 8).iterator());
        assertFalse(destination.isLocked());
        for (int expected = consumed + 1; expected <= 8; expected++) {
            assertTrue(destination.hasNext());
            assertTrue(destination.hasNext());
            assertEquals(expected, destination.next());
        }
        exhaust(destination);
        assertTrue(destination.isLocked());
    }

    @ParameterizedTest
    @MethodSource("chainingStates")
    void testRemainingSequenceWithoutHasNext(final boolean unmodifiable, final int consumed, final boolean lookAhead, final boolean emptyChildren) {
        final IteratorChain<Integer> source = createChain(Arrays.asList(1, 2, 3), Collections.singletonList(4), Arrays.asList(5, 6), emptyChildren);
        position(source, consumed, lookAhead);
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        for (int expected = consumed + 1; expected <= 6; expected++) {
            assertEquals(expected, destination.next());
        }
        assertThrows(NoSuchElementException.class, destination::next);
        exhaust(destination);
    }

    @ParameterizedTest
    @MethodSource("chainingStates")
    void testRemoveAfterRechaining(final boolean unmodifiable, final int consumed, final boolean lookAhead, final boolean emptyChildren) {
        final List<Integer> first = new ArrayList<>(Arrays.asList(1, 2, 3));
        final List<Integer> second = new ArrayList<>(Collections.singletonList(4));
        final List<Integer> third = new ArrayList<>(Arrays.asList(5, 6));
        final List<Integer> tail = new ArrayList<>(Collections.singletonList(7));
        final IteratorChain<Integer> source = createChain(first, second, third, emptyChildren);
        position(source, consumed, lookAhead);
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        destination.addIterator(tail.iterator());
        assertThrows(IllegalStateException.class, destination::remove);
        for (int expected = consumed + 1; expected <= 7; expected++) {
            assertEquals(expected, destination.next());
            // Look ahead across child boundaries and, for the final element, exhaustion before removing.
            assertEquals(expected < 7, destination.hasNext());
            if (unmodifiable && expected <= 6) {
                assertThrows(UnsupportedOperationException.class, destination::remove);
            } else {
                destination.remove();
                assertThrows(IllegalStateException.class, destination::remove);
            }
        }
        final List<Integer> remaining = new ArrayList<>(first);
        remaining.addAll(second);
        remaining.addAll(third);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6).subList(0, unmodifiable ? 6 : consumed), remaining);
        assertTrue(tail.isEmpty());
        exhaust(destination);
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testRepeatedInitializedRechaining(final boolean unmodifiable) {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            IteratorChain<Integer> chain = new IteratorChain<>(Arrays.asList(1, 2).iterator(), Collections.singletonList(3).iterator());
            for (int i = 0; i < 5000; i++) {
                assertTrue(chain.hasNext());
                chain = new IteratorChain<>(wrap(chain, unmodifiable));
            }
            assertEquals(Arrays.asList(1, 2, 3), IteratorUtils.toList(chain));
            exhaust(chain);
        });
    }
}
