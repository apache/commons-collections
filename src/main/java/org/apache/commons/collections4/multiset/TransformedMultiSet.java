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

import java.util.Set;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.set.TransformedSet;

/**
 * Decorates another {@link MultiSet} to transform objects that are added.
 * <p>
 * The add and setCount methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 * </p>
 *
 * @param <E> The type held in the multiset
 * @since 4.6.0
 */
public class TransformedMultiSet<E> extends TransformedCollection<E> implements MultiSet<E> {

    /** Serialization version */
    private static final long serialVersionUID = 20260710L;

    /**
     * Factory method to create a transforming multiset that will transform
     * existing contents of the specified multiset.
     * <p>
     * If there are any elements already in the multiset being decorated, they
     * will be transformed by this method.
     * Contrast this with {@link #transformingMultiSet(MultiSet, Transformer)}.
     *
     * @param <E> The type of the elements in the multiset
     * @param multiset  the multiset to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @return a new transformed MultiSet
     * @throws NullPointerException if multiset or transformer is null
     */
    public static <E> TransformedMultiSet<E> transformedMultiSet(final MultiSet<E> multiset,
            final Transformer<? super E, ? extends E> transformer) {
        final TransformedMultiSet<E> decorated = new TransformedMultiSet<>(multiset, transformer);
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
     * Factory method to create a transforming multiset.
     * <p>
     * If there are any elements already in the multiset being decorated, they
     * are NOT transformed. Contrast this with {@link #transformedMultiSet(MultiSet, Transformer)}.
     *
     * @param <E> The type of the elements in the multiset
     * @param multiset  the multiset to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @return a new transformed MultiSet
     * @throws NullPointerException if multiset or transformer is null
     */
    public static <E> TransformedMultiSet<E> transformingMultiSet(final MultiSet<E> multiset,
            final Transformer<? super E, ? extends E> transformer) {
        return new TransformedMultiSet<>(multiset, transformer);
    }

    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the multiset being decorated, they
     * are NOT transformed.
     *
     * @param multiset  the multiset to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @throws NullPointerException if multiset or transformer is null
     */
    protected TransformedMultiSet(final MultiSet<E> multiset, final Transformer<? super E, ? extends E> transformer) {
        super(multiset, transformer);
    }

    @Override
    public int add(final E object, final int occurrences) {
        return getMultiSet().add(transform(object), occurrences);
    }

    @Override
    public Set<MultiSet.Entry<E>> entrySet() {
        return getMultiSet().entrySet();
    }

    @Override
    public boolean equals(final Object object) {
        return object == this || decorated().equals(object);
    }

    @Override
    public int getCount(final Object object) {
        return getMultiSet().getCount(object);
    }

    /**
     * Gets the decorated multiset.
     *
     * @return the decorated multiset
     */
    protected MultiSet<E> getMultiSet() {
        return (MultiSet<E>) decorated();
    }

    @Override
    public int hashCode() {
        return decorated().hashCode();
    }

    @Override
    public int remove(final Object object, final int occurrences) {
        return getMultiSet().remove(object, occurrences);
    }

    @Override
    public int setCount(final E object, final int count) {
        return getMultiSet().setCount(transform(object), count);
    }

    @Override
    public Set<E> uniqueSet() {
        final Set<E> set = getMultiSet().uniqueSet();
        return TransformedSet.<E>transformingSet(set, transformer);
    }

}
