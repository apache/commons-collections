/*
 *  Copyright 2003-2004 The Apache Software Foundation
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
package org.apache.commons.collections.set;

import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.collection.PredicatedCollection;

/**
 * Decorates another <code>Set</code> to validate that all additions
 * match a specified predicate.
 * <p>
 * If an object cannot be added to the set, an IllegalArgumentException is thrown.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2004/02/18 01:14:27 $
 * 
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class PredicatedSet extends PredicatedCollection implements Set {

    /**
     * Factory method to create a predicated (validating) set.
     * <p>
     * If there are any elements already in the set being decorated, they
     * are validated.
     * 
     * @param set  the set to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if set or predicate is null
     * @throws IllegalArgumentException if the set contains invalid elements
     */
    public static Set decorate(Set set, Predicate predicate) {
        return new PredicatedSet(set, predicate);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the set being decorated, they
     * are validated.
     * 
     * @param set  the set to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if set or predicate is null
     * @throws IllegalArgumentException if the set contains invalid elements
     */
    protected PredicatedSet(Set set, Predicate predicate) {
        super(set, predicate);
    }

    /**
     * Gets the set being decorated.
     * 
     * @return the decorated set
     */
    protected Set getSet() {
        return (Set) getCollection();
    }

}
