/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2004 The Apache Software Foundation.  All rights
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
 */
package org.apache.commons.collections.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.Predicate;

/** 
 * A proxy {@link ListIterator ListIterator} which 
 * takes a {@link Predicate Predicate} instance to filter
 * out objects from an underlying <code>ListIterator</code> 
 * instance. Only objects for which the specified 
 * <code>Predicate</code> evaluates to <code>true</code> are
 * returned by the iterator.
 * 
 * @since Commons Collections 2.0
 * @version $Revision: 1.6 $ $Date: 2004/01/14 21:43:14 $
 * 
 * @author Rodney Waldhoff
 */
public class FilterListIterator implements ListIterator {

    /** The iterator being used */
    private ListIterator iterator;
    
    /** The predicate being used */
    private Predicate predicate;

    /** 
     * The value of the next (matching) object, when 
     * {@link #nextObjectSet} is true. 
     */
    private Object nextObject;

    /** 
     * Whether or not the {@link #nextObject} has been set
     * (possibly to <code>null</code>). 
     */
    private boolean nextObjectSet = false;   

    /** 
     * The value of the previous (matching) object, when 
     * {@link #previousObjectSet} is true. 
     */
    private Object previousObject;

    /** 
     * Whether or not the {@link #previousObject} has been set
     * (possibly to <code>null</code>). 
     */
    private boolean previousObjectSet = false;   

    /** 
     * The index of the element that would be returned by {@link #next}.
     */
    private int nextIndex = 0;
    
    //-----------------------------------------------------------------------
    /**
     *  Constructs a new <code>FilterListIterator</code> that will not 
     *  function until 
     *  {@link ProxyListIterator#setListIterator(ListIterator) setListIterator}
     *  and {@link #setPredicate(Predicate) setPredicate} are invoked.
     */
    public FilterListIterator() {
        super();
    }

    /**
     * Constructs a new <code>FilterListIterator</code> that will not 
     * function until {@link #setPredicate(Predicate) setPredicate} is invoked.
     *
     * @param iterator  the iterator to use
     */
    public FilterListIterator(ListIterator iterator ) {
        super();
        this.iterator = iterator;
    }

    /**
     * Constructs a new <code>FilterListIterator</code>.
     *
     * @param iterator  the iterator to use
     * @param predicate  the predicate to use
     */
    public FilterListIterator(ListIterator iterator, Predicate predicate) {
        super();
        this.iterator = iterator;
        this.predicate = predicate;
    }

    /**
     * Constructs a new <code>FilterListIterator</code> that will not 
     * function until 
     * {@link ProxyListIterator#setListIterator(ListIterator) setListIterator}
     * is invoked.
     *
     * @param predicate  the predicate to use.
     */
    public FilterListIterator(Predicate predicate) {
        super();
        this.predicate = predicate;
    }

    //-----------------------------------------------------------------------
    /** Not supported. */
    public void add(Object o) {
        throw new UnsupportedOperationException("FilterListIterator.add(Object) is not supported.");
    }

    public boolean hasNext() {
        if(nextObjectSet) {
            return true;
        } else {
            return setNextObject();
        }
    }

    public boolean hasPrevious() {
        if(previousObjectSet) {
            return true;
        } else {
            return setPreviousObject();
        }
    }

    public Object next() {
        if(!nextObjectSet) {
            if(!setNextObject()) {
                throw new NoSuchElementException();
            }
        }
        nextIndex++;
        Object temp = nextObject;
        clearNextObject();
        return temp;
    }

    public int nextIndex() {
        return nextIndex;
    }

    public Object previous() {
        if(!previousObjectSet) {
            if(!setPreviousObject()) {
                throw new NoSuchElementException();
            }
        }
        nextIndex--;
        Object temp = previousObject;
        clearPreviousObject();
        return temp;
    }

    public int previousIndex() {
        return (nextIndex-1);
    }

    /** Not supported. */
    public void remove() {
        throw new UnsupportedOperationException("FilterListIterator.remove() is not supported.");
    }

    /** Not supported. */
    public void set(Object o) {
        throw new UnsupportedOperationException("FilterListIterator.set(Object) is not supported.");
    }

    //-----------------------------------------------------------------------
    /** 
     * Gets the iterator this iterator is using.
     * 
     * @return the iterator.
     */
    public ListIterator getListIterator() {
        return iterator;
    }

    /** 
     * Sets the iterator for this iterator to use.
     * If iteration has started, this effectively resets the iterator.
     * 
     * @param iterator  the iterator to use
     */
    public void setListIterator(ListIterator iterator) {
        this.iterator = iterator;
    }

    //-----------------------------------------------------------------------
    /** 
     * Gets the predicate this iterator is using.
     * 
     * @return the predicate.
     */
    public Predicate getPredicate() {
        return predicate;
    }

    /** 
     * Sets the predicate this the iterator to use.
     * 
     * @param predicate  the transformer to use
     */
    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    //-----------------------------------------------------------------------
    private void clearNextObject() {
        nextObject = null;
        nextObjectSet = false;
    }

    private boolean setNextObject() {
        // if previousObjectSet,
        // then we've walked back one step in the 
        // underlying list (due to a hasPrevious() call)
        // so skip ahead one matching object
        if(previousObjectSet) {
            clearPreviousObject();
            if(!setNextObject()) {
                return false;
            } else {
                clearNextObject();
            }
        }

        while(iterator.hasNext()) {
            Object object = iterator.next();
            if(predicate.evaluate(object)) {
                nextObject = object;
                nextObjectSet = true;
                return true;
            }
        }
        return false;
    }

    private void clearPreviousObject() {
        previousObject = null;
        previousObjectSet = false;
    }

    private boolean setPreviousObject() {
        // if nextObjectSet,
        // then we've walked back one step in the 
        // underlying list (due to a hasNext() call)
        // so skip ahead one matching object
        if(nextObjectSet) {
            clearNextObject();
            if(!setPreviousObject()) {
                return false;
            } else {
                clearPreviousObject();
            }
        }

        while(iterator.hasPrevious()) {
            Object object = iterator.previous();
            if(predicate.evaluate(object)) {
                previousObject = object;
                previousObjectSet = true;
                return true;
            }
        }
        return false;
    }

}
