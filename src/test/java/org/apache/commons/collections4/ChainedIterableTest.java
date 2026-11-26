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
package org.apache.commons.collections4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Tests lazy concatenation through the utility APIs.
 */
class ChainedIterableTest {

    /**
     * Wraps a single value and records calls to hasNext.
     */
    private static Iterator<Integer> countedIterator(final AtomicInteger polls) {
        return new Iterator<Integer>() {
            private final Iterator<Integer> delegate = Collections.singletonList(1).iterator();

            @Override
            public boolean hasNext() {
                polls.incrementAndGet();
                return delegate.hasNext();
            }

            @Override
            public Integer next() {
                return delegate.next();
            }
        };
    }

    @Test
    void testCycleCreatedByChangingVarargs() {
        @SuppressWarnings("unchecked")
        final Iterable<Integer>[] sources = (Iterable<Integer>[]) new Iterable<?>[] { Collections.emptyList() };
        final Iterable<Integer> chain = IterableUtils.chainedIterable(sources);
        sources[0] = chain;
        final Iterator<Integer> iterator = chain.iterator();
        assertThrows(IllegalStateException.class, iterator::hasNext);
        assertThrows(IllegalStateException.class, iterator::hasNext);
        sources[0] = Collections.singletonList(1);
        assertEquals(1, iterator.next().intValue());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testDeepChainedIterable() {
        Iterable<Integer> chain = Collections.emptyList();
        for (int i = 0; i < 100_000; i++) {
            chain = IterableUtils.chainedIterable(chain, Collections.singletonList(i));
        }
        final Iterator<Integer> iterator = chain.iterator();
        for (int i = 0; i < 100_000; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(i, iterator.next().intValue());
        }
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testNestedCycleCanBeReplacedWithRepeatedChild() {
        @SuppressWarnings("unchecked")
        final Iterable<Integer>[] sources = (Iterable<Integer>[]) new Iterable<?>[] { Collections.singletonList(1), Collections.emptyList() };
        final Iterable<Integer> shared = IterableUtils.chainedIterable(Collections.singletonList(2), Collections.emptyList());
        Iterable<Integer> chain = IterableUtils.chainedIterable(sources);
        for (int i = 0; i < 5; i++) {
            chain = IterableUtils.chainedIterable(chain, shared);
        }
        sources[1] = chain;
        final Iterator<Integer> iterator = chain.iterator();
        assertEquals(1, iterator.next().intValue());
        assertThrows(IllegalStateException.class, iterator::hasNext);
        sources[1] = shared;
        assertEquals(Collections.nCopies(6, 2), IteratorUtils.toList(iterator));
    }

    @Test
    void testNestedIterablePollsActiveLeafOnce() {
        final AtomicInteger polls = new AtomicInteger();
        Iterable<Integer> chain = () -> countedIterator(polls);
        for (int i = 0; i < 20; i++) {
            chain = IterableUtils.chainedIterable(Collections.emptyList(), chain);
        }
        assertTrue(chain.iterator().hasNext());
        assertEquals(1, polls.get());
    }

    @Test
    void testNestedSourcesAreOpenedOnDemand() {
        final AtomicInteger opens = new AtomicInteger();
        final List<Integer> values = new ArrayList<>(Arrays.asList(null, 2));
        final Iterable<Integer> second = () -> {
            opens.incrementAndGet();
            return values.iterator();
        };
        final Iterable<Integer> chain = IterableUtils.chainedIterable(Collections.singletonList(1),
                IterableUtils.chainedIterable(Collections.emptyList(), second));
        final Iterator<Integer> first = chain.iterator();
        final Iterator<Integer> independent = chain.iterator();
        assertEquals(0, opens.get());
        assertEquals(1, first.next().intValue());
        assertEquals(1, independent.next().intValue());
        assertEquals(0, opens.get());
        assertNull(first.next());
        first.remove();
        assertEquals(Collections.singletonList(2), values);
        assertEquals(2, independent.next().intValue());
        assertEquals(2, opens.get());
    }

    @Test
    void testRetryOpeningSource() {
        final AtomicInteger attempts = new AtomicInteger();
        final Iterable<Integer> first = () -> {
            if (attempts.getAndIncrement() == 0) {
                throw new IllegalStateException("Source unavailable");
            }
            return Collections.singletonList(1).iterator();
        };
        final Iterator<Integer> iterator = IterableUtils.chainedIterable(first, Collections.singletonList(2)).iterator();
        assertThrows(IllegalStateException.class, iterator::hasNext);
        assertEquals(Arrays.asList(1, 2), IteratorUtils.toList(iterator));
    }
}
