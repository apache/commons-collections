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

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.collections4.iterators.PairedIterator;
import org.apache.commons.collections4.iterators.PairedIterator.PairedItem;

/**
 * Provides iteration over the elements contained in a pair of Iterables in-tandem.
 *
 * <p>
 * Given two {@link Iterable} instances {@code A} and {@code B}, the {@link #iterator} method on this
 * iterator provide a Pair of {@code A.next()} and {@code B.next()} until one of the iterators is
 * exhausted.
 * </p>
 * This can simplify zipping over two iterables using the for-each construct.
 * Example usage:
 * <pre>{@code
 *   List<Integer> studentIds = ...
 *   List<String> studentNames = ...
 *
 *   for (PairedItem<Integer, String> items : PairedIterable.of(studentIds, studentNames) {
 *     Integer studentId = item.getLeft();
 *     String studentName = item.getRight();
 *     ...
 *   }
 * }</pre>
 *
 * @param <L> the left elements' type
 * @param <R> the right elements' type
 */
public class PairedIterable<L, R> implements Iterable<PairedItem<L, R>> {

    /**
     * The left {@link Iterable}s to evaluate.
     */
    private final Iterable<L> leftIterable;

    /**
     * The right {@link Iterable}s to evaluate.
     */
    private final Iterable<R> rightIterable;

    // Constructor
    // ----------------------------------------------------------------------

    /**
     * Constructs a new {@code PairedIterable} that will provide iteration over two given iterables.
     *
     * @param leftIterable  the iterable for the left side element.
     * @param rightIterable the iterable for the right side element.
     * @throws NullPointerException if either iterator is null
     */
    public PairedIterable(Iterable<L> leftIterable, Iterable<R> rightIterable) {
        this.leftIterable = leftIterable;
        this.rightIterable = rightIterable;
    }

    /**
     * Convenience static factory to construct the PairedIterable from provided
     * {@link Iterable} sources.
     *
     * @param leftIterable  the iterable for the left side element.
     * @param rightIterable the iterable for the right side element.
     * @return the Iterable to iterate over the elements derived from the provided iterables.
     * @throws NullPointerException if either iterables is null
     */
    public static <L,R> PairedIterable<L,R> of(Iterable<L> leftIterable, Iterable<R> rightIterable) {
        return new PairedIterable<>(leftIterable, rightIterable);
    }

    // Iterable Methods
    // -------------------------------------------------------------------

    @Override
    public Iterator<PairedItem<L, R>> iterator() {
        return PairedIterator.ofIterables(leftIterable, rightIterable);
    }

    public Stream<PairedItem<L,R>> stream() {
        return StreamSupport.stream(spliterator(), /*parallel=*/ false);
    }
}
