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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Defines a collection that counts the number of times an object appears in
 * the collection.
 * <p>
 * Suppose you have a Bag that contains {@code {a, a, b, c}}.
 * Calling {@link #getCount(Object)} on {@code a} would return 2, while
 * calling {@link #uniqueSet()} would return {@code {a, b, c}}.
 * </p>
 * <p>
 * <i>NOTE: This interface violates the {@link Collection} contract.</i>
 * The behavior specified in many of these methods is <i>not</i> the same
 * as the behavior specified by {@code Collection}.
 * The noncompliant methods are clearly marked with "(Violation)".
 * Exercise caution when using a bag as a {@code Collection}.
 * </p>
 * <p>
 * This violation resulted from the original specification of this interface.
 * In an ideal world, the interface would be changed to fix the problems, however
 * it has been decided to maintain backwards compatibility instead.
 * </p>
 *
 * @param <E> the type of elements in this bag
 * @since 2.0
 */
public interface Bag<E> extends Collection<E> {

    /**
     * Returns the number of occurrences (cardinality) of the given
     * object currently in the bag. If the object does not exist in the
     * bag, return 0.
     *
     * @param object  the object to search for
     * @return the number of occurrences of the object, zero if not found
     */
    int getCount(Object object);

    /**
     * <i>(Violation)</i>
     * Adds one copy of the specified object to the Bag.
     * <p>
     * If the object is already in the {@link #uniqueSet()} then increment its
     * count as reported by {@link #getCount(Object)}. Otherwise add it to the
     * {@link #uniqueSet()} and report its count as 1.
     * </p>
     * <p>
     * Since this method always increases the size of the bag,
     * according to the {@link Collection#add(Object)} contract, it
     * should always return {@code true}.  Since it sometimes returns
     * {@code false}, this method violates the contract.
     * </p>
     *
     * @param object  the object to add
     * @return {@code true} if the object was not already in the {@code uniqueSet}
     */
    @Override
    boolean add(E object);

    /**
     * Adds {@code nCopies} copies of the specified object to the Bag.
     * <p>
     * If the object is already in the {@link #uniqueSet()} then increment its
     * count as reported by {@link #getCount(Object)}. Otherwise add it to the
     * {@link #uniqueSet()} and report its count as {@code nCopies}.
     * </p>
     *
     * @param object  the object to add
     * @param nCopies  the number of copies to add
     * @return {@code true} if the object was not already in the {@code uniqueSet}
     */
    boolean add(E object, int nCopies);

    /**
     * <i>(Violation)</i>
     * Removes all occurrences of the given object from the bag.
     * <p>
     * This will also remove the object from the {@link #uniqueSet()}.
     * </p>
     * <p>
     * According to the {@link Collection#remove(Object)} method,
     * this method should only remove the <i>first</i> occurrence of the
     * given object, not <i>all</i> occurrences.
     * </p>
     *
     * @param object  the object to remove
     * @return {@code true} if this call changed the collection
     */
    @Override
    boolean remove(Object object);

    /**
     * Removes {@code nCopies} copies of the specified object from the Bag.
     * <p>
     * If the number of copies to remove is greater than the actual number of
     * copies in the Bag, no error is thrown.
     * </p>
     *
     * @param object  the object to remove
     * @param nCopies  the number of copies to remove
     * @return {@code true} if this call changed the collection
     */
    boolean remove(Object object, int nCopies);

    /**
     * Returns a {@link Set} of unique elements in the Bag.
     * <p>
     * Uniqueness constraints are the same as those in {@link java.util.Set}.
     * </p>
     *
     * @return the Set of unique Bag elements
     */
    Set<E> uniqueSet();

    /**
     * Returns the total number of items in the bag across all types.
     *
     * @return the total size of the Bag
     */
    @Override
    int size();

    /**
     * <i>(Violation)</i>
     * Returns {@code true} if the bag contains all elements in
     * the given collection, respecting cardinality.  That is, if the
     * given collection {@code coll} contains {@code n} copies
     * of a given object, calling {@link #getCount(Object)} on that object must
     * be {@code &gt;= n} for all {@code n} in {@code coll}.
     *
     * <p>
     * The {@link Collection#containsAll(Collection)} method specifies
     * that cardinality should <i>not</i> be respected; this method should
     * return true if the bag contains at least one of every object contained
     * in the given collection.
     * </p>
     *
     * @param coll  the collection to check against
     * @return {@code true} if the Bag contains all the collection
     */
    @Override
    boolean containsAll(Collection<?> coll);

    /**
     * <i>(Violation)</i>
     * Remove all elements represented in the given collection,
     * respecting cardinality.  That is, if the given collection
     * {@code coll} contains {@code n} copies of a given object,
     * the bag will have {@code n} fewer copies, assuming the bag
     * had at least {@code n} copies to begin with.
     *
     * <p>
     * The {@link Collection#removeAll(Collection)} method specifies
     * that cardinality should <i>not</i> be respected; this method should
     * remove <i>all</i> occurrences of every object contained in the
     * given collection.
     * </p>
     *
     * @param coll  the collection to remove
     * @return {@code true} if this call changed the collection
     */
    @Override
    boolean removeAll(Collection<?> coll);

    /**
     * <i>(Violation)</i>
     * Remove any members of the bag that are not in the given
     * collection, respecting cardinality.  That is, if the given
     * collection {@code coll} contains {@code n} copies of a
     * given object and the bag has {@code m &gt; n} copies, then
     * delete {@code m - n} copies from the bag.  In addition, if
     * {@code e} is an object in the bag but
     * {@code !coll.contains(e)}, then remove {@code e} and any
     * of its copies.
     *
     * <p>
     * The {@link Collection#retainAll(Collection)} method specifies
     * that cardinality should <i>not</i> be respected; this method should
     * keep <i>all</i> occurrences of every object contained in the
     * given collection.
     * </p>
     *
     * @param coll  the collection to retain
     * @return {@code true} if this call changed the collection
     */
    @Override
    boolean retainAll(Collection<?> coll);

    /**
     * Returns an {@link Iterator} over the entire set of members,
     * including copies due to cardinality. This iterator is fail-fast
     * and will not tolerate concurrent modifications.
     *
     * @return iterator over all elements in the Bag
     */
    @Override
    Iterator<E> iterator();

    // The following is not part of the formal Bag interface, however where possible
    // Bag implementations should follow these comments.
//    /**
//     * Compares this Bag to another.
//     * This Bag equals another Bag if it contains the same number of occurrences of
//     * the same elements.
//     * This equals definition is compatible with the Set interface.
//     *
//     * @param obj  the Bag to compare to
//     * @return true if equal
//     */
//    boolean equals(Object obj);
//
//    /**
//     * Gets a hash code for the Bag compatible with the definition of equals.
//     * The hash code is defined as the sum total of a hash code for each element.
//     * The per element hash code is defined as
//     * {@code (e==null ? 0 : e.hashCode()) ^ noOccurances)}.
//     * This hash code definition is compatible with the Set interface.
//     *
//     * @return the hash code of the Bag
//     */
//    int hashCode();

}
