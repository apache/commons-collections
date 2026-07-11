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
package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;

/**
 * Decorates another {@code List} to ensure it can't be altered.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * </p>
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException.
 * </p>
 *
 * @param <E> The type of the elements in the list.
 * @since 3.0
 */
public final class UnmodifiableList<E>
        extends AbstractSerializableListDecorator<E>
        implements Unmodifiable {

    /** Serialization version */
    private static final long serialVersionUID = 6595182819922443652L;

    /**
     * Factory method to create an unmodifiable list.
     *
     * @param <E> The type of the elements in the list
     * @param list  the list to decorate, must not be null
     * @return A new unmodifiable list
     * @throws NullPointerException if list is null
     * @since 4.0
     */
    public static <E> List<E> unmodifiableList(final List<? extends E> list) {
        if (list instanceof Unmodifiable) {
            @SuppressWarnings("unchecked") // safe to upcast
            final List<E> tmpList = (List<E>) list;
            return tmpList;
        }
        return new UnmodifiableList<>(list);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param list  the list to decorate, must not be null
     * @throws NullPointerException if list is null
     */
    @SuppressWarnings("unchecked") // safe to upcast
    public UnmodifiableList(final List<? extends E> list) {
        super((List<E>) list);
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param index Ignored.
     * @param object Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public void add(final int index, final E object) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param object Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean add(final Object object) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param coll Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param index Ignored.
     * @param coll Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
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

    @Override
    public ListIterator<E> listIterator() {
        return UnmodifiableListIterator.unmodifiableListIterator(decorated().listIterator());
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return UnmodifiableListIterator.unmodifiableListIterator(decorated().listIterator(index));
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param index Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public E remove(final int index) {
        throw new UnsupportedOperationException();
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

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param index Ignored.
     * @param object Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public E set(final int index, final E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return new UnmodifiableList<>(decorated().subList(fromIndex, toIndex));
    }

}
