/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/CollatingIterator.java,v 1.6 2003/02/19 20:14:27 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Provides an ordered iteration over the elements contained in
 * a collection of ordered {@link Iterator}s.  In other words,
 * given two ordered {@link Iterator}s <code>A</code> and <code>B</code>,
 * my {@link #next} method will return the lesser of 
 * <code>A.next()</code> and <code>B.next()</code>.
 *
 * @since Commons Collections 2.1
 * @version $Revision: 1.6 $ $Date: 2003/02/19 20:14:27 $
 * 
 * @author Rodney Waldhoff
 * @author Stephen Colebourne
 */
public class CollatingIterator implements Iterator {

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

    // Constructors
    // -------------------------------------------------------------------
    
    /**
     * Constructs a new <Code>CollatingIterator</Code>.  Natural sort order
     * will be used, and child iterators will have to be manually added 
     * using the {@link #addIterator(Iterator)} method.
     */
    public CollatingIterator() {
        this(null,2);
    }
    
    /**
     * Constructs a new <Code>CollatingIterator</Code> that will used the
     * specified comparator for ordering.  Child iterators will have to be 
     * manually added using the {@link #addIterator(Iterator)} method.
     *
     * @param comp  the comparator to use for ordering, or <Code>null</Code>
     *    to use natural sort order
     */
    public CollatingIterator(Comparator comp) {
        this(comp,2);
    }
    
    /**
     * Constructs a new <Code>CollatingIterator</Code> that will used the
     * specified comparator for ordering and have the specified initial
     * capacity.  Child iterators will have to be 
     * manually added using the {@link #addIterator(Iterator)} method.
     *
     * @param comp  the comparator to use for ordering, or <Code>null</Code>
     *    to use natural sort order
     * @param initIterCapacity  the initial capacity for the internal list
     *    of child iterators
     */
    public CollatingIterator(Comparator comp, int initIterCapacity) {
        iterators = new ArrayList(initIterCapacity);
        setComparator(comp);
    }

    /**
     * Constructs a new <Code>CollatingIterator</Code> that will use the
     * specified comparator to provide ordered iteration over the two
     * given iterators.
     *
     * @param comp  the comparator to use to sort, or null to use natural
     *    sort order
     * @param a  the first child ordered iterator
     * @param b  the second child ordered iterator
     * @throws NullPointerException if either iterator is null
     */
    public CollatingIterator(Comparator comp, Iterator a, Iterator b) {
        this(comp,2);
        addIterator(a);
        addIterator(b);
    }

    /**
     * Constructs a new <Code>CollatingIterator</Code> that will use the
     * specified comparator to provide ordered iteration over the array
     * of iterators.
     *
     * @param comp  the comparator to use to sort, or null to use natural
     *    sort order
     * @param iterators  the array of iterators
     * @throws NullPointerException if iterators array is or contains null
     */
    public CollatingIterator(Comparator comp, Iterator[] iterators) {
        this(comp, iterators.length);
        for (int i = 0; i < iterators.length; i++) {
            addIterator(iterators[i]);
        }
    }

    /**
     * Constructs a new <Code>CollatingIterator</Code> that will use the
     * specified comparator to provide ordered iteration over the collection
     * of iterators.
     *
     * @param comp  the comparator to use to sort, or null to use natural
     *    sort order
     * @param iterators  the collection of iterators
     * @throws NullPointerException if the iterators collection is or contains null
     * @throws ClassCastException if the iterators collection contains an
     *         element that's not an {@link Iterator}
     */
    public CollatingIterator(Comparator comp, Collection iterators) {
        this(comp, iterators.size());
        for (Iterator it = iterators.iterator(); it.hasNext();) {
            Iterator item = (Iterator) it.next();
            addIterator(item);
        }
    }

    // Public Methods
    // -------------------------------------------------------------------

    /**
     * Add the given {@link Iterator} to my collection to collate.
     * @throws IllegalStateException if I've already started iterating
     * @throws NullPointerException if the iterator is null
     */
    public void addIterator(Iterator iterator) {
        checkNotStarted();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        iterators.add(iterator);
    }

    /**
     * Set the Iterator at the given index     
     * 
     * @param index      index of the Iterator to replace
     * @param iterator   Iterator to place at the given index
     * @throws IndexOutOfBoundsException if index &lt; 0 or index &gt; size()
     * @throws IllegalStateException if I've already started iterating
     * @throws NullPointerException if the iterator is null
     */
    public void setIterator(int index, Iterator iterator) throws IndexOutOfBoundsException {
        checkNotStarted();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        iterators.set(index, iterator);
    }

    /**
     * Get the list of Iterators (unmodifiable)
     * 
     * @return the unmodifiable list of iterators added
     */
    public List getIterators() {
        return Collections.unmodifiableList(iterators);
    }

    /**
     * Set the {@link Comparator} by which I collate.
     * @throws IllegalStateException if I've already started iterating
     */
    public void setComparator(Comparator comp) {
        checkNotStarted();
        comparator = comp;
    }

    /**
     * Get the {@link Comparator} by which I collate.
     */
    public Comparator getComparator() {
        return comparator;
    }

    // Iterator Methods
    // -------------------------------------------------------------------

    /**
     *  Returns <Code>true</Code> if any child iterator has remaining elements.
     *
     *  @return true if this iterator has remaining elements
     */
    public boolean hasNext() {
        start();
        return anyValueSet(valueSet) || anyHasNext(iterators);
    }

    /**
     *  Returns the next ordered element from a child iterator.
     *
     *  @return the next ordered element
     *  @throws NoSuchElementException  if no child iterator has any more
     *    elements
     */
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

    /**
     *  Removes the last returned element from the child iterator that 
     *  produced it.
     *
     *  @throws IllegalStateException  if there is no last returned element,
     *    or if the last returned element has already been removed
     */
    public void remove() {
        if(-1 == lastReturned) {
            throw new IllegalStateException("No value can be removed at present");
        } else {
            Iterator iter = (Iterator)(iterators.get(lastReturned));
            iter.remove();
        }
    }

    // Private Methods
    // -------------------------------------------------------------------

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
        if (null != values) {
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

}
