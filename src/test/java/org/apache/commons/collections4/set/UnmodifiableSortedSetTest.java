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
package org.apache.commons.collections4.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;

/**
 * Extension of {@link AbstractSortedSetTest} for exercising the
 * {@link UnmodifiableSortedSet} implementation.
 *
 * @since 3.0
 * @version $Id$
 */
public class UnmodifiableSortedSetTest<E> extends AbstractSortedSetTest<E> {
    protected UnmodifiableSortedSet<E> set = null;
    protected ArrayList<E> array = null;

    public UnmodifiableSortedSetTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(UnmodifiableSortedSetTest.class);
    }

    //-------------------------------------------------------------------
    @Override
    public SortedSet<E> makeObject() {
        return UnmodifiableSortedSet.unmodifiableSortedSet(new TreeSet<E>());
    }

    @Override
    public UnmodifiableSortedSet<E> makeFullCollection() {
        final TreeSet<E> set = new TreeSet<>();
        set.addAll(Arrays.asList(getFullElements()));
        return (UnmodifiableSortedSet<E>) UnmodifiableSortedSet.unmodifiableSortedSet(set);
    }

    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    protected void setupSet() {
        set = makeFullCollection();
        array = new ArrayList<>();
        array.add((E) Integer.valueOf(1));
    }

    /**
     * Verify that base set and subsets are not modifiable
     */
    @SuppressWarnings("unchecked")
    public void testUnmodifiable() {
        setupSet();
        verifyUnmodifiable(set);
        verifyUnmodifiable(set.headSet((E) Integer.valueOf(1)));
        verifyUnmodifiable(set.tailSet((E) Integer.valueOf(1)));
        verifyUnmodifiable(set.subSet((E) Integer.valueOf(1), (E) Integer.valueOf(3)));
    }

    public void testDecorateFactory() {
        final SortedSet<E> set = makeFullCollection();
        assertSame(set, UnmodifiableSortedSet.unmodifiableSortedSet(set));

        try {
            UnmodifiableSortedSet.unmodifiableSortedSet(null);
            fail();
        } catch (final NullPointerException ex) {}
    }

    /**
     * Verifies that a set is not modifiable
     */
    @SuppressWarnings("unchecked")
    public void verifyUnmodifiable(final Set<E> set) {
        try {
            set.add((E) "value");
            fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            set.addAll(new TreeSet<E>());
            fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            set.clear();
            fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            set.remove("x");
            fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            set.removeAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
        try {
            set.retainAll(array);
            fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException e) {
            // expected
        }
    }

    public void testComparator() {
        setupSet();
        final Comparator<? super E> c = set.comparator();
        assertTrue("natural order, so comparator should be null", c == null);
    }

    //-----------------------------------------------------------------------

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/UnmodifiableSortedSet.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/UnmodifiableSortedSet.fullCollection.version4.obj");
//    }

}
