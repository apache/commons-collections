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
package org.apache.commons.collections4.functors;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Base class for tests of composite predicates.
 *
 * @since 3.0
 */
public abstract class AbstractCompositePredicateTest<T> extends AbstractMockPredicateTest<T> {

    /**
     * Creates a new {@code TestCompositePredicate}.
     *
     * @param testValue the value which the mock predicates should expect to see (may be null).
     */
    protected AbstractCompositePredicateTest(final T testValue) {
        super(testValue);
    }

    /**
     * Creates an instance of the predicate to test.
     *
     * @param predicates the arguments to {@code getInstance}.
     *
     * @return a predicate to test.
     */
    protected abstract Predicate<T> getPredicateInstance(Predicate<? super T>... predicates);

    /**
     * Creates an instance of the predicate to test.
     *
     * @param predicates the argument to {@code getInstance}.
     *
     * @return a predicate to test.
     */
    protected abstract Predicate<T> getPredicateInstance(Collection<Predicate<T>> predicates);

    /**
     * Creates an instance of the predicate to test.
     *
     * @param mockReturnValues the return values for the mock predicates, or null if that mock is not expected
     *                         to be called
     *
     * @return a predicate to test.
     */
    protected final Predicate<T> getPredicateInstance(final Boolean... mockReturnValues) {
        final List<Predicate<T>> predicates = new ArrayList<>();
        for (final Boolean returnValue : mockReturnValues) {
            predicates.add(createMockPredicate(returnValue));
        }
        return getPredicateInstance(predicates);
    }

    /**
     * Tests whether {@code getInstance} with a one element array returns the first element in the array.
     */
    @SuppressWarnings("unchecked")
    public void singleElementArrayToGetInstance() {
        final Predicate<T> predicate = createMockPredicate(null);
        final Predicate<T> allPredicate = getPredicateInstance(predicate);
        Assertions.assertSame(predicate, allPredicate, "expected argument to be returned by getInstance()");
    }

    /**
     * Tests that passing a singleton collection to {@code getInstance} returns the single element in the
     * collection.
     */
    public void singletonCollectionToGetInstance() {
        final Predicate<T> predicate = createMockPredicate(null);
        final Predicate<T> allPredicate = getPredicateInstance(
                Collections.<Predicate<T>>singleton(predicate));
        Assertions.assertSame(predicate, allPredicate, "expected argument to be returned by getInstance()");
    }

    /**
     * Tests {@code getInstance} with a null predicate array.
     */
    @Test
    public final void nullArrayToGetInstance() {
        assertThrows(NullPointerException.class, () -> getPredicateInstance((Predicate<T>[]) null));
    }

    /**
     * Tests {@code getInstance} with a single null element in the predicate array.
     */
    @SuppressWarnings({"unchecked"})
    @Test
    public final void nullElementInArrayToGetInstance() {
        assertThrows(NullPointerException.class, () -> getPredicateInstance(new Predicate[] { null }));
    }

    /**
     * Tests {@code getInstance} with two null elements in the predicate array.
     */
    @SuppressWarnings({"unchecked"})
    @Test
    public final void nullElementsInArrayToGetInstance() {
        assertThrows(NullPointerException.class, () -> getPredicateInstance(new Predicate[] { null, null }));
    }


    /**
     * Tests {@code getInstance} with a null predicate collection
     */
    @Test
    public final void nullCollectionToGetInstance() {
        assertThrows(NullPointerException.class, () -> getPredicateInstance((Collection<Predicate<T>>) null));
    }

    /**
     * Tests {@code getInstance} with a predicate collection that contains null elements
     */
    @Test
    public final void nullElementsInCollectionToGetInstance() {
        final Collection<Predicate<T>> coll = new ArrayList<>();
        coll.add(null);
        coll.add(null);
        assertThrows(NullPointerException.class, () -> getPredicateInstance(coll));
    }

}
