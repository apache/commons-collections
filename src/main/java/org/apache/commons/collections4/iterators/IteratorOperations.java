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

package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Extends {@link Iterator} with additional default methods.
 *
 * @param <E> the type of elements returned by this iterator.
 * @since 4.5.0-M3
 */
public interface IteratorOperations<E> extends Iterator<E> {

    /**
     * Adds the remaining elements in the iterator to an arbitrary {@link Collection}. This method consumes the iterator.
     *
     * @param collection The target collection to add elements to.
     * @return the given {@code collection}.
     * @param <C> A collection of objects of type {@code <E>}.
     */
    default <C extends Collection<E>> C addTo(final C collection) {
        forEachRemaining(collection::add);
        return collection;
    }

    /**
     * Returns the next item and removes it from the iterator.
     *
     * @return the next item from the iterator.
     */
    default E removeNext() {
        final E result = next();
        remove();
        return result;
    }

    /**
     * Adds the remaining elements in the iterator to a new {@link Collection} provided by the supplier. This method consumes the iterator.
     *
     * @param collectionSupplier supplies a collection target.
     * @param <C> the collection type.
     * @return a new Collection containing the remaining elements of this instance.
     */
    default <C extends Collection<E>> C toCollection(final Supplier<C> collectionSupplier) {
        return addTo(collectionSupplier.get());
    }

    /**
     * Adds the remaining elements in the iterator to a new {@link List}. This method consumes the iterator.
     *
     * @return a new List containing the remaining elements of this instance.
     */
    default List<E> toList() {
        return toCollection(ArrayList::new);
    }

    /**
     * Adds the remaining elements in the iterator to a new {@link Set}. This method consumes the iterator.
     *
     * @return a new Set containing the remaining elements of this instance.
     */
    default Set<E> toSet() {
        return toCollection(HashSet::new);
    }

}
