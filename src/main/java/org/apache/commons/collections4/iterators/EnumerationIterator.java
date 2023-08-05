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
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Adapter to make {@link Enumeration Enumeration} instances appear
 * to be {@link Iterator Iterator} instances.
 *
 * @param <E> the type of elements returned by this iterator.
 * @since 1.0
 */
public class EnumerationIterator<E> implements Iterator<E> {

    /** The collection to remove elements from */
    private final Collection<? super E> collection;
    /** The enumeration being converted */
    private Enumeration<? extends E> enumeration;
    /** The last object retrieved */
    private E last;

    // Constructors
    /**
     * Constructs a new {@code EnumerationIterator} that will not
     * function until {@link #setEnumeration(Enumeration)} is called.
     */
    public EnumerationIterator() {
        this(null, null);
    }

    /**
     * Constructs a new {@code EnumerationIterator} that provides
     * an iterator view of the given enumeration.
     *
     * @param enumeration  the enumeration to use
     */
    public EnumerationIterator(final Enumeration<? extends E> enumeration) {
        this(enumeration, null);
    }

    /**
     * Constructs a new {@code EnumerationIterator} that will remove
     * elements from the specified collection.
     *
     * @param enumeration  the enumeration to use
     * @param collection  the collection to remove elements from
     */
    public EnumerationIterator(final Enumeration<? extends E> enumeration, final Collection<? super E> collection) {
        this.enumeration = enumeration;
        this.collection = collection;
        this.last = null;
    }

    // Iterator interface
    /**
     * Returns true if the underlying enumeration has more elements.
     *
     * @return true if the underlying enumeration has more elements
     * @throws NullPointerException  if the underlying enumeration is null
     */
    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    /**
     * Returns the next object from the enumeration.
     *
     * @return the next object from the enumeration
     * @throws NullPointerException if the enumeration is null
     */
    @Override
    public E next() {
        last = enumeration.nextElement();
        return last;
    }

    /**
     * Removes the last retrieved element if a collection is attached.
     * <p>
     * Functions if an associated {@code Collection} is known.
     * If so, the first occurrence of the last returned object from this
     * iterator will be removed from the collection.
     *
     * @throws IllegalStateException {@code next()} not called.
     * @throws UnsupportedOperationException if no associated collection
     */
    @Override
    public void remove() {
        if (collection == null) {
            throw new UnsupportedOperationException("No Collection associated with this Iterator");
        }
        if (last == null) {
            throw new IllegalStateException("next() must have been called for remove() to function");
        }
        collection.remove(last);
    }

    // Properties
    /**
     * Returns the underlying enumeration.
     *
     * @return the underlying enumeration
     */
    public Enumeration<? extends E> getEnumeration() {
        return enumeration;
    }

    /**
     * Sets the underlying enumeration.
     *
     * @param enumeration  the new underlying enumeration
     */
    public void setEnumeration(final Enumeration<? extends E> enumeration) {
        this.enumeration = enumeration;
    }

}
