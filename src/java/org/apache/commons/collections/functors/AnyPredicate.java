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
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.collections.Predicate;

/**
 * Predicate implementation that returns true if any of the
 * predicates return true.
 * If the array of predicates is empty, then this predicate returns false.
 * <p>
 * NOTE: In versions prior to 3.2 an array size of zero or one
 * threw an exception.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 * @author Matt Benson
 */
public final class AnyPredicate<T> implements Predicate<T>, PredicateDecorator<T>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 7429999530934647542L;

    /** The array of predicates to call */
    private final Predicate<? super T>[] iPredicates;

    /**
     * Factory to create the predicate.
     * <p>
     * If the array is size zero, the predicate always returns false.
     * If the array is size one, then that predicate is returned.
     *
     * @param predicates  the predicates to check, cloned, not null
     * @return the <code>any</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if any predicate in the array is null
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> getInstance(Predicate<? super T>[] predicates) {
        FunctorUtils.validate(predicates);
        if (predicates.length == 0) {
            return FalsePredicate.<T>falsePredicate();
        }
        if (predicates.length == 1) {
            return (Predicate<T>) predicates[0];
        }
        return new AnyPredicate<T>(FunctorUtils.copy(predicates));
    }

    /**
     * Factory to create the predicate.
     * <p>
     * If the collection is size zero, the predicate always returns false.
     * If the collection is size one, then that predicate is returned.
     *
     * @param predicates  the predicates to check, cloned, not null
     * @return the <code>all</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if any predicate in the array is null
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> getInstance(Collection<? extends Predicate<T>> predicates) {
        Predicate<? super T>[] preds = FunctorUtils.validate(predicates);
        if (preds.length == 0) {
            return FalsePredicate.<T>falsePredicate();
        }
        if (preds.length == 1) {
            return (Predicate<T>) preds[0];
        }
        return new AnyPredicate<T>(preds);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicates  the predicates to check, not cloned, not null
     */
    public AnyPredicate(Predicate<? super T>[] predicates) {
        super();
        iPredicates = predicates;
    }

    /**
     * Evaluates the predicate returning true if any predicate returns true.
     *
     * @param object  the input object
     * @return true if any decorated predicate return true
     */
    public boolean evaluate(T object) {
        for (int i = 0; i < iPredicates.length; i++) {
            if (iPredicates[i].evaluate(object)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the predicates, do not modify the array.
     *
     * @return the predicates
     * @since Commons Collections 3.1
     */
    public Predicate<? super T>[] getPredicates() {
        return iPredicates;
    }

}
