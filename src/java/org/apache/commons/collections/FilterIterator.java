/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/FilterIterator.java,v 1.6 2002/08/15 20:04:31 pjack Exp $
 * $Revision: 1.6 $
 * $Date: 2002/08/15 20:04:31 $
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

import java.util.Iterator;
import java.util.NoSuchElementException;

/** A Proxy {@link Iterator Iterator} which takes a {@link Predicate Predicate} instance to filter
  * out objects from an underlying {@link Iterator Iterator} instance.
  * Only objects for which the
  * specified <code>Predicate</code> evaluates to <code>true</code> are
  * returned.
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author Jan Sorensen
  */

public class FilterIterator extends ProxyIterator {
    
    /** Holds value of property predicate. */
    private Predicate predicate;

    private Object nextObject;
    private boolean nextObjectSet = false;
    
    
    //-------------------------------------------------------------------------

    /**
     *  Constructs a new <Code>FilterIterator</Code> that will not function
     *  until {@link #setIterator(Iterator) setIterator} is invoked.
     */
    public FilterIterator() {
    }
    
    /**
     *  Constructs a new <Code>FilterIterator</Code> that will not function
     *  until {@link #setPredicate(Predicate) setPredicate} is invoked.
     *
     *  @param iterator  the iterator to use
     */
    public FilterIterator( Iterator iterator ) {
        super( iterator );
    }

    /**
     *  Constructs a new <Code>FilterIterator</Code> that will use the
     *  given iterator and predicate.
     *
     *  @param iterator  the iterator to use
     *  @param predicate  the predicate to use
     */
    public FilterIterator( Iterator iterator, Predicate predicate ) {
        super( iterator );
        this.predicate = predicate;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    
    /** 
     *  Returns true if the underlying iterator contains an object that 
     *  matches the predicate.
     *
     *  @return true if there is another object that matches the predicate 
     */
    public boolean hasNext() {
        if ( nextObjectSet ) {
            return true;
        } else {
            return setNextObject();
        }
    }

    /** 
     *  Returns the next object that matches the predicate.
     * 
     *  @return the next object which matches the given predicate
     *  @throws NoSuchElementException if there are no more elements that
     *   match the predicate 
     */
    public Object next() {
        if ( !nextObjectSet ) {
            if (!setNextObject()) {
                throw new NoSuchElementException();
            }
        }
        nextObjectSet = false;
        return nextObject;
    }

    /**
     * Always throws UnsupportedOperationException as this class 
     * does look-ahead with its internal iterator.
     *
     * @throws UnsupportedOperationException  always
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
        

    // Properties
    //-------------------------------------------------------------------------
    /** Getter for property predicate.
     * @return Value of property predicate.
     */
    public Predicate getPredicate() {
        return predicate;
    }
    /** Setter for property predicate.
     * @param predicate New value of property predicate.
     */
    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    /**
     * Set nextObject to the next object. If there are no more 
     * objects then return false. Otherwise, return true.
     */
    private boolean setNextObject() {
        Iterator iterator = getIterator();
        Predicate predicate = getPredicate();
        while ( iterator.hasNext() ) {
            Object object = iterator.next();
            if ( predicate.evaluate( object ) ) {
                nextObject = object;
                nextObjectSet = true;
                return true;
            }
        }
        return false;
    }
}
