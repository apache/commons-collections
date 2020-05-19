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
package org.apache.commons.collections4;

import static org.apache.commons.collections4.functors.EqualPredicate.equalPredicate;
import static org.apache.commons.collections4.functors.TruePredicate.INSTANCE;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Set;
import java.util.LinkedHashSet;

import org.apache.commons.collections4.iterators.*;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tests for IteratorUtils.
 */
public class IteratorUtilsTest {

    /**
     * Collection of {@link Integer}s
     */
    private List<Integer> collectionA = null;

    /**
     * Collection of even {@link Integer}s
     */
    private List<Integer> collectionEven = null;

    /**
     * Collection of odd {@link Integer}s
     */
    private List<Integer> collectionOdd = null;

    private final Collection<Integer> emptyCollection = new ArrayList<>(1);

    private Iterable<Integer> iterableA = null;

    /**
     * Creates a NodeList containing the specified nodes.
     */
    private NodeList createNodeList(final Node[] nodes) {
        return new NodeList() {
            @Override
            public int getLength() {
                return nodes.length;
            }

            @Override
            public Node item(final int index) {
                return nodes[index];
            }
        };
    }

    /**
     * creates an array of four Node instances, mocked by EasyMock.
     */
    private Node[] createNodes() {
        final Node node1 = createMock(Node.class);
        final Node node2 = createMock(Node.class);
        final Node node3 = createMock(Node.class);
        final Node node4 = createMock(Node.class);
        replay(node1);
        replay(node2);
        replay(node3);
        replay(node4);

        return new Node[] { node1, node2, node3, node4 };
    }

    /**
     * Gets an immutable Iterator operating on the elements ["a", "b", "c", "d"].
     */
    private Iterator<String> getImmutableIterator() {
        final List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return IteratorUtils.unmodifiableIterator(list.iterator());
    }

    /**
     * Gets an immutable ListIterator operating on the elements ["a", "b", "c", "d"].
     */
    private ListIterator<String> getImmutableListIterator() {
        final List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return IteratorUtils.unmodifiableListIterator(list.listIterator());
    }

    @Before
    public void setUp() {
        collectionA = new ArrayList<>();
        collectionA.add(1);
        collectionA.add(2);
        collectionA.add(2);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(3);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);
        collectionA.add(4);

        iterableA = collectionA;

        collectionEven = Arrays.asList(2, 4, 6, 8, 10, 12);
        collectionOdd = Arrays.asList(1, 3, 5, 7, 9, 11);
    }

    @Test
    public void testArrayIterator() {
        final Object[] objArray = { "a", "b", "c" };
        ResettableIterator<Object> iterator = IteratorUtils.arrayIterator(objArray);
        assertEquals("a", iterator.next());
        assertEquals("b", iterator.next());
        iterator.reset();
        assertEquals("a", iterator.next());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IteratorUtils.arrayIterator(Integer.valueOf(0));
        });
        assertTrue(exception.getMessage().contains("Argument is not an array"));

        exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.arrayIterator((Object[]) null);
        });
        assertNull(exception.getMessage());

        iterator = IteratorUtils.arrayIterator(objArray, 1);
        assertEquals("b", iterator.next());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayIterator(objArray, -1);
        });
        assertTrue(exception.getMessage().contains("Start index must not be less than zero"));

        iterator = IteratorUtils.arrayIterator(objArray, 3);
        assertFalse(iterator.hasNext());
        iterator.reset();

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayIterator(objArray, 4);
        });
        assertTrue(exception.getMessage().contains("Start index must not be greater than the array length"));

        iterator = IteratorUtils.arrayIterator(objArray, 2, 3);
        assertEquals("c", iterator.next());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayIterator(objArray, 2, 4);
        });
        assertTrue(exception.getMessage().contains("End index must not be greater than the array length"));

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayIterator(objArray, -1, 1);
        });
        assertTrue(exception.getMessage().contains("Start index must not be less than zero"));

        exception = assertThrows(IllegalArgumentException.class, () -> {
            IteratorUtils.arrayIterator(objArray, 2, 1);
        });
        assertTrue(exception.getMessage().contains("End index must not be less than start index"));

        final int[] intArray = { 0, 1, 2 };
        iterator = IteratorUtils.arrayIterator(intArray);
        assertEquals(0, iterator.next());
        assertEquals(1, iterator.next());
        iterator.reset();
        assertEquals(0, iterator.next());

        iterator = IteratorUtils.arrayIterator(intArray, 1);
        assertEquals(1, iterator.next());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayIterator(intArray, -1);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that starts before the start of the array."));

        iterator = IteratorUtils.arrayIterator(intArray, 3);
        assertFalse(iterator.hasNext());
        iterator.reset();

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayIterator(intArray, 4);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that starts beyond the end of the array."));

        iterator = IteratorUtils.arrayIterator(intArray, 2, 3);
        assertEquals(2, iterator.next());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayIterator(intArray, 2, 4);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that ends beyond the end of the array."));

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayIterator(intArray, -1, 1);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that starts before the start of the array."));

        exception = assertThrows(IllegalArgumentException.class, () -> {
            IteratorUtils.arrayIterator(intArray, 2, 1);
        });
        assertTrue(exception.getMessage().contains("End index must not be less than start index."));
    }

    @Test
    public void testArrayListIterator() {
        final Object[] objArray = { "a", "b", "c", "d" };
        ResettableListIterator<Object> iterator = IteratorUtils.arrayListIterator(objArray);
        assertFalse(iterator.hasPrevious());
        assertEquals(-1, iterator.previousIndex());
        assertEquals(0, iterator.nextIndex());
        assertEquals("a", iterator.next());
        assertEquals("a", iterator.previous());
        assertEquals("a", iterator.next());
        assertEquals(0, iterator.previousIndex());
        assertEquals(1, iterator.nextIndex());
        assertEquals("b", iterator.next());
        assertEquals("c", iterator.next());
        assertEquals("d", iterator.next());
        assertEquals(4, iterator.nextIndex()); // size of list
        assertEquals(3, iterator.previousIndex());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IteratorUtils.arrayListIterator(Integer.valueOf(0));
        });
        assertTrue(exception.getMessage().contains("Argument is not an array"));

        exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.arrayListIterator((Object[]) null);
        });
        assertNull(exception.getMessage());

        iterator = IteratorUtils.arrayListIterator(objArray, 1);
        assertEquals(-1, iterator.previousIndex());
        assertFalse(iterator.hasPrevious());
        assertEquals(0, iterator.nextIndex());
        assertEquals("b", iterator.next());
        assertEquals(0, iterator.previousIndex());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayListIterator(objArray, -1);
        });
        assertTrue(exception.getMessage().contains("Start index must not be less than zero"));

        final ResettableListIterator<Object> listIterator = IteratorUtils.arrayListIterator(objArray, 3);
        assertTrue(listIterator.hasNext());
        exception = assertThrows(NoSuchElementException.class, () -> {
            listIterator.previous();
        });
        assertNull(exception.getMessage());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayListIterator(objArray, 5);
        });
        assertTrue(exception.getMessage().contains("Start index must not be greater than the array length"));

        iterator = IteratorUtils.arrayListIterator(objArray, 2, 3);
        assertEquals("c", iterator.next());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayListIterator(objArray, 2, 5);
        });
        assertTrue(exception.getMessage().contains("End index must not be greater than the array length"));

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayListIterator(objArray, -1, 1);
        });
        assertTrue(exception.getMessage().contains("Start index must not be less than zero"));

        exception = assertThrows(IllegalArgumentException.class, () -> {
            IteratorUtils.arrayListIterator(objArray, 2, 1);
        });
        assertTrue(exception.getMessage().contains("End index must not be less than start index"));

        final int[] intArray = { 0, 1, 2 };
        iterator = IteratorUtils.arrayListIterator(intArray);
        assertEquals(iterator.previousIndex(), -1);
        assertFalse(iterator.hasPrevious());
        assertEquals(0, iterator.nextIndex());
        assertEquals(0, iterator.next());
        assertEquals(0, iterator.previousIndex());
        assertEquals(1, iterator.nextIndex());
        assertEquals(1, iterator.next());
        assertEquals(1, iterator.previousIndex());
        assertEquals(2, iterator.nextIndex());
        assertEquals(1, iterator.previous());
        assertEquals(1, iterator.next());

        iterator = IteratorUtils.arrayListIterator(intArray, 1);
        assertEquals(-1, iterator.previousIndex());
        assertFalse(iterator.hasPrevious());
        assertEquals(0, iterator.nextIndex());
        assertEquals(1, iterator.next());
        assertEquals(1, iterator.previous());
        assertEquals(1, iterator.next());
        assertEquals(0, iterator.previousIndex());
        assertEquals(1, iterator.nextIndex());
        assertEquals(2, iterator.next());
        assertEquals(1, iterator.previousIndex());
        assertEquals(2, iterator.nextIndex());
        assertEquals(2, iterator.previous());
        assertEquals(0, iterator.previousIndex());
        assertEquals(1, iterator.nextIndex());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayListIterator(intArray, -1);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that starts before the start of the array."));

        iterator = IteratorUtils.arrayListIterator(intArray, 3);

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayListIterator(intArray, 4);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that starts beyond the end of the array."));

        iterator = IteratorUtils.arrayListIterator(intArray, 2, 3);
        assertFalse(iterator.hasPrevious());
        assertEquals(-1, iterator.previousIndex());
        assertEquals(2, iterator.next());
        assertTrue(iterator.hasPrevious());
        assertFalse(iterator.hasNext());

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayListIterator(intArray, 2, 4);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that ends beyond the end of the array."));

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.arrayListIterator(intArray, -1, 1);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that starts before the start of the array."));

        exception = assertThrows(IllegalArgumentException.class, () -> {
            IteratorUtils.arrayListIterator(intArray, 2, 1);
        });
        assertTrue(exception.getMessage().contains("End index must not be less than start index."));
    }

    @Test(expected = NullPointerException.class)
    public void testAsEnumerationNull() {
        IteratorUtils.asEnumeration(null);
    }

    @Test
    public void testAsIterable() {
        final List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(0));
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        final Iterator<Integer> iterator = list.iterator();

        final Iterable<Integer> iterable = IteratorUtils.asIterable(iterator);
        int expected = 0;
        for (final Integer actual : iterable) {
            assertEquals(expected, actual.intValue());
            ++expected;
        }
        // insure iteration occurred
        assertTrue(expected > 0);

        // single use iterator
        assertFalse("should not be able to iterate twice", IteratorUtils.asIterable(iterator).iterator().hasNext());
    }

    @Test
    public void testAsIterableNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.asIterable(null);
        });
        assertTrue(exception.getMessage().contains("iterator"));
    }

    @Test(expected = NullPointerException.class)
    public void testAsIterator() {
        final Vector<String> vector = new Vector<>();
        vector.addElement("zero");
        vector.addElement("one");
        final Enumeration<String> en = vector.elements();
        assertTrue(IteratorUtils.asIterator(en) instanceof Iterator);
        IteratorUtils.asIterator(null);
    }

    @Test
    public void testAsIteratorNull() {
        final Collection coll = new ArrayList();
        coll.add("test");
        final Vector<String> vector = new Vector<>();
        vector.addElement("test");
        vector.addElement("one");
        final Enumeration<String> en = vector.elements();
        assertTrue(IteratorUtils.asIterator(en, coll) instanceof Iterator);
        try {
            IteratorUtils.asIterator(null, coll);
        } catch (final NullPointerException npe) {
            //
        }
        try {
            IteratorUtils.asIterator(en, null);
        } catch (final NullPointerException npe) {
            //
        }
    }

    @Test
    public void testAsMultipleIterable() {
        final List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(0));
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        final Iterator<Integer> iterator = list.iterator();

        final Iterable<Integer> iterable = IteratorUtils.asMultipleUseIterable(iterator);
        int expected = 0;
        for (final Integer actual : iterable) {
            assertEquals(expected, actual.intValue());
            ++expected;
        }
        // insure iteration occurred
        assertTrue(expected > 0);

        // multiple use iterator
        expected = 0;
        for (final Integer actual : iterable) {
            assertEquals(expected, actual.intValue());
            ++expected;
        }
        // insure iteration occurred
        assertTrue(expected > 0);
    }

    @Test
    public void testAsMultipleIterableNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.asMultipleUseIterable(null);
        });
        assertTrue(exception.getMessage().contains("iterator"));
    }

    @Test
    public void testChainedIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator ie = arrayList.iterator();
        assertTrue(IteratorUtils.chainedIterator(ie) instanceof Iterator);
        final Collection<Iterator<?>> coll = new ArrayList();
        assertTrue(IteratorUtils.chainedIterator(coll) instanceof Iterator);

    }

    /**
     * Tests methods collatedIterator(...)
     */
    @Test
    public void testCollatedIterator() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.collatedIterator(null, collectionOdd.iterator(), null);
        });
        assertTrue(exception.getMessage().contains("iterator"));

        exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.collatedIterator(null, null, collectionEven.iterator());
        });
        assertTrue(exception.getMessage().contains("iterator"));

        // natural ordering
        Iterator<Integer> it = IteratorUtils.collatedIterator(null, collectionOdd.iterator(),
                collectionEven.iterator());

        List<Integer> result = IteratorUtils.toList(it);
        assertEquals(12, result.size());

        final List<Integer> combinedList = new ArrayList<>();
        combinedList.addAll(collectionOdd);
        combinedList.addAll(collectionEven);
        Collections.sort(combinedList);

        assertEquals(combinedList, result);

        it = IteratorUtils.collatedIterator(null, collectionOdd.iterator(), emptyCollection.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(collectionOdd, result);

        final Comparator<Integer> reverseComparator = ComparatorUtils
                .reversedComparator(ComparatorUtils.<Integer>naturalComparator());

        Collections.reverse(collectionOdd);
        Collections.reverse(collectionEven);
        Collections.reverse(combinedList);

        it = IteratorUtils.collatedIterator(reverseComparator, collectionOdd.iterator(), collectionEven.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(combinedList, result);
    }

    @Test(expected = NullPointerException.class)
    public void testCollatedIteratorCollectionNull() {
        final Collection<Iterator<?>> coll = new ArrayList<>();
        coll.add(collectionOdd.iterator());
        // natural ordering
        final Iterator<?> it = IteratorUtils.collatedIterator(null, coll);
        final List<?> result = IteratorUtils.toList(it);
        assertEquals(6, result.size());
        IteratorUtils.collatedIterator(null, (Collection<Iterator<?>>) null);
    }

    @Test(expected = NullPointerException.class)
    public void testCollatedIteratorNull() {
        final ArrayList arrayList = new ArrayList();
        // natural ordering
        Iterator<Integer> it = IteratorUtils.collatedIterator(null, collectionOdd.iterator(), collectionOdd.iterator(),
                collectionOdd.iterator());

        List<Integer> result = IteratorUtils.toList(it);
        assertEquals(18, result.size());

        it = IteratorUtils.collatedIterator(null, collectionOdd.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(collectionOdd, result);

        final Comparator<Integer> reverseComparator = ComparatorUtils
                .reversedComparator(ComparatorUtils.<Integer>naturalComparator());

        Collections.reverse(collectionOdd);

        it = IteratorUtils.collatedIterator(reverseComparator, collectionOdd.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(collectionOdd, result);
        IteratorUtils.collatedIterator(null, arrayList.iterator(), arrayList.listIterator(), null);
    }

    // -----------------------------------------------------------------------
    /**
     * Test empty iterator
     */
    @Test
    public void testEmptyIterator() {
        assertSame(EmptyIterator.INSTANCE, IteratorUtils.EMPTY_ITERATOR);
        assertSame(EmptyIterator.RESETTABLE_INSTANCE, IteratorUtils.EMPTY_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_ITERATOR instanceof ResettableIterator);
        assertFalse(IteratorUtils.EMPTY_ITERATOR instanceof OrderedIterator);
        assertFalse(IteratorUtils.EMPTY_ITERATOR instanceof ListIterator);
        assertFalse(IteratorUtils.EMPTY_ITERATOR instanceof MapIterator);
        assertFalse(IteratorUtils.EMPTY_ITERATOR.hasNext());
        IteratorUtils.EMPTY_ITERATOR.reset();
        assertSame(IteratorUtils.EMPTY_ITERATOR, IteratorUtils.EMPTY_ITERATOR);
        assertSame(IteratorUtils.EMPTY_ITERATOR, IteratorUtils.emptyIterator());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            IteratorUtils.EMPTY_ITERATOR.next();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));

        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_ITERATOR.remove();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
    }

    // -----------------------------------------------------------------------
    /**
     * Test empty list iterator
     */
    @Test
    public void testEmptyListIterator() {
        assertSame(EmptyListIterator.INSTANCE, IteratorUtils.EMPTY_LIST_ITERATOR);
        assertSame(EmptyListIterator.RESETTABLE_INSTANCE, IteratorUtils.EMPTY_LIST_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_LIST_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_LIST_ITERATOR instanceof ListIterator);
        assertTrue(IteratorUtils.EMPTY_LIST_ITERATOR instanceof ResettableIterator);
        assertTrue(IteratorUtils.EMPTY_LIST_ITERATOR instanceof ResettableListIterator);
        assertFalse(IteratorUtils.EMPTY_LIST_ITERATOR instanceof MapIterator);
        assertFalse(IteratorUtils.EMPTY_LIST_ITERATOR.hasNext());
        assertEquals(0, IteratorUtils.EMPTY_LIST_ITERATOR.nextIndex());
        assertEquals(-1, IteratorUtils.EMPTY_LIST_ITERATOR.previousIndex());
        IteratorUtils.EMPTY_LIST_ITERATOR.reset();
        assertSame(IteratorUtils.EMPTY_LIST_ITERATOR, IteratorUtils.EMPTY_LIST_ITERATOR);
        assertSame(IteratorUtils.EMPTY_LIST_ITERATOR, IteratorUtils.emptyListIterator());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            IteratorUtils.EMPTY_LIST_ITERATOR.next();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(NoSuchElementException.class, () -> {
            IteratorUtils.EMPTY_LIST_ITERATOR.previous();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_LIST_ITERATOR.remove();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.emptyListIterator().set(null);
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            IteratorUtils.emptyListIterator().add(null);
        });
        assertTrue(exception.getMessage().contains("add() not supported for empty Iterator"));
    }

    // -----------------------------------------------------------------------
    /**
     * Test empty map iterator
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyMapIterator() {
        assertSame(EmptyMapIterator.INSTANCE, IteratorUtils.EMPTY_MAP_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_MAP_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_MAP_ITERATOR instanceof MapIterator);
        assertTrue(IteratorUtils.EMPTY_MAP_ITERATOR instanceof ResettableIterator);
        assertFalse(IteratorUtils.EMPTY_MAP_ITERATOR instanceof ListIterator);
        assertFalse(IteratorUtils.EMPTY_MAP_ITERATOR instanceof OrderedIterator);
        assertFalse(IteratorUtils.EMPTY_MAP_ITERATOR instanceof OrderedMapIterator);
        assertFalse(IteratorUtils.EMPTY_MAP_ITERATOR.hasNext());
        ((ResettableIterator<Object>) IteratorUtils.EMPTY_MAP_ITERATOR).reset();
        assertSame(IteratorUtils.EMPTY_MAP_ITERATOR, IteratorUtils.EMPTY_MAP_ITERATOR);
        assertSame(IteratorUtils.EMPTY_MAP_ITERATOR, IteratorUtils.emptyMapIterator());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            IteratorUtils.EMPTY_MAP_ITERATOR.next();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_MAP_ITERATOR.remove();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_MAP_ITERATOR.getKey();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_MAP_ITERATOR.getValue();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_MAP_ITERATOR.setValue(null);
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
    }

    // -----------------------------------------------------------------------
    /**
     * Test empty map iterator
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyOrderedIterator() {
        assertSame(EmptyOrderedIterator.INSTANCE, IteratorUtils.EMPTY_ORDERED_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof OrderedIterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof ResettableIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof ListIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_ITERATOR instanceof MapIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_ITERATOR.hasNext());
        assertFalse(IteratorUtils.EMPTY_ORDERED_ITERATOR.hasPrevious());
        ((ResettableIterator<Object>) IteratorUtils.EMPTY_ORDERED_ITERATOR).reset();
        assertSame(IteratorUtils.EMPTY_ORDERED_ITERATOR, IteratorUtils.EMPTY_ORDERED_ITERATOR);
        assertSame(IteratorUtils.EMPTY_ORDERED_ITERATOR, IteratorUtils.emptyOrderedIterator());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            IteratorUtils.EMPTY_ORDERED_ITERATOR.next();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(NoSuchElementException.class, () -> {
            IteratorUtils.EMPTY_ORDERED_ITERATOR.previous();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_ORDERED_ITERATOR.remove();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
    }

    // -----------------------------------------------------------------------
    /**
     * Test empty map iterator
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testEmptyOrderedMapIterator() {
        assertSame(EmptyOrderedMapIterator.INSTANCE, IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR);
        assertTrue(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof Iterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof MapIterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof OrderedMapIterator);
        assertTrue(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof ResettableIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR instanceof ListIterator);
        assertFalse(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.hasNext());
        assertFalse(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.hasPrevious());
        ((ResettableIterator<Object>) IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR).reset();
        assertSame(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR, IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR);
        assertSame(IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR, IteratorUtils.emptyOrderedMapIterator());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.next();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(NoSuchElementException.class, () -> {
            IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.previous();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.remove();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(NoSuchElementException.class, () -> {
            IteratorUtils.EMPTY_ORDERED_ITERATOR.previous();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.getKey();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        exception = assertThrows(IllegalStateException.class, () -> {
            IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR.getValue();
        });
        assertTrue(exception.getMessage().contains("Iterator contains no elements"));
    }

    @Test
    public void testFilteredIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator ie = arrayList.iterator();
        try {
            IteratorUtils.filteredIterator(ie, null);
        } catch (final NullPointerException npe) {
            //
        }
        try {
            IteratorUtils.filteredIterator(null, null);
        } catch (final NullPointerException npe) {
            //
        }
    }

    @Test
    public void testFilteredListIterator() {
        final List arrayList = new ArrayList();
        arrayList.add("test");
        final Predicate predicate = INSTANCE;
        assertTrue(IteratorUtils.filteredListIterator(arrayList.listIterator(), predicate) instanceof ListIterator);
        try {
            IteratorUtils.filteredListIterator(null, predicate);
        } catch (final NullPointerException npe) {
            //
        }
        try {
            IteratorUtils.filteredListIterator(arrayList.listIterator(), null);
        } catch (final NullPointerException npe) {
            //
        }
    }

    @Test
    public void testFind() {
        Predicate<Number> testPredicate = equalPredicate((Number) 4);
        Integer test = IteratorUtils.find(iterableA.iterator(), testPredicate);
        assertEquals(4, (int) test);
        testPredicate = equalPredicate((Number) 45);
        test = IteratorUtils.find(iterableA.iterator(), testPredicate);
        assertNull(test);
        assertNull(IteratorUtils.find(null, testPredicate));
        Exception exception = assertThrows(NullPointerException.class, () -> {
            assertNull(IteratorUtils.find(iterableA.iterator(), null));
        });
        assertTrue(exception.getMessage().contains("predicate"));
    }

    @Test
    public void testFirstFromIterator() throws Exception {
        // Iterator, entry exists
        final Iterator<Integer> iterator = iterableA.iterator();
        assertEquals(1, (int) IteratorUtils.first(iterator));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testForEach() {
        final List<Integer> listA = new ArrayList<>();
        listA.add(1);

        final List<Integer> listB = new ArrayList<>();
        listB.add(2);

        final Closure<List<Integer>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<Integer>> col = new ArrayList<>();
        col.add(listA);
        col.add(listB);
        IteratorUtils.forEach(col.iterator(), testClosure);
        assertTrue(listA.isEmpty() && listB.isEmpty());
        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.forEach(col.iterator(), null);
        });
        assertTrue(exception.getMessage().contains("closure"));

        IteratorUtils.forEach(null, testClosure);

        // null should be OK
        col.add(null);
        IteratorUtils.forEach(col.iterator(), testClosure);
    }

    @Test
    public void testForEachButLast() {
        final List<Integer> listA = new ArrayList<>();
        listA.add(1);

        final List<Integer> listB = new ArrayList<>();
        listB.add(2);

        final Closure<List<Integer>> testClosure = ClosureUtils.invokerClosure("clear");
        final Collection<List<Integer>> col = new ArrayList<>();
        col.add(listA);
        col.add(listB);
        List<Integer> last = IteratorUtils.forEachButLast(col.iterator(), testClosure);
        assertTrue(listA.isEmpty() && !listB.isEmpty());
        assertSame(listB, last);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.forEachButLast(col.iterator(), null);
        });
        assertTrue(exception.getMessage().contains("closure"));

        IteratorUtils.forEachButLast(null, testClosure);

        // null should be OK
        col.add(null);
        col.add(null);
        last = IteratorUtils.forEachButLast(col.iterator(), testClosure);
        assertNull(last);
    }

    @Test
    public void testGetAtIndexFromIterator() throws Exception {
        // Iterator, entry exists
        Iterator<Integer> iterator = iterableA.iterator();
        assertEquals(1, (int) IteratorUtils.get(iterator, 0));
        iterator = iterableA.iterator();
        assertEquals(2, (int) IteratorUtils.get(iterator, 1));

        final Iterator<Integer> iteratorInteger = iterableA.iterator();
        // Iterator, non-existent entry
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            IteratorUtils.get(iteratorInteger, 10);
        });
        assertTrue(exception.getMessage().contains("Entry does not exist: 0"));
        assertFalse(iteratorInteger.hasNext());
    }

    @Test
    public void testGetIterator() {
        final Object[] objArray = { "a", "b", "c" };
        final Map<String, String> inMap = new HashMap<>();
        final Node[] nodes = createNodes();
        final NodeList nodeList = createNodeList(nodes);

        assertTrue(IteratorUtils.getIterator(null) instanceof EmptyIterator);
        assertTrue(IteratorUtils.getIterator(iterableA.iterator()) instanceof Iterator);
        assertTrue(IteratorUtils.getIterator(iterableA) instanceof Iterator);
        assertTrue(IteratorUtils.getIterator(objArray) instanceof ObjectArrayIterator);
        assertTrue(IteratorUtils.getIterator(inMap) instanceof Iterator);
        assertTrue(IteratorUtils.getIterator(nodeList) instanceof NodeListIterator);
        assertTrue(IteratorUtils.getIterator(new Vector().elements()) instanceof EnumerationIterator);
        final Node node1 = createMock(Node.class);
        assertTrue(IteratorUtils.getIterator(node1) instanceof NodeListIterator);
        final Dictionary dic = createMock(Dictionary.class);
        assertTrue(IteratorUtils.getIterator(dic) instanceof EnumerationIterator);
        final int[] arr = new int[8];
        assertTrue(IteratorUtils.getIterator(arr) instanceof ArrayIterator);
    }

    @Test
    public void testIndexOf() {
        Predicate<Number> testPredicate = equalPredicate((Number) 4);
        int index = IteratorUtils.indexOf(iterableA.iterator(), testPredicate);
        assertEquals(6, index);
        testPredicate = equalPredicate((Number) 45);
        index = IteratorUtils.indexOf(iterableA.iterator(), testPredicate);
        assertEquals(-1, index);
        assertEquals(-1, IteratorUtils.indexOf(null, testPredicate));
        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.indexOf(iterableA.iterator(), null);
        });
        assertTrue(exception.getMessage().contains("predicate"));
    }

    @Test(expected = NullPointerException.class)
    public void testLoopingIterator() {
        final ArrayList arrayList = new ArrayList();
        arrayList.add("test");
        final Collection coll = new ArrayList();
        coll.add("test");
        final Iterator ie = arrayList.iterator();
        assertTrue(IteratorUtils.loopingIterator(coll) instanceof ResettableIterator);
        IteratorUtils.loopingIterator(null);
    }

    @Test(expected = NullPointerException.class)
    public void testLoopingListIterator() {
        final ArrayList arrayList = new ArrayList();
        arrayList.add("test");
        final Iterator ie = arrayList.iterator();
        assertTrue(IteratorUtils.loopingListIterator(arrayList) instanceof ResettableIterator);
        IteratorUtils.loopingListIterator(null);
    }

    /**
     * Tests method nodeListIterator(Node)
     */
    @Test
    public void testNodeIterator() {
        final Node[] nodes = createNodes();
        final NodeList nodeList = createNodeList(nodes);
        final Node parentNode = createMock(Node.class);
        expect(parentNode.getChildNodes()).andStubReturn(nodeList);
        replay(parentNode);

        final Iterator<Node> iterator = IteratorUtils.nodeListIterator(parentNode);
        int expectedNodeIndex = 0;
        for (final Node actual : IteratorUtils.asIterable(iterator)) {
            assertEquals(nodes[expectedNodeIndex], actual);
            ++expectedNodeIndex;
        }

        // insure iteration occurred
        assertTrue(expectedNodeIndex > 0);

        // single use iterator
        assertFalse("should not be able to iterate twice", IteratorUtils.asIterable(iterator).iterator().hasNext());

        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.nodeListIterator((Node) null);
        });
        assertTrue(exception.getMessage().contains("node"));
    }

    /**
     * Tests method nodeListIterator(NodeList)
     */
    @Test
    public void testNodeListIterator() {
        final Node[] nodes = createNodes();
        final NodeList nodeList = createNodeList(nodes);

        final Iterator<Node> iterator = IteratorUtils.nodeListIterator(nodeList);
        int expectedNodeIndex = 0;
        for (final Node actual : IteratorUtils.asIterable(iterator)) {
            assertEquals(nodes[expectedNodeIndex], actual);
            ++expectedNodeIndex;
        }

        // insure iteration occurred
        assertTrue(expectedNodeIndex > 0);

        // single use iterator
        assertFalse("should not be able to iterate twice", IteratorUtils.asIterable(iterator).iterator().hasNext());

        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.nodeListIterator((NodeList) null);
        });
        assertTrue(exception.getMessage().contains("nodeList"));
    }

    @Test
    public void testObjectGraphIterator() {
        assertTrue(IteratorUtils.objectGraphIterator(null, null) instanceof Iterator);
    }

    @Test(expected = NullPointerException.class)
    public void testPeekingIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator ie = arrayList.iterator();
        assertTrue(IteratorUtils.peekingIterator(ie) instanceof Iterator);
        IteratorUtils.peekingIterator(null);

    }

    @Test(expected = NullPointerException.class)
    public void testPushBackIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator ie = arrayList.iterator();
        assertTrue(IteratorUtils.pushbackIterator(ie) instanceof Iterator);
        IteratorUtils.pushbackIterator(null);
    }

    @Test
    public void testSingletonIterator() {
        assertTrue(IteratorUtils.singletonIterator(new Object()) instanceof ResettableIterator);
    }

    @Test
    public void testSingletonListIterator() {
        assertTrue(IteratorUtils.singletonListIterator(new Object()) instanceof Iterator);
    }

    @Test
    public void testToArray() {
        final List<Object> list = new ArrayList<>();
        list.add(Integer.valueOf(1));
        list.add("Two");
        list.add(null);
        final Object[] result = IteratorUtils.toArray(list.iterator());
        assertEquals(list, Arrays.asList(result));

        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.toArray(null);
        });
        assertTrue(exception.getMessage().contains("iterator"));
    }

    @Test
    public void testToArray2() {
        final List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add(null);
        final String[] result = IteratorUtils.toArray(list.iterator(), String.class);
        assertEquals(list, Arrays.asList(result));

        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.toArray(list.iterator(), null);
        });
        assertTrue(exception.getMessage().contains("arrayClass"));

        exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.toArray(null, String.class);
        });
        assertTrue(exception.getMessage().contains("iterator"));
    }

    @Test
    public void testToList() {
        final List<Object> list = new ArrayList<>();
        list.add(Integer.valueOf(1));
        list.add("Two");
        list.add(null);
        final List<Object> result = IteratorUtils.toList(list.iterator());
        assertEquals(list, result);
        // add
        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.toList(null, 10);
        });
        assertTrue(exception.getMessage().contains("iterator"));

        exception = assertThrows(IllegalArgumentException.class, () -> {
            IteratorUtils.toList(list.iterator(), -1);
        });
        assertTrue(exception.getMessage().contains("Estimated size must be greater than 0"));
    }

    @Test
    public void testToListIterator() {
        final List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(0));
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        final Iterator<Integer> iterator = list.iterator();

        final ListIterator<Integer> liItr = IteratorUtils.toListIterator(iterator);
        int expected = 0;
        while (liItr.hasNext()) {
            assertEquals(expected, liItr.next().intValue());
            ++expected;
        }
    }

    @Test
    public void testToListIteratorNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            IteratorUtils.toListIterator(null);
        });
        assertTrue(exception.getMessage().contains("iterator"));
    }

    @Test
    public void testTransformedIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator ie = arrayList.iterator();
        try {
            IteratorUtils.transformedIterator(ie, null);
        } catch (final NullPointerException npe) {
            //
        }
        try {
            IteratorUtils.transformedIterator(null, null);
        } catch (final NullPointerException npe) {
            //
        }
    }

    /**
     * Test remove() for an immutable Iterator.
     */
    @Test
    public void testUnmodifiableIteratorImmutability() {
        final Iterator<String> iterator = getImmutableIterator();

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            iterator.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));
        iterator.next();

        // We shouldn't get to here.
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            iterator.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));

    }

    // -----------------------------------------------------------------------
    /**
     * Test next() and hasNext() for an immutable Iterator.
     */
    @Test
    public void testUnmodifiableIteratorIteration() {
        final Iterator<String> iterator = getImmutableIterator();

        assertTrue(iterator.hasNext());

        assertEquals("a", iterator.next());

        assertTrue(iterator.hasNext());

        assertEquals("b", iterator.next());

        assertTrue(iterator.hasNext());

        assertEquals("c", iterator.next());

        assertTrue(iterator.hasNext());

        assertEquals("d", iterator.next());

        assertFalse(iterator.hasNext());
    }

    /**
     * Test remove() for an immutable ListIterator.
     */
    @Test
    public void testUnmodifiableListIteratorImmutability() {
        final ListIterator<String> listIterator = getImmutableListIterator();

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            listIterator.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            listIterator.set("a");
        });
        assertTrue(exception.getMessage().contains("set() is not supported"));

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            listIterator.add("a");
        });
        assertTrue(exception.getMessage().contains("add() is not supported"));

        listIterator.next();

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            listIterator.remove();
        });
        assertTrue(exception.getMessage().contains("remove() is not supported"));

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            listIterator.set("a");
        });
        assertTrue(exception.getMessage().contains("set() is not supported"));

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            listIterator.add("a");
        });
        assertTrue(exception.getMessage().contains("add() is not supported"));
    }

    /**
     * Test next(), hasNext(), previous() and hasPrevious() for an immutable ListIterator.
     */
    @Test
    public void testUnmodifiableListIteratorIteration() {
        final ListIterator<String> listIterator = getImmutableListIterator();

        assertFalse(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("a", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("b", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("c", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("d", listIterator.next());

        assertTrue(listIterator.hasPrevious());
        assertFalse(listIterator.hasNext());

        assertEquals("d", listIterator.previous());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("c", listIterator.previous());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("b", listIterator.previous());

        assertTrue(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());

        assertEquals("a", listIterator.previous());

        assertFalse(listIterator.hasPrevious());
        assertTrue(listIterator.hasNext());
    }

    @Test(expected = NullPointerException.class)
    public void testUnmodifiableMapIterator() {
        final Set<?> set = new LinkedHashSet<>();
        final MapIterator ie = new EntrySetToMapIteratorAdapter(set);
        assertTrue(IteratorUtils.unmodifiableMapIterator(ie) instanceof MapIterator);
        IteratorUtils.unmodifiableMapIterator(null);

    }

    @Test
    public void testZippingIterator() {
        final ArrayList arrayList = new ArrayList();
        final Iterator ie = arrayList.iterator();
        assertTrue(IteratorUtils.zippingIterator(ie, ie, ie) instanceof ZippingIterator);
        assertTrue(IteratorUtils.zippingIterator(ie, ie) instanceof ZippingIterator);
    }
}
