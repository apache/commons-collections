/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/ProxyIterator.java,v 1.8 2004/01/08 22:26:07 scolebourne Exp $
 * ====================================================================
 *
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
 *
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;

/** 
 * A Proxy {@link Iterator Iterator} which delegates its methods to a proxy instance.
 *
 * @deprecated Use AbstractIteratorDecorator. Will be removed in v4.0
 * @since Commons Collections 1.0
 * @version $Revision: 1.8 $ $Date: 2004/01/08 22:26:07 $
 * 
 * @author James Strachan
 */
public class ProxyIterator implements Iterator {
    
    /** Holds value of property iterator. */
    private Iterator iterator;
    
    // Constructors
    //-------------------------------------------------------------------------

    /**
     * Constructs a new <code>ProxyIterator</code> that will not function
     * until {@link #setIterator(Iterator)} is called.
     */
    public ProxyIterator() {
        super();
    }
    
    /**
     * Constructs a new <code>ProxyIterator</code> that will use the
     * given iterator.
     *
     * @param iterator  the underlying iterator
     */
    public ProxyIterator(Iterator iterator) {
        super();
        this.iterator = iterator;
    }

    // Iterator interface
    //-------------------------------------------------------------------------

    /**
     *  Returns true if the underlying iterator has more elements.
     *
     *  @return true if the underlying iterator has more elements
     */
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    /**
     *  Returns the next element from the underlying iterator.
     *
     *  @return the next element from the underlying iterator
     *  @throws java.util.NoSuchElementException  if the underlying iterator 
     *    raises it because it has no more elements
     */
    public Object next() {
        return getIterator().next();
    }

    /**
     *  Removes the last returned element from the collection that spawned
     *  the underlying iterator.
     */
    public void remove() {
        getIterator().remove();
    }

    // Properties
    //-------------------------------------------------------------------------
    /** Getter for property iterator.
     * @return Value of property iterator.
     */
    public Iterator getIterator() {
        return iterator;
    }
    /** Setter for property iterator.
     * @param iterator New value of property iterator.
     */
    public void setIterator(Iterator iterator) {
        this.iterator = iterator;
    }
}
