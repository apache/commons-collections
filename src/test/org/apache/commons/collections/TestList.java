/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestList.java,v 1.8 2002/02/26 18:45:46 morgand Exp $
 * $Revision: 1.8 $
 * $Date: 2002/02/26 18:45:46 $
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

package org.apache.commons.collections;

import junit.framework.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

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
 * @author Rodney Waldhoff
 * @version $Id: TestList.java,v 1.8 2002/02/26 18:45:46 morgand Exp $
 */
public abstract class TestList extends TestCollection {
    public TestList(String testName) {
        super(testName);
    }

    /**
     * Return a new, empty {@link List} to used for testing.
     */
    public abstract List makeEmptyList();

    public List makeFullList() {
        // only works if list supports optional "add(Object)" 
        // and "add(int,Object)" operations
        List list = makeEmptyList();
        list.add("1");
        // must be able to add to the end this way
        list.add(list.size(),"4");
        // must support duplicates
        list.add("1");
        // must support insertions
        list.add(1,"3");

        // resultant list: 1, 3, 4, 1
        return list;
    }

    public Collection makeCollection() {
        return makeEmptyList();
    }

    public void testListAddByIndexBoundsChecking() {
        List list = makeEmptyList();

        try {
            list.add(Integer.MIN_VALUE,"element");
            fail("List.add should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException e) {
            // expected
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(-1,"element");
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException e) {
            // expected
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(1,"element");
            fail("List.add should throw IndexOutOfBoundsException [1]");
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException e) {
            // expected
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(Integer.MAX_VALUE,"element");
            fail("List.add should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException e) {
            // expected
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListAddByIndexBoundsChecking2() {
        List list = makeEmptyList();
        boolean added = tryToAdd(list,"element");

        try {
            list.add(-1,"element2");
            fail("List.add should throw IndexOutOfBoundsException [-1]");
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException e) {
            // expected
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.add(2,"element2");
            fail("List.add should throw IndexOutOfBoundsException [2]");
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException e) {
            // expected
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListAddByIndex() {
        List list = makeEmptyList();
        assertEquals(0,list.size());
        if(tryToAdd(list,0,"element2")) {
            assertEquals(1,list.size());
            if(tryToAdd(list,0,"element0")) {
                assertTrue(Arrays.equals(new String[] { "element0", "element2" },list.toArray()));
                if(tryToAdd(list,1,"element1")) {
                    assertTrue(Arrays.equals(new String[] { "element0", "element1", "element2" },list.toArray()));
                    if(tryToAdd(list,4,"element3")) {
                        assertTrue(Arrays.equals(new String[] { "element0", "element1", "element2", "element3" },list.toArray()));
                    }
                }
            }
        }
    }

    public void testListAdd() {
        List list = makeEmptyList();
        if(tryToAdd(list,"1")) {
            assertTrue(list.contains("1"));
            if(tryToAdd(list,"2")) {
                assertTrue(list.contains("1"));
                assertTrue(list.contains("2"));
                if(tryToAdd(list,"3")) {
                    assertTrue(list.contains("1"));
                    assertTrue(list.contains("2"));
                    assertTrue(list.contains("3"));
                    if(tryToAdd(list,"4")) {
                        assertTrue(list.contains("1"));
                        assertTrue(list.contains("2"));
                        assertTrue(list.contains("3"));
                        assertTrue(list.contains("4"));
                    }
                }
            }
        }
    }

    public void testListEqualsSelf() {
        List list = makeEmptyList();
        assertTrue(list.equals(list));
        tryToAdd(list,"elt");
        assertTrue(list.equals(list));
        tryToAdd(list,"elt2");
        assertTrue(list.equals(list));
    }

    public void testListEqualsArrayList() {
        List list1 = makeEmptyList();
        List list2 = new ArrayList();
        assertTrue(list1.equals(list2));
        assertEquals(list1.hashCode(),list2.hashCode());
        tryToAdd(list1,"a");
        assertTrue(!list1.equals(list2));
        tryToAdd(list1,"b");
        tryToAdd(list1,"c");
        tryToAdd(list1,"d");
        tryToAdd(list1,"b");

        Iterator it = list1.iterator();
        while(it.hasNext()) {
            list2.add(it.next());
        }
        assertTrue(list1.equals(list2));
        assertEquals(list1.hashCode(),list2.hashCode());
    }

    public void testListEquals() {
        List list1 = makeEmptyList();
        List list2 = makeEmptyList();
        assertTrue(list1.equals(list2));
        if(tryToAdd(list1,"a") && tryToAdd(list2,"a")) {
            assertTrue(list1.equals(list2));
            if(tryToAdd(list1,"b") && tryToAdd(list2,"b")) {
                assertTrue(list1.equals(list2));
                if(tryToAdd(list1,"c") && tryToAdd(list2,"c")) {
                    assertTrue(list1.equals(list2));
                    if(tryToAdd(list1,"b") && tryToAdd(list2,"b")) {
                        assertTrue(list1.equals(list2));
                    }
                }
            }
        }
    }

    public void testListGetByIndex() {
        List list = makeEmptyList();
        tryToAdd(list,"a");
        tryToAdd(list,"b");
        tryToAdd(list,"c");
        tryToAdd(list,"d");
        tryToAdd(list,"e");
        tryToAdd(list,"f");
        Object[] expected = list.toArray();
        for(int i=0;i<expected.length;i++) {
            assertEquals(expected[i],list.get(i));
        }
    }

    public void testListGetByIndexBoundsChecking() {
        List list = makeEmptyList();

        try {
            list.get(Integer.MIN_VALUE);
            fail("List.get should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
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
            fail("List.get should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListGetByIndexBoundsChecking2() {
        List list = makeEmptyList();
        boolean added = tryToAdd(list,"a");

        try {
            list.get(Integer.MIN_VALUE);
            fail("List.get should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
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
            list.get(1);
            fail("List.get should throw IndexOutOfBoundsException [1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(Integer.MAX_VALUE);
            fail("List.get should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListIndexOf() {
        List list = makeEmptyList();
        tryToAdd(list,"a");
        tryToAdd(list,"b");
        tryToAdd(list,"c");
        tryToAdd(list,"d");
        tryToAdd(list,"e");
        tryToAdd(list,"f");
        Object[] expected = list.toArray();
        for(int i=0;i<expected.length;i++) {
            assertEquals(i,list.indexOf(expected[i]));
        }
        assertEquals(-1,list.indexOf("g"));
    }

    public void testListLastIndexOf1() {
        List list = makeEmptyList();
        tryToAdd(list,"a");
        tryToAdd(list,"b");
        tryToAdd(list,"c");
        tryToAdd(list,"d");
        tryToAdd(list,"e");
        tryToAdd(list,"f");
        Object[] expected = list.toArray();
        for(int i=0;i<expected.length;i++) {
            assertEquals(i,list.lastIndexOf(expected[i]));
        }
        assertEquals(-1,list.indexOf("g"));
    }

    public void testListLastIndexOf2() {
        List list = makeEmptyList();
        tryToAdd(list,"a");
        tryToAdd(list,"b");
        tryToAdd(list,"c");
        tryToAdd(list,"d");
        tryToAdd(list,"e");
        tryToAdd(list,"f");
        tryToAdd(list,"a");
        tryToAdd(list,"b");
        tryToAdd(list,"c");
        tryToAdd(list,"d");
        tryToAdd(list,"e");
        tryToAdd(list,"f");
        Object[] expected = list.toArray();
        int lastIndexOfA = -1;
        int lastIndexOfB = -1;
        int lastIndexOfC = -1;
        int lastIndexOfD = -1;
        int lastIndexOfE = -1;
        int lastIndexOfF = -1;
        int lastIndexOfG = -1;
        for(int i=0;i<expected.length;i++) {
            if("a".equals(expected[i])) {
                lastIndexOfA = i;
            } else if("b".equals(expected[i])) {
                lastIndexOfB = i;
            } else if("c".equals(expected[i])) {
                lastIndexOfC = i;
            } else if("d".equals(expected[i])) {
                lastIndexOfD = i;
            } else if("e".equals(expected[i])) {
                lastIndexOfE = i;
            } else if("f".equals(expected[i])) {
                lastIndexOfF = i;
            } else if("g".equals(expected[i])) {
                lastIndexOfG = i;
            }
        }
        assertEquals(lastIndexOfA,list.lastIndexOf("a"));
        assertEquals(lastIndexOfB,list.lastIndexOf("b"));
        assertEquals(lastIndexOfC,list.lastIndexOf("c"));
        assertEquals(lastIndexOfD,list.lastIndexOf("d"));
        assertEquals(lastIndexOfE,list.lastIndexOf("e"));
        assertEquals(lastIndexOfF,list.lastIndexOf("f"));
        assertEquals(lastIndexOfG,list.lastIndexOf("g"));
    }

    public void testListSetByIndexBoundsChecking() {
        List list = makeEmptyList();

        try {
            list.set(Integer.MIN_VALUE,"a");
            fail("List.set should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException  e) {
            // expected
        }

        try {
            list.set(-1,"a");
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException  e) {
            // expected
        }

        try {
            list.set(0,"a");
            fail("List.set should throw IndexOutOfBoundsException [0]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException  e) {
            // expected
        }

        try {
            list.set(1,"a");
            fail("List.set should throw IndexOutOfBoundsException [1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException  e) {
            // expected
        }

        try {
            list.set(Integer.MAX_VALUE,"a");
            fail("List.set should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException  e) {
            // expected
        }
    }

    public void testListSetByIndexBoundsChecking2() {
        List list = makeEmptyList();
        tryToAdd(list,"element");
        tryToAdd(list,"element2");

        try {
            list.set(Integer.MIN_VALUE,"a");
            fail("List.set should throw IndexOutOfBoundsException [Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException  e) {
            // expected
        }

        try {
            list.set(-1,"a");
            fail("List.set should throw IndexOutOfBoundsException [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException  e) {
            // expected
        }

        try {
            list.set(2,"a");
            fail("List.set should throw IndexOutOfBoundsException [2]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException  e) {
            // expected
        }

        try {
            list.set(Integer.MAX_VALUE,"a");
            fail("List.set should throw IndexOutOfBoundsException [Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException  e) {
            // expected
        }
    }

    public void testListSetByIndex() {
        List list = makeEmptyList();
        tryToAdd(list,"element");
        tryToAdd(list,"element2");
        tryToAdd(list,"element3");
        tryToAdd(list,"element4");

        Object[] values = list.toArray();

        for(int i=0;i<values.length;i++) {
            try {
                Object obj = list.set(i,String.valueOf(i));
                assertEquals(obj,values[i]);
                values[i] = String.valueOf(i);
                assertTrue(Arrays.equals(values,list.toArray()));
            } catch(UnsupportedOperationException e) {
                // expected
            } catch(ClassCastException e) {
                // expected
            } catch(IllegalArgumentException  e) {
                // expected
            }
        }
    }

    public void testListRemoveByIndex() {
        List list = makeEmptyList();
        tryToAdd(list,"element");
        tryToAdd(list,"element2");
        tryToAdd(list,"element3");
        tryToAdd(list,"element4");
        tryToAdd(list,"element5");

        Object[] values = list.toArray();

        for(int i=1;i<values.length;i++) {
            try {
                Object obj = list.remove(1);
                assertEquals(obj,values[i]);
            } catch(UnsupportedOperationException e) {
                // expected
            }
        }

        if(values.length != 0) {
            try {
                Object obj = list.remove(0);
                assertEquals(obj,values[0]);
            } catch(UnsupportedOperationException e) {
                // expected
            }
        }
    }

    public void testListRemoveByValue() {
        List list = makeEmptyList();
        tryToAdd(list,"element1");
        tryToAdd(list,"element2");
        tryToAdd(list,"element3");
        tryToAdd(list,"element4");
        tryToAdd(list,"element5");

        Object[] values = list.toArray();

        for(int i=0;i<values.length;i++) {
            try {
                assertTrue(!list.remove("X"));
                assertTrue(list.contains(values[i]));
                assertTrue(list.remove(values[i]));
                assertTrue(!list.contains(values[i]));
            } catch(UnsupportedOperationException e) {
                // expected
            }
        }
    }

    public void testListListIteratorNextPrev() {
        List list = makeEmptyList();
        tryToAdd(list,"element1");
        tryToAdd(list,"element2");
        tryToAdd(list,"element3");
        tryToAdd(list,"element4");
        tryToAdd(list,"element5");
        Object[] values = list.toArray();
        ListIterator iter = list.listIterator();
        for(int i=0;i<values.length;i++) {
            assertTrue( iter.hasNext() );
            assertTrue((i!=0) == iter.hasPrevious());
            assertEquals(values[i],iter.next());
        }
        assertTrue(!iter.hasNext());
        for(int i=values.length-1;i>=0;i--) {
            assertTrue( iter.hasPrevious() );
            assertTrue((i!=(values.length-1)) == iter.hasNext());
            assertEquals(values[i],iter.previous());
        }
        assertTrue(!iter.hasPrevious());
        for(int i=0;i<values.length;i++) {
            assertTrue( iter.hasNext() );
            assertTrue((i!=0) == iter.hasPrevious());
            assertEquals(values[i],iter.next());
            assertEquals(values[i],iter.previous());
            assertEquals(values[i],iter.next());
        }
    }

    public void testListListIteratorNextIndexPrevIndex() {
        List list = makeEmptyList();
        tryToAdd(list,"element1");
        tryToAdd(list,"element2");
        tryToAdd(list,"element3");
        tryToAdd(list,"element4");
        tryToAdd(list,"element5");
        Object[] values = list.toArray();
        ListIterator iter = list.listIterator();
        for(int i=0;i<values.length;i++) {
            assertEquals("nextIndex should be " + i,i,iter.nextIndex());
            assertEquals("previousIndex should be " + (i-1),i-1,iter.previousIndex());
            assertEquals(values[i],iter.next());
        }
        assertTrue(!iter.hasNext());
        for(int i=values.length-1;i>=0;i--) {
            assertEquals("previousIndex should be " + i,i,iter.previousIndex());
            assertEquals("nextIndex should be " + (i+1),i+1,iter.nextIndex());
            assertEquals(values[i],iter.previous());
        }
    }

    public void testListListIteratorSet() {
        List list = makeEmptyList();
        tryToAdd(list,"element1");
        tryToAdd(list,"element2");
        tryToAdd(list,"element3");
        tryToAdd(list,"element4");
        tryToAdd(list,"element5");
        Object[] values = list.toArray();
        ListIterator iter = list.listIterator();

        try {
            iter.set("should fail");
            fail("ListIterator.set should fail when neither next nor previous has been called");
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(IllegalStateException e) {
            // expected
        } catch(ClassCastException e) {
            // expected
        } catch(IllegalArgumentException e) {
            // expected
        }

        for(int i=0;i<values.length;i++) {
            iter.next();
            try {
                iter.set(new Integer(i));
                values[i] = new Integer(i);
                assertTrue(Arrays.equals(values,list.toArray()));
            } catch(UnsupportedOperationException e) {
                // expected
            } catch(IllegalStateException e) {
                // expected
            } catch(ClassCastException e) {
                // expected
            } catch(IllegalArgumentException e) {
                // expected
            }
        }
        assertTrue(!iter.hasNext());
        for(int i=values.length-1;i>=0;i--) {
            iter.previous();
            try {
                iter.set(String.valueOf(i));
                values[i] = String.valueOf(i);
                assertTrue(Arrays.equals(values,list.toArray()));
            } catch(UnsupportedOperationException e) {
                // expected
            } catch(IllegalStateException e) {
                // expected
            } catch(ClassCastException e) {
                // expected
            } catch(IllegalArgumentException e) {
                // expected
            }
        }
    }

    /*

    public void testListListIterator() {
        // XXX finish me
    }

    public void testListListIteratorByIndex() {
        // XXX finish me
    }


    public void testListSubList() {
        // XXX finish me
    }

    */

    private boolean tryToAdd(List list, int index, Object obj) {
        try {
            list.add(index,obj);
            return true;
        } catch(UnsupportedOperationException e) {
            return false;
        } catch(ClassCastException e) {
            return false;
        } catch(IllegalArgumentException e) {
            return false;
        } catch(IndexOutOfBoundsException e) {
            return false;
        } catch(Throwable t) {
            t.printStackTrace();
            fail("List.add should only throw UnsupportedOperationException, ClassCastException, IllegalArgumentException, or IndexOutOfBoundsException. Found " + t.toString());
            return false; // never get here, since fail throws exception
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
        if (!(list instanceof Serializable)) return;
        
        byte[] objekt = writeExternalFormToBytes((Serializable) list);
        List list2 = (List) readExternalFormFromBytes(objekt);

        assertEquals("Both lists are same size",list.size(), 4);
        assertEquals("Both lists are same size",list2.size(),4);
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
        if (!(makeEmptyList() instanceof Serializable)) return;
        List list = (List) readExternalFormFromDisk(getCanonicalEmptyCollectionName(makeEmptyList()));
        assertTrue("List is empty",list.size()  == 0);
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
        if (!(makeFullList() instanceof Serializable)) return;
        List list = (List) readExternalFormFromDisk(getCanonicalFullCollectionName(makeFullList()));
        assertEquals("List is the right size",list.size(), 4);
    }

}
