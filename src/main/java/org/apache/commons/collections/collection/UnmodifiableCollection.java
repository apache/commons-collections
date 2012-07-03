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
package org.apache.commons.collections.collection;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.iterators.UnmodifiableIterator;

/**
 * Decorates another {@link Collection} to ensure it can't be altered.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException. 
 *
 * @param <E> the type of the elements in the collection
 * @since Commons Collections 3.0
 * @version $Id$
 */
public final class UnmodifiableCollection<E>
        extends AbstractCollectionDecorator<E>
        implements Unmodifiable {

    /** Serialization version */
    private static final long serialVersionUID = -239892006883819945L;

    /**
     * Factory method to create an unmodifiable collection.
     * <p>
     * If the collection passed in is already unmodifiable, it is returned.
     * 
     * @param <T> the type of the elements in the collection
     * @param coll  the collection to decorate, must not be null
     * @return an unmodifiable collection
     * @throws IllegalArgumentException if collection is null
     */
    public static <T> Collection<T> unmodifiableCollection(Collection<T> coll) {
        if (coll instanceof Unmodifiable) {
            return coll;
        }
        return new UnmodifiableCollection<T>(coll);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param coll  the collection to decorate, must not be null
     * @throws IllegalArgumentException if collection is null
     */
    private UnmodifiableCollection(Collection<E> coll) {
        super(coll);
    }

    //-----------------------------------------------------------------------
    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator(decorated().iterator());
    }

    @Override
    public boolean add(E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

}
