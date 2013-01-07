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
package org.apache.commons.collections.bag;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections.AbstractObjectTest;
import org.apache.commons.collections.Bag;

/**
 * Abstract test class for {@link org.apache.commons.collections.Bag Bag} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your bag fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your bag fails.
 *
 * @version $Id$
 */
public abstract class AbstractBagTest<T> extends AbstractObjectTest {
//  TODO: this class should really extend from TestCollection, but the bag
//  implementations currently do not conform to the Collection interface.  Once
//  those are fixed or at least a strategy is made for resolving the issue, this
//  can be changed back to extend TestCollection instead.

    /**
     * JUnit constructor.
     * 
     * @param testName  the test class name
     */
    public AbstractBagTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Return a new, empty bag to used for testing.
     * 
     * @return the bag to be tested
     */
    @Override
    public abstract Bag<T> makeObject();

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testBagAdd() {
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        assertTrue("Should contain 'A'", bag.contains("A"));
        assertEquals("Should have count of 1", 1, bag.getCount("A"));
        bag.add((T) "A");
        assertTrue("Should contain 'A'", bag.contains("A"));
        assertEquals("Should have count of 2", 2, bag.getCount("A"));
        bag.add((T) "B");
        assertTrue(bag.contains("A"));
        assertTrue(bag.contains("B"));
    }

    @SuppressWarnings("unchecked")
    public void testBagEqualsSelf() {
        final Bag<T> bag = makeObject();
        assertTrue(bag.equals(bag));
        bag.add((T) "elt");
        assertTrue(bag.equals(bag));
        bag.add((T) "elt"); // again
        assertTrue(bag.equals(bag));
        bag.add((T) "elt2");
        assertTrue(bag.equals(bag));
    }

    @SuppressWarnings("unchecked")
    public void testRemove() {
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        assertEquals("Should have count of 1", 1, bag.getCount("A"));
        bag.remove("A");
        assertEquals("Should have count of 0", 0, bag.getCount("A"));
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "A");
        assertEquals("Should have count of 4", 4, bag.getCount("A"));
        bag.remove("A", 0);
        assertEquals("Should have count of 4", 4, bag.getCount("A"));
        bag.remove("A", 2);
        assertEquals("Should have count of 2", 2, bag.getCount("A"));
        bag.remove("A");
        assertEquals("Should have count of 0", 0, bag.getCount("A"));
    }

    @SuppressWarnings("unchecked")
    public void testRemoveAll() {
        final Bag<T> bag = makeObject();
        bag.add((T) "A", 2);
        assertEquals("Should have count of 2", 2, bag.getCount("A"));
        bag.add((T) "B");
        bag.add((T) "C");
        assertEquals("Should have count of 4", 4, bag.size());
        final List<String> delete = new ArrayList<String>();
        delete.add("A");
        delete.add("B");
        bag.removeAll(delete);
        assertEquals("Should have count of 1", 1, bag.getCount("A"));
        assertEquals("Should have count of 0", 0, bag.getCount("B"));
        assertEquals("Should have count of 1", 1, bag.getCount("C"));
        assertEquals("Should have count of 2", 2, bag.size());
    }
    
    @SuppressWarnings("unchecked")
    public void testContains() {
        final Bag<T> bag = makeObject();
        
        assertEquals("Bag does not have at least 1 'A'", false, bag.contains("A"));
        assertEquals("Bag does not have at least 1 'B'", false, bag.contains("B"));
        
        bag.add((T) "A");  // bag 1A
        assertEquals("Bag has at least 1 'A'", true, bag.contains("A"));
        assertEquals("Bag does not have at least 1 'B'", false, bag.contains("B"));
        
        bag.add((T) "A");  // bag 2A
        assertEquals("Bag has at least 1 'A'", true, bag.contains("A"));
        assertEquals("Bag does not have at least 1 'B'", false, bag.contains("B"));
        
        bag.add((T) "B");  // bag 2A,1B
        assertEquals("Bag has at least 1 'A'", true, bag.contains("A"));
        assertEquals("Bag has at least 1 'B'", true, bag.contains("B"));
    }

    @SuppressWarnings("unchecked")
    public void testContainsAll() {
        final Bag<T> bag = makeObject();
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
        
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag does not containsAll of 1 'A'", false, bag.containsAll(known1A));
        assertEquals("Bag does not containsAll of 2 'A'", false, bag.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, bag.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, bag.containsAll(known1A1B));
        
        bag.add((T) "A");  // bag 1A
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, bag.containsAll(known1A));
        assertEquals("Bag does not containsAll of 2 'A'", false, bag.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, bag.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, bag.containsAll(known1A1B));
        
        bag.add((T) "A");  // bag 2A
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, bag.containsAll(known1A));
        assertEquals("Bag containsAll of 2 'A'", true, bag.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, bag.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, bag.containsAll(known1A1B));
        
        bag.add((T) "A");  // bag 3A
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, bag.containsAll(known1A));
        assertEquals("Bag containsAll of 2 'A'", true, bag.containsAll(known2A));
        assertEquals("Bag does not containsAll of 1 'B'", false, bag.containsAll(known1B));
        assertEquals("Bag does not containsAll of 1 'A' 1 'B'", false, bag.containsAll(known1A1B));
        
        bag.add((T) "B");  // bag 3A1B
        assertEquals("Bag containsAll of empty", true, bag.containsAll(known));
        assertEquals("Bag containsAll of 1 'A'", true, bag.containsAll(known1A));
        assertEquals("Bag containsAll of 2 'A'", true, bag.containsAll(known2A));
        assertEquals("Bag containsAll of 1 'B'", true, bag.containsAll(known1B));
        assertEquals("Bag containsAll of 1 'A' 1 'B'", true, bag.containsAll(known1A1B));
    }

    @SuppressWarnings("unchecked")
    public void testSize() {
        final Bag<T> bag = makeObject();
        assertEquals("Should have 0 total items", 0, bag.size());
        bag.add((T) "A");
        assertEquals("Should have 1 total items", 1, bag.size());
        bag.add((T) "A");
        assertEquals("Should have 2 total items", 2, bag.size());
        bag.add((T) "A");
        assertEquals("Should have 3 total items", 3, bag.size());
        bag.add((T) "B");
        assertEquals("Should have 4 total items", 4, bag.size());
        bag.add((T) "B");
        assertEquals("Should have 5 total items", 5, bag.size());
        bag.remove("A", 2);
        assertEquals("Should have 1 'A'", 1, bag.getCount("A"));
        assertEquals("Should have 3 total items", 3, bag.size());
        bag.remove("B");
        assertEquals("Should have 1 total item", 1, bag.size());
    }
    
    @SuppressWarnings("unchecked")
    public void testRetainAll() {
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        final List<String> retains = new ArrayList<String>();
        retains.add("B");
        retains.add("C");
        bag.retainAll(retains);
        assertEquals("Should have 2 total items", 2, bag.size());
    }

    @SuppressWarnings("unchecked")
    public void testIterator() {
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        assertEquals("Bag should have 3 items", 3, bag.size());
        final Iterator<T> i = bag.iterator();
    
        boolean foundA = false;
        while (i.hasNext()) {
            final String element = (String) i.next();
            // ignore the first A, remove the second via Iterator.remove()
            if (element.equals("A")) {
                if (foundA == false) {
                    foundA = true;
                } else {
                    i.remove();
                }
            }
        }
    
        assertTrue("Bag should still contain 'A'", bag.contains("A"));
        assertEquals("Bag should have 2 items", 2, bag.size());
        assertEquals("Bag should have 1 'A'", 1, bag.getCount("A"));
    }

    @SuppressWarnings("unchecked")
    public void testIteratorFail() {
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        final Iterator<T> it = bag.iterator();
        it.next();
        bag.remove("A");
        try {
            it.next();
            fail("Should throw ConcurrentModificationException");
        } catch (final ConcurrentModificationException e) {
            // expected
        }
    }
    
    @SuppressWarnings("unchecked")
    public void testIteratorFailNoMore() {
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        final Iterator<T> it = bag.iterator();
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
    public void testIteratorFailDoubleRemove() {
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
        try {
            it.remove();
            fail("Should throw IllegalStateException");
        } catch (final IllegalStateException ex) {
            // expected
        }
        assertEquals(2, bag.size());
        it.next();
        it.remove();
        assertEquals(1, bag.size());
    }
    
    @SuppressWarnings("unchecked")
    public void testIteratorRemoveProtectsInvariants() {
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        assertEquals(2, bag.size());
        final Iterator<T> it = bag.iterator();
        assertEquals("A", it.next());
        assertEquals(true, it.hasNext());
        it.remove();
        assertEquals(1, bag.size());
        assertEquals(true, it.hasNext());
        assertEquals("A", it.next());
        assertEquals(false, it.hasNext());
        it.remove();
        assertEquals(0, bag.size());
        assertEquals(false, it.hasNext());
        
        final Iterator<T> it2 = bag.iterator();
        assertEquals(false, it2.hasNext());
    }
    
    @SuppressWarnings("unchecked")
    public void testToArray() {
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

    @SuppressWarnings("unchecked")
    public void testToArrayPopulate() {
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        final String[] array = bag.toArray(new String[0]);
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
    public void testEquals() {
        final Bag<T> bag = makeObject();
        final Bag<T> bag2 = makeObject();
        assertEquals(true, bag.equals(bag2));
        bag.add((T) "A");
        assertEquals(false, bag.equals(bag2));
        bag2.add((T) "A");
        assertEquals(true, bag.equals(bag2));
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        bag2.add((T) "A");
        bag2.add((T) "B");
        bag2.add((T) "B");
        bag2.add((T) "C");
        assertEquals(true, bag.equals(bag2));
    }

    @SuppressWarnings("unchecked")
    public void testEqualsHashBag() {
        final Bag<T> bag = makeObject();
        final Bag<T> bag2 = new HashBag<T>();
        assertEquals(true, bag.equals(bag2));
        bag.add((T) "A");
        assertEquals(false, bag.equals(bag2));
        bag2.add((T) "A");
        assertEquals(true, bag.equals(bag2));
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        bag2.add((T) "A");
        bag2.add((T) "B");
        bag2.add((T) "B");
        bag2.add((T) "C");
        assertEquals(true, bag.equals(bag2));
    }

    @SuppressWarnings("unchecked")
    public void testHashCode() {
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

    //-----------------------------------------------------------------------
    public void testEmptyBagSerialization() throws IOException, ClassNotFoundException {
        final Bag<T> bag = makeObject();
        if (!(bag instanceof Serializable && isTestSerialization())) {
            return;
        }
        
        final byte[] objekt = writeExternalFormToBytes((Serializable) bag);
        final Bag<?> bag2 = (Bag<?>) readExternalFormFromBytes(objekt);

        assertEquals("Bag should be empty",0, bag.size());
        assertEquals("Bag should be empty",0, bag2.size());
    }

    @SuppressWarnings("unchecked")
    public void testFullBagSerialization() throws IOException, ClassNotFoundException {
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        final int size = bag.size();
        if (!(bag instanceof Serializable && isTestSerialization())) {
            return;
        }
        
        final byte[] objekt = writeExternalFormToBytes((Serializable) bag);
        final Bag<?> bag2 = (Bag<?>) readExternalFormFromBytes(objekt);

        assertEquals("Bag should be same size", size, bag.size());
        assertEquals("Bag should be same size", size, bag2.size());
    }

    /**
     * Skip the serialized cannonical tests for now.
     *
     * @return true
     *
     * TODO: store a new serialized object on the disk.
     */
    @Override
    protected boolean skipSerializedCanonicalTests() {
        return true;
    }

    /**
     * Compare the current serialized form of the Bag
     * against the canonical version in SVN.
     */
    public void testEmptyBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final Bag<T> bag = makeObject();
        if (bag instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final Bag<?> bag2 = (Bag<?>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(bag));
            assertTrue("Bag is empty",bag2.size()  == 0);
            assertEquals(bag, bag2);
        }
    }

    /**
     * Compare the current serialized form of the Bag
     * against the canonical version in SVN.
     */
    @SuppressWarnings("unchecked")
    public void testFullBagCompatibility() throws IOException, ClassNotFoundException {
        // test to make sure the canonical form has been preserved
        final Bag<T> bag = makeObject();
        bag.add((T) "A");
        bag.add((T) "A");
        bag.add((T) "B");
        bag.add((T) "B");
        bag.add((T) "C");
        if (bag instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            final Bag<?> bag2 = (Bag<?>) readExternalFormFromDisk(getCanonicalFullCollectionName(bag));
            assertEquals("Bag is the right size",bag.size(), bag2.size());
            assertEquals(bag, bag2);
        }
    }
}
