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

import java.util.Iterator;
import java.util.Objects;

import org.apache.commons.collections4.Unmodifiable;

/**
 * Decorates an iterator such that it cannot be modified.
 * <p>
 * Calling {@link #remove()} throws {@link UnsupportedOperationException}.
 * </p>
 *
 * @param <E> the type of elements returned by this iterator.
 * @param <T> The wrapped Iterator type.
 * @since 3.0
 */
public final class UnmodifiableIterator<E, T extends Iterator<? extends E>> implements Iterator<E>, Unmodifiable {

    /**
     * Decorates the specified iterator such that it cannot be modified.
     * <p>
     * If the iterator is already unmodifiable it is returned directly.
     * </p>
     *
     * @param <E>  the element type
     * @param iterator  the iterator to decorate
     * @return a new unmodifiable iterator
     * @throws NullPointerException if the iterator is null
     */
    public static <E> Iterator<E> unmodifiableIterator(final Iterator<? extends E> iterator) {
        Objects.requireNonNull(iterator, "iterator");
        if (iterator instanceof Unmodifiable) {
            @SuppressWarnings("unchecked") // safe to upcast
            final Iterator<E> tmpIterator = (Iterator<E>) iterator;
            return tmpIterator;
        }
        return wrap(iterator);
    }

    static <E, T extends Iterator<? extends E>> UnmodifiableIterator<E, T> wrap(final T iterator) {
        return new UnmodifiableIterator<>(iterator);
    }

    /**
     * The decorated iterator.
     */
    private final T iterator;

    /**
     * Constructs a new instance.
     *
     * @param iterator  the iterator to decorate.
     */
    private UnmodifiableIterator(final T iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public E next() {
        return iterator.next();
    }

    // TODO This method can be removed in 5.0 since it's implemented as a default method in Iterator.
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    T unwrap() {
        return iterator;
    }
}
