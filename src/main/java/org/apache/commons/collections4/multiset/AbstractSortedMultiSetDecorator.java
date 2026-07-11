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

import org.apache.commons.collections4.SortedMultiSet;

/**
 * Decorates another {@code SortedMultiSet} to provide additional behavior.
 * <p>
 * Methods are forwarded directly to the decorated multiset.
 * </p>
 *
 * @param <E> The type held in the multiset
 * @since 4.6.0
 */
public abstract class AbstractSortedMultiSetDecorator<E>
        extends AbstractMultiSetDecorator<E> implements SortedMultiSet<E> {

    /** Serialization version */
    private static final long serialVersionUID = 20260705L;

    /**
     * Constructor only used in deserialization, do not use otherwise.
     */
    protected AbstractSortedMultiSetDecorator() {
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param multiset  the multiset to decorate, must not be null
     * @throws NullPointerException if multiset is null
     */
    protected AbstractSortedMultiSetDecorator(final SortedMultiSet<E> multiset) {
        super(multiset);
    }

    @Override
    public Comparator<? super E> comparator() {
        return decorated().comparator();
    }

    /**
     * Gets the multiset being decorated.
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
