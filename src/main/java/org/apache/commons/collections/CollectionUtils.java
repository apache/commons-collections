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
package org.apache.commons.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.collections.collection.PredicatedCollection;
import org.apache.commons.collections.collection.SynchronizedCollection;
import org.apache.commons.collections.collection.TransformedCollection;
import org.apache.commons.collections.collection.UnmodifiableBoundedCollection;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.functors.TruePredicate;

/**
 * Provides utility methods and decorators for {@link Collection} instances.
 * Method parameters will take {@link Iterable} objects when possible.
 *
 * @since 1.0
 * @version $Id$
 */
//TODO - note generic types for review in wiki - especially <?> ones
//TODO - doc Cardinality Helpers
public class CollectionUtils {

    private static class CardinalityHelper<O> {
        final Map<O, Integer> cardinalityA, cardinalityB;

        public CardinalityHelper(Iterable<? extends O> a, Iterable<? extends O> b) {
            cardinalityA = CollectionUtils.<O>getCardinalityMap(a);
            cardinalityB = CollectionUtils.<O>getCardinalityMap(b);
        }

        public final int max(Object obj) {
            return Math.max(freqA(obj), freqB(obj));
        }

        public final int min(Object obj) {
            return Math.min(freqA(obj), freqB(obj));
        }

        public int freqA(Object obj) {
            return getFreq(obj, cardinalityA);
        }

        public int freqB(Object obj) {
            return getFreq(obj, cardinalityB);
        }

        private final int getFreq(final Object obj, final Map<?, Integer> freqMap) {
            Integer count = freqMap.get(obj);
            if (count != null) {
                return count.intValue();
            }
            return 0;
        }
    }

    private static class SetOperationCardinalityHelper<O> extends CardinalityHelper<O> implements Iterable<O> {
        private final Set<O> elements;
        private final List<O> newList;

        public SetOperationCardinalityHelper(Iterable<? extends O> a, Iterable<? extends O> b) {
            super(a, b);
            elements = new HashSet<O>();
            addAll(elements, a);
            addAll(elements, b);
            newList = new ArrayList<O>();
        }

        public Iterator<O> iterator() {
            return elements.iterator();
        }

        public void setCardinality(O obj, int count) {
            for (int i = 0; i < count; i++) {
                newList.add(obj);
            }
        }

        public Collection<O> list() {
            return newList;
        }

    }

    /**
     * An empty unmodifiable collection.
     * The JDK provides empty Set and List implementations which could be used for
     * this purpose. However they could be cast to Set or List which might be
     * undesirable. This implementation only implements Collection.
     */
    @SuppressWarnings("rawtypes") // we deliberately use the raw type here
    public static final Collection EMPTY_COLLECTION =
        UnmodifiableCollection.unmodifiableCollection(new ArrayList<Object>());

    /**
     * <code>CollectionUtils</code> should not normally be instantiated.
     */
    public CollectionUtils() {
    }

    /**
     * Returns the immutable EMPTY_COLLECTION with generic type safety.
     *
     * @see #EMPTY_COLLECTION
     * @since 4.0
     * @param <T> the element type
     * @return immutable empty collection
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> emptyCollection() {
        return EMPTY_COLLECTION;
    }

    /**
     * Returns an immutable empty collection if the argument is <code>null</code>,
     * or the argument itself otherwise.
     * 
     * @param <T> the element type
     * @param collection the collection, possibly <code>null</code>
     * @return an empty collection if the argument is <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return collection == null ? EMPTY_COLLECTION : collection;
    }

    /**
     * Returns a {@link Collection} containing the union of the given
     * {@link Collection}s.
     * <p>
     * The cardinality of each element in the returned {@link Collection} will
     * be equal to the maximum of the cardinality of that element in the two
     * given {@link Collection}s.
     *
     * @param a the first collection, must not be null
     * @param b the second collection, must not be null
     * @param <O> the generic type that is able to represent the types contained
     *        in both input collections.
     * @return the union of the two collections
     * @see Collection#addAll
     */
    public static <O> Collection<O> union(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);
        for (O obj : helper) {
            helper.setCardinality(obj, helper.max(obj));
        }
        return helper.list();
    }

    /**
     * Returns a {@link Collection} containing the intersection of the given
     * {@link Collection}s.
     * <p>
     * The cardinality of each element in the returned {@link Collection} will
     * be equal to the minimum of the cardinality of that element in the two
     * given {@link Collection}s.
     *
     * @param a the first collection, must not be null
     * @param b the second collection, must not be null
     * @param <O> the generic type that is able to represent the types contained
     *        in both input collections.
     * @return the intersection of the two collections
     * @see Collection#retainAll
     * @see #containsAny
     */
    public static <O> Collection<O> intersection(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);
        for (O obj : helper) {
            helper.setCardinality(obj, helper.min(obj));
        }
        return helper.list();
    }

    /**
     * Returns a {@link Collection} containing the exclusive disjunction
     * (symmetric difference) of the given {@link Collection}s.
     * <p>
     * The cardinality of each element <i>e</i> in the returned
     * {@link Collection} will be equal to
     * <tt>max(cardinality(<i>e</i>,<i>a</i>),cardinality(<i>e</i>,<i>b</i>)) - min(cardinality(<i>e</i>,<i>a</i>),
     * cardinality(<i>e</i>,<i>b</i>))</tt>.
     * <p>
     * This is equivalent to
     * <tt>{@link #subtract subtract}({@link #union union(a,b)},{@link #intersection intersection(a,b)})</tt>
     * or
     * <tt>{@link #union union}({@link #subtract subtract(a,b)},{@link #subtract subtract(b,a)})</tt>.

     * @param a the first collection, must not be null
     * @param b the second collection, must not be null
     * @param <O> the generic type that is able to represent the types contained
     *        in both input collections.
     * @return the symmetric difference of the two collections
     */
    public static <O> Collection<O> disjunction(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);
        for (O obj : helper) {
            helper.setCardinality(obj, helper.max(obj) - helper.min(obj));
        }
        return helper.list();
    }

    /**
     * Returns a new {@link Collection} containing <tt><i>a</i> - <i>b</i></tt>.
     * The cardinality of each element <i>e</i> in the returned {@link Collection}
     * will be the cardinality of <i>e</i> in <i>a</i> minus the cardinality
     * of <i>e</i> in <i>b</i>, or zero, whichever is greater.
     *
     * @param a  the collection to subtract from, must not be null
     * @param b  the collection to subtract, must not be null
     * @param <O> the generic type that is able to represent the types contained
     *        in both input collections.
     * @return a new collection with the results
     * @see Collection#removeAll
     */
    public static <O> Collection<O> subtract(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        Predicate<O> p = TruePredicate.truePredicate();
        return subtract(a, b, p);
    }

    /**
     * Returns a new {@link Collection} containing <i>a</i> minus a subset of
     * <i>b</i>.  Only the elements of <i>b</i> that satisfy the predicate
     * condition, <i>p</i> are subtracted from <i>a</i>.
     * 
     * <p>The cardinality of each element <i>e</i> in the returned {@link Collection}
     * that satisfies the predicate condition will be the cardinality of <i>e</i> in <i>a</i>
     * minus the cardinality of <i>e</i> in <i>b</i>, or zero, whichever is greater.</p>
     * <p>The cardinality of each element <i>e</i> in the returned {@link Collection} that does <b>not</b>
     * satisfy the predicate condition will be equal to the cardinality of <i>e</i> in <i>a</i>.</p>
     *
     * @param a  the collection to subtract from, must not be null
     * @param b  the collection to subtract, must not be null
     * @param p  the condition used to determine which elements of <i>b</i> are
     *        subtracted.
     * @param <O> the generic type that is able to represent the types contained
     *        in both input collections.
     * @return a new collection with the results
     * @since 4.0
     * @see Collection#removeAll
     */
    public static <O> Collection<O> subtract(final Iterable<? extends O> a,
                                             final Iterable<? extends O> b,
                                             final Predicate<O> p) {
        final ArrayList<O> list = new ArrayList<O>();
        final HashBag<O> bag = new HashBag<O>();
        for (O element : b) {
            if (p.evaluate(element)) {
                bag.add(element);
            }
        }
        for (O element : a) {
            if (!bag.remove(element, 1)) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * Returns <code>true</code> iff at least one element is in both collections.
     * <p>
     * In other words, this method returns <code>true</code> iff the
     * {@link #intersection} of <i>coll1</i> and <i>coll2</i> is not empty.
     *
     * @param coll1  the first collection, must not be null
     * @param coll2  the first collection, must not be null
     * @return <code>true</code> iff the intersection of the collections is non-empty
     * @since 2.1
     * @see #intersection
     */
    public static boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
        if (coll1.size() < coll2.size()) {
            for (Object aColl1 : coll1) {
                if (coll2.contains(aColl1)) {
                    return true;
                }
            }
        } else {
            for (Object aColl2 : coll2) {
                if (coll1.contains(aColl2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a {@link Map} mapping each unique element in the given
     * {@link Collection} to an {@link Integer} representing the number
     * of occurrences of that element in the {@link Collection}.
     * <p>
     * Only those elements present in the collection will appear as
     * keys in the map.
     *
     * @param coll
     *            the collection to get the cardinality map for, must not be
     *            null
     * @param <O>
     *            the type of object in the returned {@link Map}. This is a
     *            super type of <I>.
     * @return the populated cardinality map
     */
    public static <O> Map<O, Integer> getCardinalityMap(final Iterable<? extends O> coll) {
        Map<O, Integer> count = new HashMap<O, Integer>();
        for (O obj : coll) {
            Integer c = count.get(obj);
            if (c == null) {
                count.put(obj, Integer.valueOf(1));
            } else {
                count.put(obj, Integer.valueOf(c.intValue() + 1));
            }
        }
        return count;
    }

    /**
     * Returns <tt>true</tt> iff <i>a</i> is a sub-collection of <i>b</i>,
     * that is, iff the cardinality of <i>e</i> in <i>a</i> is less than or
     * equal to the cardinality of <i>e</i> in <i>b</i>, for each element <i>e</i>
     * in <i>a</i>.
     *
     * @param a the first (sub?) collection, must not be null
     * @param b the second (super?) collection, must not be null
     * @return <code>true</code> iff <i>a</i> is a sub-collection of <i>b</i>
     * @see #isProperSubCollection
     * @see Collection#containsAll
     */
    public static boolean isSubCollection(final Collection<?> a, final Collection<?> b) {
        CardinalityHelper<Object> helper = new CardinalityHelper<Object>(a, b);
        for (Object obj : a) {
            if (helper.freqA(obj) > helper.freqB(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns <tt>true</tt> iff <i>a</i> is a <i>proper</i> sub-collection of <i>b</i>,
     * that is, iff the cardinality of <i>e</i> in <i>a</i> is less
     * than or equal to the cardinality of <i>e</i> in <i>b</i>,
     * for each element <i>e</i> in <i>a</i>, and there is at least one
     * element <i>f</i> such that the cardinality of <i>f</i> in <i>b</i>
     * is strictly greater than the cardinality of <i>f</i> in <i>a</i>.
     * <p>
     * The implementation assumes
     * <ul>
     *    <li><code>a.size()</code> and <code>b.size()</code> represent the
     *    total cardinality of <i>a</i> and <i>b</i>, resp. </li>
     *    <li><code>a.size() < Integer.MAXVALUE</code></li>
     * </ul>
     *
     * @param a  the first (sub?) collection, must not be null
     * @param b  the second (super?) collection, must not be null
     * @return <code>true</code> iff <i>a</i> is a <i>proper</i> sub-collection of <i>b</i>
     * @see #isSubCollection
     * @see Collection#containsAll
     */
    public static boolean isProperSubCollection(final Collection<?> a, final Collection<?> b) {
        return (a.size() < b.size()) && CollectionUtils.isSubCollection(a, b);
    }

    /**
     * Returns <tt>true</tt> iff the given {@link Collection}s contain
     * exactly the same elements with exactly the same cardinalities.
     * <p>
     * That is, iff the cardinality of <i>e</i> in <i>a</i> is
     * equal to the cardinality of <i>e</i> in <i>b</i>,
     * for each element <i>e</i> in <i>a</i> or <i>b</i>.
     *
     * @param a  the first collection, must not be null
     * @param b  the second collection, must not be null
     * @return <code>true</code> iff the collections contain the same elements with the same cardinalities.
     */
    public static boolean isEqualCollection(final Collection<?> a, final Collection<?> b) {
        if(a.size() != b.size()) {
            return false;
        }
        final CardinalityHelper<Object> helper = new CardinalityHelper<Object>(a, b);
        if(helper.cardinalityA.size() != helper.cardinalityB.size()) {
            return false;
        }
        for( Object obj : helper.cardinalityA.keySet()) {
            if(helper.freqA(obj) != helper.freqB(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the number of occurrences of <i>obj</i> in <i>coll</i>.
     *
     * @param obj the object to find the cardinality of
     * @param coll the {@link Iterable} to search
     * @param <O> the type of object that the {@link Iterable} may contain.
     * @return the the number of occurrences of obj in coll
     */
    public static <O> int cardinality(O obj, final Iterable<? super O> coll) {
        if (coll instanceof Set<?>) {
            return (((Set<? super O>) coll).contains(obj) ? 1 : 0);
        }
        if (coll instanceof Bag<?>) {
            return ((Bag<? super O>) coll).getCount(obj);
        }
        int count = 0;
        if (obj == null) {
            for (Object element : coll) {
                if (element == null) {
                    count++;
                }
            }
        } else {
            for (Object element : coll) {
                if (obj.equals(element)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Finds the first element in the given collection which matches the given predicate.
     * <p>
     * If the input collection or predicate is null, or no element of the collection
     * matches the predicate, null is returned.
     *
     * @param collection  the collection to search, may be null
     * @param predicate  the predicate to use, may be null
     * @return the first element of the collection which matches the predicate or null if none could be found
     */
    public static <T> T find(Collection<T> collection, Predicate<? super T> predicate) {
        if (collection != null && predicate != null) {
            for (T item : collection) {
                if (predicate.evaluate(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Executes the given closure on each element in the collection.
     * <p>
     * If the input collection or closure is null, there is no change made.
     *
     * @param collection
     *            the collection to get the input from, may be null
     * @param closure
     *            the closure to perform, may be null
     * @return closure
     */
    public static <T, C extends Closure<? super T>> C forAllDo(Collection<T> collection, C closure) {
        if (collection != null && closure != null) {
            for (T element : collection) {
                closure.execute(element);
            }
        }
        return closure;
    }

    /**
     * Executes the given closure on each element in the collection.
     * <p>
     * If the input collection or closure is null, there is no change made.
     *
     * @param iterator
     *            the iterator to get the input from, may be null
     * @param closure
     *            the closure to perform, may be null
     * @return closure
     * @since 4.0
     */
    public static <T, C extends Closure<? super T>> C forAllDo(Iterator<T> iterator, C closure) {
        if (iterator != null && closure != null) {
            while (iterator.hasNext()) {
                closure.execute(iterator.next());
            }
        }
        return closure;
    }

    /**
     * Filter the collection by applying a Predicate to each element. If the
     * predicate returns false, remove the element.
     * <p>
     * If the input collection or predicate is null, there is no change made.
     * 
     * @param collection
     *            the collection to get the input from, may be null
     * @param predicate
     *            the predicate to use as a filter, may be null
     * @return true if the collection is modified by this call, false otherwise.
     */
    public static <T> boolean filter(Iterable<T> collection, Predicate<? super T> predicate) {
        boolean result = false;
        if (collection != null && predicate != null) {
            for (Iterator<T> it = collection.iterator(); it.hasNext();) {
                if (!predicate.evaluate(it.next())) {
                    it.remove();
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Transform the collection by applying a Transformer to each element.
     * <p>
     * If the input collection or transformer is null, there is no change made.
     * <p>
     * This routine is best for Lists, for which set() is used to do the
     * transformations "in place." For other Collections, clear() and addAll()
     * are used to replace elements.
     * <p>
     * If the input collection controls its input, such as a Set, and the
     * Transformer creates duplicates (or are otherwise invalid), the collection
     * may reduce in size due to calling this method.
     *
     * @param collection
     *            the {@link Iterable} to get the input from, may be null
     * @param transformer
     *            the transformer to perform, may be null
     */
    public static <C> void transform(Collection<C> collection,
            Transformer<? super C, ? extends C> transformer) {
        if (collection != null && transformer != null) {
            if (collection instanceof List<?>) {
                List<C> list = (List<C>) collection;
                for (ListIterator<C> it = list.listIterator(); it.hasNext();) {
                    it.set(transformer.transform(it.next()));
                }
            } else {
                Collection<C> resultCollection = collect(collection, transformer);
                collection.clear();
                collection.addAll(resultCollection);
            }
        }
    }

    /**
     * Counts the number of elements in the input collection that match the
     * predicate.
     * <p>
     * A <code>null</code> collection or predicate matches no elements.
     *
     * @param input
     *            the {@link Iterable} to get the input from, may be null
     * @param predicate
     *            the predicate to use, may be null
     * @return the number of matches for the predicate in the collection
     */
    public static <C> int countMatches(Iterable<C> input, Predicate<? super C> predicate) {
        int count = 0;
        if (input != null && predicate != null) {
            for (C o : input) {
                if (predicate.evaluate(o)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Answers true if a predicate is true for at least one element of a
     * collection.
     * <p>
     * A <code>null</code> collection or predicate returns false.
     *
     * @param input
     *            the {@link Iterable} to get the input from, may be null
     * @param predicate
     *            the predicate to use, may be null
     * @return true if at least one element of the collection matches the
     *         predicate
     */
    public static <C> boolean exists(Iterable<C> input, Predicate<? super C> predicate) {
        if (input != null && predicate != null) {
            for (C o : input) {
                if (predicate.evaluate(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Selects all elements from input collection which match the given
     * predicate into an output collection.
     * <p>
     * A <code>null</code> predicate matches no elements.
     *
     * @param inputCollection
     *            the collection to get the input from, may not be null
     * @param predicate
     *            the predicate to use, may be null
     * @return the elements matching the predicate (new list)
     * @throws NullPointerException
     *             if the input collection is null
     */
    public static <O> Collection<O> select(Collection<? extends O> inputCollection,
            Predicate<? super O> predicate) {
        return select(inputCollection, predicate, new ArrayList<O>(inputCollection.size()));
    }

    /**
     * Selects all elements from input collection which match the given
     * predicate and adds them to outputCollection.
     * <p>
     * If the input collection or predicate is null, there is no change to the
     * output collection.
     *
     * @param inputCollection
     *            the collection to get the input from, may be null
     * @param predicate
     *            the predicate to use, may be null
     * @param outputCollection
     *            the collection to output into, may not be null if the inputCollection
     *            and predicate or not null
     * @return the outputCollection
     */
    public static <O, R extends Collection<? super O>> R select(Collection<? extends O> inputCollection,
            Predicate<? super O> predicate, R outputCollection) {
        if (inputCollection != null && predicate != null) {
            for (O item : inputCollection) {
                if (predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
        return outputCollection;
    }

    /**
     * Selects all elements from inputCollection which don't match the given
     * predicate into an output collection.
     * <p>
     * If the input predicate is <code>null</code>, the result is an empty
     * list.
     *
     * @param inputCollection
     *            the collection to get the input from, may not be null
     * @param predicate
     *            the predicate to use, may be null
     * @return the elements <b>not</b> matching the predicate (new list)
     * @throws NullPointerException
     *             if the input collection is null
     */
    public static <O> Collection<O> selectRejected(Collection<? extends O> inputCollection,
            Predicate<? super O> predicate) {
        return selectRejected(inputCollection, predicate, new ArrayList<O>(inputCollection.size()));
    }

    /**
     * Selects all elements from inputCollection which don't match the given
     * predicate and adds them to outputCollection.
     * <p>
     * If the input predicate is <code>null</code>, no elements are added to
     * <code>outputCollection</code>.
     *
     * @param inputCollection
     *            the collection to get the input from, may be null
     * @param predicate
     *            the predicate to use, may be null
     * @param outputCollection
     *            the collection to output into, may not be null if the inputCollection
     *            and predicate or not null
     * @return outputCollection
     */
    public static <O, R extends Collection<? super O>> R selectRejected(
            Collection<? extends O> inputCollection, Predicate<? super O> predicate, R outputCollection) {
        if (inputCollection != null && predicate != null) {
            for (O item : inputCollection) {
                if (!predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
        return outputCollection;
    }

    /**
     * Returns a new Collection consisting of the elements of inputCollection
     * transformed by the given transformer.
     * <p>
     * If the input transformer is null, the result is an empty list.
     *
     * @param inputCollection
     *            the collection to get the input from, may not be null
     * @param transformer
     *            the transformer to use, may be null
     * @param <I> the type of object in the input collection
     * @param <O> the type of object in the output collection
     * @return the transformed result (new list)
     * @throws NullPointerException
     *             if the input collection is null
     */
    public static <I, O> Collection<O> collect(Iterable<I> inputCollection,
            Transformer<? super I, ? extends O> transformer) {
        ArrayList<O> answer = new ArrayList<O>();
        collect(inputCollection, transformer, answer);
        return answer;
    }

    /**
     * Transforms all elements from the inputIterator with the given transformer
     * and adds them to the outputCollection.
     * <p>
     * If the input iterator or transformer is null, the result is an empty
     * list.
     *
     * @param inputIterator
     *            the iterator to get the input from, may be null
     * @param transformer
     *            the transformer to use, may be null
     * @param <I> the type of object in the input collection
     * @param <O> the type of object in the output collection
     * @return the transformed result (new list)
     */
    public static <I, O> Collection<O> collect(Iterator<I> inputIterator,
            Transformer<? super I, ? extends O> transformer) {
        ArrayList<O> answer = new ArrayList<O>();
        collect(inputIterator, transformer, answer);
        return answer;
    }

    /**
     * Transforms all elements from inputCollection with the given transformer
     * and adds them to the outputCollection.
     * <p>
     * If the input collection or transformer is null, there is no change to the
     * output collection.
     *
     * @param inputCollection  the collection to get the input from, may be null
     * @param transformer  the transformer to use, may be null
     * @param outputCollection  the collection to output into, may not be null if the inputCollection
     *   and transformer are not null
     * @param <I> the type of object in the input collection
     * @param <O> the type of object in the output collection
     * @param <R> the output type of the transformer - this extends O.
     * @return the outputCollection with the transformed input added
     * @throws NullPointerException if the output collection is null and both, inputCollection and
     *   transformer are not null
     */
    public static <I, O, R extends Collection<? super O>> R collect(Iterable<? extends I> inputCollection,
            final Transformer<? super I, ? extends O> transformer, final R outputCollection) {
        if (inputCollection != null) {
            return collect(inputCollection.iterator(), transformer, outputCollection);
        }
        return outputCollection;
    }

    /**
     * Transforms all elements from the inputIterator with the given transformer
     * and adds them to the outputCollection.
     * <p>
     * If the input iterator or transformer is null, there is no change to the
     * output collection.
     *
     * @param inputIterator  the iterator to get the input from, may be null
     * @param transformer  the transformer to use, may be null
     * @param outputCollection  the collection to output into, may not be null if the inputCollection
     *   and transformer are not null
     * @param <I> the type of object in the input collection
     * @param <O> the type of object in the output collection
     * @param <R> the output type of the transformer - this extends O.
     * @return the outputCollection with the transformed input added
     * @throws NullPointerException if the output collection is null and both, inputCollection and
     *   transformer are not null
     */
    //TODO - deprecate and replace with IteratorIterable
    public static <I, O, R extends Collection<? super O>> R collect(Iterator<? extends I> inputIterator,
            final Transformer<? super I, ? extends O> transformer, final R outputCollection) {
        if (inputIterator != null && transformer != null) {
            while (inputIterator.hasNext()) {
                I item = inputIterator.next();
                O value = transformer.transform(item);
                outputCollection.add(value);
            }
        }
        return outputCollection;
    }

    //-----------------------------------------------------------------------
    /**
     * Adds an element to the collection unless the element is null.
     *
     * @param collection  the collection to add to, must not be null
     * @param object  the object to add, if null it will not be added
     * @return true if the collection changed
     * @throws NullPointerException if the collection is null
     * @since 3.2
     */
    public static <T> boolean addIgnoreNull(Collection<T> collection, T object) {
        if (collection == null) {
            throw new NullPointerException("The collection must not be null");
        }
        return (object != null && collection.add(object));
    }

    /**
     * Adds all elements in the {@link Iterable} to the given collection. If the
     * {@link Iterable} is a {@link Collection} then it is cast and will be
     * added using {@link Collection#addAll(Collection)} instead of iterating.
     *
     * @param collection
     *            the collection to add to, must not be null
     * @param iterable
     *            the iterable of elements to add, must not be null
     * @return a boolean indicating whether the collection has changed or not.
     * @throws NullPointerException
     *             if the collection or iterator is null
     */
    public static <C> boolean addAll(Collection<C> collection, Iterable<? extends C> iterable) {
        if (iterable instanceof Collection<?>) {
            return collection.addAll((Collection<? extends C>) iterable);
        }
        return addAll(collection, iterable.iterator());
    }

    /**
     * Adds all elements in the iteration to the given collection.
     *
     * @param collection
     *            the collection to add to, must not be null
     * @param iterator
     *            the iterator of elements to add, must not be null
     * @return a boolean indicating whether the collection has changed or not.
     * @throws NullPointerException
     *             if the collection or iterator is null
     */
    public static <C> boolean addAll(Collection<C> collection, Iterator<? extends C> iterator) {
        boolean changed = false;
        while (iterator.hasNext()) {
            changed |= collection.add(iterator.next());
        }
        return changed;
    }

    /**
     * Adds all elements in the enumeration to the given collection.
     *
     * @param collection  the collection to add to, must not be null
     * @param enumeration  the enumeration of elements to add, must not be null
     * @throws NullPointerException if the collection or enumeration is null
     */
    public static <C> boolean addAll(Collection<C> collection, Enumeration<? extends C> enumeration) {
        boolean changed = false;
        while (enumeration.hasMoreElements()) {
            changed |= collection.add(enumeration.nextElement());
        }
        return changed;
    }

    /**
     * Adds all elements in the array to the given collection.
     *
     * @param collection
     *            the collection to add to, must not be null
     * @param elements
     *            the array of elements to add, must not be null
     * @throws NullPointerException
     *             if the collection or array is null
     */
    public static <C> boolean addAll(Collection<C> collection, C[] elements) {
        boolean changed = false;
        for (C element : elements) {
            changed |= collection.add(element);
        }
        return changed;
    }

    /**
     * Returns the <code>index</code>-th value in {@link Iterator}, throwing
     * <code>IndexOutOfBoundsException</code> if there is no such element.
     * The Iterator is advanced to
     *      <code>index</code> (or to the end, if <code>index</code> exceeds the
     *      number of entries) as a side effect of this method.</li>
     *
     * @param iterator  the iterator to get a value from
     * @param index  the index to get
     * @param <T> the type of object in the {@link Iterator}
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @throws IllegalArgumentException if the object type is invalid
     */
    public static <T> T get(Iterator<T> iterator, int index) {
        int i = index;
        checkIndexBounds(i);
            while (iterator.hasNext()) {
                i--;
                if (i == -1) {
                    return iterator.next();
                }
                iterator.next();
            }
            throw new IndexOutOfBoundsException("Entry does not exist: " + i);
    }

    /**
     * Ensures an index is not negative.
     * @param index the index to check.
     * @throws IndexOutOfBoundsException if the index is negative.
     */
    private static void checkIndexBounds(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
    }

    /**
     * Returns the <code>index</code>-th value in the <code>iterable</code>'s {@link Iterator}, throwing
     * <code>IndexOutOfBoundsException</code> if there is no such element.
     * <p>
     * If the {@link Iterable} is a {@link List}, then it will use {@link List#get(int)}.
     *
     * @param iterable  the {@link Iterable} to get a value from
     * @param index  the index to get
     * @param <T> the type of object in the {@link Iterable}.
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public static <T> T get(Iterable<T> iterable, int index) {
        checkIndexBounds(index);
        if (iterable instanceof List<?>) {
            return ((List<T>) iterable).get(index);
        }
        return get(iterable.iterator(), index);
    }

    /**
     * Returns the <code>index</code>-th value in <code>object</code>, throwing
     * <code>IndexOutOfBoundsException</code> if there is no such element or
     * <code>IllegalArgumentException</code> if <code>object</code> is not an
     * instance of one of the supported types.
     * <p>
     * The supported types, and associated semantics are:
     * <ul>
     * <li> Map -- the value returned is the <code>Map.Entry</code> in position
     *      <code>index</code> in the map's <code>entrySet</code> iterator,
     *      if there is such an entry.</li>
     * <li> List -- this method is equivalent to the list's get method.</li>
     * <li> Array -- the <code>index</code>-th array entry is returned,
     *      if there is such an entry; otherwise an <code>IndexOutOfBoundsException</code>
     *      is thrown.</li>
     * <li> Collection -- the value returned is the <code>index</code>-th object
     *      returned by the collection's default iterator, if there is such an element.</li>
     * <li> Iterator or Enumeration -- the value returned is the
     *      <code>index</code>-th object in the Iterator/Enumeration, if there
     *      is such an element.  The Iterator/Enumeration is advanced to
     *      <code>index</code> (or to the end, if <code>index</code> exceeds the
     *      number of entries) as a side effect of this method.</li>
     * </ul>
     *
     * @param object  the object to get a value from
     * @param index  the index to get
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @throws IllegalArgumentException if the object type is invalid
     */
    public static Object get(Object object, int index) {
        int i = index;
        if (i < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + i);
        }
        if (object instanceof Map<?,?>) {
            Map<?, ?> map = (Map<?, ?>) object;
            Iterator<?> iterator = map.entrySet().iterator();
            return get(iterator, i);
        } else if (object instanceof Object[]) {
            return ((Object[]) object)[i];
        } else if (object instanceof Iterator<?>) {
            Iterator<?> it = (Iterator<?>) object;
            while (it.hasNext()) {
                i--;
                if (i == -1) {
                    return it.next();
                }
                it.next();
            }
            throw new IndexOutOfBoundsException("Entry does not exist: " + i);
        } else if (object instanceof Collection<?>) {
            Iterator<?> iterator = ((Collection<?>) object).iterator();
            return get(iterator, i);
        } else if (object instanceof Enumeration<?>) {
            Enumeration<?> it = (Enumeration<?>) object;
            while (it.hasMoreElements()) {
                i--;
                if (i == -1) {
                    return it.nextElement();
                } else {
                    it.nextElement();
                }
            }
            throw new IndexOutOfBoundsException("Entry does not exist: " + i);
        } else if (object == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
        } else {
            try {
                return Array.get(object, i);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }
    }

    /**
     * Returns the <code>index</code>-th <code>Map.Entry</code> in the <code>map</code>'s <code>entrySet</code>, throwing
     * <code>IndexOutOfBoundsException</code> if there is no such element.
     *
     * @param map  the object to get a value from
     * @param index  the index to get
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public static <K,V> Map.Entry<K, V> get(Map<K,V> map, int index) {
        checkIndexBounds(index);
        return get(map.entrySet(), index);
    }

    /**
     * Gets the size of the collection/iterator specified.
     * <p>
     * This method can handles objects as follows
     * <ul>
     * <li>Collection - the collection size
     * <li>Map - the map size
     * <li>Array - the array size
     * <li>Iterator - the number of elements remaining in the iterator
     * <li>Enumeration - the number of elements remaining in the enumeration
     * </ul>
     *
     * @param object  the object to get the size of, may be null
     * @return the size of the specified collection or 0 if the object was null
     * @throws IllegalArgumentException thrown if object is not recognised
     * @since 3.1
     */
    public static int size(Object object) {
        if (object == null) {
            return 0;
        }
        int total = 0;
        if (object instanceof Map<?,?>) {
            total = ((Map<?, ?>) object).size();
        } else if (object instanceof Collection<?>) {
            total = ((Collection<?>) object).size();
        } else if (object instanceof Object[]) {
            total = ((Object[]) object).length;
        } else if (object instanceof Iterator<?>) {
            Iterator<?> it = (Iterator<?>) object;
            while (it.hasNext()) {
                total++;
                it.next();
            }
        } else if (object instanceof Enumeration<?>) {
            Enumeration<?> it = (Enumeration<?>) object;
            while (it.hasMoreElements()) {
                total++;
                it.nextElement();
            }
        } else {
            try {
                total = Array.getLength(object);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }
        return total;
    }

    /**
     * Checks if the specified collection/array/iterator is empty.
     * <p>
     * This method can handles objects as follows
     * <ul>
     * <li>Collection - via collection isEmpty
     * <li>Map - via map isEmpty
     * <li>Array - using array size
     * <li>Iterator - via hasNext
     * <li>Enumeration - via hasMoreElements
     * </ul>
     * <p>
     * Note: This method is named to avoid clashing with
     * {@link #isEmpty(Collection)}.
     *
     * @param object  the object to get the size of, may be null
     * @return true if empty or null
     * @throws IllegalArgumentException thrown if object is not recognised
     * @since 3.2
     */
    public static boolean sizeIsEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        } else if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        } else if (object instanceof Object[]) {
            return ((Object[]) object).length == 0;
        } else if (object instanceof Iterator<?>) {
            return ((Iterator<?>) object).hasNext() == false;
        } else if (object instanceof Enumeration<?>) {
            return ((Enumeration<?>) object).hasMoreElements() == false;
        } else {
            try {
                return Array.getLength(object) == 0;
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Null-safe check if the specified collection is empty.
     * <p>
     * Null returns true.
     *
     * @param coll  the collection to check, may be null
     * @return true if empty or null
     * @since 3.2
     */
    public static boolean isEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * Null-safe check if the specified collection is not empty.
     * <p>
     * Null returns false.
     *
     * @param coll  the collection to check, may be null
     * @return true if non-null and non-empty
     * @since 3.2
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    //-----------------------------------------------------------------------
    /**
     * Reverses the order of the given array.
     *
     * @param array  the array to reverse
     */
    public static void reverseArray(Object[] array) {
        int i = 0;
        int j = array.length - 1;
        Object tmp;

        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * Returns true if no more elements can be added to the Collection.
     * <p>
     * This method uses the {@link BoundedCollection} interface to determine the
     * full status. If the collection does not implement this interface then
     * false is returned.
     * <p>
     * The collection does not have to implement this interface directly.
     * If the collection has been decorated using the decorators subpackage
     * then these will be removed to access the BoundedCollection.
     *
     * @param coll  the collection to check
     * @return true if the BoundedCollection is full
     * @throws NullPointerException if the collection is null
     */
    @SuppressWarnings("unchecked")
    public static boolean isFull(Collection<?> coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection<?>) coll).isFull();
        }
        try {
            BoundedCollection<?> bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection((Collection<Object>) coll);
            return bcoll.isFull();
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Get the maximum number of elements that the Collection can contain.
     * <p>
     * This method uses the {@link BoundedCollection} interface to determine the
     * maximum size. If the collection does not implement this interface then
     * -1 is returned.
     * <p>
     * The collection does not have to implement this interface directly.
     * If the collection has been decorated using the decorators subpackage
     * then these will be removed to access the BoundedCollection.
     *
     * @param coll  the collection to check
     * @return the maximum size of the BoundedCollection, -1 if no maximum size
     * @throws NullPointerException if the collection is null
     */
    @SuppressWarnings("unchecked")
    public static int maxSize(Collection<?> coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection<?>) coll).maxSize();
        }
        try {
            BoundedCollection<?> bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection((Collection<Object>) coll);
            return bcoll.maxSize();
        } catch (IllegalArgumentException ex) {
            return -1;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a collection containing all the elements in <code>collection</code>
     * that are also in <code>retain</code>. The cardinality of an element <code>e</code>
     * in the returned collection is the same as the cardinality of <code>e</code>
     * in <code>collection</code> unless <code>retain</code> does not contain <code>e</code>, in which
     * case the cardinality is zero. This method is useful if you do not wish to modify
     * the collection <code>c</code> and thus cannot call <code>c.retainAll(retain);</code>.
     *
     * @param collection  the collection whose contents are the target of the #retailAll operation
     * @param retain  the collection containing the elements to be retained in the returned collection
     * @return a <code>Collection</code> containing all the elements of <code>collection</code>
     * that occur at least once in <code>retain</code>.
     * @throws NullPointerException if either parameter is null
     * @since 3.2
     */
    public static <C> Collection<C> retainAll(Collection<C> collection, Collection<?> retain) {
        return ListUtils.retainAll(collection, retain);
    }

    /**
     * Removes the elements in <code>remove</code> from <code>collection</code>. That is, this
     * method returns a collection containing all the elements in <code>c</code>
     * that are not in <code>remove</code>. The cardinality of an element <code>e</code>
     * in the returned collection is the same as the cardinality of <code>e</code>
     * in <code>collection</code> unless <code>remove</code> contains <code>e</code>, in which
     * case the cardinality is zero. This method is useful if you do not wish to modify
     * the collection <code>c</code> and thus cannot call <code>collection.removeAll(remove);</code>.
     *
     * @param collection  the collection from which items are removed (in the returned collection)
     * @param remove  the items to be removed from the returned <code>collection</code>
     * @return a <code>Collection</code> containing all the elements of <code>collection</code> except
     * any elements that also occur in <code>remove</code>.
     * @throws NullPointerException if either parameter is null
     * @since 3.3 (method existed in 3.2 but was completely broken)
     */
    public static <E> Collection<E> removeAll(Collection<E> collection, Collection<?> remove) {
        return ListUtils.removeAll(collection, remove);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized collection backed by the given collection.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to
     * avoid non-deterministic behavior:
     *
     * <pre>
     * Collection c = CollectionUtils.synchronizedCollection(myCollection);
     * synchronized (c) {
     *     Iterator i = c.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     *
     * This method uses the implementation in the decorators subpackage.
     *
     * @param collection  the collection to synchronize, must not be null
     * @return a synchronized collection backed by the given collection
     * @throws IllegalArgumentException  if the collection is null
     */
    public static <C> Collection<C> synchronizedCollection(Collection<C> collection) {
        return SynchronizedCollection.synchronizedCollection(collection);
    }

    /**
     * Returns an unmodifiable collection backed by the given collection.
     * <p>
     * This method uses the implementation in the decorators subpackage.
     *
     * @param collection  the collection to make unmodifiable, must not be null
     * @return an unmodifiable collection backed by the given collection
     * @throws IllegalArgumentException  if the collection is null
     */
    public static <C> Collection<C> unmodifiableCollection(Collection<C> collection) {
        return UnmodifiableCollection.unmodifiableCollection(collection);
    }

    /**
     * Returns a predicated (validating) collection backed by the given collection.
     * <p>
     * Only objects that pass the test in the given predicate can be added to the collection.
     * Trying to add an invalid object results in an IllegalArgumentException.
     * It is important not to use the original collection after invoking this method,
     * as it is a backdoor for adding invalid objects.
     *
     * @param collection  the collection to predicate, must not be null
     * @param predicate  the predicate for the collection, must not be null
     * @param <C> the type of objects in the Collection.
     * @return a predicated collection backed by the given collection
     * @throws IllegalArgumentException  if the Collection is null
     */
    public static <C> Collection<C> predicatedCollection(Collection<C> collection, Predicate<? super C> predicate) {
        return PredicatedCollection.predicatedCollection(collection, predicate);
    }

    /**
     * Returns a transformed bag backed by the given collection.
     * <p>
     * Each object is passed through the transformer as it is added to the
     * Collection. It is important not to use the original collection after invoking this
     * method, as it is a backdoor for adding untransformed objects.
     * <p>
     * Existing entries in the specified collection will not be transformed.
     * If you want that behaviour, see {@link TransformedCollection#transformedCollection}.
     *
     * @param collection  the collection to predicate, must not be null
     * @param transformer  the transformer for the collection, must not be null
     * @return a transformed collection backed by the given collection
     * @throws IllegalArgumentException  if the Collection or Transformer is null
     */
    public static <E> Collection<E> transformingCollection(Collection<E> collection, Transformer<? super E, ? extends E> transformer) {
        return TransformedCollection.transformingCollection(collection, transformer);
    }

    /**
     * Extract the lone element of the specified Collection.
     * @param <E> collection type
     * @param collection to read
     * @return sole member of collection
     * @throws IllegalArgumentException if collection is null/empty or contains more than one element
     */
    public static <E> E extractSingleton(Collection<E> collection) {
        if (collection == null || collection.size() != 1) {
            throw new IllegalArgumentException("Can extract singleton only when collection size == 1");
        }
        return collection.iterator().next();
    }
}
