/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestCollection.java,v 1.5 2001/07/14 23:33:27 craigmcc Exp $
 * $Revision: 1.5 $
 * $Date: 2001/07/14 23:33:27 $
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
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests base {@link java.util.Collection} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeCollection} method.
 * <p>
 * If your {@link Collection} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Collection} fails.
 *
 * @author Rodney Waldhoff
 * @version $Id: TestCollection.java,v 1.5 2001/07/14 23:33:27 craigmcc Exp $
 */
public abstract class TestCollection extends TestObject {
    public TestCollection(String testName) {
        super(testName);
    }

    /**
     * Return a new, empty {@link Collection} to used for testing.
     */
    public abstract Collection makeCollection();

    public Object makeObject() {
        return makeCollection();
    }

    // optional operation
    public void testCollectionAdd() {
        Collection c = makeCollection();
        boolean added1 = tryToAdd(c,"element1");
        boolean added2 = tryToAdd(c,"element2");
    }

    // optional operation
    public void testCollectionAddAll() {
        Collection c = makeCollection();
        Collection col = new ArrayList();
        col.add("element1");
        col.add("element2");
        col.add("element3");
        boolean added = false;
        try {
            added = c.addAll(col);
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.addAll should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
    }

    // optional operation
    public void testCollectionClear() {
        Collection c = makeCollection();
        boolean cleared = false;
        try {
            c.clear();
            cleared = true;
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.clear should only throw UnsupportedOperationException. Found " + t.toString());
        }

        if(cleared) {
            assertTrue("After Collection.clear(), Collection.isEmpty() should be true.",c.isEmpty());
        }

        boolean added = tryToAdd(c,"element1");

        if(added) {
            assertTrue("After element is added, Collection.isEmpty() should be false.",!c.isEmpty());
            boolean cleared2 = false;
            try {
                c.clear();
                cleared2 = true;
            } catch(UnsupportedOperationException e) {
                // ignored, must not be supported
            } catch(Throwable t) {
                t.printStackTrace();
                fail("Collection.clear should only throw UnsupportedOperationException. Found " + t.toString());
            }
            if(cleared2) {
                assertTrue("After Collection.clear(), Collection.isEmpty() should be true.",c.isEmpty());
            }
        }
    }

    public void testCollectionContains() {
        Collection c = makeCollection();
        assertTrue("Empty Collection shouldn't contain element.",!c.contains("element1"));
        boolean added1 = tryToAdd(c,"element1");
        assertTrue("[1] If an element was added, it should be contained, if it wasn't, it shouldn't.",added1 == c.contains("element1"));

        assertTrue("Shouldn't be contained.",!c.contains("element2"));
        boolean added2 = tryToAdd(c,"element2");
        assertTrue("[2] If an element was added, it should be contained, if it wasn't, it shouldn't.",added1 == c.contains("element1"));
        assertTrue("[3] If an element was added, it should be contained, if it wasn't, it shouldn't.",added2 == c.contains("element2"));
    }

    public void testCollectionContainsAll() {
        Collection c = makeCollection();
        Collection col = new ArrayList();
        assertTrue("Every Collection should contain all elements of an empty Collection.",c.containsAll(col));
        col.add("element1");
        assertTrue("Empty Collection shouldn't contain all elements of a non-empty Collection.",!c.containsAll(col));

        boolean added1 = tryToAdd(c,"element1");
        if(added1) {
            assertTrue("[1] Should contain all.",c.containsAll(col));
        }

        col.add("element2");
        assertTrue("Shouldn't contain all.",!c.containsAll(col));

        boolean added2 = tryToAdd(c,"element2");
        if(added1 && added2) {
            assertTrue("[2] Should contain all.",c.containsAll(col));
        }
    }

    public void testCollectionEqualsSelf() {
        Collection c = makeCollection();
        assertEquals("A Collection should equal itself",c,c);
        tryToAdd(c,"element1");
        assertEquals("A Collection should equal itself",c,c);
        tryToAdd(c,"element1");
        tryToAdd(c,"element2");
        assertEquals("A Collection should equal itself",c,c);
    }

    public void testCollectionEquals() {
        Collection c1 = makeCollection();
        Collection c2 = makeCollection();
        assertEquals("Empty Collections are equal.",c1,c2);

        boolean added1_1 = tryToAdd(c1,"element1");
        if(added1_1) {
            assertTrue("Empty Collection not equal to non-empty Collection.",!c2.equals(c1));
            assertTrue("Non-empty Collection not equal to empty Collection.",!c1.equals(c2));
        }

        boolean added1_2 = tryToAdd(c2,"element1");
        assertEquals("After duplicate adds, Collections should be equal.",c1,c2);

        boolean added2_1 = tryToAdd(c1,"element2");
        boolean added3_2 = tryToAdd(c2,"element3");
        if(added2_1 || added3_2) {
            assertTrue("Should not be equal.",!c1.equals(c2));
        }
    }

    public void testCollectionHashCodeEqualsSelfHashCode() {
        Collection c = makeCollection();
        assertEquals("hashCode should be repeatable",c.hashCode(),c.hashCode());
        tryToAdd(c,"element1");
        assertEquals("after add, hashCode should be repeatable",c.hashCode(),c.hashCode());
    }

    public void testCollectionHashCodeEqualsContract() {
        Collection c1 = makeCollection();
        if(c1.equals(c1)) {
            assertEquals("[1] When two objects are equal, their hashCodes should be also.",c1.hashCode(),c1.hashCode());
        }
        Collection c2 = makeCollection();
        if(c1.equals(c2)) {
            assertEquals("[2] When two objects are equal, their hashCodes should be also.",c1.hashCode(),c2.hashCode());
        }
        tryToAdd(c1,"element1");
        tryToAdd(c2,"element1");
        if(c1.equals(c2)) {
            assertEquals("[3] When two objects are equal, their hashCodes should be also.",c1.hashCode(),c2.hashCode());
        }
    }

    public void testCollectionIsEmpty() {
        Collection c = makeCollection();
        assertTrue("New Collection should be empty.",c.isEmpty());
        boolean added = tryToAdd(c,"element1");
        if(added) {
            assertTrue("If an element was added, the Collection.isEmpty() should return false.",!c.isEmpty());
        }
    }

    public void testCollectionIterator() {
        Collection c = makeCollection();
        Iterator it1 = c.iterator();
        assertTrue("Iterator for empty Collection shouldn't have next.",!it1.hasNext());
        try {
            it1.next();
            fail("Iterator at end of Collection should throw NoSuchElementException when next is called.");
        } catch(NoSuchElementException e) {
            // expected
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.iterator.next() should only throw NoSuchElementException. Found " + t.toString());
        }

        boolean added = tryToAdd(c,"element1");
        if(added) {
            Iterator it2 = c.iterator();
            assertTrue("Iterator for non-empty Collection should have next.",it2.hasNext());
            assertEquals("element1",it2.next());
            assertTrue("Iterator at end of Collection shouldn't have next.",!it2.hasNext());
            try {
                it2.next();
                fail("Iterator at end of Collection should throw NoSuchElementException when next is called.");
            } catch(NoSuchElementException e) {
                // expected
            } catch(Throwable t) {
                t.printStackTrace();
                fail("Collection.iterator.next() should only throw NoSuchElementException. Found " + t.toString());
            }
        }
    }

    // optional operation
    public void testCollectionRemove() {
        Collection c = makeCollection();
        boolean added = tryToAdd(c,"element1");

        try {
            assertTrue("Shouldn't be able to remove an element that wasn't added.",!c.remove("element2"));
        } catch(UnsupportedOperationException e) {
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.remove should only throw UnsupportedOperationException. Found " + t.toString());
        }

        try {
            assertTrue("If added, should be removed by call to remove.",added == c.remove("element1"));
            assertTrue("If removed, shouldn't be contained.",!c.contains("element1"));
        } catch(UnsupportedOperationException e) {
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.remove should only throw UnsupportedOperationException. Found " + t.toString());
        }
    }

    // optional operation
    public void testCollectionRemoveAll() {
        Collection c = makeCollection();
        assertTrue("Initial Collection is empty.",c.isEmpty());
        try {
            c.removeAll(c);
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.removeAll should only throw UnsupportedOperationException. Found " + t.toString());
        }
        assertTrue("Collection is still empty.",c.isEmpty());

        boolean added = tryToAdd(c,"element1");
        if(added) {
            assertTrue("Collection is not empty.",!c.isEmpty());
            try {
                c.removeAll(c);
                assertTrue("Collection is empty.",c.isEmpty());
            } catch(UnsupportedOperationException e) {
                // expected
            } catch(Throwable t) {
                t.printStackTrace();
                fail("Collection.removeAll should only throw UnsupportedOperationException. Found " + t.toString());
            }
        }
    }

    // optional operation
    public void testCollectionRemoveAll2() {
        Collection c = makeCollection();
        Collection col = new ArrayList();
        col.add("element1");
        col.add("element2");
        col.add("element3");
        boolean added = false;
        try {
            added = c.addAll(col);
            if(added) {
                added = c.add("element0");
            }
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.addAll should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        col.add("element4");
        if(added) {
            assertTrue("Collection is not empty.",!c.isEmpty());
            try {
                assertTrue("Should be changed",c.removeAll(col));
                assertTrue("Collection is not empty.",!c.isEmpty());
                assertTrue("Collection should contain element",c.contains("element0"));
                assertTrue("Collection shouldn't contain removed element",!c.contains("element1"));
                assertTrue("Collection shouldn't contain removed element",!c.contains("element2"));
                assertTrue("Collection shouldn't contain removed element",!c.contains("element3"));
                assertTrue("Collection shouldn't contain removed element",!c.contains("element4"));
            } catch(UnsupportedOperationException e) {
                // expected
            } catch(Throwable t) {
                t.printStackTrace();
                fail("Collection.removeAll should only throw UnsupportedOperationException. Found " + t.toString());
            }
        }
    }

    // optional operation
    public void testCollectionRetainAll() {
        Collection a = makeCollection();
        Collection b = makeCollection();
        try {
            assertTrue(!a.retainAll(b));
            assertTrue(!a.retainAll(a));
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.retainAll should only throw UnsupportedOperationException. Found " + t.toString());
        }

        boolean added_b = b.add("element1");
        try {
            assertTrue(!a.retainAll(b));
            assertTrue(added_b == b.retainAll(a));
            assertTrue(b.isEmpty());
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.retainAll should only throw UnsupportedOperationException. Found " + t.toString());
        }

        boolean added_b1 = b.add("element1");
        boolean added_b2 = b.add("element2");
        boolean added_a1 = a.add("element1");
        if(added_b1 && added_b2 && added_a1) {
            try {
                assertTrue(!b.retainAll(b));
                assertTrue(b.contains("element1"));
                assertTrue(b.contains("element2"));

                assertTrue(!a.retainAll(b));

                assertTrue(b.retainAll(a));
                assertTrue(b.contains("element1"));
                assertTrue(!b.contains("element2"));
            } catch(UnsupportedOperationException e) {
                // expected
            } catch(Throwable t) {
                t.printStackTrace();
                fail("Collection.retainAll should only throw UnsupportedOperationException. Found " + t.toString());
            }
        }
    }

    public void testCollectionSize() {
        Collection c = makeCollection();
        assertEquals("Size of new Collection is 0.",0,c.size());
        boolean added = tryToAdd(c,"element1");
        if(added) {
            assertEquals("If one element was added, the Collection.size() should be 1.",1,c.size());
        }
    }

    public void testCollectionToArray() {
        Collection c = makeCollection();
        assertEquals("Empty Collection should return empty array for toArray",0,c.toArray().length);
        boolean added = tryToAdd(c,"element1");
        if(added) {
            assertEquals("If an element was added, the Collection.toArray().length should be 1.",1,c.toArray().length);
        } else {
            assertEquals("Empty Collection should return empty array for toArray",0,c.toArray().length);
        }

        boolean added2 = tryToAdd(c,"element2");
        if(added && added2) {
            assertEquals("If another element was added, the Collection.toArray().length should be 2.",2,c.toArray().length);
        } else if(added2) {
            assertEquals("If an element was added, the Collection.toArray().length should be 1.",1,c.toArray().length);
        } else {
            assertEquals("Empty Collection should return empty array for toArray",0,c.toArray().length);
        }
    }

    public void testCollectionToArray2() {
        Collection c = makeCollection();
        assertEquals(10,c.toArray(new String[10]).length);
        assertEquals(7,c.toArray(new Object[7]).length);
        boolean added1 = tryToAdd(c,"element1");
        if(added1) {
            String[] fits = new String[1];
            String[] small = new String[0];
            assertSame(fits,c.toArray(fits));
            assertTrue(small != c.toArray(small));
        }
    }

    /**
     * Try to add the given object to the given Collection.
     * Returns <tt>true</tt> if the element was added,
     * <tt>false</tt> otherwise.
     *
     * Fails any Throwable except UnsupportedOperationException,
     * ClassCastException, or IllegalArgumentException is thrown.
     */
    protected boolean tryToAdd(Collection c,Object obj) {
        try {
            return c.add(obj);
        } catch(UnsupportedOperationException e) {
            return false;
        } catch(ClassCastException e) {
            return false;
        } catch(IllegalArgumentException e) {
            return false;
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
            return false; // never get here, since fail throws exception
        }
    }
}
