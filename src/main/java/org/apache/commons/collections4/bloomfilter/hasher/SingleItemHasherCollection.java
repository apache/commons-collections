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

import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;

import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;

/**
 * A collection of Hashers that are combined to be a single item.  This differs from
 * the HasherCollection in that the HasherCollection counts each Hasher in the collection as
 * a different item, or in the case of an enclosed HasherCollection multiple items.  This collection
 * assumes that all hashers are combined to make a single item.
 *
 * @since 4.5
 */
public class SingleItemHasherCollection extends HasherCollection {

    /**
     * Constructs an empty SingleItemHasherCollection.
     */
    public SingleItemHasherCollection() {
        super();
    }

    /**
     * Constructs a SingleItemHasherCollection from a collection of Hasher objects.
     *
     * @param hashers A collections of Hashers to build the indices with.
     */
    public SingleItemHasherCollection(Collection<Hasher> hashers) {
        super(hashers);
    }

    /**
     * Constructor.
     *
     * @param hashers A list of Hashers to initialize the collection with.
     */
    public SingleItemHasherCollection(Hasher... hashers) {
        super(hashers);
    }

    @Override
    public IndexProducer uniqueIndices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        IndexProducer baseProducer = super.indices(shape);

        return new IndexProducer() {
            @Override
            public boolean forEachIndex(IntPredicate consumer) {
                Objects.requireNonNull(consumer, "consumer");
                int actualFuncCount = (getHashers().size() == 0 ? 1 : getHashers().size())
                        * shape.getNumberOfHashFunctions();
                if (actualFuncCount < 0 || actualFuncCount > shape.getNumberOfBits()) {
                    actualFuncCount = shape.getNumberOfBits();
                }
                Shape filterShape = Shape.fromKM(actualFuncCount, shape.getNumberOfBits());
                Filter filter = new Filter(filterShape, consumer);
                return baseProducer.forEachIndex(filter);
            }
        };
    }

    @Override
    public int size() {
        for (Hasher hasher : getHashers()) {
            if (hasher.size() > 0) {
                return 1;
            }
        }
        return 0;
    }
}
