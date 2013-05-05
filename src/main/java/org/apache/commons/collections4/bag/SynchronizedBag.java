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
package org.apache.commons.collections4.bag;

import java.util.Set;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.collection.SynchronizedCollection;

/**
 * Decorates another {@link Bag} to synchronize its behaviour
 * for a multi-threaded environment.
 * <p>
 * Methods are synchronized, then forwarded to the decorated bag.
 * Iterators must be separately synchronized around the loop.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since 3.0
 * @version $Id$
 */
public class SynchronizedBag<E> extends SynchronizedCollection<E> implements Bag<E> {

    /** Serialization version */
    private static final long serialVersionUID = 8084674570753837109L;

    /**
     * Factory method to create a synchronized bag.
     *
     * @param <E> the type of the elements in the bag
     * @param bag  the bag to decorate, must not be null
     * @return a new synchronized Bag
     * @throws IllegalArgumentException if bag is null
     * @since 4.0
     */
    public static <E> SynchronizedBag<E> synchronizedBag(final Bag<E> bag) {
        return new SynchronizedBag<E>(bag);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param bag  the bag to decorate, must not be null
     * @throws IllegalArgumentException if bag is null
     */
    protected SynchronizedBag(final Bag<E> bag) {
        super(bag);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param bag  the bag to decorate, must not be null
     * @param lock  the lock to use, must not be null
     * @throws IllegalArgumentException if bag is null
     */
    protected SynchronizedBag(final Bag<E> bag, final Object lock) {
        super(bag, lock);
    }

    /**
     * Gets the bag being decorated.
     *
     * @return the decorated bag
     */
    protected Bag<E> getBag() {
        return (Bag<E>) decorated();
    }

    //-----------------------------------------------------------------------

    public boolean add(final E object, final int count) {
        synchronized (lock) {
            return getBag().add(object, count);
        }
    }

    public boolean remove(final Object object, final int count) {
        synchronized (lock) {
            return getBag().remove(object, count);
        }
    }

    public Set<E> uniqueSet() {
        synchronized (lock) {
            final Set<E> set = getBag().uniqueSet();
            return new SynchronizedBagSet(set, lock);
        }
    }

    public int getCount(final Object object) {
        synchronized (lock) {
            return getBag().getCount(object);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Synchronized Set for the Bag class.
     */
    class SynchronizedBagSet extends SynchronizedCollection<E> implements Set<E> {
        /** Serialization version */
        private static final long serialVersionUID = 2990565892366827855L;

        /**
         * Constructor.
         * @param set  the set to decorate
         * @param lock  the lock to use, shared with the bag
         */
        SynchronizedBagSet(final Set<E> set, final Object lock) {
            super(set, lock);
        }
    }

}
