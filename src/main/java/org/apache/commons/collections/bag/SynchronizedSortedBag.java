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
package org.apache.commons.collections.bag;

import java.util.Comparator;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.SortedBag;

/**
 * Decorates another <code>SortedBag</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p>
 * Methods are synchronized, then forwarded to the decorated bag.
 * Iterators must be separately synchronized around the loop.</p>
 * <p>
 * This class is Serializable from Commons Collections 3.1.</p>
 *
 * @since Commons Collections 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public class SynchronizedSortedBag<E>
        extends SynchronizedBag<E> implements SortedBag<E> {

    /** Serialization version */
    private static final long serialVersionUID = 722374056718497858L;

    /**
     * Factory method to create a synchronized sorted bag.
     * 
     * @param <E> the type of the elements in the bag
     * @param bag  the bag to decorate, must not be null
     * @return a new synchronized SortedBag
     * @throws IllegalArgumentException if bag is null
     */
    public static <E> SynchronizedSortedBag<E> synchronizedSortedBag(SortedBag<E> bag) {
        return new SynchronizedSortedBag<E>(bag);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param bag  the bag to decorate, must not be null
     * @throws IllegalArgumentException if bag is null
     */
    protected SynchronizedSortedBag(SortedBag<E> bag) {
        super(bag);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param bag  the bag to decorate, must not be null
     * @param lock  the lock to use, must not be null
     * @throws IllegalArgumentException if bag is null
     */
    protected SynchronizedSortedBag(Bag<E> bag, Object lock) {
        super(bag, lock);
    }

    /**
     * Gets the bag being decorated.
     * 
     * @return the decorated bag
     */
    protected SortedBag<E> getSortedBag() {
        return (SortedBag<E>) collection;
    }
    
    //-----------------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    public synchronized E first() {
        synchronized (lock) {
            return getSortedBag().first();
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized E last() {
        synchronized (lock) {
            return getSortedBag().last();
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Comparator<? super E> comparator() {
        synchronized (lock) {
            return getSortedBag().comparator();
        }
    }

}
