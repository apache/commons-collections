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

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.collections4.IteratorUtils;

/**
 * Extends Iterator functionality to include operations commonly found on streams (for example filtering, concatenating, mapping). It also provides convenience methods
 * for common operations.
 *
 * @param <T> The type of object returned from the iterator.
 * @since 4.5.0-M3
 */
public final class ExtendedIterator<T> implements IteratorOperations<T> {

    /**
     * Create an ExtendedIterator returning the elements of <code>it</code>. If <code>it</code> is itself an ExtendedIterator, return that; otherwise wrap
     * <code>it</code>.
     *
     * @param <T> The type of object returned from the iterator.
     * @param it The iterator to wrap.
     * @return An Extended iterator wrapping {@code it}
     */
    public static <T> ExtendedIterator<T> create(final Iterator<T> it) {
        return it instanceof ExtendedIterator<?> ? (ExtendedIterator<T>) it : new ExtendedIterator<>(it, false);
    }

    /**
     * Creates an ExtendedIterator wrapped round a {@link Stream}. The extended iterator does not permit <code>.remove()</code>.
     * <p>
     * The stream should not be used directly. The effect of doing so is undefined.
     * </p>
     *
     * @param <T> The type of object returned from the iterator.
     * @param stream the Stream to create an iterator from.
     * @return an Extended iterator on the {@code stream} iterator.
     */
    public static <T> ExtendedIterator<T> create(final Stream<T> stream) {
        return new ExtendedIterator<>(stream.iterator(), true);
    }

    /**
     * Creates an ExtendedIterator wrapped round <code>it</code>, which does not permit <code>.remove()</code> even if <code>it</code> does.
     *
     * @param <T> The type of object returned from the iterator.
     * @param it The Iterator to wrap.
     * @return an Extended iterator on {@code it}
     * @throws UnsupportedOperationException if remove() is called on the resulting iterator.
     */
    public static <T> ExtendedIterator<T> createNoRemove(final Iterator<T> it) {
        return new ExtendedIterator<>(it, true);
    }

    /**
     * Creates an empty Extended iterator.
     *
     * @return An empty Extended iterator.
     */
    public static ExtendedIterator<?> emptyIterator() {
        return new ExtendedIterator<>(Collections.emptyIterator(), false);
    }

    /**
     * Flattens an iterator of iterators into an Iterator over the next level values. Similar to list splicing in lisp.
     *
     * @param <T> The type of object returned from the iterator.
     * @param iterators An iterator of iterators.
     * @return An iterator over the logical concatenation of the inner iterators.
     */
    public static <T> ExtendedIterator<T> flatten(final Iterator<Iterator<T>> iterators) {
        return create(IteratorUtils.chainedIterator(iterators));
    }

    /**
     * Set to <code>true</code> if this wrapping doesn't permit the use of {@link #remove()}, otherwise removal is delegated to the base iterator.
     */
    private final boolean throwOnRemove;

    /** The base iterator that we wrap */
    private final Iterator<? extends T> base;

    /**
     * Initialize this wrapping with the given base iterator and remove-control.
     *
     * @param base          the base iterator that this iterator wraps
     * @param throwOnRemove true if .remove() must throw an exception
     */
    private ExtendedIterator(final Iterator<? extends T> base, final boolean throwOnRemove) {
        this.base = base;
        this.throwOnRemove = throwOnRemove;
    }

    /**
     * Chains the {@code other} iterator to the end of this one.
     *
     * @param other the other iterator to extend this iterator with.
     * @return A new iterator returning the contents of {@code this} iterator followed by the contents of {@code other} iterator.
     * @param <X> The type of object returned from the other iterator.
     */
    public <X extends T> ExtendedIterator<T> andThen(final Iterator<X> other) {
        if (base instanceof IteratorChain) {
            ((IteratorChain<T>) base).addIterator(other);
            return this;
        }
        return new ExtendedIterator<>(new IteratorChain<>(base, other), throwOnRemove);
    }

    /**
     * Filter this iterator using a predicate. Only items for which the predicate returns {@code true} will be included in the result.
     *
     * @param predicate The predicate to filter the items with.
     * @return An iterator filtered by the predicate.
     */
    public ExtendedIterator<T> filter(final Predicate<T> predicate) {
        return new ExtendedIterator<>(new FilterIterator<>(this, predicate::test), throwOnRemove);
    }

    @Override
    public void forEachRemaining(final Consumer<? super T> action) {
        base.forEachRemaining(action);
    }

    @Override
    public boolean hasNext() {
        return base.hasNext();
    }

    /**
     * Map the elements of the iterator to a now type.
     *
     * @param function The function to map elements of {@code <T>} to type {@code <U>}.
     * @return An Extended iterator that returns a {@code <U>} for very {@code <T>} in the original iterator.
     * @param <U> The object type to return.
     */
    public <U> ExtendedIterator<U> map(final Function<T, U> function) {
        return new ExtendedIterator<>(new TransformIterator<>(this, function::apply), false);
    }

    @Override
    public T next() {
        return base.next();
    }

    @Override
    public void remove() {
        if (throwOnRemove) {
            throw new UnsupportedOperationException();
        }
        base.remove();
    }
}
