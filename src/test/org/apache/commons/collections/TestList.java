/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestList.java,v 1.3 2001/04/26 00:06:00 rwaldhoff Exp $
 * $Revision: 1.3 $
 * $Date: 2001/04/26 00:06:00 $
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
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

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
 * @version $Id: TestList.java,v 1.3 2001/04/26 00:06:00 rwaldhoff Exp $
 */
public abstract class TestList extends TestCollection {
    public TestList(String testName) {
        super(testName);
    }

    /**
     * Return a new, empty {@link List} to used for testing.
     */
    public abstract List makeList();

    public Collection makeCollection() {
        return makeList();
    }

    public void testListAddByIndexBoundsChecking() {
        List list = makeList();

        try {
            list.add(Integer.MIN_VALUE,"element");
            fail("Shouldn't get here [Integer.MIN_VALUE]");
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
            fail("Shouldn't get here [-1]");
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
            fail("Shouldn't get here [1]");
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
            fail("Shouldn't get here [Integer.MAX_VALUE]");
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
        List list = makeList();
        boolean added = tryToAdd(list,"element");

        try {
            list.add(-1,"element2");
            fail("Shouldn't get here [-1]");
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
            fail("Shouldn't get here [2]");
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
        List list = makeList();
        assertEquals(0,list.size());
        if(tryToAdd(list,0,"element2")) {
            assertEquals(1,list.size());
            if(tryToAdd(list,0,"element0")) {
                assert(Arrays.equals(new String[] { "element0", "element2" },list.toArray()));
                if(tryToAdd(list,1,"element1")) {
                    assert(Arrays.equals(new String[] { "element0", "element1", "element2" },list.toArray()));
                    if(tryToAdd(list,4,"element3")) {
                        assert(Arrays.equals(new String[] { "element0", "element1", "element2", "element3" },list.toArray()));
                    }
                }
            }
        }
    }

    public void testListAdd() {
        List list = makeList();
        if(tryToAdd(list,"1")) {
            assert(list.contains("1"));
            if(tryToAdd(list,"2")) {
                assert(list.contains("1"));
                assert(list.contains("2"));
                if(tryToAdd(list,"3")) {
                    assert(list.contains("1"));
                    assert(list.contains("2"));
                    assert(list.contains("3"));
                    if(tryToAdd(list,"4")) {
                        assert(list.contains("1"));
                        assert(list.contains("2"));
                        assert(list.contains("3"));
                        assert(list.contains("4"));
                    }
                }
            }
        }
    }

    public void testListEqualsSelf() {
        List list = makeList();
        assert(list.equals(list));
        tryToAdd(list,"elt");
        assert(list.equals(list));
        tryToAdd(list,"elt2");
        assert(list.equals(list));
    }

    public void testListEqualsArrayList() {
        List list1 = makeList();
        List list2 = new ArrayList();
        assert(list1.equals(list2));
        assertEquals(list1.hashCode(),list2.hashCode());
        tryToAdd(list1,"a");
        assert(!list1.equals(list2));
        tryToAdd(list1,"b");
        tryToAdd(list1,"c");
        tryToAdd(list1,"d");
        tryToAdd(list1,"b");

        Iterator it = list1.iterator();
        while(it.hasNext()) {
            list2.add(it.next());
        }
        assert(list1.equals(list2));
        assertEquals(list1.hashCode(),list2.hashCode());
    }

    public void testListEquals() {
        List list1 = makeList();
        List list2 = makeList();
        assert(list1.equals(list2));
        if(tryToAdd(list1,"a") && tryToAdd(list2,"a")) {
            assert(list1.equals(list2));
            if(tryToAdd(list1,"b") && tryToAdd(list2,"b")) {
                assert(list1.equals(list2));
                if(tryToAdd(list1,"c") && tryToAdd(list2,"c")) {
                    assert(list1.equals(list2));
                    if(tryToAdd(list1,"b") && tryToAdd(list2,"b")) {
                        assert(list1.equals(list2));
                    }
                }
            }
        }
    }

    public void testListGetByIndex() {
        List list = makeList();
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
        List list = makeList();

        try {
            list.get(Integer.MIN_VALUE);
            fail("Shouldn't get here [Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(-1);
            fail("Shouldn't get here [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(0);
            fail("Shouldn't get here [0]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(1);
            fail("Shouldn't get here [1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(Integer.MAX_VALUE);
            fail("Shouldn't get here [Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListGetByIndexBoundsChecking2() {
        List list = makeList();
        boolean added = tryToAdd(list,"a");

        try {
            list.get(Integer.MIN_VALUE);
            fail("Shouldn't get here [Integer.MIN_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(-1);
            fail("Shouldn't get here [-1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(1);
            fail("Shouldn't get here [1]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            list.get(Integer.MAX_VALUE);
            fail("Shouldn't get here [Integer.MAX_VALUE]");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListIndexOf() {
        List list = makeList();
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
        List list = makeList();
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
        List list = makeList();
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

    /*

    public void testListListIterator() {
        // XXX finish me
    }

    public void testListListIteratorByIndex() {
        // XXX finish me
    }

    // optional operation
    public void testListRemoveByIndex() {
        // XXX finish me
    }

    // optional operation
    public void testListRemoveByValue() {
        // XXX finish me
    }

    // optional operation
    public void testListSet() {
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

}
