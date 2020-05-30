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

import org.apache.commons.collections4.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * Base class for tests of AnyPredicate, AllPredicate, and OnePredicate.
 *
 * @since 3.0
 */
public abstract class AbstractAnyAllOnePredicateTest<T> extends AbstractCompositePredicateTest<T> {

    /**
     * Creates a new {@code TestCompositePredicate}.
     *
     * @param testValue the value which the mock predicates should expect to see (may be null).
     */
    protected AbstractAnyAllOnePredicateTest(final T testValue) {
        super(testValue);
    }

    /**
     * Tests whether {@code getInstance} with a one element array returns the first element in the array.
     */
    @Override
    @SuppressWarnings("unchecked")
    @Test
    public final void singleElementArrayToGetInstance() {
        final Predicate<T> predicate = createMockPredicate(null);
        final Predicate<T> allPredicate = getPredicateInstance(predicate);
        assertSame(predicate, allPredicate);
    }

    /**
     * Tests that passing a singleton collection to {@code getInstance} returns the single element in the
     * collection.
     */
    @Override
    @Test
    public final void singletonCollectionToGetInstance() {
        final Predicate<T> predicate = createMockPredicate(null);
        final Predicate<T> allPredicate = getPredicateInstance(
                Collections.<Predicate<T>>singleton(predicate));
        assertSame(predicate, allPredicate);
    }

    /**
     * Tests creating composite predicate instances with single predicates and verifies that the composite returns
     * the same value as the single predicate does.
     */
    @SuppressWarnings("boxing")
    public final void singleValues() {
        assertTrue(getPredicateInstance(true).evaluate(null));
        assertFalse(getPredicateInstance(false).evaluate(null));
    }

}
