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
package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections4.SortedBag;

/**
 * Decorates another {@link SortedBag} to comply with the Collection contract.
 *
 * @param <E> the type of elements in this bag
 * @since 4.0
 */
public final class CollectionSortedBag<E> extends AbstractSortedBagDecorator<E> {

    /** Serialization version */
    private static final long serialVersionUID = -2560033712679053143L;

    /**
     * Factory method to create a sorted bag that complies to the Collection contract.
     *
     * @param <E> the type of the elements in the bag
     * @param bag  the sorted bag to decorate, must not be null
     * @return a SortedBag that complies to the Collection contract
     * @throws NullPointerException if bag is null
     */
    public static <E> SortedBag<E> collectionSortedBag(final SortedBag<E> bag) {
        return new CollectionSortedBag<>(bag);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param bag  the sorted bag to decorate, must not be null
     * @throws NullPointerException if bag is null
     */
    public CollectionSortedBag(final SortedBag<E> bag) {
        super(bag);
    }

    @Override
    public boolean add(final E object) {
        return add(object, 1);
    }

    @Override
    public boolean add(final E object, final int count) {
        decorated().add(object, count);
        return true;
    }

    // Collection interface

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        boolean changed = false;
        for (final E current : coll) {
            final boolean added = add(current, 1);
            changed = changed || added;
        }
        return changed;
    }

    @Override
    public boolean containsAll(final Collection<?> coll) {
        return coll.stream().allMatch(this::contains);
    }

    /**
     * Deserializes the collection in using a custom routine.
     *
     * @param in  the input stream
     * @throws IOException if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     * @throws ClassCastException if deserialized object has wrong type
     */
    @SuppressWarnings("unchecked") // will throw CCE, see Javadoc
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setCollection((Collection<E>) in.readObject());
    }

    @Override
    public boolean remove(final Object object) {
        return remove(object, 1);
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        if (coll != null) {
            boolean result = false;
            for (final Object obj : coll) {
                final boolean changed = remove(obj, getCount(obj));
                result = result || changed;
            }
            return result;
        }
        // let the decorated bag handle the case of null argument
        return decorated().removeAll(null);
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
        if (coll != null) {
            boolean modified = false;
            final Iterator<E> e = iterator();
            while (e.hasNext()) {
                if (!coll.contains(e.next())) {
                    e.remove();
                    modified = true;
                }
            }
            return modified;
        }
        // let the decorated bag handle the case of null argument
        return decorated().retainAll(null);
    }

    /**
     * Serializes this object to an ObjectOutputStream.
     *
     * @param out the target ObjectOutputStream.
     * @throws IOException thrown when an I/O errors occur writing to the target stream.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(decorated());
    }

}
