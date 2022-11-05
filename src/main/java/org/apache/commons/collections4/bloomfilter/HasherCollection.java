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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;

/**
 * A collection of Hashers. Useful when the generation of a Bloom filter depends upon
 * multiple items.
 * <p>
 * Hashers for each item are added to the HasherCollection and then
 * the collection is used wherever a Hasher can be used in the API.
 * </p>
 * @since 4.5
 */
public class HasherCollection implements Hasher {

    /**
     * The list of hashers to be used to generate the indices.
     */
    private final List<Hasher> hashers;

    /**
     * Constructs an empty HasherCollection.
     */
    public HasherCollection() {
        this.hashers = new ArrayList<>();
    }

    /**
     * Constructs a HasherCollection from a collection of Hasher objects.
     *
     * @param hashers A collections of Hashers to build the indices with.
     */
    public HasherCollection(final Collection<Hasher> hashers) {
        Objects.requireNonNull(hashers, "hashers");
        this.hashers = new ArrayList<>(hashers);
    }

    /**
     * Constructor.
     *
     * @param hashers A list of Hashers to initialize the collection with.
     */
    public HasherCollection(Hasher... hashers) {
        this(Arrays.asList(hashers));
    }

    /**
     * Adds a hasher to the collection.
     * @param hasher The hasher to add.
     */
    public void add(Hasher hasher) {
        Objects.requireNonNull(hasher, "hasher");
        hashers.add(hasher);
    }

    /**
     * Add all the Hashers in a collection to this HasherCollection.
     * @param hashers The hashers to add.
     */
    public void add(Collection<Hasher> hashers) {
        Objects.requireNonNull(hashers, "hashers");
        this.hashers.addAll(hashers);
    }

    @Override
    public IndexProducer indices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        return new HasherCollectionIndexProducer(shape);
    }

    /**
     * Creates an IndexProducer comprising the unique indices from each of the contained
     * hashers.
     *
     * <p>This method may return duplicates if the collection of unique values from each of the contained
     * hashers contain duplicates. This is equivalent to creating Bloom filters for each contained hasher
     * and returning an IndexProducer with the concatenated output indices from each filter.</p>
     *
     * <p>A BitCountProducer generated from this IndexProducer is equivalent to a BitCountProducer from a
     * counting Bloom filter that was constructed from the contained hashers unique indices.<p>
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the iterator of integers
     */
    @Override
    public IndexProducer uniqueIndices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        return new HasherCollectionIndexProducer(shape) {
            @Override
            public boolean forEachIndex(IntPredicate consumer) {
                for (Hasher hasher : hashers) {
                    if (!hasher.uniqueIndices(shape).forEachIndex(consumer)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    /**
     * Creates an IndexProducer comprising the unique indices across all the contained
     * hashers.
     *
     * <p>This is equivalent to an IndexProducer created from a Bloom filter that comprises all
     * the contained hashers.</p>
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the iterator of integers
     */
    public IndexProducer absoluteUniqueIndices(final Shape shape) {
        int kCount = hashers.size() > 0 ? hashers.size() : 1;
        return consumer -> {
            Objects.requireNonNull(consumer, "consumer");
            // shape must handle maximum unique indices
            return uniqueIndices(shape).forEachIndex(IndexFilter.create(
                    Shape.fromKM(shape.getNumberOfHashFunctions() * kCount,
                                 shape.getNumberOfBits()), consumer));
        };
    }

    /**
     * Allow child classes access to the hashers.
     * @return hashers
     */
    protected List<Hasher> getHashers() {
        return Collections.unmodifiableList(hashers);
    }

    /**
     * IndexProducer that will return duplicates from the collection.
     */
    private class HasherCollectionIndexProducer implements IndexProducer {
        private final Shape shape;

        /**
         * Create an instance.
         *
         * @param shape The shape for the filter.
         */
        HasherCollectionIndexProducer(Shape shape) {
            this.shape = shape;
        }

        @Override
        public boolean forEachIndex(IntPredicate consumer) {
            for (Hasher hasher : hashers) {
                if (!hasher.indices(shape).forEachIndex(consumer)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int[] asIndexArray() {
            int[] result = new int[shape.getNumberOfHashFunctions() * hashers.size()];
            int[] idx = new int[1];

            // This method needs to return duplicate indices

            forEachIndex(i -> {
                result[idx[0]++] = i;
                return true;
            });
            return Arrays.copyOf(result, idx[0]);
        }
    }
}
