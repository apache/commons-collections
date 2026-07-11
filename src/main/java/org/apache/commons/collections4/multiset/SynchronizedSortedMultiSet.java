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
package org.apache.commons.collections4.multiset;

import java.util.Comparator;

import org.apache.commons.collections4.SortedMultiSet;

/**
 * Decorates another {@link SortedMultiSet} to synchronize its behavior
 * for a multithreaded environment.
 * <p>
 * Methods are synchronized, then forwarded to the decorated multiset.
 * Iterators must be separately synchronized around the loop.
 * </p>
 *
 * @param <E> The type held in the multiset.
 * @since 4.6.0
 */
public class SynchronizedSortedMultiSet<E> extends SynchronizedMultiSet<E> implements SortedMultiSet<E> {

    /** Serialization version */
    private static final long serialVersionUID = 20260705L;

    /**
     * Factory method to create a synchronized sorted multiset.
     *
     * @param <E> The type of the elements in the multiset
     * @param multiset  the multiset to decorate, must not be null
     * @return a new synchronized SortedMultiSet
     * @throws NullPointerException if multiset is null
     */
    public static <E> SynchronizedSortedMultiSet<E> synchronizedSortedMultiSet(final SortedMultiSet<E> multiset) {
        return new SynchronizedSortedMultiSet<>(multiset);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param multiset  the multiset to decorate, must not be null
     * @throws NullPointerException if multiset is null
     */
    protected SynchronizedSortedMultiSet(final SortedMultiSet<E> multiset) {
        super(multiset);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param multiset  the multiset to decorate, must not be null
     * @param lock  the lock to use, must not be null
     * @throws NullPointerException if multiset or lock is null
     */
    protected SynchronizedSortedMultiSet(final SortedMultiSet<E> multiset, final Object lock) {
        super(multiset, lock);
    }

    @Override
    public Comparator<? super E> comparator() {
        synchronized (lock) {
            return decorated().comparator();
        }
    }

    /**
     * Gets the multiset being decorated.
     *
     * @return The decorated multiset
     */
    @Override
    protected SortedMultiSet<E> decorated() {
        return (SortedMultiSet<E>) super.decorated();
    }

    @Override
    public E first() {
        synchronized (lock) {
            return decorated().first();
        }
    }

    @Override
    public E last() {
        synchronized (lock) {
            return decorated().last();
        }
    }

}
