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
package org.apache.commons.collections4.deque;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollection;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

/**
 * Decorates another {@link Deque} to transform objects that are added.
 * <p>
 * The add/offer methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 * </p>
 *
 * @param <E> the type of elements held in this deque
 * @since 4.5
 */

public class TransformedDeque<E> extends TransformedCollection<E> implements Deque<E> {

    /** Serialization version */
    private static final long serialVersionUID = 7959728067506831816L;

    /**
     * Factory method to create a transforming deque.
     * <p>
     * If there are any elements already in the deque being decorated, they
     * are NOT transformed.
     * Contrast this with {@link #transformedDeque(Deque, Transformer)}.
     *
     * @param <E> the type of the elements in the deque
     * @param deque  the deque to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @return a new transformed Deque
     * @throws NullPointerException if deque or transformer is null
     */
    public static <E> TransformedDeque<E> transformingDeque(final Deque<E> deque,
                                                            final Transformer<? super E, ? extends E> transformer) {
        return new TransformedDeque<>(deque, transformer);
    }

    /**
     * Factory method to create a transformed deque that will transform
     * existing contents of the specified deque.
     * <p>
     * If there are any elements already in the deque being decorated, they
     * will be transformed by this method.
     * Contrast this with {@link #transformingDeque(Deque, Transformer)}.
     *
     * @param <E> the type of the elements in the deque
     * @param deque  the deque to decorate, must not be null
     * @param transformer  the transformer to use for conversion, must not be null
     * @return a new transformed Deque
     * @throws NullPointerException if deque or transformer is null
     * @since 4.0
     */
    public static <E> TransformedDeque<E> transformedDeque(final Deque<E> deque,
                                                           final Transformer<? super E, ? extends E> transformer) {
        // throws NullPointerException if deque or transformer is null
        final TransformedDeque<E> decorated = new TransformedDeque<>(deque, transformer);
        if (deque.size() > 0) {
            @SuppressWarnings("unchecked") // deque is type <E>
            final E[] values = (E[]) deque.toArray(); // NOPMD - false positive for generics
            deque.clear();
            for (final E value : values) {
                decorated.decorated().add(transformer.transform(value));
            }
        }
        return decorated;
    }
    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the deque being decorated, they
     * are NOT transformed.
     *
     * @param deque        the deque to decorate, must not be null
     * @param transformer the transformer to use for conversion, must not be null
     * @throws NullPointerException if deque or transformer is null
     */
    protected TransformedDeque(final Deque<E> deque, final Transformer<? super E, ? extends E> transformer) {
        super(deque, transformer);
    }

    /**
     * Gets the decorated deque.
     *
     * @return the decorated deque
     */
    protected Deque<E> getDeque() {
        return (Deque<E>) decorated();
    }

    @Override
    public void addFirst(E e) {
        getDeque().addFirst(transform(e));
    }

    @Override
    public void addLast(E e) {
        getDeque().addLast(transform(e));
    }

    @Override
    public boolean offerFirst(E e) {
        return getDeque().offerFirst(transform(e));
    }

    @Override
    public boolean offerLast(E e) {
        return getDeque().offerLast(transform(e));
    }

    @Override
    public E removeFirst() {
        return getDeque().removeFirst();
    }

    @Override
    public E removeLast() {
        return getDeque().removeLast();
    }

    @Override
    public E pollFirst() {
        return getDeque().pollFirst();
    }

    @Override
    public E pollLast() {
        return getDeque().pollLast();
    }

    @Override
    public E getFirst() {
        return getDeque().getFirst();
    }

    @Override
    public E getLast() {
        return getDeque().getLast();
    }

    @Override
    public E peekFirst() {
        return getDeque().peekFirst();
    }

    @Override
    public E peekLast() {
        return getDeque().peekLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return getDeque().removeFirstOccurrence(transform((E) o));
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return getDeque().removeLastOccurrence(transform((E) o));
    }

    @Override
    public boolean offer(E e) {
        return getDeque().offer(transform(e));
    }

    @Override
    public E remove() {
        return getDeque().remove();
    }

    @Override
    public E poll() {
        return getDeque().poll();
    }

    @Override
    public E element() {
        return getDeque().element();
    }

    @Override
    public E peek() {
        return getDeque().peek();
    }

    @Override
    public void push(E e) {
        getDeque().push(transform(e));
    }

    @Override
    public E pop() {
        return getDeque().pop();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return getDeque().descendingIterator();
    }
}
