/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.collections4.Equator;
import org.apache.commons.collections4.Predicate;

/**
 * Predicate implementation that returns true if the input is the same object
 * as the one stored in this predicate by equals.
 *
 * @param <T> the type of the input to the predicate.
 * @since 3.0
 */
public final class EqualPredicate<T> extends AbstractPredicate<T> implements Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 5633766978029907089L;

    /**
     * Creates the predicate.
     *
     * @param <T> the type that the predicate queries
     * @param object  the object to compare to
     * @return the predicate
     */
    public static <T> Predicate<T> equalPredicate(final T object) {
        if (object == null) {
            return NullPredicate.nullPredicate();
        }
        return new EqualPredicate<>(object);
    }

    /**
     * Creates the identity predicate.
     *
     * @param <T> the type that the predicate queries
     * @param object  the object to compare to
     * @param equator  the equator to use for comparison
     * @return the predicate
     * @since 4.0
     */
    public static <T> Predicate<T> equalPredicate(final T object, final Equator<T> equator) {
        if (object == null) {
            return NullPredicate.nullPredicate();
        }
        return new EqualPredicate<>(object, equator);
    }

    /** The value to compare to */
    private final T test;

    /** The equator to use for comparison */
    private final Equator<T> equator;

    /**
     * Constructor that performs no validation.
     * Use {@code equalPredicate} if you want that.
     *
     * @param object  the object to compare to
     */
    public EqualPredicate(final T object) {
        // do not use the DefaultEquator to keep backwards compatibility
        // the DefaultEquator returns also true if the two object references are equal
        this(object, null);
    }

    /**
     * Constructor that performs no validation.
     * Use {@code equalPredicate} if you want that.
     *
     * @param test  the object to compare to
     * @param equator  the equator to use for comparison
     * @since 4.0
     */
    public EqualPredicate(final T test, final Equator<T> equator) {
        this.test = test;
        this.equator = equator;
    }

    /**
     * Gets the value.
     *
     * @return the value
     * @since 3.1
     */
    public Object getValue() {
        return test;
    }

    /**
     * Evaluates the predicate returning true if the input equals the stored value.
     *
     * @param object  the input object
     * @return true if input object equals stored value
     */
    @Override
    public boolean test(final T object) {
        if (equator != null) {
            return equator.equate(test, object);
        }
        return Objects.equals(test, object);
    }

}
