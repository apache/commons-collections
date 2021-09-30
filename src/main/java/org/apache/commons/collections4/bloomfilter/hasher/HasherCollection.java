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
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.stream.Collectors;

/**
 * The class that performs hashing on demand.
 * @since 4.5
 */
public class HasherCollection implements Hasher {


    /**
     * The list of hashers to be used to generate the iterator.
     * Package private for access by the iterator.
     */
    final List<Hasher> hashers;

    /**
     * Constructs an empty HasherCollection.
     */
    public HasherCollection() {
        this.hashers = new ArrayList<>();
    }

    /**
     * Constructs a DynamicHasher.
     *
     * @param hashers A collections of Hashers to build the iterator with.
     */
    public HasherCollection(final Collection<Hasher> hashers) {
        this.hashers = new ArrayList<>(hashers);
    }

    /**
     * Constructs a DynamicHasher.
     *
     * @param function the function to use.
     * @param buffers the byte buffers that will be hashed.
     */
    public HasherCollection(Hasher... hashers) {
        this( Arrays.asList(hashers));
    }

    public void add(Hasher hasher) {
        hashers.add(hasher);
    }

    public void add(Collection<Hasher> hashers) {
        hashers.addAll(hashers);
    }

    @Override
    public PrimitiveIterator.OfInt iterator(final Shape shape) {
        return new Iterator(shape);
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

    /**
     * The iterator of integers.
     *
     * <p>This assumes that the list of buffers is not empty.
     */
    private class Iterator implements PrimitiveIterator.OfInt {

        /** The iterator over the hashers */
        private final java.util.Iterator<Hasher> wrappedIterator;

        /** The shape of the filter we are createing */
        private final Shape shape;

        /** The iterator over the internal hasher */
        private PrimitiveIterator.OfInt current;


        /**
         * Constructs iterator with the specified shape.
         *
         * @param shape
         */
        private Iterator(final Shape shape) {
            this.shape = shape;
            wrappedIterator = hashers.iterator();
            current = null;
        }

        @Override
        public boolean hasNext() {
            if (current == null || !current.hasNext()) {
                if (wrappedIterator.hasNext()) {
                    current = wrappedIterator.next().iterator(shape);
                } else {
                    current = null;
                }
            }
            return current != null && current.hasNext();
        }

        @SuppressWarnings("cast") // Cast to long to workaround a bug in animal-sniffer.
        @Override
        public int nextInt() {
            if (hasNext()) {
                return current.nextInt();
            }
            throw new NoSuchElementException();
        }
    }

}
