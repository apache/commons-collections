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
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections4.BulkTest;

/**
 * Abstract test class for {@link SortedSet} methods and contracts.
 * <p>
 * To use, subclass and override the {@link #makeObject()}
 * method.  You may have to override other protected methods if your
 * set is not modifiable, or if your set restricts what kinds of
 * elements may be added; see {@link AbstractSetTest} for more details.
 *
 * @since 3.0
 * @version $Id$
 */
public abstract class AbstractSortedSetTest<E> extends AbstractSetTest<E> {

    /**
     * JUnit constructor.
     *
     * @param name  name for test
     */
    public AbstractSortedSetTest(final String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    /**
     * Verification extension, will check the order of elements,
     * the sets should already be verified equal.
     */
    @Override
    public void verify() {
        super.verify();

        // Check that iterator returns elements in order and first() and last()
        // are consistent
        final Iterator<E> colliter = getCollection().iterator();
        final Iterator<E> confiter = getConfirmed().iterator();
        E first = null;
        E last = null;
        while (colliter.hasNext()) {
            if (first == null) {
                first = colliter.next();
                last = first;
            } else {
              last = colliter.next();
            }
            assertEquals("Element appears to be out of order.", last, confiter.next());
        }
        if (getCollection().size() > 0) {
            assertEquals("Incorrect element returned by first().", first,
                getCollection().first());
            assertEquals("Incorrect element returned by last().", last,
                getCollection().last());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Overridden because SortedSets don't allow null elements (normally).
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
    public abstract SortedSet<E> makeObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<E> makeFullCollection() {
        return (SortedSet<E>) super.makeFullCollection();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an empty {@link TreeSet} for use in modification testing.
     *
     * @return a confirmed empty collection
     */
    @Override
    public SortedSet<E> makeConfirmedCollection() {
        return new TreeSet<E>();
    }

    //-----------------------------------------------------------------------

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
     * Bulk test {@link SortedSet#subSet(Object, Object)}.  This method runs through all of
     * the tests in {@link AbstractSortedSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing a subset.
     */
    public BulkTest bulkTestSortedSetSubSet() {
        final int length = getFullElements().length;

        final int lobound = length / 3;
        final int hibound = lobound * 2;
        return new TestSortedSetSubSet(lobound, hibound);

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

        final int lobound = length / 3;
        final int hibound = lobound * 2;
        return new TestSortedSetSubSet(hibound, true);

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
        final int lobound = length / 3;
        return new TestSortedSetSubSet(lobound, false);
    }

    public class TestSortedSetSubSet extends AbstractSortedSetTest<E> {

        private int m_Type;
        private int m_LowBound;
        private int m_HighBound;
        private E[] m_FullElements;
        private E[] m_OtherElements;

        @SuppressWarnings("unchecked")
        public TestSortedSetSubSet(final int bound, final boolean head) {
            super("TestSortedSetSubSet");
            if (head) {
                //System.out.println("HEADSET");
                m_Type = TYPE_HEADSET;
                m_HighBound = bound;
                m_FullElements = (E[]) new Object[bound];
                System.arraycopy(AbstractSortedSetTest.this.getFullElements(), 0, m_FullElements, 0, bound);
                m_OtherElements = (E[]) new Object[bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                AbstractSortedSetTest.this.getOtherElements(), 0, m_OtherElements, 0, bound - 1);
                //System.out.println(new TreeSet(Arrays.asList(m_FullElements)));
                //System.out.println(new TreeSet(Arrays.asList(m_OtherElements)));
            } else {
                //System.out.println("TAILSET");
                m_Type = TYPE_TAILSET;
                m_LowBound = bound;
                final Object[] allelements = AbstractSortedSetTest.this.getFullElements();
                //System.out.println("bound = "+bound +"::length="+allelements.length);
                m_FullElements = (E[]) new Object[allelements.length - bound];
                System.arraycopy(allelements, bound, m_FullElements, 0, allelements.length - bound);
                m_OtherElements = (E[]) new Object[allelements.length - bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                AbstractSortedSetTest.this.getOtherElements(), bound, m_OtherElements, 0, allelements.length - bound - 1);
                //System.out.println(new TreeSet(Arrays.asList(m_FullElements)));
                //System.out.println(new TreeSet(Arrays.asList(m_OtherElements)));
                //resetFull();
                //System.out.println(collection);
                //System.out.println(confirmed);

            }

        } //type

        @SuppressWarnings("unchecked")
        public TestSortedSetSubSet(final int lobound, final int hibound) {
            super("TestSortedSetSubSet");
            //System.out.println("SUBSET");
            m_Type = TYPE_SUBSET;
            m_LowBound = lobound;
            m_HighBound = hibound;
            final int length = hibound - lobound;
            //System.out.println("Low=" + lobound + "::High=" + hibound + "::Length=" + length);
            m_FullElements = (E[]) new Object[length];
            System.arraycopy(AbstractSortedSetTest.this.getFullElements(), lobound, m_FullElements, 0, length);
            m_OtherElements = (E[]) new Object[length - 1];
            System.arraycopy(//src src_pos dst dst_pos length
            AbstractSortedSetTest.this.getOtherElements(), lobound, m_OtherElements, 0, length - 1);

            //System.out.println(new TreeSet(Arrays.asList(m_FullElements)));
            //System.out.println(new TreeSet(Arrays.asList(m_OtherElements)));

        }

        @Override
        public boolean isNullSupported() {
            return AbstractSortedSetTest.this.isNullSupported();
        }
        @Override
        public boolean isAddSupported() {
            return AbstractSortedSetTest.this.isAddSupported();
        }
        @Override
        public boolean isRemoveSupported() {
            return AbstractSortedSetTest.this.isRemoveSupported();
        }
        @Override
        public boolean isFailFastSupported() {
            return AbstractSortedSetTest.this.isFailFastSupported();
        }

        @Override
        public E[] getFullElements() {
            return m_FullElements;
        }
        @Override
        public E[] getOtherElements() {
            return m_OtherElements;
        }

        private SortedSet<E> getSubSet(final SortedSet<E> set) {
            final E[] elements = AbstractSortedSetTest.this.getFullElements();
            switch (m_Type) {
                case TYPE_SUBSET :
                    return set.subSet(elements[m_LowBound], elements[m_HighBound]);
                case TYPE_HEADSET :
                    return set.headSet(elements[m_HighBound]);
                case TYPE_TAILSET :
                    return set.tailSet(elements[m_LowBound]);
                default :
                    return null;
            }
        }

        @Override
        public SortedSet<E> makeObject() {
            return getSubSet(AbstractSortedSetTest.this.makeObject());
        }

        @Override
        public SortedSet<E> makeFullCollection() {
            return getSubSet(AbstractSortedSetTest.this.makeFullCollection());
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

        static final int TYPE_SUBSET = 0;
        static final int TYPE_TAILSET = 1;
        static final int TYPE_HEADSET = 2;

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
}
