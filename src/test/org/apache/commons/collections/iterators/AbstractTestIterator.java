/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/iterators/AbstractTestIterator.java,v 1.4 2003/11/08 18:46:57 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
import java.util.NoSuchElementException;

import org.apache.commons.collections.AbstractTestObject;

/**
 * Abstract class for testing the Iterator interface.
 * <p>
 * This class provides a framework for testing an implementation of Iterator.
 * Concrete subclasses must provide the iterator to be tested.
 * They must also specify certain details of how the iterator operates by
 * overriding the supportsXxx() methods if necessary.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2003/11/08 18:46:57 $
 * 
 * @author Morgan Delagrange
 * @author Stephen Colebourne
 */
public abstract class AbstractTestIterator extends AbstractTestObject {

    /**
     * JUnit constructor.
     * 
     * @param testName  the test class name
     */
    public AbstractTestIterator(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Implement this method to return an iterator over an empty collection.
     * 
     * @return an empty iterator
     */
    protected abstract Iterator makeEmptyIterator();

    /**
     * Implement this method to return an iterator over a collection with elements.
     * 
     * @return a full iterator
     */
    protected abstract Iterator makeFullIterator();

    /**
     * Implements the abstract superclass method to return the full iterator.
     * 
     * @return a full iterator
     */
    protected Object makeObject() {
        return makeFullIterator();
    }

    /**
     * Whether or not we are testing an iterator that can be empty.
     * Default is true.
     * 
     * @return true if Iterator can be empty
     */
    protected boolean supportsEmptyIterator() {
        return true;
    }

    /**
     * Whether or not we are testing an iterator that can contain elements.
     * Default is true.
     * 
     * @return true if Iterator can be full
     */
    protected boolean supportsFullIterator() {
        return true;
    }

    /**
     * Whether or not we are testing an iterator that supports remove().
     * Default is true.
     * 
     * @return true if Iterator supports remove
     */
    protected boolean supportsRemove() {
        return true;
    }

    /**
     * Allows subclasses to add complex cross verification
     */
    protected void verify() {
        // do nothing
    }

    //-----------------------------------------------------------------------
    /**
     * Test the empty iterator.
     */
    public void testEmptyIterator() {
        if (supportsEmptyIterator() == false) {
            return;
        }

        Iterator it = makeEmptyIterator();
        
        // hasNext() should return false
        assertEquals("hasNext() should return false for empty iterators", false, it.hasNext());
        
        // next() should throw a NoSuchElementException
        try {
            it.next();
            fail("NoSuchElementException must be thrown when Iterator is exhausted");
        } catch (NoSuchElementException e) {
        }
        verify();
    }

    /**
     * Test normal iteration behaviour.
     */
    public void testFullIterator() {
        if (supportsFullIterator() == false) {
            return;
        }

        Iterator it = makeFullIterator();

        // hasNext() must be true (ensure makeFullIterator is correct!)
        assertEquals("hasNext() should return true for at least one element", true, it.hasNext());

        // next() must not throw exception (ensure makeFullIterator is correct!)
        try {
            it.next();
        } catch (NoSuchElementException e) {
            fail("Full iterators must have at least one element");
        }

        // iterate through
        while (it.hasNext()) {
            it.next();
            verify();
        }

        // next() must throw NoSuchElementException now
        try {
            it.next();
            fail("NoSuchElementException must be thrown when Iterator is exhausted");
        } catch (NoSuchElementException e) {
        }
    }

    /**
     * Test remove behaviour.
     */
    public void testRemove() {
        Iterator it = makeFullIterator();
        
        if (supportsRemove() == false) {
            // check for UnsupportedOperationException if not supported
            try {
                it.remove();
            } catch (UnsupportedOperationException ex) {}
            return;
        }
        
        // should throw IllegalStateException before next() called
        try {
            it.remove();
            fail();
        } catch (IllegalStateException ex) {}
        verify();
        
        // remove after next should be fine
        it.next();
        it.remove();
        
        // should throw IllegalStateException for second remove()
        try {
            it.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }
    
}
