/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/bag/AbstractTestBag.java,v 1.4 2003/12/24 23:09:26 scolebourne Exp $
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
package org.apache.commons.collections.bag;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections.AbstractTestObject;
import org.apache.commons.collections.Bag;

/**
 * Abstract test class for {@link Bag} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeBag} method.
 * <p>
 * If your {@link Bag} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Bag} fails.
 *
 * @version $Revision: 1.4 $ $Date: 2003/12/24 23:09:26 $
 * 
 * @author Chuck Burdick
 * @author Stephen Colebourne
 */
public abstract class AbstractTestBag extends AbstractTestObject {
//  TODO: this class should really extend from TestCollection, but the bag
//  implementations currently do not conform to the Collection interface.  Once
//  those are fixed or at least a strategy is made for resolving the issue, this
//  can be changed back to extend TestCollection instead.

    /**
     * JUnit constructor.
     * 
     * @param testName  the test class name
     */
    public AbstractTestBag(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Return a new, empty {@link Bag} to used for testing.
     * 
     * @return the bag to be tested
     */
    public abstract Bag makeBag();

    /**
     * Implements the superclass method to return the Bag.
     * 
     * @return the bag to be tested
     */
    public Object makeObject() {
        return makeBag();
    }

    //-----------------------------------------------------------------------
    public void testBagAdd() {
        Bag bag = makeBag();
        bag.add("A");
        assertTrue("Should contain 'A'", bag.contains("A"));
        assertEquals("Should have count of 1", 1, bag.getCount("A"));
        bag.add("A");
        assertTrue("Should contain 'A'", bag.contains("A"));
        assertEquals("Should have count of 2", 2, bag.getCount("A"));
        bag.add("B");
        assertTrue(bag.contains("A"));
        assertTrue(bag.contains("B"));
    }

    public void testBagEqualsSelf() {
        Bag bag = makeBag();
        assertTrue(bag.equals(bag));
        bag.add("elt");
        assertTrue(bag.equals(bag));
        bag.add("elt"); // again
        assertTrue(bag.equals(bag));
        bag.add("elt2");
        assertTrue(bag.equals(bag));
    }

    public void testRemove() {
        Bag bag = makeBag();
        bag.add("A");
        assertEquals("Should have count of 1", 1, bag.getCount("A"));
        bag.remove("A");
        assertEquals("Should have count of 0", 0, bag.getCount("A"));
        bag.add("A");
        bag.add("A");
        bag.add("A");
        bag.add("A");
        assertEquals("Should have count of 4", 4, bag.getCount("A"));
        bag.remove("A", 0);
        assertEquals("Should have count of 4", 4, bag.getCount("A"));
        bag.remove("A", 2);
        assertEquals("Should have count of 2", 2, bag.getCount("A"));
        bag.remove("A");
        assertEquals("Should have count of 0", 0, bag.getCount("A"));
    }

    public void testRemoveAll() {
        Bag bag = makeBag();
        bag.add("A", 2);
        assertEquals("Should have count of 2", 2, bag.getCount("A"));
        bag.add("B");
        bag.add("C");
        assertEquals("Should have count of 4", 4, bag.size());
        List delete = new ArrayList();
        delete.add("A");
        delete.add("B");
        bag.removeAll(delete);
        assertEquals("Should have count of 1", 1, bag.getCount("A"));
        assertEquals("Should have count of 0", 0, bag.getCount("B"));
        assertEquals("Should have count of 1", 1, bag.getCount("C"));
        assertEquals("Should have count of 2", 2, bag.size());
    }
    
    public void testContains() {
        Bag bag = makeBag();
        
        assertEquals("Bag does not have at least 1 'A'", false, bag.contains("A"));
        assertEquals("Bag does not have at least 1 'B'", false, bag.contains("B"));
        
        bag.add("A");  // bag 1A
        assertEquals("Bag has at least 1 'A'", true, bag.contains("A"));
        assertEquals("Bag does not have at least 1 'B'", false, bag.contains("B"));
        
        bag.add("A");  // bag 2A
        assertEquals("Bag has at least 1 'A'", true, bag.contains("A"));
        assertEquals("Bag does not have at least 1 'B'", false, bag.contains("B"));
        
        bag.add("B");  // bag 2A,1B
        assertEquals("Bag has at least 1 'A'", true, bag.contains("A"));
        assertEquals("Bag has at least 1 'B'", true, bag.contains("B"));
    }

    public void testContainsAll() {
        Bag bag = makeBag();
        List known = new ArrayList();
        List known1A = new ArrayList();
        known1A.add("A");
        List known2A = new ArrayList();
        known2A.add("A");
        known2A.add("A");
        List known1B = new ArrayList();
        known1B.add("B");
        List known1A1B = new ArrayList();
        known1A1B.add("A");
        known1A1B.add("B");
        
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag does not containsAll of 1 'A'", false, bag.containsAll(known1A));
        assertEquals("Bag does not containsAll of 2 'A'", false, bag.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, bag.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, bag.containsAll(known1A1B));
        
        bag.add("A");  // bag 1A
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, bag.containsAll(known1A));
        assertEquals("Bag does not containsAll of 2 'A'", false, bag.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, bag.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, bag.containsAll(known1A1B));
        
        bag.add("A");  // bag 2A
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, bag.containsAll(known1A));
        assertEquals("Bag containsAll of 2 'A'", true, bag.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, bag.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, bag.containsAll(known1A1B));
        
        bag.add("A");  // bag 3A
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, bag.containsAll(known1A));
        assertEquals("Bag containsAll of 2 'A'", true, bag.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, bag.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, bag.containsAll(known1A1B));
        
        bag.add("B");  // bag 3A1B
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, bag.containsAll(known1A));
        assertEquals("Bag containsAll of 2 'A'", true, bag.containsAll(known2A));
        assertEquals("Bag containsAll of 1 'B'", true, bag.containsAll(known1B));
        assertEquals("Bag containsAll of 1 'A' 1 'B'", true, bag.containsAll(known1A1B));
    }

    public void testSize() {
        Bag bag = makeBag();
        assertEquals("Should have 0 total items", 0, bag.size());
        bag.add("A");
        assertEquals("Should have 1 total items", 1, bag.size());
        bag.add("A");
        assertEquals("Should have 2 total items", 2, bag.size());
        bag.add("A");
        assertEquals("Should have 3 total items", 3, bag.size());
        bag.add("B");
        assertEquals("Should have 4 total items", 4, bag.size());
        bag.add("B");
        assertEquals("Should have 5 total items", 5, bag.size());
        bag.remove("A", 2);
        assertEquals("Should have 1 'A'", 1, bag.getCount("A"));
        assertEquals("Should have 3 total items", 3, bag.size());
        bag.remove("B");
        assertEquals("Should have 1 total item", 1, bag.size());
    }
    
    public void testRetainAll() {
        Bag bag = makeBag();
        bag.add("A");
        bag.add("A");
        bag.add("A");
        bag.add("B");
        bag.add("B");
        bag.add("C");
        List retains = new ArrayList();
        retains.add("B");
        retains.add("C");
        bag.retainAll(retains);
        assertEquals("Should have 2 total items", 2, bag.size());
    }

    public void testIterator() {
        Bag bag = makeBag();
        bag.add("A");
        bag.add("A");
        bag.add("B");
        assertEquals("Bag should have 3 items", 3, bag.size());
        Iterator i = bag.iterator();
    
        boolean foundA = false;
        while (i.hasNext()) {
            String element = (String) i.next();
            // ignore the first A, remove the second via Iterator.remove()
            if (element.equals("A")) {
                if (foundA == false) {
                    foundA = true;
                } else {
                    i.remove();
                }
            }
        }
    
        assertTrue("Bag should still contain 'A'", bag.contains("A"));
        assertEquals("Bag should have 2 items", 2, bag.size());
        assertEquals("Bag should have 1 'A'", 1, bag.getCount("A"));
    }

    public void testIteratorFail() {
        Bag bag = makeBag();
        bag.add("A");
        bag.add("A");
        bag.add("B");
        Iterator it = bag.iterator();
        it.next();
        bag.remove("A");
        try {
            it.next();
            fail("Should throw ConcurrentModificationException");
        } catch (ConcurrentModificationException e) {
            // expected
        }
    }
    
    public void testIteratorFailNoMore() {
        Bag bag = makeBag();
        bag.add("A");
        bag.add("A");
        bag.add("B");
        Iterator it = bag.iterator();
        it.next();
        it.next();
        it.next();
        try {
            it.next();
            fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException ex) {
            // expected
        }
    }
    
    public void testIteratorFailDoubleRemove() {
        Bag bag = makeBag();
        bag.add("A");
        bag.add("A");
        bag.add("B");
        Iterator it = bag.iterator();
        it.next();
        it.next();
        assertEquals(3, bag.size());
        it.remove();
        assertEquals(2, bag.size());
        try {
            it.remove();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
        assertEquals(2, bag.size());
        it.next();
        it.remove();
        assertEquals(1, bag.size());
    }
    
    public void testToArray() {
        Bag bag = makeBag();
        bag.add("A");
        bag.add("A");
        bag.add("B");
        bag.add("B");
        bag.add("C");
        Object[] array = bag.toArray();
        int a = 0, b = 0, c = 0;
        for (int i = 0; i < array.length; i++) {
            a += (array[i].equals("A") ? 1 : 0);
            b += (array[i].equals("B") ? 1 : 0);
            c += (array[i].equals("C") ? 1 : 0);
        }
        assertEquals(2, a);
        assertEquals(2, b);
        assertEquals(1, c);
    }

    public void testToArrayPopulate() {
        Bag bag = makeBag();
        bag.add("A");
        bag.add("A");
        bag.add("B");
        bag.add("B");
        bag.add("C");
        String[] array = (String[]) bag.toArray(new String[0]);
        int a = 0, b = 0, c = 0;
        for (int i = 0; i < array.length; i++) {
            a += (array[i].equals("A") ? 1 : 0);
            b += (array[i].equals("B") ? 1 : 0);
            c += (array[i].equals("C") ? 1 : 0);
        }
        assertEquals(2, a);
        assertEquals(2, b);
        assertEquals(1, c);
    }

    //-----------------------------------------------------------------------
    public void testEmptyBagSerialization() throws IOException, ClassNotFoundException {
        Bag bag = makeBag();
        if (!(bag instanceof Serializable)) return;
        
        byte[] objekt = writeExternalFormToBytes((Serializable) bag);
        Bag bag2 = (Bag) readExternalFormFromBytes(objekt);

        assertEquals("Bag should be empty",0, bag.size());
        assertEquals("Bag should be empty",0, bag2.size());
    }

    public void testFullBagSerialization() throws IOException, ClassNotFoundException {
        Bag bag = makeBag();
        bag.add("A");
        bag.add("A");
        bag.add("B");
        bag.add("B");
        bag.add("C");
        int size = bag.size();
        if (!(bag instanceof Serializable)) return;
        
        byte[] objekt = writeExternalFormToBytes((Serializable) bag);
        Bag bag2 = (Bag) readExternalFormFromBytes(objekt);

        assertEquals("Bag should be same size", size, bag.size());
        assertEquals("Bag should be same size", size, bag2.size());
    }

    /**
     * Compare the current serialized form of the Bag
     * against the canonical version in CVS.
     */
    public void testEmptyBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        Bag bag = makeBag();
        if(bag instanceof Serializable && !skipSerializedCanonicalTests()) {
            Bag bag2 = (Bag) readExternalFormFromDisk(getCanonicalEmptyCollectionName(bag));
            assertTrue("Bag is empty",bag2.size()  == 0);
            assertEquals(bag, bag2);
        }
    }

    /**
     * Compare the current serialized form of the Bag
     * against the canonical version in CVS.
     */
    public void testFullBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        Bag bag = makeBag();
        bag.add("A");
        bag.add("A");
        bag.add("B");
        bag.add("B");
        bag.add("C");
        if(bag instanceof Serializable && !skipSerializedCanonicalTests()) {
            Bag bag2 = (Bag) readExternalFormFromDisk(getCanonicalFullCollectionName(bag));
            assertEquals("Bag is the right size",bag.size(), bag2.size());
            assertEquals(bag, bag2);
        }
    }
}
