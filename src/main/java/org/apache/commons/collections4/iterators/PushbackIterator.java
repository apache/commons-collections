/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.iterators;

import java.util.Iterator;

import org.apache.commons.collections4.ArrayStack;

/**
 * Decorates an iterator to support pushback of elements.
 * <p>
 * The decorator stores the pushed back elements in a LIFO manner: the last element
 * that has been pushed back, will be returned as the next element in a call to {@link #next()}.
 * <p>
 * The decorator does not support the removal operation. Any call to {@link #remove()} will
 * result in an {@link UnsupportedOperationException}.
 *
 * @since 4.0
 * @version $Id$
 */
@SuppressWarnings("deprecation") // replace ArrayStack with ArrayDeque when moving to Java 6
public class PushbackIterator<E> implements Iterator<E> {

    /** The iterator being decorated. */
    private final Iterator<? extends E> iterator;

    /** The LIFO queue containing the pushed back items. */
    private ArrayStack<E> items = new ArrayStack<E>();

    //-----------------------------------------------------------------------
    /**
     * Decorates the specified iterator to support one-element lookahead.
     * <p>
     * If the iterator is already a {@link PeekingIterator} it is returned directly.
     *
     * @param <E>  the element type
     * @param iterator  the iterator to decorate
     * @return a new peeking iterator
     * @throws IllegalArgumentException if the iterator is null
     */
    public static <E> PushbackIterator<E> pushbackIterator(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("Iterator must not be null");
        }
        if (iterator instanceof PushbackIterator<?>) {
            @SuppressWarnings("unchecked") // safe cast
            final PushbackIterator<E> it = (PushbackIterator<E>) iterator;
            return it;
        }
        return new PushbackIterator<E>(iterator);
    }

    //-----------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param iterator  the iterator to decorate
     */
    public PushbackIterator(final Iterator<? extends E> iterator) {
        super();
        this.iterator = iterator;
    }

    /**
     * Push back the given element to the iterator.
     * <p>
     * Calling {@link #next()} immediately afterwards will return exactly this element.
     *
     * @param item  the element to push back to the iterator
     */
    public void pushback(final E item) {
        items.push(item);
    }

    public boolean hasNext() {
        return !items.isEmpty() ? true : iterator.hasNext();
    }

    public E next() {
        return !items.isEmpty() ? items.pop() : iterator.next();
    }

    /**
     * This iterator will always throw an {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
