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

/**
 * Decorates an iterator to support one-element lookahead while iterating.
 * <p>
 * The decorator supports the removal operation, but an {@link IllegalStateException} will be thrown if {@link #remove()} is called directly after a call to
 * {@link #peek()} or {@link #element()}.
 * </p>
 *
 * @param <E> the type of elements returned by this iterator.
 * @since 4.0
 */
public class PeekingIterator<E> implements Iterator<E> {

    /**
     * Decorates the specified iterator to support one-element lookahead.
     * <p>
     * If the iterator is already a {@link PeekingIterator} it is returned directly.
     * </p>
     *
     * @param <E>      the element type
     * @param iterator the iterator to decorate
     * @return a new peeking iterator
     * @throws NullPointerException if the iterator is null
     */
    public static <E> PeekingIterator<E> peekingIterator(final Iterator<? extends E> iterator) {
        Objects.requireNonNull(iterator, "iterator");
        if (iterator instanceof PeekingIterator<?>) {
            @SuppressWarnings("unchecked") // safe cast
            final PeekingIterator<E> it = (PeekingIterator<E>) iterator;
            return it;
        }
        return new PeekingIterator<>(iterator);
    }

    /** The iterator being decorated. */
    private final Iterator<? extends E> iterator;

    /** Indicates that the decorated iterator is exhausted. */
    private boolean exhausted;

    /** Indicates if the lookahead slot is filled. */
    private boolean slotFilled;

    /** The current slot for lookahead. */
    private E slot;

    /**
     * Constructs a new instance.
     *
     * @param iterator the iterator to decorate
     */
    public PeekingIterator(final Iterator<? extends E> iterator) {
        this.iterator = iterator;
    }

    /**
     * Returns the next element in iteration without advancing the underlying iterator. If the iterator is already exhausted, null will be returned.
     * <p>
     * Note that if the underlying iterator is a {@link FilterIterator} or a {@link FilterListIterator}, the underlying predicate will <em>not</em> be tested if
     * element() or {@link #peek()} has been called after the most recent invocation of {@link #next()}
     * </p>
     *
     * @return the next element from the iterator
     * @throws NoSuchElementException if the iterator is already exhausted according to {@link #hasNext()}
     */
    public E element() {
        fill();
        if (exhausted) {
            throw new NoSuchElementException();
        }
        return slot;
    }

    private void fill() {
        if (exhausted || slotFilled) {
            return;
        }
        if (iterator.hasNext()) {
            slot = iterator.next();
            slotFilled = true;
        } else {
            exhausted = true;
            slot = null;
            slotFilled = false;
        }
    }

    @Override
    public boolean hasNext() {
        if (exhausted) {
            return false;
        }
        return slotFilled || iterator.hasNext();
    }

    /**
     * Returns the next element in iteration.
     * <p>
     * Note that if the underlying iterator is a {@link FilterIterator} or a {@link FilterListIterator}, the underlying predicate will <em>not</em> be tested if
     * {@link #element()} or {@link #peek()} has been called after the most recent invocation of {@link #next()}.
     * </p>
     *
     * @return the next element from the iterator
     * @throws NoSuchElementException if the iterator is already exhausted according to {@link #hasNext()}.
     */
    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        final E x = slotFilled ? slot : iterator.next();
        // reset the lookahead slot
        slot = null;
        slotFilled = false;
        return x;
    }

    /**
     * Returns the next element in iteration without advancing the underlying iterator. If the iterator is already exhausted, null will be returned.
     * <p>
     * Note: this method does not throw a {@link NoSuchElementException} if the iterator is already exhausted. If you want such a behavior, use
     * {@link #element()} instead.
     * </p>
     * <p>
     * The rationale behind this is to follow the {@link java.util.Queue} interface which uses the same terminology.
     * </p>
     * <p>
     * Note that if the underlying iterator is a {@link FilterIterator} or a {@link FilterListIterator}, the underlying predicate will <em>not</em> be tested if
     * {@link #element()} or peek() has been called after the most recent invocation of {@link #next()}.
     * </p>
     *
     * @return the next element from the iterator
     */
    public E peek() {
        fill();
        return exhausted ? null : slot;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if {@link #peek()} or {@link #element()} has been called prior to the call to {@link #remove()}.
     */
    @Override
    public void remove() {
        if (slotFilled) {
            throw new IllegalStateException("peek() or element() called before remove()");
        }
        iterator.remove();
    }

}
