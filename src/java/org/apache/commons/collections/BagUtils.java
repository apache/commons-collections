package org.apache.commons.collections;


import java.util.Collection;
import java.util.Comparator;
import java.util.Set;


/**
 *  Provides utility methods and decorators for {@link Bag} 
 *  and {@link SortedBag} instances.<P>
 *
 *  @author Paul Jack
 *  @version $Id: BagUtils.java,v 1.2 2002/08/13 00:46:25 pjack Exp $
 *  @since 2.1
 */
public class BagUtils {


    /**
     *  Prevents instantiation.
     */
    private BagUtils() {
    }


    static class PredicatedBag extends CollectionUtils.PredicatedCollection 
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


    static class BoundedBag extends CollectionUtils.CollectionWrapper
    implements Bag {

        final protected int maxSize;

        public BoundedBag(Bag bag, int maxSize) {
            super(bag);
            this.maxSize = maxSize;
        }

        public boolean add(Object o) {
            validate(1);
            return collection.add(o);
        }

        public boolean addAll(Collection c) {
            validate(c.size());
            return collection.addAll(c);
        }

        public boolean add(Object o, int count) {
            validate(count);
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

        protected void validate(int delta) {
            if (delta + size() > maxSize) {
                throw new IllegalStateException("Maximum size reached.");
            }
        }
    }


    static class PredicatedSortedBag extends PredicatedBag 
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


    static class SynchronizedSortedBag extends SynchronizedBag
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


    static class UnmodifiableSortedBag extends UnmodifiableBag
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


    static class BoundedSortedBag extends BoundedBag
    implements SortedBag {

        public BoundedSortedBag(SortedBag bag, int maxSize) {
            super(bag, maxSize);
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
     *  Returns a predicated bag backed by the given bag.  Only objects
     *  that pass the test in the given predicate can be added to the bag.
     *  It is important not to use the original bag after invoking this 
     *  method, as it is a backdoor for adding unvalidated objects.
     *
     *  @param b  the bag to predicate
     *  @param p  the predicate for the bag
     *  @return  a predicated bag backed by the given bag
     */
    public static Bag predicatedBag(Bag b, Predicate p) {
        return new PredicatedBag(b, p);
    }


    /**
     *  Returns an unmodifiable view of the given bag.  Any modification
     *  attempts to the returned bag will raise an 
     *  {@link UnsupportedOperationException}.
     *
     *  @param b  the bag whose unmodifiable view is to be returned
     *  @return  an unmodifiable view of that bag
     */
    public static Bag unmodifiableBag(Bag b) {
        return new UnmodifiableBag(b);
    }


    /**
     *  Returns a synchronized (thread-safe) bag backed by the given bag.
     *  In order to guarantee serial access, it is critical that all 
     *  access to the backing bag is accomplished through the returned bag.
     *  <P>
     *  It is imperative that the user manually synchronize on the returned
     *  bag when iterating over it:
     *
     *  <Pre>
     *  Bag bag = BagUtils.synchronizedBag(new HashBag());
     *  ...
     *  synchronized(bag) {
     *      Iterator i = bag.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *      }
     *  }
     *  </Pre>
     *
     *  Failure to follow this advice may result in non-deterministic 
     *  behavior.
     *
     *  @param b  the bag to synchronize
     *  @return  a synchronized bag backed by that bag
     */
    public static Bag synchronizedBag(Bag b) {
        return new SynchronizedBag(b);
    }


    /**
     *  Returns a bounded bag backed by the given bag.
     *  New elements may only be added to the returned bag if its 
     *  size is less than the specified maximum; otherwise, an
     *  {@link IllegalStateException} will be thrown.
     *
     *  @param b  the bag whose size to bind
     *  @param maxSize  the maximum size of the returned bag
     *  @return  a bounded bag 
     */
    public static Bag boundedBag(Bag b, int maxSize) {
        return new BoundedBag(b, maxSize);
    }




    /**
     *  Returns a predicated sorted bag backed by the given sorted bag.  
     *  Only objects that pass the test in the given predicate can be 
     *  added to the bag.
     *  It is important not to use the original bag after invoking this 
     *  method, as it is a backdoor for adding unvalidated objects.
     *
     *  @param b  the sorted bag to predicate
     *  @param p  the predicate for the bag
     *  @return  a predicated bag backed by the given bag
     */
    public static SortedBag predicatedSortedBag(SortedBag b, Predicate p) {
        return new PredicatedSortedBag(b, p);
    }


    /**
     *  Returns an unmodifiable view of the given sorted bag.  Any modification
     *  attempts to the returned bag will raise an 
     *  {@link UnsupportedOperationException}.
     *
     *  @param b  the bag whose unmodifiable view is to be returned
     *  @return  an unmodifiable view of that bag
     */
    public static SortedBag unmodifiableSortedBag(SortedBag b) {
        return new UnmodifiableSortedBag(b);
    }


    /**
     *  Returns a synchronized (thread-safe) sorted bag backed by the given 
     *  sorted bag.
     *  In order to guarantee serial access, it is critical that all 
     *  access to the backing bag is accomplished through the returned bag.
     *  <P>
     *  It is imperative that the user manually synchronize on the returned
     *  bag when iterating over it:
     *
     *  <Pre>
     *  SortedBag bag = BagUtils.synchronizedSortedBag(new TreeBag());
     *  ...
     *  synchronized(bag) {
     *      Iterator i = bag.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *      }
     *  }
     *  </Pre>
     *
     *  Failure to follow this advice may result in non-deterministic 
     *  behavior.
     *
     *  @param b  the bag to synchronize
     *  @return  a synchronized bag backed by that bag
     */
    public static SortedBag synchronizedSortedBag(SortedBag b) {
        return new SynchronizedSortedBag(b);
    }


    /**
     *  Returns a bounded sorted bag backed by the given sorted bag.
     *  New elements may only be added to the returned bag if its 
     *  size is less than the specified maximum; otherwise, an
     *  {@link IllegalStateException} will be thrown.
     *
     *  @param b  the bag whose size to bind
     *  @param maxSize  the maximum size of the returned bag
     *  @return  a bounded bag 
     */
    public static SortedBag boundedSortedBag(SortedBag b, int maxSize) {
        return new BoundedSortedBag(b, maxSize);
    }


}
