/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/AbstractTestSortedSet.java,v 1.3 2003/10/10 21:19:39 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Abstract test class for {@link SortedSet} methods and contracts.
 * <p>
 * To use, subclass and override the {@link #makeEmptySet()}
 * method.  You may have to override other protected methods if your
 * set is not modifiable, or if your set restricts what kinds of
 * elements may be added; see {@link AbstractTestCollection} for more details.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/10/10 21:19:39 $
 * 
 * @author Stephen Colebourne
 * @author Dieter Wimberger
 */
public abstract class AbstractTestSortedSet extends AbstractTestSet {

    /**
     * JUnit constructor.
     *
     * @param name  name for test
     */
    public AbstractTestSortedSet(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    /**
     * Verification extension, will check the order of elements,
     * the sets should already be verified equal.
     */
    protected void verify() {
        super.verify();
        
        //Sorted sets should return in-order iterators by contract
        Iterator colliter = collection.iterator();
        Iterator confiter = confirmed.iterator();
        while (colliter.hasNext()) {
            assertEquals("Element appears to be out of order.", colliter.next(), confiter.next());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Overridden because SortedSets don't allow null elements (normally).
     * @return false
     */
    protected boolean isNullSupported() {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an empty {@link TreeSet} for use in modification testing.
     *
     * @return a confirmed empty collection
     */
    protected Collection makeConfirmedCollection() {
        return new TreeSet();
    }

    //-----------------------------------------------------------------------
    /**
     * Return the {@link AbstractTestCollection#confirmed} fixture, but cast as a
     * SortedSet.
     */
    protected SortedSet getConfirmedSortedSet() {
        return (SortedSet) confirmed;
    }

    //-----------------------------------------------------------------------
    /**
     * Override to return comparable objects.
     */
    protected Object[] getFullNonNullElements() {
        Object[] elements = new Object[30];

        for (int i = 0; i < 30; i++) {
            elements[i] = new Integer(i + i + 1);
        }
        return elements;
    }

    /**
     * Override to return comparable objects.
     */
    protected Object[] getOtherNonNullElements() {
        Object[] elements = new Object[30];
        for (int i = 0; i < 30; i++) {
            elements[i] = new Integer(i + i + 2);
        }
        return elements;
    }

    //-----------------------------------------------------------------------
    /**
     * Bulk test {@link SortedSet#subSet(Object, Object)}.  This method runs through all of
     * the tests in {@link AbstractTestSortedSet}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractTestSet} instance for testing a subset.
     */
    public BulkTest bulkTestSortedSetSubSet() {
        int length = getFullElements().length;

        int lobound = length / 3;
        int hibound = lobound * 2;
        return new TestSortedSetSubSet(lobound, hibound);

    }

    /**
     * Bulk test {@link SortedSet#headSet(Object)}.  This method runs through all of
     * the tests in {@link AbstractTestSortedSet}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractTestSet} instance for testing a headset.
     */
    public BulkTest bulkTestSortedSetHeadSet() {
        int length = getFullElements().length;

        int lobound = length / 3;
        int hibound = lobound * 2;
        return new TestSortedSetSubSet(hibound, true);

    }

    /**
     * Bulk test {@link SortedSet#tailSet(Object)}.  This method runs through all of
     * the tests in {@link AbstractTestSortedSet}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the set and the other collection views are still valid.
     *
     * @return a {@link AbstractTestSet} instance for testing a tailset.
     */
    public BulkTest bulkTestSortedSetTailSet() {
        int length = getFullElements().length;
        int lobound = length / 3;
        return new TestSortedSetSubSet(lobound, false);
    }

    class TestSortedSetSubSet extends AbstractTestSortedSet {

        private int m_Type;
        private int m_LowBound;
        private int m_HighBound;
        private Object[] m_FullElements;
        private Object[] m_OtherElements;

        public TestSortedSetSubSet(int bound, boolean head) {
            super("TestSortedSetSubSet");
            if (head) {
                //System.out.println("HEADSET");
                m_Type = TYPE_HEADSET;
                m_HighBound = bound;
                m_FullElements = new Object[bound];
                System.arraycopy(AbstractTestSortedSet.this.getFullElements(), 0, m_FullElements, 0, bound);
                m_OtherElements = new Object[bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                AbstractTestSortedSet.this.getOtherElements(), 0, m_OtherElements, 0, bound - 1);
                //System.out.println(new TreeSet(Arrays.asList(m_FullElements)));
                //System.out.println(new TreeSet(Arrays.asList(m_OtherElements)));
            } else {
                //System.out.println("TAILSET");
                m_Type = TYPE_TAILSET;
                m_LowBound = bound;
                Object[] allelements = AbstractTestSortedSet.this.getFullElements();
                //System.out.println("bound = "+bound +"::length="+allelements.length);
                m_FullElements = new Object[allelements.length - bound];
                System.arraycopy(allelements, bound, m_FullElements, 0, allelements.length - bound);
                m_OtherElements = new Object[allelements.length - bound - 1];
                System.arraycopy(//src src_pos dst dst_pos length
                AbstractTestSortedSet.this.getOtherElements(), bound, m_OtherElements, 0, allelements.length - bound - 1);
                //System.out.println(new TreeSet(Arrays.asList(m_FullElements)));
                //System.out.println(new TreeSet(Arrays.asList(m_OtherElements)));
                //resetFull();
                //System.out.println(collection);
                //System.out.println(confirmed);

            }

        } //type

        public TestSortedSetSubSet(int lobound, int hibound) {
            super("TestSortedSetSubSet");
            //System.out.println("SUBSET");
            m_Type = TYPE_SUBSET;
            m_LowBound = lobound;
            m_HighBound = hibound;
            int length = hibound - lobound;
            //System.out.println("Low=" + lobound + "::High=" + hibound + "::Length=" + length);
            m_FullElements = new Object[length];
            System.arraycopy(AbstractTestSortedSet.this.getFullElements(), lobound, m_FullElements, 0, length);
            m_OtherElements = new Object[length - 1];
            System.arraycopy(//src src_pos dst dst_pos length
            AbstractTestSortedSet.this.getOtherElements(), lobound, m_OtherElements, 0, length - 1);

            //System.out.println(new TreeSet(Arrays.asList(m_FullElements)));
            //System.out.println(new TreeSet(Arrays.asList(m_OtherElements)));

        } //TestSortedSetSubSet

        protected boolean isNullSupported() {
            return AbstractTestSortedSet.this.isNullSupported();
        } //useNullValue

        protected Object[] getFullElements() {
            //System.out.println("getFullElements()");
            return m_FullElements;
        }

        protected Object[] getOtherElements() {
            return m_OtherElements;
        }

        private SortedSet getSubSet(SortedSet set) {
            Object[] elements = AbstractTestSortedSet.this.getFullElements();
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
        } //getSubSet

        protected Set makeEmptySet() {
            SortedSet s = (SortedSet) AbstractTestSortedSet.this.makeFullSet();
            s = getSubSet(s);
            s.clear();
            return s;
        } //makeEmptySet

        protected Set makeFullSet() {
            SortedSet s = (SortedSet) AbstractTestSortedSet.this.makeFullCollection();
            return getSubSet(s);
        } //makeFullSet

        protected void resetFull() {
            AbstractTestSortedSet.this.resetFull();
            TestSortedSetSubSet.this.confirmed = getSubSet((SortedSet) AbstractTestSortedSet.this.confirmed);
            TestSortedSetSubSet.this.collection = getSubSet((SortedSet) AbstractTestSortedSet.this.collection);
        }

        protected void resetEmpty() {
            TestSortedSetSubSet.this.resetFull();
            TestSortedSetSubSet.this.confirmed.clear();
            TestSortedSetSubSet.this.collection.clear();
        }

        public BulkTest bulkTestSortedSetSubSet() {
            //Override returning null to prevent endless
            //loop of bulk tests
            return null;
        } //bulkTestSortedSetSubSet

        public BulkTest bulkTestSortedSetHeadSet() {
            return null;
        }

        public BulkTest bulkTestSortedSetTailSet() {
            return null;
        }

        static final int TYPE_SUBSET = 0;
        static final int TYPE_TAILSET = 1;
        static final int TYPE_HEADSET = 2;

    }

}
