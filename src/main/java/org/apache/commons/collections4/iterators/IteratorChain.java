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
package org.apache.commons.collections4.iterators;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * An IteratorChain is an Iterator that wraps a number of Iterators.
 * <p>
 * This class makes multiple iterators look like one to the caller. When any
 * method from the Iterator interface is called, the IteratorChain will delegate
 * to a single underlying Iterator. The IteratorChain will invoke the Iterators
 * in sequence until all Iterators are exhausted.
 * </p>
 * <p>
 * Under many circumstances, linking Iterators together in this manner is more
 * efficient (and convenient) than reading out the contents of each Iterator
 * into a List and creating a new Iterator.
 * </p>
 * <p>
 * Calling a method that adds new Iterator <i>after a method in the Iterator
 * interface has been called</i> will result in an UnsupportedOperationException.
 * </p>
 * <p>
 * Nested chains must not form cycles. If a cycle is detected during traversal or removal,
 * an {@link IllegalStateException} is thrown. Subclasses and other iterator implementations
 * are invoked normally and are responsible for their own cycle handling.
 * </p>
 * <p>
 * NOTE: As from version 3.0, the IteratorChain may contain no iterators. In
 * this case the class will function as an empty iterator.
 * </p>
 * <p>
 * NOTE: As from version 4.0, the IteratorChain stores the iterators in a queue
 * and removes any reference to them as soon as they are not used anymore. Thus,
 * the methods {@code setIterator(Iterator)} and {@code getIterators()} have been
 * removed and {@link #size()} will return the number of remaining iterators in
 * the queue.
 * </p>
 *
 * @param <E> The type of elements in this iterator.
 * @since 2.1
 */
public class IteratorChain<E> implements Iterator<E> {

    /**
     * Gets a nested chain that can be traversed without invoking overridden methods.
     * Unmodifiable decorators forward hasNext() and next(), but must still be retained for remove().
     */
    private static <E> IteratorChain<? extends E> asIteratorChain(final Iterator<? extends E> iterator) {
        Iterator<? extends E> unwrapped = iterator;
        while (unwrapped instanceof UnmodifiableIterator) {
            unwrapped = ((UnmodifiableIterator<? extends E, ?>) unwrapped).unwrap();
        }
        // Subclasses may override any iterator method or updateCurrentIterator().
        return unwrapped.getClass() == IteratorChain.class ? (IteratorChain<? extends E>) unwrapped : null;
    }

    /** The chain of iterators */
    private final Queue<Iterator<? extends E>> iteratorQueue = new LinkedList<>();

    /** The current iterator */
    private Iterator<? extends E> currentIterator;

    /**
     * The "last used" Iterator is the Iterator upon which next()
     * was most recently called used for the remove() operation only
     */
    private Iterator<? extends E> lastUsedIterator;

    /**
     * The chain is locked after the first call to an Iterator interface method.
     */
    private boolean isLocked;

    /**
     * Contains the result of the last hasNext() call until next() is invoked
     */
    private Boolean cachedHasNextValue;

    /**
     * Constructs an IteratorChain with no Iterators.
     * <p>
     * You will normally use {@link #addIterator(Iterator)} to add some
     * iterators after using this constructor.
     * </p>
     */
    public IteratorChain() {
    }

    /**
     * Constructs a new {@code IteratorChain} over the collection of
     * iterators.
     * <p>
     * This method takes a collection of iterators. The newly constructed
     * iterator will iterate through each one of the input iterators in turn.
     * </p>
     *
     * @param iteratorQueue The collection of iterators, not null
     * @throws NullPointerException if iterators collection is or contains null
     * @throws ClassCastException if iterators collection doesn't contain an
     * iterator
     */
    public IteratorChain(final Collection<? extends Iterator<? extends E>> iteratorQueue) {
        for (final Iterator<? extends E> iterator : iteratorQueue) {
            addIterator(iterator);
        }
    }

    /**
     * Constructs an IteratorChain with a single Iterator.
     * <p>
     * This method takes one iterator. The newly constructed iterator will
     * iterate through that iterator. Thus calling this constructor on its own
     * will have no effect other than decorating the input iterator.
     * </p>
     * <p>
     * You will normally use {@link #addIterator(Iterator)} to add some more
     * iterators after using this constructor.
     * </p>
     *
     * @param iterator The first child iterator in the IteratorChain, not null
     * @throws NullPointerException if the iterator is null
     */
    public IteratorChain(final Iterator<? extends E> iterator) {
        addIterator(iterator);
    }

    /**
     * Constructs a new {@code IteratorChain} over the array of iterators.
     * <p>
     * This method takes an array of iterators. The newly constructed iterator
     * will iterate through each one of the input iterators in turn.
     * </p>
     *
     * @param iteratorQueue The array of iterators, not null
     * @throws NullPointerException if iterators array is or contains null
     */
    public IteratorChain(final Iterator<? extends E>... iteratorQueue) {
        for (final Iterator<? extends E> element : iteratorQueue) {
            addIterator(element);
        }
    }

    /**
     * Constructs a new {@code IteratorChain} over the two given iterators.
     * <p>
     * This method takes two iterators. The newly constructed iterator will
     * iterate through each one of the input iterators in turn.
     * </p>
     *
     * @param first The first child iterator in the IteratorChain, not null
     * @param second The second child iterator in the IteratorChain, not null
     * @throws NullPointerException if either iterator is null
     */
    public IteratorChain(final Iterator<? extends E> first, final Iterator<? extends E> second) {
        addIterator(first);
        addIterator(second);
    }

    /**
     * Adds an Iterator to the end of the chain.
     * <p>
     * The iterator is retained, not copied. If it is another {@code IteratorChain}, iterators added to that child before it is locked are also visited.
     * Subclasses retain their iterator behavior when used as children.
     * </p>
     *
     * @param iterator Iterator to add
     * @throws UnsupportedOperationException if this chain is already locked
     * @throws NullPointerException if the iterator is null
     */
    public void addIterator(final Iterator<? extends E> iterator) {
        checkLocked();
        // Retain the iterator itself so subclass behavior and later additions to a child chain are preserved.
        iteratorQueue.add(Objects.requireNonNull(iterator, "iterator"));
    }

    /**
     * Checks whether the iterator chain is now locked and in use.
     */
    private void checkLocked() {
        if (isLocked) {
            throw new UnsupportedOperationException("IteratorChain cannot be changed after the first use of a method from the Iterator interface");
        }
    }

    /**
     * Return true if any Iterator in the IteratorChain has a remaining element.
     *
     * @return true if elements remain
     * @throws IllegalStateException if a cycle is detected in the nested chains
     */
    @Override
    public boolean hasNext() {
        lockChain();
        if (cachedHasNextValue == null) {
            updateCurrentIterator();
        }
        return cachedHasNextValue;
    }

    /**
     * Initializes the current iterator without inspecting its contents.
     */
    private void initializeCurrentIterator() {
        if (currentIterator == null) {
            pollCurrentIterator();
        }
    }

    /**
     * Determine if modifications can still be made to the IteratorChain.
     * IteratorChains cannot be modified once they have executed a method from
     * the Iterator interface.
     *
     * @return true if IteratorChain cannot be modified, false if it can
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Lock the chain so no more iterators can be added. This must be called
     * from all Iterator interface methods.
     */
    private void lockChain() {
        if (!isLocked) {
            isLocked = true;
        }
    }

    /**
     * Returns the next Object of the current Iterator
     *
     * @return Object from the current Iterator
     * @throws java.util.NoSuchElementException if all the Iterators are
     * exhausted
     * @throws IllegalStateException if a cycle is detected in the nested chains
     */
    @Override
    public E next() {
        Iterator<? extends E> iterator = nextIterator();
        IteratorChain<? extends E> chain;
        while ((chain = asIteratorChain(iterator)) != null) {
            iterator = chain.nextIterator();
        }
        return iterator.next();
    }

    /**
     * Prepares this chain for next() while retaining the actual child for remove().
     */
    private Iterator<? extends E> nextIterator() {
        lockChain();
        if (cachedHasNextValue == null) {
            updateCurrentIterator();
        }
        lastUsedIterator = currentIterator;
        cachedHasNextValue = null;
        return currentIterator;
    }

    /**
     * Advances to the next queued iterator, or an empty iterator if the queue is empty.
     */
    private void pollCurrentIterator() {
        currentIterator = iteratorQueue.isEmpty() ? EmptyIterator.<E>emptyIterator() : iteratorQueue.remove();
    }

    /**
     * Removes from the underlying collection the last element returned by the
     * Iterator. As with next() and hasNext(), this method calls remove() on the
     * underlying Iterator. Therefore, this method may throw an
     * UnsupportedOperationException if the underlying Iterator does not support
     * this method.
     *
     * @throws UnsupportedOperationException if the remove operator is not
     * supported by the underlying Iterator
     * @throws IllegalStateException if the next method has not yet been called,
     * or the remove method has already been called after the last call to the
     * next method, or a cycle is detected in the nested chains.
     */
    @Override
    public void remove() {
        lockChain();
        if (lastUsedIterator == null) {
            throw new IllegalStateException("remove() has been invoked without next()");
        }
        // Flat removal needs no traversal state, and must still delegate to subclasses and decorators.
        if (lastUsedIterator.getClass() != IteratorChain.class) {
            lastUsedIterator.remove();
            lastUsedIterator = null;
            return;
        }
        final Deque<IteratorChain<?>> chains = new ArrayDeque<>();
        final Set<IteratorChain<?>> active = Collections.newSetFromMap(new IdentityHashMap<>());
        IteratorChain<?> chain = this;
        while (true) {
            if (!active.add(chain)) {
                throw new IllegalStateException("Cyclic IteratorChain");
            }
            chain.lockChain();
            if (chain.lastUsedIterator == null) {
                throw new IllegalStateException("remove() has been invoked without next()");
            }
            chains.push(chain);
            final Iterator<?> iterator = chain.lastUsedIterator;
            // Do not unwrap unmodifiable decorators or bypass a subclass's remove() method.
            if (iterator.getClass() != IteratorChain.class) {
                iterator.remove();
                break;
            }
            chain = (IteratorChain<?>) iterator;
        }
        // Only clear removal state after the underlying remove() succeeds.
        for (final IteratorChain<?> usedChain : chains) {
            usedChain.lastUsedIterator = null;
        }
    }

    /**
     * Gets the number of iterators remaining in this chain's queue.
     * <p>
     * The current iterator is excluded. A nested chain counts as one iterator, regardless of its contents.
     * </p>
     *
     * @return Iterator count
     */
    public int size() {
        return iteratorQueue.size();
    }

    /**
     * Updates the current iterator field to ensure that the current Iterator is not exhausted.
     * Nested instances of this class are traversed iteratively; subclasses are invoked normally to preserve their overrides.
     *
     * @throws IllegalStateException if a cycle is detected in the nested chains
     */
    protected void updateCurrentIterator() {
        Deque<IteratorChain<? extends E>> parents = null;
        Set<IteratorChain<? extends E>> active = null;
        IteratorChain<? extends E> chain = this;
        while (true) {
            chain.initializeCurrentIterator();
            final IteratorChain<? extends E> child = asIteratorChain(chain.currentIterator);
            if (child == null) {
                chain.cachedHasNextValue = chain.currentIterator.hasNext();
            } else {
                child.lockChain();
                // A cached result must not conceal a link back to an active ancestor.
                if (child == chain || active != null && active.contains(child)) {
                    // Discard intermediate results so a retry also detects the cycle.
                    chain.cachedHasNextValue = null;
                    if (parents != null) {
                        for (final IteratorChain<?> parent : parents) {
                            parent.cachedHasNextValue = null;
                        }
                    }
                    throw new IllegalStateException("Cyclic IteratorChain");
                }
                if (child.cachedHasNextValue == null) {
                    if (parents == null) {
                        parents = new ArrayDeque<>();
                        active = Collections.newSetFromMap(new IdentityHashMap<>());
                        active.add(chain);
                    }
                    active.add(child);
                    parents.push(chain);
                    chain = child;
                    continue;
                }
                chain.cachedHasNextValue = child.cachedHasNextValue;
            }
            if (!chain.cachedHasNextValue && !chain.iteratorQueue.isEmpty()) {
                chain.pollCurrentIterator();
                continue;
            }
            if (parents == null || parents.isEmpty()) {
                return;
            }
            active.remove(chain);
            chain = parents.pop();
        }
    }
}
