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

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;

/**
 * Decorates an {@link Iterator} using an optional predicate to filter elements.
 * <p>
 * This iterator decorates the underlying iterator, only allowing through
 * those elements that match the specified {@link Predicate Predicate}.
 * </p>
 *
 * @param <E> the type of elements returned by this iterator.
 * @since 1.0
 */
public class FilterIterator<E> implements IteratorOperations<E> {

    /** The iterator to be filtered. */
    private Iterator<? extends E> iterator;

    /** The predicate to filter elements. */
    private Predicate<? super E> predicate = TruePredicate.truePredicate();

    /** The next object in the iteration. */
    private E nextObject;

    /** Whether the next object has been calculated yet. */
    private boolean nextObjectSet;

    /**
     * Constructs a new {@code FilterIterator} that will not function
     * until {@link #setIterator(Iterator) setIterator} is invoked.
     */
    public FilterIterator() {
    }

    /**
     * Constructs a new {@code FilterIterator} that will not function
     * until {@link #setPredicate(Predicate) setPredicate} is invoked.
     *
     * @param iterator  the iterator to use
     */
    public FilterIterator(final Iterator<? extends E> iterator) {
        this.iterator = iterator;
    }

    /**
     * Constructs a new {@code FilterIterator} that will use the
     * given iterator and predicate.
     *
     * @param iterator  the iterator to use
     * @param predicate  the predicate to use, null accepts all values.
     */
    public FilterIterator(final Iterator<? extends E> iterator, final Predicate<? super E> predicate) {
        this.iterator = iterator;
        this.predicate = safePredicate(predicate);
    }

    /**
     * Gets the iterator this iterator is using.
     *
     * @return the underlying iterator.
     */
    public Iterator<? extends E> getIterator() {
        return iterator;
    }

    /**
     * Gets the predicate this iterator is using.
     *
     * @return the filtering predicate.
     */
    public Predicate<? super E> getPredicate() {
        return predicate;
    }

    /**
     * Returns true if the underlying iterator contains an object that
     * matches the predicate.
     *
     * @return true if there is another object that matches the predicate
     * @throws NullPointerException if either the iterator or predicate are null
     */
    @Override
    public boolean hasNext() {
        return nextObjectSet || setNextObject();
    }

    /**
     * Returns the next object that matches the predicate.
     *
     * @return the next object which matches the given predicate
     * @throws NullPointerException if either the iterator or predicate are null
     * @throws NoSuchElementException if there are no more elements that
     *  match the predicate
     */
    @Override
    public E next() {
        if (!nextObjectSet && !setNextObject()) {
            throw new NoSuchElementException();
        }
        nextObjectSet = false;
        return nextObject;
    }

    /**
     * Removes from the underlying collection of the base iterator the last
     * element returned by this iterator.
     * This method can only be called
     * if {@code next()} was called, but not after
     * {@code hasNext()}, because the {@code hasNext()} call
     * changes the base iterator.
     *
     * @throws IllegalStateException if {@code hasNext()} has already
     *  been called.
     */
    @Override
    public void remove() {
        if (nextObjectSet) {
            throw new IllegalStateException("remove() cannot be called");
        }
        iterator.remove();
    }

    private Predicate<? super E> safePredicate(final Predicate<? super E> predicate) {
        return predicate != null ? predicate : TruePredicate.truePredicate();
    }

    /**
     * Sets the iterator for this iterator to use.
     * If iteration has started, this effectively resets the iterator.
     *
     * @param iterator  the iterator to use
     */
    public void setIterator(final Iterator<? extends E> iterator) {
        this.iterator = iterator;
        nextObject = null;
        nextObjectSet = false;
    }

    /**
     * Sets nextObject to the next object. If there are no more
     * objects, then return false. Otherwise, return true.
     */
    private boolean setNextObject() {
        while (iterator.hasNext()) {
            final E object = iterator.next();
            if (predicate.test(object)) {
                nextObject = object;
                nextObjectSet = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the predicate this the iterator to use where null accepts all values.
     *
     * @param predicate  the predicate to use, null accepts all values.
     */
    public void setPredicate(final Predicate<? super E> predicate) {
        this.predicate = safePredicate(predicate);
        nextObject = null;
        nextObjectSet = false;
    }

}
