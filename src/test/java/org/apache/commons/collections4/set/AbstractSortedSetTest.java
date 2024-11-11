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
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections4.BulkTest;

/**
 * Tests {@link SortedSet}.
 * <p>
 * To use, subclass and override the {@link #makeObject()}
 * method.  You may have to override other protected methods if your
 * set is not modifiable, or if your set restricts what kinds of
 * elements may be added; see {@link AbstractSetTest} for more details.
 */
public abstract class AbstractSortedSetTest<E> extends AbstractSetTest<E> {

    public class TestSortedSetSubSet extends AbstractSortedSetTest<E> {

        static final int TYPE_SUBSET = 0;
        static final int TYPE_TAILSET = 1;
        static final int TYPE_HEADSET = 2;
        private final int type;
        private int lowBound;

        private int highBound;

        private final E[] fullElements;

        private final E[] otherElements;
        @SuppressWarnings("unchecked")
        public TestSortedSetSubSet(final int bound, final boolean head) {
            if (head) {
                //System.out.println("HEADSET");
                this.type = TYPE_HEADSET;
                this.highBound = bound;
                this.fullElements = (E[]) new Object[bound];
                System.arraycopy(AbstractSortedSetTest.this.getFullElements(), 0, fullElements, 0, bound);
                this.otherElements = (E[]) new Object[bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                    AbstractSortedSetTest.this.getOtherElements(), 0, otherElements, 0, bound - 1);
                //System.out.println(new TreeSet(Arrays.asList(m_FullElements)));
                //System.out.println(new TreeSet(Arrays.asList(m_OtherElements)));
            } else {
                //System.out.println("TAILSET");
                this.type = TYPE_TAILSET;
                this.lowBound = bound;
                final Object[] allElements = AbstractSortedSetTest.this.getFullElements();
                //System.out.println("bound = "+bound +"::length="+allElements.length);
                this.fullElements = (E[]) new Object[allElements.length - bound];
                System.arraycopy(allElements, bound, fullElements, 0, allElements.length - bound);
                this.otherElements = (E[]) new Object[allElements.length - bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                    AbstractSortedSetTest.this.getOtherElements(), bound, otherElements, 0, allElements.length - bound - 1);
                //System.out.println(new TreeSet(Arrays.asList(m_FullElements)));
                //System.out.println(new TreeSet(Arrays.asList(m_OtherElements)));
                //resetFull();
                //System.out.println(collection);
                //System.out.println(confirmed);

            }

        } //type
        @SuppressWarnings("unchecked")
        public TestSortedSetSubSet(final int loBound, final int hiBound) {
            //System.out.println("SUBSET");
            this.type = TYPE_SUBSET;
            this.lowBound = loBound;
            this.highBound = hiBound;
            final int length = hiBound - loBound;
            //System.out.println("Low=" + loBound + "::High=" + hiBound + "::Length=" + length);
            this.fullElements = (E[]) new Object[length];
            System.arraycopy(AbstractSortedSetTest.this.getFullElements(), loBound, fullElements, 0, length);
            this.otherElements = (E[]) new Object[length - 1];
            System.arraycopy(//src src_pos dst dst_pos length
                AbstractSortedSetTest.this.getOtherElements(), loBound, otherElements, 0, length - 1);

            //System.out.println(new TreeSet(Arrays.asList(m_FullElements)));
            //System.out.println(new TreeSet(Arrays.asList(m_OtherElements)));

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

        private SortedSet<E> getSubSet(final SortedSet<E> set) {
            final E[] elements = AbstractSortedSetTest.this.getFullElements();
            switch (type) {
            case TYPE_SUBSET:
                return set.subSet(elements[lowBound], elements[highBound]);
            case TYPE_HEADSET:
                return set.headSet(elements[highBound]);
            case TYPE_TAILSET:
                return set.tailSet(elements[lowBound]);
            default:
                return null;
            }
        }

        @Override
        public boolean isAddSupported() {
            return AbstractSortedSetTest.this.isAddSupported();
        }

        @Override
        public boolean isFailFastSupported() {
            return AbstractSortedSetTest.this.isFailFastSupported();
        }
        @Override
        public boolean isNullSupported() {
            return AbstractSortedSetTest.this.isNullSupported();
        }
        @Override
        public boolean isRemoveSupported() {
            return AbstractSortedSetTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }
        @Override
        public SortedSet<E> makeFullCollection() {
            return getSubSet(AbstractSortedSetTest.this.makeFullCollection());
        }
        @Override
        public SortedSet<E> makeObject() {
            return getSubSet(AbstractSortedSetTest.this.makeObject());
        }

    }

    /**
     * Bulk test {@link SortedSet#headSet(Object)}.  This method runs through all of
     * the tests in {@link AbstractSortedSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing a headset.
     */
    public BulkTest bulkTestSortedSetHeadSet() {
        final int length = getFullElements().length;

        final int loBound = length / 3;
        final int hiBound = loBound * 2;
        return new TestSortedSetSubSet(hiBound, true);
    }

    /**
     * Bulk test {@link SortedSet#subSet(Object, Object)}.  This method runs through all of
     * the tests in {@link AbstractSortedSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing a subset.
     */
    public BulkTest bulkTestSortedSetSubSet() {
        final int length = getFullElements().length;

        final int loBound = length / 3;
        final int hiBound = loBound * 2;
        return new TestSortedSetSubSet(loBound, hiBound);

    }

    /**
     * Bulk test {@link SortedSet#tailSet(Object)}.  This method runs through all of
     * the tests in {@link AbstractSortedSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing a tailset.
     */
    public BulkTest bulkTestSortedSetTailSet() {
        final int length = getFullElements().length;
        final int loBound = length / 3;
        return new TestSortedSetSubSet(loBound, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<E> getCollection() {
        return (SortedSet<E>) super.getCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<E> getConfirmed() {
        return (SortedSet<E>) super.getConfirmed();
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
     * Overridden because SortedSets don't allow null elements (normally).
     * @return false
     */
    @Override
    public boolean isNullSupported() {
        return false;
    }

    /**
     * Returns an empty {@link TreeSet} for use in modification testing.
     *
     * @return a confirmed empty collection
     */
    @Override
    public SortedSet<E> makeConfirmedCollection() {
        return new TreeSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<E> makeFullCollection() {
        return (SortedSet<E>) super.makeFullCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract SortedSet<E> makeObject();

    /**
     * Verification extension, will check the order of elements,
     * the sets should already be verified equal.
     */
    @Override
    public void verify() {
        super.verify();

        // Check that iterator returns elements in order and first() and last()
        // are consistent
        final Iterator<E> collIter = getCollection().iterator();
        final Iterator<E> confIter = getConfirmed().iterator();
        E first = null;
        E last = null;
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
            assertEquals(first,
                getCollection().first(), "Incorrect element returned by first().");
            assertEquals(last,
                getCollection().last(), "Incorrect element returned by last().");
        }
    }
}
