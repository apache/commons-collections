/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/collection/TestCompositeCollection.java,v 1.2 2003/11/16 22:15:11 scolebourne Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
package org.apache.commons.collections.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Extension of {@link AbstractTestCollection} for exercising the 
 * {@link CompositeCollection} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/11/16 22:15:11 $
 * 
 * @author Brian McCallister
 * @author Phil Steitz
 */
public class TestCompositeCollection extends AbstractTestCollection {
    
    public TestCompositeCollection(String name) {
        super(name);
    }
    
    public static Test suite() {
        return new TestSuite(TestCompositeCollection.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestCompositeCollection.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
 
 //-----------------------------------------------------------------------------
    /**
     * Run stock collection tests without Mutator, so turn off add, remove
     */
    protected boolean isAddSupported() {
        return false;
    }
    
    protected boolean isRemoveSupported() {
        return false;
    }
    
    /**
     * Empty collection is empty composite
     */
    public Collection makeCollection() {
        return new CompositeCollection();
    }
    
    public Collection makeConfirmedCollection() {
        return new HashSet();
    }
    
    protected Object[] getFullElements() {
        return new Object[] {"1", "2", "3", "4"};
    }
    
    /**
     * Full collection consists of 5 collections, each with one element
     */
    protected Collection makeFullCollection() {
        CompositeCollection compositeCollection = new CompositeCollection();
        Object[] elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            Collection summand = new HashSet();
            summand.add(elements[i]);
            compositeCollection.addComposited(summand);
        }
        return compositeCollection;
    }
    
    /**
     * Full collection should look like a collection with 5 elements
     */
    protected Collection makeConfirmedFullCollection() {
        Collection collection = new HashSet();
        collection.addAll(Arrays.asList(getFullElements()));
        return collection;
    }
    
    /**
     * Override testUnsupportedRemove, since the default impl expects removeAll,
     * retainAll and iterator().remove to throw
     */
    public void testUnsupportedRemove() {    
        resetFull();
        try {
            collection.remove(null);
            fail("remove should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();
    }
    
    //--------------------------------------------------------------------------
    
    protected CompositeCollection c;
    protected Collection one;
    protected Collection two;
    
    protected void setUpTest() {
        c = new CompositeCollection();
        one = new HashSet();
        two = new HashSet();
    }
    
    protected void setUpMutatorTest() {
        setUpTest();
        c.setMutator(new CompositeCollection.CollectionMutator() {
            public boolean add(CompositeCollection composite, 
            Collection[] collections, Object obj) {
                for (int i = 0; i < collections.length; i++) {
                    collections[i].add(obj);
                }
                return true;
            }
            
            public boolean addAll(CompositeCollection composite, 
            Collection[] collections, Collection coll) {
                for (int i = 0; i < collections.length; i++) {
                    collections[i].addAll(coll);
                }
                return true;
            }
            
            public boolean remove(CompositeCollection composite, 
            Collection[] collections, Object obj) {
                for (int i = 0; i < collections.length; i++) {
                    collections[i].remove(obj);
                }
                return true;
            }
        });
    }
            
    public void testSize() {
        setUpTest();
        HashSet set = new HashSet();
        set.add("a");
        set.add("b");
        c.addComposited(set);
        assertEquals(set.size(), c.size());
    }
    
    public void testMultipleCollectionsSize() {
        setUpTest();
        HashSet set = new HashSet();
        set.add("a");
        set.add("b");
        c.addComposited(set);
        HashSet other = new HashSet();
        other.add("c");
        c.addComposited(other);
        assertEquals(set.size() + other.size(), c.size());
    }
    
    public void testIsEmpty() {
        setUpTest();
        assertTrue(c.isEmpty());
        HashSet empty = new HashSet();
        c.addComposited(empty);
        assertTrue(c.isEmpty());
        empty.add("a");
        assertTrue(!c.isEmpty());
    }
    
    
    public void testIterator() {
        setUpTest();
        one.add("1");
        two.add("2");
        c.addComposited(one);
        c.addComposited(two);
        Iterator i = c.iterator();
        Object next = i.next();
        assertTrue(c.contains(next));
        assertTrue(one.contains(next));
        next = i.next();
        i.remove();
        assertTrue(!c.contains(next));
        assertTrue(!two.contains(next));
    }
    
    public void testClear() {
        setUpTest();
        one.add("1");
        two.add("2");
        c.addComposited(one, two);
        c.clear();
        assertTrue(one.isEmpty());
        assertTrue(two.isEmpty());
        assertTrue(c.isEmpty());
    }
    
    public void testContainsAll() {
        setUpTest();
        one.add("1");
        two.add("1");
        c.addComposited(one);
        assertTrue(c.containsAll(two));
    }
    
    public void testRetainAll() {
        setUpTest();
        one.add("1");
        one.add("2");
        two.add("1");
        c.addComposited(one);
        c.retainAll(two);
        assertTrue(!c.contains("2"));
        assertTrue(!one.contains("2"));
        assertTrue(c.contains("1"));
        assertTrue(one.contains("1"));
    }
    
    public void testAddAllMutator() {
        setUpTest();
        c.setMutator(new CompositeCollection.CollectionMutator() {
            public boolean add(CompositeCollection composite, 
            Collection[] collections, Object obj) {
                for (int i = 0; i < collections.length; i++) {
                    collections[i].add(obj);
                }
                return true;
            }
            
            public boolean addAll(CompositeCollection composite, 
            Collection[] collections, Collection coll) {
                for (int i = 0; i < collections.length; i++) {
                    collections[i].addAll(coll);
                }
                return true;
            }
            
            public boolean remove(CompositeCollection composite, 
            Collection[] collections, Object obj) {
                return false;
            }
        });
        
        c.addComposited(one);
        two.add("foo");
        c.addAll(two);
        assertTrue(c.contains("foo"));
        assertTrue(one.contains("foo"));
    }
    
    public void testAddMutator() {
        setUpTest();
        c.setMutator(new CompositeCollection.CollectionMutator() {
            public boolean add(CompositeCollection composite, 
            Collection[] collections, Object obj) {
                for (int i = 0; i < collections.length; i++) {
                    collections[i].add(obj);
                }
                return true;
            }
            
            public boolean addAll(CompositeCollection composite, 
            Collection[] collections, Collection coll) {
                for (int i = 0; i < collections.length; i++) {
                    collections[i].addAll(coll);
                }
                return true;
            }
            
            public boolean remove(CompositeCollection composite, 
            Collection[] collections, Object obj) {
                return false;
            }
        });
        
        c.addComposited(one);
        c.add("foo");
        assertTrue(c.contains("foo"));
        assertTrue(one.contains("foo"));
    }
    
    public void testToCollection() {
        setUpTest();
        one.add("1");
        two.add("2");
        c.addComposited(one, two);
        Collection foo = c.toCollection();
        assertTrue(foo.containsAll(c));
        assertEquals(c.size(), foo.size());
        one.add("3");
        assertTrue(!foo.containsAll(c));
    }
    
    public void testAddAllToCollection() {
        setUpTest();
        one.add("1");
        two.add("2");
        c.addComposited(one, two);
        Collection toCollection = new HashSet();
        toCollection.addAll(c);
        assertTrue(toCollection.containsAll(c));
        assertEquals(c.size(), toCollection.size());
    }   
    
    public void testRemove() {
        setUpMutatorTest();
        one.add("1");
        two.add("2");
        two.add("1");
        c.addComposited(one, two);
        c.remove("1");
        assertTrue(!c.contains("1"));
        assertTrue(!one.contains("1"));
        assertTrue(!two.contains("1"));
    }
    
    public void testRemoveAll() {
        setUpMutatorTest();
        one.add("1");
        two.add("2");
        two.add("1");
        c.addComposited(one, two);
        c.removeAll(one);
        assertTrue(!c.contains("1"));
        assertTrue(!one.contains("1"));
        assertTrue(!two.contains("1"));
    }
    
    public void testRemoveComposited() {
        setUpMutatorTest();
        one.add("1");
        two.add("2");
        two.add("1");
        c.addComposited(one, two);    
        c.removeComposited(one);
        assertTrue(c.contains("1"));
        assertEquals(c.size(), 2);
    }
}
