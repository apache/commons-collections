/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestList.java,v 1.16 2003/02/26 01:33:22 rwaldhoff Exp $
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Tests base {@link java.util.List} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeList} method.
 * <p>
 * If your {@link List} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link List} fails.
 *
 * @version $Revision: 1.16 $ $Date: 2003/02/26 01:33:22 $
 * 
 * @author Rodney Waldhoff
 * @author Paul Jack
 * @author Stephen Colebourne
 */
public abstract class TestList extends TestCollection {


    public TestList(String testName) {
        super(testName);
    }


    /**
     *  Return a new, empty {@link List} to be used for testing.
     *
     *  @return an empty list for testing.
     */
    protected abstract List makeEmptyList();


    /**
     *  Return a new, full {@link List} to be used for testing.
     *
     *  @return a full list for testing
     */
    protected List makeFullList() {
        // only works if list supports optional "addAll(Collection)" 
        List list = makeEmptyList();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }


    /**
     *  Returns {@link makeEmptyList()}.
     *
     *  @return an empty list to be used for testing
     */
    final protected Collection makeCollection() {
        return makeEmptyList();
    }


    /**
     *  Returns {@link makeFullList()}.
     *
     *  @return a full list to be used for testing
     */
    final protected Collection makeFullCollection() {
        return makeFullList();
    }


    /**
     *  Returns true if the collections produced by 
     *  {@link #makeCollection()} and {@link #makeFullCollection()}
     *  support the <code>set</code> operation.<p>
     *  Default implementation returns true.  Override if your collection
     *  class does not support set.
     */
    protected boolean isSetSupported() {
        return true;
    }


    /**
     *  Returns the {@link collection} field cast to a {@link List}.
     *
     *  @return the collection field as a List
     */
    protected List getList() {
        return (List)collection;
    } 


    /**
     *  Returns the {@link confirmed} field cast to a {@link List}.
     *
     *  @return the confirmed field as a List
     */
    protected List getConfirmedList() {
        return (List)confirmed;
    }


    /**
     *  Tests bounds checking for {@link List#add(int, Object)} on an
     *  empty list.
     */
    public void testListAddByIndexBoundsChecking() {
        if (!isAddSupported()) return;

        List list = makeEmptyList();
        Object element = getOtherElements()[0];

        try {
            list.add(Integer.MIN_VALUE, element);
            fail("List.add should throw IndexOutOfBoundsException " +
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(-1, element);
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(1, element);
            fail("List.add should throw IndexOutOfBoundsException [1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(Integer.MAX_VALUE, element);
            fail("List.add should throw IndexOutOfBoundsException " + 
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }


    /**
     *  Tests bounds checking for {@link List#add(int, Object)} on a
     *  full list.
     */
    public void testListAddByIndexBoundsChecking2() {
        if (!isAddSupported()) return;

        List list = makeFullList();
        Object element = getOtherElements()[0];

        try {
            list.add(Integer.MIN_VALUE, element);
            fail("List.add should throw IndexOutOfBoundsException " +
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(-1, element);
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(list.size() + 1, element);
            fail("List.add should throw IndexOutOfBoundsException [size + 1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(Integer.MAX_VALUE, element);
            fail("List.add should throw IndexOutOfBoundsException " + 
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

    }


    /**
     *  Tests {@link List#add(int,Object)}.
     */
    public void testListAddByIndex() {
        if (!isAddSupported()) return;

        Object element = getOtherElements()[0];
        int max = getFullElements().length;

        for (int i = 0; i <= max; i++) {
            resetFull();
            ((List)collection).add(i, element);
            ((List)confirmed).add(i, element);
            verify();
        }
    }


    /**
     *  Tests {@link List#equals(Object)}.
     */
    public void testListEquals() {
        resetEmpty();
        List list = getList();
        assertTrue("Empty lists should be equal", list.equals(confirmed));
        verify();
        assertTrue("Empty list should equal self", list.equals(list));
        verify();

        List list2 = Arrays.asList(getFullElements());
        assertTrue("Empty list shouldn't equal full", !list.equals(list2));
        verify();

        list2 = Arrays.asList(getOtherElements());
        assertTrue("Empty list shouldn't equal other", !list.equals(list2));
        verify();

        resetFull();
        list = getList();
        assertTrue("Full lists should be equal", list.equals(confirmed));
        verify();
        assertTrue("Full list should equal self", list.equals(list));
        verify();

        list2 = makeEmptyList();
        assertTrue("Full list shouldn't equal empty", !list.equals(list2));
        verify();

        list2 = Arrays.asList(getOtherElements());
        assertTrue("Full list shouldn't equal other", !list.equals(list2));
        verify();

        list2 = Arrays.asList(getFullElements());
        Collections.reverse(list2);
        assertTrue("Full list shouldn't equal full list with same elements" +
          " but different order", !list.equals(list2));
        verify();

        assertTrue("List shouldn't equal String", !list.equals(""));
        verify();

        final List listForC = Arrays.asList(getFullElements());
        Collection c = new AbstractCollection() {
            public int size() {
                return listForC.size();
            }

            public Iterator iterator() {
                return listForC.iterator();
            }
        };

        assertTrue("List shouldn't equal nonlist with same elements " +
          " in same order", !list.equals(c));
        verify();
    }


    /**
     *  Tests {@link List#hashCode()}.
     */
    public void testListHashCode() {
        resetEmpty();
        int hash1 = collection.hashCode();
        int hash2 = confirmed.hashCode();
        assertEquals("Empty lists should have equal hashCodes", hash1, hash2);
        verify();

        resetFull();
        hash1 = collection.hashCode();
        hash2 = confirmed.hashCode();
        assertEquals("Full lists should have equal hashCodes", hash1, hash2);
        verify();
    }


    /**
     *  Tests {@link List#get(int)}.
     */
    public void testListGetByIndex() {
        resetFull();
        List list = getList();
        Object[] elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            assertEquals("List should contain correct elements", 
              elements[i], list.get(i));
            verify();
        }
    }


    /**
     *  Tests bounds checking for {@link List#get(int)} on an
     *  empty list.
     */
    public void testListGetByIndexBoundsChecking() {
        List list = makeEmptyList();

        try {
            list.get(Integer.MIN_VALUE);
            fail("List.get should throw IndexOutOfBoundsException " +
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(-1);
            fail("List.get should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(0);
            fail("List.get should throw IndexOutOfBoundsException [0]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(1);
            fail("List.get should throw IndexOutOfBoundsException [1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(Integer.MAX_VALUE);
            fail("List.get should throw IndexOutOfBoundsException " +
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }


    /**
     *  Tests bounds checking for {@link List#get(int)} on a
     *  full list.
     */
    public void testListGetByIndexBoundsChecking2() {
        List list = makeFullList();

        try {
            list.get(Integer.MIN_VALUE);
            fail("List.get should throw IndexOutOfBoundsException " + 
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(-1);
            fail("List.get should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(getFullElements().length);
            fail("List.get should throw IndexOutOfBoundsException [size]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(Integer.MAX_VALUE);
            fail("List.get should throw IndexOutOfBoundsException " + 
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }


    /**
     *  Tests {@link List#indexOf()}.
     */
    public void testListIndexOf() {
        resetFull();
        List list1 = getList();
        List list2 = getConfirmedList();

        Iterator iterator = list2.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();
            assertEquals("indexOf should return correct result", 
              list1.indexOf(element), list2.indexOf(element));            
            verify();
        }

        Object[] other = getOtherElements();
        for (int i = 0; i < other.length; i++) {
            assertEquals("indexOf should return -1 for nonexistent element",
              list1.indexOf(other[i]), -1);
            verify();
        }
    }


    /**
     *  Tests {@link List#lastIndexOf()}.
     */
    public void testListLastIndexOf() {
        resetFull();
        List list1 = getList();
        List list2 = getConfirmedList();

        Iterator iterator = list2.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();
            assertEquals("lastIndexOf should return correct result",
              list1.lastIndexOf(element), list2.lastIndexOf(element));
            verify();
        }

        Object[] other = getOtherElements();
        for (int i = 0; i < other.length; i++) {
            assertEquals("lastIndexOf should return -1 for nonexistent " +
              "element", list1.lastIndexOf(other[i]), -1);
            verify();
        }
    }


    /**
     *  Tests bounds checking for {@link List#set(int,Object)} on an
     *  empty list.
     */
    public void testListSetByIndexBoundsChecking() {
        if (!isSetSupported()) return;

        List list = makeEmptyList();
        Object element = getOtherElements()[0];

        try {
            list.set(Integer.MIN_VALUE, element);
            fail("List.set should throw IndexOutOfBoundsException " +
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.set(-1, element);
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.set(0, element);
            fail("List.set should throw IndexOutOfBoundsException [0]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.set(1, element);
            fail("List.set should throw IndexOutOfBoundsException [1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.set(Integer.MAX_VALUE, element);
            fail("List.set should throw IndexOutOfBoundsException " +
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }


    /**
     *  Tests bounds checking for {@link List#set(int,Object)} on a
     *  full list.
     */
    public void testListSetByIndexBoundsChecking2() {
        if (!isSetSupported()) return;

        List list = makeFullList();
        Object element = getOtherElements()[0];

        try {
            list.set(Integer.MIN_VALUE, element);
            fail("List.set should throw IndexOutOfBoundsException " +
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.set(-1, element);
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.set(getFullElements().length, element);
            fail("List.set should throw IndexOutOfBoundsException [size]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.set(Integer.MAX_VALUE, element);
            fail("List.set should throw IndexOutOfBoundsException " +
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 
    }


    /**
     *  Test {@link List#set(int,Object)}.
     */
    public void testListSetByIndex() {
        if (!isSetSupported()) return;

        resetFull();
        Object[] elements = getFullElements();
        Object[] other = getOtherElements();

        for (int i = 0; i < elements.length; i++) {
            Object n = other[i % other.length];
            Object v = ((List)collection).set(i, n);
            assertEquals("Set should return correct element", elements[i], v);
            ((List)confirmed).set(i, n);
            verify();
        }
    }


    /**
     *  If {@link #isSetSupported()} returns false, tests that set operation
     *  raises <Code>UnsupportedOperationException.
     */
    public void testUnsupportedSet() {
        if (isSetSupported()) return;
        
        resetFull();
        try {
            ((List) collection).set(0, new Object());
            fail("Emtpy collection should not support set.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();
    }
    

    /**
     *  Tests bounds checking for {@link List#remove(int)} on an
     *  empty list.
     */
    public void testListRemoveByIndexBoundsChecking() {
        if (!isRemoveSupported()) return;

        List list = makeEmptyList();

        try {
            list.remove(Integer.MIN_VALUE);
            fail("List.remove should throw IndexOutOfBoundsException " +
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.remove(-1);
            fail("List.remove should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.remove(0);
            fail("List.remove should throw IndexOutOfBoundsException [0]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.remove(1);
            fail("List.remove should throw IndexOutOfBoundsException [1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.remove(Integer.MAX_VALUE);
            fail("List.remove should throw IndexOutOfBoundsException " +
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }


    /**
     *  Tests bounds checking for {@link List#remove(int)} on a
     *  full list.
     */
    public void testListRemoveByIndexBoundsChecking2() {
        if (!isRemoveSupported()) return;

        List list = makeFullList();

        try {
            list.remove(Integer.MIN_VALUE);
            fail("List.remove should throw IndexOutOfBoundsException " +
              "[Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.remove(-1);
            fail("List.remove should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.remove(getFullElements().length);
            fail("List.remove should throw IndexOutOfBoundsException [size]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 

        try {
            list.remove(Integer.MAX_VALUE);
            fail("List.remove should throw IndexOutOfBoundsException " +
              "[Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } 
    }


    /**
     *  Tests {@link List#remove(int)}.
     */
    public void testListRemoveByIndex() {
        if (!isRemoveSupported()) return;

        int max = getFullElements().length;
        for (int i = 0; i < max; i++) {
            resetFull();
            Object o1 = ((List)collection).remove(i);
            Object o2 = ((List)confirmed).remove(i);
            assertEquals("remove should return correct element", o1, o2);
            verify();
        }
    }


    /**
     *  Tests the read-only bits of {@link List#listIterator()}.
     */
    public void testListListIterator() {
        resetFull();
        forwardTest(getList().listIterator(), 0);
        backwardTest(getList().listIterator(), 0);
    }


    /**
     *  Tests the read-only bits of {@link List#listIterator(int)}.
     */
    public void testListListIteratorByIndex() {
        resetFull();
        for (int i = 0; i < confirmed.size(); i++) {
            forwardTest(getList().listIterator(i), i);
            backwardTest(getList().listIterator(i), i);
        }
    }


    /**
     *  Traverses to the end of the given iterator.
     *
     *  @param iter  the iterator to traverse
     *  @param i     the starting index
     */
    private void forwardTest(ListIterator iter, int i) {
        List list = getList();
        int max = getFullElements().length;

        while (i < max) {
            assertTrue("Iterator should have next", iter.hasNext());
            assertEquals("Iterator.nextIndex should work", 
              iter.nextIndex(), i);
            assertEquals("Iterator.previousIndex should work",
              iter.previousIndex(), i - 1);
            Object o = iter.next();
            assertEquals("Iterator returned correct element", list.get(i), o);
            i++;
        }

        assertTrue("Iterator shouldn't have next", !iter.hasNext());
        assertEquals("nextIndex should be size", iter.nextIndex(), max);
        assertEquals("previousIndex should be size - 1", 
          iter.previousIndex(), max - 1);

        try {
            iter.next();
            fail("Exhausted iterator should raise NoSuchElement");
        } catch (NoSuchElementException e) {
            // expected
        }
    }


    /**
     *  Traverses to the beginning of the given iterator.
     *
     *  @param iter  the iterator to traverse
     *  @param i     the starting index
     */
    private void backwardTest(ListIterator iter, int i) {
        List list = getList();

        while (i > 0) {
            assertTrue("Iterator should have next", iter.hasPrevious());
            assertEquals("Iterator.nextIndex should work", 
              iter.nextIndex(), i);
            assertEquals("Iterator.previousIndex should work",
              iter.previousIndex(), i - 1);
            Object o = iter.previous();
            assertEquals("Iterator returned correct element", 
              list.get(i - 1), o);
            i--;
        }

        assertTrue("Iterator shouldn't have previous", !iter.hasPrevious());
        assertEquals("nextIndex should be 0", iter.nextIndex(), 0);
        assertEquals("previousIndex should be -1", 
          iter.previousIndex(), -1);

        try {
            iter.previous();
            fail("Exhausted iterator should raise NoSuchElement");
        } catch (NoSuchElementException e) {
            // expected
        }

    }


    /**
     *  Tests the {@link ListIterator#add(Object)} method of the list
     *  iterator.
     */
    public void testListIteratorAdd() {
        if (!isAddSupported()) return;

        resetEmpty();
        List list1 = getList();
        List list2 = getConfirmedList();

        Object[] elements = getFullElements();
        ListIterator iter1 = list1.listIterator();
        ListIterator iter2 = list2.listIterator();

        for (int i = 0; i < elements.length; i++) {
            iter1.add(elements[i]);
            iter2.add(elements[i]);
            verify();
        }

        resetFull();
        iter1 = getList().listIterator();
        iter2 = getConfirmedList().listIterator();
        for (int i = 0; i < elements.length; i++) {
            iter1.next();
            iter2.next();
            iter1.add(elements[i]);
            iter2.add(elements[i]);
            verify();
        }
    }


    /**
     *  Tests the {@link ListIterator#set(Object)} method of the list
     *  iterator.
     */
    public void testListIteratorSet() {
        if (!isSetSupported()) return;

        Object[] elements = getFullElements();

        resetFull();
        ListIterator iter1 = getList().listIterator();
        ListIterator iter2 = getConfirmedList().listIterator();
        for (int i = 0; i < elements.length; i++) {
            iter1.next();
            iter2.next();
            iter1.set(elements[i]);
            iter2.set(elements[i]);
            verify();
        }
    }


    public void testEmptyListSerialization() 
    throws IOException, ClassNotFoundException {
        List list = makeEmptyList();
        if (!(list instanceof Serializable)) return;
        
        byte[] objekt = writeExternalFormToBytes((Serializable) list);
        List list2 = (List) readExternalFormFromBytes(objekt);

        assertTrue("Both lists are empty",list.size()  == 0);
        assertTrue("Both lists are empty",list2.size() == 0);
    }

    public void testFullListSerialization() 
    throws IOException, ClassNotFoundException {
        List list = makeFullList();
        int size = getFullElements().length;
        if (!(list instanceof Serializable)) return;
        
        byte[] objekt = writeExternalFormToBytes((Serializable) list);
        List list2 = (List) readExternalFormFromBytes(objekt);

        assertEquals("Both lists are same size",list.size(), size);
        assertEquals("Both lists are same size",list2.size(), size);
    }

    /**
     * Compare the current serialized form of the List
     * against the canonical version in CVS.
     */
    public void testEmptyListCompatibility() throws IOException, ClassNotFoundException {
        /**
         * Create canonical objects with this code
        List list = makeEmptyList();
        if (!(list instanceof Serializable)) return;
        
        writeExternalFormToDisk((Serializable) list, getCanonicalEmptyCollectionName(list));
        */

        // test to make sure the canonical form has been preserved
        List list = makeEmptyList();
        if(list instanceof Serializable && !skipSerializedCanonicalTests()) {
            List list2 = (List) readExternalFormFromDisk(getCanonicalEmptyCollectionName(list));
            assertTrue("List is empty",list2.size()  == 0);
        }
    }

    /**
     * Compare the current serialized form of the List
     * against the canonical version in CVS.
     */
    public void testFullListCompatibility() throws IOException, ClassNotFoundException {
        /**
         * Create canonical objects with this code
        List list = makeFullList();
        if (!(list instanceof Serializable)) return;
        
        writeExternalFormToDisk((Serializable) list, getCanonicalFullCollectionName(list));
        */

        // test to make sure the canonical form has been preserved
        List list = makeFullList();
        if(list instanceof Serializable && !skipSerializedCanonicalTests()) {
            List list2 = (List) readExternalFormFromDisk(getCanonicalFullCollectionName(list));
            assertEquals("List is the right size",list2.size(), 4);
        }
    }


    /**
     *  Returns an empty {@link ArrayList}.
     */
    protected Collection makeConfirmedCollection() {
        ArrayList list = new ArrayList();
        return list;
    }


    /**
     *  Returns a full {@link ArrayList}.
     */
    protected Collection makeConfirmedFullCollection() {
        ArrayList list = new ArrayList();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }


    /**
     *  Verifies that the test list implementation matches the confirmed list
     *  implementation.
     */
    protected void verify() {
        super.verify();

        List list1 = getList();
        List list2 = getConfirmedList();

        assertEquals("List should equal confirmed", list1, list2);
        assertEquals("Confirmed should equal list", list2, list1);

        assertEquals("Hash codes should be equal", 
          list1.hashCode(), list2.hashCode());

        int i = 0;
        Iterator iterator1 = list1.iterator();
        Iterator iterator2 = list2.iterator();
        Object[] array = list1.toArray();
        while (iterator2.hasNext()) {
            assertTrue("List iterator should have next", iterator1.hasNext());
            Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            assertEquals("Iterator elements should be equal", o1, o2);
            o2 = list1.get(i);
            assertEquals("get should return correct element", o1, o2);
            o2 = array[i];
            assertEquals("toArray should have correct element", o1, o2);
            i++;
        }
    }


    /**
     *  Returns a {@link BulkTest} for testing {@link List#subList(int,int)}.
     *  The returned bulk test will run through every <Code>TestList</Code>
     *  method, <I>including</I> another <Code>bulkTestSubList</Code>.
     *  Sublists are tested until the size of the sublist is less than 10.
     *  Each sublist is 6 elements smaller than its parent list.
     *  (By default this means that two rounds of sublists will be tested).
     *  The verify() method is overloaded to test that the original list is
     *  modified when the sublist is.
     */
    public BulkTest bulkTestSubList() {
        if (getFullElements().length - 6 < 10) return null;
        return new BulkTestSubList(this);
    }


   static class BulkTestSubList extends TestList {

       private TestList outer;


       BulkTestSubList(TestList outer) {
           super("");
           this.outer = outer;
       }


       protected Object[] getFullElements() {
           List l = Arrays.asList(outer.getFullElements());
           return l.subList(3, l.size() - 3).toArray();
       }


       protected Object[] getOtherElements() {
           return outer.getOtherElements();
       }


       protected boolean isAddSupported() {
           return outer.isAddSupported();
       }


       protected boolean isRemoveSupported() {
           return outer.isRemoveSupported();
       }


       protected List makeEmptyList() { 
           return outer.makeFullList().subList(4, 4); 
       }


       protected List makeFullList() {
           int size = getFullElements().length;
           return outer.makeFullList().subList(3, size - 3);
       }


       protected void resetEmpty() {
           outer.resetFull();
           this.collection = outer.getList().subList(4, 4);
           this.confirmed = outer.getConfirmedList().subList(4, 4);
       }

       protected void resetFull() {
           outer.resetFull();
           int size = outer.confirmed.size();
           this.collection = outer.getList().subList(3, size - 3);
           this.confirmed = outer.getConfirmedList().subList(3, size - 3);
       }


       protected void verify() {
           super.verify();
           outer.verify();
       }

   }


   /**
    *  Tests that a sublist raises a {@link java.util.ConcurrentModificationException ConcurrentModificationException}
    *  if elements are added to the original list.
    */
   public void testListSubListFailFastOnAdd() {
       if (!isAddSupported()) return;

       resetFull();
       int size = collection.size();
       List sub = getList().subList(1, size);
       getList().add(getOtherElements()[0]);
       failFastAll(sub);

       resetFull();
       sub = getList().subList(1, size);
       getList().add(0, getOtherElements()[0]);
       failFastAll(sub);

       resetFull();
       sub = getList().subList(1, size);
       getList().addAll(Arrays.asList(getOtherElements()));
       failFastAll(sub);

       resetFull();
       sub = getList().subList(1, size);
       getList().addAll(0, Arrays.asList(getOtherElements()));
       failFastAll(sub);

   }


   /**
    *  Tests that a sublist raises a {@link java.util.ConcurrentModificationException ConcurrentModificationException}
    *  if elements are removed from the original list.
    */
   public void testListSubListFailFastOnRemove() {
       if (!isRemoveSupported()) return;

       resetFull();
       int size = collection.size();
       List sub = getList().subList(1, size);
       getList().remove(0);
       failFastAll(sub);

       resetFull();
       sub = getList().subList(1, size);
       getList().remove(getFullElements()[2]);
       failFastAll(sub);

       resetFull();
       sub = getList().subList(1, size);
       getList().removeAll(Arrays.asList(getFullElements()));
       failFastAll(sub);

       resetFull();
       sub = getList().subList(1, size);
       getList().retainAll(Arrays.asList(getOtherElements()));
       failFastAll(sub);

       resetFull();
       sub = getList().subList(1, size);
       getList().clear();
       failFastAll(sub);
   }


   /**
    *  Invokes all the methods on the given sublist to make sure they raise
    *  a {@link java.util.ConcurrentModificationException ConcurrentModificationException}.
    */
   protected void failFastAll(List list) {
       Method[] methods = List.class.getMethods();
       for (int i = 0; i < methods.length; i++) {
           failFastMethod(list, methods[i]);
       }
   }


   /**
    *  Invokes the given method on the given sublist to make sure it raises
    *  a {@link java.util.ConcurrentModificationException ConcurrentModificationException}.
    *
    *  Unless the method happens to be the equals() method, in which case
    *  the test is skipped.  There seems to be a bug in
    *  java.util.AbstractList.subList(int,int).equals(Object) -- it never
    *  raises a ConcurrentModificationException.
    *
    *  @param list  the sublist to test
    *  @param m     the method to invoke
    */
   protected void failFastMethod(List list, Method m) {
       if (m.getName().equals("equals")) return;

       Object element = getOtherElements()[0];
       Collection c = Collections.singleton(element);

       Class[] types = m.getParameterTypes();
       Object[] params = new Object[types.length];
       for (int i = 0; i < params.length; i++) {
           if (types[i] == Integer.TYPE) params[i] = new Integer(0);
           else if (types[i] == Collection.class) params[i] = c;
           else if (types[i] == Object.class) params[i] = element;
           else if (types[i] == Object[].class) params[i] = new Object[0];
       }

       try {
           m.invoke(list, params);
           fail(m.getName() + " should raise ConcurrentModification");
       } catch (IllegalAccessException e) {
           // impossible
       } catch (InvocationTargetException e) {
           Throwable t = e.getTargetException();
           if (t instanceof ConcurrentModificationException) {
               // expected
               return;
           } else {
               fail(m.getName() + " raised unexpected " + e);
           }
       }
   }

}
