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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests cycle detection without rejecting shared acyclic children.
 */
class IteratorChainCycleTest {

    private enum CycleShape {
        DIRECT, MUTUAL, WRAPPED_DIRECT, WRAPPED_MUTUAL, INDIRECT
    }

    private enum Prefix {
        NONE, EMPTY, VALUE
    }

    private static void assertCycle(final Executable operation) {
        assertEquals("Cyclic IteratorChain", assertThrows(IllegalStateException.class, operation).getMessage());
    }

    private static IteratorChain<Integer> createCycle(final CycleShape shape, final List<Integer> prefix) {
        final IteratorChain<Integer> root = new IteratorChain<>();
        if (prefix != null) {
            root.addIterator(prefix.iterator());
        }
        final IteratorChain<Integer> child = new IteratorChain<>();
        switch (shape) {
        case DIRECT:
            root.addIterator(root);
            break;
        case MUTUAL:
            root.addIterator(child);
            child.addIterator(root);
            break;
        case WRAPPED_DIRECT:
            root.addIterator(UnmodifiableIterator.unmodifiableIterator(root));
            break;
        case WRAPPED_MUTUAL:
            root.addIterator(UnmodifiableIterator.unmodifiableIterator(child));
            child.addIterator(UnmodifiableIterator.unmodifiableIterator(root));
            break;
        case INDIRECT:
            final IteratorChain<Integer> grandchild = new IteratorChain<>();
            root.addIterator(child);
            child.addIterator(grandchild);
            grandchild.addIterator(root);
            break;
        default:
            return fail("Unexpected cycle shape: " + shape);
        }
        return root;
    }

    private static Stream<Arguments> cycleCases() {
        final List<Arguments> arguments = new ArrayList<>();
        for (final CycleShape shape : CycleShape.values()) {
            for (final Prefix prefix : Prefix.values()) {
                for (final boolean hasNextFirst : new boolean[] { false, true }) {
                    arguments.add(Arguments.of(shape, prefix, hasNextFirst));
                }
            }
        }
        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource("cycleCases")
    void testCycleDuringTraversal(final CycleShape shape, final Prefix prefix, final boolean hasNextFirst) {
        final List<Integer> values = prefix == Prefix.NONE ? null : prefix == Prefix.EMPTY ? Collections.emptyList() : Collections.singletonList(1);
        final IteratorChain<Integer> root = createCycle(shape, values);
        if (prefix == Prefix.VALUE) {
            assertEquals(1, root.next());
        }
        if (hasNextFirst) {
            assertCycle(root::hasNext);
        } else {
            assertCycle(root::next);
        }
        // A failed traversal must not leave a cached false result that hides the cycle on retry.
        assertCycle(root::hasNext);
        assertCycle(root::next);
        assertTrue(root.isLocked());
        assertThrows(UnsupportedOperationException.class, () -> root.addIterator(Collections.emptyIterator()));
    }

    @ParameterizedTest
    @EnumSource(CycleShape.class)
    void testCycleFailurePreservesLastSuccessfulRemoval(final CycleShape shape) {
        final List<Integer> values = new ArrayList<>(Collections.singletonList(1));
        final IteratorChain<Integer> root = createCycle(shape, values);
        assertEquals(1, root.next());
        assertCycle(root::hasNext);
        root.remove();
        assertTrue(values.isEmpty());
        assertThrows(IllegalStateException.class, root::remove);
        assertCycle(root::hasNext);
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 3, 20 })
    void testRemovalCycleAfterFailedNext(final int length) {
        final List<IteratorChain<Integer>> chains = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            chains.add(new IteratorChain<>());
        }
        final List<Integer> values = new ArrayList<>(Collections.singletonList(1));
        final IteratorChain<Integer> root = chains.get(0);
        root.addIterator(values.iterator());
        for (int i = 0; i < length; i++) {
            chains.get(i).addIterator(chains.get((i + 1) % length));
        }
        assertTrue(root.hasNext());
        // Populate cached results around the cycle before consuming the finite prefix.
        assertTrue(chains.get(1).hasNext());
        assertEquals(1, root.next());
        assertTrue(root.hasNext());
        assertCycle(root::next);
        // next() has prepared removal state along the path before detecting the cycle.
        assertCycle(root::remove);
        assertCycle(root::remove);
        assertEquals(Collections.singletonList(1), values);
        assertCycle(root::hasNext);
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void testSharedChildrenAreNotCycles(final boolean unmodifiable) {
        final IteratorChain<Integer> shared = new IteratorChain<>(Collections.emptyIterator());
        final Iterator<Integer> child = unmodifiable ? UnmodifiableIterator.unmodifiableIterator(shared) : shared;
        final IteratorChain<Integer> first = new IteratorChain<>(child);
        final IteratorChain<Integer> second = new IteratorChain<>(child, Collections.singletonList(1).iterator());
        final IteratorChain<Integer> root = new IteratorChain<>(first, second, child);
        // The same child is visited through three paths during one traversal.
        assertEquals(Collections.singletonList(1), IteratorUtils.toList(root));
        assertFalse(root.hasNext());
        assertTrue(shared.isLocked());
    }

    @Test
    void testTraversalStateUsesIdentity() {
        final List<Integer> values = new ArrayList<>(Arrays.asList(1, 2));
        final IteratorChain<Integer> root = new IteratorChain<Integer>() {

            @Override
            public boolean equals(final Object other) {
                return fail("Traversal must not call equals()");
            }

            @Override
            public int hashCode() {
                return fail("Traversal must not call hashCode()");
            }
        };
        root.addIterator(new IteratorChain<>(values.iterator()));
        assertEquals(1, root.next());
        root.remove();
        assertEquals(Collections.singletonList(2), IteratorUtils.toList(root));
        assertEquals(Collections.singletonList(2), values);
    }
}
