/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/FilterListIterator.java,v 1.1 2002/02/25 23:53:20 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2002/02/25 23:53:20 $
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

import java.util.ListIterator;
import java.util.NoSuchElementException;

/** 
 * A proxy {@link ListIterator ListIterator} which takes a {@link Predicate Predicate} instance to filter
  * out objects from an underlying {@link Iterator Iterator} instance.
  * Only objects for which the
  * specified <code>Predicate</code> evaluates to <code>true</code> are
  * returned.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Jan Sorensen
  */

public class FilterListIterator extends ProxyListIterator {

    //-------------------------------------------------------------------------
    public FilterListIterator() {
    }

    public FilterListIterator(ListIterator iterator ) {
        super(iterator);
    }

    public FilterListIterator(ListIterator iterator, Predicate predicate) {
        super(iterator);
        this.predicate = predicate;
    }

    // ListIterator interface
    //-------------------------------------------------------------------------

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
        nextObjectSet = false;
        nextIndex++;
        return nextObject;
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
        previousObjectSet = false;
        nextIndex--;
        return previousObject;
    }

    public int previousIndex() {
        return (nextIndex-1);
    }

    public void remove() {
        throw new UnsupportedOperationException("FilterListIterator.remove() is not supported.");
    }

    public void set(Object o) {
        throw new UnsupportedOperationException("FilterListIterator.set(Object) is not supported.");
    }

    // Properties
    //-------------------------------------------------------------------------

    /** 
     * Getter for the predicate property.
     * @return value of the predicate property.
     */
    public Predicate getPredicate() {
        return predicate;
    }
    /** 
     * Setter for the predicate property.
     * @param predicate new value for the predicate property.
     */
    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    /**
     * Set {@link #nextObject} to the next object. If there 
     * are no more objects then return <code>false</code>. 
     * Otherwise, return <code>true</code>.
     */
    private boolean setNextObject() {
        ListIterator iterator = getListIterator();
        Predicate predicate = getPredicate();
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

    /**
     * Set {@link #nextObject} to the next object. If there 
     * are no more objects then return <code>false</code>. 
     * Otherwise, return <code>true</code>.
     */
    private boolean setPreviousObject() {
        ListIterator iterator = getListIterator();
        Predicate predicate = getPredicate();
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

    // Attributes
    //-------------------------------------------------------------------------

    /** Holds value of property "predicate". */
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
}
