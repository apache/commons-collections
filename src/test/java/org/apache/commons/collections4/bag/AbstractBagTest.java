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
package org.apache.commons.collections4.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.set.AbstractSetTest;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link org.apache.commons.collections4.Bag Bag}.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your bag fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your bag fails.
 * <p>
 * <strong>Note:</strong> The Bag interface does not conform to the Collection interface
 * so the generic collection tests from AbstractCollectionTest would normally fail.
 * As a work-around since 4.0, a CollectionBag decorator can be used
 * to make any Bag implementation comply to the Collection contract.
 * <p>
 * This abstract test class does wrap the concrete bag implementation
 * with such a decorator, see the overridden {@link #resetEmpty()} and
 * {@link #resetFull()} methods.
 * <p>
 * In addition to the generic collection tests (prefix testCollection) inherited
 * from AbstractCollectionTest, there are test methods that test the "normal" Bag
 * interface (prefix testBag). For Bag specific tests use the {@link #makeObject()} and
 * {@link #makeFullCollection()} methods instead of {@link #resetEmpty()} and resetFull(),
 * otherwise the collection will be wrapped by a {@link CollectionBag} decorator.
 */
public abstract class AbstractBagTest<T> extends AbstractCollectionTest<T> {

    public class TestBagUniqueSet extends AbstractSetTest<T> {

        @Override
        public T[] getFullElements() {
            return AbstractBagTest.this.getFullElements();
        }

        @Override
        protected int getIterationBehaviour() {
            return AbstractBagTest.this.getIterationBehaviour();
        }

        @Override
        public T[] getOtherElements() {
            return AbstractBagTest.this.getOtherElements();
        }

        @Override
        public boolean isAddSupported() {
            return false;
        }

        @Override
        public boolean isNullSupported() {
            return AbstractBagTest.this.isNullSupported();
        }

        @Override
        public boolean isRemoveSupported() {
            return false;
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public Set<T> makeFullCollection() {
            return AbstractBagTest.this.makeFullCollection().uniqueSet();
        }

        @Override
        public Set<T> makeObject() {
            return AbstractBagTest.this.makeObject().uniqueSet();
        }

        @Override
        public void resetEmpty() {
            AbstractBagTest.this.resetEmpty();
            TestBagUniqueSet.this.setCollection(AbstractBagTest.this.getCollection().uniqueSet());
            TestBagUniqueSet.this.setConfirmed(new HashSet<>(AbstractBagTest.this.getConfirmed()));
        }

        @Override
        public void resetFull() {
            AbstractBagTest.this.resetFull();
            TestBagUniqueSet.this.setCollection(AbstractBagTest.this.getCollection().uniqueSet());
            TestBagUniqueSet.this.setConfirmed(new HashSet<>(AbstractBagTest.this.getConfirmed()));
        }

        @Override
        public void verify() {
            super.verify();
        }
    }

    /**
     * JUnit constructor.
     */
    public AbstractBagTest() {
    }

    /**
     * Bulk test {@link Bag#uniqueSet()}.  This method runs through all of
     * the tests in {@link AbstractSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the bag and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing the bag's unique set
     */
    public BulkTest bulkTestBagUniqueSet() {
        return new TestBagUniqueSet();
    }

    /**
     * Returns the {@link #collection} field cast to a {@link Bag}.
     *
     * @return the collection field as a Bag
     */
    @Override
    public Bag<T> getCollection() {
        return (Bag<T>) super.getCollection();
    }

    /**
     * Returns an empty {@link ArrayList}.
     */
    @Override
    public Collection<T> makeConfirmedCollection() {
        return new ArrayList<>();
    }

    /**
     * Returns a full collection.
     */
    @Override
    public Collection<T> makeConfirmedFullCollection() {
        final Collection<T> coll = makeConfirmedCollection();
        coll.addAll(Arrays.asList(getFullElements()));
        return coll;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bag<T> makeFullCollection() {
        final Bag<T> bag = makeObject();
        bag.addAll(Arrays.asList(getFullElements()));
        return bag;
    }

    /**
     * Return a new, empty bag to used for testing.
     *
     * @return the bag to be tested
     */
    @Override
    public abstract Bag<T> makeObject();

    @Override
    public void resetEmpty() {
        setCollection(CollectionBag.collectionBag(makeObject()));
        setConfirmed(makeConfirmedCollection());
    }

    @Override
    public void resetFull() {
        setCollection(CollectionBag.collectionBag(makeFullCollection()));
        setConfirmed(makeConfirmedFullCollection());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagAdd() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        assertTrue(bag.contains("A"), "Should contain 'A'");
        assertEquals(1, bag.getCount("A"), "Should have count of 1");
        bag.add((T) "A");
        assertTrue(bag.contains("A"), "Should contain 'A'");
        assertEquals(2, bag.getCount("A"), "Should have count of 2");
        bag.add((T) "B");
        assertTrue(bag.contains("A"));
        assertTrue(bag.contains("B"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagContains() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();

        assertFalse(bag.contains("A"), "Bag does not have at least 1 'A'");
        assertFalse(bag.contains("B"), "Bag does not have at least 1 'B'");

        bag.add((T) "A");  // bag 1A
        assertTrue(bag.contains("A"), "Bag has at least 1 'A'");
        assertFalse(bag.contains("B"), "Bag does not have at least 1 'B'");

        bag.add((T) "A");  // bag 2A
        assertTrue(bag.contains("A"), "Bag has at least 1 'A'");
        assertFalse(bag.contains("B"), "Bag does not have at least 1 'B'");

        bag.add((T) "B");  // bag 2A,1B
        assertTrue(bag.contains("A"), "Bag has at least 1 'A'");
        assertTrue(bag.contains("B"), "Bag has at least 1 'B'");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagContainsAll() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        final List<String> known = new ArrayList<>();
        final List<String> known1A = new ArrayList<>();
        known1A.add("A");
        final List<String> known2A = new ArrayList<>();
        known2A.add("A");
        known2A.add("A");
        final List<String> known1B = new ArrayList<>();
        known1B.add("B");
        final List<String> known1A1B = new ArrayList<>();
        known1A1B.add("A");
        known1A1B.add("B");

        assertTrue(bag.containsAll(known), "Bag containsAll of empty");
        assertFalse(bag.containsAll(known1A), "Bag does not containsAll of 1 'A'");
        assertFalse(bag.containsAll(known2A), "Bag does not containsAll of 2 'A'");
        assertFalse(bag.containsAll(known1B), "Bag does not containsAll of 1 'B'");
        assertFalse(bag.containsAll(known1A1B), "Bag does not containsAll of 1 'A' 1 'B'");

        bag.add((T) "A");  // bag 1A
        assertTrue(bag.containsAll(known), "Bag containsAll of empty");
        assertTrue(bag.containsAll(known1A), "Bag containsAll of 1 'A'");
        assertFalse(bag.containsAll(known2A), "Bag does not containsAll of 2 'A'");
        assertFalse(bag.containsAll(known1B), "Bag does not containsAll of 1 'B'");
        assertFalse(bag.containsAll(known1A1B), "Bag does not containsAll of 1 'A' 1 'B'");

        bag.add((T) "A");  // bag 2A
        assertTrue(bag.containsAll(known), "Bag containsAll of empty");
        assertTrue(bag.containsAll(known1A), "Bag containsAll of 1 'A'");
        assertTrue(bag.containsAll(known2A), "Bag containsAll of 2 'A'");
        assertFalse(bag.containsAll(known1B), "Bag does not containsAll of 1 'B'");
        assertFalse(bag.containsAll(known1A1B), "Bag does not containsAll of 1 'A' 1 'B'");

        bag.add((T) "A");  // bag 3A
        assertTrue(bag.containsAll(known), "Bag containsAll of empty");
        assertTrue(bag.containsAll(known1A), "Bag containsAll of 1 'A'");
        assertTrue(bag.containsAll(known2A), "Bag containsAll of 2 'A'");
        assertFalse(bag.containsAll(known1B), "Bag does not containsAll of 1 'B'");
        assertFalse(bag.containsAll(known1A1B), "Bag does not containsAll of 1 'A' 1 'B'");

        bag.add((T) "B");  // bag 3A1B
        assertTrue(bag.containsAll(known), "Bag containsAll of empty");
        assertTrue(bag.containsAll(known1A), "Bag containsAll of 1 'A'");
        assertTrue(bag.containsAll(known2A), "Bag containsAll of 2 'A'");
        assertTrue(bag.containsAll(known1B), "Bag containsAll of 1 'B'");
        assertTrue(bag.containsAll(known1A1B), "Bag containsAll of 1 'A' 1 'B'");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagEquals() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        final Bag<T> bag2 = makeObject();
        assertEquals(bag, bag2);
        bag.add((T) "A");
        assertNotEquals(bag, bag2);
        bag2.add((T) "A");
        assertEquals(bag, bag2);
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        bag2.add((T) "A");
        bag2.add((T) "B");
        bag2.add((T) "B");
        bag2.add((T) "C");
        assertEquals(bag, bag2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagEqualsHashBag() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        final Bag<T> bag2 = new HashBag<>();
        assertEquals(bag, bag2);
        bag.add((T) "A");
        assertNotEquals(bag, bag2);
        bag2.add((T) "A");
        assertEquals(bag, bag2);
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        bag2.add((T) "A");
        bag2.add((T) "B");
        bag2.add((T) "B");
        bag2.add((T) "C");
        assertEquals(bag, bag2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagEqualsSelf() {
        final Bag<T> bag = makeObject();
        assertEquals(bag, bag);

        if (!isAddSupported()) {
            return;
        }

        bag.add((T) "elt");
        assertEquals(bag, bag);
        bag.add((T) "elt"); // again
        assertEquals(bag, bag);
        bag.add((T) "elt2");
        assertEquals(bag, bag);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagHashCode() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        final Bag<T> bag2 = makeObject();
        assertEquals(0, bag.hashCode());
        assertEquals(0, bag2.hashCode());
        assertEquals(bag.hashCode(), bag2.hashCode());
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        bag2.add((T) "A");
        bag2.add((T) "A");
        bag2.add((T) "B");
        bag2.add((T) "B");
        bag2.add((T) "C");
        assertEquals(bag.hashCode(), bag2.hashCode());

        int total = 0;
        total += "A".hashCode() ^ 2;
        total += "B".hashCode() ^ 2;
        total += "C".hashCode() ^ 1;
        assertEquals(total, bag.hashCode());
        assertEquals(total, bag2.hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagIterator() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        assertEquals(3, bag.size(), "Bag should have 3 items");
        final Iterator<T> i = bag.iterator();

        boolean foundA = false;
        while (i.hasNext()) {
            final String element = (String) i.next();
            // ignore the first A, remove the second via Iterator.remove()
            if (element.equals("A")) {
                if (!foundA) {
                    foundA = true;
                } else {
                    i.remove();
                }
            }
        }

        assertTrue(bag.contains("A"), "Bag should still contain 'A'");
        assertEquals(2, bag.size(), "Bag should have 2 items");
        assertEquals(1, bag.getCount("A"), "Bag should have 1 'A'");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagIteratorFail() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        final Iterator<T> it = bag.iterator();
        it.next();
        bag.remove("A");

        assertThrows(ConcurrentModificationException.class, () -> it.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagIteratorFailDoubleRemove() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        final Iterator<T> it = bag.iterator();
        it.next();
        it.next();
        assertEquals(3, bag.size());
        it.remove();
        assertEquals(2, bag.size());

        assertThrows(IllegalStateException.class, () -> it.remove());

        assertEquals(2, bag.size());
        it.next();
        it.remove();
        assertEquals(1, bag.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagIteratorFailNoMore() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        final Iterator<T> it = bag.iterator();
        it.next();
        it.next();
        it.next();

        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagIteratorRemoveProtectsInvariants() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        assertEquals(2, bag.size());
        final Iterator<T> it = bag.iterator();
        assertEquals("A", it.next());
        assertTrue(it.hasNext());
        it.remove();
        assertEquals(1, bag.size());
        assertTrue(it.hasNext());
        assertEquals("A", it.next());
        assertFalse(it.hasNext());
        it.remove();
        assertEquals(0, bag.size());
        assertFalse(it.hasNext());

        final Iterator<T> it2 = bag.iterator();
        assertFalse(it2.hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        assertEquals(1, bag.getCount("A"), "Should have count of 1");
        bag.remove("A");
        assertEquals(0, bag.getCount("A"), "Should have count of 0");
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "A");
        assertEquals(4, bag.getCount("A"), "Should have count of 4");
        bag.remove("A", 0);
        assertEquals(4, bag.getCount("A"), "Should have count of 4");
        bag.remove("A", 2);
        assertEquals(2, bag.getCount("A"), "Should have count of 2");
        bag.remove("A");
        assertEquals(0, bag.getCount("A"), "Should have count of 0");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagRemoveAll() {
        if (!isRemoveSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A", 2);
        assertEquals(2, bag.getCount("A"), "Should have count of 2");
        bag.add((T) "B");
        bag.add((T) "C");
        assertEquals(4, bag.size(), "Should have count of 4");
        final List<String> delete = new ArrayList<>();
        delete.add("A");
        delete.add("B");
        bag.removeAll(delete);
        assertEquals(1, bag.getCount("A"), "Should have count of 1");
        assertEquals(0, bag.getCount("B"), "Should have count of 0");
        assertEquals(1, bag.getCount("C"), "Should have count of 1");
        assertEquals(2, bag.size(), "Should have count of 2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagRetainAll() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        final List<String> retains = new ArrayList<>();
        retains.add("B");
        retains.add("C");
        bag.retainAll(retains);
        assertEquals(2, bag.size(), "Should have 2 total items");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagSize() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        assertEquals(0, bag.size(), "Should have 0 total items");
        bag.add((T) "A");
        assertEquals(1, bag.size(), "Should have 1 total items");
        bag.add((T) "A");
        assertEquals(2, bag.size(), "Should have 2 total items");
        bag.add((T) "A");
        assertEquals(3, bag.size(), "Should have 3 total items");
        bag.add((T) "B");
        assertEquals(4, bag.size(), "Should have 4 total items");
        bag.add((T) "B");
        assertEquals(5, bag.size(), "Should have 5 total items");
        bag.remove("A", 2);
        assertEquals(1, bag.getCount("A"), "Should have 1 'A'");
        assertEquals(3, bag.size(), "Should have 3 total items");
        bag.remove("B");
        assertEquals(1, bag.size(), "Should have 1 total item");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagToArray() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        final Object[] array = bag.toArray();
        int a = 0, b = 0, c = 0;
        for (final Object element : array) {
            a += element.equals("A") ? 1 : 0;
            b += element.equals("B") ? 1 : 0;
            c += element.equals("C") ? 1 : 0;
        }
        assertEquals(2, a);
        assertEquals(2, b);
        assertEquals(1, c);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBagToArrayPopulate() {
        if (!isAddSupported()) {
            return;
        }

        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        final String[] array = bag.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        int a = 0, b = 0, c = 0;
        for (final String element : array) {
            a += element.equals("A") ? 1 : 0;
            b += element.equals("B") ? 1 : 0;
            c += element.equals("C") ? 1 : 0;
        }
        assertEquals(2, a);
        assertEquals(2, b);
        assertEquals(1, c);
    }

    /**
     * Compare the current serialized form of the Bag
     * against the canonical version in SCM.
     */
    @Test
    public void testEmptyBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final Bag<T> bag = makeObject();
        if (bag instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final Bag<?> bag2 = (Bag<?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(bag));
            assertTrue(bag2.isEmpty(), "Bag is empty");
            assertEquals(bag, bag2);
        }
    }

    /**
     * Compare the current serialized form of the Bag
     * against the canonical version in SCM.
     */
    @Test
    public void testFullBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final Bag<T> bag = makeFullCollection();
        if (bag instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final Bag<?> bag2 = (Bag<?>) readExternalFormFromDisk(getCanonicalFullCollectionName(bag));
            assertEquals(bag.size(), bag2.size(), "Bag is the right size");
            assertEquals(bag, bag2);
        }
    }

}
