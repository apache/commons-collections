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
package org.apache.commons.collections4.bloomfilter.hasher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;

/**
 * A collection of Hashers.  Useful when the generation of a Bloom filter depends upon
 * multiple items.
 *
 * Hashers for each item are added to the HasherCollection and then
 * the collection is used wherever a Hasher can be used in the API.
 *
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
        return new IndexProducer() {
            @Override
            public void forEachIndex(IntConsumer consumer) {
                for (Hasher hasher : hashers) {
                    hasher.indices(shape).forEachIndex(consumer);
                }
            }
        };
    }

    /**
     * Allow child classes access to the hashers.
     * @return hashers
     */
    protected List<Hasher> getHashers() {
        return hashers;
    }

    @Override
    public int size() {
        int i = 0;
        for (Hasher h : hashers) {
            i += h.size();
        }
        return i;
    }
}
