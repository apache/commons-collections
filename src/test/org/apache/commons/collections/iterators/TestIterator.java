/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/iterators/Attic/TestIterator.java,v 1.3 2003/02/19 20:33:10 scolebourne Exp $
 * $Revision: 1.3 $
 * $Date: 2003/02/19 20:33:10 $
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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.TestObject;
/**
 * Base class for tetsing Iterator interface
 * 
 * @author Morgan Delagrange
 * @author Stephen Colebourne
 */
public abstract class TestIterator extends TestObject {

    public TestIterator(String testName) {
        super(testName);
    }

    public abstract Iterator makeEmptyIterator();

    public abstract Iterator makeFullIterator();

    /**
     * Whether or not we are testing an iterator that can be
     * empty.  Default is true.
     * 
     * @return true if Iterators can be empty
     */
    public boolean supportsEmptyIterator() {
        return true;
    }

    /**
     * Whether or not we are testing an iterator that can contain
     * elements.  Default is true.
     * 
     * @return true if Iterators can be empty
     */
    public boolean supportsFullIterator() {
        return true;
    }

    /**
     * Whether or not we are testing an iterator that supports
     * remove().  Default is true.
     * 
     * @return true if Iterators can be empty
     */
    public boolean supportsRemove() {
        return true;
    }

    /**
     * Should throw a NoSuchElementException.
     */
    public void testEmptyIterator() {
        if (supportsEmptyIterator() == false) {
            return;
        }

        Iterator iter = makeEmptyIterator();
        assertTrue("hasNext() should return false for empty iterators", iter.hasNext() == false);
        try {
            iter.next();
            fail("NoSuchElementException must be thrown when Iterator is exhausted");
        } catch (NoSuchElementException e) {
        }
    }

    /**
     * NoSuchElementException (or any other exception)
     * should not be thrown for the first element.  
     * NoSuchElementException must be thrown when
     * hasNext() returns false
     */
    public void testFullIterator() {
        if (supportsFullIterator() == false) {
            return;
        }

        Iterator iter = makeFullIterator();

        assertTrue("hasNext() should return true for at least one element", iter.hasNext());

        try {
            iter.next();
        } catch (NoSuchElementException e) {
            fail("Full iterators must have at least one element");
        }

        while (iter.hasNext()) {
            iter.next();
        }

        try {
            iter.next();
            fail("NoSuchElementException must be thrown when Iterator is exhausted");
        } catch (NoSuchElementException e) {
        }
    }

    /**
     * Test remove
     */
    public void testRemove() {
        Iterator it = makeFullIterator();
        
        if (supportsRemove() == false) {
            try {
                it.remove();
            } catch (UnsupportedOperationException ex) {}
            return;
        }
        
        try {
            it.remove();
            fail();
        } catch (IllegalStateException ex) {}
        
        it.next();
        it.remove();
        
        try {
            it.remove();
            fail();
        } catch (IllegalStateException ex) {}
    }
    
}
