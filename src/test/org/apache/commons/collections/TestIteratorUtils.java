/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestIteratorUtils.java,v 1.5 2003/08/31 17:28:43 scolebourne Exp $
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
