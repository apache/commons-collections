/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/iterators/TestLoopingIterator.java,v 1.2 2003/08/31 17:28:40 scolebourne Exp $
 * $Revision: 1.2 $
 * $Date: 2003/08/31 17:28:40 $
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
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Tests the LoopingIterator class using jUnit.
 * 
 * @author <a href="mailto:joncrlsn@users.sf.net">Jonathan Carlson</a>
 * @author Stephen Colebourne
 */
public class TestLoopingIterator extends TestCase {

    public TestLoopingIterator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestLoopingIterator.class);
    }

    /**
     * Tests constructor exception.
     */
    public void testConstructorEx() throws Exception {
        try {
            new LoopingIterator(null);
            fail();
        } catch (NullPointerException ex) {
        }
    }
    
    /**
     * Tests whether an empty looping iterator works as designed.
     * @throws Exception  If something unexpected occurs.
     */
    public void testLooping0() throws Exception {
        List list = new ArrayList();
        LoopingIterator loop = new LoopingIterator(list);
        assertTrue("hasNext should return false", loop.hasNext() == false);

        try {
            loop.next();
            fail("NoSuchElementException was not thrown during next() call.");
        } catch (NoSuchElementException ex) {
        }
    }

    /**
     * Tests whether a populated looping iterator works as designed.
     * @throws Exception  If something unexpected occurs.
     */
    public void testLooping1() throws Exception {
        List list = new ArrayList(Arrays.asList(new String[] { "a" }));
        LoopingIterator loop = new LoopingIterator(list);

        assertTrue("1st hasNext should return true", loop.hasNext());
        assertEquals("a", loop.next());

        assertTrue("2nd hasNext should return true", loop.hasNext());
        assertEquals("a", loop.next());

        assertTrue("3rd hasNext should return true", loop.hasNext());
        assertEquals("a", loop.next());

    }

    /**
     * Tests whether a populated looping iterator works as designed.
     * @throws Exception  If something unexpected occurs.
     */
    public void testLooping2() throws Exception {
        List list = new ArrayList(Arrays.asList(new String[] { "a", "b" }));
        LoopingIterator loop = new LoopingIterator(list);

        assertTrue("1st hasNext should return true", loop.hasNext());
        assertEquals("a", loop.next());

        assertTrue("2nd hasNext should return true", loop.hasNext());
        assertEquals("b", loop.next());

        assertTrue("3rd hasNext should return true", loop.hasNext());
        assertEquals("a", loop.next());

    }

    /**
     * Tests whether a populated looping iterator works as designed.
     * @throws Exception  If something unexpected occurs.
     */
    public void testLooping3() throws Exception {
        List list = new ArrayList(Arrays.asList(new String[] { "a", "b", "c" }));
        LoopingIterator loop = new LoopingIterator(list);

        assertTrue("1st hasNext should return true", loop.hasNext());
        assertEquals("a", loop.next());

        assertTrue("2nd hasNext should return true", loop.hasNext());
        assertEquals("b", loop.next());

        assertTrue("3rd hasNext should return true", loop.hasNext());
        assertEquals("c", loop.next());

        assertTrue("4th hasNext should return true", loop.hasNext());
        assertEquals("a", loop.next());

    }

    /**
     * Tests the remove() method on a LoopingIterator wrapped ArrayList.
     * @throws Exception  If something unexpected occurs.
     */
    public void testRemoving1() throws Exception {
        List list = new ArrayList(Arrays.asList(new String[] { "a", "b", "c" }));
        LoopingIterator loop = new LoopingIterator(list);
        assertEquals("list should have 3 elements.", 3, list.size());

        assertTrue("1st hasNext should return true", loop.hasNext());
        assertEquals("a", loop.next());
        loop.remove();  // removes a
        assertEquals("list should have 2 elements.", 2, list.size());

        assertTrue("2nd hasNext should return true", loop.hasNext());
        assertEquals("b", loop.next());
        loop.remove();  // removes b
        assertEquals("list should have 1 elements.", 1, list.size());

        assertTrue("3rd hasNext should return true", loop.hasNext());
        assertEquals("c", loop.next());
        loop.remove();  // removes c
        assertEquals("list should have 0 elements.", 0, list.size());

        assertTrue("4th hasNext should return false", loop.hasNext() == false);
        try {
            loop.next();
            fail("Expected NoSuchElementException to be thrown.");
        } catch (NoSuchElementException ex) {
        }
    }

    /**
     * Tests the reset() method on a LoopingIterator wrapped ArrayList.
     * @throws Exception  If something unexpected occurs.
     */
    public void testReset() throws Exception {
        List list = new ArrayList(Arrays.asList(new String[] { "a", "b", "c" }));
        LoopingIterator loop = new LoopingIterator(list);

        assertEquals("a", loop.next());
        assertEquals("b", loop.next());
        loop.reset();
        assertEquals("a", loop.next());
        loop.reset();
        assertEquals("a", loop.next());
        assertEquals("b", loop.next());
        assertEquals("c", loop.next());
        loop.reset();
        assertEquals("a", loop.next());
        assertEquals("b", loop.next());
        assertEquals("c", loop.next());
    }
    
    /**
     * Tests the size() method on a LoopingIterator wrapped ArrayList.
     * @throws Exception  If something unexpected occurs.
     */
    public void testSize() throws Exception {
        List list = new ArrayList(Arrays.asList(new String[] { "a", "b", "c" }));
        LoopingIterator loop = new LoopingIterator(list);

        assertEquals(3, loop.size());
        loop.next();
        loop.next();
        assertEquals(3, loop.size());
        loop.reset();
        assertEquals(3, loop.size());
        loop.next();
        loop.remove();
        assertEquals(2, loop.size());
    }

}
