/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/buffer/Attic/TestBinaryBuffer.java,v 1.1 2004/01/01 19:01:34 scolebourne Exp $
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
package org.apache.commons.collections.buffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.collection.AbstractTestCollection;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

/**
 * Tests the BinaryHeap.
 * 
 * @version $Revision: 1.1 $ $Date: 2004/01/01 19:01:34 $
 * 
 * @author Michael A. Smith
 */
public class TestBinaryBuffer extends AbstractTestCollection {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestBinaryBuffer.class);
    }

    public TestBinaryBuffer(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------  
    public void verify() {
        super.verify();
        BinaryBuffer heap = (BinaryBuffer) collection;

        Comparator c = heap.comparator;
        if (c == null) {
            c = ComparatorUtils.naturalComparator();
        }
        if (!heap.ascendingOrder) {
            c = ComparatorUtils.reversedComparator(c);
        }

        Object[] tree = heap.elements;
        for (int i = 1; i <= heap.size; i++) {
            Object parent = tree[i];
            if (i * 2 <= heap.size) {
                assertTrue("Parent is less than or equal to its left child", c.compare(parent, tree[i * 2]) <= 0);
            }
            if (i * 2 + 1 < heap.size) {
                assertTrue("Parent is less than or equal to its right child", c.compare(parent, tree[i * 2 + 1]) <= 0);
            }
        }
    }

    //-----------------------------------------------------------------------  
    /**
     * Overridden because BinaryBuffer isn't fail fast.
     * @return false
     */
    public boolean isFailFastSupported() {
        return false;
    }

    //-----------------------------------------------------------------------  
    public Collection makeConfirmedCollection() {
        return new ArrayList();
    }

    public Collection makeConfirmedFullCollection() {
        ArrayList list = new ArrayList();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    /**
     * Return a new, empty {@link Object} to used for testing.
     */
    public Collection makeCollection() {
        return new BinaryBuffer();
    }

    //-----------------------------------------------------------------------  
    public Object[] getFullElements() {
        return getFullNonNullStringElements();
    }

    public Object[] getOtherElements() {
        return getOtherNonNullStringElements();
    }

    //-----------------------------------------------------------------------  
    public void testBufferEmpty() {
        resetEmpty();
        Buffer buffer = (Buffer) collection;

        assertEquals(0, buffer.size());
        assertEquals(true, buffer.isEmpty());
        try {
            buffer.get();
            fail();
        } catch (BufferUnderflowException ex) {}

        try {
            buffer.remove();
            fail();
        } catch (BufferUnderflowException ex) {}
    }
    
    public void testBasicOps() {
        BinaryBuffer heap = new BinaryBuffer();

        heap.add("a");
        heap.add("c");
        heap.add("e");
        heap.add("b");
        heap.add("d");
        heap.add("n");
        heap.add("m");
        heap.add("l");
        heap.add("k");
        heap.add("j");
        heap.add("i");
        heap.add("h");
        heap.add("g");
        heap.add("f");

        assertTrue("heap should not be empty after adds", !heap.isEmpty());

        for (int i = 0; i < 14; i++) {
            assertEquals(
                "get using default constructor should return minimum value in the binary heap",
                String.valueOf((char) ('a' + i)),
                heap.get());

            assertEquals(
                "remove using default constructor should return minimum value in the binary heap",
                String.valueOf((char) ('a' + i)),
                heap.remove());

            if (i + 1 < 14) {
                assertTrue("heap should not be empty before all elements are removed", !heap.isEmpty());
            } else {
                assertTrue("heap should be empty after all elements are removed", heap.isEmpty());
            }
        }

        try {
            heap.get();
            fail("NoSuchElementException should be thrown if get is called after all elements are removed");
        } catch (BufferUnderflowException ex) {}

        try {
            heap.remove();
            fail("NoSuchElementException should be thrown if remove is called after all elements are removed");
        } catch (BufferUnderflowException ex) {}
    }

    public void testBasicComparatorOps() {
        BinaryBuffer heap = new BinaryBuffer(new ReverseComparator(new ComparableComparator()));

        assertTrue("heap should be empty after create", heap.isEmpty());

        try {
            heap.get();
            fail("NoSuchElementException should be thrown if get is called before any elements are added");
        } catch (BufferUnderflowException ex) {}

        try {
            heap.remove();
            fail("NoSuchElementException should be thrown if remove is called before any elements are added");
        } catch (BufferUnderflowException ex) {}

        heap.add("a");
        heap.add("c");
        heap.add("e");
        heap.add("b");
        heap.add("d");
        heap.add("n");
        heap.add("m");
        heap.add("l");
        heap.add("k");
        heap.add("j");
        heap.add("i");
        heap.add("h");
        heap.add("g");
        heap.add("f");

        assertTrue("heap should not be empty after adds", !heap.isEmpty());

        for (int i = 0; i < 14; i++) {

            // note: since we're using a comparator that reverses items, the
            // "minimum" item is "n", and the "maximum" item is "a".

            assertEquals(
                "get using default constructor should return minimum value in the binary heap",
                String.valueOf((char) ('n' - i)),
                heap.get());

            assertEquals(
                "remove using default constructor should return minimum value in the binary heap",
                String.valueOf((char) ('n' - i)),
                heap.remove());

            if (i + 1 < 14) {
                assertTrue("heap should not be empty before all elements are removed", !heap.isEmpty());
            } else {
                assertTrue("heap should be empty after all elements are removed", heap.isEmpty());
            }
        }

        try {
            heap.get();
            fail("NoSuchElementException should be thrown if get is called after all elements are removed");
        } catch (BufferUnderflowException ex) {}

        try {
            heap.remove();
            fail("NoSuchElementException should be thrown if remove is called after all elements are removed");
        } catch (BufferUnderflowException ex) {}
    }

//    public void testAddRemove() {
//        resetEmpty();
//        BinaryBuffer heap = (BinaryBuffer) collection;
//        heap.add(new Integer(0));
//        heap.add(new Integer(2));
//        heap.add(new Integer(4));
//        heap.add(new Integer(3));
//        heap.add(new Integer(8));
//        heap.add(new Integer(10));
//        heap.add(new Integer(12));
//        heap.add(new Integer(3));
//        confirmed.addAll(heap);
//        System.out.println(heap);
//        Object obj = new Integer(10);
//        heap.remove(obj);
//        confirmed.remove(obj);
//        System.out.println(heap);
//        verify();
//    }
    
}
