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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Set}.
 * <p>
 * Since {@link Set} doesn't stipulate much new behavior that isn't already
 * found in {@link Collection}, this class basically just adds tests for
 * {@link Set#equals} and {@link Set#hashCode()} along with an updated
 * {@link #verify()} that ensures elements do not appear more than once in the
 * set.
 * <p>
 * To use, subclass and override the {@link #makeObject()}
 * method.  You may have to override other protected methods if your
 * set is not modifiable, or if your set restricts what kinds of
 * elements may be added; see {@link AbstractCollectionTest} for more details.
 */
public abstract class AbstractSetTest<E> extends AbstractCollectionTest<E> {

    /**
     * Gets the {@link AbstractCollectionTest#collection} fixture, but cast as a Set.
     */
    @Override
    public Set<E> getCollection() {
        return (Set<E>) super.getCollection();
    }

    /**
     * Gets the {@link AbstractCollectionTest#confirmed} fixture, but cast as a Set.
     */
    @Override
    public Set<E> getConfirmed() {
        return (Set<E>) super.getConfirmed();
    }

    /**
     * Sets equals method is defined.
     */
    @Override
    public boolean isEqualsCheckable() {
        return true;
    }

    /**
     * Returns an empty Set for use in modification testing.
     *
     * @return a confirmed empty collection
     */
    @Override
    public Collection<E> makeConfirmedCollection() {
        return new HashSet<>();
    }

    /**
     * Returns a full Set for use in modification testing.
     *
     * @return a confirmed full collection
     */
    @Override
    public Collection<E> makeConfirmedFullCollection() {
        final Collection<E> set = makeConfirmedCollection();
        set.addAll(Arrays.asList(getFullElements()));
        return set;
    }

    /**
     * Makes a full set by first creating an empty set and then adding
     * all the elements returned by {@link #getFullElements()}.
     *
     * Override if your set does not support the add operation.
     *
     * @return a full set
     */
    @Override
    public Set<E> makeFullCollection() {
        final Set<E> set = makeObject();
        set.addAll(Arrays.asList(getFullElements()));
        return set;
    }

    /**
     * Makes an empty set.  The returned set should have no elements.
     *
     * @return an empty set
     */
    @Override
    public abstract Set<E> makeObject();

    /**
     * Tests {@link Set#equals(Object)}.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testSetEquals() {
        resetEmpty();
        assertEquals(getCollection(), getConfirmed(), "Empty sets should be equal");
        verify();

        final Collection<E> set2 = makeConfirmedCollection();
        set2.add((E) "foo");
        assertFalse(getCollection().equals(set2), "Empty set shouldn't equal nonempty set");

        resetFull();
        assertEquals(getCollection(), getConfirmed(), "Full sets should be equal");
        verify();

        set2.clear();
        set2.addAll(Arrays.asList(getOtherElements()));
        assertFalse(getCollection().equals(set2), "Sets with different contents shouldn't be equal");
    }

    /**
     * Tests {@link Set#hashCode()}.
     */
    @Test
    public void testSetHashCode() {
        resetEmpty();
        assertEquals(getCollection().hashCode(), getConfirmed().hashCode(),
                "Empty sets have equal hashCodes");

        resetFull();
        assertEquals(getCollection().hashCode(), getConfirmed().hashCode(),
                "Equal sets have equal hashCodes");
    }

    /**
     * Provides additional verifications for sets.
     */
    @Override
    public void verify() {
        super.verify();

        assertEquals(getConfirmed(), getCollection(), "Sets should be equal");
        assertEquals(getConfirmed().hashCode(), getCollection().hashCode(),
                     "Sets should have equal hashCodes");
        final Collection<E> set = makeConfirmedCollection();
        for (final E element : getCollection()) {
            assertTrue(set.add(element), "Set.iterator should only return unique elements");
        }
    }

}
