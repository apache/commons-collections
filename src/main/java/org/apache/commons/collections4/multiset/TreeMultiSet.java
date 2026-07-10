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
package org.apache.commons.collections4.multiset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.SortedMultiSet;

/**
 * Implements {@link SortedMultiSet}, using a {@link TreeMap} to provide the
 * data storage. This is the standard implementation of a sorted multiset.
 * <p>
 * Order will be maintained among the multiset members and can be viewed
 * through the iterator.
 * </p>
 * <p>
 * A {@code MultiSet} stores each object in the collection together with a
 * count of occurrences. Extra methods on the interface allow multiple copies
 * of an object to be added or removed at once.
 * </p>
 *
 * @param <E> The type held in the multiset
 * @since 4.6.0
 */
public class TreeMultiSet<E> extends AbstractMapMultiSet<E> implements SortedMultiSet<E>, Serializable {

    /** Serial version lock */
    private static final long serialVersionUID = 20260705L;

    /**
     * Constructs an empty {@link TreeMultiSet}.
     */
    public TreeMultiSet() {
        super(new TreeMap<>());
    }

    /**
     * Constructs a {@link TreeMultiSet} containing all the members of the
     * specified collection.
     *
     * @param coll the collection to copy into the multiset
     */
    public TreeMultiSet(final Collection<? extends E> coll) {
        this();
        addAll(coll);
    }

    /**
     * Constructs an empty multiset that maintains order on its unique representative
     * members according to the given {@link Comparator}.
     *
     * @param comparator the comparator to use
     */
    public TreeMultiSet(final Comparator<? super E> comparator) {
        super(new TreeMap<>(comparator));
    }

    /**
     * Constructs a multiset containing all the members of the given Iterable.
     *
     * @param iterable an iterable to copy into this multiset.
     * @since 4.6.0
     */
    public TreeMultiSet(final Iterable<? extends E> iterable) {
        super(new TreeMap<>(), iterable);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the object to be added does not implement
     * {@link Comparable} and the {@link TreeMultiSet} is using natural ordering
     * @throws NullPointerException if the specified key is null and this multiset uses
     * natural ordering, or its comparator does not permit null keys
     */
    @Override
    public int add(final E object, final int occurrences) {
        if (comparator() == null && !(object instanceof Comparable)) {
            Objects.requireNonNull(object, "object");
            throw new IllegalArgumentException("Objects of type " + object.getClass() + " cannot be added to " +
                                               "a naturally ordered TreeMultiSet as it does not implement Comparable");
        }
        return super.add(object, occurrences);
    }

    @Override
    public Comparator<? super E> comparator() {
        return getMap().comparator();
    }

    @Override
    public E first() {
        return getMap().firstKey();
    }

    @Override
    protected SortedMap<E, AbstractMapMultiSet.MutableInteger> getMap() {
        return (SortedMap<E, AbstractMapMultiSet.MutableInteger>) super.getMap();
    }

    @Override
    public E last() {
        return getMap().lastKey();
    }

    /**
     * Deserializes the multiset in using a custom routine.
     *
     * @param in  the input stream
     * @throws IOException if an error occurs while reading from the stream
     * @throws ClassNotFoundException if an object read from the stream cannot be loaded
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        @SuppressWarnings("unchecked")  // This will fail at runtime if the stream is incorrect
        final Comparator<? super E> comp = (Comparator<? super E>) in.readObject();
        setMap(new TreeMap<>(comp));
        super.doReadObject(in);
    }

    /**
     * Serializes this object to an ObjectOutputStream.
     *
     * @param out the target ObjectOutputStream.
     * @throws IOException thrown when an I/O errors occur writing to the target stream.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(comparator());
        super.doWriteObject(out);
    }

}
