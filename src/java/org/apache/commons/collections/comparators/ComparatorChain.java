/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/comparators/ComparatorChain.java,v 1.3 2002/03/01 23:48:59 morgand Exp $
 * $Revision: 1.3 $
 * $Date: 2002/03/01 23:48:59 $
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
 * UnsupportedOperationException.</p>
 * 
 * <p>Instances of ComparatorChain are not synchronized.</p>
 * 
 * @author Morgan Delagrange
 */
public class ComparatorChain implements Comparator,Serializable {

    protected List comparatorChain = null;
    // 0 = ascend; 1 = descend
    protected BitSet orderingBits = null;

    // ComparatorChain is "locked" after the first time
    // compare(Object,Object) is called)
    protected boolean isLocked = false;

    public ComparatorChain(Comparator comparator) {
        this(comparator,false);
    }

    public ComparatorChain(Comparator comparator, boolean reverse) {
        comparatorChain = new ArrayList();
        comparatorChain.add(comparator);
        orderingBits = new BitSet(1);
        if (reverse == true) {
            orderingBits.set(0);
        }
    }

    /**
     * 
     * @param list
     * @see #ComparatorChain(List,BitSet)
     */
    public ComparatorChain(List list) {
        this(list,new BitSet(list.size()));
    }

    /**
     * 
     * @param list   NOTE: This constructor performs a defensive
     *                     copy of the list elements into a new
     *                     List. 
     * @param bits
     */
    public ComparatorChain(List list, BitSet bits) {
        comparatorChain = new ArrayList();
        comparatorChain.addAll(list);
        orderingBits = bits;
    }

    public void addComparator(Comparator comparator) {
        addComparator(comparator,false);
    }

    public void addComparator(Comparator comparator, boolean reverse) {
        checkLocked();
        
        comparatorChain.add(comparator);
        if (reverse == true) {
            orderingBits.set(comparatorChain.size() - 1);
        }
    }

    public void setComparator(int index, Comparator comparator) {
        setComparator(index,comparator,false);
    }

    public void setComparator(int index, Comparator comparator, boolean reverse) {
        checkLocked();

        comparatorChain.set(index,comparator);
        if (reverse == true) {
            orderingBits.set(index);
        } else {
            orderingBits.clear(index);
        }
    }

    public void setForwardSort(int index) {
        checkLocked();
        orderingBits.clear(index);
    }

    public void setReverseSort(int index) {
        checkLocked();
        orderingBits.set(index);
    }

    public void checkLocked() {
        if (isLocked == true) {
            throw new UnsupportedOperationException("Comparator ordering cannot be changed after the first comparison is performed");
        }
    }

    public int compare(Object o1, Object o2) {
        if (isLocked == false) {
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
