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
package org.apache.commons.collections4.set;

import java.util.Comparator;
import java.util.SortedSet;

import org.apache.commons.collections4.Predicate;

/**
 * Decorates another {@code SortedSet} to validate that all additions
 * match a specified predicate.
 * <p>
 * This set exists to provide validation for the decorated set.
 * It is normally created to decorate an empty set.
 * If an object cannot be added to the set, an IllegalArgumentException is thrown.
 * </p>
 * <p>
 * One usage would be to ensure that no null entries are added to the set.
 * </p>
 * <pre>
 * SortedSet set =
 *   PredicatedSortedSet.predicatedSortedSet(new TreeSet(),
 *                                           NotNullPredicate.notNullPredicate());
 * </pre>
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * </p>
 *
 * @param <E> the type of the elements in this set
 * @since 3.0
 */
public class PredicatedSortedSet<E> extends PredicatedSet<E> implements SortedSet<E> {

    /** Serialization version */
    private static final long serialVersionUID = -9110948148132275052L;

    /**
     * Factory method to create a predicated (validating) sorted set.
     * <p>
     * If there are any elements already in the set being decorated, they
     * are validated.
     *
     * @param <E> the element type
     * @param set  the set to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @return a new predicated sorted set.
     * @throws NullPointerException if set or predicate is null
     * @throws IllegalArgumentException if the set contains invalid elements
     * @since 4.0
     */
    public static <E> PredicatedSortedSet<E> predicatedSortedSet(final SortedSet<E> set,
                                                                 final Predicate<? super E> predicate) {
        return new PredicatedSortedSet<>(set, predicate);
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
     * @throws NullPointerException if set or predicate is null
     * @throws IllegalArgumentException if the set contains invalid elements
     */
    protected PredicatedSortedSet(final SortedSet<E> set, final Predicate<? super E> predicate) {
        super(set, predicate);
    }

    /**
     * Gets the sorted set being decorated.
     *
     * @return the decorated sorted set
     */
    @Override
    protected SortedSet<E> decorated() {
        return (SortedSet<E>) super.decorated();
    }

    //-----------------------------------------------------------------------
    @Override
    public Comparator<? super E> comparator() {
        return decorated().comparator();
    }

    @Override
    public E first() {
        return decorated().first();
    }

    @Override
    public E last() {
        return decorated().last();
    }

    @Override
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        final SortedSet<E> sub = decorated().subSet(fromElement, toElement);
        return new PredicatedSortedSet<>(sub, predicate);
    }

    @Override
    public SortedSet<E> headSet(final E toElement) {
        final SortedSet<E> head = decorated().headSet(toElement);
        return new PredicatedSortedSet<>(head, predicate);
    }

    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        final SortedSet<E> tail = decorated().tailSet(fromElement);
        return new PredicatedSortedSet<>(tail, predicate);
    }

}
