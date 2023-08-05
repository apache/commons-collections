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

import static org.apache.commons.collections4.functors.AllPredicate.allPredicate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Test;

/**
 * Tests the org.apache.commons.collections.functors.AllPredicate class.
 *
 * @since 3.0
 */
@SuppressWarnings("boxing")
public class AllPredicateTest extends AbstractAnyAllOnePredicateTest<Integer> {

    /**
     * Creates a new {@code TestAllPredicate}.
     */
    public AllPredicateTest() {
        super(42);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Predicate<Integer> getPredicateInstance(final Predicate<? super Integer>... predicates) {
        return AllPredicate.allPredicate(predicates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Predicate<Integer> getPredicateInstance(final Collection<Predicate<Integer>> predicates) {
        return AllPredicate.allPredicate(predicates);
    }

    /**
     * Verifies that providing an empty predicate array evaluates to true.
     */
    @SuppressWarnings({"unchecked"})
    @Test
    public void emptyArrayToGetInstance() {
        assertTrue(getPredicateInstance(new Predicate[] {}).evaluate(null), "empty array not true");
    }

    /**
     * Verifies that providing an empty predicate collection evaluates to true.
     */
    @Test
    public void emptyCollectionToGetInstance() {
        final Predicate<Integer> allPredicate = getPredicateInstance(
                Collections.<Predicate<Integer>>emptyList());
        assertTrue(allPredicate.evaluate(getTestValue()), "empty collection not true");
    }

    /**
     * Tests whether a single true predicate evaluates to true.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void oneTruePredicate() {
        // use the constructor directly, as getInstance() returns the original predicate when passed
        // an array of size one.
        final Predicate<Integer> predicate = createMockPredicate(true);

        assertTrue(allPredicate(predicate).evaluate(getTestValue()), "single true predicate evaluated to false");
    }

    /**
     * Tests whether a single false predicate evaluates to true.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void oneFalsePredicate() {
        // use the constructor directly, as getInstance() returns the original predicate when passed
        // an array of size one.
        final Predicate<Integer> predicate = createMockPredicate(false);
        assertFalse(allPredicate(predicate).evaluate(getTestValue()),
                "single false predicate evaluated to true");
    }

    /**
     * Tests whether multiple true predicates evaluates to true.
     */
    @Test
    public void allTrue() {
        assertTrue(getPredicateInstance(true, true).evaluate(getTestValue()),
                "multiple true predicates evaluated to false");
        assertTrue(getPredicateInstance(true, true, true).evaluate(getTestValue()),
                "multiple true predicates evaluated to false");
    }

    /**
     * Tests whether combining some true and one false evaluates to false.  Also verifies that only the first
     * false predicate is actually evaluated
     */
    @Test
    public void trueAndFalseCombined() {
        assertFalse(getPredicateInstance(false, null).evaluate(getTestValue()),
                "false predicate evaluated to true");
        assertFalse(getPredicateInstance(false, null, null).evaluate(getTestValue()),
                "false predicate evaluated to true");
        assertFalse(getPredicateInstance(true, false, null).evaluate(getTestValue()),
                "false predicate evaluated to true");
        assertFalse(getPredicateInstance(true, true, false).evaluate(getTestValue()),
                "false predicate evaluated to true");
        assertFalse(getPredicateInstance(true, true, false, null).evaluate(getTestValue()),
                "false predicate evaluated to true");
    }
}
