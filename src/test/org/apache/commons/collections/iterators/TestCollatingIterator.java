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
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.comparators.ComparableComparator;

/**
 * Unit test suite for {@link CollatingIterator}.
 * 
 * @version $Revision: 1.5 $ $Date: 2004/01/14 21:34:25 $
 * @author Rodney Waldhoff
 */
public class TestCollatingIterator extends AbstractTestIterator {

    //------------------------------------------------------------ Conventional
    
    public TestCollatingIterator(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestCollatingIterator.class);
    }

    //--------------------------------------------------------------- Lifecycle

    private Comparator comparator = null;
    private ArrayList evens = null; 
    private ArrayList odds = null; 
    private ArrayList fib = null; 

    public void setUp() throws Exception {
        super.setUp();
        comparator = new ComparableComparator();
        evens = new ArrayList();
        odds = new ArrayList();
        for(int i=0;i<20;i++) {
            if(0 == i%2) {
                evens.add(new Integer(i));
            } else {
                odds.add(new Integer(i));
            }
        }
        fib = new ArrayList();
        fib.add(new Integer(1));
        fib.add(new Integer(1));
        fib.add(new Integer(2));
        fib.add(new Integer(3));
        fib.add(new Integer(5));
        fib.add(new Integer(8));
        fib.add(new Integer(13));
        fib.add(new Integer(21));
    }       

    //---------------------------------------------------- TestIterator Methods
    
    public Iterator makeEmptyIterator() {
        return new CollatingIterator(comparator);
    }

    public Iterator makeFullIterator() {
        CollatingIterator iter = new CollatingIterator(comparator);
        iter.addIterator(evens.iterator());
        iter.addIterator(odds.iterator());
        iter.addIterator(fib.iterator());
        return iter;
    }

    //------------------------------------------------------------------- Tests

    public void testGetSetComparator() {
        CollatingIterator iter = new CollatingIterator();
        assertNull(iter.getComparator());
        iter.setComparator(comparator);
        assertSame(comparator,iter.getComparator());
        iter.setComparator(null);
        assertNull(iter.getComparator());
    }

    public void testIterateEven() {
        CollatingIterator iter = new CollatingIterator(comparator);
        iter.addIterator(evens.iterator());
        for(int i=0;i<evens.size();i++) {
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i),iter.next());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateEvenOdd() {
        CollatingIterator iter = new CollatingIterator(comparator,evens.iterator(),odds.iterator());
        for(int i=0;i<20;i++) {
            assertTrue(iter.hasNext());
            assertEquals(new Integer(i),iter.next());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateOddEven() {
        CollatingIterator iter = new CollatingIterator(comparator,odds.iterator(),evens.iterator());
        for(int i=0;i<20;i++) {
            assertTrue(iter.hasNext());
            assertEquals(new Integer(i),iter.next());
        }
        assertTrue(!iter.hasNext());
    }

    public void testIterateEvenEven() {
        CollatingIterator iter = new CollatingIterator(comparator);
        iter.addIterator(evens.iterator());
        iter.addIterator(evens.iterator());
        for(int i=0;i<evens.size();i++) {
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i),iter.next());
            assertTrue(iter.hasNext());
            assertEquals(evens.get(i),iter.next());
        }
        assertTrue(!iter.hasNext());
    }


    public void testIterateFibEvenOdd() {
        CollatingIterator iter = new CollatingIterator(comparator);
        iter.addIterator(fib.iterator());
        iter.addIterator(evens.iterator());
        iter.addIterator(odds.iterator());
        
        assertEquals(new Integer(0),iter.next());  // even   0
        assertEquals(new Integer(1),iter.next());  // fib    1
        assertEquals(new Integer(1),iter.next());  // fib    1
        assertEquals(new Integer(1),iter.next());  // odd    1
        assertEquals(new Integer(2),iter.next());  // fib    2
        assertEquals(new Integer(2),iter.next());  // even   2
        assertEquals(new Integer(3),iter.next());  // fib    3
        assertEquals(new Integer(3),iter.next());  // odd    3
        assertEquals(new Integer(4),iter.next());  // even   4
        assertEquals(new Integer(5),iter.next());  // fib    5
        assertEquals(new Integer(5),iter.next());  // odd    5
        assertEquals(new Integer(6),iter.next());  // even   6
        assertEquals(new Integer(7),iter.next());  // odd    7
        assertEquals(new Integer(8),iter.next());  // fib    8
        assertEquals(new Integer(8),iter.next());  // even   8
        assertEquals(new Integer(9),iter.next());  // odd    9
        assertEquals(new Integer(10),iter.next()); // even  10
        assertEquals(new Integer(11),iter.next()); // odd   11
        assertEquals(new Integer(12),iter.next()); // even  12
        assertEquals(new Integer(13),iter.next()); // fib   13
        assertEquals(new Integer(13),iter.next()); // odd   13
        assertEquals(new Integer(14),iter.next()); // even  14
        assertEquals(new Integer(15),iter.next()); // odd   15
        assertEquals(new Integer(16),iter.next()); // even  16
        assertEquals(new Integer(17),iter.next()); // odd   17
        assertEquals(new Integer(18),iter.next()); // even  18
        assertEquals(new Integer(19),iter.next()); // odd   19
        assertEquals(new Integer(21),iter.next()); // fib   21

        assertTrue(!iter.hasNext());
    }

    public void testRemoveFromSingle() {
        CollatingIterator iter = new CollatingIterator(comparator);
        iter.addIterator(evens.iterator());
        int expectedSize = evens.size();
        while(iter.hasNext()) {
            Integer val = (Integer)(iter.next());
            if(val.intValue() % 4 == 0) {
                expectedSize--;
                iter.remove();
            }
        }
        assertEquals(expectedSize,evens.size());
    }

    public void testRemoveFromDouble() {
        CollatingIterator iter = new CollatingIterator(comparator);
        iter.addIterator(evens.iterator());
        iter.addIterator(odds.iterator());
        int expectedSize = evens.size() + odds.size();
        while(iter.hasNext()) {
            Integer val = (Integer)(iter.next());
            if(val.intValue() % 4 == 0 || val.intValue() % 3 == 0 ) {
                expectedSize--;
                iter.remove();
            }
        }
        assertEquals(expectedSize,(evens.size() + odds.size()));
    }   

}

