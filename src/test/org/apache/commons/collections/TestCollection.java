/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestCollection.java,v 1.1 2001/04/14 15:39:51 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 15:39:51 $
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
 * @author Rodney Waldhoff
 * @version $Id: TestCollection.java,v 1.1 2001/04/14 15:39:51 rwaldhoff Exp $
 */
public abstract class TestCollection extends TestCase {
    public TestCollection(String testName) {
        super(testName);
    }

    private Collection _collection = null;

    protected void setCollection(Collection c) {
        _collection = c;
    }

    // optional operation
    public void testCollectionAdd() {
        boolean added1 = false;
        try {
            added1 = _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }

        boolean added2 = false;
        try {
            added2 = _collection.add("element2");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
    }

    // optional operation
    public void testCollectionAddAll() {
        Collection col = new ArrayList();
        col.add("element1");
        col.add("element2");
        col.add("element3");
        boolean added = false;
        try {
            added = _collection.addAll(col);
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
        boolean cleared = false;
        try {
            _collection.clear();
            cleared = true;
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.clear should only throw UnsupportedOperationException. Found " + t.toString());
        }

        if(cleared) {
            assert("After Collection.clear(), Collection.isEmpty() should be true.",_collection.isEmpty());
        }

        boolean added = false;
        try {
            added = _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }

        if(added) {
            assert("After element is added, Collection.isEmpty() should be false.",!_collection.isEmpty());
            boolean cleared2 = false;
            try {
                _collection.clear();
                cleared2 = true;
            } catch(UnsupportedOperationException e) {
                // ignored, must not be supported
            } catch(Throwable t) {
                t.printStackTrace();
                fail("Collection.clear should only throw UnsupportedOperationException. Found " + t.toString());
            }
            if(cleared2) {
                assert("After Collection.clear(), Collection.isEmpty() should be true.",_collection.isEmpty());
            }
        }
    }

    public void testCollectionContains() {
        boolean added1 = false;
        try {
            added1 = _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        assert("If an element was added, it should be contained.",added1 == _collection.contains("element1"));

        boolean added2 = false;
        try {
            added2 = _collection.add("element2");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        assert("If an element was added, it should be contained.",added1 == _collection.contains("element1"));
        assert("If an element was added, it should be contained.",added2 == _collection.contains("element2"));
    }

    public void testCollectionContainsAll() {
        Collection col = new ArrayList();
        assert("Every Collection should contain all elements of an empty Collection.",_collection.containsAll(col));
        col.add("element1");
        assert("Empty Collection shouldn't contain all elements of a non-empty Collection.",!_collection.containsAll(col));

        boolean added1 = false;
        try {
            added1 = _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        if(added1) {
            assert("Should contain all.",_collection.containsAll(col));
        }

        col.add("element2");
        assert("Shouldn't contain all.",!_collection.containsAll(col));

        boolean added2 = false;
        try {
            added2 = _collection.add("element2");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        if(added1 && added2) {
            assert("Should contain all.",_collection.containsAll(col));
        }
    }

    public void testCollectionEquals() {
        assertEquals("A Collection should equal itself",_collection,_collection);
        try {
            _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        assertEquals("A Collection should equal itself",_collection,_collection);
        try {
            _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        assertEquals("A Collection should equal itself",_collection,_collection);
    }

    public void testCollectionHashCode() {
        assertEquals("A Collection's hashCode should equal itself",_collection.hashCode(),_collection.hashCode());
    }

    public void testCollectionIsEmpty() {
        assert("New Collection should be empty.",_collection.isEmpty());
        boolean added = false;
        try {
            added = _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        if(added) {
            assert("If an element was added, the Collection.isEmpty() should return false.",!_collection.isEmpty());
        }
    }

    public void testCollectionIterator() {
        Iterator it1 = _collection.iterator();
        assert("Iterator for empty Collection shouldn't have next.",!it1.hasNext());
        try {
            it1.next();
            fail("Iterator at end of Collection should throw NoSuchElementException when next is called.");
        } catch(NoSuchElementException e) {
            // expected
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.iterator.next() should only throw NoSuchElementException. Found " + t.toString());
        }

        boolean added = false;
        try {
            added = _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        if(added) {
            Iterator it2 = _collection.iterator();
            assert("Iterator for non-empty Collection should have next.",it2.hasNext());
            assertEquals("element1",it2.next());
            assert("Iterator at end of Collection shouldn't have next.",!it2.hasNext());
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
        boolean added = false;
        try {
            added = _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }

        try {
            assert("Shouldn't be able to remove an element that wasn't added.",!_collection.remove("element2"));
        } catch(UnsupportedOperationException e) {
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.remove should only throw UnsupportedOperationException. Found " + t.toString());
        }

        try {
            assert("If added, should be removed by call to remove.",added == _collection.remove("element1"));
            assert("If removed, shouldn't be contained.",!_collection.contains("element1"));
        } catch(UnsupportedOperationException e) {
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.remove should only throw UnsupportedOperationException. Found " + t.toString());
        }
    }

    // optional operation
    public void testCollectionRemoveAll() {
        assert("Initial Collection is empty.",_collection.isEmpty());
        try {
            _collection.removeAll(_collection);
        } catch(UnsupportedOperationException e) {
            // expected
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.removeAll should only throw UnsupportedOperationException. Found " + t.toString());
        }
        assert("Collection is still empty.",_collection.isEmpty());

        boolean added = false;
        try {
            added = _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        if(added) {
            assert("Collection is not empty.",!_collection.isEmpty());
            try {
                _collection.removeAll(_collection);
                assert("Collection is empty.",_collection.isEmpty());
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
        Collection col = new ArrayList();
        col.add("element1");
        col.add("element2");
        col.add("element3");
        boolean added = false;
        try {
            added = _collection.addAll(col);
            if(added) {
                added = _collection.add("element0");
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
            assert("Collection is not empty.",!_collection.isEmpty());
            try {
                assert("Should be changed",_collection.removeAll(col));
                assert("Collection is not empty.",!_collection.isEmpty());
                assert("Collection should contain element",_collection.contains("element0"));
                assert("Collection shouldn't contain removed element",!_collection.contains("element1"));
                assert("Collection shouldn't contain removed element",!_collection.contains("element2"));
                assert("Collection shouldn't contain removed element",!_collection.contains("element3"));
                assert("Collection shouldn't contain removed element",!_collection.contains("element4"));
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
    }

    public void testCollectionSize() {
        assertEquals("Size of new Collection is 0.",0,_collection.size());
        boolean added = false;
        try {
            added = _collection.add("element1");
        } catch(UnsupportedOperationException e) {
            // ignored, must not be supported
        } catch(ClassCastException e) {
            // ignored, type must not be supported
        } catch(IllegalArgumentException e) {
            // ignored, element must not be supported
        } catch(Throwable t) {
            t.printStackTrace();
            fail("Collection.add should only throw UnsupportedOperationException, ClassCastException or IllegalArgumentException. Found " + t.toString());
        }
        if(added) {
            assertEquals("If one element was added, the Collection.size() should be 1.",1,_collection.size());
        }
    }

    public void testCollectionToArray() {
    }

    public void testCollectionToArray2() {
    }
}
