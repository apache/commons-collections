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
package org.apache.commons.collections4.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;

/**
 * Decorates another {@link Collection} to ensure it can't be altered.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * </p>
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException.
 * </p>
 *
 * @param <E> The type of the elements in the collection
 * @since 3.0
 */
public final class UnmodifiableCollection<E>
        extends AbstractCollectionDecorator<E>
        implements Unmodifiable {

    /** Serialization version */
    private static final long serialVersionUID = -239892006883819945L;

    /**
     * Creates an unmodifiable collection.
     * <p>
     * If the collection passed in is already unmodifiable, it is returned.
     * </p>
     *
     * @param <T> The type of the elements in the collection.
     * @param coll  the collection to decorate, must not be null.
     * @return An unmodifiable collection.
     * @throws NullPointerException if collection is null.
     * @since 4.0
     */
    public static <T> Collection<T> unmodifiableCollection(final Collection<? extends T> coll) {
        if (coll instanceof Unmodifiable) {
            @SuppressWarnings("unchecked") // safe to upcast
            final Collection<T> tmpColl = (Collection<T>) coll;
            return tmpColl;
        }
        return new UnmodifiableCollection<>(coll);
    }

    /**
     * Constructs and wraps (not copies).
     *
     * @param coll  the collection to decorate, must not be null.
     * @throws NullPointerException if collection is null.
     */
    @SuppressWarnings("unchecked") // safe to upcast
    private UnmodifiableCollection(final Collection<? extends E> coll) {
        super((Collection<E>) coll);
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param object Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean add(final E object) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator(decorated().iterator());
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param object Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param coll Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean removeAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param filter Ignored.
     * @throws UnsupportedOperationException Always thrown.
     * @since 4.4
     */
    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param coll Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean retainAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

}
