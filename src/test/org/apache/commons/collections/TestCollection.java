/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestCollection.java,v 1.6 2002/06/18 01:14:23 mas Exp $
 * $Revision: 1.6 $
 * $Date: 2002/06/18 01:14:23 $
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * Tests base {@link java.util.Collection} methods and contracts.
 * <p>
 * You should create a concrete subclass of this class to test any custom
 * {@link Collection} implementation.  At minimum, you'll have to 
 * implement the {@link #makeCollection} method.  You might want to 
 * override some of the additional protected methods as well:<P>
 *
 * <B>Element Population Methods</B><P>
 * 
 * Override these if your collection restricts what kind of elements are
 * allowed (for instance, if <Code>null</Code> is not permitted):
 * <UL>
 * <Li>{@link #getFullElements}
 * <Li>{@link #getOtherElements}
 * </UL>
 *
 * <B>Supported Operation Methods</B><P>
 *
 * Override these if your collection doesn't support certain operations:
 * <UL>
 * <LI>{@link #supportsAdd}
 * <LI>{@link #supportsRemove}
 * </UL>
 *
 * <B>Fixture Methods</B><P>
 *
 * For tests on modification operations (adds and removes), fixtures are 
 * used to verify that the the operation results in correct state for the
 * collection.  Basically, the modification is performed against your 
 * collection implementation, and an identical modification is performed
 * against a <I>confirmed</I> collection implementation.  A confirmed 
 * collection implementation is something like 
 * <Code>java.util.ArrayList</Code>, which is known to conform exactly to
 * its collection interface's contract.  After the modification takes 
 * place on both your collection implementation and the confirmed 
 * collection implementation, the two collections are compared to see if
 * their state is identical.  The comparison is usually much more 
 * involved than a simple <Code>equals</Code> test.<P>
 *
 * The {@link #collection} field holds an instance of your collection
 * implementation; the {@link #confirmed} field holds an instance of the
 * confirmed collection implementation.  The {@link #resetEmpty} and
 * {@link #resetFull} methods set these fields to empty or full collections,
 * so that tests can proceed from a known state.<P>
 *
 * After a modification operation to both {@link #collection} and
 * {@link #confirmed}, the {@link #verify} method is invoked to compare
 * the results.  You may want to override {@link #verify} to perform
 * additional verifications.  For instance, when testing the collection
 * views of a map, {@link TestMap} overrides {@link #verify} to make sure
 * the map is changed after the collection view is changed.
 *
 * If you're extending this class directly, you will have to provide 
 * implementations for the following:
 * <UL>
 * <LI>{@link #makeConfirmedCollection()}
 * <LI>{@link #makeConfirmedFullCollection()}
 * </UL>
 *
 * Those methods should provide a confirmed collection implementation 
 * that's compatible with your collection implementation.<P>
 *
 * If you're extending {@link TestList}, {@link TestSet},
 * or {@link TestBag}, you probably don't have to worry about the
 * above methods, because those three classes already override the methods
 * to provide standard JDK confirmed collections.<P>
 *
 * <B>Other notes</B><P>
 *
 * If your {@link Collection} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Collection} fails.  For instance, the
 * {@link #testIteratorFailFast} method is provided since most collections
 * have fail-fast iterators; however, that's not strictly required by the
 * collection contract, so you may want to override that method to do 
 * nothing.<P>
 *
 * @author Rodney Waldhoff
 * @author Paul Jack
 * @version $Id: TestCollection.java,v 1.6 2002/06/18 01:14:23 mas Exp $
 */
public abstract class TestCollection extends TestObject {

    // These fields are used by reset() and verify(), and any test
    // method that tests a modification.

    /** 
     *  A collection instance that will be used for testing.
     */
    protected Collection collection;

    /** 
     *  Confirmed collection.  This is an instance of a collection that is
     *  confirmed to conform exactly to the java.util.Collection contract.
     *  Modification operations are tested by performing a mod on your 
     *  collection, performing the exact same mod on an equivalent confirmed
     *  collection, and then calling verify() to make sure your collection
     *  still matches the confirmed collection.
     */
    protected Collection confirmed;


    public TestCollection(String testName) {
        super(testName);
    }


    /**
     *  Resets the {@link #collection} and {@link #confirmed} fields to empty
     *  collections.  Invoke this method before performing a modification
     *  test.
     */
    protected void resetEmpty() {
        this.collection = makeCollection();
        this.confirmed = makeConfirmedCollection();
    }


    /**
     *  Resets the {@link #collection} and {@link #confirmed} fields to empty
     *  collections.  Invoke this method before performing a modification
     *  test.
     */
    protected void resetFull() {
        this.collection = makeFullCollection();
        this.confirmed = makeConfirmedFullCollection();
    }


    /**
     *  Verifies that {@link #collection} and {@link #confirmed} have 
     *  identical state.
     */
    protected void verify() {
        assertEquals("Collection size should match confirmed collection's",
                     confirmed.size(), collection.size());
        assertEquals("Collection isEmpty() result should match confirmed " +
                     " collection's", 
                     confirmed.isEmpty(), collection.isEmpty());
        Bag bag1 = new HashBag(confirmed);
        Bag bag2 = new HashBag(collection);
        assertEquals("Collections should contain same elements with " + 
                     " the same cardinality", bag1, bag2);
    }
    
    
    /**
     *  Returns a confirmed empty collection.
     *  For instance, an {@link java.util.ArrayList} for lists or a
     *  {@link java.util.HashSet} for sets.
     *
     *  @return a confirmed empty collection
     */
    protected abstract Collection makeConfirmedCollection();



    /**
     *  Returns a confirmed full collection.
     *  For instance, an {@link java.util.ArrayList} for lists or a
     *  {@link java.util.HashSet} for sets.  The returned collection
     *  should contain the elements returned by {@link #getFullElements}.
     *
     *  @return a confirmed full collection
     */
    protected abstract Collection makeConfirmedFullCollection();


    /**
     *  Returns true if the collections produced by 
     *  {@link #makeCollection} and {@link #makeFullCollection}
     *  support the <Code>add</Code> and <Code>addAll</Code>
     *  operations.<P>
     *  Default implementation returns true.  Override if your collection
     *  class does not support add or addAll.
     */
    protected boolean supportsAdd() {
        return true;
    }


    /**
     *  Returns true if the collections produced by 
     *  {@link #makeCollection} and {@link #makeFullCollection}
     *  support the <Code>remove</Code>, <Code>removeAll</Code>,
     *  <Code>retainAll</Code>, <Code>clear</Code> and
     *  <Code>iterator().remove</Code> methods.
     *  Default implementation returns true.  Override if your collection
     *  class does not support removal operations.
     */
    protected boolean supportsRemove() {
        return true;
    }


    /**
     *  Returns an array of objects that are contained in a collection
     *  produced by {@link #makeFullCollection}.  Every element in the
     *  returned array <I>must</I> be an element in a full collection.<P>
     *  The default implementation returns a heterogenous array of 
     *  objects with some duplicates and with the null element.  
     *  Override if you require specific testing elements.  Note that if you
     *  override {@link #makeFullCollection}, you <I>must</I> override
     *  this method to reflect the contents of a full collection.
     */
    protected Object[] getFullElements() {
        ArrayList list = new ArrayList();
        list.addAll(Arrays.asList(getFullNonNullElements()));
        list.add(4, null);
        return list.toArray();
    }


    /**
     *  Returns an array of elements that are <I>not</I> contained in a
     *  full collection.  Every element in the returned array must 
     *  not exist in a collection returned by {@link #makeFullCollection}.
     *  The default implementation returns a heterogenous array of elements
     *  without null.  Note that some of the tests add these elements
     *  to an empty or full collection, so if your collection restricts
     *  certain kinds of elements, you should override this method.
     */
    protected Object[] getOtherElements() {
        return getOtherNonNullElements();
    }
    

    /**
     * Return a new, empty {@link Collection} to be used for testing.
     */
    protected abstract Collection makeCollection();


    /**
     *  Returns a full collection to be used for testing.  The collection
     *  returned by this method should contain every element returned by
     *  {@link #getFullElements}.  The default implementation, in fact,
     *  simply invokes <Code>addAll</Code> on an empty collection with
     *  the results of {@link #getFullElements}.  Override this default
     *  if your collection doesn't support addAll.
     */
    protected Collection makeFullCollection() {
        Collection c = makeCollection();
        c.addAll(Arrays.asList(getFullElements()));
        return c;
    }


    /**
     *  Returns an empty collection for Object tests.
     */
    public Object makeObject() {
        return makeCollection();
    }


    /**
     *  Tests {@link Collection#add}.
     */
    public void testCollectionAdd() {
        if (!supportsAdd()) return;
        
        Object[] elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            resetEmpty();
            boolean r = collection.add(elements[i]);
            confirmed.add(elements[i]);
            verify();
            assertTrue("Empty collection changed after add", r);
            assertTrue("Collection size is 1 after first add", 
                       collection.size() == 1);
        }
        
        resetEmpty();
        int size = 0;
        for (int i = 0; i < elements.length; i++) {
            boolean r = collection.add(elements[i]);
            confirmed.add(elements[i]);
            verify();
            if (r) size++;
            assertEquals("Collection size should grow after add", 
                         size, collection.size());
            assertTrue("Collection should contain added element",
                       collection.contains(elements[i]));
        }
    }
    
    
    /**
     *  Tests {@link Collection#addAll}.
     */
    public void testCollectionAddAll() {
        if (!supportsAdd()) return;

        resetEmpty();
        Object[] elements = getFullElements();
        boolean r = collection.addAll(Arrays.asList(elements));
        confirmed.addAll(Arrays.asList(elements));
        verify();
        assertTrue("Empty collection should change after addAll", r);
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Collection should contain added element",
                       collection.contains(elements[i]));
        }

        resetFull();
        int size = collection.size();
        elements = getOtherElements();
        r = collection.addAll(Arrays.asList(elements));
        confirmed.addAll(Arrays.asList(elements));
        verify();
        assertTrue("Full collection should change after addAll", r);
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Full collection should contain added element",
                       collection.contains(elements[i]));
        }
        assertEquals("Size should increase after addAll", 
                     size + elements.length, collection.size());

        resetFull();
        size = collection.size();
        r = collection.addAll(Arrays.asList(getFullElements()));
        confirmed.addAll(Arrays.asList(getFullElements()));
        verify();
        if (r) {
            assertTrue("Size should increase if addAll returns true", 
                       size < collection.size());
        } else {
            assertTrue("Size should not change if addAll returns false",
                       size == collection.size());
        } 
    }


    /**
     *  If {@link #supportsAdd} returns false, tests that add operations
     *  raise <Code>UnsupportedOperationException.
     */
    public void testUnsupportedAdd() {
        if (supportsAdd()) return;
        
        try {
            makeCollection().add(new Object());
            fail("Emtpy collection should not support add.");
        } catch (UnsupportedOperationException e) {
            // expected
        }

        try {
            makeCollection().addAll(Arrays.asList(getFullElements()));
            fail("Emtpy collection should not support addAll.");
        } catch (UnsupportedOperationException e) {
            // expected
        }

        try {
            makeFullCollection().add(new Object());
            fail("Full collection should not support add.");
        } catch (UnsupportedOperationException e) {
            // expected
        }

        try {
            makeFullCollection().addAll(Arrays.asList(getOtherElements()));
            fail("Full collection should not support addAll.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }


    /**
     *  Test {@link Collection#clear}.
     */
    public void testCollectionClear() {
        if (!supportsRemove()) return;

        resetEmpty();
        collection.clear(); // just to make sure it doesn't raise anything
        verify();

        resetFull();
        collection.clear();
        confirmed.clear();
        verify();
    }    

    
    /**
     *  Tests {@link Collection#contains}.
     */
    public void testCollectionContains() {
        Collection c = makeCollection();
        ArrayList elements = new ArrayList();
        elements.addAll(Arrays.asList(getFullElements()));
        elements.addAll(Arrays.asList(getOtherElements()));
        Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            assertTrue("Empty collection shouldn't contain element", 
                       !c.contains(iter.next()));
        }
        
        elements.clear();
        elements.addAll(Arrays.asList(getFullElements()));
        c = makeFullCollection();
        iter = elements.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            assertTrue("Full collection should contain element " + o, 
                       c.contains(o));
        }
        
        elements.clear();
        elements.addAll(Arrays.asList(getOtherElements()));
        iter = elements.iterator();
        while (iter.hasNext()) {
            assertTrue("Full collection shouldn't contain element", 
                       !c.contains(iter.next()));
        }
    }


    /**
     *  Tests {@link Collection#containsAll}.
     */
    public void testCollectionContainsAll() {
        Collection c = makeCollection();
        Collection col = new HashSet();
        assertTrue("Every Collection should contain all elements of an " +
                   "empty Collection.",c.containsAll(col));
        col.addAll(Arrays.asList(getOtherElements()));
        assertTrue("Empty Collection shouldn't contain all elements of " +
                   "a non-empty Collection.",!c.containsAll(col));
        
        c = makeFullCollection();
        assertTrue("Full collection shouldn't contain other elements", 
                   !c.containsAll(col));
        
        col.clear();
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue("Full collection should containAll full elements " + 
                   c + " " + col, c.containsAll(col));
        col = Arrays.asList(getFullElements()).subList(2, 5);
        assertTrue("Full collection should containAll partial full " +
                   "elements", c.containsAll(col));
        assertTrue("Full collection should containAll itself", 
                   c.containsAll(c));
        
        col = new ArrayList();
        col.addAll(Arrays.asList(getFullElements()));
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue("Full collection should containAll duplicate full " +
                   "elements", c.containsAll(col));
    }


    /* ---------------------------------

     // Got rid of the equals() tests -- Collection doesn't define
     // any semantics for equals, and recommends you use reference-based
     // default behavior of Object.equals.  (And a test for that already
     // exists in TestObject).  Tests for equality of lists,
     // sets and bags will have to be written in test subclasses.
     
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

    -------------------------- */


    /**
     *  Tests {@link Collection#isEmpty}.
     */
    public void testCollectionIsEmpty() {
        Collection c = makeCollection();
        assertTrue("New Collection should be empty.",c.isEmpty());

        c =  makeFullCollection();
        assertTrue("Full collection shouldn't be empty", !c.isEmpty());
    }


    /**
     *  Tests the read-only functionality of {@link Collection#iterator}.
     */
    public void testCollectionIterator() {
        Collection c = makeCollection();
        Iterator it1 = c.iterator();
        assertTrue("Iterator for empty Collection shouldn't have next.",
                   !it1.hasNext());
        try {
            it1.next();
            fail("Iterator at end of Collection should throw " +
                 "NoSuchElementException when next is called.");
        } catch(NoSuchElementException e) {
            // expected
        } 
        
        c = makeFullCollection();
        it1 = c.iterator();
        for (int i = 0; i < c.size(); i++) {
            assertTrue("Iterator for full collection should haveNext", 
                       it1.hasNext());
            it1.next();
        }
        assertTrue("Iterator should be finished", !it1.hasNext());
        
        ArrayList list = new ArrayList();
        it1 = c.iterator();
        for (int i = 0; i < c.size(); i++) {
            Object next = it1.next();
            assertTrue("Collection should contain element returned by " +
                       "its iterator", c.contains(next));
            list.add(next);
        }
        try {
            it1.next();
            fail("iterator.next() should raise NoSuchElementException " +
                 "after it finishes");
        } catch (NoSuchElementException e) {
            // expected
        }
        
        /*
           Removed -- TestSet, TestBag and TestList should do this
        Collection elements = Arrays.asList(getFullElements());
        if (c instanceof Set) {
            assertTrue("Iterator should return unique elements", 
              new HashSet(list).equals(new HashSet(elements)));
        }
        if (c instanceof List) {
            assertTrue("Iterator should return sequenced elements",
              list.equals(elements));
        }
        if (c instanceof Bag) {
            assertTrue("Iterator should return duplicate elements",
              new HashBag(list).equals(new HashBag(elements)));
        }

        */
    }


    /**
     *  Tests removals from {@link Collection#iterator}.
     */
    public void testCollectionIteratorRemove() {
        if (!supportsRemove()) return;

        resetEmpty();
        try {
            collection.iterator().remove();
            fail("New iterator.remove should raise IllegalState");
        } catch (IllegalStateException e) {
            // expected
        }

        try {
            Iterator iter = collection.iterator();
            iter.hasNext();
            iter.remove();
            fail("New iterator.remove should raise IllegalState " +
                 "even after hasNext");
        } catch (IllegalStateException e) {
            // expected
        }

        resetFull();
        int size = collection.size();
        HashBag bag = new HashBag(collection);
        Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            bag.remove(o, 1);
            iter.remove();
            if ((collection instanceof Set) || (collection instanceof List) ||
                (collection instanceof Bag)) {
                // Unfortunately, we can't get away with this for a straight
                // collection that might have unordered duplicate elements,
                // but it works for Bag, Set and List.
                confirmed.remove(o);
                verify();
            }
            size--;
            assertEquals("Collection should shrink after iterator.remove",
                         collection.size(), size);
            if (bag.getCount(o) == 0) {
                assertTrue("Collection shouldn't contain element after " +
                           "iterator.remove", !collection.contains(o));
            } else {
                assertTrue("Collection should still contain element after " +
                           "iterator.remove", collection.contains(o));
            }
        }
        assertTrue("Collection should be empty after iterator purge",
                   collection.isEmpty());
        
        resetFull();
        iter = collection.iterator();
        iter.next();
        iter.remove();
        try {
            iter.remove();
            fail("Second iter.remove should raise IllegalState");
        } catch (IllegalStateException e) {
            // expected
        }
    }


    /**
     *  Tests {@link Collection#remove}.
     */
    public void testCollectionRemove() {
        if (!supportsRemove()) return;

        resetEmpty();
        Object[] elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Shouldn't remove nonexistent element", 
                       !collection.remove(elements[i]));
            verify();
        }
        
        Object[] other = getOtherElements();
        
        resetFull();
        for (int i = 0; i < other.length; i++) {
            assertTrue("Shouldn't remove nonexistent other element", 
                       !collection.remove(other[i]));
            verify();
        }
        
        int size = collection.size();
        for (int i = 0; i < elements.length; i++) {
            resetFull();
            HashBag bag = new HashBag(collection);
            assertTrue("Collection should remove extant element",
                       collection.remove(elements[i]));
            if ((collection instanceof Set) || (collection instanceof List) ||
                (collection instanceof Bag)) {
                // Can't do this for unordered straight collection...
                confirmed.remove(elements[i]);
                verify();
            }
            assertEquals("Collection should shrink after remove", 
                         size - 1, collection.size());
            bag.remove(elements[i], 1);
            if (bag.getCount(elements[i]) == 0) {
                assertTrue("Collection shouldn't contain removed element",
                           !collection.contains(elements[i]));
            } else {
                assertTrue("Collection should still contain removed element",
                           collection.contains(elements[i]));
            }
        }
    }
    

    /**
     *  Tests {@link Collection#removeAll}.
     */
    public void testCollectionRemoveAll() {
        if (!supportsRemove()) return;

        resetEmpty();
        assertTrue("Emtpy collection removeAll should return false for " +
                   "empty input", 
                   !collection.removeAll(Collections.EMPTY_SET));
        verify();
        
        assertTrue("Emtpy collection removeAll should return false for " +
                   "nonempty input", 
                   !collection.removeAll(new ArrayList(collection)));
        verify();

        resetFull();
        assertTrue("Full collection removeAll should return false for " + 
                   "empty input", 
                   !collection.removeAll(Collections.EMPTY_SET));
        verify();
        
        assertTrue("Full collection removeAll should return false for " +
                   "other elements", 
                   !collection.removeAll(Arrays.asList(getOtherElements())));
        verify();
        
        assertTrue("Full collection removeAll should return true for " +
                   "full elements", 
                   collection.removeAll(new HashSet(collection)));
        confirmed.removeAll(new HashSet(confirmed));
        verify();
        
        resetFull();
        int size = collection.size();
        Collection all = Arrays.asList(getFullElements()).subList(2, 5);
        assertTrue("Full collection removeAll should work", 
                   collection.removeAll(all));
        confirmed.removeAll(all);
        verify();
        
        assertTrue("Collection should shrink after removeAll", 
                   collection.size() < size);
        Iterator iter = all.iterator();
        while (iter.hasNext()) {
            assertTrue("Collection shouldn't contain removed element",
                       !collection.contains(iter.next()));
        }
    }


    /**
     *  Tests {@link Collection#retainAll}.
     */
    public void testCollectionRetainAll() {
        if (!supportsRemove()) return;

        resetEmpty();
        List elements = Arrays.asList(getFullElements());
        List other = Arrays.asList(getOtherElements());

        assertTrue("Empty retainAll() should return false", 
                   !collection.retainAll(Collections.EMPTY_SET));
        verify();
        
        assertTrue("Empty retainAll() should return false", 
                   !collection.retainAll(elements));
        verify();
        
        resetFull();
        assertTrue("Collection should change from retainAll empty", 
                   collection.retainAll(Collections.EMPTY_SET));
        confirmed.retainAll(Collections.EMPTY_SET);
        verify();
        
        resetFull();
        assertTrue("Collection changed from retainAll other", 
                   collection.retainAll(other));
        confirmed.retainAll(other);
        verify();
        
        resetFull();
        int size = collection.size();
        assertTrue("Collection shouldn't change from retainAll elements",
                   !collection.retainAll(elements));
        verify();
        assertEquals("Collection size shouldn't change", size, 
                     collection.size());
        
        resetFull();
        size = collection.size();
        assertTrue("Collection should changed by partial retainAll",
                   collection.retainAll(elements.subList(2, 5)));
        confirmed.retainAll(elements.subList(2, 5));
        verify();
        
        Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            assertTrue("Collection only contains retained element", 
                       elements.subList(2, 5).contains(iter.next()));
        }
        
        resetFull();
        HashSet set = new HashSet(elements);
        size = collection.size();
        assertTrue("Collection shouldn't change from retainAll without " +
                   "duplicate elements", !collection.retainAll(set));
        verify();
        assertEquals("Collection size didn't change from nonduplicate " +
                     "retainAll", size, collection.size());
    }
    
    
    /**
     *  Tests {@link Collection#size}.
     */
    public void testCollectionSize() {
        Collection c = makeCollection();
        assertEquals("Size of new Collection is 0.",0,c.size());

        c = makeFullCollection();
        assertTrue("Size of full collection should be nonzero", c.size() != 0);
    }


    /**
     *  Tests {@link Collection#toArray()}.
     */
    public void testCollectionToArray() {
        Collection c = makeCollection();
        assertEquals("Empty Collection should return empty array for toArray",
                     0, c.toArray().length);

        c = makeFullCollection();
        HashBag bag = new HashBag(c);
        Object[] array = c.toArray();
        assertEquals("Full collection toArray should be same size as " +
                     "collection", array.length, c.size());
        for (int i = 0; i < array.length; i++) {
            assertTrue("Collection should contain element in toArray",
                       c.contains(array[i]));
            bag.remove(array[i], 1);
        }
        assertTrue("Collection should return all its elements in toArray",
                   bag.isEmpty());
    }


    /**
     *  Tests {@link Collection.toArray(Object[])}.
     */
    public void testCollectionToArray2() {
        Collection c = makeCollection();
        Object[] a = new Object[] { new Object(), null, null };
        Object[] array = c.toArray(a);
        assertEquals("Given array shouldn't shrink", array, a);
        assertEquals("Last element should be set to null", a[0], null);
        
        c = makeFullCollection();
        try {
            array = c.toArray(new Void[0]);
            fail("toArray(new Void[0]) should raise ArrayStore");
        } catch (ArrayStoreException e) {
            // expected
        }

        try {
            array = c.toArray(null);
            fail("toArray(null) should raise NPE");
        } catch (NullPointerException e) {
            // expected
        }
        
        array = c.toArray(new Object[0]);
        a = c.toArray();
        assertEquals("toArrays should be equal", 
                     Arrays.asList(array), Arrays.asList(a));

        // Figure out if they're all the same class
        // TODO: It'd be nicer to detect a common superclass
        HashSet classes = new HashSet();
        for (int i = 0; i < array.length; i++) {
            classes.add((array[i] == null) ? null : array[i].getClass());
        }
        if (classes.size() > 1) return;
        
        Class cl = (Class)classes.iterator().next();
        a = (Object[])Array.newInstance(cl, 0);
        array = c.toArray(a);
        assertEquals("toArray(Object[]) should return correct array type",
                     a.getClass(), array.getClass());
        assertEquals("type-specific toArrays should be equal", 
                     Arrays.asList(array), Arrays.asList(c.toArray()));
    }


    /**
     *  Tests <Code>toString</Code> on a collection.
     */
    public void testCollectionToString() {
        Collection c = makeCollection();
        assertTrue("toString shouldn't return null", c.toString() != null);

        c = makeFullCollection();
        assertTrue("toString shouldn't return null", c.toString() != null);
    }


    /**
     *  If supportsRemove() returns false, tests to see that remove
     *  operations raise an UnsupportedOperationException.
     */
    public void testUnsupportedRemove() {
        if (supportsRemove()) return;

        try {
            makeCollection().clear();
            fail("clear should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }

        try {
            makeCollection().remove(null);
            fail("remove should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }

        try {
            makeCollection().removeAll(null);
            fail("removeAll should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }

        try {
            makeCollection().retainAll(null);
            fail("removeAll should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }

        try {
            Collection c = makeFullCollection();
            Iterator iterator = c.iterator();
            iterator.next();
            iterator.remove();
            fail("iterator.remove should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }

    }


    /**
     *  Tests that the collection's iterator is fail-fast.  
     */
    public void testCollectionIteratorFailFast() {
        if (supportsAdd()) {
            try {
                Collection c = makeFullCollection();
                Iterator iter = c.iterator();
                c.add(getOtherElements()[0]);
                iter.next();
                fail("next after add should raise ConcurrentModification");
            } catch (ConcurrentModificationException e) {
                // expected
            }
            
            try {
                Collection c = makeFullCollection();
                Iterator iter = c.iterator();
                c.addAll(Arrays.asList(getOtherElements()));
                iter.next();
                fail("next after addAll should raise ConcurrentModification");
            } catch (ConcurrentModificationException e) {
                // expected
            }
        }

        if (!supportsRemove()) return;

        try {
            Collection c = makeFullCollection();
            Iterator iter = c.iterator();
            c.clear();
            iter.next();
            fail("next after clear should raise ConcurrentModification");
        } catch (ConcurrentModificationException e) {
            // expected
        } catch (NoSuchElementException e) {
            // (also legal given spec)
        }
        
        try {
            Collection c = makeFullCollection();
            Iterator iter = c.iterator();
            c.remove(getFullElements()[0]);
            iter.next();
            fail("next after remove should raise ConcurrentModification");
        } catch (ConcurrentModificationException e) {
            // expected
        }

        try {
            Collection c = makeFullCollection();
            Iterator iter = c.iterator();
            c.removeAll(Arrays.asList(getFullElements()).subList(2,5));
            iter.next();
            fail("next after removeAll should raise ConcurrentModification");
        } catch (ConcurrentModificationException e) {
            // expected
        }

        try {
            Collection c = makeFullCollection();
            Iterator iter = c.iterator();
            c.retainAll(Arrays.asList(getFullElements()).subList(2,5));
            iter.next();
            fail("next after retainAll should raise ConcurrentModification");
        } catch (ConcurrentModificationException e) {
            // expected
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
        // FIXME: Delete this method after TestList is patched
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


    
    /**
     *  Returns a list of elements suitable for return by
     *  {@link getFullElements}.  The array returned by this method
     *  does not include null, but does include a variety of objects 
     *  of different types.  Override getFullElements to return
     *  the results of this method if your collection does not support
     *  the null element.
     */
    public static Object[] getFullNonNullElements() {
        return new Object[] {
            new String(""),
            new String("One"),
            new Integer(2),
            "Three",
            new Integer(4),
            "One",
            new Double(5),
            new Float(6),
            "Seven",
            "Eight",
            new String("Nine"),
            new Integer(10),
            new Short((short)11),
            new Long(12),
            "Thirteen",
            "14",
            "15",
            new Byte((byte)16)
        };
    }


    /**
     *  Returns the default list of objects returned by 
     *  {@link getOtherElements}.  Includes many objects
     *  of different types.
     */
    public static Object[] getOtherNonNullElements() {
        return new Object[] {
            new Integer(0),
            new Float(0),
            new Double(0),
            "Zero",
            new Short((short)0),
            new Byte((byte)0),
            new Long(0),
            new Character('\u0000'),
            "0"
        };
    }



    /**
     *  Returns a list of string elements suitable for return by
     *  {@link getFullElements}.  Override getFullElements to return
     *  the results of this method if your collection does not support
     *  heterogenous elements or the null element.
     */
    public static Object[] getFullNonNullStringElements() {
        return new Object[] {
            "If","the","dull","substance","of","my","flesh","were","thought",
            "Injurious","distance","could","not","stop","my","way",
        };
    }


    /**
     *  Returns a list of string elements suitable for return by
     *  {@link getOtherElements}.  Override getOtherElements to return
     *  the results of this method if your collection does not support
     *  heterogenous elements or the null element.
     */
    public static Object[] getOtherNonNullStringElements() {
        return new Object[] {
            "For","then","despite",/* of */"space","I","would","be","brought",
            "From","limits","far","remote","where","thou","dost","stay"
        };
    }
}
