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
package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This iterator creates a Cartesian product of the input iterables,
 * equivalent to nested for-loops.
 * <p>
 * The iterables provided to the constructor are used in reverse order, each
 * until exhaustion before proceeding to the next element of the prior iterable
 * and repeating. Consider the following example:
 * </p>
 * <pre>{@code
 * List<Character> iterable1 = Arrays.asList('A', 'B', 'C');
 * List<Character> iterable2 = Arrays.asList('1', '2', '3');
 * CartesianProductIterator<Character> it = new CartesianProductIterator<>(
 *         iterable1,
 *         iterable2);
 * while (it.hasNext()) {
 *     List<Character> tuple = it.next();
 *     System.out.println(tuple.get(0) + ", " + tuple.get(1));
 * }
 * }</pre>
 * <p>
 * The output will be:
 * </p>
 * <pre>
 * A, 1
 * A, 2
 * A, 3
 * B, 1
 * B, 2
 * B, 3
 * C, 1
 * C, 2
 * C, 3
 * </pre>
 * <p>
 * The {@code remove()} operation is not supported, and will throw an
 * {@code UnsupportedOperationException}.
 * </p>
 * <p>
 * If any of the input iterables is empty, the Cartesian product will be empty.
 * If any of the input iterables is infinite, the Cartesian product will be
 * infinite.
 * </p>
 *
 * @param <E> the type of the objects being permuted
 * @since 4.5.0-M3
 */
public class CartesianProductIterator<E> implements Iterator<List<E>> {

    /**
     * The iterables to create the Cartesian product from.
     */
    private final List<Iterable<? extends E>> iterables;

    /**
     * The iterators to generate the Cartesian product tuple from.
     */
    private final List<Iterator<? extends E>> iterators;

    /**
     * The previous generated tuple of elements.
     */
    private List<E> previousTuple;

    /**
     * Constructs a new {@code CartesianProductIterator} instance with given iterables.
     *
     * @param iterables the iterables to create the Cartesian product from
     * @throws NullPointerException if any of the iterables is null
     */
    @SafeVarargs
    public CartesianProductIterator(final Iterable<? extends E>... iterables) {
        Objects.requireNonNull(iterables, "iterables");
        this.iterables = new ArrayList<>(iterables.length);
        this.iterators = new ArrayList<>(iterables.length);
        for (final Iterable<? extends E> iterable : iterables) {
            Objects.requireNonNull(iterable, "iterable");
            this.iterables.add(iterable);
            final Iterator<? extends E> iterator = iterable.iterator();
            if (!iterator.hasNext()) {
                iterators.clear();
                break;
            }
            iterators.add(iterator);
        }
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     *
     * @return true if there are more tuples, otherwise false
     */
    @Override
    public boolean hasNext() {
        return iterators.stream().anyMatch(Iterator::hasNext);
    }

    /**
     * Returns the next tuple of the input iterables.
     *
     * @return a list of the input iterables' elements
     * @throws NoSuchElementException if there are no more tuples
     */
    @Override
    public List<E> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (previousTuple == null) {
            previousTuple = new ArrayList<>(iterables.size());
            for (final Iterator<? extends E> iterator : iterators) {
                previousTuple.add(iterator.next());
            }
            return new ArrayList<>(previousTuple);
        }
        for (int i = iterators.size() - 1; i >= 0; i--) {
            Iterator<? extends E> iterator = iterators.get(i);
            if (iterator.hasNext()) {
                previousTuple.set(i, iterator.next());
                return new ArrayList<>(previousTuple);
            }
            iterator = iterables.get(i).iterator();
            iterators.set(i, iterator);
            previousTuple.set(i, iterator.next());
        }
        throw new IllegalStateException("reached unreachable code");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
