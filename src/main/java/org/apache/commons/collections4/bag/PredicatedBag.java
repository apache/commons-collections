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
package org.apache.commons.collections4.bag;

import java.util.Set;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.PredicatedCollection;

/**
 * Decorates another {@link Bag} to validate that additions
 * match a specified predicate.
 * <p>
 * This bag exists to provide validation for the decorated bag.
 * It is normally created to decorate an empty bag.
 * If an object cannot be added to the bag, an {@link IllegalArgumentException} is thrown.
 * <p>
 * One usage would be to ensure that no null entries are added to the bag.
 * <pre>Bag bag = PredicatedBag.decorate(new HashBag(), NotNullPredicate.INSTANCE);</pre>
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since 3.0
 * @version $Id$
 */
public class PredicatedBag<E> extends PredicatedCollection<E> implements Bag<E> {

    /** Serialization version */
    private static final long serialVersionUID = -2575833140344736876L;

    /**
     * Factory method to create a predicated (validating) bag.
     * <p>
     * If there are any elements already in the bag being decorated, they
     * are validated.
     * 
     * @param <E> the type of the elements in the bag
     * @param bag  the bag to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @return a new predicated Bag
     * @throws IllegalArgumentException if bag or predicate is null
     * @throws IllegalArgumentException if the bag contains invalid elements
     */
    public static <E> PredicatedBag<E> predicatedBag(final Bag<E> bag, final Predicate<? super E> predicate) {
        return new PredicatedBag<E>(bag, predicate);
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
    protected PredicatedBag(final Bag<E> bag, final Predicate<? super E> predicate) {
        super(bag, predicate);
    }

    /**
     * Gets the decorated bag.
     * 
     * @return the decorated bag
     */
    @Override
    protected Bag<E> decorated() {
        return (Bag<E>) super.decorated();
    }
    
    //-----------------------------------------------------------------------
    
    public boolean add(final E object, final int count) {
        validate(object);
        return decorated().add(object, count);
    }

    public boolean remove(final Object object, final int count) {
        return decorated().remove(object, count);
    }

    public Set<E> uniqueSet() {
        return decorated().uniqueSet();
    }

    public int getCount(final Object object) {
        return decorated().getCount(object);
    }

}
