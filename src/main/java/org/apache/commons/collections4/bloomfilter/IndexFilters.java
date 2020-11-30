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
package org.apache.commons.collections4.bloomfilter;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Contains functions to filter indexes.
 */
final class IndexFilters {
    /** Do not instantiate. */
    private IndexFilters() {
    }

    /**
     * Transfer all distinct indexes in the specified {@code hasher} generated for the
     * specified {@code shape} to the specified {@code consumer}. For example this
     * can be used to merge a {@link Hasher} representation of a Bloom filter into a
     * {@link BloomFilter} instance that does not naturally handle duplicate indexes.
     *
     * <p>This method is functionally equivalent to:
     *
     * <pre>
     *     final Set&lt;Integer&gt; distinct = new TreeSet&lt;&gt;();
     *     hasher.iterator(shape).forEachRemaining((Consumer&lt;Integer&gt;) i -&gt; {
     *         if (distinct.add(i)) {
     *             consumer.accept(i);
     *         }
     *     });
     * </pre>
     *
     * @param hasher the hasher
     * @param shape the shape
     * @param consumer the consumer to receive distinct indexes
     * @throws NullPointerException if the hasher, shape or action are null
     * @see Hasher#iterator(Shape)
     */
    static void distinctIndexes(final Hasher hasher, final Shape shape, final IntConsumer consumer) {
        Objects.requireNonNull(hasher, "hasher");
        Objects.requireNonNull(shape, "shape");
        Objects.requireNonNull(consumer, "consumer");

        // TODO
        // This function can be optimised based on the expected size
        // (number of indexes) of the hasher and the number of bits in the shape.
        //
        // A large size would benefit from a pre-allocated BitSet-type filter.
        // A very small size may be more efficient as a simple array of values
        // that have already been seen that is scanned for each new index.
        //
        // A default is to use a Set to filter distinct values. The choice of set
        // should be evaluated. A HashSet would be optimal if size is known.
        // A TreeSet has lower memory consumption and performance is not as
        // sensitive to knowing the size in advance.

        final Set<Integer> distinct = new TreeSet<>();
        hasher.iterator(shape).forEachRemaining((Consumer<Integer>) i -> {
            if (distinct.add(i)) {
                consumer.accept(i);
            }
        });
    }
}
