package org.apache.commons.collections;


import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;


/**
 *  Provides static utility methods and decorators for {@link Set} 
 *  and {@link SortedSet} instances.
 *
 *  @author Paul Jack
 *  @version $Id: SetUtils.java,v 1.2 2002/08/13 00:46:25 pjack Exp $
 *  @since 2.1
 */
public class SetUtils {


    /**
     *  Prevents instantiation.
     */
    private SetUtils() {
    }


    static class PredicatedSet extends CollectionUtils.PredicatedCollection
    implements Set {

        public PredicatedSet(Set set, Predicate p) {
            super(set, p);
        }

    }

    static class BoundedSet extends CollectionUtils.CollectionWrapper
    implements Set {

        final protected int maxSize;


        public BoundedSet(Set set, int maxSize) {
            super(set);
            this.maxSize = maxSize;
        }

        public boolean add(Object o) {
            if (!collection.contains(o)) {
                validate(1);
            }
            return collection.add(o);
        }

        public boolean addAll(Collection c) {
            int delta = 0;
            for (Iterator iter = c.iterator(); iter.hasNext(); ) {
                if (!collection.contains(iter.next())) delta++;
            }
            validate(delta);
            return collection.addAll(c);
        }


        private void validate(int delta) {
            if (delta + size() > maxSize) {
                throw new IllegalStateException("Maximum size reached.");
            }
        }

    }


    static class PredicatedSortedSet extends PredicatedSet 
    implements SortedSet {

        public PredicatedSortedSet(SortedSet s, Predicate p) {
            super(s, p);
        }

        public SortedSet subSet(Object o1, Object o2) {
            SortedSet sub = getSortedSet().subSet(o1, o2);
            return new PredicatedSortedSet(sub, predicate);
        }

        public SortedSet headSet(Object o1) {
            SortedSet sub = getSortedSet().headSet(o1);
            return new PredicatedSortedSet(sub, predicate);
        }

        public SortedSet tailSet(Object o1) {
            SortedSet sub = getSortedSet().tailSet(o1);
            return new PredicatedSortedSet(sub, predicate);
        }

        public Object first() {
            return getSortedSet().first();
        }

        public Object last() {
            return getSortedSet().last();
        }

        public Comparator comparator() {
            return getSortedSet().comparator();
        }

        private SortedSet getSortedSet() {
            return (SortedSet)collection;
        }

    }

    /**
     *  Returns a predicated set backed by the given set.  Only objects
     *  that pass the test in the given predicate can be added to the set.
     *  It is important not to use the original set after invoking this 
     *  method, as it is a backdoor for adding unvalidated objects.
     *
     *  @param set  the set to predicate
     *  @param p  the predicate for the set
     *  @return  a predicated set backed by the given set
     */
    public static Set predicatedSet(Set set, Predicate p) {
        return new PredicatedSet(set, p);
    }


    /**
     *  Returns a bounded set backed by the given set.
     *  New elements may only be added to the returned set if its 
     *  size is less than the specified maximum; otherwise, an
     *  {@link IllegalStateException} will be thrown.
     *
     *  @param set  the set whose size to bind
     *  @param maxSize  the maximum size of the returned set
     *  @return  a bounded set 
     */
    public static Set boundedSet(Set set, int maxSize) {
        return new BoundedSet(set, maxSize);
    }


    /**
     *  Returns a predicated sorted set backed by the given sorted set.  
     *  Only objects that pass the test in the given predicate can be added
     *  to the sorted set.
     *  It is important not to use the original sorted set after invoking this 
     *  method, as it is a backdoor for adding unvalidated objects.
     *
     *  @param set  the sorted set to predicate
     *  @param p  the predicate for the sorted set
     *  @return  a predicated sorted set backed by the given sorted set
     */
    public static SortedSet predicatedSortedSet(SortedSet set, Predicate p) {
        return new PredicatedSortedSet(set, p);
    }

}
