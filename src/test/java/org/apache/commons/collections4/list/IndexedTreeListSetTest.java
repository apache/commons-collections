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

import java.util.*;

/**
 * JUnit tests.
 *
 * @since 3.0
 */
public class IndexedTreeListSetTest<E> extends AbstractListTest<E> {

    boolean extraVerify = true;

    public IndexedTreeListSetTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public String getCompatibilityVersion() {
        return "4";
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

    //-----------------------------------------------------------------------
    @Override
    public List<E> makeObject() {
        IndexedTreeListSet<E> list = new IndexedTreeListSet<>();
        list.supportSetInIterator = false;
        return list;
    }

    @Override
    public boolean isNullSupported() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public void testAdd() {
        final IndexedTreeListSet<E> lset = new IndexedTreeListSet<>(new ArrayList<E>());

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
        final IndexedTreeListSet<E> lset = new IndexedTreeListSet<>(new ArrayList<E>());

        lset.addAll(
            Arrays.asList((E[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(1)}));

        assertEquals("Duplicate element was added.", 1, lset.size());
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

    @Override
    public void testCollectionIteratorRemove() {
        try {
            extraVerify = false;
            super.testCollectionIteratorRemove();
        } finally {
            extraVerify = true;
        }
    }
    public void testCollections304() {
        final List<String> list = new LinkedList<>();
        final IndexedTreeListSet<String> decoratedList = new IndexedTreeListSet(list);
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

    @SuppressWarnings("unchecked")
    public void testCollections307() {
        List<E> list = new ArrayList<>();
        List<E> uniqueList = new IndexedTreeListSet(list);

        final String hello = "Hello";
        final String world = "World";
        uniqueList.add((E) hello);
        uniqueList.add((E) world);

        List<E> subList = list.subList(0, 0);
        List<E> subUniqueList = uniqueList.subList(0, 0);

        assertFalse(subList.contains(world)); // passes
        assertFalse(subUniqueList.contains(world)); // fails

        List<E> worldList = new ArrayList<>();
        worldList.add((E) world);
        assertFalse(subList.contains("World")); // passes
        assertFalse(subUniqueList.contains("World")); // fails

        // repeat the test with a different class than HashSet;
        // which means subclassing IndexedTreeListSet below
        list = new ArrayList<>();
        uniqueList = new IndexedTreeListSet(list, new java.util.TreeMap());

        uniqueList.add((E) hello);
        uniqueList.add((E) world);

        subList = list.subList(0, 0);
        subUniqueList = uniqueList.subList(0, 0);

        assertFalse(subList.contains(world)); // passes
        assertFalse(subUniqueList.contains(world)); // fails

        worldList = new ArrayList<>();
        worldList.add((E) world);
        assertFalse(subList.contains("World")); // passes
        assertFalse(subUniqueList.contains("World")); // fails
    }

    public void testCollections701() {
        final IndexedTreeListSet<Object> uniqueList = new IndexedTreeListSet<>(new ArrayList<>());
        final Integer obj1 = Integer.valueOf(1);
        final Integer obj2 = Integer.valueOf(2);
        uniqueList.add(obj1);
        uniqueList.add(obj2);
        assertEquals(2, uniqueList.size());
        uniqueList.add(uniqueList);
        assertEquals(3, uniqueList.size());
        final List<Object> list = new LinkedList<>();
        final IndexedTreeListSet<Object> decoratedList = new IndexedTreeListSet(list);
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
        decoratedList.add(decoratedList);
        assertEquals(4, decoratedList.size());
    }

    //-----------------------------------------------------------------------
    public void testFactory() {
        final Integer[] array = new Integer[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(1) };
        final ArrayList<Integer> list = new ArrayList<>(Arrays.asList(array));
        final IndexedTreeListSet<Integer> lset = new IndexedTreeListSet(list);

        assertEquals("Duplicate element was added.", 2, lset.size());
        assertEquals(Integer.valueOf(1), lset.get(0));
        assertEquals(Integer.valueOf(2), lset.get(1));
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
    }

    public void testIntCollectionAddAll() {
      // make a IndexedTreeListSet with one element
      final List<Integer> list = new IndexedTreeListSet<>(new ArrayList<Integer>());
      final Integer existingElement = Integer.valueOf(1);
      list.add(existingElement);

      // add two new unique elements at index 0
      final Integer firstNewElement = Integer.valueOf(2);
      final Integer secondNewElement = Integer.valueOf(3);
      Collection<Integer> collection = Arrays.asList(firstNewElement, secondNewElement);
      list.addAll(0, collection);
      assertEquals("Unique elements should be added.", 3, list.size());
      assertEquals("First new element should be at index 0", firstNewElement, list.get(0));
      assertEquals("Second new element should be at index 1", secondNewElement, list.get(1));
      assertEquals("Existing element should shift to index 2", existingElement, list.get(2));

      // add a duplicate element and a unique element at index 0
      final Integer thirdNewElement = Integer.valueOf(4);
      collection = Arrays.asList(existingElement, thirdNewElement);
      list.addAll(0, collection);
      assertEquals("Duplicate element should not be added, unique element should be added.",
        4, list.size());
      assertEquals("Third new element should be at index 0", thirdNewElement, list.get(0));
    }

    @SuppressWarnings("unchecked")
    public void testIndexOf() {
        final List<E> l = makeObject();
        l.add((E) "0");
        l.add((E) "1");
        l.add((E) "2");
        l.add((E) "3");
        l.add((E) "4");
        l.add((E) "5");
        l.add((E) "6");
        assertEquals(0, l.indexOf("0"));
        assertEquals(1, l.indexOf("1"));
        assertEquals(2, l.indexOf("2"));
        assertEquals(3, l.indexOf("3"));
        assertEquals(4, l.indexOf("4"));
        assertEquals(5, l.indexOf("5"));
        assertEquals(6, l.indexOf("6"));
        assertEquals(7, l.size());

        l.set(2, (E) "0"); // Previous "0" at index 0 was removed
        assertEquals(1, l.indexOf("0"));
        assertEquals(-1, l.indexOf("2"));
        assertEquals(0, l.indexOf("1"));
        assertEquals(6, l.size());

        l.set(2, (E) "7");
        assertEquals(-1, l.indexOf("3"));
        assertEquals(2, l.indexOf("7"));
        assertEquals(6, l.size());

        l.set(2, (E) "5"); // Previous "5" at index 4 was removed
        assertEquals(2, l.indexOf("5"));
        assertEquals(4, l.indexOf("6"));
        assertEquals(5, l.size());

        l.remove(4);
        assertEquals(2, l.indexOf("5"));

        l.remove(1);
        assertEquals(1, l.indexOf("5"));
    }

    @SuppressWarnings("unchecked")
    public void testListIterator() {
        final IndexedTreeListSet<E> lset = new IndexedTreeListSet<>(new ArrayList<E>());

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

    @SuppressWarnings("unchecked")
    public void testRetainAll() {
        final List<E> list = new ArrayList<>(10);
        final IndexedTreeListSet<E> uniqueList = new IndexedTreeListSet(list);
        for (int i = 0; i < 10; ++i) {
            uniqueList.add((E)Integer.valueOf(i));
        }

        final Collection<E> retained = new ArrayList<>(5);
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
        final List<E> list = new ArrayList<>(10);
        for (int i = 0; i < 5; ++i) {
            list.add((E)Integer.valueOf(i));
        }
        final IndexedTreeListSet<E> uniqueList = new IndexedTreeListSet(list);
        for (int i = 5; i < 10; ++i) {
            uniqueList.add((E)Integer.valueOf(i));
        }

        final Collection<E> retained = new ArrayList<>(5);
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
    public void testSet() {
        final IndexedTreeListSet<E> lset = new IndexedTreeListSet<>(new ArrayList<E>());

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

    public void testSetCollections444() {
        final IndexedTreeListSet<Integer> lset = new IndexedTreeListSet<>(new ArrayList<Integer>());

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

    public void testSubListIsUnmodifiable() {
        resetFull();
        final List<E> subList = getCollection().subList(1, 3);
        try {
            subList.remove(0);
            fail("subList should be unmodifiable");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
    }


    public void testBug35258() {
        final Object objectToRemove = Integer.valueOf(3);

        final List<Integer> TreeListSet = new IndexedTreeListSet<>();
        TreeListSet.add(Integer.valueOf(0));
        TreeListSet.add(Integer.valueOf(1));
        TreeListSet.add(Integer.valueOf(2));
        TreeListSet.add(Integer.valueOf(3));
        TreeListSet.add(Integer.valueOf(4));

        // this cause inconsistence of ListIterator()
        TreeListSet.remove(objectToRemove);

        final ListIterator<Integer> li = TreeListSet.listIterator();
        assertEquals(Integer.valueOf(0), li.next());
        assertEquals(Integer.valueOf(0), li.previous());
        assertEquals(Integer.valueOf(0), li.next());
        assertEquals(Integer.valueOf(1), li.next());
        // this caused error in bug 35258
        assertEquals(Integer.valueOf(1), li.previous());
        assertEquals(Integer.valueOf(1), li.next());
        assertEquals(Integer.valueOf(2), li.next());
        assertEquals(Integer.valueOf(2), li.previous());
        assertEquals(Integer.valueOf(2), li.next());
        assertEquals(Integer.valueOf(4), li.next());
        assertEquals(Integer.valueOf(4), li.previous());
        assertEquals(Integer.valueOf(4), li.next());
        assertEquals(false, li.hasNext());
    }

    public void testBugCollections447() {
        final List<String> TreeListSet = new IndexedTreeListSet<>();
        TreeListSet.add("A");
        TreeListSet.add("B");
        TreeListSet.add("C");
        TreeListSet.add("D");

        final ListIterator<String> li = TreeListSet.listIterator();
        assertEquals("A", li.next());
        assertEquals("B", li.next());

        assertEquals("B", li.previous());

        li.remove(); // Deletes "B"

        // previous() after remove() should move to
        // the element before the one just removed
        assertEquals("A", li.previous());
    }

    @SuppressWarnings("boxing") // OK in test code
    public void testIterationOrder() {
        // COLLECTIONS-433:
        // ensure that the iteration order of elements is correct
        // when initializing the TreeListSet with another collection

        for (int size = 1; size < 1000; size++) {
            final List<Integer> other = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                other.add(i);
            }
            final IndexedTreeListSet<Integer> l = new IndexedTreeListSet<>(other);
            final ListIterator<Integer> it = l.listIterator();
            int i = 0;
            while (it.hasNext()) {
                final Integer val = it.next();
                assertEquals(i++, val.intValue());
            }

            while (it.hasPrevious()) {
                final Integer val = it.previous();
                assertEquals(--i, val.intValue());
            }
        }
    }

    @SuppressWarnings("boxing") // OK in test code
    public void testIterationOrderAfterAddAll() {
        // COLLECTIONS-433:
        // ensure that the iteration order of elements is correct
        // when calling addAll on the TreeListSet

        // to simulate different cases in addAll, do different runs where
        // the number of elements already in the list and being added by addAll differ

        final int size = 1000;
        for (int i = 0; i < 100; i++) {
            final List<Integer> other = new ArrayList<>(size);
            for (int j = i; j < size; j++) {
                other.add(j);
            }
            final IndexedTreeListSet<Integer> l = new IndexedTreeListSet<>();
            for (int j = 0; j < i; j++) {
                l.add(j);
            }

            l.addAll(other);

            final ListIterator<Integer> it = l.listIterator();
            int cnt = 0;
            while (it.hasNext()) {
                final Integer val = it.next();
                assertEquals(cnt++, val.intValue());
            }

            while (it.hasPrevious()) {
                final Integer val = it.previous();
                assertEquals(--cnt, val.intValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void testUniqueListDoubleInsert() {
        final List<E> l = new IndexedTreeListSet(new LinkedList<E>());
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
    public void testUniqueListReInsert() {
        final List<E> l = new IndexedTreeListSet(new LinkedList<E>());
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

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/IndexedTreeListSet.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/IndexedTreeListSet.fullCollection.version4.obj");
//    }

}
