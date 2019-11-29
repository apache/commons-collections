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

import java.io.Serializable;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import java.util.Deque;
import java.util.concurrent.locks.Condition;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Decorates another {@link Deque} to support blocking operations that wait for the deque
 * to become non-empty when retrieving an element, and wait for space to become available
 * in the deque when storing an element if the decorated deque has a capacity limit.
 * <p>
 * This deque exists to support blocking operations for the decorated deque. It is thread
 * safe.
 * </p>
 *
 * @param <E> the type of elements held in this deque
 * @since 4.5
 */
public class BlockingDeque<E> extends AbstractCollection<E> implements Deque<E>, Serializable {
    final Deque<E> deque;
    final ReentrantLock lock = new ReentrantLock();

    /** Condition for waiting takes */
    private final Condition notEmpty = lock.newCondition();

    /** Condition for waiting puts */
    private final Condition notFull = lock.newCondition();

    /**
     * Factory method to create a blocking deque.
     *
     * @param <E>
     *            the type of the elements in the deque
     * @param deque
     *            the deque to decorate, must not be null
     * @return a new blocking Deque
     * @throws NullPointerException
     *             if deque is null
     */
    public static <E> BlockingDeque<E> blockingDeque(final Deque<E> deque) {
        return new BlockingDeque<>(deque);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param deque
     *            the deque to decorate, must not be null
     * @throws NullPointerException
     *             if deque is null
     */
    public BlockingDeque(final Deque<E> deque) {
        if (deque == null) {
            throw new NullPointerException();
        }
        this.deque = deque;
    }

    /**
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     */
    @Override
    public void addFirst(final E e) {
        if (!offerFirst(e))
            throw new IllegalStateException("Deque is full");
    }

    /**
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     */
    @Override
    public void addLast(final E e) {
        if (!offerLast(e))
            throw new IllegalStateException("Deque is full");
    }

    /**
     * @return {@code true} if the element was added to this deque, else
     *         {@code false}
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this deque
     */
    @Override
    public boolean offerFirst(final E e) {
        lock.lock();
        try {
            boolean r;
            if ((r = deque.offerFirst(e)) == true)
                notEmpty.signal();
            return r;
        } catch (Exception ex) {
            throw ex;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return {@code true} if the element was added to this deque, else
     *         {@code false}
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this deque
     */
    @Override
    public boolean offerLast(final E e) {
        lock.lock();
        try {
            boolean r;
            if ((r = deque.offerLast(e)) == true)
                notEmpty.signal();
            return r;
        } catch (Exception ex) {
            throw ex;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the front of this deque. It will be
     * blocked and wait for space to become available if the deque is full.
     *
     * @param e the element to add
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this deque
     */
    public void putFirst(final E e) throws InterruptedException {
        lock.lock();
        try {
            while (!deque.offerFirst(e))
                notFull.await();
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the end of this deque. It will be
     * blocked and wait for space to become available if the deque is full.
     *
     * @param e the element to add
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if the specified element is null and this
     *         deque does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this deque
     */
    public void putLast(final E e) throws InterruptedException {
        lock.lock();
        try {
            while (!deque.offerLast(e))
                notFull.await();
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E removeFirst() {
        lock.lock();
        try {
            E e;
            if ((e = deque.removeFirst()) != null)
                notFull.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E removeLast() {
        lock.lock();
        try {
            E e;
            if ((e = deque.removeLast()) != null)
                notFull.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves and removes the first element of this deque, waiting
     * if necessary until an element becomes available.
     *
     * @return the head of this deque
     * @throws InterruptedException if interrupted while waiting
     */
    public E takeFirst() throws InterruptedException {
        lock.lock();
        try {
            while (deque.size() == 0)
                notEmpty.await();
            E x = deque.removeFirst();
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves and removes the last element of this deque, waiting
     * if necessary until an element becomes available.
     *
     * @return the tail of this deque
     * @throws InterruptedException if interrupted while waiting
     */
    public E takeLast() throws InterruptedException {
        lock.lock();
        try {
            while (deque.size() == 0)
                notEmpty.await();
            E x = deque.removeLast();
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E pollFirst() {
        lock.lock();
        try {
            E e;
            if ((e = deque.pollFirst()) != null)
                notFull.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E pollLast() {
        lock.lock();
        try {
            E e;
            if ((e = deque.pollLast()) != null)
                notFull.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E getFirst() {
        lock.lock();
        try {
            return deque.getFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E getLast() {
        lock.lock();
        try {
            return deque.getLast();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E peekFirst() {
        lock.lock();
        try {
            return deque.peekFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E peekLast() {
        lock.lock();
        try {
            return deque.peekLast();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeFirstOccurrence(final Object o) {
        lock.lock();
        try {
            boolean r;
            if ((r = deque.removeFirstOccurrence(o)) == true)
                notEmpty.signal();
            return r;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeLastOccurrence(final Object o) {
        lock.lock();
        try {
            boolean r;
            if ((r = deque.removeLastOccurrence(o)) == true)
                notEmpty.signal();
            return r;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean add(final E e) {
        addLast(e);
        return true;
    }

    @Override
    public boolean offer(final E e) {
        return offerLast(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public void push(final E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(final Object o) {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        lock.lock();
        try {
            return deque.containsAll(c);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        lock.lock();
        try {
            boolean r;
            if ((r = deque.addAll(c)) == true)
                notEmpty.signalAll();
            return r;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        lock.lock();
        try {
            boolean r;
            if ((r = deque.removeAll(c)) == true)
                notFull.signalAll();
            return r;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        lock.lock();
        try {
            boolean r;
            r = deque.retainAll(c);
            if (r == true) {
                notFull.signalAll();
            }
            return r;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            deque.clear();
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(final Object o) {
        lock.lock();
        try {
            return deque.contains(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return deque.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return deque.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return deque.iterator();
    }

    @Override
    public Object[] toArray() {
        lock.lock();
        try {
            return deque.toArray();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        lock.lock();
        try {
            return deque.toArray(a);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> descendingIterator() {
        return deque.descendingIterator();
    }
}
