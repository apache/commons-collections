/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/comparators/ComparatorChain.java,v 1.7 2002/06/12 03:59:17 mas Exp $
 * $Revision: 1.7 $
 * $Date: 2002/06/12 03:59:17 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * <p>A ComparatorChain is a Comparator that wraps one or
 * more Comparators in sequence.  The ComparatorChain
 * calls each Comparator in sequence until either 1)
 * any single Comparator returns a non-zero result
 * (and that result is then returned),
 * or 2) the ComparatorChain is exhausted (and zero is
 * returned).  This type of sorting is very similar
 * to multi-column sorting in SQL, and this class
 * allows Java classes to emulate that kind of behaviour
 * when sorting a List.</p>
 * 
 * <p>To further facilitate SQL-like sorting, the order of
 * any single Comparator in the list can be reversed.</p>
 * 
 * <p>Calling a method that adds new Comparators or
 * changes the ascend/descend sort <i>after compare(Object,
 * Object) has been called</i> will result in an
 * UnsupportedOperationException.  However, <i>take care</i>
 * to not alter the underlying List of Comparators
 * or the BitSet that defines the sort order.</p>
 * 
 * <p>Instances of ComparatorChain are not synchronized.
 * The class is not thread-safe at construction time, but
 * it <i>is</i> thread-safe to perform multiple comparisons
 * after all the setup operations are complete.</p>
 * 
 * @since 2.0
 * @author Morgan Delagrange
 */
public class ComparatorChain implements Comparator,Serializable {

    protected List comparatorChain = null;
    // 0 = ascend; 1 = descend
    protected BitSet orderingBits = null;

    // ComparatorChain is "locked" after the first time
    // compare(Object,Object) is called
    protected boolean isLocked = false;

    /**
     * Construct a ComparatorChain with no Comparators.
     * You must add at least one Comparator before calling
     * the compare(Object,Object) method, or an 
     * UnsupportedOperationException is thrown
     */
    public ComparatorChain() {
        this(new ArrayList(),new BitSet());
    }

    /**
     * Construct a ComparatorChain with a single Comparator,
     * sorting in the forward order
     * 
     * @param comparator First comparator in the Comparator chain
     */
    public ComparatorChain(Comparator comparator) {
        this(comparator,false);
    }

    /**
     * Construct a Comparator chain with a single Comparator,
     * sorting in the given order
     * 
     * @param comparator First Comparator in the ComparatorChain
     * @param reverse    false = forward sort; true = reverse sort
     */
    public ComparatorChain(Comparator comparator, boolean reverse) {
        comparatorChain = new ArrayList();
        comparatorChain.add(comparator);
        orderingBits = new BitSet(1);
        if (reverse == true) {
            orderingBits.set(0);
        }
    }

    /**
     * Construct a ComparatorChain from the Comparators in the
     * List.  All Comparators will default to the forward 
     * sort order.
     * 
     * @param list   List of Comparators
     * @see #ComparatorChain(List,BitSet)
     */
    public ComparatorChain(List list) {
        this(list,new BitSet(list.size()));
    }

    /**
     * Construct a ComparatorChain from the Comparators in the
     * given List.  The sort order of each column will be
     * drawn from the given BitSet.  When determining the sort
     * order for Comparator at index <i>i</i> in the List,
     * the ComparatorChain will call BitSet.get(<i>i</i>).
     * If that method returns <i>false</i>, the forward
     * sort order is used; a return value of <i>true</i>
     * indicates reverse sort order.
     * 
     * @param list   List of Comparators.  NOTE: This constructor does not perform a
     *               defensive copy of the list
     * @param bits   Sort order for each Comparator.  Extra bits are ignored,
     *               unless extra Comparators are added by another method.
     */
    public ComparatorChain(List list, BitSet bits) {
        comparatorChain = list;
        orderingBits = bits;
    }

    /**
     * Add a Comparator to the end of the chain using the
     * forward sort order
     * 
     * @param comparator Comparator with the forward sort order
     */
    public void addComparator(Comparator comparator) {
        addComparator(comparator,false);
    }

    /**
     * Add a Comparator to the end of the chain using the
     * given sort order
     * 
     * @param comparator Comparator to add to the end of the chain
     * @param reverse    false = forward sort order; true = reverse sort order
     */
    public void addComparator(Comparator comparator, boolean reverse) {
        checkLocked();
        
        comparatorChain.add(comparator);
        if (reverse == true) {
            orderingBits.set(comparatorChain.size() - 1);
        }
    }

    /**
     * Replace the Comparator at the given index, maintaining
     * the existing sort order.
     * 
     * @param index      index of the Comparator to replace
     * @param comparator Comparator to place at the given index
     * @exception IndexOutOfBoundsException
     *                   if index < 0 or index > size()
     */
    public void setComparator(int index, Comparator comparator) 
    throws IndexOutOfBoundsException {
        setComparator(index,comparator,false);
    }

    /**
     * Replace the Comparator at the given index in the
     * ComparatorChain, using the given sort order
     * 
     * @param index      index of the Comparator to replace
     * @param comparator Comparator to set
     * @param reverse    false = forward sort order; true = reverse sort order
     */
    public void setComparator(int index, Comparator comparator, boolean reverse) {
        checkLocked();

        comparatorChain.set(index,comparator);
        if (reverse == true) {
            orderingBits.set(index);
        } else {
            orderingBits.clear(index);
        }
    }


    /**
     * Change the sort order at the given index in the
     * ComparatorChain to a forward sort.
     * 
     * @param index  Index of the ComparatorChain
     */
    public void setForwardSort(int index) {
        checkLocked();
        orderingBits.clear(index);
    }

    /**
     * Change the sort order at the given index in the
     * ComparatorChain to a reverse sort.
     * 
     * @param index  Index of the ComparatorChain
     */
    public void setReverseSort(int index) {
        checkLocked();
        orderingBits.set(index);
    }

    /**
     * Number of Comparators in the current ComparatorChain.
     * 
     * @return Comparator count
     */
    public int size() {
        return comparatorChain.size();
    }

    /**
     * Determine if modifications can still be made to the
     * ComparatorChain.  ComparatorChains cannot be modified
     * once they have performed a comparison.
     * 
     * @return true = ComparatorChain cannot be modified; false = 
     *         ComparatorChain can still be modified.
     */
    public boolean isLocked() {
        return isLocked;
    }

    // throw an exception if the ComparatorChain is locked
    private void checkLocked() {
        if (isLocked == true) {
            throw new UnsupportedOperationException("Comparator ordering cannot be changed after the first comparison is performed");
        }
    }

    private void checkChainIntegrity() {
        if (comparatorChain.size() == 0) {
            throw new UnsupportedOperationException("ComparatorChains must contain at least one Comparator");
        }
    }

    /**
     * Perform comaparisons on the Objects as per
     * Comparator.compare(o1,o2).
     * 
     * @param o1     object 1
     * @param o2     object 2
     * @return -1, 0, or 1
     * @exception UnsupportedOperationException
     *                   if the ComparatorChain does not contain at least one
     *                   Comparator
     */
    public int compare(Object o1, Object o2) throws UnsupportedOperationException {
        if (isLocked == false) {
            checkChainIntegrity();
            isLocked = true;
        }

        // iterate over all comparators in the chain
        Iterator comparators = comparatorChain.iterator();
        for (int comparatorIndex = 0; comparators.hasNext(); ++comparatorIndex) {

            Comparator comparator = (Comparator) comparators.next();
            int retval = comparator.compare(o1,o2);
            if (retval != 0) {
                // invert the order if it is a reverse sort
                if (orderingBits.get(comparatorIndex) == true) {
                    retval *= -1;
                }

                return retval;
            }

        }

        // if comparators are exhausted, return 0
        return 0;
    }

}
