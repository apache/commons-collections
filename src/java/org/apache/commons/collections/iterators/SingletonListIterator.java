/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/SingletonListIterator.java,v 1.2 2002/08/19 21:56:18 pjack Exp $
 * $Revision: 1.2 $
 * $Date: 2002/08/19 21:56:18 $
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
package org.apache.commons.collections.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;
/** 
 * <p><code>SingletonIterator</code> is an {@link ListIterator} over a single 
 * object instance.</p>
 *
 * @since 2.1
 * @author <a href="mailto:scolebourne@joda.org">Stephen Colebourne</a>
 * @version $Id: SingletonListIterator.java,v 1.2 2002/08/19 21:56:18 pjack Exp $
 */
public class SingletonListIterator implements ListIterator {

    private boolean first = true;
    private boolean nextCalled = false;
    private Object object;

    /**
     * Constructs a new <Code>SingletonListIterator</Code>.
     *
     * @param object  the single object to return from the iterator
     */
    public SingletonListIterator(Object object) {
        super();
        this.object = object;
    }

    /**
     * Is another object available from the iterator.
     * <p>
     * This returns true if the single object hasn't been returned yet.
     * 
     * @return true if the single object hasn't been returned yet
     */
    public boolean hasNext() {
        return first;
    }

    /**
     * Is a previous object available from the iterator.
     * <p>
     * This returns true if the single object has been returned.
     * 
     * @return true if the single object has been returned
     */
    public boolean hasPrevious() {
        return !first;
    }

    /**
     * Returns the index of the element that would be returned by a subsequent
     * call to <tt>next</tt>.
     *
     * @return 0 or 1 depending on current state. 
     */
    public int nextIndex() {
        return (first ? 0 : 1);
    }

    /**
     * Returns the index of the element that would be returned by a subsequent
     * call to <tt>previous</tt>. A return value of -1 indicates that the iterator is currently at
     * the start.
     *
     * @return 0 or -1 depending on current state. 
     */
    public int previousIndex() {
        return (first ? -1 : 0);
    }

    /**
     * Get the next object from the iterator.
     * <p>
     * This returns the single object if it hasn't been returned yet.
     *
     * @return the single object
     * @throws NoSuchElementException if the single object has already 
     *    been returned
     */
    public Object next() {
        if (!first) {
            throw new NoSuchElementException();
        }
        first = false;
        nextCalled = true;
        return object;
    }

    /**
     * Get the previous object from the iterator.
     * <p>
     * This returns the single object if it has been returned.
     *
     * @return the single object
     * @throws NoSuchElementException if the single object has not already 
     *    been returned
     */
    public Object previous() {
        if (first) {
            throw new NoSuchElementException();
        }
        first = true;
        return object;
    }

    /**
     * Remove always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported by this iterator");
    }
    
    /**
     * Add always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    public void add(Object obj) {
        throw new UnsupportedOperationException("add() is not supported by this iterator");
    }
    
    /**
     * Set sets the value of the singleton.
     *
     * @param obj  the object to set
     * @throws IllegalStateException if <tt>next</tt> has not been called
     */
    public void set(Object obj) {
        if (nextCalled == false) {
            throw new IllegalStateException();
        }
        this.object = obj;
    }
    
}
