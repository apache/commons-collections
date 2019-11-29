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

import org.apache.commons.collections4.collection.SynchronizedCollection;
import java.util.Deque;
import java.util.Iterator;


/**
 * Decorates another {@link Deque} to synchronize its behaviour for a multi-threaded environment.
 * <p>
 * Methods are synchronized, then forwarded to the decorated deque. Iterators must be separately synchronized around the
 * loop.
 * </p>
 *
 * @param <E> the type of the elements in the collection
 * @since 4.5
 */
public class SynchronizedDeque<E> extends SynchronizedCollection<E> implements Deque<E> {

    /** Serialization version */
    private static final long serialVersionUID = 1L;

    /**
     * Factory method to create a synchronized deque.
     *
     * @param <E>
     *            the type of the elements in the deque
     * @param deque
     *            the deque to decorate, must not be null
     * @return a new synchronized Deque
     * @throws NullPointerException
     *             if deque is null
     */
    public static <E> SynchronizedDeque<E> synchronizedDeque(final Deque<E> deque) {
        return new SynchronizedDeque<>(deque);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param deque
     *            the deque to decorate, must not be null
     * @throws NullPointerException
     *             if deque is null
     */
    protected SynchronizedDeque(final Deque<E> deque) {
        super(deque);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param deque
     *            the deque to decorate, must not be null
     * @param lock
     *            the lock to use, must not be null
     * @throws NullPointerException
     *             if deque or lock is null
     */
    protected SynchronizedDeque(final Deque<E> deque, final Object lock) {
        super(deque, lock);
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

    @Override
    public E element() {
        synchronized (lock) {
            return decorated().element();
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        synchronized (lock) {
            return decorated().equals(object);
        }
    }

    @Override
    public int hashCode() {
        synchronized (lock) {
            return decorated().hashCode();
        }
    }

    @Override
    public void addFirst(E e) {
        synchronized (lock) {
            decorated().addFirst(e);
        }
    }

    @Override
    public void addLast(E e) {
        synchronized (lock) {
            decorated().addLast(e);
        }
    }

    @Override
    public boolean offerFirst(E e) {
        synchronized (lock) {
            return decorated().offerFirst(e);
        }
    }

    @Override
    public boolean offerLast(E e) {
        synchronized (lock) {
            return decorated().offerLast(e);
        }
    }

    @Override
    public E removeFirst() {
        synchronized (lock) {
            return decorated().removeFirst();
        }
    }

    @Override
    public E removeLast() {
        synchronized (lock) {
            return decorated().removeLast();
        }
    }

    @Override
    public E pollFirst() {
        synchronized (lock) {
            return decorated().pollFirst();
        }
    }

    @Override
    public E pollLast() {
        synchronized (lock) {
            return decorated().pollLast();
        }
    }

    @Override
    public E getFirst() {
        synchronized (lock) {
            return decorated().getFirst();
        }
    }

    @Override
    public E getLast() {
        synchronized (lock) {
            return decorated().getLast();
        }
    }

    @Override
    public E peekFirst() {
        synchronized (lock) {
            return decorated().peekFirst();
        }
    }

    @Override
    public E peekLast() {
        synchronized (lock) {
            return decorated().peekLast();
        }
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        synchronized (lock) {
            return decorated().removeFirstOccurrence(o);
        }
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        synchronized (lock) {
            return decorated().removeLastOccurrence(o);
        }
    }

    @Override
    public boolean offer(final E e) {
        synchronized (lock) {
            return decorated().offer(e);
        }
    }

    @Override
    public E peek() {
        synchronized (lock) {
            return decorated().peek();
        }
    }

    @Override
    public void push(E e) {
        synchronized (lock) {
            decorated().push(e);
        }
    }

    @Override
    public E pop() {
        synchronized (lock) {
            return decorated().pop();
        }
    }

    @Override
    public Iterator<E> descendingIterator() {
        synchronized (lock) {
            return decorated().descendingIterator();
        }
    }

    @Override
    public E poll() {
        synchronized (lock) {
            return decorated().poll();
        }
    }

    @Override
    public E remove() {
        synchronized (lock) {
            return decorated().remove();
        }
    }

}
