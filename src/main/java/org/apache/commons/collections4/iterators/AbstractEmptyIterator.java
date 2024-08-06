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

import java.util.NoSuchElementException;

import org.apache.commons.collections4.ResettableIterator;

/**
 * Provides an abstract implementation of an empty iterator.
 *
 * @since 3.1
 */
abstract class AbstractEmptyIterator<E> implements ResettableIterator<E> {

    /**
     * Constructs a new instance.
     */
    protected AbstractEmptyIterator() {
    }

    /**
     * Always throws UnsupportedOperationException.
     *
     * @param ignored ignore.
     * @throws UnsupportedOperationException Always thrown.
     * @deprecated Will be removed in 5.0 without replacement.
     */
    @Deprecated
    public void add(final E ignored) {
        throw new UnsupportedOperationException("add() not supported for empty Iterator");
    }

    /**
     * Always returns false, this iterator contains no elements.
     *
     * @return Always false.
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /**
     * Always returns false, this iterator contains no elements.
     *
     * @return Always false.
     */
    public boolean hasPrevious() {
        return false;
    }

    /**
     * Always throws IllegalStateException, this iterator contains no elements.
     *
     * @return Always throws IllegalStateException.
     * @throws IllegalStateException Always thrown.
     */
    @Override
    public E next() {
        throw new NoSuchElementException("Iterator contains no elements");
    }

    /**
     * Always returns 0, this iterator contains no elements.
     *
     * @return Always returns 0.
     */
    public int nextIndex() {
        return 0;
    }

    /**
     * Always throws IllegalStateException, this iterator contains no elements.
     *
     * @return Always throws IllegalStateException.
     * @throws IllegalStateException Always thrown.
     */
    public E previous() {
        throw new NoSuchElementException("Iterator contains no elements");
    }

    /**
     * Always returns -1, this iterator contains no elements.
     *
     * @return Always returns -1.
     */
    public int previousIndex() {
        return -1;
    }

    /**
     * Always throws IllegalStateException, this iterator contains no elements.
     *
     * @throws IllegalStateException Always thrown.
     */
    @Override
    public void remove() {
        throw new IllegalStateException("Iterator contains no elements");
    }

    @Override
    public void reset() {
        // do nothing
    }

    /**
     * Always throws IllegalStateException, this iterator contains no elements.
     *
     * @param ignored ignored.
     * @throws IllegalStateException Always thrown.
     */
    public void set(final E ignored) {
        throw new IllegalStateException("Iterator contains no elements");
    }

}
