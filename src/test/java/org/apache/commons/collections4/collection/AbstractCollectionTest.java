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

import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
 * <b>Indicate Collection Behaviour</b>
 * <p>
 * Override these if your collection makes specific behaviour guarantees:
 * <ul>
 * <li>{@link #getIterationBehaviour()}</li>
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

    /**
     * Flag to indicate the collection makes no ordering guarantees for the iterator. If this is not used
     * then the behaviour is assumed to be ordered and the output order of the iterator is matched by
     * the toArray method.
     */
    public static final int UNORDERED = 0x1;

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
            final ArrayList<E> list = new ArrayList<>(Arrays.asList(getFullNonNullElements()));
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
            "",
            "One",
            Integer.valueOf(2),
            "Three",
            Integer.valueOf(4),
            "One",
            Double.valueOf(5),
            Float.valueOf(6),
            "Seven",
            "Eight",
            "Nine",
            Integer.valueOf(10),
            Short.valueOf((short) 11),
            Long.valueOf(12),
            "Thirteen",
            "14",
            "15",
            Byte.valueOf((byte) 16)
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
            Float.valueOf(0),
            Double.valueOf(0),
            "Zero",
            Short.valueOf((short) 0),
            Byte.valueOf((byte) 0),
            Long.valueOf(0),
            Character.valueOf('\u0000'),
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

    /**
     * Return a flag specifying the iteration behaviour of the collection.
     * This is used to change the assertions used by specific tests.
     * The default implementation returns 0 which indicates ordered iteration behaviour.
     *
     * @return the iteration behaviour
     * @see #UNORDERED
     */
    protected int getIterationBehaviour(){
        return 0;
    }

    // Tests
    /**
     *  Tests {@link Collection#add(Object)}.
     */
    @Test
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
            assertTrue("Empty collection changed after add", r);
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
            assertTrue("Collection should contain added element", getCollection().contains(element));
        }
    }

    /**
     *  Tests {@link Collection#addAll(Collection)}.
     */
    @Test
    public void testCollectionAddAll() {
        if (!isAddSupported()) {
            return;
        }

        resetEmpty();
        E[] elements = getFullElements();
        boolean r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue("Empty collection should change after addAll", r);
        for (final E element : elements) {
            assertTrue("Collection should contain added element", getCollection().contains(element));
        }

        resetFull();
        int size = getCollection().size();
        elements = getOtherElements();
        r = getCollection().addAll(Arrays.asList(elements));
        getConfirmed().addAll(Arrays.asList(elements));
        verify();
        assertTrue("Full collection should change after addAll", r);
        for (final E element : elements) {
            assertTrue("Full collection should contain added element",
                    getCollection().contains(element));
        }
        assertEquals("Size should increase after addAll", size + elements.length, getCollection().size());

        resetFull();
        size = getCollection().size();
        r = getCollection().addAll(Arrays.asList(getFullElements()));
        getConfirmed().addAll(Arrays.asList(getFullElements()));
        verify();
        if (r) {
            assertTrue("Size should increase if addAll returns true", size < getCollection().size());
        } else {
            assertEquals("Size should not change if addAll returns false", size, getCollection().size());
        }
    }

    /**
     *  If {@link #isAddSupported()} returns false, tests that add operations
     *  raise <code>UnsupportedOperationException.
     */
    @Test
    public void testUnsupportedAdd() {
        if (isAddSupported()) {
            return;
        }

        resetEmpty();
        assertThrows(UnsupportedOperationException.class, () -> getCollection().add(getFullNonNullElements()[0]),
                "Empty collection should not support add.");
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        assertThrows(UnsupportedOperationException.class, () -> getCollection().addAll(Arrays.asList(getFullElements())),
                "Empty collection should not support addAll.");
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        resetFull();
        assertThrows(UnsupportedOperationException.class, () -> getCollection().add(getFullNonNullElements()[0]),
                "Full collection should not support add.");
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        assertThrows(UnsupportedOperationException.class, () -> getCollection().addAll(Arrays.asList(getOtherElements())),
                "Full collection should not support addAll.");
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();
    }

    /**
     *  Test {@link Collection#clear()}.
     */
    @Test
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
    @Test
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
            assertTrue("Full collection should contain element[" + i + "]",
                    getCollection().contains(elements[i]));
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
    @Test
    public void testCollectionContainsAll() {
        resetEmpty();
        Collection<E> col = new HashSet<>();
        assertTrue("Every Collection should contain all elements of an " +
                "empty Collection.", getCollection().containsAll(col));
        col.addAll(Arrays.asList(getOtherElements()));
        assertFalse("Empty Collection shouldn't contain all elements of " +
                "a non-empty Collection.", getCollection().containsAll(col));
        // make sure calls to "containsAll" don't change anything
        verify();

        resetFull();
        assertFalse("Full collection shouldn't contain other elements", getCollection().containsAll(col));

        col.clear();
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue("Full collection should containAll full elements",
                getCollection().containsAll(col));
        // make sure calls to "containsAll" don't change anything
        verify();

        final int min = getFullElements().length < 4 ? 0 : 2;
        final int max = getFullElements().length == 1 ? 1 :
                getFullElements().length <= 5 ? getFullElements().length - 1 : 5;
        col = Arrays.asList(getFullElements()).subList(min, max);
        assertTrue("Full collection should containAll partial full elements",
                getCollection().containsAll(col));
        assertTrue("Full collection should containAll itself", getCollection().containsAll(getCollection()));
        // make sure calls to "containsAll" don't change anything
        verify();

        col = new ArrayList<>(Arrays.asList(getFullElements()));
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue("Full collection should containAll duplicate full elements",
                getCollection().containsAll(col));

        // make sure calls to "containsAll" don't change anything
        verify();
    }

    /**
     *  Tests {@link Collection#isEmpty()}.
     */
    @Test
    public void testCollectionIsEmpty() {
        resetEmpty();
        assertTrue("New Collection should be empty.", getCollection().isEmpty());
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
    @Test
    public void testCollectionIterator() {
        resetEmpty();
        Iterator<E> it1 = getCollection().iterator();
        assertFalse("Iterator for empty Collection shouldn't have next.", it1.hasNext());
        Iterator<E> finalIt1 = it1;
        assertThrows(NoSuchElementException.class, () -> finalIt1.next(),
                "Iterator at end of Collection should throw NoSuchElementException when next is called.");
        // make sure nothing has changed after non-modification
        verify();

        resetFull();
        it1 = getCollection().iterator();
        for (final E element : getCollection()) {
            assertTrue("Iterator for full collection should haveNext", it1.hasNext());
            it1.next();
        }
        assertFalse("Iterator should be finished", it1.hasNext());

        final ArrayList<E> list = new ArrayList<>();
        it1 = getCollection().iterator();
        for (int i = 0; i < getCollection().size(); i++) {
            final E next = it1.next();
            assertTrue("Collection should contain element returned by its iterator",
                    getCollection().contains(next));
            list.add(next);
        }
        Iterator<E> finalIt2 = it1;
        assertThrows(NoSuchElementException.class, () -> finalIt2.next(),
                "iterator.next() should raise NoSuchElementException after it finishes");
        // make sure nothing has changed after non-modification
        verify();
    }

    /**
     *  Tests removals from {@link Collection#iterator()}.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCollectionIteratorRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        assertThrows(IllegalStateException.class, () -> getCollection().iterator().remove(),
                "New iterator.remove should raise IllegalState");
        verify();

        final Iterator<E> iter0 = getCollection().iterator();
        iter0.hasNext();
        assertThrows(IllegalStateException.class, () -> iter0.remove(),
                "New iterator.remove should raise IllegalState even after hasNext");
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
        assertTrue("Collection should be empty after iterator purge", getCollection().isEmpty());

        resetFull();
        iter = getCollection().iterator();
        iter.next();
        iter.remove();
        Iterator<E> finalIter = iter;
        assertThrows(IllegalStateException.class, () -> finalIter.remove(),
                "Second iter.remove should raise IllegalState");
    }

    /**
     *  Tests {@link Collection#remove(Object)}.
     */
    @Test
    public void testCollectionRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        final E[] elements = getFullElements();
        for (final E element : elements) {
            assertFalse("Shouldn't remove nonexistent element", getCollection().remove(element));
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
            assertTrue("Collection should remove extant element: " + element,
                    getCollection().remove(element));

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
    @Test
    public void testCollectionRemoveAll() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        assertFalse("Empty collection removeAll should return false for empty input", getCollection().removeAll(Collections.EMPTY_SET));
        verify();

        assertFalse("Empty collection removeAll should return false for nonempty input", getCollection().removeAll(new ArrayList<>(getCollection())));
        verify();

        resetFull();
        assertFalse("Full collection removeAll should return false for empty input", getCollection().removeAll(Collections.EMPTY_SET));
        verify();

        assertFalse("Full collection removeAll should return false for other elements", getCollection().removeAll(Arrays.asList(getOtherElements())));
        verify();

        assertTrue("Full collection removeAll should return true for full elements",
                getCollection().removeAll(new HashSet<>(getCollection())));
        getConfirmed().removeAll(new HashSet<>(getConfirmed()));
        verify();

        resetFull();
        final int size = getCollection().size();
        final int min = getFullElements().length < 4 ? 0 : 2;
        final int max = getFullElements().length == 1 ? 1 :
                getFullElements().length <= 5 ? getFullElements().length - 1 : 5;
        final Collection<E> all = Arrays.asList(getFullElements()).subList(min, max);
        assertTrue("Full collection removeAll should work", getCollection().removeAll(all));
        getConfirmed().removeAll(all);
        verify();

        assertTrue("Collection should shrink after removeAll", getCollection().size() < size);
        for (final E element : all) {
            assertFalse("Collection shouldn't contain removed element", getCollection().contains(element));
        }
    }

    /**
     *  Tests {@link Collection#removeIf(Predicate)}.
     * @since 4.4
     */
    @Test
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

        assertTrue("Full collection removeIf should return true for a predicate that returns only true",
                getCollection().removeIf(e -> true));
        getConfirmed().removeIf(e -> true);
        verify();

        resetFull();
        final List<E> elements = Arrays.asList(getFullElements());

        final int mid = getFullElements().length / 2;
        final E target = elements.get(mid);

        final int size = getCollection().size();
        final int targetCount = Collections.frequency(elements, target);

        final Predicate<E> filter = target::equals;

        assertTrue("Full collection removeIf should work", getCollection().removeIf(filter));
        getConfirmed().removeIf(filter);
        verify();

        assertEquals("Collection should shrink after removeIf", getCollection().size(), size - targetCount);
        assertFalse("Collection shouldn't contain removed element", getCollection().contains(target));
    }

    /**
     *  Tests {@link Collection#retainAll(Collection)}.
     */
    @Test
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
        assertTrue("Collection should change from retainAll empty",
                getCollection().retainAll(Collections.EMPTY_SET));
        getConfirmed().retainAll(Collections.EMPTY_SET);
        verify();

        resetFull();
        assertTrue("Collection changed from retainAll other", getCollection().retainAll(other));
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
            assertTrue("Collection should changed by partial retainAll",
                    getCollection().retainAll(elements.subList(min, max)));
            getConfirmed().retainAll(elements.subList(min, max));
            verify();

            for (final E element : getCollection()) {
                assertTrue("Collection only contains retained element", elements.subList(min, max).contains(element));
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
    @Test
    public void testCollectionSize() {
        resetEmpty();
        assertEquals("Size of new Collection is 0.", 0, getCollection().size());

        resetFull();
        assertFalse("Size of full collection should be greater than zero", getCollection().isEmpty());
    }

    /**
     *  Tests {@link Collection#toArray()}.
     */
    @Test
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
            assertTrue("Collection should contain element in toArray",
                    getCollection().contains(array[i]));

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
            assertTrue("Collection should return all its elements in " + "toArray", element);
        }
    }

    /**
     *  Tests {@link Collection#toArray(Object[])}.
     */
    @Test
    public void testCollectionToArray2() {
        resetEmpty();
        Object[] a = { new Object(), null, null };
        Object[] array = getCollection().toArray(a);
        assertEquals("Given array shouldn't shrink", array, a);
        assertNull("Last element should be set to null", a[0]);
        verify();

        resetFull();
        assertThrows(ArrayStoreException.class, () -> getCollection().toArray(new Void[0]),
                "toArray(new Void[0]) should raise ArrayStore");
        verify();

        // Casting to Object[] allows compilation on Java 11.
        assertThrows(NullPointerException.class, () -> getCollection().toArray((Object[]) null),
                "toArray(null) should raise NPE");
        verify();

        array = getCollection().toArray(new Object[0]);
        a = getCollection().toArray();

        if ((getIterationBehaviour() & UNORDERED) != 0) {
            assertUnorderedArrayEquals(array, a, "toArray(Object[]) and toArray()");
        } else {
            assertEquals("toArrays should be equal", Arrays.asList(array), Arrays.asList(a));
        }
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

        if ((getIterationBehaviour() & UNORDERED) != 0) {
            assertUnorderedArrayEquals(array, getCollection().toArray(), "type-specific toArray(T[]) and toArray()");
        } else {
            assertEquals("type-specific toArrays should be equal",
                    Arrays.asList(array),
                    Arrays.asList(getCollection().toArray()));
        }
        verify();
    }

    /**
     * Assert the arrays contain the same elements, ignoring the order.
     *
     * <p>Note this does not test the arrays are deeply equal. Array elements are compared
     * using {@link Object#equals(Object)}.
     *
     * @param a1 First array
     * @param a2 Second array
     * @param msg Failure message prefix
     */
    private static void assertUnorderedArrayEquals(Object[] a1, Object[] a2, String msg) {
        Assertions.assertEquals(a1.length, a2.length, () -> msg + ": length");
        final int size = a1.length;
        // Track values that have been matched once (and only once)
        final boolean[] matched = new boolean[size];
        NEXT_OBJECT:
        for (final Object o : a1) {
            for (int i = 0; i < size; i++) {
                if (matched[i]) {
                    // skip values already matched
                    continue;
                }
                if (Objects.equals(o, a2[i])) {
                    // values matched
                    matched[i] = true;
                    // continue to the outer loop
                    continue NEXT_OBJECT;
                }
            }
            fail(msg + ": array 2 does not have object: " + o);
        }
    }

    /**
     *  Tests {@code toString} on a collection.
     */
    @Test
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
    @Test
    public void testUnsupportedRemove() {
        if (isRemoveSupported()) {
            return;
        }

        resetEmpty();
        assertThrows(UnsupportedOperationException.class, () -> getCollection().clear(),
                "clear should raise UnsupportedOperationException");
        verify();

        assertThrows(UnsupportedOperationException.class, () -> getCollection().remove(null),
                "remove should raise UnsupportedOperationException");
        verify();

        assertThrows(UnsupportedOperationException.class, () -> getCollection().removeIf(e -> true),
                "removeIf should raise UnsupportedOperationException");
        verify();

        assertThrows(UnsupportedOperationException.class, () -> getCollection().removeAll(null),
                "removeAll should raise UnsupportedOperationException");
        verify();

        assertThrows(UnsupportedOperationException.class, () -> getCollection().retainAll(null),
                "retainAll should raise UnsupportedOperationException");
        verify();

        resetFull();
        final Iterator<E> iterator = getCollection().iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, () -> iterator.remove(),
                "iterator.remove should raise UnsupportedOperationException");
        verify();

    }

    /**
     *  Tests that the collection's iterator is fail-fast.
     */
    @Test
    public void testCollectionIteratorFailFast() {
        if (!isFailFastSupported()) {
            return;
        }

        if (isAddSupported()) {
            resetFull();
            final Iterator<E> iter0 = getCollection().iterator();
            final E o = getOtherElements()[0];
            getCollection().add(o);
            getConfirmed().add(o);
            assertThrows(ConcurrentModificationException.class, () -> iter0.next(),
                    "next after add should raise ConcurrentModification");
            verify();

            resetFull();
            final Iterator<E> iter = getCollection().iterator();
            getCollection().addAll(Arrays.asList(getOtherElements()));
            getConfirmed().addAll(Arrays.asList(getOtherElements()));
            assertThrows(ConcurrentModificationException.class, () -> iter.next(),
                    "next after addAll should raise ConcurrentModification");
            verify();
        }

        if (!isRemoveSupported()) {
            return;
        }

        resetFull();
        try {
            final Iterator<E> iter = getCollection().iterator();
            getCollection().clear();
            iter.next();
            fail("next after clear should raise ConcurrentModification");
        } catch (final ConcurrentModificationException | NoSuchElementException e) {
            // ConcurrentModificationException: expected
            // NoSuchElementException: (also legal given spec)
        }

        resetFull();
        final Iterator<E> iter0 = getCollection().iterator();
        getCollection().remove(getFullElements()[0]);
        assertThrows(ConcurrentModificationException.class, () -> iter0.next(),
                "next after remove should raise ConcurrentModification");

        resetFull();
        final Iterator<E> iter1 = getCollection().iterator();
        getCollection().removeIf(e -> false);
        assertThrows(ConcurrentModificationException.class, () -> iter1.next(),
                "next after removeIf should raise ConcurrentModification");

        resetFull();
        final Iterator<E> iter2 = getCollection().iterator();
        final List<E> sublist = Arrays.asList(getFullElements()).subList(2, 5);
        getCollection().removeAll(sublist);
        assertThrows(ConcurrentModificationException.class, () -> iter2.next(),
                "next after removeAll should raise ConcurrentModification");

        resetFull();
        final Iterator<E> iter3 = getCollection().iterator();
        final List<E> sublist3 = Arrays.asList(getFullElements()).subList(2, 5);
        getCollection().retainAll(sublist3);
        assertThrows(ConcurrentModificationException.class, () -> iter3.next(),
                "next after retainAll should raise ConcurrentModification");
    }

    @Test
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
        } catch (final ClassCastException | NullPointerException e) {
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
        } catch (final ClassCastException | NullPointerException e) {
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
        } catch (final ClassCastException | NullPointerException e) {
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
        } catch (final ClassCastException | NullPointerException e) {
            //apparently not
        }
    }

}
