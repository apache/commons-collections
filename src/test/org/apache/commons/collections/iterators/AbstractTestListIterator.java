/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 */
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Abstract class for testing the ListIterator interface.
 * <p>
 * This class provides a framework for testing an implementation of ListIterator.
 * Concrete subclasses must provide the list iterator to be tested.
 * They must also specify certain details of how the list iterator operates by
 * overriding the supportsXxx() methods if necessary.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.7 $ $Date: 2004/01/14 21:34:26 $
 * 
 * @author Rodney Waldhoff
 * @author Stephen Colebourne
 */
public abstract class AbstractTestListIterator extends AbstractTestIterator {

    /**
     * JUnit constructor.
     * 
     * @param testName  the test class name
     */
    public AbstractTestListIterator(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Implement this method to return a list iterator over an empty collection.
     * 
     * @return an empty iterator
     */
    public abstract ListIterator makeEmptyListIterator();

    /**
     * Implement this method to return a list iterator over a collection with elements.
     * 
     * @return a full iterator
     */
    public abstract ListIterator makeFullListIterator();

    /**
     * Implements the abstract superclass method to return the list iterator.
     * 
     * @return an empty iterator
     */
    public Iterator makeEmptyIterator() {
        return makeEmptyListIterator();
    }

    /**
     * Implements the abstract superclass method to return the list iterator.
     * 
     * @return a full iterator
     */
    public Iterator makeFullIterator() {
        return makeFullListIterator();
    }

    /**
     * Whether or not we are testing an iterator that supports add().
     * Default is true.
     * 
     * @return true if Iterator supports add
     */
    public boolean supportsAdd() {
        return true;
    }

    /**
     * Whether or not we are testing an iterator that supports set().
     * Default is true.
     * 
     * @return true if Iterator supports set
     */
    public boolean supportsSet() {
        return true;
    }

    /**
     * The value to be used in the add and set tests.
     * Default is null.
     */
    public Object addSetValue() {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Test that the empty list iterator contract is correct.
     */
    public void testEmptyListIteratorIsIndeedEmpty() {
        if (supportsEmptyIterator() == false) {
            return;
        }

        ListIterator it = makeEmptyListIterator();
        
        assertEquals(false, it.hasNext());
        assertEquals(0, it.nextIndex());
        assertEquals(false, it.hasPrevious());
        assertEquals(-1, it.previousIndex());
        
        // next() should throw a NoSuchElementException
        try {
            it.next();
            fail("NoSuchElementException must be thrown from empty ListIterator");
        } catch (NoSuchElementException e) {
        }
        
        // previous() should throw a NoSuchElementException
        try {
            it.previous();
            fail("NoSuchElementException must be thrown from empty ListIterator");
        } catch (NoSuchElementException e) {
        }
    }
    
    /**
     * Test navigation through the iterator.
     */
    public void testWalkForwardAndBack() {
        ArrayList list = new ArrayList();
        ListIterator it = makeFullListIterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        
        // check state at end
        assertEquals(false, it.hasNext());
        assertEquals(true, it.hasPrevious());
        try {
            it.next();
            fail("NoSuchElementException must be thrown from next at end of ListIterator");
        } catch (NoSuchElementException e) {
        }
        
        // loop back through comparing
        for (int i = list.size() - 1; i >= 0; i--) {
            assertEquals(i + 1, it.nextIndex());
            assertEquals(i, it.previousIndex());
        
            Object obj = list.get(i);
            assertEquals(obj, it.previous());
        }
        
        // check state at start
        assertEquals(true, it.hasNext());
        assertEquals(false, it.hasPrevious());
        try {
            it.previous();
            fail("NoSuchElementException must be thrown from previous at start of ListIterator");
        } catch (NoSuchElementException e) {
        }
    }
    
    /**
     * Test add behaviour.
     */
    public void testAdd() {
        ListIterator it = makeFullListIterator();
        
        Object addValue = addSetValue();
        if (supportsAdd() == false) {
            // check for UnsupportedOperationException if not supported
            try {
                it.add(addValue);
            } catch (UnsupportedOperationException ex) {}
            return;
        }
        
        // add at start should be OK, added should be previous
        it = makeFullListIterator();
        it.add(addValue);
        assertEquals(addValue, it.previous());

        // add at start should be OK, added should not be next
        it = makeFullListIterator();
        it.add(addValue);
        assertTrue(addValue != it.next());

        // add in middle and at end should be OK
        it = makeFullListIterator();
        while (it.hasNext()) {
            it.next();
            it.add(addValue);
            // check add OK
            assertEquals(addValue, it.previous());
            it.next();
        }        
    }
    
    /**
     * Test set behaviour.
     */
    public void testSet() {
        ListIterator it = makeFullListIterator();
        
        if (supportsSet() == false) {
            // check for UnsupportedOperationException if not supported
            try {
                it.set(addSetValue());
            } catch (UnsupportedOperationException ex) {}
            return;
        }
        
        // should throw IllegalStateException before next() called
        try {
            it.set(addSetValue());
            fail();
        } catch (IllegalStateException ex) {}
        
        // set after next should be fine
        it.next();
        it.set(addSetValue());
        
        // repeated set calls should be fine
        it.set(addSetValue());

    }
    
    public void testRemoveThenSet() {
        ListIterator it = makeFullListIterator();
        if (supportsRemove() && supportsSet()) {
            it.next();
            it.remove();
            try {
                it.set(addSetValue());
                fail("IllegalStateException must be thrown from set after remove");
            } catch (IllegalStateException e) {
            }
        }
    }

    public void testAddThenSet() {
        ListIterator it = makeFullListIterator();        
        // add then set
        if (supportsAdd() && supportsSet()) {
            it.next();
            it.add(addSetValue());
            try {
                it.set(addSetValue());
                fail("IllegalStateException must be thrown from set after add");
            } catch (IllegalStateException e) {
            }
        }
    }
    
    /**
     * Test remove after add behaviour.
     */
    public void testAddThenRemove() {
        ListIterator it = makeFullListIterator();
        
        // add then remove
        if (supportsAdd() && supportsRemove()) {
            it.next();
            it.add(addSetValue());
            try {
                it.remove();
                fail("IllegalStateException must be thrown from remove after add");
            } catch (IllegalStateException e) {
            }
        }
    }
    
}
