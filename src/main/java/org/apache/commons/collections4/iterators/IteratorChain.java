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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
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
     * Retains the path to the current iterator. Chains on the path share it so independent access can discard it.
     */
    private static final class Traversal {

        private final List<IteratorChain<?>> path = new ArrayList<>();

        /** Length of the path prefix already linked for remove(). */
        private int linked;

        private Traversal(final IteratorChain<?> root) {
            path.add(root);
        }

        private void enter(final IteratorChain<?> chain) {
            path.add(chain);
            chain.traversal = this;
        }

        private IteratorChain<?> leaf() {
            return path.get(path.size() - 1);
        }

        private void leave() {
            path.remove(path.size() - 1).traversal = null;
            changedCurrentIterator();
        }

        private void changedCurrentIterator() {
            linked = Math.min(linked, path.size() - 1);
        }

        /**
         * Preserves the actual delegation links, including unmodifiable decorators.
         */
        private void linkForRemoval() {
            while (linked < path.size()) {
                path.get(linked++).rememberCurrentIterator();
            }
        }

        private void release() {
            if (path.size() > 1) {
                path.get(0).cachedHasNextValue = null;
            }
            for (int i = 0; i < path.size(); i++) {
                path.get(i).traversal = null;
            }
        }
    }

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

    /**
     * Creates a chain that retrieves each iterator from the source on demand.
     *
     * @param <E> The element type.
     * @param iterators The source of iterators, not null.
     * @return An iterator over the elements of the source iterators in order.
     * @throws NullPointerException if the source is null.
     * @since 4.6.1
     */
    public static <E> Iterator<E> fromIterators(final Iterator<? extends Iterator<? extends E>> iterators) {
        final IteratorChain<E> chain = new IteratorChain<>();
        chain.iteratorSource = Objects.requireNonNull(iterators, "iterators");
        return chain;
    }

    /** Iterators explicitly added to the chain. */
    private Queue<Iterator<? extends E>> iteratorQueue;

    /** A source of iterators to visit before the queue. */
    private Iterator<? extends Iterator<? extends E>> iteratorSource;

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

    /** The active traversal, if this chain is on a nested path. */
    private Traversal traversal;

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
        Objects.requireNonNull(iterator, "iterator");
        if (iteratorQueue == null) {
            iteratorQueue = new LinkedList<>();
        }
        iteratorQueue.add(iterator);
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
        return prepareNext();
    }

    /**
     * Checks for another element without invoking an override of hasNext() from next().
     */
    private boolean prepareNext() {
        lockChain();
        if (traversal != null && traversal.path.get(0) != this) {
            traversal.release();
        }
        if (cachedHasNextValue == null) {
            updateCurrentIterator();
        }
        return cachedHasNextValue;
    }

    /**
     * Initializes the current iterator without inspecting its contents.
     */
    private void initializeCurrentIterator() {
        if (currentIterator == null && !pollCurrentIterator()) {
            currentIterator = EmptyIterator.emptyIterator();
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
        if (!prepareNext()) {
            throw new NoSuchElementException();
        }
        cachedHasNextValue = null;
        final Traversal active = traversal;
        if (active == null) {
            final E value = currentIterator.next();
            rememberCurrentIterator();
            return value;
        }

        final IteratorChain<?> leaf = active.leaf();
        leaf.cachedHasNextValue = null;
        // Every entry on this path is a child of this chain.
        @SuppressWarnings("unchecked")
        final E value = (E) leaf.currentIterator.next();
        active.linkForRemoval();
        return value;
    }

    /**
     * Advances to the next available iterator, leaving the current iterator in place if there is none.
     */
    private boolean pollCurrentIterator() {
        if (iteratorSource != null) {
            final Iterator<? extends E> next = iteratorSource.hasNext() ? iteratorSource.next() : null;
            if (next != null) {
                currentIterator = next;
                return true;
            }
            iteratorSource = null;
        }
        if (iteratorQueue == null || iteratorQueue.isEmpty()) {
            return false;
        }
        currentIterator = iteratorQueue.remove();
        return true;
    }

    /**
     * Keeps the iterator that supplied the last element available for remove().
     */
    private void rememberCurrentIterator() {
        lastUsedIterator = currentIterator;
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
            clearRemoval();
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
            usedChain.clearRemoval();
        }
    }

    /**
     * Marks the removal links for renewal on the next successful next().
     */
    private void clearRemoval() {
        lastUsedIterator = null;
        if (traversal != null) {
            traversal.linked = 0;
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
        return iteratorQueue == null ? 0 : iteratorQueue.size();
    }

    /**
     * Updates the current iterator field to ensure that the current Iterator is not exhausted.
     * Nested instances of this class are traversed iteratively; subclasses are invoked normally to preserve their overrides.
     *
     * @throws IllegalStateException if a cycle is detected in the nested chains
     */
    protected void updateCurrentIterator() {
        IteratorChain<?> chain = traversal == null ? this : traversal.leaf();
        while (true) {
            chain.initializeCurrentIterator();
            final IteratorChain<?> child = asIteratorChain(chain.currentIterator);
            if (child == null) {
                if (chain.cachedHasNextValue == Boolean.TRUE || chain.currentIterator.hasNext()) {
                    if (chain != this) {
                        chain.cachedHasNextValue = Boolean.TRUE;
                    }
                    cachedHasNextValue = Boolean.TRUE;
                    return;
                }
            } else {
                child.lockChain();
                if (child == this || traversal != null && child.traversal == traversal) {
                    if (traversal != null) {
                        traversal.release();
                    }
                    throw new IllegalStateException("Cyclic IteratorChain");
                }
                if (child.cachedHasNextValue != Boolean.FALSE) {
                    if (child.traversal != null) {
                        child.traversal.release();
                    }
                    if (traversal == null) {
                        traversal = new Traversal(this);
                    }
                    traversal.enter(child);
                    chain = child;
                    continue;
                }
            }
            if (chain.pollCurrentIterator()) {
                chain.cachedHasNextValue = null;
                if (traversal != null) {
                    traversal.changedCurrentIterator();
                }
                continue;
            }
            chain.cachedHasNextValue = Boolean.FALSE;
            if (chain == this) {
                traversal = null;
                return;
            }
            traversal.leave();
            chain = traversal.leaf();
        }
    }
}
