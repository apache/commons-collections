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

import static org.apache.commons.collections.functors.FunctorUtils.coerce;
import static org.apache.commons.collections.functors.FunctorUtils.validate;
import static org.apache.commons.collections.functors.TruePredicate.truePredicate;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.collections.Predicate;

/**
 * Predicate implementation that returns true if all the
 * predicates return true.
 * If the array of predicates is empty, then this predicate returns true.
 * <p>
 * NOTE: In versions prior to 3.2 an array size of zero or one
 * threw an exception.
 *
 * @since 3.0
 * @version $Id$
 */
public final class AllPredicate<T> implements Predicate<T>, PredicateDecorator<T>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = -3094696765038308799L;
    
    /** The array of predicates to call */
    private final Predicate<? super T>[] iPredicates;
    
    /**
     * Factory to create the predicate.
     * <p>
     * If the array is size zero, the predicate always returns true.
     * If the array is size one, then that predicate is returned.
     *
     * @param <T> the type that the predicate queries
     * @param predicates  the predicates to check, cloned, not null
     * @return the <code>all</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if any predicate in the array is null
     */
    public static <T> Predicate<T> allPredicate(Predicate<? super T> ... predicates) {
        FunctorUtils.validate(predicates);
        if (predicates.length == 0) {
            return truePredicate();
        }
        if (predicates.length == 1) {
            return coerce(predicates[0]);
        }

        return new AllPredicate<T>(FunctorUtils.copy(predicates));
    }

    /**
     * Factory to create the predicate.
     * <p>
     * If the collection is size zero, the predicate always returns true.
     * If the collection is size one, then that predicate is returned.
     *
     * @param <T> the type that the predicate queries
     * @param predicates  the predicates to check, cloned, not null
     * @return the <code>all</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if any predicate in the array is null
     */
    public static <T> Predicate<T> allPredicate(Collection<? extends Predicate<T>> predicates) {
        final Predicate<T>[] preds = validate(predicates);
        if (preds.length == 0) {
            return truePredicate();
        }
        if (preds.length == 1) {
            return preds[0];
        }
        return new AllPredicate<T>(preds);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicates  the predicates to check, not cloned, not null
     */
    public AllPredicate(Predicate<? super T> ... predicates) {
        super();
        iPredicates = predicates;
    }

    /**
     * Evaluates the predicate returning true if all predicates return true.
     * 
     * @param object  the input object
     * @return true if all decorated predicates return true
     */
    public boolean evaluate(T object) {
        for (Predicate<? super T> iPredicate : iPredicates) {
            if (!iPredicate.evaluate(object)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the predicates, do not modify the array.
     * 
     * @return the predicates
     * @since 3.1
     */
    public Predicate<? super T>[] getPredicates() {
        return iPredicates;
    }

}
