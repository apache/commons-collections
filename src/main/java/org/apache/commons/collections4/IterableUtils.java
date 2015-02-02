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
package org.apache.commons.collections4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.collections4.iterators.CollatingIterator;

/**
 * Provides utility methods and decorators for {@link Iterable} instances.
 *
 * @since 4.1
 * @version $Id$
 */
public class IterableUtils {

    /**
     * Default prefix used while converting Iterable to its String representation
     */
    private static final String DEFAULT_TOSTRING_PREFIX = "[";

    /**
     * Default suffix used while converting Iterable to its String representation
     */
    private static final String DEFAULT_TOSTRING_SUFFIX = "]";

    /**
     * Default delimiter used to delimit each Iterable element 
     * while converting Iterable to its String representation
     */
    private static final String DEFAULT_TOSTRING_DELIMITER = ",";

    /**
     * Helper class to easily access cardinality properties of two iterables.
     * @param <O>  the element type
     * @since 4.1
     */
    private static class CardinalityHelper<O> {

        /** Contains the cardinality for each object in iterable A. */
        final Map<O, Integer> cardinalityA;

        /** Contains the cardinality for each object in iterable B. */
        final Map<O, Integer> cardinalityB;

        /**
         * Create a new CardinalityHelper for two iterables.
         * @param a  the first iterable
         * @param b  the second iterable
         */
        public CardinalityHelper(final Iterable<? extends O> a, final Iterable<? extends O> b) {
            cardinalityA = IterableUtils.<O> getCardinalityMap(a);
            cardinalityB = IterableUtils.<O> getCardinalityMap(b);
        }

        /**
         * Returns the maximum frequency of an object.
         * @param obj  the object
         * @return the maximum frequency of the object
         */
        public final int max(final Object obj) {
            return Math.max(freqA(obj), freqB(obj));
        }

        /**
         * Returns the minimum frequency of an object.
         * @param obj  the object
         * @return the minimum frequency of the object
         */
        public final int min(final Object obj) {
            return Math.min(freqA(obj), freqB(obj));
        }

        /**
         * Returns the frequency of this object in iterable A.
         * @param obj  the object
         * @return the frequency of the object in iterable A
         */
        public int freqA(final Object obj) {
            return getFreq(obj, cardinalityA);
        }

        /**
         * Returns the frequency of this object in iterable B.
         * @param obj  the object
         * @return the frequency of the object in iterable B
         */
        public int freqB(final Object obj) {
            return getFreq(obj, cardinalityB);
        }

        private final int getFreq(final Object obj, final Map<?, Integer> freqMap) {
            final Integer count = freqMap.get(obj);
            if (count != null) {
                return count.intValue();
            }
            return 0;
        }
    }

    /**
     * Helper class for set-related operations, e.g. union, subtract, intersection.
     * @param <O>  the element type
     * @since 4.1
     */
    private static class SetOperationCardinalityHelper<O> extends CardinalityHelper<O> implements Iterable<O> {

        /** Contains the unique elements of the two iterables. */
        private final Set<O> elements;

        /** Output collection. */
        private final List<O> newList;

        /**
         * Create a new set operation helper from the two iterables.
         * @param a  the first iterable
         * @param b  the second iterable
         */
        public SetOperationCardinalityHelper(final Iterable<? extends O> a, final Iterable<? extends O> b) {
            super(a, b);
            elements = new HashSet<O>();
            CollectionUtils.addAll(elements, a);
            CollectionUtils.addAll(elements, b);
            // the resulting list must contain at least each unique element, but may grow
            newList = new ArrayList<O>(elements.size());
        }

        public Iterator<O> iterator() {
            return elements.iterator();
        }

        /**
         * Add the object {@code count} times to the result collection.
         * @param obj  the object to add
         * @param count  the count
         */
        public void setCardinality(final O obj, final int count) {
            for (int i = 0; i < count; i++) {
                newList.add(obj);
            }
        }

        /**
         * Returns the resulting collection.
         * @return the result
         */
        public Collection<O> list() {
            return newList;
        }

    }

    /**
     * Wraps another object and uses the provided Equator to implement
     * {@link #equals(Object)} and {@link #hashCode()}.
     * <p>
     * This class can be used to store objects into a Map.
     *
     * @param <O>  the element type
     * @since 4.1
     */
    private static class EquatorWrapper<O> {
        private final Equator<? super O> equator;
        private final O object;

        public EquatorWrapper(final Equator<? super O> equator, final O object) {
            this.equator = equator;
            this.object = object;
        }

        public O getObject() {
            return object;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof EquatorWrapper)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            final EquatorWrapper<O> otherObj = (EquatorWrapper<O>) obj;
            return equator.equate(object, otherObj.getObject());
        }

        @Override
        public int hashCode() {
            return equator.hash(object);
        }
    }

    /**
     * <code>IterableUtils</code> should not normally be instantiated.
     */
    private IterableUtils() {
    }

    /**
     * Returns a {@link Collection} containing the union of the given
     * {@link Iterable}s.
     * <p>
     * The cardinality of each element in the returned {@link Collection} will
     * be equal to the maximum of the cardinality of that element in the two
     * given {@link Iterable}s.
     *
     * @param a the first iterable, must not be null
     * @param b the second iterable, must not be null
     * @param <O> the generic type that is able to represent the types contained
     *        in both input iterables.
     * @return the union of the two iterables
     * @see Collection#addAll
     * @since 4.1
     */
    public static <O> Collection<O> union(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        final SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);
        for (final O obj : helper) {
            helper.setCardinality(obj, helper.max(obj));
        }
        return helper.list();
    }

    /**
     * Returns a {@link Collection} containing the intersection of the given
     * {@link Iterable}s.
     * <p>
     * The cardinality of each element in the returned {@link Collection} will
     * be equal to the minimum of the cardinality of that element in the two
     * given {@link Iterable}s.
     *
     * @param a the first iterable, must not be null
     * @param b the second iterable, must not be null
     * @param <O> the generic type that is able to represent the types contained
     *        in both input iterables.
     * @return the intersection of the two iterables
     * @see Collection#retainAll
     * @see #containsAny
     * @since 4.1
     */
    public static <O> Collection<O> intersection(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        final SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);
        for (final O obj : helper) {
            helper.setCardinality(obj, helper.min(obj));
        }
        return helper.list();
    }

    /**
     * Returns a {@link Collection} containing the exclusive disjunction
     * (symmetric difference) of the given {@link Iterable}s.
     * <p>
     * The cardinality of each element <i>e</i> in the returned
     * {@link Collection} will be equal to
     * <tt>max(cardinality(<i>e</i>,<i>a</i>),cardinality(<i>e</i>,<i>b</i>)) - min(cardinality(<i>e</i>,<i>a</i>),
     * cardinality(<i>e</i>,<i>b</i>))</tt>.
     * <p>
     * This is equivalent to
     * {@code {@link #subtract subtract}({@link #union union(a,b)},{@link #intersection intersection(a,b)})}
     * or
     * {@code {@link #union union}({@link #subtract subtract(a,b)},{@link #subtract subtract(b,a)})}.

     * @param a the first iterable, must not be null
     * @param b the second iterable, must not be null
     * @param <O> the generic type that is able to represent the types contained
     *        in both input iterables.
     * @return the symmetric difference of the two iterables
     * @since 4.1
     */
    public static <O> Collection<O> disjunction(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        final SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);
        for (final O obj : helper) {
            helper.setCardinality(obj, helper.max(obj) - helper.min(obj));
        }
        return helper.list();
    }

    /**
     * Returns {@code true} iff <i>a</i> is a sub-iterable of <i>b</i>,
     * that is, iff the cardinality of <i>e</i> in <i>a</i> is less than or
     * equal to the cardinality of <i>e</i> in <i>b</i>, for each element <i>e</i>
     * in <i>a</i>.
     *
     * @param a the first (sub?) iterable, must not be null
     * @param b the second (super?) iterable, must not be null
     * @return <code>true</code> iff <i>a</i> is a sub-iterable of <i>b</i>
     * @see #isProperSubIterable
     * @see Collection#containsAll
     * @since 4.1
     */
    public static boolean isSubIterable(final Iterable<?> a, final Iterable<?> b) {
        final CardinalityHelper<Object> helper = new CardinalityHelper<Object>(a, b);
        for (final Object obj : a) {
            if (helper.freqA(obj) > helper.freqB(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} iff the given {@link Iterable}s contain
     * exactly the same elements with exactly the same cardinalities.
     * <p>
     * That is, iff the cardinality of <i>e</i> in <i>a</i> is
     * equal to the cardinality of <i>e</i> in <i>b</i>,
     * for each element <i>e</i> in <i>a</i> or <i>b</i>.
     *
     * @param a  the first iterable, must not be null
     * @param b  the second iterable, must not be null
     * @return <code>true</code> iff the iterables contain the same elements with the same cardinalities.
     * @since 4.1
     */
    public static boolean isEqualIterable(final Iterable<?> a, final Iterable<?> b) {
        final CardinalityHelper<Object> helper = new CardinalityHelper<Object>(a, b);
        if (helper.cardinalityA.size() != helper.cardinalityB.size()) {
            return false;
        }
        for (final Object obj : helper.cardinalityA.keySet()) {
            if (helper.freqA(obj) != helper.freqB(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} iff the given {@link Iterable}s contain
     * exactly the same elements with exactly the same cardinalities.
     * <p>
     * That is, iff the cardinality of <i>e</i> in <i>a</i> is
     * equal to the cardinality of <i>e</i> in <i>b</i>,
     * for each element <i>e</i> in <i>a</i> or <i>b</i>.
     * <p>
     * <b>Note:</b> from version 4.1 onwards this method requires the input
     * iterables and equator to be of compatible type (using bounded wildcards).
     * Providing incompatible arguments (e.g. by casting to their rawtypes)
     * will result in a {@code ClassCastException} thrown at runtime.
     *
     * @param <E>  the element type
     * @param a  the first iterable, must not be null
     * @param b  the second iterable, must not be null
     * @param equator  the Equator used for testing equality
     * @return <code>true</code> iff the iterables contain the same elements with the same cardinalities.
     * @throws IllegalArgumentException if the equator is null
     * @since 4.1
     */
    public static <E> boolean isEqualIterable(final Iterable<? extends E> a, final Iterable<? extends E> b,
            final Equator<? super E> equator) {
        if (equator == null) {
            throw new IllegalArgumentException("equator may not be null");
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        final Transformer<E, ?> transformer = new Transformer() {
            public EquatorWrapper<?> transform(final Object input) {
                return new EquatorWrapper(equator, input);
            }
        };

        return isEqualIterable(collect(a, transformer), collect(b, transformer));
    }

    /**
     * Returns a new {@link Collection} containing {@code <i>a</i> - <i>b</i>}.
     * The cardinality of each element <i>e</i> in the returned {@link Collection}
     * will be the cardinality of <i>e</i> in <i>a</i> minus the cardinality
     * of <i>e</i> in <i>b</i>, or zero, whichever is greater.
     *
     * @param a  the iterable to subtract from, must not be null
     * @param b  the iterable to subtract, must not be null
     * @param <O> the generic type that is able to represent the types contained
     *        in both input iterables.
     * @return a new collection with the results
     * @see Collection#removeAll
     * @since 4.1
     */
    public static <O> Collection<O> subtract(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        final Predicate<O> p = TruePredicate.truePredicate();
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
     * @param a  the iterable to subtract from, must not be null
     * @param b  the iterable to subtract, must not be null
     * @param p  the condition used to determine which elements of <i>b</i> are
     *        subtracted.
     * @param <O> the generic type that is able to represent the types contained
     *        in both input iterables.
     * @return a new collection with the results
     * @since 4.1
     * @see Collection#removeAll
     */
    public static <O> Collection<O> subtract(final Iterable<? extends O> a, final Iterable<? extends O> b,
            final Predicate<O> p) {
        final ArrayList<O> list = new ArrayList<O>();
        final HashBag<O> bag = new HashBag<O>();
        for (final O element : b) {
            if (p.evaluate(element)) {
                bag.add(element);
            }
        }
        for (final O element : a) {
            if (!bag.remove(element, 1)) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * Returns a {@link Map} mapping each unique element in the given
     * {@link Iterable} to an {@link Integer} representing the number
     * of occurrences of that element in the {@link Iterable}.
     * <p>
     * Only those elements present in the iterable will appear as
     * keys in the map.
     *
     * @param <O>  the type of object in the returned {@link Map}. This is a super type of <I>.
     * @param iterable  the iterable to get the cardinality map for, must not be null
     * @return the populated cardinality map
     * @since 4.1
     */
    public static <O> Map<O, Integer> getCardinalityMap(final Iterable<? extends O> iterable) {
        final Map<O, Integer> count = new HashMap<O, Integer>();
        for (final O obj : iterable) {
            final Integer c = count.get(obj);
            if (c == null) {
                count.put(obj, Integer.valueOf(1));
            } else {
                count.put(obj, Integer.valueOf(c.intValue() + 1));
            }
        }
        return count;
    }

    /**
     * Returns the number of occurrences of <i>obj</i> in <i>iterable</i>.
     *
     * @param obj the object to find the cardinality of
     * @param iterable the {@link Iterable} to search
     * @param <O> the type of object that the {@link Iterable} may contain.
     * @return the the number of occurrences of obj in iterable
     * @since 4.1
     */
    public static <O> int cardinality(final O obj, final Iterable<? super O> iterable) {
        if (iterable instanceof Set<?>) {
            return ((Set<? super O>) iterable).contains(obj) ? 1 : 0;
        }
        if (iterable instanceof Bag<?>) {
            return ((Bag<? super O>) iterable).getCount(obj);
        }
        int count = 0;
        if (obj == null) {
            for (final Object element : iterable) {
                if (element == null) {
                    count++;
                }
            }
        } else {
            for (final Object element : iterable) {
                if (obj.equals(element)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Finds the first element in the given iterable which matches the given predicate.
     * <p>
     * If the input iterable or predicate is null, or no element of the iterable
     * matches the predicate, null is returned.
     *
     * @param <T>  the type of object the {@link Iterable} contains
     * @param iterable  the iterable to search, may be null
     * @param predicate  the predicate to use, may be null
     * @return the first element of the iterable which matches the predicate or null if none could be found
     * @since 4.1
     */
    public static <T> T find(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        if (iterable != null && predicate != null) {
            for (final T item : iterable) {
                if (predicate.evaluate(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Executes the given closure on each element in the iterable.
     * <p>
     * If the input iterable or closure is null, there is no change made.
     *
     * @param <T>  the type of object the {@link Iterable} contains
     * @param <C>  the closure type
     * @param iterable  the iterable to get the input from, may be null
     * @param closure  the closure to perform, may be null
     * @return closure
     * @since 4.1
     */
    public static <T, C extends Closure<? super T>> C forAllDo(final Iterable<T> iterable, final C closure) {
        if (iterable != null && closure != null) {
            for (final T element : iterable) {
                closure.execute(element);
            }
        }
        return closure;
    }

    /**
     * Executes the given closure on each but the last element in the iterable.
     * <p>
     * If the input iterable or closure is null, there is no change made.
     *
     * @param <T>  the type of object the {@link Iterable} contains
     * @param <C>  the closure type
     * @param iterable  the iterable to get the input from, may be null
     * @param closure  the closure to perform, may be null
     * @return the last element in the iterable, or null if either iterable or closure is null
     * @since 4.1
     */
    public static <T, C extends Closure<? super T>> T forAllButLastDo(final Iterable<T> iterable, final C closure) {
        return iterable != null && closure != null ? IteratorUtils.forAllButLastDo(iterable.iterator(), closure) : null;
    }

    /**
     * Filter the iterable by applying a Predicate to each element. If the
     * predicate returns false, remove the element.
     * <p>
     * If the input iterable or predicate is null, there is no change made.
     *
     * @param <T>  the type of object the {@link Iterable} contains
     * @param iterable  the iterable to get the input from, may be null
     * @param predicate  the predicate to use as a filter, may be null
     * @return true if the iterable is modified by this call, false otherwise.
     * @since 4.1
     */
    public static <T> boolean filter(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        boolean result = false;
        if (iterable != null && predicate != null) {
            for (final Iterator<T> it = iterable.iterator(); it.hasNext();) {
                if (!predicate.evaluate(it.next())) {
                    it.remove();
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Filter the iterable by applying a Predicate to each element. If the
     * predicate returns true, remove the element.
     * <p>
     * This is equivalent to <pre>filter(iterable, PredicateUtils.notPredicate(predicate))</pre>
     * if predicate is != null.
     * <p>
     * If the input iterable or predicate is null, there is no change made.
     *
     * @param <T>  the type of object the {@link Iterable} contains
     * @param iterable  the iterable to get the input from, may be null
     * @param predicate  the predicate to use as a filter, may be null
     * @return true if the iterable is modified by this call, false otherwise.
     * @since 4.1
     */
    public static <T> boolean filterInverse(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        return filter(iterable, predicate == null ? null : PredicateUtils.notPredicate(predicate));
    }

    /**
     * Counts the number of elements in the input iterable that match the
     * predicate.
     * <p>
     * A <code>null</code> iterable or predicate matches no elements.
     *
     * @param <C>  the type of object the {@link Iterable} contains
     * @param input  the {@link Iterable} to get the input from, may be null
     * @param predicate  the predicate to use, may be null
     * @return the number of matches for the predicate in the iterable
     * @since 4.1
     */
    public static <C> int countMatches(final Iterable<C> input, final Predicate<? super C> predicate) {
        int count = 0;
        if (input != null && predicate != null) {
            for (final C o : input) {
                if (predicate.evaluate(o)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Answers true if a predicate is true for at least one element of a
     * iterable.
     * <p>
     * A <code>null</code> iterable or predicate returns false.
     *
     * @param <C>  the type of object the {@link Iterable} contains
     * @param input  the {@link Iterable} to get the input from, may be null
     * @param predicate  the predicate to use, may be null
     * @return true if at least one element of the iterable matches the predicate
     * @since 4.1
     */
    public static <C> boolean exists(final Iterable<C> input, final Predicate<? super C> predicate) {
        if (input != null && predicate != null) {
            for (final C o : input) {
                if (predicate.evaluate(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Answers true if a predicate is true for every element of a
     * iterable.
     * <p>
     * A <code>null</code> predicate returns false.<br/>
     * A <code>null</code> or empty iterable returns true.
     *
     * @param <C>  the type of object the {@link Iterable} contains
     * @param input  the {@link Iterable} to get the input from, may be null
     * @param predicate  the predicate to use, may be null
     * @return true if every element of the iterable matches the predicate or if the
     * iterable is empty, false otherwise
     * @since 4.1
     */
    public static <C> boolean matchesAll(final Iterable<C> input, final Predicate<? super C> predicate) {
        if (predicate == null) {
            return false;
        }

        if (input != null) {
            for (final C o : input) {
                if (!predicate.evaluate(o)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Selects all elements from input iterable which match the given
     * predicate into an output collection.
     * <p>
     * A <code>null</code> predicate matches no elements.
     *
     * @param <O>  the type of object the {@link Iterable} contains
     * @param inputIterable  the iterable to get the input from, may not be null
     * @param predicate  the predicate to use, may be null
     * @return the elements matching the predicate (new list)
     * @throws NullPointerException if the input iterable is null
     * @since 4.1
     */
    public static <O> Collection<O> select(final Iterable<? extends O> inputIterable,
            final Predicate<? super O> predicate) {
        final Collection<O> answer = inputIterable instanceof Collection<?> ? new ArrayList<O>(
                ((Collection<?>) inputIterable).size()) : new ArrayList<O>();
        return select(inputIterable, predicate, answer);
    }

    /**
     * Selects all elements from input iterable which match the given
     * predicate and adds them to outputCollection.
     * <p>
     * If the input iterable or predicate is null, there is no change to the
     * output collection.
     *
     * @param <O>  the type of object the {@link Iterable} contains
     * @param <R>  the type of the output {@link Collection}
     * @param inputIterable  the iterable to get the input from, may be null
     * @param predicate  the predicate to use, may be null
     * @param outputCollection  the collection to output into, may not be null if the inputIterable
     *   and predicate or not null
     * @return the outputCollection
     * @since 4.1
     */
    public static <O, R extends Collection<? super O>> R select(final Iterable<? extends O> inputIterable,
            final Predicate<? super O> predicate, final R outputCollection) {
        if (inputIterable != null && predicate != null) {
            for (final O item : inputIterable) {
                if (predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
        return outputCollection;
    }

    /**
     * Selects all elements from inputIterable which don't match the given
     * predicate into an output collection.
     * <p>
     * If the input predicate is <code>null</code>, the result is an empty
     * list.
     *
     * @param <O>  the type of object the {@link Iterable} contains
     * @param inputIterable  the iterable to get the input from, may not be null
     * @param predicate  the predicate to use, may be null
     * @return the elements <b>not</b> matching the predicate (new list)
     * @throws NullPointerException if the input iterable is null
     * @since 4.1
     */
    public static <O> Collection<O> selectRejected(final Iterable<? extends O> inputIterable,
            final Predicate<? super O> predicate) {
        final Collection<O> answer = inputIterable instanceof Collection<?> ? new ArrayList<O>(
                ((Collection<?>) inputIterable).size()) : new ArrayList<O>();
        return selectRejected(inputIterable, predicate, answer);
    }

    /**
     * Selects all elements from inputIterable which don't match the given
     * predicate and adds them to outputCollection.
     * <p>
     * If the input predicate is <code>null</code>, no elements are added to
     * <code>outputCollection</code>.
     *
     * @param <O>  the type of object the {@link Iterable} contains
     * @param <R>  the type of the output {@link Collection}
     * @param inputIterable  the iterable to get the input from, may be null
     * @param predicate  the predicate to use, may be null
     * @param outputCollection  the collection to output into, may not be null if the inputIterable
     *   and predicate or not null
     * @return outputCollection
     * @since 4.1
     */
    public static <O, R extends Collection<? super O>> R selectRejected(final Iterable<? extends O> inputIterable,
            final Predicate<? super O> predicate, final R outputCollection) {

        if (inputIterable != null && predicate != null) {
            for (final O item : inputIterable) {
                if (!predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
        return outputCollection;
    }

    /**
     * Partitions all elements from inputIterable into separate output collections,
     * based on the evaluation of the given predicate.
     * <p>
     * For each predicate, the result will contain a list holding all elements of the
     * input iterable matching the predicate. The last list will hold all elements
     * which didn't match any predicate:
     * <pre>
     *  [C1, R] = partition(I, P1) with
     *  I = input iterable
     *  P1 = first predicate
     *  C1 = collection of elements matching P1
     *  R = collection of elements rejected by all predicates
     * </pre>
     * <p>
     * If the input iterable is <code>null</code>, an empty list will be returned.
     * If the input predicate is <code>null</code>, all elements of the input iterable
     * will be added to the rejected collection.
     * <p>
     * Example: for an input list [1, 2, 3, 4, 5] calling partition with a predicate [x &lt; 3]
     * will result in the following output: [[1, 2], [3, 4, 5]].
     *
     * @param <O>  the type of object the {@link Iterable} contains
     * @param inputIterable  the iterable to get the input from, may be null
     * @param predicate  the predicate to use, may be null
     * @return a list containing the output collections
     * @since 4.1
     */
    public static <O> List<List<O>> partition(final Iterable<? extends O> inputIterable,
            final Predicate<? super O> predicate) {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        // safe
        final Factory<List<O>> factory = FactoryUtils.instantiateFactory((Class) ArrayList.class);
        @SuppressWarnings("unchecked")
        // safe
        final Predicate<? super O>[] predicates = new Predicate[] { predicate };
        return partition(inputIterable, factory, predicates);
    }

    /**
     * Partitions all elements from inputIterable into an output and rejected collection,
     * based on the evaluation of the given predicate.
     * <p>
     * Elements matching the predicate are added to the <code>outputCollection</code>,
     * all other elements are added to the <code>rejectedCollection</code>.
     * <p>
     * If the input predicate is <code>null</code>, no elements are added to
     * <code>outputCollection</code> or <code>rejectedCollection</code>.
     * <p>
     * Note: calling the method is equivalent to the following code snippet:
     * <pre>
     *   select(inputIterable, predicate, outputCollection);
     *   selectRejected(inputIterable, predicate, rejectedCollection);
     * </pre>
     *
     * @param <O>  the type of object the {@link Iterable} contains
     * @param <R>  the type of the output {@link Collection}
     * @param inputIterable  the iterable to get the input from, may be null
     * @param predicate  the predicate to use, may be null
     * @param outputCollection  the collection to output selected elements into, may not be null if the
     *   inputIterable and predicate are not null
     * @param rejectedCollection  the collection to output rejected elements into, may not be null if the
     *   inputIterable or predicate are not null
     * @since 4.1
     */
    public static <O, R extends Collection<? super O>> void partition(final Iterable<? extends O> inputIterable,
            final Predicate<? super O> predicate, R outputCollection, R rejectedCollection) {

        if (inputIterable != null && predicate != null) {
            for (final O element : inputIterable) {
                if (predicate.evaluate(element)) {
                    outputCollection.add(element);
                } else {
                    rejectedCollection.add(element);
                }
            }
        }
    }

    /**
     * Partitions all elements from inputIterable into separate output collections,
     * based on the evaluation of the given predicates.
     * <p>
     * For each predicate, the result will contain a list holding all elements of the
     * input iterable matching the predicate. The last list will hold all elements
     * which didn't match any predicate:
     * <pre>
     *  [C1, C2, R] = partition(I, P1, P2) with
     *  I = input iterable
     *  P1 = first predicate
     *  P2 = second predicate
     *  C1 = collection of elements matching P1
     *  C2 = collection of elements matching P2
     *  R = collection of elements rejected by all predicates
     * </pre>
     * <p>
     * <b>Note</b>: elements are only added to the output collection of the first matching
     * predicate, determined by the order of arguments.
     * <p>
     * If the input iterable is <code>null</code>, an empty list will be returned.
     * If the input predicate is <code>null</code>, all elements of the input iterable
     * will be added to the rejected collection.
     * <p>
     * Example: for an input list [1, 2, 3, 4, 5] calling partition with predicates [x &lt; 3]
     * and [x &lt; 5] will result in the following output: [[1, 2], [3, 4], [5]].
     *
     * @param <O>  the type of object the {@link Iterable} contains
     * @param inputIterable  the iterable to get the input from, may be null
     * @param predicates  the predicates to use, may be null
     * @return a list containing the output collections
     * @since 4.1
     */
    public static <O> List<List<O>> partition(final Iterable<? extends O> inputIterable,
            final Predicate<? super O>... predicates) {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        // safe
        final Factory<List<O>> factory = FactoryUtils.instantiateFactory((Class) ArrayList.class);
        return partition(inputIterable, factory, predicates);
    }

    /**
     * Partitions all elements from inputIterable into separate output collections,
     * based on the evaluation of the given predicates.
     * <p>
     * For each predicate, the returned list will contain a collection holding
     * all elements of the input iterable matching the predicate. The last collection
     * contained in the list will hold all elements which didn't match any predicate:
     * <pre>
     *  [C1, C2, R] = partition(I, P1, P2) with
     *  I = input iterable
     *  P1 = first predicate
     *  P2 = second predicate
     *  C1 = collection of elements matching P1
     *  C2 = collection of elements matching P2
     *  R = collection of elements rejected by all predicates
     * </pre>
     * <p>
     * <b>Note</b>: elements are only added to the output collection of the first matching
     * predicate, determined by the order of arguments.
     * <p>
     * If the input iterable is <code>null</code>, an empty list will be returned.
     * If no predicates have been provided, all elements of the input iterable
     * will be added to the rejected collection.
     * <p>
     * Example: for an input list [1, 2, 3, 4, 5] calling partition with predicates [x &lt; 3]
     * and [x &lt; 5] will result in the following output: [[1, 2], [3, 4], [5]].
     *
     * @param <O>  the type of object the {@link Iterable} contains
     * @param <R>  the type of the output {@link Collection}
     * @param inputIterable  the iterable to get the input from, may be null
     * @param partitionFactory  the factory used to create the output collections
     * @param predicates  the predicates to use, may be empty
     * @return a list containing the output collections
     * @since 4.1
     */
    public static <O, R extends Collection<O>> List<R> partition(final Iterable<? extends O> inputIterable,
            final Factory<R> partitionFactory, final Predicate<? super O>... predicates) {

        if (inputIterable == null) {
            return Collections.emptyList();
        }

        if (predicates == null || predicates.length < 1) {
            // return the entire input iterable as a single partition
            final R singlePartition = partitionFactory.create();
            select(inputIterable, PredicateUtils.truePredicate(), singlePartition);
            return Collections.singletonList(singlePartition);
        }

        // create the empty partitions
        final int numberOfPredicates = predicates.length;
        final int numberOfPartitions = numberOfPredicates + 1;
        final List<R> partitions = new ArrayList<R>(numberOfPartitions);
        for (int i = 0; i < numberOfPartitions; ++i) {
            partitions.add(partitionFactory.create());
        }

        // for each element in inputIterable:
        // find the first predicate that evaluates to true.
        // if there is a predicate, add the element to the corresponding partition.
        // if there is no predicate, add it to the last, catch-all partition.
        for (final O element : inputIterable) {
            boolean elementAssigned = false;
            for (int i = 0; i < numberOfPredicates; ++i) {
                if (predicates[i].evaluate(element)) {
                    partitions.get(i).add(element);
                    elementAssigned = true;
                    break;
                }
            }

            if (!elementAssigned) {
                // no predicates evaluated to true
                // add element to last partition
                partitions.get(numberOfPredicates).add(element);
            }
        }

        return partitions;
    }

    /**
     * Transforms all elements from inputIterable with the given transformer
     * and adds them to the outputCollection.
     * <p>
     * If the input iterable or transformer is null, there is no change to the
     * output collection.
     *
     * @param <I> the type of object in the input iterable
     * @param <O> the type of object in the output collection
     * @param <R> the output type of the transformer - this extends O.
     * @param inputIterable  the iterable to get the input from, may be null
     * @param transformer  the transformer to use, may be null
     * @param outputCollection  the collection to output into, may not be null if the inputIterable
     *   and transformer are not null
     * @return the outputCollection with the transformed input added
     * @throws NullPointerException if the output collection is null and both, inputIterable and
     *   transformer are not null
     * @since 4.1
     */
    public static <I, O, R extends Collection<? super O>> R collect(final Iterable<? extends I> inputIterable,
            final Transformer<? super I, ? extends O> transformer, final R outputCollection) {
        if (inputIterable != null) {
            return IteratorUtils.collect(inputIterable.iterator(), transformer, outputCollection);
        }
        return outputCollection;
    }

    /**
     * Returns a new Collection consisting of the elements of inputIterable
     * transformed by the given transformer.
     * <p>
     * If the input transformer is null, the result is an empty list.
     *
     * @param <I> the type of object in the input iterable
     * @param <O> the type of object in the output collection
     * @param inputIterable  the iterable to get the input from, may not be null
     * @param transformer  the transformer to use, may be null
     * @return the transformed result (new list)
     * @throws NullPointerException if the input iterable is null
     * @since 4.1
     */
    public static <I, O> Collection<O> collect(final Iterable<I> inputIterable,
            final Transformer<? super I, ? extends O> transformer) {
        final Collection<O> answer = inputIterable instanceof Collection<?> ? new ArrayList<O>(
                ((Collection<?>) inputIterable).size()) : new ArrayList<O>();
        return collect(inputIterable, transformer, answer);
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
     * @since 4.1
     */
    public static <T> T get(final Iterable<T> iterable, final int index) {
        IteratorUtils.checkIndexBounds(index);
        if (iterable instanceof List<?>) {
            return ((List<T>) iterable).get(index);
        }
        return IteratorUtils.get(iterable.iterator(), index);
    }

    /**
     * Merges two sorted Iterables, a and b, into a single, sorted List
     * such that the natural ordering of the elements is retained.
     * <p>
     * Uses the standard O(n) merge algorithm for combining two sorted lists.
     *
     * @param <O>  the element type
     * @param a  the first iterable, must not be null
     * @param b  the second iterable, must not be null
     * @return a new sorted List, containing the elements of Iterable a and b
     * @throws IllegalArgumentException if either iterable is null
     * @since 4.1
     */
    public static <O extends Comparable<? super O>> List<O> collate(Iterable<? extends O> a, Iterable<? extends O> b) {
        return collate(a, b, ComparatorUtils.<O> naturalComparator(), true);
    }

    /**
     * Merges two sorted Iterables, a and b, into a single, sorted List
     * such that the natural ordering of the elements is retained.
     * <p>
     * Uses the standard O(n) merge algorithm for combining two sorted lists.
     *
     * @param <O>  the element type
     * @param a  the first iterable, must not be null
     * @param b  the second iterable, must not be null
     * @param includeDuplicates  if {@code true} duplicate elements will be retained, otherwise
     *   they will be removed in the output collection
     * @return a new sorted List, containing the elements of Iterable a and b
     * @throws IllegalArgumentException if either iterable is null
     * @since 4.1
     */
    public static <O extends Comparable<? super O>> List<O> collate(final Iterable<? extends O> a,
            final Iterable<? extends O> b, final boolean includeDuplicates) {
        return collate(a, b, ComparatorUtils.<O> naturalComparator(), includeDuplicates);
    }

    /**
     * Merges two sorted Iterables, a and b, into a single, sorted List
     * such that the ordering of the elements according to Comparator c is retained.
     * <p>
     * Uses the standard O(n) merge algorithm for combining two sorted lists.
     *
     * @param <O>  the element type
     * @param a  the first iterable, must not be null
     * @param b  the second iterable, must not be null
     * @param c  the comparator to use for the merge.
     * @return a new sorted List, containing the elements of Iterable a and b
     * @throws IllegalArgumentException if either iterable or the comparator is null
     * @since 4.1
     */
    public static <O> List<O> collate(final Iterable<? extends O> a, final Iterable<? extends O> b,
            final Comparator<? super O> c) {
        return collate(a, b, c, true);
    }

    /**
     * Merges two sorted Iterables, a and b, into a single, sorted List
     * such that the ordering of the elements according to Comparator c is retained.
     * <p>
     * Uses the standard O(n) merge algorithm for combining two sorted lists.
     *
     * @param <O>  the element type
     * @param a  the first iterable, must not be null
     * @param b  the second iterable, must not be null
     * @param c  the comparator to use for the merge.
     * @param includeDuplicates  if {@code true} duplicate elements will be retained, otherwise
     *   they will be removed in the output collection
     * @return a new sorted List, containing the elements of Iterable a and b
     * @throws IllegalArgumentException if either iterable or the comparator is null
     * @since 4.1
     */
    public static <O> List<O> collate(final Iterable<? extends O> a, final Iterable<? extends O> b,
            final Comparator<? super O> c, final boolean includeDuplicates) {

        if (a == null || b == null) {
            throw new IllegalArgumentException("The iterables must not be null");
        }
        if (c == null) {
            throw new IllegalArgumentException("The comparator must not be null");
        }

        // if both Iterables are a Collection, we can estimate the size
        final int totalSize = a instanceof Collection<?> && b instanceof Collection<?> ? Math.max(1,
                ((Collection<?>) a).size() + ((Collection<?>) b).size()) : 10;

        final Iterator<O> iterator = new CollatingIterator<O>(c, a.iterator(), b.iterator());
        if (includeDuplicates) {
            return IteratorUtils.toList(iterator, totalSize);
        } else {
            final ArrayList<O> mergedList = new ArrayList<O>(totalSize);

            O lastItem = null;
            while (iterator.hasNext()) {
                final O item = iterator.next();
                if (lastItem == null || !lastItem.equals(item)) {
                    mergedList.add(item);
                }
                lastItem = item;
            }

            mergedList.trimToSize();
            return mergedList;
        }
    }

    /**
     * Returns a collection containing all the elements in
     * <code>iterable</code> that are also in <code>retain</code>. The
     * cardinality of an element <code>e</code> in the returned collection is
     * the same as the cardinality of <code>e</code> in <code>iterable</code>
     * unless <code>retain</code> does not contain <code>e</code>, in which case
     * the cardinality is zero. This method is useful if you do not wish to
     * modify the iterable <code>c</code> and thus cannot call
     * <code>c.retainAll(retain);</code>.
     * <p>
     * Moreover this method uses an {@link Equator} instead of
     * {@link Object#equals(Object)} to determine the equality of the elements
     * in <code>iterable</code> and <code>retain</code>. Hence this method is
     * useful in cases where the equals behavior of an object needs to be
     * modified without changing the object itself.
     *
     * @param <E> the type of object the {@link Iterable} contains
     * @param iterable the iterable whose contents are the target of the {@code retainAll} operation
     * @param retain the iterable containing the elements to be retained in the returned collection
     * @param equator the Equator used for testing equality
     * @return a <code>Collection</code> containing all the elements of <code>iterable</code>
     * that occur at least once in <code>retain</code> according to the <code>equator</code>
     * @throws NullPointerException if any of the parameters is null
     * @since 4.1
     */
    public static <E> Collection<E> retainAll(final Iterable<E> iterable, final Iterable<? extends E> retain,
            final Equator<? super E> equator) {

        final Transformer<E, EquatorWrapper<E>> transformer = new Transformer<E, EquatorWrapper<E>>() {
            public EquatorWrapper<E> transform(E input) {
                return new EquatorWrapper<E>(equator, input);
            }
        };

        final Set<EquatorWrapper<E>> retainSet = collect(retain, transformer, new HashSet<EquatorWrapper<E>>());

        final List<E> list = new ArrayList<E>();
        for (final E element : iterable) {
            if (retainSet.contains(new EquatorWrapper<E>(equator, element))) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * Removes all elements in <code>remove</code> from <code>iterable</code>.
     * That is, this method returns a collection containing all the elements in
     * <code>iterable</code> that are not in <code>remove</code>. The
     * cardinality of an element <code>e</code> in the returned collection is
     * the same as the cardinality of <code>e</code> in <code>iterable</code>
     * unless <code>remove</code> contains <code>e</code>, in which case the
     * cardinality is zero. This method is useful if you do not wish to modify
     * the iterable <code>c</code> and thus cannot call
     * <code>collection.removeAll(remove)</code>.
     * <p>
     * Moreover this method uses an {@link Equator} instead of
     * {@link Object#equals(Object)} to determine the equality of the elements
     * in <code>iterable</code> and <code>remove</code>. Hence this method is
     * useful in cases where the equals behavior of an object needs to be
     * modified without changing the object itself.
     *
     * @param <E> the type of object the {@link Iterable} contains
     * @param iterable the iterable from which items are removed (in the returned collection)
     * @param remove the items to be removed from the returned collection
     * @param equator the Equator used for testing equality
     * @return a <code>Collection</code> containing all the elements of <code>iterable</code>
     * except any element that if equal according to the <code>equator</code>
     * @throws NullPointerException if any of the parameters is null
     * @since 4.1
     */
    public static <E> Collection<E> removeAll(final Iterable<E> iterable, final Iterable<? extends E> remove,
            final Equator<? super E> equator) {

        final Transformer<E, EquatorWrapper<E>> transformer = new Transformer<E, EquatorWrapper<E>>() {
            public EquatorWrapper<E> transform(E input) {
                return new EquatorWrapper<E>(equator, input);
            }
        };

        final Set<EquatorWrapper<E>> removeSet = collect(remove, transformer, new HashSet<EquatorWrapper<E>>());

        final List<E> list = new ArrayList<E>();
        for (final E element : iterable) {
            if (!removeSet.contains(new EquatorWrapper<E>(equator, element))) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * This method checks, if any of the elements in <code>iterable</code> is
     * equal to <code>object</code>. Object equality is tested with an
     * <code>equator</code> and not {@link Object#equals(Object)}.
     *
     * @param <E> the type of object the {@link Iterable} contains
     * @param iterable the iterable from which items are compared
     * @param object the object to compare with the iterable's entries
     * @param equator the equator to use to check, if the item if equal to any
     *        of the iterable's entries.
     * @return true if <code>object</code> is in <code>iterable</code>
     *         according to <code>equator</code>
     * @throws NullPointerException if any parameter is null
     * @since 4.1
     */
    public static <E> boolean contains(final Iterable<? extends E> iterable, final E object,
            final Equator<? super E> equator) {
        for (final E obj : iterable) {
            if (equator.equate(obj, object)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts the specified Iterable into a String representation.
     * <p>
     * Elements will be delimited by a comma. The resulting representation will
     * be surrounded by square brackets.
     * 
     * @param <C>
     *            iterable type
     * @param iterable
     *            the iterable to read
     * @return the iterable String representation
     * @throws IllegalArgumentException
     *             if iterable is null
     * @since 4.1
     */
    public static <C> String toString(Iterable<C> iterable) {
        return toString(iterable, new Transformer<C, String>() {
            public String transform(C input) {
                return input.toString();
            }
        }, DEFAULT_TOSTRING_DELIMITER, DEFAULT_TOSTRING_PREFIX, DEFAULT_TOSTRING_SUFFIX);
    }

    /**
     * Converts the specified Iterable into a String representation using the specified
     * Transformer in order to convert each Iterable element into its own String representation.
     * <p>
     * Elements will be delimited by a comma. The resulting representation will be surrounded by square brackets.
     * @param <C> iterable type
     * @param iterable the iterable to read
     * @param transformer the transformer used to convert each element into its own String representation
     * @return the iterable String representation
     * @throws IllegalArgumentException if iterable or transformer is null
     * @since 4.1
     */
    public static <C> String toString(Iterable<C> iterable, Transformer<C, String> transformer) {
        return toString(iterable, transformer, DEFAULT_TOSTRING_DELIMITER, DEFAULT_TOSTRING_PREFIX,
                DEFAULT_TOSTRING_SUFFIX);
    }

    /**
     * Converts the specified Iterable into a String representation using the specified
     * Transformer in order to convert each Iterable element into its own String representation.
     * <p>
     * Elements will be delimited by the specified delimiter. The resulting representation will be 
     * surrounded by the provided prefix and suffix.
     * @param <C> iterable type
     * @param iterable the iterable to read
     * @param transformer the transformer used to convert each element into its own String representation
     * @param delimiter the char sequence used to delimit each iterable element
     * @param prefix the iterable string representation prefix
     * @param suffix the iterable string representation suffix
     * @return the iterable String representation
     * @throws IllegalArgumentException if iterable, transformer, delimiter, prefix or suffix is null
     * @since 4.1
     */
    public static <C> String toString(Iterable<C> iterable, Transformer<C, String> transformer, String delimiter,
            String prefix, String suffix) {
        if (iterable == null) {
            throw new IllegalArgumentException("iterable may not be null");
        }
        if (transformer == null) {
            throw new IllegalArgumentException("transformer may not be null");
        }
        if (delimiter == null) {
            throw new IllegalArgumentException("delimiter may not be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("prefix may not be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("suffix may not be null");
        }
        StringBuilder stringBuilder = new StringBuilder(prefix);
        for (C element : iterable) {
            stringBuilder.append(transformer.transform(element));
            stringBuilder.append(delimiter);
        }
        if (stringBuilder.length() > prefix.length()) {
            stringBuilder.setLength(stringBuilder.length() - delimiter.length());
        }
        stringBuilder.append(suffix);
        return stringBuilder.toString();
    }
}
