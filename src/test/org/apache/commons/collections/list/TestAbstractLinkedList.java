/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 */
package org.apache.commons.collections.list;

import java.util.Arrays;

/**
 * Test case for {@link AbstractLinkedList}.
 * 
 * @version $Revision: 1.2 $ $Date: 2004/01/14 21:34:28 $
 * 
 * @author Rich Dougherty
 * @author David Hay
 * @author Phil Steitz
 */
public abstract class TestAbstractLinkedList extends AbstractTestList {
    
    public TestAbstractLinkedList(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------    
    public void testRemoveFirst() {
        resetEmpty();
        AbstractLinkedList list = (AbstractLinkedList) collection;
        if (isRemoveSupported() == false) {
            try {
                list.removeFirst();
            } catch (UnsupportedOperationException ex) {}
        } 
        
        list.addAll( Arrays.asList( new String[]{"value1", "value2"}));
        assertEquals( "value1", list.removeFirst() );
        checkNodes();
        list.addLast( "value3");
        checkNodes();
        assertEquals( "value2", list.removeFirst() );
        assertEquals( "value3", list.removeFirst() );
        checkNodes();
        list.addLast( "value4" );
        checkNodes();
        assertEquals( "value4", list.removeFirst() );
        checkNodes();
    }
    
    public void testRemoveLast() {
        resetEmpty();
        AbstractLinkedList list = (AbstractLinkedList) collection;
        if (isRemoveSupported() == false) {
            try {
                list.removeLast();
            } catch (UnsupportedOperationException ex) {}
        } 
        
        list.addAll( Arrays.asList( new String[]{"value1", "value2"}));
        assertEquals( "value2", list.removeLast() );
        list.addFirst( "value3");
        checkNodes();
        assertEquals( "value1", list.removeLast() );
        assertEquals( "value3", list.removeLast() );
        list.addFirst( "value4" );
        checkNodes();
        assertEquals( "value4", list.removeFirst() );
    }
    
    public void testAddNodeAfter() {
        resetEmpty();
        AbstractLinkedList list = (AbstractLinkedList) collection;
        if (isAddSupported() == false) {
            try {
                list.addFirst(null);
            } catch (UnsupportedOperationException ex) {}
        } 
        
        list.addFirst("value1");
        list.addNodeAfter(list.getNode(0,false),"value2");
        assertEquals("value1", list.getFirst());
        assertEquals("value2", list.getLast());
        list.removeFirst();
        checkNodes();
        list.addNodeAfter(list.getNode(0,false),"value3");
        checkNodes();
        assertEquals("value2", list.getFirst());
        assertEquals("value3", list.getLast());
        list.addNodeAfter(list.getNode(0, false),"value4");
        checkNodes();
        assertEquals("value2", list.getFirst());
        assertEquals("value3", list.getLast());
        assertEquals("value4", list.get(1));
        list.addNodeAfter(list.getNode(2, false), "value5");
        checkNodes();
        assertEquals("value2", list.getFirst());
        assertEquals("value4", list.get(1));
        assertEquals("value3", list.get(2));
        assertEquals("value5", list.getLast());
    }
    
    public void testRemoveNode() {
        resetEmpty();
        if (isAddSupported() == false || isRemoveSupported() == false) return;
        AbstractLinkedList list = (AbstractLinkedList) collection;
        
        list.addAll( Arrays.asList( new String[]{"value1", "value2"}));
        list.removeNode(list.getNode(0, false));
        checkNodes();
        assertEquals("value2", list.getFirst());
        assertEquals("value2", list.getLast());
        list.addFirst("value1");
        list.addFirst("value0");
        checkNodes();
        list.removeNode(list.getNode(1, false));
        assertEquals("value0", list.getFirst());
        assertEquals("value2", list.getLast());
        checkNodes();
        list.removeNode(list.getNode(1, false));
        assertEquals("value0", list.getFirst());
        assertEquals("value0", list.getLast());
        checkNodes();
    }
    
    public void testGetNode() {
        resetEmpty();
        AbstractLinkedList list = (AbstractLinkedList) collection;
        // get marker
        assertEquals(list.getNode(0, true).previous, list.getNode(0, true).next);
        try {
            Object obj = list.getNode(0, false);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        list.addAll( Arrays.asList( new String[]{"value1", "value2"}));
        checkNodes();
        list.addFirst("value0");
        checkNodes();
        list.removeNode(list.getNode(1, false));
        checkNodes();
        try {
            Object obj = list.getNode(2, false);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            Object obj = list.getNode(-1, false);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
         try {
            Object obj = list.getNode(3, true);
            fail("Expecting IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }       
    }
    
    protected void checkNodes() {
        AbstractLinkedList list = (AbstractLinkedList) collection;
        for (int i = 0; i < list.size; i++) {
            assertEquals(list.getNode(i, false).next, list.getNode(i + 1, true));
            if (i < list.size - 1) {
                assertEquals(list.getNode(i + 1, false).previous, 
                    list.getNode(i, false));  
            }
        }
    }
        
}
