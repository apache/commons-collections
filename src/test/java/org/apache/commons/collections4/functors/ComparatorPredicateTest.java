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

import static org.apache.commons.collections4.functors.ComparatorPredicate.comparatorPredicate;

import java.util.Comparator;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.ComparatorPredicate.Criterion;
import org.junit.jupiter.api.Test;


public class ComparatorPredicateTest extends AbstractPredicateTest {
    private static class TestComparator<T extends Comparable<T>> implements Comparator<T> {
        @Override
        public int compare(final T first, final T second) {
            return first.compareTo(second);
        }
    }

    @Test
    public void compareEquals() {
        final Integer value = Integer.valueOf(10);
        final Predicate<Integer> p = comparatorPredicate(value, new TestComparator<Integer>());
        assertPredicateFalse(p, Integer.valueOf(value.intValue() - 1));
        assertPredicateTrue(p, Integer.valueOf(value.intValue()));
        assertPredicateFalse(p, Integer.valueOf(value.intValue() + 1));
    }

    @Test
    public void compareGreater() {
        final Integer value = Integer.valueOf(10);
        final Predicate<Integer> p = comparatorPredicate(value, new TestComparator<Integer>(), Criterion.GREATER);
        assertPredicateTrue(p, Integer.valueOf(value.intValue() - 1));
        assertPredicateFalse(p, Integer.valueOf(value.intValue()));
        assertPredicateFalse(p, Integer.valueOf(value.intValue() + 1));
    }

    @Test
    public void compareLess() {
        final Integer value = Integer.valueOf(10);
        final Predicate<Integer> p = comparatorPredicate(value, new TestComparator<Integer>(), Criterion.LESS);
        assertPredicateFalse(p, Integer.valueOf(value.intValue() - 1));
        assertPredicateFalse(p, Integer.valueOf(value.intValue()));
        assertPredicateTrue(p, Integer.valueOf(value.intValue() + 1));
    }

    @Test
    public void compareGreaterOrEqual() {
        final Integer value = Integer.valueOf(10);
        final Predicate<Integer> p = comparatorPredicate(value, new TestComparator<Integer>(), Criterion.GREATER_OR_EQUAL);
        assertPredicateTrue(p, Integer.valueOf(value.intValue() - 1));
        assertPredicateTrue(p, Integer.valueOf(value.intValue()));
        assertPredicateFalse(p, Integer.valueOf(value.intValue() + 1));
    }

    @Test
    public void compareLessOrEqual() {
        final Integer value = Integer.valueOf(10);
        final Predicate<Integer> p = comparatorPredicate(value, new TestComparator<Integer>(), Criterion.LESS_OR_EQUAL);
        assertPredicateFalse(p, Integer.valueOf(value.intValue() - 1));
        assertPredicateTrue(p, Integer.valueOf(value.intValue()));
        assertPredicateTrue(p, Integer.valueOf(value.intValue() + 1));
    }

    @Override
    protected Predicate<?> generatePredicate() {
        return comparatorPredicate(Integer.valueOf(10), new TestComparator<Integer>());
    }
}
