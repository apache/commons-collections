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

import static org.junit.jupiter.api.Assertions.assertThrows;

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

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.set.AbstractSetTest;
import org.junit.Test;

/**
 * Abstract test class for {@link org.apache.commons.collections4.MultiSet MultiSet}
 * methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your multiset fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your multiset fails.
 * <p>
 * This abstract test class does wrap the concrete multiset implementation
 * with such a decorator, see the overridden {@link #resetEmpty()} and
 * {@link #resetFull()} methods.
 * <p>
 * In addition to the generic collection tests (prefix testCollection) inherited
 * from AbstractCollectionTest, there are test methods that test the "normal" MultiSet
 * interface (prefix testMultiSet). For MultiSet specific tests use the {@link #makeObject()} and
 * {@link #makeFullCollection()} methods instead of {@link #resetEmpty()} and resetFull().
 *
 * @since 4.1
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

    /**
     * Returns an empty {@link ArrayList}.
     */
    @Override
    public Collection<T> makeConfirmedCollection() {
        final ArrayList<T> list = new ArrayList<>();
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

    /**
     * Returns the {@link #collection} field cast to a {@link MultiSet}.
     *
     * @return the collection field as a MultiSet
     */
    @Override
    public MultiSet<T> getCollection() {
        return (MultiSet<T>) super.getCollection();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetAdd() {
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

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetEqualsSelf() {
        final MultiSet<T> multiset = makeObject();
        assertEquals(multiset, multiset);

        if (!isAddSupported()) {
            return;
        }

        multiset.add((T) "elt");
        assertEquals(multiset, multiset);
        multiset.add((T) "elt"); // again
        assertEquals(multiset, multiset);
        multiset.add((T) "elt2");
        assertEquals(multiset, multiset);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetRemove() {
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
        assertEquals("Should have count of 1", 1, multiset.getCount("A"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetRemoveAll() {
        if (!isRemoveSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A", 2);
        assertEquals("Should have count of 2", 2, multiset.getCount("A"));
        multiset.add((T) "B");
        multiset.add((T) "C");
        assertEquals("Should have count of 4", 4, multiset.size());
        final List<String> delete = new ArrayList<>();
        delete.add("A");
        delete.add("B");
        multiset.removeAll(delete);
        assertEquals("Should have count of 0", 0, multiset.getCount("A"));
        assertEquals("Should have count of 0", 0, multiset.getCount("B"));
        assertEquals("Should have count of 1", 1, multiset.getCount("C"));
        assertEquals("Should have count of 1", 1, multiset.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetContains() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();

        assertFalse("MultiSet does not have at least 1 'A'", multiset.contains("A"));
        assertFalse("MultiSet does not have at least 1 'B'", multiset.contains("B"));

        multiset.add((T) "A");  // multiset 1A
        assertTrue("MultiSet has at least 1 'A'", multiset.contains("A"));
        assertFalse("MultiSet does not have at least 1 'B'", multiset.contains("B"));

        multiset.add((T) "A");  // multiset 2A
        assertTrue("MultiSet has at least 1 'A'", multiset.contains("A"));
        assertFalse("MultiSet does not have at least 1 'B'", multiset.contains("B"));

        multiset.add((T) "B");  // multiset 2A,1B
        assertTrue("MultiSet has at least 1 'A'", multiset.contains("A"));
        assertTrue("MultiSet has at least 1 'B'", multiset.contains("B"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetContainsAll() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
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

        assertTrue("MultiSet containsAll of empty", multiset.containsAll(known));
        assertFalse("MultiSet does not containsAll of 1 'A'", multiset.containsAll(known1A));
        assertFalse("MultiSet does not containsAll of 2 'A'", multiset.containsAll(known2A));
        assertFalse("MultiSet does not containsAll of 1 'B'", multiset.containsAll(known1B));
        assertFalse("MultiSet does not containsAll of 1 'A' 1 'B'", multiset.containsAll(known1A1B));

        multiset.add((T) "A");  // multiset 1A
        assertTrue("MultiSet containsAll of empty", multiset.containsAll(known));
        assertTrue("MultiSet containsAll of 1 'A'", multiset.containsAll(known1A));
        assertTrue("MultiSet does not containsAll 'A'", multiset.containsAll(known2A));
        assertFalse("MultiSet does not containsAll of 1 'B'", multiset.containsAll(known1B));
        assertFalse("MultiSet does not containsAll of 1 'A' 1 'B'", multiset.containsAll(known1A1B));

        multiset.add((T) "A");  // multiset 2A
        assertTrue("MultiSet containsAll of empty", multiset.containsAll(known));
        assertTrue("MultiSet containsAll of 1 'A'", multiset.containsAll(known1A));
        assertTrue("MultiSet containsAll of 2 'A'", multiset.containsAll(known2A));
        assertFalse("MultiSet does not containsAll of 1 'B'", multiset.containsAll(known1B));
        assertFalse("MultiSet does not containsAll of 1 'A' 1 'B'", multiset.containsAll(known1A1B));

        multiset.add((T) "A");  // multiset 3A
        assertTrue("MultiSet containsAll of empty", multiset.containsAll(known));
        assertTrue("MultiSet containsAll of 1 'A'", multiset.containsAll(known1A));
        assertTrue("MultiSet containsAll of 2 'A'", multiset.containsAll(known2A));
        assertFalse("MultiSet does not containsAll of 1 'B'", multiset.containsAll(known1B));
        assertFalse("MultiSet does not containsAll of 1 'A' 1 'B'", multiset.containsAll(known1A1B));

        multiset.add((T) "B");  // multiset 3A1B
        assertTrue("MultiSet containsAll of empty", multiset.containsAll(known));
        assertTrue("MultiSet containsAll of 1 'A'", multiset.containsAll(known1A));
        assertTrue("MultiSet containsAll of 2 'A'", multiset.containsAll(known2A));
        assertTrue("MultiSet containsAll of 1 'B'", multiset.containsAll(known1B));
        assertTrue("MultiSet containsAll of 1 'A' 1 'B'", multiset.containsAll(known1A1B));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetSize() {
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
        assertEquals("Should have 2 total item", 2, multiset.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetRetainAll() {
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
        final List<String> retains = new ArrayList<>();
        retains.add("B");
        retains.add("C");
        multiset.retainAll(retains);
        assertEquals("Should have 3 total items", 3, multiset.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetIterator() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        multiset.add((T) "B");
        assertEquals("MultiSet should have 3 items", 3, multiset.size());
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

        assertTrue("MultiSet should still contain 'A'", multiset.contains("A"));
        assertEquals("MultiSet should have 2 items", 2, multiset.size());
        assertEquals("MultiSet should have 1 'A'", 1, multiset.getCount("A"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetIteratorFail() {
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
        assertThrows(ConcurrentModificationException.class, () -> it.next(),
                "Should throw ConcurrentModificationException");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetIteratorFailNoMore() {
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
        assertThrows(NoSuchElementException.class, () -> it.next(),
                "Should throw NoSuchElementException");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetIteratorFailDoubleRemove() {
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
        assertThrows(IllegalStateException.class, () -> it.remove(),
                "Should throw IllegalStateException");
        assertEquals(2, multiset.size());
        it.next();
        it.remove();
        assertEquals(1, multiset.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetIteratorRemoveProtectsInvariants() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        assertEquals(2, multiset.size());
        final Iterator<T> it = multiset.iterator();
        assertEquals("A", it.next());
        assertTrue(it.hasNext());
        it.remove();
        assertEquals(1, multiset.size());
        assertTrue(it.hasNext());
        assertEquals("A", it.next());
        assertFalse(it.hasNext());
        it.remove();
        assertEquals(0, multiset.size());
        assertFalse(it.hasNext());

        final Iterator<T> it2 = multiset.iterator();
        assertFalse(it2.hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetEntrySetUpdatedToZero() {
        if (!isAddSupported()) {
            return;
        }
        final MultiSet<T> multiset = makeObject();
        multiset.add((T) "A");
        multiset.add((T) "A");
        final MultiSet.Entry<T> entry = multiset.entrySet().iterator().next();
        assertEquals(2, entry.getCount());
        multiset.remove("A");
        assertEquals(1, entry.getCount());
        multiset.remove("A");
        assertEquals(0, entry.getCount());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetToArray() {
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

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetToArrayPopulate() {
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

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetEquals() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        final MultiSet<T> multiset2 = makeObject();
        assertTrue(multiset.equals(multiset2));
        multiset.add((T) "A");
        assertFalse(multiset.equals(multiset2));
        multiset2.add((T) "A");
        assertTrue(multiset.equals(multiset2));
        multiset.add((T) "A");
        multiset.add((T) "B");
        multiset.add((T) "B");
        multiset.add((T) "C");
        multiset2.add((T) "A");
        multiset2.add((T) "B");
        multiset2.add((T) "B");
        multiset2.add((T) "C");
        assertTrue(multiset.equals(multiset2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetEqualsHashMultiSet() {
        if (!isAddSupported()) {
            return;
        }

        final MultiSet<T> multiset = makeObject();
        final MultiSet<T> multiset2 = new HashMultiSet<>();
        assertTrue(multiset.equals(multiset2));
        multiset.add((T) "A");
        assertFalse(multiset.equals(multiset2));
        multiset2.add((T) "A");
        assertTrue(multiset.equals(multiset2));
        multiset.add((T) "A");
        multiset.add((T) "B");
        multiset.add((T) "B");
        multiset.add((T) "C");
        multiset2.add((T) "A");
        multiset2.add((T) "B");
        multiset2.add((T) "B");
        multiset2.add((T) "C");
        assertTrue(multiset.equals(multiset2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiSetHashCode() {
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


    /**
     * Bulk test {@link MultiSet#uniqueSet()}.  This method runs through all of
     * the tests in {@link AbstractSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the multiset and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing the multiset's unique set
     */
    public BulkTest bulkTestMultiSetUniqueSet() {
        return new TestMultiSetUniqueSet();
    }

    public class TestMultiSetUniqueSet extends AbstractSetTest<T> {
        public TestMultiSetUniqueSet() {
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
            return AbstractMultiSetTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public void resetEmpty() {
            AbstractMultiSetTest.this.resetEmpty();
            TestMultiSetUniqueSet.this.setCollection(AbstractMultiSetTest.this.getCollection().uniqueSet());
            TestMultiSetUniqueSet.this.setConfirmed(new HashSet<>(AbstractMultiSetTest.this.getConfirmed()));
        }

        @Override
        public void resetFull() {
            AbstractMultiSetTest.this.resetFull();
            TestMultiSetUniqueSet.this.setCollection(AbstractMultiSetTest.this.getCollection().uniqueSet());
            TestMultiSetUniqueSet.this.setConfirmed(new HashSet<>(AbstractMultiSetTest.this.getConfirmed()));
        }

        @Override
        public void verify() {
            super.verify();
        }
    }


    /**
     * Compare the current serialized form of the MultiSet
     * against the canonical version in SCM.
     */
    @Test
    public void testEmptyMultiSetCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final MultiSet<T> multiset = makeObject();
        if (multiset instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final MultiSet<?> multiset2 = (MultiSet<?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(multiset));
            assertTrue("MultiSet is empty", multiset2.isEmpty());
            assertEquals(multiset, multiset2);
        }
    }

    /**
     * Compare the current serialized form of the MultiSet
     * against the canonical version in SCM.
     */
    @Test
    public void testFullMultiSetCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final MultiSet<T> multiset = makeFullCollection();
        if (multiset instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final MultiSet<?> multiset2 = (MultiSet<?>) readExternalFormFromDisk(getCanonicalFullCollectionName(multiset));
            assertEquals("MultiSet is the right size", multiset.size(), multiset2.size());
            assertEquals(multiset, multiset2);
        }
    }

}
