/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/Attic/TestLongList.java,v 1.1 2003/04/08 18:24:34 rwaldhoff Exp $
 * ====================================================================
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

package org.apache.commons.collections.primitives;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.TestList;
import org.apache.commons.collections.primitives.adapters.LongListList;
import org.apache.commons.collections.primitives.adapters.ListLongList;

/**
 * @version $Revision: 1.1 $ $Date: 2003/04/08 18:24:34 $
 * @author Rodney Waldhoff
 */
public abstract class TestLongList extends TestList {

    // conventional
    // ------------------------------------------------------------------------

    public TestLongList(String testName) {
        super(testName);
    }

    // collections testing framework
    // ------------------------------------------------------------------------

    // collections testing framework: long list
    // ------------------------------------------------------------------------

    protected abstract LongList makeEmptyLongList();

    protected LongList makeFullLongList() {
        LongList list = makeEmptyLongList();
        long[] values = getFullLongs();
        for(int i=0;i<values.length;i++) {
            list.add(values[i]);
        }
        return list;
    }

    protected long[] getFullLongs() {
        long[] result = new long[19];
        for(int i = 0; i < result.length; i++) {
            result[i] = (long)i + ((long)Integer.MAX_VALUE - (long)10);
        }
        return result;
    }

    protected long[] getOtherLongs() {
        long[] result = new long[16];
        for (int i = 0; i < result.length; i++) {
            result[i] = (long)i + (long)43;
        }
        return result;
    }
    
    // collections testing framework: inherited
    // ------------------------------------------------------------------------

    protected List makeEmptyList() {
        return new LongListList(makeEmptyLongList());
    }
        
    protected Object[] getFullElements() {
        return wrapArray(getFullLongs());
    }

    protected Object[] getOtherElements() {
        return wrapArray(getOtherLongs());
    }

    // private utils
    // ------------------------------------------------------------------------

    private Long[] wrapArray(long[] primitives) {
        Long[] result = new Long[primitives.length];
        for(int i=0;i<result.length;i++) {
            result[i] = new Long(primitives[i]);            
        }
        return result;
    }

    // tests
    // ------------------------------------------------------------------------

    public void testToJustBigEnoughLongArray() {
        LongList list = makeFullLongList();
        long[] dest = new long[list.size()];
        assertSame(dest,list.toArray(dest));
        int i=0;
        for(LongIterator iter = list.iterator(); iter.hasNext();i++) {
            assertEquals(iter.next(),dest[i]);
        }
    }
    
    public void testToLargerThanNeededLongArray() {
        LongList list = makeFullLongList();
        long[] dest = new long[list.size()*2];
        for(int i=0;i<dest.length;i++) {
            dest[i] = Long.MAX_VALUE;
        }       
        assertSame(dest,list.toArray(dest));
        int i=0;
        for(LongIterator iter = list.iterator(); iter.hasNext();i++) {
            assertEquals(iter.next(),dest[i]);
        }
        for(;i<dest.length;i++) {
            assertEquals(Long.MAX_VALUE,dest[i]);
        }
    }
    
    public void testToSmallerThanNeededLongArray() {
        LongList list = makeFullLongList();
        long[] dest = new long[list.size()/2];
        long[] dest2 = list.toArray(dest);
        assertTrue(dest != dest2);
        int i=0;
        for(LongIterator iter = list.iterator(); iter.hasNext();i++) {
            assertEquals(iter.next(),dest2[i]);
        }
    }
    
    public void testHashCodeSpecification() {
        LongList list = makeFullLongList();
		int hash = 1;
		for(LongIterator iter = list.iterator(); iter.hasNext(); ) {
			long val = iter.next();
			hash = 31*hash + ((int)(val ^ (val >>> 32)));
		}
        assertEquals(hash,list.hashCode());
    }

    public void testEqualsWithTwoLongLists() {
        LongList one = makeEmptyLongList();
        assertEquals("Equals is reflexive on empty list",one,one);
        LongList two = makeEmptyLongList();
        assertEquals("Empty lists are equal",one,two);
        assertEquals("Equals is symmetric on empty lists",two,one);
        
        one.add((long)1);
        assertEquals("Equals is reflexive on non empty list",one,one);
        assertTrue(!one.equals(two));
        assertTrue(!two.equals(one));

        two.add((long)1);
        assertEquals("Non empty lists are equal",one,two);
        assertEquals("Equals is symmetric on non empty list",one,two);
        
        one.add((long)1); one.add((long)2); one.add((long)3); one.add((long)5); one.add((long)8);
        assertEquals("Equals is reflexive on larger non empty list",one,one);
        assertTrue(!one.equals(two));
        assertTrue(!two.equals(one));
        
        two.add((long)1); two.add((long)2); two.add((long)3); two.add((long)5); two.add((long)8);
        assertEquals("Larger non empty lists are equal",one,two);
        assertEquals("Equals is symmetric on larger non empty list",two,one);

        one.add((long)9);
        two.add((long)10);
        assertTrue(!one.equals(two));
        assertTrue(!two.equals(one));

    }

    public void testLongSubListEquals() {
        LongList one = makeEmptyLongList();
        assertEquals(one,one.subList(0,0));
        assertEquals(one.subList(0,0),one);
        
        one.add((long)1);
        assertEquals(one,one.subList(0,1));
        assertEquals(one.subList(0,1),one);

        one.add((long)1); one.add((long)2); one.add((long)3); one.add((long)5); one.add((long)8);
        assertEquals(one.subList(0,4),one.subList(0,4));
        assertEquals(one.subList(3,5),one.subList(3,5));
    }
    
    public void testEqualsWithLongListAndList() {
        LongList ilist = makeEmptyLongList();
        List list = new ArrayList();
        
        assertTrue("Unwrapped, empty List should not be equal to empty LongList.",!ilist.equals(list));
        assertTrue("Unwrapped, empty LongList should not be equal to empty List.",!list.equals(ilist));
        
        assertEquals(new ListLongList(list),ilist);
        assertEquals(ilist,new ListLongList(list));
        assertEquals(new LongListList(ilist),list);
        assertEquals(list,new LongListList(ilist));
        
        ilist.add((long)1);
        list.add(new Long(1));

        assertTrue("Unwrapped, non-empty List is not equal to non-empty LongList.",!ilist.equals(list));
        assertTrue("Unwrapped, non-empty LongList is not equal to non-empty List.",!list.equals(ilist));
        
        assertEquals(new ListLongList(list),ilist);
        assertEquals(ilist,new ListLongList(list));
        assertEquals(new LongListList(ilist),list);
        assertEquals(list,new LongListList(ilist));
                
        ilist.add(1); ilist.add(2); ilist.add(3); ilist.add(5); ilist.add(8);
        list.add(new Long(1)); list.add(new Long(2)); list.add(new Long(3)); list.add(new Long(5)); list.add(new Long(8));

        assertTrue("Unwrapped, non-empty List is not equal to non-empty LongList.",!ilist.equals(list));
        assertTrue("Unwrapped, non-empty LongList is not equal to non-empty List.",!list.equals(ilist));
        
        assertEquals(new ListLongList(list),ilist);
        assertEquals(ilist,new ListLongList(list));
        assertEquals(new LongListList(ilist),list);
        assertEquals(list,new LongListList(ilist));
        
    }

    public void testClearAndSize() {
        LongList list = makeEmptyLongList();
        assertEquals(0, list.size());
        for(int i = 0; i < 100; i++) {
            list.add((long)i);
        }
        assertEquals(100, list.size());
        list.clear();
        assertEquals(0, list.size());
    }

    public void testRemoveViaSubList() {
        LongList list = makeEmptyLongList();
        for(int i = 0; i < 100; i++) {
            list.add((long)i);
        }
        LongList sub = list.subList(25,75);
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
        LongList list = makeEmptyLongList();
        for (int i = 0; i < 1000; i++) {
            list.add((long)i);
        }
        for (int i = 0; i < 1000; i++) {
            assertEquals((long)i, list.get(i));
        }
    }

    public void testAddAndShift() {
        LongList list = makeEmptyLongList();
        list.add(0, (long)1);
        assertEquals("Should have one entry", 1, list.size());
        list.add((long)3);
        list.add((long)4);
        list.add(1, (long)2);
        for(int i = 0; i < 4; i++) {
            assertEquals("Should get entry back", (long)(i + 1), list.get(i));
        }
        list.add(0, (long)0);
        for (int i = 0; i < 5; i++) {
            assertEquals("Should get entry back", (long)i, list.get(i));
        }
    }

    public void testIsSerializable() throws Exception {
        LongList list = makeFullLongList();
        assertTrue(list instanceof Serializable);
        byte[] ser = writeExternalFormToBytes((Serializable)list);
        LongList deser = (LongList)(readExternalFormFromBytes(ser));
        assertEquals(list,deser);
        assertEquals(deser,list);
    }

    public void testLongListSerializeDeserializeThenCompare() throws Exception {
        LongList list = makeFullLongList();
        if(list instanceof Serializable) {
            byte[] ser = writeExternalFormToBytes((Serializable)list);
            LongList deser = (LongList)(readExternalFormFromBytes(ser));
            assertEquals("obj != deserialize(serialize(obj))",list,deser);
        }
    }

    public void testSubListsAreNotSerializable() throws Exception {
        LongList list = makeFullLongList().subList(2,3);
        assertTrue( ! (list instanceof Serializable) );
    }

    public void testSubListOutOfBounds() throws Exception {
        try {
            makeEmptyLongList().subList(2,3);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            makeFullLongList().subList(-1,3);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }


        try {
            makeFullLongList().subList(5,2);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }

        try {
            makeFullLongList().subList(2,makeFullLongList().size()+2);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListIteratorOutOfBounds() throws Exception {
        try {
            makeEmptyLongList().listIterator(2);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            makeFullLongList().listIterator(-1);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            makeFullLongList().listIterator(makeFullLongList().size()+2);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListIteratorSetWithoutNext() throws Exception {
        LongListIterator iter = makeFullLongList().listIterator();
        try {
            iter.set(3);
            fail("Expected IllegalStateException");
        } catch(IllegalStateException e) {
            // expected
        }
    }

    public void testListIteratorSetAfterRemove() throws Exception {
        LongListIterator iter = makeFullLongList().listIterator();
        iter.next();
        iter.remove();
        try {            
            iter.set(3);
            fail("Expected IllegalStateException");
        } catch(IllegalStateException e) {
            // expected
        }
    }

}
