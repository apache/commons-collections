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

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.apache.commons.collections4.BulkTest;

/**
 * Abstract test class for {@link NavigableSet} methods and contracts.
 * <p>
 * To use, subclass and override the {@link #makeObject()}
 * method.  You may have to override other protected methods if your
 * set is not modifiable, or if your set restricts what kinds of
 * elements may be added; see {@link AbstractSetTest} for more details.
 *
 * @since 4.1
 */
public abstract class AbstractNavigableSetTest<E> extends AbstractSortedSetTest<E> {

    /**
     * JUnit constructor.
     *
     * @param name  name for test
     */
    public AbstractNavigableSetTest(final String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract NavigableSet<E> makeObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigableSet<E> makeFullCollection() {
        return (NavigableSet<E>) super.makeFullCollection();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an empty {@link TreeSet} for use in modification testing.
     *
     * @return a confirmed empty collection
     */
    @Override
    public NavigableSet<E> makeConfirmedCollection() {
        return new TreeSet<>();
    }

    //-----------------------------------------------------------------------

    /**
     * Verification extension, will check the order of elements,
     * the sets should already be verified equal.
     */
    @Override
    public void verify() {
        super.verify();

        // Check that descending iterator returns elements in order and higher(), lower(),
        // floor() and ceiling() are consistent
        final Iterator<E> colliter = getCollection().descendingIterator();
        final Iterator<E> confiter = getConfirmed().descendingIterator();
        while (colliter.hasNext()) {
            final E element = colliter.next();
            final E confelement = confiter.next();
            assertEquals("Element appears to be out of order.", confelement, element);

            assertEquals("Incorrect element returned by higher().", getConfirmed().higher(element),
                                                                    getCollection().higher(element));

            assertEquals("Incorrect element returned by lower().", getConfirmed().lower(element),
                                                                   getCollection().lower(element));

            assertEquals("Incorrect element returned by floor().", getConfirmed().floor(element),
                                                                   getCollection().floor(element));

            assertEquals("Incorrect element returned by ceiling().", getConfirmed().ceiling(element),
                                                                     getCollection().ceiling(element));
        }
    }

    //-----------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
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

        final int lobound = length / 3;
        final int hibound = lobound * 2;
        return new TestNavigableSetSubSet(lobound, hibound, false);
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

        final int lobound = length / 3;
        final int hibound = lobound * 2;
        return new TestNavigableSetSubSet(hibound, true, true);
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
        final int lobound = length / 3;
        return new TestNavigableSetSubSet(lobound, false, false);
    }

    public class TestNavigableSetSubSet extends AbstractNavigableSetTest<E> {

        private final int type;
        private int lowBound;
        private int highBound;
        private final E[] fullElements;
        private final E[] otherElements;
        private final boolean m_Inclusive;

        @SuppressWarnings("unchecked")
        public TestNavigableSetSubSet(final int bound, final boolean head, final boolean inclusive) {
            super("TestNavigableSetSubSet");
            if (head) {
                type = TYPE_HEADSET;
                m_Inclusive = inclusive;
                highBound = bound;

                final int realBound = inclusive ? bound + 1 : bound;
                fullElements = (E[]) new Object[realBound];
                System.arraycopy(AbstractNavigableSetTest.this.getFullElements(), 0, fullElements, 0, realBound);
                otherElements = (E[]) new Object[bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                    AbstractNavigableSetTest.this.getOtherElements(), 0, otherElements, 0, bound - 1);
            } else {
                type = TYPE_TAILSET;
                m_Inclusive = inclusive;
                lowBound = bound;
                final Object[] allelements = AbstractNavigableSetTest.this.getFullElements();
                final int realBound = inclusive ? bound : bound + 1;
                fullElements = (E[]) new Object[allelements.length - realBound];
                System.arraycopy(allelements, realBound, fullElements, 0, allelements.length - realBound);
                otherElements = (E[]) new Object[allelements.length - bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                    AbstractNavigableSetTest.this.getOtherElements(), bound, otherElements, 0, allelements.length - bound - 1);
            }

        } //type

        @SuppressWarnings("unchecked")
        public TestNavigableSetSubSet(final int lobound, final int hibound, final boolean inclusive) {
            super("TestNavigableSetSubSet");
            type = TYPE_SUBSET;
            lowBound = lobound;
            highBound = hibound;
            m_Inclusive = inclusive;

            final int fullLoBound = inclusive ? lobound : lobound + 1;
            final int length = hibound - lobound + 1 - (inclusive ? 0 : 2);
            fullElements = (E[]) new Object[length];
            System.arraycopy(AbstractNavigableSetTest.this.getFullElements(), fullLoBound, fullElements, 0, length);
            final int otherLength = hibound - lobound;
            otherElements = (E[]) new Object[otherLength - 1];
            System.arraycopy(//src src_pos dst dst_pos length
                AbstractNavigableSetTest.this.getOtherElements(), lobound, otherElements, 0, otherLength - 1);
        }

        @Override
        public boolean isNullSupported() {
            return AbstractNavigableSetTest.this.isNullSupported();
        }
        @Override
        public boolean isAddSupported() {
            return AbstractNavigableSetTest.this.isAddSupported();
        }
        @Override
        public boolean isRemoveSupported() {
            return AbstractNavigableSetTest.this.isRemoveSupported();
        }
        @Override
        public boolean isFailFastSupported() {
            return AbstractNavigableSetTest.this.isFailFastSupported();
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
            case TYPE_SUBSET :
                return set.subSet(elements[lowBound], m_Inclusive, elements[highBound], m_Inclusive);
            case TYPE_HEADSET :
                return set.headSet(elements[highBound], m_Inclusive);
            case TYPE_TAILSET :
                return set.tailSet(elements[lowBound], m_Inclusive);
            default :
                return null;
            }
        }

        @Override
        public NavigableSet<E> makeObject() {
            return getSubSet(AbstractNavigableSetTest.this.makeObject());
        }

        @Override
        public NavigableSet<E> makeFullCollection() {
            return getSubSet(AbstractNavigableSetTest.this.makeFullCollection());
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public BulkTest bulkTestSortedSetSubSet() {
            return null;  // prevent infinite recursion
        }
        @Override
        public BulkTest bulkTestSortedSetHeadSet() {
            return null;  // prevent infinite recursion
        }
        @Override
        public BulkTest bulkTestSortedSetTailSet() {
            return null;  // prevent infinite recursion
        }
        @Override
        public BulkTest bulkTestNavigableSetSubSet() {
            return null;  // prevent infinite recursion
        }
        @Override
        public BulkTest bulkTestNavigableSetHeadSet() {
            return null;  // prevent infinite recursion
        }
        @Override
        public BulkTest bulkTestNavigableSetTailSet() {
            return null;  // prevent infinite recursion
        }

        static final int TYPE_SUBSET = 0;
        static final int TYPE_TAILSET = 1;
        static final int TYPE_HEADSET = 2;

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

}
