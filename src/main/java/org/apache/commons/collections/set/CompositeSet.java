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
package org.apache.commons.collections.set;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.collection.CompositeCollection;

/**
 * Decorates a set of other sets to provide a single unified view.
 * <p>
 * Changes made to this set will actually be made on the decorated set.
 * Add operations require the use of a pluggable strategy.
 * If no strategy is provided then add is unsupported.
 *
 * @since 3.0
 * @version $Id$
 */
public class CompositeSet<E> extends CompositeCollection<E> implements Set<E> {

    /** Serialization version */
    private static final long serialVersionUID = 5185069727540378940L;

    /**
     * Create an empty CompositeSet
     */
    public CompositeSet() {
        super();
    }

    /**
     * Create a CompositeSet with just <code>set</code> composited.
     *
     * @param set  the initial set in the composite
     */
    public CompositeSet(Set<E> set) {
        super(set);
    }

    /**
     * Create a composite set with sets as the initial set of composited Sets.
     * 
     * @param sets  the initial sets in the composite
     */
    public CompositeSet(Set<E>... sets) {
        super(sets);
    }

    /**
     * Add a Set to this composite
     *
     * @param c Must implement Set
     * @throws IllegalArgumentException if c does not implement java.util.Set
     *         or if a SetMutator is set, but fails to resolve a collision
     * @throws UnsupportedOperationException if there is no SetMutator set, or
     *         a CollectionMutator is set instead of a SetMutator
     * @see org.apache.commons.collections.collection.CompositeCollection.CollectionMutator
     * @see SetMutator
     */
    @Override
    public synchronized void addComposited(Collection<E> c) {
        if (!(c instanceof Set)) {
            throw new IllegalArgumentException("Collections added must implement java.util.Set");
        }

        for (Set<E> set : getCollections()) {
            Collection<E> intersects = CollectionUtils.intersection(set, c);
            if (intersects.size() > 0) {
                if (this.mutator == null) {
                    throw new UnsupportedOperationException(
                        "Collision adding composited collection with no SetMutator set");
                }
                else if (!(this.mutator instanceof SetMutator)) {
                    throw new UnsupportedOperationException(
                        "Collision adding composited collection to a CompositeSet with a CollectionMutator " +
                        "instead of a SetMutator");
                }
                getMutator().resolveCollision(this, set, (Set<E>) c, intersects);
                if (CollectionUtils.intersection(set, c).size() > 0) {
                    throw new IllegalArgumentException(
                        "Attempt to add illegal entry unresolved by SetMutator.resolveCollision()");
                }
            }
        }
        super.addComposited(c);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<? extends Set<E>> getCollections() {
        return (List<Set<E>>) super.getCollections();
    }

    /**
     * Add two sets to this composite.
     *
     * @param c  the first {@link Set} to add to this composite
     * @param d  the second {@link Set} to add to this composite
     * @throws IllegalArgumentException if c or d does not implement java.util.Set
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized void addComposited(Collection<E> c, Collection<E> d) {
        if (!(c instanceof Set)) {
            throw new IllegalArgumentException("Argument must implement java.util.Set");
        }
        if (!(d instanceof Set)) {
            throw new IllegalArgumentException("Argument must implement java.util.Set");
        }
        this.addComposited(new Set[] { (Set<? extends E>) c, (Set<? extends E>) d });
    }

    /**
     * Add an array of sets to this composite
     * @param comps  the {@link Collection} of {@link Set}s to add to this composite
     * @throws IllegalArgumentException if any of the collections in comps do not implement Set
     */
    @Override
    public synchronized void addComposited(Collection<E>[] comps) {
        for (int i = comps.length - 1; i >= 0; --i) {
            this.addComposited(comps[i]);
        }
    }

    /**
     * This can receive either a {@link CompositeCollection#CollectionMutator} or a
     * {@link CompositeSet#SetMutator}. If a {@link CompositeCollection#CollectionMutator}
     * is used than conflicts when adding composited sets will throw IllegalArgumentException.
     * 
     * @param mutator  the {@link CollectionMutator} to use for this composite
     */
    @Override
    public void setMutator(CollectionMutator<E> mutator) {
        super.setMutator(mutator);
    }

    /* Set operations */

    /**
     * If a <code>CollectionMutator</code> is defined for this CompositeSet then this
     * method will be called anyway.
     *
     * @param obj  object to be removed
     * @return true if the object is removed, false otherwise
     */
    @Override
    public boolean remove(Object obj) {
        for (Set<? extends E> set : getCollections()) {
            if (set.contains(obj)) {
                return set.remove(obj);
            }
        }
        return false;
    }

    /**
     * @see Set#equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Set) {
            Set<?> set = (Set<?>) obj;
            return set.containsAll(this) && set.size() == this.size();
        }
        return false;
    }

    /**
     * @see Set#hashCode
     */
    @Override
    public int hashCode() {
        int code = 0;
        for (E e : this) {
            code += e == null ? 0 : e.hashCode();
        }
        return code;
    }

    @Override
    protected SetMutator<E> getMutator() {
        return (SetMutator<E>) super.getMutator();
    }

    /**
     * Define callbacks for mutation operations.
     * <p>
     * Defining remove() on implementations of SetMutator is pointless
     * as they are never called by CompositeSet.
     */
    public static interface SetMutator<E> extends CompositeCollection.CollectionMutator<E> {

        /**
         * Called when a Set is added to the CompositeSet and there is a
         * collision between existing and added sets.
         * <p>
         * If <code>added</code> and <code>existing</code> still have any intersects
         * after this method returns an IllegalArgumentException will be thrown.
         *
         * @param comp  the CompositeSet being modified
         * @param existing  the Set already existing in the composite
         * @param added  the Set being added to the composite
         * @param intersects  the intersection of the existing and added sets
         */
        public void resolveCollision(CompositeSet<E> comp, Set<E> existing, Set<E> added, Collection<E> intersects);
    }
}
