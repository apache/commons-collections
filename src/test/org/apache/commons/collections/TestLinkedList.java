/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestLinkedList.java,v 1.3 2003/08/31 17:28:43 scolebourne Exp $
 * $Revision: 1.3 $
 * $Date: 2003/08/31 17:28:43 $
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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Tests base {@link java.util.LinkedList} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeLinkedList} method.
 * <p>
 * If your {@link LinkedList} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link List} fails.
 *
 * @author <a href="mailto:rich@rd.gen.nz">Rich Dougherty</a>
 * @version $Id: TestLinkedList.java,v 1.3 2003/08/31 17:28:43 scolebourne Exp $
 */
public abstract class TestLinkedList extends TestList {

    public TestLinkedList(String testName) {
        super(testName);
    }

    protected List makeEmptyList() {
        return makeEmptyLinkedList();
    }

    protected List makeFullList() {
        return makeFullLinkedList();
    }

    /**
     *  Return a new, empty {@link LinkedList} to be used for testing.
     *
     *  @return an empty list for testing.
     */
    protected abstract LinkedList makeEmptyLinkedList();

    /**
     *  Return a new, full {@link List} to be used for testing.
     *
     *  @return a full list for testing
     */
    protected LinkedList makeFullLinkedList() {
        // only works if list supports optional "addAll(Collection)" 
        LinkedList list = makeEmptyLinkedList();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    /**
     *  Returns the {@link #collection} field cast to a {@link LinkedList}.
     *
     *  @return the collection field as a List
     */
    protected LinkedList getLinkedList() {
        return (LinkedList)collection;
    }

    /**
     *  Returns the {@link #confirmed} field cast to a {@link LinkedList}.
     *
     *  @return the confirmed field as a List
     */
    protected LinkedList getConfirmedLinkedList() {
        return (LinkedList)confirmed;
    }

    /**
     *  Tests {@link LinkedList#addFirst(Object)}.
     */
    public void testLinkedListAddFirst() {
        if (!isAddSupported()) return;
        Object o = "hello";

        resetEmpty();
        getLinkedList().addFirst(o);
        getConfirmedLinkedList().addFirst(o);
        verify();

        resetFull();
        getLinkedList().addFirst(o);
        getConfirmedLinkedList().addFirst(o);
        verify();
    }

    /**
     *  Tests {@link LinkedList#addLast(Object)}.
     */
    public void testLinkedListAddLast() {
        if (!isAddSupported()) return;
        Object o = "hello";

        resetEmpty();
        getLinkedList().addLast(o);
        getConfirmedLinkedList().addLast(o);
        verify();

        resetFull();
        getLinkedList().addLast(o);
        getConfirmedLinkedList().addLast(o);
        verify();
    }

    /**
     *  Tests {@link LinkedList#getFirst(Object)}.
     */
    public void testLinkedListGetFirst() {
        resetEmpty();
        try {
            getLinkedList().getFirst();
            fail("getFirst() should throw a NoSuchElementException for an " +
                    "empty list.");
        } catch (NoSuchElementException e) {
            // This is correct
        }
        verify();

        resetFull();
        Object first = getLinkedList().getFirst();
        Object confirmedFirst = getConfirmedLinkedList().getFirst();
        assertEquals("Result returned by getFirst() was wrong.",
                confirmedFirst, first);
        verify();
    }

    /**
     *  Tests {@link LinkedList#getLast(Object)}.
     */
    public void testLinkedListGetLast() {
        resetEmpty();
        try {
            getLinkedList().getLast();
            fail("getLast() should throw a NoSuchElementException for an " +
                    "empty list.");
        } catch (NoSuchElementException e) {
            // This is correct
        }
        verify();
        
        resetFull();
        Object last = getLinkedList().getLast();
        Object confirmedLast = getConfirmedLinkedList().getLast();
        assertEquals("Result returned by getLast() was wrong.",
                confirmedLast, last);
        verify();
    }

    /**
     *  Tests {@link LinkedList#removeFirst(Object)}.
     */
    public void testLinkedListRemoveFirst() {
        if (!isRemoveSupported()) return;

        resetEmpty();
        try {
            getLinkedList().removeFirst();
            fail("removeFirst() should throw a NoSuchElementException for " +
                    "an empty list.");
        } catch (NoSuchElementException e) {
            // This is correct
        }
        verify();
        
        resetFull();
        Object first = getLinkedList().removeFirst();
        Object confirmedFirst = getConfirmedLinkedList().removeFirst();
        assertEquals("Result returned by removeFirst() was wrong.",
                confirmedFirst, first);
        verify();
    }

    /**
     *  Tests {@link LinkedList#removeLast(Object)}.
     */
    public void testLinkedListRemoveLast() {
        if (!isRemoveSupported()) return;

        resetEmpty();
        try {
            getLinkedList().removeLast();
            fail("removeLast() should throw a NoSuchElementException for " +
                    "an empty list.");
        } catch (NoSuchElementException e) {
            // This is correct
        }
        verify();

        resetFull();
        Object last = getLinkedList().removeLast();
        Object confirmedLast = getConfirmedLinkedList().removeLast();
        assertEquals("Result returned by removeLast() was wrong.",
                confirmedLast, last);
        verify();
    }

    /**
     *  Returns an empty {@link ArrayList}.
     */
    protected Collection makeConfirmedCollection() {
        return new LinkedList();
    }

    /**
     *  Returns a full {@link ArrayList}.
     */
    protected Collection makeConfirmedFullCollection() {
        List list = new LinkedList();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }
}
