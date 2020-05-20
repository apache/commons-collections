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
package org.apache.commons.collections4.collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.collections4.AbstractObjectTest;

/**
 * Abstract test class for {@link java.util.Collection} methods and contracts.
 * <p>
 * You should create a concrete subclass of this class to test any custom
 * {@link Collection} implementation.  At minimum, you'll have to
 * implement the @{@link #makeObject()}, {@link #makeConfirmedCollection()}
 * and {@link #makeConfirmedFullCollection()} methods.
 * You might want to override some of the additional public methods as well:
 * <p>
 * <b>Element Population Methods</b>
 * <p>
 * Override these if your collection restricts what kind of elements are
 * allowed (for instance, if {@code null} is not permitted):
 * <ul>
 * <li>{@link #getFullElements()}
 * <li>{@link #getOtherElements()}
 * </ul>
 * <p>
 * <b>Supported Operation Methods</b>
 * <p>
 * Override these if your collection doesn't support certain operations:
 * <ul>
 * <li>{@link #isAddSupported()}
 * <li>{@link #isRemoveSupported()}
 * <li>{@link #areEqualElementsDistinguishable()}
 * <li>{@link #isNullSupported()}
 * <li>{@link #isFailFastSupported()}
 * </ul>
 * <p>
 * <b>Fixture Methods</b>
 * <p>
 * Fixtures are used to verify that the operation results in correct state
 * for the collection.  Basically, the operation is performed against your
 * collection implementation, and an identical operation is performed against a
 * <i>confirmed</i> collection implementation.  A confirmed collection
 * implementation is something like {@code java.util.ArrayList}, which is
 * known to conform exactly to its collection interface's contract.  After the
 * operation takes place on both your collection implementation and the
 * confirmed collection implementation, the two collections are compared to see
 * if their state is identical.  The comparison is usually much more involved
 * than a simple {@code equals} test.  This verification is used to ensure
 * proper modifications are made along with ensuring that the collection does
 * not change when read-only modifications are made.
 * <p>
 * The {@link #collection} field holds an instance of your collection
 * implementation; the {@link #confirmed} field holds an instance of the
 * confirmed collection implementation.  The {@link #resetEmpty()} and
 * {@link #resetFull()} methods set these fields to empty or full collections,
 * so that tests can proceed from a known state.
 * <p>
 * After a modification operation to both {@link #collection} and
 * {@link #confirmed}, the {@link #verify()} method is invoked to compare
 * the results.  You may want to override {@link #verify()} to perform
 * additional verifications.  For instance, when testing the collection
 * views of a map, {@link org.apache.commons.collections4.map.AbstractMapTest AbstractTestMap}
 * would override {@link #verify()} to make
 * sure the map is changed after the collection view is changed.
 * <p>
 * If you're extending this class directly, you will have to provide
 * implementations for the following:
 * <ul>
 * <li>{@link #makeConfirmedCollection()}
 * <li>{@link #makeConfirmedFullCollection()}
 * </ul>
 * <p>
 * Those methods should provide a confirmed collection implementation
 * that's compatible with your collection implementation.
 * <p>
 * If you're extending {@link org.apache.commons.collections4.list.AbstractListTest AbstractListTest},
 * {@link org.apache.commons.collections4.set.AbstractSetTest AbstractTestSet},
 * or {@link org.apache.commons.collections4.bag.AbstractBagTest AbstractBagTest},
 * you probably don't have to worry about the
 * above methods, because those three classes already override the methods
 * to provide standard JDK confirmed collections.<P>
 * <p>
 * <b>Other notes</b>
 * <p>
 * If your {@link Collection} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Collection} fails.
 *
 */
public abstract class AbstractCollectionTest<E> extends AbstractObjectTest {

    //
    // NOTE:
    //
    // Collection doesn't define any semantics for equals, and recommends you
    // use reference-based default behavior of Object.equals.  (And a test for
    // that already exists in AbstractTestObject).  Tests for equality of lists, sets
    // and bags will have to be written in test subclasses.  Thus, there is no
    // tests on Collection.equals nor any for Collection.hashCode.
    //

    // These fields are used by reset() and verify(), and any test
    // method that tests a modification.

    /**
     *  A collection instance that will be used for testing.
     */
    private Collection<E> collection;

    /**
     *  Confirmed collection.  This is an instance of a collection that is
     *  confirmed to conform exactly to the java.util.Collection contract.
     *  Modification operations are tested by performing a mod on your
     *  collection, performing the exact same mod on an equivalent confirmed
     *  collection, and then calling verify() to make sure your collection
     *  still matches the confirmed collection.
     */
    private Collection<E> confirmed;

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractCollectionTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     *  Specifies whether equal elements in the collection are, in fact,
     *  distinguishable with information not readily available.  That is, if a
     *  particular value is to be removed from the collection, then there is
     *  one and only one value that can be removed, even if there are other
     *  elements which are equal to it.
     *
     *  <P>In most collection cases, elements are not distinguishable (equal is
     *  equal), thus this method defaults to return false.  In some cases,
     *  however, they are.  For example, the collection returned from the map's
     *  values() collection view are backed by the map, so while there may be
     *  two values that are equal, their associated keys are not.  Since the
     *  keys are distinguishable, the values are.
     *
     *  <P>This flag is used to skip some verifications for iterator.remove()
     *  where it is impossible to perform an equivalent modification on the
     *  confirmed collection because it is not possible to determine which
     *  value in the confirmed collection to actually remove.  Tests that
     *  override the default (i.e. where equal elements are distinguishable),
     *  should provide additional tests on iterator.remove() to make sure the
     *  proper elements are removed when remove() is called on the iterator.
     **/
    public boolean areEqualElementsDistinguishable() {
        return false;
    }

    /**
     *  Returns true if the collections produced by
     *  {@link #makeObject()} and {@link #makeFullCollection()}
     *  support the {@code add} and {@code addAll}
     *  operations.<P>
     *  Default implementation returns true.  Override if your collection
     *  class does not support add or addAll.
     */
    public boolean isAddSupported() {
        return true;
    }

    /**
     *  Returns true if the collections produced by
     *  {@link #makeObject()} and {@link #makeFullCollection()}
     *  support the {@code remove}, {@code removeAll},
     *  {@code retainAll}, {@code clear} and
     *  {@code iterator().remove()} methods.
     *  Default implementation returns true.  Override if your collection
     *  class does not support removal operations.
     */
    public boolean isRemoveSupported() {
        return true;
    }

    /**
     * Returns true to indicate that the collection supports holding null.
     * The default implementation returns true;
     */
    public boolean isNullSupported() {
        return true;
    }

    /**
     * Returns true to indicate that the collection supports fail fast iterators.
     * The default implementation returns true;
     */
    public boolean isFailFastSupported() {
        return false;
    }

    /**
     * Returns true to indicate that the collection supports equals() comparisons.
     * This implementation returns false;
     */
    @Override
    public boolean isEqualsCheckable() {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     *  Verifies that {@link #collection} and {@link #confirmed} have
     *  identical state.
     */
    public void verify() {
        final int confirmedSize = getConfirmed().size();
        assertEquals("Collection size should match confirmed collection's", confirmedSize,
                getCollection().size());
        assertEquals("Collection isEmpty() result should match confirmed collection's",
                getConfirmed().isEmpty(), getCollection().isEmpty());

        // verify the collections are the same by attempting to match each
        // object in the collection and confirmed collection.  To account for
        // duplicates and differing orders, each confirmed element is copied
        // into an array and a flag is maintained for each element to determine
        // whether it has been matched once and only once.  If all elements in
        // the confirmed collection are matched once and only once and there
        // aren't any elements left to be matched in the collection,
        // verification is a success.

        // copy each collection value into an array
        final Object[] confirmedValues = new Object[confirmedSize];

        Iterator<E> iter;

        iter = getConfirmed().iterator();
        int pos = 0;
        while (iter.hasNext()) {
            confirmedValues[pos++] = iter.next();
        }

        // allocate an array of boolean flags for tracking values that have
        // been matched once and only once.
        final boolean[] matched = new boolean[confirmedSize];

        // now iterate through the values of the collection and try to match
        // the value with one in the confirmed array.
        iter = getCollection().iterator();
        while (iter.hasNext()) {
            final Object o = iter.next();
            boolean match = false;
            for (int i = 0; i < confirmedSize; i++) {
                if (matched[i]) {
                    // skip values already matched
                    continue;
                }
                if (Objects.equals(o, confirmedValues[i])) {
                    // values matched
                    matched[i] = true;
                    match = true;
                    break;
                }
            }
            // no match found!
            if (!match) {
                fail("Collection should not contain a value that the "
                        + "confirmed collection does not have: " + o + "\nTest: " + getCollection()
                        + "\nReal: " + getConfirmed());
            }
        }

        // make sure there aren't any unmatched values
        for (int i = 0; i < confirmedSize; i++) {
            if (!matched[i]) {
                // the collection didn't match all the confirmed values
                fail("Collection should contain all values that are in the confirmed collection"
                        + "\nTest: " + getCollection() + "\nReal: " + getConfirmed());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     *  Resets the {@link #collection} and {@link #confirmed} fields to empty
     *  collections.  Invoke this method before performing a modification
     *  test.
     */
    public void resetEmpty() {
        this.setCollection(makeObject());
        this.setConfirmed(makeConfirmedCollection());
    }

    /**
     *  Resets the {@link #collection} and {@link #confirmed} fields to full
     *  collections.  Invoke this method before performing a modification
     *  test.
     */
    public void resetFull() {
        this.setCollection(makeFullCollection());
        this.setConfirmed(makeConfirmedFullCollection());
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns a confirmed empty collection.
     *  For instance, an {@link java.util.ArrayList} for lists or a
     *  {@link java.util.HashSet} for sets.
     *
     *  @return a confirmed empty collection
     */
    public abstract Collection<E> makeConfirmedCollection();

    /**
     *  Returns a confirmed full collection.
     *  For instance, an {@link java.util.ArrayList} for lists or a
     *  {@link java.util.HashSet} for sets.  The returned collection
     *  should contain the elements returned by {@link #getFullElements()}.
     *
     *  @return a confirmed full collection
     */
    public abstract Collection<E> makeConfirmedFullCollection();

    /**
     * Return a new, empty {@link Collection} to be used for testing.
     */
    @Override
    public abstract Collection<E> makeObject();

    /**
     *  Returns a full collection to be used for testing.  The collection
     *  returned by this method should contain every element returned by
     *  {@link #getFullElements()}.  The default implementation, in fact,
     *  simply invokes {@code addAll} on an empty collection with
     *  the results of {@link #getFullElements()}.  Override this default
     *  if your collection doesn't support addAll.
     */
    public Collection<E> makeFullCollection() {
        final Collection<E> c = makeObject();
        c.addAll(Arrays.asList(getFullElements()));
        return c;
    }

    /**
     * Creates a new Map Entry that is independent of the first and the map.
     */
    public Map.Entry<E, E> cloneMapEntry(final Map.Entry<E, E> entry) {
        final HashMap<E, E> map = new HashMap<>();
        map.put(entry.getKey(), entry.getValue());
        return map.entrySet().iterator().next();
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns an array of objects that are contained in a collection
     *  produced by {@link #makeFullCollection()}.  Every element in the
     *  returned array <I>must</I> be an element in a full collection.<P>
     *  The default implementation returns a heterogenous array of
     *  objects with some duplicates. null is added if allowed.
     *  Override if you require specific testing elements.  Note that if you
     *  override {@link #makeFullCollection()}, you <I>must</I> override
     *  this method to reflect the contents of a full collection.
     */
    @SuppressWarnings("unchecked")
    public E[] getFullElements() {
        if (isNullSupported()) {
            final ArrayList<E> list = new ArrayList<>();
            list.addAll(Arrays.asList(getFullNonNullElements()));
            list.add(4, null);
            return (E[]) list.toArray();
        }
        return getFullNonNullElements().clone();
    }

    /**
     *  Returns an array of elements that are <I>not</I> contained in a
     *  full collection.  Every element in the returned array must
     *  not exist in a collection returned by {@link #makeFullCollection()}.
     *  The default implementation returns a heterogenous array of elements
     *  without null.  Note that some of the tests add these elements
     *  to an empty or full collection, so if your collection restricts
     *  certain kinds of elements, you should override this method.
     */
    public E[] getOtherElements() {
        return getOtherNonNullElements();
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns a list of elements suitable for return by
     *  {@link #getFullElements()}.  The array returned by this method
     *  does not include null, but does include a variety of objects
     *  of different types.  Override getFullElements to return
     *  the results of this method if your collection does not support
     *  the null element.
     */
    @SuppressWarnings("unchecked")
    public E[] getFullNonNullElements() {
        return (E[]) new Object[] {
            new String(""),
            new String("One"),
            Integer.valueOf(2),
            "Three",
            Integer.valueOf(4),
            "One",
            new Double(5),
            new Float(6),
            "Seven",
            "Eight",
            new String("Nine"),
            Integer.valueOf(10),
            new Short((short) 11),
            new Long(12),
            "Thirteen",
            "14",
            "15",
            new Byte((byte) 16)
        };
    }

    /**
     *  Returns the default list of objects returned by
     *  {@link #getOtherElements()}.  Includes many objects
     *  of different types.
     */
    @SuppressWarnings("unchecked")
    public E[] getOtherNonNullElements() {
        return (E[]) new Object[] {
            Integer.valueOf(0),
            new Float(0),
            new Double(0),
            "Zero",
            new Short((short) 0),
            new Byte((byte) 0),
            new Long(0),
            new Character('\u0000'),
            "0"
        };
    }

    /**
     *  Returns a list of string elements suitable for return by
     *  {@link #getFullElements()}.  Override getFullElements to return
     *  the results of this method if your collection does not support
     *  heterogenous elements or the null element.
     */
    public Object[] getFullNonNullStringElements() {
        return new Object[] {
            "If", "the", "dull", "substance", "of", "my", "flesh", "were",
            "thought", "Injurious", "distance", "could", "not", "stop", "my", "way",
        };
    }

    /**
     *  Returns a list of string elements suitable for return by
     *  {@link #getOtherElements()}.  Override getOtherElements to return
     *  the results of this method if your collection does not support
     *  heterogenous elements or the null element.
     */
    public Object[] getOtherNonNullStringElements() {
        return new Object[] {
            "For", "then", "despite", /* of */"space", "I", "would", "be",
            "brought", "From", "limits", "far", "remote", "where", "thou", "dost", "stay"
        };
    }

    // Tests
    //-----------------------------------------------------------------------
    /**
     *  Tests {@link Collection#add(Object)}.
     */
    public void testCollectionAdd() {
        if (!isAddSupported()) {
            return;
        }

        final E[] elements = getFullElements();
        for (final E element : elements) {
            resetEmpty();
            final boolean r = getCollection().add(element);
            getConfirmed().add(element);
            verify();
            assertTrue(r);
            assertEquals("Collection size is 1 after first add", 1, getCollection().size());
        }

        resetEmpty();
        int size = 0;
        for (final E element : elements) {
            final boolean r = getCollection().add(element);
            getConfirmed().add(element);
            verify();
            if (r) {
                size++;
            }
            assertEquals("Collection size should grow after add", size, getCollection().size());
            assertTrue(getCollection().contains(element));
        }
    }

    /**
     *  Tests {@link Collection#addAll(Collection)}.
     */
    public void testCollectionAddAll() {
        if (!isAddSupported()) {
            return;
        }

        resetEmpty();
        E[] elements = getFullElements();
        boolean r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue(r);
        for (final E element : elements) {
            assertTrue(getCollection().contains(element));
        }

        resetFull();
        int size = getCollection().size();
        elements = getOtherElements();
        r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue("Full collection should change after addAll", r);
        for (final E element : elements) {
            assertTrue(getCollection().contains(element));
        }
        assertEquals("Size should increase after addAll", size + elements.length, getCollection().size());

        resetFull();
        size = getCollection().size();
        r = getCollection().addAll(Arrays.asList(getFullElements()));
        getConfirmed().addAll(Arrays.asList(getFullElements()));
        verify();
        if (r) {
            assertTrue(size < getCollection().size());
        } else {
            assertEquals("Size should not change if addAll returns false", size, getCollection().size());
        }
    }

    /**
     *  If {@link #isAddSupported()} returns false, tests that add operations
     *  raise <code>UnsupportedOperationException.
     */
    public void testUnsupportedAdd() {
        if (isAddSupported()) {
            return;
        }

        resetEmpty();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().add(getFullNonNullElements()[0]);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().addAll(Arrays.asList(getFullElements()));
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        resetFull();
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().add(getFullNonNullElements()[0]);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().addAll(Arrays.asList(getOtherElements()));
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();
    }

    /**
     *  Test {@link Collection#clear()}.
     */
    public void testCollectionClear() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        getCollection().clear(); // just to make sure it doesn't raise anything
        verify();

        resetFull();
        getCollection().clear();
        getConfirmed().clear();
        verify();
    }

    /**
     *  Tests {@link Collection#contains(Object)}.
     */
    public void testCollectionContains() {
        Object[] elements;

        resetEmpty();
        elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            assertFalse("Empty collection shouldn't contain element[" + i + "]", getCollection().contains(elements[i]));
        }
        // make sure calls to "contains" don't change anything
        verify();

        elements = getOtherElements();
        for (int i = 0; i < elements.length; i++) {
            assertFalse("Empty collection shouldn't contain element[" + i + "]", getCollection().contains(elements[i]));
        }
        // make sure calls to "contains" don't change anything
        verify();

        resetFull();
        elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            assertTrue(getCollection().contains(elements[i]));
        }
        // make sure calls to "contains" don't change anything
        verify();

        resetFull();
        elements = getOtherElements();
        for (final Object element : elements) {
            assertFalse("Full collection shouldn't contain element", getCollection().contains(element));
        }
    }

    /**
     *  Tests {@link Collection#containsAll(Collection)}.
     */
    public void testCollectionContainsAll() {
        resetEmpty();
        Collection<E> col = new HashSet<>();
        assertTrue(getCollection().containsAll(col));
        col.addAll(Arrays.asList(getOtherElements()));
        assertFalse("Empty Collection shouldn't contain all elements of " +
                "a non-empty Collection.", getCollection().containsAll(col));
        // make sure calls to "containsAll" don't change anything
        verify();

        resetFull();
        assertFalse("Full collection shouldn't contain other elements", getCollection().containsAll(col));

        col.clear();
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue(getCollection().containsAll(col));
        // make sure calls to "containsAll" don't change anything
        verify();

        final int min = getFullElements().length < 4 ? 0 : 2;
        final int max = getFullElements().length == 1 ? 1 :
                getFullElements().length <= 5 ? getFullElements().length - 1 : 5;
        col = Arrays.asList(getFullElements()).subList(min, max);
        assertTrue(getCollection().containsAll(col));
        assertTrue(getCollection().containsAll(getCollection()));
        // make sure calls to "containsAll" don't change anything
        verify();

        col = new ArrayList<>();
        col.addAll(Arrays.asList(getFullElements()));
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue(getCollection().containsAll(col));

        // make sure calls to "containsAll" don't change anything
        verify();
    }

    /**
     *  Tests {@link Collection#isEmpty()}.
     */
    public void testCollectionIsEmpty() {
        resetEmpty();
        assertTrue(getCollection().isEmpty());
        // make sure calls to "isEmpty() don't change anything
        verify();

        resetFull();
        assertFalse("Full collection shouldn't be empty", getCollection().isEmpty());
        // make sure calls to "isEmpty() don't change anything
        verify();
    }

    /**
     *  Tests the read-only functionality of {@link Collection#iterator()}.
     */
    public void testCollectionIterator() {
        resetEmpty();
        final Iterator<E> it = getCollection().iterator();
        assertFalse("Iterator for empty Collection shouldn't have next.", it.hasNext());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            it.next();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("element"));
        }
        // make sure nothing has changed after non-modification
        verify();

        resetFull();
        Iterator<E> it1 = getCollection().iterator();
        for (int i = 0; i < getCollection().size(); i++) {
            assertTrue(it1.hasNext());
            it1.next();
        }
        assertFalse("Iterator should be finished", it1.hasNext());

        final ArrayList<E> list = new ArrayList<>();
        final Iterator<E> it2 = getCollection().iterator();
        for (int i = 0; i < getCollection().size(); i++) {
            final E next = it2.next();
            assertTrue(getCollection().contains(next));
            list.add(next);
        }
        exception = assertThrows(NoSuchElementException.class, () -> {
            it2.next();
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
        // make sure nothing has changed after non-modification
        verify();
    }

    /**
     *  Tests removals from {@link Collection#iterator()}.
     */
    @SuppressWarnings("unchecked")
    public void testCollectionIteratorRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            getCollection().iterator().remove();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("Iterator"));
        }
        verify();

        final Iterator<E> iter1 = getCollection().iterator();
        iter1.hasNext();
        exception = assertThrows(IllegalStateException.class, () -> {
            iter1.remove();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("Iterator"));
        }
        verify();

        resetFull();
        int size = getCollection().size();
        Iterator<E> iter = getCollection().iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            // TreeMap reuses the Map Entry, so the verify below fails
            // Clone it here if necessary
            if (o instanceof Map.Entry) {
                o = cloneMapEntry((Map.Entry<E, E>) o);
            }
            iter.remove();

            // if the elements aren't distinguishable, we can just remove a
            // matching element from the confirmed collection and verify
            // contents are still the same.  Otherwise, we don't have the
            // ability to distinguish the elements and determine which to
            // remove from the confirmed collection (in which case, we don't
            // verify because we don't know how).
            //
            // see areEqualElementsDistinguishable()
            if (!areEqualElementsDistinguishable()) {
                getConfirmed().remove(o);
                verify();
            }

            size--;
            assertEquals("Collection should shrink by one after iterator.remove", size,
                    getCollection().size());
        }
        assertTrue(getCollection().isEmpty());

        resetFull();
        final Iterator<E> iter2 = getCollection().iterator();
        iter2.next();
        iter2.remove();
        exception = assertThrows(IllegalStateException.class, () -> {
            iter2.remove();
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
    }

    /**
     *  Tests {@link Collection#remove(Object)}.
     */
    public void testCollectionRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        final E[] elements = getFullElements();
        for (final E element : elements) {
            assertTrue(!getCollection().remove(element));
            verify();
        }

        final E[] other = getOtherElements();

        resetFull();
        for (final E element : other) {
            assertFalse("Shouldn't remove nonexistent other element", getCollection().remove(element));
            verify();
        }

        final int size = getCollection().size();
        for (final E element : elements) {
            resetFull();
            assertTrue(getCollection().remove(element));

            // if the elements aren't distinguishable, we can just remove a
            // matching element from the confirmed collection and verify
            // contents are still the same.  Otherwise, we don't have the
            // ability to distinguish the elements and determine which to
            // remove from the confirmed collection (in which case, we don't
            // verify because we don't know how).
            //
            // see areEqualElementsDistinguishable()
            if (!areEqualElementsDistinguishable()) {
                getConfirmed().remove(element);
                verify();
            }

            assertEquals("Collection should shrink after remove", size - 1, getCollection().size());
        }
    }

    /**
     *  Tests {@link Collection#removeAll(Collection)}.
     */
    public void testCollectionRemoveAll() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        assertTrue(!getCollection().removeAll(Collections.EMPTY_SET));
        verify();

        assertTrue(!getCollection().removeAll(new ArrayList<>(getCollection())));
        verify();

        resetFull();
        assertFalse("Full collection removeAll should return false for empty input", getCollection().removeAll(Collections.EMPTY_SET));
        verify();

        assertFalse("Full collection removeAll should return false for other elements", getCollection().removeAll(Arrays.asList(getOtherElements())));
        verify();

        assertTrue(getCollection().removeAll(new HashSet<>(getCollection())));
        getConfirmed().removeAll(new HashSet<>(getConfirmed()));
        verify();

        resetFull();
        final int size = getCollection().size();
        final int min = getFullElements().length < 4 ? 0 : 2;
        final int max = getFullElements().length == 1 ? 1 :
                getFullElements().length <= 5 ? getFullElements().length - 1 : 5;
        final Collection<E> all = Arrays.asList(getFullElements()).subList(min, max);
        assertTrue(getCollection().removeAll(all));
        getConfirmed().removeAll(all);
        verify();

        assertTrue(getCollection().size() < size);
        for (final E element : all) {
            assertFalse("Collection shouldn't contain removed element", getCollection().contains(element));
        }
    }

    /**
     *  Tests {@link Collection#removeIf(Predicate)}.
     * @since 4.4
     */
    public void testCollectionRemoveIf() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        assertFalse("Empty collection removeIf should return false for a predicate that returns only false", getCollection().removeIf(e -> false));
        verify();

        assertFalse("Empty collection removeIf should return false for a predicate that returns only true", getCollection().removeIf(e -> true));
        verify();

        resetFull();
        assertFalse("Full collection removeIf should return false for a predicate that returns only false", getCollection().removeIf(e -> false));
        verify();

        assertTrue(getCollection().removeIf(e -> true));
        getConfirmed().removeIf(e -> true);
        verify();

        resetFull();
        final List<E> elements = Arrays.asList(getFullElements());

        final int mid = getFullElements().length / 2;
        final E target = elements.get(mid);

        final int size = getCollection().size();
        final int targetCount = Collections.frequency(elements, target);

        final Predicate<E> filter = e -> target.equals(e);

        assertTrue(getCollection().removeIf(filter));
        getConfirmed().removeIf(filter);
        verify();

        assertEquals("Collection should shrink after removeIf", getCollection().size(), size - targetCount);
        assertFalse("Collection shouldn't contain removed element", getCollection().contains(target));
    }

    /**
     *  Tests {@link Collection#retainAll(Collection)}.
     */
    public void testCollectionRetainAll() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        final List<E> elements = Arrays.asList(getFullElements());
        final List<E> other = Arrays.asList(getOtherElements());

        assertFalse("Empty retainAll() should return false", getCollection().retainAll(Collections.EMPTY_SET));
        verify();

        assertFalse("Empty retainAll() should return false", getCollection().retainAll(elements));
        verify();

        resetFull();
        assertTrue(getCollection().retainAll(Collections.EMPTY_SET));
        getConfirmed().retainAll(Collections.EMPTY_SET);
        verify();

        resetFull();
        assertTrue(getCollection().retainAll(other));
        getConfirmed().retainAll(other);
        verify();

        resetFull();
        int size = getCollection().size();
        assertFalse("Collection shouldn't change from retainAll elements", getCollection().retainAll(elements));
        verify();
        assertEquals("Collection size shouldn't change", size, getCollection().size());

        if (getFullElements().length > 1) {
            resetFull();
            size = getCollection().size();
            final int min = getFullElements().length < 4 ? 0 : 2;
            final int max = getFullElements().length <= 5 ? getFullElements().length - 1 : 5;
            assertTrue(getCollection().retainAll(elements.subList(min, max)));
            getConfirmed().retainAll(elements.subList(min, max));
            verify();

            for (final E element : getCollection()) {
                assertTrue(elements.subList(min, max).contains(element));
            }
        }

        resetFull();
        final HashSet<E> set = new HashSet<>(elements);
        size = getCollection().size();
        assertFalse("Collection shouldn't change from retainAll without " +
                "duplicate elements", getCollection().retainAll(set));
        verify();
        assertEquals("Collection size didn't change from nonduplicate " +
                     "retainAll", size, getCollection().size());
    }

    /**
     *  Tests {@link Collection#size()}.
     */
    public void testCollectionSize() {
        resetEmpty();
        assertEquals("Size of new Collection is 0.", 0, getCollection().size());

        resetFull();
        assertTrue(getCollection().size() > 0);
    }

    /**
     *  Tests {@link Collection#toArray()}.
     */
    public void testCollectionToArray() {
        resetEmpty();
        assertEquals("Empty Collection should return empty array for toArray",
                     0, getCollection().toArray().length);

        resetFull();
        final Object[] array = getCollection().toArray();
        assertEquals("Full collection toArray should be same size as collection",
                array.length, getCollection().size());
        final Object[] confirmedArray = getConfirmed().toArray();
        assertEquals("length of array from confirmed collection should "
                + "match the length of the collection's array", confirmedArray.length, array.length);
        final boolean[] matched = new boolean[array.length];

        for (int i = 0; i < array.length; i++) {
            assertTrue(getCollection().contains(array[i]));

            boolean match = false;
            // find a match in the confirmed array
            for (int j = 0; j < array.length; j++) {
                // skip already matched
                if (matched[j]) {
                    continue;
                }
                if (Objects.equals(array[i], confirmedArray[j])) {
                    matched[j] = true;
                    match = true;
                    break;
                }
            }
            if (!match) {
                fail("element " + i + " in returned array should be found "
                        + "in the confirmed collection's array");
            }
        }
        for (final boolean element : matched) {
            assertTrue(element);
        }
    }

    /**
     *  Tests {@link Collection#toArray(Object[])}.
     */
    public void testCollectionToArray2() {
        resetEmpty();
        Object[] a = new Object[] { new Object(), null, null };
        Object[] array = getCollection().toArray(a);
        assertEquals("Given array shouldn't shrink", array, a);
        assertNull(a[0]);
        verify();

        resetFull();
        Exception exception = assertThrows(ArrayStoreException.class, () -> {
            getCollection().toArray(new Void[0]);
        });
        if (null != exception.getMessage()) {
            assertNotNull(exception.getMessage());
        }
        verify();

        exception = assertThrows(NullPointerException.class, () -> {
            getCollection().toArray((Object[]) null);
        });
        assertNull(exception.getMessage());
        verify();

        array = getCollection().toArray(new Object[0]);
        a = getCollection().toArray();
        assertEquals("toArrays should be equal",
                     Arrays.asList(array), Arrays.asList(a));

        // Figure out if they're all the same class
        // TODO: It'd be nicer to detect a common superclass
        final HashSet<Class<?>> classes = new HashSet<>();
        for (final Object element : array) {
            classes.add(element == null ? null : element.getClass());
        }
        if (classes.size() > 1) {
            return;
        }

        Class<?> cl = classes.iterator().next();
        if (Map.Entry.class.isAssignableFrom(cl)) {  // check needed for protective cases like Predicated/Unmod map entrySet
            cl = Map.Entry.class;
        }
        a = (Object[]) Array.newInstance(cl, 0);
        array = getCollection().toArray(a);
        assertEquals("toArray(Object[]) should return correct array type",
                a.getClass(), array.getClass());
        assertEquals("type-specific toArrays should be equal",
                Arrays.asList(array),
                Arrays.asList(getCollection().toArray()));
        verify();
    }

    /**
     *  Tests {@code toString} on a collection.
     */
    public void testCollectionToString() {
        resetEmpty();
        assertNotNull("toString shouldn't return null", getCollection().toString());

        resetFull();
        assertNotNull("toString shouldn't return null", getCollection().toString());
    }

    /**
     *  If isRemoveSupported() returns false, tests to see that remove
     *  operations raise an UnsupportedOperationException.
     */
    public void testUnsupportedRemove() {
        if (isRemoveSupported()) {
            return;
        }

        resetEmpty();
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().clear();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("List is fixed size"));
        }
        verify();

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().remove(null);
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("List is fixed size"));
        }
        verify();

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().removeIf(e -> true);
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("List is fixed size"));
        }
        verify();

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().removeAll(null);
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("List is fixed size"));
        }
        verify();

        exception = assertThrows(UnsupportedOperationException.class, () -> {
            getCollection().retainAll(null);
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("List is fixed size"));
        }
        verify();

        resetFull();
        final Iterator<E> iterator = getCollection().iterator();
        iterator.next();
        exception = assertThrows(UnsupportedOperationException.class, () -> {
            iterator.remove();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("remove() is not supported"));
        }
        verify();

    }

    /**
     *  Tests that the collection's iterator is fail-fast.
     */
    public void testCollectionIteratorFailFast() {
        if (!isFailFastSupported()) {
            return;
        }

        if (isAddSupported()) {
            resetFull();
            final Iterator<E> iter = getCollection().iterator();
            final E o = getOtherElements()[0];
            getCollection().add(o);
            getConfirmed().add(o);
            Exception exception = assertThrows(ConcurrentModificationException.class, () -> {
                iter.next();
            });
            assertNull(exception.getMessage());
            verify();

            resetFull();
            final Iterator<E> iter1 = getCollection().iterator();
            getCollection().addAll(Arrays.asList(getOtherElements()));
            getConfirmed().addAll(Arrays.asList(getOtherElements()));
            exception = assertThrows(ConcurrentModificationException.class, () -> {
                iter1.next();
            });
            assertNull(exception.getMessage());
            verify();
        }

        if (!isRemoveSupported()) {
            return;
        }

        resetFull();
        final Iterator<E> iter = getCollection().iterator();
        getCollection().clear();
        Exception exception = assertThrows(ConcurrentModificationException.class, () -> {
            iter.next();
        });
        assertNull(exception.getMessage());

        resetFull();
        final Iterator<E> iter1 = getCollection().iterator();
        getCollection().remove(getFullElements()[0]);
        exception = assertThrows(ConcurrentModificationException.class, () -> {
            iter1.next();
        });
        assertNull(exception.getMessage());

        resetFull();
        final Iterator<E> iter2 = getCollection().iterator();
        getCollection().removeIf(e -> false);
        exception = assertThrows(ConcurrentModificationException.class, () -> {
            iter2.next();
        });
        assertNull(exception.getMessage());

        resetFull();
        final Iterator<E> iter3 = getCollection().iterator();
        final List<E> sublist = Arrays.asList(getFullElements()).subList(2, 5);
        getCollection().removeAll(sublist);
        exception = assertThrows(ConcurrentModificationException.class, () -> {
            iter3.next();
        });
        assertNull(exception.getMessage());

        resetFull();
        final Iterator<E> iter4 = getCollection().iterator();
        getCollection().retainAll(sublist);
        exception = assertThrows(ConcurrentModificationException.class, () -> {
            iter4.next();
        });
        assertNull(exception.getMessage());
    }

    @Override
    public void testSerializeDeserializeThenCompare() throws Exception {
        Object obj = makeObject();
        if (obj instanceof Serializable && isTestSerialization()) {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(obj);
            out.close();

            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            final Object dest = in.readObject();
            in.close();
            if (isEqualsCheckable()) {
                assertEquals("obj != deserialize(serialize(obj)) - EMPTY Collection", obj, dest);
            }
        }
        obj = makeFullCollection();
        if (obj instanceof Serializable && isTestSerialization()) {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(obj);
            out.close();

            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            final Object dest = in.readObject();
            in.close();
            if (isEqualsCheckable()) {
                assertEquals("obj != deserialize(serialize(obj)) - FULL Collection", obj, dest);
            }
        }
    }

    public Collection<E> getCollection() {
        return collection;
    }

    /**
     * Set the collection.
     * @param collection the Collection<E> to set
     */
    public void setCollection(final Collection<E> collection) {
        this.collection = collection;
    }

    public Collection<E> getConfirmed() {
        return confirmed;
    }

    /**
     * Set the confirmed.
     * @param confirmed the Collection<E> to set
     */
    public void setConfirmed(final Collection<E> confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * Handle the optional exceptions declared by {@link Collection#contains(Object)}
     * @param coll
     * @param element
     */
    protected static void assertNotCollectionContains(final Collection<?> coll, final Object element) {
        try {
            assertFalse(coll.contains(element));
        } catch (final ClassCastException e) {
            //apparently not
        } catch (final NullPointerException e) {
            //apparently not
        }
    }

    /**
     * Handle the optional exceptions declared by {@link Collection#containsAll(Collection)}
     * @param coll
     * @param sub
     */
    protected static void assertNotCollectionContainsAll(final Collection<?> coll, final Collection<?> sub) {
        try {
            assertFalse(coll.containsAll(sub));
        } catch (final ClassCastException cce) {
            //apparently not
        } catch (final NullPointerException e) {
            //apparently not
        }
    }

    /**
     * Handle optional exceptions of {@link Collection#remove(Object)}
     * @param coll
     * @param element
     */
    protected static void assertNotRemoveFromCollection(final Collection<?> coll, final Object element) {
        try {
            assertFalse(coll.remove(element));
        } catch (final ClassCastException cce) {
            //apparently not
        } catch (final NullPointerException e) {
            //apparently not
        }
    }

    /**
     * Handle optional exceptions of {@link Collection#removeAll(Collection)}
     * @param coll
     * @param sub
     */
    protected static void assertNotRemoveAllFromCollection(final Collection<?> coll, final Collection<?> sub) {
        try {
            assertFalse(coll.removeAll(sub));
        } catch (final ClassCastException cce) {
            //apparently not
        } catch (final NullPointerException e) {
            //apparently not
        }
    }
}
