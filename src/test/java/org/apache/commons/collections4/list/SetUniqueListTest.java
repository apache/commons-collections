/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * JUnit tests.
 *
 * @since 3.0
 * @version $Id$
 */
public class SetUniqueListTest<E> extends AbstractListTest<E> {

    public SetUniqueListTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public List<E> makeObject() {
        return new SetUniqueList<E>(new ArrayList<E>(), new HashSet<E>());
    }

    //-----------------------------------------------------------------------
    @Override
    public void testListIteratorSet() {
        // override to block
        resetFull();
        final ListIterator<E> it = getCollection().listIterator();
        it.next();
        try {
            it.set(null);
            fail();
        } catch (final UnsupportedOperationException ex) {}
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] getFullNonNullElements() {
        // override to avoid duplicate "One"
        return (E[]) new Object[] {
                new String(""),
                new String("One"),
                Integer.valueOf(2),
                "Three",
                Integer.valueOf(4),
                new Double(5),
                new Float(6),
                "Seven",
                "Eight",
                new String("Nine"),
                Integer.valueOf(10),
                new Short((short)11),
                new Long(12),
                "Thirteen",
                "14",
                "15",
                new Byte((byte)16)
        };
    }

    @Override
    public void testListIteratorAdd() {
        // override to cope with Set behaviour
        resetEmpty();
        final List<E> list1 = getCollection();
        final List<E> list2 = getConfirmed();

        final E[] elements = getOtherElements();  // changed here
        ListIterator<E> iter1 = list1.listIterator();
        ListIterator<E> iter2 = list2.listIterator();

        for (final E element : elements) {
            iter1.add(element);
            iter2.add(element);
            super.verify();  // changed here
        }

        resetFull();
        iter1 = getCollection().listIterator();
        iter2 = getConfirmed().listIterator();
        for (final E element : elements) {
            iter1.next();
            iter2.next();
            iter1.add(element);
            iter2.add(element);
            super.verify();  // changed here
        }
    }

    @Override
    public void testCollectionAddAll() {
        // override for set behaviour
        resetEmpty();
        E[] elements = getFullElements();
        boolean r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue("Empty collection should change after addAll", r);
        for (final E element : elements) {
            assertTrue("Collection should contain added element",
                    getCollection().contains(element));
        }

        resetFull();
        final int size = getCollection().size();
        elements = getOtherElements();
        r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue("Full collection should change after addAll", r);
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Full collection should contain added element " + i,
                    getCollection().contains(elements[i]));
        }
        assertEquals("Size should increase after addAll",
                size + elements.length, getCollection().size());
    }

    public void testIntCollectionAddAll() {
      // make a SetUniqueList with one element
      final List<Integer> list = new SetUniqueList<Integer>(new ArrayList<Integer>(), new HashSet<Integer>());
      final Integer existingElement = Integer.valueOf(1);
      list.add(existingElement);

      // add two new unique elements at index 0
      final Integer firstNewElement = Integer.valueOf(2);
      final Integer secondNewElement = Integer.valueOf(3);
      Collection<Integer> collection = Arrays.asList(new Integer[] {firstNewElement, secondNewElement});
      list.addAll(0, collection);
      assertEquals("Unique elements should be added.", 3, list.size());
      assertEquals("First new element should be at index 0", firstNewElement, list.get(0));
      assertEquals("Second new element should be at index 1", secondNewElement, list.get(1));
      assertEquals("Existing element should shift to index 2", existingElement, list.get(2));

      // add a duplicate element and a unique element at index 0
      final Integer thirdNewElement = Integer.valueOf(4);
      collection = Arrays.asList(new Integer[] {existingElement, thirdNewElement});
      list.addAll(0, collection);
      assertEquals("Duplicate element should not be added, unique element should be added.",
        4, list.size());
      assertEquals("Third new element should be at index 0", thirdNewElement, list.get(0));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void testListSetByIndex() {
        // override for set behaviour
        resetFull();
        final int size = getCollection().size();
        getCollection().set(0, (E) new Long(1000));
        assertEquals(size, getCollection().size());

        getCollection().set(2, (E) new Long(1000));
        assertEquals(size - 1, getCollection().size());
        assertEquals(new Long(1000), getCollection().get(1));  // set into 2, but shifted down to 1
    }

    boolean extraVerify = true;
    @Override
    public void testCollectionIteratorRemove() {
        try {
            extraVerify = false;
            super.testCollectionIteratorRemove();
        } finally {
            extraVerify = true;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void verify() {
        super.verify();

        if (extraVerify) {
            final int size = getCollection().size();
            getCollection().add((E) new Long(1000));
            assertEquals(size + 1, getCollection().size());

            getCollection().add((E) new Long(1000));
            assertEquals(size + 1, getCollection().size());
            assertEquals(new Long(1000), getCollection().get(size));

            getCollection().remove(size);
        }
    }

    //-----------------------------------------------------------------------
    public void testFactory() {
        final Integer[] array = new Integer[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(1) };
        final ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(array));
        final SetUniqueList<Integer> lset = SetUniqueList.setUniqueList(list);

        assertEquals("Duplicate element was added.", 2, lset.size());
        assertEquals(Integer.valueOf(1), lset.get(0));
        assertEquals(Integer.valueOf(2), lset.get(1));
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
    }

    @SuppressWarnings("unchecked")
    public void testAdd() {
        final SetUniqueList<E> lset = new SetUniqueList<E>(new ArrayList<E>(), new HashSet<E>());

        // Duplicate element
        final E obj = (E) Integer.valueOf(1);
        lset.add(obj);
        lset.add(obj);
        assertEquals("Duplicate element was added.", 1, lset.size());

        // Unique element
        lset.add((E) Integer.valueOf(2));
        assertEquals("Unique element was not added.", 2, lset.size());
    }

    @SuppressWarnings("unchecked")
    public void testAddAll() {
        final SetUniqueList<E> lset = new SetUniqueList<E>(new ArrayList<E>(), new HashSet<E>());

        lset.addAll(
            Arrays.asList((E[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(1)}));

        assertEquals("Duplicate element was added.", 1, lset.size());
    }

    @SuppressWarnings("unchecked")
    public void testSet() {
        final SetUniqueList<E> lset = new SetUniqueList<E>(new ArrayList<E>(), new HashSet<E>());

        // Duplicate element
        final E obj1 = (E) Integer.valueOf(1);
        final E obj2 = (E) Integer.valueOf(2);
        final E obj3 = (E) Integer.valueOf(3);

        lset.add(obj1);
        lset.add(obj2);
        lset.set(0, obj1);
        assertEquals(2, lset.size());
        assertSame(obj1, lset.get(0));
        assertSame(obj2, lset.get(1));

        lset.clear();
        lset.add(obj1);
        lset.add(obj2);
        lset.set(0, obj2);
        assertEquals(1, lset.size());
        assertSame(obj2, lset.get(0));

        lset.clear();
        lset.add(obj1);
        lset.add(obj2);
        lset.set(0, obj3);
        assertEquals(2, lset.size());
        assertSame(obj3, lset.get(0));
        assertSame(obj2, lset.get(1));

        lset.clear();
        lset.add(obj1);
        lset.add(obj2);
        lset.set(1, obj1);
        assertEquals(1, lset.size());
        assertSame(obj1, lset.get(0));
    }

    @SuppressWarnings("unchecked")
    public void testListIterator() {
        final SetUniqueList<E> lset = new SetUniqueList<E>(new ArrayList<E>(), new HashSet<E>());

        final E obj1 = (E) Integer.valueOf(1);
        final E obj2 = (E) Integer.valueOf(2);
        lset.add(obj1);
        lset.add(obj2);

        // Attempts to add a duplicate object
        for (final ListIterator<E> it = lset.listIterator(); it.hasNext();) {
            it.next();

            if (!it.hasNext()) {
                it.add(obj1);
                break;
            }
        }

        assertEquals("Duplicate element was added", 2, lset.size());
    }

    @SuppressWarnings("unchecked")
    public void testUniqueListReInsert() {
        final List<E> l = SetUniqueList.setUniqueList(new LinkedList<E>());
        l.add((E) new Object());
        l.add((E) new Object());

        final E a = l.get(0);

        // duplicate is removed
        l.set(0, l.get(1));
        assertEquals(1, l.size());

        // old object is added back in
        l.add(1, a);
        assertEquals(2, l.size());
    }

    @SuppressWarnings("unchecked")
    public void testUniqueListDoubleInsert() {
        final List<E> l = SetUniqueList.setUniqueList(new LinkedList<E>());
        l.add((E) new Object());
        l.add((E) new Object());

        // duplicate is removed
        l.set(0, l.get(1));
        assertEquals(1, l.size());

        // duplicate should be removed again
        l.add(1, l.get(0));
        assertEquals(1, l.size());
    }

    @SuppressWarnings("unchecked")
    public void testSetDownwardsInList() {
        /*
         * Checks the following semantics
         * [a,b]
         * set(0,b): [b]->a
         * So UniqList contains [b] and a is returned
         */
        final ArrayList<E> l = new ArrayList<E>();
        final HashSet<E> s = new HashSet<E>();
        final SetUniqueList<E> ul = new SetUniqueList<E>(l, s);

        final E a = (E) new Object();
        final E b = (E) new Object();
        ul.add(a);
        ul.add(b);
        assertEquals(a, l.get(0));
        assertEquals(b, l.get(1));
        assertTrue(s.contains(a));
        assertTrue(s.contains(b));

        assertEquals(a, ul.set(0, b));
        assertEquals(1, s.size());
        assertEquals(1, l.size());
        assertEquals(b, l.get(0));
        assertTrue(s.contains(b));
        assertFalse(s.contains(a));
    }

    @SuppressWarnings("unchecked")
    public void testSetInBiggerList() {
        /*
         * Checks the following semantics
         * [a,b,c]
         * set(0,b): [b,c]->a
         * So UniqList contains [b,c] and a is returned
         */
        final ArrayList<E> l = new ArrayList<E>();
        final HashSet<E> s = new HashSet<E>();
        final SetUniqueList<E> ul = new SetUniqueList<E>(l, s);

        final E a = (E) new Object();
        final E b = (E) new Object();
        final E c = (E) new Object();

        ul.add(a);
        ul.add(b);
        ul.add(c);
        assertEquals(a, l.get(0));
        assertEquals(b, l.get(1));
        assertEquals(c, l.get(2));
        assertTrue(s.contains(a));
        assertTrue(s.contains(b));
        assertTrue(s.contains(c));

        assertEquals(a, ul.set(0, b));
        assertEquals(2, s.size());
        assertEquals(2, l.size());
        assertEquals(b, l.get(0));
        assertEquals(c, l.get(1));
        assertFalse(s.contains(a));
        assertTrue(s.contains(b));
        assertTrue(s.contains(c));
    }

    @SuppressWarnings("unchecked")
    public void testSetUpwardsInList() {
        /*
         * Checks the following semantics
         * [a,b,c]
         * set(1,a): [a,c]->b
         * So UniqList contains [a,c] and b is returned
         */
        final ArrayList<E> l = new ArrayList<E>();
        final HashSet<E> s = new HashSet<E>();
        final SetUniqueList<E> ul = new SetUniqueList<E>(l, s);

        final E a = (E) new String("A");
        final E b = (E) new String("B");
        final E c = (E) new String("C");

        ul.add(a);
        ul.add(b);
        ul.add(c);
        assertEquals(a, l.get(0));
        assertEquals(b, l.get(1));
        assertEquals(c, l.get(2));
        assertTrue(s.contains(a));
        assertTrue(s.contains(b));
        assertTrue(s.contains(c));

        assertEquals(b, ul.set(1, a));
        assertEquals(2, s.size());
        assertEquals(2, l.size());
        assertEquals(a, l.get(0));
        assertEquals(c, l.get(1));
        assertTrue(s.contains(a));
        assertFalse(s.contains(b));
        assertTrue(s.contains(c));
    }

    public void testCollections304() {
        final List<String> list = new LinkedList<String>();
        final SetUniqueList<String> decoratedList = SetUniqueList.setUniqueList(list);
        final String s1 = "Apple";
        final String s2 = "Lemon";
        final String s3 = "Orange";
        final String s4 = "Strawberry";

        decoratedList.add(s1);
        decoratedList.add(s2);
        decoratedList.add(s3);
        assertEquals(3, decoratedList.size());

        decoratedList.set(1, s4);
        assertEquals(3, decoratedList.size());

        decoratedList.add(1, s4);
        assertEquals(3, decoratedList.size());

        decoratedList.add(1, s2);
        assertEquals(4, decoratedList.size());
    }

    public void testSubListIsUnmodifiable() {
        resetFull();
        List<E> subList = getCollection().subList(1, 3);
        try {
            subList.remove(0);
            fail("subList should be unmodifiable");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
    @SuppressWarnings("unchecked")
    public void testCollections307() {
        List<E> list = new ArrayList<E>();
        List<E> uniqueList = SetUniqueList.setUniqueList(list);

        final String hello = "Hello";
        final String world = "World";
        uniqueList.add((E) hello);
        uniqueList.add((E) world);

        List<E> subList = list.subList(0, 0);
        List<E> subUniqueList = uniqueList.subList(0, 0);

        assertFalse(subList.contains(world)); // passes
        assertFalse(subUniqueList.contains(world)); // fails

        List<E> worldList = new ArrayList<E>();
        worldList.add((E) world);
        assertFalse(subList.contains("World")); // passes
        assertFalse(subUniqueList.contains("World")); // fails

        // repeat the test with a different class than HashSet;
        // which means subclassing SetUniqueList below
        list = new ArrayList<E>();
        uniqueList = new SetUniqueList307(list, new java.util.TreeSet<E>());

        uniqueList.add((E) hello);
        uniqueList.add((E) world);

        subList = list.subList(0, 0);
        subUniqueList = uniqueList.subList(0, 0);

        assertFalse(subList.contains(world)); // passes
        assertFalse(subUniqueList.contains(world)); // fails

        worldList = new ArrayList<E>();
        worldList.add((E) world);
        assertFalse(subList.contains("World")); // passes
        assertFalse(subUniqueList.contains("World")); // fails
    }

    @SuppressWarnings("unchecked")
    public void testRetainAll() {
        final List<E> list = new ArrayList<E>(10);
        final SetUniqueList<E> uniqueList = SetUniqueList.setUniqueList(list);
        for (int i = 0; i < 10; ++i) {
            uniqueList.add((E)Integer.valueOf(i));
        }
        
        final Collection<E> retained = new ArrayList<E>(5);
        for (int i = 0; i < 5; ++i) {
            retained.add((E)Integer.valueOf(i * 2));
        }
        
        assertTrue(uniqueList.retainAll(retained));
        assertEquals(5, uniqueList.size());
        assertTrue(uniqueList.contains(Integer.valueOf(0)));
        assertTrue(uniqueList.contains(Integer.valueOf(2)));
        assertTrue(uniqueList.contains(Integer.valueOf(4)));
        assertTrue(uniqueList.contains(Integer.valueOf(6)));
        assertTrue(uniqueList.contains(Integer.valueOf(8)));
    }

    @SuppressWarnings("unchecked")
    public void testRetainAllWithInitialList() {
        // initialized with empty list
        final List<E> list = new ArrayList<E>(10);
        for (int i = 0; i < 5; ++i) {
            list.add((E)Integer.valueOf(i));
        }
        final SetUniqueList<E> uniqueList = SetUniqueList.setUniqueList(list);
        for (int i = 5; i < 10; ++i) {
            uniqueList.add((E)Integer.valueOf(i));
        }
        
        final Collection<E> retained = new ArrayList<E>(5);
        for (int i = 0; i < 5; ++i) {
            retained.add((E)Integer.valueOf(i * 2));
        }
        
        assertTrue(uniqueList.retainAll(retained));
        assertEquals(5, uniqueList.size());
        assertTrue(uniqueList.contains(Integer.valueOf(0)));
        assertTrue(uniqueList.contains(Integer.valueOf(2)));
        assertTrue(uniqueList.contains(Integer.valueOf(4)));
        assertTrue(uniqueList.contains(Integer.valueOf(6)));
        assertTrue(uniqueList.contains(Integer.valueOf(8)));
    }
    
    /*
     * test case for https://issues.apache.org/jira/browse/COLLECTIONS-427
     */
    public void testRetainAllCollections427() {
        final int size = 50000;
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        final SetUniqueList<Integer> uniqueList = SetUniqueList.setUniqueList(list);
        final ArrayList<Integer> toRetain = new ArrayList<Integer>();
        for (int i = size; i < 2*size; i++) {
            toRetain.add(i);
        }

        final long start = System.currentTimeMillis();
        uniqueList.retainAll(toRetain);
        final long stop = System.currentTimeMillis();
        
        // make sure retainAll completes under 5 seconds
        // TODO if test is migrated to JUnit 4, add a Timeout rule.
        // http://kentbeck.github.com/junit/javadoc/latest/org/junit/rules/Timeout.html
        assertTrue(stop - start < 5000);
    }
    
    public void testSetCollections444() {
        final SetUniqueList<Integer> lset = new SetUniqueList<Integer>(new ArrayList<Integer>(), new HashSet<Integer>());

        // Duplicate element
        final Integer obj1 = Integer.valueOf(1);
        final Integer obj2 = Integer.valueOf(2);

        lset.add(obj1);
        lset.add(obj2);
        lset.set(0, obj1);
        assertEquals(2, lset.size());
        assertSame(obj1, lset.get(0));
        assertSame(obj2, lset.get(1));

        assertTrue(lset.contains(obj1));
        assertTrue(lset.contains(obj2));
    }

    class SetUniqueList307 extends SetUniqueList<E> {
        /**
         * Generated serial version ID.
         */
        private static final long serialVersionUID = 1415013031022962158L;

        public SetUniqueList307(final List<E> list, final Set<E> set) {
            super(list, set);
        }
    }
    
    //-----------------------------------------------------------------------
    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/SetUniqueList.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/SetUniqueList.fullCollection.version4.obj");
//    }

}
