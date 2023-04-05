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

import java.util.Iterator;

import org.apache.commons.collections4.SortedBag;

/**
 * Abstract test class for
 * {@link org.apache.commons.collections4.SortedBag SortedBag}
 * methods and contracts.
 *
 * @since 3.0
 */
public abstract class AbstractSortedBagTest<T> extends AbstractBagTest<T> {

    public AbstractSortedBagTest(final String testName) {
        super(testName);
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
            assertEquals("Element appears to be out of order.", last, confIter.next());
        }
        if (!getCollection().isEmpty()) {
            assertEquals("Incorrect element returned by first().", first,
                getCollection().first());
            assertEquals("Incorrect element returned by last().", last,
                getCollection().last());
        }
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
     * {@inheritDoc}
     */
    @Override
    public abstract SortedBag<T> makeObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedBag<T> makeFullCollection() {
        return (SortedBag<T>) super.makeFullCollection();
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


    @Override
    public void resetEmpty() {
        this.setCollection(CollectionSortedBag.collectionSortedBag(makeObject()));
        this.setConfirmed(makeConfirmedCollection());
    }

    @Override
    public void resetFull() {
        this.setCollection(CollectionSortedBag.collectionSortedBag(makeFullCollection()));
        this.setConfirmed(makeConfirmedFullCollection());
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
     * Returns the {@link #collection} field cast to a {@link SortedBag}.
     *
     * @return the collection field as a SortedBag
     */
    @Override
    public SortedBag<T> getCollection() {
        return (SortedBag<T>) super.getCollection();
    }


    // TODO: Add the SortedBag tests!
}
