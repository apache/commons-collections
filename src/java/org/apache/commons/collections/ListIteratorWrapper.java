/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/ListIteratorWrapper.java,v 1.3 2002/08/15 20:04:31 pjack Exp $
 * $Revision: 1.3 $
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
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * As the wrapped Iterator is traversed, ListIteratorWrapper
 * builds a LinkedList of its values, permitting all required
 * operations of ListIterator.
 * 
 * @author Morgan Delagrange
 * @version $Revision: 1.3 $ $Date: 2002/08/15 20:04:31 $
 * @since 2.1
 */
public class ListIteratorWrapper implements ListIterator {

    // Constructor
    //-------------------------------------------------------------------------

    /**
     *  Constructs a new <Code>ListIteratorWrapper</Code> that will wrap
     *  the given iterator.
     *
     *  @param iterator  the iterator to wrap
     */
    public ListIteratorWrapper(Iterator iterator) {
        this.iterator = iterator;
    }

    // ListIterator interface
    //-------------------------------------------------------------------------

    /**
     *  Throws {@link UnsupportedOperationException}.
     *
     *  @param o  ignored
     *  @throws UnsupportedOperationException always
     */
    public void add(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }


    /**
     *  Returns true if there are more elements in the iterator.
     *
     *  @return true if there are more elements
     */
    public boolean hasNext() {
        if (currentIndex == wrappedIteratorIndex) {
            return iterator.hasNext();
        }

        return true;
    }

    /**
     *  Returns true if there are previous elements in the iterator.
     *
     *  @return true if there are previous elements
     */
    public boolean hasPrevious() {
        if (currentIndex == 0) {
            return false;
        }

        return true;
    }

    /**
     *  Returns the next element from the iterator.
     *
     *  @return the next element from the iterator
     *  @throws NoSuchElementException if there are no more elements
     */
    public Object next() throws NoSuchElementException {
        if (currentIndex < wrappedIteratorIndex) {
            ++currentIndex;
            return list.get(currentIndex - 1);
        }

        Object retval = iterator.next();
        list.add(retval);
        ++currentIndex;
        ++wrappedIteratorIndex;
        return retval;
    }

    /**
     *  Returns in the index of the next element.
     *
     *  @return the index of the next element
     */
    public int nextIndex() {
        return currentIndex;
    }

    /**
     *  Returns the the previous element.
     *
     *  @return the previous element
     *  @throws NoSuchElementException  if there are no previous elements
     */
    public Object previous() throws NoSuchElementException {
        if (currentIndex == 0) {
            throw new NoSuchElementException();
        }

        --currentIndex;
        return list.get(currentIndex);    
    }

    /**
     *  Returns the index of the previous element.
     *
     *  @return  the index of the previous element
     */
    public int previousIndex() {
        return currentIndex - 1;
    }

    /**
     *  Throws {@link UnsupportedOperationException}.
     *
     *  @throws UnsupportedOperationException always
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    /**
     *  Throws {@link UnsupportedOperationException}.
     *
     *  @param o  ignored
     *  @throws UnsupportedOperationException always
     */
    public void set(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    // Attributes
    //-------------------------------------------------------------------------

    /** Holds value of property "iterator". */
    private Iterator iterator = null;
    private LinkedList list = new LinkedList();
    
    // position of this iterator
    private int currentIndex = 0;
    // position of the wrapped iterator
    // this Iterator should only be used to populate the list
    private int wrappedIteratorIndex = 0;

    private static final String UNSUPPORTED_OPERATION_MESSAGE =
        "ListIteratorWrapper does not support optional operations of ListIterator.";

}

