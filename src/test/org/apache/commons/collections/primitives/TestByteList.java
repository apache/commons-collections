/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/Attic/TestByteList.java,v 1.3 2003/08/31 17:28:41 scolebourne Exp $
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

package org.apache.commons.collections.primitives;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.TestList;
import org.apache.commons.collections.primitives.adapters.ByteListList;
import org.apache.commons.collections.primitives.adapters.ListByteList;

/**
 * @version $Revision: 1.3 $ $Date: 2003/08/31 17:28:41 $
 * @author Rodney Waldhoff
 */
public abstract class TestByteList extends TestList {

    // conventional
    // ------------------------------------------------------------------------

    public TestByteList(String testName) {
        super(testName);
    }

    // collections testing framework
    // ------------------------------------------------------------------------

    // collections testing framework: byte list
    // ------------------------------------------------------------------------

    protected abstract ByteList makeEmptyByteList();

    protected ByteList makeFullByteList() {
        ByteList list = makeEmptyByteList();
        byte[] values = getFullBytes();
        for(int i=0;i<values.length;i++) {
            list.add(values[i]);
        }
        return list;
    }

    protected byte[] getFullBytes() {
        byte[] result = new byte[19];
        for(int i = 0; i < result.length; i++) {
            result[i] = (byte)(i);
        }
        return result;
    }

    protected byte[] getOtherBytes() {
        byte[] result = new byte[16];
        for(int i = 0; i < result.length; i++) {
            result[i] = (byte)(i + 43);
        }
        return result;
    }
    
    // collections testing framework: inherited
    // ------------------------------------------------------------------------

    protected List makeEmptyList() {
        return new ByteListList(makeEmptyByteList());
    }
        
    protected Object[] getFullElements() {
        return wrapArray(getFullBytes());
    }

    protected Object[] getOtherElements() {
        return wrapArray(getOtherBytes());
    }

    // private utils
    // ------------------------------------------------------------------------

    private Byte[] wrapArray(byte[] primitives) {
        Byte[] result = new Byte[primitives.length];
        for(int i=0;i<result.length;i++) {
            result[i] = new Byte(primitives[i]);            
        }
        return result;
    }

    // tests
    // ------------------------------------------------------------------------

    public void testToJustBigEnoughByteArray() {
        ByteList list = makeFullByteList();
        byte[] dest = new byte[list.size()];
        assertSame(dest,list.toArray(dest));
        int i=0;
        for(ByteIterator iter = list.iterator(); iter.hasNext();i++) {
            assertEquals(iter.next(),dest[i]);
        }
    }
    
    public void testToLargerThanNeededByteArray() {
        ByteList list = makeFullByteList();
        byte[] dest = new byte[list.size()*2];
        for(int i=0;i<dest.length;i++) {
            dest[i] = Byte.MAX_VALUE;
        }       
        assertSame(dest,list.toArray(dest));
        int i=0;
        for(ByteIterator iter = list.iterator(); iter.hasNext();i++) {
            assertEquals(iter.next(),dest[i]);
        }
        for(;i<dest.length;i++) {
            assertEquals(Byte.MAX_VALUE,dest[i]);
        }
    }
    
    public void testToSmallerThanNeededByteArray() {
        ByteList list = makeFullByteList();
        byte[] dest = new byte[list.size()/2];
        byte[] dest2 = list.toArray(dest);
        assertTrue(dest != dest2);
        int i=0;
        for(ByteIterator iter = list.iterator(); iter.hasNext();i++) {
            assertEquals(iter.next(),dest2[i]);
        }
    }
    
    public void testHashCodeSpecification() {
        ByteList list = makeFullByteList();
        int hash = 1;
        for(ByteIterator iter = list.iterator(); iter.hasNext(); ) {
            hash = 31*hash + ((int)iter.next());
        }
        assertEquals(hash,list.hashCode());
    }

    public void testEqualsWithTwoByteLists() {
        ByteList one = makeEmptyByteList();
        assertEquals("Equals is reflexive on empty list",one,one);
        ByteList two = makeEmptyByteList();
        assertEquals("Empty lists are equal",one,two);
        assertEquals("Equals is symmetric on empty lists",two,one);
        
        one.add((byte)1);
        assertEquals("Equals is reflexive on non empty list",one,one);
        assertTrue(!one.equals(two));
        assertTrue(!two.equals(one));

        two.add((byte)1);
        assertEquals("Non empty lists are equal",one,two);
        assertEquals("Equals is symmetric on non empty list",one,two);
        
        one.add((byte)1); one.add((byte)2); one.add((byte)3); one.add((byte)5); one.add((byte)8);
        assertEquals("Equals is reflexive on larger non empty list",one,one);
        assertTrue(!one.equals(two));
        assertTrue(!two.equals(one));
        
        two.add((byte)1); two.add((byte)2); two.add((byte)3); two.add((byte)5); two.add((byte)8);
        assertEquals("Larger non empty lists are equal",one,two);
        assertEquals("Equals is symmetric on larger non empty list",two,one);

        one.add((byte)9);
        two.add((byte)10);
        assertTrue(!one.equals(two));
        assertTrue(!two.equals(one));

    }

    public void testByteSubListEquals() {
        ByteList one = makeEmptyByteList();
        assertEquals(one,one.subList(0,0));
        assertEquals(one.subList(0,0),one);
        
        one.add((byte)1);
        assertEquals(one,one.subList(0,1));
        assertEquals(one.subList(0,1),one);

        one.add((byte)1); one.add((byte)2); one.add((byte)3); one.add((byte)5); one.add((byte)8);
        assertEquals(one.subList(0,4),one.subList(0,4));
        assertEquals(one.subList(3,5),one.subList(3,5));
    }
    
    public void testEqualsWithByteListAndList() {
        ByteList ilist = makeEmptyByteList();
        List list = new ArrayList();
        
        assertTrue("Unwrapped, empty List should not be equal to empty ByteList.",!ilist.equals(list));
        assertTrue("Unwrapped, empty ByteList should not be equal to empty List.",!list.equals(ilist));
        
        assertEquals(new ListByteList(list),ilist);
        assertEquals(ilist,new ListByteList(list));
        assertEquals(new ByteListList(ilist),list);
        assertEquals(list,new ByteListList(ilist));
        
        ilist.add((byte)1);
        list.add(new Byte((byte)1));

        assertTrue("Unwrapped, non-empty List is not equal to non-empty ByteList.",!ilist.equals(list));
        assertTrue("Unwrapped, non-empty ByteList is not equal to non-empty List.",!list.equals(ilist));
        
        assertEquals(new ListByteList(list),ilist);
        assertEquals(ilist,new ListByteList(list));
        assertEquals(new ByteListList(ilist),list);
        assertEquals(list,new ByteListList(ilist));
                
        ilist.add((byte)1); ilist.add((byte)2); ilist.add((byte)3); ilist.add((byte)5); ilist.add((byte)8);
        list.add(new Byte((byte)1)); list.add(new Byte((byte)2)); list.add(new Byte((byte)3)); list.add(new Byte((byte)5)); list.add(new Byte((byte)8));

        assertTrue("Unwrapped, non-empty List is not equal to non-empty ByteList.",!ilist.equals(list));
        assertTrue("Unwrapped, non-empty ByteList is not equal to non-empty List.",!list.equals(ilist));
        
        assertEquals(new ListByteList(list),ilist);
        assertEquals(ilist,new ListByteList(list));
        assertEquals(new ByteListList(ilist),list);
        assertEquals(list,new ByteListList(ilist));
        
    }

    public void testClearAndSize() {
        ByteList list = makeEmptyByteList();
        assertEquals(0, list.size());
        for(int i = 0; i < 100; i++) {
            list.add((byte)i);
        }
        assertEquals(100, list.size());
        list.clear();
        assertEquals(0, list.size());
    }

    public void testRemoveViaSubList() {
        ByteList list = makeEmptyByteList();
        for(int i = 0; i < 100; i++) {
            list.add((byte)i);
        }
        ByteList sub = list.subList(25,75);
        assertEquals(50,sub.size());
        for(int i = 0; i < 50; i++) {
            assertEquals(100-i,list.size());
            assertEquals(50-i,sub.size());
            assertEquals((byte)(25+i),sub.removeElementAt(0));
            assertEquals(50-i-1,sub.size());
            assertEquals(100-i-1,list.size());
        }
        assertEquals(0,sub.size());
        assertEquals(50,list.size());        
    }
    
    public void testAddGet() {
        ByteList list = makeEmptyByteList();
        for (int i = 0; i < 255; i++) {
            list.add((byte)i);
        }
        for (int i = 0; i < 255; i++) {
            assertEquals((byte)i, list.get(i));
        }
    }

    public void testAddAndShift() {
        ByteList list = makeEmptyByteList();
        list.add(0, (byte)1);
        assertEquals("Should have one entry", 1, list.size());
        list.add((byte)3);
        list.add((byte)4);
        list.add(1, (byte)2);
        for(int i = 0; i < 4; i++) {
            assertEquals("Should get entry back", (byte)(i + 1), list.get(i));
        }
        list.add(0, (byte)0);
        for (int i = 0; i < 5; i++) {
            assertEquals("Should get entry back", (byte)i, list.get(i));
        }
    }

    public void testIsSerializable() throws Exception {
        ByteList list = makeFullByteList();
        assertTrue(list instanceof Serializable);
        byte[] ser = writeExternalFormToBytes((Serializable)list);
        ByteList deser = (ByteList)(readExternalFormFromBytes(ser));
        assertEquals(list,deser);
        assertEquals(deser,list);
    }

    public void testByteListSerializeDeserializeThenCompare() throws Exception {
        ByteList list = makeFullByteList();
        if(list instanceof Serializable) {
            byte[] ser = writeExternalFormToBytes((Serializable)list);
            ByteList deser = (ByteList)(readExternalFormFromBytes(ser));
            assertEquals("obj != deserialize(serialize(obj))",list,deser);
        }
    }

    public void testSubListsAreNotSerializable() throws Exception {
        ByteList list = makeFullByteList().subList(2,3);
        assertTrue( ! (list instanceof Serializable) );
    }

    public void testSubListOutOfBounds() throws Exception {
        try {
            makeEmptyByteList().subList(2,3);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            makeFullByteList().subList(-1,3);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }


        try {
            makeFullByteList().subList(5,2);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }

        try {
            makeFullByteList().subList(2,makeFullByteList().size()+2);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListIteratorOutOfBounds() throws Exception {
        try {
            makeEmptyByteList().listIterator(2);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            makeFullByteList().listIterator(-1);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }

        try {
            makeFullByteList().listIterator(makeFullByteList().size()+2);
            fail("Expected IndexOutOfBoundsException");
        } catch(IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testListIteratorSetWithoutNext() throws Exception {
        ByteListIterator iter = makeFullByteList().listIterator();
        try {
            iter.set((byte)3);
            fail("Expected IllegalStateException");
        } catch(IllegalStateException e) {
            // expected
        }
    }

    public void testListIteratorSetAfterRemove() throws Exception {
        ByteListIterator iter = makeFullByteList().listIterator();
        iter.next();
        iter.remove();
        try {            
            iter.set((byte)3);
            fail("Expected IllegalStateException");
        } catch(IllegalStateException e) {
            // expected
        }
    }

}
