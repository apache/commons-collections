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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.apache.commons.collections4.ResettableIterator;

/**
 * An Iterator that restarts when it reaches the end.
 * <p>
 * The iterator will loop continuously around the provided elements, unless
 * there are no elements in the collection to begin with, or all the elements
 * have been {@link #remove removed}.
 * <p>
 * Concurrent modifications are not directly supported, and for most collection
 * implementations will throw a ConcurrentModificationException.
 *
 * @param <E> the type of elements returned by this iterator.
 * @since 3.0
 */
public class LoopingIterator<E> implements ResettableIterator<E> {

    /** The collection to base the iterator on */
    private final Collection<? extends E> collection;
    /** The current iterator */
    private Iterator<? extends E> iterator;

    /**
     * Constructor that wraps a collection.
     * <p>
     * There is no way to reset an Iterator instance without recreating it from
     * the original source, so the Collection must be passed in.
     *
     * @param collection  the collection to wrap
     * @throws NullPointerException if the collection is null
     */
    public LoopingIterator(final Collection<? extends E> collection) {
        this.collection = Objects.requireNonNull(collection, "collection");
        reset();
    }

    /**
     * Has the iterator any more elements.
     * <p>
     * Returns false only if the collection originally had zero elements, or
     * all the elements have been {@link #remove removed}.
     *
     * @return {@code true} if there are more elements
     */
    @Override
    public boolean hasNext() {
        return collection.size() > 0;
    }

    /**
     * Returns the next object in the collection.
     * <p>
     * If at the end of the collection, return the first element.
     *
     * @return the next object
     * @throws NoSuchElementException if there are no elements
     *         at all.  Use {@link #hasNext} to avoid this error.
     */
    @Override
    public E next() {
        if (collection.size() == 0) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (iterator.hasNext() == false) {
            reset();
        }
        return iterator.next();
    }

    /**
     * Removes the previously retrieved item from the underlying collection.
     * <p>
     * This feature is only supported if the underlying collection's
     * {@link Collection#iterator iterator} method returns an implementation
     * that supports it.
     * <p>
     * This method can only be called after at least one {@link #next} method call.
     * After a removal, the remove method may not be called again until another
     * next has been performed. If the {@link #reset} is called, then remove may
     * not be called until {@link #next} is called again.
     */
    @Override
    public void remove() {
        iterator.remove();
    }

    /**
     * Resets the iterator back to the start of the collection.
     */
    @Override
    public void reset() {
        iterator = collection.iterator();
    }

    /**
     * Gets the size of the collection underlying the iterator.
     *
     * @return the current collection size
     */
    public int size() {
        return collection.size();
    }

}
