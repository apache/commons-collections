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
package org.apache.commons.collections.iterators;

import java.util.ListIterator;

/**
 * Provides basic behaviour for decorating a list iterator with extra functionality.
 * <p>
 * All methods are forwarded to the decorated list iterator.
 *
 * @since 3.0
 * @version $Id$
 */
public class AbstractListIteratorDecorator<E> implements ListIterator<E> {

    /** The iterator being decorated */
    protected final ListIterator<E> iterator;

    //-----------------------------------------------------------------------
    /**
     * Constructor that decorates the specified iterator.
     *
     * @param iterator  the iterator to decorate, must not be null
     * @throws IllegalArgumentException if the collection is null
     */
    public AbstractListIteratorDecorator(final ListIterator<E> iterator) {
        super();
        if (iterator == null) {
            throw new IllegalArgumentException("ListIterator must not be null");
        }
        this.iterator = iterator;
    }

    /**
     * Gets the iterator being decorated.
     * 
     * @return the decorated iterator
     */
    protected ListIterator<E> getListIterator() {
        return iterator;
    }

    //-----------------------------------------------------------------------
    
    /** {@inheritDoc} */
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /** {@inheritDoc} */
    public E next() {
        return iterator.next();
    }

    /** {@inheritDoc} */
    public int nextIndex() {
        return iterator.nextIndex();
    }

    /** {@inheritDoc} */
    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    /** {@inheritDoc} */
    public E previous() {
        return iterator.previous();
    }

    /** {@inheritDoc} */
    public int previousIndex() {
        return iterator.previousIndex();
    }

    /** {@inheritDoc} */
    public void remove() {
        iterator.remove();
    }

    /** {@inheritDoc} */
    public void set(final E obj) {
        iterator.set(obj);
    }

    /** {@inheritDoc} */
    public void add(final E obj) {
        iterator.add(obj);
    }
    
}
