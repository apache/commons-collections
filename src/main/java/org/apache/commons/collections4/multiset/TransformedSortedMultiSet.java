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
import org.apache.commons.collections4.Transformer;

/**
 * Decorates another {@link SortedMultiSet} to transform objects that are added.
 * <p>
 * The add and setCount methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 * </p>
 *
 * @param <E> The type held in the multiset.
 * @since 4.6.0
 */
public class TransformedSortedMultiSet<E> extends TransformedMultiSet<E> implements SortedMultiSet<E> {

    /** Serialization version */
    private static final long serialVersionUID = 20260710L;

    /**
     * Factory method to create a transforming sorted multiset that will transform
     * existing contents of the specified sorted multiset.
     * <p>
     * If there are any elements already in the multiset being decorated, they
     * will be transformed by this method.
     * Contrast this with {@link #transformingSortedMultiSet(SortedMultiSet, Transformer)}.
     * </p>
     *
     * @param <E> The type of the elements in the multiset.
     * @param multiset  the multiset to decorate, must not be null.
     * @param transformer  the transformer to use for conversion, must not be null.
     * @return a new transformed SortedMultiSet.
     * @throws NullPointerException if multiset or transformer is null.
     */
    public static <E> TransformedSortedMultiSet<E> transformedSortedMultiSet(final SortedMultiSet<E> multiset,
            final Transformer<? super E, ? extends E> transformer) {
        final TransformedSortedMultiSet<E> decorated = new TransformedSortedMultiSet<>(multiset, transformer);
        if (!multiset.isEmpty()) {
            @SuppressWarnings("unchecked") // multiset is of type E
            final E[] values = (E[]) multiset.toArray(); // NOPMD - false positive for generics
            multiset.clear();
            for (final E value : values) {
                decorated.decorated().add(transformer.apply(value));
            }
        }
        return decorated;
    }

    /**
     * Factory method to create a transforming sorted multiset.
     * <p>
     * If there are any elements already in the multiset being decorated, they
     * are NOT transformed. Contrast this with
     * {@link #transformedSortedMultiSet(SortedMultiSet, Transformer)}.
     * </p>
     *
     * @param <E> The type of the elements in the multiset.
     * @param multiset  the multiset to decorate, must not be null.
     * @param transformer  the transformer to use for conversion, must not be null.
     * @return a new transformed SortedMultiSet.
     * @throws NullPointerException if multiset or transformer is null.
     */
    public static <E> TransformedSortedMultiSet<E> transformingSortedMultiSet(final SortedMultiSet<E> multiset,
            final Transformer<? super E, ? extends E> transformer) {
        return new TransformedSortedMultiSet<>(multiset, transformer);
    }

    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the multiset being decorated, they
     * are NOT transformed.
     * </p>
     *
     * @param multiset  the multiset to decorate, must not be null.
     * @param transformer  the transformer to use for conversion, must not be null.
     * @throws NullPointerException if multiset or transformer is null.
     */
    protected TransformedSortedMultiSet(final SortedMultiSet<E> multiset,
            final Transformer<? super E, ? extends E> transformer) {
        super(multiset, transformer);
    }

    @Override
    public Comparator<? super E> comparator() {
        return getSortedMultiSet().comparator();
    }

    @Override
    public E first() {
        return getSortedMultiSet().first();
    }

    /**
     * Gets the decorated sorted multiset.
     *
     * @return The decorated sorted multiset.
     */
    protected SortedMultiSet<E> getSortedMultiSet() {
        return (SortedMultiSet<E>) decorated();
    }

    @Override
    public E last() {
        return getSortedMultiSet().last();
    }

}
