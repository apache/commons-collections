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
package org.apache.commons.collections.bag;

import java.util.Comparator;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.SortedBag;

/**
 * Decorates another <code>SortedBag</code> to validate that additions
 * match a specified predicate.
 * <p>
 * If an object cannot be added to the list, an IllegalArgumentException
 * is thrown.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2004/05/15 12:27:04 $
 * 
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class PredicatedSortedBag
        extends PredicatedBag implements SortedBag {

    /**
     * Factory method to create a predicated (validating) bag.
     * <p>
     * If there are any elements already in the bag being decorated, they
     * are validated.
     * 
     * @param bag  the bag to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @return a new predicated SortedBag
     * @throws IllegalArgumentException if bag or predicate is null
     * @throws IllegalArgumentException if the bag contains invalid elements
     */
    public static SortedBag decorate(SortedBag bag, Predicate predicate) {
        return new PredicatedSortedBag(bag, predicate);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the bag being decorated, they
     * are validated.
     * 
     * @param bag  the bag to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if bag or predicate is null
     * @throws IllegalArgumentException if the bag contains invalid elements
     */
    protected PredicatedSortedBag(SortedBag bag, Predicate predicate) {
        super(bag, predicate);
    }

    /**
     * Gets the decorated sorted bag.
     * 
     * @return the decorated bag
     */
    protected SortedBag getSortedBag() {
        return (SortedBag) getCollection();
    }
    
    //-----------------------------------------------------------------------
    public Object first() {
        return getSortedBag().first();
    }

    public Object last() {
        return getSortedBag().last();
    }

    public Comparator comparator() {
        return getSortedBag().comparator();
    }

}
