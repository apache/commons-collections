/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestIteratorUtils.java,v 1.6 2003/09/29 03:56:12 psteitz Exp $
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
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;

import org.apache.commons.collections.iterators.ResetableIterator;
import org.apache.commons.collections.iterators.ResetableListIterator;
/**
 *  Tests for IteratorUtils.
 */
public class TestIteratorUtils extends BulkTest {

    public TestIteratorUtils(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestIteratorUtils.class);
    }

    public void testToList() {
        List list = new ArrayList();
        list.add(new Integer(1));
        list.add("Two");
        list.add(null);
        List result = IteratorUtils.toList(list.iterator());
        assertEquals(list, result);
    }

    public void testToArray() {
        List list = new ArrayList();
        list.add(new Integer(1));
        list.add("Two");
        list.add(null);
        Object[] result = IteratorUtils.toArray(list.iterator());
        assertEquals(list, Arrays.asList(result));
    }

    public void testToArray2() {
        List list = new ArrayList();
        list.add("One");
        list.add("Two");
        list.add(null);
        String[] result = (String[]) IteratorUtils.toArray(list.iterator(), String.class);
        assertEquals(list, Arrays.asList(result));
    }
    
    public void testArrayIterator() {
        Object[] objArray = {"a", "b", "c"};
        ResetableIterator iterator = IteratorUtils.arrayIterator(objArray);
        assertTrue(iterator.next().equals("a"));
        assertTrue(iterator.next().equals("b"));
        iterator.reset();
        assertTrue(iterator.next().equals("a"));
        
        try {
            iterator = IteratorUtils.arrayIterator(new Integer(0));
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
                // expected
        }
        
        try {
            iterator = IteratorUtils.arrayIterator(null);
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
                // expected
        }
        
        iterator = IteratorUtils.arrayIterator(objArray, 1);
        assertTrue(iterator.next().equals("b"));
        
        try {
            iterator = IteratorUtils.arrayIterator(objArray, -1);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        iterator = IteratorUtils.arrayIterator(objArray, 3);
        assertTrue(!iterator.hasNext());
        iterator.reset();
        
        try {
            iterator = IteratorUtils.arrayIterator(objArray, 4);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        iterator = IteratorUtils.arrayIterator(objArray, 2, 3);
        assertTrue(iterator.next().equals("c"));
        
        try {
            iterator = IteratorUtils.arrayIterator(objArray, 2, 4);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        try {
            iterator = IteratorUtils.arrayIterator(objArray, -1, 1);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        try {
            iterator = IteratorUtils.arrayIterator(objArray, 2, 1);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        int[] intArray = {0, 1, 2};
        iterator = IteratorUtils.arrayIterator(intArray);
        assertTrue(iterator.next().equals(new Integer(0)));
        assertTrue(iterator.next().equals(new Integer(1)));
        iterator.reset();
        assertTrue(iterator.next().equals(new Integer(0)));
        
        iterator = IteratorUtils.arrayIterator(intArray, 1);
        assertTrue(iterator.next().equals(new Integer(1)));
        
        try {
            iterator = IteratorUtils.arrayIterator(intArray, -1);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        iterator = IteratorUtils.arrayIterator(intArray, 3);
        assertTrue(!iterator.hasNext());
        iterator.reset();
        
        try {
            iterator = IteratorUtils.arrayIterator(intArray, 4);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        iterator = IteratorUtils.arrayIterator(intArray, 2, 3);
        assertTrue(iterator.next().equals(new Integer(2)));
        
        try {
            iterator = IteratorUtils.arrayIterator(intArray, 2, 4);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        try {
            iterator = IteratorUtils.arrayIterator(intArray, -1, 1);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        try {
            iterator = IteratorUtils.arrayIterator(intArray, 2, 1);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }          
    }
    
    public void testArrayListIterator() {
        Object[] objArray = {"a", "b", "c", "d"};
        ResetableListIterator iterator = IteratorUtils.arrayListIterator(objArray);
        assertTrue(!iterator.hasPrevious());
        assertTrue(iterator.previousIndex() == -1);
        assertTrue(iterator.nextIndex() == 0);
        assertTrue(iterator.next().equals("a"));
        assertTrue(iterator.previous().equals("a"));
        assertTrue(iterator.next().equals("a"));
        assertTrue(iterator.previousIndex() == 0);
        assertTrue(iterator.nextIndex() == 1);
        assertTrue(iterator.next().equals("b"));
        assertTrue(iterator.next().equals("c"));
        assertTrue(iterator.next().equals("d"));
        assertTrue(iterator.nextIndex() == 4); // size of list
        assertTrue(iterator.previousIndex() == 3);
        
        try {
            iterator = IteratorUtils.arrayListIterator(new Integer(0));
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
                // expected
        }
        
        try {
            iterator = IteratorUtils.arrayListIterator(null);
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
                // expected
        }
        
        iterator = IteratorUtils.arrayListIterator(objArray, 1);
        assertTrue(iterator.previousIndex() == -1); 
        assertTrue(!iterator.hasPrevious());
        assertTrue(iterator.nextIndex() == 0); 
        assertTrue(iterator.next().equals("b"));
        assertTrue(iterator.previousIndex() == 0);        
        
        try {
            iterator = IteratorUtils.arrayListIterator(objArray, -1);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        iterator = IteratorUtils.arrayListIterator(objArray, 3);
        assertTrue(iterator.hasNext());
        try {
            Object x = iterator.previous();
            fail("Expecting NoSuchElementException.");
        } catch (NoSuchElementException ex) {
            // expected
        }
        
        try {
            iterator = IteratorUtils.arrayListIterator(objArray, 5);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        iterator = IteratorUtils.arrayListIterator(objArray, 2, 3);
        assertTrue(iterator.next().equals("c"));
        
        try {
            iterator = IteratorUtils.arrayListIterator(objArray, 2, 5);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        try {
            iterator = IteratorUtils.arrayListIterator(objArray, -1, 1);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        try {
            iterator = IteratorUtils.arrayListIterator(objArray, 2, 1);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        
        int[] intArray = {0, 1, 2};
        iterator = IteratorUtils.arrayListIterator(intArray);
        assertTrue(iterator.previousIndex() == -1); 
        assertTrue(!iterator.hasPrevious());
        assertTrue(iterator.nextIndex() == 0); 
        assertTrue(iterator.next().equals(new Integer(0)));
        assertTrue(iterator.previousIndex() == 0); 
        assertTrue(iterator.nextIndex() == 1); 
        assertTrue(iterator.next().equals(new Integer(1)));
        assertTrue(iterator.previousIndex() == 1); 
        assertTrue(iterator.nextIndex() == 2); 
        assertTrue(iterator.previous().equals(new Integer(1)));
        assertTrue(iterator.next().equals(new Integer(1)));
        
        iterator = IteratorUtils.arrayListIterator(intArray, 1);
        assertTrue(iterator.previousIndex() == -1); 
        assertTrue(!iterator.hasPrevious());
        assertTrue(iterator.nextIndex() == 0); 
        assertTrue(iterator.next().equals(new Integer(1)));
        assertTrue(iterator.previous().equals(new Integer(1)));
        assertTrue(iterator.next().equals(new Integer(1)));
        assertTrue(iterator.previousIndex() == 0); 
        assertTrue(iterator.nextIndex() == 1); 
        assertTrue(iterator.next().equals(new Integer(2)));
        assertTrue(iterator.previousIndex() == 1); 
        assertTrue(iterator.nextIndex() == 2); 
        assertTrue(iterator.previous().equals(new Integer(2)));
        assertTrue(iterator.previousIndex() == 0); 
        assertTrue(iterator.nextIndex() == 1); 
        
        try {
            iterator = IteratorUtils.arrayListIterator(intArray, -1);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        iterator = IteratorUtils.arrayListIterator(intArray, 3);
        assertTrue(!iterator.hasNext());
     
        try {
            iterator = IteratorUtils.arrayListIterator(intArray, 4);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        iterator = IteratorUtils.arrayListIterator(intArray, 2, 3);
        assertTrue(!iterator.hasPrevious());
        assertTrue(iterator.previousIndex() == -1);
        assertTrue(iterator.next().equals(new Integer(2)));
        assertTrue(iterator.hasPrevious());
        assertTrue(!iterator.hasNext());
        
        
        try {
            iterator = IteratorUtils.arrayListIterator(intArray, 2, 4);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        try {
            iterator = IteratorUtils.arrayListIterator(intArray, -1, 1);
            fail("Expecting IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        
        try {
            iterator = IteratorUtils.arrayListIterator(intArray, 2, 1);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }          
    }
        

    /**
     * Gets an immutable Iterator operating on the elements ["a", "b", "c", "d"].
     */
    private Iterator getImmutableIterator() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return IteratorUtils.unmodifiableIterator(list.iterator());
    }

    /**
     * Gets an immutable ListIterator operating on the elements ["a", "b", "c", "d"].
     */
    private ListIterator getImmutableListIterator() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return IteratorUtils.unmodifiableListIterator(list.listIterator());
    }

	/**
	 * Test next() and hasNext() for an immutable Iterator.
	 */
    public void testUnmodifiableIteratorIteration() {
        Iterator iterator = getImmutableIterator();

        assertTrue(iterator.hasNext());

        assertEquals("a", iterator.next());

        assertTrue(iterator.hasNext());

        assertEquals("b", iterator.next());

        assertTrue(iterator.hasNext());

        assertEquals("c", iterator.next());

        assertTrue(iterator.hasNext());

        assertEquals("d", iterator.next());

        assertTrue(!iterator.hasNext());
    }

    /**
     * Test resetability
     */
    public void testResetableUnmodifiableIterator() {
        Integer four = new Integer(4);
        ResetableIterator it = (ResetableIterator) 
            IteratorUtils.unmodifiableIterator(IteratorUtils.singletonIterator(four));
        
        assertEquals(true, it.hasNext());
        assertSame(four, it.next());
        assertEquals(false, it.hasNext());
        it.reset();
        assertEquals(true, it.hasNext());
    }
    
    /**
     * Test next(), hasNext(), previous() and hasPrevious() for an immutable
     * ListIterator.
     */
    public void testUnmodifiableListIteratorIteration() {
        ListIterator listIterator = getImmutableListIterator();

        assertTrue(!listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("a", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("b", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("c", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("d", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(!listIterator.hasNext());

        assertEquals("d", listIterator.previous());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("c", listIterator.previous());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("b", listIterator.previous());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("a", listIterator.previous());

        assertTrue(!listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());
    }

    /**
     * Test resetability
     */
    public void testResetableUnmodifiableListIterator() {
        Integer four = new Integer(4);
        ResetableListIterator it = (ResetableListIterator) 
            IteratorUtils.unmodifiableListIterator(IteratorUtils.singletonListIterator(four));
        
        assertEquals(true, it.hasNext());
        assertSame(four, it.next());
        assertEquals(false, it.hasNext());
        it.reset();
        assertEquals(true, it.hasNext());
    }
    
    /**
     * Test remove() for an immutable Iterator.
     */
    public void testUnmodifiableIteratorImmutability() {
        Iterator iterator = getImmutableIterator();

        try {
            iterator.remove();
            // We shouldn't get to here.
            fail("remove() should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // This is correct; ignore the exception.
        }

        iterator.next();

        try {
            iterator.remove();
            // We shouldn't get to here.
            fail("remove() should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // This is correct; ignore the exception.
        }

    }

    /**
     * Test remove() for an immutable ListIterator.
     */
    public void testUnmodifiableListIteratorImmutability() {
    	ListIterator listIterator = getImmutableListIterator();

        try {
            listIterator.remove();
            // We shouldn't get to here.
            fail("remove() should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // This is correct; ignore the exception.
        }

        try {
            listIterator.set("a");
            // We shouldn't get to here.
            fail("set(Object) should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // This is correct; ignore the exception.
        }

        try {
            listIterator.add("a");
            // We shouldn't get to here.
            fail("add(Object) should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // This is correct; ignore the exception.
        }

        listIterator.next();

        try {
            listIterator.remove();
            // We shouldn't get to here.
            fail("remove() should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // This is correct; ignore the exception.
        }

        try {
            listIterator.set("a");
            // We shouldn't get to here.
            fail("set(Object) should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // This is correct; ignore the exception.
        }

        try {
            listIterator.add("a");
            // We shouldn't get to here.
            fail("add(Object) should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // This is correct; ignore the exception.
        }
    }
}
