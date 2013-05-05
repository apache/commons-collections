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
package org.apache.commons.collections4.set;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;

/**
 * Decorates another <code>SortedSet</code> to ensure it can't be altered.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException.
 *
 * @since 3.0
 * @version $Id$
 */
public final class UnmodifiableSortedSet<E>
        extends AbstractSortedSetDecorator<E>
        implements Unmodifiable, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = -725356885467962424L;

    /**
     * Factory method to create an unmodifiable set.
     *
     * @param <E> the element type
     * @param set  the set to decorate, must not be null
     * @return a new unmodifiable {@link SortedSet}
     * @throws IllegalArgumentException if set is null
     * @since 4.0
     */
    public static <E> SortedSet<E> unmodifiableSortedSet(final SortedSet<E> set) {
        if (set instanceof Unmodifiable) {
            return set;
        }
        return new UnmodifiableSortedSet<E>(set);
    }

    //-----------------------------------------------------------------------
    /**
     * Write the collection out using a custom routine.
     *
     * @param out  the output stream
     * @throws IOException
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(collection);
    }

    /**
     * Read the collection in using a custom routine.
     *
     * @param in  the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked") // (1) should only fail if input stream is incorrect
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        collection = (Collection<E>) in.readObject(); // (1)
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    private UnmodifiableSortedSet(final SortedSet<E> set) {
        super(set);
    }

    //-----------------------------------------------------------------------
    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator(decorated().iterator());
    }

    @Override
    public boolean add(final E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    //-----------------------------------------------------------------------
    @Override
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        final SortedSet<E> sub = decorated().subSet(fromElement, toElement);
        return new UnmodifiableSortedSet<E>(sub);
    }

    @Override
    public SortedSet<E> headSet(final E toElement) {
        final SortedSet<E> sub = decorated().headSet(toElement);
        return new UnmodifiableSortedSet<E>(sub);
    }

    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        final SortedSet<E> sub = decorated().tailSet(fromElement);
        return new UnmodifiableSortedSet<E>(sub);
    }

}
