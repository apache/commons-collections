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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections4.Bag;

/**
 * Decorates another {@link Bag} to comply with the Collection contract.
 *
 * @since 4.0
 * @version $Id$
 */
public final class CompliantBag<E>
        extends AbstractBagDecorator<E> implements Serializable {

    /** Serialization version */
    private static final long serialVersionUID = -2560033712679053143L;

    /**
     * Factory method to create a bag that complies to the Collection contract.
     *
     * @param <E> the type of the elements in the bag
     * @param bag  the bag to decorate, must not be null
     * @return a Bag that complies to the Collection contract
     * @throws IllegalArgumentException if bag is null
     */
    public static <E> Bag<E> compliantBag(final Bag<E> bag) {
        return new CompliantBag<E>(bag);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param bag  the bag to decorate, must not be null
     * @throws IllegalArgumentException if bag is null
     */
    public CompliantBag(final Bag<E> bag) {
        super(bag);
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
     * @throws ClassCastException if deserialised object has wrong type
     */
    @SuppressWarnings("unchecked") // will throw CCE, see Javadoc
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        collection = (Collection<E>) in.readObject();
    }

    //-----------------------------------------------------------------------
    // Collection interface
    //-----------------------------------------------------------------------

    @Override
    public boolean containsAll(final Collection<?> coll) {
        final Iterator<?> e = coll.iterator();
        while (e.hasNext()) {
            if(!contains(e.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean add(final E object) {
        return add(object, 1);
    }

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        boolean changed = false;
        final Iterator<? extends E> i = coll.iterator();
        while (i.hasNext()) {
            final boolean added = add(i.next(), 1);
            changed = changed || added;
        }
        return changed;
    }

    @Override
    public boolean remove(final Object object) {
        return remove(object, 1);
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        boolean result = false;
        if (coll != null) {
            final Iterator<?> i = coll.iterator();
            while (i.hasNext()) {
                final Object obj = i.next();
                final boolean changed = remove(obj, getCount(obj));
                result = result || changed;
            }
        }
        return result;
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
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

    //-----------------------------------------------------------------------
    // Bag interface
    //-----------------------------------------------------------------------

    public boolean add(final E object, final int count) {
        decorated().add(object, count);
        return true;
    }

}
