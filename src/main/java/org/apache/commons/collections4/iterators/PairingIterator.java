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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.collections4.iterators.PairingIterator.Entry;

/**
 * Provides an iteration over the elements of two iterators. It will be return a
 * {@link Entry} containing the child iterators elements.
 * <p>
 * Given two {@link Iterator} instances {@code A} and {@code B}. The
 * {@link #next()} method will return a {@link Entry} with the elements of the
 * {@code A} and {@code B} as values of the {@link Entry} until both iterators
 * are exhausted.
 * </p>
 * <p>
 * If one of the iterators is null, the result {@link Entry} contains also a
 * null values until both child iterators exhausted.
 * </p>
 * <p>
 * If one iterator has more elements then the other, the result {@link Entry}
 * will contain a null value and the value of the not empty child iterator until
 * both of the iterator exhausted.
 *
 * @param <F> type of first value. The first value of {@link Entry}
 * 
 * @param <S> type of second value. The second value of {@link Entry}
 * 
 * @since 4.4
 */
public class PairingIterator<F, S> implements Iterator<Entry<F, S>> {

    private final Iterator<F> firstIterator;
    private final Iterator<S> secondIterator;

    /**
     * Constructs a new <code>PairingIterator</code> that will provide a
     * {@link Entry} containing the child iterator elements.
     * 
     * @param firstIterator  the first iterator
     * @param secondIterator the second iterator
     */
    public PairingIterator(Iterator<F> firstIterator, Iterator<S> secondIterator) {
        this.firstIterator = firstIterator;
        this.secondIterator = secondIterator;
    }

    /**
     * Returns {@code true} if one of the child iterators has remaining elements.
     */
    @Override
    public boolean hasNext() {
        return (null != firstIterator && firstIterator.hasNext())
                || (null != secondIterator && secondIterator.hasNext());
    }

    /**
     * Returns the next {@link Entry} with the next values of the child iterators.
     * 
     * @return a {@link Entry} with the next values of child iterators
     * @throws NoSuchElementException if no child iterator has any more elements
     */
    @Override
    public Entry<F, S> next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        final F firstValue = null != firstIterator && firstIterator.hasNext() ? firstIterator.next() : null;
        final S secondValue = null != secondIterator && secondIterator.hasNext() ? secondIterator.next() : null;
        return new Entry<F, S>(firstValue, secondValue);
    }

    /**
     * Removes the last returned values of the child iterators.
     */
    @Override
    public void remove() {
        if (firstIterator != null) {
            firstIterator.remove();
        }
        if (secondIterator != null) {
            secondIterator.remove();
        }
    }

    /**
     * Contains two values of different generic types.
     *
     * @param <F> the type of the first element
     * @param <S> the type of the second element
     */
    public static class Entry<F, S> {
        private final F firstValue;
        private final S secondValue;

        /**
         * Constructs a new {@link Entry} of two generic values.
         * 
         * @param firstValue  the first value
         * @param secondValue the second value
         */
        Entry(F firstValue, S secondValue) {
            this.firstValue = firstValue;
            this.secondValue = secondValue;
        }

        /**
         * Returns the first value.
         * 
         * @return the first value
         */
        public F getFirstValue() {
            return firstValue;
        }

        /**
         * Returns the second value.
         * 
         * @return the second value
         */
        public S getSecondValue() {
            return secondValue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstValue, secondValue);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Entry other = (Entry) obj;
            return Objects.equals(firstValue, other.firstValue) && Objects.equals(secondValue, other.secondValue);
        }

        @Override
        public String toString() {
            return "Entry [firstValue=" + firstValue + ", secondValue=" + secondValue + "]";
        }

    }

}
