/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/IteratorChain.java,v 1.2 2002/08/17 11:28:36 scolebourne Exp $
 * $Revision: 1.2 $
 * $Date: 2002/08/17 11:28:36 $
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
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
/**
 * <p>An IteratorChain is an Iterator that wraps one or
 * more Iterators.  When any method from the
 * Iterator interface is called, the IteratorChain will
 * proxy to a single underlying Iterator.  The 
 * IteratorChain will invoke the Iterators in sequence until 
 * all Iterators are exhausted completely.</p>
 * 
 * <p>Under many circumstances, linking Iterators together
 * in this manner is more efficient (and convenient)
 * than reading out the contents of each Iterator into a
 * List and creating a new Iterator.</p>
 * 
 * <p>Calling a method that adds new Iterator<i>after
 * a method in the Iterator interface
 * has been called</i> will result in an
 * UnsupportedOperationException.  Subclasses should <i>take care</i>
 * to not alter the underlying List of Iterators.</p>
 * 
 * @since 2.1
 * @author Morgan Delagrange
 * @author <a href="mailto:scolebourne@joda.org">Stephen Colebourne</a>
 * @version $Id: IteratorChain.java,v 1.2 2002/08/17 11:28:36 scolebourne Exp $
 */
public class IteratorChain implements Iterator {

    protected final List iteratorChain = new ArrayList();
    protected int currentIteratorIndex = 0;
    protected Iterator currentIterator = null;
    // the "last used" Iterator is the Iterator upon which
    // next() or hasNext() was most recently called
    // used for the remove() operation only
    protected Iterator lastUsedIterator = null;

    // ComparatorChain is "locked" after the first time
    // compare(Object,Object) is called
    protected boolean isLocked = false;

    // Constructors
    // -------------------------------------------------------------------
    
    /**
     * Construct an IteratorChain with no Iterators.
     * You must add at least Iterator before calling
     * any method from the Iterator interface, or an 
     * UnsupportedOperationException is thrown
     */
    public IteratorChain() {
        super();
    }

    /**
     * Construct an IteratorChain with a single Iterator.
     * 
     * @param iterator first Iterator in the IteratorChain
     * @throws NullPointerException if the iterator is null
     */
    public IteratorChain(Iterator iterator) {
        super();
        addIterator(iterator);
    }

    /**
     * Constructs a new <Code>IteratorChain</Code> over the two
     * given iterators.
     *
     * @param a  the first child iterator
     * @param b  the second child iterator
     * @throws NullPointerException if either iterator is null
     */
    public IteratorChain(Iterator a, Iterator b) {
        super();
        addIterator(a);
        addIterator(b);
    }

    /**
     * Constructs a new <Code>IteratorChain</Code> over the array
     * of iterators.
     *
     * @param iterators  the array of iterators
     * @throws NullPointerException if iterators array is or contains null
     */
    public IteratorChain(Iterator[] iterators) {
        super();
        for (int i = 0; i < iterators.length; i++) {
            addIterator(iterators[i]);
        }
    }

    /**
     * Constructs a new <Code>IteratorChain</Code> over the collection
     * of iterators.
     *
     * @param iterators  the collection of iterators
     * @throws NullPointerException if iterators collection is or contains null
     * @throws ClassCastException if iterators collection doesn't contain an iterator
     */
    public IteratorChain(Collection iterators) {
        super();
        for (Iterator it = iterators.iterator(); it.hasNext();) {
            Iterator item = (Iterator) it.next();
            addIterator(item);
        }
    }
    
    // Public Methods
    // -------------------------------------------------------------------

    /**
     * Add an Iterator to the end of the chain 
     * 
     * @param iterator Iterator to add
     * @throws IllegalStateException if I've already started iterating
     * @throws NullPointerException if the iterator is null
     */
    public void addIterator(Iterator iterator) {
        checkLocked();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        iteratorChain.add(iterator);
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
        checkLocked();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        iteratorChain.set(index, iterator);
    }

    /**
     * Get the list of Iterators (unmodifiable)
     * 
     * @return the unmodifiable list of iterators added
     */
    public List getIterators() {
        return Collections.unmodifiableList(iteratorChain);
    }

    /**
     * Number of Iterators in the current IteratorChain.
     * 
     * @return Iterator count
     */
    public int size() {
        return iteratorChain.size();
    }

    /**
     * Determine if modifications can still be made to the
     * IteratorChain.  IteratorChains cannot be modified
     * once they have executed a method from the Iterator
     * interface.
     * 
     * @return true = IteratorChain cannot be modified; false = 
     *         IteratorChain can still be modified.
     */
    public boolean isLocked() {
        return isLocked;
    }

    // throw an exception if the IteratorChain is locked
    private void checkLocked() {
        if (isLocked == true) {
            throw new UnsupportedOperationException("IteratorChain cannot be changed after the first use of a method from the Iterator interface");
        }
    }

    private void checkChainIntegrity() {
        if (iteratorChain.size() == 0) {
            throw new UnsupportedOperationException("IteratorChains must contain at least one Iterator");
        }
    }

    // you MUST call this method whenever you call a method in the Iterator interface, because
    // this method also assigns the initial value of the currentIterator variable
    private void lockChain() {
        if (isLocked == false) {
            checkChainIntegrity();
            isLocked = true;
        }
    }

    // call this before any Iterator method to make sure that the current Iterator
    // is not exhausted
    protected void updateCurrentIterator() {
        if (currentIterator == null) {
            currentIterator = (Iterator) iteratorChain.get(0);
            // set last used iterator here, in case the user calls remove
            // before calling hasNext() or next() (although they shouldn't)
            lastUsedIterator = currentIterator;
            return;
        }

        if (currentIteratorIndex == (iteratorChain.size() - 1)) {
            return;
        }

        while (currentIterator.hasNext() == false) {
            ++currentIteratorIndex;
            currentIterator = (Iterator) iteratorChain.get(currentIteratorIndex);

            if (currentIteratorIndex == (iteratorChain.size() - 1)) {
                return;
            }
        }
    }

    /**
     * Return true if any Iterator in the IteratorChain has a remaining
     * element.
     * 
     * @return true if elements remain
     * @exception UnsupportedOperationException
     *                   if the IteratorChain does not contain at least one
     *                   Iterator
     */
    public boolean hasNext() throws UnsupportedOperationException {
        lockChain();
        updateCurrentIterator();
        lastUsedIterator = currentIterator;

        return currentIterator.hasNext();
    }

    /**
     * Returns the next Object of the current Iterator
     * 
     * @return Object from the current Iterator
     * @exception NoSuchElementException
     *                   if all the Iterators are exhausted
     * @exception UnsupportedOperationException
     *                   if the IteratorChain does not contain at least one
     *                   Iterator
     */
    public Object next() throws NoSuchElementException, UnsupportedOperationException {
        lockChain();
        updateCurrentIterator();
        lastUsedIterator = currentIterator;

        return currentIterator.next();
    }

    /**
     * Removes from the underlying collection the last element 
     * returned by the Iterator.  As with next() and hasNext(),
     * this method calls remove() on the underlying Iterator.
     * Therefore, this method may throw an 
     * UnsupportedOperationException if the underlying
     * Iterator does not support this method. 
     * 
     * @exception UnsupportedOperationException
     *                   if the remove operator is not supported by the underlying
     *                   Iterator or if there are no Iterators in the IteratorChain
     * @exception IllegalStateException
     *                   if the next method has not yet been called, or the
     *                   remove method has already been called after the last
     *                   call to the next method.
     */
    public void remove() throws UnsupportedOperationException, IllegalStateException  {
        lockChain();
        updateCurrentIterator();

        lastUsedIterator.remove();
    }


}
