/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.Comparator;
import java.util.Set;
/**
 * Provides utility methods and decorators for {@link Bag} 
 * and {@link SortedBag} instances.<P>
 *
 * @author Paul Jack
 * @author Stephen Colebourne
 * @version $Id: BagUtils.java,v 1.7.2.1 2004/05/22 12:14:01 scolebourne Exp $
 * @since 2.1
 */
public class BagUtils {

    /**
     *  Prevents instantiation.
     */
    private BagUtils() {
    }


    static class PredicatedBag 
            extends CollectionUtils.PredicatedCollection 
            implements Bag {

        public PredicatedBag(Bag b, Predicate p) {
            super(b, p);
        }

        public boolean add(Object o, int count) {
            validate(o);
            return getBag().add(o, count);
        }

        public boolean remove(Object o, int count) {
            return getBag().remove(o, count);
        }

        public Set uniqueSet() {
            return getBag().uniqueSet();
        }

        public int getCount(Object o) {
            return getBag().getCount(o);
        }

        private Bag getBag() {
            return (Bag)collection;
        }
    }


    static class UnmodifiableBag 
            extends CollectionUtils.UnmodifiableCollection
            implements Bag {

        public UnmodifiableBag(Bag bag) {
            super(bag);
        }

        private Bag getBag() {
            return (Bag)collection;
        }

        public boolean add(Object o, int count) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o, int count) {
            throw new UnsupportedOperationException();
        }

        public Set uniqueSet() {
            return ((Bag)collection).uniqueSet();
        }

        public int getCount(Object o) {
            return ((Bag)collection).getCount(o);
        }
    }


    static class SynchronizedBag
            extends CollectionUtils.SynchronizedCollection
            implements Bag {

        public SynchronizedBag(Bag bag) {
            super(bag);
        }

        public synchronized boolean add(Object o, int count) {
            return getBag().add(o, count);
        }

        public synchronized boolean remove(Object o, int count) {
            return getBag().remove(o, count);
        }

        public synchronized Set uniqueSet() {
            return getBag().uniqueSet();
        }

        public synchronized int getCount(Object o) {
            return getBag().getCount(o);
        }

        private Bag getBag() {
            return (Bag)collection;
        }

    }


    static class PredicatedSortedBag 
            extends PredicatedBag 
            implements SortedBag {

        public PredicatedSortedBag(SortedBag sb, Predicate p) {
            super(sb, p);
        }

        public Comparator comparator() {
            return getSortedBag().comparator();
        }

        public Object first() {
            return getSortedBag().first();
        }

        public Object last() {
            return getSortedBag().last();
        }

        private SortedBag getSortedBag() {
            return (SortedBag)collection;
        }
    }


    static class SynchronizedSortedBag 
            extends SynchronizedBag
            implements SortedBag {

        public SynchronizedSortedBag(SortedBag bag) {
            super(bag);
        }

        public synchronized Comparator comparator() {
            return getSortedBag().comparator();
        }

        public synchronized Object first() {
            return getSortedBag().first();
        }

        public synchronized Object last() {
            return getSortedBag().last();
        }

        private SortedBag getSortedBag() {
            return (SortedBag)collection;
        }

    }


    static class UnmodifiableSortedBag 
            extends UnmodifiableBag
            implements SortedBag {

        public UnmodifiableSortedBag(SortedBag bag) {
            super(bag);
        }

        public Comparator comparator() {
            return getSortedBag().comparator();
        }

        public Object first() {
            return getSortedBag().first();
        }

        public Object last() {
            return getSortedBag().last();
        }

        private SortedBag getSortedBag() {
            return (SortedBag)collection;
        }

    }


    /**
     * Returns a predicated bag backed by the given bag.  Only objects
     * that pass the test in the given predicate can be added to the bag.
     * It is important not to use the original bag after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param bag  the bag to predicate, must not be null
     * @param predicate  the predicate for the bag, must not be null
     * @return a predicated bag backed by the given bag
     * @throws IllegalArgumentException  if the Bag or Predicate is null
     */
    public static Bag predicatedBag(Bag bag, Predicate predicate) {
        return new PredicatedBag(bag, predicate);
    }

    /**
     * Returns an unmodifiable view of the given bag.  Any modification
     * attempts to the returned bag will raise an 
     * {@link UnsupportedOperationException}.
     *
     * @param bag  the bag whose unmodifiable view is to be returned, must not be null
     * @return an unmodifiable view of that bag
     * @throws IllegalArgumentException  if the Bag is null
     */
    public static Bag unmodifiableBag(Bag bag) {
        return new UnmodifiableBag(bag);
    }

    /**
     * Returns a synchronized (thread-safe) bag backed by the given bag.
     * In order to guarantee serial access, it is critical that all 
     * access to the backing bag is accomplished through the returned bag.
     * <p>
     * It is imperative that the user manually synchronize on the returned
     * bag when iterating over it:
     *
     * <pre>
     * Bag bag = BagUtils.synchronizedBag(new HashBag());
     * ...
     * synchronized(bag) {
     *     Iterator i = bag.iterator(); // Must be in synchronized block
     *     while (i.hasNext())
     *         foo(i.next());
     *     }
     * }
     * </pre>
     *
     * Failure to follow this advice may result in non-deterministic 
     * behavior.
     *
     * @param bag  the bag to synchronize, must not be null
     * @return a synchronized bag backed by that bag
     * @throws IllegalArgumentException  if the Bag is null
     */
    public static Bag synchronizedBag(Bag bag) {
        return new SynchronizedBag(bag);
    }

    /**
     * Returns a predicated sorted bag backed by the given sorted bag.  
     * Only objects that pass the test in the given predicate can be 
     * added to the bag.
     * It is important not to use the original bag after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param bag  the sorted bag to predicate, must not be null
     * @param predicate  the predicate for the bag, must not be null
     * @return a predicated bag backed by the given bag
     * @throws IllegalArgumentException  if the SortedBag or Predicate is null
     */
    public static SortedBag predicatedSortedBag(SortedBag bag, Predicate predicate) {
        return new PredicatedSortedBag(bag, predicate);
    }

    /**
     * Returns an unmodifiable view of the given sorted bag.  Any modification
     * attempts to the returned bag will raise an 
     * {@link UnsupportedOperationException}.
     *
     * @param bag  the bag whose unmodifiable view is to be returned, must not be null
     * @return an unmodifiable view of that bag
     * @throws IllegalArgumentException  if the SortedBag is null
     */
    public static SortedBag unmodifiableSortedBag(SortedBag bag) {
        return new UnmodifiableSortedBag(bag);
    }

    /**
     * Returns a synchronized (thread-safe) sorted bag backed by the given 
     * sorted bag.
     * In order to guarantee serial access, it is critical that all 
     * access to the backing bag is accomplished through the returned bag.
     * <p>
     * It is imperative that the user manually synchronize on the returned
     * bag when iterating over it:
     *
     * <pre>
     * SortedBag bag = BagUtils.synchronizedSortedBag(new TreeBag());
     * ...
     * synchronized(bag) {
     *     Iterator i = bag.iterator(); // Must be in synchronized block
     *     while (i.hasNext())
     *         foo(i.next());
     *     }
     * }
     * </pre>
     *
     * Failure to follow this advice may result in non-deterministic 
     * behavior.
     *
     * @param bag  the bag to synchronize, must not be null
     * @return a synchronized bag backed by that bag
     * @throws IllegalArgumentException  if the SortedBag is null
     */
    public static SortedBag synchronizedSortedBag(SortedBag bag) {
        return new SynchronizedSortedBag(bag);
    }

}
