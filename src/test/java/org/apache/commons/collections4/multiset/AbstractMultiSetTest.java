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
package org.apache.commons.collections4.multiset;

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
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.set.AbstractSetTest;

/**
 * Abstract test class for {@link org.apache.commons.collections4.MultiSet MultiSet}
 * methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your bag fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your bag fails.
 * <p>
 * This abstract test class does wrap the concrete bag implementation
 * with such a decorator, see the overridden {@link #resetEmpty()} and
 * {@link #resetFull()} methods.
 * <p>
 * In addition to the generic collection tests (prefix testCollection) inherited
 * from AbstractCollectionTest, there are test methods that test the "normal" Bag
 * interface (prefix testBag). For Bag specific tests use the {@link #makeObject()} and 
 * {@link #makeFullCollection()} methods instead of {@link #resetEmpty()} and resetFull().
 *
 * @version $Id$
 */
public abstract class AbstractMultiSetTest<T> extends AbstractCollectionTest<T> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractMultiSetTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an empty {@link ArrayList}.
     */
    @Override
    public Collection<T> makeConfirmedCollection() {
        final ArrayList<T> list = new ArrayList<T>();
        return list;
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
     * Return a new, empty multiset to used for testing.
     *
     * @return the multiset to be tested
     */
    @Override
    public abstract MultiSet<T> makeObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiSet<T> makeFullCollection() {
        final MultiSet<T> multiset = makeObject();
        multiset.addAll(Arrays.asList(getFullElements()));
        return multiset;
    }

    //-----------------------------------------------------------------------

    @Override
    public void resetEmpty() {
        this.setCollection(makeObject());
        this.setConfirmed(makeConfirmedCollection());
    }

    @Override
    public void resetFull() {
        this.setCollection(makeFullCollection());
        this.setConfirmed(makeConfirmedFullCollection());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the {@link #collection} field cast to a {@link MultiSet}.
     *
     * @return the collection field as a MultiSet
     */
    @Override
    public MultiSet<T> getCollection() {
        return (MultiSet<T>) super.getCollection();
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testBagAdd() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        assertTrue("Should contain 'A'", multiset.contains("A"));
        assertEquals("Should have count of 1", 1, multiset.getCount("A"));
        multiset.add((T) "A");
        assertTrue("Should contain 'A'", multiset.contains("A"));
        assertEquals("Should have count of 2", 2, multiset.getCount("A"));
        multiset.add((T) "B");
        assertTrue(multiset.contains("A"));
        assertTrue(multiset.contains("B"));
    }

    @SuppressWarnings("unchecked")
    public void testBagEqualsSelf() {
        final MultiSet<T> multiset = makeObject();
        assertTrue(multiset.equals(multiset));
        
        if (!isAddSupported()) {
            return;
        }
        
        multiset.add((T) "elt");
        assertTrue(multiset.equals(multiset));
        multiset.add((T) "elt"); // again
        assertTrue(multiset.equals(multiset));
        multiset.add((T) "elt2");
        assertTrue(multiset.equals(multiset));
    }

    @SuppressWarnings("unchecked")
    public void testBagRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        assertEquals("Should have count of 1", 1, multiset.getCount("A"));
        multiset.remove("A");
        assertEquals("Should have count of 0", 0, multiset.getCount("A"));
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "A");
        assertEquals("Should have count of 4", 4, multiset.getCount("A"));
        multiset.remove("A", 0);
        assertEquals("Should have count of 4", 4, multiset.getCount("A"));
        multiset.remove("A", 2);
        assertEquals("Should have count of 2", 2, multiset.getCount("A"));
        multiset.remove("A");
        assertEquals("Should have count of 0", 0, multiset.getCount("A"));
    }

    @SuppressWarnings("unchecked")
    public void testBagRemoveAll() {
        if (!isRemoveSupported()) {
            return;
        }
        
        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A", 2);
        assertEquals("Should have count of 2", 2, multiset.getCount("A"));
        multiset.add((T) "B");
        multiset.add((T) "C");
        assertEquals("Should have count of 4", 4, multiset.size());
        final List<String> delete = new ArrayList<String>();
        delete.add("A");
        delete.add("B");
        multiset.removeAll(delete);
        assertEquals("Should have count of 1", 1, multiset.getCount("A"));
        assertEquals("Should have count of 0", 0, multiset.getCount("B"));
        assertEquals("Should have count of 1", 1, multiset.getCount("C"));
        assertEquals("Should have count of 2", 2, multiset.size());
    }

    @SuppressWarnings("unchecked")
    public void testBagContains() {
        if (!isAddSupported()) {
            return;
        }
        
        final MultiSet<T> multiset = makeObject();

        assertEquals("Bag does not have at least 1 'A'", false, multiset.contains("A"));
        assertEquals("Bag does not have at least 1 'B'", false, multiset.contains("B"));

        multiset.add((T) "A");  // multiset 1A
        assertEquals("Bag has at least 1 'A'", true, multiset.contains("A"));
        assertEquals("Bag does not have at least 1 'B'", false, multiset.contains("B"));

        multiset.add((T) "A");  // multiset 2A
        assertEquals("Bag has at least 1 'A'", true, multiset.contains("A"));
        assertEquals("Bag does not have at least 1 'B'", false, multiset.contains("B"));

        multiset.add((T) "B");  // multiset 2A,1B
        assertEquals("Bag has at least 1 'A'", true, multiset.contains("A"));
        assertEquals("Bag has at least 1 'B'", true, multiset.contains("B"));
    }

    @SuppressWarnings("unchecked")
    public void testBagContainsAll() {
        if (!isAddSupported()) {
            return;
        }
        
        final MultiSet<T> multiset = makeObject();
        final List<String> known = new ArrayList<String>();
        final List<String> known1A = new ArrayList<String>();
        known1A.add("A");
        final List<String> known2A = new ArrayList<String>();
        known2A.add("A");
        known2A.add("A");
        final List<String> known1B = new ArrayList<String>();
        known1B.add("B");
        final List<String> known1A1B = new ArrayList<String>();
        known1A1B.add("A");
        known1A1B.add("B");

        assertEquals("Bag containsAll of empty", true, multiset.containsAll(known));
        assertEquals("Bag does not containsAll of 1 'A'", false, multiset.containsAll(known1A));
        assertEquals("Bag does not containsAll of 2 'A'", false, multiset.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, multiset.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, multiset.containsAll(known1A1B));

        multiset.add((T) "A");  // multiset 1A
        assertEquals("Bag containsAll of empty", true, multiset.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, multiset.containsAll(known1A));
        assertEquals("Bag does not containsAll of 2 'A'", false, multiset.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, multiset.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, multiset.containsAll(known1A1B));

        multiset.add((T) "A");  // multiset 2A
        assertEquals("Bag containsAll of empty", true, multiset.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, multiset.containsAll(known1A));
        assertEquals("Bag containsAll of 2 'A'", true, multiset.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, multiset.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, multiset.containsAll(known1A1B));

        multiset.add((T) "A");  // multiset 3A
        assertEquals("Bag containsAll of empty", true, multiset.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, multiset.containsAll(known1A));
        assertEquals("Bag containsAll of 2 'A'", true, multiset.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, multiset.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, multiset.containsAll(known1A1B));

        multiset.add((T) "B");  // multiset 3A1B
        assertEquals("Bag containsAll of empty", true, multiset.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, multiset.containsAll(known1A));
        assertEquals("Bag containsAll of 2 'A'", true, multiset.containsAll(known2A));
        assertEquals("Bag containsAll of 1 'B'", true, multiset.containsAll(known1B));
        assertEquals("Bag containsAll of 1 'A' 1 'B'", true, multiset.containsAll(known1A1B));
    }

    @SuppressWarnings("unchecked")
    public void testBagSize() {
        if (!isAddSupported()) {
            return;
        }
        
        final MultiSet<T> multiset = makeObject();
        assertEquals("Should have 0 total items", 0, multiset.size());
        multiset.add((T) "A");
        assertEquals("Should have 1 total items", 1, multiset.size());
        multiset.add((T) "A");
        assertEquals("Should have 2 total items", 2, multiset.size());
        multiset.add((T) "A");
        assertEquals("Should have 3 total items", 3, multiset.size());
        multiset.add((T) "B");
        assertEquals("Should have 4 total items", 4, multiset.size());
        multiset.add((T) "B");
        assertEquals("Should have 5 total items", 5, multiset.size());
        multiset.remove("A", 2);
        assertEquals("Should have 1 'A'", 1, multiset.getCount("A"));
        assertEquals("Should have 3 total items", 3, multiset.size());
        multiset.remove("B");
        assertEquals("Should have 1 total item", 1, multiset.size());
    }

    @SuppressWarnings("unchecked")
    public void testBagRetainAll() {
        if (!isAddSupported()) {
            return;
        }
        
        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "B");
        multiset.add((T) "B");
        multiset.add((T) "C");
        final List<String> retains = new ArrayList<String>();
        retains.add("B");
        retains.add("C");
        multiset.retainAll(retains);
        assertEquals("Should have 2 total items", 2, multiset.size());
    }

    @SuppressWarnings("unchecked")
    public void testBagIterator() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "B");
        assertEquals("Bag should have 3 items", 3, multiset.size());
        final Iterator<T> i = multiset.iterator();

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

        assertTrue("Bag should still contain 'A'", multiset.contains("A"));
        assertEquals("Bag should have 2 items", 2, multiset.size());
        assertEquals("Bag should have 1 'A'", 1, multiset.getCount("A"));
    }

    @SuppressWarnings("unchecked")
    public void testBagIteratorFail() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "B");
        final Iterator<T> it = multiset.iterator();
        it.next();
        multiset.remove("A");
        try {
            it.next();
            fail("Should throw ConcurrentModificationException");
        } catch (final ConcurrentModificationException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testBagIteratorFailNoMore() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "B");
        final Iterator<T> it = multiset.iterator();
        it.next();
        it.next();
        it.next();
        try {
            it.next();
            fail("Should throw NoSuchElementException");
        } catch (final NoSuchElementException ex) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testBagIteratorFailDoubleRemove() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "B");
        final Iterator<T> it = multiset.iterator();
        it.next();
        it.next();
        assertEquals(3, multiset.size());
        it.remove();
        assertEquals(2, multiset.size());
        try {
            it.remove();
            fail("Should throw IllegalStateException");
        } catch (final IllegalStateException ex) {
            // expected
        }
        assertEquals(2, multiset.size());
        it.next();
        it.remove();
        assertEquals(1, multiset.size());
    }

    @SuppressWarnings("unchecked")
    public void testBagIteratorRemoveProtectsInvariants() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        assertEquals(2, multiset.size());
        final Iterator<T> it = multiset.iterator();
        assertEquals("A", it.next());
        assertEquals(true, it.hasNext());
        it.remove();
        assertEquals(1, multiset.size());
        assertEquals(true, it.hasNext());
        assertEquals("A", it.next());
        assertEquals(false, it.hasNext());
        it.remove();
        assertEquals(0, multiset.size());
        assertEquals(false, it.hasNext());

        final Iterator<T> it2 = multiset.iterator();
        assertEquals(false, it2.hasNext());
    }

    @SuppressWarnings("unchecked")
    public void testBagToArray() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "B");
        multiset.add((T) "B");
        multiset.add((T) "C");
        final Object[] array = multiset.toArray();
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

    @SuppressWarnings("unchecked")
    public void testBagToArrayPopulate() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "B");
        multiset.add((T) "B");
        multiset.add((T) "C");
        final String[] array = multiset.toArray(new String[0]);
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

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testBagEquals() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        final MultiSet<T> multiset2 = makeObject();
        assertEquals(true, multiset.equals(multiset2));
        multiset.add((T) "A");
        assertEquals(false, multiset.equals(multiset2));
        multiset2.add((T) "A");
        assertEquals(true, multiset.equals(multiset2));
        multiset.add((T) "A");
        multiset.add((T) "B");
        multiset.add((T) "B");
        multiset.add((T) "C");
        multiset2.add((T) "A");
        multiset2.add((T) "B");
        multiset2.add((T) "B");
        multiset2.add((T) "C");
        assertEquals(true, multiset.equals(multiset2));
    }

    @SuppressWarnings("unchecked")
    public void testBagEqualsHashBag() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        final MultiSet<T> multiset2 = new HashMultiSet<T>();
        assertEquals(true, multiset.equals(multiset2));
        multiset.add((T) "A");
        assertEquals(false, multiset.equals(multiset2));
        multiset2.add((T) "A");
        assertEquals(true, multiset.equals(multiset2));
        multiset.add((T) "A");
        multiset.add((T) "B");
        multiset.add((T) "B");
        multiset.add((T) "C");
        multiset2.add((T) "A");
        multiset2.add((T) "B");
        multiset2.add((T) "B");
        multiset2.add((T) "C");
        assertEquals(true, multiset.equals(multiset2));
    }

    @SuppressWarnings("unchecked")
    public void testBagHashCode() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        final MultiSet<T> multiset2 = makeObject();
        assertEquals(0, multiset.hashCode());
        assertEquals(0, multiset2.hashCode());
        assertEquals(multiset.hashCode(), multiset2.hashCode());
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "B");
        multiset.add((T) "B");
        multiset.add((T) "C");
        multiset2.add((T) "A");
        multiset2.add((T) "A");
        multiset2.add((T) "B");
        multiset2.add((T) "B");
        multiset2.add((T) "C");
        assertEquals(multiset.hashCode(), multiset2.hashCode());

        int total = 0;
        total += "A".hashCode() ^ 2;
        total += "B".hashCode() ^ 2;
        total += "C".hashCode() ^ 1;
        assertEquals(total, multiset.hashCode());
        assertEquals(total, multiset2.hashCode());
    }

    //-----------------------------------------------------------------------

    /**
     * Bulk test {@link Bag#uniqueSet()}.  This method runs through all of
     * the tests in {@link AbstractSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the multiset and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing the multiset's unique set
     */
    public BulkTest bulkTestBagUniqueSet() {
        return new TestBagUniqueSet();
    }

    public class TestBagUniqueSet extends AbstractSetTest<T> {
        public TestBagUniqueSet() {
            super("");
        }

        @Override
        public T[] getFullElements() {
            return AbstractMultiSetTest.this.getFullElements();
        }

        @Override
        public T[] getOtherElements() {
            return AbstractMultiSetTest.this.getOtherElements();
        }

        @Override
        public Set<T> makeObject() {
            return AbstractMultiSetTest.this.makeObject().uniqueSet();
        }

        @Override
        public Set<T> makeFullCollection() {
            return AbstractMultiSetTest.this.makeFullCollection().uniqueSet();
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMultiSetTest.this.isNullSupported();
        }

        @Override
        public boolean isAddSupported() {
            return false;
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
        public void resetEmpty() {
            AbstractMultiSetTest.this.resetEmpty();
            TestBagUniqueSet.this.setCollection(AbstractMultiSetTest.this.getCollection().uniqueSet());
            TestBagUniqueSet.this.setConfirmed(new HashSet<T>(AbstractMultiSetTest.this.getConfirmed()));
        }

        @Override
        public void resetFull() {
            AbstractMultiSetTest.this.resetFull();
            TestBagUniqueSet.this.setCollection(AbstractMultiSetTest.this.getCollection().uniqueSet());
            TestBagUniqueSet.this.setConfirmed(new HashSet<T>(AbstractMultiSetTest.this.getConfirmed()));
        }

        @Override
        public void verify() {
            super.verify();
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Compare the current serialized form of the Bag
     * against the canonical version in SVN.
     */
    public void testEmptyBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final MultiSet<T> multiset = makeObject();
        if (multiset instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final MultiSet<?> multiset2 = (MultiSet<?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(multiset));
            assertTrue("Bag is empty",multiset2.size()  == 0);
            assertEquals(multiset, multiset2);
        }
    }

    /**
     * Compare the current serialized form of the Bag
     * against the canonical version in SVN.
     */
    public void testFullBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final MultiSet<T> multiset = makeFullCollection();
        if (multiset instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final MultiSet<?> multiset2 = (MultiSet<?>) readExternalFormFromDisk(getCanonicalFullCollectionName(multiset));
            assertEquals("Bag is the right size",multiset.size(), multiset2.size());
            assertEquals(multiset, multiset2);
        }
    }
}
