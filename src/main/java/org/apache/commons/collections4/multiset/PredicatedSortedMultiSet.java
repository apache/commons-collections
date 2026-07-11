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
package org.apache.commons.collections4.multiset;

import java.util.Comparator;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.SortedMultiSet;

/**
 * Decorates another {@link SortedMultiSet} to validate that additions
 * match a specified predicate.
 * <p>
 * This multiset exists to provide validation for the decorated multiset.
 * It is normally created to decorate an empty multiset.
 * If an object cannot be added to the multiset, an {@link IllegalArgumentException}
 * is thrown.
 * </p>
 * <p>
 * One usage would be to ensure that no null entries are added to the multiset.
 * </p>
 * <pre>
 * SortedMultiSet&lt;E&gt; set =
 *      PredicatedSortedMultiSet.predicatedSortedMultiSet(new TreeMultiSet&lt;E&gt;(),
 *                                                        NotNullPredicate.notNullPredicate());
 * </pre>
 *
 * @param <E> The type held in the multiset
 * @since 4.6.0
 */
public class PredicatedSortedMultiSet<E> extends PredicatedMultiSet<E> implements SortedMultiSet<E> {

    /** Serialization version */
    private static final long serialVersionUID = 20260705L;

    /**
     * Factory method to create a predicated (validating) multiset.
     * <p>
     * If there are any elements already in the multiset being decorated, they
     * are validated.
     *
     * @param <E> The type of the elements in the multiset
     * @param multiset  the multiset to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @return a new predicated SortedMultiSet
     * @throws NullPointerException if multiset or predicate is null
     * @throws IllegalArgumentException if the multiset contains invalid elements
     */
    public static <E> PredicatedSortedMultiSet<E> predicatedSortedMultiSet(final SortedMultiSet<E> multiset,
                                                                           final Predicate<? super E> predicate) {
        return new PredicatedSortedMultiSet<>(multiset, predicate);
    }

    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the multiset being decorated, they
     * are validated.
     * </p>
     *
     * @param multiset  the multiset to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @throws NullPointerException if multiset or predicate is null
     * @throws IllegalArgumentException if the multiset contains invalid elements
     */
    protected PredicatedSortedMultiSet(final SortedMultiSet<E> multiset, final Predicate<? super E> predicate) {
        super(multiset, predicate);
    }

    @Override
    public Comparator<? super E> comparator() {
        return decorated().comparator();
    }

    /**
     * Gets the decorated sorted multiset.
     *
     * @return The decorated multiset
     */
    @Override
    protected SortedMultiSet<E> decorated() {
        return (SortedMultiSet<E>) super.decorated();
    }

    @Override
    public E first() {
        return decorated().first();
    }

    @Override
    public E last() {
        return decorated().last();
    }

}
