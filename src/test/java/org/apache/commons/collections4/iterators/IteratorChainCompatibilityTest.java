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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests the live child-chain and subclass contracts that eager flattening broke in COLLECTIONS-722.
 */
class IteratorChainCompatibilityTest {

    private enum Construction {
        ADD, SINGLE, PAIR, ARRAY, COLLECTION, UTILS_PAIR, UTILS_ARRAY, UTILS_COLLECTION
    }

    private static Stream<Arguments> additionCases() {
        final List<Arguments> arguments = new ArrayList<>();
        for (final Construction construction : Construction.values()) {
            for (final boolean unmodifiable : new boolean[] { false, true }) {
                for (final boolean initiallyEmpty : new boolean[] { false, true }) {
                    arguments.add(Arguments.of(construction, unmodifiable, initiallyEmpty));
                }
            }
        }
        return arguments.stream();
    }

    private static Iterator<Integer> construct(final Construction construction, final Iterator<Integer> child) {
        final Iterator<Integer> tail = Collections.singletonList(9).iterator();
        switch (construction) {
        case ADD:
            final IteratorChain<Integer> added = new IteratorChain<>();
            added.addIterator(child);
            added.addIterator(tail);
            return added;
        case SINGLE:
            final IteratorChain<Integer> single = new IteratorChain<>(child);
            single.addIterator(tail);
            return single;
        case PAIR:
            return new IteratorChain<>(child, tail);
        case ARRAY:
            return new IteratorChain<>(Collections.emptyIterator(), child, tail);
        case COLLECTION:
            return new IteratorChain<>(Arrays.asList(child, tail));
        case UTILS_PAIR:
            return IteratorUtils.chainedIterator(child, tail);
        case UTILS_ARRAY:
            return IteratorUtils.chainedIterator(Collections.emptyIterator(), child, tail);
        case UTILS_COLLECTION:
            return IteratorUtils.chainedIterator(Arrays.asList(child, tail));
        default:
            return fail("Unexpected construction: " + construction);
        }
    }

    private static <E> Iterator<E> wrap(final Iterator<E> iterator, final boolean unmodifiable) {
        return unmodifiable ? UnmodifiableIterator.unmodifiableIterator(iterator) : iterator;
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testAdditionsAfterParentStartsBeforeChildIsReached(final boolean unmodifiable) {
        final IteratorChain<Integer> source = new IteratorChain<>();
        final IteratorChain<Integer> destination = new IteratorChain<>(Arrays.asList(0, 1).iterator(), wrap(source, unmodifiable));
        assertTrue(destination.hasNext());
        assertFalse(source.isLocked());
        source.addIterator(Collections.singletonList(2).iterator());
        assertEquals(0, destination.next());
        source.addIterator(Collections.singletonList(3).iterator());
        assertEquals(1, destination.next());
        assertFalse(source.isLocked());
        assertTrue(destination.hasNext());
        assertTrue(source.isLocked());
        assertThrows(UnsupportedOperationException.class, () -> source.addIterator(Collections.singletonList(4).iterator()));
        assertEquals(Arrays.asList(2, 3), IteratorUtils.toList(destination));
    }

    @ParameterizedTest
    @MethodSource("additionCases")
    void testAdditionsAfterWrapping(final Construction construction, final boolean unmodifiable, final boolean initiallyEmpty) {
        final IteratorChain<Integer> source = new IteratorChain<>();
        if (!initiallyEmpty) {
            source.addIterator(Collections.singletonList(1).iterator());
        }
        final Iterator<Integer> destination = construct(construction, wrap(source, unmodifiable));
        assertFalse(source.isLocked());
        source.addIterator(Collections.emptyIterator());
        source.addIterator(Collections.singletonList(2).iterator());
        assertEquals(initiallyEmpty ? Arrays.asList(2, 9) : Arrays.asList(1, 2, 9), IteratorUtils.toList(destination));
        assertTrue(source.isLocked());
        assertFalse(source.hasNext());
        assertThrows(UnsupportedOperationException.class, () -> source.addIterator(Collections.emptyIterator()));
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testDeepLiveChains(final boolean unmodifiable) {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            final List<Integer> values = new ArrayList<>(Arrays.asList(1, 2));
            final AtomicInteger hasNextCalls = new AtomicInteger();
            final Iterator<Integer> delegate = values.iterator();
            final Iterator<Integer> leaf = new Iterator<Integer>() {

                @Override
                public boolean hasNext() {
                    hasNextCalls.incrementAndGet();
                    return delegate.hasNext();
                }

                @Override
                public Integer next() {
                    return delegate.next();
                }

                @Override
                public void remove() {
                    delegate.remove();
                }
            };
            final IteratorChain<Integer> source = new IteratorChain<>(leaf);
            final List<IteratorChain<Integer>> chains = new ArrayList<>();
            IteratorChain<Integer> destination = source;
            for (int i = 0; i < 20000; i++) {
                destination = new IteratorChain<>(wrap(destination, unmodifiable));
                chains.add(destination);
            }
            source.addIterator(Collections.singletonList(3).iterator());
            for (final IteratorChain<Integer> chain : chains) {
                assertFalse(chain.isLocked());
            }
            assertTrue(destination.hasNext());
            assertTrue(destination.hasNext());
            assertEquals(1, hasNextCalls.get());
            assertEquals(1, destination.next());
            if (unmodifiable) {
                assertThrows(UnsupportedOperationException.class, destination::remove);
            } else {
                destination.remove();
                assertThrows(IllegalStateException.class, destination::remove);
                assertThrows(IllegalStateException.class, source::remove);
            }
            assertEquals(Arrays.asList(2, 3), IteratorUtils.toList(destination));
            assertEquals(3, hasNextCalls.get());
            assertEquals(unmodifiable ? Arrays.asList(1, 2) : Collections.singletonList(2), values);
            assertFalse(source.hasNext());
            assertThrows(NoSuchElementException.class, destination::next);
            for (final IteratorChain<Integer> chain : chains) {
                assertTrue(chain.isLocked());
                assertFalse(chain.hasNext());
            }
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testEmptyChildLocksWhenVisited(final boolean unmodifiable) {
        final IteratorChain<Integer> source = new IteratorChain<>();
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        assertFalse(source.isLocked());
        assertFalse(destination.hasNext());
        assertTrue(source.isLocked());
        assertThrows(UnsupportedOperationException.class, () -> source.addIterator(Collections.singletonList(1).iterator()));
    }

    @Test
    void testNestedRemovalCanBeRetriedAfterFailure() {
        final List<Integer> values = new ArrayList<>(Collections.singletonList(1));
        final AtomicInteger attempts = new AtomicInteger();
        final IllegalStateException failure = new IllegalStateException("First removal fails");
        final IteratorChain<Integer> source = new IteratorChain<Integer>(values.iterator()) {

            @Override
            public void remove() {
                if (attempts.incrementAndGet() == 1) {
                    throw failure;
                }
                super.remove();
            }
        };
        final IteratorChain<Integer> destination = new IteratorChain<>(new IteratorChain<>(source));
        assertEquals(1, destination.next());
        assertSame(failure, assertThrows(IllegalStateException.class, destination::remove));
        destination.remove();
        assertTrue(values.isEmpty());
        assertEquals(2, attempts.get());
        assertThrows(IllegalStateException.class, destination::remove);
        assertEquals(2, attempts.get());
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testNestedSourcesAddedAfterWrapping(final boolean unmodifiable) {
        final IteratorChain<Integer> source = new IteratorChain<>();
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        final IteratorChain<Integer> grandchild = new IteratorChain<>();
        source.addIterator(wrap(grandchild, unmodifiable));
        source.addIterator(Collections.singletonList(3).iterator());
        grandchild.addIterator(Arrays.asList(1, 2).iterator());
        assertEquals(Arrays.asList(1, 2, 3), IteratorUtils.toList(destination));
        assertTrue(grandchild.isLocked());
        assertFalse(grandchild.hasNext());
    }

    @Test
    void testQueueSizeCountsLiveChildren() {
        final IteratorChain<Integer> source = new IteratorChain<>(Collections.singletonList(1).iterator(), Collections.singletonList(2).iterator());
        final IteratorChain<Integer> destination = new IteratorChain<>(source);
        assertEquals(1, destination.size());
        assertEquals(2, source.size());
        assertTrue(destination.hasNext());
        assertEquals(0, destination.size());
        assertEquals(1, source.size());
        assertEquals(Arrays.asList(1, 2), IteratorUtils.toList(destination));
        assertEquals(0, source.size());
    }

    @Test
    void testSharedSourceConsumptionAndRemovalState() {
        final List<Integer> values = new ArrayList<>(Arrays.asList(1, 2, 3));
        final IteratorChain<Integer> source = new IteratorChain<>(values.iterator());
        final IteratorChain<Integer> first = new IteratorChain<>(source);
        final IteratorChain<Integer> second = new IteratorChain<>(source);
        assertEquals(1, source.next());
        assertEquals(2, first.next());
        source.remove();
        assertThrows(IllegalStateException.class, first::remove);
        assertEquals(3, second.next());
        second.remove();
        assertThrows(IllegalStateException.class, source::remove);
        assertEquals(Collections.singletonList(1), values);
        assertFalse(first.hasNext());
        assertFalse(second.hasNext());
        assertFalse(source.hasNext());
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testSourceAdvancesAfterWrapping(final boolean unmodifiable) {
        final IteratorChain<Integer> source = new IteratorChain<>(Arrays.asList(1, 2).iterator(), Collections.singletonList(3).iterator());
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        assertEquals(1, source.next());
        assertEquals(2, source.next());
        assertTrue(source.hasNext());
        assertEquals(Collections.singletonList(3), IteratorUtils.toList(destination));
        assertFalse(source.hasNext());
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testSubclassHasNextOverride(final boolean unmodifiable) {
        final AtomicInteger calls = new AtomicInteger();
        final IteratorChain<Integer> source = new IteratorChain<Integer>(Collections.singletonList(1).iterator()) {

            @Override
            public boolean hasNext() {
                calls.incrementAndGet();
                return false;
            }
        };
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable), Collections.singletonList(2).iterator());
        assertEquals(Collections.singletonList(2), IteratorUtils.toList(destination));
        assertEquals(1, calls.get());
    }

    @ParameterizedTest
    @CsvSource({ "false, false", "false, true", "true, false", "true, true" })
    void testSubclassNextOverride(final boolean unmodifiable, final boolean initialized) {
        final AtomicInteger calls = new AtomicInteger();
        final IteratorChain<Integer> source = new IteratorChain<Integer>(Arrays.asList(1, 2).iterator()) {

            @Override
            public Integer next() {
                calls.incrementAndGet();
                return super.next() * 10;
            }
        };
        if (initialized) {
            assertTrue(source.hasNext());
        }
        final IteratorChain<Integer> destination = new IteratorChain<>(wrap(source, unmodifiable));
        final List<Integer> values = new ArrayList<>();
        destination.forEachRemaining(values::add);
        assertEquals(Arrays.asList(10, 20), values);
        assertEquals(2, calls.get());
    }

    @Test
    void testSubclassRemoveOverrideAfterLookAhead() {
        final List<Integer> values = new ArrayList<>(Collections.singletonList(1));
        final AtomicInteger calls = new AtomicInteger();
        final IteratorChain<Integer> source = new IteratorChain<Integer>(values.iterator()) {

            @Override
            public void remove() {
                calls.incrementAndGet();
                throw new UnsupportedOperationException("Subclass is read only");
            }
        };
        final IteratorChain<Integer> destination = new IteratorChain<>(source, Collections.singletonList(2).iterator());
        assertEquals(1, destination.next());
        assertTrue(destination.hasNext());
        assertThrows(UnsupportedOperationException.class, destination::remove);
        assertEquals(1, calls.get());
        assertEquals(Collections.singletonList(1), values);
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testSubclassUpdateCurrentIteratorOverride(final boolean unmodifiable) {
        final AtomicInteger calls = new AtomicInteger();
        final IllegalStateException failure = new IllegalStateException("Retry traversal");
        final IteratorChain<Integer> source = new IteratorChain<Integer>(Collections.singletonList(1).iterator()) {

            @Override
            protected void updateCurrentIterator() {
                if (calls.incrementAndGet() == 1) {
                    throw failure;
                }
                super.updateCurrentIterator();
            }
        };
        final IteratorChain<Integer> destination = new IteratorChain<>(new IteratorChain<>(wrap(source, unmodifiable)));
        assertSame(failure, assertThrows(IllegalStateException.class, destination::hasNext));
        assertEquals(1, destination.next());
        assertEquals(2, calls.get());
        assertFalse(destination.hasNext());
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testSubclassWithoutQueuedIterators(final boolean unmodifiable) {
        final AtomicInteger remaining = new AtomicInteger(2);
        final IteratorChain<Integer> source = new IteratorChain<Integer>() {

            @Override
            public boolean hasNext() {
                return remaining.get() > 0;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return remaining.getAndDecrement();
            }
        };
        final IteratorChain<Number> destination = new IteratorChain<>(wrap(source, unmodifiable));
        assertEquals(Arrays.asList(2, 1), IteratorUtils.toList(destination));
        assertThrows(NoSuchElementException.class, destination::next);
    }

    @Test
    void testUnmodifiableBoundaryInsideModifiableChains() {
        final List<Integer> values = new ArrayList<>(Collections.singletonList(1));
        final IteratorChain<Integer> source = new IteratorChain<>(values.iterator());
        final IteratorChain<Integer> middle = new IteratorChain<>(UnmodifiableIterator.unmodifiableIterator(source));
        final IteratorChain<Integer> destination = new IteratorChain<>(middle);
        assertEquals(1, destination.next());
        assertThrows(UnsupportedOperationException.class, destination::remove);
        assertEquals(Collections.singletonList(1), values);
        // Direct access to the source retains the same removal state as normal delegation.
        source.remove();
        assertTrue(values.isEmpty());
    }
}
