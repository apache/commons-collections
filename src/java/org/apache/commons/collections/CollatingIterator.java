/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/CollatingIterator.java,v 1.2 2002/07/10 14:06:39 rwaldhoff Exp $
 * $Revision: 1.2 $
 * $Date: 2002/07/10 14:06:39 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Provides an ordered iteration over the elements contained in
 * a collection of ordered {@link Iterator}s.  In other words,
 * given two ordered {@link Iterator}s <code>A</code> and <code>B</code>,
 * my {@link #next} method will return the lesser of 
 * <code>A.next()</code> and <code>B.next()</code>.
 *
 * @version $Revision: 1.2 $ $Date: 2002/07/10 14:06:39 $
 * @author Rodney Waldhoff
 */
public class CollatingIterator implements Iterator {

    //------------------------------------------------------------ Constructors
    
    public CollatingIterator() {
        this(null,2);
    }
    
    public CollatingIterator(Comparator comp) {
        this(comp,2);
    }
    
    public CollatingIterator(Comparator comp, int initIterCapacity) {
        iterators = new ArrayList(initIterCapacity);
        setComparator(comp);
    }
    
    public CollatingIterator(Comparator comp, Iterator a, Iterator b) {
        this(comp,2);
        addIterator(a);
        addIterator(b);
    }

    //--------------------------------------------------------- Public Methods

    /**
     * Add the given {@link Iterator} to my collection to collate.
     * @throws IllegalStateException if I've already started iterating
     */
    public void addIterator(Iterator iter) throws IllegalStateException {
        checkNotStarted();
        iterators.add(iter);
    }

    /**
     * Set the {@link Comparator} by which I collate.
     * @throws IllegalStateException if I've already started iterating
     */
    public void setComparator(Comparator comp) throws IllegalStateException {
        checkNotStarted();
        comparator = comp;
    }

    /**
     * Get the {@link Comparator} by which I collate.
     */
    public Comparator getComparator() {
        return comparator;
    }

    //------------------------------------------------------- Iterator Methods

    public boolean hasNext() {
        start();
        return anyValueSet(valueSet) || anyHasNext(iterators);
    }

    public Object next() throws NoSuchElementException {
        if(!hasNext()) {
            throw new NoSuchElementException();
        } else {
            int leastIndex = least();
            if(leastIndex == -1) {
                throw new NoSuchElementException();
            } else {
                Object val = values.get(leastIndex);
                clear(leastIndex);
                lastReturned = leastIndex;
                return val;
            }
        }        
    }

    public void remove() {
        if(-1 == lastReturned) {
            throw new NoSuchElementException("No value has been returned yet.");
        } else {
            Iterator iter = (Iterator)(iterators.get(lastReturned));
            iter.remove();
        }
    }

    //--------------------------------------------------------- Private Methods

    /** Initialize my collating state if it hasn't been already. */
    private void start() {
        if(null == values) {
            values = new ArrayList(iterators.size());
            valueSet = new BitSet(iterators.size());
            for(int i=0;i<iterators.size();i++) {
                values.add(null);
                valueSet.clear(i);
            }
        }
    }

    /** 
     * Set the {@link #values} and {@link #valueSet} attributes 
     * at position <i>i</i> to the next value of the 
     * {@link #iterators iterator} at position <i>i</i>, or 
     * clear them if the <i>i</i><sup>th</sup> iterator
     * has no next value.
     *
     * @return <tt>false</tt> iff there was no value to set
     */
    private boolean set(int i) {
        Iterator iter = (Iterator)(iterators.get(i));
        if(iter.hasNext()) {
            values.set(i,iter.next());
            valueSet.set(i);
            return true;
        } else {
            values.set(i,null);
            valueSet.clear(i);
            return false;
        }
    }

    /** 
     * Clear the {@link #values} and {@link #valueSet} attributes 
     * at position <i>i</i>.
     */
    private void clear(int i) {
        values.set(i,null);
        valueSet.clear(i);
    }

    /** 
     * Throw {@link IllegalStateException} iff I've been {@link #start started}.
     * @throws IllegalStateException iff I've been {@link #start started}
     */
    private void checkNotStarted() throws IllegalStateException {
        if(null != values) {
            throw new IllegalStateException("Can't do that after next or hasNext has been called.");
        }
    }

    /** 
     * Returns the index of the least element in {@link #values},
     * {@link #set(int) setting} any uninitialized values.
     */
    private int least() throws IllegalStateException {
        int leastIndex = -1;
        Object leastObject = null;                
        for(int i=0;i<values.size();i++) {
            if(!valueSet.get(i)) {
                set(i);
            }
            if(valueSet.get(i)) {
                if(leastIndex == -1) {
                    leastIndex = i;
                    leastObject = values.get(i);
                } else {
                    Object curObject = values.get(i);
                    if(comparator.compare(curObject,leastObject) < 0) {
                        leastObject = curObject;
                        leastIndex = i;
                    }
                }
            }
        }
        return leastIndex;
    }

    /**
     * Returns <code>true</code> iff any bit in the given set is 
     * <code>true</code>.
     */
    private boolean anyValueSet(BitSet set) {
        for(int i=0;i<set.size();i++) {
            if(set.get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> iff any {@link Iterator} 
     * in the given list has a next value.
     */
    private boolean anyHasNext(ArrayList iters) {
        for(int i=0;i<iters.size();i++) {
            Iterator iter = (Iterator)iters.get(i);
            if(iter.hasNext()) {
                return true;
            }
        }
        return false;
    }

    //--------------------------------------------------------- Private Members

    /** My {@link Comparator}. */
    private Comparator comparator = null;

    /** My list of {@link Iterator}s. */
    private ArrayList iterators = null;
   
    /** {@link Iterator#next Next} objects peeked from each iterator. */
    private ArrayList values = null;
    
    /** Whether or not each {@link #values} element has been set. */
    private BitSet valueSet = null;

    /** Index of the {@link #iterators iterator} from whom the last returned value was obtained. */
    private int lastReturned = -1;

}
