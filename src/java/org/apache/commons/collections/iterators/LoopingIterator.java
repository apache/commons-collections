/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/LoopingIterator.java,v 1.5 2003/08/31 17:25:49 scolebourne Exp $
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
package org.apache.commons.collections.iterators;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An Iterator that restarts when it reaches the end.
 * <p>
 * The iterator will loop continuously around the provided elements, unless 
 * there are no elements in the collection to begin with, or all the elements
 * have been {@link #remove removed}.
 * <p>
 * Concurrent modifications are not directly supported, and for most collection
 * implementations will throw a ConcurrentModificationException. 
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2003/08/31 17:25:49 $
 *
 * @author <a href="mailto:joncrlsn@users.sf.net">Jonathan Carlson</a>
 * @author Stephen Colebourne
 */
public class LoopingIterator implements ResetableIterator {
    
    /** The collection to base the iterator on */
    private Collection collection;
    /** The current iterator */
    private Iterator iterator;

    /**
     * Constructor that wraps a collection.
     * <p>
     * There is no way to reset an Iterator instance without recreating it from
     * the original source, so the Collection must be passed in.
     * 
     * @param coll  the collection to wrap
     * @throws NullPointerException if the collection is null
     */
    public LoopingIterator(Collection coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        collection = coll;
        reset();
    }

    /** 
     * Has the iterator any more elements.
     * <p>
     * Returns false only if the collection originally had zero elements, or
     * all the elements have been {@link #remove removed}.
     * 
     * @return <code>true</code> if there are more elements
     */
    public boolean hasNext() {
        return (collection.size() > 0);
    }

    /**
     * Returns the next object in the collection.
     * <p>
     * If at the end of the collection, return the first element.
     * 
     * @throws NoSuchElementException if there are no elements
     *         at all.  Use {@link #hasNext} to avoid this error.
     */
    public Object next() {
        if (collection.size() == 0) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (iterator.hasNext() == false) {
            reset();
        }
        return iterator.next();
    }

    /**
     * Removes the previously retrieved item from the underlying collection.
     * <p>
     * This feature is only supported if the underlying collection's 
     * {@link Collection#iterator iterator} method returns an implementation 
     * that supports it.
     * <p>
     * This method can only be called after at least one {@link #next} method call.
     * After a removal, the remove method may not be called again until another
     * next has been performed. If the {@link #reset} is called, then remove may
     * not be called until {@link #next} is called again.
     */
    public void remove() {
        iterator.remove();
    }

    /**
     * Resets the iterator back to the start of the collection.
     */
    public void reset() {
        iterator = collection.iterator();
    }

    /**
     * Gets the size of the collection underlying the iterator.
     * 
     * @return the current collection size
     */
    public int size() {
        return collection.size();
    }

}
