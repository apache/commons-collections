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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.function.Executable;

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
        Assert.assertSame("expected argument to be returned by getInstance()", predicate, allPredicate);
    }

    /**
     * Tests that passing a singleton collection to {@code getInstance} returns the single element in the
     * collection.
     */
    public void singletonCollectionToGetInstance() {
        final Predicate<T> predicate = createMockPredicate(null);
        final Predicate<T> allPredicate = getPredicateInstance(
                Collections.<Predicate<T>>singleton(predicate));
        Assert.assertSame("expected argument to be returned by getInstance()", predicate, allPredicate);
    }

    /**
     * Tests {@code getInstance} with a null predicate array.
     */
    @Test
    public final void nullArrayToGetInstance() {
        final Executable testMethod = () -> getPredicateInstance((Predicate<T>[]) null);
        final NullPointerException thrown = assertThrows(NullPointerException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("predicates")));
    }

    /**
     * Tests {@code getInstance} with a single null element in the predicate array.
     */
    @SuppressWarnings({"unchecked"})
    @Test
    public final void nullElementInArrayToGetInstance() {
        final Executable testMethod = () -> getPredicateInstance(new Predicate[] { null } );
        final NullPointerException thrown = assertThrows(NullPointerException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("predicates[0]")));
    }

    /**
     * Tests {@code getInstance} with two null elements in the predicate array.
     */
    @SuppressWarnings({"unchecked"})
    @Test
    public final void nullElementsInArrayToGetInstance() {
        final Executable testMethod = () -> getPredicateInstance(new Predicate[] { null, null } );
        final NullPointerException thrown = assertThrows(NullPointerException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("predicates[0]")));
    }

    /**
     * Tests {@code getInstance} with a null predicate collection
     */
    @Test
    public final void nullCollectionToGetInstance() {
        final Executable testMethod = () -> getPredicateInstance((Collection<Predicate<T>>) null);
        final NullPointerException thrown = assertThrows(NullPointerException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("predicates")));
    }

    /**
     * Tests {@code getInstance} with a predicate collection that contains null elements
     */
    @Test
    public final void nullElementsInCollectionToGetInstance() {
        final Collection<Predicate<T>> coll = new ArrayList<>();
        coll.add(null);
        coll.add(null);

        final Executable testMethod = () -> getPredicateInstance(coll);
        final NullPointerException thrown = assertThrows(NullPointerException.class, testMethod);
        assertThat(thrown.getMessage(), is(equalTo("predicates[0]")));
    }

}
