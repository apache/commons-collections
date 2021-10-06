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
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;

/**
 * The class that performs hashing on demand.
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
        Objects.requireNonNull( hashers, "hashers");
        this.hashers = new ArrayList<>(hashers);
    }

    /**
     * Constructor.
     *
     * @param function the function to use.
     * @param buffers the byte buffers that will be hashed.
     */
    public HasherCollection(Hasher... hashers) {
        this( Arrays.asList(hashers));
    }

    /**
     * Adds a hasher to the collection.
     * @param hasher The hasher to add.
     */
    public void add(Hasher hasher) {
        Objects.requireNonNull( hasher, "hasher");
        hashers.add(hasher);
    }

    /**
     * Add all the Hashers in a collection to this HasherCollection.
     * @param hashers The hashers to add.
     */
    public void add(Collection<Hasher> hashers) {
        Objects.requireNonNull( hashers, "hashers");
        hashers.addAll(hashers);
    }

    @Override
    public IndexProducer indices(final Shape shape) {
        Objects.requireNonNull( shape, "shape");
        return new IndexProducer() {
            @Override
            public void forEachIndex(IntConsumer consumer) {
                for (Hasher hasher : hashers) {
                    hasher.indices( shape ).forEachIndex(consumer);
                }
            }
        };
    }

    @Override
    public int size() {
        int i = 0;
        for (Hasher h : hashers )
        {
            i += h.size();
        }
        return i;
    }

    @Override
    public void forEach(Consumer<Hasher> consumer) {
        Objects.requireNonNull( consumer, "consumer");
        for (Hasher h : this.hashers) {
            h.forEach(consumer);
        }
    }
}
