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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.collections4.Predicate;

/**
 * Predicate that compares the input object with the one stored in the predicate using a comparator.
 * In addition, the comparator result can be evaluated in accordance to a supplied criterion value.
 *
 * <p>In order to demonstrate the use of the predicate, the following variables are declared:</p>
 *
 * <pre>
 * Integer ONE = Integer.valueOf(1);
 * Integer TWO = Integer.valueOf(2);
 *
 * Comparator comparator = new Comparator() {
 *
 *     public int compare(Object first, Object second) {
 *         return ((Integer) second) - ((Integer) first);
 *     }
 *
 * };
 * </pre>
 *
 * <p>Using the declared variables, the {@code ComparatorPredicate} can be used in the
 * following way:</p>
 *
 * <pre>
 * ComparatorPredicate.comparatorPredicate(ONE, comparator).test(TWO);
 * </pre>
 *
 * <p>The input variable {@code TWO} in compared to the stored variable {@code ONE} using
 * the supplied {@code comparator}. This is the default usage of the predicate and will return
 * {@code true} if the underlying comparator returns {@code 0}. In addition to the default
 * usage of the predicate, it is possible to evaluate the comparator's result in several ways. The
 * following {@link Criterion} enumeration values are provided by the predicate:
 * </p>
 *
 * <ul>
 *     <li>EQUAL</li>
 *     <li>GREATER</li>
 *     <li>GREATER_OR_EQUAL</li>
 *     <li>LESS</li>
 *     <li>LESS_OR_EQUAL</li>
 * </ul>
 *
 * <p>The following examples demonstrates how these constants can be used in order to manipulate the
 * evaluation of a comparator result.</p>
 *
 * <pre>
 * ComparatorPredicate.comparatorPredicate(ONE, comparator,<strong>ComparatorPredicate.Criterion.GREATER</strong>).test(TWO);
 * </pre>
 *
 * <p>The input variable TWO is compared to the stored variable ONE using the supplied {@code comparator}
 * using the {@code GREATER} evaluation criterion constant. This instructs the predicate to
 * return {@code true} if the comparator returns a value greater than {@code 0}.</p>
 *
 * @param <T> the type of the input to the predicate.
 * @since 4.0
 */
public class ComparatorPredicate<T> extends AbstractPredicate<T> implements Serializable {

    /**
     * Enumerates the comparator criteria.
     */
    public enum Criterion {

        /**
         * Equal criterion.
         */
        EQUAL,

        /**
         * Greater criterion.
         */
        GREATER,

        /**
         * Less criterion.
         */
        LESS,

        /**
         * Greater or equal criterion.
         */
        GREATER_OR_EQUAL,

        /**
         * Less or equal Criterion.
         */
        LESS_OR_EQUAL,
    }

    private static final long serialVersionUID = -1863209236504077399L;

    /**
     * Creates the comparator predicate
     *
     * @param <T> the type that the predicate queries
     * @param object  the object to compare to
     * @param comparator  the comparator to use for comparison
     * @return the predicate
     * @throws NullPointerException if comparator is null
     */
    public static <T> Predicate<T> comparatorPredicate(final T object, final Comparator<T> comparator) {
        return comparatorPredicate(object, comparator, Criterion.EQUAL);
    }

    /**
     * Creates the comparator predicate
     *
     * @param <T> the type that the predicate queries
     * @param object  the object to compare to
     * @param comparator  the comparator to use for comparison
     * @param criterion  the criterion to use to evaluate comparison
     * @return the predicate
     * @throws NullPointerException if comparator or criterion is null
     */
    public static <T> Predicate<T> comparatorPredicate(final T object, final Comparator<T> comparator,
                                                       final Criterion criterion) {
        return new ComparatorPredicate<>(object, Objects.requireNonNull(comparator, "comparator"),
                Objects.requireNonNull(criterion, "criterion"));
    }

    /** The internal object to compare with */
    private final T object;

    /** The comparator to use for comparison */
    private final Comparator<T> comparator;

    /** The comparison evaluation criterion to use */
    private final Criterion criterion;

    /**
     * Constructor that performs no validation.
     * Use {@code comparatorPredicate} if you want that.
     *
     * @param object  the object to compare to
     * @param comparator  the comparator to use for comparison
     * @param criterion  the criterion to use to evaluate comparison
     */
    public ComparatorPredicate(final T object, final Comparator<T> comparator, final Criterion criterion) {
        this.object = object;
        this.comparator = comparator;
        this.criterion = criterion;
    }

    /**
     * Evaluates the predicate. The predicate evaluates to {@code true} in the following cases:
     *
     * <ul>
     * <li>{@code comparator.compare(object, input) == 0 &amp;&amp; criterion == EQUAL}</li>
     * <li>{@code comparator.compare(object, input) &lt; 0 &amp;&amp; criterion == LESS}</li>
     * <li>{@code comparator.compare(object, input) &gt; 0 &amp;&amp; criterion == GREATER}</li>
     * <li>{@code comparator.compare(object, input) &gt;= 0 &amp;&amp; criterion == GREATER_OR_EQUAL}</li>
     * <li>{@code comparator.compare(object, input) &lt;= 0 &amp;&amp; criterion == LESS_OR_EQUAL}</li>
     * </ul>
     *
     * @see org.apache.commons.collections4.Predicate#test(Object)
     * @see java.util.Comparator#compare(Object first, Object second)
     * @param target  the target object to compare to
     * @return {@code true} if the comparison succeeds according to the selected criterion
     * @throws IllegalStateException if the criterion is invalid (really not possible)
     */
    @Override
    public boolean test(final T target) {

        boolean result = false;
        final int comparison = comparator.compare(object, target);
        switch (criterion) {
        case EQUAL:
            result = comparison == 0;
            break;
        case GREATER:
            result = comparison > 0;
            break;
        case LESS:
            result = comparison < 0;
            break;
        case GREATER_OR_EQUAL:
            result = comparison >= 0;
            break;
        case LESS_OR_EQUAL:
            result = comparison <= 0;
            break;
        default:
            throw new IllegalStateException("The current criterion '" + criterion + "' is invalid.");
        }

        return result;
    }
}
