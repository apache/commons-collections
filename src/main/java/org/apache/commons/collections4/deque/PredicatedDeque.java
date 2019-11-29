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


import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.PredicatedCollection;

import java.util.Deque;
import java.util.Iterator;

/**
 * Decorates another {@link Deque} to validate that additions
 * match a specified predicate.
 * <p>
 * This deque exists to provide validation for the decorated deque.
 * It is normally created to decorate an empty deque.
 * If an object cannot be added to the deque, an IllegalArgumentException is thrown.
 * </p>
 * <p>
 * One usage would be to ensure that no null entries are added to the deque.
 * </p>
 * <pre>Deque deque = PredicatedDeque.predicatedDeque(new UnboundedFifoDeque(), NotNullPredicate.INSTANCE);</pre>
 *
 * @param <E> the type of elements held in this deque
 * @since 4.5
 */
public class PredicatedDeque<E> extends PredicatedCollection<E> implements Deque<E> {
    /**
     * Factory method to create a predicated (validating) deque.
     * <p>
     * If there are any elements already in the deque being decorated, they
     * are validated.
     *
     * @param <E> the type of the elements in the deque
     * @param deque  the deque to decorate, must not be null
     * @param predicate  the predicate to use for validation, must not be null
     * @return a new predicated deque
     * @throws NullPointerException if deque or predicate is null
     * @throws IllegalArgumentException if the deque contains invalid elements
     */
    public static <E> PredicatedDeque<E> predicatedDeque(final Deque<E> deque,
                                                         final Predicate<? super E> predicate) {
        return new PredicatedDeque<>(deque, predicate);
    }



    /**
     * Constructor that wraps (not copies).
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are validated.
     *
     * @param deque      the collection to decorate, must not be null
     * @param predicate the predicate to use for validation, must not be null
     * @throws NullPointerException     if collection or predicate is null
     * @throws IllegalArgumentException if the collection contains invalid elements
     */
    protected PredicatedDeque(Deque<E> deque, Predicate<? super E> predicate) {
        super(deque, predicate);
    }

    /**
     * Gets the deque being decorated.
     *
     * @return the decorated deque
     */
    @Override
    protected Deque<E> decorated() {
        return (Deque<E>) super.decorated();
    }

    /**
     * Override to validate the object being added to ensure it matches
     * the predicate.
     *
     * @param e  the object being added
     * @throws IllegalArgumentException if the add is invalid
     */

    @Override
    public void addFirst(final E e) {
        validate(e);
        decorated().addFirst(e);
    }

    @Override
    public void addLast(final E e) {
        validate(e);
        decorated().addLast(e);
    }

    @Override
    public boolean offerFirst(final E e) {
        validate(e);
        return decorated().offerFirst(e);
    }

    @Override
    public boolean offerLast(final E e) {
        validate(e);
        return decorated().offerLast(e);
    }

    @Override
    public E removeFirst() {
        return decorated().removeFirst();
    }

    @Override
    public E removeLast() {
        return decorated().removeLast();
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
    public E getFirst() {
        return decorated().getFirst();
    }

    @Override
    public E getLast() {
        return decorated().getLast();
    }

    @Override
    public E peekFirst() {
        return decorated().peekFirst();
    }

    @Override
    public E peekLast() {
        return decorated().peekLast();
    }

    @Override
    public boolean removeFirstOccurrence(final Object o) {
        validate((E) o);
        return decorated().removeFirstOccurrence(o);
    }

    @Override
    public boolean removeLastOccurrence(final Object o) {
        validate((E) o);
        return decorated().removeLastOccurrence(o);
    }

    @Override
    public boolean offer(final E e) {
        validate(e);
        return decorated().offer(e);
    }

    @Override
    public E remove() {
        return decorated().remove();
    }

    @Override
    public E poll() {
        return decorated().poll();
    }

    @Override
    public E element() {
        return decorated().element();
    }

    @Override
    public E peek() {
        return decorated().peek();
    }

    @Override
    public void push(final E e) {
        validate(e);
        decorated().push(e);
    }

    @Override
    public E pop() {
        return decorated().pop();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return decorated().descendingIterator();
    }
}
