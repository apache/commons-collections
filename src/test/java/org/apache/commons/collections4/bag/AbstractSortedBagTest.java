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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.apache.commons.collections4.SortedBag;

/**
 * Abstract test class for
 * {@link org.apache.commons.collections4.SortedBag SortedBag}
 * methods and contracts.
 */
public abstract class AbstractSortedBagTest<T> extends AbstractBagTest<T> {

    /**
     * Returns the {@link #collection} field cast to a {@link SortedBag}.
     *
     * @return the collection field as a SortedBag
     */
    @Override
    public SortedBag<T> getCollection() {
        return (SortedBag<T>) super.getCollection();
    }

    /**
     * Override to return comparable objects.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T[] getFullNonNullElements() {
        final Object[] elements = new Object[30];

        for (int i = 0; i < 30; i++) {
            elements[i] = Integer.valueOf(i + i + 1);
        }
        return (T[]) elements;
    }

    /**
     * Override to return comparable objects.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T[] getOtherNonNullElements() {
        final Object[] elements = new Object[30];
        for (int i = 0; i < 30; i++) {
            elements[i] = Integer.valueOf(i + i + 2);
        }
        return (T[]) elements;
    }

    /**
     * Overridden because SortedBags don't allow null elements (normally).
     * @return false
     */
    @Override
    public boolean isNullSupported() {
        return false;
    }

    /**
     * Returns an empty {@link TreeBag} for use in modification testing.
     *
     * @return a confirmed empty collection
     */
    @Override
    public SortedBag<T> makeConfirmedCollection() {
        return new TreeBag<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedBag<T> makeFullCollection() {
        return (SortedBag<T>) super.makeFullCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract SortedBag<T> makeObject();

    @Override
    public void resetEmpty() {
        setCollection(CollectionSortedBag.collectionSortedBag(makeObject()));
        setConfirmed(makeConfirmedCollection());
    }

    @Override
    public void resetFull() {
        setCollection(CollectionSortedBag.collectionSortedBag(makeFullCollection()));
        setConfirmed(makeConfirmedFullCollection());
    }

    /**
     * Verification extension, will check the order of elements,
     * the sets should already be verified equal.
     */
    @Override
    public void verify() {
        super.verify();

        // Check that iterator returns elements in order and first() and last()
        // are consistent
        final Iterator<T> collIter = getCollection().iterator();
        final Iterator<T> confIter = getConfirmed().iterator();
        T first = null;
        T last = null;
        while (collIter.hasNext()) {
            if (first == null) {
                first = collIter.next();
                last = first;
            } else {
                last = collIter.next();
            }
            assertEquals(last, confIter.next(), "Element appears to be out of order.");
        }
        if (!getCollection().isEmpty()) {
            assertEquals(first, getCollection().first(),
                "Incorrect element returned by first().");
            assertEquals(last, getCollection().last(),
                "Incorrect element returned by last().");
        }
    }

    // TODO: Add the SortedBag tests!
}
