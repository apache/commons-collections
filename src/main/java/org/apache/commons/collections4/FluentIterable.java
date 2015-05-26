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
package org.apache.commons.collections4;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * A FluentIterable provides a powerful yet simple API for manipulating Iterable instances in a fluent manner.
 * <p>
 * A FluentIterable can be created either from an Iterable or from a set of elements.
 * The following types of methods are provided:
 * <ul>
 *   <li>fluent methods which return a new {@code FluentIterable} instance
 *   <li>conversion methods which copy the FluentIterable's contents into a new collection or array (e.g. toList())
 *   <li>utility methods which answer questions about the FluentIterable's contents (e.g. size(), anyMatch(Predicate))
 *   <li> 
 * </ul>
 * <p>
 * The following example outputs the first 3 even numbers in the range [1, 10] into a list:
 * <pre>
 *   FluentIterable
 *       .of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
 *       .filter(new Predicate<Integer>() {
 *                   public boolean evaluate(Integer number) {
 *                        return number % 2 == 0;
 *                   }
 *              )
 *       .transform(TransformerUtils.stringValueTransformer())
 *       .limit(3)
 *       .toList();
 * </pre>
 *
 * @param <E>  the element type
 * @since 4.1
 * @version $Id: $
 */
public class FluentIterable<E> implements Iterable<E> {

    private final Iterable<E> iterable;

    // Static factory methods
    // ----------------------------------------------------------------------

    public static <T> FluentIterable<T> of(T... elements) {
        return of(Arrays.asList(elements));
    }
    
    public static <T> FluentIterable<T> of(Iterable<T> iterable) {
        if (iterable == null) {
            throw new NullPointerException("Iterable must not be null");
        }
        if (iterable instanceof FluentIterable<?>) {
            return (FluentIterable<T>) iterable;
        } else {
            return new FluentIterable<T>(iterable);
        }
    }

    // Constructor
    // ----------------------------------------------------------------------

    private FluentIterable(final Iterable<E> iterable) {
        this.iterable = iterable;
    }

    // fluent construction methods
    // ----------------------------------------------------------------------

    public FluentIterable<E> append(final E... elements) {
        return append(Arrays.asList(elements));
    }

    public FluentIterable<E> append(final Iterable<E> other) {
        return of(IterableUtils.chainedIterable(iterable, other));
    }
    
    public FluentIterable<E> eval() {
        return of(toList());
    }

    public FluentIterable<E> filter(final Predicate<E> predicate) {
        return of(IterableUtils.filteredIterable(iterable, predicate));
    }

    public FluentIterable<E> limit(final int maxSize) {
        return of(IterableUtils.boundedIterable(iterable, maxSize));
    }

    public FluentIterable<E> loop() {
        return of(IterableUtils.loopingIterable(iterable));
    }

    public FluentIterable<E> skip(int elementsToSkip) {
        return of(IterableUtils.skippingIterable(iterable, elementsToSkip));
    }

    public <O> FluentIterable<O> transform(final Transformer<? super E, ? extends O> transformer) {
        return of(IterableUtils.transformedIterable(iterable, transformer));
    }

    public FluentIterable<E> unique() {
        return of(IterableUtils.uniqueIterable(iterable));
    }
    
    // convenience methods
    // ----------------------------------------------------------------------

    public Iterator<E> iterator() {
        return iterable.iterator();
    }

    public Enumeration<E> asEnumeration() {
        return IteratorUtils.asEnumeration(iterator());
    }

    public boolean allMatch(final Predicate<? super E> predicate) {
        return IterableUtils.matchesAll(iterable, predicate);
    }

    public boolean anyMatch(final Predicate<? super E> predicate) {
        return IterableUtils.matchesAny(iterable, predicate);
    }

    public boolean isEmpty() {
        return IterableUtils.isEmpty(iterable);
    }

    public boolean contains(final Object object) {
        return IterableUtils.contains(iterable, object);
    }

    public E get(int position) {
        return IterableUtils.get(iterable, position);
    }

    public int size() {
        return IterableUtils.size(iterable);
    }

    public void copyInto(final Collection<? super E> collection) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null");
        }

        for (final E element : iterable) {
            collection.add(element);
        }
    }

    public E[] toArray(final Class<E> arrayClass) {
        return IteratorUtils.toArray(iterator(), arrayClass);
    }

    public List<E> toList() {
        return IteratorUtils.toList(iterator());
    }

    @Override
    public String toString() {
        return IterableUtils.toString(iterable);
    }

}
