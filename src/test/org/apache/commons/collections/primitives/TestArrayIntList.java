/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/Attic/TestArrayIntList.java,v 1.4 2003/01/09 13:40:11 rwaldhoff Exp $
 * $Revision: 1.4 $
 * $Date: 2003/01/09 13:40:11 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

package org.apache.commons.collections.primitives;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.TestList;
import org.apache.commons.collections.primitives.adapters.IntListList;
import org.apache.commons.collections.primitives.adapters.ListIntList;

/**
 * @version $Revision: 1.4 $ $Date: 2003/01/09 13:40:11 $
 * @author Rodney Waldhoff
 */
public class TestArrayIntList extends TestList {

    //------------------------------------------------------------ Conventional

    public TestArrayIntList(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = BulkTest.makeSuite(TestArrayIntList.class);
        return suite;
    }


    //------------------------------------------------------- TestList interface

    public List makeEmptyList() {
        return new IntListList(new ArrayIntList());
    }

    /**
     *  Returns small Integer objects for testing.
     */
    protected Object[] getFullElements() {
        Integer[] result = new Integer[19];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Integer(i + 19);
        }
        return result;
    }

    /**
     *  Returns small Integer objects for testing.
     */
    protected Object[] getOtherElements() {
        Integer[] result = new Integer[16];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Integer(i + 48);
        }
        return result;
    }

    // TODO:  Create canonical collections in CVS

    public void testCanonicalEmptyCollectionExists() {
    }

    public void testCanonicalFullCollectionExists() {
    }

    public void testEmptyListCompatibility() {
    }

    public void testFullListCompatibility() {
    }

    //------------------------------------------------------------------- Tests

    public void testEqualsWithTwoIntLists() {
        IntList one = new ArrayIntList();
        assertEquals("Equals is reflexive on empty list",one,one);
        IntList two = new ArrayIntList();
        assertEquals("Empty lists are equal",one,two);
        assertEquals("Equals is symmetric on empty lists",two,one);
        
        one.add(1);
        assertEquals("Equals is reflexive on non empty list",one,one);
        assertTrue(!one.equals(two));
        assertTrue(!two.equals(one));

        two.add(1);
        assertEquals("Non empty lists are equal",one,two);
        assertEquals("Equals is symmetric on non empty list",one,two);
        
        one.add(1); one.add(2); one.add(3); one.add(5); one.add(8);
        assertEquals("Equals is reflexive on larger non empty list",one,one);
        assertTrue(!one.equals(two));
        assertTrue(!two.equals(one));
        
        two.add(1); two.add(2); two.add(3); two.add(5); two.add(8);
        assertEquals("Larger non empty lists are equal",one,two);
        assertEquals("Equals is symmetric on larger non empty list",two,one);
    }

    public void testIntSubListEquals() {
        IntList one = new ArrayIntList();
        assertEquals(one,one.subList(0,0));
        assertEquals(one.subList(0,0),one);
        
        one.add(1);
        assertEquals(one,one.subList(0,1));
        assertEquals(one.subList(0,1),one);

        one.add(1); one.add(2); one.add(3); one.add(5); one.add(8);
        assertEquals(one.subList(0,4),one.subList(0,4));
        assertEquals(one.subList(3,5),one.subList(3,5));
    }
    
    public void testEqualsWithIntListAndList() {
        IntList ilist = new ArrayIntList();
        List list = new ArrayList();
        
        assertTrue("Unwrapped, empty List is not equal to empty IntList.",!ilist.equals(list));
        assertTrue("Unwrapped, empty IntList is not equal to empty List.",!list.equals(ilist));
        
        assertEquals(new ListIntList(list),ilist);
        assertEquals(ilist,new ListIntList(list));
        assertEquals(new IntListList(ilist),list);
        assertEquals(list,new IntListList(ilist));
        
        ilist.add(1);
        list.add(new Integer(1));

        assertTrue("Unwrapped, non-empty List is not equal to non-empty IntList.",!ilist.equals(list));
        assertTrue("Unwrapped, non-empty IntList is not equal to non-empty List.",!list.equals(ilist));
        
        assertEquals(new ListIntList(list),ilist);
        assertEquals(ilist,new ListIntList(list));
        assertEquals(new IntListList(ilist),list);
        assertEquals(list,new IntListList(ilist));
                
        ilist.add(1); ilist.add(2); ilist.add(3); ilist.add(5); ilist.add(8);
        list.add(new Integer(1)); list.add(new Integer(2)); list.add(new Integer(3)); list.add(new Integer(5)); list.add(new Integer(8));

        assertTrue("Unwrapped, non-empty List is not equal to non-empty IntList.",!ilist.equals(list));
        assertTrue("Unwrapped, non-empty IntList is not equal to non-empty List.",!list.equals(ilist));
        
        assertEquals(new ListIntList(list),ilist);
        assertEquals(ilist,new ListIntList(list));
        assertEquals(new IntListList(ilist),list);
        assertEquals(list,new IntListList(ilist));
        
    }

    public void testClearAndSize() {
        IntList list = new ArrayIntList();
        assertEquals(0, list.size());
        for(int i = 0; i < 100; i++) {
            list.add(i);
        }
        assertEquals(100, list.size());
        list.clear();
        assertEquals(0, list.size());
    }

    public void testRemoveViaSubList() {
        IntList list = new ArrayIntList();
        for(int i = 0; i < 100; i++) {
            list.add(i);
        }
        IntList sub = list.subList(25,75);
        assertEquals(50,sub.size());
        for(int i = 0; i < 50; i++) {
            assertEquals(100-i,list.size());
            assertEquals(50-i,sub.size());
            assertEquals(25+i,sub.removeElementAt(0));
            assertEquals(50-i-1,sub.size());
            assertEquals(100-i-1,list.size());
        }
        assertEquals(0,sub.size());
        assertEquals(50,list.size());        
    }
    

    public void testAddGet() {
        IntList list = new ArrayIntList();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, list.get(i));
        }
    }

    public void testAddGetLargeValues() {
        IntList list = new ArrayIntList();
        for (int i = 0; i < 1000; i++) {
            int value = ((int) (Short.MAX_VALUE));
            value += i;
            list.add(value);
        }
        for (int i = 0; i < 1000; i++) {
            int value = ((int) (Short.MAX_VALUE));
            value += i;
            assertEquals(value, list.get(i));
        }
    }

    public void testAddAndShift() {
        IntList list = new ArrayIntList();
        list.add(0, 1);
        assertEquals("Should have one entry", 1, list.size());
        list.add(3);
        list.add(4);
        list.add(1, 2);
        for(int i = 0; i < 4; i++) {
            assertEquals("Should get entry back", i + 1, list.get(i));
        }
        list.add(0, 0);
        for (int i = 0; i < 5; i++) {
            assertEquals("Should get entry back", i, list.get(i));
        }
    }

    public void testZeroInitialCapacityIsValid() {
        ArrayIntList list = new ArrayIntList(0);
    }
}
