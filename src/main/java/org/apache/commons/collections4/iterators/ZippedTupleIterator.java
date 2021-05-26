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

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.iterators.ZippedTupleIterator.ZippedTuple;

/**
 * Provides a iteration over the elements contained in a pair of Iterators.
 *
 * <p>
 * Given two {@link Iterator} instances {@code A} and {@code B}, the {@link #next} method on this
 * iterator provide a Pair of {@code A.next()} and {@code B.next()} until one of the iterators is
 * exhausted.
 * </p>
 * Example usage:
 * <pre>{@code
 *   List<Integer> studentIds = ...
 *   List<String> studentNames = ...
 *
 *   ZippedTupleIterator<ZippedTuple<Integer, String>> zippedIterator =
 *     ZippedTupleIterator.ofIterables(studentIds, studentNames);
 *
 *   while (zippedIterator.hasNext()) {
 *     ZippedTuple<Integer, String> item = zippedIterator.next();
 *     ...
 *   }
 * }</pre>
 *
 * @param <L> the left elements' type
 * @param <R> the right elements' type
 */
public class ZippedTupleIterator<L, R> implements Iterator<ZippedTuple<L, R>> {

    /**
     * The left {@link Iterator}s to evaluate.
     */
    private final Iterator<L> leftIterator;

    /**
     * The right {@link Iterator}s to evaluate.
     */
    private final Iterator<R> rightIterator;

    // Constructor
    // ----------------------------------------------------------------------

    /**
     * Constructs a new {@code ZipPairIterator} that will provide  iteration over the two given
     * iterators.
     *
     * @param leftIterator  the iterator for the left side element.
     * @param rightIterator the iterator for the right side element.
     * @throws NullPointerException if either iterator is null
     */
    public ZippedTupleIterator(Iterator<L> leftIterator, Iterator<R> rightIterator) {
      this.leftIterator = requireNonNull(leftIterator);
      this.rightIterator = requireNonNull(rightIterator);
    }

    /**
     * Convenience static factory to construct the ZipPairIterator
     *
     * @param leftIterator  the iterator for the left side element.
     * @param rightIterator the iterator for the right side element.
     * @return the iterator to iterate over the provided iterators.
     * @throws NullPointerException if either iterator is null
     */
    public static <L, R> ZippedTupleIterator<L, R> of(Iterator<L> leftIterator,
        Iterator<R> rightIterator) {
      return new ZippedTupleIterator<>(leftIterator, rightIterator);
    }

    /**
     * Convenience static factory to construct the ZipPairIterator from any {@link Iterable} sources.
     *
     * @param leftIterable  the iterable for the left side element.
     * @param rightIterable the iterable for the right side element.
     * @return the iterator to iterate over the iterators derived from the provided iterables.
     * @throws NullPointerException if either iterables is null
     */
    public static <L, R> ZippedTupleIterator<L, R> ofIterables(Iterable<L> leftIterable,
        Iterable<R> rightIterable) {
      return of(requireNonNull(leftIterable).iterator(), requireNonNull(rightIterable).iterator());
    }

    // Iterator Methods
    // -------------------------------------------------------------------

    /**
     * An immutable tuple class to represent elements from both the iterators.
     *
     * @param <L> the left elements' type
     * @param <R> the right elements' type
     */
    public static final class ZippedTuple<L, R> {

      private final L leftItem;

      private final R rightItem;

      private ZippedTuple(L leftItem, R rightItem) {
        this.leftItem = leftItem;
        this.rightItem = rightItem;
      }

      /**
       * Convenience static factory method to construct the tuple pair.
       *
       * @param left  the left element
       * @param right the right element
       * @return the Immutable tuple pair of two elements.
       */
      private static <L, R> ZippedTuple<L, R> of(L left, R right) {
        return new ZippedTuple<>(left, right);
      }

      public L getLeftItem() {
        return leftItem;
      }

      public R getRightItem() {
        return rightItem;
      }

      @Override
      public String toString() {
        return String.format("{%s, %s}", leftItem, rightItem);
      }
    }

    // Iterator Methods
    // -------------------------------------------------------------------

    /**
     * Returns {@code true} if both the child iterators have remaining elements.
     *
     * @return true if both the child iterators have remaining elements
     */
    @Override
    public boolean hasNext() {
      return leftIterator.hasNext() && rightIterator.hasNext();
    }

    /**
     * Returns the next elements from both the child iterators.
     *
     * @return the next elements from both the iterators.
     * @throws NoSuchElementException if any one child iterator is exhausted.
     */
    @Override
    public ZippedTuple<L, R> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      return ZippedTuple.of(leftIterator.next(), rightIterator.next());
    }
}
