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

import java.util.Iterator;
import java.util.NavigableSet;

import org.apache.commons.collections4.Transformer;

/**
 * Decorates another <code>NavigableSet</code> to transform objects that are added.
 * <p>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 *
 * @since 4.1
 * @version $Id$
 */
public class TransformedNavigableSet<E> extends TransformedSortedSet<E> implements NavigableSet<E> {

    /** Serialization version */
    private static final long serialVersionUID = 20150528L;

    /**
     * Factory method to create a transforming navigable set.
     * <p>
     * If there are any elements already in the set being decorated, they
     * are NOT transformed.
     * Contrast this with {@link #transformedNavigableSet(NavigableSet, Transformer)}.
     *
     * @param <E> the element type
     * @param set  the set to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @return a new transformed {@link NavigableSet}
     * @throws IllegalArgumentException if set or transformer is null
     */
    public static <E> TransformedNavigableSet<E> transformingNavigableSet(final NavigableSet<E> set,
            final Transformer<? super E, ? extends E> transformer) {
        return new TransformedNavigableSet<E>(set, transformer);
    }

    /**
     * Factory method to create a transforming navigable set that will transform
     * existing contents of the specified navigable set.
     * <p>
     * If there are any elements already in the set being decorated, they
     * will be transformed by this method.
     * Contrast this with {@link #transformingNavigableSet(NavigableSet, Transformer)}.
     *
     * @param <E> the element type
     * @param set  the set to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @return a new transformed {@link NavigableSet}
     * @throws IllegalArgumentException if set or transformer is null
     */
    public static <E> TransformedNavigableSet<E> transformedNavigableSet(final NavigableSet<E> set,
            final Transformer<? super E, ? extends E> transformer) {

        final TransformedNavigableSet<E> decorated = new TransformedNavigableSet<E>(set, transformer);
        if (transformer != null && set != null && set.size() > 0) {
            @SuppressWarnings("unchecked") // set is type E
            final E[] values = (E[]) set.toArray(); // NOPMD - false positive for generics
            set.clear();
            for (final E value : values) {
                decorated.decorated().add(transformer.transform(value));
            }
        }
        return decorated;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the set being decorated, they
     * are NOT transformed.
     *
     * @param set  the set to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if set or transformer is null
     */
    protected TransformedNavigableSet(final NavigableSet<E> set,
                                      final Transformer<? super E, ? extends E> transformer) {
        super(set, transformer);
    }

    /**
     * Gets the decorated navigable set.
     *
     * @return the decorated navigable set
     */
    @Override
    protected NavigableSet<E> decorated() {
        return (NavigableSet<E>) super.decorated();
    }

    //-----------------------------------------------------------------------

    @Override
    public E lower(E e) {
        return decorated().lower(e);
    }

    @Override
    public E floor(E e) {
        return decorated().floor(e);
    }

    @Override
    public E ceiling(E e) {
        return decorated().ceiling(e);
    }

    @Override
    public E higher(E e) {
        return decorated().higher(e);
    }

    @Override
    public E pollFirst() {
        return decorated().pollFirst();
    }

    @Override
    public E pollLast() {
        return decorated().pollLast();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return transformingNavigableSet(decorated().descendingSet(), transformer);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return decorated().descendingIterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        final NavigableSet<E> sub = decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
        return transformingNavigableSet(sub, transformer);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        final NavigableSet<E> head = decorated().headSet(toElement, inclusive);
        return transformingNavigableSet(head, transformer);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        final NavigableSet<E> tail = decorated().tailSet(fromElement, inclusive);
        return transformingNavigableSet(tail, transformer);
    }

}
