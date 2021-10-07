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

import org.apache.commons.collections4.bloomfilter.Shape;
import org.apache.commons.collections4.bloomfilter.IndexProducer;

/**
 * A Hasher create IndexProducer based on the hash implementation and the
 * provided Shape.
 *
 * @since 4.5
 */
public interface Hasher {

    /**
     * Creates an IndexProducer for this hasher based on the Shape.
     *
     * <p>The @{code IndexProducer} will create indices within the range defined by the number of bits in
     * the shape. The total number of indices will respect the number of hash functions per item
     * defined by the shape. However the count of indices may not be a multiple of the number of
     * hash functions once implementation has removed duplicates.</p>
     *
     * <p>No guarantee is made as to order of indices.</p>
     * <p>Duplicates indices for a single item must be removed.</p>
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the iterator of integers
     */
    IndexProducer indices(Shape shape);

    /**
     * Gets the number of items that will be hashed by the {@code IndexProducer}.
     * @return The number of items that will be hashed by the {@code IndexProducer}.
     */
    int size();


}
