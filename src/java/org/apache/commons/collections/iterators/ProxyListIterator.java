/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/ProxyListIterator.java,v 1.6 2003/11/02 16:29:12 scolebourne Exp $
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

import java.util.ListIterator;

/**
 * A proxy {@link ListIterator ListIterator} which delegates its
 * methods to a proxy instance.
 *
 * @deprecated Use AbstractListIteratorDecorator
 * @since Commons Collections 2.0
 * @version $Revision: 1.6 $ $Date: 2003/11/02 16:29:12 $
 * 
 * @author Rodney Waldhoff
 */
public class ProxyListIterator implements ListIterator {

    /** Holds value of property "iterator". */
    private ListIterator iterator;

    // Constructors
    //-------------------------------------------------------------------------

    /**
     * Constructs a new <code>ProxyListIterator</code> that will not 
     * function until {@link #setListIterator(ListIterator) setListIterator}
     * is invoked.
     */
    public ProxyListIterator() {
        super();
    }

    /**
     * Constructs a new <code>ProxyListIterator</code> that will use the
     * given list iterator.
     *
     * @param iterator  the list iterator to use
     */
    public ProxyListIterator(ListIterator iterator) {
        super();
        this.iterator = iterator;
    }

    // ListIterator interface
    //-------------------------------------------------------------------------

    /**
     *  Invokes the underlying {@link ListIterator#add(Object)} method.
     *
     *  @throws NullPointerException  if the underlying iterator is null
     */
    public void add(Object o) {
        getListIterator().add(o);
    }

    /**
     *  Invokes the underlying {@link ListIterator#hasNext()} method.
     *
     *  @throws NullPointerException  if the underlying iterator is null
     */
    public boolean hasNext() {
        return getListIterator().hasNext();
    }

    /**
     *  Invokes the underlying {@link ListIterator#hasPrevious()} method.
     *
     *  @throws NullPointerException  if the underlying iterator is null
     */
    public boolean hasPrevious() {
        return getListIterator().hasPrevious();
    }

    /**
     *  Invokes the underlying {@link ListIterator#next()} method.
     *
     *  @throws NullPointerException  if the underlying iterator is null
     */
    public Object next() {
        return getListIterator().next();
    }

    /**
     *  Invokes the underlying {@link ListIterator#nextIndex()} method.
     *
     *  @throws NullPointerException  if the underlying iterator is null
     */
    public int nextIndex() {
        return getListIterator().nextIndex();
    }

    /**
     *  Invokes the underlying {@link ListIterator#previous()} method.
     *
     *  @throws NullPointerException  if the underlying iterator is null
     */
    public Object previous() {
        return getListIterator().previous();
    }

    /**
     *  Invokes the underlying {@link ListIterator#previousIndex()} method.
     *
     *  @throws NullPointerException  if the underlying iterator is null
     */
    public int previousIndex() {
        return getListIterator().previousIndex();
    }

    /**
     *  Invokes the underlying {@link ListIterator#remove()} method.
     *
     *  @throws NullPointerException  if the underlying iterator is null
     */
    public void remove() {
        getListIterator().remove();
    }

    /**
     *  Invokes the underlying {@link ListIterator#set(Object)} method.
     *
     *  @throws NullPointerException  if the underlying iterator is null
     */
    public void set(Object o) {
        getListIterator().set(o);
    }

    // Properties
    //-------------------------------------------------------------------------

    /** 
     * Getter for property iterator.
     * @return Value of property iterator.
     */
    public ListIterator getListIterator() {
        return iterator;
    }

    /**
     * Setter for property iterator.
     * @param iterator New value of property iterator.
     */
    public void setListIterator(ListIterator iterator) {
        this.iterator = iterator;
    }

}

