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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.apache.commons.collections4.BulkTest;

/**
 * Tests {@link NavigableSet}.
 * <p>
 * To use, subclass and override the {@link #makeObject()}
 * method.  You may have to override other protected methods if your
 * set is not modifiable, or if your set restricts what kinds of
 * elements may be added; see {@link AbstractSetTest} for more details.
 */
public abstract class AbstractNavigableSetTest<E> extends AbstractSortedSetTest<E> {

    public class TestNavigableSetSubSet extends AbstractNavigableSetTest<E> {

        static final int TYPE_SUBSET = 0;
        static final int TYPE_TAILSET = 1;
        static final int TYPE_HEADSET = 2;
        private final int type;
        private int lowBound;
        private int highBound;

        private final E[] fullElements;

        private final E[] otherElements;

        private final boolean inclusive;
        @SuppressWarnings("unchecked")
        public TestNavigableSetSubSet(final int bound, final boolean head, final boolean inclusive) {
            if (head) {
                this.type = TYPE_HEADSET;
                this.inclusive = inclusive;
                this.highBound = bound;

                final int realBound = inclusive ? bound + 1 : bound;
                fullElements = (E[]) new Object[realBound];
                System.arraycopy(AbstractNavigableSetTest.this.getFullElements(), 0, fullElements, 0, realBound);
                otherElements = (E[]) new Object[bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                    AbstractNavigableSetTest.this.getOtherElements(), 0, otherElements, 0, bound - 1);
            } else {
                type = TYPE_TAILSET;
                this.inclusive = inclusive;
                lowBound = bound;
                final Object[] allElements = AbstractNavigableSetTest.this.getFullElements();
                final int realBound = inclusive ? bound : bound + 1;
                fullElements = (E[]) new Object[allElements.length - realBound];
                System.arraycopy(allElements, realBound, fullElements, 0, allElements.length - realBound);
                otherElements = (E[]) new Object[allElements.length - bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                    AbstractNavigableSetTest.this.getOtherElements(), bound, otherElements, 0, allElements.length - bound - 1);
            }

        } //type
        @SuppressWarnings("unchecked")
        public TestNavigableSetSubSet(final int loBound, final int hiBound, final boolean inclusive) {
            this.type = TYPE_SUBSET;
            this.lowBound = loBound;
            this.highBound = hiBound;
            this.inclusive = inclusive;

            final int fullLoBound = inclusive ? loBound : loBound + 1;
            final int length = hiBound - loBound + 1 - (inclusive ? 0 : 2);
            fullElements = (E[]) new Object[length];
            System.arraycopy(AbstractNavigableSetTest.this.getFullElements(), fullLoBound, fullElements, 0, length);
            final int otherLength = hiBound - loBound;
            otherElements = (E[]) new Object[otherLength - 1];
            System.arraycopy(//src src_pos dst dst_pos length
                AbstractNavigableSetTest.this.getOtherElements(), loBound, otherElements, 0, otherLength - 1);
        }
        @Override
        public BulkTest bulkTestNavigableSetHeadSet() {
            return null;  // prevent infinite recursion
        }

        @Override
        public BulkTest bulkTestNavigableSetSubSet() {
            return null;  // prevent infinite recursion
        }
        @Override
        public BulkTest bulkTestNavigableSetTailSet() {
            return null;  // prevent infinite recursion
        }

        @Override
        public BulkTest bulkTestSortedSetHeadSet() {
            return null;  // prevent infinite recursion
        }

        @Override
        public BulkTest bulkTestSortedSetSubSet() {
            return null;  // prevent infinite recursion
        }

        @Override
        public BulkTest bulkTestSortedSetTailSet() {
            return null;  // prevent infinite recursion
        }

        @Override
        public E[] getFullElements() {
            return fullElements;
        }

        @Override
        public E[] getOtherElements() {
            return otherElements;
        }

        private NavigableSet<E> getSubSet(final NavigableSet<E> set) {
            final E[] elements = AbstractNavigableSetTest.this.getFullElements();
            switch (type) {
            case TYPE_SUBSET:
                return set.subSet(elements[lowBound], inclusive, elements[highBound], inclusive);
            case TYPE_HEADSET:
                return set.headSet(elements[highBound], inclusive);
            case TYPE_TAILSET:
                return set.tailSet(elements[lowBound], inclusive);
            default:
                return null;
            }
        }
        @Override
        public boolean isAddSupported() {
            return AbstractNavigableSetTest.this.isAddSupported();
        }
        @Override
        public boolean isFailFastSupported() {
            return AbstractNavigableSetTest.this.isFailFastSupported();
        }
        @Override
        public boolean isNullSupported() {
            return AbstractNavigableSetTest.this.isNullSupported();
        }
        @Override
        public boolean isRemoveSupported() {
            return AbstractNavigableSetTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }
        @Override
        public NavigableSet<E> makeFullCollection() {
            return getSubSet(AbstractNavigableSetTest.this.makeFullCollection());
        }
        @Override
        public NavigableSet<E> makeObject() {
            return getSubSet(AbstractNavigableSetTest.this.makeObject());
        }

    }

    /**
     * Bulk test {@link NavigableSet#headSet(Object, boolean)}.
     * This method runs through all of the tests in {@link AbstractNavigableSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractNavigableSetTest} instance for testing a headset.
     */
    public BulkTest bulkTestNavigableSetHeadSet() {
        final int length = getFullElements().length;

        final int loBound = length / 3;
        final int hiBound = loBound * 2;
        return new TestNavigableSetSubSet(hiBound, true, true);
    }

    /**
     * Bulk test {@link NavigableSet#subSet(Object, boolean, Object, boolean)}.
     * This method runs through all of the tests in {@link AbstractNavigableSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractNavigableSetTest} instance for testing a subset.
     */
    public BulkTest bulkTestNavigableSetSubSet() {
        final int length = getFullElements().length;

        final int loBound = length / 3;
        final int hiBound = loBound * 2;
        return new TestNavigableSetSubSet(loBound, hiBound, false);
    }

    /**
     * Bulk test {@link NavigableSet#tailSet(Object, boolean)}.
     * This method runs through all of the tests in {@link AbstractNavigableSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractNavigableSetTest} instance for testing a tailset.
     */
    public BulkTest bulkTestNavigableSetTailSet() {
        final int length = getFullElements().length;
        final int loBound = length / 3;
        return new TestNavigableSetSubSet(loBound, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableSet<E> getCollection() {
        return (NavigableSet<E>) super.getCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableSet<E> getConfirmed() {
        return (NavigableSet<E>) super.getConfirmed();
    }

    /**
     * Override to return comparable objects.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E[] getFullNonNullElements() {
        final Object[] elements = new Object[30];

        for (int i = 0; i < 30; i++) {
            elements[i] = Integer.valueOf(i + i + 1);
        }
        return (E[]) elements;
    }

    /**
     * Override to return comparable objects.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E[] getOtherNonNullElements() {
        final Object[] elements = new Object[30];
        for (int i = 0; i < 30; i++) {
            elements[i] = Integer.valueOf(i + i + 2);
        }
        return (E[]) elements;
    }

    /**
     * Returns an empty {@link TreeSet} for use in modification testing.
     *
     * @return a confirmed empty collection
     */
    @Override
    public NavigableSet<E> makeConfirmedCollection() {
        return new TreeSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableSet<E> makeFullCollection() {
        return (NavigableSet<E>) super.makeFullCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract NavigableSet<E> makeObject();

    /**
     * Verification extension, will check the order of elements,
     * the sets should already be verified equal.
     */
    @Override
    public void verify() {
        super.verify();

        // Check that descending iterator returns elements in order and higher(), lower(),
        // floor() and ceiling() are consistent
        final Iterator<E> collIter = getCollection().descendingIterator();
        final Iterator<E> confIter = getConfirmed().descendingIterator();
        while (collIter.hasNext()) {
            final E element = collIter.next();
            final E confElement = confIter.next();
            assertEquals(confElement, element, "Element appears to be out of order.");

            assertEquals(getConfirmed().higher(element),
                    getCollection().higher(element), "Incorrect element returned by higher().");

            assertEquals(getConfirmed().lower(element),
                    getCollection().lower(element), "Incorrect element returned by lower().");

            assertEquals(getConfirmed().floor(element),
                    getCollection().floor(element), "Incorrect element returned by floor().");

            assertEquals(getConfirmed().ceiling(element),
                    getCollection().ceiling(element), "Incorrect element returned by ceiling().");
        }
    }

}
