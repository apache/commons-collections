/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.collections.Predicate;

/**
 * Predicate implementation that returns true if any of the predicates return true.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2004/02/18 00:59:20 $
 *
 * @author Stephen Colebourne
 */
public final class AnyPredicate implements Predicate, Serializable {

    /** Serial version UID */
    static final long serialVersionUID = 7429999530934647542L;
    
    /** The array of predicates to call */
    private final Predicate[] iPredicates;
    
    /**
     * Factory to create the predicate.
     * 
     * @param predicates  the predicates to check, cloned, not null
     * @return the <code>any</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if the predicates array has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the array is null
     */
    public static Predicate getInstance(Predicate[] predicates) {
        FunctorUtils.validateMin2(predicates);
        predicates = FunctorUtils.copy(predicates);
        return new AnyPredicate(predicates);
    }

    /**
     * Factory to create the predicate.
     * 
     * @param predicates  the predicates to check, cloned, not null
     * @return the <code>all</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if any predicate in the array is null
     * @throws IllegalArgumentException if the predicates array has less than 2 elements
     */
    public static Predicate getInstance(Collection predicates) {
        Predicate[] preds = FunctorUtils.validate(predicates);
        return new AnyPredicate(preds);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param predicates  the predicates to check, not cloned, not null
     */
    public AnyPredicate(Predicate[] predicates) {
        super();
        iPredicates = predicates;
    }

    /**
     * Return the predicate result.
     */
    public boolean evaluate(Object object) {
        for (int i = 0; i < iPredicates.length; i++) {
            if (iPredicates[i].evaluate(object)) {
                return true;
            }
        }
        return false;
    }
    
}
